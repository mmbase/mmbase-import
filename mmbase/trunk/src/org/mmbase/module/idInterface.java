/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;

public interface idInterface {
	public String getState(String userName,String name);
	public String getValue(String userName,String name);
	public String setValue(String userName,String name, String value);
	public String setState(String userName,String name, String value);
}
