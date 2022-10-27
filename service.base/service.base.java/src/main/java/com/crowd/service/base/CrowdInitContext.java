package com.crowd.service.base;

public interface CrowdInitContext {
	
	public String load(String name) throws Throwable;

	public void registerTable(Class<? extends TableDefine> clazz) throws Throwable;

}
