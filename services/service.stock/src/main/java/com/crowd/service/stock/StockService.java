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
import java.util.Random;
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
		//
		JSONArray crowdData = quoteKLineData("sh000852", days);
		JSONArray targetData = quoteKLineData(code, days);
		if (targetData.length() < crowdData.length()) {
			crowdData = quoteKLineData("sh000852", targetData.length());
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
	public void test0(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String startDay = input.getString("startDay");
		int maxPositionDays = input.getInt("maxPositionDays");
		float targetAmplitude = input.getFloat("targetAmplitude");
		Set<String> daySet = new HashSet<String>(); // 交易日集合（无序）
		List<String> dayList = new ArrayList<String>(); // 交易日列表
		List<String> codeList = new ArrayList<String>(); // 所有可用股票代码
		Map<String, Map<String, KData>> code2KDataMap = new HashMap<String, Map<String, KData>>(); // 按股票代码索引到一个Map，该Map通过日期索引到对应K线数据
		Map<String, List<KData>> code2KDataList = new HashMap<String, List<KData>>(); // 按股票代码索引到一个包含所有日期k线数据的列表
		File dir = new File("F:\\kdata");
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
				kData.day = dataArray.getString(0);
				kData.closePrice = new BigDecimal(dataArray.getString(2));
				kData.highPrice = new BigDecimal(dataArray.getString(3));
				kData.volume = new BigDecimal(dataArray.getString(5));
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
		List<OrderInfo> historyOrders = new ArrayList<OrderInfo>();
		List<AssetInfo> assetInfos = new ArrayList<AssetInfo>();
		//
		startDay = dayList.get(0);
		String INDEX_CODE = "sh000852"; // 指数代码
		BigDecimal INDEX_POINT_VALUE = new BigDecimal(200); // 指数单点价值
		BigDecimal SINGLE_STOCK_VALUE = new BigDecimal(30000); // 个股买入市值
		BigDecimal TARGET_AMPLITUDE = new BigDecimal(targetAmplitude);
		BigDecimal fundValue = new BigDecimal(2000000);
		BigDecimal balanceValue = new BigDecimal(1500000);
		BigDecimal contractBaseValue = new BigDecimal(500000);
		BigDecimal initIndexValue = code2KDataMap.get(INDEX_CODE).get(startDay).closePrice;
		for (int i = 0; i < dayList.size(); i++) {
			//
			String day = dayList.get(i);
			BigDecimal indexValue = code2KDataMap.get(INDEX_CODE).get(day).closePrice;
			// 卖出股票
			BigDecimal marketValue = BigDecimal.ZERO;
			Map<String, OrderInfo> newActiveOrders = new HashMap<String, OrderInfo>();
			for (OrderInfo orderInfo : activeOrders.values()) {
				orderInfo.days = orderInfo.days + 1;
				KData kData = code2KDataMap.get(orderInfo.code).get(day);
				// 该日期有交易（未停牌）
				if (kData != null) {
					BigDecimal indexAmplitude = indexValue.subtract(orderInfo.openIndexValue)
							.divide(orderInfo.openIndexValue, 4, RoundingMode.HALF_UP);
					BigDecimal stockAmplitude = indexAmplitude.add(TARGET_AMPLITUDE);
					BigDecimal targetPrice = orderInfo.openPrice.add(orderInfo.openPrice.multiply(stockAmplitude))
							.setScale(2, RoundingMode.HALF_UP);
//					BigDecimal price = kData.closePrice.max(kData.highPrice);
					BigDecimal price = kData.closePrice; // XXX：此处可以优化为盘中满足平仓条件的价格
//					BigDecimal stockAmplitude = price.subtract(orderInfo.openPrice).divide(orderInfo.openPrice, 4,
//							RoundingMode.HALF_UP);
					if (kData.highPrice.compareTo(targetPrice) >= 0 || orderInfo.days > maxPositionDays) {
						orderInfo.closeIndexValue = indexValue;
						orderInfo.closeDay = day;
						orderInfo.closePrice = price;
						historyOrders.add(orderInfo);
						BigDecimal value = price.multiply(orderInfo.volume);
						value = value.subtract(value.multiply(new BigDecimal("0.0012")));
						balanceValue = balanceValue.add(value);
						continue;
					}
				}
				newActiveOrders.put(orderInfo.code, orderInfo);
				marketValue = marketValue.add(orderInfo.volume.multiply(orderInfo.openPrice));
			}
			activeOrders = newActiveOrders;

			// 买入股票
			while (true) {
				// 持仓市值达到目标
				if (marketValue.compareTo(indexValue.multiply(INDEX_POINT_VALUE)) >= 0) {
					break;
				}
				//
				int index = new Random().nextInt(codeList.size()); // XXX：随机买入
				String code = codeList.get(index);
				// XXX:注意排除今日或近期卖出的股票

				// 排除持仓的股票
				if (activeOrders.containsKey(code)) {
					continue;
				}
				// 该日期无交易（停牌）
				if (!code2KDataMap.get(code).containsKey(day)) {
					continue;
				}
				//
				OrderInfo orderInfo = new OrderInfo();
				orderInfo.code = code;
				orderInfo.openDay = day;
				orderInfo.openPrice = code2KDataMap.get(code).get(day).closePrice;
				orderInfo.volume = SINGLE_STOCK_VALUE
						.divide(orderInfo.openPrice.multiply(new BigDecimal(100)), 0, RoundingMode.HALF_DOWN)
						.multiply(new BigDecimal(100));
				orderInfo.openIndexValue = indexValue;
				activeOrders.put(code, orderInfo);
				// 为简化计算，实际余额可能小于0
				BigDecimal value = orderInfo.openPrice.multiply(orderInfo.volume);
				value = value.subtract(value.multiply(new BigDecimal("0.0002")));
				balanceValue = balanceValue.subtract(value);
				marketValue = marketValue.add(value);
			}
			//
			AssetInfo assetInfo = new AssetInfo();
			assetInfo.day = day;
			assetInfo.balanceValue = balanceValue;
			assetInfo.marketValue = marketValue;
			assetInfo.contractValue = contractBaseValue
					.add(initIndexValue.subtract(indexValue).multiply(INDEX_POINT_VALUE));
			assetInfos.add(assetInfo);
		}
		//
		JSONArray dataSeries = new JSONArray();
		output.put("dataSeries", dataSeries);
		for (int i = 0; i < assetInfos.size(); i++) {
			AssetInfo assetInfo = assetInfos.get(i);
			JSONArray dataArray = new JSONArray();
			dataArray.put(assetInfo.day);
			dataArray.put(assetInfo.balanceValue.add(assetInfo.marketValue).add(assetInfo.contractValue)
					.subtract(fundValue).setScale(2, RoundingMode.HALF_UP));
			dataArray.put(assetInfo.marketValue.setScale(2, RoundingMode.HALF_UP));
			dataArray.put(assetInfo.balanceValue.setScale(2, RoundingMode.HALF_UP));
			dataArray.put(assetInfo.contractValue.subtract(contractBaseValue).setScale(2, RoundingMode.HALF_UP));
			dataSeries.put(dataArray);
		}
		//
//		for (OrderInfo orderInfo : activeOrders.values()) {
//			System.out.println(
//					orderInfo.code + ":" + orderInfo.openDay + "," + orderInfo.days + "," + orderInfo.openPrice);
//		}
//		System.out.println("当前交易数量：" + activeOrders.size());
//		System.out.println("历史交易数量：" + historyOrders.size());
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
		return quoteKLineData(code, "", periodLen);
	}

	private final static JSONArray quoteKLineData(String code, String startDay, String endDay) throws Throwable {
		JSONArray resultArray = new JSONArray();
		List<JSONArray> arrayList = new ArrayList<JSONArray>();
		while (true) {
			JSONArray array = quoteKLineData(code, endDay, 320);
			Thread.sleep(1000);
			arrayList.add(array);
			String firstDay = array.getJSONArray(0).getString(0);
			if (firstDay.compareTo(startDay) <= 0 || firstDay.equals(endDay)) {
				break;
			}
			endDay = firstDay;
		}
		for (int i = arrayList.size() - 1; i >= 0; i--) {
			JSONArray array = arrayList.get(i);
			for (int j = (i == arrayList.size() - 1 ? 0 : 1); j < array.length(); j++) {
				String day = array.getJSONArray(j).getString(0);
				if (day.compareTo(startDay) < 0) {
					continue;
				}
				resultArray.put(array.get(j));
			}
		}
		return resultArray;
	}

	private final static JSONArray quoteKLineData(String code, String endDay, int periodLen) throws Throwable {
		StringBuffer buffer = new StringBuffer();
		HttpURLConnection connection = (HttpURLConnection) new URL(
				"https://proxy.finance.qq.com/ifzqgtimg/appstock/app/newfqkline/get?_var=kline_dayqfq&param=" + code
						+ ",day,," + endDay + "," + periodLen + ",qfq").openConnection();
		try {
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
			connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("Referer", "https://gu.qq.com/");
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
		str = str.substring(str.indexOf("{"));
		JSONObject o = new JSONObject(str).getJSONObject("data").getJSONObject(code);
		return o.has("qfqday") ? o.getJSONArray("qfqday") : o.getJSONArray("day");
	}

	public final static void main(String[] args) throws Throwable {
//		printAllStockByCategory(new String[][] { { "hs300", "" } });
//		downloadKlineData();
//		test();
	}

	private final static void downloadKlineData() throws Throwable {
//		String zz500 = "sh600008,sh600021,sh600022,sh600027,sh600032,sh600038,sh600056,sh600060,sh600062,sh600066,sh600079,sh600095,sh600096,sh600109,sh600118,sh600126,sh600131,sh600141,sh600143,sh600153,sh600155,sh600157,sh600160,sh600161,sh600166,sh600167,sh600170,sh600171,sh600177,sh600195,sh600208,sh600258,sh600259,sh600271,sh600282,sh600297,sh600298,sh600299,sh600315,sh600316,sh600325,sh600329,sh600339,sh600348,sh600350,sh600352,sh600369,sh600372,sh600373,sh600377,sh600378,sh600380,sh600390,sh600392,sh600398,sh600399,sh600409,sh600415,sh600416,sh600418,sh600435,sh600481,sh600482,sh600486,sh600487,sh600489,sh600497,sh600498,sh600499,sh600500,sh600507,sh600511,sh600516,sh600517,sh600521,sh600528,sh600529,sh600535,sh600536,sh600546,sh600549,sh600556,sh600563,sh600566,sh600580,sh600582,sh600597,sh600598,sh600623,sh600637,sh600642,sh600655,sh600663,sh600667,sh600673,sh600699,sh600704,sh600705,sh600707,sh600718,sh600737,sh600739,sh600755,sh600764,sh600765,sh600782,sh600801,sh600808,sh600820,sh600827,sh600839,sh600848,sh600859,sh600862,sh600863,sh600867,sh600871,sh600873,sh600879,sh600885,sh600895,sh600901,sh600906,sh600909,sh600927,sh600928,sh600956,sh600959,sh600967,sh600968,sh600970,sh600985,sh600988,sh600995,sh600998,sh601000,sh601005,sh601016,sh601058,sh601077,sh601098,sh601106,sh601108,sh601118,sh601128,sh601136,sh601139,sh601156,sh601158,sh601162,sh601168,sh601179,sh601187,sh601198,sh601228,sh601231,sh601233,sh601298,sh601456,sh601555,sh601568,sh601577,sh601598,sh601608,sh601611,sh601636,sh601665,sh601666,sh601696,sh601717,sh601718,sh601778,sh601828,sh601866,sh601869,sh601880,sh601928,sh601958,sh601966,sh601969,sh601990,sh601991,sh601992,sh601997,sh603000,sh603026,sh603056,sh603077,sh603127,sh603156,sh603160,sh603218,sh603225,sh603228,sh603233,sh603267,sh603317,sh603338,sh603355,sh603379,sh603444,sh603456,sh603517,sh603529,sh603568,sh603589,sh603596,sh603638,sh603650,sh603658,sh603688,sh603707,sh603712,sh603737,sh603786,sh603816,sh603826,sh603858,sh603866,sh603868,sh603882,sh603883,sh603885,sh603893,sh603927,sh603939,sh605358,sh688002,sh688006,sh688009,sh688029,sh688052,sh688072,sh688082,sh688099,sh688105,sh688107,sh688116,sh688169,sh688185,sh688188,sh688200,sh688208,sh688220,sh688234,sh688248,sh688256,sh688276,sh688281,sh688289,sh688295,sh688301,sh688385,sh688390,sh688516,sh688520,sh688521,sh688536,sh688538,sh688567,sh688690,sh688772,sh688777,sh688778,sh688779,sh688819,sh689009,sz000009,sz000012,sz000021,sz000027,sz000031,sz000039,sz000050,sz000060,sz000066,sz000089,sz000155,sz000156,sz000400,sz000401,sz000402,sz000415,sz000423,sz000513,sz000519,sz000537,sz000547,sz000553,sz000559,sz000563,sz000581,sz000591,sz000598,sz000623,sz000629,sz000630,sz000636,sz000683,sz000703,sz000709,sz000728,sz000729,sz000738,sz000739,sz000750,sz000778,sz000783,sz000785,sz000807,sz000825,sz000830,sz000831,sz000869,sz000878,sz000883,sz000887,sz000893,sz000898,sz000930,sz000932,sz000933,sz000937,sz000958,sz000959,sz000960,sz000967,sz000970,sz000975,sz000987,sz000988,sz000997,sz000998,sz001203,sz001227,sz001872,sz001914,sz002008,sz002010,sz002019,sz002025,sz002028,sz002030,sz002032,sz002056,sz002065,sz002078,sz002080,sz002081,sz002092,sz002110,sz002128,sz002131,sz002138,sz002152,sz002153,sz002155,sz002156,sz002183,sz002185,sz002192,sz002195,sz002203,sz002221,sz002223,sz002240,sz002244,sz002249,sz002250,sz002266,sz002268,sz002273,sz002281,sz002294,sz002299,sz002326,sz002340,sz002353,sz002368,sz002372,sz002373,sz002384,sz002385,sz002399,sz002407,sz002408,sz002409,sz002422,sz002423,sz002429,sz002430,sz002432,sz002439,sz002444,sz002463,sz002465,sz002468,sz002487,sz002497,sz002500,sz002505,sz002506,sz002507,sz002508,sz002511,sz002518,sz002531,sz002532,sz002557,sz002563,sz002568,sz002572,sz002595,sz002600,sz002608,sz002624,sz002625,sz002653,sz002670,sz002673,sz002683,sz002690,sz002705,sz002738,sz002739,sz002745,sz002761,sz002791,sz002797,sz002831,sz002850,sz002867,sz002901,sz002925,sz002926,sz002936,sz002939,sz002945,sz002958,sz002966,sz002985,sz003035,sz300001,sz300003,sz300009,sz300012,sz300017,sz300024,sz300026,sz300037,sz300058,sz300070,sz300073,sz300088,sz300115,sz300118,sz300136,sz300144,sz300146,sz300182,sz300212,sz300244,sz300251,sz300253,sz300257,sz300285,sz300296,sz300308,sz300357,sz300363,sz300373,sz300383,sz300390,sz300395,sz300418,sz300438,sz300442,sz300474,sz300482,sz300487,sz300529,sz300558,sz300568,sz300595,sz300604,sz300618,sz300676,sz300677,sz300682,sz300699,sz300724,sz300741,sz300748,sz300776,sz300832,sz300850,sz300861,sz300866,sz300888,sz301029";
//		String hs300 = "sh600000,sh600009,sh600010,sh600011,sh600015,sh600016,sh600018,sh600019,sh600025,sh600028,sh600029,sh600030,sh600031,sh600036,sh600039,sh600048,sh600050,sh600061,sh600085,sh600089,sh600104,sh600111,sh600115,sh600132,sh600150,sh600176,sh600183,sh600188,sh600196,sh600219,sh600233,sh600276,sh600309,sh600332,sh600346,sh600362,sh600383,sh600406,sh600426,sh600436,sh600438,sh600460,sh600519,sh600547,sh600570,sh600584,sh600585,sh600588,sh600600,sh600606,sh600660,sh600674,sh600690,sh600732,sh600741,sh600745,sh600754,sh600760,sh600763,sh600795,sh600803,sh600809,sh600837,sh600845,sh600875,sh600884,sh600886,sh600887,sh600893,sh600900,sh600905,sh600918,sh600919,sh600926,sh600941,sh600958,sh600989,sh600999,sh601006,sh601009,sh601012,sh601021,sh601066,sh601088,sh601100,sh601111,sh601117,sh601138,sh601155,sh601166,sh601169,sh601186,sh601211,sh601216,sh601225,sh601229,sh601236,sh601238,sh601288,sh601318,sh601319,sh601328,sh601336,sh601360,sh601377,sh601390,sh601398,sh601600,sh601601,sh601607,sh601615,sh601618,sh601628,sh601633,sh601658,sh601668,sh601669,sh601688,sh601689,sh601698,sh601699,sh601728,sh601766,sh601788,sh601799,sh601800,sh601808,sh601816,sh601818,sh601838,sh601857,sh601865,sh601868,sh601872,sh601877,sh601878,sh601881,sh601888,sh601898,sh601899,sh601901,sh601919,sh601939,sh601985,sh601988,sh601989,sh601995,sh601998,sh603019,sh603185,sh603195,sh603259,sh603260,sh603288,sh603290,sh603369,sh603392,sh603486,sh603501,sh603659,sh603799,sh603806,sh603833,sh603899,sh603986,sh603993,sh605117,sh605499,sh688005,sh688008,sh688012,sh688036,sh688065,sh688111,sh688126,sh688187,sh688223,sh688303,sh688363,sh688396,sh688561,sh688599,sh688981,sz000001,sz000002,sz000063,sz000069,sz000100,sz000157,sz000166,sz000301,sz000333,sz000338,sz000408,sz000425,sz000538,sz000568,sz000596,sz000617,sz000625,sz000651,sz000661,sz000708,sz000723,sz000725,sz000733,sz000768,sz000776,sz000786,sz000792,sz000800,sz000858,sz000876,sz000877,sz000895,sz000938,sz000963,sz000977,sz000983,sz001289,sz001979,sz002001,sz002007,sz002027,sz002049,sz002050,sz002064,sz002074,sz002120,sz002129,sz002142,sz002179,sz002180,sz002202,sz002230,sz002236,sz002241,sz002252,sz002271,sz002304,sz002311,sz002352,sz002371,sz002410,sz002414,sz002415,sz002459,sz002460,sz002466,sz002475,sz002493,sz002555,sz002594,sz002601,sz002648,sz002709,sz002714,sz002736,sz002756,sz002812,sz002821,sz002841,sz002916,sz002920,sz002938,sz003816,sz300014,sz300015,sz300033,sz300059,sz300122,sz300124,sz300142,sz300207,sz300223,sz300274,sz300316,sz300347,sz300408,sz300413,sz300433,sz300450,sz300454,sz300496,sz300498,sz300601,sz300628,sz300661,sz300750,sz300751,sz300759,sz300760,sz300763,sz300769,sz300782,sz300896,sz300919,sz300957,sz300979,sz300999";
		String zs = "sh000852";
		String[] codes = StringUtils.split(zs, ",");
		for (int i = 0; i < codes.length; i++) {
			File file = new File("F:\\kdata\\" + codes[i] + ".json");
			if (!file.exists()) {
				JSONArray array = quoteKLineData(codes[i], "2018-01-01", "2023-10-31");
				file.createNewFile();
				RandomAccessFile raf = new RandomAccessFile(file, "rw");
				raf.write(array.toString(4).getBytes());
				raf.close();
			}
		}

	}

	private final static void df() throws Throwable {
		Map<String, String> hydm2name = new HashMap<String, String>();
		for (int i = 0; i < hyInfos.length; i++) {
			hydm2name.put(hyInfos[i][0], hyInfos[i][1]);
		}
		Map<String, String> stock2hy = new HashMap<String, String>();
		String s = "A01:sh600108,sh600354,sh600359,sh600371,sh600506,sh600540,sh600598,sh601118,sz000713,sz000998,sz002041,sz002772,sz300087,sz300143,sz300189;A02:sh600265,sz000592,sz002200,sz002679;A03:sh600189,sh600265,sz000663,sz002200,sz002679;A04:sh600097,sh600257,sh600467,sz000798,sz002069,sz002086,sz002696,sz200992;A05:sh600965,sh600975,sz000735,sz001201,sz002234,sz002321,sz002458,sz002505,sz300106,sz300313,sz300498;B06:sh600121,sh600123,sh600157,sh600188,sh600348,sh600395,sh600397,sh600403,sh600508,sh600758,sh600925,sh600971,sh600985,sh601001,sh601088,sh601101,sh601225,sh601666,sh601699,sh601898,sh601918,sh900948,sz000552,sz000937,sz000983;B07:sh600489,sh600497,sh600547,sh600988,sh601168,sh601899,sh601958,sh603799,sh603993,sz000426,sz000603,sz000688,sz000697,sz000758,sz000975,sz002155;B08:sh600382,sh601121,sh601969,sz000655,sz000762,sz000923,sz001203;B09:sh600259,sh600301,sh600338,sh600489,sh600497,sh600547,sh600711,sh600988,sh601020,sh601069,sh601168,sh601899,sh601958,sh603132,sh603993,sz000426,sz000506,sz000603,sz000688,sz000758,sz000975,sz001337,sz002155,sz002192,sz002978;B10:sh603505,sh605086;B11:sh600339,sh600583,sh600871,sh600968,sh601808,sh603619,sh603727,sh603979,sz002207,sz002554,sz002683,sz002828,sz300084,sz300157,sz300164,sz300191;B12:;C13:sh600107,sh600137,sh600177,sh600233,sh600398,sh600400,sh601566,sh601718,sh603001,sh603116,sh603518,sh603555,sh603808,sz000902,sz000955,sz002015,sz002029,sz002036,sz002044,sz002154,sz002239,sz002291,sz002404,sz002425,sz002485,sz002517,sz002569,sz002612,sz002687,sz002762,sz002763;C14:sh600439,sh603608,sh603958,sz002494,sz002674;C15:bj832023,sh600059,sh600084,sh600132,sh600189,sh600197,sh600199,sh600238,sh600300,sh600365,sh600519,sh600543,sh600559,sh600573,sh600600,sh600616,sh600696,sh600702,sh600779,sh600809,sh600962,sh601579,sh603156,sh603198,sh603369,sh603589,sh603711,sh603779,sh603919,sh605198,sh605337,sh605388,sh605499,sz000568,sz000596,sz000729,sz000752,sz000799,sz000848,sz000858,sz000860,sz000869,sz000929,sz000995,sz001338,sz002304,sz002461,sz002568,sz002646,sz200596,sz200869,sz300997;C16:;C17:bj833394,bj838262,sh600156,sh600220,sh600232,sh600448,sh600493,sh600626,sh600630,sh600689,sh600987,sh601339,sh601599,sh603055,sh603073,sh603130,sh603238,sh603307,sh603365,sh603558,sh603665,sh603889,sh605003,sh605055,sh605080,sh605155,sh605180,sh605189,sh900922,sz000726,sz000850,sz000955,sz002042,sz002083,sz002087,sz002144,sz002193,sz002293,sz002327,sz002394,sz002397,sz002516,sz003041,sz200726,sz300163,sz300577,sz300658,sz300819,sz300877,sz300888,sz300918,sz300952,sz300993;C18:sh600107,sh600137,sh600177,sh600398,sh600400,sh601566,sh601718,sh603196,sh603511,sh603518,sh603555,sh603587,sh603808,sh603839,sh603877,sh603908,sh605138,sz001209,sz001234,sz002003,sz002029,sz002154,sz002269,sz002404,sz002486,sz002563,sz002569,sz002612,sz002634,sz002656,sz002687,sz002762,sz002763,sz002832,sz002875,sz003016,sz300005,sz300840,sz301066,sz301276;C19:sh600439,sh600735,sh603001,sh603116,sh603557,sh603608,sh603958,sh605068,sz002494,sz002674,sz300591,sz300979;C20:sh600076,sh600321,sh601996,sh603226,sz000910,sz001211,sz002043,sz002631,sz301227;C21:sh600321,sh601996,sz000592,sz000910,sz002043,sz002240,sz002259,sz002354,sz002631;C22:bj872392,sh600103,sh600235,sh600308,sh600356,sh600433,sh600567,sh600793,sh600963,sh600966,sh603022,sh603165,sh603607,sh603687,sh603733,sh603863,sh605007,sh605009,sh605377,sh605500,sz000488,sz000815,sz001206,sz002012,sz002067,sz002078,sz002228,sz002303,sz002511,sz002521,sz002565,sz002799,sz002831,sz003006,sz200488,sz300883,sz301009,sz301062,sz301223,sz301296,sz301355,sz301469;C23:bj833075,sh600836,sh601515,sh603058,sh603429,sh603499,sz000695,sz000812,sz002117,sz002191,sz002229,sz002599,sz002836,sz002951,sz003003;C24:sh603272,sh603899,sh605099,sh605299,sz000017,sz001222,sz001300,sz002103,sz002292,sz002301,sz002348,sz002574,sz002575,sz002678,sz002721,sz002731,sz002740,sz002862,sz002899,sz200017,sz300329,sz300640,sz300651,sz300703,sz300756,sz301011,sz301287,sz301335;C25:sh603008,sh603600,sh603661,sh603818,sh603898,sz002489,sz002572,sz002798;C26:bj430489,bj830832,bj830974,bj831304,bj832471,bj833819,bj834033,bj834261,bj836419,bj836957,bj838402,bj870866,bj873527,sh600075,sh600078,sh600096,sh600135,sh600141,sh600160,sh600165,sh600230,sh600249,sh600273,sh600277,sh600309,sh600315,sh600319,sh600328,sh600352,sh600367,sh600370,sh600378,sh600389,sh600409,sh600423,sh600426,sh600470,sh600486,sh600500,sh600596,sh600610,sh600618,sh600623,sh600691,sh600714,sh600722,sh600727,sh600731,sh600746,sh600796,sh600800,sh600844,sh600935,sh600955,sh600989,sh601065,sh601208,sh601216,sh601568,sh601678,sh603010,sh603026,sh603041,sh603065,sh603067,sh603077,sh603078,sh603086,sh603110,sh603125,sh603155,sh603172,sh603181,sh603188,sh603192,sh603193,sh603213,sh603217,sh603227,sh603255,sh603260,sh603276,sh603281,sh603330,sh603360,sh603378,sh603379,sh603585,sh603599,sh603605,sh603630,sh603639,sh603650,sh603681,sh603683,sh603722,sh603737,sh603790,sh603810,sh603822,sh603823,sh603867,sh603879,sh603906,sh603916,sh603928,sh603931,sh603938,sh603948,sh603968,sh603977,sh603980,sh603983,sh605008,sh605020,sh605033,sh605166,sh605183,sh605366,sh605399,sh605566,sh605589,sh688106,sh688116,sh688129,sh688157,sh688199,sh688267,sh688268,sh688269,sh688275,sh688350,sh688353,sh688356,sh688357,sh688359,sh688550,sh688571,sh688585,sh688602,sh688625,sh688639,sh688690,sh688707,sh688716,sh688737,sh900906,sh900908,sh900909,sh900921,sz000301,sz000408,sz000422,sz000510,sz000525,sz000545,sz000553,sz000565,sz000635,sz000683,sz000691,sz000707,sz000731,sz000792,sz000818,sz000822,sz000830,sz000881,sz000893,sz000902,sz000912,sz000920,sz000930,sz000953,sz000985,sz000990,sz001207,sz001217,sz001218,sz001231,sz001255,sz001328,sz001333,sz002002,sz002004,sz002037,sz002054,sz002057,sz002068,sz002092,sz002094,sz002096,sz002109,sz002125,sz002136,sz002145,sz002165,sz002170,sz002211,sz002215,sz002226,sz002246,sz002250,sz002258,sz002274,sz002312,sz002319,sz002326,sz002341,sz002360,sz002361,sz002386,sz002391,sz002398,sz002407,sz002408,sz002409,sz002440,sz002442,sz002455,sz002469,sz002470,sz002476,sz002496,sz002497,sz002513,sz002538,sz002539,sz002545,sz002584,sz002588,sz002591,sz002601,sz002629,sz002632,sz002637,sz002643,sz002648,sz002666,sz002669,sz002709,sz002734,sz002741,sz002748,sz002749,sz002753,sz002758,sz002783,sz002802,sz002805,sz002809,sz002810,sz002827,sz002895,sz002909,sz002915,sz002917,sz002919,sz002942,sz002971,sz002986,sz003002,sz003017,sz003022,sz003042,sz200553,sz300019,sz300037,sz300041,sz300054,sz300055,sz300067,sz300072,sz300082,sz300107,sz300109,sz300121,sz300132,sz300135,sz300174,sz300200,sz300214,sz300225,sz300236,sz300243,sz300261,sz300285,sz300343,sz300387,sz300398,sz300405,sz300429,sz300437,sz300446,sz300481,sz300487,sz300505,sz300522,sz300535,sz300537,sz300568,sz300575,sz300576,sz300596,sz300610,sz300637,sz300641,sz300655,sz300665,sz300684,sz300721,sz300725,sz300727,sz300740,sz300741,sz300758,sz300796,sz300798,sz300801,sz300804,sz300821,sz300834,sz300848,sz300856,sz300886,sz300891,sz300910,sz300927,sz300957,sz301035,sz301036,sz301037,sz301059,sz301065,sz301069,sz301076,sz301077,sz301090,sz301092,sz301100,sz301108,sz301118,sz301149,sz301190,sz301209,sz301212,sz301216,sz301220,sz301238,sz301256,sz301283,sz301286,sz301292,sz301300,sz301349,sz301371,sz301373,sz301393,sz301395,sz301429,sz301487,sz301518,sz301555;C27:bj430017,bj430047,bj430478,bj830946,bj832566,bj832735,bj832982,bj833230,bj833266,bj833575,bj836433,bj837344,bj839729,bj873167,sh600062,sh600079,sh600080,sh600085,sh600129,sh600161,sh600195,sh600196,sh600200,sh600201,sh600211,sh600216,sh600222,sh600227,sh600252,sh600267,sh600276,sh600285,sh600299,sh600329,sh600332,sh600351,sh600380,sh600420,sh600422,sh600436,sh600479,sh600488,sh600513,sh600518,sh600521,sh600530,sh600535,sh600557,sh600566,sh600572,sh600594,sh600613,sh600624,sh600664,sh600671,sh600750,sh600771,sh600789,sh600812,sh600851,sh600867,sh600993,sh601089,sh603087,sh603139,sh603168,sh603222,sh603229,sh603351,sh603367,sh603387,sh603392,sh603439,sh603456,sh603520,sh603538,sh603566,sh603567,sh603590,sh603658,sh603669,sh603676,sh603707,sh603718,sh603811,sh603858,sh603880,sh603896,sh603963,sh603976,sh603998,sh605116,sh605177,sh605199,sh605507,sh688062,sh688068,sh688075,sh688076,sh688091,sh688098,sh688117,sh688136,sh688163,sh688166,sh688176,sh688177,sh688180,sh688185,sh688189,sh688192,sh688193,sh688197,sh688217,sh688221,sh688235,sh688247,sh688253,sh688266,sh688276,sh688278,sh688289,sh688298,sh688302,sh688317,sh688319,sh688321,sh688331,sh688336,sh688338,sh688363,sh688366,sh688373,sh688382,sh688393,sh688399,sh688426,sh688428,sh688443,sh688468,sh688505,sh688506,sh688513,sh688520,sh688526,sh688553,sh688566,sh688575,sh688578,sh688606,sh688656,sh688658,sh688670,sh688687,sh688739,sh688767,sh688799,sh900904,sh900917,sz000153,sz000403,sz000423,sz000513,sz000518,sz000534,sz000538,sz000566,sz000590,sz000597,sz000623,sz000650,sz000661,sz000739,sz000756,sz000766,sz000788,sz000790,sz000813,sz000908,sz000915,sz000919,sz000931,sz000952,sz000989,sz000999,sz001367,sz002001,sz002007,sz002019,sz002020,sz002022,sz002030,sz002038,sz002082,sz002099,sz002102,sz002107,sz002166,sz002198,sz002252,sz002262,sz002275,sz002287,sz002294,sz002317,sz002332,sz002349,sz002365,sz002370,sz002390,sz002393,sz002399,sz002412,sz002422,sz002424,sz002433,sz002435,sz002437,sz002550,sz002562,sz002566,sz002581,sz002603,sz002644,sz002653,sz002675,sz002688,sz002693,sz002728,sz002737,sz002750,sz002755,sz002773,sz002793,sz002817,sz002821,sz002826,sz002864,sz002868,sz002872,sz002873,sz002880,sz002898,sz002900,sz002907,sz002923,sz002932,sz002940,sz300006,sz300009,sz300016,sz300026,sz300039,sz300086,sz300108,sz300110,sz300111,sz300119,sz300122,sz300142,sz300147,sz300158,sz300181,sz300194,sz300199,sz300204,sz300233,sz300239,sz300254,sz300255,sz300267,sz300289,sz300294,sz300357,sz300363,sz300381,sz300391,sz300401,sz300406,sz300434,sz300436,sz300439,sz300452,sz300463,sz300482,sz300485,sz300497,sz300501,sz300519,sz300534,sz300558,sz300573,sz300583,sz300584,sz300601,sz300630,sz300636,sz300639,sz300642,sz300683,sz300685,sz300702,sz300705,sz300723,sz300832,sz300841,sz300871,sz300878,sz300942,sz300966,sz301075,sz301089,sz301093,sz301111,sz301130,sz301201,sz301207,sz301211,sz301246,sz301258,sz301277,sz301281,sz301301,sz301331,sz301507,sz301509;C28:bj836077,sh600063,sh600346,sh600527,sh600810,sh600889,sh601113,sh601233,sh603225,sh603332,sh688065,sh688203,sh688295,sh688722,sz000420,sz000677,sz000703,sz000782,sz000936,sz000949,sz002064,sz002172,sz002206,sz002254,sz002427,sz002493,sz002998,sz300699,sz300777,sz300876,sz301057;C29:bj831195,bj831834,bj832089,bj832225,bj832469,bj836247,bj836871,bj837174,bj838163,bj870204,bj871642,bj871694,bj873665,sh600143,sh600182,sh600210,sh600458,sh600469,sh601058,sh601163,sh601500,sh601966,sh603033,sh603051,sh603150,sh603212,sh603221,sh603266,sh603408,sh603580,sh603615,sh603655,sh603657,sh603726,sh603806,sh603856,sh603991,sh603992,sh605255,sh605488,sh688026,sh688219,sh688299,sh688323,sh688386,sh688560,sh688669,sh688680,sz000589,sz000599,sz000619,sz000659,sz000859,sz000887,sz000973,sz001368,sz001378,sz002014,sz002108,sz002224,sz002243,sz002263,sz002324,sz002372,sz002381,sz002395,sz002420,sz002522,sz002585,sz002641,sz002676,sz002694,sz002735,sz002768,sz002790,sz002812,sz002825,sz002838,sz002886,sz002984,sz003011,sz003018,sz300021,sz300169,sz300180,sz300198,sz300218,sz300221,sz300230,sz300305,sz300320,sz300321,sz300375,sz300478,sz300539,sz300547,sz300586,sz300587,sz300599,sz300644,sz300677,sz300716,sz300717,sz300731,sz300767,sz300806,sz300849,sz300868,sz300905,sz300920,sz300955,sz300980,sz300981,sz300995,sz301000,sz301003,sz301019,sz301131,sz301161,sz301193,sz301196,sz301198,sz301233,sz301237,sz301323,sz301356;C30:bj832175,bj833580,bj835179,bj835185,bj836149,bj836675,bj836807,bj839719,bj839725,bj839792,sh600172,sh600176,sh600207,sh600293,sh600326,sh600366,sh600425,sh600449,sh600516,sh600529,sh600585,sh600586,sh600660,sh600668,sh600678,sh600720,sh600783,sh600801,sh600802,sh600819,sh600876,sh600881,sh600883,sh601636,sh601865,sh601992,sh603021,sh603119,sh603256,sh603268,sh603385,sh603578,sh603601,sh603612,sh603616,sh603663,sh603688,sh603725,sh603826,sh603838,sh605006,sh605122,sh688077,sh688119,sh688233,sh688300,sh688398,sh688598,sh688733,sh900918,sz000012,sz000023,sz000401,sz000672,sz000786,sz000789,sz000795,sz000877,sz000935,sz001212,sz001216,sz001269,sz001296,sz001301,sz001322,sz002066,sz002080,sz002088,sz002162,sz002201,sz002205,sz002225,sz002233,sz002271,sz002297,sz002302,sz002392,sz002457,sz002571,sz002596,sz002623,sz002671,sz002742,sz002785,sz002798,sz002918,sz003012,sz003037,sz200012,sz300160,sz300179,sz300196,sz300224,sz300234,sz300374,sz300395,sz300554,sz300606,sz300690,sz300700,sz300715,sz300737,sz300748,sz300861,sz300890,sz301010,sz301071,sz301188;C31:sh600103,sh600163,sh600235,sh600308,sh600356,sh600419,sh600433,sh600462,sh600567,sh600793,sh600963,sh600966,sh603022,sz000488,sz000576,sz000815,sz000820,sz000833,sz002012,sz002067,sz002078,sz002228,sz002235,sz002247,sz002303,sz002511,sz002521,sz002565,sz002799,sz200488,sz301009;C32:bj839167,bj873576,sh600110,sh600111,sh600206,sh600219,sh600255,sh600281,sh600331,sh600361,sh600362,sh600392,sh600456,sh600459,sh600490,sh600531,sh600549,sh600595,sh600768,sh600961,sh601137,sh601212,sh601388,sh601600,sh601609,sh601677,sh601702,sh603399,sh603527,sh603799,sh603876,sh603978,sh605208,sh688102,sh688122,sh688231,sh688257,sz000060,sz000612,sz000629,sz000630,sz000633,sz000657,sz000737,sz000751,sz000807,sz000831,sz000878,sz000933,sz000960,sz000962,sz002114,sz002128,sz002149,sz002160,sz002167,sz002171,sz002182,sz002203,sz002237,sz002240,sz002295,sz002333,sz002378,sz002379,sz002428,sz002460,sz002466,sz002501,sz002532,sz002540,sz002578,sz002667,sz002716,sz002824,sz002842,sz002988,sz003038,sz300034,sz300057,sz300337,sz300489,sz300618,sz300697,sz300855,sz300963,sz301219,sz301398;C33:bj430510,bj831768,bj833751,bj835857,bj836699,bj836720,bj871634,bj873305,bj873693,sh600114,sh600477,sh600496,sh600558,sh600615,sh600992,sh601686,sh601700,sh601968,sh603028,sh603040,sh603112,sh603278,sh603348,sh603577,sh603626,sh603629,sh603848,sh603937,sh603969,sh603985,sh605123,sh605158,sh605268,sh605376,sh688059,sh688186,sh688308,sh688355,sh688379,sh688456,sh688786,sz000039,sz000055,sz000778,sz000969,sz001226,sz002026,sz002032,sz002084,sz002132,sz002135,sz002150,sz002318,sz002342,sz002374,sz002403,sz002443,sz002444,sz002478,sz002487,sz002514,sz002541,sz002547,sz002564,sz002615,sz002652,sz002701,sz002722,sz002743,sz002752,sz002760,sz002787,sz002791,sz002843,sz002846,sz002850,sz002921,sz002965,sz002969,sz002976,sz003043,sz200055,sz300328,sz300345,sz300488,sz300828,sz300881,sz300885,sz300946,sz300985,sz300986,sz300988,sz301004,sz301040,sz301055,sz301063,sz301137,sz301160,sz301163,sz301202,sz301268,sz301307,sz301377;C34:bj430418,bj830839,bj830896,bj831278,bj831689,bj831855,bj832662,bj833455,bj833943,bj836270,bj838670,bj870508,bj871245,bj872808,bj873169,bj873223,sh600218,sh600243,sh600416,sh600421,sh600444,sh600592,sh600619,sh600835,sh600875,sh600894,sh601002,sh601177,sh601615,sh601727,sh601882,sh601956,sh603011,sh603090,sh603109,sh603131,sh603173,sh603187,sh603201,sh603218,sh603269,sh603270,sh603277,sh603279,sh603298,sh603315,sh603320,sh603321,sh603339,sh603356,sh603611,sh603617,sh603667,sh603699,sh603757,sh603915,sh603966,sh605060,sh605100,sh605259,sh605286,sh605389,sh688017,sh688165,sh688211,sh688251,sh688255,sh688305,sh688333,sh688360,sh688409,sh688433,sh688448,sh688455,sh688557,sh688558,sh688577,sh688660,sh688678,sh688697,sh900910,sh900925,sz000404,sz000410,sz000530,sz000570,sz000595,sz000678,sz000777,sz000811,sz000816,sz000837,sz000880,sz000903,sz001225,sz001268,sz001288,sz001380,sz002009,sz002011,sz002046,sz002050,sz002122,sz002152,sz002158,sz002202,sz002248,sz002255,sz002272,sz002282,sz002347,sz002367,sz002418,sz002438,sz002472,sz002480,sz002483,sz002520,sz002523,sz002529,sz002534,sz002537,sz002552,sz002553,sz002559,sz002598,sz002630,sz002633,sz002639,sz002685,sz002686,sz002689,sz002774,sz002795,sz002808,sz002816,sz002871,sz002884,sz002896,sz002903,sz002943,sz003025,sz003033,sz200530,sz200570,sz200706,sz200771,sz300004,sz300024,sz300083,sz300091,sz300126,sz300145,sz300154,sz300161,sz300185,sz300193,sz300257,sz300260,sz300263,sz300411,sz300420,sz300421,sz300441,sz300470,sz300473,sz300503,sz300512,sz300540,sz300607,sz300611,sz300669,sz300694,sz300718,sz300772,sz300780,sz300809,sz300817,sz300850,sz300853,sz300904,sz300907,sz300931,sz300943,sz300971,sz300984,sz300990,sz300992,sz301020,sz301028,sz301029,sz301032,sz301043,sz301056,sz301070,sz301079,sz301107,sz301125,sz301151,sz301232,sz301252,sz301255,sz301261,sz301272,sz301273,sz301279,sz301309,sz301311,sz301317,sz301353,sz301368,sz301399,sz301446,sz301448,sz301548,sz301559;C35:sh600836,sh601515,sz000812,sz002117,sz002191,sz002229,sz002599;C36:bj831906,bj832000,bj832978,bj833454,bj833533,bj836221,bj837663,bj838171,bj838837,bj839946,bj870436,sh600006,sh600066,sh600081,sh600104,sh600148,sh600166,sh600178,sh600213,sh600303,sh600375,sh600418,sh600480,sh600501,sh600523,sh600609,sh600686,sh600698,sh600699,sh600733,sh600741,sh600742,sh600841,sh600933,sh600960,sh601127,sh601238,sh601279,sh601633,sh601689,sh601717,sh601777,sh601799,sh603006,sh603009,sh603013,sh603035,sh603037,sh603048,sh603085,sh603089,sh603107,sh603121,sh603158,sh603161,sh603166,sh603178,sh603179,sh603190,sh603197,sh603211,sh603239,sh603286,sh603305,sh603306,sh603319,sh603335,sh603358,sh603586,sh603596,sh603730,sh603758,sh603767,sh603768,sh603786,sh603788,sh603809,sh603922,sh603926,sh603949,sh603950,sh603982,sh603997,sh605005,sh605018,sh605088,sh605128,sh605133,sh605151,sh605228,sh605319,sh605333,sh688280,sh688612,sh900920,sh900946,sh900953,sz000030,sz000338,sz000550,sz000559,sz000572,sz000581,sz000625,sz000700,sz000800,sz000868,sz000901,sz000951,sz000957,sz000980,sz000981,sz001260,sz001278,sz001282,sz001311,sz001319,sz002048,sz002085,sz002101,sz002126,sz002190,sz002213,sz002239,sz002265,sz002283,sz002284,sz002328,sz002355,sz002363,sz002406,sz002434,sz002448,sz002454,sz002488,sz002510,sz002536,sz002590,sz002592,sz002593,sz002594,sz002662,sz002664,sz002703,sz002708,sz002715,sz002725,sz002765,sz002806,sz002863,sz002870,sz002920,sz200030,sz200550,sz200581,sz200625,sz300100,sz300176,sz300237,sz300258,sz300304,sz300428,sz300507,sz300580,sz300585,sz300643,sz300652,sz300680,sz300695,sz300733,sz300742,sz300745,sz300863,sz300893,sz300926,sz300969,sz300978,sz301005,sz301007,sz301039,sz301072,sz301119,sz301133,sz301170,sz301181,sz301192,sz301225,sz301229,sz301298,sz301397,sz301499,sz301529,sz301550;C37:sh603899,sz002103,sz002292,sz002301,sz002348,sz002502,sz002575,sz002605,sz002678,sz002699,sz002831,sz300043,sz300329;C38:bj430718,bj830809,bj831627,bj831641,bj833523,bj834062,bj834639,bj834682,bj834770,bj835237,bj835368,bj835985,bj836239,bj837046,bj870726,bj873152,bj873339,bj873593,sh600089,sh600105,sh600112,sh600152,sh600192,sh600202,sh600241,sh600261,sh600268,sh600290,sh600312,sh600336,sh600379,sh600438,sh600468,sh600478,sh600481,sh600487,sh600517,sh600520,sh600522,sh600537,sh600550,sh600577,sh600580,sh600590,sh600651,sh600690,sh600732,sh600847,sh600869,sh600885,sh600973,sh600983,sh601012,sh601126,sh601179,sh601311,sh601369,sh601616,sh601877,sh603015,sh603016,sh603031,sh603045,sh603050,sh603063,sh603070,sh603075,sh603097,sh603105,sh603195,sh603215,sh603219,sh603303,sh603331,sh603333,sh603355,sh603366,sh603398,sh603486,sh603488,sh603489,sh603507,sh603515,sh603519,sh603530,sh603551,sh603579,sh603583,sh603606,sh603618,sh603659,sh603677,sh603679,sh603685,sh603701,sh603703,sh603728,sh603819,sh603829,sh603861,sh603868,sh603897,sh603988,sh605066,sh605117,sh605196,sh605222,sh605277,sh605288,sh605336,sh605365,sh605378,sh605555,sh688005,sh688032,sh688033,sh688063,sh688148,sh688169,sh688223,sh688226,sh688303,sh688330,sh688339,sh688345,sh688348,sh688349,sh688390,sh688395,sh688408,sh688429,sh688472,sh688517,sh688567,sh688599,sh688611,sh688663,sh688676,sh688681,sh688719,sh688772,sh688778,sh688779,sh688793,sh688819,sz000049,sz000070,sz000333,sz000400,sz000521,sz000533,sz000541,sz000651,sz000836,sz000921,sz000922,sz000982,sz001208,sz001259,sz001283,sz002005,sz002028,sz002035,sz002056,sz002074,sz002076,sz002090,sz002112,sz002129,sz002139,sz002141,sz002168,sz002169,sz002176,sz002184,sz002218,sz002227,sz002242,sz002245,sz002249,sz002270,sz002276,sz002290,sz002300,sz002309,sz002334,sz002335,sz002339,sz002346,sz002350,sz002358,sz002364,sz002451,sz002452,sz002459,sz002471,sz002491,sz002498,sz002508,sz002518,sz002527,sz002531,sz002533,sz002543,sz002546,sz002560,sz002576,sz002580,sz002606,sz002610,sz002614,sz002617,sz002638,sz002665,sz002668,sz002677,sz002692,sz002705,sz002706,sz002723,sz002724,sz002733,sz002738,sz002745,sz002756,sz002759,sz002801,sz002823,sz002851,sz002860,sz002865,sz002879,sz002882,sz002892,sz002922,sz002927,sz002953,sz002959,sz003021,sz003023,sz200512,sz200521,sz200541,sz300001,sz300014,sz300018,sz300032,sz300035,sz300040,sz300048,sz300062,sz300068,sz300069,sz300073,sz300080,sz300093,sz300105,sz300116,sz300117,sz300118,sz300124,sz300125,sz300129,sz300141,sz300153,sz300207,sz300208,sz300217,sz300222,sz300247,sz300252,sz300265,sz300272,sz300274,sz300279,sz300283,sz300317,sz300340,sz300341,sz300342,sz300376,sz300403,sz300407,sz300409,sz300423,sz300427,sz300432,sz300438,sz300444,sz300447,sz300477,sz300484,sz300490,sz300491,sz300499,sz300510,sz300530,sz300569,sz300593,sz300617,sz300625,sz300626,sz300660,sz300670,sz300681,sz300693,sz300713,sz300724,sz300750,sz300763,sz300769,sz300808,sz300820,sz300824,sz300827,sz300833,sz300889,sz300894,sz300911,sz300913,sz300919,sz300932,sz300933,sz301008,sz301012,sz301023,sz301031,sz301082,sz301120,sz301121,sz301155,sz301168,sz301187,sz301210,sz301222,sz301226,sz301266,sz301278,sz301291,sz301295,sz301310,sz301320,sz301327,sz301332,sz301359,sz301361,sz301362,sz301386,sz301388,sz301418,sz301439,sz301525;C39:bj430139,bj430198,bj831167,bj831526,bj832110,bj832491,bj832876,bj833346,bj833914,bj834950,bj835640,bj836395,bj837212,bj837821,bj838701,bj838971,bj870199,bj870357,bj870976,bj871857,bj871981,bj872190,bj872374,bj873001,sh600060,sh600100,sh600118,sh600130,sh600151,sh600171,sh600183,sh600198,sh600203,sh600237,sh600271,sh600288,sh600330,sh600345,sh600353,sh600355,sh600360,sh600363,sh600405,sh600435,sh600460,sh600498,sh600525,sh600552,sh600562,sh600563,sh600584,sh600601,sh600666,sh600703,sh600707,sh600745,sh600764,sh600775,sh600776,sh600839,sh600877,sh600884,sh600888,sh600898,sh600980,sh600990,sh601138,sh601231,sh601869,sh603002,sh603005,sh603019,sh603023,sh603025,sh603042,sh603052,sh603068,sh603083,sh603106,sh603115,sh603118,sh603133,sh603160,sh603185,sh603186,sh603228,sh603236,sh603267,sh603290,sh603296,sh603327,sh603328,sh603380,sh603386,sh603390,sh603496,sh603501,sh603516,sh603528,sh603595,sh603633,sh603660,sh603678,sh603712,sh603738,sh603773,sh603803,sh603890,sh603893,sh603920,sh603933,sh603936,sh603986,sh603989,sh605058,sh605111,sh605118,sh605218,sh605258,sh605358,sh605588,sh688002,sh688007,sh688008,sh688011,sh688019,sh688020,sh688025,sh688027,sh688035,sh688036,sh688041,sh688047,sh688048,sh688049,sh688055,sh688061,sh688079,sh688080,sh688081,sh688093,sh688100,sh688103,sh688107,sh688110,sh688123,sh688126,sh688132,sh688135,sh688138,sh688141,sh688143,sh688146,sh688150,sh688153,sh688159,sh688167,sh688172,sh688175,sh688181,sh688182,sh688183,sh688184,sh688195,sh688205,sh688208,sh688209,sh688210,sh688213,sh688216,sh688220,sh688230,sh688234,sh688249,sh688260,sh688261,sh688270,sh688272,sh688282,sh688286,sh688288,sh688307,sh688311,sh688313,sh688322,sh688326,sh688332,sh688347,sh688352,sh688362,sh688371,sh688372,sh688375,sh688380,sh688381,sh688385,sh688387,sh688388,sh688396,sh688401,sh688403,sh688416,sh688418,sh688432,sh688439,sh688450,sh688458,sh688469,sh688475,sh688484,sh688486,sh688489,sh688496,sh688498,sh688503,sh688511,sh688512,sh688515,sh688519,sh688522,sh688525,sh688533,sh688535,sh688538,sh688539,sh688548,sh688549,sh688552,sh688582,sh688591,sh688592,sh688593,sh688595,sh688596,sh688601,sh688603,sh688608,sh688609,sh688618,sh688620,sh688629,sh688636,sh688655,sh688661,sh688662,sh688665,sh688667,sh688668,sh688683,sh688689,sh688693,sh688696,sh688711,sh688728,sh688766,sh688776,sh688788,sh688798,sh688800,sh688981,sh900941,sz000016,sz000020,sz000021,sz000045,sz000050,sz000063,sz000066,sz000100,sz000413,sz000509,sz000536,sz000547,sz000561,sz000586,sz000636,sz000670,sz000725,sz000727,sz000733,sz000801,sz000810,sz000823,sz000909,sz000938,sz000970,sz000977,sz000988,sz001229,sz001270,sz001308,sz001309,sz001314,sz001339,sz001373,sz002017,sz002025,sz002036,sz002045,sz002049,sz002052,sz002055,sz002077,sz002079,sz002089,sz002104,sz002106,sz002119,sz002130,sz002134,sz002137,sz002138,sz002151,sz002156,sz002161,sz002179,sz002180,sz002185,sz002189,sz002194,sz002199,sz002214,sz002217,sz002222,sz002231,sz002236,sz002241,sz002273,sz002281,sz002288,sz002289,sz002296,sz002308,sz002313,sz002351,sz002369,sz002376,sz002383,sz002384,sz002387,sz002388,sz002389,sz002396,sz002402,sz002413,sz002414,sz002415,sz002426,sz002429,sz002436,sz002446,sz002449,sz002456,sz002463,sz002465,sz002475,sz002484,sz002506,sz002512,sz002519,sz002528,sz002577,sz002579,sz002583,sz002587,sz002600,sz002635,sz002636,sz002655,sz002660,sz002681,sz002729,sz002766,sz002782,sz002792,sz002796,sz002813,sz002815,sz002829,sz002835,sz002841,sz002845,sz002848,sz002855,sz002859,sz002861,sz002866,sz002869,sz002876,sz002881,sz002885,sz002888,sz002897,sz002902,sz002906,sz002913,sz002916,sz002925,sz002935,sz002937,sz002938,sz002947,sz002952,sz002955,sz002960,sz002962,sz002972,sz002977,sz002981,sz002983,sz002992,sz002993,sz003015,sz003019,sz003026,sz003028,sz003031,sz003040,sz200016,sz200020,sz200045,sz200413,sz200468,sz200725,sz300042,sz300045,sz300046,sz300053,sz300065,sz300076,sz300077,sz300078,sz300079,sz300088,sz300101,sz300102,sz300114,sz300115,sz300120,sz300127,sz300128,sz300134,sz300136,sz300139,sz300155,sz300162,sz300177,sz300205,sz300211,sz300213,sz300219,sz300220,sz300223,sz300227,sz300232,sz300241,sz300256,sz300270,sz300282,sz300296,sz300301,sz300303,sz300308,sz300319,sz300322,sz300323,sz300327,sz300331,sz300346,sz300351,sz300353,sz300373,sz300389,sz300390,sz300393,sz300394,sz300397,sz300408,sz300414,sz300433,sz300449,sz300455,sz300456,sz300458,sz300460,sz300474,sz300476,sz300479,sz300502,sz300504,sz300514,sz300516,sz300531,sz300543,sz300546,sz300548,sz300555,sz300563,sz300565,sz300566,sz300570,sz300581,sz300582,sz300590,sz300602,sz300615,sz300620,sz300623,sz300627,sz300628,sz300629,sz300632,sz300638,sz300647,sz300656,sz300657,sz300661,sz300666,sz300672,sz300679,sz300686,sz300689,sz300691,sz300698,sz300701,sz300708,sz300709,sz300710,sz300711,sz300726,sz300735,sz300739,sz300743,sz300747,sz300752,sz300762,sz300782,sz300787,sz300790,sz300793,sz300802,sz300807,sz300811,sz300814,sz300822,sz300831,sz300835,sz300842,sz300843,sz300847,sz300852,sz300857,sz300866,sz300870,sz300884,sz300903,sz300909,sz300916,sz300936,sz300939,sz300940,sz300951,sz300956,sz300964,sz300965,sz300968,sz300976,sz300991,sz301002,sz301041,sz301042,sz301045,sz301050,sz301051,sz301067,sz301086,sz301106,sz301123,sz301132,sz301135,sz301141,sz301150,sz301152,sz301157,sz301165,sz301176,sz301180,sz301182,sz301183,sz301189,sz301191,sz301205,sz301217,sz301251,sz301280,sz301282,sz301285,sz301308,sz301314,sz301318,sz301319,sz301321,sz301326,sz301328,sz301329,sz301330,sz301348,sz301358,sz301366,sz301379,sz301383,sz301387,sz301389,sz301391,sz301419,sz301486,sz301488,sz301489,sz301503,sz301511,sz301517;C40:bj430476,bj430556,bj430685,bj830779,bj830879,bj831305,bj831961,bj832651,bj832885,bj833509,bj834407,bj836260,bj836504,bj870299,bj871396,sh600071,sh601222,sh601567,sh603100,sh603275,sh603297,sh603416,sh603556,sh603662,sh603700,sh688010,sh688056,sh688112,sh688115,sh688127,sh688160,sh688283,sh688320,sh688337,sh688502,sh688528,sh688570,sh688597,sh688600,sh688610,sh688616,sh688622,sh688628,sh688671,sh688686,sh688698,sh688768,sz001266,sz002058,sz002121,sz002175,sz002338,sz002658,sz002747,sz002767,sz002849,sz002857,sz002877,sz002979,sz002980,sz300007,sz300066,sz300112,sz300137,sz300165,sz300203,sz300259,sz300286,sz300306,sz300354,sz300360,sz300370,sz300371,sz300417,sz300430,sz300445,sz300466,sz300480,sz300515,sz300553,sz300557,sz300567,sz300572,sz300648,sz300667,sz300720,sz300800,sz300838,sz300862,sz300880,sz300882,sz300897,sz301006,sz301129,sz301303,sz301421,sz301510,sz301528;C41:sh600179,sh600281,sh600333,sh600339,sh600688,sh600721,sh600725,sh600740,sh600792,sh600989,sh600997,sh601011,sz000059,sz000637,sz000698,sz000723,sz000819,sz002377,sz002778;C42:sh600217,sh688087,sh688196,sz000573,sz000820,sz002340,sz002672,sz002996,sz300779,sz300930,sz301026,sz301068,sz301265,sz301500;C43:sh600075,sh600078,sh600096,sh600135,sh600141,sh600155,sh600160,sh600226,sh600227,sh600228,sh600229,sh600230,sh600249,sh600277,sh600299,sh600301,sh600309,sh600315,sh600319,sh600328,sh600352,sh600367,sh600378,sh600389,sh600409,sh600423,sh600426,sh600470,sh600481,sh600486,sh600490,sh600538,sh600589,sh600596,sh600599,sh600618,sh600636,sh600691,sh600722,sh600727,sh600731,sh600746,sh600769,sh600803,sh600844,sh600985,sh601208,sh601216,sh601678,sh603002,sh603010,sh603020,sh603026,sh603051,sh603067,sh603077,sh603113,sh603186,sh603227,sh603519,sh603599,sh603737,sh603798,sh603822,sh603928,sh603968,sh605099,sh688199,sh900908,sh900921,sz000155,sz000422,sz000510,sz000523,sz000525,sz000553,sz000565,sz000627,sz000635,sz000683,sz000707,sz000731,sz000737,sz000755,sz000782,sz000792,sz000818,sz000822,sz000830,sz000912,sz000950,sz000953,sz000985,sz001207,sz001218,sz002010,sz002019,sz002037,sz002053,sz002054,sz002057,sz002061,sz002068,sz002092,sz002094,sz002096,sz002109,sz002125,sz002136,sz002145,sz002165,sz002167,sz002170,sz002192,sz002211,sz002215,sz002217,sz002226,sz002246,sz002250,sz002256,sz002258,sz002274,sz002319,sz002326,sz002341,sz002360,sz002361,sz002386,sz002391,sz002407,sz002408,sz002409,sz002440,sz002442,sz002453,sz002455,sz002466,sz002470,sz002476,sz002496,sz002497,sz002513,sz002538,sz002539,sz002562,sz002568,sz002581,sz002584,sz002588,sz002597,sz002601,sz002632,sz002637,sz002643,sz002648,sz002666,sz002669,sz002709,sz002734,sz002741,sz002748,sz002753,sz002802,sz002805,sz002838,sz002915,sz200553,sz300019,sz300037,sz300041,sz300054,sz300063,sz300067,sz300072,sz300082,sz300107,sz300108,sz300109,sz300121,sz300132,sz300135,sz300174,sz300192,sz300200,sz300214,sz300225,sz300236,sz300243,sz300261,sz300285,sz300343,sz300387,sz300405,sz300481,sz300487,sz300505,sz300758,sz301201;D44:sh600011,sh600021,sh600023,sh600025,sh600027,sh600032,sh600101,sh600116,sh600149,sh600163,sh600167,sh600236,sh600310,sh600396,sh600452,sh600483,sh600505,sh600509,sh600578,sh600642,sh600644,sh600674,sh600719,sh600726,sh600744,sh600780,sh600795,sh600821,sh600863,sh600864,sh600886,sh600900,sh600905,sh600969,sh600979,sh600982,sh600995,sh601016,sh601619,sh601778,sh601985,sh601991,sh603693,sh605011,sh605028,sh605162,sh605580,sh900937,sz000027,sz000037,sz000040,sz000155,sz000507,sz000531,sz000537,sz000539,sz000543,sz000591,sz000600,sz000601,sz000690,sz000692,sz000722,sz000767,sz000791,sz000862,sz000875,sz000883,sz000899,sz000958,sz000966,sz000993,sz001210,sz001258,sz001286,sz001289,sz001896,sz002015,sz002039,sz002256,sz002479,sz002608,sz002893,sz003816,sz200037,sz200539,sz300335;D45:bj831010,sh600333,sh600617,sh600635,sh600681,sh600803,sh600903,sh600917,sh600956,sh601139,sh603053,sh603080,sh603318,sh603393,sh603689,sh603706,sh605090,sh605169,sh605368,sh900913,sz000407,sz000421,sz000593,sz000669,sz001299,sz001331,sz002259,sz002267,sz002700,sz002911,sz300332,sz300435;D46:sh600008,sh600168,sh600187,sh600283,sh600461,sh600769,sh600874,sh601158,sh601199,sh601368,sh603291,sh603759,sh603817,sz000544,sz000598,sz000605,sz003039;E47:sh600159,sh600939,sz000628;E48:bj830964,sh600039,sh600072,sh600133,sh600170,sh600248,sh600284,sh600463,sh600491,sh600502,sh600512,sh600667,sh600820,sh600846,sh600853,sh600970,sh601068,sh601117,sh601186,sh601390,sh601611,sh601618,sh601668,sh601669,sh601789,sh601800,sh601868,sh603007,sh603176,sh603316,sh603388,sh603637,sh603778,sh603815,sh603843,sh603955,sh603959,sh605303,sh605598,sz000010,sz000090,sz000498,sz000685,sz000711,sz000928,sz001267,sz002051,sz002060,sz002061,sz002062,sz002116,sz002140,sz002307,sz002431,sz002542,sz002586,sz002628,sz002663,sz002761,sz002775,sz002941,sz003001,sz300008,sz300517,sz300536,sz300649,sz300712,sz300982,sz301098;E49:sh601133,sh603163,sh603929,sz000032;E50:sh600193,sh600234,sh601886,sh603030,sh603098,sh603137,sh603828,sh605178,sh605287,sh605289,sh900939,sz002047,sz002081,sz002163,sz002323,sz002325,sz002375,sz002482,sz002620,sz002713,sz002789,sz002811,sz002822,sz002830,sz002856,sz002963,sz002989,sz300506,sz300621;F51:bj832149,sh600051,sh600056,sh600058,sh600082,sh600083,sh600098,sh600128,sh600153,sh600173,sh600180,sh600212,sh600250,sh600278,sh600287,sh600335,sh600387,sh600415,sh600511,sh600538,sh600546,sh600599,sh600605,sh600608,sh600647,sh600648,sh600704,sh600710,sh600739,sh600753,sh600755,sh600822,sh600829,sh600833,sh600981,sh600998,sh601028,sh601061,sh603003,sh603071,sh603108,sh603122,sh603223,sh603368,sh603716,sh603970,sh605056,sh900912,sh900927,sz000019,sz000028,sz000034,sz000062,sz000065,sz000096,sz000151,sz000159,sz000411,sz000554,sz000626,sz000632,sz000638,sz000652,sz000701,sz000705,sz000829,sz000900,sz000906,sz000927,sz000950,sz000996,sz001287,sz001298,sz001316,sz002072,sz002091,sz002133,sz002221,sz002416,sz002441,sz002462,sz002505,sz002589,sz002788,sz002819,sz003020,sz200019,sz200028,sz300131,sz300184,sz300475,sz300538,sz300650,sz300755,sz300937,sz300975,sz301015,sz301085,sz301099,sz301126,sz301166,sz301263,sz301370;F52:bj838030,sh600272,sh600280,sh600297,sh600306,sh600337,sh600386,sh600628,sh600653,sh600655,sh600693,sh600694,sh600697,sh600712,sh600713,sh600729,sh600738,sh600774,sh600778,sh600785,sh600814,sh600824,sh600827,sh600828,sh600838,sh600857,sh600858,sh600859,sh600865,sh600916,sh600976,sh601010,sh601086,sh601116,sh601366,sh601607,sh601933,sh603101,sh603214,sh603233,sh603353,sh603708,sh603719,sh603777,sh603883,sh603900,sh603939,sh605136,sh605188,sh605266,sh605599,sh900923,sh900943,sz000007,sz000025,sz000026,sz000078,sz000417,sz000419,sz000501,sz000564,sz000679,sz000715,sz000753,sz000757,sz000759,sz000851,sz000963,sz002024,sz002187,sz002251,sz002277,sz002280,sz002336,sz002356,sz002419,sz002556,sz002561,sz002640,sz002697,sz002727,sz002780,sz002999,sz200025,sz200026,sz300022,sz300209,sz300464,sz300592,sz300622,sz300783,sz300892,sz300945,sz301017,sz301078,sz301088,sz301177,sz301376,sz301381,sz301408,sz301558;G53:sh600125,sh601006,sh601333,sh601816,sz000557,sz001213;G54:sh600012,sh600020,sh600033,sh600035,sh600106,sh600119,sh600269,sh600350,sh600368,sh600377,sh600548,sh600561,sh600611,sh600650,sh600662,sh600676,sh600834,sh601107,sh601188,sh601518,sh603069,sh603813,sh900903,sh900914,sz000088,sz000429,sz000548,sz000755,sz000828,sz001965,sz002357,sz002627,sz002682,sz002800,sz200429;G55:bj833171,sh600017,sh600018,sh600026,sh600190,sh600279,sh600428,sh600575,sh600717,sh600751,sh600798,sh601000,sh601008,sh601018,sh601022,sh601228,sh601298,sh601326,sh601866,sh601872,sh601880,sh601919,sh601975,sh603162,sh603167,sh603209,sh603565,sh900938,sh900952,sz000520,sz000582,sz000905,sz001205,sz001872,sz002040,sz002320,sz201872;G56:sh600004,sh600009,sh600029,sh600115,sh600221,sh600897,sh601021,sh601111,sh601156,sh603885,sh900945,sz000089,sz000099,sz002928;G57:;G58:bj872351,sh600179,sh601598,sh603128,sh603329,sh603713,sh603871,sh603967,sh605050,sz001202,sz001317;G59:sh600787,sh600794,sh603066,sh603535,sz002492,sz002930,sz300013,sz300240,sz300873;G60:sh600233,sh603056,sz002120,sz002352,sz002468;H61:sh600258,sh600754,sh601007,sh900934,sz000428,sz301073;H62:sh605108,sz000721,sz002186,sz002306;I63:sh600037,sh600050,sh600637,sh600831,sh600936,sh600941,sh600959,sh600996,sh601698,sh601728,sh601929,sh603559,sz000839,sz002238,sz300288,sz300310,sz300770,sz301262;I64:sh600070,sh600226,sh600228,sh600358,sh600556,sh600589,sh600633,sh600640,sh600804,sh600986,sh601360,sh603000,sh603258,sh603444,sh603533,sh603613,sh603825,sh603881,sh603888,sh688158,sz000503,sz000676,sz002095,sz002131,sz002174,sz002235,sz002247,sz002264,sz002315,sz002354,sz002425,sz002467,sz002502,sz002517,sz002530,sz002555,sz002558,sz002605,sz002624,sz002803,sz002995,sz003010,sz300031,sz300043,sz300052,sz300113,sz300148,sz300226,sz300242,sz300295,sz300315,sz300418,sz300442,sz300459,sz300467,sz300494,sz300571,sz300766,sz300773,sz300785,sz300792,sz300921,sz300987,sz301001,sz301110,sz301171,sz301382,sz301428;I65:bj430090,bj430425,bj430564,bj830799,bj831832,bj832171,bj834415,bj835184,bj835207,bj835305,bj835508,bj835670,bj837092,bj837592,bj837748,bj838227,bj838924,bj839493,bj839680,bj839790,bj872953,sh600131,sh600225,sh600289,sh600406,sh600410,sh600446,sh600476,sh600536,sh600570,sh600571,sh600588,sh600602,sh600636,sh600654,sh600718,sh600728,sh600734,sh600756,sh600797,sh600845,sh600850,sh600880,sh600892,sh603039,sh603123,sh603138,sh603171,sh603189,sh603206,sh603220,sh603232,sh603322,sh603383,sh603421,sh603602,sh603636,sh603869,sh603887,sh603918,sh603927,sh603990,sh605398,sh688004,sh688018,sh688023,sh688030,sh688031,sh688038,sh688039,sh688045,sh688051,sh688052,sh688058,sh688060,sh688066,sh688078,sh688083,sh688088,sh688095,sh688099,sh688109,sh688111,sh688118,sh688130,sh688152,sh688168,sh688171,sh688173,sh688188,sh688191,sh688201,sh688206,sh688207,sh688225,sh688227,sh688228,sh688229,sh688232,sh688244,sh688246,sh688252,sh688256,sh688258,sh688259,sh688262,sh688279,sh688291,sh688292,sh688296,sh688316,sh688318,sh688325,sh688327,sh688343,sh688365,sh688368,sh688369,sh688391,sh688435,sh688479,sh688500,sh688507,sh688508,sh688521,sh688536,sh688561,sh688562,sh688568,sh688579,sh688588,sh688589,sh688590,sh688619,sh688631,sh688651,sh688657,sh688682,sh688699,sh688702,sh688777,sh688787,sh900901,sh900926,sz000004,sz000158,sz000409,sz000555,sz000682,sz000889,sz000948,sz000971,sz000997,sz002063,sz002065,sz002093,sz002123,sz002148,sz002153,sz002195,sz002197,sz002212,sz002230,sz002232,sz002253,sz002261,sz002268,sz002279,sz002298,sz002316,sz002322,sz002331,sz002362,sz002368,sz002373,sz002380,sz002401,sz002405,sz002410,sz002421,sz002439,sz002453,sz002474,sz002544,sz002602,sz002609,sz002642,sz002649,sz002657,sz002771,sz002777,sz002908,sz002912,sz002929,sz002970,sz002987,sz002990,sz003004,sz003005,sz003007,sz003029,sz300002,sz300010,sz300017,sz300020,sz300025,sz300036,sz300044,sz300047,sz300050,sz300051,sz300074,sz300075,sz300085,sz300096,sz300098,sz300150,sz300166,sz300167,sz300168,sz300170,sz300183,sz300188,sz300212,sz300229,sz300231,sz300235,sz300245,sz300248,sz300249,sz300250,sz300253,sz300264,sz300271,sz300275,sz300277,sz300287,sz300290,sz300292,sz300299,sz300300,sz300302,sz300311,sz300324,sz300333,sz300339,sz300344,sz300348,sz300349,sz300350,sz300352,sz300359,sz300365,sz300366,sz300369,sz300377,sz300378,sz300379,sz300380,sz300383,sz300386,sz300399,sz300419,sz300440,sz300448,sz300451,sz300454,sz300465,sz300468,sz300469,sz300493,sz300496,sz300508,sz300513,sz300518,sz300520,sz300523,sz300525,sz300532,sz300533,sz300541,sz300542,sz300550,sz300552,sz300556,sz300559,sz300560,sz300561,sz300578,sz300579,sz300588,sz300597,sz300598,sz300603,sz300605,sz300608,sz300609,sz300613,sz300624,sz300634,sz300645,sz300659,sz300663,sz300671,sz300674,sz300678,sz300682,sz300687,sz300730,sz300738,sz300768,sz300789,sz300799,sz300810,sz300830,sz300845,sz300846,sz300851,sz300872,sz300895,sz300925,sz300935,sz300941,sz300959,sz300996,sz301095,sz301117,sz301139,sz301153,sz301159,sz301162,sz301172,sz301178,sz301179,sz301185,sz301195,sz301197,sz301208,sz301213,sz301218,sz301221,sz301236,sz301248,sz301269,sz301270,sz301299,sz301302,sz301313,sz301315,sz301316,sz301337,sz301339,sz301378,sz301380,sz301396;J66:sh600000,sh600015,sh600016,sh600036,sh600830,sh600901,sh600908,sh600919,sh600926,sh600928,sh601009,sh601077,sh601128,sh601166,sh601169,sh601187,sh601229,sh601288,sh601328,sh601398,sh601528,sh601577,sh601658,sh601665,sh601818,sh601825,sh601838,sh601860,sh601916,sh601939,sh601963,sh601988,sh601997,sh601998,sh603323,sz000001,sz001227,sz002142,sz002807,sz002839,sz002936,sz002948,sz002958,sz002966;J67:sh600030,sh600061,sh600095,sh600109,sh600369,sh600621,sh600837,sh600906,sh600909,sh600918,sh600927,sh600958,sh600999,sh601059,sh601066,sh601099,sh601108,sh601136,sh601162,sh601198,sh601211,sh601236,sh601375,sh601377,sh601456,sh601555,sh601688,sh601696,sh601788,sh601878,sh601881,sh601901,sh601990,sh601995,sh603093,sz000166,sz000617,sz000686,sz000712,sz000728,sz000750,sz000776,sz000783,sz001236,sz002500,sz002670,sz002673,sz002736,sz002797,sz002926,sz002939,sz002945,sz002961,sz300059;J68:sh601318,sh601319,sh601336,sh601601,sh601628,sz000627;J69:sh600053,sh600120,sh600155,sh600318,sh600390,sh600643,sh600705,sh600816,sh601519,sz000416,sz000563,sz000567,sz000987,sz002423,sz002647,sz300033,sz300803;K70:sh600007,sh600048,sh600064,sh600067,sh600094,sh600162,sh600185,sh600208,sh600215,sh600223,sh600239,sh600246,sh600266,sh600322,sh600325,sh600340,sh600376,sh600383,sh600503,sh600510,sh600515,sh600533,sh600565,sh600604,sh600606,sh600620,sh600622,sh600638,sh600639,sh600641,sh600649,sh600657,sh600658,sh600663,sh600665,sh600675,sh600683,sh600684,sh600692,sh600708,sh600716,sh600736,sh600743,sh600748,sh600773,sh600791,sh600807,sh600823,sh600848,sh600854,sh600895,sh601155,sh601512,sh601588,sh603506,sh900902,sh900911,sh900928,sh900932,sh900940,sh900957,sz000002,sz000006,sz000011,sz000014,sz000029,sz000031,sz000036,sz000042,sz000046,sz000048,sz000069,sz000402,sz000514,sz000517,sz000558,sz000560,sz000608,sz000615,sz000620,sz000631,sz000656,sz000668,sz000718,sz000720,sz000736,sz000797,sz000838,sz000863,sz000886,sz000897,sz000926,sz000961,sz000965,sz001914,sz001979,sz002016,sz002146,sz002208,sz002244,sz002285,sz002305,sz002314,sz002377,sz002968,sz200011,sz200029,sz300917;L71:sh603300,sz000415;L72:bj834021,bj837242,sh600057,sh600113,sh600138,sh600462,sh600539,sh600790,sh600826,sh600861,sh601828,sh601888,sh603117,sh603569,sh603598,sh603648,sh603682,sh603729,sh603836,sh605168,sh900929,sz000056,sz000058,sz000061,sz000524,sz000785,sz000796,sz000861,sz000882,sz000917,sz001228,sz002010,sz002027,sz002115,sz002127,sz002177,sz002181,sz002183,sz002188,sz002210,sz002291,sz002344,sz002400,sz002485,sz002654,sz002707,sz002712,sz002769,sz002818,sz002878,sz002889,sz200056,sz200058,sz300058,sz300061,sz300063,sz300071,sz300269,sz300280,sz300612,sz300662,sz300688,sz300781,sz300795,sz300805,sz300947,sz301102,sz301169;M73:sh600645,sh600721,sh603127,sh603259,sh688046,sh688073,sh688105,sh688131,sh688133,sh688137,sh688179,sh688202,sh688222,sh688238,sh688265,sh688293,sh688621,sz300149,sz300347,sz300404,sz300759,sz301047,sz301080,sz301096,sz301230,sz301257,sz301333,sz301520;M74:bj831039,bj833427,bj833873,bj836208,bj836892,bj871753,bj873122,sh600052,sh600629,sh600868,sh601226,sh601965,sh603017,sh603018,sh603060,sh603126,sh603153,sh603183,sh603357,sh603458,sh603698,sh603776,sh603859,sh603860,sh603909,sh605167,sh688053,sh688248,sh688315,sh688334,sh688509,sz000068,sz000710,sz000779,sz002178,sz002776,sz002883,sz002949,sz002967,sz003008,sz003013,sz300012,sz300081,sz300215,sz300284,sz300384,sz300416,sz300492,sz300500,sz300564,sz300635,sz300668,sz300675,sz300676,sz300732,sz300746,sz300778,sz300797,sz300825,sz300826,sz300844,sz300887,sz300901,sz300928,sz300938,sz300949,sz300977,sz300983,sz300989,sz301024,sz301027,sz301038,sz301046,sz301058,sz301091,sz301115,sz301136,sz301167,sz301215,sz301228,sz301235,sz301289,sz301297,sz301306,sz301365,sz301390,sz301505;M75:sh688287,sz000504,sz003035;M76:;N77:bj832145,bj836263,sh600292,sh600323,sh600475,sh601200,sh601330,sh601827,sh603177,sh603200,sh603359,sh603568,sh603588,sh603603,sh603797,sh603903,sh605069,sh605081,sh688057,sh688069,sh688156,sh688178,sh688370,sh688466,sh688480,sh688485,sh688565,sh688679,sh688701,sz000005,sz000035,sz000546,sz000803,sz000826,sz000885,sz000890,sz000967,sz002034,sz002266,sz002310,sz002549,sz002573,sz002616,sz002717,sz002887,sz003027,sz300070,sz300103,sz300152,sz300172,sz300187,sz300190,sz300197,sz300262,sz300266,sz300355,sz300388,sz300422,sz300495,sz300614,sz300664,sz300692,sz300774,sz300816,sz300854,sz300864,sz300867,sz300899,sz300912,sz300929,sz300948,sz300958,sz300961,sz301049,sz301109,sz301127,sz301148,sz301203,sz301259,sz301305,sz301372,sz301519;N78:bj831370,sh600054,sh600576,sh600593,sh600706,sh600749,sh603099,sh603136,sh603199,sh900942,sz000430,sz000610,sz000809,sz000888,sz000978,sz001230,sz002033,sz002059,sz002159,sz002973,sz300815,sz300859,sz301175;O79:;O80:sz300736;O81:;P82:sh600455,sh600661,sh600730,sh603377,sh605098,sz000526,sz002607,sz002621,sz002659,sz003032,sz300192,sz300338;Q83:sh600327,sh600568,sh600763,sh603882,sz000516,sz002044,sz002173,sz002219,sz002524,sz002622,sz300015,sz300143,sz300244,sz301060,sz301103,sz301239,sz301267,sz301293;Q84:sz000616;R85:sh600229,sh600373,sh600551,sh600757,sh600825,sh601019,sh601098,sh601801,sh601811,sh601858,sh601900,sh601921,sh601928,sh601949,sh601999,sh603096,sh603230,sh603999,sh605577,sz000607,sz000719,sz000793,sz300364,sz300654,sz300788,sz301025,sz301052,sz301231;R86:sh600088,sh600715,sh600977,sh601595,sh603103,sh603721,sz000156,sz000665,sz000802,sz000892,sz001330,sz002343,sz002445,sz002739,sz002905,sz300027,sz300133,sz300182,sz300251,sz300291,sz300426,sz300528;R87:sh603466,sz000681,sz002699,sz300144,sz300413,sz300860;R88:sh600136,sh600158,sz002858;R89:;S90:sh600603,sh600673,sh600682,sh600766,sh600770,sh600805,sz000009,sz000532,sz000551,sz000571,sz000609,sz000833;";
		String[] hyArray = StringUtils.split(s, ";");
		for (int i = 0; i < hyArray.length; i++) {
			String[] array = StringUtils.split(hyArray[i], ":");
			String hyInfo = array[0];
			String[] codeArray = new String[0];
			if (array.length == 2) {
				codeArray = StringUtils.split(array[1], ",");
			}
//			System.out.println(hyInfo + ":" + codeArray.length);fsbb
			for (int x = 0; x < codeArray.length; x++) {
				String stockHy = stock2hy.get(codeArray[x]);
				stock2hy.put(codeArray[x], stockHy == null ? ("[" + hydm2name.get(hyInfo) + "]")
						: (stockHy + "," + ("[" + hydm2name.get(hyInfo) + "]")));
			}
		}
		int count = 0;
		for (String code : stock2hy.keySet()) {
			String stockHy = stock2hy.get(code);
			if (stockHy.contains(",")) {
				System.out.println("[" + (count++) + "]" + code + ":" + stockHy);
			}
		}
	}

	public final static void printAllStockByCategory(String[][] categories) throws Throwable {
		for (int i = 0; i < categories.length; i++) {
			String code = categories[i][0];
//			String name = hyinfos[i][1];
			JSONArray stockArray = new JSONArray();
			int count = getStockCount(code);
			Thread.sleep(200);
			int page = 1;
			while (count > 0) {
				JSONArray arr = getStocks(code, page);
				Thread.sleep(3000);
				for (int x = 0; x < arr.length(); x++) {
					stockArray.put(arr.getJSONObject(x));
				}
				page++;
				count = count - 100;
			}
			//
			System.out.print(code + ":");
			for (int j = 0; j < stockArray.length(); j++) {
				JSONObject stockInfo = stockArray.getJSONObject(j);
				System.out.print(stockInfo.getString("symbol"));
				if (j < stockArray.length() - 1) {
					System.out.print(",");
				}
			}
			System.out.println();
		}
	}

	private final static int getStockCount(String hydm) throws Throwable {
		StringBuffer buffer = new StringBuffer();
		HttpURLConnection connection = (HttpURLConnection) new URL(
				"https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeStockCount?node="
						+ hydm).openConnection();
		try {
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
			connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
			connection.setRequestProperty("Accept", "*/*");
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
		if (buffer.length() >= 3) {
			return Integer.parseInt(buffer.substring(1, buffer.length() - 1));
		} else {
			return 0;
		}
	}

	private final static JSONArray getStocks(String hydm, int page) throws Throwable {
		StringBuffer buffer = new StringBuffer();
		HttpURLConnection connection = (HttpURLConnection) new URL(
				"https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page="
						+ page + "&num=100&sort=symbol&asc=1&node=" + hydm + "&symbol=&_s_r_a=init").openConnection();
		try {
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
			connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
			connection.setRequestProperty("Accept", "*/*");
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
		return new JSONArray(buffer.toString());
	}

	private final static String[][] hyInfos = new String[][] { { "A01", "农业" }, { "A02", "林业" }, { "A03", "畜牧业" },
			{ "A04", "渔业" }, { "A05", "农、林、牧、渔服务业" }, { "B06", "煤炭开采和洗选业" }, { "B07", "石油和天然气开采业" },
			{ "B08", "黑色金属矿采选业" }, { "B09", "有色金属矿采选业" }, { "B10", "非金属矿采选业" }, { "B11", "开采辅助活动" }, { "B12", "其他采矿业" },
			{ "C13", "农副食品加工业" }, { "C14", "食品制造业" }, { "C15", "酒、饮料和精制茶制造业" }, { "C16", "烟草制品业" }, { "C17", "纺织业" },
			{ "C18", "纺织服装、服饰业" }, { "C19", "皮革、毛皮、羽毛及其制品和制鞋业" }, { "C20", "木材加工及木、竹、藤、棕、草制品业" }, { "C21", "家具制造业" },
			{ "C22", "造纸及纸制品业" }, { "C23", "印刷和记录媒介复制业" }, { "C24", "文教、工美、体育和娱乐用品制造业" }, { "C25", "石油加工、炼焦及核燃料加工业" },
			{ "C26", "化学原料及化学制品制造业" }, { "C27", "医药制造业" }, { "C28", "化学纤维制造业" }, { "C29", "橡胶和塑料制品业" },
			{ "C30", "非金属矿物制品业" }, { "C31", "黑色金属冶炼及压延加工业" }, { "C32", "有色金属冶炼和压延加工业" }, { "C33", "金属制品业" },
			{ "C34", "通用设备制造业" }, { "C35", "专用设备制造业" }, { "C36", "汽车制造业" }, { "C37", "铁路、船舶、航空航天和其它运输设备制造业" },
			{ "C38", "电气机械及器材制造业" }, { "C39", "计算机、通信和其他电子设备制造业" }, { "C40", "仪器仪表制造业" }, { "C41", "其他制造业" },
			{ "C42", "废弃资源综合利用业" }, { "C43", "金属制品、机械和设备" }, { "D44", "电力、热力生产和供应业" }, { "D45", "燃气生产和供应业" },
			{ "D46", "水的生产和供应业" }, { "E47", "房屋建筑业" }, { "E48", "土木工程建筑业" }, { "E49", "建筑安装业" },
			{ "E50", "建筑装饰和其他建筑业" }, { "F51", "批发业" }, { "F52", "零售业" }, { "G53", "铁路运输业" }, { "G54", "道路运输业" },
			{ "G55", "水上运输业" }, { "G56", "航空运输业" }, { "G57", "管道运输业" }, { "G58", "装卸搬运和运输代理业" }, { "G59", "仓储业" },
			{ "G60", "邮政业" }, { "H61", "住宿业" }, { "H62", "餐饮业" }, { "I63", "电信、广播电视和卫星传输服务" }, { "I64", "互联网和相关服务" },
			{ "I65", "软件和信息技术服务业" }, { "J66", "货币金融服务" }, { "J67", "资本市场服务" }, { "J68", "保险业" }, { "J69", "其他金融业" },
			{ "K70", "房地产业" }, { "L71", "租赁业" }, { "L72", "商务服务业" }, { "M73", "研究和试验发展" }, { "M74", "专业技术服务业" },
			{ "M75", "科技推广和应用服务业" }, { "M76", "水利管理业" }, { "N77", "生态保护和环境治理业" }, { "N78", "公共设施管理业" },
			{ "O79", "居民服务业" }, { "O80", "机动车、电子产品和日用产品修理业" }, { "O81", "其他服务业" }, { "P82", "教育" }, { "Q83", "卫生" },
			{ "Q84", "社会工作" }, { "R85", "新闻和出版业" }, { "R86", "广播、电视、电影和影视录音制作业" }, { "R87", "文化艺术业" }, { "R88", "体育" },
			{ "R89", "娱乐业" }, { "S90", "综合" } }; // hangye_Z

}

class KData {
	String day;
	BigDecimal closePrice;
	BigDecimal highPrice;
	BigDecimal volume;
}

class OrderInfo {
	String code;
	String openDay;
	String closeDay;
	BigDecimal openPrice;
	BigDecimal closePrice;
	BigDecimal volume;
	BigDecimal openIndexValue;
	BigDecimal closeIndexValue;
	int days;
}

class AssetInfo {
	String day;
	BigDecimal balanceValue; // 资金余额
	BigDecimal marketValue; // 股票市值
	BigDecimal contractValue; // 合约价值
}