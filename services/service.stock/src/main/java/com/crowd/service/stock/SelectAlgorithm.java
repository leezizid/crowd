package com.crowd.service.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class SelectAlgorithm {

	protected List<String> codeList = new ArrayList<String>();

	abstract boolean ignore(KData kData, KData indexKData);

	abstract public String nextCode();

	public SelectAlgorithm(List<String> codeList) {
		this.codeList.addAll(codeList);
	}

}

class RandomAlgorithm extends SelectAlgorithm {

	private List<String> randomCodeList = new ArrayList<String>();

	public RandomAlgorithm(List<String> codeList) {
		super(codeList);
		randomCodeList.addAll(codeList);
	}

	@Override
	boolean ignore(KData kData, KData indexKData) {
		return false;
	}

	@Override
	public String nextCode() {
		if (randomCodeList.size() == 0) {
			return null;
		}
		return randomCodeList.remove(new Random().nextInt(randomCodeList.size()));
	}

}

class TrendAlgorithm extends SelectAlgorithm {
	private final static BigDecimal THRESHOLD_HIGH = new BigDecimal("0.02"); // 0.0185
	private final static BigDecimal THRESHOLD_LOW = new BigDecimal("0.01");
	private final static BigDecimal THRESHOLD_STEP = new BigDecimal("0.001");
	private BigDecimal threshold;
	private List<String> randomCodeList = new ArrayList<String>();

	public TrendAlgorithm(List<String> codeList) {
		super(codeList);
		randomCodeList.addAll(codeList);
		threshold = THRESHOLD_HIGH;
	}

	public String nextCode() {
		if (randomCodeList.size() == 0) {
			if (threshold.compareTo(THRESHOLD_LOW) < 0) {
				return null;
			} else {
				threshold = threshold.subtract(THRESHOLD_STEP);
				randomCodeList.addAll(codeList);
			}
//			return null;
		}
		return randomCodeList.remove(new Random().nextInt(randomCodeList.size()));
	}

	public boolean ignore(KData kData, KData indexKData) {

		// 过去n个交易日，上涨1%的次数低过少的，忽略
		int count = 10;
		int targetCount = 5;
		int matchCount = 0;
//		BigDecimal lowPrice = new BigDecimal(1000);
//		BigDecimal highPrice = BigDecimal.ZERO;
//		BigDecimal lastPrice = kData.closePrice;
		while (count > 0) {
			if (kData == null || kData.prev == null) {
				break;
			}
			if (kData.closePrice.subtract(kData.prev.closePrice).divide(kData.prev.closePrice, 4, RoundingMode.HALF_UP)
					.compareTo(threshold) > 0) {
				matchCount++;
			}
//			if (kData.closePrice.subtract(kData.prev.closePrice)
//					.divide(kData.prev.closePrice, 4, RoundingMode.HALF_UP)
//					.subtract(indexKData.closePrice.subtract(indexKData.prev.closePrice)
//							.divide(indexKData.prev.closePrice, 4, RoundingMode.HALF_UP))
//					.compareTo(threshold) > 0) {
//				matchCount++;
//			}
//			highPrice = highPrice.max(kData.highPrice);
//			lowPrice = lowPrice.min(kData.lowPrice);
			kData = kData.prev;
			indexKData = indexKData.prev;
			count--;
		}
		if (count > 0) {
			return true;
		}
		if (count > 0 || matchCount < targetCount) {
			return true;
		}
		// 当前价低于近十日最高价15%
//		if (highPrice.compareTo(lastPrice) > 0 && highPrice.subtract(lastPrice)
//				.divide(lastPrice, 4, RoundingMode.HALF_UP).compareTo(new BigDecimal(0.15)) > 0) {
//			return true;
//		}
//
//		// 当前价高于近十日最低价15%
//		if (lastPrice.compareTo(lowPrice) > 0 && lastPrice.subtract(lowPrice).divide(lowPrice, 4, RoundingMode.HALF_UP)
//				.compareTo(new BigDecimal(0.15)) > 0) {
//			return true;
//		}

		// 过去n个交易日，下跌超过3%的次数过多的，忽略
//		v = new BigDecimal("0.95");
//		count = 10;
//		targetCount = 1;
//		matchCount = 0;
//		kData = todayKData.prev;
//		while (count > 0) {
//			if (kData == null || kData.prev == null) {
//				break;
//			}
//			if (kData.closePrice.compareTo(kData.prev.closePrice.multiply(v)) < 0) {
//				matchCount++;
//			}
//			kData = kData.prev;
//			count--;
//		}
//		if (count > 0 || matchCount > targetCount) {
////			continue;
//		}

		return false;
	}

}
