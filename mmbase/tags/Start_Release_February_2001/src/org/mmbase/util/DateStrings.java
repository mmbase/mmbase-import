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
	public String day[];	
	public String longday[];	
	public String month[];	
	public String longmonth[];	

	/**
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


	/* English */
	public static String days[]={ "Sun","Mon","Tue","Wed","Thu","Fri","Sat","Sun" };
	public static String longdays[]={ "Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday" };
	public static String months[]={ "Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec" };
	public static String longmonths[]={ "January","February","March","April","May","June","July","Augustus","September","October","November","December" };

	/* Dutch */
	public static String Dutch_days[]={ "Zon","Maa","Din","Woe","Don","Vry","Zat","Zon" };
	public static String Dutch_longdays[]={ "Zondag","Maandag","Dinsdag","Woensdag","Donderdag","Vrijdag","Zaterdag","Zondag" };
	public static String Dutch_months[]={ "Jan","Feb","Maa","Apr","Mei","Jun","Jul","Aug","Sep","Okt","Nov","Dec" };
	public static String Dutch_longmonths[]={ "Januari","Februari","Maart","April","Mei","Juni","Juli","Augustus","September","Oktober","November","December" };
}
