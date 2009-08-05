package com.finalist.cmsc.dataconversion.service;

import java.util.HashMap;

public class Data {

   private Integer relateId;
   
   private String relateTable;
   
   private String tableName;
   
   private Byte type;   
   
   private HashMap<Integer,Integer> identifiers;
   
   private String sourceType;
   
   private String destinationType;
   
   private String sourceRelationType;
   
   private String destinationRelationType;
   
   private String relationType;
   
   private String reverse ;
   
   public Data(Byte type) {
      this.type = type;
   }
   
   public String getSourceType() {
      return sourceType;
   }

   public void setSourceType(String sourceType) {
      this.sourceType = sourceType;
   }

   public String getDestinationType() {
      return destinationType;
   }

   public void setDestinationType(String destinationType) {
      this.destinationType = destinationType;
   }

   public String getSourceRelationType() {
      return sourceRelationType;
   }

   public void setSourceRelationType(String sourceRelationType) {
      this.sourceRelationType = sourceRelationType;
   }

   public String getDestinationRelationType() {
      return destinationRelationType;
   }

   public void setDestinationRelationType(String destinationRelationType) {
      this.destinationRelationType = destinationRelationType;
   }

   public Integer getRelateId() {
      return relateId;
   }

   public void setRelateId(Integer relateId) {
      this.relateId = relateId;
   }

   public String getRelateTable() {
      return relateTable;
   }

   public void setRelateTable(String relateTable) {
      this.relateTable = relateTable;
   }

   public String getTableName() {
      return tableName;
   }

   public void setTableName(String tableName) {
      this.tableName = tableName;
   }

   public Byte getType() {
      return type;
   }

   public void setType(Byte type) {
      this.type = type;
   }

   public HashMap<Integer,Integer> getIdentifiers() {
      return identifiers;
   }

   public void setIdentifiers(HashMap<Integer,Integer> identifiers) {
      this.identifiers = identifiers;
   }

   public String getRelationType() {
      return relationType;
   }

   public void setRelationType(String relationType) {
      this.relationType = relationType;
   }

   public String getReverse() {
      return reverse;
   }

   public void setReverse(String reverse) {
      this.reverse = reverse;
   }
}
