package com.finalist.cmsc.beans.om;

import net.sf.mmapps.commons.beans.NodeBean;

/**
 * @author Wouter Heijke
 */
public class Properties extends NodeBean {

   private String key;

   private String description;

   private String value;

   private String prod;

   private String preprod;

   private String test;

   private String dev;


   public String getDescription() {
      return description;
   }


   public void setDescription(String description) {
      this.description = description;
   }


   public String getDev() {
      return dev;
   }


   public void setDev(String dev) {
      this.dev = dev;
   }


   public String getKey() {
      return key;
   }


   public void setKey(String key) {
      this.key = key;
   }


   public String getPreprod() {
      return preprod;
   }


   public void setPreprod(String preprod) {
      this.preprod = preprod;
   }


   public String getProd() {
      return prod;
   }


   public void setProd(String prod) {
      this.prod = prod;
   }


   public String getTest() {
      return test;
   }


   public void setTest(String test) {
      this.test = test;
   }


   public String getValue() {
      return value;
   }


   public void setValue(String value) {
      this.value = value;
   }
}
