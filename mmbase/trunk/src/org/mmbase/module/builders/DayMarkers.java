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

import org.mmbase.util.logging.*;

/**
 * @author Daniel Ockeloen,Rico Jansen
 * @version $Id: DayMarkers.java,v 1.21 2001-12-03 15:47:07 vpro Exp $
 */
public class DayMarkers extends MMObjectBuilder {

	private static Logger log = Logging.getLoggerInstance(DayMarkers.class.getName()); 

	private int day = 0; // current day number/count
	//private Hashtable daycache = new Hashtable(); 	// day -> mark
	private TreeMap daycache = new TreeMap();           // day -> mark, but ordered

	private int smallestMark = 0; // will be queried when this builder is started
	private int smallestDay  = 0; // will be queried when this builder is started

	/**
	 * Put in cache. This function essentially does the casting to
	 * Integer and wrapping in 'synchronized' for you. 
	 */
	private void cachePut(int day, int mark) {
		synchronized(daycache) {
			daycache.put(new Integer(day),new Integer(mark)); 
		}
	}

	public DayMarkers() {		
		day = currentDay();

	}

	public boolean init() {
		log.debug("Init of DayMarkers");
		boolean result;
		result = super.init();
		try {
			MultiConnection con = mmb.getConnection();
			Statement stmt=con.createStatement();		

			ResultSet rs=stmt.executeQuery("select "+mmb.getDatabase().getAllowedField("number")+", daycount from " + mmb.baseName + "_" + tableName + " order by "+mmb.getDatabase().getAllowedField("number"));
			if (rs.next()) {
				smallestMark   = rs.getInt(1);
				smallestDay    = rs.getInt(2);
			} else {
			    smallestDay = currentDay();
			    createMarker();
			    //smallestMark = 0;
			}
			stmt.close();
			con.close();
		} catch (SQLException e) {
			log.error("SQL Exception " + e + ". Could not find smallestMarker, smallestDay");
			result = false;
		}
		return result;
	}

	/**
	 * The current time in days
	 */

	private int currentDay() {
		return (int)(DateSupport.currentTimeMillis()/(1000*60*60*24));
	}

	
	// if it only calls super, it does not have to be overrided, does it?
	//public boolean commit(MMObjectNode node) {
	//	boolean res=super.commit(node);
	//	return(res);
	//}

	/**
	 * Creates a mark in the database, if necessary.
	 */

	private void createMarker() {
		int max  = -1;
		int mday = -1;
		log.info("Daymarker -> DAY="+day);
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select "+mmb.getDatabase().getAllowedField("number")+" from "+mmb.baseName+"_"+tableName+" where daycount="+day);
			if (rs.next()) {
				mday=rs.getInt(1);
			}
			stmt.close();
			con.close();
		} catch (Exception e) {
			log.error(Logging.stackTrace(e));
		}

		if (mday<0) { // it was not in the database
			log.info("DayMarker inserting new marker " + day);
			try {
				MultiConnection con=mmb.getConnection();
				Statement stmt=con.createStatement();
				ResultSet rs = stmt.executeQuery("select max("+mmb.getDatabase().getAllowedField("number")+") from "+mmb.baseName+"_object");
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
			log.info("DayMarker marker already exists " + day);
		}
	}

	/**
	 * This gets called every hour to see if the day has past.
	*/
	public void probe() {
		int newday;
		newday=currentDay();
		//debug("Days "+newday+" current "+day);
		if (newday>day) {
			day = newday;
			createMarker();
		}
	}

	/** 
	 * Returns the age, in days, of a node. So, this does the inverse of most methods in this 
	 * class. It converts a node number (which is like a mark) to a day.
	 */

	public int getAge(MMObjectNode node) {
			
		int nodeNumber = node.getIntValue("number");
		// first, check if it accidentily can be found with the cache:
		Set days = daycache.entrySet(); 
		Iterator i = days.iterator();
		if (i.hasNext()) {   // cache not empty
			Map.Entry current = (Map.Entry)i.next();
			Map.Entry previous = null;
			while (i.hasNext() && ((Integer)current.getValue()).intValue() < nodeNumber) { // search until current > nodeNumber
				previous = current;
				current = (Map.Entry)i.next();
			}			
			if ((previous != null) && ((Integer)current.getValue()).intValue() >= nodeNumber) { // found in cache
				// if we found a lower and a higher mark on two consecutive days, return the lower.
				if (((Integer)current.getKey()).intValue() - ((Integer)previous.getKey()).intValue() == 1) {  
					return day - ((Integer)previous.getKey()).intValue();
				}
			}
			
		}
		log.debug("Could not find with daycache " + nodeNumber + ", searching in database now");


		MultiConnection con=mmb.getConnection();
		if (con==null) { log.error("Could not get connection to database"); return(-1);} 
		try {
			Statement stmt=con.createStatement();
			String query = "select mark, daycount from " + mmb.baseName + "_" + tableName + " where mark < "+ nodeNumber + " order by daycount desc";
			// mark < in stead of mark = will of course only be used in database with are not on line always, such 
			// that some days do not have a mark.
			log.debug(query);
			ResultSet rs=stmt.executeQuery(query);
			// search the first daycount of which' mark is lower.
			// that must be the day which we were searching (at least a good estimate)
			if (rs.next()) {
				int mark = rs.getInt(1);
				int daycount = rs.getInt(2);			   
				cachePut(daycount, mark);   // found one, could as well cache it
				getDayCount(daycount + 1);  // next time, this can be count with the cache as well
				stmt.close();
				con.close();
				return(day - daycount);
			} else {
				// hmm, strange, perhaps we have to seek the oldest daycount, but for the moment:
				log.error("daycount could not be found");
				stmt.close();
				con.close();
				return 0; // very old.						   
			}

		} catch(java.sql.SQLException e) {
			log.error(Logging.stackTrace(e));
			return(-1);
		}
		
	}

	/**
	 * The current day count, that is, the time in days, of today.
	 **/
	public int getDayCount() {
		return (day);
	}

	/**
	 * Given an age, this function returns a mark, _not a day count_.
	 * @param daysold a time in days ago.
	 **/

	public int getDayCountAge(int daysold) {
		int wday = day - daysold;
		return(getDayCount(wday));
	}


	/**
	 * This function has nothing to do with getDayCount(). 
	 *
	 * @param day A time in days
	 * @return    The mark on that day
	 */
	private int getDayCount(int wday) {

		log.debug("finding mark of day " + wday);
		Integer result = (Integer)daycache.get(new Integer(wday)); 
		if (result!=null) { // already in cache
			return(result.intValue());
		}
		log.debug("could not be found in cache");


		if (wday < smallestDay) { // will not be possible to find in database
			if (log.isDebugEnabled() ) {
				log.debug("Day " + wday + " is smaller than smallest in database");
			}
			return 0;
		}
		
		if (mmb==null) return(-1);
		if (wday<=day) {
			try {
				MultiConnection con=mmb.getConnection();
				if (con==null) return(-1);
				Statement stmt=con.createStatement();
				ResultSet rs=stmt.executeQuery("select mark, daycount from "+mmb.baseName+"_daymarks where daycount >= " + wday + " order by daycount");
				if (rs.next()) {
					int tmp=rs.getInt(1);
					int founddaycount = rs.getInt(2);
					if (founddaycount != wday) { 
						log.error("Could not find day " + wday + ", surrogated with " + founddaycount);						
					} else {
						log.debug("Found in db, will be inserted in cache");
					}
					cachePut(wday, tmp);
					stmt.close();
					con.close();					
					return(tmp);
				} else {
					log.error("Could not find mark of day " + wday);
					stmt.close();
					con.close();					
					return 0; // but it must be relativily new.
				}
			} catch(Exception e) {
				log.error("Could not find mark of day " + wday);
				// cachePut(wday, 0);
				return(0);
			}
		} else {
			return(Integer.MAX_VALUE);
		}
		//cachePut(wday, 0);
		//return(0);
	}

	/**
	 * Scan. Knows the tokens, COUNT, COUNTAGE, COUNTMONTH, COUNTPREVMONTH, COUNTNEXTMONTH
	 * etc.
	 *
	 **/
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
			} else if (token.equals("TIMETOOBJECTNUMBER")){
				ival=fetchIntValue(command);
				rtn=""+getDayCount(ival/86400);			} else {
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

	private Calendar getCalendarDays(int days) {
		GregorianCalendar cal=new GregorianCalendar();
		java.util.Date d=new java.util.Date(((long)days)*24*3600*1000);
		cal.setTime(d);
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

	public int getDayCountByObject(int number) {
		int mday=0;
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select max(daycount) from "+mmb.baseName+"_"+tableName+" where mark<"+number);
			if (rs.next()) {
				mday=rs.getInt(1);
			}
			stmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return(mday);
	}

	public int getMonthsByDayCount(int daycount) {
		int months=0;
		int year,month;
		Calendar calendar;
		
		calendar=getCalendarDays(daycount);
		year=calendar.get(Calendar.YEAR)-1970;
		month=calendar.get(Calendar.MONTH);
		months=month+year*12;
		return(months);
	}
}
