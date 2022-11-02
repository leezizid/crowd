package com.crowd.service.base;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.type.GUID;

public interface CrowdContext {

	public JSONObject invoke(String path, JSONObject inputObject) throws Throwable;

	public String asyncInvoke(String path, JSONObject inputObject) throws Throwable;

	public void sendMessage(String topic, JSONObject message) throws Throwable;

	public void sendMessage(String topic, JSONObject message, Set<String> users, Set<String> clients) throws Throwable;

	public TopicSubscriberHandle subscribeTopic(String topic, TopicSubscriber subscriber) throws Throwable;

	public JSONArray listWorkers() throws Throwable;

	public void disposeWorker(String workerHandle) throws Throwable;

	public void insert(Class<? extends TableDefine> table, JSONObject fieldValues) throws Throwable;

	public int update(Class<? extends TableDefine> table, JSONObject fieldValues) throws Throwable;

	public int deleteById(Class<? extends TableDefine> table, GUID recid) throws Throwable;

	public int deleteBy(Class<? extends TableDefine> table, JSONObject fieldValues) throws Throwable;

	public JSONObject findById(Class<? extends TableDefine> table, GUID recid) throws Throwable;

	public JSONArray findAll(Class<? extends TableDefine> table) throws Throwable;

	public JSONArray findAll(Class<? extends TableDefine> table, String orderInfo) throws Throwable;

	public JSONArray findBy(Class<? extends TableDefine> table, JSONObject fieldValues, String orderInfo)
			throws Throwable;

	public JSONArray executeQuery(Statement statement) throws Throwable;

	public int executeUpdate(Statement statement) throws Throwable;

	public Statement createPrepareStatement(String prepareSql);

	public void changeUserSession(String userName, String userPwd) throws Throwable;

	public Logger getLogger();

	public void save(String name, String content) throws Throwable;

	public String load(String name) throws Throwable;
	
	public void delete(String name) throws Throwable;
	
	public String load(String domain, String name) throws Throwable;

}
