package com.crowd.tool.tapis.ctp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;
import java.util.Calendar;

import org.json.JSONObject;

public abstract class CtpMarketAPI extends CtpBaseApi {

	private String front;

	private String[] symbols;

	public CtpMarketAPI(String id, String front, String... symbols) {
		super(id);
		this.front = front;
		this.symbols = symbols;
	}

	public void subscribe(String[] symbols) {
		this.symbols = symbols;
		try {
			CtpApiLibrary.instance.subscribe(id, symbols, symbols.length);
		} catch (Throwable t) {

		}
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
					long time = calcTickTime(messageObject.getString("UpdateTime"))
							+ messageObject.getInt("UpdateMillisec");
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
				System.out.print("M_OnRspError:" + messageObject);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private long calcTickTime(String updateTimeString) {
		Calendar now = Calendar.getInstance();
		//
		int localTimeSecond = now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60
				+ now.get(Calendar.SECOND);
		int tickTimeSecond = Integer.parseInt(updateTimeString.substring(0, 2)) * 3600
				+ Integer.parseInt(updateTimeString.substring(3, 5)) * 60
				+ Integer.parseInt(updateTimeString.substring(6, 8));
		//
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		//
		if (localTimeSecond - tickTimeSecond > 20 * 3600) {
			// 本地时间过慢，需要往后一天
			now.setTimeInMillis(now.getTimeInMillis() + 24 * 3600 * 1000);
		} else if (tickTimeSecond - localTimeSecond > 20 * 3600) {
			// 本地时间过快，需要往前一天
			now.setTimeInMillis(now.getTimeInMillis() - 24 * 3600 * 1000);
		}
		return now.getTimeInMillis() + tickTimeSecond * 1000;
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
