#include "Base64.h"

typedef unsigned char byte;
const byte BASE64_STANDARD_ENCODE[] = { "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=" };
const byte BASE64_BIN[] = { 128,64,32,16,8,4,2,1 };


std::string ByteToBin8(byte aByte) {
	return std::bitset<8>(aByte).to_string();
}

byte Bin8ToByte(std::string aStr) {
	byte rByte = 0;
	for (int i = 0; i < 8; ++i) if (aStr[i] == '1') rByte += BASE64_BIN[i];
	return rByte;
}

std::string Bin8ToBin6(std::string aStr) {
	return "00" + aStr.substr(0, 6) + "00" + aStr.substr(6, 6) + "00" + aStr.substr(12, 6) + "00" + aStr.substr(18, 6);
}

std::string Bin6ToBin8(std::string aStr) {
	return aStr.substr(2, 6) + aStr.substr(10, 6) + aStr.substr(18, 6) + aStr.substr(26, 6);
}

std::string Encode(std::string aStr, int Type) {
	std::string Bin6 = "";
	switch (Type) {
	case 1:
		Bin6 = "00" + ByteToBin8(aStr[0]).substr(0, 6) + "00" + ByteToBin8(aStr[0]).substr(6, 2) + "00000100000001000000";
		break;
	case 2:
		Bin6 = "00" + ByteToBin8(aStr[0]).substr(0, 6) + "00" + ByteToBin8(aStr[0]).substr(6, 2) + ByteToBin8(aStr[1]).substr(0, 4) + "00" + ByteToBin8(aStr[1]).substr(4, 4) + "0001000000";
		break;
	default:
		Bin6 = Bin8ToBin6(ByteToBin8(aStr[0]) + ByteToBin8(aStr[1]) + ByteToBin8(aStr[2]));
		break;
	}
	return  std::string(1, BASE64_STANDARD_ENCODE[Bin8ToByte(Bin6.substr(0, 8))]) +
		std::string(1, BASE64_STANDARD_ENCODE[Bin8ToByte(Bin6.substr(8, 8))]) +
		std::string(1, BASE64_STANDARD_ENCODE[Bin8ToByte(Bin6.substr(16, 8))]) +
		std::string(1, BASE64_STANDARD_ENCODE[Bin8ToByte(Bin6.substr(24, 8))]);
}

std::string Base64_Encode(std::string toEncode) {
	std::string rStr = "";
	for (int i = 0, ni = toEncode.size(); i < ni; i += 3) {
		switch (ni - i)
		{
		case 1:
			rStr += Encode(toEncode.substr(i, 1), 1);
			break;
		case 2:
			rStr += Encode(toEncode.substr(i, 2), 2);
			break;
		default:
			rStr += Encode(toEncode.substr(i, 3), 3);
			break;
		}
	}
	return rStr;
}
int findDict(byte b) {
	for (int i = 0; i < 65; ++i)
		if (b == BASE64_STANDARD_ENCODE[i])
			return i;
	return -1;
}
std::string Decode(std::string aStr) {
	std::string Bin8 = ByteToBin8(findDict(aStr[0])) + ByteToBin8(findDict(aStr[1])) + ByteToBin8(findDict(aStr[2])) + ByteToBin8(findDict(aStr[3]));
	std::string rStr = "";
	if (Bin8[17] == '1') {
		Bin8 = Bin8.substr(2, 6) + Bin8.substr(10, 2);
		rStr = std::string(1, Bin8ToByte(Bin8));
	}
	else if (Bin8[25] == '1') {
		Bin8 = Bin8.substr(2, 6) + Bin8.substr(10, 6) + Bin8.substr(18, 4);
		rStr = std::string(1, Bin8ToByte(Bin8.substr(0, 8))) + std::string(1, Bin8ToByte(Bin8.substr(8, 8)));
	}
	else {
		Bin8 = Bin8.substr(2, 6) + Bin8.substr(10, 6) + Bin8.substr(18, 6) + Bin8.substr(26, 6);
		rStr = std::string(1, Bin8ToByte(Bin8.substr(0, 8))) + std::string(1, Bin8ToByte(Bin8.substr(8, 8))) + std::string(1, Bin8ToByte(Bin8.substr(16, 8)));
	}
	return rStr;
}
std::string Base64_Decode(std::string toDecode) {
	std::string rStr = "";
	for (int i = 0, ni = toDecode.size(); i < ni; i += 4) {
		rStr += Decode(toDecode.substr(i, 4));
	}
	return rStr;
}