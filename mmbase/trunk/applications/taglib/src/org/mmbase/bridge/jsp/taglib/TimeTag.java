/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.bridge.jsp.taglib;
import  org.mmbase.bridge.jsp.taglib.util.Attribute;

import java.util.*;
import java.text.*;
import org.mmbase.util.logging.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

/**
 * The time taglib provides you easy functionality for using times in your web pages.
 *
 * @author  Rob Vermeulen (VPRO)
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.6
 * @version $Id: TimeTag.java,v 1.39 2004-03-05 13:39:05 rob Exp $
 */
public class TimeTag extends ContextReferrerTag implements Writer, WriterReferrer {

    private static final Logger log = Logging.getLoggerInstance(TimeTag.class);

    private static final int DAY = 1000 * 60 * 60 * 24;

    private Attribute time        = Attribute.NULL;
    private Attribute inputFormat = Attribute.NULL;
    private Attribute offset      = Attribute.NULL;

    private Attribute precision   = Attribute.NULL;
    private Attribute relevance   = Attribute.NULL;

    private final static int PRECISION_UNSET   = -1;
    private final static int PRECISION_SECONDS = 1;
    private final static int PRECISION_MINUTES = 2;
    private final static int PRECISION_HOURS   = 3;
    private final static int PRECISION_DAYS    = 4;
    private final static int PRECISION_WEEKS   = 5;
    private final static int PRECISION_MONTHS  = 6;
    private final static int PRECISION_YEARS   = 7;

    /**
     * DateFormat user for parsing dates. Given dates are always in english.
     */
    private SimpleDateFormat parseFormat = new SimpleDateFormat("", Locale.ENGLISH);

    /**
     * Format attribute used for displaying the dates.
     */
    private Attribute dateFormat = Attribute.NULL;

    /**
     * Fast way to find the day number of a day
     */
    static private Map days   = new HashMap();
    /**
     * Fast way to find the month number of a month
     */
    static private Map months = new HashMap();

    static {
        DateFormatSymbols dfs = new SimpleDateFormat("", Locale.ENGLISH).getDateFormatSymbols();
        setDays(dfs);
        setMonths(dfs);
   }


    // Attributes
    public void setTime(String time) throws JspTagException {
        this.time = getAttribute(time);
    }

    public void setFormat(String f) throws JspTagException {
        dateFormat = getAttribute(f);
    }

    public void setPrecision(String p) throws JspTagException {
        precision = getAttribute(p);
    }

    protected int getPrecisionConstant(String p) throws JspTagException {
        if (p.equals("")) {
            return PRECISION_UNSET;
        } else if (p.equals("seconds")) {
            return PRECISION_SECONDS;
        } else if (p.equals("minutes")) {
            return PRECISION_MINUTES;
        } else if (p.equals("hours")) {
            return PRECISION_HOURS;
        } else if (p.equals("days")) {
            return PRECISION_DAYS;
        } else if (p.equals("weeks")) {
            return PRECISION_WEEKS;
        } else if (p.equals("months")) {
            return PRECISION_MONTHS;
        } else if (p.equals("years")) {
            return PRECISION_YEARS;
        } else {
            throw new JspTagException("Unknown value for precision/significance attribute: '" + p + "'");
        }
    }

    private int getPrecision() throws JspTagException {
        String p = precision.getString(this).toLowerCase();
        return getPrecisionConstant(p);

    }


    public void setRelevance(String p) throws JspTagException {
        relevance = getAttribute(p);
    }

    private int getRelevance() throws JspTagException {
        String p = relevance.getString(this).toLowerCase();
        return getPrecisionConstant(p);
    }

    protected DateFormat getFormat() throws JspTagException {

        DateFormat df;
        if (log.isDebugEnabled()) log.debug("format: '" + dateFormat + "'");

        String format = dateFormat.getString(this);
        Locale locale;

        LocaleTag localeTag = (LocaleTag) findParentTag(LocaleTag.class.getName(), null, false);
        if (localeTag != null) {
            locale = localeTag.getLocale();
        } else {
            locale = org.mmbase.bridge.ContextProvider.getDefaultCloudContext().getDefaultLocale();
        }

        // symbolic formats. Perhaps will be moved to another attribute or so.
        if (format.length() > 0 && format.charAt(0) == ':') {
            log.debug("found symbolic format");
            if (format.charAt(1) == '.') {
                df = DateFormat.getTimeInstance(getDateFormatStyle(format.substring(2)), locale);
            } else if (format.indexOf('.') == -1) {
                df = DateFormat.getDateInstance(getDateFormatStyle(format.substring(1)), locale);
            } else {
                int i = format.indexOf('.');
                df = DateFormat.getDateTimeInstance(getDateFormatStyle(format.substring(1, i)),
                                                            getDateFormatStyle(format.substring(i+1)), locale);
            }
        } else if (format.equals("e")) {
            df = new DayOfWeekDateFormat();
            
        } else {
            df = new SimpleDateFormat(format, locale);
        }
        return df;

    }

    public void setInputformat(String inputformat) throws JspTagException {
        this.inputFormat = getAttribute(inputformat);
    }

    public void setOffset(String offset) throws JspTagException {
        this.offset = getAttribute(offset);
    }

    public int doStartTag() throws JspTagException {
        
        helper.setValue(evaluateTime());

        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }


    public int doEndTag() throws JspTagException {
        return helper.doEndTag();
    }

    public void release () {
        inputFormat = Attribute.NULL;
        offset =      Attribute.NULL;
        dateFormat =  Attribute.NULL;
    }


    /**
     * Converts a string to a DateFormat constant.
     *
     * @param style A string describing the dateformat style (FULL, LONG, MEDIUM, SHORT)
     * @return A DateFormat style constant.
     * @see    java.text.DateFormat
     */
    private int getDateFormatStyle(String style) throws JspTagException {
        if ("FULL".equals(style)) {
            return DateFormat.FULL;
        } else if ("LONG".equals(style)) {
            return DateFormat.LONG;
        } else if ("MEDIUM".equals(style)) {
            return DateFormat.MEDIUM;
        } else if ("SHORT".equals(style)) {
            return DateFormat.SHORT;
        } else {
            throw new JspTagException("Unknown DateFormat Style " + style);
        }
    }
    /**
     * Evaluate the time attribute.
     * @TODO This function is a too complicated. The several functionalities should be spread to different functions.
     * @javadoc
     */
    private String evaluateTime() throws JspTagException {
        if (log.isDebugEnabled()) { 
            log.debug("time: '"+time+"' offset: '"+offset+"' format: '"+dateFormat+"' inputformat: '"+inputFormat +"'");
        }

        String usetime = null;
        Date date = null;
        // If the time attribute is not set, check if referid is used, otherwise check if the parent set the time.
        // Otherwise use current time
        if(time == Attribute.NULL) {
            if(getReferid() != null) { // try to get it from other time tag
                usetime = getString(getReferid());
            } else {                   // try to get it from parent writer.
                Writer w =  findWriter(false);
                if (w != null) {
                    usetime = "" + w.getWriterValue();
                }
            }

            if(usetime == null) { // still not found
                throw new JspTagException("Cannot evaluate time. No time attribute given, no referid attribute set, and no writer parent tag found.");
            }
        } else {
            usetime = time.getString(this);
        }

        String iformat = inputFormat.getString(this);
        if (iformat.equals("")) {
            // Is the time given in second from EPOC (UTC)?
            try {
                long timeFromEpoc = Long.parseLong(usetime);
                date = new Date(timeFromEpoc*1000);
            } catch (NumberFormatException nfe) {
                // perhaps it was a keyWord...
                // this will be explored hereafter.
                // TODO Should we depend on exceptions? I think this is slow (?), and also ugly (though that is a matter of taste).
                // indeed, using exceptions as if statements is rather low performance.
                log.debug("Time not given in second from epoc");
            }

            // Is a day specified, like: monday, tuesday ?
            if(date == null && isDay(usetime)) {
                try {
                    date = handleDay(usetime);
                } catch (ParseException e) {
                    String msg = "Cannot evaluate handleDay with time '" + usetime + "' (exception:" + e + ")";
                    // Why should we log this? designers dont have access to logs
                    // log.error(msg);
                    throw new TaglibException(msg, e);
                }
            }
            // Is a month specified, like: january, february ?
            if(date == null && isMonth(usetime)) {
                try {
                    date = handleMonth(usetime);
                } catch (ParseException e) {
                    String msg = "Cannot evaluate handleMonth with time '" + usetime + "' (exception:" + e + ")";
                    // Why should we log this? designers dont have access to logs
                    // log.error(msg);
                    throw new TaglibException(msg, e);
                }
            }
            // Is a keyword used, like: yesterday, today ?
            if(date == null && isKeyword(usetime)) {
                try {
                    date = handleKeyword(usetime);
                } catch (ParseException e) {
                    String msg = "Cannot evaluate handleKeyword with time '" + usetime + "' (exception:" + e + ")";
                    // Why should we log this? designers dont have access to logs
                    // log.error(msg);
                    throw new TaglibException(msg, e);
                }
            }
            if (date == null) {
                date = getDate();  // Try to parse it in three standard ways.
            }

        } else { // The input format is provided. We use that to parse the time attribute
            try {
                parseFormat.applyPattern(iformat);
                date = parseFormat.parse(usetime);
            } catch (java.text.ParseException e) {
                throw new TaglibException(e);
            }

        }


        if (log.isDebugEnabled()) log.debug("date: '" + date + "'");

        if (date == null) { // don't know if it can come here, but if it does, an exception must be thrown!
            throw new JspTagException("Could not evaluate time " + usetime);
        }

        // Calculate the offset
        if(offset != Attribute.NULL) {
            long calculatedDate = date.getTime();
            long os;
            String off = offset.getString(this);
            try {
               os  = Long.parseLong(off) * 1000;
            } catch (NumberFormatException nfe) {
                os = new Double(off).longValue() * 1000;
            }
            date = new Date(calculatedDate + os);
        }

        if (precision != Attribute.NULL) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int prec = getPrecision();
            switch(prec) {
                case PRECISION_YEARS:    cal.set(Calendar.MONTH,  Calendar.JANUARY);
                case PRECISION_MONTHS:   cal.set(Calendar.DAY_OF_MONTH,  1);
                case PRECISION_DAYS:     cal.set(Calendar.HOUR_OF_DAY,  0);
                case PRECISION_HOURS:    cal.set(Calendar.MINUTE,    0);
                case PRECISION_MINUTES:  cal.set(Calendar.SECOND,    0);
                case PRECISION_SECONDS:
                default:                 cal.set(Calendar.MILLISECOND,    0);
            }
            if (prec == PRECISION_WEEKS)  {
                // this can not be done in above fall-through mechanism, because should not be done if >= PRECION_WEEKS
                cal.set(Calendar.DAY_OF_WEEK, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            }
            date = cal.getTime();
        }

        if (relevance != Attribute.NULL) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int rel = getRelevance();
            switch(rel) {
                case PRECISION_SECONDS:  cal.set(Calendar.MINUTE,    0);
                case PRECISION_MINUTES:  cal.set(Calendar.HOUR,      0);
                case PRECISION_HOURS:    cal.set(Calendar.DAY_OF_MONTH,    0);
                case PRECISION_WEEKS:    ;
                case PRECISION_DAYS:     cal.set(Calendar.MONTH,  Calendar.JANUARY);
                case PRECISION_MONTHS:   cal.set(Calendar.YEAR, 0);
                case PRECISION_YEARS:
                default:
            }
            date = cal.getTime();
        }

        if (dateFormat == Attribute.NULL) {
            // If no format is specified, we return the time in second from EPOC (UTC)
            return "" + date.getTime() / 1000;
        } else {
            return getFormat().format(date);
        }
    }

    /**
     * Check if a certain string is the name of a day.
     */
    private boolean isDay(String day) {
        if (log.isDebugEnabled()) log.debug("day: '"+day+"'");
        return days.containsKey(day.toLowerCase());
    }

    /**
     * Check if a certain string is the name of a month.
     */
    private boolean isMonth(String month) {
        if (log.isDebugEnabled()) log.debug("month: '"+month+"'");
        return months.containsKey(month.toLowerCase());
    }

    /**
     * check if it is a keyword
     */
    private boolean isKeyword(String keyword) {
        if (log.isDebugEnabled()) log.debug("keyword: '"+keyword+"'");

        return
            keyword.equalsIgnoreCase("now") ||
            keyword.equalsIgnoreCase("today") ||
            keyword.equalsIgnoreCase("tomorrow") ||
            keyword.equalsIgnoreCase("yesterday");
    }

    /**
     * The time is giving in a standard format i.e. yyyy/mm/dd
     * hh:mm:ss, yyyy/mm/dd, or hh:mm:ss.  This function parses and
     * returns the corresponding Date object, or throws an exception
     * if unsuccessful.
     *
     * aargh!
     */
    private Date getDate() throws JspTagException {
        if(time == Attribute.NULL) return null;

        Date date = new Date();
        try {
            String t = time.getString(this);
            if(t.length() == 10) {
                parseFormat.applyPattern("yyyy/MM/dd");
                date = parseFormat.parse(t);
            } else if(t.length() == 8) {
                parseFormat.applyPattern("HH:mm:ss");
                date = parseFormat.parse(t);
            } else if(t.length() == 19) {
                parseFormat.applyPattern("yyyy/MM/dd HH:mm:ss");
                date = parseFormat.parse(t);
            } else {
                throw new JspTagException("Time '" + t+ "' could not be parsed according to yyyy/MM/dd, HH:mm:ss or yyyy/MM/dd HH:mm:ss");
            }
        } catch (ParseException e) {
            throw new TaglibException(e);
        }
        return date;

    }


    /**
     * Evaluate the start of the day asked for.
     */
    private Date handleDay(String snextday) throws ParseException, JspTagException  {
        int nextday = ((Integer)days.get(snextday.toLowerCase())).intValue();

        // Find out which day it is.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        // Calc how many days futher
        int diff = (7+nextday-today)%7;

        // If diff=0, calculate if we want to return the same day or next week.
        long seconds = 0;
        if(diff==0) {
            // No offset given, return next week.
            if(offset == Attribute.NULL){
                diff = 7;
            } else {
                // Calculate how many seconds are already past this day
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                seconds = hours*3600+minutes+60;

                long ioffset = Long.parseLong(offset.getString(this));
                // If we are not over the offset return the same day, otherwise next week.
                if(seconds > ioffset) {
                    diff = 7;
                }
            }
        }

        // Go to the correct day
        calendar.add(Calendar.DAY_OF_WEEK, diff);
        return getBeginOfDay(calendar);
    }

    /**
     * Evaluate the start of the month asked for.
     */
    private Date handleMonth(String smonth) throws ParseException, JspTagException {
        int month = ((Integer)months.get(smonth.toLowerCase())).intValue();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int today = calendar.get(Calendar.MONTH);

        // Calc how many months futher
        int diff = (12+month-today)%12;

        // If diff=0, calculate if we want to return the same day or next month.
        long seconds = 0;
        if(diff==0) {
            // No offset is given, return next month
            if(offset==null){
                diff=12;
            } else {
                // Calculate how many seconds are already past this day
                int days = calendar.get(Calendar.DAY_OF_MONTH);
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                seconds = days*24*3600+hours*3600+minutes+60;

                long ioffset = Long.parseLong(offset.getString(this));
                // If we are not over the offset return the same day, otherwise next month.
                if(seconds>ioffset) {
                    diff=12;
                }
            }
        }
        // Go to the correct month
        calendar.add(Calendar.MONTH,diff);

        return getBeginOfMonth(calendar);
    }

    /**
     * convert a keyword into a date
     * @param keyword the keyword to convert
     */
    private Date handleKeyword(String keyword) throws ParseException {
        if(keyword.equals("now")) {
            return new Date();
        }

        int index=0;

        if(keyword.equals("today"))   {
            index = 0;
        } else
            if(keyword.equals("tomorrow"))   {
                index = DAY;
            } else
                if(keyword.equals("yesterday"))   {
                    index = -DAY;
                }

        long now = System.currentTimeMillis();
        Date newDate = new Date(now + index);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newDate);

        return getBeginOfDay(calendar);
    }


    /**
     * Finds the start of the day.
     * @param date A date.
     * @return The beginning of the day of the given Date
     */
    private Date getBeginOfDay(Calendar cal) throws ParseException {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,      0);
        cal.set(Calendar.SECOND,      0);
        return cal.getTime();
    }

    /**
     * Finds the start of the month
     * @param date A date.
     * @return The beginning of the month of the given Date
     */
    private Date getBeginOfMonth(Calendar cal) throws ParseException {
        cal.set(Calendar.DAY_OF_MONTH,  1);
        return getBeginOfDay(cal);
    }

    /**
     * set days with correct day number
     */
    static private void setDays(DateFormatSymbols dfs) {
        String[] dayarray = dfs.getWeekdays();

        for(int i=0; i<dayarray.length;i++) {
            days.put(dayarray[i].toLowerCase(),new Integer(i));
        }
    }

    /**
     * set month with correct month number
     */
    static private void setMonths(DateFormatSymbols dfs) {
        String[] montharray = dfs.getMonths();

        for(int i=0; i<montharray.length;i++) {
            months.put(montharray[i].toLowerCase(),new Integer(i));
        }
    }

    protected class DayOfWeekDateFormat extends DateFormat {
        public Date parse(String source, ParsePosition pos) {
            Calendar calendar = Calendar.getInstance();
            int day = source.charAt(0) - '0';
            pos.setIndex(pos.getIndex() + 1);            
            calendar.set(Calendar.DAY_OF_WEEK, day);
            return calendar.getTime();
        }
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            // pos.setBeginIndex(0); pos.setEndIndex(1);
            toAppendTo.append(calendar.get(Calendar.DAY_OF_WEEK));
            return toAppendTo;
        }

        
    }
}
