/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.bridge.jsp.taglib;

import java.util.*;
import java.text.*;
import org.mmbase.util.logging.*;
import org.mmbase.bridge.jsp.taglib.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

/**
 * The time taglib provides you easy functionality for using times in your web pages.
 *
 * @author  Rob Vermeulen (VPRO)
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.6
 * @version $Id: TimeTag.java,v 1.15 2002-10-16 21:18:49 michiel Exp $
 */
public class TimeTag extends ContextReferrerTag implements Writer {
    
    private static Logger log = Logging.getLoggerInstance(TimeTag.class.getName());
    protected WriterHelper helper = new WriterHelper();
    
    private static final int DAY = 1000*60*60*24;
    
    private String time = null;
    private String inputformat = null;
    private String offset = null;
 
    /**
     * DateFormat user for parsing dates. Given dates are always in english. 
     */
    private SimpleDateFormat parseFormat = new SimpleDateFormat("", Locale.ENGLISH);

    /**
     * Format attribute used for displaying the dates.
     */
    private DateFormat dateFormat;
    
    /**
     * Fast way to find the day number of a day
     */
    static private Hashtable days = new Hashtable();
    /**
     * Fast way to find the month number of a month
     */
    static private Hashtable months = new Hashtable();
    
    static {
        DateFormatSymbols dfs = new SimpleDateFormat("", Locale.ENGLISH).getDateFormatSymbols();
        setDays(dfs);
        setMonths(dfs);
    }


    // Writer functionality
    public void haveBody() { helper.haveBody(); }

       
    public void setVartype(String t) throws JspTagException {
        helper.setVartype(t);
    }
    
    public void setJspvar(String j) {
        helper.setJspvar(j);
    }
    
    public void setWrite(String w) throws JspTagException {
        helper.setWrite(getAttributeBoolean(w));
    }
    
    // Attributes
    public void setTime(String time) throws JspTagException {
        if (log.isDebugEnabled()) log.debug("time: '"+time+"'");
        this.time = getAttributeValue(time);
    }
    
    public void setFormat(String f) throws JspTagException {
        if (log.isDebugEnabled()) log.debug("format: '"+f+"'");
        
        String format = getAttributeValue(f);
        Locale locale; 
                       
        LocaleTag localeTag = (LocaleTag) findParentTag("org.mmbase.bridge.jsp.taglib.LocaleTag", null, false);
        if (localeTag != null) {
            locale = localeTag.getLocale();
        } else {
            locale = org.mmbase.bridge.LocalContext.getCloudContext().getDefaultLocale();
        }              

        // symbolic formats. Perhaps will be moved to another attribute or so.
        if (format.charAt(0) == ':') {
            log.debug("found symbolic format");
            if (format.charAt(1) == '.') {
                dateFormat = DateFormat.getTimeInstance(getDateFormatStyle(format.substring(2)), locale);
            } else if (format.indexOf('.') == -1) {
                dateFormat = DateFormat.getDateInstance(getDateFormatStyle(format.substring(1)), locale);
            } else {
                int i = format.indexOf('.');
                dateFormat = DateFormat.getDateTimeInstance(getDateFormatStyle(format.substring(1, i)), 
                                                            getDateFormatStyle(format.substring(i+1)), locale);
            }
        } else {
            dateFormat = new SimpleDateFormat(format, locale);
        }

    }    
    
    public void setInputformat(String inputformat) throws JspTagException {
        this.inputformat = getAttributeValue(inputformat);
    }
    
    public void setOffset(String offset) throws JspTagException {
        this.offset = getAttributeValue(offset);
    }
        
    public Object getWriterValue() throws JspTagException {
        return evaluateTime();
    }
    

    
    public int doStartTag() throws JspTagException {
        helper.setValue(evaluateTime());
        helper.setJspvar(pageContext); 
      
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }        
        time = null; // time variable is set without use of setTime,
                     // precautionally we set it to null here. I don't
                     // think it is really needed.
        return EVAL_BODY_BUFFERED;
    }
    
    public int doAfterBody() throws JspException {
        helper.setBodyContent(getBodyContent());
        return super.doAfterBody();
    }


    public int doEndTag() throws JspTagException {
        return helper.doEndTag();
    }
    
    public void release () {
        time = null;
        inputformat = null;
        offset = null;
        dateFormat = null;
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
        if (log.isDebugEnabled()) log.debug("time: '"+time+"' offset: '"+offset+"' format: '"+dateFormat+"' inputformat: '"+inputformat +"'");
        
        Date date = null; 
        // If the time attribute is not set, check if referid is used, otherwise check if the parent set the time.
        // Otherwise use current time
        if(time == null) {
            if(getReferid() != null) { // try to get it from other time tag
                time = getString(getReferid());
            } else {                   // try to get it from parent writer.
                Writer w =  (Writer) findParentTag("org.mmbase.bridge.jsp.taglib.Writer", null, false);
                if (w != null) {
                    time = "" + w.getWriterValue();
                }
            }
            if(time == null) { // still not found
                throw new JspTagException("Cannot evaluate time. No time attribute given, no referid attribute set, and no writer parent tag found.");
            }
        } 

        if(time != null){ // time was given in attribute
            // Is the time given in second from EPOC (UTC)?
            try {
                long timeFromEpoc = Long.parseLong(time);
                date = new Date(timeFromEpoc*1000);
            } catch (NumberFormatException nfe) {
                // perhaps it was a keyWord... 
                // this will be explored hereafter.
                // TODO Should we depend on exceptions? I think this is slow (?), and also ugly (though that is a matter of taste).
                // indeed, using exceptions as if statements is rather low performance.
                log.debug("Time not given in second from epoc");
            }
        }       
        
        // Is a day specified, like: monday, tuesday ?
        if(date == null && isDay(time)) {
            try {
                date = handleDay(time);
            } catch (ParseException e) {  
                String msg = "Cannot evaluate handleDay with time '" + time + "' (exception:" + e + ")";
                // Why should we log this? designers dont have access to logs
                // log.error(msg);
                throw new JspTagException(msg);
            }
        }
        // Is a month specified, like: january, february ?
        if(date == null && isMonth(time)) {
            try {
                date = handleMonth(time);
            } catch (ParseException e) {
                String msg = "Cannot evaluate handleMonth with time '" + time + "' (exception:" + e + ")";
                // Why should we log this? designers dont have access to logs
                // log.error(msg);
                throw new JspTagException(msg);
            }
        }
        // Is a keyword used, like: yesterday, today ?
        if(date == null && isKeyword(time)) {
            try {
                date = handleKeyword(time);
            } catch (ParseException e) {
                String msg = "Cannot evaluate handleKeyword with time '" + time + "' (exception:" + e + ")";
                // Why should we log this? designers dont have access to logs
                // log.error(msg);
                throw new JspTagException(msg);
            }
        }
        

        // still not found, look if for other formats, like yyyy-MM-ss
        if(date == null) {
            if (inputformat == null) {  // If no input format is given try to parse it in three standard ways.                
                date = getDate();
            } else { // The input format is provided. We use that to parse the time attribute
                try {
                    parseFormat.applyPattern(inputformat);
                    date = parseFormat.parse(time);
                } catch (Exception e) {
                     throw new JspTagException(e.toString());
                }
            }
        }

               
        if (log.isDebugEnabled()) log.debug("date: '" + date + "'");

        if (date == null) { // don't know if it can come here, but if it does, an exception must be thrown!
            throw new JspTagException("Could not evalutate time " + time);
        }

        // Calculate the offset
        if(offset != null) {
            long calculatedDate = date.getTime();
            long os = Long.parseLong(offset)*1000;
            date = new Date(calculatedDate + os);
        }
        
        if (dateFormat == null) {
            // If no format is specifyd, we return the time in second from EPOC (UTC)
            return "" + date.getTime()/1000;
        } else {
            return dateFormat.format(date);
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

     */
    private Date getDate() throws JspTagException {
        if(time == null) return null;

        Date date = new Date(); 
        try {
            if(time.length() == 10) {
                parseFormat.applyPattern("yyyy/MM/dd");
                date = parseFormat.parse(time);
            } else if(time.length() == 8) {
                parseFormat.applyPattern("HH:mm:ss");
                date = parseFormat.parse(time);
            } else if(time.length() == 19) {
                parseFormat.applyPattern("yyyy/MM/dd HH:mm:ss");
                date = parseFormat.parse(time);
            } else {
                throw new JspTagException("Time could not be parsed according to yyyy/MM/dd, HH:mm:ss or yyyy/MM/dd HH:mm:ss");
            }
        } catch (ParseException e) {
            throw new JspTagException(e.toString());
        }
        return date;
        
    }
    
    
    /**
     * Evaluate the start of the day asked for.
     */
    private Date handleDay(String snextday) throws ParseException {
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
            if(offset==null){
                diff=7;
            } else {
                // Calculate how many seconds are already past this day
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                seconds = hours*3600+minutes+60;
                
                long ioffset = Long.parseLong(offset);
                // If we are not over the offset return the same day, otherwise next week.
                if(seconds>ioffset) {
                    diff=7;
                }
            }
        }
        
        // Go to the correct day
        calendar.add(Calendar.DAY_OF_WEEK,diff);
        
        return getBeginOfDay(calendar.getTime());
    }
    
    /**
     * Evaluate the start of the month asked for.
     */
    private Date handleMonth(String smonth) throws ParseException {
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
                
                long ioffset = Long.parseLong(offset);
                // If we are not over the offset return the same day, otherwise next month.
                if(seconds>ioffset) {
                    diff=12;
                }
            }
        }
        // Go to the correct month
        calendar.add(Calendar.MONTH,diff);
        
        return getBeginOfMonth(calendar.getTime());
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
            index=0;
        } else
            if(keyword.equals("tomorrow"))   {
                index=DAY;
            } else
                if(keyword.equals("yesterday"))   {
                    index=-DAY;
                }
        
        long now = System.currentTimeMillis();
        Date newdate = new Date(now+index);
        
        return getBeginOfDay(newdate);
    }
    
    /**
     * Finds the start of the day.
     * @param date A date.
     * @return The beginning of the day of the given Date
     */
    private Date getBeginOfDay(Date date) throws ParseException {
        Calendar cal = Calendar.getInstance(); 
        cal.setTime(date);
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
    private Date getBeginOfMonth(Date date) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH,  1);
        return getBeginOfDay(cal.getTime());
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
}
