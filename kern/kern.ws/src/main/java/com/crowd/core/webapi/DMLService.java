//package com.crowd.core.webapi;
//
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.Date;
//import java.util.List;
//
//import org.apache.commons.lang3.StringUtils;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//public class DMLService {
//
//	final static void executeUpdate(Context context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
//		String prepareSql = inputObject.getString("prepareSql").trim();
//		JSONArray arguments = inputObject.getJSONArray("arguments");
//		DBCommand command = initDBCommandBySQL(context, prepareSql, arguments);
//		try {
//			outputObject.put("count", command.executeUpdate());
//		} finally {
//			command.unuse();
//		}
//	}
//
//	final static void executeQuery(Context context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
//		String prepareSql = inputObject.getString("prepareSql").trim();
//		JSONArray arguments = inputObject.getJSONArray("arguments");
//		DBCommand command = initDBCommandBySQL(context, prepareSql, arguments);
//		try {
//			JSONArray rows = new JSONArray();
//			RecordSet rs = command.executeQuery();
//			while (rs.next()) {
//				JSONObject row = new JSONObject();
//				for (RecordSetField field : rs.getFields()) {
//					row.put(field.getName(), field.getObject() instanceof Date ? field.getDate() : field.getObject());
//				}
//				rows.put(row);
//			}
//			outputObject.put("rows", rows);
//		} finally {
//			command.unuse();
//		}
//	}
//
//	final static void insert(Context context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
//		TableDefine tableDefine = getTableDefine(context, inputObject);
//		JSONObject fieldValues = inputObject.getJSONObject("fieldValues");
//		List<String> fieldList = getFieldList(fieldValues);
//		StringBuffer dnaSqlBuffer = new StringBuffer(generateDNASQLDefine("insert", tableDefine, fieldList));
//		dnaSqlBuffer.append("begin\r\n");
//		dnaSqlBuffer.append("	insert into ");
//		dnaSqlBuffer.append(tableDefine.getName());
//		dnaSqlBuffer.append("(");
//		for (int i = 0; i < fieldList.size(); i++) {
//			dnaSqlBuffer.append(fieldList.get(i));
//			if (i < fieldValues.length() - 1) {
//				dnaSqlBuffer.append(",");
//			}
//		}
//		dnaSqlBuffer.append(") values(");
//		for (int i = 0; i < fieldList.size(); i++) {
//			dnaSqlBuffer.append("@p");
//			dnaSqlBuffer.append(i);
//			if (i < fieldValues.length() - 1) {
//				dnaSqlBuffer.append(",");
//			}
//		}
//		dnaSqlBuffer.append(")\r\n");
//		dnaSqlBuffer.append("end\r\n");
//		DBCommand command = context.prepareStatement(dnaSqlBuffer.toString());
//		try {
//			for (int i = 0; i < fieldList.size(); i++) {
//				Object value = fieldValues.get(fieldList.get(i));
//				command.setArgumentValue(i, value);
//			}
//			outputObject.put("count", command.executeUpdate());
//		} finally {
//			command.unuse();
//		}
//	}
//
//	final static void update(Context context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
//		TableDefine tableDefine = getTableDefine(context, inputObject);
//		JSONObject fieldValues = inputObject.getJSONObject("fieldValues");
//		List<String> fieldList = new ArrayList<String>();
//		String recidFieldName = null;
//		for (String fieldName : fieldValues.keySet()) {
//			if (fieldName.equalsIgnoreCase("RECID")) {
//				recidFieldName = fieldName;
//			} else {
//				fieldList.add(fieldName);
//			}
//		}
//		if (recidFieldName == null) {
//			throw new IllegalArgumentException("找不到RECIID键值");
//		}
//		fieldList.add(recidFieldName);
//		StringBuffer dnaSqlBuffer = new StringBuffer(generateDNASQLDefine("update", tableDefine, fieldList));
//		dnaSqlBuffer.append("begin\r\n");
//		dnaSqlBuffer.append("	update ");
//		dnaSqlBuffer.append(tableDefine.getName());
//		dnaSqlBuffer.append(" as t set ");
//		for (int i = 0; i < fieldList.size() - 1; i++) {
//			dnaSqlBuffer.append(fieldList.get(i));
//			dnaSqlBuffer.append(" = @p");
//			dnaSqlBuffer.append(i);
//			if (i < fieldValues.length() - 2) {
//				dnaSqlBuffer.append(", ");
//			}
//		}
//		dnaSqlBuffer.append(" where t.recid = @p");
//		dnaSqlBuffer.append(fieldList.size() - 1);
//		dnaSqlBuffer.append("\r\n");
//		dnaSqlBuffer.append("end\r\n");
//		DBCommand command = context.prepareStatement(dnaSqlBuffer.toString());
//		try {
//			for (int i = 0; i < fieldList.size(); i++) {
//				Object value = fieldValues.get(fieldList.get(i));
//				command.setArgumentValue(i, value);
//			}
//			outputObject.put("count", command.executeUpdate());
//		} finally {
//			command.unuse();
//		}
//	}
//
//	final static void delete(Context context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
//		TableDefine tableDefine = getTableDefine(context, inputObject);
//		JSONObject fieldValues = inputObject.getJSONObject("fieldValues");
//		List<String> fieldList = getFieldList(fieldValues);
//		if (fieldList.size() == 0) {
//			throw new IllegalArgumentException("不允许无参数的删除操作");
//		}
//		StringBuffer dnaSqlBuffer = new StringBuffer(generateDNASQLDefine("delete", tableDefine, fieldList));
//		dnaSqlBuffer.append("begin\r\n");
//		dnaSqlBuffer.append("	delete from ");
//		dnaSqlBuffer.append(tableDefine.getName());
//		dnaSqlBuffer.append(" as t");
//		if (fieldList.size() > 0) {
//			dnaSqlBuffer.append(" where ");
//			for (int i = 0; i < fieldList.size(); i++) {
//				dnaSqlBuffer.append("t.");
//				dnaSqlBuffer.append(fieldList.get(i));
//				dnaSqlBuffer.append(" = @p");
//				dnaSqlBuffer.append(i);
//				if (i < fieldValues.length() - 1) {
//					dnaSqlBuffer.append(" and ");
//				}
//			}
//		}
//		dnaSqlBuffer.append("\r\nend\r\n");
//		DBCommand command = context.prepareStatement(dnaSqlBuffer.toString());
//		try {
//			for (int i = 0; i < fieldList.size(); i++) {
//				Object value = fieldValues.get(fieldList.get(i));
//				command.setArgumentValue(i, value);
//			}
//			outputObject.put("count", command.executeUpdate());
//		} finally {
//			command.unuse();
//		}
//	}
//
//	final static void find(Context context, JSONObject inputObject, JSONObject outputObject) throws Throwable {
//		TableDefine tableDefine = getTableDefine(context, inputObject);
//		JSONObject fieldValues = inputObject.getJSONObject("fieldValues");
//		String orderInfo = inputObject.optString("orderInfo");
//		List<String> fieldList = getFieldList(fieldValues);
//		//
//		StringBuffer dnaSqlBuffer = new StringBuffer(generateDNASQLDefine("query", tableDefine, fieldList));
//		dnaSqlBuffer.append("begin\r\n");
//		dnaSqlBuffer.append("	select ");
//		int count = 0;
//		for (TableFieldDefine fieldDefine : tableDefine.getFields()) {
//			dnaSqlBuffer.append("t.");
//			dnaSqlBuffer.append(fieldDefine.getName());
//			if (count < tableDefine.getFields().size() - 1) {
//				dnaSqlBuffer.append(", ");
//			}
//			count++;
//		}
//		dnaSqlBuffer.append(" from ");
//		dnaSqlBuffer.append(tableDefine.getName());
//		dnaSqlBuffer.append(" as t");
//		if (fieldList.size() > 0) {
//			dnaSqlBuffer.append(" where ");
//			for (int i = 0; i < fieldList.size(); i++) {
//				dnaSqlBuffer.append("t.");
//				dnaSqlBuffer.append(fieldList.get(i));
//				dnaSqlBuffer.append(" = @p");
//				dnaSqlBuffer.append(i);
//				if (i < fieldValues.length() - 1) {
//					dnaSqlBuffer.append(" and ");
//				}
//			}
//		}
//		if (StringUtils.isNotEmpty(orderInfo)) {
//			dnaSqlBuffer.append(" order by ");
//			dnaSqlBuffer.append(orderInfo);
//		}
//		dnaSqlBuffer.append("\r\nend\r\n");
//		//
//		DBCommand command = context.prepareStatement(dnaSqlBuffer.toString());
//		try {
//			for (int i = 0; i < fieldList.size(); i++) {
//				Object value = fieldValues.get(fieldList.get(i));
//				command.setArgumentValue(i, value);
//			}
//			JSONArray rows = new JSONArray();
//			RecordSet rs = command.executeQuery();
//			while (rs.next()) {
//				JSONObject row = new JSONObject();
//				for (RecordSetField field : rs.getFields()) {
//					row.put(field.getName(), field.getObject() instanceof Date ? field.getDate() : field.getObject());
//				}
//				rows.put(row);
//			}
//			outputObject.put("rows", rows);
//		} finally {
//			command.unuse();
//		}
//	}
//
//	private final static DBCommand initDBCommandBySQL(Context context, String prepareSql, JSONArray arguments) {
//		String type = prepareSql.substring(0, prepareSql.indexOf(" ")).trim();
//		StringBuffer dnaSqlBuffer = new StringBuffer("define ");
//		dnaSqlBuffer.append(type.equals("select") ? "query" : type);
//		dnaSqlBuffer.append(" test(");
//		for (int i = 0; i < arguments.length(); i++) {
//			JSONObject argumentInfo = arguments.getJSONObject(i);
//			dnaSqlBuffer.append("@p");
//			dnaSqlBuffer.append(i);
//			dnaSqlBuffer.append(" ");
//			dnaSqlBuffer.append(argumentInfo.getString("dataType"));
//			if (i < arguments.length() - 1) {
//				dnaSqlBuffer.append(",");
//			}
//		}
//		dnaSqlBuffer.append(")\r\n");
//		dnaSqlBuffer.append("begin\r\n	");
//		int index = 0;
//		for (int i = 0; i < prepareSql.length(); i++) {
//			char c = prepareSql.charAt(i);
//			if (c == '?') {
//				dnaSqlBuffer.append("@p");
//				dnaSqlBuffer.append(index++);
//			} else {
//				dnaSqlBuffer.append(c);
//			}
//		}
//		dnaSqlBuffer.append("\r\n");
//		dnaSqlBuffer.append("end\r\n");
//		DBCommand command = context.prepareStatement(dnaSqlBuffer.toString());
//		for (int i = 0; i < arguments.length(); i++) {
//			JSONObject argumentInfo = arguments.getJSONObject(i);
//			String dataType = argumentInfo.getString("dataType");
//			if (argumentInfo.has("value")) {
//				Object value = argumentInfo.get("value");
//				if (dataType.equals("date")) {
//					value = new Date((long) value);
//				} else if (dataType.equals("blob")) {
//					value = Base64.getDecoder().decode((String) value);
//				}
//				command.setArgumentValue(i, value);
//			} else {
//				command.setArgumentValue(i, null);
//			}
//		}
//		return command;
//	}
//
//	private final static String getDataTypeName(DataType dataType) {
//		return dataType.isString() ? "string" : dataType.toString();
//	}
//
//	private final static TableDefine getTableDefine(Context context, JSONObject inputObject) {
//		String tableName = inputObject.getString("tableName");
//		TableDefine tableDefine = context.find(TableDefine.class, tableName);
//		if (tableDefine == null) {
//			throw new IllegalArgumentException("找不到表定义[" + tableName + "]");
//		}
//		return tableDefine;
//	}
//
//	private final static List<String> getFieldList(JSONObject fieldValues) {
//		List<String> fieldList = new ArrayList<String>();
//		for (String fieldName : fieldValues.keySet()) {
//			fieldList.add(fieldName);
//		}
//		return fieldList;
//	}
//
//	private final static String generateDNASQLDefine(String type, TableDefine tableDefine, List<String> fieldList) {
//		StringBuffer stringBuffer = new StringBuffer("define " + type + " test(");
//		for (int i = 0; i < fieldList.size(); i++) {
//			TableFieldDefine fieldDefine = tableDefine.findColumn(fieldList.get(i));
//			if (fieldDefine == null) {
//				throw new IllegalArgumentException("表[" + tableDefine.getName() + "]无字段[" + fieldList.get(i) + "]");
//			}
//			stringBuffer.append("@p");
//			stringBuffer.append(i);
//			stringBuffer.append(" ");
//			stringBuffer.append(getDataTypeName(fieldDefine.getType()));
//			if (i < fieldList.size() - 1) {
//				stringBuffer.append(",");
//			}
//		}
//		stringBuffer.append(")\r\n");
//		return stringBuffer.toString();
//	}
//
//}
