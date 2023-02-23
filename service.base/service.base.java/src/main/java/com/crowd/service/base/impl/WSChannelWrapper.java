package com.crowd.service.base.impl;

import java.net.URI;
import java.util.Hashtable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.type.GUID;

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

public class WSChannelWrapper implements Runnable {

	private final static int TIME_WS_CHECK = 30 * 1000; // 检查WS状态的间隔
	private final static int TIME_WS_CHECK_TIMEOUT = 45 * 1000; // 检查时间出现异常的时间（即最后消息时间不应该超过这个时间，超过即认为掉线自动重连）

	final static int WS_CLOSE = 0; // 已经关闭，属于稳定状态，可能由关闭时间定时或者在APP转到前台时触发转至WS_WAIT_CONNECTING，
	final static int WS_CONNECTING = 10; // 正在连接；属于临时状态，一定会在短期内转至WS_CONNECTED或者WS_CLOSE
	final static int WS_CONNECTED = 100; // 已经连接；属于稳定状态，在关闭后转为WS_CLOSE

	private int WS_status = WS_CLOSE;
	private long WS_lastMessageTime = 0;
	private long WS_lastCheckTime = 0;

	private NioEventLoopGroup eventLoopGroup;
	private Channel channel;

	private String serverUrl;
	private JSONObject registerInfo;

	private Hashtable<String, MessageLock> messageLocks = new Hashtable<String, MessageLock>();

	private Hashtable<String, CrowdWorkerContextImpl> workerContexts = new Hashtable<String, CrowdWorkerContextImpl>();

	// XXX：如果掉线（网络问题，或者网关出异常），对于APP而言有两个异常情况：1、正在执行的工作线程，无法正常返回网关响应数据；2、正在对其他服务的调用，无法得到响应，以超时结束（应该处理掉线以便得到及时响应）
	public WSChannelWrapper(String serverUrl, JSONObject registerInfo) {
		this.serverUrl = serverUrl;
		this.registerInfo = registerInfo;
	}

	int getStatus() {
		return this.WS_status;
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
							writeMessage("system.check", new JSONObject());
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
		// 先终止目前正在执行的外部调用
		for (String mid : messageLocks.keySet()) {
			try {
				MessageLock messageLock = messageLocks.get(mid);
				if (messageLock != null) {
					messageLock.exception = true;
					synchronized (messageLock) {
						messageLock.notify();
					}
				}
			} catch (Throwable t) {

			}
		}
		messageLocks.clear();
		//
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
//			t.printStackTrace();
			System.out.println("连接失败，稍后尝试");
			WS_status = WS_CLOSE;
			throw t;
		}
	}

	JSONObject invokeRemoteService(String sid, String tid, String apiPath, JSONObject input, boolean async) {
		String mid = GUID.randomID().toString();
		MessageLock messageLock = new MessageLock();
		messageLocks.put(mid, messageLock);
		synchronized (messageLock) {
			try {
				JSONObject contentObject = new JSONObject();
				contentObject.put("sid", sid);
				contentObject.put("tid", tid);
				contentObject.put("apiPath", apiPath);
				contentObject.put("async", async);
				contentObject.put("params", input);
				writeMessage("system.processor.request", mid, contentObject);
				messageLock.wait(120 * 1000); // XXX：设置合理的超时时间
				if (messageLock.contentObject != null) {
					if (messageLock.contentObject.has("error")) {
						throw new RuntimeException(
								messageLock.contentObject.getJSONObject("error").getString("message"));
					}
					if (messageLock.contentObject.has("data")) {
						return messageLock.contentObject.getJSONObject("data");
					} else {
						throw new RuntimeException("远程服务器没有返回正确的数据");
					}
				} else if (messageLock.exception) {
					throw new IllegalStateException("通道异常关闭");
				} else {
					throw new IllegalStateException("等待结果超时");
				}
			} catch (InterruptedException t) {
				throw new IllegalStateException("等待结果发生错误，" + t.getMessage());
			} finally {
				messageLocks.remove(mid);
			}
		}
	}

	void updateSubscribeTopics(JSONArray topics) {
		JSONObject o = new JSONObject();
		o.put("topics", topics);
		writeMessage("system.subscribe", o);
	}

	void sendRemoteMessage(String topic, JSONObject messageObject) {
		writeMessage(topic, GUID.randomID().toString(), messageObject);
	}

	private void writeMessage(String topic, String mid, JSONObject messageObject) {
		JSONObject o = new JSONObject();
		o.put("topic", topic);
		o.put("mid", mid);
		o.put("content", messageObject);
		channel.writeAndFlush(new TextWebSocketFrame(o.toString()));
	}

	private void writeMessage(String topic, JSONObject messageObject) {
		writeMessage(topic, GUID.randomID().toString(), messageObject);
	}

	private void handleConnected() {
		//
		JSONObject messageObject = new JSONObject();
		messageObject.put("clientInfo", "crowdApp");
		messageObject.put("registerInfo", registerInfo);
		//
		JSONArray workers = new JSONArray();
		for (String handle : workerContexts.keySet()) {
			workers.put(workerContexts.get(handle).toJSON());
		}
		messageObject.put("workers", workers);
		//
		writeMessage("system.register", messageObject);
		//
		WS_lastMessageTime = System.currentTimeMillis();
		WS_status = WS_CONNECTED;
		//
		System.out.println("已经连接:");
		System.out.println(messageObject.toString(4));
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
					final String messageId = messageObject.getString("mid");
					if ("system.check".equals(topic)) {
						// 心跳ehco
					} else if ("system.register".equals(topic)) {

					} else if ("system.processor.request".equals(topic)) {
						final JSONObject contentObject = messageObject.getJSONObject("content");
						String cid = contentObject.getString("cid");
						String tid = contentObject.getString("tid");
						String sid = contentObject.getString("sid");
						boolean rootTransaction = contentObject.optBoolean("rootTransaction");
						boolean async = contentObject.optBoolean("async");
						String apiPath = contentObject.getString("apiPath");
						JSONObject params = contentObject.getJSONObject("params");
						String serivceName = StringUtils.split(apiPath.substring(1), "/")[0];
						if (async) {
							// TODO：用线程池
							final String workerHandle = contentObject.getString("workerHandle");
							Thread t = new Thread(new Runnable() {
								@Override
								public void run() {
									CrowdWorkerContextImpl workerContext = new CrowdWorkerContextImpl(
											WSChannelWrapper.this, workerHandle, apiPath, params.toString(),
											serivceName);
									try {
										workerContexts.put(workerHandle, workerContext);
										CrowdApp.invokeLocalWorker(workerContext, apiPath, params);
									} catch (Throwable t) {
										JSONObject errorObject = new JSONObject();
										errorObject.put("type", t.getClass().getName());
										errorObject.put("message",
												t.getMessage() == null ? t.getClass().getName() : t.getMessage());
										workerContext.setException(errorObject);
									} finally {
										workerContexts.remove(workerHandle);
										//
										JSONObject contentObject = new JSONObject();
										contentObject.put("workerHandle", workerHandle);
										writeMessage("system.processor.worker.finished", messageId, contentObject);
									}
								}
							});
							t.setName("Worker-" + System.currentTimeMillis());
							t.start();
							// 直接返回调用结果，实际worker中将有独立的sid和tid
							JSONObject returnObject = new JSONObject();
							JSONObject outputObject = new JSONObject();
							outputObject.put("workerHandle", workerHandle);
							returnObject.put("data", outputObject);
							returnObject.put("cid", cid);
							returnObject.put("tid", tid);
							returnObject.put("sid", sid);
							returnObject.put("rootTransaction", rootTransaction);
							returnObject.put("async", async);
							returnObject.put("workerHandle", workerHandle);
							writeMessage("system.processor.response", messageId, returnObject); // 需要把原始消息ID写回
						} else {
							// TODO：用线程池
							Thread t = new Thread(new Runnable() {
								@Override
								public void run() {
									JSONObject returnObject = CrowdApp.invokeLocalMethod(
											new CrowdContextImpl(sid, tid, serivceName), apiPath, params);
									returnObject.put("cid", cid);
									returnObject.put("tid", tid);
									returnObject.put("sid", sid);
									returnObject.put("rootTransaction", rootTransaction);
									writeMessage("system.processor.response", messageId, returnObject); // 需要把原始消息ID写回
								}
							});
							t.setName("Handler-" + System.currentTimeMillis());
							t.start();
						}
					} else if ("system.processor.response".equals(topic)) {
						try {
							String mid = messageObject.optString("mid");
							if (messageLocks.containsKey(mid)) {
								MessageLock messageLock = messageLocks.remove(mid);
								if (messageLock != null) {
									messageLock.contentObject = messageObject.getJSONObject("content");
									synchronized (messageLock) {
										messageLock.notify();
									}
								}
							}
						} catch (Throwable t) {
						}
					} else if ("system.processor.worker.dispose".equals(topic)) {
						JSONObject contentObject = messageObject.getJSONObject("content");
						CrowdWorkerContextImpl crowdContext = workerContexts
								.get(contentObject.getString("workerHandle"));
						if (crowdContext != null) {
							crowdContext.dispose();
						}
					} else {
						// 其他订阅消息
						CrowdApp.notifyLocalSubscribers(topic, messageObject.getJSONObject("content"));
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			} else if (frame instanceof CloseWebSocketFrame) {
				ch.close();
			}
		}

	}
}

class MessageLock {

	JSONObject contentObject;

	boolean exception;

}