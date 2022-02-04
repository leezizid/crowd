package com.crowd.tool.testagent;

import org.json.JSONObject;

public class Main {

	public final static void main(String[] args) throws Throwable {
		JSONObject clientInfo = new JSONObject();
		clientInfo.put("processors", Runtime.getRuntime().availableProcessors());
		new Thread(new ClientChannel("ws://" + System.getProperty("AgentServer") + ":33338/websocket", clientInfo))
				.start();
	}

}
