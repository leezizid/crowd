package com.crowd.tool.tapis.ctp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.crowd.tool.misc.OrderType;
import com.crowd.tool.misc.PositionSide;

public abstract class CtpTradeAPI extends CtpBaseApi {

	private ConnectInfo connectInfo;

	private boolean login = false;

	private Map<String, OrderInfo> orderMap = new HashMap<String, OrderInfo>();

	private int serverOrderCount;

	private Hashtable<Integer, LockObject> locks = new Hashtable<Integer, LockObject>();

	public CtpTradeAPI(String id, ConnectInfo connectInfo) {
		super(id);
		this.connectInfo = connectInfo;
	}

	public AccountInfo getAccountInfo() throws Throwable {
		if (!login) {
			throw new IllegalStateException("账户未登录");
		}
		int requestID = getRequestID();
		LockObject lock = new LockObject();
		locks.put(requestID, lock);
		try {
			int rtnCode = CtpApiLibrary.instance.reqQryTradingAccount(id, requestID);
			if (rtnCode != 0) {
				Thread.sleep(1000);
				rtnCode = CtpApiLibrary.instance.reqQryTradingAccount(id, requestID);
			}
			if (rtnCode != 0) {
				throw new IllegalStateException("调用CTP接口错误：" + rtnCode);
			}
			synchronized (lock) {
				if (lock.result == null) {
					lock.wait(5000);
				}
			}
		} finally {
			locks.remove(requestID);
		}
		if (lock.result == null) {
			throw new IllegalStateException("获取账户信息超时");
		}
		return (AccountInfo) lock.result;
	}

	public synchronized PositionInfo[] getPositions() throws Throwable {
		if (!login) {
			throw new IllegalStateException("账户未登录");
		}
		int requestID = getRequestID();
		LockObject lock = new LockObject();
		locks.put(requestID, lock);
		try {
			int rtnCode = CtpApiLibrary.instance.reqQryInvestorPosition(id, requestID);
			if (rtnCode != 0) {
				Thread.sleep(1000);
				rtnCode = CtpApiLibrary.instance.reqQryInvestorPosition(id, requestID);
			}
			if (rtnCode != 0) {
				throw new IllegalStateException("调用CTP接口错误：" + rtnCode);
			}
			synchronized (lock) {
				if (lock.result == null) {
					lock.wait(5000);
				}
			}
		} finally {
			locks.remove(requestID);
		}
		if (lock.result == null) {
			throw new IllegalStateException("获取账户持仓超时");
		}
		//
		return (PositionInfo[]) lock.result;
	}

	public synchronized void queryInstruments() throws Throwable {
		// 查询委托数据
		int requestID = getRequestID();
		LockObject lock = new LockObject();
		locks.put(requestID, lock);
		try {
			int rtnCode = CtpApiLibrary.instance.reqQryClassifiedInstrument(id, requestID);
			if (rtnCode != 0) {
				Thread.sleep(1000);
				rtnCode = CtpApiLibrary.instance.reqQryClassifiedInstrument(id, requestID);
			}
			if (rtnCode != 0) {
				throw new IllegalStateException("调用CTP接口错误：" + rtnCode);
			}
			synchronized (lock) {
				if (lock.result == null) {
					lock.wait(5000);
				}
			}
		} finally {
			locks.remove(requestID);
		}
		if (lock.result == null) {
			throw new IllegalStateException("获取合约列表超时");
		}
		CTPInstrument[] instruments = (CTPInstrument[]) lock.result;
		this.handleInstrumentQueryFinished(instruments);
	}

	public void cancelOrder(String symbol, String orderId) throws Throwable {
		if (!login) {
			throw new IllegalStateException("账户未登录");
		}
		if (orderMap.containsKey(orderId)) {
			String[] symbolInfo = StringUtils.split(symbol, ".");
			String[] orderIdInfo = StringUtils.split(orderId, ".");
			CtpApiLibrary.instance.reqCancelOrder(id, getRequestID(), symbolInfo[0], symbolInfo[1], orderIdInfo[1]);
		} else {
			// XXX：对于缓存查询不到的委托，直接生成撤单消息
			handleOrderUpdated(orderId, symbol, 0, BigDecimal.ZERO, true);
		}
	}

	public synchronized String postOrder(String exchangeID, String symbol, OrderType type, PositionSide positionSide,
			float price, int volume) throws Throwable {
		if (!login) {
			throw new IllegalStateException("账户未登录");
		}
		char direction;
		if (type == OrderType.Open) {
			direction = positionSide == PositionSide.Long ? '0' : '1';
		} else {
			direction = positionSide == PositionSide.Long ? '1' : '0';
			type = OrderType.CloseToday; // XXX：暂时强制为平今操作
		}
		//
		int requestID = getRequestID();
		LockObject lock = new LockObject();
		locks.put(requestID, lock);
		try {
			int rtnCode = CtpApiLibrary.instance.reqPostOrder(id, requestID, String.valueOf(serverOrderCount + 1),
					exchangeID, symbol, String.valueOf(type.ordinal()).charAt(0), direction, price, volume);
			if (rtnCode != 0) {
				throw new IllegalStateException("调用CTP接口错误：" + rtnCode);
			}
			synchronized (lock) {
				if (lock.result == null) {
					lock.wait(5000);
				}
			}
		} finally {
			locks.remove(requestID);
		}
		if (lock.result != null) {
			OrderInfo orderInfo = (OrderInfo) lock.result;
			this.serverOrderCount++;
			if (lock.error != null) {
				// 委托成功后立即撤单，既有结果又有错误
				throw new IllegalStateException(lock.error);
			} else {
				// 正常结果
				this.orderMap.put(orderInfo.getServerOrderId(), orderInfo); // 新委托加入缓存
				return orderInfo.getServerOrderId();
			}
		} else {
			if (lock.error != null) {
				throw new IllegalStateException(lock.error);
			} else {
				// result和error都为空，则为超时
				throw new IllegalStateException("下单超时");
			}
		}
	}

	public synchronized OrderInfo[] getOrders() throws Throwable {
		if (!login) {
			throw new IllegalStateException("账户未登录");
		}
		// 查询委托数据
		int requestID = getRequestID();
		LockObject lock = new LockObject();
		locks.put(requestID, lock);
		try {
			int rtnCode = CtpApiLibrary.instance.reqQryOrder(id, requestID);
			if (rtnCode != 0) {
				Thread.sleep(1000);
				rtnCode = CtpApiLibrary.instance.reqQryOrder(id, requestID);
			}
			if (rtnCode != 0) {
				throw new IllegalStateException("调用CTP接口错误：" + rtnCode);
			}
			synchronized (lock) {
				if (lock.result == null) {
					lock.wait(5000);
				}
			}
		} finally {
			locks.remove(requestID);
		}
		if (lock.result == null) {
			throw new IllegalStateException("获取账户委托列表超时");
		}
		Map<String, OrderInfo> newOrderMap = new HashMap<String, OrderInfo>();
		OrderInfo[] orders = (OrderInfo[]) lock.result;
		for (OrderInfo orderInfo : orders) {
			if (orderInfo.getServerOrderId().endsWith(".")) {
				// 无效的委托记录
				continue;
			}
			newOrderMap.put(orderInfo.getServerOrderId(), orderInfo);
		}

		// 查询成交数据
		requestID = getRequestID();
		lock = new LockObject();
		locks.put(requestID, lock);
		try {
			int rtnCode = CtpApiLibrary.instance.reqQryTrade(id, requestID);
			if (rtnCode != 0) {
				Thread.sleep(1000);
				rtnCode = CtpApiLibrary.instance.reqQryTrade(id, requestID);
			}
			if (rtnCode != 0) {
				throw new IllegalStateException("调用CTP接口错误：" + rtnCode);
			}
			synchronized (lock) {
				if (lock.result == null) {
					lock.wait(5000);
				}
			}
		} finally {
			locks.remove(requestID);
		}
		if (lock.result == null) {
			throw new IllegalStateException("获取账户成交列表超时");
		}
		TradeInfo[] trades = (TradeInfo[]) lock.result;

		// 更新委托数据
		for (int i = 0; i < trades.length; i++) {
			OrderInfo orderInfo = newOrderMap.get(trades[i].getOrderId());
			if (orderInfo == null) {
				throw new IllegalStateException("成交列表中存在委托记录不存在的情况");
			}
			orderInfo.addTrade(trades[i]);
		}

		// 返回数据
		this.orderMap = newOrderMap;
		this.serverOrderCount = orders.length; // 记录所有委托数量总和，以便生成OrderRef
		return orders;
	}

	@SuppressWarnings("unchecked")
	public void handleMessage(String type, String message) {
		JSONObject messageObject;
		try {
//			System.out.println("----" + type);
			messageObject = new JSONObject(new String(Base64.getDecoder().decode(message), "gbk"));
//			System.out.println("----" + messageObject);
			if ("T_OnFrontConnected".equals(type)) {
				CtpApiLibrary.instance.reqAuthenticate(id, getRequestID());
			} else if ("T_OnFrontDisconnected".equals(type)) {
				login = false;
				// CTP会自动重连
			} else if ("T_OnRspAuthenticate".equals(type)) {
				if (messageObject.optInt("ErrorID") != 0) {
					dispose("客戶端认证失败");
					return;
				}
				CtpApiLibrary.instance.reqTraderUserLogin(id, getRequestID());
			} else if ("T_OnRspUserLogin".equals(type)) {
				if (messageObject.optInt("ErrorID") != 0) {
					dispose("登录失败");
					return;
				}
				CtpApiLibrary.instance.reqSettlementInfoConfirm(id, getRequestID());
			} else if ("T_OnRspSettlementInfoConfirm".equals(type)) {
				login = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
							getOrders();
						} catch (Throwable t) {
							t.printStackTrace();
							dispose("初始化委托缓存失败");
						}
						try {
							queryInstruments();
						} catch (Throwable t) {
						}
					}
				}).start();
			} else if ("T_OnRspQryTradingAccount".equals(type)) {
				int requestID = messageObject.optInt("nRequestID");
				LockObject lock = locks.get(requestID);
				if (lock != null) {
					AccountInfo info = new AccountInfo();
					info.setAvailableValue(new BigDecimal(messageObject.getDouble("Available")));
					info.setBalanceValue(new BigDecimal(messageObject.getDouble("Balance")));
					info.setCurrentMarginValue(new BigDecimal(messageObject.getDouble("CurrMargin")));
					info.setPositionProfit(new BigDecimal(messageObject.getDouble("PositionProfit")));
					lock.result = info;
					synchronized (lock) {
						lock.notify();
					}
				}
			} else if ("T_OnRspQryInvestorPosition".equals(type)) {
				int requestID = messageObject.optInt("nRequestID");
				LockObject lock = locks.get(requestID);
				if (lock != null) {
					if (lock.temp == null) {
						lock.temp = new ArrayList<PositionInfo>();
					}
					List<PositionInfo> positionList = (List<PositionInfo>) lock.temp;
					if (StringUtils.isNotEmpty(messageObject.optString("InstrumentID"))) {// 用于处理空数据情况
						PositionInfo info = new PositionInfo();
						info.setSymbol(
								messageObject.optString("ExchangeID") + "." + messageObject.optString("InstrumentID"));
						info.setPositionSide(PositionSide.values()[messageObject.getInt("PosiDirection") - 50]);
						info.setMarketPrice(new BigDecimal(messageObject.getDouble("SettlementPrice")));
						info.setTotalVolume(messageObject.getInt("Position"));
						info.setTodayVolume(messageObject.getInt("TodayPosition"));
						positionList.add(info);
					}
					if (messageObject.getBoolean("bIsLast")) {
						lock.result = positionList.toArray(new PositionInfo[0]);
						synchronized (lock) {
							lock.notify();
						}
					}
				}
			} else if ("T_OnRspQryOrder".equals(type)) {
				int requestID = messageObject.optInt("nRequestID");
				LockObject lock = locks.get(requestID);
				if (lock != null) {
					if (lock.temp == null) {
						lock.temp = new ArrayList<OrderInfo>();
					}
					List<OrderInfo> orderList = (List<OrderInfo>) lock.temp;
					if (StringUtils.isNotEmpty(messageObject.optString("ExchangeID"))) { // 用于处理空数据情况
						orderList.add(messageToOrderInfo(messageObject));
					}
					if (messageObject.getBoolean("bIsLast")) {
						lock.result = orderList.toArray(new OrderInfo[0]);
						synchronized (lock) {
							lock.notify();
						}
					}
				}
			} else if ("T_OnRspQryTrade".equals(type)) {
				int requestID = messageObject.optInt("nRequestID");
				LockObject lock = locks.get(requestID);
				if (lock != null) {
					if (lock.temp == null) {
						lock.temp = new ArrayList<OrderInfo>();
					}
					List<TradeInfo> tradeList = (List<TradeInfo>) lock.temp;
					if (StringUtils.isNotEmpty(messageObject.optString("InstrumentID"))) {// 用于处理空数据情况
						TradeInfo info = new TradeInfo(messageObject.getString("TradeID"),
								messageObject.getString("ExchangeID") + "." + messageObject.getString("OrderSysID"),
								messageObject.getInt("Volume"), new BigDecimal(messageObject.getDouble("Price")));
						tradeList.add(info);
					}
					if (messageObject.getBoolean("bIsLast")) {
						lock.result = tradeList.toArray(new TradeInfo[0]);
						synchronized (lock) {
							lock.notify();
						}
					}
				}
			} else if ("T_OnRtnOrder".equals(type)) {
				// XXX：OnRtnOrder消息情况比较复杂，任何委托更新都会触发此消息，并且都是系统推送，无nRequestID，但都有订单RequestID字段
				int requestID = messageObject.getInt("RequestID");
				int orderStatus = messageObject.optInt("OrderStatus");
				LockObject lock = locks.get(requestID);
				if (lock != null) {
					lock.result = messageToOrderInfo(messageObject);
					// 当lock不为空，要对当前正在进行的委托动作进行处理，有三种种结果：
					// 1、委托提交完毕
					if (StringUtils.isNotEmpty(messageObject.getString("OrderSysID"))) {
						synchronized (lock) {
							lock.notify();
						}
					}
					// 1、委托提交后因参数错误立即撤单，结果是委托失败
					else if (orderStatus == 53) {
						lock.error = messageObject.getString("StatusMsg");
						synchronized (lock) {
							lock.notify();
						}
					}
					// 3、委托处于正在提交中的未知状态
					else {
						// 不处理
					}
				} else {
					// 当lock为空，则是其他服务器主动推送的委托状态变化单，进行以下处理：
					// 1、如果是撤单状态，则尝试更新OrderInfo
					// 2、如果orderMap中没有信息，并获取OrderInfo是正确委托，则尝试加入
					// 3、如果是其他状态，例如成交，不处理，交给T_OnRtnTrade消息处理即可
					String orderId = messageObject.getString("ExchangeID") + "."
							+ messageObject.getString("OrderSysID");
					if (orderMap.containsKey(orderId)) {
						if (orderStatus == 53) {
							OrderInfo orderInfo = orderMap.get(orderId);
							if (orderInfo != null) {
								orderInfo.setCanceled(true);
								handleOrderUpdated(orderId, orderInfo.getSymbol(), orderInfo.getExecVolume(),
										orderInfo.getExecValue(), true);
							}
						} else {
							// 不处理
						}
					} else {
						// XXX：考虑是否加入orderMap缓存
					}
				}
			} else if ("T_OnRspOrderInsert".equals(type) || "OnErrRtnOrderInsert".equals(type)) {
				// 下单错误如果是价格错误等逻辑错误，则通过onRtnOrder正常响应，一般是立即形成撤单。这种情况后续可以查询到委托记录，但是没有委托编号
				// 如果是合约代码等严重错误，则响应T_OnRspOrderInsert和T_OnErrRtnOrderInsert，前者是消息响应，后者是委托反馈响应，这种情况不会形成委托记录，后续查询也查询不到
				//
				// OnRspOrderInsert消息有nRequestID和bIsLast字段，OnErrRtnOrderInsert消息无nRequestID和bIsLast字段，
				// OnRspOrderInsert会先于OnErrRtnOrderInsert触发，都有Insert时传递的RequestID字段
				int requestID = messageObject.optInt("RequestID");
				LockObject lock = locks.get(requestID);
				if (lock != null) {
					lock.error = messageObject.getString("ErrorMsg");
					synchronized (lock) {
						lock.notify();
					}
				}
			} else if ("T_OnRtnTrade".equals(type)) {
				// 成交触发此消息
				// 尝试更新OrderInfo
				String tradeId = messageObject.getString("TradeID");
				String orderId = messageObject.getString("ExchangeID") + "." + messageObject.getString("OrderSysID");
				int volume = messageObject.getInt("Volume");
				BigDecimal price = new BigDecimal(messageObject.getDouble("Price"));
				OrderInfo orderInfo = orderMap.get(orderId);
				if (orderInfo != null) {
					orderInfo.addTrade(new TradeInfo(tradeId, orderId, volume, price));
					handleOrderUpdated(orderId, orderInfo.getSymbol(), orderInfo.getExecVolume(),
							orderInfo.getExecValue(), orderInfo.isCanceled());
				}
			} else if ("T_OnRspQryClassifiedInstrument".equals(type)) {
				int requestID = messageObject.optInt("nRequestID");
				LockObject lock = locks.get(requestID);
				if (lock != null) {
					if (lock.temp == null) {
						lock.temp = new ArrayList<CTPInstrument>();
					}
					List<CTPInstrument> instrumentList = (List<CTPInstrument>) lock.temp;
					if (StringUtils.isNotEmpty(messageObject.optString("InstrumentID"))) { // 用于处理空数据情况
						CTPInstrument o = new CTPInstrument();
						o.setId(messageObject.getString("InstrumentID"));
						o.setName(messageObject.getString("InstrumentName"));
						o.setExchangeId(messageObject.getString("ExchangeID"));
						o.setProductId(messageObject.getString("ProductID"));
						o.setDeliveryYear(messageObject.getInt("DeliveryYear"));
						o.setDeliveryMonth(messageObject.getInt("DeliveryMonth"));
						if (!o.getId().endsWith("efp") && !o.getId().endsWith("TAS")) {
							instrumentList.add(o);
						}
					}
					if (messageObject.getBoolean("bIsLast")) {
						lock.result = instrumentList.toArray(new CTPInstrument[0]);
						synchronized (lock) {
							lock.notify();
						}
					}
				}
//				System.out.println(messageObject);
			} else if ("T_OnRspError".equals(type)) {
				int requestID = messageObject.optInt("nRequestID");
				LockObject lock = locks.get(requestID);
				if (lock != null) {
					lock.error = messageObject.getString("ErrorMsg");
					synchronized (lock) {
						lock.notify();
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private OrderInfo messageToOrderInfo(JSONObject messageObject) {
		OrderInfo info = new OrderInfo();
		info.setSymbol(messageObject.optString("ExchangeID") + "." + messageObject.optString("InstrumentID"));
		info.setType(OrderType.values()[messageObject.getInt("CombOffsetFlag")]);
		info.setPrice(messageObject.getDouble("LimitPrice"));
		info.setVolume(messageObject.getInt("VolumeTotalOriginal"));
		int direction = messageObject.getInt("Direction") - 48;
		if (info.getType() == OrderType.Open) {
			info.setPositionSide(direction == 0 ? PositionSide.Long : PositionSide.Short);
		} else {
			info.setPositionSide(direction == 0 ? PositionSide.Short : PositionSide.Long);
		}
		info.setTime(messageObject.getString("InsertDate") + " " + messageObject.getString("InsertTime"));
		info.setServerOrderId(messageObject.getString("ExchangeID") + "." + messageObject.getString("OrderSysID"));
		info.setCanceled(messageObject.getInt("OrderStatus") == 53);
//		info.setClientOrderId(messageObject.getString("OrderLocalID"));
//		info.setSubmitStatus(messageObject.getInt("OrderSubmitStatus"));
//		info.setOrderStatus(messageObject.getInt("OrderStatus"));
		return info;
	}

	public final boolean isLogin() {
		return login;
	}

	protected void doInit(String flowDir) {
		System.out.println("TraderAPIVersion:"
				+ CtpApiLibrary.instance.initTrader(id, flowDir, connectInfo.getJsonBase64String(), this));
	}

	protected void doRelease() {
		CtpApiLibrary.instance.releaseTrader(id);
	}

	public void synchronizeOrder(String serverOrderId) throws Throwable {
		OrderInfo orderInfo = this.orderMap.get(serverOrderId);
		if (orderInfo != null) {
			handleOrderUpdated(serverOrderId, orderInfo.getSymbol(), orderInfo.getExecVolume(),
					orderInfo.getExecValue(), orderInfo.isCanceled());
		}
	}

	protected abstract void handleOrderUpdated(String serverOrderId, String symbol, int execVolume,
			BigDecimal execValue, boolean canceled);

	protected abstract void handleInstrumentQueryFinished(CTPInstrument[] instruments);
}

class LockObject {

	Object temp;

	Object result;

	String error;

}