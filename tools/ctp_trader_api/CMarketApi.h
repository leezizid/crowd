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

	//��ʼ��ϵͳ
	virtual const char* init(const char* dir, const char* marketFront, JnaResCallback jnaCallback);

	//����
	virtual const void release();

	//��¼
	virtual const int reqUserLogin(int requestID);

	//�ǳ�
	virtual const int reqUserLogout(int requestID);

	//����
	virtual const int subscribe(char** instrumentIDs, int count);

};




