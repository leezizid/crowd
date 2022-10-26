#pragma once
#define _CRT_SECURE_NO_WARNINGS

#include <map>
#include "Tool.h"
#include "CTraderApi.h"
#include "CMarketApi.h"

using namespace std;

map<string, CTraderApi*> traderApis;
map<string, CMarketApi*> marketApis;


//初始行情接口
extern "C" __declspec(dllexport) const void initMarket(char* id, const char* dir, const char* marketFront, JnaResCallback jnaResCallback);
const void initMarket(char* id, const char* dir, const char* marketFront, JnaResCallback jnaCallback)
{
	marketApis[id] = new CMarketApi();
	marketApis[id]->init(dir, marketFront, jnaCallback);
}

//销毁行情接口
extern "C" __declspec(dllexport) const void releaseMarket(char* id);
const void releaseMarket(char* id)
{
	marketApis[id]->release();
	marketApis.erase(id);
}

//行情登录
extern "C" __declspec(dllexport) const int reqMarketUserLogin(char* id, int requestID);
const int reqMarketUserLogin(char* id, int requestID)
{
	return marketApis[id]->reqUserLogin(requestID);
}

//行情登出
extern "C" __declspec(dllexport) const int reqMarketUserLogout(char* id, int requestID);
const int reqMarketUserLogout(char* id, int requestID)
{
	return marketApis[id]->reqUserLogout(requestID);
}

//订阅行情
extern "C" __declspec(dllexport) const int subscribe(char* id, char** instrumentIDs, int count);
const int subscribe(char* id, char** instrumentIDs, int count)
{
	return marketApis[id]->subscribe(instrumentIDs, count);
}

//初始化交易接口
extern "C" __declspec(dllexport) const void initTrader(char* id, const char* dir, const char* input, JnaResCallback jnaResCallback);
const void initTrader(char* id, const char* dir, const char* input, JnaResCallback jnaCallback)
{
	traderApis[id] = new CTraderApi();
	traderApis[id]->init(dir, input, jnaCallback);
}

//销毁交易接口
extern "C" __declspec(dllexport) const void releaseTrader(char* id);
const void releaseTrader(char* id)
{
	traderApis[id]->release();
	traderApis.erase(id);
}

//认证客户端
extern "C" __declspec(dllexport) const int reqAuthenticate(char* id, int requestID);
const int reqAuthenticate(char* id, int requestID)
{
	return traderApis[id]->reqAuthenticate(requestID);
}


//交易登录
extern "C" __declspec(dllexport) const int reqTraderUserLogin(char* id, int requestID);
const int reqTraderUserLogin(char* id, int requestID)
{
	return traderApis[id]->reqUserLogin(requestID);
}

//交易登出
extern "C" __declspec(dllexport) const int reqTraderUserLogout(char* id, int requestID);
const int reqTraderUserLogout(char* id, int requestID)
{
	return traderApis[id]->reqUserLogout(requestID);
}


//确认结算单
extern "C" __declspec(dllexport) const int reqSettlementInfoConfirm(char* id, int requestID);
const int reqSettlementInfoConfirm(char* id, int requestID)
{
	return traderApis[id]->reqSettlementInfoConfirm(requestID);
}


//查询资金账户
extern "C" __declspec(dllexport)const int reqQryTradingAccount(char* id, int requestID);
const int reqQryTradingAccount(char* id, int requestID)
{
	return traderApis[id]->reqQryTradingAccount(requestID);
}

//查询持仓
extern "C" __declspec(dllexport)const int reqQryInvestorPosition(char* id, int requestID);
const int reqQryInvestorPosition(char* id, int requestID)
{
	return traderApis[id]->reqQryInvestorPosition(requestID);
}

//查询委托
extern "C" __declspec(dllexport)const int reqQryOrder(char* id, int requestID);
const int reqQryOrder(char* id, int requestID)
{
	return traderApis[id]->reqQryOrder(requestID);
}

//查询成交
extern "C" __declspec(dllexport)const int reqQryTrade(char* id, int requestID);
const int reqQryTrade(char* id, int requestID)
{
	return traderApis[id]->reqQryTrade(requestID);
}

//撤销委托
extern "C" __declspec(dllexport)const int reqCancelOrder(char* id, int requestID, const char* exchangeID, const char* instrumentID, const char* orderID);
const int reqCancelOrder(char* id, int requestID, const char* exchangeID, const char* instrumentID, const char* orderID)
{
	return traderApis[id]->reqCancelOrder(requestID, exchangeID, instrumentID, orderID);
}


//撤销委托
extern "C" __declspec(dllexport)const int reqPostOrder(char* id, int requestID, const char* orderRef, const char* exchangeID, const char* instrumentID, char type, char direction, float price, int volumn);
const int reqPostOrder(char* id, int requestID, const char* orderRef, const char* exchangeID, const char* instrumentID,char type, char direction, float price, int volumn)
{
	return traderApis[id]->reqPostOrder(requestID, orderRef, exchangeID, instrumentID, type, direction, price, volumn);
}


//查询合约
extern "C" __declspec(dllexport)const int reqQryClassifiedInstrument(char* id, int requestID);
const int reqQryClassifiedInstrument(char* id, int requestID)
{
	return traderApis[id]->reqQryClassifiedInstrument(requestID);
}