#pragma once
#include "stdafx.h"
#include <list>
#include <iostream>
#include <string>
#include <stdio.h>
#include <windows.h>
#include <time.h>
#include "ThostFtdcMdApi.h"
#include "DataCollect.h"
#include <conio.h>
#include "CMarketSpi.h"
#include <vector>
#include <map>
#include <json.h>
using namespace std;


class CMarketApi {

public:
	CMarketApi() {};
	~CMarketApi() {};

private:
	CThostFtdcMdApi* marketApi;
	CMarketSpi* marketSpi;

public:

	//初始化系统
	virtual const char* init(const char* dir, const char* marketFront, JnaResCallback jnaCallback);

	//销毁
	virtual const void release();

	//登录
	virtual const int reqUserLogin(int requestID);

	//登出
	virtual const int reqUserLogout(int requestID);

	//订阅
	virtual const int subscribe(char** instrumentIDs, int count);

};




