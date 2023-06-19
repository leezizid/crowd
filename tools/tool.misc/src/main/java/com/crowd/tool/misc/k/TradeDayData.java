package com.crowd.tool.misc.k;

import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.crowd.tool.misc.ProductDefine;
import com.crowd.tool.misc.TradeDays;

public class TradeDayData {

	private List<TickData> _tickList = new ArrayList<TickData>();

	private List<KLineData> _1mKLineDataList = new ArrayList<KLineData>();

	private List<KLineData> _5mKLineDataList = new ArrayList<KLineData>();

	private List<KLineData> _15mKLineDataList = new ArrayList<KLineData>();

	private List<KLineData> _1hKLineDataList = new ArrayList<KLineData>();

	private List<KLineData> _4hKLineDataList = new ArrayList<KLineData>();

	private KLineData _1dKLineData;

	private int priceScale;

	private String tradeDay;

	private long tradeDayTime;

	private BigDecimal lastTickVolume = BigDecimal.ZERO;

	private ProductDefine productDefine;

	public TradeDayData(String tradeDay, ProductDefine productDefine) {
		this.productDefine = productDefine;
		this.tradeDay = tradeDay;
		this.priceScale = productDefine.getPriceScale();
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

	public final void onTick(TickData tickData) {
		_tickList.add(tickData);
		long time = tickData.getTime();
		BigDecimal price = tickData.getLastPrice();
		BigDecimal volume = tickData.getVolume().subtract(lastTickVolume);
		BigDecimal openInterest = tickData.getOpenInterest();
		//
		pushKLineData(_1mKLineDataList, time, price, volume, openInterest, 60);
		pushKLineData(_5mKLineDataList, time, price, volume, openInterest, 60 * 5);
		pushKLineData(_15mKLineDataList, time, price, volume, openInterest, 60 * 15);
		pushKLineData(_1hKLineDataList, time, price, volume, openInterest, 60 * 60);
		pushKLineData(_4hKLineDataList, time, price, volume, openInterest, 60 * 60 * 4);
		if (_1dKLineData == null) {
			_1dKLineData = new KLineData(priceScale, TradeDays.getTradeDayTime(tradeDay), price, volume, openInterest);
		} else {
			_1dKLineData.put(price, volume, openInterest);
		}
		//
		lastTickVolume = tickData.getVolume();
	}

	private final void pushKLineData(List<KLineData> dataList, long time, BigDecimal price, BigDecimal volume,
			BigDecimal openInterest, int interval) {
		if (dataList.size() > 0) {
			KLineData klineData = dataList.get(dataList.size() - 1);
			if (klineData.isIn(time)) {
				klineData.put(price, volume, openInterest);
				return;
			} else {
				klineData.finish();
			}
		}
		//
		KLineData newData = new KLineData(interval, priceScale, time, price, volume, openInterest);
		// 判断是否有缺失数据，补全缺失的K线（仅处理小于30分钟的K线）
		if (interval < 60 * 30) {
			long t = this.productDefine.afterMarketPhase(newData.getBaseTime());
			if (t > 0) {
				long prevKlineTime = 0;
				if(dataList.size() > 0) {
					prevKlineTime = dataList.get(dataList.size() - 1).getBaseTime();
				}
				if (newData.getBaseTime() - prevKlineTime > t * 1000) {
					prevKlineTime = newData.getBaseTime() - t * 1000;
					dataList.add(new KLineData(interval, priceScale, prevKlineTime, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO));
//					System.out.println(dataList.get(dataList.size() - 1));
				}
				while (newData.getBaseTime() - prevKlineTime > interval * 1000) {
					prevKlineTime = prevKlineTime + interval * 1000;
					dataList.add(new KLineData(interval, priceScale, prevKlineTime, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO));
//					System.out.println(dataList.get(dataList.size() - 1));
				}
			}
		}
		dataList.add(newData);
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
//			TickData tickData = _tickList.get(i);
//			output.write((tickData.toString(priceScale) + "\r\n").getBytes());
//		}
//	}

	public void writeTickDataToStream(DataOutput output) throws IOException {
		for (int i = 0; i < _tickList.size(); i++) {
			TickData tickData = _tickList.get(i);
			tickData.writeToStream(output, priceScale);
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
