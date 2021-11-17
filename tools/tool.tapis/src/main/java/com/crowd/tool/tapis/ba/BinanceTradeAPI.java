package com.crowd.tool.tapis.ba;

import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.tool.misc.OrderType;
import com.crowd.tool.misc.PositionSide;

public interface BinanceTradeAPI {

	public JSONObject getAccountInfo() throws Throwable;

	public String postOrder(String clientOrderId, OrderType type, String symbol, PositionSide positionSide, BigDecimal amount,
			BigDecimal price) throws Throwable;

	public void cancelOrder(String symbol, String serverOrderId) throws Throwable;

	public JSONArray getPositions() throws Throwable;

	public JSONArray getOpenOrders() throws Throwable;

	public void synchronizeOrder(String symbol, String serverOrderId) throws Throwable;

}
