/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class DayMarkers extends MMObjectBuilder {

	public int day=0;
	Hashtable daycache=new Hashtable();
	boolean setmarker=false;

	public DayMarkers() {
		day=daycount();
	}

	public int daycount() {
		int time=(int)(DateSupport.currentTimeMillis()/1000);
		return(time/(3600*24));
	}

	

	public boolean commit(MMObjectNode node) {
		boolean res=super.commit(node);
		return(res);
	}


	public void createMarker() {
		if (setmarker) return;
		int max=-1;
		int mday=-1;
		System.out.println("Daymarker -> DAY="+day);
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select number from "+mmb.baseName+"_"+tableName+" where daycount="+day);
			if (rs.next()) {
				mday=rs.getInt(1);
			}
			stmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (mday<0) {
			System.out.println("DayMarker inserting new marker "+day);
			try {
				MultiConnection con=mmb.getConnection();
				Statement stmt=con.createStatement();
				ResultSet rs=stmt.executeQuery("select max(number) from "+mmb.baseName+"_object");
				if (rs.next()) {
					max=rs.getInt(1);
				}
				stmt.close();
				con.close();
				MMObjectNode node=getNewNode("system");
				node.setValue("daycount",day);
				node.setValue("mark",max);
				insert("system",node);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("DayMarker marker already exists "+day);
		}
		setmarker=true;
	}

	public int getAge(MMObjectNode node) {
		/*
		return(node.getAge());
		*/
		// still has to be implemented with a cache !!!

		return(-1);
	}

	public int getDayCountAge(int daysold) {
		int wday=day-daysold;
		return(getDayCount(wday));
	}

	public int getDayCount(int wday) {
		Integer result=(Integer)daycache.get(new Integer(wday));
		if (result!=null) {
			return(result.intValue());
		}
		if (mmb==null) return(-1);
		if (wday<=day) {
			try {
				MultiConnection con=mmb.getConnection();
				if (con==null) return(-1);
				Statement stmt=con.createStatement();
				ResultSet rs=stmt.executeQuery("select mark from "+mmb.baseName+"_daymarks where daycount="+(wday));
				if (rs.next()) {
					int tmp=rs.getInt(1);
					daycache.put(new Integer(wday),new Integer(tmp));
					stmt.close();
					con.close();
					return(tmp);
				}
				stmt.close();
				con.close();
			} catch(Exception e) {
				daycache.put(new Integer(wday),new Integer(1));
				return(1);
			}
		} else {
			return(Integer.MAX_VALUE);
		}
		daycache.put(new Integer(wday),new Integer(1));
		return(1);
	}


    public String replace(scanpage sp, StringTokenizer command) {
		String rtn="";
		int ival;
		if (command.hasMoreTokens()) {
            String token=command.nextToken();
            if (token.equals("COUNT")) {
				ival=fetchIntValue(command);
				rtn=""+getDayCount(ival);
			} else if (token.equals("COUNTAGE")) {
				ival=fetchIntValue(command);
				rtn=""+getDayCountAge(ival);
			} else if (token.equals("COUNTMONTH")) {
				ival=fetchIntValue(command);
				rtn=""+getDayCount(getDayCountMonth(ival));
			} else if (token.equals("COUNTNEXTMONTH")) {
				ival=fetchIntValue(command);
				rtn=""+getDayCount(getDayCountNextMonth(ival));
			} else if (token.equals("COUNTPREVMONTH")) {
				ival=fetchIntValue(command);
				rtn=""+getDayCount(getDayCountPreviousMonth(ival));
			} else if (token.equals("COUNTPREVDELTAMONTH")) {
				ival=fetchIntValue(command);
				int delta=0-fetchIntValue(command);
				rtn=""+getDayCount(getDayCountDeltaMonth(ival,delta));
			} else if (token.equals("COUNTNEXTDELTAMONTH")) {
				ival=fetchIntValue(command);
				int delta=fetchIntValue(command);
				rtn=""+getDayCount(getDayCountDeltaMonth(ival,delta));
			} else {
				rtn="UnknownCommand";
			}
        }
		return(rtn);
    }

	private int fetchIntValue(StringTokenizer command) {
		String val;
		int ival;
		if (command.hasMoreTokens()) {
			val=command.nextToken();
		} else {
			val="0";
		}
		try {
			ival=Integer.parseInt(val);
		} catch (NumberFormatException e) {
			ival=0;
		}
		return(ival);
	}

	private Calendar getCalendarMonths(int months) {
		int year,month;
		year=months/12;
		month=months%12;
		GregorianCalendar cal=new GregorianCalendar();
		cal.set(year+1970,month,1,0,0,0);
		return(cal);
	}

	private int getDayCountMonth(int months) {
		Calendar cal=getCalendarMonths(months);
		return((int)((cal.getTime().getTime())/(24*3600*1000)));
	}

	private int getDayCountPreviousMonth(int months) {
		Calendar cal=getCalendarMonths(months);
		cal.add(Calendar.MONTH,-1);
		return((int)((cal.getTime().getTime())/(24*3600*1000)));
	}

	private int getDayCountNextMonth(int months) {
		Calendar cal=getCalendarMonths(months);
		cal.add(Calendar.MONTH,1);
		return((int)((cal.getTime().getTime())/(24*3600*1000)));
	}

	private int getDayCountDeltaMonth(int months,int delta) {
		Calendar cal=getCalendarMonths(months);
		cal.add(Calendar.MONTH,delta);
		return((int)((cal.getTime().getTime())/(24*3600*1000)));
	}

}
