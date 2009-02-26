/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.struts;

import org.apache.struts.action.ActionForm;

public class QuickSearchForm extends ActionForm {

   private String path;


   public String getPath() {
      return path;
   }


   public void setPath(String path) {
      this.path = path;
   }

}
