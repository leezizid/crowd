package com.crowd.tool.tapis.ctp;

import java.util.ArrayList;
import java.util.List;

import com.crowd.tool.misc.ProductDefine;
import com.crowd.tool.misc.ProductDefineRegistry;

public class CTPProducts {

	static final String SHFE = "SHFE";
	static final String CFFEX = "CFFEX";
	static final String INE = "INE";
	static final String DCE = "DCE";
	static final String CZCE = "CZCE";
	static final String MARKET_TIME_1 = "01:00-02:15~02:30-03:30~05:30-07:00"; // 不含夜盘，10点15分停盘15分钟
	static final String MARKET_TIME_2 = "01:30-03:30~05:00-07:00,"; // 股指期货
	static final String MARKET_TIME_3 = "01:00-02:15~02:30-03:30~05:30-07:00,13:00-15:00"; // 含夜盘到23点:00
	static final String MARKET_TIME_4 = "01:00-02:15~02:30-03:30~05:30-07:00,13:00-17:00"; // 含夜盘到01点:00
	static final String MARKET_TIME_5 = "01:00-02:15~02:30-03:30~05:30-07:00,13:00-18:30"; // 含夜盘到2点:30

	static ProductDefine[] allProducts;

	static {
		List<ProductDefine> productList = new ArrayList<ProductDefine>();
		// SHFE
		productList.add(new ProductDefine("au", SHFE, "沪金", 1000, 2, MARKET_TIME_5));
		productList.add(new ProductDefine("ag", SHFE, "沪银", 15, 0, MARKET_TIME_5));
		productList.add(new ProductDefine("cu", SHFE, "沪铜", 5, 0, MARKET_TIME_4));
		productList.add(new ProductDefine("al", SHFE, "沪铝", 5, 0, MARKET_TIME_4));
		productList.add(new ProductDefine("zn", SHFE, "沪锌", 5, 0, MARKET_TIME_4));
		productList.add(new ProductDefine("ni", SHFE, "沪镍", 1, 0, MARKET_TIME_4));
		productList.add(new ProductDefine("sn", SHFE, "沪锡", 1, 0, MARKET_TIME_4));
		productList.add(new ProductDefine("pb", SHFE, "沪铅", 5, 0, MARKET_TIME_4));
		productList.add(new ProductDefine("ss", SHFE, "不锈钢", 5, 0, MARKET_TIME_4));
		productList.add(new ProductDefine("rb", SHFE, "螺纹", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("hc", SHFE, "热卷", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("wr", SHFE, "线材", 10, 0, MARKET_TIME_1));
		productList.add(new ProductDefine("fu", SHFE, "燃油", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("bu", SHFE, "沥青", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("ru", SHFE, "沪胶", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("sp", SHFE, "纸浆", 10, 0, MARKET_TIME_3));
		//productList.add(new ProductDefine("ao", SHFE, "氧化铝", 20, 0, MARKET_TIME_4));

		// CFFEX
		productList.add(new ProductDefine("IF", CFFEX, "沪深300", 300, 1, MARKET_TIME_2));
		productList.add(new ProductDefine("IH", CFFEX, "上证50", 300, 1, MARKET_TIME_2));
		productList.add(new ProductDefine("IC", CFFEX, "中证500", 200, 1, MARKET_TIME_2));
		productList.add(new ProductDefine("IM", CFFEX, "中证1000", 200, 1, MARKET_TIME_2));
		productList.add(new ProductDefine("TS", CFFEX, "二债", 10000, 3, MARKET_TIME_2));
		productList.add(new ProductDefine("TF", CFFEX, "五债", 10000, 3, MARKET_TIME_2));
		productList.add(new ProductDefine("T", CFFEX, "十债", 10000, 3, MARKET_TIME_2));

		// INE
		productList.add(new ProductDefine("sc", INE, "原油", 1000, 1, MARKET_TIME_5));
		productList.add(new ProductDefine("lu", INE, "低硫燃料油", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("nr", INE, "20号胶", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("bc", INE, "阴极铜", 5, 0, MARKET_TIME_4));

		// DCE
		productList.add(new ProductDefine("m", DCE, "豆粕", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("y", DCE, "豆油", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("a", DCE, "豆一", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("b", DCE, "豆二", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("p", DCE, "棕榈", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("c", DCE, "玉米", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("cs", DCE, "淀粉", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("jd", DCE, "鸡蛋", 5, 0, MARKET_TIME_1));
		productList.add(new ProductDefine("bb", DCE, "胶板", 500, 2, MARKET_TIME_1));// XXX:
		productList.add(new ProductDefine("fb", DCE, "纤板", 10, 1, MARKET_TIME_1));
		productList.add(new ProductDefine("l", DCE, "塑料", 5, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("v", DCE, "聚氯乙烯", 5, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("eb", DCE, "苯乙烯", 5, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("pp", DCE, "聚苯烯", 5, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("j", DCE, "焦炭", 100, 1, MARKET_TIME_3));
		productList.add(new ProductDefine("jm", DCE, "焦煤", 60, 1, MARKET_TIME_3));
		productList.add(new ProductDefine("i", DCE, "铁矿", 100, 1, MARKET_TIME_3));
		productList.add(new ProductDefine("eg", DCE, "乙二醇", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("rr", DCE, "梗米", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("pg", DCE, "液化石油气", 20, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("lh", DCE, "生猪", 16, 0, MARKET_TIME_1));

		// CZCE
		productList.add(new ProductDefine("PK", CZCE, "花生", 5, 0, MARKET_TIME_1));
		productList.add(new ProductDefine("PF", CZCE, "短纤", 5, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("SA", CZCE, "纯碱", 20, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("UR", CZCE, "尿素", 20, 0, MARKET_TIME_1));
		productList.add(new ProductDefine("CJ", CZCE, "红枣", 5, 0, MARKET_TIME_1));
		productList.add(new ProductDefine("CF", CZCE, "棉花", 5, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("CY", CZCE, "棉纱", 5, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("SR", CZCE, "白糖", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("RS", CZCE, "菜籽", 5, 0, MARKET_TIME_1));// XXX:
		productList.add(new ProductDefine("OI", CZCE, "菜油", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("RM", CZCE, "菜粕", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("MA", CZCE, "甲醇", 10, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("TA", CZCE, "PTA", 5, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("FG", CZCE, "玻璃", 20, 0, MARKET_TIME_3));
		productList.add(new ProductDefine("SF", CZCE, "硅铁", 5, 0, MARKET_TIME_1));
		productList.add(new ProductDefine("SM", CZCE, "锰硅", 5, 0, MARKET_TIME_1));
		productList.add(new ProductDefine("AP", CZCE, "苹果", 10, 0, MARKET_TIME_1));
		productList.add(new ProductDefine("WH", CZCE, "强麦", 20, 0, MARKET_TIME_1));// XXX:
		productList.add(new ProductDefine("PM", CZCE, "普麦", 50, 0, MARKET_TIME_1));// XXX:
		productList.add(new ProductDefine("RI", CZCE, "早稻", 20, 0, MARKET_TIME_1));// XXX:
		productList.add(new ProductDefine("LR", CZCE, "晚稻", 20, 0, MARKET_TIME_1));// XXX:
		productList.add(new ProductDefine("JR", CZCE, "梗稻", 20, 0, MARKET_TIME_1));// XXX:
		productList.add(new ProductDefine("ZC", CZCE, "动煤", 100, 1, MARKET_TIME_3));// XXX:

		//
		for (ProductDefine productDefine : productList) {
			ProductDefineRegistry.register(productDefine);
		}
		//
		allProducts = productList.toArray(new ProductDefine[0]);
	}

	public final static void init() {

	}

	public final static ProductDefine find(String name) {
		ProductDefine product = ProductDefineRegistry.find(name);
		if (product != null) {
			return product;
		}
		if (name.indexOf(".") != -1) {
			name = name.substring(name.indexOf(".") + 1);
		}
		if (name.length() > 4) {
			try {
				Integer.parseInt(name.substring(name.length() - 4));
				product = ProductDefineRegistry.find(name.substring(0, name.length() - 4));
				if (product != null) {
					return product;
				}
			} catch (Throwable t) {
				product = ProductDefineRegistry.find(name.substring(0, name.length() - 3));
				if (product != null) {
					return product;
				}
			}
		}
		return null;
	}

	public final static ProductDefine[] getAllProducts() {
		return allProducts;
	}
}
