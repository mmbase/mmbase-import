
/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.util.Date;
import java.sql.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class MMEvents extends MMObjectBuilder {

	MMEventsProbe probe;

	public MMEvents() {
	}

	
	public String getGUIIndicator(MMObjectNode node) {
		int tmp=node.getIntValue("start");
		//String str=DateSupport.getMonthDay(tmp)+"/"+DateSupport.getMonth(tmp)+"/"+DateSupport.getYear(tmp);
		String str=DateSupport.getTime(tmp)+"/"+DateSupport.getMonthDay(tmp)+"/"+DateSupport.getMonth(tmp)+"/"+DateSupport.getYear(tmp);
			return(str);
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("start")) {
			int str=node.getIntValue("start");
			return(DateSupport.getTimeSec(str)+" op "+DateSupport.getMonthDay(str)+"/"+DateSupport.getMonth(str)+"/"+DateSupport.getYear(str));
		} else if (field.equals("stop")) {
			int str=node.getIntValue("stop");
			return(DateSupport.getTimeSec(str)+" op "+DateSupport.getMonthDay(str)+"/"+DateSupport.getMonth(str)+"/"+DateSupport.getYear(str));
		} else if (field.equals("playtime")) {
			int str=node.getIntValue("playtime");
			return(DateSupport.getTimeSecLen(str));
		}
		return(null);
	}

	public Object getValue(MMObjectNode node,String field) {
		if (field.indexOf("time_")!=-1) {
			int str=(int)node.getIntValue(field.substring(5));
			return(DateSupport.getTime(str));
		} else if (field.equals("time(start)")) {
			node.prefix="mmevents.";
			int str=(int)node.getIntValue("start");
			node.prefix="";
			return(DateSupport.getTime(str));
		} else if (field.equals("time(stop)")) {
			node.prefix="mmevents.";
			int str=(int)node.getIntValue("stop");
			node.prefix="";
			return(DateSupport.getTime(str));
		} else if (field.indexOf("timesec_")!=-1) {
			int str=(int)node.getIntValue(field.substring(8));
			return(DateSupport.getTimeSec(str));
		} else if (field.indexOf("longmonth_")!=-1) {
			int str=(int)node.getIntValue(field.substring(10));
			return(DateStrings.Dutch_longmonths[DateSupport.getMonthInt(str)]);
		} else if (field.indexOf("month_")!=-1) {
			int str=(int)node.getIntValue(field.substring(6));
			return(DateStrings.Dutch_months[DateSupport.getMonthInt(str)]);
		} else if (field.indexOf("weekday_")!=-1) {
			int str=(int)node.getIntValue(field.substring(8));
			return(DateStrings.Dutch_longdays[DateSupport.getWeekDayInt(str)]);
		} else if (field.indexOf("shortday_")!=-1) {
			int str=(int)node.getIntValue(field.substring(8));
			return(DateStrings.Dutch_days[DateSupport.getWeekDayInt(str)]);
		} else if (field.indexOf("day_")!=-1) {
			int str=(int)node.getIntValue(field.substring(4));
			return(""+DateSupport.getDayInt(str));
		} else if (field.indexOf("year_")!=-1) {
			int str=(int)node.getIntValue(field.substring(5));
			return(DateSupport.getYear(str));
		} 
		return(super.getValue(node,field));
	}

	public void probeCall() {
		// the queue is really a bad idea have to make up
		// a better way.
		Vector also=new Vector();
		System.out.println("MMEvent probe CALL");
		int now=(int)(DateSupport.currentTimeMillis()/1000);
		System.out.println("NOW="+now);
		MMObjectNode snode=null,enode=null;
		Enumeration e=search("WHERE start>"+now+" AND start<"+(now+(3600*2))+" ORDER BY start");
		if (e.hasMoreElements()) {
			snode=(MMObjectNode)e.nextElement();
			while (e.hasMoreElements()) {
				also.addElement(e.nextElement());
			}
		}
		e=search("WHERE stop>"+now+" AND stop<"+(now+(3600*2))+" ORDER BY stop");
		if (e.hasMoreElements()) {
			enode=(MMObjectNode)e.nextElement();
			while (e.hasMoreElements()) {
				also.addElement(e.nextElement());
			}
		}

		MMObjectNode wnode=null;
		int sleeptime=-1;
		if (snode!=null && enode==null) { 
			sleeptime=snode.getIntValue("start");
			wnode=snode;
		}
		if (snode==null && enode!=null) {
			sleeptime=enode.getIntValue("stop");
			wnode=enode;
		}		
		if (snode!=null && enode!=null) {
			if (snode.getIntValue("start")<enode.getIntValue("stop")) {
				sleeptime=snode.getIntValue("start");
				wnode=snode;
			} else {
				sleeptime=enode.getIntValue("stop");
				wnode=enode;
			}
		}
	
		if (sleeptime!=-1) {	
			System.out.println("SLEEPTIME="+(sleeptime-now)+" wnode="+wnode+" also="+also);
			try {Thread.sleep((sleeptime-now)*1000);} catch (InterruptedException f){}
			wnode.commit();	
			Enumeration g=also.elements();
			while (g.hasMoreElements()) {
				wnode=(MMObjectNode)g.nextElement();
				if ((wnode.getIntValue("start")==sleeptime) || (wnode.getIntValue("stop")==sleeptime)) wnode.commit();
			}
		} else {	
			try {Thread.sleep(300*1000);} catch (InterruptedException f){}
		}
	}
}

