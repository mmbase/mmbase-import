/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import org.mmbase.util.logging.*;

/**
 * This util class contains several methods and constants to manipulate relative time values.
 * The relative time value has to be provided as either one integer value (representing the time in milliseconds),
 * or as a set of time attribute integers (hours,minutes,seconds and milliseconds).
 *
 * @application SCAN or Tools (INFO, AnnotRel builder)
 * @author David V van Zeventer
 * @version $Id$
 */
public class RelativeTime {

    //These time attribute constants define their position in the relative time format (h:m:s.ms)
    private final static int HOUR_POS   = 0;
    private final static int MINUTE_POS = 1;
    private final static int SECOND_POS = 2;
    private final static int MILLI_POS  = 3;

    // logger
    private static Logger log = Logging.getLoggerInstance(RelativeTime.class.getName());

    /**
     * Retrieves the amount of hours that are left in the timeValue variable (representing the time in milliseconds).
     * @param timeValue An integer which holds the relative time value.
     * @return The amount of hours left.
     */
    public static int getHours(int timeValue) {
        return getTimeValue(timeValue,HOUR_POS);
    }

    /**
     * Retrieves the amount of minutes that are left in the timeValue variable (representing the time in milliseconds).
     * @param timeValue An integer which holds the relative time value.
     * @return The amount of minutes left.
     */
    public static int getMinutes(int timeValue) {
        return getTimeValue(timeValue,MINUTE_POS);
    }

    /**
     * Retrieves the amount of seconds that are left in the timeValue variable (representing the time in milliseconds).
     * @param timeValue An integer which holds the relative time value.
     * @return The amount of seconds left.
     */
    public static int getSeconds(int timeValue) {
        return getTimeValue(timeValue,SECOND_POS);
    }

    /**
     * Retrieves the amount of milliseconds that are left in the timeValue variable (representing the time in milliseconds).
     * @param timeValue An integer which holds the relative time value.
     * @return The amount of milliseconds left.
     */
    public static int getMillis(int timeValue) {
        return getTimeValue(timeValue,MILLI_POS);
    }

    /**
     * Retrieves the amount of hours or minutes or seconds or milliseconds that are left in the timeValue variable
     * (representing the time in milliseconds). Which time attribute is retrieved is determined by the timePos value.
     * @param timeValue An integer which holds the relative time value.
     * @param timePos An integer which holds the time attribute value requested.
     * @return The amount of time attribute parts left OR -1 when an invalid timePosition is provided.
     */
    private static int getTimeValue(int timeValue,int timePos) {
        int HOUR_IN_MILLIS   = 60*60*1000;
        int MINUTE_IN_MILLIS = 60*1000;
        int SECOND_IN_MILLIS = 1000;
        int timeLeft, hoursLeft, minutesLeft, secondsLeft, millisLeft;

        if (timeValue >= 0) {
            timeLeft = timeValue;
            hoursLeft = timeLeft / HOUR_IN_MILLIS;
            if (timePos == HOUR_POS) {
                return hoursLeft;
            }

            timeLeft -= hoursLeft * HOUR_IN_MILLIS;
            minutesLeft = timeLeft / MINUTE_IN_MILLIS;
            if (timePos == MINUTE_POS) {
                return minutesLeft;
            }

            timeLeft -= minutesLeft * MINUTE_IN_MILLIS;
            secondsLeft = timeLeft / SECOND_IN_MILLIS;
            if (timePos == SECOND_POS) {
                return secondsLeft;
            }

            timeLeft -= secondsLeft * SECOND_IN_MILLIS;
            millisLeft = timeLeft;
            if (timePos == MILLI_POS) {
                return millisLeft;
            }

            //Invalid timePosition used -> returning -1
            log.warn("Invalid timePos used -> timePos="+timePos+" returning -1");
            return -1;

        } else {    //Negative timeValue used -> returning -1
            log.warn("Negative timeValue used at position "+timePos+" -> timeValue="+timeValue+" returning -1");
            return -1;
        }
    }

    /**
     * Converts an integer (representing the time in milliseconds) to a string (like "12:42:15.020")
     * @param timeValue The amount of time in milliseconds.
     * @return String containing the amount of time in the "h:m:s.ms" format.
     */
    public static String convertIntToTime(int timeValue) {

        int h  = getHours(timeValue);
        int m  = getMinutes(timeValue);
        int s  = getSeconds(timeValue);
        int ms = getMillis(timeValue);
        if (!testTimeValue(h,HOUR_POS)) h = -1;
        if (!testTimeValue(m,MINUTE_POS)) m = -1;
        if (!testTimeValue(s,SECOND_POS)) s = -1;
        if (!testTimeValue(ms,MILLI_POS)) ms= -1;

        //Setting milliseconds attribute to right format value.
        if (ms < 0) {
            return (h+":"+m+":"+s+"."+ms);
        } else if ((ms/100.0) >= 1) {
            return (h+":"+m+":"+s+"."+ms);
        } else if ((ms/10.0) >= 1) {
            return (h+":"+m+":"+s+".0"+ms);
        } else {
            return (h+":"+m+":"+s+".00"+ms);
        }
    }

    /**
     * Converts the time attribute values to one integer representing the time in milliseconds.
     * @param h The amount of hours
     * @param m The amount of minutes
     * @param s The amount of seconds
     * @param ms The amount of millis
     * @return The amount of time in milliseconds OR -1 if the timeValue contains negative values.
     */
    public static int convertTimeToInt(int h,int m,int s,int ms) {
        //Setting milliseconds attribute to right format value.
        if (ms < 0) {
            return convertTimeToInt(h+":"+m+":"+s+"."+ms);
        } else if ((ms/100.0) >= 1) {
            return convertTimeToInt(h+":"+m+":"+s+"."+ms);
        } else if ((ms/10.0) >= 1) {
            return convertTimeToInt(h+":"+m+":"+s+".0"+ms);
        } else {
            return convertTimeToInt(h+":"+m+":"+s+".00"+ms);
        }
    }

    /**
     * Converts a string (like "12:42:15.020") to milliseconds
     * @param time A string which contains the relative time in the format "hours:minutes:seconds.millis"
     * @return The amount of time in milliseconds OR -1 if one of the time attributes provided is invalid.
     */
    public static int convertTimeToInt(String time) {
        int result = 0;        //The amount of milliseconds that is to be returned.
        String attrStrValues[] = new String[4];

        // Splice string value into time attribute tokens.
        int timePos=0;
        StringTokenizer st = new StringTokenizer(time, ":.");
        while (st.hasMoreTokens()) {
               attrStrValues[timePos] = new String(st.nextToken());
            timePos++;
        }

        // Test all time attribute values.
        for (int i=0; i<attrStrValues.length; i++) {
            if (!testTimeValue(Integer.parseInt(attrStrValues[i]),i)) {
                return -1;
            }
        }

        // Convert hours,minutes and seconds to millis and adding them to result.
        int attrIntValue = Integer.parseInt(attrStrValues[HOUR_POS]);
        result += attrIntValue*3600*1000;
        attrIntValue = Integer.parseInt(attrStrValues[MINUTE_POS]);
        result += attrIntValue*60*1000;
        attrIntValue = Integer.parseInt(attrStrValues[SECOND_POS]);
        result += attrIntValue*1000;

        // Convert milliseconds attribute value and adding them to result.
        attrIntValue = Integer.parseInt(attrStrValues[MILLI_POS]);
        if (attrStrValues[MILLI_POS].length() == 3) {
            result += attrIntValue;
        } else if (attrStrValues[MILLI_POS].length() == 2) {
            result += 10 * attrIntValue;
        } else if (attrStrValues[MILLI_POS].length() == 1) {
            result += 100 * attrIntValue;
        }

        return result;
    }

    /**
     * Tests if a time attribute (h,m,s or ms) is valid or not.
     * @param timeAttrValue This value holds the time attribute value.
     * @param timePos Denotes the position time attribute value.
     * @return true if value is valid, otherwise returns false.
     */

    private static boolean testTimeValue(int timeAttrValue, int timePos) {
        // Test if value is negative.
        if (timeAttrValue < 0) {
            log.warn("Negative timeAttrValue used at position "+timePos+" -> timeAttrValue="+timeAttrValue+" returning false");
            return false;
        }
        if (timePos == HOUR_POS) {
            if (timeAttrValue >23) {
                log.warn("Invalid timeAttrValue used at position "+timePos+" -> timeAttrValue="+timeAttrValue+" returning false");
                return false;
            }
        } else if (timePos == MINUTE_POS) {
            if (timeAttrValue >59) {
                log.warn("Invalid timeAttrValue used at position "+timePos+" -> timeAttrValue="+timeAttrValue+" returning false");
                return false;
            }
        } else if (timePos == SECOND_POS) {
            if (timeAttrValue >59) {
                log.warn("Invalid timeAttrValue used at position "+timePos+" -> timeAttrValue="+timeAttrValue+" returning false");
                return false;
            }
        } else if (timePos == MILLI_POS) {
            if (timeAttrValue >999) {
                log.warn("Invalid timeAttrValue used at position "+timePos+" -> timeAttrValue="+timeAttrValue+" returning false");
                return false;
            }
        } else {     //Invalid timePosition provided -> returning false
            log.warn("Invalid timePos provided -> timePos="+timePos+" returning false");
            return false;
        }

        return true;
    }

    /**
     * Entry point for calling this class from commandline.
     * For testing
     */
    public static void main(String args[]) {
        //Use java org.mmbase.util.RelativeTime
        Enumeration e;
        String timeKey;
        int timeValue;
        Properties testProps = new Properties();
        testProps.put("23:59:59.999", new Integer(86399999));
        testProps.put("0:0:0.0"     , new Integer(0));
        testProps.put("23:0:0.0"    , new Integer(82800000));
        testProps.put("0:59:0.0"    , new Integer(3540000));
        testProps.put("0:0:59.0"    , new Integer(59000));
        testProps.put("0:0:0.999"   , new Integer(999));
        testProps.put("1:33:59.5"   , new Integer(5639500));
        testProps.put("1:33:59.52"  , new Integer(5639520));
        testProps.put("1:33:59.521" , new Integer(5639521));
        testProps.put("1:33:59.012" , new Integer(5639012));
        testProps.put("1:33:09.002" , new Integer(5589002));
        testProps.put("24:33:09.002", new Integer(88389002));
        testProps.put("0:0:2.100"   , new Integer(2100));
        testProps.put("0:0:2.010"   , new Integer(2010));

        log.info("----------------------------------------");
        log.info("|Testing RelativeTime util class       |");
        log.info("----------------------------------------");

        //log.info("Testing getHours(args[0])   = "+getHours(Integer.parseInt(args[0])));

        if ((args.length <1) || (args.length >1)) {
            log.info("Usage: RelativeTime methodName");
            log.info("Methods available: getHours,getMinutes,getSeconds,getMillis,convertIntToTime,convertTimeToInt,convertTimeToInt2");
        } else {
            if (args[0].equals("getHours")) {
                log.info("Testing method: "+args[0]);
                e = testProps.keys();
                while (e.hasMoreElements()) {
                    timeKey = (String)e.nextElement();
                    timeValue = ((Integer)testProps.get(timeKey)).intValue();
                    log.info("getHours using time = "+timeKey+" ->"+getHours(timeValue));
                }
            } else if (args[0].equals("getMinutes")) {
                log.info("Testing method: "+args[0]);
                e = testProps.keys();
                while (e.hasMoreElements()) {
                    timeKey = (String)e.nextElement();
                    timeValue = ((Integer)testProps.get(timeKey)).intValue();
                    log.info("getMinutes using time = "+timeKey+" ->"+getMinutes(timeValue));
                }
            } else if (args[0].equals("getSeconds")) {
                log.info("Testing method: "+args[0]);
                e = testProps.keys();
                while (e.hasMoreElements()) {
                    timeKey = (String)e.nextElement();
                    timeValue = ((Integer)testProps.get(timeKey)).intValue();
                    log.info("getSeconds using time = "+timeKey+" ->"+getSeconds(timeValue));
                }
            } else if (args[0].equals("getMillis")) {
                log.info("Testing method: "+args[0]);
                e = testProps.keys();
                while (e.hasMoreElements()) {
                    timeKey = (String)e.nextElement();
                    timeValue = ((Integer)testProps.get(timeKey)).intValue();
                    log.info("getMillis using time  = "+timeKey+" ->"+getMillis(timeValue));
                }
            } else if (args[0].equals("convertIntToTime")) {
                log.info("Testing method: "+args[0]);
                e = testProps.elements();
                while (e.hasMoreElements()) {
                    timeValue = ((Integer)e.nextElement()).intValue();
                    log.info("convertIntToTime using timeValue = "+timeValue+" ->"+convertIntToTime(timeValue));
                }
            } else if (args[0].equals("convertTimeToInt")) {
                log.info("Testing method: "+args[0]);
                e = testProps.keys();
                while (e.hasMoreElements()) {
                    timeKey = (String)e.nextElement();
                    timeValue = convertTimeToInt(timeKey);
                    log.info("convertTimeToInt using timeKey="+timeKey+" , timeValue="+timeValue+" in testProps? "+testProps.contains(new Integer(timeValue)));
                }
            } else if (args[0].equals("convertTimeToInt2")) {
                log.info("Testing method: "+args[0]);
                timeValue = convertTimeToInt(4,33,9,32);
                log.info("convertTimeToInt2 using 4:33:9:32 , timeValue="+timeValue);
            }
        }
    }
}
