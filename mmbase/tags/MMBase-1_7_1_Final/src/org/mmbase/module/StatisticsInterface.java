/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;


public interface StatisticsInterface {
	//public boolean JamesEvent(JamesEvent evt);
	public String setCount(String number,int inc);
	public String setAliasCount(String alias,int inc);
	public void NewStat(String name,String description,int timeslices,int timeinterval, int timeslice, String data, int inc);
	public String getCount(String number);
	public boolean countSimpleEvent(String eventname);
}
