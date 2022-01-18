package com.crowd.tool.tapis.ctp;

public class CTPProduct {

	private String name;

	private String exchange;

	private String title;

	private int minAmount;

	private int multiplier;

	private int priceScale;

	private String marketTime;

	public CTPProduct(String name, String exchange, String title, int multiplier, int priceScale, String marketTime) {
		super();
		this.name = name;
		this.exchange = exchange;
		this.title = title;
		this.minAmount = 1;
		this.multiplier = multiplier;
		this.priceScale = priceScale;
		this.marketTime = marketTime;
	}

	public String getName() {
		return name;
	}

	public String getExchange() {
		return exchange;
	}

	public String getTitle() {
		return title;
	}

	public int getMinAmount() {
		return minAmount;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public int getPriceScale() {
		return priceScale;
	}

	public String getMarketTime() {
		return marketTime;
	}

}
