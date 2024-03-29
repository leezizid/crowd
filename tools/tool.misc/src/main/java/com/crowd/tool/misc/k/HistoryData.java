package com.crowd.tool.misc.k;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.crowd.tool.misc.TradeDays;

public class HistoryData {

	private final static int DAY_MILLIS = 24 * 60 * 60 * 1000;

	private static String HistoryDataDir;

	private final static Map<String, byte[]> cache = new HashMap<String, byte[]>();
	private final static List<String> keyList = new ArrayList<String>();

	static {
		HistoryDataDir = System.getProperty("HistoryDataDir");
		if(StringUtils.isEmpty(HistoryDataDir)) {
			HistoryDataDir = System.getProperty("user.dir") + File.separator + "MD";
		}
	}

	public final static void main(String[] args) throws Throwable {
		String pname = "CZCE.AP";
		String period = "5m";
		String[][] dataArray = readKLineData(pname, period, "2016-01-01", "2021-12-31");
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < dataArray.length; i++) {
			String[] dataRow = dataArray[i];
			long time = Long.parseLong(dataRow[0]);
			String openPrice = dataRow[1];
			String closePrice = dataRow[2];
			String highPrice = dataRow[3];
			String lowPrice = dataRow[4];
			String avgPrice = dataRow[5];
			String volume = dataRow[6];
			String openInterest1 = "0";
			String openInterest2 = dataRow[7];
			if (i > 0) {
				String[] prevDataRow = dataArray[i - 1];
				openInterest1 = prevDataRow[7];
				//
				if (volume.equals("0")) {
					// 如果当前量为0，则价格全部取上一数据的收盘价格
					openPrice = dataRow[1] = closePrice = dataRow[2] = highPrice = dataRow[3] = lowPrice = dataRow[4] = avgPrice = dataRow[5] = prevDataRow[2];
					openInterest2 = dataRow[7] = openInterest1;
				} else {
					int n = i - 1;
					// 如果数据跨度属于正常周期，则开盘价格取上次收盘价格
					while (true) {
						if (n < 0 || (time - Long.parseLong(dataArray[n][0])) > (1000 * 60 * 5 * (i - n))) {
							break;
						}
						if (!dataArray[n][6].equals("0")) {
							openPrice = dataArray[n][2];
							break;
						}
						n--;
					}
				}
			}
			stringBuffer.append("[");
			stringBuffer.append(time);
			stringBuffer.append(",");
			stringBuffer.append(openPrice);
			stringBuffer.append(",");
			stringBuffer.append(closePrice);
			stringBuffer.append(",");
			stringBuffer.append(highPrice);
			stringBuffer.append(",");
			stringBuffer.append(lowPrice);
			stringBuffer.append(",");
			stringBuffer.append(avgPrice);
			stringBuffer.append(",");
			stringBuffer.append(volume);
			stringBuffer.append(",");
			stringBuffer.append(openInterest1);
			stringBuffer.append(",");
			stringBuffer.append(openInterest2);
			stringBuffer.append("]");
			if (i < dataArray.length - 1) {
				stringBuffer.append(",");
			}
		}
		RandomAccessFile klineRAF = new RandomAccessFile(new File("Z:\\MD\\kline\\" + pname + "." + period + ".txt"),
				"rw");
		klineRAF.setLength(0);
		klineRAF.write(stringBuffer.toString().getBytes());
		klineRAF.close();
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
		String path = HistoryDataDir + File.separator + symbol + File.separator + "daytick" + File.separator + tradeDay
				+ ".dat";
		if (!cache.containsKey(path)) {
			//
			while (cache.size() >= 256) {
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

	public final static String[][] readKLineData(String symbol, String type, String startDay, String endDay)
			throws Throwable {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long startTime = sdf.parse(startDay).getTime();
		long endTime = sdf.parse(endDay).getTime();
		if (endTime < startTime) {
			throw new IllegalArgumentException("结束时间不能小于开始时间");
		}
		File file = new File(
				HistoryDataDir + File.separator + symbol + File.separator + "kline" + File.separator + type + ".dat");
		if (!file.exists()) {
			throw new IllegalArgumentException("无法找到指定品种和时间类型的K线数据");
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
//		if (type.equals("1d")) {
//			sdf = new SimpleDateFormat("yyyy-MM-dd");
//		} else {
//			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		}
		String[][] dataArray = new String[(endIndex - startIndex) / 36][];
		raf.seek(startIndex);
		for (int i = 0; i < dataArray.length; i++) {
			StringBuffer buffer = new StringBuffer();
			long time = raf.readLong();
			int upOrDown = (int) (time % 10);
			time = (time / 10) * 10;
			buffer.append(time);
			buffer.append(",");
//			buffer.append("'");
//			buffer.append(sdf.format(new Date(time)));
//			buffer.append("'");
//			buffer.append(",");
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
			buffer.append(raf.readInt()); // 收盘持仓量
			buffer.append(",");
			buffer.append(upOrDown);
			dataArray[i] = StringUtils.split(buffer.toString(), ',');
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
			// 初始化数据文件
			raf.setLength(64 * 1024);
			raf.seek(0);
			raf.writeLong(1451577600000L); // 起始日期2016-01-01
			raf.writeLong(1451577600000L); // 当前日期2016-01-01
			raf.writeLong(64 * 1024); // 当前文件有效长度（含头部）
			raf.seek(64); // 64字节头部（目前用到8+8+8字节，后面预留）
			raf.writeInt(64 * 1024); // 起始日期对应数据位置
		}
		//
		raf.seek(0);
		long baseTime = raf.readLong();
		long lastMarkTime = raf.readLong();
		long validSize = raf.readLong();
		if (tradeDayTime <= lastMarkTime) {
			throw new IllegalStateException("指定交易日数据已经存在，不能重复写入");
		}
		//
		int nDays = (int) ((lastMarkTime - baseTime) / DAY_MILLIS);
		raf.seek(64 + nDays * 4 + 4);
		nDays = (int) ((tradeDayTime - lastMarkTime) / DAY_MILLIS);
		long currentDayTime = lastMarkTime;
		for (int i = 0; i < nDays; i++) {
			// 校验是否存在交易日没有数据
			currentDayTime = currentDayTime + DAY_MILLIS;
			if (i < nDays - 1 && TradeDays.isTradeDay(currentDayTime)) {
				throw new IllegalStateException("不能跨交易日写入数据");
			}
			// 写入索引值
			raf.writeInt((int) validSize);
		}
		// 定位到有效数据区域最后位置，准备开始写入数据
		raf.seek(validSize);
	}

	// 完成写入数据后调用，标记数完整性，如果没有正确标记，则可以在prepareWriteData阶段识别出无效数据
	private final static void postWriteKLineData(RandomAccessFile raf, long tradeDayTime) throws Throwable {
		long valieSize = raf.getFilePointer();
		// 基于目前写入的数据的位置，作为文件的有效长度（对于之前可能存在写入错误数据的纠正），同时写入最后标记时间和文件有效大小
		raf.setLength(valieSize);
		raf.seek(8);
		raf.writeLong(tradeDayTime);
		raf.writeLong(valieSize);
	}

}
