package com.crowd.service.testcenter;

import org.json.JSONObject;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class ServerChannel {

	private static long _id = 0;

	private long id;

	private String remoteAddress;

	private JSONObject clientInfo;

	private Channel channel;

	public ServerChannel(String remoteAddress, JSONObject clientInfo, Channel channel) {
		super();
		this.id = _id++;
		this.remoteAddress = remoteAddress;
		this.clientInfo = clientInfo;
		this.channel = channel;
	}

	public long getId() {
		return this.id;
	}

	public String getRemoteAddress() {
		return this.remoteAddress;
	}

	public JSONObject getClientInfo() {
		return clientInfo;
	}

	public void writeMessage(String topic, JSONObject message) {
		JSONObject obj = new JSONObject();
		obj.put("topic", topic);
		obj.put("content", message);
		channel.writeAndFlush(new TextWebSocketFrame(obj.toString()));
	}

}
