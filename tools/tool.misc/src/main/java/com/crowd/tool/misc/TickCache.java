package com.crowd.tool.misc;

import java.util.ArrayList;
import java.util.List;

import com.crowd.tool.misc.zb.EMA;

public class TickCache {

	private TickInfo[] tickArray;
	private double[] ema5Values;
	private int continuousTimeMinute;
	private int cursor = -1; // 最新tick的索引
	private long validStartTime; //
	private long lastTime; //
	private EMA ema5;

	public TickCache(int count, int continuousTimeMinute) {
		tickArray = new TickInfo[count]; // 保留足够数量的tick数据，以便进行匹配序列核对
		ema5Values = new double[count];
		this.continuousTimeMinute = continuousTimeMinute;
	}

	public final void cacheTickInfo(TickInfo tickInfo) {
		cursor++;
		if (cursor == tickArray.length) {
			cursor = 0;
		}
		tickArray[cursor] = tickInfo;
		// XXX: 如果tick时间相隔超过16分钟，则重置有效开始时间
		if (tickInfo.getTime() - lastTime > 1000 * 60 * 16) {
			validStartTime = tickInfo.getTime();
			ema5 = new EMA(2 * 60 * 5);
		}
		lastTime = tickInfo.getTime();
		ema5Values[cursor] = ema5.push(tickInfo.getNewPrice().doubleValue());
	}

	/**
	 * 是否存在必要的连续数据（25分钟左右）
	 * 
	 * @return
	 */
	public boolean isContinuous() {
		return lastTime - validStartTime >= 1000 * 60 * continuousTimeMinute;
	}

	/**
	 * 提取指定基准时间之前的交易量信息，按一定周期分组合并
	 * 
	 * @param markTime       基准时间，往前提取
	 * @param periodInterval 按什么样的周期组合成一组（例如20秒）
	 * @param periodCount    共提取多少组（例如60组，每组20秒，就是60分钟的数据）
	 * @return
	 */
	public double[] getVolumes(long markTime, int periodInterval, int periodCount) {
		double[] volumes = new double[periodCount];
		long startTime = markTime - periodInterval * 1000 * periodCount;
		int index = cursor;
		// 找到序列起始索引
		while (true) {
			if (tickArray[index] == null) {
				throw new IllegalStateException("前序数据不足");
			}
			if (tickArray[index].getTime() <= startTime) {
				break;
			}
			index--;
			if (index == -1) {
				index = tickArray.length - 1;
			}
		}
		// 从起始开始，统计每个间隔的数据
		for (int i = 0; i < periodCount; i++) {
			while (true) {
				if (tickArray[index].getTime() >= startTime + (i + 1) * periodInterval * 1000) {
					break;
				}
				volumes[i] = volumes[i] + tickArray[index].getNewVolume().doubleValue();
				index++;
				if (index == tickArray.length) {
					index = 0;
				}
			}
		}
		return volumes;
	}

	/**
	 * 提取指定基准时间之前的价格信息，并不合并或在分组，仅简单返回所有tick价格信息
	 * 
	 * @param markTime   基准时间，往前提取
	 * @param timeSecond 往前提取多少秒
	 * @return
	 */
	public double[] getPrices(long markTime, long timeSecond) {
		List<Double> priceList = new ArrayList<Double>();
		long startTime = markTime - timeSecond * 1000;
		int index = cursor;
		// 找到序列起始索引
		while (true) {
			if (tickArray[index] == null) {
				throw new IllegalStateException("前序数据不足");
			}
			if (tickArray[index].getTime() <= startTime) {
				break;
			}
			if (tickArray[index].getTime() < markTime) {
				priceList.add(tickArray[index].getNewPrice().doubleValue());
			}
			index--;
			if (index == -1) {
				index = tickArray.length - 1;
			}
		}
		double[] prices = new double[priceList.size()];
		for (int i = 0; i < prices.length; i++) {
			prices[i] = priceList.get(priceList.size() - i - 1);
		}
		return prices;
	}

	/**
	 * 返回基准时间之前的指定时间内，价格偏离ema值的最大数值
	 * 
	 * @return
	 */
	public double getMaxDeviation(long markTime, long timeSecond) {
		double maxDeviation = 0;
		long startTime = markTime - timeSecond * 1000;
		int index = cursor;
		// 找到序列起始索引
		while (true) {
			if (tickArray[index] == null) {
				throw new IllegalStateException("前序数据不足");
			}
			if (tickArray[index].getTime() <= startTime) {
				break;
			}
			if (tickArray[index].getTime() < markTime) {
				maxDeviation = Math.max(maxDeviation,
						Math.abs(tickArray[index].getNewPrice().doubleValue() - ema5Values[index]));
			}
			index--;
			if (index == -1) {
				index = tickArray.length - 1;
			}
		}
		return maxDeviation;
	}

	public double getLastPrice() {
		return tickArray[cursor].getNewPrice().doubleValue();
	}

	public double getLastEmaValue() {
		return ema5Values[cursor];
	}

}
