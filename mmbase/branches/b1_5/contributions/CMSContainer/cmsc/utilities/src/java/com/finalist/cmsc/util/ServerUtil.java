/*
 * Created on Nov 14, 2003 by edwin
 *
 */
package com.finalist.cmsc.util;

import javax.naming.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Utility methods to check whether we are running in staging, live or single.
 *
 * @author Edwin van der Elst, Finalist IT-Group - Date :Nov 14, 2003
 */
public final class ServerUtil {

   /** MMBase logging system */
   private static final Logger log = Logging.getLoggerInstance(ServerUtil.class.getName());

   private static boolean live;
   private static boolean single;
   private static boolean useSvrName = false;
   private static boolean production = false;
   private static boolean readonly = false;

   private static boolean loaded;


   private ServerUtil() {
      // Utility
   }

   public static boolean isSingle(){
      getSetting();
      return single;
   }

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
      return useSvrName;
   }


   public static boolean isReadonly() {
      getSetting();
      return readonly;
   }

   private static void getSetting() {
      if (!loaded) {
         live = false;
         try {
            InitialContext context = new InitialContext();
            Context env = (Context) context.lookup("java:comp/env");
            String liveOrStaging = (String) env.lookup("server/LiveOrStaging");
            live = "live".equals(liveOrStaging);
            single = "single".equals(liveOrStaging);
            String useServerNameStr = (String) env.lookup("server/useServerName");
            if (useServerNameStr != null && useServerNameStr.length() > 0) {
               useSvrName = Boolean.valueOf(useServerNameStr);
            }

            String productionStr = (String) env.lookup("server/production");
            if (productionStr != null && productionStr.length() > 0) {
               production = Boolean.valueOf(productionStr);
            }
            
            String readonlyStr = (String) env.lookup("server/readonly");
            if (readonlyStr != null && readonlyStr.length() > 0) {
               readonly = Boolean.valueOf(readonlyStr);
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
