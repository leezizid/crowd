#include "Tool.h"

using namespace std;



Json::Value parseJsonString(const char* str)
{
	Json::CharReaderBuilder b;
	Json::CharReader* reader(b.newCharReader());
	Json::Value root;

	JSONCPP_STRING errs;
	bool ok = reader->parse(str, str + std::strlen(str), &root, &errs);
	if (!ok || errs.size() > 0)
	{
		string s = str;
		throw string("parseJsonString error"); 
	}
	delete reader;
	return root;
}

