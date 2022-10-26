package com.crowd.tool.misc.k;

import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.crowd.tool.misc.DateHelper;

public class KLineData {

	private BigDecimal openPrice;

	private BigDecimal closePrice;

	private BigDecimal highPrice;

	private BigDecimal lowPrice;

	private BigDecimal avgPrice;

	private BigDecimal volumn;

	private BigDecimal openInterest; // 收盘开仓量

	private int count;

	private long baseTime;

	private int interval;

	private int priceScale;

	private boolean close;

	private String label;

	public KLineData(int interval, int priceScale, long time, BigDecimal price, BigDecimal volumn,
			BigDecimal openInterest) {
		this.interval = interval;
		this.priceScale = priceScale;
		this.baseTime = (time / (interval * 1000)) * interval * 1000;
		this.openPrice = price;
		this.closePrice = price;
		this.highPrice = price;
		this.lowPrice = price;
		this.avgPrice = price;
		this.volumn = volumn;
		this.openInterest = openInterest;
		this.count = 1;
		this.label = DateHelper.dateTime2String(new Date(this.baseTime));
	}

	public KLineData(int priceScale, long baseTime, BigDecimal price, BigDecimal volumn, BigDecimal openInterest) {
		this.priceScale = priceScale;
		this.openPrice = price;
		this.closePrice = price;
		this.highPrice = price;
		this.lowPrice = price;
		this.avgPrice = price;
		this.volumn = volumn;
		this.count = 1;
		this.openInterest = openInterest;
		this.baseTime = baseTime;
		this.label = DateHelper.dateTime2String(new Date(this.baseTime));
	}

	public boolean isIn(long time) {
		return this.baseTime == (time / (interval * 1000)) * interval * 1000 || this.baseTime == time - interval * 1000;
	}

	public void put(BigDecimal price, BigDecimal volumn, BigDecimal openInterest) {
		this.closePrice = price;
		this.highPrice = this.highPrice.max(price);
		this.lowPrice = this.lowPrice.min(price);
		this.avgPrice = this.avgPrice.multiply(new BigDecimal(count)).add(price).divide(new BigDecimal(count + 1),
				priceScale, RoundingMode.HALF_UP);
		this.volumn = this.volumn.add(volumn);
		this.openInterest = openInterest;
		this.count++;
	}

	public void finish() {
		this.close = true;
	}

//	public void setBaseTimeAndLabel(String label, long baseTime) {
//		this.label = label;
//		this.baseTime = baseTime;
//	}

	public long getBaseTime() {
		return this.baseTime;
	}

	public BigDecimal getOpenPrice() {
		return this.openPrice;
	}

	public BigDecimal getClosePrice() {
		return this.closePrice;
	}

	public BigDecimal getHighPrice() {
		return this.highPrice;
	}

	public BigDecimal getLowPrice() {
		return this.lowPrice;
	}

	public BigDecimal getAvgPrice() {
		return this.avgPrice;
	}

	public BigDecimal getVolumn() {
		return this.volumn;
	}

	public BigDecimal getOpenInterest() {
		return this.openInterest;
	}

	public int getCount() {
		return this.count;
	}

	public boolean isClose() {
		return close;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(this.baseTime);
		buffer.append(",\"");
		buffer.append(this.label);
		buffer.append("\",");
//		buffer.append(this.count);
//		buffer.append(",");
		buffer.append(this.getOpenPrice());
		buffer.append(",");
		buffer.append(this.getClosePrice());
		buffer.append(",");
		buffer.append(this.getHighPrice());
		buffer.append(",");
		buffer.append(this.getLowPrice());
		buffer.append(",");
		buffer.append(this.getAvgPrice());
		buffer.append(",");
		buffer.append(this.getVolumn());
		buffer.append(",");
		buffer.append(this.closePrice.compareTo(this.openPrice) >= 0 ? 1 : 0);
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * 写入bytes，一共32字节
	 * 
	 * @param output
	 * @throws IOException
	 */
	public void writeToStream(DataOutput output) throws IOException {
		output.writeLong(this.baseTime + (this.closePrice.compareTo(this.openPrice) >= 0 ? 1 : 0));
		output.writeFloat(this.getOpenPrice().floatValue());
		output.writeFloat(this.getClosePrice().floatValue());
		output.writeFloat(this.getHighPrice().floatValue());
		output.writeFloat(this.getLowPrice().floatValue());
		output.writeFloat(this.getAvgPrice().floatValue());
		output.writeInt(this.getVolumn().intValue());
		output.writeInt(this.getOpenInterest().intValue());
	}

}
