package com.finalist.cmsc.dataconversion.service;

import java.util.Properties;

import org.apache.struts.action.ActionForm;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class ConversionThread  extends Thread{
   
   private static Logger log = Logging.getLoggerInstance(ConversionThread.class.getName());
   
   Properties properties;
   
   public ConversionThread(Properties s) {
       super(new Job(s), "Data Conversion Start Thread");
       setDaemon(true); // if init never ends, don't hinder destroy
   }
   
   public static class Job implements Runnable {
      private final Properties form;
      public Job(Properties s) {
          form = s;
      }

      public void run() {

          synchronized(Job.class) {             
             Conversion conversion = new Conversion(form);
             conversion.converseAll();
          }
      }
  }
}
