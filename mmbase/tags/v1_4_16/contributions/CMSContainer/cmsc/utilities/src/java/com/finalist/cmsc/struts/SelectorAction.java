/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;

public abstract class SelectorAction extends TreeAction {

   private String linkPattern;
   private String target;


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      if (mapping instanceof SelectorActionMapping) {
         SelectorActionMapping selectoMapping = (SelectorActionMapping) mapping;
         linkPattern = selectoMapping.getLinkPattern();
         target = selectoMapping.getTarget();
      }

      return super.execute(mapping, form, request, response, cloud);
   }


   protected String getLinkPattern() {
      return linkPattern;
   }


   protected String getTarget() {
      return target;
   }
}
