package com.crowd.tool.misc.zb;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EMA {

	private double[] values;
	private int next = 0;
	private int validCount = 0;
	private double sum = 0;

	public EMA(int size) {
		values = new double[size];
	}

	public double push(double value) {
		//
		sum = sum + value;
		if (validCount < values.length) {
			validCount++;
		} else {
			sum = sum - values[next];
		}
		//
		values[next] = value;
		//
		next++;
		if (next == values.length) {
			next = 0;
		}
		//
		return new BigDecimal(sum / validCount).setScale(4, RoundingMode.HALF_UP).doubleValue();
	}

}
