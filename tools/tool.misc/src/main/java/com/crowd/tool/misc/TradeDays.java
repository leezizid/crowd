package com.crowd.tool.misc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TradeDays {

	private final static String[] excludeDays = new String[] {
			// 2016
			"2016-01-01", "2016-01-04", "2016-02-08", "2016-02-09", "2016-02-10", "2016-02-11", "2016-02-12",
			"2016-04-04", "2016-05-02", "2016-06-09", "2016-06-10", "2016-09-15", "2016-09-16", "2016-10-03",
			"2016-10-04", "2016-10-05", "2016-10-06", "2016-10-07",
			// 2017
			"2017-01-02", "2017-01-27", "2017-01-30", "2017-01-31", "2017-02-01", "2017-02-02", "2017-04-03",
			"2017-04-04", "2017-05-01", "2017-05-29", "2017-05-30", "2017-10-02", "2017-10-03", "2017-10-04",
			"2017-10-05", "2017-10-06",
			// 2019
			"2018-01-01", "2018-02-15", "2018-02-16", "2018-02-19", "2018-02-20", "2018-02-21", "2018-04-05",
			"2018-04-06", "2018-04-30", "2018-05-01", "2018-06-18", "2018-09-24", "2018-10-01", "2018-10-02",
			"2018-10-03", "2018-10-04", "2018-10-05", "2018-12-31",
			// 2019
			"2019-01-01", "2019-02-04", "2019-02-05", "2019-02-06", "2019-02-07", "2019-02-08", "2019-04-05",
			"2019-05-01", "2019-05-02", "2019-05-03", "2019-06-07", "2019-09-13", "2019-10-01", "2019-10-02",
			"2019-10-03", "2019-10-04", "2019-10-07",
			// 2020
			"2020-01-01", "2020-01-24", "2020-01-27", "2020-01-28", "2020-01-29", "2020-01-30", "2020-01-31",
			"2020-04-06", "2020-05-01", "2020-05-04", "2020-05-05", "2020-06-25", "2020-06-26", "2020-10-01",
			"2020-10-02", "2020-10-05", "2020-10-06", "2020-10-07", "2020-10-08",
			// 2021
			"2021-01-01", "2021-02-11", "2021-02-12", "2021-02-15", "2021-02-16", "2021-02-17", "2021-04-05",
			"2021-05-03", "2021-05-04", "2021-05-05", "2021-06-14", "2021-09-20", "2021-09-21", "2021-10-01",
			"2021-10-04", "2021-10-05", "2021-10-06", "2021-10-07",
			// 2022
			"2022-01-03", "2022-01-31", "2022-02-01", "2022-02-02", "2022-02-03", "2022-02-04", "2022-04-04",
			"2022-04-05", "2022-05-02", "2022-05-03", "2022-05-04", "2022-06-03", "2022-09-12", "2022-10-03",
			"2022-10-04", "2022-10-05", "2022-10-06", "2022-10-07",
			// 2023
			"2023-01-02", "2023-01-23", "2023-01-24", "2023-01-25", "2023-01-26", "2023-01-27", "2023-04-05",
			"2023-05-01", "2023-05-02", "2023-05-03", "2023-06-22", "2023-06-23", "2023-09-29", "2023-10-02",
			"2023-10-03", "2023-10-04", "2023-10-05", "2023-10-06",
			// 2024

			// miss（缺失交易日数据）
			"2022-06-23", "2023-02-17", "2023-02-22", "2023-06-01", "2023-06-02", "2023-06-05" };

	private static Map<String, Long> days = new HashMap<String, Long>();
	private static List<String> dayList = new ArrayList<String>();
	private static long endTime;

	static {
		Set<String> excludeDaySet = new HashSet<String>();
		for (String s : excludeDays) {
			excludeDaySet.add(s);
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date startDay = sdf.parse("2016-01-01");
			Date endDay = sdf.parse("2023-12-31");
			endTime = endDay.getTime();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDay);
			while (true) {
				int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
				String day = sdf.format(calendar.getTime());
				if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && !excludeDaySet.contains(day)) {
					days.put(day, calendar.getTimeInMillis());
					dayList.add(day);
				}
				calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
				if (calendar.getTime().after(endDay)) {
					break;
				}
			}

		} catch (Throwable t) {

		}
	}

	public final static String[] getTradeDayList() {
		return dayList.toArray(new String[0]);
	}

	public final static String[] getTradeDayList(String startDay, String endDay) {
		List<String> matchList = new ArrayList<String>();
		boolean matchStart = false;
		for (String day : dayList) {
			if (day.equals(endDay)) {
				matchList.add(day);
				break;
			}
			if (day.equals(startDay)) {
				matchStart = true;
			}
			if (matchStart) {
				matchList.add(day);
			}
		}
		return matchList.toArray(new String[0]);
	}

	public final static String matchTradeDay(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		String day = format(calendar);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if (hour < 18) { // 18点之前都认为是当前交易日，18点之后，往后找
			if (days.containsKey(day)) {
				return day;
			}
		}
		while (true) {
			if (calendar.getTimeInMillis() > endTime) {
				return null;
			}
			calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
			day = format(calendar);
			if (days.containsKey(day)) {
				return day;
			}
		}
	}

	public final static long getTradeDayTime(String day) {
		if (days.containsKey(day)) {
			return days.get(day);
		}
		throw new IllegalArgumentException();
	}

	public final static boolean isTradeDay(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		String day = format(calendar);
		return days.containsKey(day);
	}

	private final static String format(Calendar calendar) {
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		StringBuffer buffer = new StringBuffer();
		buffer.append(year);
		buffer.append("-");
		if (month < 10) {
			buffer.append("0");
		}
		buffer.append(month);
		buffer.append("-");
		if (day < 10) {
			buffer.append("0");
		}
		buffer.append(day);
		return buffer.toString();
	}

	public final static void main(String[] args) {
		for (String day : dayList) {
			System.out.println(day);
		}
	}

}
