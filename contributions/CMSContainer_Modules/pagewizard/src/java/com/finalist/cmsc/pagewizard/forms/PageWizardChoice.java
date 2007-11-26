package com.finalist.cmsc.pagewizard.forms;

public class PageWizardChoice {
   private String type;
   private String parameter;


   public PageWizardChoice(String type, String parameter) {
      this.type = type;
      this.parameter = parameter;
   }


   public String getParameter() {
      return parameter;
   }


   public String getType() {
      return type;
   }

}
