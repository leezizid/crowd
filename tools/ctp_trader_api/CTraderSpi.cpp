#pragma  once
#include "stdafx.h"
#include "CTraderSpi.h"
#include <stdio.h>
#include <string>
#include "Tool.h"
#include "Base64.h"

void CTraderSpi::OnFrontConnected()
{
	Json::Value out;
	out["message"] = "connected";
	jnaResCallback("T_OnFrontConnected", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnFrontDisconnected(int nReason)
{
	Json::Value out;
	out["nReason"] = nReason;
	jnaResCallback("T_OnFrontDisconnected", Base64_Encode(out.toStyledString()).c_str());
}

void CTraderSpi::OnRspAuthenticate(CThostFtdcRspAuthenticateField* pRspAuthenticateField, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pRspAuthenticateField)
	{
		out["BrokerID"] =  pRspAuthenticateField->BrokerID;
		out["UserID"] = pRspAuthenticateField->UserID;
		out["UserProductInfo"] = pRspAuthenticateField->UserProductInfo;
		out["AppID"] = pRspAuthenticateField->AppID;
		out["AppType"] = pRspAuthenticateField->AppType;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspAuthenticate", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspUserLogin(CThostFtdcRspUserLoginField* pRspUserLogin, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
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
	jnaResCallback("T_OnRspUserLogin", Base64_Encode(out.toStyledString()).c_str());
};


//确认结算单回报
void CTraderSpi::OnRspSettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField* pSettlementInfoConfirm, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pSettlementInfoConfirm)
	{
		out["BrokerID"] = pSettlementInfoConfirm->BrokerID;
		out["InvestorID"] = pSettlementInfoConfirm->InvestorID;
		out["ConfirmDate"] = pSettlementInfoConfirm->ConfirmDate;
		out["ConfirmTime"] = pSettlementInfoConfirm->ConfirmTime;
		out["AccountID"] = pSettlementInfoConfirm->AccountID;
		out["CurrencyID"] = pSettlementInfoConfirm->CurrencyID;
		out["SettlementID"] = pSettlementInfoConfirm->SettlementID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspSettlementInfoConfirm", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryTradingAccount(CThostFtdcTradingAccountField* pTradingAccount, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pTradingAccount)
	{
		out["BrokerID"] = pTradingAccount->BrokerID;
		out["AccountID"] = pTradingAccount->AccountID;
		out["TradingDay"] = pTradingAccount->TradingDay;
		out["CurrencyID"] = pTradingAccount->CurrencyID;
		out["SettlementID"] = pTradingAccount->SettlementID;
		out["BizType"] = pTradingAccount->BizType;
		out["PreMortgage"] = pTradingAccount->PreMortgage;
		out["PreCredit"] = pTradingAccount->PreCredit;
		out["PreDeposit"] = pTradingAccount->PreDeposit;
		out["PreBalance"] = pTradingAccount->PreBalance;
		out["PreMargin"] = pTradingAccount->PreMargin;
		out["InterestBase"] = pTradingAccount->InterestBase;
		out["Interest"] = pTradingAccount->Interest;
		out["Deposit"] = pTradingAccount->Deposit;
		out["Withdraw"] = pTradingAccount->Withdraw;
		out["FrozenMargin"] = pTradingAccount->FrozenMargin;
		out["FrozenCash"] = pTradingAccount->FrozenCash;
		out["FrozenCommission"] = pTradingAccount->FrozenCommission;
		out["CurrMargin"] = pTradingAccount->CurrMargin;
		out["CashIn"] = pTradingAccount->CashIn;
		out["Commission"] = pTradingAccount->Commission;
		out["CloseProfit"] = pTradingAccount->CloseProfit;
		out["PositionProfit"] = pTradingAccount->PositionProfit;
		out["Balance"] = pTradingAccount->Balance;
		out["Available"] = pTradingAccount->Available;
		out["WithdrawQuota"] = pTradingAccount->WithdrawQuota;
		out["Reserve"] = pTradingAccount->Reserve;
		out["Credit"] = pTradingAccount->Credit;
		out["Mortgage"] = pTradingAccount->Mortgage;
		out["ExchangeMargin"] = pTradingAccount->ExchangeMargin;
		out["DeliveryMargin"] = pTradingAccount->DeliveryMargin;
		out["ExchangeDeliveryMargin"] = pTradingAccount->ExchangeDeliveryMargin;
		out["ReserveBalance"] = pTradingAccount->ReserveBalance;
		out["PreFundMortgageIn"] = pTradingAccount->PreFundMortgageIn;
		out["PreFundMortgageOut"] = pTradingAccount->PreFundMortgageOut;
		out["FundMortgageIn"] = pTradingAccount->FundMortgageIn;
		out["FundMortgageOut"] = pTradingAccount->FundMortgageOut;
		out["FundMortgageAvailable"] = pTradingAccount->FundMortgageAvailable;
		out["MortgageableFund"] = pTradingAccount->MortgageableFund;
		out["SpecProductMargin"] = pTradingAccount->SpecProductMargin;
		out["SpecProductFrozenMargin"] = pTradingAccount->SpecProductFrozenMargin;
		out["SpecProductCommission"] = pTradingAccount->SpecProductCommission;
		out["SpecProductFrozenCommission"] = pTradingAccount->SpecProductFrozenCommission;
		out["SpecProductPositionProfit"] = pTradingAccount->SpecProductPositionProfit;
		out["SpecProductCloseProfit"] = pTradingAccount->SpecProductCloseProfit;
		out["SpecProductPositionProfitByAlg"] = pTradingAccount->SpecProductPositionProfitByAlg;
		out["SpecProductExchangeMargin"] = pTradingAccount->SpecProductExchangeMargin;
		out["FrozenSwap"] = pTradingAccount->FrozenSwap;
		out["RemainSwap"] = pTradingAccount->RemainSwap;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryTradingAccount", Base64_Encode(out.toStyledString()).c_str());

};

void CTraderSpi::OnRspOrderInsert(CThostFtdcInputOrderField* pInputOrder, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInputOrder)
	{
		out["BrokerID"] = pInputOrder->BrokerID;
		out["InvestorID"] = pInputOrder->InvestorID;
		out["InstrumentID"] = pInputOrder->InstrumentID;
		out["OrderRef"] = pInputOrder->OrderRef;
		out["UserID"] = pInputOrder->UserID;
		out["CombOffsetFlag"] = pInputOrder->CombOffsetFlag;
		out["CombHedgeFlag"] = pInputOrder->CombHedgeFlag;
		out["GTDDate"] = pInputOrder->GTDDate;
		out["BusinessUnit"] = pInputOrder->BusinessUnit;
		out["ExchangeID"] = pInputOrder->ExchangeID;
		out["InvestUnitID"] = pInputOrder->InvestUnitID;
		out["AccountID"] = pInputOrder->AccountID;
		out["CurrencyID"] = pInputOrder->CurrencyID;
		out["ClientID"] = pInputOrder->ClientID;
		out["IPAddress"] = pInputOrder->IPAddress;
		out["MacAddress"] = pInputOrder->MacAddress;
		out["VolumeTotalOriginal"] = pInputOrder->VolumeTotalOriginal;
		out["MinVolume"] = pInputOrder->MinVolume;
		out["IsAutoSuspend"] = pInputOrder->IsAutoSuspend;
		out["RequestID"] = pInputOrder->RequestID;
		out["UserForceClose"] = pInputOrder->UserForceClose;
		out["IsSwapOrder"] = pInputOrder->IsSwapOrder;
		out["OrderPriceType"] = pInputOrder->OrderPriceType;
		out["Direction"] = pInputOrder->Direction;
		out["TimeCondition"] = pInputOrder->TimeCondition;
		out["VolumeCondition"] = pInputOrder->VolumeCondition;
		out["ContingentCondition"] = pInputOrder->ContingentCondition;
		out["ForceCloseReason"] = pInputOrder->ForceCloseReason;
		out["LimitPrice"] = pInputOrder->LimitPrice;
		out["StopPrice"] = pInputOrder->StopPrice;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspOrderInsert", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryOrder(CThostFtdcOrderField* pOrder, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pOrder)
	{
		out["BrokerID"] = pOrder->BrokerID;
		out["InvestorID"] = pOrder->InvestorID;
		out["InstrumentID"] = pOrder->InstrumentID;
		out["OrderRef"] = pOrder->OrderRef;
		out["UserID"] = pOrder->UserID;
		out["CombOffsetFlag"] = pOrder->CombOffsetFlag;
		out["CombHedgeFlag"] = pOrder->CombHedgeFlag;
		out["GTDDate"] = pOrder->GTDDate;
		out["BusinessUnit"] = pOrder->BusinessUnit;
		out["OrderLocalID"] = pOrder->OrderLocalID;
		out["ExchangeID"] = pOrder->ExchangeID;
		out["ParticipantID"] = pOrder->ParticipantID;
		out["ClientID"] = pOrder->ClientID;
		out["ExchangeInstID"] = pOrder->ExchangeInstID;
		out["TraderID"] = pOrder->TraderID;
		out["TradingDay"] = pOrder->TradingDay;
		out["OrderSysID"] = pOrder->OrderSysID;
		out["InsertDate"] = pOrder->InsertDate;
		out["InsertTime"] = pOrder->InsertTime;
		out["ActiveTime"] = pOrder->ActiveTime;
		out["SuspendTime"] = pOrder->SuspendTime;
		out["UpdateTime"] = pOrder->UpdateTime;
		out["CancelTime"] = pOrder->CancelTime;
		out["ActiveTraderID"] = pOrder->ActiveTraderID;
		out["ClearingPartID"] = pOrder->ClearingPartID;
		out["UserProductInfo"] = pOrder->UserProductInfo;
		out["StatusMsg"] = pOrder->StatusMsg;
		out["ActiveUserID"] = pOrder->ActiveUserID;
		out["RelativeOrderSysID"] = pOrder->RelativeOrderSysID;
		out["BranchID"] = pOrder->BranchID;
		out["InvestUnitID"] = pOrder->InvestUnitID;
		out["AccountID"] = pOrder->AccountID;
		out["CurrencyID"] = pOrder->CurrencyID;
		out["IPAddress"] = pOrder->IPAddress;
		out["MacAddress"] = pOrder->MacAddress;
		out["VolumeTotalOriginal"] = pOrder->VolumeTotalOriginal;
		out["MinVolume"] = pOrder->MinVolume;
		out["IsAutoSuspend"] = pOrder->IsAutoSuspend;
		out["RequestID"] = pOrder->RequestID;
		out["InstallID"] = pOrder->InstallID;
		out["NotifySequence"] = pOrder->NotifySequence;
		out["SettlementID"] = pOrder->SettlementID;
		out["VolumeTraded"] = pOrder->VolumeTraded;
		out["VolumeTotal"] = pOrder->VolumeTotal;
		out["SequenceNo"] = pOrder->SequenceNo;
		out["FrontID"] = pOrder->FrontID;
		out["SessionID"] = pOrder->SessionID;
		out["UserForceClose"] = pOrder->UserForceClose;
		out["BrokerOrderSeq"] = pOrder->BrokerOrderSeq;
		out["ZCETotalTradedVolume"] = pOrder->ZCETotalTradedVolume;
		out["IsSwapOrder"] = pOrder->IsSwapOrder;
		out["OrderPriceType"] = pOrder->OrderPriceType;
		out["Direction"] = pOrder->Direction;
		out["TimeCondition"] = pOrder->TimeCondition;
		out["VolumeCondition"] = pOrder->VolumeCondition;
		out["ContingentCondition"] = pOrder->ContingentCondition;
		out["ForceCloseReason"] = pOrder->ForceCloseReason;
		out["OrderSubmitStatus"] = pOrder->OrderSubmitStatus;
		out["OrderSource"] = pOrder->OrderSource;
		out["OrderStatus"] = pOrder->OrderStatus;
		out["OrderType"] = pOrder->OrderType;
		out["LimitPrice"] = pOrder->LimitPrice;
		out["StopPrice"] = pOrder->StopPrice;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryOrder", Base64_Encode(out.toStyledString()).c_str());
};


void CTraderSpi::OnRspQryInvestorPosition(CThostFtdcInvestorPositionField* pInvestorPosition, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInvestorPosition)
	{
		out["InstrumentID"] = pInvestorPosition->InstrumentID;
		out["BrokerID"] = pInvestorPosition->BrokerID;
		out["InvestorID"] = pInvestorPosition->InvestorID;
		out["TradingDay"] = pInvestorPosition->TradingDay;
		out["ExchangeID"] = pInvestorPosition->ExchangeID;
		out["InvestUnitID"] = pInvestorPosition->InvestUnitID;
		out["YdPosition"] = pInvestorPosition->YdPosition;
		out["Position"] = pInvestorPosition->Position;
		out["LongFrozen"] = pInvestorPosition->LongFrozen;
		out["ShortFrozen"] = pInvestorPosition->ShortFrozen;
		out["OpenVolume"] = pInvestorPosition->OpenVolume;
		out["CloseVolume"] = pInvestorPosition->CloseVolume;
		out["SettlementID"] = pInvestorPosition->SettlementID;
		out["CombPosition"] = pInvestorPosition->CombPosition;
		out["CombLongFrozen"] = pInvestorPosition->CombLongFrozen;
		out["CombShortFrozen"] = pInvestorPosition->CombShortFrozen;
		out["TodayPosition"] = pInvestorPosition->TodayPosition;
		out["StrikeFrozen"] = pInvestorPosition->StrikeFrozen;
		out["AbandonFrozen"] = pInvestorPosition->AbandonFrozen;
		out["YdStrikeFrozen"] = pInvestorPosition->YdStrikeFrozen;
		out["PosiDirection"] = pInvestorPosition->PosiDirection;
		out["HedgeFlag"] = pInvestorPosition->HedgeFlag;
		out["PositionDate"] = pInvestorPosition->PositionDate;
		out["LongFrozenAmount"] = pInvestorPosition->LongFrozenAmount;
		out["ShortFrozenAmount"] = pInvestorPosition->ShortFrozenAmount;
		out["OpenAmount"] = pInvestorPosition->OpenAmount;
		out["CloseAmount"] = pInvestorPosition->CloseAmount;
		out["PositionCost"] = pInvestorPosition->PositionCost;
		out["PreMargin"] = pInvestorPosition->PreMargin;
		out["UseMargin"] = pInvestorPosition->UseMargin;
		out["FrozenMargin"] = pInvestorPosition->FrozenMargin;
		out["FrozenCash"] = pInvestorPosition->FrozenCash;
		out["FrozenCommission"] = pInvestorPosition->FrozenCommission;
		out["CashIn"] = pInvestorPosition->CashIn;
		out["Commission"] = pInvestorPosition->Commission;
		out["CloseProfit"] = pInvestorPosition->CloseProfit;
		out["PositionProfit"] = pInvestorPosition->PositionProfit;
		out["PreSettlementPrice"] = pInvestorPosition->PreSettlementPrice;
		out["SettlementPrice"] = pInvestorPosition->SettlementPrice;
		out["OpenCost"] = pInvestorPosition->OpenCost;
		out["ExchangeMargin"] = pInvestorPosition->ExchangeMargin;
		out["CloseProfitByDate"] = pInvestorPosition->CloseProfitByDate;
		out["CloseProfitByTrade"] = pInvestorPosition->CloseProfitByTrade;
		out["MarginRateByMoney"] = pInvestorPosition->MarginRateByMoney;
		out["MarginRateByVolume"] = pInvestorPosition->MarginRateByVolume;
		out["StrikeFrozenAmount"] = pInvestorPosition->StrikeFrozenAmount;
		out["PositionCostOffset"] = pInvestorPosition->PositionCostOffset;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryInvestorPosition", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnHeartBeatWarning(int nTimeLapse)
{
	Json::Value out;
	out["nTimeLapse"] = nTimeLapse;
	jnaResCallback("T_OnHeartBeatWarning", Base64_Encode(out.toStyledString()).c_str());
}

void CTraderSpi::OnRspUserLogout(CThostFtdcUserLogoutField* pUserLogout, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
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
	jnaResCallback("T_OnRspUserLogout", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspUserPasswordUpdate(CThostFtdcUserPasswordUpdateField* pUserPasswordUpdate, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pUserPasswordUpdate)
	{
		out["BrokerID"] = pUserPasswordUpdate->BrokerID;
		out["UserID"] = pUserPasswordUpdate->UserID;
		out["OldPassword"] = pUserPasswordUpdate->OldPassword;
		out["NewPassword"] = pUserPasswordUpdate->NewPassword;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspUserPasswordUpdate", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspTradingAccountPasswordUpdate(CThostFtdcTradingAccountPasswordUpdateField* pTradingAccountPasswordUpdate, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pTradingAccountPasswordUpdate)
	{
		out["BrokerID"] = pTradingAccountPasswordUpdate->BrokerID;
		out["AccountID"] = pTradingAccountPasswordUpdate->AccountID;
		out["OldPassword"] = pTradingAccountPasswordUpdate->OldPassword;
		out["NewPassword"] = pTradingAccountPasswordUpdate->NewPassword;
		out["CurrencyID"] = pTradingAccountPasswordUpdate->CurrencyID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspTradingAccountPasswordUpdate", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspUserAuthMethod(CThostFtdcRspUserAuthMethodField* pRspUserAuthMethod, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pRspUserAuthMethod)
	{
		out["UsableAuthMethod"] = pRspUserAuthMethod->UsableAuthMethod;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspUserAuthMethod", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspGenUserCaptcha(CThostFtdcRspGenUserCaptchaField* pRspGenUserCaptcha, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pRspGenUserCaptcha)
	{
		out["BrokerID"] = pRspGenUserCaptcha->BrokerID;
		out["UserID"] = pRspGenUserCaptcha->UserID;
		out["CaptchaInfo"] = pRspGenUserCaptcha->CaptchaInfo;
		out["CaptchaInfoLen"] = pRspGenUserCaptcha->CaptchaInfoLen;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspGenUserCaptcha", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspGenUserText(CThostFtdcRspGenUserTextField* pRspGenUserText, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pRspGenUserText)
	{
		out["UserTextSeq"] = pRspGenUserText->UserTextSeq;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspGenUserText", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspParkedOrderInsert(CThostFtdcParkedOrderField* pParkedOrder, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pParkedOrder)
	{
		out["BrokerID"] = pParkedOrder->BrokerID;
		out["InvestorID"] = pParkedOrder->InvestorID;
		out["InstrumentID"] = pParkedOrder->InstrumentID;
		out["OrderRef"] = pParkedOrder->OrderRef;
		out["UserID"] = pParkedOrder->UserID;
		out["CombOffsetFlag"] = pParkedOrder->CombOffsetFlag;
		out["CombHedgeFlag"] = pParkedOrder->CombHedgeFlag;
		out["GTDDate"] = pParkedOrder->GTDDate;
		out["BusinessUnit"] = pParkedOrder->BusinessUnit;
		out["ExchangeID"] = pParkedOrder->ExchangeID;
		out["ParkedOrderID"] = pParkedOrder->ParkedOrderID;
		out["ErrorMsg"] = pParkedOrder->ErrorMsg;
		out["AccountID"] = pParkedOrder->AccountID;
		out["CurrencyID"] = pParkedOrder->CurrencyID;
		out["ClientID"] = pParkedOrder->ClientID;
		out["InvestUnitID"] = pParkedOrder->InvestUnitID;
		out["IPAddress"] = pParkedOrder->IPAddress;
		out["MacAddress"] = pParkedOrder->MacAddress;
		out["VolumeTotalOriginal"] = pParkedOrder->VolumeTotalOriginal;
		out["MinVolume"] = pParkedOrder->MinVolume;
		out["IsAutoSuspend"] = pParkedOrder->IsAutoSuspend;
		out["RequestID"] = pParkedOrder->RequestID;
		out["UserForceClose"] = pParkedOrder->UserForceClose;
		out["ErrorID"] = pParkedOrder->ErrorID;
		out["IsSwapOrder"] = pParkedOrder->IsSwapOrder;
		out["OrderPriceType"] = pParkedOrder->OrderPriceType;
		out["Direction"] = pParkedOrder->Direction;
		out["TimeCondition"] = pParkedOrder->TimeCondition;
		out["VolumeCondition"] = pParkedOrder->VolumeCondition;
		out["ContingentCondition"] = pParkedOrder->ContingentCondition;
		out["ForceCloseReason"] = pParkedOrder->ForceCloseReason;
		out["UserType"] = pParkedOrder->UserType;
		out["Status"] = pParkedOrder->Status;
		out["LimitPrice"] = pParkedOrder->LimitPrice;
		out["StopPrice"] = pParkedOrder->StopPrice;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspParkedOrderInsert", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspParkedOrderAction(CThostFtdcParkedOrderActionField* pParkedOrderAction, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pParkedOrderAction)
	{
		out["BrokerID"] = pParkedOrderAction->BrokerID;
		out["InvestorID"] = pParkedOrderAction->InvestorID;
		out["OrderRef"] = pParkedOrderAction->OrderRef;
		out["ExchangeID"] = pParkedOrderAction->ExchangeID;
		out["OrderSysID"] = pParkedOrderAction->OrderSysID;
		out["UserID"] = pParkedOrderAction->UserID;
		out["InstrumentID"] = pParkedOrderAction->InstrumentID;
		out["ParkedOrderActionID"] = pParkedOrderAction->ParkedOrderActionID;
		out["ErrorMsg"] = pParkedOrderAction->ErrorMsg;
		out["InvestUnitID"] = pParkedOrderAction->InvestUnitID;
		out["IPAddress"] = pParkedOrderAction->IPAddress;
		out["MacAddress"] = pParkedOrderAction->MacAddress;
		out["OrderActionRef"] = pParkedOrderAction->OrderActionRef;
		out["RequestID"] = pParkedOrderAction->RequestID;
		out["FrontID"] = pParkedOrderAction->FrontID;
		out["SessionID"] = pParkedOrderAction->SessionID;
		out["VolumeChange"] = pParkedOrderAction->VolumeChange;
		out["ErrorID"] = pParkedOrderAction->ErrorID;
		out["ActionFlag"] = pParkedOrderAction->ActionFlag;
		out["UserType"] = pParkedOrderAction->UserType;
		out["Status"] = pParkedOrderAction->Status;
		out["LimitPrice"] = pParkedOrderAction->LimitPrice;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspParkedOrderAction", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspOrderAction(CThostFtdcInputOrderActionField* pInputOrderAction, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInputOrderAction)
	{
		out["BrokerID"] = pInputOrderAction->BrokerID;
		out["InvestorID"] = pInputOrderAction->InvestorID;
		out["OrderRef"] = pInputOrderAction->OrderRef;
		out["ExchangeID"] = pInputOrderAction->ExchangeID;
		out["OrderSysID"] = pInputOrderAction->OrderSysID;
		out["UserID"] = pInputOrderAction->UserID;
		out["InstrumentID"] = pInputOrderAction->InstrumentID;
		out["InvestUnitID"] = pInputOrderAction->InvestUnitID;
		out["IPAddress"] = pInputOrderAction->IPAddress;
		out["MacAddress"] = pInputOrderAction->MacAddress;
		out["OrderActionRef"] = pInputOrderAction->OrderActionRef;
		out["RequestID"] = pInputOrderAction->RequestID;
		out["FrontID"] = pInputOrderAction->FrontID;
		out["SessionID"] = pInputOrderAction->SessionID;
		out["VolumeChange"] = pInputOrderAction->VolumeChange;
		out["ActionFlag"] = pInputOrderAction->ActionFlag;
		out["LimitPrice"] = pInputOrderAction->LimitPrice;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspOrderAction", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspRemoveParkedOrder(CThostFtdcRemoveParkedOrderField* pRemoveParkedOrder, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pRemoveParkedOrder)
	{
		out["BrokerID"] = pRemoveParkedOrder->BrokerID;
		out["InvestorID"] = pRemoveParkedOrder->InvestorID;
		out["ParkedOrderID"] = pRemoveParkedOrder->ParkedOrderID;
		out["InvestUnitID"] = pRemoveParkedOrder->InvestUnitID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspRemoveParkedOrder", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspRemoveParkedOrderAction(CThostFtdcRemoveParkedOrderActionField* pRemoveParkedOrderAction, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pRemoveParkedOrderAction)
	{
		out["BrokerID"] = pRemoveParkedOrderAction->BrokerID;
		out["InvestorID"] = pRemoveParkedOrderAction->InvestorID;
		out["ParkedOrderActionID"] = pRemoveParkedOrderAction->ParkedOrderActionID;
		out["InvestUnitID"] = pRemoveParkedOrderAction->InvestUnitID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspRemoveParkedOrderAction", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspExecOrderInsert(CThostFtdcInputExecOrderField* pInputExecOrder, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInputExecOrder)
	{
		out["BrokerID"] = pInputExecOrder->BrokerID;
		out["InvestorID"] = pInputExecOrder->InvestorID;
		out["InstrumentID"] = pInputExecOrder->InstrumentID;
		out["ExecOrderRef"] = pInputExecOrder->ExecOrderRef;
		out["UserID"] = pInputExecOrder->UserID;
		out["BusinessUnit"] = pInputExecOrder->BusinessUnit;
		out["ExchangeID"] = pInputExecOrder->ExchangeID;
		out["InvestUnitID"] = pInputExecOrder->InvestUnitID;
		out["AccountID"] = pInputExecOrder->AccountID;
		out["CurrencyID"] = pInputExecOrder->CurrencyID;
		out["ClientID"] = pInputExecOrder->ClientID;
		out["IPAddress"] = pInputExecOrder->IPAddress;
		out["MacAddress"] = pInputExecOrder->MacAddress;
		out["Volume"] = pInputExecOrder->Volume;
		out["RequestID"] = pInputExecOrder->RequestID;
		out["OffsetFlag"] = pInputExecOrder->OffsetFlag;
		out["HedgeFlag"] = pInputExecOrder->HedgeFlag;
		out["ActionType"] = pInputExecOrder->ActionType;
		out["PosiDirection"] = pInputExecOrder->PosiDirection;
		out["ReservePositionFlag"] = pInputExecOrder->ReservePositionFlag;
		out["CloseFlag"] = pInputExecOrder->CloseFlag;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspExecOrderInsert", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspExecOrderAction(CThostFtdcInputExecOrderActionField* pInputExecOrderAction, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInputExecOrderAction)
	{
		out["BrokerID"] = pInputExecOrderAction->BrokerID;
		out["InvestorID"] = pInputExecOrderAction->InvestorID;
		out["ExecOrderRef"] = pInputExecOrderAction->ExecOrderRef;
		out["ExchangeID"] = pInputExecOrderAction->ExchangeID;
		out["ExecOrderSysID"] = pInputExecOrderAction->ExecOrderSysID;
		out["UserID"] = pInputExecOrderAction->UserID;
		out["InstrumentID"] = pInputExecOrderAction->InstrumentID;
		out["InvestUnitID"] = pInputExecOrderAction->InvestUnitID;
		out["IPAddress"] = pInputExecOrderAction->IPAddress;
		out["MacAddress"] = pInputExecOrderAction->MacAddress;
		out["ExecOrderActionRef"] = pInputExecOrderAction->ExecOrderActionRef;
		out["RequestID"] = pInputExecOrderAction->RequestID;
		out["FrontID"] = pInputExecOrderAction->FrontID;
		out["SessionID"] = pInputExecOrderAction->SessionID;
		out["ActionFlag"] = pInputExecOrderAction->ActionFlag;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspExecOrderAction", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspForQuoteInsert(CThostFtdcInputForQuoteField* pInputForQuote, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInputForQuote)
	{
		out["BrokerID"] = pInputForQuote->BrokerID;
		out["InvestorID"] = pInputForQuote->InvestorID;
		out["InstrumentID"] = pInputForQuote->InstrumentID;
		out["ForQuoteRef"] = pInputForQuote->ForQuoteRef;
		out["UserID"] = pInputForQuote->UserID;
		out["ExchangeID"] = pInputForQuote->ExchangeID;
		out["InvestUnitID"] = pInputForQuote->InvestUnitID;
		out["IPAddress"] = pInputForQuote->IPAddress;
		out["MacAddress"] = pInputForQuote->MacAddress;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspForQuoteInsert", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQuoteInsert(CThostFtdcInputQuoteField* pInputQuote, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInputQuote)
	{
		out["BrokerID"] = pInputQuote->BrokerID;
		out["InvestorID"] = pInputQuote->InvestorID;
		out["InstrumentID"] = pInputQuote->InstrumentID;
		out["QuoteRef"] = pInputQuote->QuoteRef;
		out["UserID"] = pInputQuote->UserID;
		out["BusinessUnit"] = pInputQuote->BusinessUnit;
		out["AskOrderRef"] = pInputQuote->AskOrderRef;
		out["BidOrderRef"] = pInputQuote->BidOrderRef;
		out["ForQuoteSysID"] = pInputQuote->ForQuoteSysID;
		out["ExchangeID"] = pInputQuote->ExchangeID;
		out["InvestUnitID"] = pInputQuote->InvestUnitID;
		out["ClientID"] = pInputQuote->ClientID;
		out["IPAddress"] = pInputQuote->IPAddress;
		out["MacAddress"] = pInputQuote->MacAddress;
		out["AskVolume"] = pInputQuote->AskVolume;
		out["BidVolume"] = pInputQuote->BidVolume;
		out["RequestID"] = pInputQuote->RequestID;
		out["AskOffsetFlag"] = pInputQuote->AskOffsetFlag;
		out["BidOffsetFlag"] = pInputQuote->BidOffsetFlag;
		out["AskHedgeFlag"] = pInputQuote->AskHedgeFlag;
		out["BidHedgeFlag"] = pInputQuote->BidHedgeFlag;
		out["AskPrice"] = pInputQuote->AskPrice;
		out["BidPrice"] = pInputQuote->BidPrice;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQuoteInsert", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQuoteAction(CThostFtdcInputQuoteActionField* pInputQuoteAction, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInputQuoteAction)
	{
		out["BrokerID"] = pInputQuoteAction->BrokerID;
		out["InvestorID"] = pInputQuoteAction->InvestorID;
		out["QuoteRef"] = pInputQuoteAction->QuoteRef;
		out["ExchangeID"] = pInputQuoteAction->ExchangeID;
		out["QuoteSysID"] = pInputQuoteAction->QuoteSysID;
		out["UserID"] = pInputQuoteAction->UserID;
		out["InstrumentID"] = pInputQuoteAction->InstrumentID;
		out["InvestUnitID"] = pInputQuoteAction->InvestUnitID;
		out["ClientID"] = pInputQuoteAction->ClientID;
		out["IPAddress"] = pInputQuoteAction->IPAddress;
		out["MacAddress"] = pInputQuoteAction->MacAddress;
		out["QuoteActionRef"] = pInputQuoteAction->QuoteActionRef;
		out["RequestID"] = pInputQuoteAction->RequestID;
		out["FrontID"] = pInputQuoteAction->FrontID;
		out["SessionID"] = pInputQuoteAction->SessionID;
		out["ActionFlag"] = pInputQuoteAction->ActionFlag;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQuoteAction", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspBatchOrderAction(CThostFtdcInputBatchOrderActionField* pInputBatchOrderAction, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInputBatchOrderAction)
	{
		out["BrokerID"] = pInputBatchOrderAction->BrokerID;
		out["InvestorID"] = pInputBatchOrderAction->InvestorID;
		out["ExchangeID"] = pInputBatchOrderAction->ExchangeID;
		out["UserID"] = pInputBatchOrderAction->UserID;
		out["InvestUnitID"] = pInputBatchOrderAction->InvestUnitID;
		out["IPAddress"] = pInputBatchOrderAction->IPAddress;
		out["MacAddress"] = pInputBatchOrderAction->MacAddress;
		out["OrderActionRef"] = pInputBatchOrderAction->OrderActionRef;
		out["RequestID"] = pInputBatchOrderAction->RequestID;
		out["FrontID"] = pInputBatchOrderAction->FrontID;
		out["SessionID"] = pInputBatchOrderAction->SessionID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspBatchOrderAction", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspOptionSelfCloseInsert(CThostFtdcInputOptionSelfCloseField* pInputOptionSelfClose, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInputOptionSelfClose)
	{
		out["BrokerID"] = pInputOptionSelfClose->BrokerID;
		out["InvestorID"] = pInputOptionSelfClose->InvestorID;
		out["InstrumentID"] = pInputOptionSelfClose->InstrumentID;
		out["OptionSelfCloseRef"] = pInputOptionSelfClose->OptionSelfCloseRef;
		out["UserID"] = pInputOptionSelfClose->UserID;
		out["BusinessUnit"] = pInputOptionSelfClose->BusinessUnit;
		out["ExchangeID"] = pInputOptionSelfClose->ExchangeID;
		out["InvestUnitID"] = pInputOptionSelfClose->InvestUnitID;
		out["AccountID"] = pInputOptionSelfClose->AccountID;
		out["CurrencyID"] = pInputOptionSelfClose->CurrencyID;
		out["ClientID"] = pInputOptionSelfClose->ClientID;
		out["IPAddress"] = pInputOptionSelfClose->IPAddress;
		out["MacAddress"] = pInputOptionSelfClose->MacAddress;
		out["Volume"] = pInputOptionSelfClose->Volume;
		out["RequestID"] = pInputOptionSelfClose->RequestID;
		out["HedgeFlag"] = pInputOptionSelfClose->HedgeFlag;
		out["OptSelfCloseFlag"] = pInputOptionSelfClose->OptSelfCloseFlag;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspOptionSelfCloseInsert", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspOptionSelfCloseAction(CThostFtdcInputOptionSelfCloseActionField* pInputOptionSelfCloseAction, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInputOptionSelfCloseAction)
	{
		out["BrokerID"] = pInputOptionSelfCloseAction->BrokerID;
		out["InvestorID"] = pInputOptionSelfCloseAction->InvestorID;
		out["OptionSelfCloseRef"] = pInputOptionSelfCloseAction->OptionSelfCloseRef;
		out["ExchangeID"] = pInputOptionSelfCloseAction->ExchangeID;
		out["OptionSelfCloseSysID"] = pInputOptionSelfCloseAction->OptionSelfCloseSysID;
		out["UserID"] = pInputOptionSelfCloseAction->UserID;
		out["InstrumentID"] = pInputOptionSelfCloseAction->InstrumentID;
		out["InvestUnitID"] = pInputOptionSelfCloseAction->InvestUnitID;
		out["IPAddress"] = pInputOptionSelfCloseAction->IPAddress;
		out["MacAddress"] = pInputOptionSelfCloseAction->MacAddress;
		out["OptionSelfCloseActionRef"] = pInputOptionSelfCloseAction->OptionSelfCloseActionRef;
		out["RequestID"] = pInputOptionSelfCloseAction->RequestID;
		out["FrontID"] = pInputOptionSelfCloseAction->FrontID;
		out["SessionID"] = pInputOptionSelfCloseAction->SessionID;
		out["ActionFlag"] = pInputOptionSelfCloseAction->ActionFlag;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspOptionSelfCloseAction", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspCombActionInsert(CThostFtdcInputCombActionField* pInputCombAction, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInputCombAction)
	{
		out["BrokerID"] = pInputCombAction->BrokerID;
		out["InvestorID"] = pInputCombAction->InvestorID;
		out["InstrumentID"] = pInputCombAction->InstrumentID;
		out["CombActionRef"] = pInputCombAction->CombActionRef;
		out["UserID"] = pInputCombAction->UserID;
		out["ExchangeID"] = pInputCombAction->ExchangeID;
		out["IPAddress"] = pInputCombAction->IPAddress;
		out["MacAddress"] = pInputCombAction->MacAddress;
		out["InvestUnitID"] = pInputCombAction->InvestUnitID;
		out["Volume"] = pInputCombAction->Volume;
		out["Direction"] = pInputCombAction->Direction;
		out["CombDirection"] = pInputCombAction->CombDirection;
		out["HedgeFlag"] = pInputCombAction->HedgeFlag;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspCombActionInsert", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryTrade(CThostFtdcTradeField* pTrade, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pTrade)
	{
		out["BrokerID"] = pTrade->BrokerID;
		out["InvestorID"] = pTrade->InvestorID;
		out["InstrumentID"] = pTrade->InstrumentID;
		out["OrderRef"] = pTrade->OrderRef;
		out["UserID"] = pTrade->UserID;
		out["ExchangeID"] = pTrade->ExchangeID;
		out["TradeID"] = pTrade->TradeID;
		out["OrderSysID"] = pTrade->OrderSysID;
		out["ParticipantID"] = pTrade->ParticipantID;
		out["ClientID"] = pTrade->ClientID;
		out["ExchangeInstID"] = pTrade->ExchangeInstID;
		out["TradeDate"] = pTrade->TradeDate;
		out["TradeTime"] = pTrade->TradeTime;
		out["TraderID"] = pTrade->TraderID;
		out["OrderLocalID"] = pTrade->OrderLocalID;
		out["ClearingPartID"] = pTrade->ClearingPartID;
		out["BusinessUnit"] = pTrade->BusinessUnit;
		out["TradingDay"] = pTrade->TradingDay;
		out["InvestUnitID"] = pTrade->InvestUnitID;
		out["Volume"] = pTrade->Volume;
		out["SequenceNo"] = pTrade->SequenceNo;
		out["SettlementID"] = pTrade->SettlementID;
		out["BrokerOrderSeq"] = pTrade->BrokerOrderSeq;
		out["Direction"] = pTrade->Direction;
		out["TradingRole"] = pTrade->TradingRole;
		out["OffsetFlag"] = pTrade->OffsetFlag;
		out["HedgeFlag"] = pTrade->HedgeFlag;
		out["TradeType"] = pTrade->TradeType;
		out["PriceSource"] = pTrade->PriceSource;
		out["TradeSource"] = pTrade->TradeSource;
		out["Price"] = pTrade->Price;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryTrade", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryInvestor(CThostFtdcInvestorField* pInvestor, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInvestor)
	{
		out["InvestorID"] = pInvestor->InvestorID;
		out["BrokerID"] = pInvestor->BrokerID;
		out["InvestorGroupID"] = pInvestor->InvestorGroupID;
		out["InvestorName"] = pInvestor->InvestorName;
		out["IdentifiedCardNo"] = pInvestor->IdentifiedCardNo;
		out["Telephone"] = pInvestor->Telephone;
		out["Address"] = pInvestor->Address;
		out["OpenDate"] = pInvestor->OpenDate;
		out["Mobile"] = pInvestor->Mobile;
		out["CommModelID"] = pInvestor->CommModelID;
		out["MarginModelID"] = pInvestor->MarginModelID;
		out["IsActive"] = pInvestor->IsActive;
		out["IdentifiedCardType"] = pInvestor->IdentifiedCardType;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryInvestor", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryTradingCode(CThostFtdcTradingCodeField* pTradingCode, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pTradingCode)
	{
		out["InvestorID"] = pTradingCode->InvestorID;
		out["BrokerID"] = pTradingCode->BrokerID;
		out["ExchangeID"] = pTradingCode->ExchangeID;
		out["ClientID"] = pTradingCode->ClientID;
		out["BranchID"] = pTradingCode->BranchID;
		out["InvestUnitID"] = pTradingCode->InvestUnitID;
		out["IsActive"] = pTradingCode->IsActive;
		out["ClientIDType"] = pTradingCode->ClientIDType;
		out["BizType"] = pTradingCode->BizType;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryTradingCode", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryInstrumentMarginRate(CThostFtdcInstrumentMarginRateField* pInstrumentMarginRate, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInstrumentMarginRate)
	{
		out["InstrumentID"] = pInstrumentMarginRate->InstrumentID;
		out["BrokerID"] = pInstrumentMarginRate->BrokerID;
		out["InvestorID"] = pInstrumentMarginRate->InvestorID;
		out["ExchangeID"] = pInstrumentMarginRate->ExchangeID;
		out["InvestUnitID"] = pInstrumentMarginRate->InvestUnitID;
		out["IsRelative"] = pInstrumentMarginRate->IsRelative;
		out["InvestorRange"] = pInstrumentMarginRate->InvestorRange;
		out["HedgeFlag"] = pInstrumentMarginRate->HedgeFlag;
		out["LongMarginRatioByMoney"] = pInstrumentMarginRate->LongMarginRatioByMoney;
		out["LongMarginRatioByVolume"] = pInstrumentMarginRate->LongMarginRatioByVolume;
		out["ShortMarginRatioByMoney"] = pInstrumentMarginRate->ShortMarginRatioByMoney;
		out["ShortMarginRatioByVolume"] = pInstrumentMarginRate->ShortMarginRatioByVolume;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryInstrumentMarginRate", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryInstrumentCommissionRate(CThostFtdcInstrumentCommissionRateField* pInstrumentCommissionRate, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInstrumentCommissionRate)
	{
		out["InstrumentID"] = pInstrumentCommissionRate->InstrumentID;
		out["BrokerID"] = pInstrumentCommissionRate->BrokerID;
		out["InvestorID"] = pInstrumentCommissionRate->InvestorID;
		out["ExchangeID"] = pInstrumentCommissionRate->ExchangeID;
		out["InvestUnitID"] = pInstrumentCommissionRate->InvestUnitID;
		out["InvestorRange"] = pInstrumentCommissionRate->InvestorRange;
		out["BizType"] = pInstrumentCommissionRate->BizType;
		out["OpenRatioByMoney"] = pInstrumentCommissionRate->OpenRatioByMoney;
		out["OpenRatioByVolume"] = pInstrumentCommissionRate->OpenRatioByVolume;
		out["CloseRatioByMoney"] = pInstrumentCommissionRate->CloseRatioByMoney;
		out["CloseRatioByVolume"] = pInstrumentCommissionRate->CloseRatioByVolume;
		out["CloseTodayRatioByMoney"] = pInstrumentCommissionRate->CloseTodayRatioByMoney;
		out["CloseTodayRatioByVolume"] = pInstrumentCommissionRate->CloseTodayRatioByVolume;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryInstrumentCommissionRate", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryExchange(CThostFtdcExchangeField* pExchange, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pExchange)
	{
		out["ExchangeID"] = pExchange->ExchangeID;
		out["ExchangeName"] = pExchange->ExchangeName;
		out["ExchangeProperty"] = pExchange->ExchangeProperty;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryExchange", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryProduct(CThostFtdcProductField* pProduct, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pProduct)
	{
		out["ProductID"] = pProduct->ProductID;
		out["ProductName"] = pProduct->ProductName;
		out["ExchangeID"] = pProduct->ExchangeID;
		out["TradeCurrencyID"] = pProduct->TradeCurrencyID;
		out["ExchangeProductID"] = pProduct->ExchangeProductID;
		out["VolumeMultiple"] = pProduct->VolumeMultiple;
		out["MaxMarketOrderVolume"] = pProduct->MaxMarketOrderVolume;
		out["MinMarketOrderVolume"] = pProduct->MinMarketOrderVolume;
		out["MaxLimitOrderVolume"] = pProduct->MaxLimitOrderVolume;
		out["MinLimitOrderVolume"] = pProduct->MinLimitOrderVolume;
		out["ProductClass"] = pProduct->ProductClass;
		out["PositionType"] = pProduct->PositionType;
		out["PositionDateType"] = pProduct->PositionDateType;
		out["CloseDealType"] = pProduct->CloseDealType;
		out["MortgageFundUseRange"] = pProduct->MortgageFundUseRange;
		out["PriceTick"] = pProduct->PriceTick;
		out["UnderlyingMultiple"] = pProduct->UnderlyingMultiple;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryProduct", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryInstrument(CThostFtdcInstrumentField* pInstrument, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInstrument)
	{
		out["InstrumentID"] = pInstrument->InstrumentID;
		out["ExchangeID"] = pInstrument->ExchangeID;
		out["InstrumentName"] = pInstrument->InstrumentName;
		out["ExchangeInstID"] = pInstrument->ExchangeInstID;
		out["ProductID"] = pInstrument->ProductID;
		out["CreateDate"] = pInstrument->CreateDate;
		out["OpenDate"] = pInstrument->OpenDate;
		out["ExpireDate"] = pInstrument->ExpireDate;
		out["StartDelivDate"] = pInstrument->StartDelivDate;
		out["EndDelivDate"] = pInstrument->EndDelivDate;
		out["UnderlyingInstrID"] = pInstrument->UnderlyingInstrID;
		out["DeliveryYear"] = pInstrument->DeliveryYear;
		out["DeliveryMonth"] = pInstrument->DeliveryMonth;
		out["MaxMarketOrderVolume"] = pInstrument->MaxMarketOrderVolume;
		out["MinMarketOrderVolume"] = pInstrument->MinMarketOrderVolume;
		out["MaxLimitOrderVolume"] = pInstrument->MaxLimitOrderVolume;
		out["MinLimitOrderVolume"] = pInstrument->MinLimitOrderVolume;
		out["VolumeMultiple"] = pInstrument->VolumeMultiple;
		out["IsTrading"] = pInstrument->IsTrading;
		out["ProductClass"] = pInstrument->ProductClass;
		out["InstLifePhase"] = pInstrument->InstLifePhase;
		out["PositionType"] = pInstrument->PositionType;
		out["PositionDateType"] = pInstrument->PositionDateType;
		out["MaxMarginSideAlgorithm"] = pInstrument->MaxMarginSideAlgorithm;
		out["OptionsType"] = pInstrument->OptionsType;
		out["CombinationType"] = pInstrument->CombinationType;
		out["PriceTick"] = pInstrument->PriceTick;
		out["LongMarginRatio"] = pInstrument->LongMarginRatio;
		out["ShortMarginRatio"] = pInstrument->ShortMarginRatio;
		out["StrikePrice"] = pInstrument->StrikePrice;
		out["UnderlyingMultiple"] = pInstrument->UnderlyingMultiple;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryInstrument", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryDepthMarketData(CThostFtdcDepthMarketDataField* pDepthMarketData, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pDepthMarketData)
	{
		out["TradingDay"] = pDepthMarketData->TradingDay;
		out["InstrumentID"] = pDepthMarketData->InstrumentID;
		out["ExchangeID"] = pDepthMarketData->ExchangeID;
		out["ExchangeInstID"] = pDepthMarketData->ExchangeInstID;
		out["UpdateTime"] = pDepthMarketData->UpdateTime;
		out["ActionDay"] = pDepthMarketData->ActionDay;
		out["Volume"] = pDepthMarketData->Volume;
		out["UpdateMillisec"] = pDepthMarketData->UpdateMillisec;
		out["BidVolume1"] = pDepthMarketData->BidVolume1;
		out["AskVolume1"] = pDepthMarketData->AskVolume1;
		out["BidVolume2"] = pDepthMarketData->BidVolume2;
		out["AskVolume2"] = pDepthMarketData->AskVolume2;
		out["BidVolume3"] = pDepthMarketData->BidVolume3;
		out["AskVolume3"] = pDepthMarketData->AskVolume3;
		out["BidVolume4"] = pDepthMarketData->BidVolume4;
		out["AskVolume4"] = pDepthMarketData->AskVolume4;
		out["BidVolume5"] = pDepthMarketData->BidVolume5;
		out["AskVolume5"] = pDepthMarketData->AskVolume5;
		out["LastPrice"] = pDepthMarketData->LastPrice;
		out["PreSettlementPrice"] = pDepthMarketData->PreSettlementPrice;
		out["PreClosePrice"] = pDepthMarketData->PreClosePrice;
		out["PreOpenInterest"] = pDepthMarketData->PreOpenInterest;
		out["OpenPrice"] = pDepthMarketData->OpenPrice;
		out["HighestPrice"] = pDepthMarketData->HighestPrice;
		out["LowestPrice"] = pDepthMarketData->LowestPrice;
		out["Turnover"] = pDepthMarketData->Turnover;
		out["OpenInterest"] = pDepthMarketData->OpenInterest;
		out["ClosePrice"] = pDepthMarketData->ClosePrice;
		out["SettlementPrice"] = pDepthMarketData->SettlementPrice;
		out["UpperLimitPrice"] = pDepthMarketData->UpperLimitPrice;
		out["LowerLimitPrice"] = pDepthMarketData->LowerLimitPrice;
		out["PreDelta"] = pDepthMarketData->PreDelta;
		out["CurrDelta"] = pDepthMarketData->CurrDelta;
		out["BidPrice1"] = pDepthMarketData->BidPrice1;
		out["AskPrice1"] = pDepthMarketData->AskPrice1;
		out["BidPrice2"] = pDepthMarketData->BidPrice2;
		out["AskPrice2"] = pDepthMarketData->AskPrice2;
		out["BidPrice3"] = pDepthMarketData->BidPrice3;
		out["AskPrice3"] = pDepthMarketData->AskPrice3;
		out["BidPrice4"] = pDepthMarketData->BidPrice4;
		out["AskPrice4"] = pDepthMarketData->AskPrice4;
		out["BidPrice5"] = pDepthMarketData->BidPrice5;
		out["AskPrice5"] = pDepthMarketData->AskPrice5;
		out["AveragePrice"] = pDepthMarketData->AveragePrice;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryDepthMarketData", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQrySettlementInfo(CThostFtdcSettlementInfoField* pSettlementInfo, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pSettlementInfo)
	{
		out["TradingDay"] = pSettlementInfo->TradingDay;
		out["BrokerID"] = pSettlementInfo->BrokerID;
		out["InvestorID"] = pSettlementInfo->InvestorID;
		out["Content"] = pSettlementInfo->Content;
		out["AccountID"] = pSettlementInfo->AccountID;
		out["CurrencyID"] = pSettlementInfo->CurrencyID;
		out["SettlementID"] = pSettlementInfo->SettlementID;
		out["SequenceNo"] = pSettlementInfo->SequenceNo;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQrySettlementInfo", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryTransferBank(CThostFtdcTransferBankField* pTransferBank, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pTransferBank)
	{
		out["BankID"] = pTransferBank->BankID;
		out["BankBrchID"] = pTransferBank->BankBrchID;
		out["BankName"] = pTransferBank->BankName;
		out["IsActive"] = pTransferBank->IsActive;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryTransferBank", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryInvestorPositionDetail(CThostFtdcInvestorPositionDetailField* pInvestorPositionDetail, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInvestorPositionDetail)
	{
		out["InstrumentID"] = pInvestorPositionDetail->InstrumentID;
		out["BrokerID"] = pInvestorPositionDetail->BrokerID;
		out["InvestorID"] = pInvestorPositionDetail->InvestorID;
		out["OpenDate"] = pInvestorPositionDetail->OpenDate;
		out["TradeID"] = pInvestorPositionDetail->TradeID;
		out["TradingDay"] = pInvestorPositionDetail->TradingDay;
		out["CombInstrumentID"] = pInvestorPositionDetail->CombInstrumentID;
		out["ExchangeID"] = pInvestorPositionDetail->ExchangeID;
		out["InvestUnitID"] = pInvestorPositionDetail->InvestUnitID;
		out["Volume"] = pInvestorPositionDetail->Volume;
		out["SettlementID"] = pInvestorPositionDetail->SettlementID;
		out["CloseVolume"] = pInvestorPositionDetail->CloseVolume;
		out["TimeFirstVolume"] = pInvestorPositionDetail->TimeFirstVolume;
		out["HedgeFlag"] = pInvestorPositionDetail->HedgeFlag;
		out["Direction"] = pInvestorPositionDetail->Direction;
		out["TradeType"] = pInvestorPositionDetail->TradeType;
		out["OpenPrice"] = pInvestorPositionDetail->OpenPrice;
		out["CloseProfitByDate"] = pInvestorPositionDetail->CloseProfitByDate;
		out["CloseProfitByTrade"] = pInvestorPositionDetail->CloseProfitByTrade;
		out["PositionProfitByDate"] = pInvestorPositionDetail->PositionProfitByDate;
		out["PositionProfitByTrade"] = pInvestorPositionDetail->PositionProfitByTrade;
		out["Margin"] = pInvestorPositionDetail->Margin;
		out["ExchMargin"] = pInvestorPositionDetail->ExchMargin;
		out["MarginRateByMoney"] = pInvestorPositionDetail->MarginRateByMoney;
		out["MarginRateByVolume"] = pInvestorPositionDetail->MarginRateByVolume;
		out["LastSettlementPrice"] = pInvestorPositionDetail->LastSettlementPrice;
		out["SettlementPrice"] = pInvestorPositionDetail->SettlementPrice;
		out["CloseAmount"] = pInvestorPositionDetail->CloseAmount;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryInvestorPositionDetail", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryNotice(CThostFtdcNoticeField* pNotice, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pNotice)
	{
		out["BrokerID"] = pNotice->BrokerID;
		out["Content"] = pNotice->Content;
		out["SequenceLabel"] = pNotice->SequenceLabel;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryNotice", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQrySettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField* pSettlementInfoConfirm, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pSettlementInfoConfirm)
	{
		out["BrokerID"] = pSettlementInfoConfirm->BrokerID;
		out["InvestorID"] = pSettlementInfoConfirm->InvestorID;
		out["ConfirmDate"] = pSettlementInfoConfirm->ConfirmDate;
		out["ConfirmTime"] = pSettlementInfoConfirm->ConfirmTime;
		out["AccountID"] = pSettlementInfoConfirm->AccountID;
		out["CurrencyID"] = pSettlementInfoConfirm->CurrencyID;
		out["SettlementID"] = pSettlementInfoConfirm->SettlementID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQrySettlementInfoConfirm", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryInvestorPositionCombineDetail(CThostFtdcInvestorPositionCombineDetailField* pInvestorPositionCombineDetail, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInvestorPositionCombineDetail)
	{
		out["TradingDay"] = pInvestorPositionCombineDetail->TradingDay;
		out["OpenDate"] = pInvestorPositionCombineDetail->OpenDate;
		out["ExchangeID"] = pInvestorPositionCombineDetail->ExchangeID;
		out["BrokerID"] = pInvestorPositionCombineDetail->BrokerID;
		out["InvestorID"] = pInvestorPositionCombineDetail->InvestorID;
		out["ComTradeID"] = pInvestorPositionCombineDetail->ComTradeID;
		out["TradeID"] = pInvestorPositionCombineDetail->TradeID;
		out["InstrumentID"] = pInvestorPositionCombineDetail->InstrumentID;
		out["CombInstrumentID"] = pInvestorPositionCombineDetail->CombInstrumentID;
		out["InvestUnitID"] = pInvestorPositionCombineDetail->InvestUnitID;
		out["SettlementID"] = pInvestorPositionCombineDetail->SettlementID;
		out["TotalAmt"] = pInvestorPositionCombineDetail->TotalAmt;
		out["LegID"] = pInvestorPositionCombineDetail->LegID;
		out["LegMultiple"] = pInvestorPositionCombineDetail->LegMultiple;
		out["TradeGroupID"] = pInvestorPositionCombineDetail->TradeGroupID;
		out["HedgeFlag"] = pInvestorPositionCombineDetail->HedgeFlag;
		out["Direction"] = pInvestorPositionCombineDetail->Direction;
		out["Margin"] = pInvestorPositionCombineDetail->Margin;
		out["ExchMargin"] = pInvestorPositionCombineDetail->ExchMargin;
		out["MarginRateByMoney"] = pInvestorPositionCombineDetail->MarginRateByMoney;
		out["MarginRateByVolume"] = pInvestorPositionCombineDetail->MarginRateByVolume;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryInvestorPositionCombineDetail", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryCFMMCTradingAccountKey(CThostFtdcCFMMCTradingAccountKeyField* pCFMMCTradingAccountKey, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pCFMMCTradingAccountKey)
	{
		out["BrokerID"] = pCFMMCTradingAccountKey->BrokerID;
		out["ParticipantID"] = pCFMMCTradingAccountKey->ParticipantID;
		out["AccountID"] = pCFMMCTradingAccountKey->AccountID;
		out["CurrentKey"] = pCFMMCTradingAccountKey->CurrentKey;
		out["KeyID"] = pCFMMCTradingAccountKey->KeyID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryCFMMCTradingAccountKey", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryEWarrantOffset(CThostFtdcEWarrantOffsetField* pEWarrantOffset, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pEWarrantOffset)
	{
		out["TradingDay"] = pEWarrantOffset->TradingDay;
		out["BrokerID"] = pEWarrantOffset->BrokerID;
		out["InvestorID"] = pEWarrantOffset->InvestorID;
		out["ExchangeID"] = pEWarrantOffset->ExchangeID;
		out["InstrumentID"] = pEWarrantOffset->InstrumentID;
		out["InvestUnitID"] = pEWarrantOffset->InvestUnitID;
		out["Volume"] = pEWarrantOffset->Volume;
		out["Direction"] = pEWarrantOffset->Direction;
		out["HedgeFlag"] = pEWarrantOffset->HedgeFlag;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryEWarrantOffset", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryInvestorProductGroupMargin(CThostFtdcInvestorProductGroupMarginField* pInvestorProductGroupMargin, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInvestorProductGroupMargin)
	{
		out["ProductGroupID"] = pInvestorProductGroupMargin->ProductGroupID;
		out["BrokerID"] = pInvestorProductGroupMargin->BrokerID;
		out["InvestorID"] = pInvestorProductGroupMargin->InvestorID;
		out["TradingDay"] = pInvestorProductGroupMargin->TradingDay;
		out["ExchangeID"] = pInvestorProductGroupMargin->ExchangeID;
		out["InvestUnitID"] = pInvestorProductGroupMargin->InvestUnitID;
		out["SettlementID"] = pInvestorProductGroupMargin->SettlementID;
		out["HedgeFlag"] = pInvestorProductGroupMargin->HedgeFlag;
		out["FrozenMargin"] = pInvestorProductGroupMargin->FrozenMargin;
		out["LongFrozenMargin"] = pInvestorProductGroupMargin->LongFrozenMargin;
		out["ShortFrozenMargin"] = pInvestorProductGroupMargin->ShortFrozenMargin;
		out["UseMargin"] = pInvestorProductGroupMargin->UseMargin;
		out["LongUseMargin"] = pInvestorProductGroupMargin->LongUseMargin;
		out["ShortUseMargin"] = pInvestorProductGroupMargin->ShortUseMargin;
		out["ExchMargin"] = pInvestorProductGroupMargin->ExchMargin;
		out["LongExchMargin"] = pInvestorProductGroupMargin->LongExchMargin;
		out["ShortExchMargin"] = pInvestorProductGroupMargin->ShortExchMargin;
		out["CloseProfit"] = pInvestorProductGroupMargin->CloseProfit;
		out["FrozenCommission"] = pInvestorProductGroupMargin->FrozenCommission;
		out["Commission"] = pInvestorProductGroupMargin->Commission;
		out["FrozenCash"] = pInvestorProductGroupMargin->FrozenCash;
		out["CashIn"] = pInvestorProductGroupMargin->CashIn;
		out["PositionProfit"] = pInvestorProductGroupMargin->PositionProfit;
		out["OffsetAmount"] = pInvestorProductGroupMargin->OffsetAmount;
		out["LongOffsetAmount"] = pInvestorProductGroupMargin->LongOffsetAmount;
		out["ShortOffsetAmount"] = pInvestorProductGroupMargin->ShortOffsetAmount;
		out["ExchOffsetAmount"] = pInvestorProductGroupMargin->ExchOffsetAmount;
		out["LongExchOffsetAmount"] = pInvestorProductGroupMargin->LongExchOffsetAmount;
		out["ShortExchOffsetAmount"] = pInvestorProductGroupMargin->ShortExchOffsetAmount;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryInvestorProductGroupMargin", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryExchangeMarginRate(CThostFtdcExchangeMarginRateField* pExchangeMarginRate, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pExchangeMarginRate)
	{
		out["BrokerID"] = pExchangeMarginRate->BrokerID;
		out["InstrumentID"] = pExchangeMarginRate->InstrumentID;
		out["ExchangeID"] = pExchangeMarginRate->ExchangeID;
		out["HedgeFlag"] = pExchangeMarginRate->HedgeFlag;
		out["LongMarginRatioByMoney"] = pExchangeMarginRate->LongMarginRatioByMoney;
		out["LongMarginRatioByVolume"] = pExchangeMarginRate->LongMarginRatioByVolume;
		out["ShortMarginRatioByMoney"] = pExchangeMarginRate->ShortMarginRatioByMoney;
		out["ShortMarginRatioByVolume"] = pExchangeMarginRate->ShortMarginRatioByVolume;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryExchangeMarginRate", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryExchangeMarginRateAdjust(CThostFtdcExchangeMarginRateAdjustField* pExchangeMarginRateAdjust, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pExchangeMarginRateAdjust)
	{
		out["BrokerID"] = pExchangeMarginRateAdjust->BrokerID;
		out["InstrumentID"] = pExchangeMarginRateAdjust->InstrumentID;
		out["HedgeFlag"] = pExchangeMarginRateAdjust->HedgeFlag;
		out["LongMarginRatioByMoney"] = pExchangeMarginRateAdjust->LongMarginRatioByMoney;
		out["LongMarginRatioByVolume"] = pExchangeMarginRateAdjust->LongMarginRatioByVolume;
		out["ShortMarginRatioByMoney"] = pExchangeMarginRateAdjust->ShortMarginRatioByMoney;
		out["ShortMarginRatioByVolume"] = pExchangeMarginRateAdjust->ShortMarginRatioByVolume;
		out["ExchLongMarginRatioByMoney"] = pExchangeMarginRateAdjust->ExchLongMarginRatioByMoney;
		out["ExchLongMarginRatioByVolume"] = pExchangeMarginRateAdjust->ExchLongMarginRatioByVolume;
		out["ExchShortMarginRatioByMoney"] = pExchangeMarginRateAdjust->ExchShortMarginRatioByMoney;
		out["ExchShortMarginRatioByVolume"] = pExchangeMarginRateAdjust->ExchShortMarginRatioByVolume;
		out["NoLongMarginRatioByMoney"] = pExchangeMarginRateAdjust->NoLongMarginRatioByMoney;
		out["NoLongMarginRatioByVolume"] = pExchangeMarginRateAdjust->NoLongMarginRatioByVolume;
		out["NoShortMarginRatioByMoney"] = pExchangeMarginRateAdjust->NoShortMarginRatioByMoney;
		out["NoShortMarginRatioByVolume"] = pExchangeMarginRateAdjust->NoShortMarginRatioByVolume;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryExchangeMarginRateAdjust", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryExchangeRate(CThostFtdcExchangeRateField* pExchangeRate, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pExchangeRate)
	{
		out["BrokerID"] = pExchangeRate->BrokerID;
		out["FromCurrencyID"] = pExchangeRate->FromCurrencyID;
		out["ToCurrencyID"] = pExchangeRate->ToCurrencyID;
		out["FromCurrencyUnit"] = pExchangeRate->FromCurrencyUnit;
		out["ExchangeRate"] = pExchangeRate->ExchangeRate;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryExchangeRate", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQrySecAgentACIDMap(CThostFtdcSecAgentACIDMapField* pSecAgentACIDMap, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pSecAgentACIDMap)
	{
		out["BrokerID"] = pSecAgentACIDMap->BrokerID;
		out["UserID"] = pSecAgentACIDMap->UserID;
		out["AccountID"] = pSecAgentACIDMap->AccountID;
		out["CurrencyID"] = pSecAgentACIDMap->CurrencyID;
		out["BrokerSecAgentID"] = pSecAgentACIDMap->BrokerSecAgentID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQrySecAgentACIDMap", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryProductExchRate(CThostFtdcProductExchRateField* pProductExchRate, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pProductExchRate)
	{
		out["ProductID"] = pProductExchRate->ProductID;
		out["QuoteCurrencyID"] = pProductExchRate->QuoteCurrencyID;
		out["ExchangeID"] = pProductExchRate->ExchangeID;
		out["ExchangeRate"] = pProductExchRate->ExchangeRate;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryProductExchRate", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryProductGroup(CThostFtdcProductGroupField* pProductGroup, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pProductGroup)
	{
		out["ProductID"] = pProductGroup->ProductID;
		out["ExchangeID"] = pProductGroup->ExchangeID;
		out["ProductGroupID"] = pProductGroup->ProductGroupID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryProductGroup", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryMMInstrumentCommissionRate(CThostFtdcMMInstrumentCommissionRateField* pMMInstrumentCommissionRate, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pMMInstrumentCommissionRate)
	{
		out["InstrumentID"] = pMMInstrumentCommissionRate->InstrumentID;
		out["BrokerID"] = pMMInstrumentCommissionRate->BrokerID;
		out["InvestorID"] = pMMInstrumentCommissionRate->InvestorID;
		out["InvestorRange"] = pMMInstrumentCommissionRate->InvestorRange;
		out["OpenRatioByMoney"] = pMMInstrumentCommissionRate->OpenRatioByMoney;
		out["OpenRatioByVolume"] = pMMInstrumentCommissionRate->OpenRatioByVolume;
		out["CloseRatioByMoney"] = pMMInstrumentCommissionRate->CloseRatioByMoney;
		out["CloseRatioByVolume"] = pMMInstrumentCommissionRate->CloseRatioByVolume;
		out["CloseTodayRatioByMoney"] = pMMInstrumentCommissionRate->CloseTodayRatioByMoney;
		out["CloseTodayRatioByVolume"] = pMMInstrumentCommissionRate->CloseTodayRatioByVolume;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryMMInstrumentCommissionRate", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryMMOptionInstrCommRate(CThostFtdcMMOptionInstrCommRateField* pMMOptionInstrCommRate, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pMMOptionInstrCommRate)
	{
		out["InstrumentID"] = pMMOptionInstrCommRate->InstrumentID;
		out["BrokerID"] = pMMOptionInstrCommRate->BrokerID;
		out["InvestorID"] = pMMOptionInstrCommRate->InvestorID;
		out["InvestorRange"] = pMMOptionInstrCommRate->InvestorRange;
		out["OpenRatioByMoney"] = pMMOptionInstrCommRate->OpenRatioByMoney;
		out["OpenRatioByVolume"] = pMMOptionInstrCommRate->OpenRatioByVolume;
		out["CloseRatioByMoney"] = pMMOptionInstrCommRate->CloseRatioByMoney;
		out["CloseRatioByVolume"] = pMMOptionInstrCommRate->CloseRatioByVolume;
		out["CloseTodayRatioByMoney"] = pMMOptionInstrCommRate->CloseTodayRatioByMoney;
		out["CloseTodayRatioByVolume"] = pMMOptionInstrCommRate->CloseTodayRatioByVolume;
		out["StrikeRatioByMoney"] = pMMOptionInstrCommRate->StrikeRatioByMoney;
		out["StrikeRatioByVolume"] = pMMOptionInstrCommRate->StrikeRatioByVolume;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryMMOptionInstrCommRate", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryInstrumentOrderCommRate(CThostFtdcInstrumentOrderCommRateField* pInstrumentOrderCommRate, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInstrumentOrderCommRate)
	{
		out["InstrumentID"] = pInstrumentOrderCommRate->InstrumentID;
		out["BrokerID"] = pInstrumentOrderCommRate->BrokerID;
		out["InvestorID"] = pInstrumentOrderCommRate->InvestorID;
		out["ExchangeID"] = pInstrumentOrderCommRate->ExchangeID;
		out["InvestUnitID"] = pInstrumentOrderCommRate->InvestUnitID;
		out["InvestorRange"] = pInstrumentOrderCommRate->InvestorRange;
		out["HedgeFlag"] = pInstrumentOrderCommRate->HedgeFlag;
		out["OrderCommByVolume"] = pInstrumentOrderCommRate->OrderCommByVolume;
		out["OrderActionCommByVolume"] = pInstrumentOrderCommRate->OrderActionCommByVolume;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryInstrumentOrderCommRate", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQrySecAgentTradingAccount(CThostFtdcTradingAccountField* pTradingAccount, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pTradingAccount)
	{
		out["BrokerID"] = pTradingAccount->BrokerID;
		out["AccountID"] = pTradingAccount->AccountID;
		out["TradingDay"] = pTradingAccount->TradingDay;
		out["CurrencyID"] = pTradingAccount->CurrencyID;
		out["SettlementID"] = pTradingAccount->SettlementID;
		out["BizType"] = pTradingAccount->BizType;
		out["PreMortgage"] = pTradingAccount->PreMortgage;
		out["PreCredit"] = pTradingAccount->PreCredit;
		out["PreDeposit"] = pTradingAccount->PreDeposit;
		out["PreBalance"] = pTradingAccount->PreBalance;
		out["PreMargin"] = pTradingAccount->PreMargin;
		out["InterestBase"] = pTradingAccount->InterestBase;
		out["Interest"] = pTradingAccount->Interest;
		out["Deposit"] = pTradingAccount->Deposit;
		out["Withdraw"] = pTradingAccount->Withdraw;
		out["FrozenMargin"] = pTradingAccount->FrozenMargin;
		out["FrozenCash"] = pTradingAccount->FrozenCash;
		out["FrozenCommission"] = pTradingAccount->FrozenCommission;
		out["CurrMargin"] = pTradingAccount->CurrMargin;
		out["CashIn"] = pTradingAccount->CashIn;
		out["Commission"] = pTradingAccount->Commission;
		out["CloseProfit"] = pTradingAccount->CloseProfit;
		out["PositionProfit"] = pTradingAccount->PositionProfit;
		out["Balance"] = pTradingAccount->Balance;
		out["Available"] = pTradingAccount->Available;
		out["WithdrawQuota"] = pTradingAccount->WithdrawQuota;
		out["Reserve"] = pTradingAccount->Reserve;
		out["Credit"] = pTradingAccount->Credit;
		out["Mortgage"] = pTradingAccount->Mortgage;
		out["ExchangeMargin"] = pTradingAccount->ExchangeMargin;
		out["DeliveryMargin"] = pTradingAccount->DeliveryMargin;
		out["ExchangeDeliveryMargin"] = pTradingAccount->ExchangeDeliveryMargin;
		out["ReserveBalance"] = pTradingAccount->ReserveBalance;
		out["PreFundMortgageIn"] = pTradingAccount->PreFundMortgageIn;
		out["PreFundMortgageOut"] = pTradingAccount->PreFundMortgageOut;
		out["FundMortgageIn"] = pTradingAccount->FundMortgageIn;
		out["FundMortgageOut"] = pTradingAccount->FundMortgageOut;
		out["FundMortgageAvailable"] = pTradingAccount->FundMortgageAvailable;
		out["MortgageableFund"] = pTradingAccount->MortgageableFund;
		out["SpecProductMargin"] = pTradingAccount->SpecProductMargin;
		out["SpecProductFrozenMargin"] = pTradingAccount->SpecProductFrozenMargin;
		out["SpecProductCommission"] = pTradingAccount->SpecProductCommission;
		out["SpecProductFrozenCommission"] = pTradingAccount->SpecProductFrozenCommission;
		out["SpecProductPositionProfit"] = pTradingAccount->SpecProductPositionProfit;
		out["SpecProductCloseProfit"] = pTradingAccount->SpecProductCloseProfit;
		out["SpecProductPositionProfitByAlg"] = pTradingAccount->SpecProductPositionProfitByAlg;
		out["SpecProductExchangeMargin"] = pTradingAccount->SpecProductExchangeMargin;
		out["FrozenSwap"] = pTradingAccount->FrozenSwap;
		out["RemainSwap"] = pTradingAccount->RemainSwap;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQrySecAgentTradingAccount", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQrySecAgentCheckMode(CThostFtdcSecAgentCheckModeField* pSecAgentCheckMode, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pSecAgentCheckMode)
	{
		out["InvestorID"] = pSecAgentCheckMode->InvestorID;
		out["BrokerID"] = pSecAgentCheckMode->BrokerID;
		out["CurrencyID"] = pSecAgentCheckMode->CurrencyID;
		out["BrokerSecAgentID"] = pSecAgentCheckMode->BrokerSecAgentID;
		out["CheckSelfAccount"] = pSecAgentCheckMode->CheckSelfAccount;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQrySecAgentCheckMode", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQrySecAgentTradeInfo(CThostFtdcSecAgentTradeInfoField* pSecAgentTradeInfo, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pSecAgentTradeInfo)
	{
		out["BrokerID"] = pSecAgentTradeInfo->BrokerID;
		out["BrokerSecAgentID"] = pSecAgentTradeInfo->BrokerSecAgentID;
		out["InvestorID"] = pSecAgentTradeInfo->InvestorID;
		out["LongCustomerName"] = pSecAgentTradeInfo->LongCustomerName;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQrySecAgentTradeInfo", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryOptionInstrTradeCost(CThostFtdcOptionInstrTradeCostField* pOptionInstrTradeCost, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pOptionInstrTradeCost)
	{
		out["BrokerID"] = pOptionInstrTradeCost->BrokerID;
		out["InvestorID"] = pOptionInstrTradeCost->InvestorID;
		out["InstrumentID"] = pOptionInstrTradeCost->InstrumentID;
		out["ExchangeID"] = pOptionInstrTradeCost->ExchangeID;
		out["InvestUnitID"] = pOptionInstrTradeCost->InvestUnitID;
		out["HedgeFlag"] = pOptionInstrTradeCost->HedgeFlag;
		out["FixedMargin"] = pOptionInstrTradeCost->FixedMargin;
		out["MiniMargin"] = pOptionInstrTradeCost->MiniMargin;
		out["Royalty"] = pOptionInstrTradeCost->Royalty;
		out["ExchFixedMargin"] = pOptionInstrTradeCost->ExchFixedMargin;
		out["ExchMiniMargin"] = pOptionInstrTradeCost->ExchMiniMargin;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryOptionInstrTradeCost", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryOptionInstrCommRate(CThostFtdcOptionInstrCommRateField* pOptionInstrCommRate, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pOptionInstrCommRate)
	{
		out["InstrumentID"] = pOptionInstrCommRate->InstrumentID;
		out["BrokerID"] = pOptionInstrCommRate->BrokerID;
		out["InvestorID"] = pOptionInstrCommRate->InvestorID;
		out["ExchangeID"] = pOptionInstrCommRate->ExchangeID;
		out["InvestUnitID"] = pOptionInstrCommRate->InvestUnitID;
		out["InvestorRange"] = pOptionInstrCommRate->InvestorRange;
		out["OpenRatioByMoney"] = pOptionInstrCommRate->OpenRatioByMoney;
		out["OpenRatioByVolume"] = pOptionInstrCommRate->OpenRatioByVolume;
		out["CloseRatioByMoney"] = pOptionInstrCommRate->CloseRatioByMoney;
		out["CloseRatioByVolume"] = pOptionInstrCommRate->CloseRatioByVolume;
		out["CloseTodayRatioByMoney"] = pOptionInstrCommRate->CloseTodayRatioByMoney;
		out["CloseTodayRatioByVolume"] = pOptionInstrCommRate->CloseTodayRatioByVolume;
		out["StrikeRatioByMoney"] = pOptionInstrCommRate->StrikeRatioByMoney;
		out["StrikeRatioByVolume"] = pOptionInstrCommRate->StrikeRatioByVolume;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryOptionInstrCommRate", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryExecOrder(CThostFtdcExecOrderField* pExecOrder, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pExecOrder)
	{
		out["BrokerID"] = pExecOrder->BrokerID;
		out["InvestorID"] = pExecOrder->InvestorID;
		out["InstrumentID"] = pExecOrder->InstrumentID;
		out["ExecOrderRef"] = pExecOrder->ExecOrderRef;
		out["UserID"] = pExecOrder->UserID;
		out["BusinessUnit"] = pExecOrder->BusinessUnit;
		out["ExecOrderLocalID"] = pExecOrder->ExecOrderLocalID;
		out["ExchangeID"] = pExecOrder->ExchangeID;
		out["ParticipantID"] = pExecOrder->ParticipantID;
		out["ClientID"] = pExecOrder->ClientID;
		out["ExchangeInstID"] = pExecOrder->ExchangeInstID;
		out["TraderID"] = pExecOrder->TraderID;
		out["TradingDay"] = pExecOrder->TradingDay;
		out["ExecOrderSysID"] = pExecOrder->ExecOrderSysID;
		out["InsertDate"] = pExecOrder->InsertDate;
		out["InsertTime"] = pExecOrder->InsertTime;
		out["CancelTime"] = pExecOrder->CancelTime;
		out["ClearingPartID"] = pExecOrder->ClearingPartID;
		out["UserProductInfo"] = pExecOrder->UserProductInfo;
		out["StatusMsg"] = pExecOrder->StatusMsg;
		out["ActiveUserID"] = pExecOrder->ActiveUserID;
		out["BranchID"] = pExecOrder->BranchID;
		out["InvestUnitID"] = pExecOrder->InvestUnitID;
		out["AccountID"] = pExecOrder->AccountID;
		out["CurrencyID"] = pExecOrder->CurrencyID;
		out["IPAddress"] = pExecOrder->IPAddress;
		out["MacAddress"] = pExecOrder->MacAddress;
		out["Volume"] = pExecOrder->Volume;
		out["RequestID"] = pExecOrder->RequestID;
		out["InstallID"] = pExecOrder->InstallID;
		out["NotifySequence"] = pExecOrder->NotifySequence;
		out["SettlementID"] = pExecOrder->SettlementID;
		out["SequenceNo"] = pExecOrder->SequenceNo;
		out["FrontID"] = pExecOrder->FrontID;
		out["SessionID"] = pExecOrder->SessionID;
		out["BrokerExecOrderSeq"] = pExecOrder->BrokerExecOrderSeq;
		out["OffsetFlag"] = pExecOrder->OffsetFlag;
		out["HedgeFlag"] = pExecOrder->HedgeFlag;
		out["ActionType"] = pExecOrder->ActionType;
		out["PosiDirection"] = pExecOrder->PosiDirection;
		out["ReservePositionFlag"] = pExecOrder->ReservePositionFlag;
		out["CloseFlag"] = pExecOrder->CloseFlag;
		out["OrderSubmitStatus"] = pExecOrder->OrderSubmitStatus;
		out["ExecResult"] = pExecOrder->ExecResult;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryExecOrder", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryForQuote(CThostFtdcForQuoteField* pForQuote, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pForQuote)
	{
		out["BrokerID"] = pForQuote->BrokerID;
		out["InvestorID"] = pForQuote->InvestorID;
		out["InstrumentID"] = pForQuote->InstrumentID;
		out["ForQuoteRef"] = pForQuote->ForQuoteRef;
		out["UserID"] = pForQuote->UserID;
		out["ForQuoteLocalID"] = pForQuote->ForQuoteLocalID;
		out["ExchangeID"] = pForQuote->ExchangeID;
		out["ParticipantID"] = pForQuote->ParticipantID;
		out["ClientID"] = pForQuote->ClientID;
		out["ExchangeInstID"] = pForQuote->ExchangeInstID;
		out["TraderID"] = pForQuote->TraderID;
		out["InsertDate"] = pForQuote->InsertDate;
		out["InsertTime"] = pForQuote->InsertTime;
		out["StatusMsg"] = pForQuote->StatusMsg;
		out["ActiveUserID"] = pForQuote->ActiveUserID;
		out["InvestUnitID"] = pForQuote->InvestUnitID;
		out["IPAddress"] = pForQuote->IPAddress;
		out["MacAddress"] = pForQuote->MacAddress;
		out["InstallID"] = pForQuote->InstallID;
		out["FrontID"] = pForQuote->FrontID;
		out["SessionID"] = pForQuote->SessionID;
		out["BrokerForQutoSeq"] = pForQuote->BrokerForQutoSeq;
		out["ForQuoteStatus"] = pForQuote->ForQuoteStatus;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryForQuote", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryQuote(CThostFtdcQuoteField* pQuote, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pQuote)
	{
		out["BrokerID"] = pQuote->BrokerID;
		out["InvestorID"] = pQuote->InvestorID;
		out["InstrumentID"] = pQuote->InstrumentID;
		out["QuoteRef"] = pQuote->QuoteRef;
		out["UserID"] = pQuote->UserID;
		out["BusinessUnit"] = pQuote->BusinessUnit;
		out["QuoteLocalID"] = pQuote->QuoteLocalID;
		out["ExchangeID"] = pQuote->ExchangeID;
		out["ParticipantID"] = pQuote->ParticipantID;
		out["ClientID"] = pQuote->ClientID;
		out["ExchangeInstID"] = pQuote->ExchangeInstID;
		out["TraderID"] = pQuote->TraderID;
		out["TradingDay"] = pQuote->TradingDay;
		out["QuoteSysID"] = pQuote->QuoteSysID;
		out["InsertDate"] = pQuote->InsertDate;
		out["InsertTime"] = pQuote->InsertTime;
		out["CancelTime"] = pQuote->CancelTime;
		out["ClearingPartID"] = pQuote->ClearingPartID;
		out["AskOrderSysID"] = pQuote->AskOrderSysID;
		out["BidOrderSysID"] = pQuote->BidOrderSysID;
		out["UserProductInfo"] = pQuote->UserProductInfo;
		out["StatusMsg"] = pQuote->StatusMsg;
		out["ActiveUserID"] = pQuote->ActiveUserID;
		out["AskOrderRef"] = pQuote->AskOrderRef;
		out["BidOrderRef"] = pQuote->BidOrderRef;
		out["ForQuoteSysID"] = pQuote->ForQuoteSysID;
		out["BranchID"] = pQuote->BranchID;
		out["InvestUnitID"] = pQuote->InvestUnitID;
		out["AccountID"] = pQuote->AccountID;
		out["CurrencyID"] = pQuote->CurrencyID;
		out["IPAddress"] = pQuote->IPAddress;
		out["MacAddress"] = pQuote->MacAddress;
		out["AskVolume"] = pQuote->AskVolume;
		out["BidVolume"] = pQuote->BidVolume;
		out["RequestID"] = pQuote->RequestID;
		out["InstallID"] = pQuote->InstallID;
		out["NotifySequence"] = pQuote->NotifySequence;
		out["SettlementID"] = pQuote->SettlementID;
		out["SequenceNo"] = pQuote->SequenceNo;
		out["FrontID"] = pQuote->FrontID;
		out["SessionID"] = pQuote->SessionID;
		out["BrokerQuoteSeq"] = pQuote->BrokerQuoteSeq;
		out["AskOffsetFlag"] = pQuote->AskOffsetFlag;
		out["BidOffsetFlag"] = pQuote->BidOffsetFlag;
		out["AskHedgeFlag"] = pQuote->AskHedgeFlag;
		out["BidHedgeFlag"] = pQuote->BidHedgeFlag;
		out["OrderSubmitStatus"] = pQuote->OrderSubmitStatus;
		out["QuoteStatus"] = pQuote->QuoteStatus;
		out["AskPrice"] = pQuote->AskPrice;
		out["BidPrice"] = pQuote->BidPrice;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryQuote", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryOptionSelfClose(CThostFtdcOptionSelfCloseField* pOptionSelfClose, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pOptionSelfClose)
	{
		out["BrokerID"] = pOptionSelfClose->BrokerID;
		out["InvestorID"] = pOptionSelfClose->InvestorID;
		out["InstrumentID"] = pOptionSelfClose->InstrumentID;
		out["OptionSelfCloseRef"] = pOptionSelfClose->OptionSelfCloseRef;
		out["UserID"] = pOptionSelfClose->UserID;
		out["BusinessUnit"] = pOptionSelfClose->BusinessUnit;
		out["OptionSelfCloseLocalID"] = pOptionSelfClose->OptionSelfCloseLocalID;
		out["ExchangeID"] = pOptionSelfClose->ExchangeID;
		out["ParticipantID"] = pOptionSelfClose->ParticipantID;
		out["ClientID"] = pOptionSelfClose->ClientID;
		out["ExchangeInstID"] = pOptionSelfClose->ExchangeInstID;
		out["TraderID"] = pOptionSelfClose->TraderID;
		out["TradingDay"] = pOptionSelfClose->TradingDay;
		out["OptionSelfCloseSysID"] = pOptionSelfClose->OptionSelfCloseSysID;
		out["InsertDate"] = pOptionSelfClose->InsertDate;
		out["InsertTime"] = pOptionSelfClose->InsertTime;
		out["CancelTime"] = pOptionSelfClose->CancelTime;
		out["ClearingPartID"] = pOptionSelfClose->ClearingPartID;
		out["UserProductInfo"] = pOptionSelfClose->UserProductInfo;
		out["StatusMsg"] = pOptionSelfClose->StatusMsg;
		out["ActiveUserID"] = pOptionSelfClose->ActiveUserID;
		out["BranchID"] = pOptionSelfClose->BranchID;
		out["InvestUnitID"] = pOptionSelfClose->InvestUnitID;
		out["AccountID"] = pOptionSelfClose->AccountID;
		out["CurrencyID"] = pOptionSelfClose->CurrencyID;
		out["IPAddress"] = pOptionSelfClose->IPAddress;
		out["MacAddress"] = pOptionSelfClose->MacAddress;
		out["Volume"] = pOptionSelfClose->Volume;
		out["RequestID"] = pOptionSelfClose->RequestID;
		out["InstallID"] = pOptionSelfClose->InstallID;
		out["NotifySequence"] = pOptionSelfClose->NotifySequence;
		out["SettlementID"] = pOptionSelfClose->SettlementID;
		out["SequenceNo"] = pOptionSelfClose->SequenceNo;
		out["FrontID"] = pOptionSelfClose->FrontID;
		out["SessionID"] = pOptionSelfClose->SessionID;
		out["BrokerOptionSelfCloseSeq"] = pOptionSelfClose->BrokerOptionSelfCloseSeq;
		out["HedgeFlag"] = pOptionSelfClose->HedgeFlag;
		out["OptSelfCloseFlag"] = pOptionSelfClose->OptSelfCloseFlag;
		out["OrderSubmitStatus"] = pOptionSelfClose->OrderSubmitStatus;
		out["ExecResult"] = pOptionSelfClose->ExecResult;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryOptionSelfClose", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryInvestUnit(CThostFtdcInvestUnitField* pInvestUnit, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInvestUnit)
	{
		out["BrokerID"] = pInvestUnit->BrokerID;
		out["InvestorID"] = pInvestUnit->InvestorID;
		out["InvestUnitID"] = pInvestUnit->InvestUnitID;
		out["InvestorUnitName"] = pInvestUnit->InvestorUnitName;
		out["InvestorGroupID"] = pInvestUnit->InvestorGroupID;
		out["CommModelID"] = pInvestUnit->CommModelID;
		out["MarginModelID"] = pInvestUnit->MarginModelID;
		out["AccountID"] = pInvestUnit->AccountID;
		out["CurrencyID"] = pInvestUnit->CurrencyID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryInvestUnit", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryCombInstrumentGuard(CThostFtdcCombInstrumentGuardField* pCombInstrumentGuard, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pCombInstrumentGuard)
	{
		out["BrokerID"] = pCombInstrumentGuard->BrokerID;
		out["InstrumentID"] = pCombInstrumentGuard->InstrumentID;
		out["ExchangeID"] = pCombInstrumentGuard->ExchangeID;
		out["GuarantRatio"] = pCombInstrumentGuard->GuarantRatio;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryCombInstrumentGuard", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryCombAction(CThostFtdcCombActionField* pCombAction, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pCombAction)
	{
		out["BrokerID"] = pCombAction->BrokerID;
		out["InvestorID"] = pCombAction->InvestorID;
		out["InstrumentID"] = pCombAction->InstrumentID;
		out["CombActionRef"] = pCombAction->CombActionRef;
		out["UserID"] = pCombAction->UserID;
		out["ActionLocalID"] = pCombAction->ActionLocalID;
		out["ExchangeID"] = pCombAction->ExchangeID;
		out["ParticipantID"] = pCombAction->ParticipantID;
		out["ClientID"] = pCombAction->ClientID;
		out["ExchangeInstID"] = pCombAction->ExchangeInstID;
		out["TraderID"] = pCombAction->TraderID;
		out["TradingDay"] = pCombAction->TradingDay;
		out["UserProductInfo"] = pCombAction->UserProductInfo;
		out["StatusMsg"] = pCombAction->StatusMsg;
		out["IPAddress"] = pCombAction->IPAddress;
		out["MacAddress"] = pCombAction->MacAddress;
		out["ComTradeID"] = pCombAction->ComTradeID;
		out["BranchID"] = pCombAction->BranchID;
		out["InvestUnitID"] = pCombAction->InvestUnitID;
		out["Volume"] = pCombAction->Volume;
		out["InstallID"] = pCombAction->InstallID;
		out["NotifySequence"] = pCombAction->NotifySequence;
		out["SettlementID"] = pCombAction->SettlementID;
		out["SequenceNo"] = pCombAction->SequenceNo;
		out["FrontID"] = pCombAction->FrontID;
		out["SessionID"] = pCombAction->SessionID;
		out["Direction"] = pCombAction->Direction;
		out["CombDirection"] = pCombAction->CombDirection;
		out["HedgeFlag"] = pCombAction->HedgeFlag;
		out["ActionStatus"] = pCombAction->ActionStatus;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryCombAction", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryTransferSerial(CThostFtdcTransferSerialField* pTransferSerial, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pTransferSerial)
	{
		out["TradeDate"] = pTransferSerial->TradeDate;
		out["TradingDay"] = pTransferSerial->TradingDay;
		out["TradeTime"] = pTransferSerial->TradeTime;
		out["TradeCode"] = pTransferSerial->TradeCode;
		out["BankID"] = pTransferSerial->BankID;
		out["BankBranchID"] = pTransferSerial->BankBranchID;
		out["BankAccount"] = pTransferSerial->BankAccount;
		out["BankSerial"] = pTransferSerial->BankSerial;
		out["BrokerID"] = pTransferSerial->BrokerID;
		out["BrokerBranchID"] = pTransferSerial->BrokerBranchID;
		out["AccountID"] = pTransferSerial->AccountID;
		out["InvestorID"] = pTransferSerial->InvestorID;
		out["IdentifiedCardNo"] = pTransferSerial->IdentifiedCardNo;
		out["CurrencyID"] = pTransferSerial->CurrencyID;
		out["OperatorCode"] = pTransferSerial->OperatorCode;
		out["BankNewAccount"] = pTransferSerial->BankNewAccount;
		out["ErrorMsg"] = pTransferSerial->ErrorMsg;
		out["PlateSerial"] = pTransferSerial->PlateSerial;
		out["SessionID"] = pTransferSerial->SessionID;
		out["FutureSerial"] = pTransferSerial->FutureSerial;
		out["ErrorID"] = pTransferSerial->ErrorID;
		out["BankAccType"] = pTransferSerial->BankAccType;
		out["FutureAccType"] = pTransferSerial->FutureAccType;
		out["IdCardType"] = pTransferSerial->IdCardType;
		out["AvailabilityFlag"] = pTransferSerial->AvailabilityFlag;
		out["TradeAmount"] = pTransferSerial->TradeAmount;
		out["CustFee"] = pTransferSerial->CustFee;
		out["BrokerFee"] = pTransferSerial->BrokerFee;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryTransferSerial", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryAccountregister(CThostFtdcAccountregisterField* pAccountregister, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pAccountregister)
	{
		out["TradeDay"] = pAccountregister->TradeDay;
		out["BankID"] = pAccountregister->BankID;
		out["BankBranchID"] = pAccountregister->BankBranchID;
		out["BankAccount"] = pAccountregister->BankAccount;
		out["BrokerID"] = pAccountregister->BrokerID;
		out["BrokerBranchID"] = pAccountregister->BrokerBranchID;
		out["AccountID"] = pAccountregister->AccountID;
		out["IdentifiedCardNo"] = pAccountregister->IdentifiedCardNo;
		out["CustomerName"] = pAccountregister->CustomerName;
		out["CurrencyID"] = pAccountregister->CurrencyID;
		out["RegDate"] = pAccountregister->RegDate;
		out["OutDate"] = pAccountregister->OutDate;
		out["LongCustomerName"] = pAccountregister->LongCustomerName;
		out["TID"] = pAccountregister->TID;
		out["IdCardType"] = pAccountregister->IdCardType;
		out["OpenOrDestroy"] = pAccountregister->OpenOrDestroy;
		out["CustType"] = pAccountregister->CustType;
		out["BankAccType"] = pAccountregister->BankAccType;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryAccountregister", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspError(CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspError", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnOrder(CThostFtdcOrderField* pOrder)
{
	Json::Value out;
	if (pOrder)
	{
		out["BrokerID"] = pOrder->BrokerID;
		out["InvestorID"] = pOrder->InvestorID;
		out["InstrumentID"] = pOrder->InstrumentID;
		out["OrderRef"] = pOrder->OrderRef;
		out["UserID"] = pOrder->UserID;
		out["CombOffsetFlag"] = pOrder->CombOffsetFlag;
		out["CombHedgeFlag"] = pOrder->CombHedgeFlag;
		out["GTDDate"] = pOrder->GTDDate;
		out["BusinessUnit"] = pOrder->BusinessUnit;
		out["OrderLocalID"] = pOrder->OrderLocalID;
		out["ExchangeID"] = pOrder->ExchangeID;
		out["ParticipantID"] = pOrder->ParticipantID;
		out["ClientID"] = pOrder->ClientID;
		out["ExchangeInstID"] = pOrder->ExchangeInstID;
		out["TraderID"] = pOrder->TraderID;
		out["TradingDay"] = pOrder->TradingDay;
		out["OrderSysID"] = pOrder->OrderSysID;
		out["InsertDate"] = pOrder->InsertDate;
		out["InsertTime"] = pOrder->InsertTime;
		out["ActiveTime"] = pOrder->ActiveTime;
		out["SuspendTime"] = pOrder->SuspendTime;
		out["UpdateTime"] = pOrder->UpdateTime;
		out["CancelTime"] = pOrder->CancelTime;
		out["ActiveTraderID"] = pOrder->ActiveTraderID;
		out["ClearingPartID"] = pOrder->ClearingPartID;
		out["UserProductInfo"] = pOrder->UserProductInfo;
		out["StatusMsg"] = pOrder->StatusMsg;
		out["ActiveUserID"] = pOrder->ActiveUserID;
		out["RelativeOrderSysID"] = pOrder->RelativeOrderSysID;
		out["BranchID"] = pOrder->BranchID;
		out["InvestUnitID"] = pOrder->InvestUnitID;
		out["AccountID"] = pOrder->AccountID;
		out["CurrencyID"] = pOrder->CurrencyID;
		out["IPAddress"] = pOrder->IPAddress;
		out["MacAddress"] = pOrder->MacAddress;
		out["VolumeTotalOriginal"] = pOrder->VolumeTotalOriginal;
		out["MinVolume"] = pOrder->MinVolume;
		out["IsAutoSuspend"] = pOrder->IsAutoSuspend;
		out["RequestID"] = pOrder->RequestID;
		out["InstallID"] = pOrder->InstallID;
		out["NotifySequence"] = pOrder->NotifySequence;
		out["SettlementID"] = pOrder->SettlementID;
		out["VolumeTraded"] = pOrder->VolumeTraded;
		out["VolumeTotal"] = pOrder->VolumeTotal;
		out["SequenceNo"] = pOrder->SequenceNo;
		out["FrontID"] = pOrder->FrontID;
		out["SessionID"] = pOrder->SessionID;
		out["UserForceClose"] = pOrder->UserForceClose;
		out["BrokerOrderSeq"] = pOrder->BrokerOrderSeq;
		out["ZCETotalTradedVolume"] = pOrder->ZCETotalTradedVolume;
		out["IsSwapOrder"] = pOrder->IsSwapOrder;
		out["OrderPriceType"] = pOrder->OrderPriceType;
		out["Direction"] = pOrder->Direction;
		out["TimeCondition"] = pOrder->TimeCondition;
		out["VolumeCondition"] = pOrder->VolumeCondition;
		out["ContingentCondition"] = pOrder->ContingentCondition;
		out["ForceCloseReason"] = pOrder->ForceCloseReason;
		out["OrderSubmitStatus"] = pOrder->OrderSubmitStatus;
		out["OrderSource"] = pOrder->OrderSource;
		out["OrderStatus"] = pOrder->OrderStatus;
		out["OrderType"] = pOrder->OrderType;
		out["LimitPrice"] = pOrder->LimitPrice;
		out["StopPrice"] = pOrder->StopPrice;
	}
	jnaResCallback("T_OnRtnOrder", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnTrade(CThostFtdcTradeField* pTrade)
{
	Json::Value out;
	if (pTrade)
	{
		out["BrokerID"] = pTrade->BrokerID;
		out["InvestorID"] = pTrade->InvestorID;
		out["InstrumentID"] = pTrade->InstrumentID;
		out["OrderRef"] = pTrade->OrderRef;
		out["UserID"] = pTrade->UserID;
		out["ExchangeID"] = pTrade->ExchangeID;
		out["TradeID"] = pTrade->TradeID;
		out["OrderSysID"] = pTrade->OrderSysID;
		out["ParticipantID"] = pTrade->ParticipantID;
		out["ClientID"] = pTrade->ClientID;
		out["ExchangeInstID"] = pTrade->ExchangeInstID;
		out["TradeDate"] = pTrade->TradeDate;
		out["TradeTime"] = pTrade->TradeTime;
		out["TraderID"] = pTrade->TraderID;
		out["OrderLocalID"] = pTrade->OrderLocalID;
		out["ClearingPartID"] = pTrade->ClearingPartID;
		out["BusinessUnit"] = pTrade->BusinessUnit;
		out["TradingDay"] = pTrade->TradingDay;
		out["InvestUnitID"] = pTrade->InvestUnitID;
		out["Volume"] = pTrade->Volume;
		out["SequenceNo"] = pTrade->SequenceNo;
		out["SettlementID"] = pTrade->SettlementID;
		out["BrokerOrderSeq"] = pTrade->BrokerOrderSeq;
		out["Direction"] = pTrade->Direction;
		out["TradingRole"] = pTrade->TradingRole;
		out["OffsetFlag"] = pTrade->OffsetFlag;
		out["HedgeFlag"] = pTrade->HedgeFlag;
		out["TradeType"] = pTrade->TradeType;
		out["PriceSource"] = pTrade->PriceSource;
		out["TradeSource"] = pTrade->TradeSource;
		out["Price"] = pTrade->Price;
	}
	jnaResCallback("T_OnRtnTrade", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnOrderInsert(CThostFtdcInputOrderField* pInputOrder, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pInputOrder)
	{
		out["BrokerID"] = pInputOrder->BrokerID;
		out["InvestorID"] = pInputOrder->InvestorID;
		out["InstrumentID"] = pInputOrder->InstrumentID;
		out["OrderRef"] = pInputOrder->OrderRef;
		out["UserID"] = pInputOrder->UserID;
		out["CombOffsetFlag"] = pInputOrder->CombOffsetFlag;
		out["CombHedgeFlag"] = pInputOrder->CombHedgeFlag;
		out["GTDDate"] = pInputOrder->GTDDate;
		out["BusinessUnit"] = pInputOrder->BusinessUnit;
		out["ExchangeID"] = pInputOrder->ExchangeID;
		out["InvestUnitID"] = pInputOrder->InvestUnitID;
		out["AccountID"] = pInputOrder->AccountID;
		out["CurrencyID"] = pInputOrder->CurrencyID;
		out["ClientID"] = pInputOrder->ClientID;
		out["IPAddress"] = pInputOrder->IPAddress;
		out["MacAddress"] = pInputOrder->MacAddress;
		out["VolumeTotalOriginal"] = pInputOrder->VolumeTotalOriginal;
		out["MinVolume"] = pInputOrder->MinVolume;
		out["IsAutoSuspend"] = pInputOrder->IsAutoSuspend;
		out["RequestID"] = pInputOrder->RequestID;
		out["UserForceClose"] = pInputOrder->UserForceClose;
		out["IsSwapOrder"] = pInputOrder->IsSwapOrder;
		out["OrderPriceType"] = pInputOrder->OrderPriceType;
		out["Direction"] = pInputOrder->Direction;
		out["TimeCondition"] = pInputOrder->TimeCondition;
		out["VolumeCondition"] = pInputOrder->VolumeCondition;
		out["ContingentCondition"] = pInputOrder->ContingentCondition;
		out["ForceCloseReason"] = pInputOrder->ForceCloseReason;
		out["LimitPrice"] = pInputOrder->LimitPrice;
		out["StopPrice"] = pInputOrder->StopPrice;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnOrderInsert", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnOrderAction(CThostFtdcOrderActionField* pOrderAction, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pOrderAction)
	{
		out["BrokerID"] = pOrderAction->BrokerID;
		out["InvestorID"] = pOrderAction->InvestorID;
		out["OrderRef"] = pOrderAction->OrderRef;
		out["ExchangeID"] = pOrderAction->ExchangeID;
		out["OrderSysID"] = pOrderAction->OrderSysID;
		out["ActionDate"] = pOrderAction->ActionDate;
		out["ActionTime"] = pOrderAction->ActionTime;
		out["TraderID"] = pOrderAction->TraderID;
		out["OrderLocalID"] = pOrderAction->OrderLocalID;
		out["ActionLocalID"] = pOrderAction->ActionLocalID;
		out["ParticipantID"] = pOrderAction->ParticipantID;
		out["ClientID"] = pOrderAction->ClientID;
		out["BusinessUnit"] = pOrderAction->BusinessUnit;
		out["UserID"] = pOrderAction->UserID;
		out["StatusMsg"] = pOrderAction->StatusMsg;
		out["InstrumentID"] = pOrderAction->InstrumentID;
		out["BranchID"] = pOrderAction->BranchID;
		out["InvestUnitID"] = pOrderAction->InvestUnitID;
		out["IPAddress"] = pOrderAction->IPAddress;
		out["MacAddress"] = pOrderAction->MacAddress;
		out["OrderActionRef"] = pOrderAction->OrderActionRef;
		out["RequestID"] = pOrderAction->RequestID;
		out["FrontID"] = pOrderAction->FrontID;
		out["SessionID"] = pOrderAction->SessionID;
		out["VolumeChange"] = pOrderAction->VolumeChange;
		out["InstallID"] = pOrderAction->InstallID;
		out["ActionFlag"] = pOrderAction->ActionFlag;
		out["OrderActionStatus"] = pOrderAction->OrderActionStatus;
		out["LimitPrice"] = pOrderAction->LimitPrice;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnOrderAction", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnInstrumentStatus(CThostFtdcInstrumentStatusField* pInstrumentStatus)
{
	Json::Value out;
	if (pInstrumentStatus)
	{
		out["ExchangeID"] = pInstrumentStatus->ExchangeID;
		out["ExchangeInstID"] = pInstrumentStatus->ExchangeInstID;
		out["SettlementGroupID"] = pInstrumentStatus->SettlementGroupID;
		out["InstrumentID"] = pInstrumentStatus->InstrumentID;
		out["EnterTime"] = pInstrumentStatus->EnterTime;
		out["TradingSegmentSN"] = pInstrumentStatus->TradingSegmentSN;
		out["InstrumentStatus"] = pInstrumentStatus->InstrumentStatus;
		out["EnterReason"] = pInstrumentStatus->EnterReason;
	}
	jnaResCallback("T_OnRtnInstrumentStatus", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnBulletin(CThostFtdcBulletinField* pBulletin)
{
	Json::Value out;
	if (pBulletin)
	{
		out["ExchangeID"] = pBulletin->ExchangeID;
		out["TradingDay"] = pBulletin->TradingDay;
		out["NewsType"] = pBulletin->NewsType;
		out["SendTime"] = pBulletin->SendTime;
		out["Abstract"] = pBulletin->Abstract;
		out["ComeFrom"] = pBulletin->ComeFrom;
		out["Content"] = pBulletin->Content;
		out["URLLink"] = pBulletin->URLLink;
		out["MarketID"] = pBulletin->MarketID;
		out["BulletinID"] = pBulletin->BulletinID;
		out["SequenceNo"] = pBulletin->SequenceNo;
		out["NewsUrgency"] = pBulletin->NewsUrgency;
	}
	jnaResCallback("T_OnRtnBulletin", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnTradingNotice(CThostFtdcTradingNoticeInfoField* pTradingNoticeInfo)
{
	Json::Value out;
	if (pTradingNoticeInfo)
	{
		out["BrokerID"] = pTradingNoticeInfo->BrokerID;
		out["InvestorID"] = pTradingNoticeInfo->InvestorID;
		out["SendTime"] = pTradingNoticeInfo->SendTime;
		out["FieldContent"] = pTradingNoticeInfo->FieldContent;
		out["InvestUnitID"] = pTradingNoticeInfo->InvestUnitID;
		out["SequenceNo"] = pTradingNoticeInfo->SequenceNo;
		out["SequenceSeries"] = pTradingNoticeInfo->SequenceSeries;
	}
	jnaResCallback("T_OnRtnTradingNotice", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnErrorConditionalOrder(CThostFtdcErrorConditionalOrderField* pErrorConditionalOrder)
{
	Json::Value out;
	if (pErrorConditionalOrder)
	{
		out["BrokerID"] = pErrorConditionalOrder->BrokerID;
		out["InvestorID"] = pErrorConditionalOrder->InvestorID;
		out["InstrumentID"] = pErrorConditionalOrder->InstrumentID;
		out["OrderRef"] = pErrorConditionalOrder->OrderRef;
		out["UserID"] = pErrorConditionalOrder->UserID;
		out["CombOffsetFlag"] = pErrorConditionalOrder->CombOffsetFlag;
		out["CombHedgeFlag"] = pErrorConditionalOrder->CombHedgeFlag;
		out["GTDDate"] = pErrorConditionalOrder->GTDDate;
		out["BusinessUnit"] = pErrorConditionalOrder->BusinessUnit;
		out["OrderLocalID"] = pErrorConditionalOrder->OrderLocalID;
		out["ExchangeID"] = pErrorConditionalOrder->ExchangeID;
		out["ParticipantID"] = pErrorConditionalOrder->ParticipantID;
		out["ClientID"] = pErrorConditionalOrder->ClientID;
		out["ExchangeInstID"] = pErrorConditionalOrder->ExchangeInstID;
		out["TraderID"] = pErrorConditionalOrder->TraderID;
		out["TradingDay"] = pErrorConditionalOrder->TradingDay;
		out["OrderSysID"] = pErrorConditionalOrder->OrderSysID;
		out["InsertDate"] = pErrorConditionalOrder->InsertDate;
		out["InsertTime"] = pErrorConditionalOrder->InsertTime;
		out["ActiveTime"] = pErrorConditionalOrder->ActiveTime;
		out["SuspendTime"] = pErrorConditionalOrder->SuspendTime;
		out["UpdateTime"] = pErrorConditionalOrder->UpdateTime;
		out["CancelTime"] = pErrorConditionalOrder->CancelTime;
		out["ActiveTraderID"] = pErrorConditionalOrder->ActiveTraderID;
		out["ClearingPartID"] = pErrorConditionalOrder->ClearingPartID;
		out["UserProductInfo"] = pErrorConditionalOrder->UserProductInfo;
		out["StatusMsg"] = pErrorConditionalOrder->StatusMsg;
		out["ActiveUserID"] = pErrorConditionalOrder->ActiveUserID;
		out["RelativeOrderSysID"] = pErrorConditionalOrder->RelativeOrderSysID;
		out["ErrorMsg"] = pErrorConditionalOrder->ErrorMsg;
		out["BranchID"] = pErrorConditionalOrder->BranchID;
		out["InvestUnitID"] = pErrorConditionalOrder->InvestUnitID;
		out["AccountID"] = pErrorConditionalOrder->AccountID;
		out["CurrencyID"] = pErrorConditionalOrder->CurrencyID;
		out["IPAddress"] = pErrorConditionalOrder->IPAddress;
		out["MacAddress"] = pErrorConditionalOrder->MacAddress;
		out["VolumeTotalOriginal"] = pErrorConditionalOrder->VolumeTotalOriginal;
		out["MinVolume"] = pErrorConditionalOrder->MinVolume;
		out["IsAutoSuspend"] = pErrorConditionalOrder->IsAutoSuspend;
		out["RequestID"] = pErrorConditionalOrder->RequestID;
		out["InstallID"] = pErrorConditionalOrder->InstallID;
		out["NotifySequence"] = pErrorConditionalOrder->NotifySequence;
		out["SettlementID"] = pErrorConditionalOrder->SettlementID;
		out["VolumeTraded"] = pErrorConditionalOrder->VolumeTraded;
		out["VolumeTotal"] = pErrorConditionalOrder->VolumeTotal;
		out["SequenceNo"] = pErrorConditionalOrder->SequenceNo;
		out["FrontID"] = pErrorConditionalOrder->FrontID;
		out["SessionID"] = pErrorConditionalOrder->SessionID;
		out["UserForceClose"] = pErrorConditionalOrder->UserForceClose;
		out["BrokerOrderSeq"] = pErrorConditionalOrder->BrokerOrderSeq;
		out["ZCETotalTradedVolume"] = pErrorConditionalOrder->ZCETotalTradedVolume;
		out["ErrorID"] = pErrorConditionalOrder->ErrorID;
		out["IsSwapOrder"] = pErrorConditionalOrder->IsSwapOrder;
		out["OrderPriceType"] = pErrorConditionalOrder->OrderPriceType;
		out["Direction"] = pErrorConditionalOrder->Direction;
		out["TimeCondition"] = pErrorConditionalOrder->TimeCondition;
		out["VolumeCondition"] = pErrorConditionalOrder->VolumeCondition;
		out["ContingentCondition"] = pErrorConditionalOrder->ContingentCondition;
		out["ForceCloseReason"] = pErrorConditionalOrder->ForceCloseReason;
		out["OrderSubmitStatus"] = pErrorConditionalOrder->OrderSubmitStatus;
		out["OrderSource"] = pErrorConditionalOrder->OrderSource;
		out["OrderStatus"] = pErrorConditionalOrder->OrderStatus;
		out["OrderType"] = pErrorConditionalOrder->OrderType;
		out["LimitPrice"] = pErrorConditionalOrder->LimitPrice;
		out["StopPrice"] = pErrorConditionalOrder->StopPrice;
	}
	jnaResCallback("T_OnRtnErrorConditionalOrder", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnExecOrder(CThostFtdcExecOrderField* pExecOrder)
{
	Json::Value out;
	if (pExecOrder)
	{
		out["BrokerID"] = pExecOrder->BrokerID;
		out["InvestorID"] = pExecOrder->InvestorID;
		out["InstrumentID"] = pExecOrder->InstrumentID;
		out["ExecOrderRef"] = pExecOrder->ExecOrderRef;
		out["UserID"] = pExecOrder->UserID;
		out["BusinessUnit"] = pExecOrder->BusinessUnit;
		out["ExecOrderLocalID"] = pExecOrder->ExecOrderLocalID;
		out["ExchangeID"] = pExecOrder->ExchangeID;
		out["ParticipantID"] = pExecOrder->ParticipantID;
		out["ClientID"] = pExecOrder->ClientID;
		out["ExchangeInstID"] = pExecOrder->ExchangeInstID;
		out["TraderID"] = pExecOrder->TraderID;
		out["TradingDay"] = pExecOrder->TradingDay;
		out["ExecOrderSysID"] = pExecOrder->ExecOrderSysID;
		out["InsertDate"] = pExecOrder->InsertDate;
		out["InsertTime"] = pExecOrder->InsertTime;
		out["CancelTime"] = pExecOrder->CancelTime;
		out["ClearingPartID"] = pExecOrder->ClearingPartID;
		out["UserProductInfo"] = pExecOrder->UserProductInfo;
		out["StatusMsg"] = pExecOrder->StatusMsg;
		out["ActiveUserID"] = pExecOrder->ActiveUserID;
		out["BranchID"] = pExecOrder->BranchID;
		out["InvestUnitID"] = pExecOrder->InvestUnitID;
		out["AccountID"] = pExecOrder->AccountID;
		out["CurrencyID"] = pExecOrder->CurrencyID;
		out["IPAddress"] = pExecOrder->IPAddress;
		out["MacAddress"] = pExecOrder->MacAddress;
		out["Volume"] = pExecOrder->Volume;
		out["RequestID"] = pExecOrder->RequestID;
		out["InstallID"] = pExecOrder->InstallID;
		out["NotifySequence"] = pExecOrder->NotifySequence;
		out["SettlementID"] = pExecOrder->SettlementID;
		out["SequenceNo"] = pExecOrder->SequenceNo;
		out["FrontID"] = pExecOrder->FrontID;
		out["SessionID"] = pExecOrder->SessionID;
		out["BrokerExecOrderSeq"] = pExecOrder->BrokerExecOrderSeq;
		out["OffsetFlag"] = pExecOrder->OffsetFlag;
		out["HedgeFlag"] = pExecOrder->HedgeFlag;
		out["ActionType"] = pExecOrder->ActionType;
		out["PosiDirection"] = pExecOrder->PosiDirection;
		out["ReservePositionFlag"] = pExecOrder->ReservePositionFlag;
		out["CloseFlag"] = pExecOrder->CloseFlag;
		out["OrderSubmitStatus"] = pExecOrder->OrderSubmitStatus;
		out["ExecResult"] = pExecOrder->ExecResult;
	}
	jnaResCallback("T_OnRtnExecOrder", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnExecOrderInsert(CThostFtdcInputExecOrderField* pInputExecOrder, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pInputExecOrder)
	{
		out["BrokerID"] = pInputExecOrder->BrokerID;
		out["InvestorID"] = pInputExecOrder->InvestorID;
		out["InstrumentID"] = pInputExecOrder->InstrumentID;
		out["ExecOrderRef"] = pInputExecOrder->ExecOrderRef;
		out["UserID"] = pInputExecOrder->UserID;
		out["BusinessUnit"] = pInputExecOrder->BusinessUnit;
		out["ExchangeID"] = pInputExecOrder->ExchangeID;
		out["InvestUnitID"] = pInputExecOrder->InvestUnitID;
		out["AccountID"] = pInputExecOrder->AccountID;
		out["CurrencyID"] = pInputExecOrder->CurrencyID;
		out["ClientID"] = pInputExecOrder->ClientID;
		out["IPAddress"] = pInputExecOrder->IPAddress;
		out["MacAddress"] = pInputExecOrder->MacAddress;
		out["Volume"] = pInputExecOrder->Volume;
		out["RequestID"] = pInputExecOrder->RequestID;
		out["OffsetFlag"] = pInputExecOrder->OffsetFlag;
		out["HedgeFlag"] = pInputExecOrder->HedgeFlag;
		out["ActionType"] = pInputExecOrder->ActionType;
		out["PosiDirection"] = pInputExecOrder->PosiDirection;
		out["ReservePositionFlag"] = pInputExecOrder->ReservePositionFlag;
		out["CloseFlag"] = pInputExecOrder->CloseFlag;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnExecOrderInsert", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnExecOrderAction(CThostFtdcExecOrderActionField* pExecOrderAction, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pExecOrderAction)
	{
		out["BrokerID"] = pExecOrderAction->BrokerID;
		out["InvestorID"] = pExecOrderAction->InvestorID;
		out["ExecOrderRef"] = pExecOrderAction->ExecOrderRef;
		out["ExchangeID"] = pExecOrderAction->ExchangeID;
		out["ExecOrderSysID"] = pExecOrderAction->ExecOrderSysID;
		out["ActionDate"] = pExecOrderAction->ActionDate;
		out["ActionTime"] = pExecOrderAction->ActionTime;
		out["TraderID"] = pExecOrderAction->TraderID;
		out["ExecOrderLocalID"] = pExecOrderAction->ExecOrderLocalID;
		out["ActionLocalID"] = pExecOrderAction->ActionLocalID;
		out["ParticipantID"] = pExecOrderAction->ParticipantID;
		out["ClientID"] = pExecOrderAction->ClientID;
		out["BusinessUnit"] = pExecOrderAction->BusinessUnit;
		out["UserID"] = pExecOrderAction->UserID;
		out["StatusMsg"] = pExecOrderAction->StatusMsg;
		out["InstrumentID"] = pExecOrderAction->InstrumentID;
		out["BranchID"] = pExecOrderAction->BranchID;
		out["InvestUnitID"] = pExecOrderAction->InvestUnitID;
		out["IPAddress"] = pExecOrderAction->IPAddress;
		out["MacAddress"] = pExecOrderAction->MacAddress;
		out["ExecOrderActionRef"] = pExecOrderAction->ExecOrderActionRef;
		out["RequestID"] = pExecOrderAction->RequestID;
		out["FrontID"] = pExecOrderAction->FrontID;
		out["SessionID"] = pExecOrderAction->SessionID;
		out["InstallID"] = pExecOrderAction->InstallID;
		out["ActionFlag"] = pExecOrderAction->ActionFlag;
		out["OrderActionStatus"] = pExecOrderAction->OrderActionStatus;
		out["ActionType"] = pExecOrderAction->ActionType;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnExecOrderAction", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnForQuoteInsert(CThostFtdcInputForQuoteField* pInputForQuote, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pInputForQuote)
	{
		out["BrokerID"] = pInputForQuote->BrokerID;
		out["InvestorID"] = pInputForQuote->InvestorID;
		out["InstrumentID"] = pInputForQuote->InstrumentID;
		out["ForQuoteRef"] = pInputForQuote->ForQuoteRef;
		out["UserID"] = pInputForQuote->UserID;
		out["ExchangeID"] = pInputForQuote->ExchangeID;
		out["InvestUnitID"] = pInputForQuote->InvestUnitID;
		out["IPAddress"] = pInputForQuote->IPAddress;
		out["MacAddress"] = pInputForQuote->MacAddress;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnForQuoteInsert", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnQuote(CThostFtdcQuoteField* pQuote)
{
	Json::Value out;
	if (pQuote)
	{
		out["BrokerID"] = pQuote->BrokerID;
		out["InvestorID"] = pQuote->InvestorID;
		out["InstrumentID"] = pQuote->InstrumentID;
		out["QuoteRef"] = pQuote->QuoteRef;
		out["UserID"] = pQuote->UserID;
		out["BusinessUnit"] = pQuote->BusinessUnit;
		out["QuoteLocalID"] = pQuote->QuoteLocalID;
		out["ExchangeID"] = pQuote->ExchangeID;
		out["ParticipantID"] = pQuote->ParticipantID;
		out["ClientID"] = pQuote->ClientID;
		out["ExchangeInstID"] = pQuote->ExchangeInstID;
		out["TraderID"] = pQuote->TraderID;
		out["TradingDay"] = pQuote->TradingDay;
		out["QuoteSysID"] = pQuote->QuoteSysID;
		out["InsertDate"] = pQuote->InsertDate;
		out["InsertTime"] = pQuote->InsertTime;
		out["CancelTime"] = pQuote->CancelTime;
		out["ClearingPartID"] = pQuote->ClearingPartID;
		out["AskOrderSysID"] = pQuote->AskOrderSysID;
		out["BidOrderSysID"] = pQuote->BidOrderSysID;
		out["UserProductInfo"] = pQuote->UserProductInfo;
		out["StatusMsg"] = pQuote->StatusMsg;
		out["ActiveUserID"] = pQuote->ActiveUserID;
		out["AskOrderRef"] = pQuote->AskOrderRef;
		out["BidOrderRef"] = pQuote->BidOrderRef;
		out["ForQuoteSysID"] = pQuote->ForQuoteSysID;
		out["BranchID"] = pQuote->BranchID;
		out["InvestUnitID"] = pQuote->InvestUnitID;
		out["AccountID"] = pQuote->AccountID;
		out["CurrencyID"] = pQuote->CurrencyID;
		out["IPAddress"] = pQuote->IPAddress;
		out["MacAddress"] = pQuote->MacAddress;
		out["AskVolume"] = pQuote->AskVolume;
		out["BidVolume"] = pQuote->BidVolume;
		out["RequestID"] = pQuote->RequestID;
		out["InstallID"] = pQuote->InstallID;
		out["NotifySequence"] = pQuote->NotifySequence;
		out["SettlementID"] = pQuote->SettlementID;
		out["SequenceNo"] = pQuote->SequenceNo;
		out["FrontID"] = pQuote->FrontID;
		out["SessionID"] = pQuote->SessionID;
		out["BrokerQuoteSeq"] = pQuote->BrokerQuoteSeq;
		out["AskOffsetFlag"] = pQuote->AskOffsetFlag;
		out["BidOffsetFlag"] = pQuote->BidOffsetFlag;
		out["AskHedgeFlag"] = pQuote->AskHedgeFlag;
		out["BidHedgeFlag"] = pQuote->BidHedgeFlag;
		out["OrderSubmitStatus"] = pQuote->OrderSubmitStatus;
		out["QuoteStatus"] = pQuote->QuoteStatus;
		out["AskPrice"] = pQuote->AskPrice;
		out["BidPrice"] = pQuote->BidPrice;
	}
	jnaResCallback("T_OnRtnQuote", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnQuoteInsert(CThostFtdcInputQuoteField* pInputQuote, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pInputQuote)
	{
		out["BrokerID"] = pInputQuote->BrokerID;
		out["InvestorID"] = pInputQuote->InvestorID;
		out["InstrumentID"] = pInputQuote->InstrumentID;
		out["QuoteRef"] = pInputQuote->QuoteRef;
		out["UserID"] = pInputQuote->UserID;
		out["BusinessUnit"] = pInputQuote->BusinessUnit;
		out["AskOrderRef"] = pInputQuote->AskOrderRef;
		out["BidOrderRef"] = pInputQuote->BidOrderRef;
		out["ForQuoteSysID"] = pInputQuote->ForQuoteSysID;
		out["ExchangeID"] = pInputQuote->ExchangeID;
		out["InvestUnitID"] = pInputQuote->InvestUnitID;
		out["ClientID"] = pInputQuote->ClientID;
		out["IPAddress"] = pInputQuote->IPAddress;
		out["MacAddress"] = pInputQuote->MacAddress;
		out["AskVolume"] = pInputQuote->AskVolume;
		out["BidVolume"] = pInputQuote->BidVolume;
		out["RequestID"] = pInputQuote->RequestID;
		out["AskOffsetFlag"] = pInputQuote->AskOffsetFlag;
		out["BidOffsetFlag"] = pInputQuote->BidOffsetFlag;
		out["AskHedgeFlag"] = pInputQuote->AskHedgeFlag;
		out["BidHedgeFlag"] = pInputQuote->BidHedgeFlag;
		out["AskPrice"] = pInputQuote->AskPrice;
		out["BidPrice"] = pInputQuote->BidPrice;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnQuoteInsert", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnQuoteAction(CThostFtdcQuoteActionField* pQuoteAction, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pQuoteAction)
	{
		out["BrokerID"] = pQuoteAction->BrokerID;
		out["InvestorID"] = pQuoteAction->InvestorID;
		out["QuoteRef"] = pQuoteAction->QuoteRef;
		out["ExchangeID"] = pQuoteAction->ExchangeID;
		out["QuoteSysID"] = pQuoteAction->QuoteSysID;
		out["ActionDate"] = pQuoteAction->ActionDate;
		out["ActionTime"] = pQuoteAction->ActionTime;
		out["TraderID"] = pQuoteAction->TraderID;
		out["QuoteLocalID"] = pQuoteAction->QuoteLocalID;
		out["ActionLocalID"] = pQuoteAction->ActionLocalID;
		out["ParticipantID"] = pQuoteAction->ParticipantID;
		out["ClientID"] = pQuoteAction->ClientID;
		out["BusinessUnit"] = pQuoteAction->BusinessUnit;
		out["UserID"] = pQuoteAction->UserID;
		out["StatusMsg"] = pQuoteAction->StatusMsg;
		out["InstrumentID"] = pQuoteAction->InstrumentID;
		out["BranchID"] = pQuoteAction->BranchID;
		out["InvestUnitID"] = pQuoteAction->InvestUnitID;
		out["IPAddress"] = pQuoteAction->IPAddress;
		out["MacAddress"] = pQuoteAction->MacAddress;
		out["QuoteActionRef"] = pQuoteAction->QuoteActionRef;
		out["RequestID"] = pQuoteAction->RequestID;
		out["FrontID"] = pQuoteAction->FrontID;
		out["SessionID"] = pQuoteAction->SessionID;
		out["InstallID"] = pQuoteAction->InstallID;
		out["ActionFlag"] = pQuoteAction->ActionFlag;
		out["OrderActionStatus"] = pQuoteAction->OrderActionStatus;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnQuoteAction", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnForQuoteRsp(CThostFtdcForQuoteRspField* pForQuoteRsp)
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
	jnaResCallback("T_OnRtnForQuoteRsp", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnCFMMCTradingAccountToken(CThostFtdcCFMMCTradingAccountTokenField* pCFMMCTradingAccountToken)
{
	Json::Value out;
	if (pCFMMCTradingAccountToken)
	{
		out["BrokerID"] = pCFMMCTradingAccountToken->BrokerID;
		out["ParticipantID"] = pCFMMCTradingAccountToken->ParticipantID;
		out["AccountID"] = pCFMMCTradingAccountToken->AccountID;
		out["Token"] = pCFMMCTradingAccountToken->Token;
		out["KeyID"] = pCFMMCTradingAccountToken->KeyID;
	}
	jnaResCallback("T_OnRtnCFMMCTradingAccountToken", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnBatchOrderAction(CThostFtdcBatchOrderActionField* pBatchOrderAction, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pBatchOrderAction)
	{
		out["BrokerID"] = pBatchOrderAction->BrokerID;
		out["InvestorID"] = pBatchOrderAction->InvestorID;
		out["ExchangeID"] = pBatchOrderAction->ExchangeID;
		out["ActionDate"] = pBatchOrderAction->ActionDate;
		out["ActionTime"] = pBatchOrderAction->ActionTime;
		out["TraderID"] = pBatchOrderAction->TraderID;
		out["ActionLocalID"] = pBatchOrderAction->ActionLocalID;
		out["ParticipantID"] = pBatchOrderAction->ParticipantID;
		out["ClientID"] = pBatchOrderAction->ClientID;
		out["BusinessUnit"] = pBatchOrderAction->BusinessUnit;
		out["UserID"] = pBatchOrderAction->UserID;
		out["StatusMsg"] = pBatchOrderAction->StatusMsg;
		out["InvestUnitID"] = pBatchOrderAction->InvestUnitID;
		out["IPAddress"] = pBatchOrderAction->IPAddress;
		out["MacAddress"] = pBatchOrderAction->MacAddress;
		out["OrderActionRef"] = pBatchOrderAction->OrderActionRef;
		out["RequestID"] = pBatchOrderAction->RequestID;
		out["FrontID"] = pBatchOrderAction->FrontID;
		out["SessionID"] = pBatchOrderAction->SessionID;
		out["InstallID"] = pBatchOrderAction->InstallID;
		out["OrderActionStatus"] = pBatchOrderAction->OrderActionStatus;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnBatchOrderAction", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnOptionSelfClose(CThostFtdcOptionSelfCloseField* pOptionSelfClose)
{
	Json::Value out;
	if (pOptionSelfClose)
	{
		out["BrokerID"] = pOptionSelfClose->BrokerID;
		out["InvestorID"] = pOptionSelfClose->InvestorID;
		out["InstrumentID"] = pOptionSelfClose->InstrumentID;
		out["OptionSelfCloseRef"] = pOptionSelfClose->OptionSelfCloseRef;
		out["UserID"] = pOptionSelfClose->UserID;
		out["BusinessUnit"] = pOptionSelfClose->BusinessUnit;
		out["OptionSelfCloseLocalID"] = pOptionSelfClose->OptionSelfCloseLocalID;
		out["ExchangeID"] = pOptionSelfClose->ExchangeID;
		out["ParticipantID"] = pOptionSelfClose->ParticipantID;
		out["ClientID"] = pOptionSelfClose->ClientID;
		out["ExchangeInstID"] = pOptionSelfClose->ExchangeInstID;
		out["TraderID"] = pOptionSelfClose->TraderID;
		out["TradingDay"] = pOptionSelfClose->TradingDay;
		out["OptionSelfCloseSysID"] = pOptionSelfClose->OptionSelfCloseSysID;
		out["InsertDate"] = pOptionSelfClose->InsertDate;
		out["InsertTime"] = pOptionSelfClose->InsertTime;
		out["CancelTime"] = pOptionSelfClose->CancelTime;
		out["ClearingPartID"] = pOptionSelfClose->ClearingPartID;
		out["UserProductInfo"] = pOptionSelfClose->UserProductInfo;
		out["StatusMsg"] = pOptionSelfClose->StatusMsg;
		out["ActiveUserID"] = pOptionSelfClose->ActiveUserID;
		out["BranchID"] = pOptionSelfClose->BranchID;
		out["InvestUnitID"] = pOptionSelfClose->InvestUnitID;
		out["AccountID"] = pOptionSelfClose->AccountID;
		out["CurrencyID"] = pOptionSelfClose->CurrencyID;
		out["IPAddress"] = pOptionSelfClose->IPAddress;
		out["MacAddress"] = pOptionSelfClose->MacAddress;
		out["Volume"] = pOptionSelfClose->Volume;
		out["RequestID"] = pOptionSelfClose->RequestID;
		out["InstallID"] = pOptionSelfClose->InstallID;
		out["NotifySequence"] = pOptionSelfClose->NotifySequence;
		out["SettlementID"] = pOptionSelfClose->SettlementID;
		out["SequenceNo"] = pOptionSelfClose->SequenceNo;
		out["FrontID"] = pOptionSelfClose->FrontID;
		out["SessionID"] = pOptionSelfClose->SessionID;
		out["BrokerOptionSelfCloseSeq"] = pOptionSelfClose->BrokerOptionSelfCloseSeq;
		out["HedgeFlag"] = pOptionSelfClose->HedgeFlag;
		out["OptSelfCloseFlag"] = pOptionSelfClose->OptSelfCloseFlag;
		out["OrderSubmitStatus"] = pOptionSelfClose->OrderSubmitStatus;
		out["ExecResult"] = pOptionSelfClose->ExecResult;
	}
	jnaResCallback("T_OnRtnOptionSelfClose", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnOptionSelfCloseInsert(CThostFtdcInputOptionSelfCloseField* pInputOptionSelfClose, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pInputOptionSelfClose)
	{
		out["BrokerID"] = pInputOptionSelfClose->BrokerID;
		out["InvestorID"] = pInputOptionSelfClose->InvestorID;
		out["InstrumentID"] = pInputOptionSelfClose->InstrumentID;
		out["OptionSelfCloseRef"] = pInputOptionSelfClose->OptionSelfCloseRef;
		out["UserID"] = pInputOptionSelfClose->UserID;
		out["BusinessUnit"] = pInputOptionSelfClose->BusinessUnit;
		out["ExchangeID"] = pInputOptionSelfClose->ExchangeID;
		out["InvestUnitID"] = pInputOptionSelfClose->InvestUnitID;
		out["AccountID"] = pInputOptionSelfClose->AccountID;
		out["CurrencyID"] = pInputOptionSelfClose->CurrencyID;
		out["ClientID"] = pInputOptionSelfClose->ClientID;
		out["IPAddress"] = pInputOptionSelfClose->IPAddress;
		out["MacAddress"] = pInputOptionSelfClose->MacAddress;
		out["Volume"] = pInputOptionSelfClose->Volume;
		out["RequestID"] = pInputOptionSelfClose->RequestID;
		out["HedgeFlag"] = pInputOptionSelfClose->HedgeFlag;
		out["OptSelfCloseFlag"] = pInputOptionSelfClose->OptSelfCloseFlag;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnOptionSelfCloseInsert", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnOptionSelfCloseAction(CThostFtdcOptionSelfCloseActionField* pOptionSelfCloseAction, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pOptionSelfCloseAction)
	{
		out["BrokerID"] = pOptionSelfCloseAction->BrokerID;
		out["InvestorID"] = pOptionSelfCloseAction->InvestorID;
		out["OptionSelfCloseRef"] = pOptionSelfCloseAction->OptionSelfCloseRef;
		out["ExchangeID"] = pOptionSelfCloseAction->ExchangeID;
		out["OptionSelfCloseSysID"] = pOptionSelfCloseAction->OptionSelfCloseSysID;
		out["ActionDate"] = pOptionSelfCloseAction->ActionDate;
		out["ActionTime"] = pOptionSelfCloseAction->ActionTime;
		out["TraderID"] = pOptionSelfCloseAction->TraderID;
		out["OptionSelfCloseLocalID"] = pOptionSelfCloseAction->OptionSelfCloseLocalID;
		out["ActionLocalID"] = pOptionSelfCloseAction->ActionLocalID;
		out["ParticipantID"] = pOptionSelfCloseAction->ParticipantID;
		out["ClientID"] = pOptionSelfCloseAction->ClientID;
		out["BusinessUnit"] = pOptionSelfCloseAction->BusinessUnit;
		out["UserID"] = pOptionSelfCloseAction->UserID;
		out["StatusMsg"] = pOptionSelfCloseAction->StatusMsg;
		out["InstrumentID"] = pOptionSelfCloseAction->InstrumentID;
		out["BranchID"] = pOptionSelfCloseAction->BranchID;
		out["InvestUnitID"] = pOptionSelfCloseAction->InvestUnitID;
		out["IPAddress"] = pOptionSelfCloseAction->IPAddress;
		out["MacAddress"] = pOptionSelfCloseAction->MacAddress;
		out["OptionSelfCloseActionRef"] = pOptionSelfCloseAction->OptionSelfCloseActionRef;
		out["RequestID"] = pOptionSelfCloseAction->RequestID;
		out["FrontID"] = pOptionSelfCloseAction->FrontID;
		out["SessionID"] = pOptionSelfCloseAction->SessionID;
		out["InstallID"] = pOptionSelfCloseAction->InstallID;
		out["ActionFlag"] = pOptionSelfCloseAction->ActionFlag;
		out["OrderActionStatus"] = pOptionSelfCloseAction->OrderActionStatus;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnOptionSelfCloseAction", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnCombAction(CThostFtdcCombActionField* pCombAction)
{
	Json::Value out;
	if (pCombAction)
	{
		out["BrokerID"] = pCombAction->BrokerID;
		out["InvestorID"] = pCombAction->InvestorID;
		out["InstrumentID"] = pCombAction->InstrumentID;
		out["CombActionRef"] = pCombAction->CombActionRef;
		out["UserID"] = pCombAction->UserID;
		out["ActionLocalID"] = pCombAction->ActionLocalID;
		out["ExchangeID"] = pCombAction->ExchangeID;
		out["ParticipantID"] = pCombAction->ParticipantID;
		out["ClientID"] = pCombAction->ClientID;
		out["ExchangeInstID"] = pCombAction->ExchangeInstID;
		out["TraderID"] = pCombAction->TraderID;
		out["TradingDay"] = pCombAction->TradingDay;
		out["UserProductInfo"] = pCombAction->UserProductInfo;
		out["StatusMsg"] = pCombAction->StatusMsg;
		out["IPAddress"] = pCombAction->IPAddress;
		out["MacAddress"] = pCombAction->MacAddress;
		out["ComTradeID"] = pCombAction->ComTradeID;
		out["BranchID"] = pCombAction->BranchID;
		out["InvestUnitID"] = pCombAction->InvestUnitID;
		out["Volume"] = pCombAction->Volume;
		out["InstallID"] = pCombAction->InstallID;
		out["NotifySequence"] = pCombAction->NotifySequence;
		out["SettlementID"] = pCombAction->SettlementID;
		out["SequenceNo"] = pCombAction->SequenceNo;
		out["FrontID"] = pCombAction->FrontID;
		out["SessionID"] = pCombAction->SessionID;
		out["Direction"] = pCombAction->Direction;
		out["CombDirection"] = pCombAction->CombDirection;
		out["HedgeFlag"] = pCombAction->HedgeFlag;
		out["ActionStatus"] = pCombAction->ActionStatus;
	}
	jnaResCallback("T_OnRtnCombAction", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnCombActionInsert(CThostFtdcInputCombActionField* pInputCombAction, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pInputCombAction)
	{
		out["BrokerID"] = pInputCombAction->BrokerID;
		out["InvestorID"] = pInputCombAction->InvestorID;
		out["InstrumentID"] = pInputCombAction->InstrumentID;
		out["CombActionRef"] = pInputCombAction->CombActionRef;
		out["UserID"] = pInputCombAction->UserID;
		out["ExchangeID"] = pInputCombAction->ExchangeID;
		out["IPAddress"] = pInputCombAction->IPAddress;
		out["MacAddress"] = pInputCombAction->MacAddress;
		out["InvestUnitID"] = pInputCombAction->InvestUnitID;
		out["Volume"] = pInputCombAction->Volume;
		out["Direction"] = pInputCombAction->Direction;
		out["CombDirection"] = pInputCombAction->CombDirection;
		out["HedgeFlag"] = pInputCombAction->HedgeFlag;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnCombActionInsert", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRspQryContractBank(CThostFtdcContractBankField* pContractBank, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pContractBank)
	{
		out["BrokerID"] = pContractBank->BrokerID;
		out["BankID"] = pContractBank->BankID;
		out["BankBrchID"] = pContractBank->BankBrchID;
		out["BankName"] = pContractBank->BankName;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryContractBank", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryParkedOrder(CThostFtdcParkedOrderField* pParkedOrder, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pParkedOrder)
	{
		out["BrokerID"] = pParkedOrder->BrokerID;
		out["InvestorID"] = pParkedOrder->InvestorID;
		out["InstrumentID"] = pParkedOrder->InstrumentID;
		out["OrderRef"] = pParkedOrder->OrderRef;
		out["UserID"] = pParkedOrder->UserID;
		out["CombOffsetFlag"] = pParkedOrder->CombOffsetFlag;
		out["CombHedgeFlag"] = pParkedOrder->CombHedgeFlag;
		out["GTDDate"] = pParkedOrder->GTDDate;
		out["BusinessUnit"] = pParkedOrder->BusinessUnit;
		out["ExchangeID"] = pParkedOrder->ExchangeID;
		out["ParkedOrderID"] = pParkedOrder->ParkedOrderID;
		out["ErrorMsg"] = pParkedOrder->ErrorMsg;
		out["AccountID"] = pParkedOrder->AccountID;
		out["CurrencyID"] = pParkedOrder->CurrencyID;
		out["ClientID"] = pParkedOrder->ClientID;
		out["InvestUnitID"] = pParkedOrder->InvestUnitID;
		out["IPAddress"] = pParkedOrder->IPAddress;
		out["MacAddress"] = pParkedOrder->MacAddress;
		out["VolumeTotalOriginal"] = pParkedOrder->VolumeTotalOriginal;
		out["MinVolume"] = pParkedOrder->MinVolume;
		out["IsAutoSuspend"] = pParkedOrder->IsAutoSuspend;
		out["RequestID"] = pParkedOrder->RequestID;
		out["UserForceClose"] = pParkedOrder->UserForceClose;
		out["ErrorID"] = pParkedOrder->ErrorID;
		out["IsSwapOrder"] = pParkedOrder->IsSwapOrder;
		out["OrderPriceType"] = pParkedOrder->OrderPriceType;
		out["Direction"] = pParkedOrder->Direction;
		out["TimeCondition"] = pParkedOrder->TimeCondition;
		out["VolumeCondition"] = pParkedOrder->VolumeCondition;
		out["ContingentCondition"] = pParkedOrder->ContingentCondition;
		out["ForceCloseReason"] = pParkedOrder->ForceCloseReason;
		out["UserType"] = pParkedOrder->UserType;
		out["Status"] = pParkedOrder->Status;
		out["LimitPrice"] = pParkedOrder->LimitPrice;
		out["StopPrice"] = pParkedOrder->StopPrice;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryParkedOrder", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryParkedOrderAction(CThostFtdcParkedOrderActionField* pParkedOrderAction, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pParkedOrderAction)
	{
		out["BrokerID"] = pParkedOrderAction->BrokerID;
		out["InvestorID"] = pParkedOrderAction->InvestorID;
		out["OrderRef"] = pParkedOrderAction->OrderRef;
		out["ExchangeID"] = pParkedOrderAction->ExchangeID;
		out["OrderSysID"] = pParkedOrderAction->OrderSysID;
		out["UserID"] = pParkedOrderAction->UserID;
		out["InstrumentID"] = pParkedOrderAction->InstrumentID;
		out["ParkedOrderActionID"] = pParkedOrderAction->ParkedOrderActionID;
		out["ErrorMsg"] = pParkedOrderAction->ErrorMsg;
		out["InvestUnitID"] = pParkedOrderAction->InvestUnitID;
		out["IPAddress"] = pParkedOrderAction->IPAddress;
		out["MacAddress"] = pParkedOrderAction->MacAddress;
		out["OrderActionRef"] = pParkedOrderAction->OrderActionRef;
		out["RequestID"] = pParkedOrderAction->RequestID;
		out["FrontID"] = pParkedOrderAction->FrontID;
		out["SessionID"] = pParkedOrderAction->SessionID;
		out["VolumeChange"] = pParkedOrderAction->VolumeChange;
		out["ErrorID"] = pParkedOrderAction->ErrorID;
		out["ActionFlag"] = pParkedOrderAction->ActionFlag;
		out["UserType"] = pParkedOrderAction->UserType;
		out["Status"] = pParkedOrderAction->Status;
		out["LimitPrice"] = pParkedOrderAction->LimitPrice;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryParkedOrderAction", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryTradingNotice(CThostFtdcTradingNoticeField* pTradingNotice, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pTradingNotice)
	{
		out["BrokerID"] = pTradingNotice->BrokerID;
		out["InvestorID"] = pTradingNotice->InvestorID;
		out["UserID"] = pTradingNotice->UserID;
		out["SendTime"] = pTradingNotice->SendTime;
		out["FieldContent"] = pTradingNotice->FieldContent;
		out["InvestUnitID"] = pTradingNotice->InvestUnitID;
		out["SequenceNo"] = pTradingNotice->SequenceNo;
		out["InvestorRange"] = pTradingNotice->InvestorRange;
		out["SequenceSeries"] = pTradingNotice->SequenceSeries;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryTradingNotice", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryBrokerTradingParams(CThostFtdcBrokerTradingParamsField* pBrokerTradingParams, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pBrokerTradingParams)
	{
		out["BrokerID"] = pBrokerTradingParams->BrokerID;
		out["InvestorID"] = pBrokerTradingParams->InvestorID;
		out["CurrencyID"] = pBrokerTradingParams->CurrencyID;
		out["AccountID"] = pBrokerTradingParams->AccountID;
		out["MarginPriceType"] = pBrokerTradingParams->MarginPriceType;
		out["Algorithm"] = pBrokerTradingParams->Algorithm;
		out["AvailIncludeCloseProfit"] = pBrokerTradingParams->AvailIncludeCloseProfit;
		out["OptionRoyaltyPriceType"] = pBrokerTradingParams->OptionRoyaltyPriceType;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryBrokerTradingParams", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQryBrokerTradingAlgos(CThostFtdcBrokerTradingAlgosField* pBrokerTradingAlgos, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pBrokerTradingAlgos)
	{
		out["BrokerID"] = pBrokerTradingAlgos->BrokerID;
		out["ExchangeID"] = pBrokerTradingAlgos->ExchangeID;
		out["InstrumentID"] = pBrokerTradingAlgos->InstrumentID;
		out["HandlePositionAlgoID"] = pBrokerTradingAlgos->HandlePositionAlgoID;
		out["FindMarginRateAlgoID"] = pBrokerTradingAlgos->FindMarginRateAlgoID;
		out["HandleTradingAccountAlgoID"] = pBrokerTradingAlgos->HandleTradingAccountAlgoID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryBrokerTradingAlgos", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQueryCFMMCTradingAccountToken(CThostFtdcQueryCFMMCTradingAccountTokenField* pQueryCFMMCTradingAccountToken, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pQueryCFMMCTradingAccountToken)
	{
		out["BrokerID"] = pQueryCFMMCTradingAccountToken->BrokerID;
		out["InvestorID"] = pQueryCFMMCTradingAccountToken->InvestorID;
		out["InvestUnitID"] = pQueryCFMMCTradingAccountToken->InvestUnitID;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQueryCFMMCTradingAccountToken", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRtnFromBankToFutureByBank(CThostFtdcRspTransferField* pRspTransfer)
{
	Json::Value out;
	if (pRspTransfer)
	{
		out["TradeCode"] = pRspTransfer->TradeCode;
		out["BankID"] = pRspTransfer->BankID;
		out["BankBranchID"] = pRspTransfer->BankBranchID;
		out["BrokerID"] = pRspTransfer->BrokerID;
		out["BrokerBranchID"] = pRspTransfer->BrokerBranchID;
		out["TradeDate"] = pRspTransfer->TradeDate;
		out["TradeTime"] = pRspTransfer->TradeTime;
		out["BankSerial"] = pRspTransfer->BankSerial;
		out["TradingDay"] = pRspTransfer->TradingDay;
		out["CustomerName"] = pRspTransfer->CustomerName;
		out["IdentifiedCardNo"] = pRspTransfer->IdentifiedCardNo;
		out["BankAccount"] = pRspTransfer->BankAccount;
		out["BankPassWord"] = pRspTransfer->BankPassWord;
		out["AccountID"] = pRspTransfer->AccountID;
		out["Password"] = pRspTransfer->Password;
		out["UserID"] = pRspTransfer->UserID;
		out["CurrencyID"] = pRspTransfer->CurrencyID;
		out["Message"] = pRspTransfer->Message;
		out["Digest"] = pRspTransfer->Digest;
		out["DeviceID"] = pRspTransfer->DeviceID;
		out["BrokerIDByBank"] = pRspTransfer->BrokerIDByBank;
		out["BankSecuAcc"] = pRspTransfer->BankSecuAcc;
		out["OperNo"] = pRspTransfer->OperNo;
		out["ErrorMsg"] = pRspTransfer->ErrorMsg;
		out["LongCustomerName"] = pRspTransfer->LongCustomerName;
		out["PlateSerial"] = pRspTransfer->PlateSerial;
		out["SessionID"] = pRspTransfer->SessionID;
		out["InstallID"] = pRspTransfer->InstallID;
		out["FutureSerial"] = pRspTransfer->FutureSerial;
		out["RequestID"] = pRspTransfer->RequestID;
		out["TID"] = pRspTransfer->TID;
		out["ErrorID"] = pRspTransfer->ErrorID;
		out["LastFragment"] = pRspTransfer->LastFragment;
		out["IdCardType"] = pRspTransfer->IdCardType;
		out["CustType"] = pRspTransfer->CustType;
		out["VerifyCertNoFlag"] = pRspTransfer->VerifyCertNoFlag;
		out["FeePayFlag"] = pRspTransfer->FeePayFlag;
		out["BankAccType"] = pRspTransfer->BankAccType;
		out["BankSecuAccType"] = pRspTransfer->BankSecuAccType;
		out["BankPwdFlag"] = pRspTransfer->BankPwdFlag;
		out["SecuPwdFlag"] = pRspTransfer->SecuPwdFlag;
		out["TransferStatus"] = pRspTransfer->TransferStatus;
		out["TradeAmount"] = pRspTransfer->TradeAmount;
		out["FutureFetchAmount"] = pRspTransfer->FutureFetchAmount;
		out["CustFee"] = pRspTransfer->CustFee;
		out["BrokerFee"] = pRspTransfer->BrokerFee;
	}
	jnaResCallback("T_OnRtnFromBankToFutureByBank", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnFromFutureToBankByBank(CThostFtdcRspTransferField* pRspTransfer)
{
	Json::Value out;
	if (pRspTransfer)
	{
		out["TradeCode"] = pRspTransfer->TradeCode;
		out["BankID"] = pRspTransfer->BankID;
		out["BankBranchID"] = pRspTransfer->BankBranchID;
		out["BrokerID"] = pRspTransfer->BrokerID;
		out["BrokerBranchID"] = pRspTransfer->BrokerBranchID;
		out["TradeDate"] = pRspTransfer->TradeDate;
		out["TradeTime"] = pRspTransfer->TradeTime;
		out["BankSerial"] = pRspTransfer->BankSerial;
		out["TradingDay"] = pRspTransfer->TradingDay;
		out["CustomerName"] = pRspTransfer->CustomerName;
		out["IdentifiedCardNo"] = pRspTransfer->IdentifiedCardNo;
		out["BankAccount"] = pRspTransfer->BankAccount;
		out["BankPassWord"] = pRspTransfer->BankPassWord;
		out["AccountID"] = pRspTransfer->AccountID;
		out["Password"] = pRspTransfer->Password;
		out["UserID"] = pRspTransfer->UserID;
		out["CurrencyID"] = pRspTransfer->CurrencyID;
		out["Message"] = pRspTransfer->Message;
		out["Digest"] = pRspTransfer->Digest;
		out["DeviceID"] = pRspTransfer->DeviceID;
		out["BrokerIDByBank"] = pRspTransfer->BrokerIDByBank;
		out["BankSecuAcc"] = pRspTransfer->BankSecuAcc;
		out["OperNo"] = pRspTransfer->OperNo;
		out["ErrorMsg"] = pRspTransfer->ErrorMsg;
		out["LongCustomerName"] = pRspTransfer->LongCustomerName;
		out["PlateSerial"] = pRspTransfer->PlateSerial;
		out["SessionID"] = pRspTransfer->SessionID;
		out["InstallID"] = pRspTransfer->InstallID;
		out["FutureSerial"] = pRspTransfer->FutureSerial;
		out["RequestID"] = pRspTransfer->RequestID;
		out["TID"] = pRspTransfer->TID;
		out["ErrorID"] = pRspTransfer->ErrorID;
		out["LastFragment"] = pRspTransfer->LastFragment;
		out["IdCardType"] = pRspTransfer->IdCardType;
		out["CustType"] = pRspTransfer->CustType;
		out["VerifyCertNoFlag"] = pRspTransfer->VerifyCertNoFlag;
		out["FeePayFlag"] = pRspTransfer->FeePayFlag;
		out["BankAccType"] = pRspTransfer->BankAccType;
		out["BankSecuAccType"] = pRspTransfer->BankSecuAccType;
		out["BankPwdFlag"] = pRspTransfer->BankPwdFlag;
		out["SecuPwdFlag"] = pRspTransfer->SecuPwdFlag;
		out["TransferStatus"] = pRspTransfer->TransferStatus;
		out["TradeAmount"] = pRspTransfer->TradeAmount;
		out["FutureFetchAmount"] = pRspTransfer->FutureFetchAmount;
		out["CustFee"] = pRspTransfer->CustFee;
		out["BrokerFee"] = pRspTransfer->BrokerFee;
	}
	jnaResCallback("T_OnRtnFromFutureToBankByBank", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnRepealFromBankToFutureByBank(CThostFtdcRspRepealField* pRspRepeal)
{
	Json::Value out;
	if (pRspRepeal)
	{
		out["BankRepealSerial"] = pRspRepeal->BankRepealSerial;
		out["TradeCode"] = pRspRepeal->TradeCode;
		out["BankID"] = pRspRepeal->BankID;
		out["BankBranchID"] = pRspRepeal->BankBranchID;
		out["BrokerID"] = pRspRepeal->BrokerID;
		out["BrokerBranchID"] = pRspRepeal->BrokerBranchID;
		out["TradeDate"] = pRspRepeal->TradeDate;
		out["TradeTime"] = pRspRepeal->TradeTime;
		out["BankSerial"] = pRspRepeal->BankSerial;
		out["TradingDay"] = pRspRepeal->TradingDay;
		out["CustomerName"] = pRspRepeal->CustomerName;
		out["IdentifiedCardNo"] = pRspRepeal->IdentifiedCardNo;
		out["BankAccount"] = pRspRepeal->BankAccount;
		out["BankPassWord"] = pRspRepeal->BankPassWord;
		out["AccountID"] = pRspRepeal->AccountID;
		out["Password"] = pRspRepeal->Password;
		out["UserID"] = pRspRepeal->UserID;
		out["CurrencyID"] = pRspRepeal->CurrencyID;
		out["Message"] = pRspRepeal->Message;
		out["Digest"] = pRspRepeal->Digest;
		out["DeviceID"] = pRspRepeal->DeviceID;
		out["BrokerIDByBank"] = pRspRepeal->BrokerIDByBank;
		out["BankSecuAcc"] = pRspRepeal->BankSecuAcc;
		out["OperNo"] = pRspRepeal->OperNo;
		out["ErrorMsg"] = pRspRepeal->ErrorMsg;
		out["LongCustomerName"] = pRspRepeal->LongCustomerName;
		out["RepealTimeInterval"] = pRspRepeal->RepealTimeInterval;
		out["RepealedTimes"] = pRspRepeal->RepealedTimes;
		out["PlateRepealSerial"] = pRspRepeal->PlateRepealSerial;
		out["FutureRepealSerial"] = pRspRepeal->FutureRepealSerial;
		out["PlateSerial"] = pRspRepeal->PlateSerial;
		out["SessionID"] = pRspRepeal->SessionID;
		out["InstallID"] = pRspRepeal->InstallID;
		out["FutureSerial"] = pRspRepeal->FutureSerial;
		out["RequestID"] = pRspRepeal->RequestID;
		out["TID"] = pRspRepeal->TID;
		out["ErrorID"] = pRspRepeal->ErrorID;
		out["BankRepealFlag"] = pRspRepeal->BankRepealFlag;
		out["BrokerRepealFlag"] = pRspRepeal->BrokerRepealFlag;
		out["LastFragment"] = pRspRepeal->LastFragment;
		out["IdCardType"] = pRspRepeal->IdCardType;
		out["CustType"] = pRspRepeal->CustType;
		out["VerifyCertNoFlag"] = pRspRepeal->VerifyCertNoFlag;
		out["FeePayFlag"] = pRspRepeal->FeePayFlag;
		out["BankAccType"] = pRspRepeal->BankAccType;
		out["BankSecuAccType"] = pRspRepeal->BankSecuAccType;
		out["BankPwdFlag"] = pRspRepeal->BankPwdFlag;
		out["SecuPwdFlag"] = pRspRepeal->SecuPwdFlag;
		out["TransferStatus"] = pRspRepeal->TransferStatus;
		out["TradeAmount"] = pRspRepeal->TradeAmount;
		out["FutureFetchAmount"] = pRspRepeal->FutureFetchAmount;
		out["CustFee"] = pRspRepeal->CustFee;
		out["BrokerFee"] = pRspRepeal->BrokerFee;
	}
	jnaResCallback("T_OnRtnRepealFromBankToFutureByBank", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnRepealFromFutureToBankByBank(CThostFtdcRspRepealField* pRspRepeal)
{
	Json::Value out;
	if (pRspRepeal)
	{
		out["BankRepealSerial"] = pRspRepeal->BankRepealSerial;
		out["TradeCode"] = pRspRepeal->TradeCode;
		out["BankID"] = pRspRepeal->BankID;
		out["BankBranchID"] = pRspRepeal->BankBranchID;
		out["BrokerID"] = pRspRepeal->BrokerID;
		out["BrokerBranchID"] = pRspRepeal->BrokerBranchID;
		out["TradeDate"] = pRspRepeal->TradeDate;
		out["TradeTime"] = pRspRepeal->TradeTime;
		out["BankSerial"] = pRspRepeal->BankSerial;
		out["TradingDay"] = pRspRepeal->TradingDay;
		out["CustomerName"] = pRspRepeal->CustomerName;
		out["IdentifiedCardNo"] = pRspRepeal->IdentifiedCardNo;
		out["BankAccount"] = pRspRepeal->BankAccount;
		out["BankPassWord"] = pRspRepeal->BankPassWord;
		out["AccountID"] = pRspRepeal->AccountID;
		out["Password"] = pRspRepeal->Password;
		out["UserID"] = pRspRepeal->UserID;
		out["CurrencyID"] = pRspRepeal->CurrencyID;
		out["Message"] = pRspRepeal->Message;
		out["Digest"] = pRspRepeal->Digest;
		out["DeviceID"] = pRspRepeal->DeviceID;
		out["BrokerIDByBank"] = pRspRepeal->BrokerIDByBank;
		out["BankSecuAcc"] = pRspRepeal->BankSecuAcc;
		out["OperNo"] = pRspRepeal->OperNo;
		out["ErrorMsg"] = pRspRepeal->ErrorMsg;
		out["LongCustomerName"] = pRspRepeal->LongCustomerName;
		out["RepealTimeInterval"] = pRspRepeal->RepealTimeInterval;
		out["RepealedTimes"] = pRspRepeal->RepealedTimes;
		out["PlateRepealSerial"] = pRspRepeal->PlateRepealSerial;
		out["FutureRepealSerial"] = pRspRepeal->FutureRepealSerial;
		out["PlateSerial"] = pRspRepeal->PlateSerial;
		out["SessionID"] = pRspRepeal->SessionID;
		out["InstallID"] = pRspRepeal->InstallID;
		out["FutureSerial"] = pRspRepeal->FutureSerial;
		out["RequestID"] = pRspRepeal->RequestID;
		out["TID"] = pRspRepeal->TID;
		out["ErrorID"] = pRspRepeal->ErrorID;
		out["BankRepealFlag"] = pRspRepeal->BankRepealFlag;
		out["BrokerRepealFlag"] = pRspRepeal->BrokerRepealFlag;
		out["LastFragment"] = pRspRepeal->LastFragment;
		out["IdCardType"] = pRspRepeal->IdCardType;
		out["CustType"] = pRspRepeal->CustType;
		out["VerifyCertNoFlag"] = pRspRepeal->VerifyCertNoFlag;
		out["FeePayFlag"] = pRspRepeal->FeePayFlag;
		out["BankAccType"] = pRspRepeal->BankAccType;
		out["BankSecuAccType"] = pRspRepeal->BankSecuAccType;
		out["BankPwdFlag"] = pRspRepeal->BankPwdFlag;
		out["SecuPwdFlag"] = pRspRepeal->SecuPwdFlag;
		out["TransferStatus"] = pRspRepeal->TransferStatus;
		out["TradeAmount"] = pRspRepeal->TradeAmount;
		out["FutureFetchAmount"] = pRspRepeal->FutureFetchAmount;
		out["CustFee"] = pRspRepeal->CustFee;
		out["BrokerFee"] = pRspRepeal->BrokerFee;
	}
	jnaResCallback("T_OnRtnRepealFromFutureToBankByBank", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnFromBankToFutureByFuture(CThostFtdcRspTransferField* pRspTransfer)
{
	Json::Value out;
	if (pRspTransfer)
	{
		out["TradeCode"] = pRspTransfer->TradeCode;
		out["BankID"] = pRspTransfer->BankID;
		out["BankBranchID"] = pRspTransfer->BankBranchID;
		out["BrokerID"] = pRspTransfer->BrokerID;
		out["BrokerBranchID"] = pRspTransfer->BrokerBranchID;
		out["TradeDate"] = pRspTransfer->TradeDate;
		out["TradeTime"] = pRspTransfer->TradeTime;
		out["BankSerial"] = pRspTransfer->BankSerial;
		out["TradingDay"] = pRspTransfer->TradingDay;
		out["CustomerName"] = pRspTransfer->CustomerName;
		out["IdentifiedCardNo"] = pRspTransfer->IdentifiedCardNo;
		out["BankAccount"] = pRspTransfer->BankAccount;
		out["BankPassWord"] = pRspTransfer->BankPassWord;
		out["AccountID"] = pRspTransfer->AccountID;
		out["Password"] = pRspTransfer->Password;
		out["UserID"] = pRspTransfer->UserID;
		out["CurrencyID"] = pRspTransfer->CurrencyID;
		out["Message"] = pRspTransfer->Message;
		out["Digest"] = pRspTransfer->Digest;
		out["DeviceID"] = pRspTransfer->DeviceID;
		out["BrokerIDByBank"] = pRspTransfer->BrokerIDByBank;
		out["BankSecuAcc"] = pRspTransfer->BankSecuAcc;
		out["OperNo"] = pRspTransfer->OperNo;
		out["ErrorMsg"] = pRspTransfer->ErrorMsg;
		out["LongCustomerName"] = pRspTransfer->LongCustomerName;
		out["PlateSerial"] = pRspTransfer->PlateSerial;
		out["SessionID"] = pRspTransfer->SessionID;
		out["InstallID"] = pRspTransfer->InstallID;
		out["FutureSerial"] = pRspTransfer->FutureSerial;
		out["RequestID"] = pRspTransfer->RequestID;
		out["TID"] = pRspTransfer->TID;
		out["ErrorID"] = pRspTransfer->ErrorID;
		out["LastFragment"] = pRspTransfer->LastFragment;
		out["IdCardType"] = pRspTransfer->IdCardType;
		out["CustType"] = pRspTransfer->CustType;
		out["VerifyCertNoFlag"] = pRspTransfer->VerifyCertNoFlag;
		out["FeePayFlag"] = pRspTransfer->FeePayFlag;
		out["BankAccType"] = pRspTransfer->BankAccType;
		out["BankSecuAccType"] = pRspTransfer->BankSecuAccType;
		out["BankPwdFlag"] = pRspTransfer->BankPwdFlag;
		out["SecuPwdFlag"] = pRspTransfer->SecuPwdFlag;
		out["TransferStatus"] = pRspTransfer->TransferStatus;
		out["TradeAmount"] = pRspTransfer->TradeAmount;
		out["FutureFetchAmount"] = pRspTransfer->FutureFetchAmount;
		out["CustFee"] = pRspTransfer->CustFee;
		out["BrokerFee"] = pRspTransfer->BrokerFee;
	}
	jnaResCallback("T_OnRtnFromBankToFutureByFuture", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnFromFutureToBankByFuture(CThostFtdcRspTransferField* pRspTransfer)
{
	Json::Value out;
	if (pRspTransfer)
	{
		out["TradeCode"] = pRspTransfer->TradeCode;
		out["BankID"] = pRspTransfer->BankID;
		out["BankBranchID"] = pRspTransfer->BankBranchID;
		out["BrokerID"] = pRspTransfer->BrokerID;
		out["BrokerBranchID"] = pRspTransfer->BrokerBranchID;
		out["TradeDate"] = pRspTransfer->TradeDate;
		out["TradeTime"] = pRspTransfer->TradeTime;
		out["BankSerial"] = pRspTransfer->BankSerial;
		out["TradingDay"] = pRspTransfer->TradingDay;
		out["CustomerName"] = pRspTransfer->CustomerName;
		out["IdentifiedCardNo"] = pRspTransfer->IdentifiedCardNo;
		out["BankAccount"] = pRspTransfer->BankAccount;
		out["BankPassWord"] = pRspTransfer->BankPassWord;
		out["AccountID"] = pRspTransfer->AccountID;
		out["Password"] = pRspTransfer->Password;
		out["UserID"] = pRspTransfer->UserID;
		out["CurrencyID"] = pRspTransfer->CurrencyID;
		out["Message"] = pRspTransfer->Message;
		out["Digest"] = pRspTransfer->Digest;
		out["DeviceID"] = pRspTransfer->DeviceID;
		out["BrokerIDByBank"] = pRspTransfer->BrokerIDByBank;
		out["BankSecuAcc"] = pRspTransfer->BankSecuAcc;
		out["OperNo"] = pRspTransfer->OperNo;
		out["ErrorMsg"] = pRspTransfer->ErrorMsg;
		out["LongCustomerName"] = pRspTransfer->LongCustomerName;
		out["PlateSerial"] = pRspTransfer->PlateSerial;
		out["SessionID"] = pRspTransfer->SessionID;
		out["InstallID"] = pRspTransfer->InstallID;
		out["FutureSerial"] = pRspTransfer->FutureSerial;
		out["RequestID"] = pRspTransfer->RequestID;
		out["TID"] = pRspTransfer->TID;
		out["ErrorID"] = pRspTransfer->ErrorID;
		out["LastFragment"] = pRspTransfer->LastFragment;
		out["IdCardType"] = pRspTransfer->IdCardType;
		out["CustType"] = pRspTransfer->CustType;
		out["VerifyCertNoFlag"] = pRspTransfer->VerifyCertNoFlag;
		out["FeePayFlag"] = pRspTransfer->FeePayFlag;
		out["BankAccType"] = pRspTransfer->BankAccType;
		out["BankSecuAccType"] = pRspTransfer->BankSecuAccType;
		out["BankPwdFlag"] = pRspTransfer->BankPwdFlag;
		out["SecuPwdFlag"] = pRspTransfer->SecuPwdFlag;
		out["TransferStatus"] = pRspTransfer->TransferStatus;
		out["TradeAmount"] = pRspTransfer->TradeAmount;
		out["FutureFetchAmount"] = pRspTransfer->FutureFetchAmount;
		out["CustFee"] = pRspTransfer->CustFee;
		out["BrokerFee"] = pRspTransfer->BrokerFee;
	}
	jnaResCallback("T_OnRtnFromFutureToBankByFuture", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnRepealFromBankToFutureByFutureManual(CThostFtdcRspRepealField* pRspRepeal)
{
	Json::Value out;
	if (pRspRepeal)
	{
		out["BankRepealSerial"] = pRspRepeal->BankRepealSerial;
		out["TradeCode"] = pRspRepeal->TradeCode;
		out["BankID"] = pRspRepeal->BankID;
		out["BankBranchID"] = pRspRepeal->BankBranchID;
		out["BrokerID"] = pRspRepeal->BrokerID;
		out["BrokerBranchID"] = pRspRepeal->BrokerBranchID;
		out["TradeDate"] = pRspRepeal->TradeDate;
		out["TradeTime"] = pRspRepeal->TradeTime;
		out["BankSerial"] = pRspRepeal->BankSerial;
		out["TradingDay"] = pRspRepeal->TradingDay;
		out["CustomerName"] = pRspRepeal->CustomerName;
		out["IdentifiedCardNo"] = pRspRepeal->IdentifiedCardNo;
		out["BankAccount"] = pRspRepeal->BankAccount;
		out["BankPassWord"] = pRspRepeal->BankPassWord;
		out["AccountID"] = pRspRepeal->AccountID;
		out["Password"] = pRspRepeal->Password;
		out["UserID"] = pRspRepeal->UserID;
		out["CurrencyID"] = pRspRepeal->CurrencyID;
		out["Message"] = pRspRepeal->Message;
		out["Digest"] = pRspRepeal->Digest;
		out["DeviceID"] = pRspRepeal->DeviceID;
		out["BrokerIDByBank"] = pRspRepeal->BrokerIDByBank;
		out["BankSecuAcc"] = pRspRepeal->BankSecuAcc;
		out["OperNo"] = pRspRepeal->OperNo;
		out["ErrorMsg"] = pRspRepeal->ErrorMsg;
		out["LongCustomerName"] = pRspRepeal->LongCustomerName;
		out["RepealTimeInterval"] = pRspRepeal->RepealTimeInterval;
		out["RepealedTimes"] = pRspRepeal->RepealedTimes;
		out["PlateRepealSerial"] = pRspRepeal->PlateRepealSerial;
		out["FutureRepealSerial"] = pRspRepeal->FutureRepealSerial;
		out["PlateSerial"] = pRspRepeal->PlateSerial;
		out["SessionID"] = pRspRepeal->SessionID;
		out["InstallID"] = pRspRepeal->InstallID;
		out["FutureSerial"] = pRspRepeal->FutureSerial;
		out["RequestID"] = pRspRepeal->RequestID;
		out["TID"] = pRspRepeal->TID;
		out["ErrorID"] = pRspRepeal->ErrorID;
		out["BankRepealFlag"] = pRspRepeal->BankRepealFlag;
		out["BrokerRepealFlag"] = pRspRepeal->BrokerRepealFlag;
		out["LastFragment"] = pRspRepeal->LastFragment;
		out["IdCardType"] = pRspRepeal->IdCardType;
		out["CustType"] = pRspRepeal->CustType;
		out["VerifyCertNoFlag"] = pRspRepeal->VerifyCertNoFlag;
		out["FeePayFlag"] = pRspRepeal->FeePayFlag;
		out["BankAccType"] = pRspRepeal->BankAccType;
		out["BankSecuAccType"] = pRspRepeal->BankSecuAccType;
		out["BankPwdFlag"] = pRspRepeal->BankPwdFlag;
		out["SecuPwdFlag"] = pRspRepeal->SecuPwdFlag;
		out["TransferStatus"] = pRspRepeal->TransferStatus;
		out["TradeAmount"] = pRspRepeal->TradeAmount;
		out["FutureFetchAmount"] = pRspRepeal->FutureFetchAmount;
		out["CustFee"] = pRspRepeal->CustFee;
		out["BrokerFee"] = pRspRepeal->BrokerFee;
	}
	jnaResCallback("T_OnRtnRepealFromBankToFutureByFutureManual", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnRepealFromFutureToBankByFutureManual(CThostFtdcRspRepealField* pRspRepeal)
{
	Json::Value out;
	if (pRspRepeal)
	{
		out["BankRepealSerial"] = pRspRepeal->BankRepealSerial;
		out["TradeCode"] = pRspRepeal->TradeCode;
		out["BankID"] = pRspRepeal->BankID;
		out["BankBranchID"] = pRspRepeal->BankBranchID;
		out["BrokerID"] = pRspRepeal->BrokerID;
		out["BrokerBranchID"] = pRspRepeal->BrokerBranchID;
		out["TradeDate"] = pRspRepeal->TradeDate;
		out["TradeTime"] = pRspRepeal->TradeTime;
		out["BankSerial"] = pRspRepeal->BankSerial;
		out["TradingDay"] = pRspRepeal->TradingDay;
		out["CustomerName"] = pRspRepeal->CustomerName;
		out["IdentifiedCardNo"] = pRspRepeal->IdentifiedCardNo;
		out["BankAccount"] = pRspRepeal->BankAccount;
		out["BankPassWord"] = pRspRepeal->BankPassWord;
		out["AccountID"] = pRspRepeal->AccountID;
		out["Password"] = pRspRepeal->Password;
		out["UserID"] = pRspRepeal->UserID;
		out["CurrencyID"] = pRspRepeal->CurrencyID;
		out["Message"] = pRspRepeal->Message;
		out["Digest"] = pRspRepeal->Digest;
		out["DeviceID"] = pRspRepeal->DeviceID;
		out["BrokerIDByBank"] = pRspRepeal->BrokerIDByBank;
		out["BankSecuAcc"] = pRspRepeal->BankSecuAcc;
		out["OperNo"] = pRspRepeal->OperNo;
		out["ErrorMsg"] = pRspRepeal->ErrorMsg;
		out["LongCustomerName"] = pRspRepeal->LongCustomerName;
		out["RepealTimeInterval"] = pRspRepeal->RepealTimeInterval;
		out["RepealedTimes"] = pRspRepeal->RepealedTimes;
		out["PlateRepealSerial"] = pRspRepeal->PlateRepealSerial;
		out["FutureRepealSerial"] = pRspRepeal->FutureRepealSerial;
		out["PlateSerial"] = pRspRepeal->PlateSerial;
		out["SessionID"] = pRspRepeal->SessionID;
		out["InstallID"] = pRspRepeal->InstallID;
		out["FutureSerial"] = pRspRepeal->FutureSerial;
		out["RequestID"] = pRspRepeal->RequestID;
		out["TID"] = pRspRepeal->TID;
		out["ErrorID"] = pRspRepeal->ErrorID;
		out["BankRepealFlag"] = pRspRepeal->BankRepealFlag;
		out["BrokerRepealFlag"] = pRspRepeal->BrokerRepealFlag;
		out["LastFragment"] = pRspRepeal->LastFragment;
		out["IdCardType"] = pRspRepeal->IdCardType;
		out["CustType"] = pRspRepeal->CustType;
		out["VerifyCertNoFlag"] = pRspRepeal->VerifyCertNoFlag;
		out["FeePayFlag"] = pRspRepeal->FeePayFlag;
		out["BankAccType"] = pRspRepeal->BankAccType;
		out["BankSecuAccType"] = pRspRepeal->BankSecuAccType;
		out["BankPwdFlag"] = pRspRepeal->BankPwdFlag;
		out["SecuPwdFlag"] = pRspRepeal->SecuPwdFlag;
		out["TransferStatus"] = pRspRepeal->TransferStatus;
		out["TradeAmount"] = pRspRepeal->TradeAmount;
		out["FutureFetchAmount"] = pRspRepeal->FutureFetchAmount;
		out["CustFee"] = pRspRepeal->CustFee;
		out["BrokerFee"] = pRspRepeal->BrokerFee;
	}
	jnaResCallback("T_OnRtnRepealFromFutureToBankByFutureManual", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnQueryBankBalanceByFuture(CThostFtdcNotifyQueryAccountField* pNotifyQueryAccount)
{
	Json::Value out;
	if (pNotifyQueryAccount)
	{
		out["TradeCode"] = pNotifyQueryAccount->TradeCode;
		out["BankID"] = pNotifyQueryAccount->BankID;
		out["BankBranchID"] = pNotifyQueryAccount->BankBranchID;
		out["BrokerID"] = pNotifyQueryAccount->BrokerID;
		out["BrokerBranchID"] = pNotifyQueryAccount->BrokerBranchID;
		out["TradeDate"] = pNotifyQueryAccount->TradeDate;
		out["TradeTime"] = pNotifyQueryAccount->TradeTime;
		out["BankSerial"] = pNotifyQueryAccount->BankSerial;
		out["TradingDay"] = pNotifyQueryAccount->TradingDay;
		out["CustomerName"] = pNotifyQueryAccount->CustomerName;
		out["IdentifiedCardNo"] = pNotifyQueryAccount->IdentifiedCardNo;
		out["BankAccount"] = pNotifyQueryAccount->BankAccount;
		out["BankPassWord"] = pNotifyQueryAccount->BankPassWord;
		out["AccountID"] = pNotifyQueryAccount->AccountID;
		out["Password"] = pNotifyQueryAccount->Password;
		out["UserID"] = pNotifyQueryAccount->UserID;
		out["CurrencyID"] = pNotifyQueryAccount->CurrencyID;
		out["Digest"] = pNotifyQueryAccount->Digest;
		out["DeviceID"] = pNotifyQueryAccount->DeviceID;
		out["BrokerIDByBank"] = pNotifyQueryAccount->BrokerIDByBank;
		out["BankSecuAcc"] = pNotifyQueryAccount->BankSecuAcc;
		out["OperNo"] = pNotifyQueryAccount->OperNo;
		out["ErrorMsg"] = pNotifyQueryAccount->ErrorMsg;
		out["LongCustomerName"] = pNotifyQueryAccount->LongCustomerName;
		out["PlateSerial"] = pNotifyQueryAccount->PlateSerial;
		out["SessionID"] = pNotifyQueryAccount->SessionID;
		out["FutureSerial"] = pNotifyQueryAccount->FutureSerial;
		out["InstallID"] = pNotifyQueryAccount->InstallID;
		out["RequestID"] = pNotifyQueryAccount->RequestID;
		out["TID"] = pNotifyQueryAccount->TID;
		out["ErrorID"] = pNotifyQueryAccount->ErrorID;
		out["LastFragment"] = pNotifyQueryAccount->LastFragment;
		out["IdCardType"] = pNotifyQueryAccount->IdCardType;
		out["CustType"] = pNotifyQueryAccount->CustType;
		out["VerifyCertNoFlag"] = pNotifyQueryAccount->VerifyCertNoFlag;
		out["BankAccType"] = pNotifyQueryAccount->BankAccType;
		out["BankSecuAccType"] = pNotifyQueryAccount->BankSecuAccType;
		out["BankPwdFlag"] = pNotifyQueryAccount->BankPwdFlag;
		out["SecuPwdFlag"] = pNotifyQueryAccount->SecuPwdFlag;
		out["BankUseAmount"] = pNotifyQueryAccount->BankUseAmount;
		out["BankFetchAmount"] = pNotifyQueryAccount->BankFetchAmount;
	}
	jnaResCallback("T_OnRtnQueryBankBalanceByFuture", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnBankToFutureByFuture(CThostFtdcReqTransferField* pReqTransfer, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pReqTransfer)
	{
		out["TradeCode"] = pReqTransfer->TradeCode;
		out["BankID"] = pReqTransfer->BankID;
		out["BankBranchID"] = pReqTransfer->BankBranchID;
		out["BrokerID"] = pReqTransfer->BrokerID;
		out["BrokerBranchID"] = pReqTransfer->BrokerBranchID;
		out["TradeDate"] = pReqTransfer->TradeDate;
		out["TradeTime"] = pReqTransfer->TradeTime;
		out["BankSerial"] = pReqTransfer->BankSerial;
		out["TradingDay"] = pReqTransfer->TradingDay;
		out["CustomerName"] = pReqTransfer->CustomerName;
		out["IdentifiedCardNo"] = pReqTransfer->IdentifiedCardNo;
		out["BankAccount"] = pReqTransfer->BankAccount;
		out["BankPassWord"] = pReqTransfer->BankPassWord;
		out["AccountID"] = pReqTransfer->AccountID;
		out["Password"] = pReqTransfer->Password;
		out["UserID"] = pReqTransfer->UserID;
		out["CurrencyID"] = pReqTransfer->CurrencyID;
		out["Message"] = pReqTransfer->Message;
		out["Digest"] = pReqTransfer->Digest;
		out["DeviceID"] = pReqTransfer->DeviceID;
		out["BrokerIDByBank"] = pReqTransfer->BrokerIDByBank;
		out["BankSecuAcc"] = pReqTransfer->BankSecuAcc;
		out["OperNo"] = pReqTransfer->OperNo;
		out["LongCustomerName"] = pReqTransfer->LongCustomerName;
		out["PlateSerial"] = pReqTransfer->PlateSerial;
		out["SessionID"] = pReqTransfer->SessionID;
		out["InstallID"] = pReqTransfer->InstallID;
		out["FutureSerial"] = pReqTransfer->FutureSerial;
		out["RequestID"] = pReqTransfer->RequestID;
		out["TID"] = pReqTransfer->TID;
		out["LastFragment"] = pReqTransfer->LastFragment;
		out["IdCardType"] = pReqTransfer->IdCardType;
		out["CustType"] = pReqTransfer->CustType;
		out["VerifyCertNoFlag"] = pReqTransfer->VerifyCertNoFlag;
		out["FeePayFlag"] = pReqTransfer->FeePayFlag;
		out["BankAccType"] = pReqTransfer->BankAccType;
		out["BankSecuAccType"] = pReqTransfer->BankSecuAccType;
		out["BankPwdFlag"] = pReqTransfer->BankPwdFlag;
		out["SecuPwdFlag"] = pReqTransfer->SecuPwdFlag;
		out["TransferStatus"] = pReqTransfer->TransferStatus;
		out["TradeAmount"] = pReqTransfer->TradeAmount;
		out["FutureFetchAmount"] = pReqTransfer->FutureFetchAmount;
		out["CustFee"] = pReqTransfer->CustFee;
		out["BrokerFee"] = pReqTransfer->BrokerFee;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnBankToFutureByFuture", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnFutureToBankByFuture(CThostFtdcReqTransferField* pReqTransfer, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pReqTransfer)
	{
		out["TradeCode"] = pReqTransfer->TradeCode;
		out["BankID"] = pReqTransfer->BankID;
		out["BankBranchID"] = pReqTransfer->BankBranchID;
		out["BrokerID"] = pReqTransfer->BrokerID;
		out["BrokerBranchID"] = pReqTransfer->BrokerBranchID;
		out["TradeDate"] = pReqTransfer->TradeDate;
		out["TradeTime"] = pReqTransfer->TradeTime;
		out["BankSerial"] = pReqTransfer->BankSerial;
		out["TradingDay"] = pReqTransfer->TradingDay;
		out["CustomerName"] = pReqTransfer->CustomerName;
		out["IdentifiedCardNo"] = pReqTransfer->IdentifiedCardNo;
		out["BankAccount"] = pReqTransfer->BankAccount;
		out["BankPassWord"] = pReqTransfer->BankPassWord;
		out["AccountID"] = pReqTransfer->AccountID;
		out["Password"] = pReqTransfer->Password;
		out["UserID"] = pReqTransfer->UserID;
		out["CurrencyID"] = pReqTransfer->CurrencyID;
		out["Message"] = pReqTransfer->Message;
		out["Digest"] = pReqTransfer->Digest;
		out["DeviceID"] = pReqTransfer->DeviceID;
		out["BrokerIDByBank"] = pReqTransfer->BrokerIDByBank;
		out["BankSecuAcc"] = pReqTransfer->BankSecuAcc;
		out["OperNo"] = pReqTransfer->OperNo;
		out["LongCustomerName"] = pReqTransfer->LongCustomerName;
		out["PlateSerial"] = pReqTransfer->PlateSerial;
		out["SessionID"] = pReqTransfer->SessionID;
		out["InstallID"] = pReqTransfer->InstallID;
		out["FutureSerial"] = pReqTransfer->FutureSerial;
		out["RequestID"] = pReqTransfer->RequestID;
		out["TID"] = pReqTransfer->TID;
		out["LastFragment"] = pReqTransfer->LastFragment;
		out["IdCardType"] = pReqTransfer->IdCardType;
		out["CustType"] = pReqTransfer->CustType;
		out["VerifyCertNoFlag"] = pReqTransfer->VerifyCertNoFlag;
		out["FeePayFlag"] = pReqTransfer->FeePayFlag;
		out["BankAccType"] = pReqTransfer->BankAccType;
		out["BankSecuAccType"] = pReqTransfer->BankSecuAccType;
		out["BankPwdFlag"] = pReqTransfer->BankPwdFlag;
		out["SecuPwdFlag"] = pReqTransfer->SecuPwdFlag;
		out["TransferStatus"] = pReqTransfer->TransferStatus;
		out["TradeAmount"] = pReqTransfer->TradeAmount;
		out["FutureFetchAmount"] = pReqTransfer->FutureFetchAmount;
		out["CustFee"] = pReqTransfer->CustFee;
		out["BrokerFee"] = pReqTransfer->BrokerFee;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnFutureToBankByFuture", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnRepealBankToFutureByFutureManual(CThostFtdcReqRepealField* pReqRepeal, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pReqRepeal)
	{
		out["BankRepealSerial"] = pReqRepeal->BankRepealSerial;
		out["TradeCode"] = pReqRepeal->TradeCode;
		out["BankID"] = pReqRepeal->BankID;
		out["BankBranchID"] = pReqRepeal->BankBranchID;
		out["BrokerID"] = pReqRepeal->BrokerID;
		out["BrokerBranchID"] = pReqRepeal->BrokerBranchID;
		out["TradeDate"] = pReqRepeal->TradeDate;
		out["TradeTime"] = pReqRepeal->TradeTime;
		out["BankSerial"] = pReqRepeal->BankSerial;
		out["TradingDay"] = pReqRepeal->TradingDay;
		out["CustomerName"] = pReqRepeal->CustomerName;
		out["IdentifiedCardNo"] = pReqRepeal->IdentifiedCardNo;
		out["BankAccount"] = pReqRepeal->BankAccount;
		out["BankPassWord"] = pReqRepeal->BankPassWord;
		out["AccountID"] = pReqRepeal->AccountID;
		out["Password"] = pReqRepeal->Password;
		out["UserID"] = pReqRepeal->UserID;
		out["CurrencyID"] = pReqRepeal->CurrencyID;
		out["Message"] = pReqRepeal->Message;
		out["Digest"] = pReqRepeal->Digest;
		out["DeviceID"] = pReqRepeal->DeviceID;
		out["BrokerIDByBank"] = pReqRepeal->BrokerIDByBank;
		out["BankSecuAcc"] = pReqRepeal->BankSecuAcc;
		out["OperNo"] = pReqRepeal->OperNo;
		out["LongCustomerName"] = pReqRepeal->LongCustomerName;
		out["RepealTimeInterval"] = pReqRepeal->RepealTimeInterval;
		out["RepealedTimes"] = pReqRepeal->RepealedTimes;
		out["PlateRepealSerial"] = pReqRepeal->PlateRepealSerial;
		out["FutureRepealSerial"] = pReqRepeal->FutureRepealSerial;
		out["PlateSerial"] = pReqRepeal->PlateSerial;
		out["SessionID"] = pReqRepeal->SessionID;
		out["InstallID"] = pReqRepeal->InstallID;
		out["FutureSerial"] = pReqRepeal->FutureSerial;
		out["RequestID"] = pReqRepeal->RequestID;
		out["TID"] = pReqRepeal->TID;
		out["BankRepealFlag"] = pReqRepeal->BankRepealFlag;
		out["BrokerRepealFlag"] = pReqRepeal->BrokerRepealFlag;
		out["LastFragment"] = pReqRepeal->LastFragment;
		out["IdCardType"] = pReqRepeal->IdCardType;
		out["CustType"] = pReqRepeal->CustType;
		out["VerifyCertNoFlag"] = pReqRepeal->VerifyCertNoFlag;
		out["FeePayFlag"] = pReqRepeal->FeePayFlag;
		out["BankAccType"] = pReqRepeal->BankAccType;
		out["BankSecuAccType"] = pReqRepeal->BankSecuAccType;
		out["BankPwdFlag"] = pReqRepeal->BankPwdFlag;
		out["SecuPwdFlag"] = pReqRepeal->SecuPwdFlag;
		out["TransferStatus"] = pReqRepeal->TransferStatus;
		out["TradeAmount"] = pReqRepeal->TradeAmount;
		out["FutureFetchAmount"] = pReqRepeal->FutureFetchAmount;
		out["CustFee"] = pReqRepeal->CustFee;
		out["BrokerFee"] = pReqRepeal->BrokerFee;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnRepealBankToFutureByFutureManual", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnRepealFutureToBankByFutureManual(CThostFtdcReqRepealField* pReqRepeal, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pReqRepeal)
	{
		out["BankRepealSerial"] = pReqRepeal->BankRepealSerial;
		out["TradeCode"] = pReqRepeal->TradeCode;
		out["BankID"] = pReqRepeal->BankID;
		out["BankBranchID"] = pReqRepeal->BankBranchID;
		out["BrokerID"] = pReqRepeal->BrokerID;
		out["BrokerBranchID"] = pReqRepeal->BrokerBranchID;
		out["TradeDate"] = pReqRepeal->TradeDate;
		out["TradeTime"] = pReqRepeal->TradeTime;
		out["BankSerial"] = pReqRepeal->BankSerial;
		out["TradingDay"] = pReqRepeal->TradingDay;
		out["CustomerName"] = pReqRepeal->CustomerName;
		out["IdentifiedCardNo"] = pReqRepeal->IdentifiedCardNo;
		out["BankAccount"] = pReqRepeal->BankAccount;
		out["BankPassWord"] = pReqRepeal->BankPassWord;
		out["AccountID"] = pReqRepeal->AccountID;
		out["Password"] = pReqRepeal->Password;
		out["UserID"] = pReqRepeal->UserID;
		out["CurrencyID"] = pReqRepeal->CurrencyID;
		out["Message"] = pReqRepeal->Message;
		out["Digest"] = pReqRepeal->Digest;
		out["DeviceID"] = pReqRepeal->DeviceID;
		out["BrokerIDByBank"] = pReqRepeal->BrokerIDByBank;
		out["BankSecuAcc"] = pReqRepeal->BankSecuAcc;
		out["OperNo"] = pReqRepeal->OperNo;
		out["LongCustomerName"] = pReqRepeal->LongCustomerName;
		out["RepealTimeInterval"] = pReqRepeal->RepealTimeInterval;
		out["RepealedTimes"] = pReqRepeal->RepealedTimes;
		out["PlateRepealSerial"] = pReqRepeal->PlateRepealSerial;
		out["FutureRepealSerial"] = pReqRepeal->FutureRepealSerial;
		out["PlateSerial"] = pReqRepeal->PlateSerial;
		out["SessionID"] = pReqRepeal->SessionID;
		out["InstallID"] = pReqRepeal->InstallID;
		out["FutureSerial"] = pReqRepeal->FutureSerial;
		out["RequestID"] = pReqRepeal->RequestID;
		out["TID"] = pReqRepeal->TID;
		out["BankRepealFlag"] = pReqRepeal->BankRepealFlag;
		out["BrokerRepealFlag"] = pReqRepeal->BrokerRepealFlag;
		out["LastFragment"] = pReqRepeal->LastFragment;
		out["IdCardType"] = pReqRepeal->IdCardType;
		out["CustType"] = pReqRepeal->CustType;
		out["VerifyCertNoFlag"] = pReqRepeal->VerifyCertNoFlag;
		out["FeePayFlag"] = pReqRepeal->FeePayFlag;
		out["BankAccType"] = pReqRepeal->BankAccType;
		out["BankSecuAccType"] = pReqRepeal->BankSecuAccType;
		out["BankPwdFlag"] = pReqRepeal->BankPwdFlag;
		out["SecuPwdFlag"] = pReqRepeal->SecuPwdFlag;
		out["TransferStatus"] = pReqRepeal->TransferStatus;
		out["TradeAmount"] = pReqRepeal->TradeAmount;
		out["FutureFetchAmount"] = pReqRepeal->FutureFetchAmount;
		out["CustFee"] = pReqRepeal->CustFee;
		out["BrokerFee"] = pReqRepeal->BrokerFee;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnRepealFutureToBankByFutureManual", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnErrRtnQueryBankBalanceByFuture(CThostFtdcReqQueryAccountField* pReqQueryAccount, CThostFtdcRspInfoField* pRspInfo)
{
	Json::Value out;
	if (pReqQueryAccount)
	{
		out["TradeCode"] = pReqQueryAccount->TradeCode;
		out["BankID"] = pReqQueryAccount->BankID;
		out["BankBranchID"] = pReqQueryAccount->BankBranchID;
		out["BrokerID"] = pReqQueryAccount->BrokerID;
		out["BrokerBranchID"] = pReqQueryAccount->BrokerBranchID;
		out["TradeDate"] = pReqQueryAccount->TradeDate;
		out["TradeTime"] = pReqQueryAccount->TradeTime;
		out["BankSerial"] = pReqQueryAccount->BankSerial;
		out["TradingDay"] = pReqQueryAccount->TradingDay;
		out["CustomerName"] = pReqQueryAccount->CustomerName;
		out["IdentifiedCardNo"] = pReqQueryAccount->IdentifiedCardNo;
		out["BankAccount"] = pReqQueryAccount->BankAccount;
		out["BankPassWord"] = pReqQueryAccount->BankPassWord;
		out["AccountID"] = pReqQueryAccount->AccountID;
		out["Password"] = pReqQueryAccount->Password;
		out["UserID"] = pReqQueryAccount->UserID;
		out["CurrencyID"] = pReqQueryAccount->CurrencyID;
		out["Digest"] = pReqQueryAccount->Digest;
		out["DeviceID"] = pReqQueryAccount->DeviceID;
		out["BrokerIDByBank"] = pReqQueryAccount->BrokerIDByBank;
		out["BankSecuAcc"] = pReqQueryAccount->BankSecuAcc;
		out["OperNo"] = pReqQueryAccount->OperNo;
		out["LongCustomerName"] = pReqQueryAccount->LongCustomerName;
		out["PlateSerial"] = pReqQueryAccount->PlateSerial;
		out["SessionID"] = pReqQueryAccount->SessionID;
		out["FutureSerial"] = pReqQueryAccount->FutureSerial;
		out["InstallID"] = pReqQueryAccount->InstallID;
		out["RequestID"] = pReqQueryAccount->RequestID;
		out["TID"] = pReqQueryAccount->TID;
		out["LastFragment"] = pReqQueryAccount->LastFragment;
		out["IdCardType"] = pReqQueryAccount->IdCardType;
		out["CustType"] = pReqQueryAccount->CustType;
		out["VerifyCertNoFlag"] = pReqQueryAccount->VerifyCertNoFlag;
		out["BankAccType"] = pReqQueryAccount->BankAccType;
		out["BankSecuAccType"] = pReqQueryAccount->BankSecuAccType;
		out["BankPwdFlag"] = pReqQueryAccount->BankPwdFlag;
		out["SecuPwdFlag"] = pReqQueryAccount->SecuPwdFlag;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	jnaResCallback("T_OnErrRtnQueryBankBalanceByFuture", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnRepealFromBankToFutureByFuture(CThostFtdcRspRepealField* pRspRepeal)
{
	Json::Value out;
	if (pRspRepeal)
	{
		out["BankRepealSerial"] = pRspRepeal->BankRepealSerial;
		out["TradeCode"] = pRspRepeal->TradeCode;
		out["BankID"] = pRspRepeal->BankID;
		out["BankBranchID"] = pRspRepeal->BankBranchID;
		out["BrokerID"] = pRspRepeal->BrokerID;
		out["BrokerBranchID"] = pRspRepeal->BrokerBranchID;
		out["TradeDate"] = pRspRepeal->TradeDate;
		out["TradeTime"] = pRspRepeal->TradeTime;
		out["BankSerial"] = pRspRepeal->BankSerial;
		out["TradingDay"] = pRspRepeal->TradingDay;
		out["CustomerName"] = pRspRepeal->CustomerName;
		out["IdentifiedCardNo"] = pRspRepeal->IdentifiedCardNo;
		out["BankAccount"] = pRspRepeal->BankAccount;
		out["BankPassWord"] = pRspRepeal->BankPassWord;
		out["AccountID"] = pRspRepeal->AccountID;
		out["Password"] = pRspRepeal->Password;
		out["UserID"] = pRspRepeal->UserID;
		out["CurrencyID"] = pRspRepeal->CurrencyID;
		out["Message"] = pRspRepeal->Message;
		out["Digest"] = pRspRepeal->Digest;
		out["DeviceID"] = pRspRepeal->DeviceID;
		out["BrokerIDByBank"] = pRspRepeal->BrokerIDByBank;
		out["BankSecuAcc"] = pRspRepeal->BankSecuAcc;
		out["OperNo"] = pRspRepeal->OperNo;
		out["ErrorMsg"] = pRspRepeal->ErrorMsg;
		out["LongCustomerName"] = pRspRepeal->LongCustomerName;
		out["RepealTimeInterval"] = pRspRepeal->RepealTimeInterval;
		out["RepealedTimes"] = pRspRepeal->RepealedTimes;
		out["PlateRepealSerial"] = pRspRepeal->PlateRepealSerial;
		out["FutureRepealSerial"] = pRspRepeal->FutureRepealSerial;
		out["PlateSerial"] = pRspRepeal->PlateSerial;
		out["SessionID"] = pRspRepeal->SessionID;
		out["InstallID"] = pRspRepeal->InstallID;
		out["FutureSerial"] = pRspRepeal->FutureSerial;
		out["RequestID"] = pRspRepeal->RequestID;
		out["TID"] = pRspRepeal->TID;
		out["ErrorID"] = pRspRepeal->ErrorID;
		out["BankRepealFlag"] = pRspRepeal->BankRepealFlag;
		out["BrokerRepealFlag"] = pRspRepeal->BrokerRepealFlag;
		out["LastFragment"] = pRspRepeal->LastFragment;
		out["IdCardType"] = pRspRepeal->IdCardType;
		out["CustType"] = pRspRepeal->CustType;
		out["VerifyCertNoFlag"] = pRspRepeal->VerifyCertNoFlag;
		out["FeePayFlag"] = pRspRepeal->FeePayFlag;
		out["BankAccType"] = pRspRepeal->BankAccType;
		out["BankSecuAccType"] = pRspRepeal->BankSecuAccType;
		out["BankPwdFlag"] = pRspRepeal->BankPwdFlag;
		out["SecuPwdFlag"] = pRspRepeal->SecuPwdFlag;
		out["TransferStatus"] = pRspRepeal->TransferStatus;
		out["TradeAmount"] = pRspRepeal->TradeAmount;
		out["FutureFetchAmount"] = pRspRepeal->FutureFetchAmount;
		out["CustFee"] = pRspRepeal->CustFee;
		out["BrokerFee"] = pRspRepeal->BrokerFee;
	}
	jnaResCallback("T_OnRtnRepealFromBankToFutureByFuture", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnRepealFromFutureToBankByFuture(CThostFtdcRspRepealField* pRspRepeal)
{
	Json::Value out;
	if (pRspRepeal)
	{
		out["BankRepealSerial"] = pRspRepeal->BankRepealSerial;
		out["TradeCode"] = pRspRepeal->TradeCode;
		out["BankID"] = pRspRepeal->BankID;
		out["BankBranchID"] = pRspRepeal->BankBranchID;
		out["BrokerID"] = pRspRepeal->BrokerID;
		out["BrokerBranchID"] = pRspRepeal->BrokerBranchID;
		out["TradeDate"] = pRspRepeal->TradeDate;
		out["TradeTime"] = pRspRepeal->TradeTime;
		out["BankSerial"] = pRspRepeal->BankSerial;
		out["TradingDay"] = pRspRepeal->TradingDay;
		out["CustomerName"] = pRspRepeal->CustomerName;
		out["IdentifiedCardNo"] = pRspRepeal->IdentifiedCardNo;
		out["BankAccount"] = pRspRepeal->BankAccount;
		out["BankPassWord"] = pRspRepeal->BankPassWord;
		out["AccountID"] = pRspRepeal->AccountID;
		out["Password"] = pRspRepeal->Password;
		out["UserID"] = pRspRepeal->UserID;
		out["CurrencyID"] = pRspRepeal->CurrencyID;
		out["Message"] = pRspRepeal->Message;
		out["Digest"] = pRspRepeal->Digest;
		out["DeviceID"] = pRspRepeal->DeviceID;
		out["BrokerIDByBank"] = pRspRepeal->BrokerIDByBank;
		out["BankSecuAcc"] = pRspRepeal->BankSecuAcc;
		out["OperNo"] = pRspRepeal->OperNo;
		out["ErrorMsg"] = pRspRepeal->ErrorMsg;
		out["LongCustomerName"] = pRspRepeal->LongCustomerName;
		out["RepealTimeInterval"] = pRspRepeal->RepealTimeInterval;
		out["RepealedTimes"] = pRspRepeal->RepealedTimes;
		out["PlateRepealSerial"] = pRspRepeal->PlateRepealSerial;
		out["FutureRepealSerial"] = pRspRepeal->FutureRepealSerial;
		out["PlateSerial"] = pRspRepeal->PlateSerial;
		out["SessionID"] = pRspRepeal->SessionID;
		out["InstallID"] = pRspRepeal->InstallID;
		out["FutureSerial"] = pRspRepeal->FutureSerial;
		out["RequestID"] = pRspRepeal->RequestID;
		out["TID"] = pRspRepeal->TID;
		out["ErrorID"] = pRspRepeal->ErrorID;
		out["BankRepealFlag"] = pRspRepeal->BankRepealFlag;
		out["BrokerRepealFlag"] = pRspRepeal->BrokerRepealFlag;
		out["LastFragment"] = pRspRepeal->LastFragment;
		out["IdCardType"] = pRspRepeal->IdCardType;
		out["CustType"] = pRspRepeal->CustType;
		out["VerifyCertNoFlag"] = pRspRepeal->VerifyCertNoFlag;
		out["FeePayFlag"] = pRspRepeal->FeePayFlag;
		out["BankAccType"] = pRspRepeal->BankAccType;
		out["BankSecuAccType"] = pRspRepeal->BankSecuAccType;
		out["BankPwdFlag"] = pRspRepeal->BankPwdFlag;
		out["SecuPwdFlag"] = pRspRepeal->SecuPwdFlag;
		out["TransferStatus"] = pRspRepeal->TransferStatus;
		out["TradeAmount"] = pRspRepeal->TradeAmount;
		out["FutureFetchAmount"] = pRspRepeal->FutureFetchAmount;
		out["CustFee"] = pRspRepeal->CustFee;
		out["BrokerFee"] = pRspRepeal->BrokerFee;
	}
	jnaResCallback("T_OnRtnRepealFromFutureToBankByFuture", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRspFromBankToFutureByFuture(CThostFtdcReqTransferField* pReqTransfer, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pReqTransfer)
	{
		out["TradeCode"] = pReqTransfer->TradeCode;
		out["BankID"] = pReqTransfer->BankID;
		out["BankBranchID"] = pReqTransfer->BankBranchID;
		out["BrokerID"] = pReqTransfer->BrokerID;
		out["BrokerBranchID"] = pReqTransfer->BrokerBranchID;
		out["TradeDate"] = pReqTransfer->TradeDate;
		out["TradeTime"] = pReqTransfer->TradeTime;
		out["BankSerial"] = pReqTransfer->BankSerial;
		out["TradingDay"] = pReqTransfer->TradingDay;
		out["CustomerName"] = pReqTransfer->CustomerName;
		out["IdentifiedCardNo"] = pReqTransfer->IdentifiedCardNo;
		out["BankAccount"] = pReqTransfer->BankAccount;
		out["BankPassWord"] = pReqTransfer->BankPassWord;
		out["AccountID"] = pReqTransfer->AccountID;
		out["Password"] = pReqTransfer->Password;
		out["UserID"] = pReqTransfer->UserID;
		out["CurrencyID"] = pReqTransfer->CurrencyID;
		out["Message"] = pReqTransfer->Message;
		out["Digest"] = pReqTransfer->Digest;
		out["DeviceID"] = pReqTransfer->DeviceID;
		out["BrokerIDByBank"] = pReqTransfer->BrokerIDByBank;
		out["BankSecuAcc"] = pReqTransfer->BankSecuAcc;
		out["OperNo"] = pReqTransfer->OperNo;
		out["LongCustomerName"] = pReqTransfer->LongCustomerName;
		out["PlateSerial"] = pReqTransfer->PlateSerial;
		out["SessionID"] = pReqTransfer->SessionID;
		out["InstallID"] = pReqTransfer->InstallID;
		out["FutureSerial"] = pReqTransfer->FutureSerial;
		out["RequestID"] = pReqTransfer->RequestID;
		out["TID"] = pReqTransfer->TID;
		out["LastFragment"] = pReqTransfer->LastFragment;
		out["IdCardType"] = pReqTransfer->IdCardType;
		out["CustType"] = pReqTransfer->CustType;
		out["VerifyCertNoFlag"] = pReqTransfer->VerifyCertNoFlag;
		out["FeePayFlag"] = pReqTransfer->FeePayFlag;
		out["BankAccType"] = pReqTransfer->BankAccType;
		out["BankSecuAccType"] = pReqTransfer->BankSecuAccType;
		out["BankPwdFlag"] = pReqTransfer->BankPwdFlag;
		out["SecuPwdFlag"] = pReqTransfer->SecuPwdFlag;
		out["TransferStatus"] = pReqTransfer->TransferStatus;
		out["TradeAmount"] = pReqTransfer->TradeAmount;
		out["FutureFetchAmount"] = pReqTransfer->FutureFetchAmount;
		out["CustFee"] = pReqTransfer->CustFee;
		out["BrokerFee"] = pReqTransfer->BrokerFee;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspFromBankToFutureByFuture", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspFromFutureToBankByFuture(CThostFtdcReqTransferField* pReqTransfer, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pReqTransfer)
	{
		out["TradeCode"] = pReqTransfer->TradeCode;
		out["BankID"] = pReqTransfer->BankID;
		out["BankBranchID"] = pReqTransfer->BankBranchID;
		out["BrokerID"] = pReqTransfer->BrokerID;
		out["BrokerBranchID"] = pReqTransfer->BrokerBranchID;
		out["TradeDate"] = pReqTransfer->TradeDate;
		out["TradeTime"] = pReqTransfer->TradeTime;
		out["BankSerial"] = pReqTransfer->BankSerial;
		out["TradingDay"] = pReqTransfer->TradingDay;
		out["CustomerName"] = pReqTransfer->CustomerName;
		out["IdentifiedCardNo"] = pReqTransfer->IdentifiedCardNo;
		out["BankAccount"] = pReqTransfer->BankAccount;
		out["BankPassWord"] = pReqTransfer->BankPassWord;
		out["AccountID"] = pReqTransfer->AccountID;
		out["Password"] = pReqTransfer->Password;
		out["UserID"] = pReqTransfer->UserID;
		out["CurrencyID"] = pReqTransfer->CurrencyID;
		out["Message"] = pReqTransfer->Message;
		out["Digest"] = pReqTransfer->Digest;
		out["DeviceID"] = pReqTransfer->DeviceID;
		out["BrokerIDByBank"] = pReqTransfer->BrokerIDByBank;
		out["BankSecuAcc"] = pReqTransfer->BankSecuAcc;
		out["OperNo"] = pReqTransfer->OperNo;
		out["LongCustomerName"] = pReqTransfer->LongCustomerName;
		out["PlateSerial"] = pReqTransfer->PlateSerial;
		out["SessionID"] = pReqTransfer->SessionID;
		out["InstallID"] = pReqTransfer->InstallID;
		out["FutureSerial"] = pReqTransfer->FutureSerial;
		out["RequestID"] = pReqTransfer->RequestID;
		out["TID"] = pReqTransfer->TID;
		out["LastFragment"] = pReqTransfer->LastFragment;
		out["IdCardType"] = pReqTransfer->IdCardType;
		out["CustType"] = pReqTransfer->CustType;
		out["VerifyCertNoFlag"] = pReqTransfer->VerifyCertNoFlag;
		out["FeePayFlag"] = pReqTransfer->FeePayFlag;
		out["BankAccType"] = pReqTransfer->BankAccType;
		out["BankSecuAccType"] = pReqTransfer->BankSecuAccType;
		out["BankPwdFlag"] = pReqTransfer->BankPwdFlag;
		out["SecuPwdFlag"] = pReqTransfer->SecuPwdFlag;
		out["TransferStatus"] = pReqTransfer->TransferStatus;
		out["TradeAmount"] = pReqTransfer->TradeAmount;
		out["FutureFetchAmount"] = pReqTransfer->FutureFetchAmount;
		out["CustFee"] = pReqTransfer->CustFee;
		out["BrokerFee"] = pReqTransfer->BrokerFee;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspFromFutureToBankByFuture", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRspQueryBankAccountMoneyByFuture(CThostFtdcReqQueryAccountField* pReqQueryAccount, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pReqQueryAccount)
	{
		out["TradeCode"] = pReqQueryAccount->TradeCode;
		out["BankID"] = pReqQueryAccount->BankID;
		out["BankBranchID"] = pReqQueryAccount->BankBranchID;
		out["BrokerID"] = pReqQueryAccount->BrokerID;
		out["BrokerBranchID"] = pReqQueryAccount->BrokerBranchID;
		out["TradeDate"] = pReqQueryAccount->TradeDate;
		out["TradeTime"] = pReqQueryAccount->TradeTime;
		out["BankSerial"] = pReqQueryAccount->BankSerial;
		out["TradingDay"] = pReqQueryAccount->TradingDay;
		out["CustomerName"] = pReqQueryAccount->CustomerName;
		out["IdentifiedCardNo"] = pReqQueryAccount->IdentifiedCardNo;
		out["BankAccount"] = pReqQueryAccount->BankAccount;
		out["BankPassWord"] = pReqQueryAccount->BankPassWord;
		out["AccountID"] = pReqQueryAccount->AccountID;
		out["Password"] = pReqQueryAccount->Password;
		out["UserID"] = pReqQueryAccount->UserID;
		out["CurrencyID"] = pReqQueryAccount->CurrencyID;
		out["Digest"] = pReqQueryAccount->Digest;
		out["DeviceID"] = pReqQueryAccount->DeviceID;
		out["BrokerIDByBank"] = pReqQueryAccount->BrokerIDByBank;
		out["BankSecuAcc"] = pReqQueryAccount->BankSecuAcc;
		out["OperNo"] = pReqQueryAccount->OperNo;
		out["LongCustomerName"] = pReqQueryAccount->LongCustomerName;
		out["PlateSerial"] = pReqQueryAccount->PlateSerial;
		out["SessionID"] = pReqQueryAccount->SessionID;
		out["FutureSerial"] = pReqQueryAccount->FutureSerial;
		out["InstallID"] = pReqQueryAccount->InstallID;
		out["RequestID"] = pReqQueryAccount->RequestID;
		out["TID"] = pReqQueryAccount->TID;
		out["LastFragment"] = pReqQueryAccount->LastFragment;
		out["IdCardType"] = pReqQueryAccount->IdCardType;
		out["CustType"] = pReqQueryAccount->CustType;
		out["VerifyCertNoFlag"] = pReqQueryAccount->VerifyCertNoFlag;
		out["BankAccType"] = pReqQueryAccount->BankAccType;
		out["BankSecuAccType"] = pReqQueryAccount->BankSecuAccType;
		out["BankPwdFlag"] = pReqQueryAccount->BankPwdFlag;
		out["SecuPwdFlag"] = pReqQueryAccount->SecuPwdFlag;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQueryBankAccountMoneyByFuture", Base64_Encode(out.toStyledString()).c_str());
};

void CTraderSpi::OnRtnOpenAccountByBank(CThostFtdcOpenAccountField* pOpenAccount)
{
	Json::Value out;
	if (pOpenAccount)
	{
		out["TradeCode"] = pOpenAccount->TradeCode;
		out["BankID"] = pOpenAccount->BankID;
		out["BankBranchID"] = pOpenAccount->BankBranchID;
		out["BrokerID"] = pOpenAccount->BrokerID;
		out["BrokerBranchID"] = pOpenAccount->BrokerBranchID;
		out["TradeDate"] = pOpenAccount->TradeDate;
		out["TradeTime"] = pOpenAccount->TradeTime;
		out["BankSerial"] = pOpenAccount->BankSerial;
		out["TradingDay"] = pOpenAccount->TradingDay;
		out["CustomerName"] = pOpenAccount->CustomerName;
		out["IdentifiedCardNo"] = pOpenAccount->IdentifiedCardNo;
		out["CountryCode"] = pOpenAccount->CountryCode;
		out["Address"] = pOpenAccount->Address;
		out["ZipCode"] = pOpenAccount->ZipCode;
		out["Telephone"] = pOpenAccount->Telephone;
		out["MobilePhone"] = pOpenAccount->MobilePhone;
		out["Fax"] = pOpenAccount->Fax;
		out["EMail"] = pOpenAccount->EMail;
		out["BankAccount"] = pOpenAccount->BankAccount;
		out["BankPassWord"] = pOpenAccount->BankPassWord;
		out["AccountID"] = pOpenAccount->AccountID;
		out["Password"] = pOpenAccount->Password;
		out["CurrencyID"] = pOpenAccount->CurrencyID;
		out["Digest"] = pOpenAccount->Digest;
		out["DeviceID"] = pOpenAccount->DeviceID;
		out["BrokerIDByBank"] = pOpenAccount->BrokerIDByBank;
		out["BankSecuAcc"] = pOpenAccount->BankSecuAcc;
		out["OperNo"] = pOpenAccount->OperNo;
		out["UserID"] = pOpenAccount->UserID;
		out["ErrorMsg"] = pOpenAccount->ErrorMsg;
		out["LongCustomerName"] = pOpenAccount->LongCustomerName;
		out["PlateSerial"] = pOpenAccount->PlateSerial;
		out["SessionID"] = pOpenAccount->SessionID;
		out["InstallID"] = pOpenAccount->InstallID;
		out["TID"] = pOpenAccount->TID;
		out["ErrorID"] = pOpenAccount->ErrorID;
		out["LastFragment"] = pOpenAccount->LastFragment;
		out["IdCardType"] = pOpenAccount->IdCardType;
		out["Gender"] = pOpenAccount->Gender;
		out["CustType"] = pOpenAccount->CustType;
		out["MoneyAccountStatus"] = pOpenAccount->MoneyAccountStatus;
		out["VerifyCertNoFlag"] = pOpenAccount->VerifyCertNoFlag;
		out["CashExchangeCode"] = pOpenAccount->CashExchangeCode;
		out["BankAccType"] = pOpenAccount->BankAccType;
		out["BankSecuAccType"] = pOpenAccount->BankSecuAccType;
		out["BankPwdFlag"] = pOpenAccount->BankPwdFlag;
		out["SecuPwdFlag"] = pOpenAccount->SecuPwdFlag;
	}
	jnaResCallback("T_OnRtnOpenAccountByBank", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnCancelAccountByBank(CThostFtdcCancelAccountField* pCancelAccount)
{
	Json::Value out;
	if (pCancelAccount)
	{
		out["TradeCode"] = pCancelAccount->TradeCode;
		out["BankID"] = pCancelAccount->BankID;
		out["BankBranchID"] = pCancelAccount->BankBranchID;
		out["BrokerID"] = pCancelAccount->BrokerID;
		out["BrokerBranchID"] = pCancelAccount->BrokerBranchID;
		out["TradeDate"] = pCancelAccount->TradeDate;
		out["TradeTime"] = pCancelAccount->TradeTime;
		out["BankSerial"] = pCancelAccount->BankSerial;
		out["TradingDay"] = pCancelAccount->TradingDay;
		out["CustomerName"] = pCancelAccount->CustomerName;
		out["IdentifiedCardNo"] = pCancelAccount->IdentifiedCardNo;
		out["CountryCode"] = pCancelAccount->CountryCode;
		out["Address"] = pCancelAccount->Address;
		out["ZipCode"] = pCancelAccount->ZipCode;
		out["Telephone"] = pCancelAccount->Telephone;
		out["MobilePhone"] = pCancelAccount->MobilePhone;
		out["Fax"] = pCancelAccount->Fax;
		out["EMail"] = pCancelAccount->EMail;
		out["BankAccount"] = pCancelAccount->BankAccount;
		out["BankPassWord"] = pCancelAccount->BankPassWord;
		out["AccountID"] = pCancelAccount->AccountID;
		out["Password"] = pCancelAccount->Password;
		out["CurrencyID"] = pCancelAccount->CurrencyID;
		out["Digest"] = pCancelAccount->Digest;
		out["DeviceID"] = pCancelAccount->DeviceID;
		out["BrokerIDByBank"] = pCancelAccount->BrokerIDByBank;
		out["BankSecuAcc"] = pCancelAccount->BankSecuAcc;
		out["OperNo"] = pCancelAccount->OperNo;
		out["UserID"] = pCancelAccount->UserID;
		out["ErrorMsg"] = pCancelAccount->ErrorMsg;
		out["LongCustomerName"] = pCancelAccount->LongCustomerName;
		out["PlateSerial"] = pCancelAccount->PlateSerial;
		out["SessionID"] = pCancelAccount->SessionID;
		out["InstallID"] = pCancelAccount->InstallID;
		out["TID"] = pCancelAccount->TID;
		out["ErrorID"] = pCancelAccount->ErrorID;
		out["LastFragment"] = pCancelAccount->LastFragment;
		out["IdCardType"] = pCancelAccount->IdCardType;
		out["Gender"] = pCancelAccount->Gender;
		out["CustType"] = pCancelAccount->CustType;
		out["MoneyAccountStatus"] = pCancelAccount->MoneyAccountStatus;
		out["VerifyCertNoFlag"] = pCancelAccount->VerifyCertNoFlag;
		out["CashExchangeCode"] = pCancelAccount->CashExchangeCode;
		out["BankAccType"] = pCancelAccount->BankAccType;
		out["BankSecuAccType"] = pCancelAccount->BankSecuAccType;
		out["BankPwdFlag"] = pCancelAccount->BankPwdFlag;
		out["SecuPwdFlag"] = pCancelAccount->SecuPwdFlag;
	}
	jnaResCallback("T_OnRtnCancelAccountByBank", Base64_Encode(out.toStyledString()).c_str());
};
void CTraderSpi::OnRtnChangeAccountByBank(CThostFtdcChangeAccountField* pChangeAccount)
{
	Json::Value out;
	if (pChangeAccount)
	{
		out["TradeCode"] = pChangeAccount->TradeCode;
		out["BankID"] = pChangeAccount->BankID;
		out["BankBranchID"] = pChangeAccount->BankBranchID;
		out["BrokerID"] = pChangeAccount->BrokerID;
		out["BrokerBranchID"] = pChangeAccount->BrokerBranchID;
		out["TradeDate"] = pChangeAccount->TradeDate;
		out["TradeTime"] = pChangeAccount->TradeTime;
		out["BankSerial"] = pChangeAccount->BankSerial;
		out["TradingDay"] = pChangeAccount->TradingDay;
		out["CustomerName"] = pChangeAccount->CustomerName;
		out["IdentifiedCardNo"] = pChangeAccount->IdentifiedCardNo;
		out["CountryCode"] = pChangeAccount->CountryCode;
		out["Address"] = pChangeAccount->Address;
		out["ZipCode"] = pChangeAccount->ZipCode;
		out["Telephone"] = pChangeAccount->Telephone;
		out["MobilePhone"] = pChangeAccount->MobilePhone;
		out["Fax"] = pChangeAccount->Fax;
		out["EMail"] = pChangeAccount->EMail;
		out["BankAccount"] = pChangeAccount->BankAccount;
		out["BankPassWord"] = pChangeAccount->BankPassWord;
		out["NewBankAccount"] = pChangeAccount->NewBankAccount;
		out["NewBankPassWord"] = pChangeAccount->NewBankPassWord;
		out["AccountID"] = pChangeAccount->AccountID;
		out["Password"] = pChangeAccount->Password;
		out["CurrencyID"] = pChangeAccount->CurrencyID;
		out["BrokerIDByBank"] = pChangeAccount->BrokerIDByBank;
		out["Digest"] = pChangeAccount->Digest;
		out["ErrorMsg"] = pChangeAccount->ErrorMsg;
		out["LongCustomerName"] = pChangeAccount->LongCustomerName;
		out["PlateSerial"] = pChangeAccount->PlateSerial;
		out["SessionID"] = pChangeAccount->SessionID;
		out["InstallID"] = pChangeAccount->InstallID;
		out["TID"] = pChangeAccount->TID;
		out["ErrorID"] = pChangeAccount->ErrorID;
		out["LastFragment"] = pChangeAccount->LastFragment;
		out["IdCardType"] = pChangeAccount->IdCardType;
		out["Gender"] = pChangeAccount->Gender;
		out["CustType"] = pChangeAccount->CustType;
		out["MoneyAccountStatus"] = pChangeAccount->MoneyAccountStatus;
		out["BankAccType"] = pChangeAccount->BankAccType;
		out["VerifyCertNoFlag"] = pChangeAccount->VerifyCertNoFlag;
		out["BankPwdFlag"] = pChangeAccount->BankPwdFlag;
		out["SecuPwdFlag"] = pChangeAccount->SecuPwdFlag;
	}
	jnaResCallback("T_OnRtnChangeAccountByBank", Base64_Encode(out.toStyledString()).c_str());
};


void CTraderSpi::OnRspQryClassifiedInstrument(CThostFtdcInstrumentField* pInstrument, CThostFtdcRspInfoField* pRspInfo, int nRequestID, bool bIsLast)
{
	Json::Value out;
	if (pInstrument)
	{
		out["InstrumentID"] = pInstrument->InstrumentID;
		out["ExchangeID"] = pInstrument->ExchangeID;
		out["InstrumentName"] = pInstrument->InstrumentName;
		out["ExchangeInstID"] = pInstrument->ExchangeInstID;
		out["ProductID"] = pInstrument->ProductID;
		out["CreateDate"] = pInstrument->CreateDate;
		out["OpenDate"] = pInstrument->OpenDate;
		out["ExpireDate"] = pInstrument->ExpireDate;
		out["StartDelivDate"] = pInstrument->StartDelivDate;
		out["EndDelivDate"] = pInstrument->EndDelivDate;
		out["UnderlyingInstrID"] = pInstrument->UnderlyingInstrID;
		out["DeliveryYear"] = pInstrument->DeliveryYear;
		out["DeliveryMonth"] = pInstrument->DeliveryMonth;
		out["MaxMarketOrderVolume"] = pInstrument->MaxMarketOrderVolume;
		out["MinMarketOrderVolume"] = pInstrument->MinMarketOrderVolume;
		out["MaxLimitOrderVolume"] = pInstrument->MaxLimitOrderVolume;
		out["MinLimitOrderVolume"] = pInstrument->MinLimitOrderVolume;
		out["VolumeMultiple"] = pInstrument->VolumeMultiple;
		out["IsTrading"] = pInstrument->IsTrading;
		out["ProductClass"] = pInstrument->ProductClass;
		out["InstLifePhase"] = pInstrument->InstLifePhase;
		out["PositionType"] = pInstrument->PositionType;
		out["PositionDateType"] = pInstrument->PositionDateType;
		out["MaxMarginSideAlgorithm"] = pInstrument->MaxMarginSideAlgorithm;
		out["OptionsType"] = pInstrument->OptionsType;
		out["CombinationType"] = pInstrument->CombinationType;
		out["PriceTick"] = pInstrument->PriceTick;
		out["LongMarginRatio"] = pInstrument->LongMarginRatio;
		out["ShortMarginRatio"] = pInstrument->ShortMarginRatio;
		out["StrikePrice"] = pInstrument->StrikePrice;
		out["UnderlyingMultiple"] = pInstrument->UnderlyingMultiple;
	}
	if (pRspInfo)
	{
		out["ErrorMsg"] = pRspInfo->ErrorMsg;
		out["ErrorID"] = pRspInfo->ErrorID;
	}
	out["nRequestID"] = nRequestID;
	out["bIsLast"] = bIsLast;
	jnaResCallback("T_OnRspQryClassifiedInstrument", Base64_Encode(out.toStyledString()).c_str());
};
