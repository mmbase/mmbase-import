package com.finalist.cmsc.dataconversion.service;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

public class DateUtil {

   public static String getDateTime() {
      return getDataTime("yyyy-MM-dd HH:mm:ss");
   }
   
   public static String getDataTime(String pattern) {
      Date date = new Date();
      return getDataTime(date,pattern);
   } 
   
   public static String getDataTime(Date date,String pattern) {
     return  DateFormatUtils.format(date, pattern);
   }
   
   public static String getDataTime(long millonTime,String pattern) {
      return  DateFormatUtils.format(millonTime, pattern);
    }
   public static void main(String[] args) {
      System.out.println("############--"+getDateTime());
   }
}
