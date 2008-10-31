package com.finalist.cmsc.debug;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class RequestCountThread extends Thread {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(RequestCountThread.class.getName());

   private int requestCount = 0;
   private long lastCheck = 0;
   private String name;


   public RequestCountThread(String name) {
      super("RequestCountThread " + name);
      this.name = name;
      setDaemon(true);
      start();
   }


   public void count() {
      lastCheck = System.currentTimeMillis();
      requestCount++;
   }


   @Override
   public void run() {
      while (true) {
         long now = System.currentTimeMillis();
         if (requestCount > 0 && now - (lastCheck + 10000) > 0) {
            log.warn("===== " + name + " count: " + requestCount);
            requestCount = 0;
         }
         try {
            Thread.sleep(5000);
         }
         catch (InterruptedException e) {
            log.error(Logging.stackTrace(e));
         }
      }
   }

}
