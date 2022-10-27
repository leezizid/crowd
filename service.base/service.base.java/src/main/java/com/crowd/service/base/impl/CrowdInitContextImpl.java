package com.crowd.service.base.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.crowd.service.base.CrowdInitContext;
import com.crowd.service.base.TableDefine;

public final class CrowdInitContextImpl implements CrowdInitContext {

	private String serviceName;

	private List<TableDefine> registerTables = new ArrayList<TableDefine>();

	CrowdInitContextImpl(String serviceName) {
		this.serviceName = serviceName;
	}

	public final void registerTable(Class<? extends TableDefine> clazz) throws Throwable {
		registerTables.add(clazz.newInstance());
	}

	public TableDefine[] getRegisterTables() {
		return registerTables.toArray(new TableDefine[0]);
	}

	public String load(String name) throws Throwable {
		JSONObject inputObject = new JSONObject();
		inputObject.put("domain", this.serviceName);
		inputObject.put("name", name);
		JSONObject outputObject = CrowdApp.invokeRemoteService(null, null, CrowdApp.CORE_SERVICE_FILE_LOAD, inputObject,
				false);
		return outputObject.getString("content");
	}

}
