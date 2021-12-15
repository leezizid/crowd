package com.crowd.tool.tstrategy;

import java.math.BigDecimal;

import com.crowd.tool.misc.CurrencyType;

/**
 * 策略信息
 */
public interface StrategyInfo {

	public TransactionInfo[] getTransactions();

	public TransactionInfo findTransaction(String clientOrderId);

	public int getTransactionCount();

	public long getLastTransactionTime();

	public String getLastError();

	public OrderInfo findOrder(String clientOrderId);

	public String getId();

	public String getName();
	
	public String getArguments();

	/**
	 * 
	 * @return
	 */
	public BigDecimal getSize();

	/**
	 * 
	 * @return
	 */
	public BigDecimal getProfit();

	/**
	 * 
	 * @return
	 */
	public BigDecimal getCost();

	/**
	 * 货币类型
	 * 
	 * @return
	 */
	public CurrencyType getCurrencyType();

	/**
	 * 策略服务名：一般而言，对于不同语言环境，都会编写一个基础策略服务，策略编写者继承该服务并注册，这里指定即可
	 * 
	 * @return
	 */
	public String getService();

	/**
	 * 市场数据来源（可以是实时数据来源，也可以是历史数据文件）。格式：file:</xxx/xxx/xxxx.xxx>、binance.delivery:xxxx@aggTrade等等，具体根据实际支持情况而定
	 * 注意：实际策略交易的标的，未必和这里定义的市场来源一致。有些市场来源数据可以同时推送多个标的，有些市场（股票）不会实时推送个股，而是推送大盘指数，然后再通过指标过滤出个股进行交易
	 * 
	 * @return
	 */
	public String getMarketDataSource();

	/**
	 * 通道ID，仅用于实盘；回测环境下不需要通道
	 * 
	 * @return
	 */
	public String getChannelId();
	
	/**
	 * 支持交易的产品组ID
	 * @return
	 */
	public String getProductGroup();

}
