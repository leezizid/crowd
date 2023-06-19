package com.crowd.tool.tstrategy.impl;

import java.math.BigDecimal;

import com.crowd.tool.misc.TickInfo;

public class TickInfoImpl implements TickInfo {

	private String symbol;

	private long time;

	private BigDecimal lowerLimitPrice;

	private BigDecimal upperLimitPrice;

	private BigDecimal newPrice;

	private BigDecimal newVolume;
	
	private BigDecimal openInterest;

	public TickInfoImpl(String symbol, long time, BigDecimal lowerLimitPrice, BigDecimal upperLimitPrice,
			BigDecimal newPrice, BigDecimal newVolume, BigDecimal openInterest) {
		super();
		this.symbol = symbol;
		this.time = time;
		this.lowerLimitPrice = lowerLimitPrice;
		this.upperLimitPrice = upperLimitPrice;
		this.newPrice = newPrice;
		this.newVolume = newVolume;
		this.openInterest = openInterest;
	}

	public String getSymbol() {
		return symbol;
	}

	public long getTime() {
		return time;
	}

	public BigDecimal getLowerLimitPrice() {
		return lowerLimitPrice;
	}

	public BigDecimal getUpperLimitPrice() {
		return upperLimitPrice;
	}

	public BigDecimal getNewPrice() {
		return newPrice;
	}

	public BigDecimal getNewVolume() {
		return newVolume;
	}

	public BigDecimal getNewOpenInterest() {
		return openInterest;
	}
	

}
