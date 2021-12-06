package com.crowd.service.tchannel;

import java.io.File;
import java.io.RandomAccessFile;

import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.CrowdInitContext;
import com.crowd.service.base.CrowdMethod;
import com.crowd.service.base.CrowdService;

public class HistoryDataService implements CrowdService {

	private final static String HistoryDataDir;

	static {
		HistoryDataDir = System.getProperty("HistoryDataDir");
	}

	@Override
	public void init(CrowdInitContext context) throws Throwable {

	}

	@Override
	public String getName() {
		return "history-data";
	}
	
	@CrowdMethod
	public void dayKLine(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		try {
		String symbol = input.getString("symbol");
		File dataFile = new File(
				HistoryDataDir + File.separator + symbol + File.separator + "kline" + File.separator + "1d.js");
		RandomAccessFile raf = new RandomAccessFile(dataFile, "r");
		byte[] content = new byte[(int) dataFile.length()];
		raf.read(content);
		raf.close();
		output.put("content", new String(content));
		}catch(Throwable t) {
			throw new IllegalStateException("获取历史数据失败");
		}
	}

	@CrowdMethod
	public void dayTimeLine(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		try {
		String symbol = input.getString("symbol");
		String day = input.getString("day");
		File dataFile = new File(
				HistoryDataDir + File.separator + symbol + File.separator + "daytime" + File.separator + day + ".js");
		RandomAccessFile raf = new RandomAccessFile(dataFile, "r");
		byte[] content = new byte[(int) dataFile.length()];
		raf.read(content);
		raf.close();
		output.put("content", new String(content));
		}catch(Throwable t) {
			throw new IllegalStateException("获取历史数据失败");
		}
	}

}
