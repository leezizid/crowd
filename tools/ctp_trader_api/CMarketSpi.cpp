#pragma  once
#include "stdafx.h"
#include "CMarketSpi.h"
#include <stdio.h>
#include <string>
#include "Tool.h"
#include "Base64.h"

///���ͻ����뽻�׺�̨������ͨ������ʱ����δ��¼ǰ�����÷��������á�
void CMarketSpi::OnFrontConnected()
{
	Json::Value out;
	out["message"] = "connected";
	jnaResCallback("M_OnFrontConnected", Base64_Encode(out.toStyledString()).c_str());
}

///���ͻ����뽻�׺�̨ͨ�����ӶϿ�ʱ���÷��������á���������������API���Զ��������ӣ��ͻ��˿ɲ�������
///@param nReason ����ԭ��
///        0x1001 �����ʧ��
///        0x1002 ����дʧ��
///        0x2001 ����������ʱ
///        0x2002 ��������ʧ��
///        0x2003 �յ�������
void CMarketSpi::OnFrontDisconnected(int nReason)
{
	Json::Value out;
	out["nReason"] = nReason;
	jnaResCallback("M_OnFrontDisconnected", Base64_Encode(out.toStyledString()).c_str());
}

///������ʱ���档����ʱ��δ�յ�����ʱ���÷��������á�
///@param nTimeLapse �����ϴν��ձ��ĵ�ʱ��
void CMarketSpi::OnHeartBeatWarning(int nTimeLapse)
{
	Json::Value out;
	out["nTimeLapse"] = nTimeLapse;
	jnaResCallback("M_OnHeartBeatWarning", Base64_Encode(out.toStyledString()).c_str());
}


///��¼������Ӧ
void CMarketSpi::OnRspUserLogin(CThostFtdcRspUserLoginField* pRspUserLogin, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pRspUserLogin)
	{
		out["TradingDay"] = pRspUserLogin->TradingDay;
		out["LoginTime"] = pRspUserLogin->LoginTime;
		out["BrokerID"] = pRspUserLogin->BrokerID;
		out["UserID"] = pRspUserLogin->UserID;
		out["SystemName"] = pRspUserLogin->SystemName;
		out["MaxOrderRef"] = pRspUserLogin->MaxOrderRef;
		out["SHFETime"] = pRspUserLogin->SHFETime;
		out["DCETime"] = pRspUserLogin->DCETime;
		out["CZCETime"] = pRspUserLogin->CZCETime;
		out["FFEXTime"] = pRspUserLogin->FFEXTime;
		out["INETime"] = pRspUserLogin->INETime;
		out["FrontID"] = pRspUserLogin->FrontID;
		out["SessionID"] = pRspUserLogin->SessionID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("M_OnRspUserLogin", Base64_Encode(out.toStyledString()).c_str());
}

///�ǳ�������Ӧ
void CMarketSpi::OnRspUserLogout(CThostFtdcUserLogoutField* pUserLogout, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pUserLogout)
	{
		out["BrokerID"] = pUserLogout->BrokerID;
		out["UserID"] = pUserLogout->UserID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("M_OnRspUserLogout", Base64_Encode(out.toStyledString()).c_str());
}

///�����ѯ�鲥��Լ��Ӧ
void CMarketSpi::OnRspQryMulticastInstrument(CThostFtdcMulticastInstrumentField* pMulticastInstrument, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pMulticastInstrument)
	{
		out["TopicID"] = pMulticastInstrument->TopicID;
		out["InstrumentID"] = pMulticastInstrument->InstrumentID;
		out["InstrumentNo"] = pMulticastInstrument->InstrumentNo;
		out["CodePrice"] = pMulticastInstrument->CodePrice;
		out["VolumeMultiple"] = pMulticastInstrument->VolumeMultiple;
		out["PriceTick"] = pMulticastInstrument->PriceTick;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("M_OnRspQryMulticastInstrument", Base64_Encode(out.toStyledString()).c_str());
}

///����Ӧ��
void CMarketSpi::OnRspError(CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("M_OnRspError", Base64_Encode(out.toStyledString()).c_str());
}

///��������Ӧ��
void CMarketSpi::OnRspSubMarketData(CThostFtdcSpecificInstrumentField* pSpecificInstrument, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pSpecificInstrument)
	{
		out["InstrumentID"] = pSpecificInstrument->InstrumentID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("M_OnRspSubMarketData", Base64_Encode(out.toStyledString()).c_str());
}

///ȡ����������Ӧ��
void CMarketSpi::OnRspUnSubMarketData(CThostFtdcSpecificInstrumentField* pSpecificInstrument, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pSpecificInstrument)
	{
		out["InstrumentID"] = pSpecificInstrument->InstrumentID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("M_OnRspUnSubMarketData", Base64_Encode(out.toStyledString()).c_str());
}

///����ѯ��Ӧ��
void CMarketSpi::OnRspSubForQuoteRsp(CThostFtdcSpecificInstrumentField* pSpecificInstrument, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pSpecificInstrument)
	{
		out["InstrumentID"] = pSpecificInstrument->InstrumentID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("M_OnRspSubForQuoteRsp", Base64_Encode(out.toStyledString()).c_str());
}

///ȡ������ѯ��Ӧ��
void CMarketSpi::OnRspUnSubForQuoteRsp(CThostFtdcSpecificInstrumentField* pSpecificInstrument, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pSpecificInstrument)
	{
		out["InstrumentID"] = pSpecificInstrument->InstrumentID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("M_OnRspUnSubForQuoteRsp", Base64_Encode(out.toStyledString()).c_str());
}

///�������֪ͨ
void CMarketSpi::OnRtnDepthMarketData(CThostFtdcDepthMarketDataField* pDepthMarketData)
{
	Json::Value out;
	if (pDepthMarketData)
	{
		out["TradingDay"] = pDepthMarketData->TradingDay;
		out["InstrumentID"] = pDepthMarketData->InstrumentID;
		out["ExchangeID"] = pDepthMarketData->ExchangeID;
		out["ExchangeInstID"] = pDepthMarketData->ExchangeInstID;
		out["LastPrice"] = pDepthMarketData->LastPrice;
		out["PreSettlementPrice"] = pDepthMarketData->PreSettlementPrice;
		out["PreClosePrice"] = pDepthMarketData->PreClosePrice;
		out["PreOpenInterest"] = pDepthMarketData->PreOpenInterest;
		out["OpenPrice"] = pDepthMarketData->OpenPrice;
		out["HighestPrice"] = pDepthMarketData->HighestPrice;
		out["LowestPrice"] = pDepthMarketData->LowestPrice;
		out["Volume"] = pDepthMarketData->Volume;
		out["Turnover"] = pDepthMarketData->Turnover;
		out["OpenInterest"] = pDepthMarketData->OpenInterest;
		out["ClosePrice"] = pDepthMarketData->ClosePrice;
		out["SettlementPrice"] = pDepthMarketData->SettlementPrice;
		out["UpperLimitPrice"] = pDepthMarketData->UpperLimitPrice;
		out["LowerLimitPrice"] = pDepthMarketData->LowerLimitPrice;
		out["PreDelta"] = pDepthMarketData->PreDelta;
		out["CurrDelta"] = pDepthMarketData->CurrDelta;
		out["UpdateTime"] = pDepthMarketData->UpdateTime;
		out["UpdateMillisec"] = pDepthMarketData->UpdateMillisec;
		out["BidPrice1"] = pDepthMarketData->BidPrice1;
		out["BidVolume1"] = pDepthMarketData->BidVolume1;
		out["AskPrice1"] = pDepthMarketData->AskPrice1;
		out["AskVolume1"] = pDepthMarketData->AskVolume1;
		out["BidPrice2"] = pDepthMarketData->BidPrice2;
		out["BidVolume2"] = pDepthMarketData->BidVolume2;
		out["AskPrice2"] = pDepthMarketData->AskPrice2;
		out["AskVolume2"] = pDepthMarketData->AskVolume2;
		out["BidPrice3"] = pDepthMarketData->BidPrice3;
		out["BidVolume3"] = pDepthMarketData->BidVolume3;
		out["AskPrice3"] = pDepthMarketData->AskPrice3;
		out["AskVolume3"] = pDepthMarketData->AskVolume3;
		out["BidPrice4"] = pDepthMarketData->BidPrice4;
		out["BidVolume4"] = pDepthMarketData->BidVolume4;
		out["AskPrice4"] = pDepthMarketData->AskPrice4;
		out["AskVolume4"] = pDepthMarketData->AskVolume4;
		out["BidPrice5"] = pDepthMarketData->BidPrice5;
		out["BidVolume5"] = pDepthMarketData->BidVolume5;
		out["AskPrice5"] = pDepthMarketData->AskPrice5;
		out["AskVolume5"] = pDepthMarketData->AskVolume5;
		out["AveragePrice"] = pDepthMarketData->AveragePrice;
		out["ActionDay"] = pDepthMarketData->ActionDay;
		out["BandingUpperPrice"] = pDepthMarketData->BandingUpperPrice;
		out["BandingLowerPrice"] = pDepthMarketData->BandingLowerPrice;
	}
	jnaResCallback("M_OnRtnDepthMarketData", Base64_Encode(out.toStyledString()).c_str());
}

///ѯ��֪ͨ
void CMarketSpi::OnRtnForQuoteRsp(CThostFtdcForQuoteRspField* pForQuoteRsp)
{
	Json::Value out;
	if (pForQuoteRsp)
	{
		out["TradingDay"] = pForQuoteRsp->TradingDay;
		out["InstrumentID"] = pForQuoteRsp->InstrumentID;
		out["ForQuoteSysID"] = pForQuoteRsp->ForQuoteSysID;
		out["ForQuoteTime"] = pForQuoteRsp->ForQuoteTime;
		out["ActionDay"] = pForQuoteRsp->ActionDay;
		out["ExchangeID"] = pForQuoteRsp->ExchangeID;
	}
	jnaResCallback("M_OnRtnForQuoteRsp", Base64_Encode(out.toStyledString()).c_str());
}


