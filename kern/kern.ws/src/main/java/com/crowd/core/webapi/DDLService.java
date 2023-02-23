package com.crowd.core.webapi;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.lang3.StringUtils;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
public class DDLService {
//
//	final static void refactor(Context context, JSONArray tableArray) throws Throwable {
//		for (int i = 0; i < tableArray.length(); i++) {
//			JSONObject o = tableArray.getJSONObject(i);
//			String tableName = o.getString("name");
//			String tableTitle = o.getString("title");
//			JSONArray fields = o.getJSONArray("fields");
//			JSONArray indexes = o.getJSONArray("indexes");
//			syncTable(context, tableName, tableTitle, fields, indexes);
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	private static void syncTable(Context context, String tableName, String tableTitle, JSONArray fields,
//			JSONArray indexes) {
//		TableDeclare tableDeclare = context.find(TableDeclare.class, tableName);
//		tableDeclare.setCategory("");
//		tableDeclare.setTitle(tableTitle);
//		ModifiableNamedElementContainer<TableFieldDeclare> fieldList = (ModifiableNamedElementContainer<TableFieldDeclare>) tableDeclare
//				.getFields();
//		ModifiableNamedElementContainer<IndexDeclare> indexList = (ModifiableNamedElementContainer<IndexDeclare>) tableDeclare
//				.getIndexes();
//		boolean needPost = false;
//		// 处理新字段或者更新字段（暂不支持删除字段）
//		for (int i = 0; i < fields.length(); i++) {
//			if (checkFieldInfoNeedPost(tableDeclare, fieldList, fields.getJSONObject(i))) {
//				needPost = true;
//			}
//		}
//		// 删除不需要的索引，建立新索引
//		Map<String, Boolean> indexesInfo = new HashMap<String, Boolean>();
//		for (int i = 0; i < indexes.length(); i++) {
//			JSONObject o = indexes.getJSONObject(i);
//			indexesInfo.put(o.getString("filedNames"), o.optBoolean("unique"));
//		}
//		int maxIndexNumber = 0;
//		List<IndexDeclare> removingIndexList = new ArrayList<IndexDeclare>();
//		for (IndexDeclare indexDeclare : indexList) {
//			try {
//				int n = Integer.parseInt(indexDeclare.getName().substring(4));
//				if (maxIndexNumber < n) {
//					maxIndexNumber = n;
//				}
//			} catch (Throwable t) {
//
//			}
//			List<String> itemNameList = new ArrayList<String>();
//			for (IndexItemDeclare indexItem : indexDeclare.getItems()) {
//				itemNameList.add(indexItem.getField().getName());
//			}
//			String key = StringUtils.join(itemNameList, ",");
//			if (indexesInfo.containsKey(key)) {
//				boolean isUnique = indexesInfo.remove(key);
//				if (indexDeclare.isUnique() != isUnique) {
//					indexDeclare.setUnique(isUnique);
//					needPost = true;
//				}
//			} else {
//				removingIndexList.add(indexDeclare);
//			}
//		}
//		if (removingIndexList.size() > 0) {
//			for (IndexDeclare indexDeclare : removingIndexList) {
//				indexList.remove(indexDeclare);
//			}
//			needPost = true;
//		}
//		for (String filedNames : indexesInfo.keySet()) {
//			IndexDeclare indexDeclare = tableDeclare.newIndex("IDX_" + (++maxIndexNumber));
//			for (String fieldName : StringUtils.split(filedNames, ",")) {
//				TableFieldDeclare fieldDeclare = fieldList.find(fieldName);
//				if (fieldDeclare == null) {
//					throw new IllegalArgumentException("索引[" + filedNames + "]中指定的字段[" + fieldName + "]不存在");
//				}
//				indexDeclare.addItem(fieldDeclare);
//			}
//			indexDeclare.setUnique(indexesInfo.get(filedNames));
//			needPost = true;
//		}
//		//
//		if (needPost) {
//			PublishTableDefineTask publishTableDefineTask = new PublishTableDefineTask(tableDeclare);
//			context.handle(publishTableDefineTask);
//		}
//	}
//
//	private static final boolean checkFieldInfoNeedPost(TableDeclare tableDeclare,
//			ModifiableNamedElementContainer<TableFieldDeclare> fieldList, JSONObject fieldInfo) {
//		boolean needPost = false;
//		String name = fieldInfo.getString("name");
//		String title = fieldInfo.optString("title");
//		boolean isPrimary = fieldInfo.optBoolean("primary");
//		Object defaultValue = fieldInfo.opt("defaultValue");
//		//
//		TableFieldDeclare fieldDeclare = fieldList.find(name);
//		DataType dateType = getDataType(fieldInfo);
//		if (fieldDeclare == null) {
//			fieldDeclare = tableDeclare.newField(name, dateType);
//			fieldDeclare.setTitle(title);
//			fieldDeclare.setPrimaryKey(isPrimary);
//			fieldDeclare.setDefault(defaultValue);
//			needPost = true;
//		} else {
//			if (fieldDeclare.getType().getRootType() != dateType.getRootType()) {
//				throw new UnsupportedOperationException("暂不支持字段类型转换");
//			}
//			if (fieldDeclare.getType().isString()) {
//				if (fieldDeclare.getType() != dateType) {
//					fieldDeclare.adjustType(dateType);
//					needPost = true;
//				}
//			}
//			if (!fieldDeclare.getTitle().equals(title)) {
//				fieldDeclare.setTitle(title);
//				needPost = true;
//			}
//			if (fieldDeclare.isPrimaryKey() != isPrimary) {
//				fieldDeclare.setPrimaryKey(isPrimary);
//				needPost = true;
//			}
//			if ((fieldDeclare.getDefault() == null && defaultValue != null) || (fieldDeclare.getDefault() != null
//					&& !fieldDeclare.getDefault().getObject().equals(defaultValue))) {
//				fieldDeclare.setDefault(defaultValue);
//				needPost = true;
//			}
//		}
//
//		return needPost;
//	}
//
//	private final static DataType getDataType(JSONObject fieldInfo) {
//		String type = fieldInfo.getString("type");
//		if ("string".equals(type)) {
//			return TypeFactory.VARCHAR(fieldInfo.getInt("length"));
//		}
//		if ("boolean".equals(type)) {
//			return TypeFactory.BOOLEAN;
//		}
//		if ("byte".equals(type)) {
//			return TypeFactory.BYTE;
//		}
//		if ("int".equals(type)) {
//			return TypeFactory.INT;
//		}
//		if ("long".equals(type)) {
//			return TypeFactory.LONG;
//		}
//		if ("float".equals(type)) {
//			return TypeFactory.FLOAT;
//		}
//		if ("double".equals(type)) {
//			return TypeFactory.DOUBLE;
//		}
//		if ("guid".equals(type)) {
//			return TypeFactory.GUID;
//		}
//		if ("date".equals(type)) {
//			return TypeFactory.DATE;
//		}
//		if ("text".equals(type)) {
//			return TypeFactory.TEXT;
//		}
//		// if ("blob".equals(type)) {
//		// return TypeFactory.BLOB;
//		// }
//		throw new UnsupportedOperationException("不支持的数据类型[" + type + "]");
//	}
//
}
