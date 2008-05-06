package com.finalist.cmsc.dataconversion.forms;

import com.finalist.cmsc.struts.MMBaseForm;

@SuppressWarnings("serial")
public class ConverseForm extends MMBaseForm  {

   /**
    * 
    */
   private String driver;   
   private String url;   
   private String port;   
   private String user;   
   private String password;
   
   public String getDriver() {
      return driver;
   }
   public void setDriver(String driver) {
      this.driver = driver;
   }
   public String getUrl() {
      return url;
   }
   public void setUrl(String url) {
      this.url = url;
   }
   public String getPort() {
      return port;
   }
   public void setPort(String port) {
      this.port = port;
   }
   public String getUser() {
      return user;
   }
   public void setUser(String user) {
      this.user = user;
   }
   public String getPassword() {
      return password;
   }
   public void setPassword(String password) {
      this.password = password;
   }
   
   
}
