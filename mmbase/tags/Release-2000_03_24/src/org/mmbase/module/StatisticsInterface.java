/*
*/

package org.mmbase.module;

import java.util.*;
import java.awt.*;

public interface StatisticsInterface {
	//public boolean JamesEvent(JamesEvent evt);
	public String setCount(String number,int inc);
	public String setAliasCount(String alias,int inc);
	public void NewStat(String name,String description,int timeslices,int timeinterval, int timeslice, String data, int inc);
	public String getCount(String number);
	public boolean countSimpleEvent(String eventname);
}
