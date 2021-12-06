package com.crowd.tool.tstrategy;

import java.math.BigDecimal;

/**
 * 基础（半自动化/手动）策略接口
 */
public interface IStrategy {

	/**
	 * 
	 * @param context
	 */
	public void init(StrategyContext context);

	/**
	 * 
	 * @param context
	 * @param symbol
	 * @param price
	 * @return
	 */
	public BigDecimal calcTransactionAmount(StrategyContext context, String symbol, BigDecimal price);

	/**
	 * 
	 * @param context
	 * @param tickerInfo
	 */
	public void onTick(StrategyContext context, TickerInfo tickerInfo);

}
