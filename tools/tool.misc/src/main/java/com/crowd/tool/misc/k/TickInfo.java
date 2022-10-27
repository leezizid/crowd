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

	private BigDecimal volumn;

	private BigDecimal openInterest;

	private BigDecimal bidPrice1;

	private BigDecimal bidVolumn1;

	private BigDecimal askPrice1;

	private BigDecimal askVolumn1;

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

	public BigDecimal getVolumn() {
		return volumn;
	}

	public void setVolumn(BigDecimal volumn) {
		this.volumn = volumn;
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

	public BigDecimal getBidVolumn1() {
		return bidVolumn1;
	}

	public void setBidVolumn1(BigDecimal bidVolumn1) {
		this.bidVolumn1 = bidVolumn1;
	}

	public BigDecimal getAskPrice1() {
		return askPrice1;
	}

	public void setAskPrice1(BigDecimal askPrice1) {
		this.askPrice1 = askPrice1;
	}

	public BigDecimal getAskVolumn1() {
		return askVolumn1;
	}

	public void setAskVolumn1(BigDecimal askVolumn1) {
		this.askVolumn1 = askVolumn1;
	}

	public void writeToStream(DataOutput dout, int priceScale) throws IOException {
		dout.writeLong(time);
		dout.writeFloat(lastPrice.setScale(priceScale, RoundingMode.HALF_UP).floatValue());
		dout.writeInt(volumn.intValue());
		dout.writeInt(openInterest.intValue());
		dout.writeFloat(bidPrice1.setScale(priceScale, RoundingMode.HALF_UP).floatValue());
		dout.writeInt(bidVolumn1.intValue());
		dout.writeFloat(askPrice1.setScale(priceScale, RoundingMode.HALF_UP).floatValue());
		dout.writeInt(askVolumn1.intValue());
	}

	public void readFromStream(DataInput din) throws IOException {
		this.time = din.readLong();
		this.lastPrice = new BigDecimal(din.readFloat());
		this.volumn = new BigDecimal(din.readInt());
		this.openInterest = new BigDecimal(din.readInt());
		this.bidPrice1 = new BigDecimal(din.readFloat());
		this.bidVolumn1 = new BigDecimal(din.readInt());
		this.askPrice1 = new BigDecimal(din.readFloat());
		this.askVolumn1 = new BigDecimal(din.readInt());
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
//		buffer.append(volumn.intValue());
//		buffer.append(",");
////		buffer.append(tickInfo.getValue().setScale(2, RoundingMode.HALF_UP));
////		buffer.append(",");
//		buffer.append(openInterest.intValue());
//		buffer.append(",");
//		buffer.append(bidPrice1.setScale(priceScale, RoundingMode.HALF_UP));
//		buffer.append(",");
//		buffer.append(bidVolumn1.intValue());
//		buffer.append(",");
//		buffer.append(askPrice1.setScale(priceScale, RoundingMode.HALF_UP));
//		buffer.append(",");
//		buffer.append(askVolumn1.intValue());
//		if (bidPrice2 != null) {
//			buffer.append(",");
//			buffer.append(bidPrice2.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(bidVolumn2.intValue());
//			buffer.append(",");
//			buffer.append(askPrice2.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(askVolumn2.intValue());
//			buffer.append(",");
//			buffer.append(bidPrice3.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(bidVolumn3.intValue());
//			buffer.append(",");
//			buffer.append(askPrice3.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(askVolumn3.intValue());
//			buffer.append(",");
//			buffer.append(bidPrice4.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(bidVolumn4.intValue());
//			buffer.append(",");
//			buffer.append(askPrice4.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(askVolumn4.intValue());
//			buffer.append(",");
//			buffer.append(bidPrice5.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(bidVolumn5.intValue());
//			buffer.append(",");
//			buffer.append(askPrice5.setScale(priceScale, RoundingMode.HALF_UP));
//			buffer.append(",");
//			buffer.append(askVolumn5.intValue());
//		}
//		return buffer.toString();
//	}
}
