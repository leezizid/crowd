package com.crowd.tool.tstrategy;

import java.math.BigDecimal;

import com.crowd.tool.misc.PositionSide;

/**
 * 
 * 交易信息（交易是一个量化策略的基本核算单元，一般只包括一笔开仓订单和一笔平仓订单，特殊策略可以包括多笔开平仓订单）
 */
public interface TransactionInfo {

	public OrderInfo[] getOrders();

	public int getId();

	public String getSymbol();

	public PositionSide getPositionSide();

	public BigDecimal getPositionVolumn();

	public BigDecimal getLockVolumn();

	public BigDecimal getPositionPrice();

	public BigDecimal getBalance();

	public BigDecimal getCost();

	public long getOpenTime();

	public long getCloseTime();
	
	public BigDecimal getTakePrice();
	
	public BigDecimal getStopPrice();
	
	public boolean getForceCloseFlag();

}
