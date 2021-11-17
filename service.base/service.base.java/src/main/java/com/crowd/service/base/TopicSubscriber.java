package com.crowd.service.base;

import org.json.JSONObject;

public interface TopicSubscriber {

	public void messageReceived(JSONObject messageObject) throws Throwable;

}
