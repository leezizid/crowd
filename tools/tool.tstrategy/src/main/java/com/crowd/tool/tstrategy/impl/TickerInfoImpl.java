package com.crowd.tool.tstrategy.impl;

import java.math.BigDecimal;

import com.crowd.tool.tstrategy.TickerInfo;

public class TickerInfoImpl implements TickerInfo {

	private String symbol;

	private long time;

	private BigDecimal lowerLimitPrice;

	private BigDecimal upperLimitPrice;

	private BigDecimal price;

	private BigDecimal amount;

	public TickerInfoImpl(String symbol, long time, BigDecimal lowerLimitPrice, BigDecimal upperLimitPrice,
			BigDecimal price, BigDecimal amount) {
		super();
		this.symbol = symbol;
		this.time = time;
		this.lowerLimitPrice = lowerLimitPrice;
		this.upperLimitPrice = upperLimitPrice;
		this.price = price;
		this.amount = amount;
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

	public BigDecimal getPrice() {
		return price;
	}

	public BigDecimal getAmount() {
		return amount;
	}

}
