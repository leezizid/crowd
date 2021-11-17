package com.crowd.tool.tapis.ba;

import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.tool.misc.OrderType;
import com.crowd.tool.misc.PositionSide;

public abstract class BinanceSpotTradeAPI extends BinanceSpotBaseAPI implements BinanceTradeAPI {

	public BinanceSpotTradeAPI(String apiKey, String secretKey) throws Throwable {
		super(apiKey, secretKey);
	}

	@Override
	protected final void handleMessage(JSONObject messageObject) {
		System.out.println("[SPOT-Account]:" + messageObject.toString());
	}

	@Override
	public JSONObject getAccountInfo() throws Throwable {
		return callSyncWithSignature("/api/v3/account", UrlParamsBuilder.build());
	}

	@Override
	public String postOrder(String clientOrderId, OrderType type, String symbol, PositionSide positionSide, BigDecimal amount,
			BigDecimal price) throws Throwable {
		return null;
	}

	@Override
	public void cancelOrder(String symbol, String orderId) throws Throwable {

	}

	@Override
	public JSONArray getPositions() throws Throwable {
		return new JSONArray();
	}

	@Override
	public JSONArray getOpenOrders() throws Throwable {
		return new JSONArray();
	}

	public void synchronizeOrder(String symbol, String serverOrderId) throws Throwable {

	}

}
