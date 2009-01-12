package com.finalist.cmsc.tools.generator;

import java.io.File;

public class VCSConfig {
   private String url;
   private String username;
   private String password;
   private String module;
   private String type;

   public String getWorkingfolder() {
      if ("svn".equals(type)) {
         return workingfolder + File.separator + this.module;
      } else {
         return workingfolder;
      }
   }

   public void setWorkingfolder(String workingfolder) {
      this.workingfolder = workingfolder;
   }

   private String workingfolder;


   public VCSConfig(String type) {
      this.type = type;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getModule() {
      return module;
   }

   public void setModule(String module) {
      this.module = module;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }
}
