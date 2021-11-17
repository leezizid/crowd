package com.crowd.tool.misc;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PyHelper {

	
	public final static String toShortPinYinString(String str) {
		if(str == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		String[] arr = null;
		for (int i = 0; i < str.length(); i++) {
			arr = PinyinHelper.toHanyuPinyinStringArray(str.charAt(i));
			if (arr != null && arr.length > 0) {
				sb.append(arr[0].charAt(0));
			} else {
				sb.append(str.charAt(i));
			}
		}
		return sb.toString();
	}
	
	public final static String toPinYinString(String str) {
		if(str == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		String[] arr = null;

		for (int i = 0; i < str.length(); i++) {
			arr = PinyinHelper.toHanyuPinyinStringArray(str.charAt(i));
			if (arr != null && arr.length > 0) {
				for (String string : arr) {
					sb.append(string);
				}
			} else {
				sb.append(str.charAt(i));
			}
		}

		return sb.toString();
	}
	
}
