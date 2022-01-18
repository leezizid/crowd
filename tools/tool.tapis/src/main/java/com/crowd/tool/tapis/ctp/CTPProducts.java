package com.crowd.tool.tapis.ctp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CTPProducts {

	private final static List<CTPProduct> productList = new ArrayList<CTPProduct>();
	private final static Map<String, CTPProduct> products = new HashMap<String, CTPProduct>();

	static {
		// SHFE
		productList.add(new CTPProduct("au", "SHFE", "沪金", 1000, 2, ""));
		productList.add(new CTPProduct("ag", "SHFE", "沪银", 15, 0, ""));
		productList.add(new CTPProduct("cu", "SHFE", "沪铜", 5, 0, ""));
		productList.add(new CTPProduct("al", "SHFE", "沪铝", 5, 0, ""));
		productList.add(new CTPProduct("zn", "SHFE", "沪锌", 5, 0, ""));
		productList.add(new CTPProduct("ni", "SHFE", "沪镍", 1, 0, ""));
		productList.add(new CTPProduct("sn", "SHFE", "沪锡", 1, 0, ""));
		productList.add(new CTPProduct("pb", "SHFE", "沪铅", 5, 0, ""));
		productList.add(new CTPProduct("ss", "SHFE", "不锈钢", 5, 0, ""));
		productList.add(new CTPProduct("rb", "SHFE", "螺纹", 10, 0, ""));
		productList.add(new CTPProduct("hc", "SHFE", "热卷", 10, 0, ""));
		productList.add(new CTPProduct("wr", "SHFE", "线材", 10, 0, ""));
		productList.add(new CTPProduct("fu", "SHFE", "燃油", 10, 0, ""));
		productList.add(new CTPProduct("bu", "SHFE", "沥青", 10, 0, ""));
		productList.add(new CTPProduct("ru", "SHFE", "沪胶", 10, 0, ""));
		productList.add(new CTPProduct("sp", "SHFE", "纸浆", 10, 0, ""));

		// CFFEX
		productList.add(new CTPProduct("IF", "CFFEX", "沪深300", 300, 1, ""));
		productList.add(new CTPProduct("IH", "CFFEX", "上证50", 200, 1, ""));
		productList.add(new CTPProduct("IC", "CFFEX", "中证500", 200, 1, ""));
		productList.add(new CTPProduct("TS", "CFFEX", "二债", 10000, 3, ""));
		productList.add(new CTPProduct("TF", "CFFEX", "五债", 10000, 3, ""));
		productList.add(new CTPProduct("T", "CFFEX", "十债", 10000, 3, ""));

		// INE
		productList.add(new CTPProduct("sc", "INE", "原油", 1000, 1, ""));
		productList.add(new CTPProduct("lu", "INE", "低硫燃料油", 10, 0, ""));
		productList.add(new CTPProduct("nr", "INE", "20号胶", 10, 0, ""));
		productList.add(new CTPProduct("bc", "INE", "阴极铜", 5, 0, ""));

		// DCE
		productList.add(new CTPProduct("m", "DCE", "豆粕", 10, 0, ""));
		productList.add(new CTPProduct("y", "DCE", "豆油", 10, 0, ""));
		productList.add(new CTPProduct("a", "DCE", "豆一", 10, 0, ""));
		productList.add(new CTPProduct("b", "DCE", "豆二", 10, 0, ""));
		productList.add(new CTPProduct("p", "DCE", "棕榈", 10, 0, ""));
		productList.add(new CTPProduct("c", "DCE", "玉米", 10, 0, ""));
		productList.add(new CTPProduct("cs", "DCE", "淀粉", 10, 0, ""));
		productList.add(new CTPProduct("jd", "DCE", "鸡蛋", 5, 0, ""));
		productList.add(new CTPProduct("bb", "DCE", "胶板", 500, 2, ""));
		productList.add(new CTPProduct("fb", "DCE", "纤板", 10, 1, ""));
		productList.add(new CTPProduct("l", "DCE", "塑料", 5, 0, ""));
		productList.add(new CTPProduct("v", "DCE", "聚氯乙烯", 5, 0, ""));
		productList.add(new CTPProduct("eb", "DCE", "苯乙烯", 5, 0, ""));
		productList.add(new CTPProduct("pp", "DCE", "聚苯烯", 5, 0, ""));
		productList.add(new CTPProduct("j", "DCE", "焦炭", 100, 1, ""));
		productList.add(new CTPProduct("jm", "DCE", "焦煤", 60, 1, ""));
		productList.add(new CTPProduct("i", "DCE", "铁矿", 100, 1, ""));
		productList.add(new CTPProduct("eg", "DCE", "乙二醇", 10, 0, ""));
		productList.add(new CTPProduct("rr", "DCE", "梗米", 10, 0, ""));
		productList.add(new CTPProduct("pg", "DCE", "液化石油气", 20, 0, ""));
		productList.add(new CTPProduct("lh", "DCE", "生猪", 16, 0, ""));

		// CZCE
		productList.add(new CTPProduct("PK", "CZCE", "花生", 5, 0, ""));
		productList.add(new CTPProduct("PF", "CZCE", "短纤", 5, 0, ""));
		productList.add(new CTPProduct("SA", "CZCE", "纯碱", 20, 0, ""));
		productList.add(new CTPProduct("UR", "CZCE", "尿素", 20, 0, ""));
		productList.add(new CTPProduct("CJ", "CZCE", "红枣", 5, 0, ""));
		productList.add(new CTPProduct("CF", "CZCE", "棉花", 5, 0, ""));
		productList.add(new CTPProduct("CY", "CZCE", "棉纱", 5, 0, ""));
		productList.add(new CTPProduct("SR", "CZCE", "白糖", 10, 0, ""));
		productList.add(new CTPProduct("RS", "CZCE", "菜籽", 5, 0, ""));
		productList.add(new CTPProduct("OI", "CZCE", "菜油", 10, 0, ""));
		productList.add(new CTPProduct("RM", "CZCE", "菜粕", 10, 0, ""));
		productList.add(new CTPProduct("ZC", "CZCE", "动煤", 100, 1, ""));
		productList.add(new CTPProduct("MA", "CZCE", "甲醇", 10, 0, ""));
		productList.add(new CTPProduct("TA", "CZCE", "PTA", 5, 0, ""));
		productList.add(new CTPProduct("FG", "CZCE", "玻璃", 20, 0, ""));
		productList.add(new CTPProduct("SF", "CZCE", "硅铁", 5, 0, ""));
		productList.add(new CTPProduct("SM", "CZCE", "锰硅", 5, 0, ""));
		productList.add(new CTPProduct("AP", "CZCE", "苹果", 10, 0, ""));
		productList.add(new CTPProduct("WH", "CZCE", "强麦", 20, 0, ""));
		productList.add(new CTPProduct("PM", "CZCE", "普麦", 50, 0, ""));
		productList.add(new CTPProduct("RI", "CZCE", "早稻", 20, 0, ""));
		productList.add(new CTPProduct("LR", "CZCE", "晚稻", 20, 0, ""));
		productList.add(new CTPProduct("JR", "CZCE", "梗稻", 20, 0, ""));

		//
		for (CTPProduct p : productList) {
			products.put(p.getName(), p);
		}

	}
	
	
	public final static CTPProduct find(String name) {
		return products.get(name);
	}
}
