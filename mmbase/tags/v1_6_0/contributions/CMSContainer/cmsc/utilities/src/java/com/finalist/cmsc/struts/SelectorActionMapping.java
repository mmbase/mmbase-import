/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.struts;

import org.apache.struts.action.ActionMapping;

@SuppressWarnings("serial")
public class SelectorActionMapping extends ActionMapping {

   private String linkPattern;
   private String target;


   protected String getLinkPattern() {
      return linkPattern;
   }


   public void setLinkPattern(String linkPattern) {
      this.linkPattern = linkPattern;
   }


   protected String getTarget() {
      return target;
   }


   public void setTarget(String target) {
      this.target = target;
   }

}
