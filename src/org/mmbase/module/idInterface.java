package org.mmbase.module;

public interface idInterface {
	public String getState(String userName,String name);
	public String getValue(String userName,String name);
	public String setValue(String userName,String name, String value);
	public String setState(String userName,String name, String value);
}
