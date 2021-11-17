package com.crowd.service.base;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.impl.TableFieldImpl;
import com.crowd.service.base.impl.TableIndexImpl;

public class TableDefine {

	private String name;

	private String title;

	private List<TableFieldImpl> filedList = new ArrayList<TableFieldImpl>();

	private List<TableIndexImpl> indexList = new ArrayList<TableIndexImpl>();

	public TableDefine(String name, String title) {
		this.name = name;
		this.title = title;
	}

	public final String getName() {
		return this.name;
	}

	public final TableFieldImpl[] getFields() {
		return this.filedList.toArray(new TableFieldImpl[0]);
	}

	public TableField newStringField(String name, int length) {
		return newField(name, "string").setLength(length);
	}

	public TableField newBoolField(String name) {
		return newField(name, "boolean");
	}

	public TableField newGUIDField(String name) {
		return newField(name, "guid");
	}

	public TableField newByteField(String name) {
		return newField(name, "byte");
	}

	public TableField newIntField(String name) {
		return newField(name, "int");
	}

	public TableField newLongField(String name) {
		return newField(name, "long");
	}

	public TableField newFloatField(String name) {
		return newField(name, "float");
	}

	public TableField newDoubleField(String name) {
		return newField(name, "double");
	}

	public TableField newTextField(String name) {
		return newField(name, "text");
	}

	// public TableField newBlobField(String name) {
	// return newField(name, "blob");
	// }

	public TableField newDateField(String name) {
		return newField(name, "date");
	}

	private TableField newField(String name, String type) {
		TableFieldImpl f = new TableFieldImpl(name, type);
		filedList.add(f);
		return f;
	}

	public TableIndex newIndex(TableField field, TableField... fields) {
		TableIndexImpl index = new TableIndexImpl(field, fields);
		indexList.add(index);
		return index;
	}

	public JSONObject toJSON() {
		JSONObject o = new JSONObject();
		o.put("name", name);
		o.put("title", title);
		JSONArray fieldArray = new JSONArray();
		for (TableFieldImpl field : filedList) {
			fieldArray.put(field.toJSON());
		}
		o.put("fields", fieldArray);
		JSONArray indexArray = new JSONArray();
		for (TableIndexImpl index : indexList) {
			indexArray.put(index.toJSON());
		}
		o.put("indexes", indexArray);
		return o;
	}
}
