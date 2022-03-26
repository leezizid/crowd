package com.crowd.tool.tstrategy.impl;

import java.math.BigDecimal;

import org.json.JSONObject;

import com.crowd.tool.misc.OrderType;
import com.crowd.tool.misc.PositionSide;
import com.crowd.tool.tstrategy.OrderInfo;

/**
 * 订单信息
 *
 */
class OrderInfoImpl implements OrderInfo {

	/**
	 * 客户端订单ID
	 */
	private String clientOrderId;

	/**
	 * 服务端订单ID
	 */
	private String serverOrderId;

	/**
	 * 订单时间
	 */
	private long time;

	/**
	 * 产品或者合约代码/名称
	 */
	private String symbol;

	/**
	 * 方向LONG/SHORT
	 */
	private PositionSide positionSide;

	/**
	 * 开平0/1
	 */
	private OrderType type;

	/**
	 * 数量
	 */
	private BigDecimal volumn;

	/**
	 * 价格
	 */
	private BigDecimal price;

	/**
	 * 完成数量
	 */
	private BigDecimal execVolumn;

	/**
	 * 完成金额
	 */
	private BigDecimal execValue;

	/**
	 * 费用
	 */
	private BigDecimal costValue;

	/**
	 * 成交均价
	 */
	private BigDecimal avgPrice;

	/**
	 * 是否已经撤销
	 */
	private boolean canceled;

	/**
	 * 
	 */
	private long lastCancelTime;

	/**
	 * 错误信息
	 */
	private String error = "";

	public String getClientOrderId() {
		return clientOrderId;
	}

	public void setClientOrderId(String clientOrderId) {
		this.clientOrderId = clientOrderId;
	}

	public String getServerOrderId() {
		return serverOrderId;
	}

	public void setServerOrderId(String serverOrderId) {
		this.serverOrderId = serverOrderId;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public PositionSide getPositionSide() {
		return positionSide;
	}

	public void setPositionSide(PositionSide positionSide) {
		this.positionSide = positionSide;
	}

	public OrderType getType() {
		return type;
	}

	public void setType(OrderType type) {
		this.type = type;
	}

	public BigDecimal getVolumn() {
		return volumn;
	}

	public void setVolumn(BigDecimal volumn) {
		this.volumn = volumn;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getExecVolumn() {
		return execVolumn;
	}

	public void setExecVolumn(BigDecimal execVolumn) {
		this.execVolumn = execVolumn;
	}

	public BigDecimal getExecValue() {
		return execValue;
	}

	public void setExecValue(BigDecimal execValue) {
		this.execValue = execValue;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public BigDecimal getCostValue() {
		return costValue;
	}

	public void setCostValue(BigDecimal costValue) {
		this.costValue = costValue;
	}

	public BigDecimal getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(BigDecimal avgPrice) {
		this.avgPrice = avgPrice;
	}

	@Override
	public boolean isFinished() {
		return this.getExecVolumn().compareTo(this.getVolumn()) == 0 || this.isCanceled();
	}

	public long getLastCancelTime() {
		return lastCancelTime;
	}

	public void setLastCancelTime(long lastCancelTime) {
		this.lastCancelTime = lastCancelTime;
	}

	public void fromJSON(JSONObject o) {
		this.clientOrderId = o.optString("clientOrderId");
		this.serverOrderId = o.optString("serverOrderId");
		this.time = o.optLong("time");
		this.symbol = o.optString("symbol");
		this.positionSide = o.optString("positionSide").equalsIgnoreCase("Long") ? PositionSide.Long
				: PositionSide.Short;
		this.type = o.optString("type").equalsIgnoreCase("Open") ? OrderType.Open : OrderType.Close;
		this.volumn = new BigDecimal(o.optDouble("volumn"));
		this.price = new BigDecimal(o.optDouble("price"));
		this.execVolumn = new BigDecimal(o.optDouble("execVolumn"));
		this.execValue = new BigDecimal(o.optDouble("execValue"));
		this.costValue = new BigDecimal(o.optDouble("costValue"));
		this.avgPrice = new BigDecimal(o.optDouble("avgPrice"));
		this.canceled = o.optBoolean("canceled");
		this.error = o.optString("error");
		this.lastCancelTime = o.optLong("lastCancelTime");
	}

	public JSONObject toJSON() {
		JSONObject o = new JSONObject();
		o.put("clientOrderId", clientOrderId);
		o.put("serverOrderId", serverOrderId);
		o.put("time", time);
		o.put("symbol", symbol);
		o.put("positionSide", positionSide.name());
		o.put("type", type.name());
		o.put("volumn", volumn.doubleValue());
		o.put("price", price.doubleValue());
		o.put("execVolumn", execVolumn.doubleValue());
		o.put("execValue", execValue.doubleValue());
		o.put("costValue", costValue.doubleValue());
		o.put("avgPrice", avgPrice.doubleValue());
		o.put("canceled", canceled);
		o.put("error", error);
		o.put("lastCancelTime", lastCancelTime);
		return o;
	}

}
