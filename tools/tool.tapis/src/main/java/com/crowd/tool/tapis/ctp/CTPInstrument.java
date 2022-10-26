package com.crowd.tool.tapis.ctp;

public class CTPInstrument {

	private String id;

	private String name;

	private String exchangeId;

	private String productId;

	private String deliveryYear;

	private String deliveryMonth;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExchangeId() {
		return exchangeId;
	}

	public void setExchangeId(String exchangeId) {
		this.exchangeId = exchangeId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getDeliveryYear() {
		return deliveryYear;
	}

	public void setDeliveryYear(int deliveryYear) {
		this.deliveryYear = String.valueOf(deliveryYear);
	}

	public String getDeliveryMonth() {
		return deliveryMonth;
	}

	public void setDeliveryMonth(int deliveryMonth) {
		this.deliveryMonth = (deliveryMonth < 10 ? "0" : "") + deliveryMonth;
	}

	public String toString() {
		return this.exchangeId + "." + this.productId + ":" + this.id + "," + this.name;
	}

}
