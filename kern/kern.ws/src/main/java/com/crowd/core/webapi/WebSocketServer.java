package com.crowd.core.webapi;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

public class WebSocketServer implements Runnable {

	static final boolean SSL = false; // System.getProperty("ssl") != null;
	static final int PORT = Integer.parseInt(System.getProperty("wsserver_port", "33333")); // SSL ? "8443" :

	public final static String KEY_REMOTE_ADDR = "___remoteAddr___";

//	static Map<String, List<MessageSubscriber>> allSubscribers = new HashMap<String, List<MessageSubscriber>>();
	Hashtable<Channel, WebSocketChannel> allChannels = new Hashtable<Channel, WebSocketChannel>();
	Hashtable<String, WebSocketChannel> allChannelsById = new Hashtable<String, WebSocketChannel>();
	Hashtable<String, WebSocketChannel> serviceChannelRegistry = new Hashtable<String, WebSocketChannel>();

	// 当前活动的事务ID和通道ID的对应关系，键为目标调用通道ID，值为tid和来源通道cid#mid的对应集合
	Hashtable<String, Hashtable<String, String>> activeTransactionRegistry = new Hashtable<String, Hashtable<String, String>>();

	Hashtable<String, WorkerInfo> workerHandles = new Hashtable<String, WorkerInfo>();

	private void sendSubscribeMessage(String topic, String messageId, JSONObject messageObject) {
		if (messageId == null) {
			messageId = GUID.randomID().toString();
		}
		for (WebSocketChannel channel : allChannels.values()) {
			channel.writeMessage(topic, messageId, messageObject);
		}
	}

	private String newWorkerHandle(String apiPath, String params) {
		String workerHandle = apiPath + "-" + System.nanoTime();
		workerHandles.put(workerHandle, new WorkerInfo(apiPath, params));
		sendActiveWorkerMessage();
		return workerHandle;
	}

	private void removeWorkerHandle(String workerHandle) {
		workerHandles.remove(workerHandle);
		sendActiveWorkerMessage();
	}

	private void updateWorkerHandle(JSONObject o) {
		String handle = o.getString("handle");
		WorkerInfo workerInfo = workerHandles.get(handle);
		if (workerInfo != null) {
			workerInfo.progress = o.getFloat("progress");
			workerInfo.info = o.getString("info");
			sendActiveWorkerMessage();
		}
	}

	private void sendActiveWorkerMessage() {
		JSONObject messageObject = new JSONObject();
		JSONArray arr = getActiveWorkerInfos();
		messageObject.put("workers", arr);
		sendSubscribeMessage("portal.workers", null, messageObject);
	}

	private JSONArray getActiveWorkerInfos() {
		JSONArray arr = new JSONArray();
		for (String handle : workerHandles.keySet()) {
			WorkerInfo workerInfo = workerHandles.get(handle);
			JSONObject o = new JSONObject();
			o.put("handle", handle);
			o.put("path", workerInfo.path);
			o.put("params", workerInfo.params);
			o.put("time", workerInfo.startTime);
			o.put("status", workerInfo.status);
			o.put("progress", workerInfo.progress);
			o.put("info", workerInfo.info);
			arr.put(o);
		}
		return arr;
	}

	public void run() {
		try {
			final SslContext sslCtx;
			if (SSL) {
				SelfSignedCertificate ssc = new SelfSignedCertificate();
				sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
			} else {
				sslCtx = null;
			}

			EventLoopGroup bossGroup = new NioEventLoopGroup(1);
			EventLoopGroup workerGroup = new NioEventLoopGroup();
			try {
				ServerBootstrap b = new ServerBootstrap();
				b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
						.childHandler(new WebSocketServerInitializer(sslCtx));
				Channel ch = b.bind(PORT).sync().channel();
				ch.closeFuture().sync();
			} finally {
				bossGroup.shutdownGracefully();
				workerGroup.shutdownGracefully();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

		private static final String WEBSOCKET_PATH = "/websocket";

		private final SslContext sslCtx;

		public WebSocketServerInitializer(SslContext sslCtx) {
			this.sslCtx = sslCtx;
		}

		@Override
		public void initChannel(final SocketChannel ch) throws Exception {
			ChannelPipeline pipeline = ch.pipeline();
			if (sslCtx != null) {
				pipeline.addLast(sslCtx.newHandler(ch.alloc()));
			}
			pipeline.addLast(new IdleStateHandler(90, 90, 90));
			pipeline.addLast(new IdleEventHandle());
			pipeline.addLast(new HttpServerCodec());
			pipeline.addLast(new HttpObjectAggregator(1048576));
			pipeline.addLast(new WebSocketServerCompressionHandler());
			pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true, 65535 * 128));
			pipeline.addLast(new WebSocketIndexPageHandler());
			pipeline.addLast(new WebSocketFrameHandler());

			//
			ch.closeFuture().addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {
					WebSocketChannel webSocketChannel = allChannels.remove(ch);
//					SessionManager.disposeSession(webSocketChannel.getSessionId());
					String id = webSocketChannel.getId();
					allChannelsById.remove(id);
					// 将注册的服务方法注销
					Set<String> invalidMethods = new HashSet<String>();
					for (String methodName : serviceChannelRegistry.keySet()) {
						if (serviceChannelRegistry.get(methodName).getId().equals(id)) {
							invalidMethods.add(methodName);
						}
					}
					//
					Set<String> invalidWorkerHandles = new HashSet<String>();
					for (String methodName : invalidMethods) {
						for (String handle : workerHandles.keySet()) {
							if (methodName.equals(workerHandles.get(handle).path)) {
								invalidWorkerHandles.add(handle);
							}
						}
					}
					for (String methodName : invalidMethods) {
						serviceChannelRegistry.remove(methodName);
					}
					for (String handle : invalidWorkerHandles) {
						workerHandles.remove(handle);
					}
					//
					sendActiveWorkerMessage();

					//
					Hashtable<String, String> tid2cidRegistry = activeTransactionRegistry.remove(id);
					if (tid2cidRegistry != null) {
						for (String tid : tid2cidRegistry.keySet()) {
							String[] info = StringUtils.split(tid2cidRegistry.get(tid), "#");
							String cid = info[0];
							String mid = info[1];
							WebSocketChannel invokerChannel = allChannelsById.get(cid);
							try {
//								DMLServiceHandler.resolve(tid, false);
							} catch (Throwable e) {
							}
							try {
								if (invokerChannel != null) {
									JSONObject returnObject = new JSONObject();
									JSONObject errorObject = new JSONObject();
									errorObject.put("type", java.lang.RuntimeException.class.getName());
									errorObject.put("message", "事务因服务通道异常关闭结束");
									returnObject.put("error", errorObject);
									invokerChannel.writeMessage("system.processor.response", mid, returnObject);
								}
							} catch (Throwable t) {

							}
						}
					}
				}
			});
		}

	}

	private class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
			if (frame instanceof TextWebSocketFrame) {
				String message = ((TextWebSocketFrame) frame).text();
				try {
					JSONObject messageObject = new JSONObject(message);
					String topic = messageObject.optString("topic");
					String messageId = messageObject.optString("mid");
					JSONObject contentObject = messageObject.optJSONObject("content");
					String remoteAddr = ctx.channel().remoteAddress().toString();
					if ("system.register".equals(topic)) {
						Session session = null;
						//
						String clientInfo = contentObject.optString("clientInfo");
						JSONArray topics = contentObject == null ? null : contentObject.optJSONArray("topics");
						JSONArray methods = null;
						JSONArray workers = null;
						if ("crowdApp".equals(clientInfo)) {
							JSONObject registerInfo = contentObject.getJSONObject("registerInfo");
							topics = registerInfo.getJSONArray("subscribers");
							methods = registerInfo.getJSONArray("methods");
							workers = contentObject.getJSONArray("workers");
							//
							// DDLService.refactor(context, registerInfo.getJSONArray("tables"));
						} else {
							try {
								session = SessionManager.getSession(messageObject.optString("sessionId"));
								if (session.getUser() != null && (!session.getUser().isActive())) {
									throw new IllegalStateException();
								}
							} catch (Throwable t) {
								session = SessionManager.newSession();
							}
						}
						//
						WebSocketChannel webSocketChannel = new WebSocketChannel(
								session != null ? session.getId() : null, clientInfo, topics, ctx.channel());
						allChannels.put(ctx.channel(), webSocketChannel);
						allChannelsById.put(webSocketChannel.getId(), webSocketChannel);
						if (methods != null) {
							for (int i = 0; i < methods.length(); i++) {
								serviceChannelRegistry.put(methods.getString(i), webSocketChannel);
							}
						}
						if (workers != null && workers.length() > 0) {
							for (int i = 0; i < workers.length(); i++) {
								JSONObject o = workers.getJSONObject(i);
								WorkerInfo info = new WorkerInfo(o.getString("path"), o.getString("params"));
								info.startTime = o.getLong("time");
								info.status = o.getString("status");
								workerHandles.put(o.getString("handle"), info);
							}
							sendActiveWorkerMessage();
						}
						//
						JSONObject returnObject = new JSONObject();
						if (session != null) {
							User loginUser = session.getUser();
							returnObject.put("userId", loginUser == null ? "" : loginUser.getId());
							returnObject.put("userName", loginUser == null ? "" : loginUser.getName());
						}
						webSocketChannel.writeMessage(topic, messageId, returnObject);
					} else {
						WebSocketChannel webSocketChannel = allChannels.get(ctx.channel());
						if (webSocketChannel == null) {
							return; // XXX：服务端已处理关闭，但客户端未处理，不要返回客户端数据
						}
						//
////						Session session;
//						try {
//							session = SessionManager.getSession(webSocketChannel.getSessionId());
//							if (session.getUser() != null && (!session.getUser().isActive())) {
//								throw new IllegalStateException();
//							}
//						} catch (Throwable t) {
//							session = SessionManager.newSession();
//							webSocketChannel.setSessionId(session.getId());
//						}
						//
						if ("system.check".equals(topic)) {
							webSocketChannel.writeMessage(topic, messageId, new JSONObject());
							if (webSocketChannel.getSessionId() != null) {
								SessionManager.getSession(webSocketChannel.getSessionId());
							}
						} else if ("system.subscribe".equals(topic)) {
							webSocketChannel
									.setTopics(contentObject == null ? null : contentObject.getJSONArray("topics"));
						} else if ("system.processor.request".equals(topic)) {
							String tid = contentObject.optString("tid");
							String sid = contentObject.optString("sid");
							boolean async = contentObject.optBoolean("async");
							boolean rootTransaction = false;
							if (StringUtils.isEmpty(tid)) {
								tid = GUID.randomID().toString();
								sid = webSocketChannel.getSessionId(); // XXX：这里假设tid为空都是由UI发起，通道中自带会话ID，实际可能由worker发起（worker内发起一次调用，就是一个独立事务，如果需要多个调用形成一个事务，可以通过封装一个单独的服务来处理）
								if (sid == null) {
									sid = "";
								}
								rootTransaction = true; // XXX:根事务一般由UI发起，调用一个非核心（___Core）请求；也有可能由后台APP发起，但是发起过程需要通过CrowdApp静态方法提交到网关发起；综上，root事务一定是非核心请求
							}
							String apiPath = contentObject.getString("apiPath");
							try {
								if (apiPath.startsWith("/___core.")) {
									// XXX：校验调用者权限（例如不能是从web界面过来）
									JSONObject input = contentObject.getJSONObject("params");
									JSONObject output = new JSONObject();
									input.put(KEY_REMOTE_ADDR, remoteAddr);
									//
									if (apiPath.startsWith("/___core.ds/")) {
//										DMLServiceHandler.invoke(session, tid, apiPath, input, output);
									} else if (apiPath.equals("/___core.file/save")) {
										String domain = input.getString("domain");
										String name = input.getString("name");
										String content = input.getString("content");
										FileServiceHandler.save(domain, name, content);
									} else if (apiPath.equals("/___core.file/delete")) {
										String domain = input.getString("domain");
										String name = input.getString("name");
										FileServiceHandler.delete(domain, name);
									} else if (apiPath.equals("/___core.file/load")) {
										String domain = input.getString("domain");
										String name = input.getString("name");
										String content = FileServiceHandler.load(domain, name);
										output.put("content", content);
									} else if (apiPath.equals("/___core.user/change")) {
										Session session = SessionManager.getSession(sid);
										session.changeUser(input.getString("userName"), input.getString("userPwd"));
									} else if (apiPath.equals("/___core.worker/list")) {
										output.put("workers", getActiveWorkerInfos());
									} else if (apiPath.equals("/___core.worker/dispose")) {
										String workerHandle = input.getString("workerHandle");
										if (workerHandles.containsKey(workerHandle)) {
											WorkerInfo workerInfo = workerHandles.get(workerHandle);
											workerInfo.status = "disposing";
											WebSocketChannel workerChannel = serviceChannelRegistry
													.get(workerInfo.path);
											if (workerChannel != null) {
												JSONObject o = new JSONObject();
												o.put("workerHandle", workerHandle);
												workerChannel.writeMessage("system.processor.worker.dispose",
														GUID.randomID().toString(), o);
											}
											sendActiveWorkerMessage();
										}
									}
									JSONObject returnObject = new JSONObject();
									returnObject.put("data", output);
									webSocketChannel.writeMessage("system.processor.response", messageId, returnObject);
								} else {
									WebSocketChannel serviceChannel = serviceChannelRegistry.get(apiPath);
									if (serviceChannel == null) {
										throw new IllegalArgumentException("找不到指定的数据处理器");
									}
									//
									if (!activeTransactionRegistry.containsKey(serviceChannel.getId())) {
										activeTransactionRegistry.put(serviceChannel.getId(),
												new Hashtable<String, String>()); // 目标通道ID --> (tid-cid_mid)
									}
									Hashtable<String, String> tid2cidRegistry = activeTransactionRegistry
											.get(serviceChannel.getId());
									tid2cidRegistry.put(tid, webSocketChannel.getId() + "#" + messageId);
									//
									contentObject.put("tid", tid);
									contentObject.put("sid", sid);
									contentObject.put("async", async);
									if (async) {
										contentObject.put("workerHandle", newWorkerHandle(apiPath,
												contentObject.getJSONObject("params").toString()));
									}
									if (rootTransaction) {
										contentObject.put("rootTransaction", rootTransaction);
									}
									contentObject.put("cid", webSocketChannel.getId());
									serviceChannel.writeMessage(topic, messageId, contentObject);
								}
							} catch (Throwable e) {
								// 异常直接写入返回消息
								JSONObject returnObject = new JSONObject();
								// apiLogger.logFatal(null, e, true);
								JSONObject errorObject = new JSONObject();
								errorObject.put("type", e.getClass().getName());
								errorObject.put("message",
										e.getMessage() == null ? e.getClass().getName() : e.getMessage());
								returnObject.put("error", errorObject);
								webSocketChannel.writeMessage("system.processor.response", messageId, returnObject);
							}
						} else if ("system.processor.response".equals(topic)) {
							try {
								// 找到调用来源通道，将处理结果直接输出
								String invokerChannelId = (String) contentObject.remove("cid");
								WebSocketChannel invokerChannel = allChannelsById.get(invokerChannelId);
								//
								if (contentObject.optBoolean("rootTransaction")) {
									try {
										//
										String tid = contentObject.getString("tid");
										Hashtable<String, String> tid2cidRegistry = activeTransactionRegistry
												.get(webSocketChannel.getId());
										if (tid2cidRegistry == null || !tid2cidRegistry.containsKey(tid)) {
											throw new IllegalStateException("调用已经异常结束");
										}
										tid2cidRegistry.remove(tid);
//										DMLServiceHandler.resolve(tid, !contentObject.has("error"));
									} catch (Throwable e) {
										if (invokerChannel != null) {
											JSONObject returnObject = new JSONObject();
											JSONObject errorObject = new JSONObject();
											errorObject.put("type", e.getClass().getName());
											errorObject.put("message",
													e.getMessage() == null ? e.getClass().getName() : e.getMessage());
											returnObject.put("error", errorObject);
											invokerChannel.writeMessage("system.processor.response", messageId,
													returnObject);
											throw e;
										}
									}
								}
								if (invokerChannel != null) {
									invokerChannel.writeMessage(topic, messageId, contentObject);
								}
							} catch (Throwable e) {
							}
						} else if ("system.processor.worker.finished".equals(topic)) {
							removeWorkerHandle(contentObject.getString("workerHandle"));
						} else if ("system.processor.worker.update".equals(topic)) {
							updateWorkerHandle(contentObject);
						} else {
							// 其他订阅主题，发送到所有连接的客户端通道
							sendSubscribeMessage(topic, messageId, contentObject);
						}
					}
				} catch (Throwable t) {

				}
			} else {
				String message = "unsupported frame type: " + frame.getClass().getName();
				throw new UnsupportedOperationException(message);
			}
		}
	}

	private class IdleEventHandle extends ChannelInboundHandlerAdapter {
		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			if (evt instanceof IdleStateEvent) {
				if (ctx.channel().isOpen()) {
					ctx.close();
				}
			} else {
				// 传递给下一个处理程序
				super.userEventTriggered(ctx, evt);
			}
		}
	}

	private class WebSocketIndexPageHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
			// Handle a bad request.
			if (!req.decoderResult().isSuccess()) {
				sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
				return;
			}

			// Allow only GET methods.
			if (req.method() != GET) {
				sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
				return;
			}

			// Send the index page
			if ("/".equals(req.uri()) || "/index.html".equals(req.uri())) {
				ByteBuf content = Unpooled.copiedBuffer("", CharsetUtil.US_ASCII);
				FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);

				res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
				HttpUtil.setContentLength(res, content.readableBytes());

				sendHttpResponse(ctx, req, res);
			} else {
				sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();
		}

		private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
			// Generate an error page if response getStatus code is not OK
			// (200).
			if (res.status().code() != 200) {
				ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
				res.content().writeBytes(buf);
				buf.release();
				HttpUtil.setContentLength(res, res.content().readableBytes());
			}

			// Send the response and close the connection if necessary.
			ChannelFuture f = ctx.channel().writeAndFlush(res);
			if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		}

	}
}

class WorkerInfo {

	String path;

	String params;

	long startTime;

	String status;

	float progress;

	String info;

	public WorkerInfo(String path, String params) {
		super();
		this.path = path;
		this.params = params;
		this.startTime = System.currentTimeMillis();
		this.status = "none";
		this.progress = 0;
		this.info = "";
	}

}
