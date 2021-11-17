package com.crowd.tool.tstrategy;

import java.math.BigDecimal;

import com.crowd.tool.misc.PositionSide;
import com.crowd.tool.misc.ProductInfo;

public interface StrategyContext {

	public StrategyInfo getStrategyInfo();

	public ProductInfo findProduct(String symbol);

	public void openPosition(long time, String symbol, PositionSide positionSide, BigDecimal price, BigDecimal amount,
			BigDecimal takePrice, BigDecimal stopPrice) throws Throwable;

	public void closePosition(long time, TransactionInfo transactionInfo, BigDecimal price) throws Throwable;

	public void forceClose(long time, int transactionId) throws Throwable;

	public void cancelOrders(long time, int transactionId) throws Throwable;

}
