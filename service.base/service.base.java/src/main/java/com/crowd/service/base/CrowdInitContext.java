package com.crowd.service.base;

public interface CrowdInitContext {

	public void registerTable(Class<? extends TableDefine> clazz) throws Throwable;

}
