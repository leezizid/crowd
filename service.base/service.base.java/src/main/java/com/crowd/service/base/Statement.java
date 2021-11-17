package com.crowd.service.base;

import java.util.Date;

import com.crowd.service.type.GUID;

public interface Statement {

	public Statement addStringArgument(String v);

	public Statement addIntArgument(int v);

	public Statement addLongArgument(long v);

	public Statement addByteArgument(byte v);

	public Statement addBooleanArgument(boolean v);

	public Statement addFloatArgument(float v);

	public Statement addDoubleArgument(double v);

	public Statement addGUIDArgument(GUID v);

	public Statement addDateArgument(Date v);

}
