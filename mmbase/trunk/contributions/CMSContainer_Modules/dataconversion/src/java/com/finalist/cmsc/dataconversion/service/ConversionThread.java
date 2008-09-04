package com.finalist.cmsc.dataconversion.service;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.struts.action.ActionForm;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class ConversionThread  extends Thread{
   
   private static final Logger log = Logging.getLoggerInstance(ConversionThread.class.getName());
   
   Properties properties;
   
   public ConversionThread(Properties s, ServletContext context) {
       super(new Job(s, context), "Data Conversion Start Thread");
       setDaemon(true); // if init never ends, don't hinder destroy
   }
   
   public static class Job implements Runnable {
      private final Properties form;
      private ServletContext context;
      public Job(Properties s, ServletContext context) {
          form = s;
          this.context = context;
      }

      public void run() {

          synchronized(Job.class) {             
             Conversion conversion = new Conversion(form, context);
             conversion.converseAll();
          }
      }
  }
}
