/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import org.mmbase.util.dateparser.*;

import java.util.*;
import junit.framework.TestCase;

/**
 * 
 * @author Michiel Meeuwissen
 * @verion $Id: DateParserTest.java,v 1.3 2006-03-28 23:55:30 michiel Exp $
 */
public class DateParserTest extends TestCase {


    protected boolean sufficientlyEqual(Object value1, Object value2) {
        if (value1 == null) return value2 == null;
        if (value1 instanceof Date && value2 instanceof Date) {
            // for dynamic dates.
            return Math.abs(((Date) value1).getTime() - ((Date) value2).getTime()) < 200L;
        } else {
            return value1.equals(value2);
        }
    }
    /**
     */
    public void testNow() {
        try {
            assertTrue(sufficientlyEqual(DynamicDate.getInstance("now"), new Date()));
            assertTrue(sufficientlyEqual(DynamicDate.getInstance("now - 5 minute"), new Date(System.currentTimeMillis() - 5 * 60 * 1000)));
            assertTrue(sufficientlyEqual(DynamicDate.getInstance("now + 5 minute"), new Date(System.currentTimeMillis() + 5 * 60 * 1000)));
            assertTrue(sufficientlyEqual(DynamicDate.getInstance("now + 5 hour"), new Date(System.currentTimeMillis() + 5 * 60 * 60 * 1000)));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    int NULL = -100000;
    int IGN  = -100001;
    protected int[] getNow(int[] n) {
        Calendar cal = Calendar.getInstance();
        return new int[] {
            n[0] == NULL ? 0 : (n[0] == IGN ? IGN : cal.get(Calendar.YEAR) + n[0]),
            n[1] == NULL ? 0 : (n[1] == IGN ? IGN : cal.get(Calendar.MONTH) + n[1]),
            n[2] == NULL ? 0 : (n[2] == IGN ? IGN : cal.get(Calendar.DAY_OF_MONTH) + n[2]),
            n[3] == NULL ? 0 : (n[3] == IGN ? IGN : cal.get(Calendar.HOUR_OF_DAY) + n[3]),
            n[4] == NULL ? 0 : (n[4] == IGN ? IGN : cal.get(Calendar.MINUTE) + n[4]),
            n[5] == NULL ? 0 : (n[5] == IGN ? IGN : cal.get(Calendar.SECOND) + n[5])};
    }
    protected boolean compare(Date date, int[] fields) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (fields[0] != IGN && cal.get(Calendar.YEAR) != fields[0]) return false;
        if (fields[1] != IGN && cal.get(Calendar.MONTH) != fields[1]) return false;
        if (fields[2] != IGN && cal.get(Calendar.DAY_OF_MONTH) != fields[2]) return false;
        if (fields[3] != IGN && cal.get(Calendar.HOUR_OF_DAY) != fields[3]) return false;
        if (fields[4] != IGN && cal.get(Calendar.MINUTE) != fields[4]) return false;
        if (fields[5] != IGN && cal.get(Calendar.SECOND) != fields[5]) return false;
        return true;

    }

    protected void assertTrue(String date1, int[] date2) throws ParseException {
        Date  dyndate = DynamicDate.getInstance(date1);
        int[] r     = getNow(date2);
        assertTrue(date1 + "->" + dyndate + " != " + new Date(r[0] - 1900, r[1], r[2], r[3], r[4], r[5]), compare(dyndate, r));
    }
    public void testDay() throws ParseException {
        Date date = DynamicDate.getInstance("today");
        assertTrue("today", new int[] {0, 0, 0, NULL, NULL, NULL});
        assertTrue("today + 1 day", new int[] {0, 0, 1, NULL, NULL, NULL});
        assertTrue("tomorrow", new int[] {0, 0, 1, NULL, NULL, NULL});
        //for now only checking if no exception:
        DynamicDate.getInstance("tomorrow 5 oclock");
        DynamicDate.getInstance("now 5 oclock");
        DynamicDate.getInstance("2006-01-10T06:12Z");
        DynamicDate.getInstance("2006-01-10 06:12");
        //DynamicDate.getInstance("2006-01-10 06:12 TZ CET");
        DynamicDate.getInstance("2006-01-10 5 oclock");
        DynamicDate.getInstance("friday");
        DynamicDate.getInstance("next friday");
        DynamicDate.getInstance("previous friday");
    }

    protected void beginOfDay(Calendar cal) {
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
    }
    public void testToday() {
        try {
            Calendar today = Calendar.getInstance();
            beginOfDay(today);
            assertTrue(sufficientlyEqual(DynamicDate.getInstance("today"), today.getTime()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
    }

}
