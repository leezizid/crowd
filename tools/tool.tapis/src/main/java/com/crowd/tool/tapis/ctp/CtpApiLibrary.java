package com.crowd.tool.tapis.ctp;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;

public interface CtpApiLibrary extends Library {

	static CtpApiLibrary instance = Native.load("ctpbridge", CtpApiLibrary.class);

	String initMarket(String id, String flowDir, String front, SpiCallback spiCallback);

	void releaseMarket(String id);

	public int reqMarketUserLogin(String id, int requestID);

	public int reqMarketUserLogout(String i, int requestID);

	int subscribe(String id, String[] instrumentIDs, int count);

	String initTrader(String id, String flowDir, String input, SpiCallback spiCallback);

	void releaseTrader(String id);

	public int reqAuthenticate(String id, int requestID);

	public int reqTraderUserLogin(String id, int requestID);

	public int reqTraderUserLogout(String i, int requestID);

	public int reqSettlementInfoConfirm(String id, int requestID);

	public int reqQryTradingAccount(String id, int requestID);

	public int reqQryInvestorPosition(String id, int requestID);

	public int reqQryOrder(String id, int requestID);

	public int reqQryTrade(String id, int requestID);

	public int reqPostOrder(String id, int requestID, String orderRef, String exchangeID, String instrumentID,
			char type, char direction, float price, int volume);

	public int reqCancelOrder(String id, int requestID, String exchangeID, String instrumentID, String orderID);
	
	public int reqQryClassifiedInstrument(String id, int requestID);

}

interface SpiCallback extends Callback {

	void handleMessage(String type, String message);

}
