/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import java.text.DateFormat;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.database.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.*;

/**
 * Daymarkers are used to calculate the age of MMBase objects.
 * Every day a daymarker is added to the daymarks table. Such an entry
 * consists of a daycount (number of days from 1970), and a count 
 * (current object number of that day). 
 *
 * @sql
 * @author Daniel Ockeloen,Rico Jansen
 * @author Michiel Meeuwissen
 * @version $Id: DayMarkers.java,v 1.31 2004-01-06 20:28:03 michiel Exp $
 */
public class DayMarkers extends MMObjectBuilder {

    private static final Logger log = Logging.getLoggerInstance(DayMarkers.class);

    private int day = 0; // current day number/count
    private TreeMap daycache = new TreeMap();           // day -> mark, but ordered

    public static String FIELD_DAYCOUNT =   "daycount";

    private int smallestMark; // will be queried when this builder is started
    private int smallestDay; // will be queried when this builder is started

    /**
     * Put in cache. This function essentially does the casting to
     * Integer and wrapping in 'synchronized' for you.
     */
    private void cachePut(int day, int mark) {
        synchronized(daycache) {
            daycache.put(new Integer(day), new Integer(mark));
        }
    }

    /**
     * set the current day. This is the number of days from 1970.
     */
    public DayMarkers() {
        day = currentDay();
    }

    /**
     * Calculate smallestMark, and smallestDay.
     * smallestMark is the smallest object number for which a daymark exists.
     * smallestDay is the first daymarker that was set.
     * @sql
     */
    public boolean init() {
        log.debug("Init of DayMarkers");
        boolean result;
        result = super.init();
        smallestMark = 0;
        smallestDay  = 0;

        try {
            NodeSearchQuery query = new NodeSearchQuery(this);
            FieldDefs fieldDefs = getField("number");
            StepField field = query.getField(fieldDefs);
            BasicSortOrder sortOrder = query.addSortOrder(field);
            query.setMaxNumber(1);
            List resultList = getNodes(query);
            if (resultList.size() > 0) {
                MMObjectNode mark = (MMObjectNode) resultList.get(0);
                smallestMark = mark.getIntValue("number");
                smallestDay  = mark.getIntValue("daycount");
            }
            
            if (smallestDay == 0) {
                smallestDay = currentDay();
                createMarker();
            }
        } catch (SearchQueryException e) {
            log.error("SQL Exception " + e + ". Could not find smallestMarker, smallestDay");
            result = false;
        }

        return result;
    }

    /**
     * The current time in days since 1-1-1970
     */
    private int currentDay() {
        return (int)(System.currentTimeMillis()/(1000*60*60*24));
    }


    /**
     * Creates a mark in the database, if necessary.
     * @sql
     */
    private void createMarker() {
        int max  = -1;
        int mday = -1;
        if (log.isDebugEnabled()) {
            log.debug("Daymarker -> DAY=" + day);
        }
        MultiConnection con=null;
        Statement stmt=null;
        try {
            con=mmb.getConnection();
            stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select "+mmb.getDatabase().getAllowedField("number")+" from "+mmb.baseName+"_"+tableName+" where daycount="+day);
            if (rs.next()) {
                mday=rs.getInt(1);
            }
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        } finally {
            mmb.closeConnection(con,stmt);
        }

        //clear connection vars
        con=null;
        stmt=null;
        if (mday<0) { // it was not in the database
            log.service("Inserting new daymarker " + day);
            try {
                con=mmb.getConnection();
                stmt=con.createStatement();
                ResultSet rs = stmt.executeQuery("select max("+mmb.getDatabase().getAllowedField("number")+") from "+mmb.baseName+"_object");
                if (rs.next()) {
                    max=rs.getInt(1);
                }
                mmb.closeConnection(con,stmt);
                MMObjectNode node=getNewNode("system");
                node.setValue("daycount",day);
                node.setValue("mark",max);
                insert("system",node);
            } catch(Exception e) {
                log.error(Logging.stackTrace(e));
                mmb.closeConnection(con,stmt);
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
     * @sql
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

        try {
            NodeSearchQuery query = new NodeSearchQuery(this);
            FieldDefs dayCountFieldDefs = getField("daycount");
            StepField dayCount = query.getField(dayCountFieldDefs);
            BasicSortOrder sortOrder = query.addSortOrder(dayCount);
            sortOrder.setDirection(SortOrder.ORDER_DESCENDING);
            FieldDefs markFieldDefs = getField("mark");
            StepField markField = query.getField(markFieldDefs);
            BasicFieldValueConstraint cons = new BasicFieldValueConstraint(markField, new Integer(nodeNumber));
            cons.setOperator(FieldValueConstraint.LESS);
            query.setConstraint(cons);
            query.setMaxNumber(1);

            List resultList = getNodes(query);
            // mark < in stead of mark = will of course only be used in database with are not on line always, such
            // that some days do not have a mark.
            if (log.isDebugEnabled()) {
                log.debug(query);
            }

            // String query = "select mark, daycount from " + mmb.baseName + "_" + tableName + " where mark < "+ nodeNumber + " order by daycount desc";
            if (resultList.size() > 0) {
                // search the first daycount of which' mark is lower.
                // that must be the day which we were searching (at least a good estimate)
                MMObjectNode markNode = (MMObjectNode) resultList.get(0);
                int mark     = markNode.getIntValue("mark");
                int daycount = markNode.getIntValue("daycount");
                cachePut(daycount, mark);   // found one, could as well cache it
                getDayCount(daycount + 1);  // next time, this can be count with the cache as well
                return day - daycount;
            } else {
                // hmm, strange, perhaps we have to seek the oldest daycount, but for the moment:
                log.service("daycount could not be found for node " + node.getNumber());
                // determining the oldest daycount:
                query = new NodeSearchQuery(this);
                FieldDefs numberFieldDefs = getField("number");
                StepField number = query.getField(numberFieldDefs);
                sortOrder = query.addSortOrder(number);
                sortOrder.setDirection(SortOrder.ORDER_ASCENDING);
                query.setMaxNumber(1);
                resultList = getNodes(query);
                
                if (resultList.size() > 0) {
                    MMObjectNode markNode = (MMObjectNode) resultList.get(0);
                    int mark     = markNode.getIntValue("mark");
                    int daycount = markNode.getIntValue("daycount");
                    cachePut(daycount, mark);   // found one, could as well cache it
                    getDayCount(daycount + 1);  // next time, this can be count with the cache as well
                    return day - daycount;
                } else {
                    // no daymarks found at all.
                    return 0; // everything from today.
                }

            }

        } catch(SearchQueryException e) {
            log.error(Logging.stackTrace(e));
            return -1;
        }

    }

    /**
     * The current day count.
     * @return the number of days from 1970 of today.
     **/
    public int getDayCount() {
        return day;
    }

    /**
     * Given an age, this function returns a mark, _not a day count_.
     * @param daysold a time in days ago.
     * @return the smallest object number of all objects that are younger than given parameter daysold.
     **/
    public int getDayCountAge(int daysold) {
        int wday = day - daysold;
        return getDayCount(wday);
    }

    /**
     *
     * @sql
     * @param wday number of days from 1970
     * @return the smallest object number of all objects that are younger than given parameter daysold.
     */
    private int getDayCount(int wday) {

        log.debug("finding mark of day " + wday);
        Integer result = (Integer)daycache.get(new Integer(wday));
        if (result!=null) { // already in cache
            return result.intValue();
        }
        log.debug("could not be found in cache");


        if (wday < smallestDay) { // will not be possible to find in database
            if (log.isDebugEnabled() ) {
                log.debug("Day " + wday + " is smaller than smallest in database");
            }
            return 0;
        }

        if (mmb==null) return -1;
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
                    return tmp;
                } else {
                    log.error("Could not find mark of day " + wday);
                    stmt.close();
                    con.close();
                    return 0; // but it must be relativily new.
                }
            } catch(Exception e) {
                log.error("Could not find mark of day " + wday);
                return 0;
            }
        } else {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Scan. Known tokens are: 
     * COUNT-X gets an object number of X days after 1970
     * COUNTAGE-X gets an object number of X days old
     * COUNTMONTH-X gets an object number of X months after 1970 
     * COUNTNEXTMONTH-X gets an object number of X+1 months after 1970
     * COUNTPREVMONTH-X gets an object number of X-1 months after 1970
     * COUNTPREVDELTAMONTH-X-Y gets an object number of X-Y months after 1970
     * COUNTNEXTDELTAMONTH-X-Y gets an object number of X+Y months after 1970
     * TIMETOOBJECTNUMBER gets an object number of X seconds after 1970
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
                rtn=""+getDayCount(ival/86400);
            } else {
                rtn="UnknownCommand";
            }
        }
        return rtn;
    }

    /**
     * @javadoc
     */
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
        return ival;
    }

    /** 
     * get a Calendar
     * @param months number of months from 1970
     * @return calendar with date specified in months from 1970
     */
    private Calendar getCalendarMonths(int months) {
        int year,month;
        year=months/12;
        month=months%12;
        GregorianCalendar cal=new GregorianCalendar();
        cal.set(year+1970,month,1,0,0,0);
        return cal;
    }

    /**
     * @javadoc
     */
    private Calendar getCalendarDays(int days) {
        GregorianCalendar cal=new GregorianCalendar();
        java.util.Date d=new java.util.Date(((long)days)*24*3600*1000);
        cal.setTime(d);
        return cal;
    }

    /**
     * @javadoc
     */
    private int getDayCountMonth(int months) {
        Calendar cal=getCalendarMonths(months);
        return (int)((cal.getTime().getTime())/(24*3600*1000));
    }

    /**
     * @javadoc
     */
    private int getDayCountPreviousMonth(int months) {
        Calendar cal=getCalendarMonths(months);
        cal.add(Calendar.MONTH,-1);
        return (int)((cal.getTime().getTime())/(24*3600*1000));
    }

    /**
     * @javadoc
     */
    private int getDayCountNextMonth(int months) {
        Calendar cal=getCalendarMonths(months);
        cal.add(Calendar.MONTH,1);
        return (int)((cal.getTime().getTime())/(24*3600*1000));
    }

    /**
     * @javadoc
     */
    private int getDayCountDeltaMonth(int months,int delta) {
        Calendar cal=getCalendarMonths(months);
        cal.add(Calendar.MONTH,delta);
        return (int)((cal.getTime().getTime())/(24*3600*1000));
    }

    /**
     * @javadoc
     * @sql
     */
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
            log.error(Logging.stackTrace(e));
        }
        return mday;
    }

    /**
     * @javadoc
     */
    public int getMonthsByDayCount(int daycount) {
        int months=0;
        int year,month;
        Calendar calendar;

        calendar=getCalendarDays(daycount);
        year=calendar.get(Calendar.YEAR)-1970;
        month=calendar.get(Calendar.MONTH);
        months=month+year*12;
        return months;
    }


    /**
     *  Returns the date of a daymarker
     *  @param node The node of which the date is wanted
     *  @return a <code>Date</code> which is the date
     */
    public java.util.Date getDate(MMObjectNode node) {
        int dayCount = node.getIntValue(FIELD_DAYCOUNT);
         return new java.util.Date(((long)dayCount)*24*3600*1000);
    }

    /**
     *  Returns gui information for a specific node. This value is retrieved by retrieving the field 'gui()' of the node (node.getStringValue("gui()") )
     *  @param node The node of which the gui information is wanted
     *  @return a <code>String</code> in which the current date is shown
     */
    public String getLocaleGUIIndicator(Locale locale, MMObjectNode node) {
        return DateFormat.getDateInstance(DateFormat.LONG, locale).format(getDate(node));

    }

}
