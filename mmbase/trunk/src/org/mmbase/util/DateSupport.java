package org.mmbase.util;

import java.util.*;
import java.text.*;
import java.net.*;
import java.util.StringTokenizer;

/**
 * Some routines to support dates better
 * @author Rico Jansen
 * @version 21 Mar 1997
 */
public class DateSupport {

	static int offset=0;
	static boolean dooffset=false;

	static {
		String host=null;	
		try {
			String tmp=""+InetAddress.getLocalHost();
			int pos=tmp.indexOf("/");
			if (pos!=-1) {
				host=tmp.substring(0,pos);
				System.out.println("HOST="+host);
			}
		} catch(Exception e) {
		}

		ExtendedProperties prop = new ExtendedProperties("/mm/vpro/james/util/DateSupport_"+host+".properties"); // 1.2
		if (prop.size()>0) {
		String fileoffset=(String)prop.get("OFFSET");
		try {
			offset=Integer.parseInt(fileoffset);
			dooffset=true;
			System.out.println("OFFSET="+offset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	}	

	static public int daysInMonth(int year, int month) {
		int months[]={ 31,28,31,30,31,30,30,31,30,31,30,31 };
		int days = months[month];
		year = (year<90) ? year+2000 : year+1900;

		// Make an exception for the intercalary day. 
		if (month==1) {
			if(year%4==0 && year%100!=0 || year%400==0) days=29;
		} 
		return days;
	}

	static public int secondInYear(Date d) {
        Date b = new Date ((d.getYear()),0,0);
        return((int)((d.getTime()-b.getTime())/1000));
    }

	static public int dayInYear(Date d) {
		return((int)(secondInYear(d)/(3600*24)));
	}

	static public int weekInYear(Date d) {
		return((dayInYear(d)/7)+1);
	}

	static public long milliDate(int year,int week) {
		Date d;
		d=new Date(year,0,0);
		return(d.getTime()+(((long)(week-1))*7*24*3600*1000));
	}

	static public Date Date(int year,int week,int day) {
		Date d;
		int dag;
		d=new Date(milliDate(year,week));
		day%=7;
		dag=d.getDay();
		while (day!=dag) {
			// shift forward (day-dag)
//			System.out.println("Shift "+dag+" -> "+day);
			d=new Date(milliDate(year,week)+((day-dag)*24*3600*1000));
			dag=d.getDay();
		}
		return(d);
	}

	static public String datumToType(String datum,String type) {
		Date d;
		int id;

		try {
			id=Integer.parseInt(datum);
		} catch (NumberFormatException e) {
			id=0;
		}
		if (id==0) id=(int)(System.currentTimeMillis()/1000);
		d=new Date(1000*(long)id);
		if (type.equals("Artikel")) {
//			System.out.println("DateSupport (datumToType) -> "+datum+" , Atype: "+type);
			return(RFC1123.makeDate(d));
		} else if (type.equals("Gids")) {
//			System.out.println("DateSupport (datumToType) -> "+datum+" , Gtype: "+type);
			return("Gids "+DateSupport.weekInYear(d)+" '"+d.getYear());
		} else {
//			System.out.println("DateSupport (datumToType) -> "+datum+" , Ntype: "+type);
			return(datum);
		}
	}

	static public String typeToDatum(String datum,String type) {
		Date d;

		if (type.equals("Artikel")) {
//			System.out.println("DateSupport (typeToDatum) -> "+datum+" , Atype: "+type);
			d=new Date(datum.substring(5));
			return(""+(d.getTime()/1000));
		} else if (type.equals("Gids")) {
			String y,w;
			int iy,iw;
			long l;

//			System.out.println("DateSupport (typeToDatum) -> "+datum+" , Gtype: "+type);

			try {
				w=datum.substring(5,datum.indexOf(" ",5));
//				System.out.println("Gids -> '"+w+"'");
				y=datum.substring(datum.indexOf("'")+1);
//				System.out.println("Gids -> '"+y+"'");
				try {
					iw=Integer.parseInt(w);
					iy=Integer.parseInt(y);
				} catch (NumberFormatException e) {
					iw=iy=0;
				}
				l=milliDate(iy,iw);
			} catch (Exception e ) {
				l=0;
			}
			d=new Date(l);
			return(""+(d.getTime()/1000));
		} else {
			System.out.println("DateSupport (typeToDatum) -> "+datum+" , Ntype: "+type);

			return(datum);
		}
	}

	/**
	 * Create date strings in the form yyyy-mm-dd
	 */
	public static String makedbmdate(Date da) {
		int m,d,y;
		m=da.getMonth()+1;
		d=da.getDate();
		y=da.getYear()+1900;
		return(""+y+"-"+(m<10 ? "0"+m : ""+m)+"-"+(d<10 ? "0"+d : ""+d));
	}

	/**
	 * parse date strings in the form yyyy-mm-dd
	 */
	public static Date parsedbmdate(String wh) {
		Date thedate;
		int y=0,m=0,d=0;
		StringTokenizer tok=new StringTokenizer(wh,"- /");
		// The date is in the form yyyy-mm-dd
		try {
			y=Integer.parseInt(tok.nextToken())-1900;
			m=Integer.parseInt(tok.nextToken())-1;
			d=Integer.parseInt(tok.nextToken());
			thedate=new Date(y,m,d);
		} catch (Exception e) {
//			System.out.println("DateSupport: parsedbmdate "+y+","+m+","+d+" : "+tok+" , "+d);
			thedate=new Date();
		}
		return(thedate);
	}

	/**
	 * Puts a colon between a time of 1223 format
	 */
	public static String colontime(String time) {
		if (time.length()==4) {
			return(time.substring(0,2)+":"+time.substring(2,4));
		}
		return(time);
	}


	public static int parsedate( String sDate )
	{
		SimpleDateFormat 	df 	 = (SimpleDateFormat)DateFormat.getDateTimeInstance();
		TimeZone tz;
		df.applyLocalizedPattern("yyyyMMdd");

		tz=TimeZone.getTimeZone("ECT"); //Apparently we live there ?
		df.setTimeZone(tz);

		Date		date = null;
		try
		{
			date = df.parse( sDate);	
		}
		catch( java.text.ParseException e )
		{
			System.out.println( e.toString() );
		}

		if( date != null)
			return 	(int)((date.getTime()-getMilliOffset())/1000);
		else
			return -1;
	}
	//--------------------------------------------------

	/**
	 * parse date strings in the form yyyymmdd
	 */
	public static int parsedate_old(String wh) {
		Date thedate;
		int y=0,m=0,d=0;
		// The date is in the form yyyymmdd
		try {
			y=Integer.parseInt(wh.substring(0,4))-1900;
			m=Integer.parseInt(wh.substring(4,6))-1;
			d=Integer.parseInt(wh.substring(6,8));
			thedate=new Date(y,m,d);
		} catch (Exception e) {
//			System.out.println("DateSupport: parsedate ("+wh+") "+y+","+m+","+d+" , "+d);
			thedate=new Date();
		}
		return((int)((thedate.getTime())/1000));
	}

	/**
	 * parse time strings in the form hhmmss
	 */
	public static int parsetime(String wh) {
		int h=0,m=0,s=0;
		try {
			h=Integer.parseInt(wh.substring(0,2));
			m=Integer.parseInt(wh.substring(2,4));
			s=Integer.parseInt(wh.substring(4,6));
		} catch (Exception e) {
			System.out.println("DateSupport: maketime ("+wh+")");
		}
		return(s+60*(m+60*h));
	}

	/**
	 * parse time strings in the from yyyymmddhhmmss
	 */
	public static int parsedatetime(String wh) {
		return(parsedate(wh.substring(0,8))+parsetime(wh.substring(8,14)));
	}


	/**
	 * returns time in strings to hhmm
	 */
	public static String  getTime(int val) {
		if (dooffset) {
			val+=offset;
		}
		Date v=new Date((long)val*1000);
		String result;
		int h=v.getHours();
		if (h<10) {
			result="0"+h;
		} else {
			result=""+h;
		}
		int m=v.getMinutes();
		if (m<10) {
			result+=":0"+m;
		} else {
			result+=":"+m;
		}
		return(result);
	}


	/**
	 * returns time in strings to hhmm
	 */
	public static String  getTimeSec(int val) {
		Date v;
		if (val == -1) {
			v = new Date ();
		}
		else {
			if (dooffset) {
				val+=offset;
			}
			v = new Date((long)val*1000);
		}

		String result;
		int h=v.getHours();
		if (h<10) {
			result="0"+h;
		} else {
			result=""+h;
		}
		int m=v.getMinutes();
		if (m<10) {
			result+=":0"+m;
		} else {
			result+=":"+m;
		}
		int s=v.getSeconds();
		if (s<10) {
			result+=":0"+s;
		} else {
			result+=":"+s;
		}
		return(result);
	}


	/**
	 * returns time in strings to hhmm
	 */
	public static String  getTimeSecLen(int val) {
		String result;
		int h=(val/3600);
		if (h<10) {
			result="0"+h;
		} else {
			result=""+h;
		}
		val-=(h*3600);


		int m=(val/60);
		if (m<10) {
			result+=":0"+m;
		} else {
			result+=":"+m;
		}
		val-=(m*60);

		int s=val;
		if (s<10) {
			result+=":0"+s;
		} else {
			result+=":"+s;
		}
		return(result);
	}


	/**
	 * returns time in strings to hhmm
	 */
	public static String getMonthDay(int val) {
		if (dooffset) {
			val+=offset;
		}
		Date v=new Date((long)val*1000);
		String result;
		int d=v.getDate();
		if (d<10) {
			result="0"+d;
		} else {
			result=""+d;
		}
		return(result);
	}


	/**
	 * returns time in strings to hhmm
	 */
	public static String getMonth(int val) {
		if (dooffset) {
			val+=offset;
		}
		Date v=new Date((long)val*1000);
		String result;
		int m=v.getMonth();
		m++;
		if (m<10) {
			result="0"+m;
		} else {
			result=""+m;
		}
		return(result);
	}


	/**
	 * returns time in strings to hhmm
	 */
	public static String getYear(int val) {
		//System.out.println(val);
		if (dooffset) {
			val+=offset;
		}
		Date v=new Date(((long)val)*1000);
		int m=v.getYear();
		return(""+(m+1900));
	}


	/**
	 * returns time in strings to hhmm
	 */
	public static int getMonthInt(int val) {
		if (dooffset) {
			val+=offset;
		}
		Date v=new Date((long)val*1000);
		String result;
		int m=v.getMonth();
		return(m);
	}


	/**
	 * returns time in strings to hhmm
	 */
	public static int getWeekDayInt(int val) {
		if (dooffset) {
			val+=offset;
		}
		Date v=new Date((long)val*1000);
		int m=v.getDay();
		return(m);
	}

	/**
	 * returns time in strings to hhmm
	 */
	public static int getDayInt(int val) {
		if (dooffset) {
			val+=offset;
		}
		Date v=new Date((long)val*1000);
		int m=v.getDate();
		return(m);
	}

	public static long getMilliOffset() {
		if (!dooffset) {
			TimeZone tz1,tz2;
			long off=5400;
			int off1,off2;
			Date d=new Date();
	
			tz1=TimeZone.getDefault(); // This is MET but they think it's the Middle East
			tz2=TimeZone.getTimeZone("ECT"); //Apparently we live there ?
			off1=tz1.getRawOffset();
			off2=tz2.getRawOffset();
			if (tz1.inDaylightTime(d)) {
				if (System.getProperty("os.name").equals("Linux")) {
	//				System.out.println("Linux");
					off1+=(3600*1000); // Activate before sunday morning
				} else {
					off1+=(3600*1000);
				}
			}
			if (tz2.inDaylightTime(d)) {
				off2+=(3600*1000);
			} else {
	//			System.out.println("DateSupport Warning Invalid timezone");
	//			off2+=(3600*1000);
			}
	
			off=off1-off2;
			return(off);
		} else {
			return((long)offset*1000);
		}
	}

	public static long currentTimeMillis() {
		return(System.currentTimeMillis()-getMilliOffset());
	}


  /**
   * Convert a string (like "12:42:15 1/2/97") to milliseconds from 1970
   * @param date String which contains the date and time in the format "hour:minutes:sec day/month/year"
   * @return the elapsed milliseconds since 1970 from this date
   */
   public static long convertDateToLong( String date ) 
   {
	// Next line was the old code:
	// return (convertStringToLong(date));

	Calendar cal = null;
      
	cal = setTimeZone(3,30) ;
	cal = parseDate( cal, date );

	Date d = cal.getTime ();
	long l = d.getTime ();

	// Hack!
/*
	if ((cal.getTimeZone ()).inDaylightTime (d)) {
		l += 60 * 60 * 1000;
		d.setTime (l);
	}
*/

	return l;
   }

  /**
   * Convert date to long with timezone-offset
   * 
   * example : convertDateToLongWithTimeZone ( "14:12:56 3/5/1998", 3, 30 )
   *           will convert the date to milliseconds passes from 1970 untill this date with -3:30 timezone 
   */
   public static long convertDateToLongWithTimeZone( String date, int hour, int minutes )
   {
      return( convertStringToLongWithTimeZone( date, hour, minutes ) );
   }



  /*
   * ----- private functions used by convertDateToLong --------
   */

   private static long convertStringToLong( String date )
   {
         // Set timezone to local timezone (Netherlands = 3:30 difference)
      return ( convertDateToLongWithTimeZone( date, 3, 30) );   
   }

   private static long convertStringToLongWithTimeZone( String date , int hour, int minutes)
   {
         // Set timezone 
      Calendar calendar = setTimeZone(hour,minutes);
         // Now convert the datestring to calendardate 
      calendar = parseDate( calendar, date);
         // calculate the milliseconds since 1970
      Date   mydate = calendar.getTime();
         // return this calculation
      return(mydate.getTime());
   }

   private static Calendar setTimeZone(int hours, int minutes)
   {
               // get the supported ids for GMT-08:00 (Pacific Standard Time)
        String[] ids = TimeZone.getAvailableIDs((hours * 60 + minutes) * 60 * 1000);
               // if no ids were returned, something is wrong. get out.
        if (ids.length == 0)
        {
           System.out.println("Timezone is wrong...");
           System.exit(0);
        }
        System.out.println("Current Time");
               // create a Pacific Standard Time time zone
        SimpleTimeZone pdt = new SimpleTimeZone((hours * 60+minutes) * 60 * 1000, ids[0]);
               // set up rules for daylight savings time
        pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
               // create a GregorianCalendar with the Pacific Daylight time zone
               // and the current date and time
        Calendar calendar = new GregorianCalendar(pdt);
      return (calendar);
   }
 
   public static Calendar parseDate(Calendar cal, String date)
   {
      StringTokenizer tok = new StringTokenizer(date, "-\n\r:/ ");
      String token = null;
 
         cal.clear(Calendar.HOUR_OF_DAY);
 
         token = tok.nextToken();
         cal.set(Calendar.HOUR_OF_DAY,         new Integer(token).intValue());
         token = tok.nextToken();
         cal.set(Calendar.MINUTE,      new Integer(token).intValue());
         token = tok.nextToken();
         cal.set(Calendar.SECOND,      new Integer(token).intValue());
         token = tok.nextToken();
         cal.set(Calendar.DAY_OF_MONTH, new Integer(token).intValue());
         token = tok.nextToken();
         cal.set(Calendar.MONTH,        new Integer(token).intValue()-1);
         token = tok.nextToken();
         cal.set(Calendar.YEAR,         new Integer(token).intValue());
      return (cal);
  }

   public static Calendar parseDateRev(Calendar cal, String date)
   {
      StringTokenizer tok = new StringTokenizer(date, "-\n\r:/ ");
      String token = null;
 
         cal.clear(Calendar.HOUR_OF_DAY);
 
         token = tok.nextToken();
         cal.set(Calendar.YEAR,         new Integer(token).intValue());
         token = tok.nextToken();
         cal.set(Calendar.MONTH,        new Integer(token).intValue()-1);
         token = tok.nextToken();
         cal.set(Calendar.DAY_OF_MONTH, new Integer(token).intValue());
         token = tok.nextToken();
         cal.set(Calendar.HOUR_OF_DAY,         new Integer(token).intValue());
         token = tok.nextToken();
         cal.set(Calendar.MINUTE,      new Integer(token).intValue());
         token = tok.nextToken();
         cal.set(Calendar.SECOND,      new Integer(token).intValue());
      return (cal);
  }

	public static String date2string (int time) {
		return(getTimeSec(time)+" "+getMonthDay(time)+"/"+getMonth(time)+"/"+getYear(time));				
	}

	public static String date2day(int time) {
		return(getYear(time)+"-"+getMonth(time)+"-"+getMonthDay(time));
	}

	public static String date2date(int time) {
		return(getYear(time)+"-"+getMonth(time)+"-"+getMonthDay(time)+" "+getTimeSec(time));
	}

	private static String dumpdate(int d) {
		Date dd=new Date((long)d*1000);
		StringBuffer b=new StringBuffer();
		
		b.append(" Year "+dd.getYear());
		b.append(" Month "+(dd.getMonth()+1));
		b.append(" Day "+dd.getDate());
		b.append(" Weekday "+dd.getDay());
		b.append(" Hours "+dd.getHours());
		b.append(" Minutes "+dd.getMinutes());
		b.append(" Seconds "+dd.getSeconds());
		b.append(" Time "+dd.getTime());
		return(b.toString());
	}


	public static void main(String args[]) {
		System.out.println("Date (without corr)"+date2string((int)(System.currentTimeMillis()/1000))+" "+System.currentTimeMillis()/1000);
		System.out.println("Date (with corr)"+date2string((int)(DateSupport.currentTimeMillis()/1000))+" : "+DateSupport.currentTimeMillis()/1000);
		System.out.println("Date "+args[0]+" "+date2string(Integer.parseInt(args[0])));
		System.out.println("Date "+args[0]+" "+dumpdate(Integer.parseInt(args[0])));
        String ID = System.getProperty("user.timezone", "GMT");
		System.out.println("ID "+ID+" : "+getMilliOffset());
		System.out.println("ParseDate "+parsedate(args[1]));
//		System.out.println("ParseDate "+parsedate2(args[1]));
	}
}
