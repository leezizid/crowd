package com.crowd.tool.tapis.ba;

import org.json.JSONObject;

import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateClientExtensionHandshaker;

abstract class BinanceDeliveryBaseAPI extends BinanceBaseAPI {

	public BinanceDeliveryBaseAPI(String apiKey, String secretKey, String... subscribes) throws Throwable {
		super("https://dapi.binance.com", "wss://dstream.binance.com/ws", apiKey, secretKey, subscribes);
	}

	@Override
	protected final String getListenKey() {
		JSONObject o = callSyncWithApikey("/dapi/v1/listenKey", UrlParamsBuilder.build().setMethod("POST"));
		return o.getString("listenKey");
	}

	@Override
	protected final PerMessageDeflateClientExtensionHandshaker getPerMessageDeflateClientExtensionHandshaker() {
		return new PerMessageDeflateClientExtensionHandshaker();
	}

}
