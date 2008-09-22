package com.finalist.newsletter.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
  *   date Util class 
  *
  */
public class DateUtil {
   private static Log log = LogFactory.getLog(DateUtil.class);

   /**
     *   parser string("yyyy-MM-dd") to date object
     *
     *   @param raw string object
     *   @return Date object
     *
     */
   public static Date parser(String raw) {
      Date date = null;

      if (StringUtils.isNotBlank(raw)) {
         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
         try {
            date = format.parse(raw);
         } catch (ParseException e) {
            log.error(e);
         }
      }
      return date;
   }

   /**
     *   parser  date object to string ojbect("yyyy-MM-dd"),
     *
     *   @param date Date ojbect
     *   @return a String object 
     *
     */
   public static String parser(Date date) {

      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      return format.format(date);
   }

   /**
     *   calculate  date ,
     *
     *   @param date Date ojbect
     *   @param value  the value used to operate at
     *   @param mode   m = month;w=week of year;d=date;
     *   @return a Date object 
     *
     */
   public static Date calculateDateByDuration(Date date, int value, String mode) {
      Calendar calender = new GregorianCalendar();
      calender.setTime(date);

      if ("m".equals(mode)) {
         calender.add(Calendar.MONTH, value);
      }

      if ("w".equals(mode)) {
         calender.add(Calendar.WEEK_OF_YEAR, value);
      }

      if ("d".equals(mode)) {
         calender.add(Calendar.DATE, value);
      }

      return calender.getTime();
   }
   /**
     *
     *   get current datetime by millins type
     *
     */
   public static Date getCurrent() {
      return new Date(System.currentTimeMillis());
   }
}
