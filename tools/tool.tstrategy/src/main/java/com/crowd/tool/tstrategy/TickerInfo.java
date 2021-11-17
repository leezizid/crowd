package com.crowd.tool.tstrategy;

import java.math.BigDecimal;

public interface TickerInfo {

	public String getSymbol();

	public long getTime();

	public BigDecimal getPrice();

	public BigDecimal getAmount();

}
