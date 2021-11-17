package com.crowd.tool.misc;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Products {

	private Map<String, ProductInfo> productInfos = new HashMap<String, ProductInfo>();

	private JSONArray productArray;

//	private final static Map<String, Products> allProducts = new HashMap<String, Products>();

	static {
//		try {
//			InputStream is = Products.class.getResourceAsStream("Products.json");
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			byte[] buffer = new byte[4096];
//			int readCount = 0;
//			while ((readCount = is.read(buffer)) > 0) {
//				baos.write(buffer, 0, readCount);
//			}
//			JSONArray arr = new JSONArray(new String(baos.toByteArray()));
//			for (int i = 0; i < arr.length(); i++) {
//				JSONObject o = arr.getJSONObject(i);
//				String id = o.getString("id");
//				Products products = new Products(o.getJSONArray("products"));
//				allProducts.put(id, products);
//			}
//		} catch (Throwable t) {
//			t.printStackTrace();
//		}
	}

//	public final static Products getProducts(JSONArray arr) {
//		return new Products()
//	}

	public final ProductInfo getProduct(String symbol) {
		return productInfos.get(symbol);
	}

	public Products(String content) {
		JSONArray arr = new JSONArray(content);
		if (arr != null) {
			for (int i = 0; i < arr.length(); i++) {
				JSONObject o = arr.getJSONObject(i);
				ProductInfo productInfo = new ProductInfo();
				productInfo.fromJSON(o);
				productInfos.put(productInfo.getSymbol(), productInfo);
			}
		}
		this.productArray = arr;
	}

	public JSONArray toJSONArray() {
		return productArray;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		for (int i = 0; i < productArray.length(); i++) {
			buffer.append(productArray.getJSONObject(i).getString("title"));
			if (i < productArray.length() - 1) {
				buffer.append(",");
			}
		}
		buffer.append(")");
		return buffer.toString();
	}

}
