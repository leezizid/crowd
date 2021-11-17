package com.crowd.service.base.impl;

import java.util.ArrayList;
import java.util.List;

import com.crowd.service.base.CrowdInitContext;
import com.crowd.service.base.TableDefine;

public final class CrowdInitContextImpl implements CrowdInitContext {

	private List<TableDefine> registerTables = new ArrayList<TableDefine>();

	public final void registerTable(Class<? extends TableDefine> clazz) throws Throwable {
		registerTables.add(clazz.newInstance());
	}

	public TableDefine[] getRegisterTables() {
		return registerTables.toArray(new TableDefine[0]);
	}

}
