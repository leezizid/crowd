package com.crowd.tool.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Products {

	private Map<String, ProductInfo> productInfos = new HashMap<String, ProductInfo>();

	private List<ProductInfo> productList = new ArrayList<ProductInfo>();

	public final ProductInfo getProduct(String symbol) {
		return productInfos.get(symbol);
	}

	public Products(String content, JSONObject mainDateInfos) {
		JSONArray arr = new JSONArray(content);
		if (arr != null) {
			for (int i = 0; i < arr.length(); i++) {
				JSONObject o = arr.getJSONObject(i);
				ProductInfo productInfo = new ProductInfo();
				productInfo.fromJSON(o);
				if (mainDateInfos != null) {
					productInfo.setDate(mainDateInfos.getString(productInfo.getProductDefine().getName()));
				}
				productInfos.put(productInfo.getSymbol(), productInfo);
				productList.add(productInfo);
			}
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		for (int i = 0; i < productList.size(); i++) {
			buffer.append(productList.get(i).getTitle());
			if (i < productList.size() - 1) {
				buffer.append(",");
			}
		}
		buffer.append(")");
		return buffer.toString();
	}

	public JSONArray toJSONArray() {
		JSONArray array = new JSONArray();
		for (int i = 0; i < productList.size(); i++) {
			JSONObject o = new JSONObject();
			o.put("symbol", productList.get(i).getSymbol());
			o.put("title", productList.get(i).getTitle());
			array.put(o);
		}
		return array;
	}

}
