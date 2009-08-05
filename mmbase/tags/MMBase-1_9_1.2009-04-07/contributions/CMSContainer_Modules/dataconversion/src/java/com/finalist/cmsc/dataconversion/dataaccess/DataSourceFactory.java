package com.finalist.cmsc.dataconversion.dataaccess;

import java.util.Properties;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.dataconversion.service.DateUtil;

/**
 * Factory method type
 * @author Neil_Gong
 *
 */
public class DataSourceFactory {
   
   private static final Logger log = Logging.getLoggerInstance(DataSourceFactory.class.getName());

   public static  DataSource getDataSource(Properties properties) {
     
      String dbInfo = " [datetime=%s]----->database info :[driverClassName=%s] [url=%s] [username=%s] [node id=%s]";
      log.info(String.format(dbInfo,DateUtil.getDateTime(), properties.get("driverClassName"),properties.get("url"),properties.get("username"),properties.get("node")));
      BasicDataSource ds = null;
      try {
         ds= (BasicDataSource) BasicDataSourceFactory.createDataSource(properties);
      } 
      catch (Exception e) {
         log.error("create  DataSource Error!->"+e.getMessage());
      }
      return ds;
   }
}
