package com.crowd.tool.tapis.ctp;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.crowd.tool.misc.OrderType;
import com.crowd.tool.misc.PositionSide;

public class OrderInfo {

	// 合约代码
	private String symbol;

	private String serverOrderId;

	// private String clientOrderId;

	private OrderType type;

	private PositionSide positionSide;

	private String time;

	private double price;

	private int volumn;

	private int execVolumn;

	private BigDecimal execValue = new BigDecimal(0);

//	private int orderStatus;

	private boolean canceled;

	private Set<String> tradeIds = new HashSet<String>();

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getServerOrderId() {
		return serverOrderId;
	}

	public void setServerOrderId(String serverOrderId) {
		this.serverOrderId = serverOrderId;
	}

//	public String getClientOrderId() {
//		return clientOrderId;
//	}
//
//	public void setClientOrderId(String clientOrderId) {
//		this.clientOrderId = clientOrderId;
//	}

	public OrderType getType() {
		return type;
	}

	public void setType(OrderType type) {
		this.type = type;
	}

	public PositionSide getPositionSide() {
		return positionSide;
	}

	public void setPositionSide(PositionSide positionSide) {
		this.positionSide = positionSide;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getVolumn() {
		return volumn;
	}

	public void setVolumn(int volumn) {
		this.volumn = volumn;
	}

	public int getExecVolumn() {
		return execVolumn;
	}

//	public void setExecVolumn(int execVolumn) {
//		this.execVolumn = execVolumn;
//	}

//	public int getOrderStatus() {
//		return orderStatus;
//	}
//
//	public void setOrderStatus(int orderStatus) {
//		this.orderStatus = orderStatus;
//	}

	public BigDecimal getExecValue() {
		return execValue;
	}

//	public void setExecValue(BigDecimal execValue) {
//		this.execValue = execValue;
//	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public void addTrade(TradeInfo tradeInfo) {
		if (!this.tradeIds.contains(tradeInfo.getId())) {
			this.tradeIds.add(tradeInfo.getId());
			this.execVolumn = this.execVolumn + tradeInfo.getVolumn();
			this.execValue = this.execValue.add(tradeInfo.getPrice().multiply(new BigDecimal(tradeInfo.getVolumn()))
					.multiply(CTPProducts.find(symbol).getMultiplier()));
		}
	}

}
