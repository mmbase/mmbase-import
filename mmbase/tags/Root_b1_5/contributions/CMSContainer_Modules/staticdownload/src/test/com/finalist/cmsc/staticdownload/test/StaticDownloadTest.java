package com.finalist.cmsc.staticdownload.test;

import com.finalist.cmsc.staticdownload.DownloadThread;

import junit.framework.TestCase;

public class StaticDownloadTest extends TestCase {

   
	   public void testReplaceFlash1() {
		      String input = "<object width=\"550\" height=\"400\"><param name=\"movie\" value=\"http://www.google.com/somefilename.swf\"/><embed src=\"../somefilename.swf\" width=\"550\" height=\"400\"></embed></object>";
		      String result = "<object width=\"550\" height=\"400\"><param name=\"movie\" value=\"../somefilename.swf\"/><embed src=\"../somefilename.swf\" width=\"550\" height=\"400\"></embed></object>";
		       assertEquals(DownloadThread.fixFlash(input), result);
		       
		   }
	   public void testReplaceFlash2() {
		      String input = "<object width=\"550\" height=\"400\"><param name=\"movie\" value=\"../blah/arg/somefilename.swf\"/><embed src=\"../somefilename.swf\" width=\"550\" height=\"400\"></embed></object>";
		      String result = "<object width=\"550\" height=\"400\"><param name=\"movie\" value=\"../somefilename.swf\"/><embed src=\"../somefilename.swf\" width=\"550\" height=\"400\"></embed></object>";
		       assertEquals(DownloadThread.fixFlash(input), result);
		       
		   }
   public void testReplaceFlash3() {
      String input = "<object width=\"550\" height=\"400\"><embed src=\"../somefilename.swf\" width=\"550\" height=\"400\"></embed><param name=\"movie\" value=\"http://www.google.com/somefilename.swf\"/></object>";
      String result = "<object width=\"550\" height=\"400\"><embed src=\"../somefilename.swf\" width=\"550\" height=\"400\"></embed><param name=\"movie\" value=\"../somefilename.swf\"/></object>";
       assertEquals(DownloadThread.fixFlash(input), result);
       
   }

   public void testReplaceFlash4() {
      String input = "<object width=\"550\" height=\"400\"><embed src=\"../somefilename.swf\" width=\"550\" height=\"400\"></embed><param name=\"movie\" value=\"http://www.google.com/somefilename.swf\"/></object><object width=\"550\" height=\"400\"><param name=\"movie\" value=\"http://www.google.com/somefilename.swf\"/><embed src=\"../somefilename.swf\" width=\"550\" height=\"400\"></embed></object>";
      String result = "<object width=\"550\" height=\"400\"><embed src=\"../somefilename.swf\" width=\"550\" height=\"400\"></embed><param name=\"movie\" value=\"../somefilename.swf\"/></object><object width=\"550\" height=\"400\"><param name=\"movie\" value=\"../somefilename.swf\"/><embed src=\"../somefilename.swf\" width=\"550\" height=\"400\"></embed></object>";
       assertEquals(DownloadThread.fixFlash(input), result);
       
   }

   
}
