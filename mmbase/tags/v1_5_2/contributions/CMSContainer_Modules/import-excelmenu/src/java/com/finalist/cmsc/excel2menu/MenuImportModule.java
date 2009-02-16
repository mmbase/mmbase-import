package com.finalist.cmsc.excel2menu;

import java.util.Map;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.Module;
import org.mmbase.module.tools.MMAdmin;
import org.mmbase.util.ResourceLoader;

/**
 * Module to convert excel menu information to CMSC specific data structure
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.3 $
 */
public class MenuImportModule extends Module {
   static Log log = LogFactory.getLog(MenuImportModule.class);

   /**
    * Time to wait between tries.
    */
   private static final long INTERVAL_TRIES = 1000L;

   /** The mmadmin instance */
   MMAdmin mmadmin = null;


   public void onload() {
      // nothing
   }


   public void init() {
      mmadmin = (MMAdmin) Module.getModule("mmadmin", true);
      Thread runOnce = new Thread(new ConversionTask());
      runOnce.setDaemon(true);
      runOnce.start();
   }

   private class ConversionTask implements Runnable {
      /**
       * The thread in which the external links will be checked
       */
      public void run() {
         try {
            while (!mmadmin.getState()) {
               // not started, sleep some time
               Thread.sleep(INTERVAL_TRIES);
            }
         }
         catch (InterruptedException e) {
            log.debug(e.getMessage(), e);
         }
         startConversion();
      }
   }


   /**
    * Start indexing the cloud after MMBase has really started.
    */
   void startConversion() {
      log.info("MenuImportModule starting Excel conversion");

      String inputfile = getInitParameter("inputfile");

      Map<String, String> params = getInitParameters();
      ExcelConfig config = new ExcelConfig(params);

      Excel2Menu t = new Excel2Menu(getAdminCloud(), config);
      t.convert(ResourceLoader.getConfigurationRoot().getResourceAsStream(inputfile));
   }


   private Cloud getAdminCloud() {
      CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = cloudProvider.getAdminCloud();
      return cloud;
   }

}
