#ifndef _LOG_H__
#define _LOG_H__

#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <string>
#include <time.h>
#include <windows.h>
#include <json.h>

Json::Value parseJsonString(const char* str);

typedef void(*JnaResCallback)(const char* type, const char* msg);

#endif