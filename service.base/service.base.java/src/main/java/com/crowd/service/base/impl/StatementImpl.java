package com.crowd.service.base.impl;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.Statement;
import com.crowd.service.type.GUID;

public final class StatementImpl implements Statement {

	private String prepareSql;

	private JSONArray arguments = new JSONArray();

	public StatementImpl(String prepareSql) {
		this.prepareSql = prepareSql;
	}

	JSONArray executeQuery(CrowdContextImpl contextImpl) throws Throwable {
		JSONObject outputObject = contextImpl.invoke(CrowdApp.CORE_SERVICE_DML_EXECUTE_QUERY, initInputObjct());
		return outputObject.getJSONArray("rows");
	}

	int executeUpdate(CrowdContextImpl contextImpl) throws Throwable {
		JSONObject outputObject = contextImpl.invoke(CrowdApp.CORE_SERVICE_DML_EXECUTE_UPDATE, initInputObjct());
		return outputObject.getInt("count");
	}

	private JSONObject initInputObjct() {
		JSONObject inputObject = new JSONObject();
		inputObject.put("prepareSql", prepareSql);
		inputObject.put("arguments", arguments);
		return inputObject;
	}

	public Statement addStringArgument(String v) {
		addArugment("string", v);
		return this;
	}

	public Statement addIntArgument(int v) {
		addArugment("int", v);
		return this;
	}

	public Statement addLongArgument(long v) {
		addArugment("long", v);
		return this;
	}

	public Statement addByteArgument(byte v) {
		addArugment("byte", v);
		return this;
	}

	public Statement addBooleanArgument(boolean v) {
		addArugment("boolean", v);
		return this;
	}

	public Statement addFloatArgument(float v) {
		addArugment("float", v);
		return this;
	}

	public Statement addDoubleArgument(double v) {
		addArugment("double", v);
		return this;
	}

	public Statement addGUIDArgument(GUID v) {
		addArugment("guid", v);
		return this;
	}

	public Statement addDateArgument(Date v) {
		addArugment("date", v.getTime());
		return this;
	}

	// public Statement addBytesArgument(byte[] v) {
	// addArugment("blob", v);
	// return this;
	// }

	private void addArugment(String dataType, Object v) {
		JSONObject o = new JSONObject();
		o.put("dataType", dataType);
		o.put("value", v);
		arguments.put(o);
	}

}
