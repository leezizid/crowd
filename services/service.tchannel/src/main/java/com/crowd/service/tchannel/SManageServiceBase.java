package com.crowd.service.tchannel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.CrowdMethod;
import com.crowd.service.base.CrowdService;
import com.crowd.tool.misc.DateHelper;
import com.crowd.tool.misc.NumberFormatter;
import com.crowd.tool.misc.Products;
import com.crowd.tool.misc.TradeDays;

public abstract class SManageServiceBase implements CrowdService {

	@CrowdMethod
	public void info(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String id = input.getString("id");
		boolean history = input.optBoolean("history");
		boolean profits = input.optBoolean("profits");
		boolean plStat = input.optBoolean("plStat");
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
		property.put("name", "策略参数");
		property.put("value", contentObject.has("arguments") ? contentObject.getString("arguments") : "");
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
				float volume = orderObject.optFloat("volume");
				float execVolume = orderObject.optFloat("execVolume");
				if (canceled || execVolume == volume) {
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
		if (history || profits) {
			String content = context.load(id + ".history");
			JSONArray profitArray = new JSONArray();
			String startDay = "";
			String endDay = "";
			if (StringUtils.isNotEmpty(content)) {
				Map<String, JSONArray> dayProfitInfos = new HashMap<String, JSONArray>();
				JSONArray arr = new JSONArray(content);
				for (int i = 0; i < arr.length(); i++) {
					JSONObject o = arr.getJSONObject(i);
					String day = TradeDays.matchTradeDay(o.getLong("closeTime"));
					if (i == 0) {
						startDay = day;
						endDay = day;
					} else {
						endDay = day;
					}
					if (!dayProfitInfos.containsKey(day)) {
						JSONArray dayProfit = new JSONArray();
						dayProfit.put(day);
						dayProfit.put(0);
						dayProfit.put(0);
						dayProfitInfos.put(day, dayProfit);
					}
					JSONArray dayProfit = dayProfitInfos.get(day);
					dayProfit.put(1, dayProfit.getDouble(1) + o.getDouble("balance"));
					dayProfit.put(2,
							new BigDecimal(dayProfit.getDouble(2) + o.getDouble("balance") - o.getDouble("cost"))
									.setScale(0, RoundingMode.HALF_UP));
				}
				String[] tradeDays = TradeDays.getTradeDayList(startDay, endDay);
				JSONArray tradeDayArray = new JSONArray();
				for (int i = 0; i < tradeDays.length; i++) {
					String day = tradeDays[i];
					tradeDayArray.put(day);
					JSONArray dayProfit = dayProfitInfos.get(day);
					if (i == 0) {
						profitArray.put(dayProfit);
					} else {
						JSONArray profitInfo = new JSONArray();
						profitInfo.put(day);
						JSONArray prevProfitInfo = profitArray.getJSONArray(profitArray.length() - 1);
						if (dayProfit != null) {
							profitInfo.put(dayProfit.getDouble(1) + prevProfitInfo.getDouble(1));
							profitInfo.put(dayProfit.getDouble(2) + prevProfitInfo.getDouble(2));
						} else {
							profitInfo.put(prevProfitInfo.getDouble(1));
							profitInfo.put(prevProfitInfo.getDouble(2));
						}
						profitArray.put(profitInfo);
					}
				}
				output.put("tradeDays", tradeDayArray);
			}
			output.put("profits", profitArray);
			//
			if(history) {
				JSONArray historyArray = new JSONArray();
				JSONArray matches = new JSONArray();
				int historyCount = 0;
				if (StringUtils.isNotEmpty(content)) {
					JSONArray arr = new JSONArray(content);
					historyCount = arr.length();
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
						m.put(o.getDouble("balance"));
						matches.put(m);
					}
					int currentPage = input.getInt("currentPage");
					int pageCount = 18;
					for (int i = (currentPage - 1) * pageCount; i < arr.length(); i++) {
						JSONObject o = arr.getJSONObject(i);
						long time = o.getLong("openTime");
						o.put("openTime", DateHelper.dateTime2String(new Date(o.getLong("openTime"))));
						o.put("closeTime", DateHelper.dateTime2String(new Date(o.getLong("closeTime"))));
						o.put("cost", numberFormatter.format(o.getDouble("cost"), 8));
						double balance = o.getDouble("balance");
						o.put("balance", (balance >= 0 ? "+" : "") + numberFormatter.format(balance, 8));
						o.put("tradeDay", TradeDays.matchTradeDay(time));
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
						if (historyArray.length() >= pageCount) {
							break;
						}
					}
				}
				output.put("history", historyArray);
				output.put("historyCount", historyCount);
				output.put("matches", matches);
			}
		}
		if (plStat) {
			String content = context.load(id + ".history");
			Map<Double, Integer> statisticsMap = new HashMap<Double, Integer>();
			if (StringUtils.isNotEmpty(content)) {
				JSONArray arr = new JSONArray(content);
				for (int i = 0; i < arr.length(); i++) {
					JSONObject o = arr.getJSONObject(i);
					double v = o.getDouble("balance");
					if (!statisticsMap.containsKey(v)) {
						statisticsMap.put(v, 0);
					}
					statisticsMap.put(v, statisticsMap.get(v) + 1);
				}
			}
			List<Double> profitValueArray = new ArrayList<Double>(statisticsMap.keySet());
			profitValueArray.sort(new Comparator<Double>() {
				@Override
				public int compare(Double o1, Double o2) {
					return o1.compareTo(o2);
				}
			});
			JSONArray statisticsArray = new JSONArray();
			JSONArray xArray = new JSONArray();
			JSONArray y1Array = new JSONArray();
			JSONArray y2Array = new JSONArray();
			JSONArray y3Array = new JSONArray();
			for (int i = 0; i < profitValueArray.size(); i++) {
				double v = profitValueArray.get(i);
				int count = statisticsMap.get(profitValueArray.get(i));
				xArray.put(Math.abs(v));
				if (v < 0) {
					y1Array.put(count);
					y2Array.put("-");
					y3Array.put("-");
				} else if (v == 0) {
					y1Array.put("-");
					y2Array.put(count);
					y3Array.put("-");
				} else if (v > 0) {
					y1Array.put("-");
					y2Array.put("-");
					y3Array.put(count);
				}
			}
			statisticsArray.put(xArray);
			statisticsArray.put(y1Array);
			statisticsArray.put(y2Array);
			statisticsArray.put(y3Array);
			output.put("plStats", statisticsArray);
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
//		output.put("productArray", products.toJSONArray());
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
	
	@CrowdMethod
	public final void saveTransactions(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String id = input.getString("id");
		JSONArray transactions = input.getJSONArray("transactions");
		context.save(id + ".history", transactions.toString(4));
	}

	protected final String load(CrowdContext context, String id) throws Throwable {
		return context.load(id + ".info");
	}

}
