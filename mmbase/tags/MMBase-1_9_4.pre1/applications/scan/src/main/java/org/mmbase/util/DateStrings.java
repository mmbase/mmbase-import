/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.text.DateFormatSymbols;
import java.util.Locale;

/**
 * The DateString class provides constant text strings for the weekday, month etc.
 *
 * @deprecated FIX dutch days
 * @version $Id$
 */
public class DateStrings {

    /**
     *  Dutch short week day names
     */
    public static final String Dutch_days[]={"", "zon","maa","din","woe","don","vry","zat","zon" };

    public static final DateStrings DUTCH_DATESTRINGS = new DateStrings("nl");
    public static final DateStrings ENGLISH_DATESTRINGS = new DateStrings("en");

    /**
     *  Short week day names (value deoends on chosen language)
     */
    private String day[];
    /**
     *  Long week day names (value deoends on chosen language)
     */
    private String longday[];
    /**
     *  Long short month names (value deoends on chosen language)
     */
    private String month[];
    /**
     *  Long month names (value deoends on chosen language)
     */
    private String longmonth[];

    /**
     * Creates a DateString insatnce, configured for the specified language.
     * The name of the language  has to be an ISO 639 code.
     */
    public DateStrings(String language) {
        Locale aLocale = new Locale(language);
        DateFormatSymbols symbols = new DateFormatSymbols(aLocale);
        
        day = symbols.getShortWeekdays();
        longday = symbols.getWeekdays();
        month = symbols.getShortMonths();
        longmonth = symbols.getMonths();
        
        if (language.equals("nl")) {
            day=Dutch_days;
        }
    }

    public String getMonth(int monthInt) {
        return longmonth[monthInt];
    }

    public String getShortMonth(int monthInt) {
        return month[monthInt];
    }

    public String getDay(int weekDayInt) {
        return longday[weekDayInt + 1];
    }

    public String getShortDay(int weekDayInt) {
        return day[weekDayInt + 1];
    }
    
}