package com.crowd.service.stock;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.CrowdInitContext;
import com.crowd.service.base.CrowdMethod;
import com.crowd.service.base.CrowdService;

public class StockService implements CrowdService {

	@Override
	public String getName() {
		return "stock";
	}

	@Override
	public void init(CrowdInitContext context) throws Throwable {
	}

	@Override
	public void postInit(CrowdInitContext context) throws Throwable {
	}

	private JSONArray loadData(CrowdContext context) {
		try {
			return new JSONArray(context.load("pool.json"));
		} catch (Throwable t) {
			return new JSONArray();
		}
	}

	private void saveData(CrowdContext context, JSONArray array) throws Throwable {
		context.save("pool.json", array.toString(4));
	}

	@CrowdMethod
	public void add(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String code = input.getString("code");
		boolean isOk = false;
		if (code.length() == 6 && (code.startsWith("6") || code.startsWith("3") || code.startsWith("0"))) {
			code = code.trim();
			if (code.startsWith("6")) {
				code = "sh" + code;
			} else if (code.startsWith("0") || code.startsWith("3")) {
				code = "sz" + code;
			}
			JSONArray array = loadData(context);
			for (int i = 0; i < array.length(); i++) {
				if (code.equals(array.getJSONObject(i).getString("code"))) {
					throw new IllegalStateException("股票池中已经存在该股票");
				}
			}
			String[] info = quoteInfo(code);
			if (info.length > 10) {
				JSONArray newArray = new JSONArray();
				JSONObject o = new JSONObject();
				o.put("key", code);
				o.put("code", code);
				o.put("name", info[0]);
				o.put("buyDate", "");
				newArray.put(o);
				for (int i = 0; i < array.length(); i++) {
					newArray.put(array.getJSONObject(i));
				}
				context.save("pool.json", newArray.toString(4));
				isOk = true;
				output.put("stock", o);
				output.put("stocks", newArray);
			}
		}
		//
		if (!isOk) {
			throw new IllegalStateException("股票代码不正确");
		}
	}

	@CrowdMethod
	public void delete(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		JSONArray array = loadData(context);
		JSONArray newArray = new JSONArray();
		for (int i = 0; i < array.length(); i++) {
			JSONObject o = array.getJSONObject(i);
			if (!o.getString("code").equals(input.getString("code"))) {
				newArray.put(o);
			}
		}
		saveData(context, newArray);
		list(context, input, output);
	}

	@CrowdMethod
	public void update(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		JSONArray array = loadData(context);
		JSONObject targetObj = null;
		for (int i = 0; i < array.length(); i++) {
			JSONObject o = array.getJSONObject(i);
			if (o.getString("code").equals(input.getString("code"))) {
				targetObj = o;
				o.put("buyDate", input.getString("buyDate"));
			}
		}
		if (targetObj != null) {
			saveData(context, array);
			list(context, input, output);
			chartData(context, input, output);
			output.put("stock", targetObj);
		}
	}

	@CrowdMethod
	public void list(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		JSONArray array = loadData(context);
		output.put("stocks", array);
		if (array.length() > 0) {
			output.put("stock", array.getJSONObject(0));
		} else {
			JSONObject o = new JSONObject();
			o.put("code", "");
			o.put("buyDate", "");
			output.put("stock", o);
		}
	}

	@CrowdMethod
	public void chartData(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String code = input.getString("code");
		String buyDate = input.getString("buyDate");
		int days = input.getInt("days");
//		System.out.println(input);
		JSONArray amplitudeSeries = new JSONArray();
		JSONArray klineSeries = new JSONArray();
		output.put("amplitudeSeries", amplitudeSeries);
		output.put("klineSeries", klineSeries);
		output.put("buyDateIndex", -1);
		if (StringUtils.isEmpty(code)) {
			return;
		}
		String INDEX_CODE = "sh000016"; // 指数代码
		//
		JSONArray crowdData = quoteKLineData(INDEX_CODE, days);
		JSONArray targetData = quoteKLineData(code, days);
		if (targetData.length() < crowdData.length()) {
			crowdData = quoteKLineData(INDEX_CODE, targetData.length());
		}
		int length = targetData.length();

		BigDecimal crowdBasePrice = new BigDecimal(crowdData.getJSONArray(0).getString(2));
		BigDecimal targetBasePrice = new BigDecimal(targetData.getJSONArray(0).getString(2));
		if (StringUtils.isNotEmpty(buyDate)) {
			for (int i = 0; i < length; i++) {
				JSONArray crowdObj = crowdData.getJSONArray(i);
				JSONArray targetObj = targetData.getJSONArray(i);
				String day = crowdObj.getString(0);
				if (day.compareTo(buyDate) <= 0) {
					crowdBasePrice = new BigDecimal(crowdObj.getString(2));
					targetBasePrice = new BigDecimal(targetObj.getString(2));
					output.put("buyDateIndex", i);
//					break;
				}
			}
		}
		for (int i = 0; i < length; i++) {
			JSONArray crowdObj = crowdData.getJSONArray(i);
			JSONArray targetObj = targetData.getJSONArray(i);
			String day = crowdObj.getString(0);
			//
			JSONArray amplitudeArray = new JSONArray();
			BigDecimal crowdPrice = new BigDecimal(crowdObj.getString(2));
			BigDecimal targetPrice = new BigDecimal(targetObj.getString(2));
			BigDecimal crowdAmplitude = crowdPrice.subtract(crowdBasePrice).divide(crowdBasePrice, 4,
					RoundingMode.HALF_UP);
			BigDecimal targetAmplitude = targetPrice.subtract(targetBasePrice).divide(targetBasePrice, 4,
					RoundingMode.HALF_UP);
			amplitudeArray.put(day);
			amplitudeArray.put(crowdAmplitude);
			amplitudeArray.put(targetAmplitude);
			amplitudeArray.put(targetAmplitude.subtract(crowdAmplitude));
			amplitudeSeries.put(amplitudeArray);

			//
			BigDecimal openPrice = new BigDecimal(targetObj.getString(1));
			BigDecimal closePrice = new BigDecimal(targetObj.getString(2));
			BigDecimal highPrice = new BigDecimal(targetObj.getString(3));
			BigDecimal lowPrice = new BigDecimal(targetObj.getString(4));
			BigDecimal volume = new BigDecimal(targetObj.getString(5));
			JSONArray klineArray = new JSONArray();
			klineArray.put(day);
			klineArray.put(openPrice);
			klineArray.put(closePrice);
			klineArray.put(highPrice);
			klineArray.put(lowPrice);
			klineArray.put(volume);
			klineArray.put(closePrice.compareTo(openPrice) >= 0 ? 1 : 0);
			klineSeries.put(klineArray);
		}
//		System.out.println(output);
	}

	private final static Map<String, JSONArray> dataCache = new HashMap<String, JSONArray>();

	@CrowdMethod
	public void queryTestInfo(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		int pageNum = input.getInt("pageNum");
		JSONArray dataSeries = new JSONArray();
		JSONArray properties = new JSONArray();
		JSONArray orders = new JSONArray();
		int orderCount = 0;
		String startDay = "2020-01-01";
		int maxPositionDays = 200;
		float targetAmplitude = 0.1f;
		float stopAmplitude = 0.15f;
		String algorithm = "cci";
		int amplitudeMode = 0;
		try {
			JSONObject testResult = new JSONObject(context.load("testresult.json"));
			JSONObject baseInfo = testResult.getJSONObject("baseInfo");
			properties = convertTestProperties(baseInfo);
			dataSeries = testResult.getJSONArray("dataSeries");
			JSONArray historyOrders = testResult.getJSONArray("historyOrders");
			orders = convertTestOrders(historyOrders, pageNum);
			orderCount = historyOrders.length();
			startDay = baseInfo.getString("startDay");
			maxPositionDays = baseInfo.getInt("maxPositionDays");
			targetAmplitude = baseInfo.getFloat("targetAmplitude");
			stopAmplitude = baseInfo.getFloat("stopAmplitude");
			algorithm = baseInfo.getString("algorithm");
			amplitudeMode = baseInfo.getInt("amplitudeMode");
		} catch (Throwable t) {
		}
		output.put("properties", properties);
		output.put("dataSeries", dataSeries);
		output.put("orders", orders);
		output.put("orderCount", orderCount);
		output.put("startDay", startDay);
		output.put("maxPositionDays", maxPositionDays);
		output.put("targetAmplitude", targetAmplitude);
		output.put("stopAmplitude", stopAmplitude);
		output.put("algorithm", algorithm);
		output.put("amplitudeMode", amplitudeMode);
	}

	@CrowdMethod
	public void queryTestOrders(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		int pageNum = input.getInt("pageNum");
		JSONArray orders = new JSONArray();
		try {
			JSONObject testResult = new JSONObject(context.load("testresult.json"));
			orders = convertTestOrders(testResult.getJSONArray("historyOrders"), pageNum);
		} catch (Throwable t) {
		}
		output.put("orders", orders);
	}

	@CrowdMethod
	public void startTest(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String startDay = input.getString("startDay");
		int maxPositionDays = input.getInt("maxPositionDays");
		float targetAmplitude = input.getFloat("targetAmplitude");
		float stopAmplitude = input.getFloat("stopAmplitude");
		String algorithm = input.getString("algorithm");
		int amplitudeMode = input.getInt("amplitudeMode");
		Set<String> daySet = new HashSet<String>(); // 交易日集合（无序）
		List<String> dayList = new ArrayList<String>(); // 交易日列表
		List<String> codeList = new ArrayList<String>(); // 所有可用股票代码
		Map<String, Map<String, KData>> code2KDataMap = new HashMap<String, Map<String, KData>>(); // 按股票代码索引到一个Map，该Map通过日期索引到对应K线数据
		Map<String, List<KData>> code2KDataList = new HashMap<String, List<KData>>(); // 按股票代码索引到一个包含所有日期k线数据的列表
		String INDEX_CODE = "sh000016"; // 指数代码
		File dir = new File("E:\\kdata\\" + INDEX_CODE);
		// 数据初始准备
		for (File file : dir.listFiles()) {
			String code = file.getName().substring(0, file.getName().indexOf("."));
			if (!dataCache.containsKey(code)) {
				byte[] buffer = new byte[(int) file.length()];
				RandomAccessFile raf = new RandomAccessFile(file, "r");
				raf.read(buffer);
				raf.close();
				dataCache.put(code, new JSONArray(new String(buffer)));
			}
			JSONArray array = dataCache.get(code);
			String firstDay = array.getJSONArray(0).getString(0);
			if (firstDay.compareTo(startDay) > 0) {
				continue;
			}
			Map<String, KData> kDataMap = new HashMap<String, KData>();
			List<KData> kDataList = new ArrayList<KData>();
			for (int i = 0; i < array.length(); i++) {
				JSONArray dataArray = array.getJSONArray(i);
				KData kData = new KData();
				//
				kData.day = dataArray.getString(0);
				kData.openPrice = new BigDecimal(dataArray.getString(1));
				kData.closePrice = new BigDecimal(dataArray.getString(2));
				kData.highPrice = new BigDecimal(dataArray.getString(3));
				kData.lowPrice = new BigDecimal(dataArray.getString(4));
				kData.volume = new BigDecimal(dataArray.getString(5));
				//
				if (kDataList.size() > 0) {
					kData.prev = kDataList.get(kDataList.size() - 1);
				}
				kDataList.add(kData);
				kDataMap.put(kData.day, kData);
				if (kData.day.compareTo(startDay) >= 0) {
					daySet.add(kData.day);
				}
			}
			codeList.add(code);
			code2KDataMap.put(code, kDataMap);
			code2KDataList.put(code, kDataList);
		}
		dayList.addAll(daySet);
		Collections.sort(dayList);

		//
		Map<String, OrderInfo> activeOrders = new HashMap<String, OrderInfo>();
		JSONArray historyOrders = new JSONArray();
		List<DayReportInfo> dayReportInfos = new ArrayList<DayReportInfo>();
		//
		startDay = dayList.get(0);
		BigDecimal BROKER_RATE = new BigDecimal(0.0002); // 券商交易费率
		BigDecimal TAX_RATE = new BigDecimal(0.0005); // 印花税率
		BigDecimal INDEX_POINT_VALUE = new BigDecimal(300); // 指数单点价值
		BigDecimal SINGLE_STOCK_VALUE = new BigDecimal(15000); // 个股买入市值
		BigDecimal TARGET_AMPLITUDE = new BigDecimal(targetAmplitude);
		BigDecimal STOP_AMPLITUDE = new BigDecimal(stopAmplitude).negate();
		BigDecimal FUND_VALUE = new BigDecimal(1500000);
		BigDecimal balanceValue = BigDecimal.ZERO; // 资金余额初始为0，仅作为计算标准
		BigDecimal marketValue = BigDecimal.ZERO; // 上个交易最后计算的市值
		BigDecimal initIndexValue = code2KDataMap.get(INDEX_CODE).get(startDay).openPrice;
		int winCount = 0;
		int loseCount = 0;
		BigDecimal winValue = BigDecimal.ZERO;
		BigDecimal loseValue = BigDecimal.ZERO;
		int normalCloseCount = 0;
		int forceCloseCount = 0;
		BigDecimal brokerValue = BigDecimal.ZERO;
		BigDecimal taxValue = BigDecimal.ZERO;

		// 每天定时处理
		for (int i = 0; i < dayList.size(); i++) {
			String day = dayList.get(i);
			KData todayIndexKData = code2KDataMap.get(INDEX_CODE).get(day);
			int openCount = 0;

			// 早盘挂高价买入，只要不是直接涨停，就可以以当日开盘价成交买入
			if (i < dayList.size() - 1) {
				SelectAlgorithm selectAlgorithm = new RandomAlgorithm(codeList);
				if (algorithm.equals("trend")) {
					selectAlgorithm = new TrendAlgorithm(codeList);
				}
				while (true) {
					// 持仓市值达到目标（按照昨日指数收盘价核算目标市值）
					if (marketValue.compareTo(todayIndexKData.prev.closePrice.multiply(INDEX_POINT_VALUE)) >= 0) {
						break;
					}
					// 算法选出一个最优的股票，如果无法选出，则中止
					String code = selectAlgorithm.nextCode();
					if (code == null) {
						break;
					}

					//
					if (code.equals(INDEX_CODE)) {
						continue;
					}

					// 排除持仓的股票
					if (activeOrders.containsKey(code)) {
						continue;
					}
					// 该日期无交易（停牌）
					if (!code2KDataMap.get(code).containsKey(day)) {
						continue;
					}

					//
					KData todayKData = code2KDataMap.get(code).get(day);
					if (todayKData.prev == null) {
						continue;
					}

					// 昨日收盘价低于2元的忽略
					if (todayKData.prev.closePrice.compareTo(new BigDecimal(2)) < 0) {
						continue;
					}
					// 昨日收盘价高于100元的忽略
					if (todayKData.prev.closePrice.compareTo(new BigDecimal(100)) > 0) {
						continue;
					}

					//
					if (selectAlgorithm.ignore(todayKData.prev, todayIndexKData.prev)) {
						continue;
					}

					//
					OrderInfo orderInfo = new OrderInfo();
					orderInfo.code = code;
					orderInfo.openDay = day;
					orderInfo.openPrice = todayKData.openPrice; // XXX：买入成交价格一定为当日开盘价（委托时按高价委托，数量不大时没有影响，数量大可能会影响市场）
					orderInfo.lastPrice = todayKData.closePrice;
					orderInfo.volume = SINGLE_STOCK_VALUE
							.divide(orderInfo.openPrice.multiply(new BigDecimal(100)), 0, RoundingMode.HALF_DOWN)
							.multiply(new BigDecimal(100));
					orderInfo.openIndexValue = todayIndexKData.openPrice;
					if (orderInfo.volume.compareTo(BigDecimal.ZERO) > 0) {
						activeOrders.put(code, orderInfo);
						openCount++;
						BigDecimal value = orderInfo.openPrice.multiply(orderInfo.volume);
						balanceValue = balanceValue.subtract(value).subtract(value.multiply(BROKER_RATE));
						marketValue = marketValue.add(value);
						brokerValue = brokerValue.add(value.multiply(BROKER_RATE));
					}
				}

			}

			// 卖出股票
			BigDecimal openMarketValue = marketValue; // 开盘买入股票后的持仓市值
			marketValue = BigDecimal.ZERO; // 收盘重新计算市值
			Map<String, OrderInfo> newActiveOrders = new HashMap<String, OrderInfo>();
			for (OrderInfo orderInfo : activeOrders.values()) {
				boolean sellFlag = false;
				if (orderInfo.days > 0) {
					KData todayKData = code2KDataMap.get(orderInfo.code).get(day);
					// 该日期有交易（未停牌）
					if (todayKData != null) {
						orderInfo.lastPrice = todayKData.closePrice; // lastPrice主要用于记录最后一个收盘价，避免后面停牌无法计算市值
						BigDecimal indexAmplitude = BigDecimal.ZERO;
						if (amplitudeMode == 0) {
							indexAmplitude = todayIndexKData.openPrice.subtract(orderInfo.openIndexValue)
									.divide(orderInfo.openIndexValue, 4, RoundingMode.HALF_UP); // XXX：这里暂用今日指数开盘价核算，实际应该根据实时指数值动态计算，并不断调整止盈和止损价格
						}
						BigDecimal targetPrice = orderInfo.openPrice
								.add(orderInfo.openPrice.multiply(indexAmplitude.add(TARGET_AMPLITUDE)))
								.setScale(2, RoundingMode.UP);
						BigDecimal stopPrice = orderInfo.openPrice
								.add(orderInfo.openPrice.multiply(indexAmplitude.add(STOP_AMPLITUDE)))
								.setScale(2, RoundingMode.HALF_UP);
						// 任何一只持仓股票，在开盘前挂止盈单，盘中根据情况撤止盈单，挂止损单
						// 1、止盈单开盘按targetPrice挂单卖出，如果当日最高价可以达到该价格，则说明止盈单可以成交。如果开盘价就大于止盈价，则成交价按开盘价，否则后续必然按止盈价成交。
						// 2、止损单需要实时监测价格，当价格达到或低于止损价格时，按市价卖出；由于实际成交价格不能精确确定，目前做法是不设止损，通过最大持股天数在收盘时卖出控制损失。
						// 3、如果收盘前（2点55分）止盈单或止损单均未成交，并且该委托持仓天数超过最大持仓天数，则撤销挂单，按收盘价卖出，只要数量不大，对市场影响不大
						//
						if (todayKData.highPrice.compareTo(targetPrice) >= 0) {
							orderInfo.closePrice = todayKData.openPrice.max(targetPrice);
							sellFlag = true;
							normalCloseCount++;
						} else if (todayKData.lowPrice.compareTo(stopPrice) <= 0) {
							orderInfo.closePrice = todayKData.openPrice.min(stopPrice);
							sellFlag = true;
							orderInfo.forceFlag = true;
							forceCloseCount++;
						} else if (orderInfo.days >= maxPositionDays) {
							orderInfo.closePrice = todayKData.closePrice;
							sellFlag = true;
							orderInfo.forceFlag = true;
							forceCloseCount++;
						}
					}
				}

				// XXX：这里是最后一天测试，无论如何都处理成卖出，使用lastPrice，即使不准确也对整体影响不大
				if (!sellFlag && i == dayList.size() - 1) {
					orderInfo.closePrice = orderInfo.lastPrice;
					sellFlag = true;
					orderInfo.forceFlag = true;
					forceCloseCount++;
				}

				// 处理卖出委托
				if (sellFlag) {
					orderInfo.closeIndexValue = todayIndexKData.closePrice; // XXX：应该取委托实际成交时的指数价格，但目前影响不大
					orderInfo.closeDay = day;
					BigDecimal value = orderInfo.closePrice.multiply(orderInfo.volume);
					balanceValue = balanceValue.add(value).subtract(value.multiply(BROKER_RATE.add(TAX_RATE)));
					brokerValue = brokerValue.add(value.multiply(BROKER_RATE));
					taxValue = taxValue.add(value.multiply(TAX_RATE));

					// 统计累计盈亏
					BigDecimal profitValue = orderInfo.closePrice.multiply(orderInfo.volume)
							.multiply(BigDecimal.ONE.subtract(TAX_RATE).subtract(BROKER_RATE))
							.subtract(orderInfo.openPrice.multiply(orderInfo.volume)
									.multiply(BigDecimal.ONE.add(BROKER_RATE)));
					if (profitValue.compareTo(BigDecimal.ZERO) >= 0) {
						winValue = winValue.add(profitValue);
						winCount++;
					} else {
						loseValue = loseValue.add(profitValue.abs());
						loseCount++;
					}
					//
					orderInfo.profitValue = profitValue;
					historyOrders.put(orderInfo.toJSONArray());
				} else {
					newActiveOrders.put(orderInfo.code, orderInfo);
					marketValue = marketValue.add(orderInfo.volume.multiply(orderInfo.lastPrice));
				}
				//
				orderInfo.days = orderInfo.days + 1;
			}

			//
			activeOrders = newActiveOrders;

			//
			DayReportInfo dayReportInfo = new DayReportInfo();
			dayReportInfo.day = day;
			dayReportInfo.balanceValue = balanceValue;
			dayReportInfo.marketValue = marketValue;
			dayReportInfo.contractProfit = initIndexValue.subtract(todayIndexKData.closePrice)
					.multiply(INDEX_POINT_VALUE).setScale(2, RoundingMode.HALF_UP);
			dayReportInfo.profitValue0 = balanceValue.add(marketValue).add(dayReportInfo.contractProfit).setScale(2,
					RoundingMode.HALF_UP);
			dayReportInfo.profitValue1 = balanceValue.add(marketValue).setScale(2, RoundingMode.HALF_UP);
			dayReportInfo.openCount = openCount;
			dayReportInfo.positionRadio = openMarketValue
					.divide(todayIndexKData.openPrice.multiply(INDEX_POINT_VALUE), 4, RoundingMode.HALF_UP)
					.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP); // XXX：这里的openMarketValue是按照指数昨日收盘价核算买入的，所以如果今日开盘指数波动较大，整个比值会在100%上下明显波动
			dayReportInfos.add(dayReportInfo);
		}
		//
		BigDecimal profit0MaxValue = BigDecimal.ZERO; // 总盈亏最高点
		BigDecimal profit1MaxValue = BigDecimal.ZERO; // 股票盈亏最高点
		BigDecimal profit0MaxWithdrawalValue = BigDecimal.ZERO; // 总盈亏最大回撤
		BigDecimal profit1MaxWithdrawalValue = BigDecimal.ZERO; // 股票盈亏最大回撤
		JSONArray dataSeries = new JSONArray();
		output.put("dataSeries", dataSeries);
		for (int i = 0; i < dayReportInfos.size(); i++) {
			DayReportInfo dayReportInfo = dayReportInfos.get(i);
			dataSeries.put(dayReportInfo.toJSONArray());
			//
			profit0MaxValue = profit0MaxValue.max(dayReportInfo.profitValue0);
			profit1MaxValue = profit1MaxValue.max(dayReportInfo.profitValue1);
			profit0MaxWithdrawalValue = profit0MaxWithdrawalValue
					.max(profit0MaxValue.subtract(dayReportInfo.profitValue0));
			profit1MaxWithdrawalValue = profit1MaxWithdrawalValue
					.max(profit1MaxValue.subtract(dayReportInfo.profitValue1));
		}

		// 计算平均持股天数
		int averagePositionDays = 0;
		for (int i = 0; i < historyOrders.length(); i++) {
			averagePositionDays = averagePositionDays + historyOrders.getJSONArray(i).getInt(7);
		}
		if (historyOrders.length() == 0) {
			averagePositionDays = Integer.MAX_VALUE;
		} else {
			averagePositionDays = averagePositionDays / historyOrders.length();
		}

		//
		DayReportInfo dayReportInfo = dayReportInfos.get(dayReportInfos.size() - 1);
		BigDecimal years = new BigDecimal(dayList.size()).divide(new BigDecimal(244), 3, RoundingMode.HALF_UP)
				.setScale(2, RoundingMode.HALF_UP);

		JSONObject testResult = new JSONObject();
		JSONObject baseInfo = new JSONObject();
		baseInfo.put("startDay", startDay);
		baseInfo.put("algorithm", algorithm);
		baseInfo.put("amplitudeMode", amplitudeMode);
		baseInfo.put("maxPositionDays", maxPositionDays);
		baseInfo.put("targetAmplitude", targetAmplitude);
		baseInfo.put("stopAmplitude", stopAmplitude);
		baseInfo.put("historyOrderCount", historyOrders.length());
		baseInfo.put("activeOrderCount", activeOrders.size()); // 可能停牌无法卖出
		baseInfo.put("normalCloseCount", normalCloseCount); // 主动平仓数量
		baseInfo.put("forceCloseCount", forceCloseCount); // 强平数量
		baseInfo.put("profitValue0", dayReportInfo.profitValue0);
		baseInfo.put("profitValue1", dayReportInfo.profitValue1);
		baseInfo.put("winValue", winValue.setScale(2, RoundingMode.HALF_UP));
		baseInfo.put("loseValue", loseValue.setScale(2, RoundingMode.HALF_UP));
		baseInfo.put("winCount", winCount);
		baseInfo.put("loseCount", loseCount);
		baseInfo.put("brokerValue", brokerValue.setScale(2, RoundingMode.HALF_UP));
		baseInfo.put("taxValue", taxValue.setScale(2, RoundingMode.HALF_UP));
		baseInfo.put("maxWithdrawalRadio1", profit0MaxWithdrawalValue.divide(FUND_VALUE, 4, RoundingMode.HALF_UP));
		baseInfo.put("maxWithdrawalRadio2", profit1MaxWithdrawalValue.divide(FUND_VALUE, 4, RoundingMode.HALF_UP));
		baseInfo.put("averagePositionDays", averagePositionDays);
		baseInfo.put("years", years);
		baseInfo.put("profitYearRadio0",
				dayReportInfo.profitValue0.divide(years.multiply(FUND_VALUE), 4, RoundingMode.HALF_UP));
		baseInfo.put("profitYearRadio1",
				dayReportInfo.profitValue1.divide(years.multiply(FUND_VALUE), 4, RoundingMode.HALF_UP));

		//
		testResult.put("baseInfo", baseInfo);
		testResult.put("dataSeries", dataSeries);
		testResult.put("historyOrders", historyOrders);
		context.save("testresult.json", testResult.toString(4));

		//
		output.put("properties", convertTestProperties(baseInfo));
		output.put("orders", convertTestOrders(historyOrders, 1));
		output.put("orderCount", historyOrders.length());
	}

	private JSONArray convertTestOrders(JSONArray historyOrders, int pageNum) {
		int pageSize = 20;
		JSONArray orders = new JSONArray();
		if (pageSize * (pageNum - 1) < historyOrders.length()) {
			for (int i = 0; i < pageSize; i++) {
				int index = pageSize * (pageNum - 1) + i;
				if (index < historyOrders.length()) {
					JSONArray arr = historyOrders.getJSONArray(index);
					JSONObject o = new JSONObject();
					o.put("code", arr.get(0));
					o.put("openDay", arr.get(1));
					o.put("closeDay", arr.get(2));
					o.put("openPrice", arr.get(3));
					o.put("closePrice", arr.get(4));
					o.put("volume", arr.get(5));
					o.put("profitRadio",
							new BigDecimal(arr.getDouble(6))
									.divide(new BigDecimal(arr.getDouble(3)).multiply(new BigDecimal(arr.getDouble(5))),
											4, RoundingMode.HALF_UP)
									.multiply(new BigDecimal(100)).doubleValue() + "%");
					o.put("days", arr.get(7));
					o.put("forceClose", arr.optBoolean(8) ? "是" : "");
					orders.put(o);
				}
			}
		}
		return orders;
	}

	private JSONArray convertTestProperties(JSONObject baseInfo) {
		JSONArray properties = new JSONArray();
		properties.put(new JSONObject("{\"name\":\"测试数据范围\",\"value\":\"" + baseInfo.opt("startDay") + " - 2024-03-29（"
				+ baseInfo.opt("years") + "年）\"}"));
		properties.put(new JSONObject("{\"name\":\"止盈\",\"value\":\""
				+ new BigDecimal(baseInfo.optFloat("targetAmplitude") * 100).setScale(2, RoundingMode.HALF_UP)
				+ "%\"}"));
		properties.put(new JSONObject("{\"name\":\"止损\",\"value\":\""
				+ new BigDecimal(baseInfo.optFloat("stopAmplitude") * 100).setScale(2, RoundingMode.HALF_UP) + "%\"}"));
		properties.put(new JSONObject(
				"{\"name\":\"波动模式\",\"value\":\"" + (baseInfo.optInt("amplitudeMode") == 0 ? "相对" : "绝对") + "\"}"));
		properties.put(new JSONObject("{\"name\":\"最大持股天数\",\"value\":\"" + baseInfo.opt("maxPositionDays") + "\"}"));
		properties.put(new JSONObject("{\"name\":\"选股算法\",\"value\":\"" + baseInfo.opt("algorithm") + "\"}"));
		properties.put(new JSONObject("{\"name\":\"综合盈亏\",\"value\":\"" + baseInfo.opt("profitValue0") + "\"}"));
		properties.put(new JSONObject("{\"name\":\"年化收益率\",\"value\":\""
				+ new BigDecimal(baseInfo.optDouble("profitYearRadio0") * 100).setScale(2, RoundingMode.HALF_UP)
				+ "%\"}"));
		properties.put(new JSONObject("{\"name\":\"最大回撤比例\",\"value\":\""
				+ new BigDecimal(baseInfo.optDouble("maxWithdrawalRadio1") * 100).setScale(2, RoundingMode.HALF_UP)
				+ "%\"}"));
		properties.put(new JSONObject("{\"name\":\"股票盈亏\",\"value\":\"" + baseInfo.opt("profitValue1") + "\"}"));
		properties.put(new JSONObject("{\"name\":\"年化收益率\",\"value\":\""
				+ new BigDecimal(baseInfo.optDouble("profitYearRadio1") * 100).setScale(2, RoundingMode.HALF_UP)
				+ "%\"}"));
		properties.put(new JSONObject("{\"name\":\"最大回撤比例\",\"value\":\""
				+ new BigDecimal(baseInfo.optDouble("maxWithdrawalRadio2") * 100).setScale(2, RoundingMode.HALF_UP)
				+ "%\"}"));
		properties.put(new JSONObject("{\"name\":\"总交易数量\",\"value\":\"" + baseInfo.opt("historyOrderCount") + "\"}"));
		properties.put(new JSONObject("{\"name\":\"主动平仓数量\",\"value\":\"" + baseInfo.opt("normalCloseCount") + "\"}"));
		properties.put(new JSONObject("{\"name\":\"强行平仓数量\",\"value\":\"" + baseInfo.opt("forceCloseCount") + "\"}"));
		properties.put(new JSONObject("{\"name\":\"盈利数量\",\"value\":\"" + baseInfo.opt("winCount") + "\"}"));
		properties.put(new JSONObject("{\"name\":\"亏损数量\",\"value\":\"" + baseInfo.opt("loseCount") + "\"}"));
		properties.put(new JSONObject("{\"name\":\"平仓盈利\",\"value\":\"" + baseInfo.opt("winValue") + "\"}"));
		properties.put(new JSONObject("{\"name\":\"平仓亏损\",\"value\":\"" + baseInfo.opt("loseValue") + "\"}"));
		properties
				.put(new JSONObject("{\"name\":\"平均持股天数\",\"value\":\"" + baseInfo.opt("averagePositionDays") + "\"}"));
		properties.put(new JSONObject("{\"name\":\"券商手续费\",\"value\":\"" + baseInfo.opt("brokerValue") + "\"}"));
		properties.put(new JSONObject("{\"name\":\"印花税\",\"value\":\"" + baseInfo.opt("taxValue") + "\"}"));
		return properties;
	}

	private String[] quoteInfo(String code) throws Throwable {
		StringBuffer buffer = new StringBuffer();
		HttpURLConnection connection = (HttpURLConnection) new URL("https://hq.sinajs.cn/list=" + code)
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
		String str = buffer.toString();
		str = str.substring(str.indexOf("\"") + 1, str.length() - 2);
		return StringUtils.split(str, ",");
	}

	private final static JSONArray quoteKLineData(String code, int periodLen) throws Throwable {
		return Tools.quoteKLineData(code, "", periodLen);
	}

}

class KData {
	String day;
	BigDecimal openPrice;
	BigDecimal closePrice;
	BigDecimal highPrice;
	BigDecimal lowPrice;
	BigDecimal volume;
//	BigDecimal avgPrice; // (closePrice+highPrice+lowPrice)/ 3
//	BigDecimal maValue;
//	BigDecimal mdValue;
//	BigDecimal cciValue;
//	BigDecimal maSumValue = BigDecimal.ZERO;
//	BigDecimal mdSumValue = BigDecimal.ZERO;
	//
	KData prev;
}

class OrderInfo {
	String code;
	String openDay;
	String closeDay;
	BigDecimal openPrice;
	BigDecimal closePrice;
	BigDecimal lastPrice; // 最新价
	BigDecimal volume;
	BigDecimal openIndexValue;
	BigDecimal closeIndexValue;
	BigDecimal profitValue;
	int days;
	boolean forceFlag;

	JSONArray toJSONArray() {
		JSONArray o = new JSONArray();
		o.put(code);
		o.put(openDay);
		o.put(closeDay);
		o.put(openPrice);
		o.put(closePrice);
		o.put(volume);
		o.put(profitValue);
		o.put(days);
		o.put(forceFlag);
		return o;
	}
}

class DayReportInfo {
	String day;
	BigDecimal balanceValue; // 资金余额
	BigDecimal marketValue; // 股票市值
	BigDecimal contractProfit; // 合约盈亏
	BigDecimal profitValue0;
	BigDecimal profitValue1;
	int openCount; // 开仓数量
	BigDecimal positionRadio; // 持仓占目标比例

	JSONArray toJSONArray() {
		JSONArray dataArray = new JSONArray();
		dataArray.put(day);
		dataArray.put(profitValue0);
		dataArray.put(profitValue1);
		dataArray.put(contractProfit);
		dataArray.put(openCount);
		dataArray.put(positionRadio);
		return dataArray;
	}
}

//class StockZBValue {
//	String code;
//	double cci;
//}