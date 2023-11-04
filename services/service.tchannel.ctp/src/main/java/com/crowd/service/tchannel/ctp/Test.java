package com.crowd.service.tchannel.ctp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.crowd.tool.misc.k.HistoryData;

public class Test {

	public final static void main(String[] args) throws Throwable {
		System.setProperty("HistoryDataDir", "Z:\\MD");
		String[][] data1 = HistoryData.readKLineData("SHFE.hc", "5m", "2023-10-21", "2023-10-31");
		String[][] data2 = HistoryData.readKLineData("SHFE.rb", "5m", "2023-10-21", "2023-10-31");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int cursor1 = 0;
		int cursor2 = 0;
		double maxPrice = 0;
		double minPrice = Float.MAX_VALUE;
		double avgPrice = 0;
		double priceSum = 0;
		int count = 0;
		StringBuffer buffer1 = new StringBuffer();
		StringBuffer buffer2 = new StringBuffer();
		while (cursor1 < data1.length && cursor2 < data2.length) {
			String[] dataArray1 = data1[cursor1];
			String[] dataArray2 = data2[cursor2];
			if (dataArray1[0].equals(dataArray2[0])) {
				String date = sdf.format(new Date(Long.parseLong(dataArray1[0])));
				float price1 = Float.parseFloat(dataArray1[3]);
				float price2 = Float.parseFloat(dataArray2[4]);
				double price = new BigDecimal(price1 - price2).setScale(4, RoundingMode.HALF_UP).doubleValue();
				priceSum += price;
//				System.out.println(date + "	" + price);
				buffer1.append("'" + date + "',");
				buffer2.append("" + price + ",");
				maxPrice = Math.max(maxPrice, price);
				minPrice = Math.min(minPrice, price);
				cursor1++;
				cursor2++;
				count++;
			} else if (dataArray1[0].compareTo(dataArray2[0]) < 0) {
				cursor1++;
			} else if (dataArray1[0].compareTo(dataArray2[0]) > 0) {
				cursor2++;
			}
		}
		System.out.println(buffer1.toString());
		System.out.println(buffer2.toString());
		avgPrice = priceSum / count;
		System.out.println("avgPrice:" + avgPrice + ",maxPrice:" + maxPrice + ",minPrice:" + minPrice);
	}

}
