package com.crowd.tool.tapis.ctp;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class CtpInstruments {

	private final static Map<String, BigDecimal> multipliers = new HashMap<String, BigDecimal>();

	private final static Map<String, String> exchangeNames = new HashMap<String, String>();

	static {
		// SHFE
		multipliers.put("au", new BigDecimal(1000)); // 沪金
		exchangeNames.put("au", "SHFE");
		multipliers.put("ag", new BigDecimal(15)); // 沪银
		exchangeNames.put("ag", "SHFE");
		multipliers.put("cu", new BigDecimal(5)); // 沪铜
		exchangeNames.put("cu", "SHFE");
		multipliers.put("al", new BigDecimal(5)); // 沪铝
		exchangeNames.put("al", "SHFE");
		multipliers.put("zn", new BigDecimal(5)); // 沪锌
		exchangeNames.put("zn", "SHFE");
		multipliers.put("ni", new BigDecimal(1)); // 沪镍
		exchangeNames.put("ni", "SHFE");
		multipliers.put("sn", new BigDecimal(1)); // 沪锡
		exchangeNames.put("sn", "SHFE");
		multipliers.put("pb", new BigDecimal(5)); // 沪铅
		exchangeNames.put("pb", "SHFE");
		multipliers.put("ss", new BigDecimal(5)); // 不锈钢
		exchangeNames.put("ss", "SHFE");
		multipliers.put("rb", new BigDecimal(10)); // 螺纹
		exchangeNames.put("rb", "SHFE");
		multipliers.put("hc", new BigDecimal(10)); // 热卷
		exchangeNames.put("hc", "SHFE");
		multipliers.put("wr", new BigDecimal(10)); // 线材
		exchangeNames.put("wr", "SHFE");
		multipliers.put("fu", new BigDecimal(10)); // 燃油
		exchangeNames.put("fu", "SHFE");
		multipliers.put("bu", new BigDecimal(10)); // 沥青
		exchangeNames.put("bu", "SHFE");
		multipliers.put("ru", new BigDecimal(10)); // 沪胶
		exchangeNames.put("ru", "SHFE");
		multipliers.put("sp", new BigDecimal(10)); // 纸浆
		exchangeNames.put("sp", "SHFE");

		// CFFEX
		multipliers.put("IF", new BigDecimal(300)); // IF沪深300
		exchangeNames.put("IF", "CFFEX");
		multipliers.put("IH", new BigDecimal(300)); // 上证50
		exchangeNames.put("IH", "CFFEX");
		multipliers.put("IC", new BigDecimal(200)); // 中证500
		exchangeNames.put("IC", "CFFEX");
		multipliers.put("TS", new BigDecimal(10000)); // 二债
		exchangeNames.put("TS", "CFFEX");
		multipliers.put("TF", new BigDecimal(10000)); // 五债
		exchangeNames.put("TF", "CFFEX");
		multipliers.put("T", new BigDecimal(10000)); // 十债
		exchangeNames.put("T", "CFFEX");

		// INE
		multipliers.put("sc", new BigDecimal(1000)); // 原油
		exchangeNames.put("sc", "INE");
		multipliers.put("lu", new BigDecimal(10)); // 低硫燃料油
		exchangeNames.put("lu", "INE");
		multipliers.put("nr", new BigDecimal(10)); // 20号胶
		exchangeNames.put("nr", "INE");
		multipliers.put("bc", new BigDecimal(5)); // 阴极铜
		exchangeNames.put("bc", "INE");

		// DCE
		multipliers.put("m", new BigDecimal(10)); // 豆粕
		exchangeNames.put("m", "DCE");
		multipliers.put("y", new BigDecimal(10)); // 豆油
		exchangeNames.put("y", "DCE");
		multipliers.put("a", new BigDecimal(10)); // 豆一
		exchangeNames.put("a", "DCE");
		multipliers.put("b", new BigDecimal(10)); // 豆二
		exchangeNames.put("b", "DCE");
		multipliers.put("p", new BigDecimal(10)); // 棕榈
		exchangeNames.put("p", "DCE");
		multipliers.put("c", new BigDecimal(10)); // 玉米
		exchangeNames.put("c", "DCE");
		multipliers.put("cs", new BigDecimal(10)); // 淀粉
		exchangeNames.put("cs", "DCE");
		multipliers.put("jd", new BigDecimal(5)); // 鸡蛋
		exchangeNames.put("jd", "DCE");
		multipliers.put("bb", new BigDecimal(500)); // 胶板
		exchangeNames.put("bb", "DCE");
		multipliers.put("fb", new BigDecimal(10)); // 纤板
		exchangeNames.put("fb", "DCE");
		multipliers.put("l", new BigDecimal(5)); // 塑料
		exchangeNames.put("l", "DCE");
		multipliers.put("v", new BigDecimal(5)); // 聚氯乙烯PVC
		exchangeNames.put("v", "DCE");
		multipliers.put("eb", new BigDecimal(5)); // 苯乙烯
		exchangeNames.put("eb", "DCE");
		multipliers.put("pp", new BigDecimal(5)); // 聚苯烯
		exchangeNames.put("pp", "DCE");
		multipliers.put("j", new BigDecimal(100)); // 焦炭
		exchangeNames.put("j", "DCE");
		multipliers.put("jm", new BigDecimal(60)); // 焦煤
		exchangeNames.put("jm", "DCE");
		multipliers.put("i", new BigDecimal(100)); // 铁矿
		exchangeNames.put("i", "DCE");
		multipliers.put("eg", new BigDecimal(10)); // 乙二醇
		exchangeNames.put("eg", "DCE");
		multipliers.put("rr", new BigDecimal(10)); // 梗米
		exchangeNames.put("rr", "DCE");
		multipliers.put("pg", new BigDecimal(20)); // 液化石油气
		exchangeNames.put("pg", "DCE");
		multipliers.put("lh", new BigDecimal(16)); // 生猪
		exchangeNames.put("lh", "DCE");

		// CZCE
		multipliers.put("PK", new BigDecimal(5)); // 花生
		exchangeNames.put("PK", "CZCE");
		multipliers.put("PF", new BigDecimal(5)); // 短纤
		exchangeNames.put("PF", "CZCE");
		multipliers.put("SA", new BigDecimal(20)); // 纯碱
		exchangeNames.put("SA", "CZCE");
		multipliers.put("UR", new BigDecimal(20)); // 尿素
		exchangeNames.put("UR", "CZCE");
		multipliers.put("CJ", new BigDecimal(5)); // 红枣
		exchangeNames.put("CJ", "CZCE");
		multipliers.put("CF", new BigDecimal(5)); // 棉花
		exchangeNames.put("CF", "CZCE");
		multipliers.put("CY", new BigDecimal(5)); // 棉纱
		exchangeNames.put("CY", "CZCE");
		multipliers.put("SR", new BigDecimal(10)); // 白糖
		exchangeNames.put("SR", "CZCE");
		multipliers.put("RS", new BigDecimal(5)); // 菜籽
		exchangeNames.put("RS", "CZCE");
		multipliers.put("OI", new BigDecimal(10)); // 菜油
		exchangeNames.put("OI", "CZCE");
		multipliers.put("RM", new BigDecimal(10)); // 菜粕
		exchangeNames.put("RM", "CZCE");
		multipliers.put("ZC", new BigDecimal(100));// 动煤
		exchangeNames.put("ZC", "CZCE");
		multipliers.put("MA", new BigDecimal(10)); // 甲醇
		exchangeNames.put("MA", "CZCE");
		multipliers.put("TA", new BigDecimal(5)); // PTA
		exchangeNames.put("TA", "CZCE");
		multipliers.put("FG", new BigDecimal(20)); // 玻璃
		exchangeNames.put("FG", "CZCE");
		multipliers.put("SF", new BigDecimal(5)); // 硅铁
		exchangeNames.put("SF", "CZCE");
		multipliers.put("SM", new BigDecimal(5)); // 锰硅
		exchangeNames.put("SM", "CZCE");
		multipliers.put("AP", new BigDecimal(10)); // 苹果
		exchangeNames.put("AP", "CZCE");
		multipliers.put("WH", new BigDecimal(20)); // 强麦
		exchangeNames.put("WH", "CZCE");
		multipliers.put("PM", new BigDecimal(50)); // 普麦
		exchangeNames.put("PM", "CZCE");
		multipliers.put("RI", new BigDecimal(20)); // RI早稻
		exchangeNames.put("RI", "CZCE");
		multipliers.put("LR", new BigDecimal(20)); // LR晚稻
		exchangeNames.put("LR", "CZCE");
		multipliers.put("JR", new BigDecimal(20)); // JR梗稻
		exchangeNames.put("JR", "CZCE");

	}

	public final static String getExchange(String instrumentID) {
		String productCode = instrumentID.substring(0, instrumentID.length() - 4);
		if (exchangeNames.containsKey(productCode)) {
			return exchangeNames.get(productCode);
		} else {
			throw new IllegalStateException("找不到合约" + instrumentID + "的产品信息");
		}
	}

	public final static BigDecimal getInstrumentMultiplier(String symbol) {
		String instrumentID = StringUtils.split(symbol, ".")[1];
		String productCode = instrumentID.substring(0, instrumentID.length() - 4);
		if (multipliers.containsKey(productCode)) {
			return multipliers.get(productCode);
		} else {
			throw new IllegalStateException("找不到合约" + symbol + "的产品信息");
		}
	}

}
