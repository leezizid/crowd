package com.crowd.service.testcenter;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.Hashtable;

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
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

public class AgentServer implements Runnable {

	static final int PORT = 33338;

	Hashtable<Channel, ServerChannel> allChannels = new Hashtable<Channel, ServerChannel>();
	Hashtable<Long, ServerChannel> allChannelsById = new Hashtable<Long, ServerChannel>();

	public void getAgents() {

	}

	public void requestTestTask(JSONObject taskInfo) {
		allChannels.values().iterator().next().writeMessage("request", taskInfo);
	}

	private void handleResponse(JSONObject contentObject) {
		System.err.println(contentObject.toString(4));
		// TODO：处理结果
	}

	public void run() {
		try {
			EventLoopGroup bossGroup = new NioEventLoopGroup(1);
			EventLoopGroup workerGroup = new NioEventLoopGroup();
			try {
				ServerBootstrap b = new ServerBootstrap();
				b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
						.childHandler(new WebSocketServerInitializer());
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

		@Override
		public void initChannel(final SocketChannel ch) throws Exception {
			ChannelPipeline pipeline = ch.pipeline();
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
					ServerChannel webSocketChannel = allChannels.remove(ch);
					long id = webSocketChannel.getId();
					allChannelsById.remove(id);
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
					JSONObject contentObject = messageObject.optJSONObject("content");
					if ("register".equals(topic)) {
						ServerChannel webSocketChannel = new ServerChannel(ctx.channel().remoteAddress().toString(),
								contentObject.getJSONObject("clientInfo"), ctx.channel());
						allChannels.put(ctx.channel(), webSocketChannel);
						allChannelsById.put(webSocketChannel.getId(), webSocketChannel);
						webSocketChannel.writeMessage(topic, new JSONObject());
					} else {
						ServerChannel webSocketChannel = allChannels.get(ctx.channel());
						if (webSocketChannel == null) {
							return;
						}
						if ("check".equals(topic)) {
							webSocketChannel.writeMessage(topic, new JSONObject());
						} else if ("response".equals(topic)) {
							handleResponse(contentObject);
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
