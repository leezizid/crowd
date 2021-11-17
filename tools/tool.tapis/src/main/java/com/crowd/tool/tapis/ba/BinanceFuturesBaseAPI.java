package com.crowd.tool.tapis.ba;

import org.json.JSONObject;

import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateClientExtensionHandshaker;

abstract class BinanceFuturesBaseAPI extends BinanceBaseAPI {

	public BinanceFuturesBaseAPI(String apiKey, String secretKey, String... subscribes) throws Throwable {
		super("https://fapi.binance.com", "wss://fstream.binance.com/ws", apiKey, secretKey, subscribes);
	}

	@Override
	protected final String getListenKey() {
		JSONObject o = callSyncWithApikey("/fapi/v1/listenKey", UrlParamsBuilder.build().setMethod("POST"));
		return o.getString("listenKey");
	}

	@Override
	protected final PerMessageDeflateClientExtensionHandshaker getPerMessageDeflateClientExtensionHandshaker() {
		return new PerMessageDeflateClientExtensionHandshaker();
	}

}
