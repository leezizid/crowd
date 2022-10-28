package com.crowd.tool.misc;

import java.math.BigDecimal;

import org.json.JSONObject;

/**
 * 交易标的
 *
 */
public class ProductInfo {

	private ProductDefine productDefine;

	private BigDecimal openMakerCostRate;

	private BigDecimal closeMakerCostRate;

	private BigDecimal openTakerCostRate;

	private BigDecimal closeTakerCostRate;

	public String getSymbol() {
		return productDefine.getExchange() + "." + productDefine.getName();
	}

	public String getTitle() {
		return productDefine.getTitle();
	}

	public boolean isDelivery() {
		return productDefine.isDelivery();
	}

	public BigDecimal getMinVolume() {
		return productDefine.getMinVolume();
	}

	public BigDecimal getMultiplier() {
		return productDefine.getMultiplier();
	}

	public BigDecimal getOpenMakerCostRate() {
		return openMakerCostRate;
	}

	public BigDecimal getCloseMakerCostRate() {
		return closeMakerCostRate;
	}

	public BigDecimal getOpenTakerCostRate() {
		return openTakerCostRate;
	}

	public BigDecimal getCloseTakerCostRate() {
		return closeTakerCostRate;
	}
	
	public int getPriceScale() {
		return productDefine.getPriceScale();
	}

	/**
	 * 是否在开盘期间
	 * 
	 * @param time单位秒
	 * @return
	 */
	public boolean isInMarket(long time) {
		return productDefine.isInMarket(time);
	}

	/**
	 * 开盘后多久，如果不在开盘时间内，返回距离开盘的时间(负数）
	 * 
	 * @param time单位秒
	 * @return
	 */
	public long afterOpenMarket(long time) {
		return productDefine.afterOpenMarket(time);
	}

	/**
	 * 多久后收盘，如果不在开盘时间内，返回-1
	 * 
	 * @param time单位秒
	 * @return
	 */
	public long beforeCloseMarket(long time) {
		return productDefine.beforeCloseMarket(time);
	}

	public void fromJSON(JSONObject o) {
		String symbol = o.getString("symbol");
		String name = symbol.substring(symbol.indexOf(".") + 1);
		this.productDefine = ProductDefineRegistry.find(name);
		if (o.has("baseRate")) {
			BigDecimal baseRate = new BigDecimal(o.getDouble("baseRate"));
			this.openMakerCostRate = this.openTakerCostRate = this.closeMakerCostRate = this.closeTakerCostRate = baseRate;
		} else {
			this.openMakerCostRate = new BigDecimal(o.getDouble("openMakerCostRate"));
			this.openTakerCostRate = new BigDecimal(o.getDouble("openTakerCostRate"));
			this.closeMakerCostRate = new BigDecimal(o.getDouble("closeMakerCostRate"));
			this.closeTakerCostRate = new BigDecimal(o.getDouble("closeTakerCostRate"));
		}
	}

}
