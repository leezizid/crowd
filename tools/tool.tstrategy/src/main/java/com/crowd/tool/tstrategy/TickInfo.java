package com.crowd.tool.tstrategy;

import java.math.BigDecimal;

public interface TickInfo {

	public String getSymbol();

	public long getTime();

	public BigDecimal getNewPrice();

	public BigDecimal getNewVolumn();

}
