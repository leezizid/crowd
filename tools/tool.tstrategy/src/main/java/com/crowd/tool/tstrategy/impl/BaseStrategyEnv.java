package com.crowd.tool.tstrategy.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.crowd.service.base.CrowdContext;
import com.crowd.tool.misc.OrderType;
import com.crowd.tool.misc.PositionSide;
import com.crowd.tool.misc.ProductInfo;
import com.crowd.tool.misc.Products;
import com.crowd.tool.tstrategy.IStrategy;
import com.crowd.tool.tstrategy.OrderInfo;
import com.crowd.tool.tstrategy.StrategyContext;
import com.crowd.tool.tstrategy.StrategyEnv;
import com.crowd.tool.tstrategy.StrategyInfo;
import com.crowd.tool.tstrategy.TransactionInfo;

public abstract class BaseStrategyEnv implements StrategyContext, StrategyEnv {

	protected StrategyInfoImpl strategyInfo;

	protected IStrategy strategyInstance;

	private CrowdContext crowdContext;

	private Products products;
	
	//
//	private Calendar calendar = Calendar.getInstance();
//	private String tradeDay = "";

	public BaseStrategyEnv(CrowdContext crowdContext, IStrategy strategyInstance, StrategyInfo strategyInfo,
			Products products) throws Throwable {
		this.crowdContext = crowdContext;
		this.strategyInstance = strategyInstance;
		this.strategyInfo = (StrategyInfoImpl) strategyInfo;
		this.products = products;
		this.strategyInstance.init(this);
		this.init(crowdContext);
	}

	public final StrategyInfo getStrategyInfo() {
		return this.strategyInfo;
	}

	@Override
	public ProductInfo findProduct(String symbol) {
		return this.products.getProduct(symbol);
	}

	protected abstract void init(CrowdContext crowdContext) throws Throwable;

	public void onTick(String symbol, long time, BigDecimal lowerLimitPrice, BigDecimal upperLimitPrice,
			BigDecimal price, BigDecimal volumn) {
		synchronized (strategyInfo) {
			//
//			calendar.setTimeInMillis(time);
//			int hour = calendar.get(Calendar.HOUR_OF_DAY);
//			if(hour >= 6 && hour <= 18) {
//				int year = calendar.get(Calendar.YEAR);
//				int month = calendar.get(Calendar.MONTH) + 1;
//				int day = calendar.get(Calendar.DAY_OF_MONTH);
//				String newTradeDay = year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day;
//				if (!newTradeDay.equals(tradeDay)) {
//					strategyInfo.addTradeDay(newTradeDay);
//					tradeDay = newTradeDay;
//					try {
//						saveInfo(crowdContext);
//					} catch (Throwable t) {
//
//					}
//				}
//			}
			//
			for (TransactionInfo transaction : strategyInfo.getTransactions()) {
				if (transaction.getSymbol().equals(symbol)) {
					try {
						ProductInfo productInfo = findProduct(transaction.getSymbol());
						if (transaction.getPositionVolumn().compareTo(BigDecimal.ZERO) > 0) {
							if (transaction.getForceCloseFlag()) {
								if (transaction.getPositionSide() == PositionSide.Long) {
									if (lowerLimitPrice.compareTo(BigDecimal.ZERO) == 0) {
										closePosition(time, transaction, price.subtract(price
												.multiply(new BigDecimal("0.01")).setScale(0, RoundingMode.HALF_UP)));
									} else {
										closePosition(time, transaction, lowerLimitPrice);
									}
								} else {
									if (upperLimitPrice.compareTo(BigDecimal.ZERO) == 0) {
										closePosition(time, transaction, price.add(price
												.multiply(new BigDecimal("0.01")).setScale(0, RoundingMode.HALF_UP)));
									} else {
										closePosition(time, transaction, upperLimitPrice);
									}
								}
							} else {
								if (transaction.getPositionSide() == PositionSide.Long) {
									BigDecimal thresholdPrice = transaction.getTakePrice()
											.subtract(transaction.getPositionPrice())
											.divide(new BigDecimal(2), 4, RoundingMode.HALF_UP)
											.add(transaction.getPositionPrice()); // 止盈单下达需要的阈值
									// 价格超过止盈挂单阈值，挂止盈单
									if (price.compareTo(thresholdPrice) > 0) {
										closePosition(time, transaction, transaction.getTakePrice());
									}
									// 开始亏损，撤销所有挂单（通常为止盈挂单）
									if (price.compareTo(transaction.getPositionPrice()) < 0) {
										cancelOrders(time, transaction.getId());
									}
									// 止损价格、收盘时间前、持仓时间达到阈值，强行平仓
									if (price.compareTo(transaction.getStopPrice()) <= 0) {
										forceClose(time, transaction.getId());
									}
								} else {
									BigDecimal thresholdPrice = transaction.getPositionPrice()
											.subtract(transaction.getTakePrice())
											.divide(new BigDecimal(2), 4, RoundingMode.HALF_UP)
											.add(transaction.getTakePrice()); // 止盈单下达需要的阈值
									// 价格低于止盈挂单阈值，挂止盈单
									if (price.compareTo(thresholdPrice) < 0) {
										closePosition(time, transaction, transaction.getTakePrice());
									}
									// 开始亏损，撤销所有挂单（通常为止盈挂单）
									if (price.compareTo(transaction.getPositionPrice()) > 0) {
										cancelOrders(time, transaction.getId());
									}
									// 止损价格、收盘时间前、持仓时间达到阈值，强行平仓
									if (price.compareTo(transaction.getStopPrice()) >= 0) {
										forceClose(time, transaction.getId());
									}
								}

								// 接近产品收盘时间，则强制平仓
								if (productInfo.beforeCloseMarket(time) < 60 * 5) {
									forceClose(time, transaction.getId());
								}

								// TODO：计算产品持仓时间，收敛止盈止损点，看是否需要强行平仓
							}
						} else {
							// 如果开仓一直没有成交，收盘前30分钟撤单
							if (productInfo.beforeCloseMarket(time) < 60 * 30) {
								cancelOrders(time, transaction.getId());
							}
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
			this.strategyInstance.onTick(this,
					new TickInfoImpl(symbol, time, lowerLimitPrice, upperLimitPrice, price, volumn));
		}
	}

	public void openPosition(long time, String symbol, PositionSide positionSide, BigDecimal price, BigDecimal volumn,
			BigDecimal takePrice, BigDecimal stopPrice) throws Throwable {

		// 交易时间是否合理：收盘30分钟内不交易，
		ProductInfo productInfo = findProduct(symbol);
		if (productInfo.beforeCloseMarket(time) < 30 * 60) {
			throw new IllegalStateException("离收盘不超过30分钟，不允许开仓");
		}

		// 开盘内15分钟不交易
		if (productInfo.afterOpenMarket(time) < 15 * 60) {
			throw new IllegalStateException("开盘内15分钟，不允许开仓");
		}

		// 上次交易时间30分钟内不交易，不交易
		if (time - strategyInfo.getLastTransactionTime() < 30 * 60 * 1000) {
//			throw new IllegalStateException("离上次交易不超过30分钟，不允许开仓");
		}

		// 当前有活动交易，不交易
		if (strategyInfo.getTransactions().length > 0) {
			throw new IllegalStateException("当前有活动交易，不允许开仓");
		}

		// 检查止损止盈价格是否合理：盈亏比、亏损比例、止盈价格低于委托价格、止损价格高于委托价格等
		if (positionSide == PositionSide.Long) {
			if (takePrice.compareTo(price) <= 0) {
				throw new IllegalStateException("止盈价格必须高于委托价格");
			}
			if (stopPrice.compareTo(price) >= 0) {
				throw new IllegalStateException("止损价格必须低于委托价格");
			}
			if (price.subtract(stopPrice).compareTo(takePrice.subtract(price)) > 0) {
//				throw new IllegalStateException("盈亏比例不合理");
			}
			if (price.subtract(stopPrice).divide(price, 4, RoundingMode.HALF_EVEN)
					.compareTo(new BigDecimal("0.005")) > 0) {
				//throw new IllegalStateException("止损价格偏低，亏损比例不能超过0.5%");
			}
		} else {
			if (takePrice.compareTo(price) >= 0) {
				throw new IllegalStateException("止盈价格必须低于委托价格");
			}
			if (stopPrice.compareTo(price) <= 0) {
				throw new IllegalStateException("止损价格必须高于委托价格");
			}
			if (price.subtract(takePrice).compareTo(stopPrice.subtract(price)) <= 0) {
//				throw new IllegalStateException("盈亏比例不合理");
			}
			if (stopPrice.subtract(price).divide(price, 4, RoundingMode.HALF_EVEN)
					.compareTo(new BigDecimal("0.005")) > 0) {
//				throw new IllegalStateException("止损价格偏低，亏损比例不能超过0.5%");
			}
		}

		// 以下规则可由策略自己实现
		// 1、挂单价格在接近涨停和跌停一定范围内不交易
		// 2、对亏损比例进行策略级核算，避免过大的策略级亏损

		//
		synchronized (strategyInfo) {
			// 开仓
			TransactionInfoImpl transactionInfo = new TransactionInfoImpl();
			transactionInfo.setId(strategyInfo.getTransactionCount() + 1);
			transactionInfo.setSymbol(symbol);
			transactionInfo.setBalance(BigDecimal.ZERO);
			transactionInfo.setCost(BigDecimal.ZERO);
			transactionInfo.setPositionSide(positionSide);
			transactionInfo.setPositionVolumn(BigDecimal.ZERO);
			transactionInfo.setOpenTime(time);
			transactionInfo.setOrderPrice(price);
			transactionInfo.setTakePrice(takePrice);
			transactionInfo.setStopPrice(stopPrice);
			OrderInfoImpl orderInfo = new OrderInfoImpl();
			orderInfo.setClientOrderId(strategyInfo.getId() + "_" + transactionInfo.getId() + "_" + time);
			orderInfo.setTime(time);
			orderInfo.setSymbol(symbol);
			orderInfo.setPositionSide(positionSide);
			orderInfo.setType(OrderType.Open);
			orderInfo.setVolumn(volumn);
			orderInfo.setPrice(price);
			orderInfo.setExecVolumn(BigDecimal.ZERO);
			orderInfo.setExecValue(BigDecimal.ZERO);
			orderInfo.setCostValue(BigDecimal.ZERO);
			orderInfo.setAvgPrice(BigDecimal.ZERO);
			orderInfo.setCanceled(false);
			transactionInfo.addOrder(orderInfo);
			strategyInfo.addTransaction(transactionInfo);
			//
			saveInfo(crowdContext);
			//
			try {
				orderInfo.setServerOrderId(postOrder(crowdContext, orderInfo));
			} catch (Throwable e) {
				e.printStackTrace();
				orderInfo.setError(e.getMessage());
				orderInfo.setServerOrderId(""); // XXX：需要手动处理该订单状态
			} finally {
				// TODO:存储策略信息
				transactionInfo.updateOrder(orderInfo);
				strategyInfo.updateTransaction(transactionInfo);
				try {
					saveInfo(crowdContext);
				} catch (Throwable t) {
					// TODO：记录严重日志
					t.printStackTrace();
				}
			}
		}
	}

	public void closePosition(long time, TransactionInfo transactionInfo, BigDecimal price) throws Throwable {
		synchronized (strategyInfo) {
			TransactionInfoImpl transactionInfoImpl = (TransactionInfoImpl) transactionInfo;
			BigDecimal volumn = transactionInfoImpl.getPositionVolumn().subtract(transactionInfoImpl.getLockVolumn());
			if (volumn.compareTo(BigDecimal.ZERO) <= 0) {
				return;
			}
			// 开仓
			OrderInfoImpl orderInfo = new OrderInfoImpl();
			orderInfo.setClientOrderId(strategyInfo.getId() + "_" + transactionInfo.getId() + "_" + time);
			orderInfo.setTime(time);
			orderInfo.setSymbol(transactionInfo.getSymbol());
			orderInfo.setPositionSide(transactionInfoImpl.getPositionSide());
			orderInfo.setType(OrderType.Close);
			orderInfo.setVolumn(volumn);
			orderInfo.setPrice(price);
			orderInfo.setExecVolumn(BigDecimal.ZERO);
			orderInfo.setExecValue(BigDecimal.ZERO);
			orderInfo.setCostValue(BigDecimal.ZERO);
			orderInfo.setAvgPrice(BigDecimal.ZERO);
			orderInfo.setCanceled(false);
			transactionInfoImpl.addOrder(orderInfo);
			strategyInfo.updateTransaction(transactionInfoImpl);
			//
			saveInfo(crowdContext);
			//
			try {
				orderInfo.setServerOrderId(postOrder(crowdContext, orderInfo));
			} catch (Throwable e) {
				e.printStackTrace();
				orderInfo.setError(e.getMessage());
				orderInfo.setServerOrderId(""); // XXX：需要手动处理该订单状态
			} finally {
				// TODO:存储策略信息
				transactionInfoImpl.updateOrder(orderInfo);
				strategyInfo.updateTransaction(transactionInfoImpl);
				try {
					saveInfo(crowdContext);
				} catch (Throwable t) {
					// TODO：记录严重日志
					t.printStackTrace();
				}
			}
		}
	}

	public final void handleOrderUpdated(long time, OrderInfo orderInfo, boolean canceled, BigDecimal execVolumn,
			BigDecimal execValue) {
		synchronized (strategyInfo) {
			OrderInfoImpl matchOrderInfo = (OrderInfoImpl) orderInfo;
			TransactionInfoImpl transactionInfo = (TransactionInfoImpl) strategyInfo
					.findTransaction(matchOrderInfo.getClientOrderId());
			if (canceled) {
				matchOrderInfo.setCanceled(true);
			}
			ProductInfo productInfo = findProduct(transactionInfo.getSymbol()); // XXX：productInfo可能为空（下单时信息错误）
			if (execVolumn.compareTo(matchOrderInfo.getExecVolumn()) > 0) {
				// 处理交易持仓变化和盈亏情况
				BigDecimal newExecVolumn = execVolumn.subtract(matchOrderInfo.getExecVolumn());
				BigDecimal newExecValue = execValue.subtract(matchOrderInfo.getExecValue());
				//
				BigDecimal newPrice = newExecValue.divide(newExecVolumn.multiply(productInfo.getMultiplier()), 2,
						RoundingMode.HALF_UP);
				BigDecimal avgPrice = execValue.divide(execVolumn.multiply(productInfo.getMultiplier()), 16,
						RoundingMode.HALF_UP);
				if (productInfo.isDelivery()) {
					newPrice = newExecVolumn.multiply(productInfo.getMultiplier()).divide(newExecValue, 2,
							RoundingMode.HALF_UP);
					avgPrice = execVolumn.multiply(productInfo.getMultiplier()).divide(execValue, 16,
							RoundingMode.HALF_UP);
				}
				//
				BigDecimal costValue = BigDecimal.ZERO;
				if (matchOrderInfo.getType() == OrderType.Open) {
					if (newPrice.compareTo(matchOrderInfo.getPrice()) == 0) {
						costValue = newExecValue.multiply(productInfo.getOpenMakerCostRate());
					} else {
						costValue = newExecValue.multiply(productInfo.getOpenTakerCostRate());
					}
				} else {
					if (newPrice.compareTo(matchOrderInfo.getPrice()) == 0) {
						costValue = newExecValue.multiply(productInfo.getCloseMakerCostRate());
					} else {
						costValue = newExecValue.multiply(productInfo.getCloseTakerCostRate());
					}
				}
				//
				matchOrderInfo.setExecVolumn(execVolumn);
				matchOrderInfo.setExecValue(execValue);
				matchOrderInfo.setAvgPrice(avgPrice);
				matchOrderInfo.setCostValue(matchOrderInfo.getCostValue().add(costValue));
				//
				transactionInfo.setCost(transactionInfo.getCost().add(costValue));
				if ((matchOrderInfo.getType() == OrderType.Open
						&& matchOrderInfo.getPositionSide() == PositionSide.Long)
						|| (matchOrderInfo.getType() == OrderType.Close
								&& matchOrderInfo.getPositionSide() == PositionSide.Short)) {
					transactionInfo.setBalance(transactionInfo.getBalance().subtract(newExecValue));
				} else {
					transactionInfo.setBalance(transactionInfo.getBalance().add(newExecValue));
				}
				if (matchOrderInfo.getType() == OrderType.Open) {
					transactionInfo.setPositionVolumn(transactionInfo.getPositionVolumn().add(newExecVolumn));
					transactionInfo.setPositionTime(time);
				} else {
					transactionInfo.setPositionVolumn(transactionInfo.getPositionVolumn().subtract(newExecVolumn));
				}
				if (transactionInfo.getPositionVolumn().compareTo(BigDecimal.ZERO) > 0) {
					if (productInfo.isDelivery()) {
						transactionInfo.setPositionPrice(
								transactionInfo.getPositionVolumn().multiply(productInfo.getMultiplier())
										.divide(transactionInfo.getBalance(), 4, RoundingMode.HALF_UP).abs());
					} else {
						transactionInfo
								.setPositionPrice(
										transactionInfo.getBalance()
												.divide(transactionInfo.getPositionVolumn()
														.multiply(productInfo.getMultiplier()), 4, RoundingMode.HALF_UP)
												.abs());
					}
				} else {
					transactionInfo.setPositionPrice(BigDecimal.ZERO);
				}
			}
			// 如果交易持仓为0，并且没有挂单，则关闭交易
			if (transactionInfo.canClose()) {
				try {
					saveTransaction(crowdContext,
							strategyInfo.endTransaction(productInfo, transactionInfo.getId(), time));
				} catch (Throwable t) {
					// TODO：记录严重日志
				}
			}
			try {
				saveInfo(crowdContext);
			} catch (Throwable t) {
				// TODO：记录严重日志
			}
		}
	}

	@Override
	public void handleManualOpen(PositionSide side, String symbol, BigDecimal price, BigDecimal targetpPrice,
			BigDecimal stopPrice) throws Throwable {
		synchronized (strategyInfo) {
			this.openPosition(System.currentTimeMillis(), symbol, side, price,
					strategyInstance.calcTransactionVolumn(this, symbol, price), targetpPrice, stopPrice);
		}
	}

//	@Override
//	public void handleManualClose(int transactionId) throws Throwable {
//		synchronized (strategyInfo) {
//			for (TransactionInfo transactionInfo : strategyInfo.getTransactions()) {
//				if (transactionInfo.getId() == transactionId) {
//					this.closePosition(System.currentTimeMillis(), transactionInfo, BigDecimal.ZERO);
//					return;
//				}
//			}
//		}
//	}

	// 手动平仓、持仓时间到期自动平仓、收盘强制平仓、到达止损价位强制平仓等调用此方法即可完成
	public final void forceClose(long time, int transactionId) throws Throwable {
		synchronized (strategyInfo) {
			for (TransactionInfo transactionInfo : strategyInfo.getTransactions()) {
				if (transactionInfo.getId() == transactionId && !transactionInfo.getForceCloseFlag()) {
					try {
						// 标记交易为强平标记
						((TransactionInfoImpl) transactionInfo).setForceCloseFlag(true);
						saveInfo(crowdContext);
					} catch (Throwable t) {
						t.printStackTrace();
						// TODO：记录严重日志
					}
					cancelOrders(time, transactionId);
					return;
				}
			}
		}

	}

	public final void cancelOrders(long time, int transactionId) throws Throwable {
		synchronized (strategyInfo) {
			for (TransactionInfo transactionInfo : strategyInfo.getTransactions()) {
				if (transactionInfo.getId() == transactionId) {
					try {
						boolean updated = false;
						for (OrderInfo orderInfo : transactionInfo.getOrders()) {
							if (!orderInfo.isFinished() && time - orderInfo.getLastCancelTime() > 10 * 1000) {
								((OrderInfoImpl) orderInfo).setLastCancelTime(time);
								cancelOrder(crowdContext, time, orderInfo.getSymbol(), orderInfo.getServerOrderId());
								updated = true;
							}
						}
						if (updated) {
							saveInfo(crowdContext);
						}
					} catch (Throwable t) {
						t.printStackTrace();
						// TODO：记录严重日志
					}
					return;
				}
			}
		}
	}

	protected abstract String postOrder(CrowdContext context, OrderInfo orderInfo) throws Throwable;

	protected abstract void cancelOrder(CrowdContext context, long time, String symbol, String serverOrderId)
			throws Throwable;

	protected abstract void saveInfo(CrowdContext crowdContext) throws Throwable;

	protected abstract void saveTransaction(CrowdContext crowdContext, TransactionInfoImpl transactionInfo)
			throws Throwable;

//	public final static StrategyInfo createStrategyInfo(JSONObject o) throws Throwable {
//		StrategyInfoImpl strategyInfo = new StrategyInfoImpl();
//		strategyInfo.fromJSON(new JSONObject(o));
//		return strategyInfo;
//	}

}
