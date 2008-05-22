/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.*;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * Utility methods to check wether we are running in staging or live.
 *  
 * @author Edwin van der Elst Date :Nov 14, 2003
 *   
 */
public class ServerUtil {
   /** the date + time long format */
   private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

   private static final Logger log = Logging.getLoggerInstance(ServerUtil.class);

   public static boolean isLive() {
      getSetting();
      return live;
   }

   public static boolean isStaging() {
      getSetting();
      return !live;
   }

   public static String getWebserviceUrl() {
      String webserviceUrl = PropertiesUtil.getProperty("ggg.webservice.url");
      return (webserviceUrl != null) ? webserviceUrl : "";
   }
   
   private static boolean live;
   
   private static boolean loaded = false;
   private static void getSetting() {
      if (!loaded) {
         try {
            InitialContext context = new InitialContext();
            Context env = (Context) context.lookup("java:comp/env");
            String liveOrStaging = (String) env.lookup("leocms/LiveOrStaging");
            live = "live".equals(liveOrStaging);
            loaded=true;
         } catch (NamingException ne) {
            log.error("Error looking up leocms/LiveOrStaging" + ne);
         }
      }
   }

   /**
    * Returns String from given long according to dd-MM-yyyy HH:mm:ss
    * @param date the date to format
    * @return Datestring
    */
   public static String getDateTimeString(long date) {
      return DATE_TIME_FORMAT.format(new Date(date));
   }

   /**
    * Returns String from present time according to dd-MM-yyyy HH:mm:ss
    * @return Datestring
    */
   public static String getDateTimeString() {
      return DATE_TIME_FORMAT.format(new Date());
   }
   
   /**
    * Creates returns jvm size
    * @return Sizestring
    */
   public String jvmSize() {
     Runtime rt = Runtime.getRuntime();
     return " (total " + rt.totalMemory() / (1024 * 1024) + " Mbyte / free " + rt.freeMemory() / (1024 * 1024) + " Mbyte)";
   }
}
