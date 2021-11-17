package com.crowd.tool.tapis.ba;

import org.json.JSONObject;

public abstract class BinanceFuturesMarketAPI extends BinanceFuturesBaseAPI {

	public BinanceFuturesMarketAPI(String... subscribes) throws Throwable {
		super(null, null, subscribes);
	}

	@Override
	protected void handleMessage(JSONObject messageObject) {
		System.out.println("[F-Market]:" + messageObject.toString());
	}

}
