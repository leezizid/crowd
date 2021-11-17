package com.crowd.service.base.impl;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.Logger;
import com.crowd.service.base.Statement;
import com.crowd.service.base.TableDefine;
import com.crowd.service.base.TopicSubscriber;
import com.crowd.service.base.TopicSubscriberHandle;
import com.crowd.service.type.GUID;

public class CrowdContextImpl implements CrowdContext {

	String sid;

	String tid;

	private boolean hasUpdate;

	private String serviceName;

	CrowdContextImpl(String sid, String tid, String serviceName) {
		this.sid = sid;
		this.tid = tid;
		this.serviceName = serviceName;
	}

	public final JSONObject invoke(String path, JSONObject inputObject) throws Throwable {
		return CrowdApp.invokeRemoteService(sid, tid, path, inputObject, false);
	}

	@Override
	public JSONObject asyncInvoke(String path, JSONObject inputObject) throws Throwable {
		return CrowdApp.invokeRemoteService(sid, tid, path, inputObject, true);
	}

	@Override
	public void sendMessage(String topic, JSONObject message) throws Throwable {
		CrowdApp.sendRemoteMessage(topic, message);
	}

	@Override
	public void sendMessage(String topic, JSONObject message, Set<String> users, Set<String> clients) throws Throwable {
		throw new UnsupportedOperationException("暂不支持给指定用户和客户端发送消息");
	}

	@Override
	public JSONArray listWorkers() throws Throwable {
		JSONObject result = invoke(CrowdApp.CORE_SERVICE_WORKER_LIST, new JSONObject());
		return result.getJSONArray("workers");
	}

	@Override
	public void disposeWorker(String workerHandle) throws Throwable {
		JSONObject inputObject = new JSONObject();
		inputObject.put("workerHandle", workerHandle);
		invoke(CrowdApp.CORE_SERVICE_WORKER_DISPOSE, inputObject);
	}

	public final void insert(Class<? extends TableDefine> table, JSONObject fieldValues) throws Throwable {
		hasUpdate = true;
		JSONObject inputObject = initInputObject(table);
		inputObject.put("fieldValues", fieldValues);
		invoke(CrowdApp.CORE_SERVICE_DML_INSERT, inputObject);
	}

	public final int update(Class<? extends TableDefine> table, JSONObject fieldValues) throws Throwable {
		hasUpdate = true;
		JSONObject inputObject = initInputObject(table);
		inputObject.put("fieldValues", fieldValues);
		return invoke(CrowdApp.CORE_SERVICE_DML_UPDATE, inputObject).getInt("count");
	}

	public final int deleteById(Class<? extends TableDefine> table, GUID recid) throws Throwable {
		hasUpdate = true;
		JSONObject fieldValues = new JSONObject();
		fieldValues.put("recid", recid);
		return deleteBy(table, fieldValues);
	}

	public final int deleteBy(Class<? extends TableDefine> table, JSONObject fieldValues) throws Throwable {
		if (fieldValues == null || fieldValues.length() == 0) {
			throw new IllegalArgumentException("不允许无参数的删除操作");
		}
		hasUpdate = true;
		JSONObject inputObject = initInputObject(table);
		inputObject.put("fieldValues", fieldValues);
		return invoke(CrowdApp.CORE_SERVICE_DML_DELETE, inputObject).getInt("count");
	}

	public final JSONObject findById(Class<? extends TableDefine> table, GUID recid) throws Throwable {
		JSONObject fieldValues = new JSONObject();
		fieldValues.put("recid", recid);
		JSONArray rows = findBy(table, fieldValues, null);
		if (rows.length() > 0) {
			return rows.getJSONObject(0);
		}
		return null;
	}

	public final JSONArray findAll(Class<? extends TableDefine> table) throws Throwable {
		return findAll(table, "");
	}

	public final JSONArray findAll(Class<? extends TableDefine> table, String orderInfo) throws Throwable {
		return findBy(table, new JSONObject(), orderInfo);
	}

	public final JSONArray findBy(Class<? extends TableDefine> table, JSONObject fieldValues, String orderInfo)
			throws Throwable {
		JSONObject inputObject = initInputObject(table);
		inputObject.put("fieldValues", fieldValues);
		inputObject.put("orderInfo", orderInfo);
		return invoke(CrowdApp.CORE_SERVICE_DML_FIND, inputObject).getJSONArray("rows");
	}

	public final JSONArray executeQuery(Statement statement) throws Throwable {
		return ((StatementImpl) statement).executeQuery(this);
	}

	public final int executeUpdate(Statement statement) throws Throwable {
		hasUpdate = true;
		return ((StatementImpl) statement).executeUpdate(this);
	}

	public final Statement createPrepareStatement(String prepareSql) {
		return new StatementImpl(prepareSql);
	}

	public void changeUserSession(String userName, String userPwd) throws Throwable {
		JSONObject input = new JSONObject();
		input.put("userName", userName);
		input.put("userPwd", userPwd);
		invoke(CrowdApp.CORE_SERVICE_USER_CHANGE, input);
	}

	private final JSONObject initInputObject(Class<? extends TableDefine> table) {
		JSONObject inputObject = new JSONObject();
		inputObject.put("tableName", CrowdApp.registerTables.get(table).getName());
		return inputObject;
	}

	void resolveTrans(boolean commit) throws Throwable {
		if (hasUpdate) {
			JSONObject input = new JSONObject();
			input.put("commit", commit);
			invoke(CrowdApp.CORE_SERVICE_DML_RESOLVE, input);
		}
	}

	public Logger getLogger() {
		return null;
	}

	@Override
	public void save(String name, String content) throws Throwable {
		JSONObject inputObject = new JSONObject();
		inputObject.put("domain", this.serviceName);
		inputObject.put("name", name);
		inputObject.put("content", content);
		invoke(CrowdApp.CORE_SERVICE_FILE_SAVE, inputObject);
	}

	@Override
	public String load(String name) throws Throwable {
		return load(this.serviceName, name);
	}

	@Override
	public String load(String domain, String name) throws Throwable {
		JSONObject inputObject = new JSONObject();
		inputObject.put("domain", domain);
		inputObject.put("name", name);
		JSONObject outputObject = invoke(CrowdApp.CORE_SERVICE_FILE_LOAD, inputObject);
		return outputObject.getString("content");
	}

	@Override
	public void delete(String name) throws Throwable {
		JSONObject inputObject = new JSONObject();
		inputObject.put("domain", this.serviceName);
		inputObject.put("name", name);
		invoke(CrowdApp.CORE_SERVICE_FILE_DELETE, inputObject);
	}

	@Override
	public TopicSubscriberHandle subscribeTopic(String topic, TopicSubscriber subscriber) throws Throwable {
		return CrowdApp.subscribeTopic(topic, subscriber);
	}

}
