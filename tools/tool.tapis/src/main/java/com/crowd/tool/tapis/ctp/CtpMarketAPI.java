package com.crowd.tool.tapis.ctp;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

public abstract class CtpMarketAPI extends CtpBaseApi {

	private String front;

	private String[] symbols;

	private RandomAccessFile raf;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public CtpMarketAPI(String id, String front, String... symbols) {
		super(id);
		this.front = front;
		this.symbols = symbols;
		try {
			File file = new File("F:\\mdstream\\20220107.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			raf = new RandomAccessFile(file, "rw");
			raf.setLength(0);
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
				try {
					String s = sdf.format(new Date()) + ":M_OnFrontConnected";
					System.out.println(s);
					raf.write((s + "\r\n").toString().getBytes());
				} catch (Throwable t) {

				}
			} else if ("M_OnRspUserLogin".equals(type)) {
				CtpApiLibrary.instance.subscribe(id, symbols, symbols.length);
			} else if ("M_OnRtnDepthMarketData".equals(type)) {
				String instrumentID = messageObject.getString("InstrumentID");
				String symbol = CTPInstruments.find(instrumentID).getExchange() + "." + instrumentID;
				BigDecimal lowerLimitPrice = new BigDecimal(messageObject.getDouble("LowerLimitPrice"));
				BigDecimal upperLimitPrice = new BigDecimal(messageObject.getDouble("UpperLimitPrice"));
				BigDecimal price = new BigDecimal(messageObject.getDouble("LastPrice"));
				BigDecimal amount = new BigDecimal(messageObject.getInt("Volume"));
				handleMarketData(symbol, System.currentTimeMillis(), lowerLimitPrice, upperLimitPrice, price, amount);

				if (amount.compareTo(BigDecimal.ZERO) > 0) {
					try {
						String actionDay = messageObject.getString("ActionDay");
						String timeString = actionDay.substring(0, 4) + "-" + actionDay.substring(4, 6) + "-"
								+ actionDay.substring(6, 8) + " " + messageObject.getString("UpdateTime") + "."
								+ messageObject.getInt("UpdateMillisec");
						Date time = sdf.parse(timeString);
						StringBuffer buffer = new StringBuffer();
						buffer.append(sdf.format(time) + "000000");
						buffer.append(",");
						buffer.append(time.getTime() + "000000");
						buffer.append(",");
						buffer.append(messageObject.get("LastPrice"));
						buffer.append(",");
						buffer.append(messageObject.get("HighestPrice"));
						buffer.append(",");
						buffer.append(messageObject.get("LowestPrice"));
						buffer.append(",");
						buffer.append(messageObject.get("Volume"));
						buffer.append(",");
						buffer.append(messageObject.get("Turnover"));
						buffer.append(",");
						buffer.append(messageObject.get("OpenInterest"));
						buffer.append(",");
						buffer.append(messageObject.get("BidPrice1"));
						buffer.append(",");
						buffer.append(messageObject.get("BidVolume1"));
						buffer.append(",");
						buffer.append(messageObject.get("AskPrice1"));
						buffer.append(",");
						buffer.append(messageObject.get("AskVolume1"));
						buffer.append(",");
						buffer.append(symbol);
						buffer.append("\r\n");
//						raf.write((messageObject.toString() + "\r\n").getBytes());
						raf.write(buffer.toString().getBytes());
					} catch (Throwable t) {
					}
				}
			} else if ("M_OnFrontDisconnected".equals(type)) {
				try {
					String s = sdf.format(new Date()) + ":M_OnFrontDisconnected";
					System.out.println(s);
					raf.write((s + "\r\n").toString().getBytes());
				} catch (Throwable t) {

				}
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

	public final static void main(String[] args) throws Throwable {
		String[] EX_NAMES = new String[] { "CZCE", "DCE", "SHFE", "CFFEX", "INE" };
		String[][] EX_PRODUCTS = new String[][] {
				{ "AP", "CF", "CJ", "CY", "FG", "JR", "LR", "MA", "OI", "PF", "PK", "PM", "RI", "RM", "RS", "SA", "SF",
						"SM", "SR", "TA", "UR", "WH", "ZC" },
				{ "a", "b", "bb", "c", "cs", "eb", "eg", "fb", "i", "j", "jd", "jm", "l", "lh", "m", "p", "pg", "pp",
						"rr", "v", "y" },
				{ "ag", "al", "au", "bu", "cu", "fu", "hc", "ni", "pb", "rb", "ru", "sn", "sp", "ss", "wr", "zn" },
				{ "IF", "IH", "IC", "T", "TS", "TF" }, { "bc", "lu", "nr", "sc" } };
		String[] monthes = new String[] { "2201", "2202", "2203", "2204", "2205", "2206", "2207", "2208" };
		List<String> symbolList = new ArrayList<String>();
		for (int i = 0; i < EX_NAMES.length; i++) {
			for (int j = 0; j < EX_PRODUCTS[i].length; j++) {
				for (String month : monthes) {
					if (i == 0) {
						symbolList.add(EX_PRODUCTS[i][j] + month.substring(1));
					} else {
						symbolList.add(EX_PRODUCTS[i][j] + month);
					}
				}
			}
		}
		String[] symbols = symbolList.toArray(new String[0]);
		CtpMarketAPI test = new CtpMarketAPI("1", "tcp://140.206.242.115:42213", symbols) {

			@Override
			protected void handleMarketData(String symbol, long time, BigDecimal lowerLimitPrice,
					BigDecimal upperLimitPrice, BigDecimal price, BigDecimal amount) {
				if (symbol.endsWith("a2203")) {
					System.out.println(price + "----" + amount);
				}
				if (symbol.endsWith("a2202")) {
					System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
				}
			}

			@Override
			protected boolean checkContextDisposed() {
				return false;
			}

		};
		test.run();
	}
}
