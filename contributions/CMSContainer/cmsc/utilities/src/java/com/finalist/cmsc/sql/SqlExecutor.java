package com.finalist.cmsc.sql;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.CloudContext;
import org.mmbase.bridge.ContextProvider;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Nico Klasens
 */
public class SqlExecutor {

   /** MMBase logging system */
   private static Logger log = Logging.getLoggerInstance(SqlExecutor.class.getName());
   private Connection connection;

   public String execute(SqlAction sqlAction) {
      String result = "";

      Cloud cloud = sqlAction.getCloud();
      if (cloud == null) {
         sqlAction.setCloud(getCloud());
      }
      MMBase mmb = MMBase.getMMBase();
      sqlAction.setMmb(mmb);
      
      String sql = sqlAction.getSql();
      
      Connection con = null;
      PreparedStatement stmt = null;
      ResultSet rs = null;
      try {
         con = getConnection();
         stmt = con.prepareStatement(sql);
         rs = stmt.executeQuery();
         
         result = sqlAction.process(rs);
      } catch (Exception e) {
       log.warn("Database connection failed " + Logging.stackTrace(e));
      } finally {
         try {
            if (rs != null) {
               rs.close();
            }
         } catch (Exception e) {
          log.debug("Statement close failed");
         }
         try {
            if (stmt != null) {
               stmt.close();
            }
         } catch (Exception e) {
          log.debug("Statement close failed");
         }
         try {
            if (con != null) {
               con.close();
            }
         } catch (Exception e) {
          log.debug("Connection close failed");
         }
      }
      return result + "<br /> Done"; 
   }

   private Connection getConnection() {
        if (connection == null) {
            throw new NullPointerException("connection not set. Write code to get it from MMBase datasource!");
        }
        return connection;
   }

   public void setConnection(Connection connection) {
       this.connection = connection;
   }

   public static final String DEFAULT_SESSIONNAME = "cloud_mmbase";
   public static final String DEFAULT_CLOUD_NAME = "mmbase";
   public static final String DEFAULT_AUTHENTICATION = "name/password";
   
   private static Cloud getCloud() {
      final CloudContext context = ContextProvider.getCloudContext("local");
      final Map loginInfo = getUserCredentials("admin", "admin2k");
      return context.getCloud(DEFAULT_CLOUD_NAME, DEFAULT_AUTHENTICATION, loginInfo);
   }
   
   public static Map getUserCredentials(String username, String password) {
      Map<String,String> result = new HashMap<String,String>(3, 0.7f);
      result.put("username", username);
      result.put("password", password);
      return result;
   }

}
