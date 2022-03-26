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

	private BigDecimal highestPrice;

	private BigDecimal lowestPrice;

	private BigDecimal volumn;

	private BigDecimal value;

	private BigDecimal openInterest;

	private BigDecimal bidPrice1;

	private BigDecimal bidVolumn1;

	private BigDecimal askPrice1;

	private BigDecimal askVolumn1;

	private BigDecimal bidPrice2;

	private BigDecimal bidVolumn2;

	private BigDecimal askPrice2;

	private BigDecimal askVolumn2;

	private BigDecimal bidPrice3;

	private BigDecimal bidVolumn3;

	private BigDecimal askPrice3;

	private BigDecimal askVolumn3;

	private BigDecimal bidPrice4;

	private BigDecimal bidVolumn4;

	private BigDecimal askPrice4;

	private BigDecimal askVolumn4;

	private BigDecimal bidPrice5;

	private BigDecimal bidVolumn5;

	private BigDecimal askPrice5;

	private BigDecimal askVolumn5;

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

	public BigDecimal getHighestPrice() {
		return highestPrice;
	}

	public void setHighestPrice(BigDecimal highestPrice) {
		this.highestPrice = highestPrice;
	}

	public BigDecimal getLowestPrice() {
		return lowestPrice;
	}

	public void setLowestPrice(BigDecimal lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	public BigDecimal getVolumn() {
		return volumn;
	}

	public void setVolumn(BigDecimal volumn) {
		this.volumn = volumn;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
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

	public BigDecimal getBidPrice2() {
		return bidPrice2;
	}

	public void setBidPrice2(BigDecimal bidPrice2) {
		this.bidPrice2 = bidPrice2;
	}

	public BigDecimal getBidVolumn2() {
		return bidVolumn2;
	}

	public void setBidVolumn2(BigDecimal bidVolumn2) {
		this.bidVolumn2 = bidVolumn2;
	}

	public BigDecimal getAskPrice2() {
		return askPrice2;
	}

	public void setAskPrice2(BigDecimal askPrice2) {
		this.askPrice2 = askPrice2;
	}

	public BigDecimal getAskVolumn2() {
		return askVolumn2;
	}

	public void setAskVolumn2(BigDecimal askVolumn2) {
		this.askVolumn2 = askVolumn2;
	}

	public BigDecimal getBidPrice3() {
		return bidPrice3;
	}

	public void setBidPrice3(BigDecimal bidPrice3) {
		this.bidPrice3 = bidPrice3;
	}

	public BigDecimal getBidVolumn3() {
		return bidVolumn3;
	}

	public void setBidVolumn3(BigDecimal bidVolumn3) {
		this.bidVolumn3 = bidVolumn3;
	}

	public BigDecimal getAskPrice3() {
		return askPrice3;
	}

	public void setAskPrice3(BigDecimal askPrice3) {
		this.askPrice3 = askPrice3;
	}

	public BigDecimal getAskVolumn3() {
		return askVolumn3;
	}

	public void setAskVolumn3(BigDecimal askVolumn3) {
		this.askVolumn3 = askVolumn3;
	}

	public BigDecimal getBidPrice4() {
		return bidPrice4;
	}

	public void setBidPrice4(BigDecimal bidPrice4) {
		this.bidPrice4 = bidPrice4;
	}

	public BigDecimal getBidVolumn4() {
		return bidVolumn4;
	}

	public void setBidVolumn4(BigDecimal bidVolumn4) {
		this.bidVolumn4 = bidVolumn4;
	}

	public BigDecimal getAskPrice4() {
		return askPrice4;
	}

	public void setAskPrice4(BigDecimal askPrice4) {
		this.askPrice4 = askPrice4;
	}

	public BigDecimal getAskVolumn4() {
		return askVolumn4;
	}

	public void setAskVolumn4(BigDecimal askVolumn4) {
		this.askVolumn4 = askVolumn4;
	}

	public BigDecimal getBidPrice5() {
		return bidPrice5;
	}

	public void setBidPrice5(BigDecimal bidPrice5) {
		this.bidPrice5 = bidPrice5;
	}

	public BigDecimal getBidVolumn5() {
		return bidVolumn5;
	}

	public void setBidVolumn5(BigDecimal bidVolumn5) {
		this.bidVolumn5 = bidVolumn5;
	}

	public BigDecimal getAskPrice5() {
		return askPrice5;
	}

	public void setAskPrice5(BigDecimal askPrice5) {
		this.askPrice5 = askPrice5;
	}

	public BigDecimal getAskVolumn5() {
		return askVolumn5;
	}

	public void setAskVolumn5(BigDecimal askVolumn5) {
		this.askVolumn5 = askVolumn5;
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
		this.bidPrice1 =  new BigDecimal(din.readFloat());
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
