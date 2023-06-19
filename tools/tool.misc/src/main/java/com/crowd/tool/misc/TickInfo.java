package com.crowd.tool.misc;

import java.math.BigDecimal;

public interface TickInfo {

	public String getSymbol();

	public long getTime();

	public BigDecimal getNewPrice();

	public BigDecimal getNewVolume();
	
	public BigDecimal getNewOpenInterest();

}
