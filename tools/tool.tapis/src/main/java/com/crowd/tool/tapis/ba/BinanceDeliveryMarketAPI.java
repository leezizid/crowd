package com.crowd.tool.tapis.ba;

import org.json.JSONObject;

public abstract class BinanceDeliveryMarketAPI extends BinanceDeliveryBaseAPI {

	public BinanceDeliveryMarketAPI(String... subscribes) throws Throwable {
		super(null, null, subscribes);
	}

	@Override
	protected void handleMessage(JSONObject messageObject) {
		System.out.println("[D-Market]:" + messageObject.toString());
	}

}
