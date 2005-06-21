/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.bridge.jsp.taglib;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import java.util.*;
import java.text.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.Casting;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

/**
 * The time taglib provides you easy functionality for using times in your web pages.
 *
 * @author  Rob Vermeulen (VPRO)
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.6
 * @version $Id: TimeTag.java,v 1.48 2005-06-21 06:09:55 michiel Exp $
 */
public class TimeTag extends ContextReferrerTag implements Writer, WriterReferrer {

    private static final Logger log = Logging.getLoggerInstance(TimeTag.class);

    private static final int DAY = 1000 * 60 * 60 * 24;

    private Attribute time = Attribute.NULL;
    private Attribute inputFormat = Attribute.NULL;
    private Attribute offset = Attribute.NULL;

    private Attribute precision = Attribute.NULL;
    private Attribute relevance = Attribute.NULL;

    private Attribute timezone = Attribute.NULL;

    private final static int PRECISION_UNSET = -1;
    private final static int PRECISION_SECONDS = 1;
    private final static int PRECISION_MINUTES = 2;
    private final static int PRECISION_HOURS = 3;
    private final static int PRECISION_DAYS = 4;
    private final static int PRECISION_WEEKS = 5;
    private final static int PRECISION_MONTHS = 6;
    private final static int PRECISION_YEARS = 7;

    /**
     * Fast way to find the day number of a day
     */
    static private Map days = new HashMap();
    /**
     * Fast way to find the month number of a month
     */
    static private Map months = new HashMap();

    static {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
        setDays(dfs);
        setMonths(dfs);
    }

    /**
     * Format attribute used for displaying the dates.
     */
    private Attribute dateFormat = Attribute.NULL;

    /**
     * @since MMBase-1.7.1
     */
    protected Date date;

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
        if (log.isDebugEnabled()) {
            log.debug("format: '" + dateFormat + "'");
        }
        return  org.mmbase.util.DateFormats.getInstance(dateFormat.getString(this), timezone.getString(this), getLocale());
    }


    public void setInputformat(String inputformat) throws JspTagException {
        this.inputFormat = getAttribute(inputformat);
    }

    public void setOffset(String offset) throws JspTagException {
        this.offset = getAttribute(offset);
    }

    public void setTimezone(String tz) throws JspTagException {
        timezone = getAttribute(tz);
    }

    /**
     * You could implement a tag for the body of time-tag, which can then receive the date-object.
     * @todo  Don't we use getCalendar (to get Locales right)
     * @since MMBase-1.7.1
     */
    public Date getDate() {
        return date;
    }

    public int doStartTag() throws JspTagException {

        helper.setValue(evaluateTime());

        if (getId() != null) {
            if (helper.getVartype() == WriterHelper.TYPE_DATE) {
                getContextProvider().getContextContainer().register(getId(), getDate());
            } else {
                getContextProvider().getContextContainer().register(getId(), helper.getValue());
            }
        }
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    public int doEndTag() throws JspTagException {
        helper.doEndTag();
        return super.doEndTag();
    }

    public void release() {
        inputFormat = Attribute.NULL;
        offset = Attribute.NULL;
        dateFormat = Attribute.NULL;
    }

    /**
     * Evaluate the time attribute.
     * @TODO This function is a too complicated. The several functionalities should be spread to different functions.
     * @javadoc
     */
    private String evaluateTime() throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("time: '" + time + "' offset: '" + offset + "' format: '" + dateFormat + "' inputformat: '" + inputFormat + "'");
        }

        String useTime = null;
        date = null;
        // If the time attribute is not set, check if referid is used, otherwise check if the parent set the time.
        // Otherwise use current time
        if (time == Attribute.NULL) {
            if (getReferid() != null) { // try to get it from other time tag
                Object object = getObject(getReferid());
                if (object instanceof Date) {
                    date = (Date) object;
                } else {
                    useTime = Casting.toString(object);
                }
            } else { // try to get it from parent writer.
                Writer w = findWriter(false);
                if (w != null) {
                    Object object = w.getWriterValue();
                    if (object instanceof Date) {
                        date = (Date) object;
                    } else {
                        useTime = "" + object;
                    }

                }
            }

            if (useTime == null && date == null) { // still not found
                throw new JspTagException("Cannot evaluate time. No time attribute given, no referid attribute set, and no writer parent tag found.");
            }
        } else {
            useTime = time.getString(this);
        }

        String iformat = inputFormat.getString(this);
        if (iformat.equals("")) {
            if (date == null) {
                // Is the time given in second from EPOC (UTC)?
                try {
                    long timeFromEpoc = Long.parseLong(useTime);
                    date = new Date(timeFromEpoc * 1000);
                } catch (NumberFormatException nfe) {
                    // perhaps it was a keyWord...
                    // this will be explored hereafter.
                    // TODO Should we depend on exceptions? I think this is slow (?), and also ugly (though that is a matter of taste).
                    // indeed, using exceptions as if statements is rather low performance.
                    log.debug("Time not given in second from epoc");
                }
            }

            // Is a day specified, like: monday, tuesday ?
            if (date == null && isDay(useTime)) {
                try {
                    date = handleDay(useTime);
                } catch (ParseException e) {
                    String msg = "Cannot evaluate handleDay with time '" + useTime + "' (exception:" + e + ")";
                    // Why should we log this? designers dont have access to logs
                    // log.error(msg);
                    throw new TaglibException(msg, e);
                }
            }
            // Is a month specified, like: january, february ?
            if (date == null && isMonth(useTime)) {
                try {
                    date = handleMonth(useTime);
                } catch (ParseException e) {
                    String msg = "Cannot evaluate handleMonth with time '" + useTime + "' (exception:" + e + ")";
                    // Why should we log this? designers dont have access to logs
                    // log.error(msg);
                    throw new TaglibException(msg, e);
                }
            }
            // Is a keyword used, like: yesterday, today ?
            if (date == null && isKeyword(useTime)) {
                try {
                    date = handleKeyword(useTime);
                } catch (ParseException e) {
                    String msg = "Cannot evaluate handleKeyword with time '" + useTime + "' (exception:" + e + ")";
                    // Why should we log this? designers dont have access to logs
                    // log.error(msg);
                    throw new TaglibException(msg, e);
                }
            }
            if (date == null) {
                date = parseTime(); // Try to parse it in three standard ways.
            }
        } else { // The input format is provided. We use that to parse the time attribute
            try {
                SimpleDateFormat format = getDateFormat();
                format.applyPattern(iformat);
                date = format.parse(useTime);
            } catch (java.text.ParseException e) {
                throw new TaglibException(e);
            }

        }

        if (log.isDebugEnabled()) {
            log.debug("date: '" + date + "'");
        }

        if (date == null) { // date fields can be null.
            date = new Date(0);
        }

        // Calculate the offset
        if (offset != Attribute.NULL) {
            long calculatedDate = date.getTime();
            long os;
            String off = offset.getString(this);
            try {
                os = Long.parseLong(off) * 1000;
            } catch (NumberFormatException nfe) {
                os = new Double(off).longValue() * 1000;
            }
            date = new Date(calculatedDate + os);
        }

        // precision sets fields of date opbject to 0 starting with least relevant bits (for caching purposes)
        if (precision != Attribute.NULL) {
            Calendar cal = Calendar.getInstance(getLocale());
            cal.setTime(date);
            int prec = getPrecision();
            switch (prec) {
            case PRECISION_YEARS :
                cal.set(Calendar.MONTH, Calendar.JANUARY);
            case PRECISION_MONTHS :
                cal.set(Calendar.DAY_OF_MONTH, 1);
            case PRECISION_DAYS :
                cal.set(Calendar.HOUR_OF_DAY, 0);
            case PRECISION_HOURS :
                cal.set(Calendar.MINUTE, 0);
            case PRECISION_MINUTES :
                cal.set(Calendar.SECOND, 0);
            case PRECISION_SECONDS :
            default :
                cal.set(Calendar.MILLISECOND, 0);
            }
            if (prec == PRECISION_WEEKS) {
                // this can not be done in above fall-through mechanism, because should not be done if >= PRECION_WEEKS                
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            }
            date = cal.getTime();
        }

        // relevance (which is not in tld (yet), works the other way around. It sets the 'maximum
        // relevance bit'. If for example you set it to 'months', then the yare is set to 0 and can
        // be ignored. In this way you can e.g. check if it january, or if it is somebody's birthday.
        if (relevance != Attribute.NULL) {
            Calendar cal = Calendar.getInstance(getLocale());
            cal.setTime(date);
            int rel = getRelevance();
            switch (rel) {
                case PRECISION_SECONDS :
                    cal.set(Calendar.MINUTE, 0);
                case PRECISION_MINUTES :
                    cal.set(Calendar.HOUR, 0);
                case PRECISION_HOURS :
                    cal.set(Calendar.DAY_OF_MONTH, 0);
                case PRECISION_WEEKS :
                    ;
                case PRECISION_DAYS :
                    cal.set(Calendar.MONTH, Calendar.JANUARY);
                case PRECISION_MONTHS :
                    cal.set(Calendar.YEAR, 0);
                case PRECISION_YEARS :
                default :
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
        if (log.isDebugEnabled())
            log.debug("day: '" + day + "'");
        return days.containsKey(day.toLowerCase());
    }

    /**
     * Check if a certain string is the name of a month.
     */
    private boolean isMonth(String month) {
        if (log.isDebugEnabled())
            log.debug("month: '" + month + "'");
        return months.containsKey(month.toLowerCase());
    }

    /**
     * check if it is a keyword
     */
    private boolean isKeyword(String keyword) {
        if (log.isDebugEnabled())
            log.debug("keyword: '" + keyword + "'");

        return keyword.equalsIgnoreCase("now") || keyword.equalsIgnoreCase("today") || keyword.equalsIgnoreCase("tomorrow") || keyword.equalsIgnoreCase("yesterday");
    }

    /**
     * The time is giving in a standard format i.e. yyyy/mm/dd
     * hh:mm:ss, yyyy/mm/dd, or hh:mm:ss.  This function parses and
     * returns the corresponding Date object, or throws an exception
     * if unsuccessful.
     * So, it parses the 'time' attribute without the use of 'inputformat'.
     *
     */
    private Date parseTime() throws JspTagException {
        if (time == Attribute.NULL)
            return null;

        Date date = new Date();
        try {
            String t = time.getString(this);
            if (t.length() == 10) {
                SimpleDateFormat format = getDateFormat();
                format.applyPattern("yyyy/MM/dd");
                date = format.parse(t);
            } else if (t.length() == 8) {
                SimpleDateFormat format = getDateFormat();
                format.applyPattern("HH:mm:ss");
                date = format.parse(t);
            } else if (t.length() == 19) {
                SimpleDateFormat format = getDateFormat();
                format.applyPattern("yyyy/MM/dd HH:mm:ss");
                date = format.parse(t);
            } else {
                throw new JspTagException("Time '" + t + "' could not be parsed according to yyyy/MM/dd, HH:mm:ss or yyyy/MM/dd HH:mm:ss");
            }
        } catch (ParseException e) {
            throw new TaglibException(e);
        }
        return date;

    }

    /**
     * Evaluate the start of the day asked for.
     */
    private Date handleDay(String snextday) throws ParseException, JspTagException {
        int nextday = ((Integer) days.get(snextday.toLowerCase())).intValue();

        // Find out which day it is.
        Calendar calendar = Calendar.getInstance(getLocale());
        calendar.setTime(new Date());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        // Calc how many days futher
        int diff = (7 + nextday - today) % 7;

        // If diff=0, calculate if we want to return the same day or next week.
        long seconds = 0;
        if (diff == 0) {
            // No offset given, return next week.
            if (offset.getString(this).equals("")) {
                diff = 7;
            } else {
                // Calculate how many seconds are already past this day
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                seconds = hours * 3600 + minutes + 60;

                long ioffset = Long.parseLong(offset.getString(this));
                // If we are not over the offset return the same day, otherwise next week.
                if (seconds > ioffset) {
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
        int month = ((Integer) months.get(smonth.toLowerCase())).intValue();

        Calendar calendar = Calendar.getInstance(getLocale());
        calendar.setTime(new Date());
        int today = calendar.get(Calendar.MONTH);

        // Calc how many months futher
        int diff = (12 + month - today) % 12;

        // If diff=0, calculate if we want to return the same day or next month.
        long seconds = 0;
        if (diff == 0) {
            // No offset is given, return next month
            if (offset.getString(this).equals("")) {
                diff = 12;
            } else {
                // Calculate how many seconds are already past this day
                int days = calendar.get(Calendar.DAY_OF_MONTH);
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                seconds = days * 24 * 3600 + hours * 3600 + minutes + 60;

                long ioffset = Long.parseLong(offset.getString(this));
                // If we are not over the offset return the same day, otherwise next month.
                if (seconds > ioffset) {
                    diff = 12;
                }
            }
        }
        // Go to the correct month
        calendar.add(Calendar.MONTH, diff);

        return getBeginOfMonth(calendar);
    }

    /**
     * convert a keyword into a date
     * @param keyword the keyword to convert
     */
    private Date handleKeyword(String keyword) throws ParseException, JspTagException {
        if (keyword.equals("now")) {
            return new Date();
        }

        int index = 0;

        if (keyword.equals("today")) {
            index = 0;
        } else if (keyword.equals("tomorrow")) {
            index = DAY;
        } else if (keyword.equals("yesterday")) {
            index = -DAY;
        }

        long now = System.currentTimeMillis();
        Date newDate = new Date(now + index);
        Calendar calendar = Calendar.getInstance(getLocale());
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
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * Finds the start of the month
     * @param date A date.
     * @return The beginning of the month of the given Date
     */
    private Date getBeginOfMonth(Calendar cal) throws ParseException {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return getBeginOfDay(cal);
    }

    /**
     * set days with correct day number
     */
    static private void setDays(DateFormatSymbols dfs) {
        String[] dayarray = dfs.getWeekdays();

        for (int i = 0; i < dayarray.length; i++) {
            days.put(dayarray[i].toLowerCase(), new Integer(i));
        }
    }

    /**
     * set month with correct month number
     */
    static private void setMonths(DateFormatSymbols dfs) {
        String[] montharray = dfs.getMonths();

        for (int i = 0; i < montharray.length; i++) {
            months.put(montharray[i].toLowerCase(), new Integer(i));
        }
    }
    /**
     * DateFormat used for parsing dates.
     */
    protected SimpleDateFormat getDateFormat() throws JspTagException {
        return new SimpleDateFormat("", getLocale());
    }
}
