/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.navigation.forms;

import com.finalist.cmsc.struts.MMBaseForm;

public class MassModifyForm extends MMBaseForm {

   private static final long serialVersionUID = 6397905713331011685L;

   private int requiredLayout;
   private int newLayout;
   private boolean linkPortlets;


   public int getRequiredLayout() {
      return requiredLayout;
   }


   public void setRequiredLayout(int requiredLayout) {
      this.requiredLayout = requiredLayout;
   }


   public int getNewLayout() {
      return newLayout;
   }


   public void setNewLayout(int newLayout) {
      this.newLayout = newLayout;
   }


   public boolean isLinkPortlets() {
      return linkPortlets;
   }


   public void setLinkPortlets(boolean linkPortlets) {
      this.linkPortlets = linkPortlets;
   }

}
