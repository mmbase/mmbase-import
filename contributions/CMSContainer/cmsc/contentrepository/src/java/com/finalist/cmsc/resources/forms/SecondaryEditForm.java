package com.finalist.cmsc.resources.forms;

import org.apache.struts.action.ActionForm;

@SuppressWarnings("serial")
public class SecondaryEditForm extends ActionForm {

   public static final String ACTION_SAVE = "save";
   public static final String ACTION_CANCEL = "cancel";
   public static final String ACTION_INIT = "init";

   private int number;
   private String title;
   private String url;
   private String description;
   private String returnUrl;

   private String action;


   public int getNumber() {
      return number;
   }


   public void setNumber(int number) {
      this.number = number;
   }


   public String getDescription() {
      return description;
   }


   public void setDescription(String description) {
      this.description = description;
   }


   public String getReturnUrl() {
      return returnUrl;
   }


   public void setReturnUrl(String returnUrl) {
      this.returnUrl = returnUrl;
   }


   public String getTitle() {
      return title;
   }


   public void setTitle(String title) {
      this.title = title;
   }


   public String getUrl() {
      return url;
   }


   public void setUrl(String url) {
      this.url = url;
   }


   public String getAction() {
      return action;
   }


   public void setAction(String action) {
      this.action = action;
   }
}
