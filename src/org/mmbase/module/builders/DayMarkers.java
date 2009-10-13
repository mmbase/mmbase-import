/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.text.DateFormat;
import org.mmbase.module.core.*;
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
 * @author Daniel Ockeloen,Rico Jansen
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class DayMarkers extends MMObjectBuilder {

    public static final String FIELD_DAYCOUNT = "daycount";
    public static final String FIELD_MARK     = "mark";
    public static final long SECONDS_IN_A_DAY     = 24*3600;
    public static final long MILLISECONDS_IN_A_DAY     = SECONDS_IN_A_DAY*1000;

    private static final Logger log = Logging.getLoggerInstance(DayMarkers.class);

    private int day = 0; // current day number/count
    private Map daycache = new TreeMap();           // day -> mark, but ordered

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
     */
    public boolean init() {
        log.debug("Init of DayMarkers");
        boolean result;
        result = super.init();
        smallestDay  = 0;

        try {
            NodeSearchQuery query = new NodeSearchQuery(this);
            StepField field = query.getField(getField(FIELD_NUMBER));
            query.addSortOrder(field);
            query.setMaxNumber(1);
            List resultList = getNodes(query);
            if (resultList.size() > 0) {
                MMObjectNode mark = (MMObjectNode) resultList.get(0);
                smallestDay  = mark.getIntValue(FIELD_DAYCOUNT);
            }
            if (smallestDay == 0) {
                smallestDay = currentDay();
                createMarker();
            }
        } catch (SearchQueryException e) {
            log.error("SQL Exception " + e + ". Could not find smallestMarker, smallestDay");
            result = false;
        }
        createMarker();

        return result;
    }

    /**
     * The current time in days since 1-1-1970
     */
    private int currentDay() {
        return (int)(System.currentTimeMillis()/MILLISECONDS_IN_A_DAY);
    }


    /**
     * Creates a mark in the database, if necessary.
     */
    private void createMarker() {
        // test if the node for today exists
        NodeSearchQuery query = new NodeSearchQuery(this);
        query.setMaxNumber(1);
        StepField daycountField = query.getField(getField(FIELD_DAYCOUNT));
        BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(daycountField, new Integer(day));
        query.setConstraint(constraint);
        try {
            List resultList = getNodes(query);
            if (resultList.size() == 0) {
                // if not, retrieve the mark (highest node number) for today
                MMObjectBuilder root = mmb.getRootBuilder();
                query = new NodeSearchQuery(root);
                ModifiableQuery modifiedQuery = new ModifiableQuery(query);
                Step step = (Step) query.getSteps().get(0);
                AggregatedField field = new BasicAggregatedField(
                    step, root.getField(FIELD_NUMBER), AggregatedField.AGGREGATION_TYPE_MAX);
                List newFields = new ArrayList(1);
                newFields.add(field);
                modifiedQuery.setFields(newFields);
                List results = mmb.getSearchQueryHandler().getNodes(modifiedQuery, new ResultBuilder(mmb, modifiedQuery));
                ResultNode result = (ResultNode) results.get(0);
                int max = result.getIntValue(FIELD_NUMBER);
                // add a new daymarker node
                MMObjectNode node = getNewNode(SYSTEM_OWNER);
                node.setValue(FIELD_DAYCOUNT,day);
                node.setValue(FIELD_MARK,max);
                insert(SYSTEM_OWNER,node);
            }
        } catch (SearchQueryException e) {
            log.error(Logging.stackTrace(e));
        }

    }

    /**
     * This gets called every hour to see if the day has past.
     */
    public void probe() {
        int newday;
        newday = currentDay();
        //debug("Days "+newday+" current "+day);
        if (newday > day) {
            day = newday;
            createMarker();
        }
    }


    /**
     * Returns the age, in days, of a node. So, this does the inverse of most methods in this
     * class. It converts a node number (which is like a mark) to a day.
     */
    public int getAge(int nodeNumber) {
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
            StepField dayCount = query.getField(getField(FIELD_DAYCOUNT));
            BasicSortOrder sortOrder = query.addSortOrder(dayCount);
            sortOrder.setDirection(SortOrder.ORDER_DESCENDING);
            StepField markField = query.getField(getField(FIELD_MARK));
            BasicFieldValueConstraint cons = new BasicFieldValueConstraint(markField, new Integer(nodeNumber));
            cons.setOperator(FieldCompareConstraint.LESS);
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
                int mark     = markNode.getIntValue(FIELD_MARK);
                int daycount = markNode.getIntValue(FIELD_DAYCOUNT);
                cachePut(daycount, mark);   // found one, could as well cache it
                getDayCount(daycount + 1);  // next time, this can be count with the cache as well
                return day - daycount;
            } else {
                // hmm, strange, perhaps we have to seek the oldest daycount, but for the moment:
                log.service("daycount could not be found for node " + nodeNumber);
                // determining the oldest daycount:
                query = new NodeSearchQuery(this);
                StepField number = query.getField(getField(FIELD_NUMBER));
                sortOrder = query.addSortOrder(number);
                sortOrder.setDirection(SortOrder.ORDER_ASCENDING);
                query.setMaxNumber(1);
                resultList = getNodes(query);

                if (resultList.size() > 0) {
                    MMObjectNode markNode = (MMObjectNode) resultList.get(0);
                    int mark     = markNode.getIntValue(FIELD_MARK);
                    int daycount = markNode.getIntValue(FIELD_DAYCOUNT);
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
     * Given an age, this function returns a mark, _not a day count_, and also _not an age_!
     * @param daysold a time in days ago.
     * @return the smallest object number of all objects that are younger than given parameter daysold.
     **/
    public int getDayCountAge(int daysold) {
        int wday = day - daysold;
        return getDayCount(wday);
    }

    /**
     * Calculates the smallest object number of all objects that are younger than the specified age.
     * @param wday teh age in number of days from 1970
     * @return the smallest object number, 0 if it can't be found
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
        if (wday <= day) {
            NodeSearchQuery query = new NodeSearchQuery(this);
            query.setMaxNumber(1);
            StepField daycountField = query.getField(getField(FIELD_DAYCOUNT));
            BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(daycountField, new Integer(wday));
            constraint.setOperator(FieldCompareConstraint.GREATER_EQUAL);
            query.setConstraint(constraint);
            int mark = 0;
            try {
                List resultList = getNodes(query);
                if (resultList.size() != 0) {
                    MMObjectNode resultNode = (MMObjectNode) resultList.get(0);
                    mark = resultNode.getIntValue(FIELD_MARK);
                    int daycount = resultNode.getIntValue(FIELD_DAYCOUNT);
                    if (daycount != wday) {
                        log.error("Could not find day " + wday + ", surrogated with " + daycount);
                    } else {
                        log.debug("Found in db, will be inserted in cache");
                    }
                    cachePut(wday, mark);
                }
            } catch (SearchQueryException e) {
                log.error(Logging.stackTrace(e));
            }
            return mark;
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
    public String replace(PageInfo sp, StringTokenizer command) {
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
                rtn=""+getDayCount((int)(ival/SECONDS_IN_A_DAY));
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
        GregorianCalendar cal = new GregorianCalendar();
        java.util.Date d = new java.util.Date((days)*MILLISECONDS_IN_A_DAY);
        cal.setTime(d);
        return cal;
    }

    /**
     * @javadoc
     */
    private int getDayCountMonth(int months) {
        Calendar cal = getCalendarMonths(months);
        return (int)(cal.getTime().getTime()/MILLISECONDS_IN_A_DAY);
    }

    /**
     * @javadoc
     */
    private int getDayCountPreviousMonth(int months) {
        Calendar cal = getCalendarMonths(months);
        cal.add(Calendar.MONTH,-1);
        return (int)(cal.getTime().getTime()/MILLISECONDS_IN_A_DAY);
    }

    /**
     * @javadoc
     */
    private int getDayCountNextMonth(int months) {
        Calendar cal = getCalendarMonths(months);
        cal.add(Calendar.MONTH,1);
        return (int)(cal.getTime().getTime()/MILLISECONDS_IN_A_DAY);
    }

    /**
     * @javadoc
     */
    private int getDayCountDeltaMonth(int months,int delta) {
        Calendar cal = getCalendarMonths(months);
        cal.add(Calendar.MONTH,delta);
        return (int)(cal.getTime().getTime()/MILLISECONDS_IN_A_DAY);
    }

    /**
     * @javadoc
     */
    public int getDayCountByObject(int number) {
        NodeSearchQuery query = new NodeSearchQuery(this);
        query.setMaxNumber(1);
        StepField markField = query.getField(getField(FIELD_MARK));
        BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(markField, new Integer(number));
        constraint.setOperator(FieldCompareConstraint.LESS);
        query.setConstraint(constraint);
        ModifiableQuery modifiedQuery = new ModifiableQuery(query);
        Step step = (Step) query.getSteps().get(0);
        AggregatedField field = new BasicAggregatedField(
            step, getField(FIELD_DAYCOUNT), AggregatedField.AGGREGATION_TYPE_MAX);
        List newFields = new ArrayList(1);
        newFields.add(field);
        modifiedQuery.setFields(newFields);
        try {
            List results = mmb.getSearchQueryHandler().getNodes(modifiedQuery, new ResultBuilder(mmb, modifiedQuery));
            ResultNode result = (ResultNode) results.get(0);
            return result.getIntValue(FIELD_DAYCOUNT);
        } catch (SearchQueryException e) {
            log.error(Logging.stackTrace(e));
            return 0;
        }

    }

    /**
     * @javadoc
     */
    public int getMonthsByDayCount(int daycount) {
        int year,month;
        Calendar calendar;

        calendar = getCalendarDays(daycount);
        year = calendar.get(Calendar.YEAR)-1970;
        month = calendar.get(Calendar.MONTH);
        return month + year * 12;
    }


    /**
     *  Returns the date of a daymarker
     *  @param node The node of which the date is wanted
     *  @return a <code>Date</code> which is the date
     */
    public java.util.Date getDate(MMObjectNode node) {
        int dayCount = node.getIntValue(FIELD_DAYCOUNT);
        return new java.util.Date(dayCount*MILLISECONDS_IN_A_DAY);
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
