package com.finalist.cmsc.staticdownload.test;

import com.finalist.cmsc.staticdownload.DownloadSettings;
import com.finalist.cmsc.staticdownload.DownloadThread;

import junit.framework.TestCase;

public class StaticDownloadTest extends TestCase {

   public void testDownload()  {
      DownloadSettings downloadSettings = new DownloadSettings(2, "e:\\temp\\wgetoutput\\dl", "e:\\temp\\wgetoutput", "E:\\temp\\wget\\wget.exe", "blaat", null);
      
//      DownloadThread downloadThread = new DownloadThread("http://www.cmscontainer.org", downloadSettings);
//      DownloadThread downloadThread = new DownloadThread("http://nijmegen-demo.finalist.com/nijmegen-live/www.nijmegen.nl", downloadSettings);
      DownloadThread downloadThread = new DownloadThread("http://nai.finalist.com/nai-live/www.nai.nl", downloadSettings);
      downloadThread.start();
      
      while(downloadThread.isDownloading()) {
         try {
            Thread.sleep(100);
         } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      System.out.println((downloadThread.getEndTime()-downloadThread.getStartTime())/1000+" seconds needed");
   }
}
