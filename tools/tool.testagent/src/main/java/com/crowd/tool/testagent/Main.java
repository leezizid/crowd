package com.crowd.tool.testagent;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.json.JSONObject;

public class Main {

	public final static void main(String[] args) throws Throwable {
		String serviceName = "com.crowd.service.strategy.cta.CTAStrategyService";
		String arguments = "v1,0.0015,120,0.7,1.4";
		String symbol = "SHFE.ag";
		String rate = "0.00006";
		String dateSource = "20160101,20160331";

		Process process = Runtime.getRuntime()
				.exec("E:\\java\\bin\\java" + " -DServiceName=" + serviceName + " -DArguments=" + arguments
						+ " -DSymbol=" + symbol + " -DRate=" + rate + " -DDataSource=" + dateSource
						+ " -DHistoryDataDir=Z:\\MD " + "-jar E:\\Bin\\test.jar");
		InputStream inputStream = process.getInputStream();
		byte[] buffer = new byte[1024 * 4];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len = -1;
		while ((len = inputStream.read(buffer)) >= 0) {
			baos.write(buffer, 0, len);
		}
		JSONObject result = new JSONObject(new String(baos.toByteArray()));
		System.out.println(result.toString(4));
	}

}
