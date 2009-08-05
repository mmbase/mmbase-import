/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author ebunders
 * this class is a bean for storing a date and a time.
 * They are separate becouse you may want to use different input fields in the 
 * html form.
 * You can set the date formate for date and time separetely. default is: dd-MM-yyyy kk:mm:ss
 *
 */
public class DateTime {
	private String dateFormat = "dd-MM-yyyy";
	private String timeFormat = "kk:mm:ss";
    
    private static final Logger log = Logging.getLoggerInstance(DateTime.class);
    
    private SimpleDateFormat sdf = new SimpleDateFormat(dateFormat  + timeFormat);
    
	
	private String date;
	private String time;
    
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
        log.info("setting date to "+date);
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public Date getParsedDate() throws ParseException{
		return sdf.parse(date + time);
	}
	
	public long getDateInSeconds() throws ParseException {
		return  getParsedDate().getTime()/1000;
	}
	
	public String toString(){
		String result = date  + time;
		
		try {
			result = getParsedDate().toString();
		} catch (ParseException e) {
			result += " unparseable";
		}
		
		return result;
	}
    
    
    /**
     * override the date format string. a new dateformat object is created
     * @param dateFormat
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        sdf = new SimpleDateFormat(dateFormat  + timeFormat);
    }
    
    /**
     * override the time format string. a new dateformat object is created
     * @param timeFormat
     */
    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
        sdf = new SimpleDateFormat(dateFormat  + timeFormat);
    }
}
