package com.crowd.service.tchannel;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.CrowdMethod;
import com.crowd.service.base.CrowdService;
import com.crowd.tool.misc.DateHelper;
import com.crowd.tool.misc.NumberFormatter;
import com.crowd.tool.misc.Products;

public abstract class SManageServiceBase implements CrowdService {

	@CrowdMethod
	public void info(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String id = input.getString("id");
		boolean history = input.optBoolean("history");
		JSONObject contentObject = new JSONObject(load(context, id));
		String serviceName = contentObject.getString("service");
		String productGroup = contentObject.getString("productGroup");
		String tChannelId = contentObject.getString("channelId");
		String marketDataSource = contentObject.getString("marketDataSource");
		String currencyType = contentObject.getString("currencyType");
		String strategyId = contentObject.getString("id");
		Products products = new Products(context.load("products", productGroup + ".json"));
		NumberFormatter numberFormatter = NumberFormatter.getInstance();
		//
		JSONArray properties = new JSONArray();
		JSONObject property = new JSONObject();
		property.put("name", "策略标识");
		property.put("value", strategyId);
		properties.put(property);
		property = new JSONObject();
		property.put("name", "策略名称");
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
		property.put("value", products.toString());
		properties.put(property);
		property = new JSONObject();
		property.put("name", "通道");
		property.put("value", tChannelId);
		properties.put(property);
		property = new JSONObject();
		property.put("name", "规模");
		property.put("value", numberFormatter.format(contentObject.getDouble("size"), 8) + " " + currencyType);
		properties.put(property);
		property = new JSONObject();
		property.put("name", "已实现盈亏");
		property.put("value", (contentObject.getDouble("profit") >= 0 ? "+" : "")
				+ numberFormatter.format(contentObject.getDouble("profit"), 8));
		properties.put(property);
		property = new JSONObject();
		property.put("name", "已消耗费用");
		property.put("value", "-" + numberFormatter.format(contentObject.getDouble("cost"), 8));
		properties.put(property);
		property = new JSONObject();
		property.put("name", "交易数量");
		property.put("value", contentObject.getInt("transactionCount"));
		properties.put(property);
		property = new JSONObject();
		property.put("name", "最后交易时间");
		property.put("value", DateHelper.dateTime2String(new Date(contentObject.getLong("lastTransactionTime"))));
		properties.put(property);
		property = new JSONObject();
		property.put("name", "最后错误信息");
		property.put("value", contentObject.optString("lastError"));
		properties.put(property);
		//
		JSONArray transactionArray = contentObject.getJSONArray("transactions");
		JSONArray openOrderArray = new JSONArray();
		for (int i = 0; i < transactionArray.length(); i++) {
			JSONObject transactionObject = transactionArray.getJSONObject(i);
			JSONArray orderArray = transactionObject.getJSONArray("orders");
			int openOrderCount = 0;
			int closeOrderCount = 0;
			for (int j = 0; j < orderArray.length(); j++) {
				JSONObject orderObject = orderArray.getJSONObject(j);
				boolean canceled = orderObject.optBoolean("canceled");
				float amount = orderObject.optFloat("amount");
				float execAmount = orderObject.optFloat("execAmount");
				if (canceled || execAmount == amount) {
					closeOrderCount++;
				} else {
					openOrderCount++;
					openOrderArray.put(orderObject);
				}
				//
				orderObject.put("time", DateHelper.dateTime2String(new Date(orderObject.getLong("time"))));
				orderObject.put("price", numberFormatter.format(orderObject.getDouble("price"), 2));
				orderObject.put("avgPrice", numberFormatter.format(orderObject.getDouble("avgPrice"), 2));
				orderObject.put("costValue", numberFormatter.format(orderObject.getDouble("costValue"), 8));
			}
			transactionObject.put("openTime",
					DateHelper.dateTime2String(new Date(transactionObject.getLong("openTime"))));
			transactionObject.put("positionPrice",
					numberFormatter.format(transactionObject.optDouble("positionPrice"), 2));
			transactionObject.put("cost", numberFormatter.format(transactionObject.getDouble("cost"), 8));
			transactionObject.put("openOrderCount", openOrderCount);
			transactionObject.put("closeOrderCount", closeOrderCount);
		}
		if (history) {
			String content = context.load(id + ".history");
			JSONArray historyArray = new JSONArray();
			JSONArray matches = new JSONArray();
			if (StringUtils.isNotEmpty(content)) {
				JSONArray arr = new JSONArray(content);
				for (int i = 0; i < arr.length(); i++) {
					JSONObject o = arr.getJSONObject(i);
					JSONArray m = new JSONArray();
					m.put(o.getLong("openTime"));
					m.put(o.getLong("positionTime"));
					m.put(o.getLong("closeTime"));
					m.put(o.getLong("orderPrice"));
					m.put(o.getLong("takePrice"));
					m.put(o.getLong("stopPrice"));
					m.put(o.getString("positionSide"));
					matches.put(m);
				}
				for (int i = arr.length() - 1; i >= 0; i--) {
					JSONObject o = arr.getJSONObject(i);
					o.put("openTime", DateHelper.dateTime2String(new Date(o.getLong("openTime"))));
					o.put("closeTime", DateHelper.dateTime2String(new Date(o.getLong("closeTime"))));
					o.put("cost", numberFormatter.format(o.getDouble("cost"), 8));
					double balance = o.getDouble("balance");
					o.put("balance", (balance >= 0 ? "+" : "") + numberFormatter.format(balance, 8));
					//
					JSONArray orders = o.getJSONArray("orders");
					for (int j = 0; j < orders.length(); j++) {
						JSONObject order = orders.getJSONObject(j);
						order.put("time", DateHelper.dateTime2String(new Date(order.getLong("time"))));
						order.put("price", numberFormatter.format(order.getDouble("price"), 2));
						order.put("avgPrice", numberFormatter.format(order.getDouble("avgPrice"), 2));
						order.put("costValue", numberFormatter.format(order.getDouble("costValue"), 8));
					}
					//
					historyArray.put(o);
				}
			}
			output.put("history", historyArray);
			output.put("matches", matches);
		}
		//
		//
		output.put("progress", getWorkerProgress(context, "/" + serviceName + "/start", id));
		output.put("tChannelId", tChannelId);
		output.put("strategyId", strategyId);
		output.put("serviceName", serviceName);
		output.put("properties", properties);
		output.put("transactions", transactionArray);
		output.put("openOrders", openOrderArray);
		output.put("productArray", products.toJSONArray());
		output.put("tradeDays", contentObject.optJSONArray("tradeDays"));
	}

	@CrowdMethod
	public final void stopStrategy(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String id = input.getString("id");
		this.stop(context, id);
	}

	protected final void start(CrowdContext context, String id, boolean test) throws Throwable {
		String serviceName = new JSONObject(load(context, id)).getString("service");
		String apiPath = "/" + serviceName + "/start";
		if (getWorkerHandle(context, apiPath, id) != null) {
			throw new IllegalStateException("策略服务已经启动");
		}
		JSONObject input = new JSONObject();
		input.put("id", id);
		input.put("test", test);
		context.asyncInvoke(apiPath, input);
	}

	protected final void stop(CrowdContext context, String id) throws Throwable {
		String serviceName = new JSONObject(load(context, id)).getString("service");
		String apiPath = "/" + serviceName + "/start";
		String handle = getWorkerHandle(context, apiPath, id);
		if (handle != null) {
			context.disposeWorker(handle);
		}
	}

	protected final String getWorkerHandle(CrowdContext context, String apiPath, String id) throws Throwable {
		JSONArray workers = context.listWorkers();
		for (int i = 0; i < workers.length(); i++) {
			JSONObject workerObject = workers.getJSONObject(i);
			String workerApiPath = workerObject.getString("path");
			JSONObject workerParams = new JSONObject(workerObject.getString("params"));
			if (workerApiPath.equals(apiPath) && workerParams.getString("id").equals(id)) {
				return workerObject.getString("handle");
			}
		}
		return null;
	}

	protected final float getWorkerProgress(CrowdContext context, String apiPath, String id) throws Throwable {
		JSONArray workers = context.listWorkers();
		for (int i = 0; i < workers.length(); i++) {
			JSONObject workerObject = workers.getJSONObject(i);
			String workerApiPath = workerObject.getString("path");
			JSONObject workerParams = new JSONObject(workerObject.getString("params"));
			if (workerApiPath.equals(apiPath) && workerParams.getString("id").equals(id)) {
				return workerObject.getFloat("progress");
			}
		}
		return -1;
	}

	@CrowdMethod
	public final void loadInfo(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String content = load(context, input.getString("id"));
		output.put("content", content);
	}

	@CrowdMethod
	public final void saveInfo(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String id = input.getString("id");
		String content = input.getString("content");
		context.save(id + ".info", content);
	}

	@CrowdMethod
	public final void saveTransaction(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String id = input.getString("id");
		JSONObject transaction = input.getJSONObject("transaction");
		JSONArray transactionHistory = new JSONArray();
		String content = context.load(id + ".history");
		if (StringUtils.isNotEmpty(content)) {
			transactionHistory = new JSONArray(content);
		}
		transactionHistory.put(transaction);
		context.save(id + ".history", transactionHistory.toString(4));
	}

	protected final String load(CrowdContext context, String id) throws Throwable {
		return context.load(id + ".info");
	}

}
