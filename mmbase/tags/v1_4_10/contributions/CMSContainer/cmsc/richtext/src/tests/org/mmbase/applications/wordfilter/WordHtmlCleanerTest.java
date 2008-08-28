package org.mmbase.applications.wordfilter;

import junit.framework.TestCase;

import java.util.regex.Pattern;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class WordHtmlCleanerTest extends TestCase {

   /**
    * Test for NIJ-385. Bold tags with only spaces in it where reduced to a self closing tag, which will
    * render al following text bold in IE.<br/>
    * E.g. The following html <code>&lt;b&gt; &lt;/b&gt;</code> would be cleaned as <code>&lt;b/&gt;</code>. 
    */
   public void testEmptyTagsCleaner() {
       String html = "<b> </b><i> </i><u> </u> <B> </B> <U> </U> <I> </I> ";

       String cleanedHtml = WordHtmlCleaner.cleanHtml(html, true);
       Pattern p = Pattern.compile("<[bBiIuU]\\s*/>");
       assertFalse("Cleaned html still has some empty tags left!" + cleanedHtml, p.matcher(cleanedHtml).find());
   }
   
   /**
    * Tests for CMSC-421, test a bunch of cleanups for the whitespace.
    */
   public void testRemoveWhiteSpace() {
       doTestFilter("<p>test</p><p>test</p>", "test<br/><br/>test");
       doTestFilter("test<br/><br/><br/><br/>test", "test<br/><br/><br/><br/>test");
       doTestFilter("<p>test</p><p>x</p><p>test</p>", "test<br/><br/>x<br/><br/>test");
       doTestFilter("<p>test</p><p></p><p>test</p>", "test<br/><br/>test");
       doTestFilter("<p>test</p><p><p>test</p>", "test<br/><br/>test");
       doTestFilter("<p>test</p><p>test", "test<br/><br/>test");
       doTestFilter("<p>test</p><p>test&nbsp;</p>", "test<br/><br/>test");
       doTestFilter(
           "  <p><font face=\"Times New Roman\" size=\"3\">Heading</font></p>" + 
           "  <p> </p>" + 
           "  <p><font face=\"Times New Roman\" size=\"3\">Paragraph one</font></p>" + 
           "  <p> </p>" + 
           "  <p><font face=\"Times New Roman\" size=\"3\">Paragraph two</font></p>" + 
           "  <p> </p>" + 
           "  <p> </p>" + 
           "  <p> </p>",
           "Heading<br/><br/>Paragraph one<br/><br/>Paragraph two");
       doTestFilter(
           "  <p><font face=\"Times New Roman\" size=\"3\">Heading</font></p>" + 
           "  <p>&nbsp;</p>" + 
           "  <p><font face=\"Times New Roman\" size=\"3\">Paragraph one</font></p>" + 
           "  <p>&nbsp;</p>" + 
           "  <p><font face=\"Times New Roman\" size=\"3\">Paragraph two</font></p>" + 
           "  <p>&nbsp;</p>" + 
           "  <p>&nbsp;</p>",
           "Heading<br/><br/><br/><br/>Paragraph one<br/><br/><br/><br/>Paragraph two");
   }
   
   
   /**
    * CMSC-417: FWP, fixed the problem with the 'ugly' lists sometimes pasted from word,
    * these lists are created by adding spaces and tabs before and behind the dots of the lists.
    */
   public void testFixLists() {
      String input = "<p style=\"margin-left: 53.4pt; text-indent: -18pt;\" class=\"MsoNormal\"><!--[if !supportLists]--><span style=\"font-family: Wingdings;\">§<span style=\"font-family: &quot;Times New Roman&quot;; font-style: normal; font-variant: normal; font-weight: normal; font-size: 7pt; line-height: normal; font-size-adjust: none; font-stretch: normal;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n\r"+
                     "</span></span><!--[endif]--><span>&nbsp;</span><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>Een</p>"+
                     "<p style=\"margin-left: 53.4pt; text-indent: -18pt;\" class=\"MsoNormal\"><!--[if !supportLists]--><span style=\"font-family: Wingdings;\">§<span style=\"font-family: &quot;Times New Roman&quot;; font-style: normal; font-variant: normal; font-weight: normal; font-size: 7pt; line-height: normal; font-size-adjust: none; font-stretch: normal;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
                     "</span></span><!--[endif]--><span>&nbsp;</span><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>Twee</p>";
      doTestFilter(input, "<ul><li>Een</li><li>Twee</li></ul>");

      String input2 = "<p style=\"margin-left: 89.4pt; text-indent: -18pt;\" class=\"MsoNormal\"><!--[if !supportLists]--><span style=\"font-family: Symbol;\">·<span style=\"font-family: &quot;Times New Roman&quot;; font-style: normal; font-variant: normal; font-weight: normal; font-size: 7pt; line-height: normal; font-size-adjust: none; font-stretch: normal;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
                     "</span></span><!--[endif]-->Een</p>"+
                     "<p style=\"margin-left: 89.4pt; text-indent: -18pt;\" class=\"MsoNormal\"><!--[if !supportLists]--><span style=\"font-family: Symbol;\">·<span style=\"font-family: &quot;Times New Roman&quot;; font-style: normal; font-variant: normal; font-weight: normal; font-size: 7pt; line-height: normal; font-size-adjust: none; font-stretch: normal;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
                     "</span></span><!--[endif]-->Twee</p>";
      doTestFilter(input2, "<ul><li>Een</li><li>Twee</li></ul>");

      String input3 = "<ol><li>Een</li><li>Twee</li></ol>";
      doTestFilter(input3, "<ol><li>Een</li><li>Twee</li></ol>");
   }
   
   /**
    * CMSC-416: FP: Problems with linebreaks in hidden if blocks
    */
   public void testLinebreaksInHtmlIfComments() {
      doTestFilter("te<!--[if !supportLineBreaknewLine]-->x<!--[endif]-->st", "test");
      doTestFilter("te<!--[if !supportLineBreaknewLine]-->\r\n<!--[endif]-->st", "test");
   }
   
    /**
     * As specified in NIJ-780, only a single <br/> should be the result of a replaced <p></p>
     */
    public void testReplaceParagraphSingleBr() {
    	doTestFilter("<p>This is paragraph</p><p>paragraph 2</p>", "This is paragraph<br/><br/>paragraph 2");
    }
    
	/**
     * As specified in NIJ-780, h1 till h7 should be replaced by a <strong>...</strong><br/>
     */
    public void testReplaceHeaderEnd() {
    	for(int count = 1; count <= 7; count++) {
    		doTestFilter("<h"+count+">Header</h"+count+">", "<strong>Header</strong>");
            doTestFilter("<h"+count+">Header</h"+count+"><p>paragraph 2</p>", "<strong>Header</strong><br/>paragraph 2");
    	}
    }

    public void testYoutube() {
    	String code = "<object width=\"425\" height=\"355\"><param name=\"movie\" value=\"http://www.youtube.com/v/MI4TO5vhzRM&hl=en\"></param><param name=\"wmode\" value=\"transparent\"></param><embed src=\"http://www.youtube.com/v/MI4TO5vhzRM&hl=en\" type=\"application/x-shockwave-flash\" wmode=\"transparent\" width=\"425\" height=\"355\"></embed></object>";
    	String expected = "<object height=\"355\" width=\"425\"><param name=\"movie\" value=\"http://www.youtube.com/v/MI4TO5vhzRM&amp;hl=en\"/><param name=\"wmode\" value=\"transparent\"/><embed height=\"355\" src=\"http://www.youtube.com/v/MI4TO5vhzRM&amp;hl=en\" type=\"application/x-shockwave-flash\" width=\"425\" wmode=\"transparent\"/></object>";
    	doTestFilter(code, expected);
    }

    private void doTestFilter(String input, String expected) {
        String cleanedHtml = WordHtmlCleaner.cleanHtml(input, true);
        assertEquals(expected, cleanedHtml);
    }
    
}
