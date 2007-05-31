package org.mmbase.applications.wordfilter;

import junit.framework.TestCase;

import java.util.regex.Pattern;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class WordHtmlCleanerTest extends TestCase {

   /**
    * Test for jira issue NIJ-385. Bold tags with only spaces in it where reduced to a self closing tag, which will
    * render al following text bold in IE.<br/>
    * E.g. The following html <code>&lt;b&gt; &lt;/b&gt;</code> would be cleaned as <code>&lt;b/&gt;</code>. 
    */
   public void testEmptyTagsCleaner() {
       String html = "<b> </b><i> </i><u> </u> <B> </B> <U> </U> <I> </I> ";

       String cleanedHtml = WordHtmlCleaner.cleanHtml(html);
       Pattern p = Pattern.compile("<[bBiIuU]\\s*/>");
       assertFalse("Cleaned html still has some empty tags left!" + cleanedHtml, p.matcher(cleanedHtml).find());
   }
   
   /**
    * Tests for jira issue CMSC-421, test a bunch of cleanups for the whitespace.
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
   
   private void doTestFilter(String input, String expected) {
      String cleanedHtml = WordHtmlCleaner.cleanHtml(input);
      assertEquals(expected, cleanedHtml);
   }
}
