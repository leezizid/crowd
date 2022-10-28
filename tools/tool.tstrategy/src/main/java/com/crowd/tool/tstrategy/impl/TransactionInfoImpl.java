package com.crowd.tool.tstrategy.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.tool.misc.OrderType;
import com.crowd.tool.misc.PositionSide;
import com.crowd.tool.tstrategy.OrderInfo;
import com.crowd.tool.tstrategy.TransactionInfo;

/**
 * 
 * 交易信息（交易是一个量化策略的基本核算单元，一般只包括一笔开仓订单和一笔平仓订单，特殊策略可以包括多笔开平仓订单）
 */
class TransactionInfoImpl implements TransactionInfo {

	/**
	 * 交易ID
	 */
	private int id;

	/**
	 * 交易标的
	 */
	private String symbol;

	/**
	 * 持仓方向
	 */
	private PositionSide positionSide;

	/**
	 * 持仓数量
	 */
	private BigDecimal positionVolume;

	/**
	 * 持仓价格
	 */
	private BigDecimal positionPrice;

	/**
	 * 余额
	 */
	private BigDecimal balance;

	/**
	 * 各种费用
	 */
	private BigDecimal cost;

	/**
	 * 开始时间
	 */
	private long openTime;
	
	
	/**
	 * 开仓时间
	 */
	private long positionTime;

	/**
	 * 结束时间
	 */
	private long closeTime;

	/**
	 * 止盈价格
	 */
	private BigDecimal takePrice;
	
	/**
	 * 委托价格
	 */
	private BigDecimal orderPrice;

	/**
	 * 止损价格
	 */
	private BigDecimal stopPrice;

	/**
	 * 强制平仓标志
	 */
	private boolean forceCloseFlag;

	/**
	 * 订单列表
	 */
	private List<OrderInfoImpl> orders = new ArrayList<OrderInfoImpl>();

	/**
	 * 增加新订单
	 * 
	 * @param orderInfo
	 */
	public void addOrder(OrderInfoImpl orderInfo) {
		this.orders.add(orderInfo);
	}

	public void updateOrder(OrderInfoImpl orderInfo) {
		int index = -1;
		for (int i = 0; i < this.orders.size(); i++) {
			if (this.orders.get(i).getClientOrderId().equals(orderInfo.getClientOrderId())) {
				index = i;
			}
		}
		if (index == -1) {
			throw new IllegalArgumentException("找不到指定的订单ID");
		}
		this.orders.set(index, orderInfo);
	}

	public OrderInfo[] getOrders() {
		return this.orders.toArray(new OrderInfo[0]);
	}

	public BigDecimal getLockVolume() {
		BigDecimal lockVolume = BigDecimal.ZERO;
		for (OrderInfo orderInfo : this.getOrders()) {
			if (orderInfo.getType() == OrderType.Close && !orderInfo.isFinished()) {
				lockVolume = lockVolume.add(orderInfo.getVolume().subtract(orderInfo.getExecVolume()));
			}
		}
		return lockVolume;
	}

	public boolean canClose() {
		if (!this.getPositionVolume().equals(BigDecimal.ZERO)) {
			return false;
		}
		for (OrderInfo orderInfo : this.getOrders()) {
			if (!orderInfo.isFinished()) {
				return false;
			}
		}
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public BigDecimal getPositionVolume() {
		return positionVolume;
	}

	public void setPositionVolume(BigDecimal positionVolume) {
		this.positionVolume = positionVolume;
	}

	public BigDecimal getPositionPrice() {
		return positionPrice;
	}

	public void setPositionPrice(BigDecimal positionPrice) {
		this.positionPrice = positionPrice;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public long getOpenTime() {
		return openTime;
	}

	public void setOpenTime(long openTime) {
		this.openTime = openTime;
	}
	
	public long getPositionTime() {
		return positionTime;
	}
	
	public void setPositionTime(long positionTime) {
		this.positionTime = positionTime;
	}

	public long getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(long closeTime) {
		this.closeTime = closeTime;
	}
	
	

	public BigDecimal getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(BigDecimal orderPrice) {
		this.orderPrice = orderPrice;
	}

	public BigDecimal getTakePrice() {
		return takePrice;
	}

	public void setTakePrice(BigDecimal takePrice) {
		this.takePrice = takePrice;
	}

	public BigDecimal getStopPrice() {
		return stopPrice;
	}

	public void setStopPrice(BigDecimal stopPrice) {
		this.stopPrice = stopPrice;
	}

	public boolean getForceCloseFlag() {
		return forceCloseFlag;
	}

	public void setForceCloseFlag(boolean forceCloseFlag) {
		this.forceCloseFlag = forceCloseFlag;
	}

	public void fromJSON(JSONObject o) {
		this.id = o.optInt("id");
		this.symbol = o.optString("symbol");
		this.positionSide = PositionSide.valueOf(o.optString("positionSide"));
		this.positionVolume = new BigDecimal(o.optDouble("positionVolume"));
		this.positionPrice = new BigDecimal(o.optDouble("positionPrice"));
		this.balance = new BigDecimal(o.optDouble("balance"));
		this.cost = new BigDecimal(o.optDouble("cost"));
		this.openTime = o.optLong("openTime");
		this.positionTime = o.optLong("positionTime");
		this.closeTime = o.optLong("closeTime");
		this.orderPrice = new BigDecimal(o.optDouble("orderPrice"));
		this.takePrice = new BigDecimal(o.optDouble("takePrice"));
		this.stopPrice = new BigDecimal(o.optDouble("stopPrice"));
		this.forceCloseFlag = o.optBoolean("forceCloseFlag");
		this.orders.clear();
		JSONArray orderArray = o.optJSONArray("orders");
		if (orderArray != null) {
			for (int i = 0; i < orderArray.length(); i++) {
				OrderInfoImpl orderInfo = new OrderInfoImpl();
				orderInfo.fromJSON(orderArray.getJSONObject(i));
				this.orders.add(orderInfo);
			}
		}
	}

	public JSONObject toJSON() {
		JSONObject o = new JSONObject();
		o.put("id", id);
		o.put("symbol", symbol);
		o.put("positionSide", positionSide.name());
		o.put("positionVolume", positionVolume.doubleValue());
		o.put("positionPrice", positionPrice != null ? positionPrice.doubleValue() : 0);
		o.put("balance", balance.doubleValue());
		o.put("cost", cost.doubleValue());
		o.put("openTime", openTime);
		o.put("positionTime", positionTime);
		o.put("closeTime", closeTime);
		o.put("orderPrice", orderPrice != null ? orderPrice.doubleValue() : 0);
		o.put("takePrice", takePrice != null ? takePrice.doubleValue() : 0);
		o.put("stopPrice", stopPrice != null ? stopPrice.doubleValue() : 0);
		o.put("forceCloseFlag", forceCloseFlag);
		JSONArray orderArray = new JSONArray();
		for (int i = 0; i < this.orders.size(); i++) {
			orderArray.put(this.orders.get(i).toJSON());
		}
		o.put("orders", orderArray);
		return o;
	}

}
