package com.finalist.newsletter.util;

import junit.framework.TestCase;

import java.util.Date;

public class DateUtilTest extends TestCase {
   public void testCalculateDateByDuration(){
      Date stardDate = DateUtil.parser("2008-05-01");

      Date date = DateUtil.calculateDateByDuration(stardDate,1,"d");
      assertEquals("2008-05-02",DateUtil.parser(date));

      date = DateUtil.calculateDateByDuration(stardDate,8,"m");
      assertEquals("2009-01-01",DateUtil.parser(date));
   }
   
}
