package com.crowd.core.webapi;

import org.apache.commons.lang3.StringUtils;

public class Session {

	private String id;

	private User user;

	private long lastActiveTime;

	public Session() {
		this.id = String.valueOf(GUID.randomLong());
		this.active();
	}

	public void active() {
		this.lastActiveTime = System.currentTimeMillis();
	}

	public boolean isExpired() {
		return System.currentTimeMillis() - lastActiveTime > 3 * 60 * 1000;
	}

	public User getUser() {
		return user;
	}

	public String getId() {
		return id;
	}

	public void changeUser(String userName, String userPwd) throws Throwable {
		if (StringUtils.isEmpty(userName) && StringUtils.isEmpty(userPwd)) {
			this.user = null;
			return;
		}
		if ("admin".equals(userName) && "".equals(userPwd)) {
			this.user = new User(GUID.MD5Of(userName), userName, "管理员");
		} else {
			throw new IllegalStateException("用户名密码不正确");
		}
	}

}
