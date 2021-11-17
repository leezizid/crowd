package com.crowd.tool.misc;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class NumberFormatter {

	private NumberFormat formatter;

	public final static NumberFormatter getInstance() {
		return new NumberFormatter();
	}

	private NumberFormatter() {
		formatter = NumberFormat.getInstance();
		formatter.setGroupingUsed(true);
	}

	private void setFractionDigits(int digits) {
		formatter.setMinimumFractionDigits(digits);
		formatter.setMaximumFractionDigits(digits);
		formatter.setGroupingUsed(true);
	}

	public String format(BigDecimal number, int digits) { 
		setFractionDigits(digits);
		return formatter.format(number);
	} 

	public String format(long number, int digits) {
		setFractionDigits(digits);
		return formatter.format(number);
	}

	public String format(int number, int digits) {
		setFractionDigits(digits);
		return formatter.format(number);
	}

	public String format(double number, int digits) {
		setFractionDigits(digits);
		return formatter.format(number);
	}

	public String format(float number, int digits) {
		setFractionDigits(digits);
		return formatter.format(number);
	}

	public String formatPercent(BigDecimal number, int digits) {
		if(number == null) {
			return "--";
		}
		setFractionDigits(digits);
		formatter.setGroupingUsed(false);
		return formatter.format(number.multiply(new BigDecimal(100))) + "%";
	}

}
