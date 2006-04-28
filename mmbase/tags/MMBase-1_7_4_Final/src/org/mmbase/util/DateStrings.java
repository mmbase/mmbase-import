/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
* The DateString class provides constant text strings for the weekday, month etc.
*
*/
public class DateStrings {
    /**
     *  English short week day names
     */
    public static String days[]={ "Sun","Mon","Tue","Wed","Thu","Fri","Sat","Sun" };
    /**
     *  English long week day names
     */
    public static String longdays[]={ "Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday" };
    /**
     *  English short month names
     */
    public static String months[]={ "Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec" };
    /**
     *  English long month names
     */
    public static String longmonths[]={ "January","February","March","April","May","June","July","August","September","October","November","December" };

    /**
     *  Dutch short week day names
     */
    public static String Dutch_days[]={ "zon","maa","din","woe","don","vry","zat","zon" };
    /**
     *  Dutch long week day names
     */
    public static String Dutch_longdays[]={ "zondag","maandag","dinsdag","woensdag","donderdag","vrijdag","zaterdag","zondag" };
    /**
     *  Dutch short month names
     */
    public static String Dutch_months[]={ "jan","feb","maa","apr","mei","jun","jul","aug","sep","okt","nov","dec" };
    /**
     *  Dutch long month names
     */
    public static String Dutch_longmonths[]={ "januari","februari","maart","april","mei","juni","juli","augustus","september","oktober","november","december" };

    /**
     *  Short week day names (value deoends on chosen language)
     */
    public String day[];
    /**
     *  Long week day names (value deoends on chosen language)
     */
    public String longday[];
    /**
     *  Long short month names (value deoends on chosen language)
     */
    public String month[];
    /**
     *  Long month names (value deoends on chosen language)
     */
    public String longmonth[];

    /**
     * Creates a DateString insatnce, configured for the specified language.
     * The name of the language  has to be an ISO 639 code.
     */
    public DateStrings(String language) {
        if (language.equals("nl")) {
            day=Dutch_days;
            longday=Dutch_longdays;
            month=Dutch_months;
            longmonth=Dutch_longmonths;
        } else {
            // If language is unknown or English.
            day=days;
            longday=longdays;
            month=months;
            longmonth=longmonths;
        }
    }
}
