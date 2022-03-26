package com.crowd.tool.tstrategy.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.tool.misc.OrderType;
import com.crowd.tool.misc.PositionSide;
import com.crowd.tool.misc.ProductInfo;
import com.crowd.tool.misc.Products;
import com.crowd.tool.tstrategy.IStrategy;
import com.crowd.tool.tstrategy.OrderInfo;
import com.crowd.tool.tstrategy.StrategyInfo;

public final class BacktestStrategyEnv extends BaseStrategyEnv {

	private List<OrderInfoImpl> orderList = new ArrayList<OrderInfoImpl>();

	private JSONArray transactions = new JSONArray();

	public BacktestStrategyEnv(CrowdContext crowdContext, IStrategy strategyInstance, StrategyInfo strategyInfo,
			Products products) throws Throwable {
		super(crowdContext, strategyInstance, strategyInfo, products);
	}

	protected void init(CrowdContext crowdContext) throws Throwable {
	}

	public final void dispose(CrowdContext crowdContext) {
		try {
			JSONObject input = new JSONObject();
			input.put("id", strategyInfo.getId());
			input.put("transactions", transactions);
			crowdContext.invoke("/backtest-manage/saveTransactions", input);
		} catch (Throwable t) {

		}
	}

	public void onTick(String symbol, long time, BigDecimal lowerLimitPrice, BigDecimal upperLimitPrice,
			BigDecimal price, BigDecimal volumn) {
		super.onTick(symbol, time, lowerLimitPrice, upperLimitPrice, price, volumn);
		//
		for (int i = 0; i < orderList.size(); i++) {
			OrderInfoImpl orderInfo = orderList.get(i);
			ProductInfo productInfo = findProduct(orderInfo.getSymbol());
			if (orderInfo.getSymbol().equals(symbol)) {
				boolean matched = false;
				if ((orderInfo.getType() == OrderType.Open && orderInfo.getPositionSide() == PositionSide.Long)
						|| (orderInfo.getType() == OrderType.Close
								&& orderInfo.getPositionSide() == PositionSide.Short)) {
					if (price.compareTo(orderInfo.getPrice()) <= 0) {
						matched = true;
					}
				} else {
					if (price.compareTo(orderInfo.getPrice()) >= 0) {
						matched = true;
					}
				}
				if (matched) {
					// XXX：先简化成交量，假设价格到了就可以全部成交，后续应该优化
					BigDecimal execVolumn = orderInfo.getVolumn();
					orderInfo.setExecVolumn(execVolumn);
					OrderInfoImpl matchOrderInfo = (OrderInfoImpl) strategyInfo.findOrder(orderInfo.getClientOrderId());
					handleOrderUpdated(time, matchOrderInfo, false, execVolumn,
							productInfo.isDelivery()
									? execVolumn.multiply(findProduct(symbol).getMultiplier()).divide(price, 8,
											RoundingMode.HALF_UP)
									: execVolumn.multiply(findProduct(symbol).getMultiplier()).multiply(price));
				}
			}
		}
		for (int i = orderList.size() - 1; i >= 0; i--) {
			OrderInfoImpl orderInfo = orderList.get(i);
			if (orderInfo.isFinished()) {
				orderList.remove(i);
			}
		}
	}

	@Override
	protected String postOrder(CrowdContext context, OrderInfo orderInfo) throws Throwable {
		OrderInfoImpl newOrder = new OrderInfoImpl();
		newOrder.fromJSON(((OrderInfoImpl) orderInfo).toJSON());
		newOrder.setServerOrderId("s" + System.nanoTime());
		orderList.add(newOrder);
		return newOrder.getServerOrderId();
	}

	@Override
	protected void cancelOrder(CrowdContext context, long time, String symbol, String serverOrderId) throws Throwable {
		for (int i = orderList.size() - 1; i >= 0; i--) {
			OrderInfoImpl orderInfo = orderList.get(i);
			if (orderInfo.getServerOrderId().equals(serverOrderId)) {
				orderList.remove(i);
			}
		}
		//
		handleOrderUpdated(time, (OrderInfoImpl) strategyInfo.findOrderByServerId(serverOrderId), true, BigDecimal.ZERO,
				BigDecimal.ZERO);
	}

	protected void saveInfo(CrowdContext crowdContext) throws Throwable {
		saveInfo(crowdContext, strategyInfo);
	}

	protected void saveTransaction(CrowdContext crowdContext, TransactionInfoImpl transactionInfo) throws Throwable {
//		JSONObject input = new JSONObject();
//		input.put("id", strategyInfo.getId());
//		input.put("transaction", transactionInfo.toJSON());
//		crowdContext.invoke("/backtest-manage/saveTransaction", input);
		transactions.put(transactionInfo.toJSON());
	}

	public final static StrategyInfoImpl createStrategyInfo(CrowdContext crowdContext, String id) throws Throwable {
		JSONObject input = new JSONObject();
		input.put("id", id);
		JSONObject output = crowdContext.invoke("/backtest-manage/loadInfo", input);
		String content = output.getString("content");
		StrategyInfoImpl strategyInfo = new StrategyInfoImpl();
		strategyInfo.fromJSON(new JSONObject(content));
		return strategyInfo;
	}

	public final static void saveStrategyError(CrowdContext crowdContext, String id, String error) throws Throwable {
		StrategyInfoImpl info = createStrategyInfo(crowdContext, id);
		info.setLastError(error);
		saveInfo(crowdContext, info);
	}

	private final static void saveInfo(CrowdContext crowdContext, StrategyInfoImpl info) throws Throwable {
		JSONObject input = new JSONObject();
		input.put("id", info.getId());
		input.put("content", info.toJSON().toString(4));
		crowdContext.invoke("/backtest-manage/saveInfo", input);
	}

}
