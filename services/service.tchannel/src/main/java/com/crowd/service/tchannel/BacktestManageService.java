package com.crowd.service.tchannel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.CrowdInitContext;
import com.crowd.service.base.CrowdMethod;
import com.crowd.tool.misc.NumberFormatter;

public class BacktestManageService extends SManageServiceBase {

	private Map<String, JSONObject> strategyTemplates = new HashMap<String, JSONObject>();

	@Override
	public void init(CrowdInitContext context) throws Throwable {

	}

	@Override
	public String getName() {
		return "backtest-manage";
	}

	@CrowdMethod
	public void list(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String content = context.load("backtests.json");
		JSONArray arr = new JSONArray();
		if (StringUtils.isNotEmpty(content)) {
			arr = new JSONArray(content);
		}
		JSONArray treeNodes = new JSONArray();
		JSONArray expandedKeys = new JSONArray();
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			String id = o.getString("id");
			JSONObject groupNode = new JSONObject();
			groupNode.put("key", id);
			groupNode.put("label", o.getString("name"));
			groupNode.put("type", 0);
			JSONArray childNodes = new JSONArray();
			JSONArray testArray = o.getJSONArray("backtests");
			for (int j = 0; j < testArray.length(); j++) {
				JSONObject testObject = testArray.getJSONObject(j);
				childNodes.put(new JSONObject("{key: \"" + testObject.getString("id") + "\", label: \""
						+ testObject.getString("name") + "\", type: 1}"));
			}
			groupNode.put("children", childNodes);
			treeNodes.put(groupNode);
//			expandedKeys.put(id);
			//
			JSONObject templateObject = new JSONObject();
			templateObject.put("id", o.getString("id"));
			templateObject.put("name", o.getString("name"));
			templateObject.put("size", o.getDouble("size"));
			templateObject.put("currencyType", o.getString("currencyType"));
			templateObject.put("marketDataSource", o.getString("marketDataSource"));
			templateObject.put("productGroup", o.getString("productGroup"));
			templateObject.put("service", o.getString("service"));
			strategyTemplates.put(id, templateObject);
		}
		//
		output.put("treeNodes", treeNodes);
		output.put("expandedKeys", expandedKeys);
		output.put("selection", arr.length() > 0 ? 0 : -1);
	}

	@CrowdMethod
	public void template(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String id = input.getString("id");

		JSONObject contentObject = strategyTemplates.get(id);
		String serviceName = contentObject.getString("service");
		String marketDataSource = contentObject.getString("marketDataSource");
		String currencyType = contentObject.getString("currencyType");
		String templateId = contentObject.getString("id");
		NumberFormatter numberFormatter = NumberFormatter.getInstance();
		//
		JSONArray properties = new JSONArray();
		JSONObject property = new JSONObject();
		property.put("name", "模板标识");
		property.put("value", templateId);
		properties.put(property);
		property = new JSONObject();
		property.put("name", "模板名称");
		property.put("value", contentObject.getString("name"));
		properties.put(property);
		property = new JSONObject();
		property.put("name", "策略服务");
		property.put("value", serviceName);
		properties.put(property);
		property = new JSONObject();
		property.put("name", "市场数据");
		property.put("value", marketDataSource);
		properties.put(property);
		property = new JSONObject();
		property.put("name", "交易产品组");
		property.put("value", contentObject.getString("productGroup"));
		properties.put(property);
		property = new JSONObject();
		property.put("name", "规模");
		property.put("value", numberFormatter.format(contentObject.getDouble("size"), 8) + " " + currencyType);
		properties.put(property);
		//
		output.put("properties", properties);
	}

	@CrowdMethod
	public synchronized void startStrategy(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String templatedId = input.getString("id");
		String arguments = input.getString("arguments");
		JSONObject templateObject = strategyTemplates.get(templatedId);
		String testName = newTestName();
		String testId = templatedId + "_" + testName;
		JSONObject newBacktestObject = new JSONObject();
		newBacktestObject.put("id", testId);
		newBacktestObject.put("name", testName);
		newBacktestObject.put("size", templateObject.getDouble("size"));
		newBacktestObject.put("currencyType", templateObject.getString("currencyType"));
		newBacktestObject.put("marketDataSource", templateObject.getString("marketDataSource"));
		newBacktestObject.put("productGroup", templateObject.getString("productGroup"));
		newBacktestObject.put("service", templateObject.getString("service"));
		newBacktestObject.put("transactions", new JSONArray());
		newBacktestObject.put("transactionCount", 0);
		newBacktestObject.put("lastTransactionTime", 0);
		newBacktestObject.put("profit", 0);
		newBacktestObject.put("cost", 0);
		newBacktestObject.put("channelId", "测试");
		newBacktestObject.put("arguments", arguments);

		JSONArray arr = new JSONArray(context.load("backtests.json"));
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			String id = o.getString("id");
			if (id.equals(templatedId)) {
				JSONArray testArray = o.getJSONArray("backtests");
				JSONObject testObj = new JSONObject();
				testObj.put("id", testId);
				testObj.put("name", testName);
				testArray.put(testObj);
			}
		}
		context.save("backtests.json", arr.toString(4));
		context.save(testId + ".info", newBacktestObject.toString(4));

		//
		JSONObject treeNode = new JSONObject();
		treeNode.put("key", testId);
		treeNode.put("label", testName);
		treeNode.put("type", 1);
		output.put("newTreeNode", treeNode);

		//
		try {
			this.start(context, testId, true);
		} catch (Throwable t) {
			newBacktestObject.put("lastError", t.getMessage());
			context.save(testId + ".info", newBacktestObject.toString(4));
		}
	}

	@CrowdMethod
	public synchronized void deleteTest(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String testId = input.getString("id");
		JSONArray arr = new JSONArray(context.load("backtests.json"));
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			String templatedId = o.getString("id");
			if (testId.startsWith(templatedId + "_")) {
				JSONArray testArray = o.getJSONArray("backtests");
				JSONArray newTestArray = new JSONArray();
				for (int j = 0; j < testArray.length(); j++) {
					if (testArray.getJSONObject(j).getString("id").equals(testId)) {
						// 判断回测是否还在运行中
						String serviceName = new JSONObject(load(context, testId)).getString("service");
						String handle = getWorkerHandle(context, "/" + serviceName + "/start", testId);
						if (handle != null) {
							throw new IllegalStateException("回测正在运行，不能删除");
						}
					} else {
						newTestArray.put(testArray.getJSONObject(j));
					}
				}
				o.put("backtests", newTestArray);
			}
		}
		context.save("backtests.json", arr.toString(4));
		context.delete(testId + ".info");
		context.delete(testId + ".history");
	}

	@CrowdMethod
	public synchronized void createTemplate(CrowdContext context, JSONObject input, JSONObject output)
			throws Throwable {
		JSONArray arr = new JSONArray();
		try {
			arr = new JSONArray(context.load("backtests.json"));
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
		JSONObject templateObject = input.getJSONObject("templateInfo");
		templateObject.put("id", newId);
		templateObject.put("backtests", new JSONArray());
		arr.put(templateObject);
		context.save("backtests.json", arr.toString(4));
		//
		this.list(context, input, output);
		output.put("selection", arr.length() - 1);
	}

	@CrowdMethod
	public synchronized void deleteTemplate(CrowdContext context, JSONObject input, JSONObject output)
			throws Throwable {
		String templateId = input.getString("id");
		JSONArray arr = new JSONArray(context.load("backtests.json"));
		JSONArray newArr = new JSONArray();
		int selection = -1;
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			if (o.getString("id").equals(templateId)) {
				selection = i;
				if (o.getJSONArray("backtests").length() > 0) {
					throw new IllegalStateException("模板下存在回测实例，不能删除");
				}
			} else {
				newArr.put(o);
			}
		}
		context.save("backtests.json", newArr.toString(4));
		//
		this.list(context, input, output);
		//
		if (selection == newArr.length()) {
			selection--;
		}
		output.put("selection", newArr.length() > 0 ? selection : -1);
	}

	private final static String newTestName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return sdf.format(new Date());
	}

}
