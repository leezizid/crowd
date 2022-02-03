package com.crowd.tool.misc;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

public class ProductDefine {

	private String name;

	private String exchange;

	private String title;

	private BigDecimal minAmount;

	private BigDecimal multiplier;

	private int priceScale;

	private MarketRegion[] marketRegions;

	public ProductDefine(String name, String exchange, String title, int multiplier, int priceScale,
			String marketTime) {
		super();
		this.name = name;
		this.exchange = exchange;
		this.title = title;
		this.minAmount = new BigDecimal(1);
		this.multiplier = new BigDecimal(multiplier);
		this.priceScale = priceScale;
		//
		String[] regionInfos = StringUtils.split(marketTime, ",");
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

	public String getName() {
		return name;
	}

	public String getExchange() {
		return exchange;
	}

	public String getTitle() {
		return title;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public BigDecimal getMultiplier() {
		return multiplier;
	}

	public int getPriceScale() {
		return priceScale;
	}
	
	public boolean isDelivery() {
		return false;
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
