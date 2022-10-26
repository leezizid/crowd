#pragma once
#include "stdafx.h"
#include <list>
#include <iostream>
#include <string>
#include <stdio.h>
#include <windows.h>
#include <time.h>
#include "ThostFtdcTraderApi.h"
#include "DataCollect.h"
#include <conio.h>
#include "CTraderSpi.h"
#include <vector>
#include <map>
#include <json.h>
using namespace std;


class CTraderApi {

public:
	CTraderApi() {};
	~CTraderApi() {};

private:
	CThostFtdcTraderApi* ctpApi;
	CTraderSpi* ctpSpi;

	string traderFront;
	string brokerID;
	string authCode;
	string productInfo;
	string appID;
	string userID;
	string password;
	string investorID;
	string macAddress;

public:

	//初始化系统
	virtual const void init(const char* dir, const char* input, JnaResCallback jnaCallback);

	//销毁
	virtual const void release();

	//认证客户端
	virtual const int reqAuthenticate(int requestID);

	//登录
	virtual const int reqUserLogin(int requestID);

	//登出
	virtual const int reqUserLogout(int requestID);

	//确认结算单
	virtual const int reqSettlementInfoConfirm(int requestID);

	//查询资金账户
	virtual const int reqQryTradingAccount(int requestID);

	//查询持仓接口
	virtual const int reqQryInvestorPosition(int requestID);

	//查询委托接口
	virtual const int reqQryOrder(int requestID);

	//查询交易接口
	virtual const int reqQryTrade(int requestID);

	//撤单接口
	virtual const int reqCancelOrder(int requestID, const char* exchangeID, const char* instrumentID, const char* orderID);

	//请求委托接口
	virtual const int reqPostOrder(int requestID, const char* orderRef, const char* exchangeID, const char* instrumentID, char type, char direction, float price, int volumn);


	//查询合约接口
	virtual const int reqQryClassifiedInstrument(int requestID);

	//
	virtual const char* getApiVersion();


};




