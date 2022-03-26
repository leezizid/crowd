package com.crowd.service.tchannel.ctp;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.CrowdInitContext;
import com.crowd.service.base.CrowdMethod;
import com.crowd.service.base.CrowdService;
import com.crowd.service.base.CrowdWorker;
import com.crowd.service.base.CrowdWorkerContext;
import com.crowd.tool.misc.OrderType;
import com.crowd.tool.misc.PositionSide;
import com.crowd.tool.tapis.ctp.AccountInfo;
import com.crowd.tool.tapis.ctp.ConnectInfo;
import com.crowd.tool.tapis.ctp.CtpTradeAPI;
import com.crowd.tool.tapis.ctp.OrderInfo;
import com.crowd.tool.tapis.ctp.PositionInfo;

public class CtpChannelService implements CrowdService {

	private static Map<String, CtpTradeAPI> channels = new HashMap<String, CtpTradeAPI>();

	@Override
	public void init(CrowdInitContext context) throws Throwable {
	}

	@Override
	public String getName() {
		return "tchannel.ctp";
	}

	@CrowdMethod
	public void status(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		//
		String id = inputObject.optString("id");
		//
		JSONArray properties = new JSONArray();
		//
		addRow(properties, "标识", inputObject.optString("id"));
		addRow(properties, "名称", inputObject.optString("name"));
		addRow(properties, "描述", inputObject.optString("desc"));

		//
		CtpTradeAPI tradeAPI = channels.get(id);
		if (tradeAPI == null || tradeAPI.isDisposed()) {
			channels.remove(id);
			outputObject.put("status", false);
		} else {
			outputObject.put("status", true);
		}
		outputObject.put("properties", properties);
	}

	@CrowdMethod
	public void info(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String id = inputObject.optString("id");
		status(context, inputObject, outputObject);
		JSONArray properties = outputObject.getJSONArray("properties");
		if (outputObject.optBoolean("status")) {
			CtpTradeAPI tradeAPI = channels.get(id);
			if (tradeAPI.isLogin()) {
				addRow(properties, "登录状态", "是");
				try {
					AccountInfo accountInfo = tradeAPI.getAccountInfo();
					addRow(properties, "权益", String.valueOf(accountInfo.getBalanceValue().doubleValue()));
					addRow(properties, "可用", accountInfo.getAvailableValue().doubleValue());
					addRow(properties, "占用保证金", accountInfo.getCurrentMarginValue().doubleValue());
					addRow(properties, "持仓盈亏", accountInfo.getPositionProfit().doubleValue());
				} catch (Throwable t) {
					addRow(properties, "错误", t.getMessage());
				}
			} else {
				addRow(properties, "登录状态", "否");
			}
		}
		//
		outputObject.put("properties", properties);
	}

	@CrowdMethod
	public void positions(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String id = inputObject.optString("id");
		status(context, inputObject, outputObject);
		JSONArray properties = outputObject.getJSONArray("properties");
		if (outputObject.optBoolean("status")) {
			CtpTradeAPI tradeAPI = channels.get(id);
			if (tradeAPI.isLogin()) {
				JSONArray positionArray = new JSONArray();
				try {
					for (PositionInfo positionInfo : tradeAPI.getPositions()) {
						if (positionInfo.getTotalVolumn() > 0) {
							JSONObject positionObj = new JSONObject();
							positionObj.put("symbol", positionInfo.getSymbol());
							positionObj.put("volumn",
									positionInfo.getPositionSide() == PositionSide.Long ? positionInfo.getTotalVolumn()
											: -positionInfo.getTotalVolumn());
							positionObj.put("marketPrice", positionInfo.getMarketPrice());
							positionArray.put(positionObj);
						}
					}
				} catch (Throwable t) {
					outputObject.put("dataError", t.getMessage());
				}
				outputObject.put("positions", positionArray);
			}
		}
		//
		outputObject.put("properties", properties);
	}

	@CrowdMethod
	public void orders(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String id = inputObject.optString("id");
		status(context, inputObject, outputObject);
		JSONArray properties = outputObject.getJSONArray("properties");
		if (outputObject.optBoolean("status")) {
			CtpTradeAPI tradeAPI = channels.get(id);
			if (tradeAPI.isLogin()) {
				JSONArray orderArray = new JSONArray();
				try {
					for (OrderInfo orderInfo : tradeAPI.getOrders()) {
						if (orderInfo.getVolumn() == orderInfo.getExecVolumn() || orderInfo.isCanceled()) {
							continue;
						}
						JSONObject orderObj = new JSONObject();
						orderObj.put("symbol", orderInfo.getSymbol());
						orderObj.put("serverOrderId", orderInfo.getServerOrderId());
						// orderObj.put("clientOrderId", orderInfo.getClientOrderId());
						orderObj.put("volumn", orderInfo.getVolumn());
						orderObj.put("execVolumn", orderInfo.getExecVolumn());
						orderObj.put("price", orderInfo.getPrice());
						orderObj.put("type", orderInfo.getType());
						orderObj.put("positionSide", orderInfo.getPositionSide());
						orderObj.put("time", orderInfo.getTime());
						orderArray.put(orderObj);
					}
				} catch (Throwable t) {
					t.printStackTrace();
					outputObject.put("dataError", t.getMessage());
				}
				outputObject.put("openOrders", orderArray);
			}
		}
		//
		outputObject.put("properties", properties);
	}

	@CrowdWorker
	public void startWorker(final CrowdWorkerContext crowdContext, JSONObject inputObject) throws Throwable {
		//
		final String id = inputObject.optString("id");
		String traderFront = inputObject.optString("traderFront");
		String brokerId = inputObject.getString("brokerId");
		String userId = inputObject.getString("userId");
		String password = inputObject.getString("password");
		String investorId = inputObject.getString("investorId");
		String authCode = inputObject.getString("authCode");
		String macAddress = inputObject.getString("macAddress");
		String appId = "SHINNY_Q7V2_2.93";
		String productInfo = "SHINNYQ7V2";
		ConnectInfo connectInfo = new ConnectInfo(traderFront, brokerId, userId, password, investorId, authCode, appId,
				productInfo, macAddress);
		//
		CtpTradeAPI ctpTradeAPI = channels.get(id);
		if (ctpTradeAPI == null) {
			ctpTradeAPI = new CtpTradeAPI(id, connectInfo) {
				protected boolean checkContextDisposed() {
					return crowdContext.isDisposed() || !channels.containsKey(id);
				}

				@Override
				protected void handleOrderUpdated(String serverOrderId, String symbol, int execVolumn, BigDecimal execValue,
						boolean canceled) {
					JSONObject updateMessageObject = new JSONObject();
					updateMessageObject.put("channelId", id);
					updateMessageObject.put("serverOrderId", serverOrderId);
					updateMessageObject.put("symbol", symbol);
					updateMessageObject.put("execVolumn", execVolumn);
					updateMessageObject.put("execValue", execValue.doubleValue());
					updateMessageObject.put("canceled", canceled);
					try {
						crowdContext.sendMessage("serverOrderUpdated", updateMessageObject);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}

			};
			channels.put(id, ctpTradeAPI);
			try {
				ctpTradeAPI.run();
			} finally {
				channels.remove(id);
			}
		}
	}

	@CrowdWorker
	public void stopWorker(final CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject)
			throws Throwable {
		String id = inputObject.optString("id");
		CtpTradeAPI ctpTradeAPI = channels.get(id);
		if (ctpTradeAPI != null) {
			ctpTradeAPI.dispose("");
		}
	}

	@CrowdMethod
	public void postOrder(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String tChannelId = inputObject.optString("tChannelId");
		CtpTradeAPI ctpTradeAPI = channels.get(tChannelId);
		if (ctpTradeAPI == null || ctpTradeAPI.isDisposed()) {
			throw new IllegalStateException("" + tChannelId + "通道服务未启动");
		}
		String[] symbolInfo = StringUtils.split(inputObject.getString("symbol"), ".");
		PositionSide positionSide = PositionSide.valueOf(inputObject.getString("positionSide"));
		OrderType orderType = OrderType.valueOf(inputObject.getString("type"));
		float price = Float.parseFloat(inputObject.getString("price"));
		int volumn = Integer.parseInt(inputObject.getString("volumn"));
		outputObject.put("serverOrderId",
				ctpTradeAPI.postOrder(symbolInfo[0], symbolInfo[1], orderType, positionSide, price, volumn));
	}

	@CrowdMethod
	public void cancelOrder(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject)
			throws Throwable {
		String tChannelId = inputObject.optString("tChannelId");
		String symbol = inputObject.optString("symbol");
		String serverOrderId = inputObject.optString("serverOrderId");
		CtpTradeAPI ctpTradeAPI = channels.get(tChannelId);
		if (ctpTradeAPI == null || ctpTradeAPI.isDisposed()) {
			throw new IllegalStateException("" + tChannelId + "通道服务未启动");
		}
		ctpTradeAPI.cancelOrder(symbol, serverOrderId);
	}

	@CrowdMethod
	public void synchronizeOrders(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject)
			throws Throwable {
		String tChannelId = inputObject.optString("tChannelId");
		CtpTradeAPI ctpTradeAPI = channels.get(tChannelId);
		if (ctpTradeAPI == null || ctpTradeAPI.isDisposed()) {
			throw new IllegalStateException("" + tChannelId + "通道服务未启动");
		}
		JSONArray orders = inputObject.getJSONArray("orders");
		for (int i = 0; i < orders.length(); i++) {
			JSONObject o = orders.getJSONObject(i);
			ctpTradeAPI.synchronizeOrder(o.getString("serverOrderId"));
		}
	}

	private void addRow(JSONArray properties, String name, Object value) {
		JSONObject row = new JSONObject();
		row.put("name", name);
		row.put("value", value);
		properties.put(row);
	}
}
