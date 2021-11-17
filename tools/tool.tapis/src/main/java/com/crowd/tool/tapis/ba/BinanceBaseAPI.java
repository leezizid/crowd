package com.crowd.tool.tapis.ba;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONObject;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateFrameClientExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateClientExtensionHandshaker;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class BinanceBaseAPI {

	private final static int WS_CLOSE = 0; // 已经关闭，属于稳定状态，可能由关闭时间定时或者在APP转到前台时触发转至WS_WAIT_CONNECTING，
	private final static int WS_CONNECTING = 10; // 正在连接；属于临时状态，一定会在短期内转至WS_CONNECTED或者WS_CLOSE
	private final static int WS_CONNECTED = 100; // 已经连接；属于稳定状态，在关闭后转为WS_CLOSE
	private final static int TIME_WS_CHECK = 30 * 1000; // 检查WS状态的间隔
	private final static int TIME_WS_CHECK_TIMEOUT = 45 * 1000; // 检查时间出现异常的时间（即最后消息时间不应该超过这个时间，超过即认为掉线自动重连）

	private String apiKey;

	private String secretKey;

	private int WS_status = WS_CLOSE;

	private long WS_lastMessageTime = 0;

	private long WS_lastCheckTime = 0;

	private NioEventLoopGroup eventLoopGroup;
	private Channel channel;

	private String restServerUrl;

	private URI wsServerUri;
	private String wsServerHost;
	private int wsServerPort;
	private SslContext sslCtx;

	private String listenKey;
	private String[] subscribes;

	private boolean disposed;

	private static final OkHttpClient client = new OkHttpClient();

	/**
	 * @param marketType
	 */
	public BinanceBaseAPI(String restServerUrl, String wsServerUrl, String apiKey, String secretKey,
			String... subscribes) throws Throwable {
		this.apiKey = apiKey;
		this.secretKey = secretKey;
		this.subscribes = subscribes;
		this.restServerUrl = restServerUrl;
		//
		wsServerUri = new URI(wsServerUrl);
		String scheme = wsServerUri.getScheme() == null ? "ws" : wsServerUri.getScheme();
		if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
			throw new IllegalArgumentException();
		}
		wsServerHost = wsServerUri.getHost() == null ? "127.0.0.1" : wsServerUri.getHost();
		wsServerPort = wsServerUri.getPort();
		if (wsServerPort == -1) {
			if ("ws".equalsIgnoreCase(scheme)) {
				wsServerPort = 80;
			} else if ("wss".equalsIgnoreCase(scheme)) {
				wsServerPort = 443;
			}
		}
		if ("wss".equalsIgnoreCase(scheme)) {
			sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		} else {
			sslCtx = null;
		}
	}

	public final void run() {
		if (apiKey != null && secretKey != null) {
			listenKey = getListenKey();
		}
		while (!isDisposed()) {
			try {
				if (WS_status == WS_CLOSE) {
					tryConnect();
				} else if (WS_status == WS_CONNECTED) {
					long now = System.currentTimeMillis();
					// 如果最后消息时间超过check超时时间，则认为连接已经失效，尝试关闭连接并标记连接状态
					if (now - WS_lastMessageTime > TIME_WS_CHECK_TIMEOUT) {
						try {
							WS_status = WS_CLOSE;
							channel.close();
						} catch (Throwable t) {
						}
					} else if (now - WS_lastCheckTime > TIME_WS_CHECK) {
						querySubscribes(); // 通过发送查询订阅来保持与服务器端通信
						WS_lastCheckTime = now;
						try {
							if (apiKey != null && secretKey != null) {
								String newListenKey = getListenKey();
//								System.out.println("newListenKey:" + newListenKey + "---oldListenKey:" + listenKey);
								if (!newListenKey.equals(listenKey)) {
									subscribeEvents();
								}
							}
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				try {
					Thread.sleep(3000);
				} catch (Throwable t) {

				}
			}
		}
		try {
			channel.close();
		} catch (Throwable t) {

		}
	}

	protected abstract boolean checkContextDisposed();

	public final void dispose() {
		disposed = true;
	}

	public final boolean isDisposed() {
		return disposed || checkContextDisposed();
	}

	public JSONObject callSync(String path, UrlParamsBuilder builder) {
		return callSync(createRequest(path, builder));
	}

	public JSONObject callSyncWithApikey(String path, UrlParamsBuilder builder) {
		return callSync(createRequestWithApikey(path, builder));
	}

	public JSONObject callSyncWithSignature(String path, UrlParamsBuilder builder) {
		return callSync(createRequestWithSignature(path, builder));
	}

	private JSONObject callSync(Request request) {
		try {
			String str;
			Response response = client.newCall(request).execute();
			if (response != null && response.body() != null) {
				str = response.body().string();
				response.close();
			} else {
				throw new BinanceApiException(BinanceApiException.ENV_ERROR,
						"[Invoking] Cannot get the response from server");
			}
			JSONObject result = null;
			if (str.startsWith("[")) {
				result = new JSONObject();
				result.put("data", new JSONArray(str));
			} else {
				result = new JSONObject(str);
			}
			checkResponse(result);
			return result;
		} catch (BinanceApiException e) {
			throw e;
		} catch (Exception e) {
			throw new BinanceApiException(BinanceApiException.ENV_ERROR,
					"[Invoking] Unexpected error: " + e.getMessage());
		}
	}

	private void checkResponse(JSONObject json) {
		try {
			if (json.has("success")) {
				boolean success = json.getBoolean("success");
				if (!success) {
					String err_code = json.optString("code", "");
					String err_msg = json.optString("msg", "");
					if ("".equals(err_code)) {
						throw new BinanceApiException(BinanceApiException.EXEC_ERROR, "[Executing] " + err_msg);
					} else {
						throw new BinanceApiException(BinanceApiException.EXEC_ERROR,
								"[Executing] " + err_code + ": " + err_msg);
					}
				}
			} else if (json.has("code")) {

				int code = json.optInt("code");
				if (code != 200) {
					String message = json.optString("msg", "");
					throw new BinanceApiException(BinanceApiException.EXEC_ERROR,
							"[Executing] " + code + ": " + message);
				}
			}
		} catch (BinanceApiException e) {
			throw e;
		} catch (Exception e) {
			throw new BinanceApiException(BinanceApiException.RUNTIME_ERROR,
					"[Invoking] Unexpected error: " + e.getMessage());
		}
	}

	private Request createRequest(String path, UrlParamsBuilder builder) {
		String requestUrl = restServerUrl + path;
		if (builder != null) {
			if (builder.hasPostParam()) {
				return new Request.Builder().url(requestUrl).post(builder.buildPostBody())
						.addHeader("Content-Type", "application/json")
//						.addHeader("client_SDK_Version", "binance_futures-1.0.1-java")
						.build();
			} else {
				return new Request.Builder().url(requestUrl + builder.buildUrl())
						.addHeader("Content-Type", "application/x-www-form-urlencoded")
//						.addHeader("client_SDK_Version", "binance_futures-1.0.1-java")
						.build();
			}
		} else {
			return new Request.Builder().url(requestUrl).addHeader("Content-Type", "application/x-www-form-urlencoded")
//					.addHeader("client_SDK_Version", "binance_futures-1.0.1-java")
					.build();
		}
	}

	private Request createRequestWithSignature(String path, UrlParamsBuilder builder) {
		if (builder == null) {
			throw new BinanceApiException(BinanceApiException.RUNTIME_ERROR,
					"[Invoking] Builder is null when create request with Signature");
		}
		String requestUrl = restServerUrl + path;
		new ApiSignature().createSignature(apiKey, secretKey, builder);
		if (builder.hasPostParam()) {
			requestUrl += builder.buildUrl();
			return new Request.Builder().url(requestUrl).post(builder.buildPostBody())
					.addHeader("Content-Type", "application/json").addHeader("X-MBX-APIKEY", apiKey)
//					.addHeader("client_SDK_Version", "binance_futures-1.0.1-java")
					.build();
		} else if (builder.checkMethod("PUT")) {
			requestUrl += builder.buildUrl();
			return new Request.Builder().url(requestUrl).put(builder.buildPostBody())
					.addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("X-MBX-APIKEY", apiKey)
//					.addHeader("client_SDK_Version", "binance_futures-1.0.1-java")
					.build();
		} else if (builder.checkMethod("DELETE")) {
			requestUrl += builder.buildUrl();
			return new Request.Builder().url(requestUrl).delete()
					.addHeader("Content-Type", "application/x-www-form-urlencoded")
//					.addHeader("client_SDK_Version", "binance_futures-1.0.1-java")
					.addHeader("X-MBX-APIKEY", apiKey).build();
		} else {
			requestUrl += builder.buildUrl();
			return new Request.Builder().url(requestUrl).addHeader("Content-Type", "application/x-www-form-urlencoded")
//					.addHeader("client_SDK_Version", "binance_futures-1.0.1-java")
					.addHeader("X-MBX-APIKEY", apiKey).build();
		}
	}

	private Request createRequestWithApikey(String path, UrlParamsBuilder builder) {
		if (builder == null) {
			throw new BinanceApiException(BinanceApiException.RUNTIME_ERROR,
					"[Invoking] Builder is null when create request with Signature");
		}
		String requestUrl = restServerUrl + path;
		requestUrl += builder.buildUrl();
		if (builder.hasPostParam()) {
			return new Request.Builder().url(requestUrl).post(builder.buildPostBody())
					.addHeader("Content-Type", "application/json").addHeader("X-MBX-APIKEY", apiKey)
//					.addHeader("client_SDK_Version", "binance_futures-1.0.1-java")
					.build();
		} else if (builder.checkMethod("DELETE")) {
			return new Request.Builder().url(requestUrl).delete()
					.addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("X-MBX-APIKEY", apiKey)
//					.addHeader("client_SDK_Version", "binance_futures-1.0.1-java")
					.build();
		} else if (builder.checkMethod("PUT")) {
			return new Request.Builder().url(requestUrl).put(builder.buildPostBody())
					.addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("X-MBX-APIKEY", apiKey)
//					.addHeader("client_SDK_Version", "binance_futures-1.0.1-java")
					.build();
		} else {
			return new Request.Builder().url(requestUrl).addHeader("Content-Type", "application/x-www-form-urlencoded")
					.addHeader("X-MBX-APIKEY", apiKey)
//					.addHeader("client_SDK_Version", "binance_futures-1.0.1-java")
					.build();
		}
	}

	private void tryConnect() throws Throwable {
		try {
			WS_status = WS_CONNECTING;
			if (eventLoopGroup != null) {
				eventLoopGroup.shutdownGracefully();
			}
			Bootstrap bootstrap = new Bootstrap();
			eventLoopGroup = new NioEventLoopGroup();
			final WebSocketClientHandler messageHandler = new WebSocketClientHandler(
					WebSocketClientHandshakerFactory.newHandshaker(wsServerUri, WebSocketVersion.V13, null, true,
							new DefaultHttpHeaders(), 65536 * 1024));
			bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							if (sslCtx != null) {
								p.addLast(sslCtx.newHandler(ch.alloc(), wsServerHost, wsServerPort));
							}
							p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192000),
									new CustomWebSocketClientCompressionHandler(), messageHandler);
						}
					});
			channel = bootstrap.connect(wsServerHost, wsServerPort).sync().channel();
			messageHandler.handshakeFuture();
		} catch (Throwable t) {
			t.printStackTrace();
			System.out.println("连接服务器[" + this.wsServerHost + "]失败，稍后尝试");
			WS_status = WS_CLOSE;
			throw t;
		}
	}

	private void writeMessage(JSONObject messageObject) {
		channel.writeAndFlush(new TextWebSocketFrame(messageObject.toString()));
	}

	private void handleConnected() {
		WS_lastMessageTime = System.currentTimeMillis();
		System.out.println("连接[" + wsServerHost + "]成功");
		subscribeEvents();
	}

	private void subscribeEvents() {
		JSONObject o = new JSONObject();
		o.put("id", System.currentTimeMillis());
		o.put("method", "SUBSCRIBE");
		JSONArray params = new JSONArray();
		o.put("params", params);
		for (String subscribe : subscribes) {
			params.put(subscribe);
		}
		if (listenKey != null) {
			params.put(listenKey);
		}
		writeMessage(o);
	}

	private void querySubscribes() {
		JSONObject o = new JSONObject();
		o.put("id", System.currentTimeMillis());
		o.put("method", "LIST_SUBSCRIPTIONS");
		writeMessage(o);
	}

	private void handleMessage(String message) {
		handleMessage(new JSONObject(message));
	}

	protected abstract void handleMessage(JSONObject messageObject);

	protected abstract String getListenKey();

	protected abstract PerMessageDeflateClientExtensionHandshaker getPerMessageDeflateClientExtensionHandshaker();

	private class CustomWebSocketClientCompressionHandler extends WebSocketClientExtensionHandler {

		private CustomWebSocketClientCompressionHandler() {
			super(getPerMessageDeflateClientExtensionHandshaker(), new DeflateFrameClientExtensionHandshaker(false),
					new DeflateFrameClientExtensionHandshaker(true));
		}
	}

	private class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

		private final WebSocketClientHandshaker handshaker;
		private ChannelPromise handshakeFuture;

		public WebSocketClientHandler(WebSocketClientHandshaker handshaker) {
			this.handshaker = handshaker;
		}

		public ChannelFuture handshakeFuture() {
			return handshakeFuture;
		}

		@Override
		public void handlerAdded(ChannelHandlerContext ctx) {
			handshakeFuture = ctx.newPromise();
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			handshaker.handshake(ctx.channel());
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) {
			WS_status = WS_CLOSE;
			System.out.println("连接[" + wsServerHost + "]已经关闭");
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			if (!handshakeFuture.isDone()) {
				handshakeFuture.setFailure(cause);
			}
			WS_status = WS_CLOSE;
			ctx.close();
		}

		@Override
		public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
			Channel ch = ctx.channel();
			if (!handshaker.isHandshakeComplete()) {
				try {
					handshaker.finishHandshake(ch, (FullHttpResponse) msg);
					handshakeFuture.setSuccess();
					//
					WS_status = WS_CONNECTED;
					handleConnected();
				} catch (WebSocketHandshakeException e) {
					handshakeFuture.setFailure(e);
				}
				return;
			}
			if (msg instanceof FullHttpResponse) {
				FullHttpResponse response = (FullHttpResponse) msg;
				throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status()
						+ ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
			}
			WebSocketFrame frame = (WebSocketFrame) msg;
			if (frame instanceof TextWebSocketFrame) {
				WS_lastMessageTime = System.currentTimeMillis();
				TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
				try {
					handleMessage(textFrame.text());
				} catch (Throwable t) {
					t.printStackTrace();
				}
			} else if (frame instanceof PingWebSocketFrame) {
//				System.out.println("WebSocket Client received ping");
				ch.writeAndFlush(new PongWebSocketFrame());
			} else if (frame instanceof PongWebSocketFrame) {
//				System.out.println("WebSocket Client received pong");
			} else if (frame instanceof CloseWebSocketFrame) {
				ch.close();
			}
		}

	}
}

class ApiSignature {

	static final String op = "op";
	static final String opValue = "auth";
	private static final String signatureMethodValue = "HmacSHA256";
	public static final String signatureVersionValue = "2";
	public static final long DEFAULT_RECEIVING_WINDOW = 60_000L;

	void createSignature(String accessKey, String secretKey, UrlParamsBuilder builder) {

		if (accessKey == null || "".equals(accessKey) || secretKey == null || "".equals(secretKey)) {
			throw new BinanceApiException(BinanceApiException.KEY_MISSING, "API key and secret key are required");
		}

		builder.putToUrl("recvWindow", Long.toString(DEFAULT_RECEIVING_WINDOW)).putToUrl("timestamp",
				Long.toString(System.currentTimeMillis()));

		Mac hmacSha256;
		try {
			hmacSha256 = Mac.getInstance(signatureMethodValue);
			SecretKeySpec secKey = new SecretKeySpec(secretKey.getBytes(), signatureMethodValue);
			hmacSha256.init(secKey);
		} catch (NoSuchAlgorithmException e) {
			throw new BinanceApiException(BinanceApiException.RUNTIME_ERROR,
					"[Signature] No such algorithm: " + e.getMessage());
		} catch (InvalidKeyException e) {
			throw new BinanceApiException(BinanceApiException.RUNTIME_ERROR,
					"[Signature] Invalid key: " + e.getMessage());
		}
		String payload = builder.buildSignature();
		String actualSign = new String(Hex.encodeHex(hmacSha256.doFinal(payload.getBytes())));

		builder.putToUrl("signature", actualSign);

	}

}
