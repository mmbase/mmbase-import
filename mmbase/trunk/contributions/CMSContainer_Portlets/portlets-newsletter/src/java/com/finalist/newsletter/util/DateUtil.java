package com.finalist.newsletter.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static Date parser (String date) throws ParseException{

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.parse(date);
	}

	public static String parser (Date date){

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return DateFormat.getDateInstance().format(date);
	}
}
