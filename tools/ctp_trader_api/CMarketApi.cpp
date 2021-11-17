#pragma once
#define _CRT_SECURE_NO_WARNINGS

#include "CMarketApi.h"
#include "Tool.h"
#include "Base64.h"
#include "ThostFtdcMdApi.h"

using namespace std;


//初始化系统
const void CMarketApi::init(const char* dir, const char* marketFront, JnaResCallback jnaCallback)
{
	marketApi = CThostFtdcMdApi::CreateFtdcMdApi(dir);
	marketSpi = new CMarketSpi(jnaCallback);
	marketApi->RegisterSpi(marketSpi);
	marketApi->RegisterFront(const_cast<char*>(marketFront));
	marketApi->Init();
	//traderApi->Join();
}

//销毁
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


//登录
const int CMarketApi::reqUserLogin(int requestID)
{
	CThostFtdcReqUserLoginField loginInfo = { 0 };
	return marketApi->ReqUserLogin(&loginInfo, requestID);
}

//登出
const int CMarketApi::reqUserLogout(int requestID)
{
	CThostFtdcUserLogoutField  loginInfo = { 0 };
	return marketApi->ReqUserLogout(&loginInfo, requestID);
}

//订阅行情
const int CMarketApi::subscribe(char** instrumentIDs, int count)
{
	return marketApi->SubscribeMarketData(instrumentIDs, count);
}





