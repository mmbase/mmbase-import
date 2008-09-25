package com.finalist.cmsc.dataconversion.dataaccess;

import java.util.Collection;

public class DataHolder {
   
   private String tableName;

   private Collection<Elements> collection;

   private Integer snumber ;
   
   private Integer dnumber;
   
   public String getTableName() {
      return tableName;
   }

   public void setTableName(String tableName) {
      this.tableName = tableName;
   }

   public Collection<Elements> getCollection() {
      return collection;
   }

   public void setCollection(Collection<Elements> collection) {
      this.collection = collection;
   }

   public Integer getSnumber() {
      return snumber;
   }

   public void setSnumber(Integer snumber) {
      this.snumber = snumber;
   }

   public Integer getDnumber() {
      return dnumber;
   }

   public void setDnumber(Integer dnumber) {
      this.dnumber = dnumber;
   }
   
}
