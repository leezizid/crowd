package com.crowd.tool.tapis.ctp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Base64;

import org.json.JSONObject;

public abstract class CtpMarketAPI extends CtpBaseApi {

	private String front;

	private String[] symbols;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");

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
				handleConnected();
			} else if ("M_OnRspUserLogin".equals(type)) {
				CtpApiLibrary.instance.subscribe(id, symbols, symbols.length);
			} else if ("M_OnRtnDepthMarketData".equals(type)) {
				int volume = messageObject.getInt("Volume");
				if (volume > 0) {
					String instrumentID = messageObject.getString("InstrumentID");
					long time = sdf.parse(messageObject.getString("ActionDay") + messageObject.getString("UpdateTime"))
							.getTime() + messageObject.getInt("UpdateMillisec");
					String symbol = CTPProducts.find(instrumentID).getExchange() + "." + instrumentID;
					BigDecimal lowerLimitPrice = convertBigDecimal(messageObject, "LowerLimitPrice");
					BigDecimal upperLimitPrice = convertBigDecimal(messageObject, "UpperLimitPrice");
					BigDecimal price = convertBigDecimal(messageObject, "LastPrice");
					int openInterest = messageObject.getInt("OpenInterest");
					BigDecimal bidPrice1 = convertBigDecimal(messageObject, "BidPrice1");
					int bidVolume1 = messageObject.getInt("BidVolume1");
					BigDecimal askPrice1 = convertBigDecimal(messageObject, "AskPrice1");
					int askVolume1 = messageObject.getInt("AskVolume1");
					handleMarketData(symbol, lowerLimitPrice, upperLimitPrice, time, price, volume, openInterest,
							bidPrice1, bidVolume1, askPrice1, askVolume1);
//				System.out.println(messageObject);
				}
			} else if ("M_OnFrontDisconnected".equals(type)) {
				handleDisconnected();
			} else if ("M_OnRspError".equals(type)) {

			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private BigDecimal convertBigDecimal(JSONObject o, String key) {
		BigDecimal v = o.getBigDecimal(key);
		if (v.precision() > 13) {
			v = v.setScale(3, RoundingMode.HALF_UP);
		}
		return v;
	}

	protected void handleConnected() {

	}

	protected void handleDisconnected() {

	}

	protected abstract void handleMarketData(String symbol, BigDecimal lowerLimitPrice, BigDecimal upperLimitPrice,
			long time, BigDecimal price, int volume, int openInterest, BigDecimal bidPrice1, int bidVolume1,
			BigDecimal askPrice1, int askVolume1);

	protected void doInit(String flowDir) {
		CtpApiLibrary.instance.initMarket(id, flowDir, front, this);
	}

	protected void doRelease() {
		CtpApiLibrary.instance.releaseMarket(id);
	}

}
