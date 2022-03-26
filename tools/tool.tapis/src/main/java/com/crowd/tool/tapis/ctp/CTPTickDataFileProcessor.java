package com.crowd.tool.tapis.ctp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.crowd.tool.misc.ProductDefine;
import com.crowd.tool.misc.TradeDays;
import com.crowd.tool.misc.k.HistoryData;
import com.crowd.tool.misc.k.TickInfo;
import com.crowd.tool.misc.k.TradeDayData;

public class CTPTickDataFileProcessor {

	public final static void main(String[] args) throws Throwable {
		String exName = "SHFE";
		String productName = "al";
		String symbol = exName + "." + productName;
		File sourceDir = new File("Z:\\MD\\" + symbol);
		List<String> fileNameList = new ArrayList<String>();
		for (File file : sourceDir.listFiles()) {
			if (file.isFile() && file.getAbsolutePath().endsWith(".csv")) {
				fileNameList.add(file.getName());
			}
		}
		Collections.sort(fileNameList);

		//
//		String[] info = StringUtils.split(sourceDir.getName(), ".");
		ProductDefine ctpProduct = CTPInstruments.find(productName);
		if (ctpProduct == null || !ctpProduct.getExchange().equals(exName)) {
			return;
		}
		new File(sourceDir, "daytick").mkdirs();
		new File(sourceDir, "daytime").mkdirs();
		new File(sourceDir, "kline").mkdirs();

		String finishDay = "";
		File markFile = new File(sourceDir, ".mark");
		if (markFile.exists()) {
			RandomAccessFile tradeDayFinishMarkRAF = new RandomAccessFile(markFile, "r");
			finishDay = tradeDayFinishMarkRAF.readLine().trim();
			tradeDayFinishMarkRAF.close();
		}

		int progressCount = 0;
		TradeDayData tradeDayData = null;
		for (String fileName : fileNameList) {
			// 忽略已经处理的数据文件
			if (StringUtils.isNotEmpty(finishDay)) {
				String dataMonth = fileName.substring(fileName.lastIndexOf('_') + 1, fileName.indexOf(".csv"));
				if (dataMonth.compareTo(finishDay.substring(0, 4) + finishDay.substring(5, 7)) < 0) {
					continue;
				}
			}
			RandomAccessFile raf = new RandomAccessFile(new File(sourceDir, fileName), "r");
			byte[] content = new byte[(int) raf.length()];
			raf.read(content);
			raf.close();
			ByteArrayInputStream bais = new ByteArrayInputStream(content);
			InputStreamReader streamReader = new InputStreamReader(bais);
			BufferedReader bufferedReader = new BufferedReader(streamReader);
			try {
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					TickInfo tickInfo = convertToTickInfo(line);
					if (tickInfo != null) {
						String tradeDay = TradeDays.matchTradeDay(tickInfo.getTime());
						// 忽略已经处理的数据行
						if (StringUtils.isNotEmpty(finishDay)) {
							if (tradeDay.equals(finishDay) || tradeDay.compareTo(finishDay) < 0) {
								continue;
							}
						}
						if (tradeDayData == null || !tradeDayData.getTradeDay().equals(tradeDay)) {
							// 保存数据
							save(symbol, tradeDayData, sourceDir);
							// 创建新数据
							tradeDayData = new TradeDayData(tradeDay, ctpProduct.getPriceScale());
							System.out.print("开始处理" + tradeDay + "...");
						}
						tradeDayData.onTick(tickInfo);
						progressCount++;
						if (progressCount == 1000) {
							progressCount = 0;
							System.out.print(".");
						}
					} else if (line.trim().equals("---###---")) {
						save(symbol, tradeDayData, sourceDir);
						tradeDayData = null;
					}
				}
			} finally {
				bais.close();
				streamReader.close();
				bufferedReader.close();
			}
		}
		save(symbol, tradeDayData, sourceDir);
	}

	private static void save(String symbol, TradeDayData tradeDayData, File sourceDir) throws Throwable {
		if (tradeDayData == null) {
			return;
		}

		String tradeDay = tradeDayData.getTradeDay();
		File dayTickDir = new File(sourceDir, "daytick");

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
		RandomAccessFile tradeDayFinishMarkRAF = new RandomAccessFile(new File(sourceDir, ".mark"), "rw");
		tradeDayFinishMarkRAF.setLength(0);
		tradeDayFinishMarkRAF.write(tradeDay.getBytes());
		tradeDayFinishMarkRAF.close();
		//
		System.out.println("完成");

	}

	private static TickInfo convertToTickInfo(String line) {
		try {
			String[] info = StringUtils.split(line, ",");
			TickInfo tickInfo = new TickInfo();
			tickInfo.setLabel(info[0].substring(0, info[0].length() - 6));
			tickInfo.setTime(Long.parseLong(info[1]) / 1000000);
			tickInfo.setLastPrice(new BigDecimal(info[2]));
			tickInfo.setHighestPrice(new BigDecimal(info[3]));
			tickInfo.setLowestPrice(new BigDecimal(info[4]));
			tickInfo.setVolumn(new BigDecimal(info[5]));
			tickInfo.setValue(new BigDecimal(info[6]));
			tickInfo.setOpenInterest(new BigDecimal(info[7]));
			tickInfo.setBidPrice1(getBidOrAskValue(info[8]));
			tickInfo.setBidVolumn1(getBidOrAskValue(info[9]));
			tickInfo.setAskPrice1(getBidOrAskValue(info[10]));
			tickInfo.setAskVolumn1(getBidOrAskValue(info[11]));
			if (info.length > 12) {
				tickInfo.setBidPrice2(getBidOrAskValue(info[12]));
				tickInfo.setBidVolumn2(getBidOrAskValue(info[13]));
				tickInfo.setAskPrice2(getBidOrAskValue(info[14]));
				tickInfo.setAskVolumn2(getBidOrAskValue(info[15]));
			}
			if (info.length > 16) {
				tickInfo.setBidPrice3(getBidOrAskValue(info[16]));
				tickInfo.setBidVolumn3(getBidOrAskValue(info[17]));
				tickInfo.setAskPrice3(getBidOrAskValue(info[18]));
				tickInfo.setAskVolumn3(getBidOrAskValue(info[19]));
			}
			if (info.length > 20) {
				tickInfo.setBidPrice4(getBidOrAskValue(info[20]));
				tickInfo.setBidVolumn4(getBidOrAskValue(info[21]));
				tickInfo.setAskPrice4(getBidOrAskValue(info[22]));
				tickInfo.setAskVolumn4(getBidOrAskValue(info[23]));
			}
			if (info.length > 24) {
				tickInfo.setBidPrice5(getBidOrAskValue(info[24]));
				tickInfo.setBidVolumn5(getBidOrAskValue(info[25]));
				tickInfo.setAskPrice5(getBidOrAskValue(info[26]));
				tickInfo.setAskVolumn5(getBidOrAskValue(info[27]));
			}
			return tickInfo;
		} catch (Throwable t) {
			return null;
		}
	}

	private static BigDecimal getBidOrAskValue(String s) {
		try {
			return new BigDecimal(s);
		} catch (Throwable t) {
			return BigDecimal.ZERO;
		}
	}

}
