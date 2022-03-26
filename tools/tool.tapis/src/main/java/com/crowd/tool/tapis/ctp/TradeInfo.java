package com.crowd.tool.tapis.ctp;

import java.math.BigDecimal;

public class TradeInfo {

	private String id;

	private String orderId;

	private int volumn;

	private BigDecimal price;

	public TradeInfo(String id, String orderId, int volumn, BigDecimal price) {
		super();
		this.id = id;
		this.orderId = orderId;
		this.volumn = volumn;
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public int getVolumn() {
		return volumn;
	}

	public void setVolumn(int volumn) {
		this.volumn = volumn;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
