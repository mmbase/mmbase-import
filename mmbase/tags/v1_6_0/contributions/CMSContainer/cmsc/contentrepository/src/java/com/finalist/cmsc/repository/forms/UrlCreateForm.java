package com.finalist.cmsc.repository.forms;

import org.apache.struts.action.ActionForm;

@SuppressWarnings("serial")
public class UrlCreateForm extends ActionForm {

   private String title;
   private String description;
   private String url;
   private String parentchannel;
   private String strict;
   
   public String getTitle() {
      return title;
   }
   public void setTitle(String name) {
      this.title = name;
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
   public String getStrict() {
      return strict;
   }
   public void setStrict(String strict) {
      this.strict = strict;
   }

}
