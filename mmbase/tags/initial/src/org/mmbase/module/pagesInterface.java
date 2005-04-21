/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;


public interface pagesInterface {
	public void init();
	public pageInfo getPage(String page);
	public String getValue(pageInfo page,String wanted);
	public String setValue(pageInfo page,String key,String value);
}
