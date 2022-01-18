package com.crowd.service.tchannel;

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
import com.crowd.tool.misc.k.HistoryData;

public class HistoryDataService implements CrowdService {

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
			String[] dataList = HistoryData.readKLineData(symbol, "1d", startDay, endDay);
			Map<String, JSONArray> datas = new HashMap<String, JSONArray>();
			for (int i = 0; i < dataList.length; i++) {
				JSONArray data = new JSONArray(dataList[i]);
				String day = data.getString(1);
				datas.put(day, data);
			}
			JSONArray klineArray = new JSONArray();
			for (String day : tradeDays) {
				if (datas.containsKey(day)) {
					klineArray.put(datas.get(day));
				} else {
					// XXX：由于K线数据可能不完整
					JSONArray data = new JSONArray();
					data.put(new SimpleDateFormat("yyyy-MM-dd").parse(day).getTime());
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
			output.put("content", HistoryData.readDaytimeData(symbol, day));
		} catch (Throwable t) {
			throw new IllegalStateException("获取历史数据失败");
		}
	}

}
