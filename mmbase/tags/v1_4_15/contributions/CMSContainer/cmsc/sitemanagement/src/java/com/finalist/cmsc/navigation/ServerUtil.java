/*
 * Created on Nov 14, 2003 by edwin
 *
 */
package com.finalist.cmsc.navigation;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Utility methods to check wether we are running in staging or live.
 * 
 * @author Edwin van der Elst, Finalist IT-Group - Date :Nov 14, 2003
 */
public class ServerUtil {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(ServerUtil.class.getName());

   private static boolean live;
   private static boolean useServerName = false;
   private static boolean production = false;

   private static boolean loaded;


   public static boolean isLive() {
      getSetting();
      return live;
   }


   public static boolean isStaging() {
      getSetting();
      return !live;
   }


   public static boolean isProduction() {
      getSetting();
      return production;
   }


   public static boolean useServerName() {
      getSetting();
      return useServerName;
   }


   private static void getSetting() {
      if (!loaded) {
         live = false;
         try {
            InitialContext context = new InitialContext();
            Context env = (Context) context.lookup("java:comp/env");
            String liveOrStaging = (String) env.lookup("server/LiveOrStaging");
            live = "live".equals(liveOrStaging);

            String useServerNameStr = (String) env.lookup("server/useServerName");
            if (useServerNameStr != null && useServerNameStr.length() > 0) {
               useServerName = Boolean.valueOf(useServerNameStr);
            }

            String productionStr = (String) env.lookup("server/production");
            if (productionStr != null && productionStr.length() > 0) {
               production = Boolean.valueOf(productionStr);
            }
         }
         catch (NamingException ne) {
            log.debug("Error looking up server/LiveOrStaging", ne);
         }
         loaded = true;
      }
   }


   /*
    * Returns the value of the context setting given as a string.
    */
   public static String getEnvironmentVariableValue(String contextSetting) {
      String result = "";
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         result = (String) env.lookup(contextSetting);
      }
      catch (NamingException ne) {
         log.debug("Error looking up context setting '" + contextSetting + "'", ne);
      }

      return result;
   }
}
