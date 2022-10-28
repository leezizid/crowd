package com.crowd.tool.tapis.ctp;

import java.math.BigDecimal;

import com.crowd.tool.misc.PositionSide;

public class PositionInfo {

	// 合约代码
	private String symbol;

	// 持仓数量
	private int totalVolume;

	// 今日开仓数量
	private int todayVolume;

	// 多空方向
	private PositionSide positionSide;

	// 当前（结算）价
	private BigDecimal marketPrice;

	public int getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(int totalVolume) {
		this.totalVolume = totalVolume;
	}

	public int getTodayVolume() {
		return todayVolume;
	}

	public void setTodayVolume(int todayVolume) {
		this.todayVolume = todayVolume;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public PositionSide getPositionSide() {
		return positionSide;
	}

	public void setPositionSide(PositionSide positionSide) {
		this.positionSide = positionSide;
	}

	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}

}
