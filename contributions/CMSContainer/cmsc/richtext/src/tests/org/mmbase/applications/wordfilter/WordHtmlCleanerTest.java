package org.mmbase.applications.wordfilter;

import junit.framework.TestCase;

import java.util.regex.Pattern;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class WordHtmlCleanerTest extends TestCase {

   /**
    * Test for jira issue NIJ-385. Bold tags with only spaces in it where
    * reduced to a self closing tag, which will render al following text bold in
    * IE.<br/> E.g. The following html <code>&lt;b&gt; &lt;/b&gt;</code>
    * would be cleaned as <code>&lt;b/&gt;</code>.
    */
   public void testEmptyTagsCleaner() {
      String html = "<b> </b><i> </i><u> </u> <B> </B> <U> </U> <I> </I> ";

      String cleanedHtml = WordHtmlCleaner.cleanHtml(html, true);
      Pattern p = Pattern.compile("<[bBiIuU]\\s*/>");
      assertFalse("Cleaned html still has some empty tags left!" + cleanedHtml, p.matcher(cleanedHtml).find());
   }


   /**
    * Tests for jira issue CMSC-421, test a bunch of cleanups for the
    * whitespace.
    */
   public void testRemoveWhiteSpace() {
      doTestFilter("<p>test</p><p>test</p>", "test<br/><br/>test");
   }


   public void testRemoveWhiteSpace2() {
      doTestFilter("test<br/><br/><br/><br/>test", "test<br/><br/><br/><br/>test");
   }


   public void testRemoveWhiteSpace3() {
      doTestFilter("<p>test</p><p>x</p><p>test</p>", "test<br/><br/>x<br/><br/>test");
   }


   public void testRemoveWhiteSpace4() {
      doTestFilter("<p>test</p><p></p><p>test</p>", "test<br/><br/><br/><br/>test");
   }


   public void testRemoveWhiteSpace5() {
      doTestFilter("<p>test</p><p><p>test</p>", "test<br/><br/>test");
   }


   public void testRemoveWhiteSpace6() {
      doTestFilter("<p>test</p><p>test", "test<br/><br/>test");
   }


   /**
    * CMSC-417: FWP, fixed the problem with the 'ugly' lists sometimes pasted
    * from word, these lists are created by adding spaces and tabs before and
    * behind the dots of the lists.
    */
   public void testFixLists() {
      String input = "<p style=\"margin-left: 53.4pt; text-indent: -18pt;\" class=\"MsoNormal\"><!--[if !supportLists]--><span style=\"font-family: Wingdings;\">§<span style=\"font-family: &quot;Times New Roman&quot;; font-style: normal; font-variant: normal; font-weight: normal; font-size: 7pt; line-height: normal; font-size-adjust: none; font-stretch: normal;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n\r"
            + "</span></span><!--[endif]--><span>&nbsp;</span><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>Een</p>"
            + "<p style=\"margin-left: 53.4pt; text-indent: -18pt;\" class=\"MsoNormal\"><!--[if !supportLists]--><span style=\"font-family: Wingdings;\">§<span style=\"font-family: &quot;Times New Roman&quot;; font-style: normal; font-variant: normal; font-weight: normal; font-size: 7pt; line-height: normal; font-size-adjust: none; font-stretch: normal;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + "</span></span><!--[endif]--><span>&nbsp;</span><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>Twee</p>";
      doTestFilter(input, "<ul><li>Een</li><li>Twee</li></ul>");
   }


   public void testFixLists2() {
      String input = "<p style=\"margin-left: 89.4pt; text-indent: -18pt;\" class=\"MsoNormal\"><!--[if !supportLists]--><span style=\"font-family: Symbol;\">·<span style=\"font-family: &quot;Times New Roman&quot;; font-style: normal; font-variant: normal; font-weight: normal; font-size: 7pt; line-height: normal; font-size-adjust: none; font-stretch: normal;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + "</span></span><!--[endif]-->Een</p>"
            + "<p style=\"margin-left: 89.4pt; text-indent: -18pt;\" class=\"MsoNormal\"><!--[if !supportLists]--><span style=\"font-family: Symbol;\">·<span style=\"font-family: &quot;Times New Roman&quot;; font-style: normal; font-variant: normal; font-weight: normal; font-size: 7pt; line-height: normal; font-size-adjust: none; font-stretch: normal;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + "</span></span><!--[endif]-->Twee</p>";
      doTestFilter(input, "<ul><li>Een</li><li>Twee</li></ul>");
   }


   public void testFixLists3() {
      String input = "<ol><li>Een</li><li>Twee</li></ol>";
      doTestFilter(input, "<ol><li>Een</li><li>Twee</li></ol>");
   }


   /**
    * CMSC-416: FP: Problems with linebreaks in hidden if blocks
    */
   public void testLinebreaksInHtmlIfComments() {
      doTestFilter("te<!--[if !supportLineBreaknewLine]-->x<!--[endif]-->st", "test");
   }


   public void testLinebreaksInHtmlIfComments2() {
      doTestFilter("te<!--[if !supportLineBreaknewLine]-->\r\n<!--[endif]-->st", "test");
   }


   private void doTestFilter(String input, String expected) {
      String cleanedHtml = WordHtmlCleaner.cleanHtml(input, true);
      assertEquals(expected, cleanedHtml);
   }
}
