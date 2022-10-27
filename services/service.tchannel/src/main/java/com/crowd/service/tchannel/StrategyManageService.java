package com.crowd.service.tchannel;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.CrowdInitContext;
import com.crowd.service.base.CrowdMethod;

public class StrategyManageService extends SManageServiceBase {

	@Override
	public void init(CrowdInitContext context) throws Throwable {

	}
	
	@Override
	public void postInit(CrowdInitContext context) throws Throwable {
		
	}


	@Override
	public String getName() {
		return "strategy-manage";
	}

	@CrowdMethod
	public void list(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String content = context.load("strategys.json");
		JSONArray arr = new JSONArray();
		if (StringUtils.isNotEmpty(content)) {
			arr = new JSONArray(content);
		}
		JSONArray treeNodes = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("label", "所有策略");
		rootNode.put("key", "0");
		rootNode.put("disabled", true);
		treeNodes.put(rootNode);
		JSONArray expandedKeys = new JSONArray();
		expandedKeys.put("0");
		JSONArray children = new JSONArray();
		rootNode.put("children", children);
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			JSONObject strategyNode = new JSONObject();
			strategyNode.put("label", o.getString("name"));
			strategyNode.put("key", o.getString("id"));
			children.put(strategyNode);
		}
		//
		output.put("treeNodes", treeNodes);
		output.put("expandedKeys", expandedKeys);
	}

	@CrowdMethod
	public void startStrategy(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String id = input.getString("id");
		this.start(context, id, false);
	}

	@CrowdMethod
	public synchronized void createStrategy(CrowdContext context, JSONObject input, JSONObject output)
			throws Throwable {
		JSONArray arr = new JSONArray();
		try {
			arr = new JSONArray(context.load("strategys.json"));
		} catch (Throwable t) {

		}
		int maxId = 0;
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			int id = Integer.parseInt(o.getString("id"));
			if (id > maxId) {
				maxId = id;
			}
		}
		String newId = String.valueOf(++maxId);
		//
		JSONObject strategyObject = input.getJSONObject("strategyInfo");
		strategyObject.put("id", newId);
		strategyObject.put("transactions", new JSONArray());
		strategyObject.put("profit", 0);
		strategyObject.put("cost", 0);
		strategyObject.put("transactionCount", 0);
		strategyObject.put("lastTransactionTime", 0);
		//
		arr.put(new JSONObject(
				"{\"id\" : \"" + newId + "\", \"name\" : \"" + strategyObject.getString("name") + "\"}"));
		//
		context.save("strategys.json", arr.toString(4));
		context.save(newId + ".info", strategyObject.toString(4));
		//
		this.list(context, input, output);
		output.put("selection", arr.length() - 1);
	}

	@CrowdMethod
	public synchronized void deleteStrategy(CrowdContext context, JSONObject input, JSONObject output)
			throws Throwable {
		String id = input.getString("id");
		JSONArray arr = new JSONArray(context.load("strategys.json"));
		JSONArray newArr = new JSONArray();
		int selection = -1;
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			if (o.getString("id").equals(id)) {
				selection = i;
				// 判断回测是否还在运行中
				String serviceName = new JSONObject(load(context, id)).getString("service");
				String handle = getWorkerHandle(context, "/" + serviceName + "/start", id);
				if (handle != null) {
					throw new IllegalStateException("策略正在运行，不能删除");
				}
			} else {
				newArr.put(o);
			}
		}
		context.save("strategys.json", newArr.toString(4));
		context.delete(id + ".info");
		context.delete(id + ".history");
		//
		this.list(context, input, output);
		//
		if (selection == newArr.length()) {
			selection--;
		}
		output.put("selection", newArr.length() > 0 ? selection : -1);
	}

	@CrowdMethod
	public void createOrder(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String strategyId = input.getString("strategyId");
		JSONObject strategyInfo = new JSONObject(load(context, strategyId));
		String serviceName = strategyInfo.getString("service");
		String apiPath = "/" + serviceName + "/order";
		context.invoke(apiPath, input);
	}

	@CrowdMethod
	public void cancelOrder(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String id = input.getString("id");
		JSONObject o = new JSONObject(load(context, id));
		String serviceName = o.getString("service");
		String apiPath = "/" + serviceName + "/start";
		if (getWorkerHandle(context, apiPath, id) == null) {
			throw new IllegalStateException("策略服务未启动");
		}
		String serverOrderId = input.getString("serverOrderId");
		String symbol = input.getString("symbol");
		String tChannelId = input.getString("tChannelId");
		if (StringUtils.isEmpty(serverOrderId)) {
			// 直接废单
			JSONObject messageObject = new JSONObject();
			messageObject.put("strategyId", id);
			messageObject.put("clientOrderId", input.getString("clientOrderId"));
			context.sendMessage("orderConfirmFailed", messageObject);
		} else {
			// 提交通道撤单
			TChannelService.cancelOrder(context, tChannelId, symbol, serverOrderId);
		}
	}

}
