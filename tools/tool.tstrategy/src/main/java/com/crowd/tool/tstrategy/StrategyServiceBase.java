package com.crowd.tool.tstrategy;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import com.crowd.tool.misc.TradeDays;
import com.crowd.tool.misc.k.HistoryData;
import com.crowd.tool.misc.k.TickInfo;
import com.crowd.tool.tapis.ba.BinanceDeliveryMarketAPI;
import com.crowd.tool.tapis.ctp.CTPProducts;
import com.crowd.tool.tapis.ctp.CtpMarketAPI;
import com.crowd.tool.tstrategy.impl.BacktestStrategyEnv;
import com.crowd.tool.tstrategy.impl.RealStrategyEnv;

public abstract class StrategyServiceBase implements CrowdService {

	// 实盘正在运行的策略执行环境缓存
	private Hashtable<String, StrategyEnv> runningStrategyEnvs = new Hashtable<String, StrategyEnv>();

	@Override
	public void init(CrowdInitContext context) throws Throwable {
		CTPProducts.init();
	}

	@Override
	public void postInit(CrowdInitContext context) throws Throwable {

	}

	/**
	 * 策略启动
	 */
	@CrowdWorker
	public void start(CrowdWorkerContext crowdContext, JSONObject inputObject) throws Throwable {
		String id = inputObject.getString("id");
		boolean test = inputObject.optBoolean("test");
		StrategyInfo strategyInfo;
		if (test) {
			strategyInfo = BacktestStrategyEnv.createStrategyInfo(crowdContext, id);
		} else {
			strategyInfo = RealStrategyEnv.createStrategyInfo(crowdContext, id);
		}
		if (strategyInfo != null) {
			try {
				StrategyEnv strategyEnv;
				String productGroup = strategyInfo.getProductGroup(); // 格式类似：ctp.yly
				String productGroupType = productGroup.substring(0, productGroup.indexOf("."));
				JSONObject mainDateInfos = crowdContext.invoke("/tchannel." + productGroupType + "/mainDateInfos",
						new JSONObject()); // 调用特定通道服务方法获取主力合约信息
				if (test) {
					Products products = new Products(crowdContext.load("products", productGroup + ".json"), null);
					strategyEnv = StrategyEnv.createTest(crowdContext,
							createStrategyInstance(strategyInfo.getArguments()), strategyInfo, products);
				} else {
					Products products = new Products(crowdContext.load("products", productGroup + ".json"),
							mainDateInfos);
					strategyEnv = StrategyEnv.createReal(crowdContext,
							createStrategyInstance(strategyInfo.getArguments()), strategyInfo, products);
					runningStrategyEnvs.put(id, strategyEnv);
				}
				try {
					String marketDataSource = strategyInfo.getMarketDataSource();
					if (marketDataSource.startsWith("history:")) {
						if (!test) {
							throw new IllegalArgumentException("不支持使用文件数据来源推送实盘交易");
						}
						String[] info = StringUtils.split(marketDataSource.substring("history:".length()), ",");
						String symbol = info[0];
						String startDay = info[1];
						String endDay = info[2];
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						String[] tradeDays = TradeDays.getTradeDayList(
								TradeDays.matchTradeDay(sdf.parse(startDay).getTime()),
								TradeDays.matchTradeDay(sdf.parse(endDay).getTime()));
						try {
							int finishCount = 0;
							int totalCount = tradeDays.length;
							for (String tradeDay : tradeDays) {
								if (crowdContext.isDisposed()) {
									break;
								}
								BigDecimal lastTotalVolume = BigDecimal.ZERO;
								byte[] content = HistoryData.readTradeDayTickData(symbol, tradeDay);
								DataInputStream dis = new DataInputStream(new ByteArrayInputStream(content));
								int count = content.length / 36;
								for (int i = 0; i < count; i++) {
									TickInfo tickInfo = new TickInfo();
									tickInfo.readFromStream(dis);
									strategyEnv.onTick(symbol + "", tickInfo.getTime(), BigDecimal.ZERO,
											BigDecimal.ZERO, tickInfo.getLastPrice(),
											tickInfo.getVolume().subtract(lastTotalVolume), tickInfo.getOpenInterest());
									lastTotalVolume = tickInfo.getVolume();
								}
								crowdContext.reportWork(new BigDecimal(++finishCount)
										.divide(new BigDecimal(totalCount), 4, RoundingMode.HALF_UP).floatValue(), "");
							}
						} catch (Throwable t) {
							t.printStackTrace();
						} finally {
//							System.out.println();
						}
					} else if (marketDataSource.startsWith("file:")) {
						if (!test) {
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
						int volumeIndex = info.optInt("volumeIndex", -1);
						int totalVolumeIndex = info.optInt("totalVolumeIndex", -1);
						//

						JSONArray fileNameArray = info.getJSONArray("files");
						int reportCount = 0;
						int finishCount = 0;
						for (int i = 0; i < fileNameArray.length(); i++) {
							raf = new RandomAccessFile(new File(file.getParentFile(), fileNameArray.getString(i)), "r");
							try {
								String line = null;
								BigDecimal lastTotalVolume = BigDecimal.ZERO;
								while ((line = raf.readLine()) != null) {
									if (crowdContext.isDisposed()) {
										break;
									}
									try {
										String[] arr = StringUtils.split(line, ",");
										long time = 0;
										BigDecimal volume = BigDecimal.ZERO;
										if (timeIndex >= 0) {
											time = Long.parseLong(arr[timeIndex]);
										} else if (nanoTimeIndex >= 0) {
											time = Long.parseLong(arr[nanoTimeIndex]) / 1000000;
										}
										if (volumeIndex >= 0) {
											volume = new BigDecimal(arr[volumeIndex]);
										} else if (totalVolumeIndex >= 0) {
											BigDecimal newTotalVolume = new BigDecimal(arr[totalVolumeIndex]);
											if (newTotalVolume.compareTo(lastTotalVolume) < 0) {
												volume = newTotalVolume;
											} else {
												volume = newTotalVolume.subtract(lastTotalVolume);
											}
											lastTotalVolume = newTotalVolume;
										}
										BigDecimal price = new BigDecimal(arr[priceIndex]);
										//
										strategyEnv.onTick(symbol, time, BigDecimal.ZERO, BigDecimal.ZERO, price,
												volume, BigDecimal.ZERO);
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
									BigDecimal volume = new BigDecimal(messageObject.optString("q"));
									crowdContext.reportWork(1, price.toString());
									strategyEnv.onTick(symbol, time, BigDecimal.ZERO, BigDecimal.ZERO, price, volume,
											BigDecimal.ZERO);
								}
							}

							protected boolean checkContextDisposed() {
								return crowdContext.isDisposed();
							}

						}.run();
					} else if (marketDataSource.startsWith("CTP:")) {
						String symbol = marketDataSource.substring("CTP:".length());
						String front = "tcp://" + System.getProperty("MarketDataServer"); //
						new CtpMarketAPI(id, front, symbol + mainDateInfos.getString(symbol)) {

							@Override
							protected boolean checkContextDisposed() {
								Calendar calendar = Calendar.getInstance();
								calendar.setTimeInMillis(System.currentTimeMillis());
								int hour = calendar.get(Calendar.HOUR_OF_DAY);
								if (hour == 16) {
									return true; // 每天16点自动关闭策略，需要重新开始以刷新主力合约
								}
								return crowdContext.isDisposed();
							}

							@Override
							protected void handleMarketData(String symbol, BigDecimal lowerLimitPrice,
									BigDecimal upperLimitPrice, long time, BigDecimal price, int volume,
									int openInterest, BigDecimal bidPrice1, int bidVolume1, BigDecimal askPrice1,
									int askVolume1) {
								crowdContext.reportWork(1, String.valueOf(price.doubleValue()));
								strategyEnv.onTick(symbol, time, lowerLimitPrice, upperLimitPrice, price,
										new BigDecimal(volume), new BigDecimal(openInterest));
							}
						}.run();
					} else if (marketDataSource.startsWith("CTPTEST:")) {
						String symbol = marketDataSource.substring("CTPTEST:".length());
						String front = "tcp://180.168.146.187:10211"; // simnow行情
						new CtpMarketAPI(id, front, symbol + mainDateInfos.getString(symbol)) {

							@Override
							protected boolean checkContextDisposed() {
								return crowdContext.isDisposed();
							}

							@Override
							protected void handleMarketData(String symbol, BigDecimal lowerLimitPrice,
									BigDecimal upperLimitPrice, long time, BigDecimal price, int volume,
									int openInterest, BigDecimal bidPrice1, int bidVolume1, BigDecimal askPrice1,
									int askVolume1) {
								crowdContext.reportWork(1, String.valueOf(price.doubleValue()));
								strategyEnv.onTick(symbol, time, lowerLimitPrice, upperLimitPrice, price,
										new BigDecimal(volume), new BigDecimal(openInterest));
							}
						}.run();
					} else if (marketDataSource.startsWith("CTPMOCK:")) {
						String symbol = marketDataSource.substring("CTPMOCK:".length());
						String front = "tcp://180.168.146.187:10131"; // 全天测试行情
						new CtpMarketAPI(id, front, symbol + mainDateInfos.getString(symbol)) {

							@Override
							protected boolean checkContextDisposed() {
								return crowdContext.isDisposed();
							}

							@Override
							protected void handleMarketData(String symbol, BigDecimal lowerLimitPrice,
									BigDecimal upperLimitPrice, long time, BigDecimal price, int volume,
									int openInterest, BigDecimal bidPrice1, int bidVolume1, BigDecimal askPrice1,
									int askVolume1) {
								crowdContext.reportWork(1, String.valueOf(price.doubleValue()));
								strategyEnv.onTick(symbol, time, lowerLimitPrice, upperLimitPrice, price,
										new BigDecimal(volume), new BigDecimal(openInterest));
							}
						}.run();
					} else {
						throw new IllegalArgumentException("未知类型的数据源");
					}
				} finally {
					strategyEnv.dispose(crowdContext);
					if (!test) {
						runningStrategyEnvs.remove(id);
					}
				}
			} catch (Throwable t) {
				if (test) {
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

	protected abstract IStrategy createStrategyInstance(String arguments);

}
