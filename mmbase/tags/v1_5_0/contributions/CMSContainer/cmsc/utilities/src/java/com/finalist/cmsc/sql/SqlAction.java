package com.finalist.cmsc.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mmbase.bridge.BridgeException;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.MMBase;

/**
 * TODO: javadoc
 *
 * @author Nico Klasens
 */
public abstract class SqlAction {

   private MMBase mmb;
   private Cloud cloud;


   public abstract String getSql();


   public abstract String process(ResultSet rs) throws BridgeException, SQLException;


   public String getFieldname(String name) {
      if (mmb == null) {
         throw new IllegalStateException("MMBase system not found");
      }
      return (String) mmb.getStorageManagerFactory().getStorageIdentifier(name);
   }


   public String getTable(String name) {
      if (mmb == null) {
         throw new IllegalStateException("MMBase system not found");
      }
      return mmb.getBaseName() + "_" + name;
   }


   public String getTableField(String table, String field) {
      return getTable(table) + "." + getFieldname(field);
   }


   protected Cloud getCloud() {
      return cloud;
   }


   public void setCloud(Cloud cloud) {
      this.cloud = cloud;
   }


   public void setMmb(MMBase mmb) {
      this.mmb = mmb;
   }
}
