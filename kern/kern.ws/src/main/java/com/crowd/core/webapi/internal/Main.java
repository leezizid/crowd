package com.crowd.core.webapi.internal;

import com.crowd.core.webapi.WebSocketServer;

public class Main {

	public final static void main(String[] args) {
		new Thread(new WebSocketServer()).start();
		System.out.println("Crowd Kern Start...");
	}

}
