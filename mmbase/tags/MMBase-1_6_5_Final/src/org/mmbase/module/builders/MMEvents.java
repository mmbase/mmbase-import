/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.util.Date;
import java.sql.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * @author Daniel Ockeloen
 * @version $Id: MMEvents.java,v 1.11 2002-03-14 10:47:13 vpro Exp $
 */
public class MMEvents extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(MMEvents.class.getName());
	MMEventsProbe probe;
	DateStrings datestrings;
    private int notifyWindow=3600;
    private boolean enableNotify=true;

	public MMEvents() {
	}

	public boolean init() {
        String tmp;
        int nw;
		super.init();
		datestrings = new DateStrings(mmb.getLanguage());
        tmp=getInitParameter("NotifyWindow");
        if (tmp!=null) {
            try {
                nw=Integer.parseInt(tmp);
                notifyWindow=nw;
            } catch (NumberFormatException xx) {}
        }
        tmp=getInitParameter("EnableNotify");
        if (tmp!=null && (tmp.equals("false") || tmp.equals("no"))) {
            enableNotify=false;
        }
        if (enableNotify) probe=new MMEventsProbe(this);
		return true;
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
			return(datestrings.longmonth[DateSupport.getMonthInt(str)]);
		} else if (field.indexOf("month_")!=-1) {
			int str=(int)node.getIntValue(field.substring(6));
			return(datestrings.month[DateSupport.getMonthInt(str)]);
		} else if (field.indexOf("weekday_")!=-1) {
			int str=(int)node.getIntValue(field.substring(8));
			return(datestrings.longday[DateSupport.getWeekDayInt(str)]);
		} else if (field.indexOf("shortday_")!=-1) {
			int str=(int)node.getIntValue(field.substring(8));
			return(datestrings.day[DateSupport.getWeekDayInt(str)]);
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
		log.debug("MMEvent probe CALL");
		int now=(int)(DateSupport.currentTimeMillis()/1000);
		log.debug("The currenttime in seconds NOW="+now);
		MMObjectNode snode=null,enode=null;
		Enumeration e=search("WHERE start>"+now+" AND start<"+(now+(notifyWindow))+" ORDER BY start");
		if (e.hasMoreElements()) {
			snode=(MMObjectNode)e.nextElement();
			while (e.hasMoreElements()) {
				also.addElement(e.nextElement());
			}
		}
		e=search("WHERE stop>"+now+" AND stop<"+(now+(notifyWindow))+" ORDER BY stop");
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
			log.debug("SLEEPTIME="+(sleeptime-now)+" wnode="+wnode+" also="+also);
			try {
				Thread.sleep((sleeptime-now)*1000);
			} catch (InterruptedException f) {
				log.error("interrupted while sleeping");
			}
			log.debug("Node local change "+wnode.getIntValue("number"));
			super.nodeLocalChanged(mmb.getMachineName(),""+wnode.getIntValue("number"),tableName,"c");
			Enumeration g=also.elements();
			while (g.hasMoreElements()) {
				wnode=(MMObjectNode)g.nextElement();
				if ((wnode.getIntValue("start")==sleeptime) || (wnode.getIntValue("stop")==sleeptime)) {
					log.debug("Node local change "+wnode.getIntValue("number"));
					super.nodeLocalChanged(mmb.getMachineName(),""+wnode.getIntValue("number"),tableName,"c");
				}
			}
		} else {	
			try {
				Thread.sleep(300*1000);
			} catch (InterruptedException f) {
				log.error("interrupted while sleeping");
			}
		}
	}

	public int insert(String owner,MMObjectNode node) {
		int val=node.getIntValue("start");
		int newval=(int)(DateSupport.currentTimeMillis()/1000);
		if (val==-1) {
			node.setValue("start",newval);
			
		}
		val=node.getIntValue("stop");
		if (val==-1) {
			node.setValue("stop",newval);
			
		}
		return(super.insert(owner,node));
	}

	public boolean commit(MMObjectNode node) {
		int val=node.getIntValue("start");
		int newval=(int)(DateSupport.currentTimeMillis()/1000);
		if (val==-1) {
			node.setValue("start",newval);
			
		}
		val=node.getIntValue("stop");
		if (val==-1) {
			node.setValue("stop",newval);
			
		}
		return(super.commit(node));
	}
}
