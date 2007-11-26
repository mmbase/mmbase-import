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
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public abstract class FormlessAction extends Action {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(FormlessAction.class.getName());


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {

      if (log.isDebugEnabled()) {
         if (form != null) {
            log.debug("ignore actionform " + form.getClass().getName());
            log.debug("ignore response " + response.getClass().getName());
         }
      }
      return execute(mapping, request);
   }


   public abstract ActionForward execute(ActionMapping mapping, HttpServletRequest request) throws Exception;

}
