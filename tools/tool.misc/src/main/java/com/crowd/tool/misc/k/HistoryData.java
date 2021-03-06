package com.crowd.tool.misc.k;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crowd.tool.misc.TradeDays;

public class HistoryData {

	private final static int DAY_MILLIS = 24 * 60 * 60 * 1000;

	private final static String HistoryDataDir;
	
	
	private final static Map<String,byte[]> cache = new HashMap<String, byte[]>();
	private final static List<String> keyList = new ArrayList<String>();
	

	static {
		HistoryDataDir = System.getProperty("HistoryDataDir");
	}

	public final static void main(String[] args) throws Throwable {
		String[] dataArray = readKLineData("SHFE.au", "1h", "2016-01-01", "2022-12-31");
		for (String s : dataArray) {
			System.out.println(s);
		}
	}

	public final static void writeDaytimeData(String symbol, TradeDayData tradeDayData) throws Throwable {
		File dataFile = new File(HistoryDataDir + File.separator + symbol + File.separator + "daytime" + File.separator
				+ tradeDayData.getTradeDay() + ".js");
		RandomAccessFile tradeDayTimeLineRAF = new RandomAccessFile(dataFile, "rw");
		tradeDayTimeLineRAF.setLength(0);
		tradeDayTimeLineRAF.write(tradeDayData.get1mKLineDataString().getBytes());
		tradeDayTimeLineRAF.close();
	}

	public final static String readDaytimeData(String symbol, String tradeDay) throws Throwable {
		File dataFile = new File(HistoryDataDir + File.separator + symbol + File.separator + "daytime" + File.separator
				+ tradeDay + ".js");
		RandomAccessFile raf = new RandomAccessFile(dataFile, "r");
		byte[] content = new byte[(int) dataFile.length()];
		raf.read(content);
		raf.close();
		return new String(content);
	}

	public final synchronized static byte[] readTradeDayTickData(String symbol, String tradeDay) throws Throwable {
		String path = HistoryDataDir + File.separator + symbol + File.separator + "daytick" + File.separator
				+ tradeDay + ".dat";
		if(!cache.containsKey(path)) {
			//
			while(cache.size() >= 256) {
				cache.remove(keyList.remove(0));
			}
			//
			File dataFile = new File(path);
			RandomAccessFile raf = new RandomAccessFile(dataFile, "r");
			byte[] content = new byte[(int) dataFile.length()];
			raf.read(content);
			raf.close();
			//
			keyList.add(path);
			cache.put(path, content);
		}
		return cache.get(path);
	}

	public final static String[] readKLineData(String symbol, String type, String startDay, String endDay)
			throws Throwable {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long startTime = sdf.parse(startDay).getTime();
		long endTime = sdf.parse(endDay).getTime();
		if (endTime < startTime) {
			throw new IllegalArgumentException("????????????????????????????????????");
		}
		File file = new File(
				HistoryDataDir + File.separator + symbol + File.separator + "kline" + File.separator + type + ".dat");
		if (!file.exists()) {
			throw new IllegalArgumentException("??????????????????????????????????????????K?????????");
		}
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		long baseTime = raf.readLong();
		raf.readLong();
		int validSize = (int) raf.readLong();
		//
		int nDays = (int) ((startTime - baseTime) / DAY_MILLIS);
		raf.seek(64 + nDays * 4);
		int startIndex = raf.readInt();
		if (startIndex == 0) {
			startIndex = validSize;
		}
		if (startIndex > validSize) {
			startIndex = validSize;
		}
		//
		nDays = (int) ((endTime - baseTime) / DAY_MILLIS) + 1;
		raf.seek(64 + nDays * 4);
		int endIndex = raf.readInt();
		if (endIndex == 0) {
			endIndex = (int) validSize;
		}
		if (endIndex > validSize) {
			endIndex = validSize;
		}
		//
		if (type.equals("1d")) {
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		} else {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}
		String[] dataArray = new String[(endIndex - startIndex) / 32];
		raf.seek(startIndex);
		for (int i = 0; i < dataArray.length; i++) {
			StringBuffer buffer = new StringBuffer();
			long time = raf.readLong();
			int upOrDown = (int) (time % 10);
			time = (time / 10) * 10;
			buffer.append("[");
			buffer.append(time);
			buffer.append(",");
			buffer.append("'");
			buffer.append(sdf.format(new Date(time)));
			buffer.append("'");
			buffer.append(",");
			buffer.append(raf.readFloat());
			buffer.append(",");
			buffer.append(raf.readFloat());
			buffer.append(",");
			buffer.append(raf.readFloat());
			buffer.append(",");
			buffer.append(raf.readFloat());
			buffer.append(",");
			buffer.append(raf.readFloat());
			buffer.append(",");
			buffer.append(raf.readInt());
			buffer.append(",");
			buffer.append(upOrDown);
			buffer.append("]");
			dataArray[i] = buffer.toString();
		}
		raf.close();
		return dataArray;
	}

	public final static void writeKLineData(String symbol, TradeDayData tradeDayData) throws Throwable {
		File klineDir = new File(HistoryDataDir + File.separator + symbol + File.separator + "kline");
		//
		RandomAccessFile raf = new RandomAccessFile(new File(klineDir, "1d.dat"), "rw");
		prepareWriteKLineData(raf, tradeDayData.getTradeDayTime());
		tradeDayData.write1dToStream(raf);
		postWriteKLineData(raf, tradeDayData.getTradeDayTime());
		raf.close();

		//
		raf = new RandomAccessFile(new File(klineDir, "1h.dat"), "rw");
		prepareWriteKLineData(raf, tradeDayData.getTradeDayTime());
		tradeDayData.write1hToStream(raf);
		postWriteKLineData(raf, tradeDayData.getTradeDayTime());
		raf.close();

		//
		raf = new RandomAccessFile(new File(klineDir, "15m.dat"), "rw");
		prepareWriteKLineData(raf, tradeDayData.getTradeDayTime());
		tradeDayData.write15mToStream(raf);
		postWriteKLineData(raf, tradeDayData.getTradeDayTime());
		raf.close();

		//
		raf = new RandomAccessFile(new File(klineDir, "5m.dat"), "rw");
		prepareWriteKLineData(raf, tradeDayData.getTradeDayTime());
		tradeDayData.write5mToStream(raf);
		postWriteKLineData(raf, tradeDayData.getTradeDayTime());
		raf.close();
	}

	private final static void prepareWriteKLineData(RandomAccessFile raf, long tradeDayTime) throws Throwable {
		if (raf.length() == 0) {
			// ?????????????????????
			raf.setLength(64 * 1024);
			raf.seek(0);
			raf.writeLong(1451577600000L); // ????????????2016-01-01
			raf.writeLong(1451577600000L); // ????????????2016-01-01
			raf.writeLong(64 * 1024); // ???????????????????????????????????????
			raf.seek(64); // 64???????????????????????????8+8+8????????????????????????
			raf.writeInt(64 * 1024); // ??????????????????????????????
		}
		//
		raf.seek(0);
		long baseTime = raf.readLong();
		long lastMarkTime = raf.readLong();
		long validSize = raf.readLong();
		if (tradeDayTime <= lastMarkTime) {
			throw new IllegalStateException("??????????????????????????????????????????????????????");
		}
		//
		int nDays = (int) ((lastMarkTime - baseTime) / DAY_MILLIS);
		raf.seek(64 + nDays * 4 + 4);
		nDays = (int) ((tradeDayTime - lastMarkTime) / DAY_MILLIS);
		long currentDayTime = lastMarkTime;
		for (int i = 0; i < nDays; i++) {
			// ???????????????????????????????????????
			currentDayTime = currentDayTime + DAY_MILLIS;
			if (i < nDays - 1 && TradeDays.isTradeDay(currentDayTime)) {
				throw new IllegalStateException("??????????????????????????????");
			}
			// ???????????????
			raf.writeInt((int) validSize);
		}
		// ??????????????????????????????????????????????????????????????????
		raf.seek(validSize);
	}

	// ??????????????????????????????????????????????????????????????????????????????????????????prepareWriteData???????????????????????????
	private final static void postWriteKLineData(RandomAccessFile raf, long tradeDayTime) throws Throwable {
		long valieSize = raf.getFilePointer();
		// ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		raf.setLength(valieSize);
		raf.seek(8);
		raf.writeLong(tradeDayTime);
		raf.writeLong(valieSize);
	}

}
