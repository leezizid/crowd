package com.crowd.tool.tapis.ba;

import org.json.JSONObject;

public abstract class BinanceSpotMarketAPI extends BinanceSpotBaseAPI {

	public BinanceSpotMarketAPI(String... subscribes) throws Throwable {
		super(null, null, subscribes);
	}

	@Override
	protected void handleMessage(JSONObject messageObject) {
		System.out.println("[SPOT-Market]:" + messageObject.toString());
	}

}
