/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib;

import java.util.List;

import com.finalist.cmsc.beans.om.Stylesheet;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;

/**
 * Tag to include stylesheets in Page
 */
public class InsertStylesheetTag extends AbstractListTag<Stylesheet> {

   private boolean override = false;


   @Override
   protected List<Stylesheet> getList() {
      return SiteManagement.getStylesheetForPageByPath(getPath(), override);
   }


   public void setOverride(String override) {
      this.override = (override != null) && (override.equals("true"));
   }
}
