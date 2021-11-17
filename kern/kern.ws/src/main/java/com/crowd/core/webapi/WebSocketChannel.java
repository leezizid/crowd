package com.crowd.core.webapi;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketChannel {

	private String id;

	private String sessionId;

	private boolean needFeedback; // 是否需要反馈sessionId

	private String clientInfo;

	private Channel channel;

	private Set<String> topics; // 客户端订阅的主题

	private String userName;

	public WebSocketChannel(String sessionId, String clientInfo, JSONArray topics, Channel channel) {
		super();
		this.id = GUID.randomID().toString();
		this.sessionId = sessionId;
		this.clientInfo = clientInfo;
		this.channel = channel;
		this.needFeedback = true;
		this.setTopics(topics);
	}

	public String getId() {
		return this.id;
	}

//	public void setSessionId(String sessionId) {
//		this.sessionId = sessionId;
//		this.needFeedback = true;
//	}

	public String getSessionId() {
		return sessionId;
	}

	public String getClientInfo() {
		return clientInfo;
	}

//
	public void setTopics(JSONArray topics) {
		this.topics = new HashSet<String>();
		if (topics != null) {
			for (int i = 0; i < topics.length(); i++) {
				this.topics.add(topics.getString(i));
			}
		}
	}

	public void writeMessage(String topic, String mid, JSONObject message) {
		if (this.topics.contains(topic) || topic.startsWith("system.")) {
			JSONObject obj = new JSONObject();
			obj.put("topic", topic);
			obj.put("mid", mid);
			if (needFeedback) {
				obj.put("sessionId", String.valueOf(sessionId));
				needFeedback = false;
			}
			obj.put("content", message);
			channel.writeAndFlush(new TextWebSocketFrame(obj.toString()));
		}
	}

	public void postLogin(String userName) {
		this.userName = userName;
	}

	public void postLogout(String userName) {
		this.userName = null;
	}

	public String getUserName() {
		return this.userName;
	}

}
