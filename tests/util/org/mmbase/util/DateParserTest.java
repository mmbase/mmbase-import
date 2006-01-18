/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import junit.framework.TestCase;

/**
 * 
 * @author Michiel Meeuwissen
 * @verion $Id: DateParserTest.java,v 1.2 2006-01-18 09:10:25 michiel Exp $
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
