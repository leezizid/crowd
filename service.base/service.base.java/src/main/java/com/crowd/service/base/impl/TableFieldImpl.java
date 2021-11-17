package com.crowd.service.base.impl;

import org.json.JSONObject;

import com.crowd.service.base.TableField;

public final class TableFieldImpl implements TableField {

	private String name;

	private boolean primary;

	private Object defaultValue;

	public String type;

	private int length;

	public TableFieldImpl(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public TableField setLength(int length) {
		this.length = length;
		return this;
	}

	public TableField setPrimary(boolean primary) {
		this.primary = primary;
		return this;
	}

	public TableField setDefault(Object defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public JSONObject toJSON() {
		JSONObject o = new JSONObject();
		o.put("name", name);
		o.put("type", type);
		o.put("length", length);
		o.put("primary", primary);
		o.put("defaultValue", defaultValue);
		return o;
	}

}
