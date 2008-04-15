package com.finalist.newsletter.cao.impl;

import com.finalist.newsletter.BaseNewsletterTest;
import com.finalist.newsletter.cao.NewsLetterStatisticCAO;

import java.util.List;

public class NewsLetterStatisticCAOImplTest extends BaseNewsletterTest {

   NewsLetterStatisticCAO cao;

   protected void setUp() throws Exception {
      super.setUp();
      cao = new NewsLetterStatisticCAOImpl(cloud);
      clearAllNode("newsletterdailylog");
   }

   public void testLogPubliction() {
      cao.logPubliction(123,23);
      cao.logPubliction(323,33);

      List list = cao.getAllRecords();

      assertEquals(2,list.size());
   }
}
