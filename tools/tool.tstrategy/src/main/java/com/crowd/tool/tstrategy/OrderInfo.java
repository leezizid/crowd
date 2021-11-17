package com.crowd.tool.tstrategy;

import java.math.BigDecimal;

import com.crowd.tool.misc.OrderType;
import com.crowd.tool.misc.PositionSide;

/**
 * 订单信息
 *
 */
public interface OrderInfo {

	public String getClientOrderId();

	public String getServerOrderId();

	public long getTime();

	public String getSymbol();

	public PositionSide getPositionSide();

	public OrderType getType();

	public BigDecimal getAmount();

	public BigDecimal getPrice();

	public BigDecimal getExecAmount();

	public BigDecimal getExecValue();

	public BigDecimal getCostValue();

	public BigDecimal getAvgPrice();

	public boolean isCanceled();

	public String getError();

	public boolean isFinished();
	
	public long getLastCancelTime();

}
