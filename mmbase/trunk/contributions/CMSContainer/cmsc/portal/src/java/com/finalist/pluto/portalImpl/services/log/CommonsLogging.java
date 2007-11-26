package com.finalist.pluto.portalImpl.services.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.services.log.LogService;
import org.apache.pluto.services.log.Logger;

/**
 * Direct Commons logging for Pluto
 */
public class CommonsLogging implements LogService {
   private static Log log;


   public Logger getLogger(Log logger) {
      log = logger;
      return new LoggerImpl(log);
   }


   public Logger getLogger(String component) {
      log = LogFactory.getLog(component);
      return new LoggerImpl(log);
   }


   public Logger getLogger(Class clazz) {
      log = LogFactory.getLog(clazz);
      return new LoggerImpl(log);
   }
}
