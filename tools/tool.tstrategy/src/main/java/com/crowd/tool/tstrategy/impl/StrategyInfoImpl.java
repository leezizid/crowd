package com.crowd.tool.tstrategy.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.tool.misc.CurrencyType;
import com.crowd.tool.misc.ProductInfo;
import com.crowd.tool.tstrategy.OrderInfo;
import com.crowd.tool.tstrategy.StrategyInfo;
import com.crowd.tool.tstrategy.TransactionInfo;

/**
 * 策略信息
 */
final class StrategyInfoImpl implements StrategyInfo {

	/**
	 *  策略ID
	 */
	private String id;

	/**
	 *  策略名称
	 */
	private String name;

	/**
	 * 策略服务
	 */
	private String service;

	/**
	 * 参考市场数据
	 */
	private String marketDataSource;

	/**
	 *  通道ID
	 */
	private String channelId;

	/**
	 * 交易产品组ID
	 */
	private String productGroup;

	/**
	 * 策略规模（初始化指定，不可更改）
	 */
	private BigDecimal size;

	/**
	 * 策略盈亏(已关闭交易)
	 */
	private BigDecimal profit;

	/**
	 * 所有费用(已关闭交易)
	 */
	private BigDecimal cost;

	/**
	 * 
	 */
	private CurrencyType currencyType;

	/**
	 * 总交易数量（包括历史交易）
	 */
	private int transactionCount;

	/**
	 * 最后一次交易时间
	 */
	private long lastTransactionTime;

	/**
	 * 
	 */
	private String lastError;

	/**
	 * 当前活动交易列表（一般只有一笔交易）
	 */
	private List<TransactionInfoImpl> transactions = new ArrayList<TransactionInfoImpl>();
	
	/**
	 * 交易日列表
	 */
	private List<String> tradeDayList = new ArrayList<String>();
	
	/**
	 * 增加交易日
	 * @param tradeDay
	 */
	public void addTradeDay(String tradeDay) {
		tradeDayList.add(tradeDay);
	}

	/**
	 * 开始一笔交易
	 * 
	 * @param transactoinInfo
	 */
	public void addTransaction(TransactionInfoImpl transactoinInfo) {
		this.transactions.add(transactoinInfo);
		this.transactionCount++;
	}

	/**
	 * 更新一笔交易
	 * 
	 * @param transactoinInfo
	 */
	public void updateTransaction(TransactionInfoImpl transactoinInfo) {
		int index = -1;
		for (int i = 0; i < this.transactions.size(); i++) {
			if (this.transactions.get(i).getId() == transactoinInfo.getId()) {
				index = i;
			}
		}
		if (index == -1) {
			throw new IllegalArgumentException("找不到指定的交易ID");
		}
		this.transactions.set(index, transactoinInfo);
	}

	/**
	 * 结束一笔交易，清空并返回交易对象，以便单独存储历史交易数据
	 * 
	 * @param transactionId
	 * @return
	 */
	public TransactionInfoImpl endTransaction(ProductInfo productInfo, int transactionId, long time) {
		int index = -1;
		for (int i = 0; i < this.transactions.size(); i++) {
			if (this.transactions.get(i).getId() == transactionId) {
				index = i;
			}
		}
		if (index == -1) {
			throw new IllegalArgumentException("找不到指定的交易ID");
		}
		TransactionInfoImpl transactionInfo = this.transactions.get(index);
		if (!transactionInfo.canClose()) {
			throw new IllegalStateException("交易持仓数量不为0或存在未完成的订单，不能关闭");
		}
		this.transactions.remove(index);
 		transactionInfo.setCloseTime(time);
		// XXX：productInfo可能为空（下单时信息错误）
		if (productInfo != null && productInfo.isDelivery()) {
			// 资产本位的盈利计算正好相反（资产价格增加，实际余额会降低）
			transactionInfo.setBalance(transactionInfo.getBalance().negate());
		}
		this.profit = this.profit.add(transactionInfo.getBalance());
		this.cost = this.cost.add(transactionInfo.getCost());
		this.lastTransactionTime = transactionInfo.getCloseTime();
		return transactionInfo;
	}

	/**
	 * 获取当前交易列表
	 * 
	 * @return
	 */
	public TransactionInfo[] getTransactions() {
		return this.transactions.toArray(new TransactionInfo[0]);
	}

	/**
	 * 查找Transaction
	 * 
	 * @param clientOrderId
	 * @return
	 */
	public TransactionInfo findTransaction(String clientOrderId) {
		for (int i = 0; i < transactions.size(); i++) {
			for (OrderInfo orderInfo : transactions.get(i).getOrders()) {
				if (orderInfo.getClientOrderId().equals(clientOrderId)) {
					return transactions.get(i);
				}
			}
		}
		return null;
	}

	/**
	 * 查找Order
	 * 
	 * @param clientOrderId
	 * @return
	 */
	public OrderInfo findOrder(String clientOrderId) {
		for (int i = 0; i < transactions.size(); i++) {
			for (OrderInfo orderInfo : transactions.get(i).getOrders()) {
				if (orderInfo.getClientOrderId().equals(clientOrderId)) {
					return orderInfo;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param serverOrderId
	 * @return
	 */
	public OrderInfo findOrderByServerId(String serverOrderId) {
		for (int i = 0; i < transactions.size(); i++) {
			for (OrderInfo orderInfo : transactions.get(i).getOrders()) {
				if (serverOrderId.equals(orderInfo.getServerOrderId())) {
					return orderInfo;
				}
			}
		}
		return null;
	}

	/**
	 * 查找看是否存在正在提交的委托，主要是为了在获得成交消息时判断是否需要等待委托状态先完成
	 * 
	 * @param symbol
	 * @return
	 */
	public boolean existSubmitingOrder(String symbol) {
		for (int i = 0; i < transactions.size(); i++) {
			for (OrderInfo orderInfo : transactions.get(i).getOrders()) {
				if (StringUtils.isEmpty(orderInfo.getServerOrderId()) && orderInfo.getSymbol().equals(symbol)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	public String getMarketDataSource() {
		return marketDataSource;
	}

	public void setMarketDataSource(String marketDataSource) {
		this.marketDataSource = marketDataSource;
	}

	public BigDecimal getSize() {
		return size;
	}

	public void setSize(BigDecimal size) {
		this.size = size;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public CurrencyType getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(CurrencyType currencyType) {
		this.currencyType = currencyType;
	}

	public int getTransactionCount() {
		return transactionCount;
	}

	public void setTransactionCount(int transactionCount) {
		this.transactionCount = transactionCount;
	}

	public String getLastError() {
		return lastError;
	}

	public void setLastError(String lastError) {
		this.lastError = lastError;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public long getLastTransactionTime() {
		return lastTransactionTime;
	}

	/**
	 * 
	 * @param o
	 */
	public void fromJSON(JSONObject o) {
		this.id = o.getString("id");
		this.name = o.optString("name");
		this.service = o.optString("service");
		this.channelId = o.optString("channelId");
		this.productGroup = o.optString("productGroup");
		this.marketDataSource = o.optString("marketDataSource");
		this.size = new BigDecimal(o.optDouble("size"));
		this.profit = new BigDecimal(o.optDouble("profit"));
		this.cost = new BigDecimal(o.optDouble("cost"));
		this.currencyType = CurrencyType.valueOf(o.optString("currencyType"));
		this.transactionCount = o.optInt("transactionCount");
		this.lastTransactionTime = o.optLong("lastTransactionTime");
		this.lastError = o.optString("lastError");
		this.transactions.clear();
		JSONArray transactionArray = o.optJSONArray("transactions");
		if (transactionArray != null) {
			for (int i = 0; i < transactionArray.length(); i++) {
				TransactionInfoImpl transactoinInfo = new TransactionInfoImpl();
				transactoinInfo.fromJSON(transactionArray.getJSONObject(i));
				this.transactions.add(transactoinInfo);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public JSONObject toJSON() {
		JSONObject o = new JSONObject();
		o.put("id", id);
		o.put("name", name);
		o.put("service", service);
		o.put("channelId", channelId);
		o.put("productGroup", productGroup);
		o.put("marketDataSource", marketDataSource);
		o.put("size", size.doubleValue());
		o.put("profit", profit.doubleValue());
		o.put("cost", cost.doubleValue());
		o.put("currencyType", currencyType.name());
		o.put("transactionCount", transactionCount);
		o.put("lastTransactionTime", lastTransactionTime);
		o.put("lastError", lastError);
		JSONArray transactionArray = new JSONArray();
		for (int i = 0; i < this.transactions.size(); i++) {
			transactionArray.put(this.transactions.get(i).toJSON());
		}
		o.put("transactions", transactionArray);
		o.put("tradeDays", new JSONArray(tradeDayList));
		return o;
	}

}
