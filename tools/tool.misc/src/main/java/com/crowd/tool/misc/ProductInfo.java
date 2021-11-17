package com.crowd.tool.misc;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

/**
 * 交易标的
 *
 */
public class ProductInfo {

	private String symbol;

	private String title;

	private boolean delivery;

	private BigDecimal minAmount;

	private BigDecimal multiplier;

	private BigDecimal openMakerCostRate;

	private BigDecimal closeMakerCostRate;

	private BigDecimal openTakerCostRate;

	private BigDecimal closeTakerCostRate;

	private MarketRegion[] marketRegions;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isDelivery() {
		return delivery;
	}

	public void setDelivery(boolean delivery) {
		this.delivery = delivery;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(BigDecimal multiplier) {
		this.multiplier = multiplier;
	}

	public BigDecimal getOpenMakerCostRate() {
		return openMakerCostRate;
	}

	public void setOpenMakerCostRate(BigDecimal openMakerCostRate) {
		this.openMakerCostRate = openMakerCostRate;
	}

	public BigDecimal getCloseMakerCostRate() {
		return closeMakerCostRate;
	}

	public void setCloseMakerCostRate(BigDecimal closeMakerCostRate) {
		this.closeMakerCostRate = closeMakerCostRate;
	}

	public BigDecimal getOpenTakerCostRate() {
		return openTakerCostRate;
	}

	public void setOpenTakerCostRate(BigDecimal openTakerCostRate) {
		this.openTakerCostRate = openTakerCostRate;
	}

	public BigDecimal getCloseTakerCostRate() {
		return closeTakerCostRate;
	}

	public void setCloseTakerCostRate(BigDecimal closeTakerCostRate) {
		this.closeTakerCostRate = closeTakerCostRate;
	}

	/**
	 * 是否在开盘期间
	 * 
	 * @param time单位秒
	 * @return
	 */
	public boolean isInMarket(long time) {
		if (marketRegions == null || marketRegions.length == 0) {
			return true;
		}
		time = processTime(time);
		for (MarketRegion region : marketRegions) {
			if (time >= region.start && time < region.end) {
				for (MarketPhase phase : region.phases) {
					if (time >= phase.start && time < phase.end) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 开盘后多久，如果不在开盘时间内，返回距离开盘的时间(负数）
	 * 
	 * @param time单位秒
	 * @return
	 */
	public long afterOpenMarket(long time) {
		if (marketRegions == null || marketRegions.length == 0) {
			return Long.MAX_VALUE;
		}
		time = processTime(time);
		for (MarketRegion region : marketRegions) {
			if (time < region.end) {
				return time - region.start;
			}
		}
		return time - marketRegions[0].start - 24 * 3600;
	}

	/**
	 * 多久后收盘，如果不在开盘时间内，返回-1
	 * 
	 * @param time单位秒
	 * @return
	 */
	public long beforeCloseMarket(long time) {
		if (marketRegions == null || marketRegions.length == 0) {
			return Long.MAX_VALUE;
		}
		time = processTime(time);
		for (MarketRegion region : marketRegions) {
			if (time >= region.start && time < region.end) {
				return region.end - time;
			}
		}
		return -1;
	}

	private long processTime(long time) {
//		time = time - 3600000 * 8; // 统一前推8小时
		time = time % (3600000 * 24); // 保留当天相对秒数
		time = time / 1000; // 去除毫秒信息
		return time;
	}

	public void fromJSON(JSONObject o) {
		this.setSymbol(o.getString("symbol"));
		this.setTitle(o.getString("title"));
		this.setDelivery(o.getBoolean("delivery"));
		this.setMinAmount(new BigDecimal(o.getDouble("minAmount")));
		this.setMultiplier(new BigDecimal(o.getDouble("multiplier")));
		this.setOpenMakerCostRate(new BigDecimal(o.getDouble("openMakerCostRate")));
		this.setOpenTakerCostRate(new BigDecimal(o.getDouble("openTakerCostRate")));
		this.setCloseMakerCostRate(new BigDecimal(o.getDouble("closeMakerCostRate")));
		this.setCloseTakerCostRate(new BigDecimal(o.getDouble("closeTakerCostRate")));
		String info = o.getString("marketTime");
		String[] regionInfos = StringUtils.split(info, ",");
		this.marketRegions = new MarketRegion[regionInfos.length];
		for (int i = 0; i < regionInfos.length; i++) {
			String[] phaseInfos = StringUtils.split(regionInfos[i], "~");
			MarketPhase[] marketPhases = new MarketPhase[phaseInfos.length];
			for (int j = 0; j < phaseInfos.length; j++) {
				marketPhases[j] = new MarketPhase(phaseInfos[j]);
			}
			this.marketRegions[i] = new MarketRegion(marketPhases);
		}
	}

}

class MarketRegion {

	long start;

	long end;

	MarketPhase[] phases;

	public MarketRegion(MarketPhase[] phases) {
		this.phases = phases;
		this.start = phases[0].start;
		this.end = phases[phases.length - 1].end;
	}

}

class MarketPhase {

	long start;

	long end;

	public MarketPhase(String info) {
		String[] arr = StringUtils.split(info, "-");
		String[] startTimeInfo = StringUtils.split(arr[0], ":");
		String[] endTimeInfo = StringUtils.split(arr[1], ":");
		this.start = Integer.parseInt(startTimeInfo[0]) * 3600 + Integer.parseInt(startTimeInfo[1]) * 60;
		this.end = Integer.parseInt(endTimeInfo[0]) * 3600 + Integer.parseInt(endTimeInfo[1]) * 60;
	}

}
