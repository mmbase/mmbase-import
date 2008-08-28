package com.finalist.cmsc.pagewizard.forms;

import java.util.ArrayList;

public class PageWizardPortlet {

   private String position;
   private String contentType;
   private String contentTypeName;
   private ArrayList<PageWizardChoice> choices = new ArrayList<PageWizardChoice>();


   public PageWizardPortlet(String position) {
      this.position = position;
   }


   public ArrayList<PageWizardChoice> getChoices() {
      return choices;
   }


   public void setChoices(ArrayList<PageWizardChoice> choices) {
      this.choices = choices;
   }


   public String getPosition() {
      return position;
   }


   public String getContentType() {
      return contentType;
   }


   public void setContentType(String contentType) {
      this.contentType = contentType;
   }


   public String getContentTypeName() {
      return contentTypeName;
   }


   public void setContentTypeName(String contentTypeName) {
      this.contentTypeName = contentTypeName;
   }
}
