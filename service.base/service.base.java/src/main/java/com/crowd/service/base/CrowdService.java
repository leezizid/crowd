package com.crowd.service.base;

public interface CrowdService {

	void init(CrowdInitContext context) throws Throwable;
	
	void postInit(CrowdInitContext context) throws Throwable;

	String getName();

}
