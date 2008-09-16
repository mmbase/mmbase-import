package com.finalist.mmbase.util;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * It would have been nice to have these methods into the CloudFactory,
 * but apparently there are multiple CloudFactory classes in our project
 * or libraries. It sometimes fails to compile.
 * 
 * @author Nico Klasens (Finalist IT Group)
 * @created 24-nov-2003
 * @version $Revision: 1.1 $
 */
public class CloudFactory2 {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(CloudFactory2.class.getName());
   
   /**
    * @param request httprequest
    * Returns the admin instance of the Cloud
    * @return The admin Cloud
    */
   public static Cloud getCloud(HttpServletRequest request) {
      String sessionname = "cloud_mmbase";
      
      HttpSession session = request.getSession(false);
      if (session != null) {
         Cloud cloud = (Cloud) session.getAttribute(sessionname);
         if (cloud != null) {
            log.debug("cloud for user " + cloud.getUser().getIdentifier());
            return cloud;
         }
      }
      throw new RuntimeException("No cloud instance in user session.");
   }
}
