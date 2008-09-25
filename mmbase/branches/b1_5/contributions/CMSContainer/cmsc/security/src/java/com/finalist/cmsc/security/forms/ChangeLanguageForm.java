package com.finalist.cmsc.security.forms;

import org.apache.struts.action.ActionForm;

/**
 * Form bean for the ChangePasswordForm page.
 * 
 * @author Nico Klasens
 */
@SuppressWarnings("serial")
public class ChangeLanguageForm extends ActionForm {

   private String language;


   public String getLanguage() {
      return language;
   }


   public void setLanguage(String language) {
      this.language = language;
   }
}