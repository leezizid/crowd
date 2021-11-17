package com.crowd.tool.tstrategy;

import java.math.BigDecimal;

import com.crowd.tool.misc.OrderType;
import com.crowd.tool.misc.PositionSide;

/**
 * 基础（半自动化/手动）策略接口
 */
public interface IStrategy {

	public void init(StrategyContext context);

	public void validateOrderRisk(StrategyContext context, OrderType type, String symbol, PositionSide positionSide,
			float amount, float price) throws Throwable;

	public BigDecimal calcTransactionAmount(StrategyContext context, String symbol, BigDecimal price);

	/**
	 * 
	 * @param context
	 * @param tickerInfo
	 */
	public void onTick(StrategyContext context, TickerInfo tickerInfo);

}
