package com.crowd.tool.tapis.ba;

import static io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateServerExtensionHandshaker.MAX_WINDOW_SIZE;

import org.json.JSONObject;

import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateClientExtensionHandshaker;

abstract class BinanceSpotBaseAPI extends BinanceBaseAPI {

	public BinanceSpotBaseAPI(String apiKey, String secretKey, String... subscribes) throws Throwable {
		super("https://api.binance.com", "wss://stream.binance.com:9443/ws", apiKey, secretKey, subscribes);
	}

	@Override
	protected final String getListenKey() {
		JSONObject o = callSyncWithApikey("/api/v3/userDataStream", UrlParamsBuilder.build().setMethod("POST"));
		return o.getString("listenKey");
	}

	@Override
	protected final PerMessageDeflateClientExtensionHandshaker getPerMessageDeflateClientExtensionHandshaker() {
		return new PerMessageDeflateClientExtensionHandshaker(6, ZlibCodecFactory.isSupportingWindowSizeAndMemLevel(),
				MAX_WINDOW_SIZE, true, true);
	}

}
