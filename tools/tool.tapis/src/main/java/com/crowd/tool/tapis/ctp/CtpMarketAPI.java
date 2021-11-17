package com.crowd.tool.tapis.ctp;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Base64;

import org.json.JSONObject;

public abstract class CtpMarketAPI extends CtpBaseApi {

	private String front;

	private String[] symbols;

	public CtpMarketAPI(String id, String front, String... symbols) {
		super(id);
		this.front = front;
		this.symbols = symbols;
	}

	public void handleMessage(String type, String message) {
		JSONObject messageObject;
		try {
			// System.out.println("----" + type);
			messageObject = new JSONObject(new String(Base64.getDecoder().decode(message), "gbk"));
			// System.out.println("----" + messageObject);
			if ("M_OnFrontConnected".equals(type)) {
				CtpApiLibrary.instance.reqMarketUserLogin(id, getRequestID());
			} else if ("M_OnRspUserLogin".equals(type)) {
				CtpApiLibrary.instance.subscribe(id, symbols, symbols.length);
			} else if ("M_OnRtnDepthMarketData".equals(type)) {
				String instrumentID = messageObject.getString("InstrumentID");
				String symbol = CtpInstruments.getExchange(instrumentID) + "." + instrumentID;
				BigDecimal lowerLimitPrice = new BigDecimal(messageObject.getDouble("LowerLimitPrice"));
				BigDecimal upperLimitPrice = new BigDecimal(messageObject.getDouble("UpperLimitPrice"));
				BigDecimal price = new BigDecimal(messageObject.getDouble("LastPrice"));
				BigDecimal amount = new BigDecimal(messageObject.getInt("Volume"));
				handleMarketData(symbol, System.currentTimeMillis(), lowerLimitPrice, upperLimitPrice, price, amount);
			} else if ("M_OnFrontDisconnected".equals(type)) {

			} else if ("M_OnRspError".equals(type)) {

			}
		} catch (UnsupportedEncodingException e) {

		}
	}

	protected abstract void handleMarketData(String symbol, long time, BigDecimal lowerLimitPrice,
			BigDecimal upperLimitPrice, BigDecimal price, BigDecimal amount);

	protected void doInit(String flowDir) {
		CtpApiLibrary.instance.initMarket(id, flowDir, front, this);
	}

	protected void doRelease() {
		CtpApiLibrary.instance.releaseMarket(id);
	}

}
