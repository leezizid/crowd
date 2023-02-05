#pragma once
#define _CRT_SECURE_NO_WARNINGS

#include "CTraderApi.h"
#include "Tool.h"
#include "Base64.h"
#include "ThostFtdcTraderApi.h"

using namespace std;


//初始化系统
const char* CTraderApi::init(const char* dir, const char* input, JnaResCallback jnaCallback)
{
	Json::Value inputObject;
	inputObject = parseJsonString(Base64_Decode(input).c_str());

	//
	traderFront = inputObject["traderFront"].asString();
	brokerID = inputObject["brokerID"].asString();
	authCode = inputObject["authCode"].asString();
	productInfo = inputObject["productInfo"].asString();
	appID = inputObject["appID"].asString();
	userID = inputObject["userID"].asString();
	password = inputObject["password"].asString();
	investorID = inputObject["investorID"].asString();
	macAddress = inputObject["macAddress"].asString();
	//
	ctpApi = CThostFtdcTraderApi::CreateFtdcTraderApi(dir);
	ctpSpi = new CTraderSpi(jnaCallback);
	ctpApi->RegisterSpi(ctpSpi);
	ctpApi->SubscribePrivateTopic(THOST_TERT_QUICK);
	ctpApi->SubscribePublicTopic(THOST_TERT_QUICK);
	ctpApi->RegisterFront(const_cast<char*>(traderFront.c_str()));
	ctpApi->Init();
	//traderApi->Join();
	return ctpApi->GetApiVersion();
}

//销毁
const void CTraderApi::release()
{
	if (ctpApi)
	{
		ctpApi->Release();
		ctpApi = NULL;
	}
	if (ctpSpi) {
		ctpSpi = NULL;
	}
}

//认证客户端
const int CTraderApi::reqAuthenticate(int requestID)
{
	CThostFtdcReqAuthenticateField authInfo = { 0 };
	strcpy_s(authInfo.BrokerID, brokerID.c_str());
	strcpy_s(authInfo.UserID, userID.c_str());
	strcpy_s(authInfo.UserProductInfo, productInfo.c_str());
	strcpy_s(authInfo.AuthCode, authCode.c_str());
	strcpy_s(authInfo.AppID, appID.c_str());
	return ctpApi->ReqAuthenticate(&authInfo, requestID);
}


//登录
const int CTraderApi::reqUserLogin(int requestID)
{
	CThostFtdcReqUserLoginField loginInfo = { 0 };
	strcpy_s(loginInfo.BrokerID, brokerID.c_str());
	strcpy_s(loginInfo.UserID, userID.c_str());
	strcpy_s(loginInfo.Password, password.c_str());
	//strcpy_s(reqUserLogin.ClientIPAddress, "::c0a8:0101");
	//strcpy_s(reqUserLogin.UserProductInfo, "123");
	return ctpApi->ReqUserLogin(&loginInfo, requestID);
}

//登出
const int CTraderApi::reqUserLogout(int requestID)
{
	CThostFtdcUserLogoutField  loginInfo = { 0 };
	strcpy_s(loginInfo.BrokerID, brokerID.c_str());
	strcpy_s(loginInfo.UserID, userID.c_str());
	return ctpApi->ReqUserLogout(&loginInfo, requestID);
}


//确认结算单
const int CTraderApi::reqSettlementInfoConfirm(int requestID)
{
	CThostFtdcSettlementInfoConfirmField a = { 0 };
	strcpy_s(a.BrokerID, brokerID.c_str());
	strcpy_s(a.InvestorID, investorID.c_str());
	return ctpApi->ReqSettlementInfoConfirm(&a, requestID);
}


//查询资金账户
const int CTraderApi::reqQryTradingAccount(int requestID)
{
	CThostFtdcQryTradingAccountField a = { 0 };
	strcpy_s(a.BrokerID, brokerID.c_str());
	strcpy_s(a.InvestorID, investorID.c_str());
	strcpy_s(a.CurrencyID, "CNY");
	return ctpApi->ReqQryTradingAccount(&a, requestID);
}


//查询持仓接口
const int CTraderApi::reqQryInvestorPosition(int requestID)
{
	CThostFtdcQryInvestorPositionField  a = { 0 };
	strcpy_s(a.BrokerID, brokerID.c_str());
	strcpy_s(a.InvestorID, investorID.c_str());
	//strcpy_s(a.InstrumentID, instrumentID);
	//strcpy_s(a.ExchangeID, exchangeID);
	return ctpApi->ReqQryInvestorPosition(&a, requestID);
}

//查询委托接口
const int CTraderApi::reqQryOrder(int requestID)
{
	CThostFtdcQryOrderField   a = { 0 };
	strcpy_s(a.BrokerID, brokerID.c_str());
	strcpy_s(a.InvestorID, investorID.c_str());
	return ctpApi->ReqQryOrder(&a, requestID);
}

//查询成交接口
const int CTraderApi::reqQryTrade(int requestID)
{
	CThostFtdcQryTradeField    a = { 0 };
	strcpy_s(a.BrokerID, brokerID.c_str());
	strcpy_s(a.InvestorID, investorID.c_str());
	return ctpApi->ReqQryTrade(&a, requestID);
}

//撤单接口
const int CTraderApi::reqCancelOrder(int requestID, const char* exchangeID, const char* instrumentID, const char* orderID)
{
	CThostFtdcInputOrderActionField a = { 0 };
	strcpy_s(a.BrokerID, brokerID.c_str());
	strcpy_s(a.InvestorID, investorID.c_str());
	strcpy_s(a.MacAddress, macAddress.c_str());
	strcpy_s(a.UserID, userID.c_str());
	strcpy_s(a.OrderSysID, orderID);
	strcpy_s(a.ExchangeID, exchangeID);
	strcpy_s(a.InstrumentID, instrumentID);
	a.ActionFlag = THOST_FTDC_AF_Delete;
	//a.RequestID = requestID;
	return ctpApi->ReqOrderAction(&a, requestID);
}


//提交委托接口
const int CTraderApi::reqPostOrder(int requestID, const char* orderRef, const char* exchangeID, const char* instrumentID, char type, char direction, float price, int volumn)
{
	CThostFtdcInputOrderField a = { 0 };
	strcpy_s(a.BrokerID, brokerID.c_str());
	strcpy_s(a.InvestorID, investorID.c_str());
	strcpy_s(a.MacAddress, macAddress.c_str());
	strcpy_s(a.UserID, userID.c_str());
	strcpy_s(a.ExchangeID, exchangeID);
	strcpy_s(a.InstrumentID, instrumentID);
	strcpy_s(a.OrderRef, orderRef);
	//
	a.CombOffsetFlag[0] = type;
	a.Direction = direction;
	a.VolumeTotalOriginal = volumn;
	a.RequestID = requestID;
	//
	if (price == 0) {
		a.OrderPriceType = THOST_FTDC_OPT_AnyPrice;//市价
	}
	else {
		a.OrderPriceType = THOST_FTDC_OPT_LimitPrice;//限价
		a.LimitPrice = price;
	}
	a.CombHedgeFlag[0] = THOST_FTDC_HF_Speculation;//投机
	a.TimeCondition = THOST_FTDC_TC_GFD;///当日有效
	a.VolumeCondition = THOST_FTDC_VC_AV;///任意数量
	a.MinVolume = 1;
	a.ContingentCondition = THOST_FTDC_CC_Immediately;
	a.StopPrice = 0;
	a.ForceCloseReason = THOST_FTDC_FCC_NotForceClose;
	a.IsAutoSuspend = 0;
	return ctpApi->ReqOrderInsert(&a, requestID);
}


//查询期货合约列表
const int CTraderApi::reqQryClassifiedInstrument(int requestID)
{
	CThostFtdcQryClassifiedInstrumentField  a = { 0 };
	a.ClassType = '1';
	a.TradingType = '1';
	return ctpApi->ReqQryClassifiedInstrument(&a, requestID);
}

const char* CTraderApi::getApiVersion()
{
	return ctpApi->GetApiVersion();
}