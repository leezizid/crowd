package com.crowd.tool.testagent;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;

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
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.util.CharsetUtil;

public class ClientChannel implements Runnable {

	private final static int TIME_WS_CHECK = 30 * 1000; // 检查WS状态的间隔
	private final static int TIME_WS_CHECK_TIMEOUT = 45 * 1000; // 检查时间出现异常的时间（即最后消息时间不应该超过这个时间，超过即认为掉线自动重连）

	private final static int WS_CLOSE = 0; // 已经关闭，属于稳定状态，可能由关闭时间定时或者在APP转到前台时触发转至WS_WAIT_CONNECTING，
	private final static int WS_CONNECTING = 10; // 正在连接；属于临时状态，一定会在短期内转至WS_CONNECTED或者WS_CLOSE
	private final static int WS_CONNECTED = 100; // 已经连接；属于稳定状态，在关闭后转为WS_CLOSE

	private int WS_status = WS_CLOSE;
	private long WS_lastMessageTime = 0;
	private long WS_lastCheckTime = 0;

	private NioEventLoopGroup eventLoopGroup;
	private Channel channel;

	private String serverUrl;
	private JSONObject clientInfo;

	public ClientChannel(String serverUrl, JSONObject clientInfo) {
		this.serverUrl = serverUrl;
		this.clientInfo = clientInfo;
	}

	public void run() {
		while (true) {
			try {
				if (WS_status == WS_CLOSE) {
					handleConnect();
				} else if (WS_status == WS_CONNECTED) {
					long now = System.currentTimeMillis();
					// 如果最后消息时间超过check超时时间，则认为连接已经失效，尝试关闭连接并标记连接状态
					if (now - WS_lastMessageTime > TIME_WS_CHECK_TIMEOUT) {
						try {
							WS_status = WS_CLOSE;
							channel.close();
						} catch (Throwable t) {
						}
					}
					// 如果连接正常，当距上次发送check消息时间超过指定间隔，则再次发送check消息
					else if (now - WS_lastCheckTime > TIME_WS_CHECK) {
						try {
							writeMessage("check", new JSONObject());
							WS_lastCheckTime = now;
						} catch (Throwable t) {
						}
					}
				}
			} catch (Throwable t) {
			} finally {
				try {
					Thread.sleep(3000);
				} catch (Throwable t) {

				}
			}
		}
	}

	public void handleConnect() throws Throwable {
		try {
			WS_status = WS_CONNECTING;
			if (eventLoopGroup != null) {
				eventLoopGroup.shutdownGracefully();
			}
			URI uri = new URI(serverUrl);
			Bootstrap bootstrap = new Bootstrap();
			eventLoopGroup = new NioEventLoopGroup();
			final WebSocketClientHandler messageHandler = new WebSocketClientHandler(WebSocketClientHandshakerFactory
					.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders(), 65536 * 1024));
			bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192000),
									WebSocketClientCompressionHandler.INSTANCE, messageHandler);
						}
					});
			channel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
			messageHandler.handshakeFuture().sync();
		} catch (Throwable t) {
			System.out.println("连接失败，稍后尝试");
			WS_status = WS_CLOSE;
			throw t;
		}
	}

	private void writeMessage(String topic, JSONObject messageObject) {
		JSONObject o = new JSONObject();
		o.put("topic", topic);
		o.put("content", messageObject);
		channel.writeAndFlush(new TextWebSocketFrame(o.toString()));
	}

	private void handleConnected() {
		//
		JSONObject messageObject = new JSONObject();
		messageObject.put("clientInfo", clientInfo);
		writeMessage("register", messageObject);
		//
		WS_lastMessageTime = System.currentTimeMillis();
		WS_status = WS_CONNECTED;
		//
		System.out.println("已经连接:" + messageObject.toString());
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
			System.out.println("连接已经关闭");
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
				TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
				try {
					WS_lastMessageTime = System.currentTimeMillis();
					JSONObject messageObject = new JSONObject(textFrame.text());
					String topic = messageObject.optString("topic");
					if ("check".equals(topic)) {
					} else if ("register".equals(topic)) {
					} else if ("request".equals(topic)) {
						new Thread(new TestRunner(messageObject.getJSONObject("content"))).start();
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			} else if (frame instanceof CloseWebSocketFrame) {
				ch.close();
			}
		}

	}

	private class TestRunner implements Runnable {

		JSONObject taskInfo;

		TestRunner(JSONObject taskInfo) {
			this.taskInfo = taskInfo;
		}

		@Override
		public void run() {
//			String serviceName = "com.crowd.service.strategy.cta.CTAStrategyService";
//			String arguments = "v1,0.0015,120,0.7,1.4";
//			String symbol = "SHFE.ag";
//			String rate = "0.00006";
//			String dateSource = "20160101,20160331";
			String serviceName = taskInfo.getString("serviceName");
			String arguments = taskInfo.getString("arguments");
			String symbol = taskInfo.getString("symbol");
			String rate = taskInfo.getString("rate");
			String dateSource = taskInfo.getString("dateSource");
			String historyDataDir = System.getProperty("HistoryDataDir");
			String runtimeDir = System.getProperty("RuntimeDir");
			JSONObject messageObject = new JSONObject();
			messageObject.put("id", taskInfo.getString("id"));
			try {
				Process process = Runtime.getRuntime()
						.exec("java" + " -DServiceName=" + serviceName + " -DArguments=" + arguments + " -DSymbol="
								+ symbol + " -DRate=" + rate + " -DDataSource=" + dateSource + " -DHistoryDataDir="
								+ historyDataDir + " -jar " + runtimeDir + "\\test.jar");
				InputStream inputStream = process.getInputStream();
				byte[] buffer = new byte[1024 * 4];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int len = -1;
				while ((len = inputStream.read(buffer)) >= 0) {
					baos.write(buffer, 0, len);
				}
				messageObject.put("result", new JSONObject(new String(baos.toByteArray())));
			} catch (Throwable t) {
				messageObject.put("error", t.getMessage());
			}
			writeMessage("response", messageObject);
		}

	}
}
