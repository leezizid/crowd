package com.crowd.service.tchannel.ctp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.CrowdInitContext;
import com.crowd.service.base.CrowdMethod;
import com.crowd.service.base.CrowdService;
import com.crowd.service.base.CrowdSubscriber;
import com.crowd.service.base.CrowdWorker;
import com.crowd.service.base.CrowdWorkerContext;
import com.crowd.tool.misc.OrderType;
import com.crowd.tool.misc.PositionSide;
import com.crowd.tool.misc.ProductDefine;
import com.crowd.tool.misc.TradeDays;
import com.crowd.tool.tapis.ctp.AccountInfo;
import com.crowd.tool.tapis.ctp.CTPInstrument;
import com.crowd.tool.tapis.ctp.CTPProducts;
import com.crowd.tool.tapis.ctp.ConnectInfo;
import com.crowd.tool.tapis.ctp.CtpMarketAPI;
import com.crowd.tool.tapis.ctp.CtpTradeAPI;
import com.crowd.tool.tapis.ctp.OrderInfo;
import com.crowd.tool.tapis.ctp.PositionInfo;

public class CtpChannelService implements CrowdService {

	private static Map<String, CtpTradeAPI> channels = new HashMap<String, CtpTradeAPI>();

	private String marketDataServer;
	private String marketDataDir;

	@Override
	public void init(CrowdInitContext context) throws Throwable {
		marketDataServer = System.getProperty("MarketDataServer");
		marketDataDir = System.getProperty("MarketDataDir");
		if(StringUtils.isEmpty(marketDataDir)) {
			marketDataDir = System.getProperty("user.dir") + File.separator + "mdstream";
		}
	}

	@Override
	public void postInit(CrowdInitContext context) throws Throwable {
		if(StringUtils.isNotEmpty(marketDataServer)) {
			context.startWorker("/" + getName() + "/marketDataMonitor", new JSONObject());
		}
	}

	@Override
	public String getName() {
		return "tchannel.ctp";
	}

	@CrowdMethod
	public void status(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		//
		String id = inputObject.optString("id");
		//
		JSONArray properties = new JSONArray();
		//
		addRow(properties, "标识", inputObject.optString("id"));
		addRow(properties, "名称", inputObject.optString("name"));
		addRow(properties, "描述", inputObject.optString("desc"));

		//
		CtpTradeAPI tradeAPI = channels.get(id);
		if (tradeAPI == null || tradeAPI.isDisposed()) {
			channels.remove(id);
			outputObject.put("status", false);
		} else {
			outputObject.put("status", true);
		}
		outputObject.put("properties", properties);
	}

	@CrowdMethod
	public void info(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String id = inputObject.optString("id");
		status(context, inputObject, outputObject);
		JSONArray properties = outputObject.getJSONArray("properties");
		if (outputObject.optBoolean("status")) {
			CtpTradeAPI tradeAPI = channels.get(id);
			if (tradeAPI.isLogin()) {
				addRow(properties, "登录状态", "是");
				try {
					AccountInfo accountInfo = tradeAPI.getAccountInfo();
					addRow(properties, "权益", String.valueOf(accountInfo.getBalanceValue().doubleValue()));
					addRow(properties, "可用", accountInfo.getAvailableValue().doubleValue());
					addRow(properties, "占用保证金", accountInfo.getCurrentMarginValue().doubleValue());
					addRow(properties, "持仓盈亏", accountInfo.getPositionProfit().doubleValue());
				} catch (Throwable t) {
					addRow(properties, "错误", t.getMessage());
				}
			} else {
				addRow(properties, "登录状态", "否");
			}
		}
		//
		outputObject.put("properties", properties);
	}

	@CrowdMethod
	public void positions(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String id = inputObject.optString("id");
		status(context, inputObject, outputObject);
		JSONArray properties = outputObject.getJSONArray("properties");
		if (outputObject.optBoolean("status")) {
			CtpTradeAPI tradeAPI = channels.get(id);
			if (tradeAPI.isLogin()) {
				JSONArray positionArray = new JSONArray();
				try {
					for (PositionInfo positionInfo : tradeAPI.getPositions()) {
						if (positionInfo.getTotalVolume() > 0) {
							JSONObject positionObj = new JSONObject();
							positionObj.put("symbol", positionInfo.getSymbol());
							positionObj.put("volume",
									positionInfo.getPositionSide() == PositionSide.Long ? positionInfo.getTotalVolume()
											: -positionInfo.getTotalVolume());
							positionObj.put("marketPrice", positionInfo.getMarketPrice());
							positionArray.put(positionObj);
						}
					}
				} catch (Throwable t) {
					outputObject.put("dataError", t.getMessage());
				}
				outputObject.put("positions", positionArray);
			}
		}
		//
		outputObject.put("properties", properties);
	}

	@CrowdMethod
	public void orders(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String id = inputObject.optString("id");
		status(context, inputObject, outputObject);
		JSONArray properties = outputObject.getJSONArray("properties");
		if (outputObject.optBoolean("status")) {
			CtpTradeAPI tradeAPI = channels.get(id);
			if (tradeAPI.isLogin()) {
				JSONArray orderArray = new JSONArray();
				try {
					for (OrderInfo orderInfo : tradeAPI.getOrders()) {
						if (orderInfo.getVolume() == orderInfo.getExecVolume() || orderInfo.isCanceled()) {
							continue;
						}
						JSONObject orderObj = new JSONObject();
						orderObj.put("symbol", orderInfo.getSymbol());
						orderObj.put("serverOrderId", orderInfo.getServerOrderId());
						// orderObj.put("clientOrderId", orderInfo.getClientOrderId());
						orderObj.put("volume", orderInfo.getVolume());
						orderObj.put("execVolume", orderInfo.getExecVolume());
						orderObj.put("price", orderInfo.getPrice());
						orderObj.put("type", orderInfo.getType());
						orderObj.put("positionSide", orderInfo.getPositionSide());
						orderObj.put("time", orderInfo.getTime());
						orderArray.put(orderObj);
					}
				} catch (Throwable t) {
					t.printStackTrace();
					outputObject.put("dataError", t.getMessage());
				}
				outputObject.put("openOrders", orderArray);
			}
		}
		//
		outputObject.put("properties", properties);
	}

	@CrowdWorker
	public void startWorker(final CrowdWorkerContext crowdContext, JSONObject inputObject) throws Throwable {
		//
		final String id = inputObject.optString("id");
		String traderFront = inputObject.optString("traderFront");
		String brokerId = inputObject.getString("brokerId");
		String userId = inputObject.getString("userId");
		String password = inputObject.getString("password");
		String investorId = inputObject.getString("investorId");
		String appId = inputObject.getString("appId");
		String productInfo = inputObject.getString("productInfo");
		String authCode = inputObject.getString("authCode");
		String macAddress = inputObject.getString("macAddress");
		ConnectInfo connectInfo = new ConnectInfo(traderFront, brokerId, userId, password, investorId, authCode, appId,
				productInfo, macAddress);
		//
		final Calendar calendar = Calendar.getInstance();
		CtpTradeAPI ctpTradeAPI = channels.get(id);
		if (ctpTradeAPI == null) {
			ctpTradeAPI = new CtpTradeAPI(id, connectInfo) {
				protected boolean checkContextDisposed() {
					calendar.setTimeInMillis(System.currentTimeMillis());
					int hour = calendar.get(Calendar.HOUR_OF_DAY);
					if (hour == 16) {
						return true; //每天16点自动关闭接口
					}
					return crowdContext.isDisposed() || !channels.containsKey(id);
				}

				@Override
				protected void handleOrderUpdated(String serverOrderId, String symbol, int execVolume,
						BigDecimal execValue, boolean canceled) {
					JSONObject updateMessageObject = new JSONObject();
					updateMessageObject.put("channelId", id);
					updateMessageObject.put("serverOrderId", serverOrderId);
					updateMessageObject.put("symbol", symbol);
					updateMessageObject.put("execVolume", execVolume);
					updateMessageObject.put("execValue", execValue.doubleValue());
					updateMessageObject.put("canceled", canceled);
					try {
						crowdContext.sendMessage("serverOrderUpdated", updateMessageObject);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}

				protected void handleInstrumentQueryFinished(CTPInstrument[] instruments) {
					JSONArray arr = new JSONArray();
					for (CTPInstrument instrument : instruments) {
						JSONObject o = new JSONObject();
						o.put("id", instrument.getId());
						o.put("name", instrument.getName());
						o.put("exchangeId", instrument.getExchangeId());
						o.put("productId", instrument.getProductId());
						o.put("deliveryYear", instrument.getDeliveryYear());
						o.put("deliveryMonth", instrument.getDeliveryMonth());
						arr.put(o);
					}
					try {
						crowdContext.save("instruments.info", arr.toString(4));
					} catch (Throwable t) {

					}
				}

			};
			channels.put(id, ctpTradeAPI);
			try {
				ctpTradeAPI.run();
			} finally {
				channels.remove(id);
			}
		}
	}

	@CrowdMethod
	public void stopWorker(final CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject)
			throws Throwable {
		String id = inputObject.optString("id");
		CtpTradeAPI ctpTradeAPI = channels.get(id);
		if (ctpTradeAPI != null) {
			ctpTradeAPI.dispose("");
		}
	}

	@CrowdMethod
	public void postOrder(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String tChannelId = inputObject.optString("tChannelId");
		CtpTradeAPI ctpTradeAPI = channels.get(tChannelId);
		if (ctpTradeAPI == null || ctpTradeAPI.isDisposed()) {
			throw new IllegalStateException("" + tChannelId + "通道服务未启动");
		}
		String[] symbolInfo = StringUtils.split(inputObject.getString("symbol"), ".");
		PositionSide positionSide = PositionSide.valueOf(inputObject.getString("positionSide"));
		OrderType orderType = OrderType.valueOf(inputObject.getString("type"));
		float price = Float.parseFloat(inputObject.getString("price"));
		int volume = Integer.parseInt(inputObject.getString("volume"));
		outputObject.put("serverOrderId",
				ctpTradeAPI.postOrder(symbolInfo[0], symbolInfo[1], orderType, positionSide, price, volume));
	}

	@CrowdMethod
	public void cancelOrder(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject)
			throws Throwable {
		String tChannelId = inputObject.optString("tChannelId");
		String symbol = inputObject.optString("symbol");
		String serverOrderId = inputObject.optString("serverOrderId");
		CtpTradeAPI ctpTradeAPI = channels.get(tChannelId);
		if (ctpTradeAPI == null || ctpTradeAPI.isDisposed()) {
			throw new IllegalStateException("" + tChannelId + "通道服务未启动");
		}
		ctpTradeAPI.cancelOrder(symbol, serverOrderId);
	}

	@CrowdMethod
	public void synchronizeOrders(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject)
			throws Throwable {
		String tChannelId = inputObject.optString("tChannelId");
		CtpTradeAPI ctpTradeAPI = channels.get(tChannelId);
		if (ctpTradeAPI == null || ctpTradeAPI.isDisposed()) {
			throw new IllegalStateException("" + tChannelId + "通道服务未启动");
		}
		JSONArray orders = inputObject.getJSONArray("orders");
		for (int i = 0; i < orders.length(); i++) {
			JSONObject o = orders.getJSONObject(i);
			ctpTradeAPI.synchronizeOrder(o.getString("serverOrderId"));
		}
	}

	private void addRow(JSONArray properties, String name, Object value) {
		JSONObject row = new JSONObject();
		row.put("name", name);
		row.put("value", value);
		properties.put(row);
	}

	@CrowdMethod
	public void instruments(CrowdContext context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
		String targetExchangeId = inputObject.optString("exchangeId");
		List<JSONObject> instruments = new ArrayList<JSONObject>();
		JSONArray arr = null;
		try {
			arr = new JSONArray(context.load("instruments.data"));
		} catch (Throwable t) {
			arr = new JSONArray(context.load("instruments.info"));
		}
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			String id = o.getString("id");
			String exchangeId = o.getString("exchangeId");
			boolean isMain = o.optBoolean("isMain");
			if (targetExchangeId.equals("main") ? isMain : exchangeId.equals(targetExchangeId)) {
				String productId = o.getString("productId");
				ProductDefine productDefine = CTPProducts.find(productId);
				if (productDefine != null) {
					o.put("name", id.replace(productId, productDefine.getTitle() + "-"));
				} else {
					o.put("name", "--");
				}
				o.put("id", exchangeId + "." + id);
				o.put("isMain", o.optBoolean("isMain") ? "是" : "");
				instruments.add(o);
			}
		}
		//
		Collections.sort(instruments, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject o1, JSONObject o2) {
				return o1.getString("id").compareTo(o2.getString("id"));
			}
		});
		//
		outputObject.put("instruments", new JSONArray(instruments));
	}

	@CrowdMethod
	public void mainDateInfos(CrowdContext crowdContext, JSONObject inputObject, JSONObject outputObject)
			throws Throwable {
		JSONArray arr = new JSONArray(crowdContext.load("instruments.data"));
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			boolean isMain = o.optBoolean("isMain");
			if (isMain) {
				String productId = o.getString("productId");
				outputObject.put(productId, o.getString("id").substring(productId.length()));
			}
		}
	}

	@CrowdWorker
	public void marketDataMonitor(CrowdWorkerContext context, JSONObject inputObject) throws Throwable {
		Calendar calendar = Calendar.getInstance();
//		while (!context.isDisposed()) {
		while (true) {
			try {
				long time = System.currentTimeMillis();
				if (TradeDays.isTradeDay(time)) {
					calendar.setTimeInMillis(time);
					int hour = calendar.get(Calendar.HOUR_OF_DAY);
					int minute = calendar.get(Calendar.MINUTE);
					if (hour == 16 && minute < 10) {
						updateInstrumentData(context, TradeDays.matchTradeDay(time));
						if (market != null) {
							market.subscribe(getMainInstruments(context));
						}
					}
				}
				if (market != null && raf != null) {
					flushDataBuffer();
				}
				// 由于CTP接口可以自动重连，所以无论如何都开启
				if (market == null) {
					context.asyncInvoke("/" + getName() + "/marketDataWorker", inputObject);
				}
				//
				Thread.sleep(3 * 60 * 1000);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private CtpMarketAPI market;

	private RandomAccessFile raf;

	private StringBuffer dataBuffer0 = new StringBuffer();

	private StringBuffer dataBuffer1 = new StringBuffer();

	private int activeBuffer = 0;

	private Object bufferLocker = new Object();

	private void flushDataBuffer() {
		StringBuffer dataBuffer;
		synchronized (bufferLocker) {
			if (activeBuffer == 0) {
				dataBuffer = dataBuffer0;
				activeBuffer = 1;
			} else {
				dataBuffer = dataBuffer1;
				activeBuffer = 0;
			}
		}
		try {
			raf.writeBytes(dataBuffer.toString());
		} catch (Throwable t) {

		}
		dataBuffer.setLength(0);
	}

	@CrowdSubscriber("portal.workers")
	public void onWorkersChanged(CrowdContext context, JSONObject message) throws Throwable {
		// System.out.println("" + message);
	}

	@CrowdWorker
	public void marketDataWorker(CrowdWorkerContext context, JSONObject inputObject) throws Throwable {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
		market = new CtpMarketAPI("marketDataWorker", "tcp://" + marketDataServer, getMainInstruments(context)) {

			@Override
			protected void handleMarketData(String symbol, BigDecimal lowerLimitPrice, BigDecimal upperLimitPrice,
					long time, BigDecimal price, int volume, int openInterest, BigDecimal bidPrice1, int bidVolume1,
					BigDecimal askPrice1, int askVolume1) {
				synchronized (bufferLocker) {
					StringBuffer dataBuffer = dataBuffer0;
					if (activeBuffer == 1) {
						dataBuffer = dataBuffer1;
					}
					dataBuffer.append(symbol);
					dataBuffer.append(",");
					dataBuffer.append(sdf.format(new Date(time)));
//					dataBuffer.append(",");
//					dataBuffer.append(time);
					dataBuffer.append(",");
					dataBuffer.append(price);
					dataBuffer.append(",");
					dataBuffer.append(volume);
					dataBuffer.append(",");
					dataBuffer.append(openInterest);
					dataBuffer.append(",");
					dataBuffer.append(bidPrice1);
					dataBuffer.append(",");
					dataBuffer.append(bidVolume1);
					dataBuffer.append(",");
					dataBuffer.append(askPrice1);
					dataBuffer.append(",");
					dataBuffer.append(askVolume1);
					dataBuffer.append("\r\n");
				}
			}

			protected void handleConnected() {
				System.out.println("CTP Market Connected...");
				try {
					String tradeDay = TradeDays.matchTradeDay(System.currentTimeMillis());
					if (tradeDay != null) {
						int i = 0;
						while (true) {
							File file = new File(marketDataDir + File.separator + tradeDay + "_" + i + ".txt");
							if (file.exists()) {
								i++;
								continue;
							}
							file.createNewFile();
							raf = new RandomAccessFile(file, "rw");
							raf.setLength(0);
							break;
						}
					}
				} catch (Throwable t) {

				}
			}

			protected void handleDisconnected() {
				System.out.println("CTP Market Disconnected...");
				if (raf != null) {
					flushDataBuffer();
					try {
						raf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					raf = null;
				}
			}

			@Override
			protected boolean checkContextDisposed() {
				return context.isDisposed();
			}

		};
		market.run();
		market = null;
	}

	/**
	 * 获取最近收盘确定的主力合约
	 * 
	 * @param context
	 * @return
	 * @throws Throwable
	 */
	private String[] getMainInstruments(CrowdContext context) throws Throwable {
		JSONArray arr = new JSONArray(context.load("instruments.data"));
		List<String> idList = new ArrayList<String>();
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			boolean isMain = o.optBoolean("isMain");
			if (isMain) {
				idList.add(o.getString("id"));
			}
		}
		return idList.toArray(new String[0]);
	}

	/**
	 * 更新合约列表中的合约最新持仓和交易量等数据
	 */
	public void updateInstrumentData(CrowdContext context, String tradeDay) throws Throwable {
		//
		try {
			if (tradeDay
					.equals(new JSONArray(context.load("instruments.data")).getJSONObject(0).optString("tradeDay"))) {
				return;
			}
		} catch (Throwable t) {

		}
		//
		JSONArray arr = new JSONArray();
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		for (ProductDefine productDefine : CTPProducts.getAllProducts()) {
			for (int i = 0; i < 9; i++) { // 只做9个月内的合约
				int y = year;
				int m = (month + i);
				if (m > 12) {
					m = m - 12;
					y = y + 1;
				}
				String code = String.valueOf(y).substring(2) + (m < 10 ? "0" : "") + m;
				if (productDefine.getExchange().equals("CZCE")) {
					code = code.substring(1);
				}
				JSONObject o = new JSONObject();
				o.put("exchangeId", productDefine.getExchange());
				o.put("productId", productDefine.getName());
				o.put("id", productDefine.getName() + code);
				o.put("name", productDefine.getTitle() + code);
				o.put("deliveryYear", String.valueOf(y));
				o.put("deliveryMonth", code.substring(code.length() - 2));
				arr.put(o);
			}
		}
//		JSONArray arr = new JSONArray(context.load("instruments.info"));
		String[] quoteCodes = new String[arr.length()];
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			String productId = o.getString("productId");
			quoteCodes[i] = "nf_" + productId.toUpperCase() + o.getString("deliveryYear").substring(2)
					+ o.getString("deliveryMonth");
		}
		Map<String, Integer> mainInstruments = new HashMap<String, Integer>();
		String[] results = quoteSina(quoteCodes);
		JSONArray outputArray = new JSONArray();
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			String[] result = StringUtils.splitPreserveAllTokens(results[i], ",");
			int tradeVolume = 0;
			if (result.length == 50) {
				// 股指：0开盘,1最高,2最低,3最新,4成交量,5成交金额,6持仓量 ,-14日期,-13时间
//				System.out.println(result[result.length - 1] + "#" + result[result.length - 14] + ":"
//						+ result[result.length - 13] + "," + result[6] + ":" + result[4]);
				o.put("sinaName", result[result.length - 1]);
				o.put("timeInfo", result[result.length - 14] + "." + result[result.length - 13]);
				o.put("positionVolume", result[6]);
				o.put("tradeVolume", result[4]);
				o.put("lastPrice", result[3]);
				tradeVolume = Integer.parseInt(result[4]);
			} else if (result.length == 44) {
				// 商品：0名称,1时间,2开盘,3最低,4最高,5结算,6最新,7卖1,8买1,9,10昨结算,11买1量,12卖1量,13持仓量,14成交量,15交易所,16品种,17日期,1,,,,,,,,,均价,买2价,买2量,买3价,买3量,买4价,买4量,买5价,买5量,卖2价,卖2量,卖3价,卖3量,卖4价,卖4量,卖5价,卖5量
//				System.out
//						.println(result[0] + "#" + result[17] + ":" + result[1] + "," + result[13] + ":" + result[14]);
				o.put("sinaName", result[0]);
				o.put("timeInfo", result[17] + "." + result[1].substring(0, 2) + ":" + result[1].substring(2, 4) + ":"
						+ result[1].substring(4, 6));
				o.put("positionVolume", result[13]);
				o.put("tradeVolume", result[14]);
				o.put("lastPrice", result[6]);
				tradeVolume = Integer.parseInt(result[14]);
			} else {
				// System.out.println("--------");
				o.put("sinaName", "--");
				o.put("timeInfo", "--");
				o.put("positionVolume", "--");
				o.put("tradeVolume", "0");
				o.put("lastPrice", "--");
			}
			if (tradeVolume > 0 && o.getString("timeInfo").substring(0, 10).equals(tradeDay)) {
				outputArray.put(o);
				String productId = o.getString("productId");
				if (!mainInstruments.containsKey(productId) || tradeVolume > mainInstruments.get(productId)) {
					mainInstruments.put(productId, tradeVolume);
				}
			}
		}
		// 标记数据交易日和主力合约标志
		for (int i = 0; i < outputArray.length(); i++) {
			JSONObject o = outputArray.getJSONObject(i);
			String productId = o.getString("productId");
			int tradeVolume = Integer.parseInt(o.getString("tradeVolume"));
			if (mainInstruments.get(productId) != null && mainInstruments.get(productId) == tradeVolume) {
				o.put("isMain", true);
				mainInstruments.remove(productId);
			} else {
				o.put("isMain", false);
			}
			o.put("tradeDay", tradeDay);
		}
		//
		context.save("instruments.data", outputArray.toString(4));
	}

	private String[] quoteSina(String[] codes) throws Throwable {
		int limit = 50;
		int current = 0;
		List<String> allResultList = new ArrayList<String>();
		while (true) {
			if (current == codes.length) {
				break;
			}
			List<String> codeList = new ArrayList<String>();
			for (int i = 0; i < limit; i++) {
				codeList.add(codes[current++]);
				if (current == codes.length) {
					break;
				}
			}
			String[] results = StringUtils.split(quoteSina(StringUtils.join(codeList, ",")), ";");
			for (int i = 0; i < results.length; i++) {
				String result = results[i];
				allResultList.add(result.substring(result.indexOf("\"") + 1, result.length() - 1));
			}
		}
		return allResultList.toArray(new String[0]);
	}

	private String quoteSina(String list) throws Throwable {
		StringBuffer buffer = new StringBuffer();
		HttpURLConnection connection = (HttpURLConnection) new URL("https://hq.sinajs.cn/list=" + list)
				.openConnection();
		try {
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
			connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("Referer", "https://finance.sina.com.cn");
			connection.connect();
			if (connection.getResponseCode() == 200) {
				InputStream is = connection.getInputStream();
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "GBK"));
					String line = null;
					while ((line = reader.readLine()) != null) {
						buffer.append(line);
					}
				} finally {
					is.close();
				}
			}
		} finally {
			connection.disconnect();
		}
		return buffer.toString();
	}

}
