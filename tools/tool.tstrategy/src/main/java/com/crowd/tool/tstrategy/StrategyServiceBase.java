package com.crowd.tool.tstrategy;

import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Hashtable;

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
import com.crowd.tool.misc.Products;
import com.crowd.tool.tapis.ba.BinanceDeliveryMarketAPI;
import com.crowd.tool.tapis.ctp.CtpMarketAPI;
import com.crowd.tool.tstrategy.impl.BacktestStrategyEnv;
import com.crowd.tool.tstrategy.impl.RealStrategyEnv;

public abstract class StrategyServiceBase implements CrowdService {

	// 实盘正在运行的策略执行环境缓存
	private Hashtable<String, StrategyEnv> runningStrategyEnvs = new Hashtable<String, StrategyEnv>();

	@Override
	public void init(CrowdInitContext context) throws Throwable {

	}

	/**
	 * 策略启动
	 */
	@CrowdWorker
	public void start(CrowdWorkerContext crowdContext, JSONObject inputObject) throws Throwable {
		String id = inputObject.getString("id");
		boolean testFlag = inputObject.optBoolean("test");
		StrategyInfo strategyInfo;
		if (testFlag) {
			strategyInfo = BacktestStrategyEnv.createStrategyInfo(crowdContext, id);
		} else {
			strategyInfo = RealStrategyEnv.createStrategyInfo(crowdContext, id);
		}
		if (strategyInfo != null) {
			try {
				StrategyEnv strategyEnv;
				Products products = new Products(
						crowdContext.load("products", strategyInfo.getProductGroup() + ".json"));
				if (testFlag) {
					strategyEnv = StrategyEnv.createTest(crowdContext, createStrategyInstance(), strategyInfo,
							products);
				} else {
					strategyEnv = StrategyEnv.createReal(crowdContext, createStrategyInstance(), strategyInfo,
							products);
					runningStrategyEnvs.put(id, strategyEnv);
				}
				try {
					String marketDataSource = strategyInfo.getMarketDataSource();
					if (marketDataSource.startsWith("file:")) {
						if (!testFlag) {
							throw new IllegalArgumentException("不支持使用文件数据来源推送实盘交易");
						}
						File file = new File(marketDataSource.substring("file:".length()));
						if (!file.exists()) {
							throw new IllegalArgumentException("指定文件不存在");
						}
						byte[] buffer = new byte[(int) file.length()];
						RandomAccessFile raf = new RandomAccessFile(file, "r");
						try {
							raf.read(buffer);
						} finally {
							raf.close();
						}
						JSONObject info = new JSONObject(new String(buffer));
						String symbol = info.getString("symbol");
						int totalCount = info.getInt("lineCount");
						int timeIndex = info.optInt("timeIndex", -1);
						int nanoTimeIndex = info.optInt("nanoTimeIndex", -1);
						int priceIndex = info.optInt("priceIndex", -1);
						int amountIndex = info.optInt("amountIndex", -1);
						int totalAmountIndex = info.optInt("totalAmountIndex", -1);
						//

						JSONArray fileNameArray = info.getJSONArray("files");
						int reportCount = 0;
						int finishCount = 0;
						for (int i = 0; i < fileNameArray.length(); i++) {
							raf = new RandomAccessFile(new File(file.getParentFile(), fileNameArray.getString(i)), "r");
							try {
								String line = null;
								BigDecimal lastTotalAmount = BigDecimal.ZERO;
								while ((line = raf.readLine()) != null) {
									if (crowdContext.isDisposed()) {
										break;
									}
									try {
										String[] arr = StringUtils.split(line, ",");
										long time = 0;
										BigDecimal amount = BigDecimal.ZERO;
										if (timeIndex >= 0) {
											time = Long.parseLong(arr[timeIndex]);
										} else if (nanoTimeIndex >= 0) {
											time = Long.parseLong(arr[nanoTimeIndex]) / 1000000;
										}
										if (amountIndex >= 0) {
											amount = new BigDecimal(arr[amountIndex]);
										} else if (totalAmountIndex >= 0) {
											BigDecimal newTotalAmount = new BigDecimal(arr[totalAmountIndex]);
											if (newTotalAmount.compareTo(lastTotalAmount) < 0) {
												amount = newTotalAmount;
											} else {
												amount = newTotalAmount.subtract(lastTotalAmount);
											}
											lastTotalAmount = newTotalAmount;
										}
										BigDecimal price = new BigDecimal(arr[priceIndex]);
										//
										strategyEnv.onTick(symbol, time, BigDecimal.ZERO, BigDecimal.ZERO, price,
												amount);
										reportCount++;
										finishCount++;
										if (reportCount >= (totalCount * 0.01)) {
											reportCount = 0;
											crowdContext.reportWork(new BigDecimal(finishCount)
													.divide(new BigDecimal(totalCount), 4, RoundingMode.HALF_UP)
													.floatValue(), "");
										}
									} catch (Throwable t) {

									}
								}
							} finally {
								raf.close();
							}
						}
					} else if (marketDataSource.startsWith("BA.S:")) {
						throw new IllegalArgumentException("暂不支持");
					} else if (marketDataSource.startsWith("BA.F:")) {
						throw new IllegalArgumentException("暂不支持");
					} else if (marketDataSource.startsWith("BA.D:")) {
						// 启动行情推送和自动化策略执行机制
						crowdContext.reportWork(1, ""); // XXX：将进度设置为1
						new BinanceDeliveryMarketAPI(
								StringUtils.split(marketDataSource.substring("BA.D:".length()), ",")) {
							@Override
							protected void handleMessage(JSONObject messageObject) {
								if (messageObject.optString("e").equals("aggTrade")) {
									String symbol = messageObject.getString("s");
									long time = messageObject.getLong("T");
									BigDecimal price = new BigDecimal(messageObject.optString("p"));
									BigDecimal amount = new BigDecimal(messageObject.optString("q"));
									crowdContext.reportWork(1, price.toString());
									strategyEnv.onTick(symbol, time, BigDecimal.ZERO, BigDecimal.ZERO, price, amount);
								}
							}

							protected boolean checkContextDisposed() {
								return crowdContext.isDisposed();
							}

						}.run();
					} else if (marketDataSource.startsWith("CTP:")) {
						String[] symbols = StringUtils.split(marketDataSource.substring("CTP:".length()), ",");
						String front = "tcp://140.206.242.115:42213"; // 正式行情(中信建投）
						new CtpMarketAPI(id, front, symbols) {

							@Override
							protected boolean checkContextDisposed() {
								return crowdContext.isDisposed();
							}

							@Override
							protected void handleMarketData(String symbol, long time, BigDecimal lowerLimitPrice,
									BigDecimal upperLimitPrice, BigDecimal price, BigDecimal amount) {
								crowdContext.reportWork(1, String.valueOf(price.doubleValue()));
								strategyEnv.onTick(symbol, time, lowerLimitPrice, upperLimitPrice, price, amount);
							}
						}.run();
					} else if (marketDataSource.startsWith("CTPTEST:")) {
						String[] symbols = StringUtils.split(marketDataSource.substring("CTPTEST:".length()), ",");
						String front = "tcp://180.168.146.187:10211"; // simnow行情
						new CtpMarketAPI(id, front, symbols) {

							@Override
							protected boolean checkContextDisposed() {
								return crowdContext.isDisposed();
							}

							@Override
							protected void handleMarketData(String symbol, long time, BigDecimal lowerLimitPrice,
									BigDecimal upperLimitPrice, BigDecimal price, BigDecimal amount) {
								crowdContext.reportWork(1, String.valueOf(price.doubleValue()));
								strategyEnv.onTick(symbol, time, lowerLimitPrice, upperLimitPrice, price, amount);
							}
						}.run();
					} else if (marketDataSource.startsWith("CTPMOCK:")) {
						String[] symbols = StringUtils.split(marketDataSource.substring("CTPMOCK:".length()), ",");
						String front = "tcp://180.168.146.187:10131"; // 全天测试行情
						new CtpMarketAPI(id, front, symbols) {

							@Override
							protected boolean checkContextDisposed() {
								return crowdContext.isDisposed();
							}

							@Override
							protected void handleMarketData(String symbol, long time, BigDecimal lowerLimitPrice,
									BigDecimal upperLimitPrice, BigDecimal price, BigDecimal amount) {
								crowdContext.reportWork(1, String.valueOf(price.doubleValue()));
								strategyEnv.onTick(symbol, time, lowerLimitPrice, upperLimitPrice, price, amount);
							}
						}.run();
					} else {
						throw new IllegalArgumentException("未知类型的数据源");
					}
				} finally {
					strategyEnv.dispose();
					if (!testFlag) {
						runningStrategyEnvs.remove(id);
					}
				}
			} catch (Throwable t) {
				if (testFlag) {
					BacktestStrategyEnv.saveStrategyError(crowdContext, id, t.getMessage());
				} else {
					RealStrategyEnv.saveStrategyError(crowdContext, id, t.getMessage());
				}
			}
		}
	}

	@CrowdMethod
	public void order(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		String strategyId = input.getString("strategyId");
		StrategyEnv strategyEnv = runningStrategyEnvs.get(strategyId);
		if (strategyEnv == null) {
			throw new IllegalStateException("无法找到指定策略执行环境，策略服务或已经停止");
		}
		JSONObject orderInfo = input.getJSONObject("orderInfo");
		OrderType orderType = OrderType.valueOf(orderInfo.getString("type"));
		if (orderType == OrderType.Open) {
			strategyEnv.handleManualOpen(PositionSide.valueOf(orderInfo.getString("side")),
					orderInfo.getString("symbol"), new BigDecimal(orderInfo.getString("price")),
					new BigDecimal(orderInfo.getString("takePrice")), new BigDecimal(orderInfo.getString("stopPrice")));
		} else {
			strategyEnv.forceClose(System.currentTimeMillis(), orderInfo.getInt("transactionId"));
		}
	}

	protected abstract IStrategy createStrategyInstance();

}
