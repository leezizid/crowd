package com.crowd.tool.tapis.ctp;

import com.crowd.tool.misc.ProductDefine;
import com.crowd.tool.misc.ProductDefineRegistry;

public class CTPProducts {

	static final String SHFE = "SHFE";
	static final String CFFEX = "CFFEX";
	static final String INE = "INE";
	static final String DCE = "DCE";
	static final String CZCE = "CZCE";
	static final String MARKET_TIME_1 = "01:00-02:15~02:30-03:30~05:30-07:00";
	static final String MARKET_TIME_2 = "01:30-03:30~05:00-07:00,";
	static final String MARKET_TIME_3 = "01:00-02:15~02:30-03:30~05:30-07:00,13:00-15:00";
	static final String MARKET_TIME_4 = "01:00-02:15~02:30-03:30~05:30-07:00,13:00-17:00";
	static final String MARKET_TIME_5 = "01:00-02:15~02:30-03:30~05:30-07:00,13:00-18:30";

	static {
		// SHFE
		ProductDefineRegistry.register(new ProductDefine("au", SHFE, "沪金", 1000, 2, MARKET_TIME_5));
		ProductDefineRegistry.register(new ProductDefine("ag", SHFE, "沪银", 15, 0, MARKET_TIME_5));
		ProductDefineRegistry.register(new ProductDefine("cu", SHFE, "沪铜", 5, 0, MARKET_TIME_4));
		ProductDefineRegistry.register(new ProductDefine("al", SHFE, "沪铝", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("zn", SHFE, "沪锌", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("ni", SHFE, "沪镍", 1, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("sn", SHFE, "沪锡", 1, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("pb", SHFE, "沪铅", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("ss", SHFE, "不锈钢", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("rb", SHFE, "螺纹", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("hc", SHFE, "热卷", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("wr", SHFE, "线材", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("fu", SHFE, "燃油", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("bu", SHFE, "沥青", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("ru", SHFE, "沪胶", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("sp", SHFE, "纸浆", 10, 0, ""));

		// CFFEX
		ProductDefineRegistry.register(new ProductDefine("IF", CFFEX, "沪深300", 300, 1, ""));
		ProductDefineRegistry.register(new ProductDefine("IH", CFFEX, "上证50", 200, 1, ""));
		ProductDefineRegistry.register(new ProductDefine("IC", CFFEX, "中证500", 200, 1, ""));
		ProductDefineRegistry.register(new ProductDefine("TS", CFFEX, "二债", 10000, 3, ""));
		ProductDefineRegistry.register(new ProductDefine("TF", CFFEX, "五债", 10000, 3, ""));
		ProductDefineRegistry.register(new ProductDefine("T", CFFEX, "十债", 10000, 3, ""));

		// INE
		ProductDefineRegistry.register(new ProductDefine("sc", INE, "原油", 1000, 1, ""));
		ProductDefineRegistry.register(new ProductDefine("lu", INE, "低硫燃料油", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("nr", INE, "20号胶", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("bc", INE, "阴极铜", 5, 0, ""));

		// DCE
		ProductDefineRegistry.register(new ProductDefine("m", DCE, "豆粕", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("y", DCE, "豆油", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("a", DCE, "豆一", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("b", DCE, "豆二", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("p", DCE, "棕榈", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("c", DCE, "玉米", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("cs", DCE, "淀粉", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("jd", DCE, "鸡蛋", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("bb", DCE, "胶板", 500, 2, ""));
		ProductDefineRegistry.register(new ProductDefine("fb", DCE, "纤板", 10, 1, ""));
		ProductDefineRegistry.register(new ProductDefine("l", DCE, "塑料", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("v", DCE, "聚氯乙烯", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("eb", DCE, "苯乙烯", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("pp", DCE, "聚苯烯", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("j", DCE, "焦炭", 100, 1, ""));
		ProductDefineRegistry.register(new ProductDefine("jm", DCE, "焦煤", 60, 1, MARKET_TIME_3));
		ProductDefineRegistry.register(new ProductDefine("i", DCE, "铁矿", 100, 1, ""));
		ProductDefineRegistry.register(new ProductDefine("eg", DCE, "乙二醇", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("rr", DCE, "梗米", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("pg", DCE, "液化石油气", 20, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("lh", DCE, "生猪", 16, 0, ""));

		// CZCE
		ProductDefineRegistry.register(new ProductDefine("PK", CZCE, "花生", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("PF", CZCE, "短纤", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("SA", CZCE, "纯碱", 20, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("UR", CZCE, "尿素", 20, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("CJ", CZCE, "红枣", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("CF", CZCE, "棉花", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("CY", CZCE, "棉纱", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("SR", CZCE, "白糖", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("RS", CZCE, "菜籽", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("OI", CZCE, "菜油", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("RM", CZCE, "菜粕", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("ZC", CZCE, "动煤", 100, 1, ""));
		ProductDefineRegistry.register(new ProductDefine("MA", CZCE, "甲醇", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("TA", CZCE, "PTA", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("FG", CZCE, "玻璃", 20, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("SF", CZCE, "硅铁", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("SM", CZCE, "锰硅", 5, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("AP", CZCE, "苹果", 10, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("WH", CZCE, "强麦", 20, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("PM", CZCE, "普麦", 50, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("RI", CZCE, "早稻", 20, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("LR", CZCE, "晚稻", 20, 0, ""));
		ProductDefineRegistry.register(new ProductDefine("JR", CZCE, "梗稻", 20, 0, ""));

	}
	
	public final static void init() {
		
	}

	public final static ProductDefine find(String name) {
		ProductDefine product = ProductDefineRegistry.find(name);
		if (product != null) {
			return product;
		}
		if (name.length() > 4) {
			product = ProductDefineRegistry.find(name.substring(0, name.length() - 4));
			if (product != null) {
				return product;
			}
			product = ProductDefineRegistry.find(name.substring(0, name.length() - 3));
			if (product != null) {
				return product;
			}
		}
		return null;
	}

}
