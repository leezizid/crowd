package com.crowd.service.base.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.crowd.service.base.TableField;
import com.crowd.service.base.TableIndex;

public final class TableIndexImpl implements TableIndex {

	// private String name;

	private boolean unique;

	private List<String> fieldList;

	public TableIndexImpl(TableField field, TableField... fields) {
		// this.name = name;
		this.fieldList = new ArrayList<String>();
		this.fieldList.add(field.getName());
		for (TableField f : fields) {
			this.fieldList.add(f.getName());
		}
	}

	public TableIndex setUnique(boolean unique) {
		this.unique = unique;
		return this;
	}

	public JSONObject toJSON() {
		JSONObject o = new JSONObject();
		// o.put("name", name);
		o.put("unique", unique);
		o.put("filedNames", StringUtils.join(fieldList, ","));
		return o;
	}

}
