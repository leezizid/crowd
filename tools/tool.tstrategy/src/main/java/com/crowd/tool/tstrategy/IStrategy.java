package com.crowd.tool.tstrategy;

import java.math.BigDecimal;

import com.crowd.tool.misc.TickInfo;

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
	public BigDecimal calcTransactionVolume(StrategyContext context, String symbol, BigDecimal price);

	/**
	 * 
	 * @param context
	 * @param tickerInfo
	 */
	public void onTick(StrategyContext context, TickInfo tickerInfo);

}
