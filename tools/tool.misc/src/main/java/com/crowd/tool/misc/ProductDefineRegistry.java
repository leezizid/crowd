package com.crowd.tool.misc;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统中所有可交易的产品信息注册表
 */
public class ProductDefineRegistry {

	private final static Map<String, ProductDefine> registry = new HashMap<String, ProductDefine>();

	public final static void register(ProductDefine define) {
		registry.put(define.getName(), define);
	}

	public final static ProductDefine find(String name) {
		return registry.get(name);
	}

}
