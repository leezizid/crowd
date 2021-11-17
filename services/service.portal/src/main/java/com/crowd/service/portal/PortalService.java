package com.crowd.service.portal;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.CrowdInitContext;
import com.crowd.service.base.CrowdMethod;
import com.crowd.service.base.CrowdService;
import com.crowd.service.base.CrowdWorker;
import com.crowd.service.base.CrowdWorkerContext;

public class PortalService implements CrowdService {

	@Override
	public String getName() {
		return "portal";
	}

	@Override
	public void init(CrowdInitContext context) throws Throwable {
	}

	@CrowdMethod
	public void login(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String userName = input.optString("userName");
		String userPwd = input.optString("userPwd");
		context.changeUserSession(userName, userPwd);
		output.put("userName", userName);
	}

	@CrowdMethod
	public void logout(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		context.changeUserSession("", "");
		output.put("userName", "");
	}

	@CrowdMethod
	public void listActiveWorkers(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
//		JSONArray activeWorkers = new JSONArray();
		output.put("activeWorkers", context.listWorkers());
//		JSONArray arr = context.listWorkers();
//		for (int i = 0; i < arr.length(); i++) {
//			JSONObject o = arr.getJSONObject(i);
//			JSONObject row = new JSONObject();
//			row.put("name", o.getString("handle"));
//			row.put("value", o.getString("path"));
//			activeWorkers.put(row);
//		}
	}

	@CrowdMethod
	public void disposeWorker(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String workerHandle = input.getString("workerHandle");
		context.disposeWorker(workerHandle);
	}

	@CrowdMethod
	public void startWorker(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String apiPath = input.getString("path");
		context.asyncInvoke(apiPath, new JSONObject());
	}

	@CrowdWorker
	public void testWorker(CrowdWorkerContext context, JSONObject input) throws Throwable {
		while (!context.isDisposed()) {
			System.out.println("..." + System.currentTimeMillis());
			Thread.sleep(2000);
		}
	}

	@CrowdMethod
	public void listAllWorkers(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		//
		JSONObject strategyServiceNode = new JSONObject();
		strategyServiceNode.put("label", "策略服务");
		strategyServiceNode.put("key", "1");
		strategyServiceNode.put("disabled", true);
		JSONArray childNodes = new JSONArray();
		childNodes.put(new JSONObject("{key: \"" + "/portal/testWorker" + "\", label: \"测试服务1\"}"));
		childNodes.put(new JSONObject("{key: \"" + "/test_strategy1/start" + "\", label: \"测试策略1\"}"));
		childNodes.put(new JSONObject("{key: \"" + "/test_strategy2/start" + "\", label: \"测试策略2\"}"));
		strategyServiceNode.put("children", childNodes);
		//
		JSONObject tchannelServiceNode = new JSONObject();
		tchannelServiceNode.put("key", "2");
		tchannelServiceNode.put("label", "通道服务");
		tchannelServiceNode.put("disabled", true);
		tchannelServiceNode.put("children", new JSONArray());
		//
		JSONObject otherServiceNode = new JSONObject();
		otherServiceNode.put("key", "3");
		otherServiceNode.put("label", "其他");
		otherServiceNode.put("disabled", true);
		otherServiceNode.put("children", new JSONArray());
		//
		JSONArray treeNodes = new JSONArray();
		treeNodes.put(strategyServiceNode);
		treeNodes.put(tchannelServiceNode);
		treeNodes.put(otherServiceNode);
		//
		JSONArray expandedKeys = new JSONArray();
		expandedKeys.put("1");
		expandedKeys.put("2");
		expandedKeys.put("3");
		//
		outputObject.put("allWorkers", treeNodes);
		outputObject.put("expandedKeys", expandedKeys);
	}

	
//	@CrowdSubscriber("portal.workers")
//	public void onWorkersChanged(CrowdContext context, JSONObject message) throws Throwable {
////		System.out.println("----" + message);
//	}
	
}
