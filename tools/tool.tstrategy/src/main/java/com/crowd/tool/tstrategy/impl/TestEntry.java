package com.crowd.tool.tstrategy.impl;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdWorkerContext;
import com.crowd.service.base.Logger;
import com.crowd.service.base.Statement;
import com.crowd.service.base.TableDefine;
import com.crowd.service.base.TopicSubscriber;
import com.crowd.service.base.TopicSubscriberHandle;
import com.crowd.service.type.GUID;
import com.crowd.tool.tstrategy.StrategyServiceBase;

public class TestEntry {

	public final static void main(String[] args) throws Throwable {
		String serviceName = System.getProperty("ServiceName");
		String arguments = System.getProperty("Arguments");
		String symbol = System.getProperty("Symbol");
		String baseRate = System.getProperty("Rate");
		String dataSource = System.getProperty("DataSource");

		//
		test(serviceName, arguments, symbol, baseRate, dataSource);
	}

	private final static void test(String serviceName, String arguments, String symbol, String baseRate,
			String dataSource) {
		try {
			//
			JSONObject infoObject = new JSONObject();
			infoObject.put("id", "");
			infoObject.put("name", "");
			infoObject.put("productGroup", "");
			infoObject.put("channelId", "");
			infoObject.put("currencyType", "CNY");
			infoObject.put("size", 0);
			infoObject.put("profit", 0);
			infoObject.put("cost", 0);
			infoObject.put("marketDataSource", "history:" + symbol + "," + dataSource);
			infoObject.put("arguments", arguments);
			//
			JSONArray productsObject = new JSONArray(
					"[{\"symbol\": \"" + symbol + "\", \"baseRate\": " + baseRate + "}]");

			String info = infoObject.toString(4);
			String products = productsObject.toString(4);
			StrategyServiceBase strategyService = (StrategyServiceBase) Class.forName(serviceName)
					.getDeclaredConstructor().newInstance();
			strategyService.init(null);
			TestWorkerContext context = new TestWorkerContext(info, products);
			JSONObject inputObject = new JSONObject("{\"id\": \"\", \"test\": true}");
			strategyService.start(context, inputObject);
			JSONObject result = new JSONObject();
			result.put("strategyInfo", new JSONObject(context.getStrategyInfo()));
			result.put("transactions", context.getTransactions());
			System.out.println(result.toString());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}

class TestWorkerContext implements CrowdWorkerContext {

	private boolean disposed;

	private String products;

	private JSONArray transactions;

	private String strategyInfo;

	public TestWorkerContext(String strategyInfo, String products) {
		this.strategyInfo = strategyInfo;
		this.products = products;
//		System.out.println(this.strategyInfo);
//		System.out.println(this.products);
	}

	public JSONArray getTransactions() {
		return transactions;
	}

	public String getStrategyInfo() {
		return strategyInfo;
	}

	@Override
	public JSONObject invoke(String path, JSONObject inputObject) throws Throwable {
		JSONObject outputObject = new JSONObject();
		if (path.equals("/backtest-manage/saveTransactions")) {
			this.transactions = inputObject.getJSONArray("transactions");
			return outputObject;
		} else if (path.equals("/backtest-manage/saveInfo")) {
			this.strategyInfo = inputObject.getString("content");
			return outputObject;
		} else if (path.equals("/backtest-manage/loadInfo")) {
			outputObject.put("content", this.strategyInfo);
			return outputObject;
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public String load(String domain, String name) throws Throwable {
		if (domain.equals("products")) {
			return products;
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public void dispose() {
		this.disposed = true;
	}

	@Override
	public void reportWork(float progress, String info) {

	}

	@Override
	public void save(String name, String content) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public String load(String name) throws Throwable {
		throw new UnsupportedOperationException();
	}

	////////////////////////

	@Override
	public TopicSubscriberHandle subscribeTopic(String topic, TopicSubscriber subscriber) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendMessage(String topic, JSONObject message, Set<String> users, Set<String> clients) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendMessage(String topic, JSONObject message) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public JSONArray listWorkers() throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public Logger getLogger() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void disposeWorker(String workerHandle) throws Throwable {
		throw new UnsupportedOperationException();

	}

	@Override
	public void changeUserSession(String userName, String userPwd) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public String asyncInvoke(String path, JSONObject inputObject) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setResult(JSONObject dataObject) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setException(JSONObject errorObject) {
		throw new UnsupportedOperationException();
	}

	/////////////////////

	@Override
	public void insert(Class<? extends TableDefine> table, JSONObject fieldValues) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Class<? extends TableDefine> table, JSONObject fieldValues) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public JSONObject findById(Class<? extends TableDefine> table, GUID recid) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public JSONArray findBy(Class<? extends TableDefine> table, JSONObject fieldValues, String orderInfo)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public JSONArray findAll(Class<? extends TableDefine> table, String orderInfo) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public JSONArray findAll(Class<? extends TableDefine> table) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate(Statement statement) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public JSONArray executeQuery(Statement statement) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public int deleteById(Class<? extends TableDefine> table, GUID recid) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public int deleteBy(Class<? extends TableDefine> table, JSONObject fieldValues) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(String name) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public Statement createPrepareStatement(String prepareSql) {
		throw new UnsupportedOperationException();
	}

}
