//package com.crowd.service.stock;
//
//public class ZB {
//
//	public final static double CCI(double[] values, int period) {
//		if (values.length < period * 2 - 1) {
//			throw new IllegalStateException("数据长度不够");
//		}
//		//
//		double sum = 0;
//		//
//		double[] maValues = new double[values.length];
//		for (int i = values.length - period; i < values.length; i++) {
//			if (i == values.length - period) {
//				for (int x = 0; x < period; x++) {
//					sum = sum + values[i - period + x + 1]; //加上最后面的值
//				}
//			} else {
//				sum = sum + values[i]; //
//			}
//			maValues[i] = sum / period;
//			sum = sum - values[i - period + 1]; //移除最前面的值
//		}
//		//
//		sum = 0;
//		for (int i = 0; i < period; i++) {
//			sum = sum + (maValues[values.length - i - 1] - values[values.length - i - 1]);
//		}
//		double mdValue = sum / period;
//		//
//		return (values[values.length - 1] - maValues[values.length - 1]) / (mdValue * 0.015);
//	}
//
//	public final static void main(String[] args) {
//		double[] values = new double[] { 2.4, 2.42, 2.34, 2.31, 2.28, 2.29, 2.27, 2.19, 2.19, 2.18, 2.20, 2.17 };
//		System.out.println(CCI(values, 4));
//	}
//
//}
