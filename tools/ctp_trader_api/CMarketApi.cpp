#pragma once
#define _CRT_SECURE_NO_WARNINGS

#include "CMarketApi.h"
#include "Tool.h"
#include "Base64.h"
#include "ThostFtdcMdApi.h"

using namespace std;


//��ʼ��ϵͳ
const void CMarketApi::init(const char* dir, const char* marketFront, JnaResCallback jnaCallback)
{
	marketApi = CThostFtdcMdApi::CreateFtdcMdApi(dir);
	marketSpi = new CMarketSpi(jnaCallback);
	marketApi->RegisterSpi(marketSpi);
	marketApi->RegisterFront(const_cast<char*>(marketFront));
	marketApi->Init();
	//traderApi->Join();
}

//����
const void CMarketApi::release()
{
	if (marketApi)
	{
		marketApi->Release();
		marketApi = NULL;
	}
	if (marketSpi) {
		marketSpi = NULL;
	}
}


//��¼
const int CMarketApi::reqUserLogin(int requestID)
{
	CThostFtdcReqUserLoginField loginInfo = { 0 };
	return marketApi->ReqUserLogin(&loginInfo, requestID);
}

//�ǳ�
const int CMarketApi::reqUserLogout(int requestID)
{
	CThostFtdcUserLogoutField  loginInfo = { 0 };
	return marketApi->ReqUserLogout(&loginInfo, requestID);
}

//��������
const int CMarketApi::subscribe(char** instrumentIDs, int count)
{
	return marketApi->SubscribeMarketData(instrumentIDs, count);
}





