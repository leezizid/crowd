package com.crowd.tool.tapis.ctp;

import java.math.BigDecimal;

public class AccountInfo {

	private BigDecimal availableValue;

	private BigDecimal balanceValue;

	private BigDecimal currentMarginValue;

	private BigDecimal positionProfit;

	public BigDecimal getAvailableValue() {
		return availableValue;
	}

	public void setAvailableValue(BigDecimal availableValue) {
		this.availableValue = availableValue;
	}

	public BigDecimal getBalanceValue() {
		return balanceValue;
	}

	public void setBalanceValue(BigDecimal balanceValue) {
		this.balanceValue = balanceValue;
	}

	public BigDecimal getCurrentMarginValue() {
		return currentMarginValue;
	}

	public void setCurrentMarginValue(BigDecimal currentMarginValue) {
		this.currentMarginValue = currentMarginValue;
	}

	public BigDecimal getPositionProfit() {
		return positionProfit;
	}

	public void setPositionProfit(BigDecimal positionProfit) {
		this.positionProfit = positionProfit;
	}

}
