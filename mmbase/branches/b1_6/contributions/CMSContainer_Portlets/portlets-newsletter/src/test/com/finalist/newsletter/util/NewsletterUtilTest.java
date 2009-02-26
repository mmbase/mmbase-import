package com.finalist.newsletter.util;

import junit.framework.TestCase;

public class NewsletterUtilTest extends TestCase {


   public void testCalibrateRelativeURL() {
      String liveURL = "http://localhost/cmsc-community/";

      String link = "<link href=\"/cmsc-community/editors/css/main.css\" type=\"text/css\" rel=\"stylesheet\" />";
      String calibratedLink = "<link href=\"http://localhost/cmsc-community/editors/css/main.css\" type=\"text/css\" rel=\"stylesheet\" />";
      verifyLink(liveURL, link, calibratedLink);

      String script = "<script src=\"/cmsc-community/js/window.js\" type=\"text/javascript\"></script>";
      String calibratedScript = "<script src=\"http://localhost/cmsc-community/js/window.js\" type=\"text/javascript\"></script>";
      verifyLink(liveURL,script,calibratedScript);

      String href = "<a href=\"/cmsc-community/afsd/_ac_submenu/AC/_md_submenu/delete\" title=\"Delete\" class=\"portlet-mode-type-admin\">";
      String calibratedHref = "<a href=\"http://localhost/cmsc-community/afsd/_ac_submenu/AC/_md_submenu/delete\" title=\"Delete\" class=\"portlet-mode-type-admin\">";
      verifyLink(liveURL,href,calibratedHref);

      String img = "<img alt=\"\" src=\"/cmsc-community/gfx/top.jpg\"/>";
      String calibratedImg = "<img alt=\"\" src=\"http://localhost/cmsc-community/gfx/top.jpg\"/>";
      verifyLink(liveURL,img,calibratedImg);
   }

   public void testCalibrateRelativeURLWithServername() {

      String liveURL = "http://www.finalist.com/";

      String link = "<link rel=\"stylesheet\" type=\"text/css\" href=\"/fwcmsc/css/main.css\" media=\"all\"/>";
      String calibratedLink = "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.finalist.com/fwcmsc/css/main.css\" media=\"all\"/>";
      verifyLink(liveURL, link, calibratedLink);

      String script = "<script type=\"text/javascript\" src=\"/fwcmsc/js/carroussel.js\"></script>";
      String calibratedScript = "<script type=\"text/javascript\" src=\"http://www.finalist.com/fwcmsc/js/carroussel.js\"></script>";
      verifyLink(liveURL,script,calibratedScript);

      String href = "<a href=\"/content/13970/scrum_training_class_door_jutta_eckstein\" class=\"read-on\">";
      String calibratedHref = "<a href=\"http://www.finalist.com/content/13970/scrum_training_class_door_jutta_eckstein\" class=\"read-on\">";
      verifyLink(liveURL,href,calibratedHref);

      String img = "<img src=\"/fwcmsc/images/logo_finalist.gif\" style=\"border:none\"/>";
      String calibratedImg = "<img src=\"http://www.finalist.com/fwcmsc/images/logo_finalist.gif\" style=\"border:none\"/>";
      verifyLink(liveURL,img,calibratedImg);
   }

   private void verifyLink(String liveURL, String link, String calibratedLink) {
      String linkResult = NewsletterUtil.calibrateRelativeURL(link, liveURL);

      assertEquals(calibratedLink, linkResult);
   }
}
