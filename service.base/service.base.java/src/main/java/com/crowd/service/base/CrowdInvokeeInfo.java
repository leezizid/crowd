package com.crowd.service.base;

import java.lang.reflect.Method;

public class CrowdInvokeeInfo {

	private CrowdService serviceInstance;

	private Method method;

	public CrowdInvokeeInfo(CrowdService serviceInstance, Method method) {
		super();
		this.serviceInstance = serviceInstance;
		this.method = method;
	}

	public CrowdService getServiceInstance() {
		return serviceInstance;
	}

	public Method getMethod() {
		return method;
	}

}
