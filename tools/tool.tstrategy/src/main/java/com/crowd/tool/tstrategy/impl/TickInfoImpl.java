package com.crowd.tool.tstrategy.impl;

import java.math.BigDecimal;

import com.crowd.tool.tstrategy.TickInfo;

public class TickInfoImpl implements TickInfo {

	private String symbol;

	private long time;

	private BigDecimal lowerLimitPrice;

	private BigDecimal upperLimitPrice;

	private BigDecimal newPrice;

	private BigDecimal newVolumn;

	public TickInfoImpl(String symbol, long time, BigDecimal lowerLimitPrice, BigDecimal upperLimitPrice,
			BigDecimal newPrice, BigDecimal newVolumn) {
		super();
		this.symbol = symbol;
		this.time = time;
		this.lowerLimitPrice = lowerLimitPrice;
		this.upperLimitPrice = upperLimitPrice;
		this.newPrice = newPrice;
		this.newVolumn = newVolumn;
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

	public BigDecimal getNewVolumn() {
		return newVolumn;
	}

}
