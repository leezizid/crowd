package com.crowd.tool.misc.k;

import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.crowd.tool.misc.TradeDays;

public class TradeDayData {

	private List<TickInfo> _tickList = new ArrayList<TickInfo>();

	private List<KLineData> _1mKLineDataList = new ArrayList<KLineData>();

	private List<KLineData> _5mKLineDataList = new ArrayList<KLineData>();

	private List<KLineData> _15mKLineDataList = new ArrayList<KLineData>();

	private List<KLineData> _1hKLineDataList = new ArrayList<KLineData>();

	private List<KLineData> _4hKLineDataList = new ArrayList<KLineData>();

	private KLineData _1dKLineData;

	private int priceScale;

	private String tradeDay;

	private long tradeDayTime;

	private BigDecimal lastTickVolumn = BigDecimal.ZERO;

	public TradeDayData(String tradeDay, int priceScale) {
		this.tradeDay = tradeDay;
		this.priceScale = priceScale;
		this.tradeDayTime = TradeDays.getTradeDayTime(tradeDay);
	}

	public String getTradeDay() {
		return tradeDay;
	}

	public long getTradeDayTime() {
		return tradeDayTime;
	}

//	public void setTradeDayTime(long tradeDayTime) {
//		this.tradeDayTime = tradeDayTime;
//	}

//	public KLineData get1dKLineData() {
//		return _1dKLineData;
//	}

	public final void onTick(TickInfo tickInfo) {
		_tickList.add(tickInfo);
		long time = tickInfo.getTime();
		BigDecimal price = tickInfo.getLastPrice();
		BigDecimal volumn = tickInfo.getVolumn().subtract(lastTickVolumn);
		//
		pushKLineData(_1mKLineDataList, time, price, volumn, 60);
		pushKLineData(_5mKLineDataList, time, price, volumn, 60 * 5);
		pushKLineData(_15mKLineDataList, time, price, volumn, 60 * 15);
		pushKLineData(_1hKLineDataList, time, price, volumn, 60 * 60);
		pushKLineData(_4hKLineDataList, time, price, volumn, 60 * 60 * 4);
		if (_1dKLineData == null) {
			_1dKLineData = new KLineData(priceScale, TradeDays.getTradeDayTime(tradeDay), price, volumn);
		} else {
			_1dKLineData.put(price, volumn);
		}
		//
		lastTickVolumn = tickInfo.getVolumn();
	}

	private final void pushKLineData(List<KLineData> dataList, long time, BigDecimal price, BigDecimal volumn,
			int interval) {
		if (dataList.size() > 0) {
			KLineData klineData = dataList.get(dataList.size() - 1);
			if (klineData.isIn(time)) {
				klineData.put(price, volumn);
				return;
			} else {
				klineData.finish();
			}
		}
		//
		dataList.add(new KLineData(interval, priceScale, time, price, volumn));
	}

	public String get1mKLineDataString() {
		return toString(_1mKLineDataList);
	}
//
//	public String get5mKLineDataString() {
//		return toString(_5mKLineDataList);
//	}
//
//	public String get1hKLineDataString() {
//		return toString(_1hKLineDataList);
//	}

//	public String get1dKLineDataString() {
//		_1dKLineData.setBaseTimeAndLabel(DateHelper.date2String(new Date(this.tradeDayTime)), this.tradeDayTime);
//		return _1dKLineData.toString();
//	}

//	public void writeTickDataToCsv(DataOutput output) throws IOException {
//		for (int i = 0; i < _tickList.size(); i++) {
//			TickInfo tickInfo = _tickList.get(i);
//			output.write((tickInfo.toString(priceScale) + "\r\n").getBytes());
//		}
//	}

	public void writeTickDataToStream(DataOutput output) throws IOException {
		for (int i = 0; i < _tickList.size(); i++) {
			TickInfo tickInfo = _tickList.get(i);
			tickInfo.writeToStream(output, priceScale);
		}
	}

	public void write5mToStream(DataOutput output) throws IOException {
		for (int i = 0; i < _5mKLineDataList.size(); i++) {
			KLineData klineData = _5mKLineDataList.get(i);
			klineData.writeToStream(output);
		}
	}

	public void write15mToStream(DataOutput output) throws IOException {
		for (int i = 0; i < _15mKLineDataList.size(); i++) {
			KLineData klineData = _15mKLineDataList.get(i);
			klineData.writeToStream(output);
		}
	}

	public void write1hToStream(DataOutput output) throws IOException {
		for (int i = 0; i < _1hKLineDataList.size(); i++) {
			KLineData klineData = _1hKLineDataList.get(i);
			klineData.writeToStream(output);
		}
	}

	public void write4hToStream(DataOutput output) throws IOException {
		for (int i = 0; i < _4hKLineDataList.size(); i++) {
			KLineData klineData = _4hKLineDataList.get(i);
			klineData.writeToStream(output);
		}
	}

	public void write1dToStream(DataOutput output) throws IOException {
		_1dKLineData.writeToStream(output);
	}

//	public String toString() {
//		StringBuffer stringBuffer = new StringBuffer();
//		stringBuffer.append("_1mKLineData =" + toString(_1mKLineDataList));
//		stringBuffer.append("\r\n");
//		stringBuffer.append("_5mKLineDataList =" + toString(_5mKLineDataList));
//		stringBuffer.append("\r\n");
//		stringBuffer.append("_15mKLineDataList =" + toString(_15mKLineDataList));
//		stringBuffer.append("\r\n");
//		stringBuffer.append("_1hKLineDataList =" + toString(_1hKLineDataList));
//		stringBuffer.append("\r\n");
//		stringBuffer.append("_4hKLineDataList =" + toString(_4hKLineDataList));
//		stringBuffer.append("\r\n");
//		stringBuffer.append("_1dKLineData =" + get1dKLineDataString());
//		stringBuffer.append("\r\n");
//		return stringBuffer.toString();
//	}

	public String toString(List<KLineData> dataList) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("[\r\n");
		for (int i = 0; i < dataList.size(); i++) {
			stringBuffer.append("	");
			KLineData klineData = dataList.get(i);
			stringBuffer.append(klineData.toString());
			if (i < dataList.size() - 1) {
				stringBuffer.append(",");
			}
			stringBuffer.append("\r\n");
		}
		stringBuffer.append("]");
		return stringBuffer.toString();
	}

}
