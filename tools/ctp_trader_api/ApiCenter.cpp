#pragma once
#define _CRT_SECURE_NO_WARNINGS

#include <map>
#include "Tool.h"
#include "CTraderApi.h"
#include "CMarketApi.h"

using namespace std;

map<string, CTraderApi*> traderApis;
map<string, CMarketApi*> marketApis;


//��ʼ����ӿ�
extern "C" __declspec(dllexport) const void initMarket(char* id, const char* dir, const char* marketFront, JnaResCallback jnaResCallback);
const void initMarket(char* id, const char* dir, const char* marketFront, JnaResCallback jnaCallback)
{
	marketApis[id] = new CMarketApi();
	marketApis[id]->init(dir, marketFront, jnaCallback);
}

//��������ӿ�
extern "C" __declspec(dllexport) const void releaseMarket(char* id);
const void releaseMarket(char* id)
{
	marketApis[id]->release();
	marketApis.erase(id);
}

//�����¼
extern "C" __declspec(dllexport) const int reqMarketUserLogin(char* id, int requestID);
const int reqMarketUserLogin(char* id, int requestID)
{
	return marketApis[id]->reqUserLogin(requestID);
}

//����ǳ�
extern "C" __declspec(dllexport) const int reqMarketUserLogout(char* id, int requestID);
const int reqMarketUserLogout(char* id, int requestID)
{
	return marketApis[id]->reqUserLogout(requestID);
}

//��������
extern "C" __declspec(dllexport) const int subscribe(char* id, char** instrumentIDs, int count);
const int subscribe(char* id, char** instrumentIDs, int count)
{
	return marketApis[id]->subscribe(instrumentIDs, count);
}

//��ʼ�����׽ӿ�
extern "C" __declspec(dllexport) const void initTrader(char* id, const char* dir, const char* input, JnaResCallback jnaResCallback);
const void initTrader(char* id, const char* dir, const char* input, JnaResCallback jnaCallback)
{
	traderApis[id] = new CTraderApi();
	traderApis[id]->init(dir, input, jnaCallback);
}

//���ٽ��׽ӿ�
extern "C" __declspec(dllexport) const void releaseTrader(char* id);
const void releaseTrader(char* id)
{
	traderApis[id]->release();
	traderApis.erase(id);
}

//��֤�ͻ���
extern "C" __declspec(dllexport) const int reqAuthenticate(char* id, int requestID);
const int reqAuthenticate(char* id, int requestID)
{
	return traderApis[id]->reqAuthenticate(requestID);
}


//���׵�¼
extern "C" __declspec(dllexport) const int reqTraderUserLogin(char* id, int requestID);
const int reqTraderUserLogin(char* id, int requestID)
{
	return traderApis[id]->reqUserLogin(requestID);
}

//���׵ǳ�
extern "C" __declspec(dllexport) const int reqTraderUserLogout(char* id, int requestID);
const int reqTraderUserLogout(char* id, int requestID)
{
	return traderApis[id]->reqUserLogout(requestID);
}


//ȷ�Ͻ��㵥
extern "C" __declspec(dllexport) const int reqSettlementInfoConfirm(char* id, int requestID);
const int reqSettlementInfoConfirm(char* id, int requestID)
{
	return traderApis[id]->reqSettlementInfoConfirm(requestID);
}


//��ѯ�ʽ��˻�
extern "C" __declspec(dllexport)const int reqQryTradingAccount(char* id, int requestID);
const int reqQryTradingAccount(char* id, int requestID)
{
	return traderApis[id]->reqQryTradingAccount(requestID);
}

//��ѯ�ֲ�
extern "C" __declspec(dllexport)const int reqQryInvestorPosition(char* id, int requestID);
const int reqQryInvestorPosition(char* id, int requestID)
{
	return traderApis[id]->reqQryInvestorPosition(requestID);
}

//��ѯί��
extern "C" __declspec(dllexport)const int reqQryOrder(char* id, int requestID);
const int reqQryOrder(char* id, int requestID)
{
	return traderApis[id]->reqQryOrder(requestID);
}

//��ѯ�ɽ�
extern "C" __declspec(dllexport)const int reqQryTrade(char* id, int requestID);
const int reqQryTrade(char* id, int requestID)
{
	return traderApis[id]->reqQryTrade(requestID);
}

//����ί��
extern "C" __declspec(dllexport)const int reqCancelOrder(char* id, int requestID, const char* exchangeID, const char* instrumentID, const char* orderID);
const int reqCancelOrder(char* id, int requestID, const char* exchangeID, const char* instrumentID, const char* orderID)
{
	return traderApis[id]->reqCancelOrder(requestID, exchangeID, instrumentID, orderID);
}


//����ί��
extern "C" __declspec(dllexport)const int reqPostOrder(char* id, int requestID, const char* orderRef, const char* exchangeID, const char* instrumentID, char type, char direction, float price, int volumn);
const int reqPostOrder(char* id, int requestID, const char* orderRef, const char* exchangeID, const char* instrumentID,char type, char direction, float price, int volumn)
{
	return traderApis[id]->reqPostOrder(requestID, orderRef, exchangeID, instrumentID, type, direction, price, volumn);
}


//��ѯ��Լ
extern "C" __declspec(dllexport)const int reqQryClassifiedInstrument(char* id, int requestID);
const int reqQryClassifiedInstrument(char* id, int requestID)
{
	return traderApis[id]->reqQryClassifiedInstrument(requestID);
}