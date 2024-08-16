package com.crowd.service.stock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.CrowdInitContext;
import com.crowd.service.base.CrowdMethod;
import com.crowd.service.base.CrowdService;
import com.crowd.tool.misc.NumberFormatter;

public class SZ50Service implements CrowdService {

	@Override
	public String getName() {
		return "sz50";
	}

	@Override
	public void init(CrowdInitContext context) throws Throwable {
	}

	@Override
	public void postInit(CrowdInitContext context) throws Throwable {
	}

	@CrowdMethod
	public void listStocks(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		try {
			NumberFormatter numberFormatter = NumberFormatter.getInstance();
			JSONArray stockArray = new JSONArray(context.load(input.getString("tradePeriod") + ".json"));
			List<String> activeStockList = new ArrayList<String>();
			for (int i = 0; i < stockArray.length(); i++) {
				JSONObject stockInfo = stockArray.getJSONObject(i);
				if (stockInfo.has("transactions")) {
					JSONArray transactions = stockInfo.getJSONArray("transactions");
					double historyProfit = 0;
					for (int j = 0; j < transactions.length(); j++) {
						JSONObject transaction = transactions.getJSONObject(j);
						updateTransactionData(transaction);
						if (StringUtils.isNotEmpty(transaction.getString("finishTime"))) {
							historyProfit += transaction.getDouble("achievedProfit");
						}
					}
					stockInfo.put("achievedProfit", 0);
					stockInfo.put("historyProfit", historyProfit);
					stockInfo.put("amount", 0);
					stockInfo.put("price", 0);
					stockInfo.put("positionValue", 0);
					stockInfo.put("index", 0);
					stockInfo.put("maxCostValue", 0);
					stockInfo.put("costValue", 0);
					stockInfo.put("profit", 0);
					stockInfo.put("profitRadio", 0);
					stockInfo.put("active", false);
					if (transactions.length() > 0) {
						JSONObject transaction = transactions.getJSONObject(0);
						if (StringUtils.isEmpty(transaction.getString("finishTime"))) {
							stockInfo.put("amount", transaction.getInt("amount"));
							stockInfo.put("price", transaction.getFloat("price"));
							stockInfo.put("achievedProfit", transaction.getInt("achievedProfit"));
							stockInfo.put("positionValue", transaction.getDouble("positionValue"));
							stockInfo.put("index", transaction.getFloat("index"));
							stockInfo.put("maxCostValue", transaction.getDouble("maxCostValue"));
							stockInfo.put("costValue", transaction.getDouble("costValue"));
							stockInfo.put("active", true);
							activeStockList.add("sh" + stockInfo.getString("code"));
						}
					}
					//
				}
			}
			double totalCostValue = 0;
			double totalProfit = 0;
			double totalHistoryProfit = 0;
			activeStockList.add("sh000016");
			String[] results = quoteSina(activeStockList.toArray(new String[0]));
			float lastIndex = Float.parseFloat(StringUtils.splitPreserveAllTokens(results[results.length - 1], ",")[3]);
			for (int i = 0; i < stockArray.length(); i++) {
				JSONObject stockInfo = stockArray.getJSONObject(i);
				int amount = stockInfo.getInt("amount");
				double positionValue = stockInfo.getDouble("positionValue");
				float index = stockInfo.getFloat("index");
				if (stockInfo.getBoolean("active")) {
					float lastPrice = Float.parseFloat(StringUtils.splitPreserveAllTokens(results[i], ",")[3]);
					double positionProfit = 0;
					if (amount > 0) {
						positionProfit = amount * lastPrice - positionValue * lastIndex / index;
					}
					double profit = positionProfit + stockInfo.getDouble("achievedProfit");
					//
					stockInfo.put("lastPrice", lastPrice);
					stockInfo.put("lastIndex", lastIndex);
					stockInfo.put("profit", profit);
					stockInfo.put("profitRadio", profit / stockInfo.getDouble("maxCostValue"));
				} else {
					stockInfo.put("lastPrice", 0);
					stockInfo.put("lastIndex", 0);
				}

				//
				totalCostValue += stockInfo.getDouble("costValue");
				totalProfit += stockInfo.getDouble("profit");
				totalHistoryProfit += stockInfo.getDouble("historyProfit");

				//
				stockInfo.put("serial", i + 1);
				stockInfo.put("lastPrice", numberFormatter.format(stockInfo.getFloat("lastPrice"), 3));
				stockInfo.put("lastIndex", numberFormatter.format(stockInfo.getFloat("lastIndex"), 4));
				stockInfo.put("price", numberFormatter.format(stockInfo.getFloat("price"), 3));
				stockInfo.put("index", numberFormatter.format(index, 4));
				stockInfo.put("profit", numberFormatter.format(stockInfo.getDouble("profit"), 2));
				stockInfo.put("profitRadio",
						numberFormatter.formatPercent(new BigDecimal(stockInfo.getDouble("profitRadio")), 2));
				stockInfo.put("historyProfit", numberFormatter.format(stockInfo.getDouble("historyProfit"), 2));
				stockInfo.put("maxCostValue", numberFormatter.format(stockInfo.getDouble("maxCostValue"), 2));
				stockInfo.put("costValue", numberFormatter.format(stockInfo.getDouble("costValue"), 2));

			}
			//
			JSONObject totalObject = new JSONObject();
			totalObject.put("name", "合计：");
			totalObject.put("costValue", numberFormatter.format(totalCostValue, 2));
			totalObject.put("profit", numberFormatter.format(totalProfit, 2));
			totalObject.put("historyProfit", numberFormatter.format(totalHistoryProfit, 2));

			//
			JSONArray array = new JSONArray();
			array.put(totalObject);
			for (int i = 0; i < stockArray.length(); i++) {
				array.put(stockArray.get(i));
			}
			//
			output.put("stocks", array);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	// 给定买入卖出记录计算已实现盈亏、剩余持仓金额、数量、对应指数均值
	private void updateTransactionData(JSONObject transaction) {
		JSONArray orders = transaction.getJSONArray("orders");
		JSONArray positions = new JSONArray();
		double maxCostValue = 0;
		double costValue = 0;
		double achievedProfit = 0; // 已实现盈亏
		for (int i = 0; i < orders.length(); i++) {
			JSONObject order = orders.getJSONObject(i);
			if (order.getInt("type") == 0) {
				JSONObject position = new JSONObject();
				int amount = order.getInt("amount");
				float price = order.getFloat("price");
				position.put("amount", amount);
				position.put("price", price);
				position.put("index", order.get("index"));
				positions.put(position);
				costValue += amount * price;
			} else if (order.getInt("type") == 1) {
				int targetAmount = order.getInt("amount");
				float sellPrice = order.getFloat("price");
				float sellIndex = order.getFloat("index");
				costValue -= targetAmount * sellPrice;
				double sellProfit = 0;
				for (int j = 0; j < positions.length(); j++) {
					JSONObject position = positions.getJSONObject(j);
					int positionAmount = position.getInt("amount");
					if (positionAmount == 0) {
						continue;
					}
					float buyPrice = position.getFloat("price");
					float buyIndex = position.getFloat("index");
					int sellAmount = Math.min(positionAmount, targetAmount);
					sellProfit = sellProfit + sellAmount * (sellPrice - buyPrice * (sellIndex / buyIndex));
					//
					targetAmount = targetAmount - sellAmount;
					position.put("amount", positionAmount - sellAmount);
					if (targetAmount == 0) {
						break;
					}
				}
				achievedProfit += sellProfit;
			} else if (order.getInt("type") == 2) {
				double bonusValue = order.getDouble("value");
				int bonusAmount = order.getInt("amount");
				costValue -= bonusValue;
				maxCostValue -= bonusValue;
				processBonus(positions, bonusAmount, bonusValue);
			} else if (order.getInt("type") == 9) {
				// TODO：扣税，扣除盈利
			}
			// 计算最大持仓金额
			if (costValue > maxCostValue) {
				maxCostValue = costValue;
			}
		}
		//
		double positionValue = 0;
		double x = 0; // X为持仓金额除以当时指数的值的求和，总持仓金额除以X即平均指数
		int amount = 0;
		for (int i = 0; i < positions.length(); i++) {
			JSONObject position = positions.getJSONObject(i);
			int positionAmount = position.getInt("amount");
			float positionPrice = position.getFloat("price");
			float positionIndex = position.getFloat("index");
			amount += positionAmount;
			positionValue += positionPrice * positionAmount;
			x += (positionPrice * positionAmount / positionIndex);
		}
		transaction.put("amount", amount);

		//
		if (amount > 0) {
			transaction.put("price", costValue / amount);
			transaction.put("index", positionValue / x);
		} else {
			transaction.put("price", 0);
			transaction.put("index", 0);
		}
		transaction.put("positionValue", positionValue);
		transaction.put("achievedProfit", achievedProfit);
		transaction.put("costValue", costValue);
		transaction.put("maxCostValue", maxCostValue);
	}

	/**
	 * 处理分红
	 */
	private void processBonus(JSONArray positions, int bonusAmount, double bonusValue) {
		int totalAmount = 0;
		for (int i = 0; i < positions.length(); i++) {
			JSONObject position = positions.getJSONObject(i);
			totalAmount += position.getInt("amount");
		}
		for (int i = 0; i < positions.length(); i++) {
			JSONObject position = positions.getJSONObject(i);
			int positionAmount = position.getInt("amount");
			float price = position.getFloat("price");
			double positionValue = positionAmount * price;
			if (positionAmount == 0) {
				continue;
			}
			double radio = ((double) positionAmount) / ((double) totalAmount); // 持仓数量占总数量的比例，按比例分红
			// 持仓金额先减去分红现金
			positionValue = positionValue - radio * bonusValue;
			// 数量增加对应转增数量
			positionAmount = positionAmount + (int) (radio * bonusAmount);
			// 最后计算价格
			price = (float) positionValue / positionAmount;
			//
			position.put("amount", positionAmount);
			position.put("price", price);
		}
	}

	/**
	 * 查询行情
	 */
	private String[] quoteSina(String[] codes) throws Throwable {
		int limit = 50;
		int current = 0;
		List<String> allResultList = new ArrayList<String>();
		while (true) {
			if (current == codes.length) {
				break;
			}
			List<String> codeList = new ArrayList<String>();
			for (int i = 0; i < limit; i++) {
				codeList.add(codes[current++]);
				if (current == codes.length) {
					break;
				}
			}
			String[] results = StringUtils.split(quoteSina(StringUtils.join(codeList, ",")), ";");
			for (int i = 0; i < results.length; i++) {
				String result = results[i];
				allResultList.add(result.substring(result.indexOf("\"") + 1, result.length() - 1));
			}
		}
		return allResultList.toArray(new String[0]);
	}

	private String quoteSina(String list) throws Throwable {
		StringBuffer buffer = new StringBuffer();
		HttpURLConnection connection = (HttpURLConnection) new URL("https://hq.sinajs.cn/list=" + list)
				.openConnection();
		try {
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
			connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("Referer", "https://finance.sina.com.cn");
			connection.connect();
			if (connection.getResponseCode() == 200) {
				InputStream is = connection.getInputStream();
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "GBK"));
					String line = null;
					while ((line = reader.readLine()) != null) {
						buffer.append(line);
					}
				} finally {
					is.close();
				}
			}
		} finally {
			connection.disconnect();
		}
		return buffer.toString();
	}

}
