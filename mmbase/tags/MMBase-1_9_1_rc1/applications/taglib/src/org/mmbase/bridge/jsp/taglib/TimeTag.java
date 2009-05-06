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
import org.mmbase.util.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

/**
 * The time taglib provides you easy functionality for using times in your web pages.
 *
 * @author  Rob Vermeulen (VPRO)
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.6
 * @version $Id: TimeTag.java,v 1.58 2007-07-18 07:50:47 michiel Exp $
 */
public class TimeTag extends ContextReferrerTag implements Writer, WriterReferrer {

    private static final Logger log = Logging.getLoggerInstance(TimeTag.class);

    private Attribute time        = Attribute.NULL;
    private Attribute inputFormat = Attribute.NULL;
    private Attribute offset      = Attribute.NULL;

    private Attribute precision   = Attribute.NULL;
    private Attribute relevance   = Attribute.NULL;

    private Attribute timezone    = Attribute.NULL;

    private final static int PRECISION_UNSET = -1;
    private final static int PRECISION_SECONDS = 1;
    private final static int PRECISION_MINUTES = 2;
    private final static int PRECISION_HOURS = 3;
    private final static int PRECISION_DAYS = 4;
    private final static int PRECISION_WEEKS = 5;
    private final static int PRECISION_MONTHS = 6;
    private final static int PRECISION_YEARS = 7;
    private final static int PRECISION_PARSE = 100;


    /**
     * Format attribute used for displaying the dates.
     */
    private Attribute dateFormat = Attribute.NULL;

    /**
     * @since MMBase-1.7.1
     */
    protected Date _date;

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

    protected static int getPrecisionConstant(String p) throws JspTagException {
        if (p.length() == 0) {
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
            return PRECISION_PARSE;
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

    public TimeZone getTimeZone()  {
        try {
            String tz = timezone.getString(this);
            if (tz != null && !"".equals(tz)) {
                return TimeZone.getTimeZone(tz);
            } else {
                return super.getTimeZone();
            }
        } catch (JspTagException jte) {
            return super.getTimeZone();
        }
    }

    protected DateFormat getFormat() throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("format: '" + dateFormat + "'");
        }
        return  org.mmbase.util.DateFormats.getInstance(dateFormat.getString(this), getTimeZone().getID(), getLocale());
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
        return _date;
    }

    public int doStartTag() throws JspTagException {
        log.debug("Start-tag of mm:time ");
        try {
            _date = evaluateTime();
        } catch (ParseException pe) {
            throw new TaglibException(pe);
        }
        if (log.isDebugEnabled()) {
            log.debug("Using date " + _date);
        }
        if (helper.getVartype() == WriterHelper.TYPE_DATE) {
            helper.setValue(_date);
        } else {
            String formattedDate = format(_date);
            if (log.isDebugEnabled()) {
                log.debug("Formatteddate " + formattedDate);
            }
            helper.setValue(formattedDate);
        }

        if (getId() != null) {
            if (helper.getVartype() == WriterHelper.TYPE_DATE) {
                getContextProvider().getContextContainer().register(getId(), _date);
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

    public void doFinally() {
        inputFormat = Attribute.NULL;
        offset = Attribute.NULL;
        dateFormat = Attribute.NULL;
        super.doFinally();
    }

    /**
     * Evaluate the time attribute.
     * @javadoc
     */
    private Date evaluateTime() throws JspTagException, ParseException {
        if (log.isDebugEnabled()) {
            log.debug("time: '" + time + "' offset: '" + offset + "' format: '" + dateFormat + "' inputformat: '" + inputFormat + "'");
        }

        String useTime = null;
        Date date = null;
        // If the time attribute is not set, check if referid is used, otherwise check if the parent set the time.
        // Otherwise use current time
        if (time == Attribute.NULL) {
            if (getReferid() != null) { // try to get it from other time tag
                Object object = getObject(getReferid());
                if (object instanceof Date) {
                    date = (Date) object;
                } else {
                    try {
                        useTime = Casting.toString(object);
                    } catch (Error e) {
                        log.debug(e);
                        useTime = null;
                    }
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

        TimeZone tz = getTimeZone();
        String iformat = inputFormat.getString(this);
        if (iformat.length() == 0) {
            if (date == null) {
                try {
                    String tzTime = useTime.startsWith("TZ") ? useTime : ("TZ" + tz.getID() + " " + useTime);
                    date = DynamicDate.eval(DynamicDate.getInstance(tzTime));
                } catch (Throwable e) {
                    log.debug(e);
                    // Try to parse it in three standard ways, this can be considered Legacy, because DynamicDate.getInstance can handle everything already.
                    date = parseTime(useTime); 
                }
            }
        } else { // The input format is provided. We use that to parse the time attribute
            try {
                // nothing to do with TIMEZONE?
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

        date = handleOffset(date);
        try {
            date = handlePrecision(date);
        } catch (org.mmbase.util.dateparser.ParseException pe) {
            throw new TaglibException(pe);
        }
        date = handleRelevance(date);
        return date;
    }


    private String format(Date date) throws JspTagException {
        if (dateFormat == Attribute.NULL) {
            // If no format is specified, we return the time in second from EPOC (UTC)
            return "" + date.getTime() / 1000;
        } else {
            return getFormat().format(date);
        }
    }
    
    private Date handleOffset(Date date) throws JspTagException {
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
        return date;
    }
    
    private Date handlePrecision(Date date) throws JspTagException, org.mmbase.util.dateparser.ParseException {
        // precision sets fields of date opbject to 0 starting with least relevant bits (for caching purposes)
        if (precision != Attribute.NULL) {
            Calendar cal = Calendar.getInstance(getTimeZone(), getLocale());
            cal.setTime(date);
            int prec = getPrecision();
            switch (prec) {
            case PRECISION_PARSE:
                long p = DynamicDate.getInstance("duration + " + precision.getString(this)).getTime();
                cal.setTime(new Date((date.getTime() / p) * p));
                break;
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
        return date;
    }
    private Date handleRelevance(Date date) throws JspTagException  {
        // relevance (which is not in tld (yet), works the other way around. It sets the 'maximum
        // relevance bit'. If for example you set it to 'months', then the year is set to 0 and can
        // be ignored. In this way you can e.g. check if it january, or if it is somebody's birthday.
        if (relevance != Attribute.NULL) {
            Calendar cal = Calendar.getInstance(getTimeZone(), getLocale());
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
        return date;
    }

    /**
     * LEGACY.
     *
     * The time is giving in a standard format i.e. yyyy/mm/dd
     * hh:mm:ss, yyyy/mm/dd, or hh:mm:ss.  This function parses and
     * returns the corresponding Date object, or throws an exception
     * if unsuccessful.
     * So, it parses the 'time' attribute without the use of 'inputformat'.
     *
     */
    private Date parseTime(String t) throws ParseException, JspTagException {

        Date date = new Date();
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
            throw new ParseException("Time '" + t + "' could not be parsed  according to yyyy/MM/dd, HH:mm:ss or yyyy/MM/dd HH:mm:ss", 0);
        }

        return date;

    }


    /**
     * DateFormat used for parsing dates.
     */
    protected SimpleDateFormat getDateFormat() throws JspTagException {
        return new SimpleDateFormat("", getLocale());
    }
}
