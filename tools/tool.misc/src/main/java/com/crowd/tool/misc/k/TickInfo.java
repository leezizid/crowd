package com.crowd.tool.misc.k;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TickInfo {

	private String label;

	private long time;

	private BigDecimal lastPrice;

	private BigDecimal volume;

	private BigDecimal openInterest;

	private BigDecimal bidPrice1;

	private BigDecimal bidVolume1;

	private BigDecimal askPrice1;

	private BigDecimal askVolume1;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getOpenInterest() {
		return openInterest;
	}

	public void setOpenInterest(BigDecimal openInterest) {
		this.openInterest = openInterest;
	}

	public BigDecimal getBidPrice1() {
		return bidPrice1;
	}

	public void setBidPrice1(BigDecimal bidPrice1) {
		this.bidPrice1 = bidPrice1;
	}

	public BigDecimal getBidVolume1() {
		return bidVolume1;
	}

	public void setBidVolume1(BigDecimal bidVolume1) {
		this.bidVolume1 = bidVolume1;
	}

	public BigDecimal getAskPrice1() {
		return askPrice1;
	}

	public void setAskPrice1(BigDecimal askPrice1) {
		this.askPrice1 = askPrice1;
	}

	public BigDecimal getAskVolume1() {
		return askVolume1;
	}

	public void setAskVolume1(BigDecimal askVolume1) {
		this.askVolume1 = askVolume1;
	}

	public void writeToStream(DataOutput dout, int priceScale) throws IOException {
		dout.writeLong(time);
		dout.writeFloat(lastPrice.setScale(priceScale, RoundingMode.HALF_UP).floatValue());
		dout.writeInt(volume.intValue());
		dout.writeInt(openInterest.intValue());
		dout.writeFloat(bidPrice1.setScale(priceScale, RoundingMode.HALF_UP).floatValue());
		dout.writeInt(bidVolume1.intValue());
		dout.writeFloat(askPrice1.setScale(priceScale, RoundingMode.HALF_UP).floatValue());
		dout.writeInt(askVolume1.intValue());
	}

	public void readFromStream(DataInput din) throws IOException {
		this.time = din.readLong();
		this.lastPrice = new BigDecimal(din.readFloat());
		this.volume = new BigDecimal(din.readInt());
		this.openInterest = new BigDecimal(din.readInt());
		this.bidPrice1 = new BigDecimal(din.readFloat());
		this.bidVolume1 = new BigDecimal(din.readInt());
		this.askPrice1 = new BigDecimal(din.readFloat());
		this.askVolume1 = new BigDecimal(din.readInt());
	}

//	public String toString(int priceScale) {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append(label);
//		buffer.append(",");
//		buffer.append(time);
//		buffer.append(",");
//		buffer.append(lastPrice.setScale(priceScale, RoundingMode.HALF_UP));
//		buffer.append(",");
////		buffer.append(tickInfo.getHighestPrice().setScale(priceScale, RoundingMode.HALF_UP));
////		buffer.append(",");
////		buffer.append(tickInfo.getLowestPrice().setScale(priceScale, RoundingMode.HALF_UP));
////		buffer.append(",");
//		buffer.append(volume.intValue());
//		buffer.append(",");
////		buffer.append(tickInfo.getValue().setScale(2, RoundingMode.HALF_UP));
////		buffer.append(",");
//		buffer.append(openInterest.intValue());
//		buffer.append(",");
//		buffer.append(bidPrice1.setScale(priceScale, RoundingMode.HALF_UP));
//		buffer.append(",");
//		buffer.append(bidVolume1.intValue());
//		buffer.append(",");
//		buffer.append(askPrice1.setScale(priceScale, RoundingMode.HALF_UP));
//		buffer.append(",");
//		buffer.append(askVolume1.intValue());
//		if (bidPrice2 != null) {
//			buffer.append(",");
//			buffer.append(bidPrice2.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(bidVolume2.intValue());
//			buffer.append(",");
//			buffer.append(askPrice2.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(askVolume2.intValue());
//			buffer.append(",");
//			buffer.append(bidPrice3.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(bidVolume3.intValue());
//			buffer.append(",");
//			buffer.append(askPrice3.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(askVolume3.intValue());
//			buffer.append(",");
//			buffer.append(bidPrice4.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(bidVolume4.intValue());
//			buffer.append(",");
//			buffer.append(askPrice4.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(askVolume4.intValue());
//			buffer.append(",");
//			buffer.append(bidPrice5.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(bidVolume5.intValue());
//			buffer.append(",");
//			buffer.append(askPrice5.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(askVolume5.intValue());
//		}
//		return buffer.toString();
//	}
}
