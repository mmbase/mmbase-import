package com.finalist.cmsc.pagewizard.forms;

import org.apache.struts.action.ActionForm;

@SuppressWarnings("serial")
public class CreateContentForm extends ActionForm {

   private String creation;
   private String contentType;
   private String returnUrl;


   public String getContentType() {
      return contentType;
   }


   public void setContentType(String contentType) {
      this.contentType = contentType;
   }


   public String getCreation() {
      return creation;
   }


   public void setCreation(String creation) {
      this.creation = creation;
   }


   public String getReturnUrl() {
      return returnUrl;
   }


   public void setReturnUrl(String returnUrl) {
      this.returnUrl = returnUrl;
   }

}
