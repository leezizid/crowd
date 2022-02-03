package com.crowd.tool.tstrategy.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.TopicSubscriber;
import com.crowd.service.base.TopicSubscriberHandle;
import com.crowd.tool.misc.Products;
import com.crowd.tool.tstrategy.IStrategy;
import com.crowd.tool.tstrategy.OrderInfo;
import com.crowd.tool.tstrategy.StrategyInfo;
import com.crowd.tool.tstrategy.TransactionInfo;

public final class RealStrategyEnv extends BaseStrategyEnv {

	private TopicSubscriberHandle orderUpdatedSubscriberHandle;

	private TopicSubscriberHandle orderFailedSubscriberHandle;

	public RealStrategyEnv(CrowdContext crowdContext, IStrategy strategyInstance, StrategyInfo strategyInfo,
			Products products) throws Throwable {
		super(crowdContext, strategyInstance, strategyInfo, products);
	}

	protected void init(CrowdContext crowdContext) throws Throwable {
		this.orderUpdatedSubscriberHandle = crowdContext.subscribeTopic("serverOrderUpdated", new TopicSubscriber() {
			@Override
			public void messageReceived(JSONObject messageObject) throws Throwable {
				String channelId = messageObject.getString("channelId");
				String symbol = messageObject.getString("symbol");
				if (channelId.equals(strategyInfo.getChannelId())) {
					String serverOrderId = messageObject.getString("serverOrderId");
					OrderInfoImpl matchOrderInfo = (OrderInfoImpl) strategyInfo.findOrderByServerId(serverOrderId);
					if (matchOrderInfo == null && strategyInfo.existSubmitingOrder(symbol)) {
						int waitTime = 0;
						// 有时回报速度很快，委托信息还没有被加入到缓存中，尝试等待多试几次
						while (matchOrderInfo == null) {
							Thread.sleep(100);
							waitTime++;
							if (waitTime > 20) {
								break;
							}
							matchOrderInfo = (OrderInfoImpl) strategyInfo.findOrderByServerId(serverOrderId);
						}
					}
					if (matchOrderInfo != null) {
						if (StringUtils.isNotEmpty(matchOrderInfo.getServerOrderId())) {
							handleOrderUpdated(System.currentTimeMillis(), matchOrderInfo,
									messageObject.optBoolean("canceled"),
									new BigDecimal(messageObject.optDouble("execAmount")),
									new BigDecimal(messageObject.optDouble("execValue")));
						}
					}

				}
			}
		});
		this.orderFailedSubscriberHandle = crowdContext.subscribeTopic("orderConfirmFailed", new TopicSubscriber() {
			@Override
			public void messageReceived(JSONObject messageObject) throws Throwable {
				String strategyId = messageObject.getString("strategyId");
				String clientOrderId = messageObject.getString("clientOrderId");
				if (strategyId.equals(strategyInfo.getId())) {
					OrderInfoImpl matchOrderInfo = (OrderInfoImpl) strategyInfo.findOrder(clientOrderId);
					if (matchOrderInfo != null) {
						handleOrderUpdated(System.currentTimeMillis(), matchOrderInfo, true, BigDecimal.ZERO,
								BigDecimal.ZERO);
					}
				}
			}
		});
		// 查询未完成订单，更新订单
		try {
			this.synchronizeOrders(crowdContext);
		} catch (Throwable t) {
		}
	}

	public final void dispose(CrowdContext crowdContext) {
		orderUpdatedSubscriberHandle.unsubscribe();
		orderFailedSubscriberHandle.unsubscribe();
	}

	/** 
	 * 
	 */
	private void synchronizeOrders(CrowdContext crowdContext) throws Throwable {
		List<OrderInfo> openOrders = new ArrayList<OrderInfo>();
		for (TransactionInfo transactionInfo : strategyInfo.getTransactions()) {
			for (OrderInfo orderInfo : transactionInfo.getOrders()) {
				if (StringUtils.isNotEmpty(orderInfo.getServerOrderId()) && !orderInfo.isFinished()) {
					OrderInfoImpl o = new OrderInfoImpl();
					o.fromJSON(((OrderInfoImpl) orderInfo).toJSON());
					openOrders.add(o);
				}
			}
		}
		//
		JSONObject input = new JSONObject();
		JSONArray arr = new JSONArray();
		for (OrderInfo orderInfo : openOrders) {
			JSONObject o = new JSONObject();
			o.put("symbol", orderInfo.getSymbol());
			o.put("serverOrderId", orderInfo.getServerOrderId());
			arr.put(o);
		}
		input.put("orders", arr);
		input.put("tChannelId", strategyInfo.getChannelId());
		crowdContext.invoke("/tchannel/synchronizeOrders", input);
	}

	@Override
	protected String postOrder(CrowdContext context, OrderInfo orderInfo) throws Throwable {
		JSONObject orderObject = new JSONObject();
		orderObject.put("tChannelId", strategyInfo.getChannelId());
		orderObject.put("clientOrderId", orderInfo.getClientOrderId());
		orderObject.put("type", orderInfo.getType().name());
		orderObject.put("symbol", orderInfo.getSymbol());
		orderObject.put("positionSide", orderInfo.getPositionSide().name());
		orderObject.put("amount", String.valueOf(orderInfo.getAmount()));
		orderObject.put("price", String.valueOf(orderInfo.getPrice()));
		JSONObject result = context.invoke("/tchannel/postOrder", orderObject);
		return result.getString("serverOrderId");
	}

	@Override
	protected void cancelOrder(CrowdContext context, long time, String symbol, String serverOrderId) throws Throwable {
		JSONObject inputObject = new JSONObject();
		inputObject.put("tChannelId", strategyInfo.getChannelId());
		inputObject.put("symbol", symbol);
		inputObject.put("serverOrderId", serverOrderId);
		context.invoke("/tchannel/cancelOrder", inputObject);
	}

	protected void saveInfo(CrowdContext crowdContext) throws Throwable {
		saveInfo(crowdContext, strategyInfo);
	}

	protected void saveTransaction(CrowdContext crowdContext, TransactionInfoImpl transactionInfo) throws Throwable {
		JSONObject input = new JSONObject();
		input.put("id", strategyInfo.getId());
		input.put("transaction", transactionInfo.toJSON());
		crowdContext.invoke("/strategy-manage/saveTransaction", input);
	}

	public final static StrategyInfoImpl createStrategyInfo(CrowdContext crowdContext, String id) throws Throwable {
		JSONObject input = new JSONObject();
		input.put("id", id);
		JSONObject output = crowdContext.invoke("/strategy-manage/loadInfo", input);
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
		crowdContext.invoke("/strategy-manage/saveInfo", input);
	}

}
