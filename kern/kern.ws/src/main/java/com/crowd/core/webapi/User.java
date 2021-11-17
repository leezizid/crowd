package com.crowd.core.webapi;

public class User {

	private GUID id;
	private String name;
	private String title;

	public User(GUID id, String name, String title) {
		this.id = id;
		this.name = name;
		this.title = title;
	}

	public boolean isActive() {
		return true;
	}

	public GUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

}
