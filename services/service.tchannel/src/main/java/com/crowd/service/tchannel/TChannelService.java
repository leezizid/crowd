package com.crowd.service.tchannel;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.CrowdInitContext;
import com.crowd.service.base.CrowdMethod;
import com.crowd.service.base.CrowdService;
import com.crowd.tool.tapis.ctp.CTPProducts;

public class TChannelService implements CrowdService {

	private static List<JSONObject> tChannelList;

	public void init(CrowdInitContext context) throws Throwable {
//		new BinanceSpotMarketAPI("btcusdt@ticker");
//		new BinanceFuturesMarketAPI("btcusdt@ticker");
//		final BinanceDeliveryMarketAPI binanceDeliveryMarketAPI = new BinanceDeliveryMarketAPI("btcusd_perp@ticker") {
//			@Override
//			protected void handleMessage(JSONObject messageObject) {
//				System.out.println(messageObject);
//			}
//
//		};
//		new Thread(new Runnable() {
//			public void run() {
//				binanceDeliveryMarketAPI.init();
//			}
//		}).start();
		CTPProducts.init();
	}

	public String getName() {
		return "tchannel";
	}

	private static void initChannelList(CrowdContext context) {
		JSONArray arr = new JSONArray();
		try {
			arr = new JSONArray(context.load("tchannels.json"));
		} catch (Throwable t) {
			t.printStackTrace();
		}
		tChannelList = new ArrayList<JSONObject>();
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			tChannelList.add(o);
//			tChannelList.add(new TChannelInfo(o.getString("id"), o.getInt("type"), o.getString("name"),
//					o.getString("desc"), o.getString("apiKey"), o.getString("secretKey"), o.getString("parentKey"), o.getJSONObject("param")));
		}
	}

	@CrowdMethod
	public void list(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		initChannelList(context);
		//
		JSONObject binanceChannelsNode = new JSONObject();
		String vendorBinance = "Binance";
		binanceChannelsNode.put("label", vendorBinance);
		binanceChannelsNode.put("key", vendorBinance);
		binanceChannelsNode.put("disabled", true);
		JSONArray childNodes = new JSONArray();
		for (JSONObject channelInfo : tChannelList) {
			if (vendorBinance.equalsIgnoreCase(channelInfo.getString("vendor"))) {
				childNodes.put(new JSONObject("{key: \"" + channelInfo.getString("id") + "\", label: \""
						+ channelInfo.getString("name") + "\"}"));
			}
		}
		binanceChannelsNode.put("children", childNodes);
		//
		JSONObject kuaiqiChannelsNode = new JSONObject();
		String vendorKuaiqi = "Kuaiqi";
		kuaiqiChannelsNode.put("key", vendorKuaiqi);
		kuaiqiChannelsNode.put("label", vendorKuaiqi);
		kuaiqiChannelsNode.put("disabled", true);
		childNodes = new JSONArray();
		for (JSONObject channelInfo : tChannelList) {
			if (vendorKuaiqi.equalsIgnoreCase(channelInfo.getString("vendor"))) {
				childNodes.put(new JSONObject("{key: \"" + channelInfo.getString("id") + "\", label: \""
						+ channelInfo.getString("name") + "\"}"));
			}
		}
		kuaiqiChannelsNode.put("children", childNodes);
		//
		JSONObject ctpChannelsNode = new JSONObject();
		String vendorCTP = "CTP";
		ctpChannelsNode.put("key", vendorCTP);
		ctpChannelsNode.put("label", vendorCTP);
		ctpChannelsNode.put("disabled", true);
		childNodes = new JSONArray();
		for (JSONObject channelInfo : tChannelList) {
			if (vendorCTP.equalsIgnoreCase(channelInfo.getString("vendor"))) {
				childNodes.put(new JSONObject("{key: \"" + channelInfo.getString("id") + "\", label: \""
						+ channelInfo.getString("name") + "\"}"));
			}
		}
		ctpChannelsNode.put("children", childNodes);
		//
		JSONArray treeNodes = new JSONArray();
		treeNodes.put(binanceChannelsNode);
		treeNodes.put(kuaiqiChannelsNode);
		treeNodes.put(ctpChannelsNode);
		//
		JSONArray expandedKeys = new JSONArray();
		expandedKeys.put(vendorBinance);
		expandedKeys.put(vendorKuaiqi);
		expandedKeys.put(vendorCTP);
		//
		outputObject.put("channels", treeNodes);
		outputObject.put("expandedKeys", expandedKeys);
	}

	@CrowdMethod
	public void status(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String id = inputObject.optString("id");
		JSONObject channelInfo = findTChannelInfo(context, id);
		try {
			jsonCopy(context.invoke(getServiceNamePrefix(channelInfo) + "status", channelInfo), outputObject);
		} catch (Throwable t) {
			defaultStatus(channelInfo, outputObject);
		}
	}

	@CrowdMethod
	public void info(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String id = inputObject.optString("id");
		JSONObject channelInfo = findTChannelInfo(context, id);
		try {
			int type = inputObject.getInt("type");
			String methodName = "info";
			if (type == 1) {
				methodName = "positions";
			} else if (type == 2) {
				methodName = "orders";
			}
			jsonCopy(context.invoke(getServiceNamePrefix(channelInfo) + methodName, channelInfo), outputObject);
		} catch (Throwable t) {
			defaultStatus(channelInfo, outputObject);
		}
	}

//	@CrowdMethod
//	public void products(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject) throws Throwable {
//		String id = inputObject.optString("id");
//		TChannelInfo channelInfo = findTChannelInfo(crowdContext, id);
//		outputObject.put("products", channelInfo.getProducts().toJSONArray());
//	}

	@CrowdMethod
	public void start(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		//
		String id = inputObject.optString("id");
		JSONObject channelInfo = findTChannelInfo(crowdContext, id);
		JSONObject result = crowdContext.invoke(getServiceNamePrefix(channelInfo) + "status", channelInfo);
		if (result.optBoolean("status")) {
			throw new IllegalStateException("服务已经处于启动状态");
		} else {
			crowdContext.asyncInvoke(getServiceNamePrefix(channelInfo) + "startWorker", channelInfo);
		}
	}

	@CrowdMethod
	public void stop(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		//
		String id = inputObject.optString("id");
		JSONObject channelInfo = findTChannelInfo(crowdContext, id);
		crowdContext.invoke(getServiceNamePrefix(channelInfo) + "stopWorker", channelInfo);
	}

	private static String getServiceNamePrefix(JSONObject channelInfo) {
		return "/tchannel." + channelInfo.getString("vendor").toLowerCase() + "/";
	}

	private void jsonCopy(JSONObject source, JSONObject target) {
		for (String key : source.keySet()) {
			target.put(key, source.get(key));
		}
	}

	@CrowdMethod
	public void postOrder(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String id = inputObject.optString("tChannelId");
		JSONObject channelInfo = findTChannelInfo(crowdContext, id);
		jsonCopy(crowdContext.invoke(getServiceNamePrefix(channelInfo) + "postOrder", inputObject), outputObject);
	}

	@CrowdMethod
	public void cancelOrder(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject)
			throws Throwable {
		String tChannelId = inputObject.optString("tChannelId");
		JSONObject channelInfo = findTChannelInfo(crowdContext, tChannelId);
		jsonCopy(crowdContext.invoke(getServiceNamePrefix(channelInfo) + "cancelOrder", inputObject), outputObject);
	}

	@CrowdMethod
	public void synchronizeOrders(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject)
			throws Throwable {
		String tChannelId = inputObject.optString("tChannelId");
		JSONObject channelInfo = findTChannelInfo(crowdContext, tChannelId);
		jsonCopy(crowdContext.invoke(getServiceNamePrefix(channelInfo) + "synchronizeOrders", inputObject),
				outputObject);
	}

	private static JSONObject findTChannelInfo(CrowdContext context, String id) {
		if (tChannelList == null) {
			initChannelList(context);
		}
		for (JSONObject info : tChannelList) {
			if (info.getString("id").equals(id)) {
				return info;
			}
		}
		throw new IllegalArgumentException("找不到指定ID的通道");
	}

	static void cancelOrder(CrowdContext crowdContext, String tChannelId, String symbol, String serverOrderId)
			throws Throwable {
		JSONObject channelInfo = findTChannelInfo(crowdContext, tChannelId);
		//
		JSONObject inputObject = new JSONObject();
		inputObject.put("tChannelId", tChannelId);
		inputObject.put("symbol", symbol);
		inputObject.put("serverOrderId", serverOrderId);
		crowdContext.invoke(getServiceNamePrefix(channelInfo) + "cancelOrder", inputObject);
	}

	private void defaultStatus(JSONObject channelInfo, JSONObject outputObject) {
		outputObject.put("status", false);
		JSONArray properties = new JSONArray();
		addRow(properties, "标识", channelInfo.optString("id"));
		addRow(properties, "名称", channelInfo.optString("name"));
		addRow(properties, "描述", channelInfo.optString("desc"));
		outputObject.put("properties", properties);
	}

	private void addRow(JSONArray properties, String name, Object value) {
		JSONObject row = new JSONObject();
		row.put("name", name);
		row.put("value", value);
		properties.put(row);
	}
}
