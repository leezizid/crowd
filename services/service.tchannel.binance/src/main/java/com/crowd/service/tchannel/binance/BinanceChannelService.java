package com.crowd.service.tchannel.binance;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
import com.crowd.tool.tapis.ba.BinanceBaseAPI;
import com.crowd.tool.tapis.ba.BinanceDeliveryTradeAPI;
import com.crowd.tool.tapis.ba.BinanceFuturesTradeAPI;
import com.crowd.tool.tapis.ba.BinanceSpotTradeAPI;
import com.crowd.tool.tapis.ba.BinanceTradeAPI;

public class BinanceChannelService implements CrowdService {

	private static Map<String, BinanceTradeAPI> binanceAPIs = new HashMap<String, BinanceTradeAPI>();

	public void init(CrowdInitContext context) throws Throwable {

	}

	public String getName() {
		return "tchannel.binance";
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
		BinanceTradeAPI binanceAPI = binanceAPIs.get(id);
		if (binanceAPI == null || ((BinanceBaseAPI) binanceAPI).isDisposed()) {
			binanceAPIs.remove(id);
			outputObject.put("status", false);
		} else {
			outputObject.put("status", true);
		}
		outputObject.put("properties", properties);
	}

	@CrowdMethod
	public void info(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String id = inputObject.optString("id");
		int type = inputObject.optInt("type");
		status(context, inputObject, outputObject);
		JSONArray properties = outputObject.getJSONArray("properties");
		if (outputObject.optBoolean("status")) {
			BinanceTradeAPI binanceAPI = binanceAPIs.get(id);
			if (type == 0) {
				//
				JSONObject result = binanceAPI.getAccountInfo();
				JSONArray assetList = result.getJSONArray("balances");
				for (int i = 0; i < assetList.length(); i++) {
					JSONObject assetInfo = assetList.getJSONObject(i);
					double freeBalance = assetInfo.getDouble("free");
					if (freeBalance > 0) {
						addRow(properties, assetInfo.getString("asset") + "余额", freeBalance);
					}
				}
			} else if (type == 1) {
				//
				JSONObject result = binanceAPI.getAccountInfo();
				JSONArray assetList = result.getJSONArray("assets");
				for (int i = 0; i < assetList.length(); i++) {
					JSONObject assetInfo = assetList.getJSONObject(i);
					double walletBalance = assetInfo.getDouble("walletBalance");
					addRow(properties, assetInfo.getString("asset") + "余额", walletBalance);
				}
				addRow(properties, "钱包余额", result.get("totalWalletBalance"));
				addRow(properties, "未实现盈亏", result.get("totalUnrealizedProfit"));
			}
			if (type == 2) {

				JSONObject result = binanceAPI.getAccountInfo();
				JSONArray assetList = result.getJSONArray("assets");
				for (int i = 0; i < assetList.length(); i++) {
					JSONObject assetInfo = assetList.getJSONObject(i);
					double walletBalance = assetInfo.getDouble("walletBalance");
					double unrealizedProfit = assetInfo.getDouble("unrealizedProfit");
					if (walletBalance + unrealizedProfit > 0) {
						addRow(properties, assetInfo.getString("asset") + "余额", walletBalance + unrealizedProfit);
					}
				}
			}
		}
		//
		outputObject.put("properties", properties);
	}

	@CrowdMethod
	public void positions(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String id = inputObject.optString("id");
		int type = inputObject.optInt("type");
		status(context, inputObject, outputObject);
		JSONArray properties = outputObject.getJSONArray("properties");
		if (outputObject.optBoolean("status")) {
			BinanceTradeAPI binanceAPI = binanceAPIs.get(id);
			if (type == 0) {
				outputObject.put("positions", binanceAPI.getPositions());
			} else if (type == 1) {
				outputObject.put("positions", binanceAPI.getPositions());
			}
			if (type == 2) {
				outputObject.put("positions", binanceAPI.getPositions());
			}
		}
		//
		outputObject.put("properties", properties);
	}

	@CrowdMethod
	public void orders(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String id = inputObject.optString("id");
		int type = inputObject.optInt("type");
		status(context, inputObject, outputObject);
		JSONArray properties = outputObject.getJSONArray("properties");
		if (outputObject.optBoolean("status")) {
			BinanceTradeAPI binanceAPI = binanceAPIs.get(id);
			if (type == 0) {
			} else if (type == 1) {
			}
			if (type == 2) {
				outputObject.put("openOrders", binanceAPI.getOpenOrders());
			}
		}
		//
		outputObject.put("properties", properties);
	}

	@CrowdWorker
	public void startWorker(final CrowdWorkerContext crowdContext, JSONObject inputObject) throws Throwable {
		//
		final String id = inputObject.optString("id");
		String apiKey = inputObject.optString("apiKey");
		String secretKey = inputObject.optString("secretKey");
		int type = inputObject.optInt("type");
		//
		if (type == 0) {
			//
			BinanceTradeAPI binanceAPI = binanceAPIs.get(id);
			if (binanceAPI == null) {
				binanceAPI = new BinanceSpotTradeAPI(apiKey, secretKey) {
					protected boolean checkContextDisposed() {
						return crowdContext.isDisposed() || !binanceAPIs.containsKey(id);
					}
				};
				binanceAPIs.put(id, binanceAPI);
				try {
					((BinanceSpotTradeAPI) binanceAPI).run();
				} catch (Throwable t) {
					t.printStackTrace();
				} finally {
					binanceAPIs.remove(id);
				}
			}
		} else if (type == 1) {
			BinanceTradeAPI binanceAPI = binanceAPIs.get(id);
			if (binanceAPI == null) {
				binanceAPI = new BinanceFuturesTradeAPI(apiKey, secretKey) {
					protected boolean checkContextDisposed() {
						return crowdContext.isDisposed() || !binanceAPIs.containsKey(id);
					}
				};
				binanceAPIs.put(id, binanceAPI);
				try {
					((BinanceFuturesTradeAPI) binanceAPI).run();
				} catch (Throwable t) {
					t.printStackTrace();
				} finally {
					binanceAPIs.remove(id);
				}
			}
		}
		if (type == 2) {
			//
			BinanceTradeAPI binanceAPI = binanceAPIs.get(id);
			if (binanceAPI == null) {
				binanceAPI = new BinanceDeliveryTradeAPI(apiKey, secretKey) {
					protected boolean checkContextDisposed() {
						return crowdContext.isDisposed() || !binanceAPIs.containsKey(id);
					}

					@Override
					protected void handleOrderUpdated(String serverOrderId, String symbol, BigDecimal execVolumn,
							BigDecimal execValue, boolean canceled) {
						// XXX:并不是所有交易接口都可以直接拿到clientOrderId，这时候通道自己需要维护一个映射表来查找
//						String[] arr = StringUtils.split(clientOrderId, "_");
//						String strategyId = arr[0];
						JSONObject updateMessageObject = new JSONObject();
						updateMessageObject.put("channelId", id);
						updateMessageObject.put("serverOrderId", serverOrderId);
						updateMessageObject.put("symbol", symbol);
						updateMessageObject.put("execVolumn", execVolumn.doubleValue());
						updateMessageObject.put("execValue", execValue.doubleValue());
						updateMessageObject.put("canceled", canceled);
						try {
							crowdContext.sendMessage("serverOrderUpdated", updateMessageObject);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				};
				binanceAPIs.put(id, binanceAPI);
				try {
					((BinanceDeliveryTradeAPI) binanceAPI).run();
				} finally {
					binanceAPIs.remove(id);
				}
			}
		}
	}

	@CrowdWorker
	public void stopWorker(final CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject)
			throws Throwable {
		String id = inputObject.optString("id");
		BinanceBaseAPI binanceAPI = (BinanceBaseAPI) binanceAPIs.remove(id);
		if (binanceAPI != null) {
			binanceAPI.dispose();
		}
	}

	@CrowdMethod
	public void postOrder(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		//
		String id = inputObject.optString("tChannelId");
		BinanceBaseAPI binanceAPI = (BinanceBaseAPI) binanceAPIs.get(id);
		if (binanceAPI == null || binanceAPI.isDisposed()) {
			throw new IllegalStateException("" + id + "通道服务未启动");
		}
		BinanceTradeAPI tradeAPI = (BinanceTradeAPI) binanceAPI;
		//
		String clientOrderId = inputObject.optString("clientOrderId");
		OrderType type = OrderType.valueOf(inputObject.getString("type"));
		String symbol = inputObject.getString("symbol");
		PositionSide positionSide = PositionSide.valueOf(inputObject.getString("positionSide"));
		BigDecimal volumn = new BigDecimal(inputObject.getString("volumn"));
		BigDecimal price = new BigDecimal(inputObject.getString("price"));
		outputObject.put("serverOrderId", tradeAPI.postOrder(clientOrderId, type, symbol, positionSide, volumn, price));
	}

	@CrowdMethod
	public void cancelOrder(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject)
			throws Throwable {
		String tChannelId = inputObject.optString("tChannelId");
		String symbol = inputObject.optString("symbol");
		String serverOrderId = inputObject.optString("serverOrderId");
		BinanceBaseAPI binanceAPI = (BinanceBaseAPI) binanceAPIs.get(tChannelId);
		if (binanceAPI == null || binanceAPI.isDisposed()) {
			throw new IllegalStateException("" + tChannelId + "通道服务未启动");
		}
		BinanceTradeAPI tradeAPI = (BinanceTradeAPI) binanceAPI;
		tradeAPI.cancelOrder(symbol, serverOrderId);
	}

	@CrowdMethod
	public void synchronizeOrders(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject)
			throws Throwable {
		String tChannelId = inputObject.optString("tChannelId");
		BinanceBaseAPI binanceAPI = (BinanceBaseAPI) binanceAPIs.get(tChannelId);
		if (binanceAPI == null || binanceAPI.isDisposed()) {
			throw new IllegalStateException("" + tChannelId + "通道服务未启动");
		}
		BinanceTradeAPI tradeAPI = (BinanceTradeAPI) binanceAPI;
		JSONArray orders = inputObject.getJSONArray("orders");
		for (int i = 0; i < orders.length(); i++) {
			JSONObject o = orders.getJSONObject(i);
			tradeAPI.synchronizeOrder(o.getString("symbol"), o.getString("serverOrderId"));
		}
	}

	private void addRow(JSONArray properties, String name, Object value) {
		JSONObject row = new JSONObject();
		row.put("name", name);
		row.put("value", value);
		properties.put(row);
	}
}
