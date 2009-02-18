package com.finalist.cmsc.repository.forms;

import org.apache.struts.action.ActionForm;

@SuppressWarnings("serial")
public class UrlCreateForm extends ActionForm {

   private String name;
   private String description;
   private String url;
   private String parentchannel;
   
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public String getDescription() {
      return description;
   }
   public void setDescription(String description) {
      this.description = description;
   }
   public String getUrl() {
      return url;
   }
   public void setUrl(String url) {
      this.url = url;
   }
   public String getParentchannel() {
      return parentchannel;
   }
   public void setParentchannel(String parentchannel) {
      this.parentchannel = parentchannel;
   }

}
