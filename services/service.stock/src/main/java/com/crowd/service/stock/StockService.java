package com.crowd.service.stock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;

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
				JSONObject o = new JSONObject();
				o.put("key", code);
				o.put("code", code);
				o.put("name", info[0]);
				o.put("buyDate", "");
				array.put(o);
				context.save("pool.json", array.toString(4));
				isOk = true;
				output.put("stock", o);
				output.put("stocks", array);
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
		int days  = input.getInt("days");
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
		if(targetData.length() < crowdData.length()) {
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

	private JSONArray quoteKLineData(String code, int periodLen) throws Throwable {
		StringBuffer buffer = new StringBuffer();
		HttpURLConnection connection = (HttpURLConnection) new URL(
				"https://proxy.finance.qq.com/ifzqgtimg/appstock/app/newfqkline/get?_var=kline_dayqfq&param=" + code
						+ ",day,,," + periodLen + ",qfq").openConnection();
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

}
