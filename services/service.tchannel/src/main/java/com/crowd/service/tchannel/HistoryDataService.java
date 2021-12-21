package com.crowd.service.tchannel;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.CrowdInitContext;
import com.crowd.service.base.CrowdMethod;
import com.crowd.service.base.CrowdService;
import com.crowd.tool.misc.TradeDays;

public class HistoryDataService implements CrowdService {

	private final static String HistoryDataDir;

	static {
		HistoryDataDir = System.getProperty("HistoryDataDir");
	}

	@Override
	public void init(CrowdInitContext context) throws Throwable {

	}

	@Override
	public String getName() {
		return "history-data";
	}

	@CrowdMethod
	public void dayKLine(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		try {
			String symbol = input.getString("symbol");
			String startDay = input.getString("startDay");
			String endDay = input.getString("endDay");
			String[] tradeDays = TradeDays.getTradeDayList(startDay, endDay);
			File dataFile = new File(
					HistoryDataDir + File.separator + symbol + File.separator + "kline" + File.separator + "1d.js");
			RandomAccessFile raf = new RandomAccessFile(dataFile, "r");
			byte[] content = new byte[(int) dataFile.length()];
			raf.read(content);
			raf.close();
			JSONArray dataArray = new JSONArray(new String(content));
			Map<String, JSONArray> datas = new HashMap<String, JSONArray>();
			for (int i = 0; i < dataArray.length(); i++) {
				JSONArray arr = dataArray.getJSONArray(i);
				String day = arr.getString(1);
				datas.put(day, arr);
			}
			JSONArray klineArray = new JSONArray();
			for (String day : tradeDays) {
				if (datas.containsKey(day)) {
					klineArray.put(datas.get(day));
				} else {
					// XXX：由于K线数据可能不完整
					JSONArray data = new JSONArray();
					data.put(new SimpleDateFormat("yyyy/MM/dd").parse(day).getTime());
					data.put(day);
					data.put("-");
					data.put("-");
					data.put("-");
					data.put("-");
					data.put("-");
					data.put("-");
					data.put("-");
					klineArray.put(data);
				}
			}
			output.put("content", klineArray.toString());
		} catch (Throwable t) {
			throw new IllegalStateException("获取历史数据失败");
		}
	}

	@CrowdMethod
	public void dayTimeLine(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		try {
			String symbol = input.getString("symbol");
			String day = input.getString("day");
			day = day.replace('/', '-');
			File dataFile = new File(HistoryDataDir + File.separator + symbol + File.separator + "daytime"
					+ File.separator + day + ".js");
			RandomAccessFile raf = new RandomAccessFile(dataFile, "r");
			byte[] content = new byte[(int) dataFile.length()];
			raf.read(content);
			raf.close();
			output.put("content", new String(content));
		} catch (Throwable t) {
			throw new IllegalStateException("获取历史数据失败");
		}
	}

}
