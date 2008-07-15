package com.finalist.newsletter.util;

import junit.framework.TestCase;

public class NewsletterPublicationUtilTest extends TestCase {

   public void testGetBounces(){
      String server = "124.42.2.213";
      String user = "gmark";
      String password = "111111";
      NewsletterPublicationUtil.getBounces(server,"pop3",user,password);
      assertTrue(true);
   }
}
