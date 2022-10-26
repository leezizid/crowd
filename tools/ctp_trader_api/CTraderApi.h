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

	//��ʼ��ϵͳ
	virtual const void init(const char* dir, const char* input, JnaResCallback jnaCallback);

	//����
	virtual const void release();

	//��֤�ͻ���
	virtual const int reqAuthenticate(int requestID);

	//��¼
	virtual const int reqUserLogin(int requestID);

	//�ǳ�
	virtual const int reqUserLogout(int requestID);

	//ȷ�Ͻ��㵥
	virtual const int reqSettlementInfoConfirm(int requestID);

	//��ѯ�ʽ��˻�
	virtual const int reqQryTradingAccount(int requestID);

	//��ѯ�ֲֽӿ�
	virtual const int reqQryInvestorPosition(int requestID);

	//��ѯί�нӿ�
	virtual const int reqQryOrder(int requestID);

	//��ѯ���׽ӿ�
	virtual const int reqQryTrade(int requestID);

	//�����ӿ�
	virtual const int reqCancelOrder(int requestID, const char* exchangeID, const char* instrumentID, const char* orderID);

	//����ί�нӿ�
	virtual const int reqPostOrder(int requestID, const char* orderRef, const char* exchangeID, const char* instrumentID, char type, char direction, float price, int volumn);


	//��ѯ��Լ�ӿ�
	virtual const int reqQryClassifiedInstrument(int requestID);

	//
	virtual const char* getApiVersion();


};




