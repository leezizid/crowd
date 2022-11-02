package com.crowd.service.base;

import org.json.JSONObject;

public interface CrowdInitContext {

	public String load(String name) throws Throwable;

	public void startWorker(String path, JSONObject inputObject) throws Throwable;

	public void registerTable(Class<? extends TableDefine> clazz) throws Throwable;

}
