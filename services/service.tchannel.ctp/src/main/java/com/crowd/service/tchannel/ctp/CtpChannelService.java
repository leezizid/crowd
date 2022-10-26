package com.crowd.service.tchannel.ctp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.crowd.service.base.CrowdWorker;
import com.crowd.service.base.CrowdWorkerContext;
import com.crowd.tool.misc.OrderType;
import com.crowd.tool.misc.PositionSide;
import com.crowd.tool.misc.ProductDefine;
import com.crowd.tool.tapis.ctp.AccountInfo;
import com.crowd.tool.tapis.ctp.CTPInstrument;
import com.crowd.tool.tapis.ctp.CTPProducts;
import com.crowd.tool.tapis.ctp.ConnectInfo;
import com.crowd.tool.tapis.ctp.CtpTradeAPI;
import com.crowd.tool.tapis.ctp.OrderInfo;
import com.crowd.tool.tapis.ctp.PositionInfo;

public class CtpChannelService implements CrowdService {

	private static Map<String, CtpTradeAPI> channels = new HashMap<String, CtpTradeAPI>();

	@Override
	public void init(CrowdInitContext context) throws Throwable {
	}

	@Override
	public String getName() {
		return "tchannel.ctp";
	}

	/**
	 * 更新合约列表中的合约最新持仓和交易量等数据
	 */
	@CrowdMethod
	public void updateInstrumentData(CrowdContext context, JSONObject inputObject, JSONObject outputObject)
			throws Throwable {
		JSONArray arr = new JSONArray(context.load("instruments.info"));
		String[] quoteCodes = new String[arr.length()];
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			String productId = o.getString("productId");
			quoteCodes[i] = "nf_" + productId.toUpperCase() + o.getString("deliveryYear").substring(2)
					+ o.getString("deliveryMonth");
		}
		String[] results = quoteSina(quoteCodes);
		for (int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			String[] result = StringUtils.splitPreserveAllTokens(results[i], ",");
			if (result.length == 50) {
				// 股指：0开盘,1最高,2最低,3最新,4成交量,5成交金额,6持仓量 ,-14日期,-13时间
//				System.out.println(result[result.length - 1] + "#" + result[result.length - 14] + ":"
//						+ result[result.length - 13] + "," + result[6] + ":" + result[4]);
				o.put("sinaName", result[result.length - 1]);
				o.put("timeInfo", result[result.length - 14] + "." + result[result.length - 13]);
				o.put("positionVolumn", result[6]);
				o.put("tradeVolumn", result[4]);
			} else if (result.length == 44) {
				// 商品：0名称,1时间,2开盘,3最低,4最高,5结算,6最新,7卖1,8买1,9,10昨结算,11买1量,12卖1量,13持仓量,14成交量,15交易所,16品种,17日期,1,,,,,,,,,均价,买2价,买2量,买3价,买3量,买4价,买4量,买5价,买5量,卖2价,卖2量,卖3价,卖3量,卖4价,卖4量,卖5价,卖5量
//				System.out
//						.println(result[0] + "#" + result[17] + ":" + result[1] + "," + result[13] + ":" + result[14]);
				o.put("sinaName", result[0]);
				o.put("timeInfo", result[17] + "." + result[1].substring(0, 2) + ":" + result[1].substring(2, 4) + ":"
						+ result[1].substring(4, 6));
				o.put("positionVolumn", result[13]);
				o.put("tradeVolumn", result[14]);
			} else {
				System.out.println("--------");
				o.put("sinaName", "");
				o.put("timeInfo", "");
				o.put("positionVolumn", "");
				o.put("tradeVolumn", "");
			}
		}
		//
		context.save("instruments.data", arr.toString(4));
		instruments(context, inputObject, outputObject);
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
			if (exchangeId.equals(targetExchangeId)) {
				String productId = o.getString("productId");
				ProductDefine productDefine = CTPProducts.find(productId);
				if (productDefine != null) {
					o.put("name", id.replace(productId, productDefine.getTitle() + "-"));
				} else {
					o.put("name", "--");
				}
				o.put("id", exchangeId + "." + id);
				o.put("positionVolumn", o.opt("positionVolumn"));
				o.put("tradeVolumn", o.opt("tradeVolumn"));
				o.put("timeInfo", o.opt("timeInfo"));
				o.put("title", o.opt("sinaName"));
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
						if (positionInfo.getTotalVolumn() > 0) {
							JSONObject positionObj = new JSONObject();
							positionObj.put("symbol", positionInfo.getSymbol());
							positionObj.put("volumn",
									positionInfo.getPositionSide() == PositionSide.Long ? positionInfo.getTotalVolumn()
											: -positionInfo.getTotalVolumn());
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
						if (orderInfo.getVolumn() == orderInfo.getExecVolumn() || orderInfo.isCanceled()) {
							continue;
						}
						JSONObject orderObj = new JSONObject();
						orderObj.put("symbol", orderInfo.getSymbol());
						orderObj.put("serverOrderId", orderInfo.getServerOrderId());
						// orderObj.put("clientOrderId", orderInfo.getClientOrderId());
						orderObj.put("volumn", orderInfo.getVolumn());
						orderObj.put("execVolumn", orderInfo.getExecVolumn());
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
		String authCode = inputObject.getString("authCode");
		String macAddress = inputObject.getString("macAddress");
		String appId = "SHINNY_Q7V2_2.93";
		String productInfo = "SHINNYQ7V2";
		ConnectInfo connectInfo = new ConnectInfo(traderFront, brokerId, userId, password, investorId, authCode, appId,
				productInfo, macAddress);
		//
		CtpTradeAPI ctpTradeAPI = channels.get(id);
		if (ctpTradeAPI == null) {
			ctpTradeAPI = new CtpTradeAPI(id, connectInfo) {
				protected boolean checkContextDisposed() {
					return crowdContext.isDisposed() || !channels.containsKey(id);
				}

				@Override
				protected void handleOrderUpdated(String serverOrderId, String symbol, int execVolumn,
						BigDecimal execValue, boolean canceled) {
					JSONObject updateMessageObject = new JSONObject();
					updateMessageObject.put("channelId", id);
					updateMessageObject.put("serverOrderId", serverOrderId);
					updateMessageObject.put("symbol", symbol);
					updateMessageObject.put("execVolumn", execVolumn);
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

	@CrowdWorker
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
		int volumn = Integer.parseInt(inputObject.getString("volumn"));
		outputObject.put("serverOrderId",
				ctpTradeAPI.postOrder(symbolInfo[0], symbolInfo[1], orderType, positionSide, price, volumn));
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
}
