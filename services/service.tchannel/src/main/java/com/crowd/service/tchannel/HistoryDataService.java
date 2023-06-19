package com.crowd.service.tchannel;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.crowd.tool.misc.k.TickData;
import com.crowd.tool.misc.zb.EMA;

public class HistoryDataService implements CrowdService {

	@Override
	public void init(CrowdInitContext context) throws Throwable {

	}
	
	@Override
	public void postInit(CrowdInitContext context) throws Throwable {
		
	}


	@Override
	public String getName() {
		return "history-data";
	}

	@CrowdMethod
	public void dayKLine(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String symbol = input.getString("symbol");
			String startDay = input.getString("startDay");
			String endDay = input.getString("endDay");
			String[] tradeDays = TradeDays.getTradeDayList(startDay, endDay);
			String[][] dataList = HistoryData.readKLineData(symbol, "1d", startDay, endDay);
			Map<String, JSONArray> datas = new HashMap<String, JSONArray>();
			for (int i = 0; i < dataList.length; i++) {
				JSONArray data = new JSONArray();
				String day = sdf.format(new Date(Long.parseLong(dataList[i][0]))); 
				data.put(dataList[i][0]);
				data.put(day);
				data.put(Float.parseFloat(dataList[i][1]));
				data.put(Float.parseFloat(dataList[i][2]));
				data.put(Float.parseFloat(dataList[i][3]));
				data.put(Float.parseFloat(dataList[i][4]));
				data.put(Float.parseFloat(dataList[i][5]));
				data.put(Integer.parseInt(dataList[i][6]));
				data.put(Integer.parseInt(dataList[i][8]));
				datas.put(day, data);
			}
			JSONArray klineArray = new JSONArray();
			for (String day : tradeDays) {
				if (datas.containsKey(day)) {
					klineArray.put(datas.get(day));
				} else {
					// XXX：由于K线数据可能不完整
					JSONArray data = new JSONArray();
					data.put(sdf.parse(day).getTime());
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

	
	
	@CrowdMethod
	public void contextTickChartData(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String symbol = input.getString("symbol");
		long markTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(input.getString("time")).getTime();
		String tradeDay = TradeDays.matchTradeDay(markTime);
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SSS");
		JSONArray tickArray = new JSONArray();
		byte[] content = HistoryData.readTradeDayTickData(symbol, tradeDay);
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(content));
		int count = content.length / 36;
		int prevVolume = 0;
		EMA ema5m = new EMA(600);
//		EMA ema1m = new EMA(120);
		for (int i = 0; i < count; i++) {
			TickData tickData = new TickData();
			tickData.readFromStream(dis);
			if (tickData.getTime() < markTime - 25 * 60 * 1000) {
				continue;
			}
			if (tickData.getTime() > markTime + 25 * 60 * 1000) {
				break;
			}
			JSONArray dataArray = new JSONArray();
			dataArray.put(sdf.format(tickData.getTime()));
			dataArray.put(tickData.getLastPrice().doubleValue());
			dataArray.put(prevVolume == 0 ? 0 : tickData.getVolume().intValue() - prevVolume); 
			dataArray.put(ema5m.push(tickData.getLastPrice().doubleValue()));
//			dataArray.put(ema1m.push(tickInfo.getLastPrice().doubleValue()));
			tickArray.put(dataArray);
			//
			prevVolume = tickData.getVolume().intValue();
		}
		output.put("data", tickArray);
	}
	
}
