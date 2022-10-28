package com.crowd.tool.tapis.ba;

import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.tool.misc.OrderType;
import com.crowd.tool.misc.PositionSide;

public abstract class BinanceDeliveryTradeAPI extends BinanceDeliveryBaseAPI implements BinanceTradeAPI {

	public BinanceDeliveryTradeAPI(String apiKey, String secretKey) throws Throwable {
		super(apiKey, secretKey);
	}

	@Override
	protected final void handleMessage(JSONObject messageObject) {
		if (messageObject.optString("e").equals("ORDER_TRADE_UPDATE")) {
			try {
				JSONObject o = messageObject.getJSONObject("o");
				long orderId = o.getLong("i");
				String symbol = o.getString("s");
				JSONObject orderObject = queryOrder(symbol, orderId);
				String status = orderObject.getString("status");
				handleOrderUpdated(String.valueOf(orderId), symbol, new BigDecimal(orderObject.getString("executedQty")),
						new BigDecimal(orderObject.getString("cumBase")), status.equals("CANCELED"));
			} catch (Throwable t) {

			}
		}
		System.out.println("[D-Account]:" + messageObject.toString());
	}

	@Override
	public JSONObject getAccountInfo() throws Throwable {
		return callSyncWithSignature("/dapi/v1/account", UrlParamsBuilder.build());
	}

	public JSONObject queryOrder(String symbol, long serverOrderId) throws Throwable {
		return callSyncWithSignature("/dapi/v1/order", UrlParamsBuilder.build().setMethod("GET")
				.putToUrl("symbol", symbol).putToUrl("orderId", serverOrderId));
	}

	public void synchronizeOrder(String symbol, String serverOrderId) throws Throwable {
		JSONObject orderObject = queryOrder(symbol, Long.parseLong(serverOrderId));
		handleOrderUpdated(String.valueOf(serverOrderId), symbol, new BigDecimal(orderObject.getString("executedQty")),
				new BigDecimal(orderObject.getString("cumBase")), orderObject.getString("status").equals("CANCELED")
						|| orderObject.getString("status").equals("EXPIRED"));
	}

	protected abstract void handleOrderUpdated(String serverOrderId, String symbol, BigDecimal execVolume,
			BigDecimal execValue, boolean canceled);

	@Override
	public String postOrder(String clientOrderId, OrderType type, String symbol, PositionSide positionSide,
			BigDecimal volume, BigDecimal price) throws Throwable {
		String side;
		if (positionSide == PositionSide.Long) {
			side = type == OrderType.Open ? "BUY" : "SELL";
		} else if (positionSide == PositionSide.Short) {
			side = type == OrderType.Open ? "SELL" : "BUY";
		} else {
			throw new IllegalArgumentException();
		}
		UrlParamsBuilder urlParams = UrlParamsBuilder.build().setMethod("POST");
		urlParams.putToUrl("symbol", symbol);
		urlParams.putToUrl("side", side);
		urlParams.putToUrl("positionSide", positionSide);
		urlParams.putToUrl("type", "LIMIT");
		urlParams.putToUrl("timeInForce", "GTC");
		urlParams.putToUrl("quantity", volume);
		urlParams.putToUrl("price", price);
		urlParams.putToUrl("newClientOrderId", clientOrderId);
		JSONObject result = callSyncWithSignature("/dapi/v1/order", urlParams);
		return String.valueOf(result.getLong("orderId"));
	}

	@Override
	public void cancelOrder(String symbol, String orderId) throws Throwable {
		UrlParamsBuilder urlParams = UrlParamsBuilder.build().setMethod("DELETE");
		urlParams.putToUrl("symbol", symbol);
		urlParams.putToUrl("orderId", orderId);
		callSyncWithSignature("/dapi/v1/order", urlParams);
	}

	@Override
	public JSONArray getPositions() throws Throwable {
		JSONArray positionArray = new JSONArray();
		JSONObject result = callSyncWithSignature("/dapi/v1/positionRisk", UrlParamsBuilder.build());
		if (result.has("data")) {
			JSONArray arr = result.getJSONArray("data");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject o = arr.getJSONObject(i);
				if (o.optInt("positionAmt") != 0) {
					JSONObject positionObject = new JSONObject();
					positionObject.put("symbol", o.getString("symbol"));
					positionObject.put("volume", o.optInt("positionAmt"));
					positionObject.put("liquidationPrice", o.optDouble("liquidationPrice"));
					positionObject.put("marketPrice", o.optDouble("markPrice"));
					positionArray.put(positionObject);
				}
			}
		}
		return positionArray;
	}

	@Override
	public JSONArray getOpenOrders() throws Throwable {
		JSONArray orderArray = new JSONArray();
		JSONObject result = callSyncWithSignature("/dapi/v1/openOrders", UrlParamsBuilder.build());
		if (result.has("data")) {
			JSONArray arr = result.getJSONArray("data");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject o = arr.getJSONObject(i);
				JSONObject orderObject = new JSONObject();
				orderObject.put("symbol", o.getString("symbol"));
				orderObject.put("clientOrderId", o.getString("clientOrderId"));
				orderObject.put("serverOrderId", o.getLong("orderId"));
				orderObject.put("time", o.getLong("time"));
				orderObject.put("type", o.getString("side"));
				orderObject.put("positionSide", o.getString("positionSide"));
				orderObject.put("volume", o.getString("origQty"));
				orderObject.put("execVolume", o.getString("executedQty"));
				orderObject.put("price", o.getString("price"));
				orderObject.put("avgPrice", o.getString("avgPrice"));
				orderArray.put(orderObject);
			}
		}
		return orderArray;
	}

}
