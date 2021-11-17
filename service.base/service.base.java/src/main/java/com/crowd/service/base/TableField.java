package com.crowd.service.base;

public interface TableField {

	public String getName();

	public TableField setLength(int length);

	public TableField setPrimary(boolean primary);

	public TableField setDefault(Object defaultValue);

}
