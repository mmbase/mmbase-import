package com.finalist.cmsc.pagewizard.forms;

import java.util.ArrayList;

public class PageWizardDefinition {
   private int number;
   private String name;
   private String description;
   private ArrayList<PageWizardPortlet> portlets = new ArrayList<PageWizardPortlet>();


   public PageWizardDefinition(int number, String name, String description) {
      this.number = number;
      this.name = name;
      this.description = description;
   }


   public String getDescription() {
      return description;
   }


   public String getName() {
      return name;
   }


   public int getNumber() {
      return number;
   }


   public ArrayList<PageWizardPortlet> getPortlets() {
      return portlets;
   }


   public void setPortlets(ArrayList<PageWizardPortlet> portlets) {
      this.portlets = portlets;
   }

}
