package com.crowd.tool.tstrategy;

import java.math.BigDecimal;

import com.crowd.service.base.CrowdContext;
import com.crowd.tool.misc.PositionSide;
import com.crowd.tool.misc.Products;
import com.crowd.tool.tstrategy.impl.BacktestStrategyEnv;
import com.crowd.tool.tstrategy.impl.RealStrategyEnv;

public interface StrategyEnv extends StrategyContext {

	public void dispose();

	public void onTick(String symbol, long time, BigDecimal lowerLimitPrice, BigDecimal upperLimitPrice,
			BigDecimal price, BigDecimal volumn);

	public void handleOrderUpdated(long time, OrderInfo matchOrderInfo, boolean canceled, BigDecimal execAmount,
			BigDecimal execValue) throws Throwable;

	public void handleManualOpen(PositionSide side, String symbol, BigDecimal price, BigDecimal takePrice,
			BigDecimal stopPrice) throws Throwable;

//	public void handleManualClose(int transactionId) throws Throwable;

	public static StrategyEnv createReal(CrowdContext crowdContext, IStrategy strategyInstance,
			StrategyInfo strategyInfo, Products products) throws Throwable {
		return new RealStrategyEnv(crowdContext, strategyInstance, strategyInfo, products);
	}

	public static StrategyEnv createTest(CrowdContext crowdContext, IStrategy strategyInstance,
			StrategyInfo strategyInfo, Products products) throws Throwable {
		return new BacktestStrategyEnv(crowdContext, strategyInstance, strategyInfo, products);
	}

}
