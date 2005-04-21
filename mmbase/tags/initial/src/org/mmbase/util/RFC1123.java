/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.util;

import java.util.*;
import java.text.*;
import java.lang.*;

public class RFC1123 {
static String days[]={ "Sun, ","Mon, ","Tue, ","Wed, ","Thu, ","Fri, ","Sat, ","Sun, " };
static String months[]={ "Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec" };

	public static String makeDateV1(Date d) {
		return(days[d.getDay()]+d.toGMTString());
	}

	public static void main(String args[]) {

		System.out.println("Date "+makeDateV1(new Date()));
		System.out.println("Date "+makeDateV2(new Date()));
		System.out.println("Date(corr) "+makeDateV1(new Date(DateSupport.currentTimeMillis())));
		System.out.println("Date(corr) "+makeDateV2(new Date(DateSupport.currentTimeMillis())));

	}
	public static String makeDate(Date d) { 
		return(makeDateV2(d));
	}

	public static String makeDateV2(Date d) {

        DateFormat formatter=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(d);
	}

}
