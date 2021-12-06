package com.crowd.tool.misc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

	public final static Date getDayStart(Date date) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		return now.getTime();
	}

	public final static Date getDayEnd(Date date) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.HOUR_OF_DAY, 23);
		now.set(Calendar.MINUTE, 59);
		now.set(Calendar.SECOND, 59);
		now.set(Calendar.MILLISECOND, 999);
		return now.getTime();
	}

	public final static Date getTodayStart() {
		return getDayStart(new Date());
	}

	public final static Date getTodayEnd() {
		return getDayEnd(new Date());
	}

	public final static Date convertStartDate(String s) {
		try {
			Calendar now = Calendar.getInstance();
			now.setTime(new Date(Long.parseLong(s)));
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.MILLISECOND, 0);
			return now.getTime();
		} catch (Throwable t) {
			return null;
		}
	}

	public final static Date convertEndDate(String s) {
		try {
			Calendar now = Calendar.getInstance();
			now.setTime(new Date(Long.parseLong(s)));
			now.set(Calendar.HOUR_OF_DAY, 23);
			now.set(Calendar.MINUTE, 59);
			now.set(Calendar.SECOND, 59);
			now.set(Calendar.MILLISECOND, 999);
			return now.getTime();
		} catch (Throwable t) {
			return null;
		}
	}
	
	public final static String dateTime2String(Date date) {
		if(date == null || date.getTime() == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(date);
	}

	public final static String date2String(Date date) {
		if(date == null || date.getTime() == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format(date);
	}
	
	
	public final static String time2String(Date date) {
		if(date == null || date.getTime() == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(date);
	}

}
