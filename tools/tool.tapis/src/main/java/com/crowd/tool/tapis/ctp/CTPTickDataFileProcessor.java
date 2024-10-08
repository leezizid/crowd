package com.crowd.tool.tapis.ctp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.crowd.tool.misc.ProductDefine;
import com.crowd.tool.misc.TradeDays;
import com.crowd.tool.misc.k.HistoryData;
import com.crowd.tool.misc.k.TickData;
import com.crowd.tool.misc.k.TradeDayData;

public class CTPTickDataFileProcessor {

//	public final static void main0(String[] args) throws Throwable {
//		File sourceDir = new File("F:\\MD_CFFEX\\");
//		for (File dir : sourceDir.listFiles()) {
//			String name = dir.getName();
//			System.out.println(name + "...");
//			RandomAccessFile raf = new RandomAccessFile(
//					dir.getAbsolutePath() + File.separator + name + "_tick_202211.csv", "rw");
//			RandomAccessFile raf2 = new RandomAccessFile(
//					dir.getAbsolutePath() + File.separator + name + "_tick_202214.txt", "rw");
//			raf2.setLength(0);
//			long length = 0;
//			String line = raf.readLine();
//			while ((line = raf.readLine()) != null) {
//				String[] arr = StringUtils.split(line, ",");
//				long time = Long.parseLong(arr[1]);
//				if (time < 1668171600000000000L) {
//					length = raf.getFilePointer();
//				} else {
//					String timeStr = arr[0].replace(" ", "").replace("-", "").replace(":", "").replace(".", "")
//							.substring(2, 17);
//					raf2.writeBytes(name + "," + timeStr + "," + arr[2] + "," + arr[5] + "," + arr[7] + "," + arr[8]
//							+ "," + arr[9] + "," + arr[10] + "," + arr[11] + "\r\n");
//				}
//			}
//			raf.setLength(length);
//			raf.close();
//			raf2.close();
//		}
//	}

//	public final static void main(String[] args) throws Throwable {
//		String exName = "CFFEX";
//		String[] productNames = new String[] { "IC", "IF", "IH", "IM", "T", "TF", "TS" };
//		for (String productName : productNames) {
//			processTQTickFile(exName, productName);
//		}
//		exName = "INE";
//		productNames = new String[] { "bc", "lu", "nr", "sc" };
//		for (String productName : productNames) {
//			processTQTickFile(exName, productName);
//		}
//		exName = "CZCE";
//		productNames = new String[] { "AP", "CF", "CJ", "CY", "FG", "LR", "MA", "OI", "PF", "PK", "PM", "RI", "RM",
//				"RS", "SA", "SF", "SM", "SR", "TA", "UR", "WH", "ZC" };
//		for (String productName : productNames) {
//			processTQTickFile(exName, productName);
//		}
//		exName = "DCE";
//		productNames = new String[] { "a", "b", "bb", "c", "cs", "eb", "eg", "fb", "i", "j", "jd", "jm", "l", "lh", "m",
//				"p", "pg", "pp", "rr", "v", "y" };
//		for (String productName : productNames) {
//			processTQTickFile(exName, productName);
//		}
//		exName = "SHFE";
//		productNames = new String[] { "ag", "al", "au", "bu", "cu", "fu", "hc", "ni", "pb", "rb", "ru", "sn",
//				"sp", "ss", "wr", "zn" };
//		for (String productName : productNames) {
//			processTQTickFile(exName, productName);
//		}
//	}

	public final static void main(String[] args) throws Throwable {
		//
		processMDStreams();

		//
		String exName = "CFFEX";
		String[] productNames = new String[] { "IC", "IF", "IH", "IM", "T", "TF", "TS" };
		for (String productName : productNames) {
			processMDStreamFile(exName, productName);
		}
		//
		exName = "INE";
		productNames = new String[] { "bc", "lu", "nr", "sc" };
		for (String productName : productNames) {
			processMDStreamFile(exName, productName);
		}
		//
		exName = "CZCE";
		productNames = new String[] { "AP", "CF", "CJ", "CY", "FG", "LR", "MA", "OI", "PF", "PK", "PM", "RI", "RM",
				"RS", "SA", "SF", "SM", "SR", "TA", "UR", "WH", "ZC" };
		for (String productName : productNames) {
			processMDStreamFile(exName, productName);
		}
		//
		exName = "DCE";
		productNames = new String[] { "a", "b", "bb", "c", "cs", "eb", "eg", "fb", "i", "j", "jd", "jm", "l", "lh", "m",
				"p", "pg", "pp", "rr", "v", "y" };
		for (String productName : productNames) {
			processMDStreamFile(exName, productName);
		}
		//
		exName = "SHFE";
		productNames = new String[] { "ag", "al", "au", "bu", "cu", "fu", "hc", "ni", "pb", "rb", "ru", "sn", "sp",
				"ss", "wr", "zn" };
		for (String productName : productNames) {
			processMDStreamFile(exName, productName);
		}
	}

	public final static void processMDStreams() throws Throwable {
		File sourceDir = new File("E:\\mdstream");
		Set<String> tradeDays = new HashSet<String>();
		for (File file : sourceDir.listFiles()) {
			if (file.getName().length() >= 16) {
				tradeDays.add(file.getName().substring(0, 10));
			}
		}
		List<String> tradeDayList = new ArrayList<String>();
		tradeDayList.addAll(tradeDays);
		tradeDayList.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		for (String tradeDay : tradeDayList) {
			processMDStream(tradeDay);
		}
	}

	public final static void processMDStream(String tradeDay) throws Throwable {
		File sourceDir = new File("E:\\mdstream");
		File targetDir = new File("Z:\\BAK");
//		String tradeDay = "2023-03-24";
		int index = 0;
		Map<String, StringBuffer> buffers = new HashMap<String, StringBuffer>();
		while (true) {
			File sourceFile = new File(sourceDir, tradeDay + "_" + index + ".txt");
			if (!sourceFile.exists()) {
				break;
			}
			byte[] content = new byte[(int) sourceFile.length()];
			RandomAccessFile sourceRaf = new RandomAccessFile(sourceFile, "r");
			sourceRaf.read(content);
			sourceRaf.close();
			//
			ByteArrayInputStream bais = new ByteArrayInputStream(content);
			InputStreamReader streamReader = new InputStreamReader(bais);
			BufferedReader bufferedReader = new BufferedReader(streamReader);
			try {
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					String[] arr = StringUtils.split(line, ",");
					String[] productInfo = StringUtils.split(arr[0], ".");
					ProductDefine productDefine = CTPProducts.find(productInfo[1]);
					String fileName = targetDir.getAbsolutePath() + File.separator + productDefine.getExchange() + "."
							+ productDefine.getName() + File.separator + tradeDay + ".txt";
					StringBuffer buffer = buffers.get(fileName);
					if (buffer == null) {
						buffer = new StringBuffer();
						buffers.put(fileName, buffer);
					}
					buffer.append(line);
					buffer.append("\r\n");
				}
			} finally {
				bais.close();
				streamReader.close();
				bufferedReader.close();
			}
			index++;
		}
		for (String fileName : buffers.keySet()) {
			File file = new File(fileName);
			if (file.exists()) {
				continue;
			}
			file.getParentFile().mkdir();
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.setLength(0);
			raf.write(buffers.get(fileName).toString().getBytes());
			raf.close();
		}
	}

	public final static void processMDStreamFile(String exName, String productName) throws Throwable {
		String symbol = exName + "." + productName;
		File sourceDir = new File("Z:\\BAK\\" + symbol);
		File targetDir = new File("Z:\\MD\\" + symbol);
		final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
		//
		ProductDefine ctpProduct = CTPProducts.find(productName);
		if (ctpProduct == null || !ctpProduct.getExchange().equals(exName)) {
			return;
		}
		new File(targetDir, "daytick").mkdirs();
		new File(targetDir, "daytime").mkdirs();
		new File(targetDir, "kline").mkdirs();
		String[] tradeDays = TradeDays.getTradeDayList();
		int currentTradeDayIndex = 0;
		File markFile = new File(targetDir, ".mark");
		if (markFile.exists()) {
			RandomAccessFile tradeDayFinishMarkRAF = new RandomAccessFile(markFile, "r");
			String finishDay = tradeDayFinishMarkRAF.readLine().trim();
			tradeDayFinishMarkRAF.close();
			if (StringUtils.isNotEmpty(finishDay)) {
				for (int i = 0; i < tradeDays.length; i++) {
					if (tradeDays[i].equals(finishDay)) {
						currentTradeDayIndex = i + 1;
						break;
					}
				}
			}
		}
		//
		if (currentTradeDayIndex == tradeDays.length) {
			throw new IllegalStateException("所有可用交易日均处理完毕");
		}
		//
		for (int i = currentTradeDayIndex; i < tradeDays.length; i++) {
			String tradeDay = tradeDays[i];
			File file = new File(sourceDir, tradeDay + ".txt");
			if (!file.exists()) {
				break;
			}
			TradeDayData tradeDayData = new TradeDayData(tradeDay, ctpProduct);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			byte[] content = new byte[(int) raf.length()];
			raf.read(content);
			raf.close();
			ByteArrayInputStream bais = new ByteArrayInputStream(content);
			InputStreamReader streamReader = new InputStreamReader(bais);
			BufferedReader bufferedReader = new BufferedReader(streamReader);
			try {
				String line = null;
				long lastTime = 0;
				while (StringUtils.isNotEmpty(line = bufferedReader.readLine())) {
					String[] info = StringUtils.split(line, ",");
					long time = sdf.parse(info[1]).getTime();
					if (time <= lastTime) {
						continue;
					}
					TickData tickData = new TickData();
					tickData.setLabel(info[1]);
					tickData.setTime(time);
					tickData.setLastPrice(new BigDecimal(info[2]));
					tickData.setVolume(new BigDecimal(info[3]));
					tickData.setOpenInterest(new BigDecimal(info[4]));
					tickData.setBidPrice1(getBidOrAskValue(info[5]));
					tickData.setBidVolume1(getBidOrAskValue(info[6]));
					tickData.setAskPrice1(getBidOrAskValue(info[7]));
					tickData.setAskVolume1(getBidOrAskValue(info[8]));
					tradeDayData.onTick(tickData);
					lastTime = time;
				}
			} finally {
				bais.close();
				streamReader.close();
				bufferedReader.close();
			}
			save(symbol, tradeDayData, targetDir);
		}
	}

//	public final static void processTQTickFile(String exName, String productName) throws Throwable {
//		String symbol = exName + "." + productName;
//		File sourceDir = new File("Z:\\BAK\\" + symbol);
//		File targetDir = new File("Z:\\MD\\" + symbol);
//		if(!sourceDir.exists()) {
//			return;
//		}
//		List<String> fileNameList = new ArrayList<String>();
//		for (File file : sourceDir.listFiles()) {
//			if (file.isFile() && file.getAbsolutePath().endsWith(".csv")) {
//				fileNameList.add(file.getName());
//			}
//		}
//		Collections.sort(fileNameList);
//
//		//
////		String[] info = StringUtils.split(sourceDir.getName(), ".");
//		ProductDefine ctpProduct = CTPProducts.find(productName);
//		if (ctpProduct == null || !ctpProduct.getExchange().equals(exName)) {
//			return;
//		}
//		new File(targetDir, "daytick").mkdirs();
//		new File(targetDir, "daytime").mkdirs();
//		new File(targetDir, "kline").mkdirs();
//
//		String finishDay = "";
//		File markFile = new File(targetDir, ".mark");
//		if (markFile.exists()) {
//			RandomAccessFile tradeDayFinishMarkRAF = new RandomAccessFile(markFile, "r");
//			finishDay = tradeDayFinishMarkRAF.readLine().trim();
//			tradeDayFinishMarkRAF.close();
//		}
//		//
//		String[] tradeDays = TradeDays.getTradeDayList();
//		int tradeDayIndex = 0;
//		if (StringUtils.isNotEmpty(finishDay)) {
//			for (int i = 0; i < tradeDays.length; i++) {
//				if (tradeDays[i].equals(finishDay)) {
//					tradeDayIndex = i + 1;
//					break;
//				}
//			}
//		}
//		if (tradeDayIndex == tradeDays.length) {
//			throw new IllegalStateException("所有可用交易日均处理完毕");
//		}
//		//
//		int progressCount = 0;
//		TradeDayData tradeDayData = null;
//		for (String fileName : fileNameList) {
//			// 忽略已经处理的数据文件
//			if (StringUtils.isNotEmpty(finishDay)) {
//				String dataMonth = fileName.substring(fileName.lastIndexOf('_') + 1, fileName.indexOf(".csv"));
//				if (dataMonth.compareTo(finishDay.substring(0, 4) + finishDay.substring(5, 7)) < 0) {
//					continue;
//				}
//			}
//			RandomAccessFile raf = new RandomAccessFile(new File(sourceDir, fileName), "r");
//			byte[] content = new byte[(int) raf.length()];
//			raf.read(content);
//			raf.close();
//			ByteArrayInputStream bais = new ByteArrayInputStream(content);
//			InputStreamReader streamReader = new InputStreamReader(bais);
//			BufferedReader bufferedReader = new BufferedReader(streamReader);
//			try {
//				String line = null;
//				while ((line = bufferedReader.readLine()) != null) {
//					TickData tickData = convertToTickData(line);
//					if (tickData != null) {
//						String tradeDay = TradeDays.matchTradeDay(tickData.getTime());
//						// 忽略已经处理的数据行
//						if (StringUtils.isNotEmpty(finishDay)) {
//							if (tradeDay.equals(finishDay) || tradeDay.compareTo(finishDay) < 0) {
//								continue;
//							}
//						}
//						if (tradeDayData == null || !tradeDayData.getTradeDay().equals(tradeDay)) {
//							// 保存数据
//							save(symbol, tradeDayData, targetDir);
//							// 创建新数据
//							while (!tradeDay.equals(tradeDays[tradeDayIndex])) {
//								tradeDayData = new TradeDayData(tradeDays[tradeDayIndex], ctpProduct);
//								System.out.print("默认处理" + tradeDays[tradeDayIndex] + "***");
//								//
//								TickData mockTickData = new TickData();
//								mockTickData.setTime(
//										TradeDays.getTradeDayTime(tradeDays[tradeDayIndex]) + 1000 * 60 * 60 * 15);
//								mockTickData.setLastPrice(BigDecimal.ZERO);
//								mockTickData.setVolume(BigDecimal.ZERO);
//								mockTickData.setOpenInterest(BigDecimal.ZERO);
//								mockTickData.setBidPrice1(BigDecimal.ZERO);
//								mockTickData.setBidVolume1(BigDecimal.ZERO);
//								mockTickData.setAskPrice1(BigDecimal.ZERO);
//								mockTickData.setAskVolume1(BigDecimal.ZERO);
//								tradeDayData.onTick(mockTickData);
//								save(symbol, tradeDayData, targetDir);
//								tradeDayIndex++;
//							}
//							tradeDayData = new TradeDayData(tradeDay, ctpProduct);
//							System.out.print("开始处理" + tradeDay + "...");
//							tradeDayIndex++;
//						}
//						tradeDayData.onTick(tickData);
//						progressCount++;
//						if (progressCount == 1000) {
//							progressCount = 0;
//							System.out.print(".");
//						}
//					} else if (line.trim().equals("---###---")) {
//						save(symbol, tradeDayData, targetDir);
//						tradeDayData = null;
//					}
//				}
//			} finally {
//				bais.close();
//				streamReader.close();
//				bufferedReader.close();
//			}
//		}
//		save(symbol, tradeDayData, targetDir);
//	}

	private static void save(String symbol, TradeDayData tradeDayData, File targetDir) throws Throwable {
		if (tradeDayData == null) {
			return;
		}

		String tradeDay = tradeDayData.getTradeDay();
		File dayTickDir = new File(targetDir, "daytick");

		System.out.print("正在写入文件");

		//
		HistoryData.writeDaytimeData(symbol, tradeDayData);
		System.out.print(".");
		Thread.sleep(10);

//		RandomAccessFile tradeDayTickCsvRAF = new RandomAccessFile(new File(dayTickDir, tradeDay + ".csv"), "rw");
//		tradeDayTickCsvRAF.setLength(0);
//		tradeDayData.writeTickDataToCsv(tradeDayTickCsvRAF);
//		tradeDayTickCsvRAF.close();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		tradeDayData.writeTickDataToStream(new DataOutputStream(baos));
		RandomAccessFile tradeDayTickDatRAF = new RandomAccessFile(new File(dayTickDir, tradeDay + ".dat"), "rw");
		tradeDayTickDatRAF.setLength(0);
		tradeDayTickDatRAF.write(baos.toByteArray());
		tradeDayTickDatRAF.close();
		System.out.print(".");
		Thread.sleep(10);

		//
		HistoryData.writeKLineData(symbol, tradeDayData);
		System.out.print(".");
		Thread.sleep(10);

		// 写入标记文件
		RandomAccessFile tradeDayFinishMarkRAF = new RandomAccessFile(new File(targetDir, ".mark"), "rw");
		tradeDayFinishMarkRAF.setLength(0);
		tradeDayFinishMarkRAF.write(tradeDay.getBytes());
		tradeDayFinishMarkRAF.close();
		//
		System.out.println("完成");

	}

//	private static TickData convertToTickData(String line) {
//		try {
//			String[] info = StringUtils.split(line, ",");
//			long time = Long.parseLong(info[1]);
//			TickData tickData = new TickData();
//			tickData.setLabel(info[0].substring(0, info[0].length() - 6));
//			tickData.setTime(time / 1000000 + ((time / 1000000) % 1000 > 0 ? 0 : (time % 1000000) / 1000)); // 某些tick数据时间有问题，毫秒数为0，但是后面有更小的精度数值
//			tickData.setLastPrice(new BigDecimal(info[2]));
////			tickData.setHighestPrice(new BigDecimal(info[3]));
////			tickData.setLowestPrice(new BigDecimal(info[4]));
//			tickData.setVolume(new BigDecimal(info[5]));
////			tickData.setValue(new BigDecimal(info[6]));
//			tickData.setOpenInterest(new BigDecimal(info[7]));
//			tickData.setBidPrice1(getBidOrAskValue(info[8]));
//			tickData.setBidVolume1(getBidOrAskValue(info[9]));
//			tickData.setAskPrice1(getBidOrAskValue(info[10]));
//			tickData.setAskVolume1(getBidOrAskValue(info[11]));
////			if (info.length > 12) {
////				tickData.setBidPrice2(getBidOrAskValue(info[12]));
////				tickData.setBidVolume2(getBidOrAskValue(info[13]));
////				tickData.setAskPrice2(getBidOrAskValue(info[14]));
////				tickData.setAskVolume2(getBidOrAskValue(info[15]));
////			}
////			if (info.length > 16) {
////				tickData.setBidPrice3(getBidOrAskValue(info[16]));
////				tickData.setBidVolume3(getBidOrAskValue(info[17]));
////				tickData.setAskPrice3(getBidOrAskValue(info[18]));
////				tickData.setAskVolume3(getBidOrAskValue(info[19]));
////			}
////			if (info.length > 20) {
////				tickData.setBidPrice4(getBidOrAskValue(info[20]));
////				tickData.setBidVolume4(getBidOrAskValue(info[21]));
////				tickData.setAskPrice4(getBidOrAskValue(info[22]));
////				tickData.setAskVolume4(getBidOrAskValue(info[23]));
////			}
////			if (info.length > 24) {
////				tickData.setBidPrice5(getBidOrAskValue(info[24]));
////				tickData.setBidVolume5(getBidOrAskValue(info[25]));
////				tickData.setAskPrice5(getBidOrAskValue(info[26]));
////				tickData.setAskVolume5(getBidOrAskValue(info[27]));
////			}
//			return tickData;
//		} catch (Throwable t) {
//			return null;
//		}
//	}

	private static BigDecimal getBidOrAskValue(String s) {
		try {
			return new BigDecimal(s);
		} catch (Throwable t) {
			return BigDecimal.ZERO;
		}
	}

}
