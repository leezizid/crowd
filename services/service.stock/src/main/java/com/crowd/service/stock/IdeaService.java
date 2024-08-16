package com.crowd.service.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.CrowdInitContext;
import com.crowd.service.base.CrowdMethod;
import com.crowd.service.base.CrowdService;
import com.crowd.tool.misc.k.HistoryData;

public class IdeaService implements CrowdService {

	@Override
	public String getName() {
		return "idea";
	}

	@Override
	public void init(CrowdInitContext context) throws Throwable {
	}

	@Override
	public void postInit(CrowdInitContext context) throws Throwable {
	}

	@CrowdMethod
	public void startTest(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		JSONArray dataSeries = new JSONArray();

		//
		System.setProperty("HistoryDataDir", "Z:\\MD");
		String[][] data1 = HistoryData.readKLineData("SHFE.hc", "1d", "2020-01-01", "2023-10-31");
		String[][] data2 = HistoryData.readKLineData("SHFE.rb", "1d", "2020-01-01", "2023-10-31");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int cursor1 = 0;
		int cursor2 = 0;
		while (cursor1 < data1.length && cursor2 < data2.length) {
			String[] dataArray1 = data1[cursor1];
			String[] dataArray2 = data2[cursor2];
			if (dataArray1[0].equals(dataArray2[0])) {
				String date = sdf.format(new Date(Long.parseLong(dataArray1[0])));
				float price1 = Float.parseFloat(dataArray1[2]);
				float price2 = Float.parseFloat(dataArray2[2]);
				double price = new BigDecimal(price2 - price1).setScale(4, RoundingMode.HALF_UP).doubleValue();
				cursor1++;
				cursor2++;
				//
				JSONArray dataArray = new JSONArray();
				dataArray.put(date);
				dataArray.put(price1);
				dataArray.put(price2);
				dataArray.put(price);
				dataSeries.put(dataArray);
			} else if (dataArray1[0].compareTo(dataArray2[0]) < 0) {
				cursor1++;
			} else if (dataArray1[0].compareTo(dataArray2[0]) > 0) {
				cursor2++;
			}
		}

		//
		output.put("dataSeries", dataSeries);
	}

}
