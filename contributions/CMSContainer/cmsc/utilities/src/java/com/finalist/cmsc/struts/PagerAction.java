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

public class PagerAction extends MMBaseAction {

   protected static final String RESULTS = "results";
   protected static final String RESULT_COUNT = "resultCount";
   protected static final String DIRECTION = "direction";
   protected static final String ORDER = "order";
   protected static final String OFFSET = "offset";


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      PagerForm pagerForm = (PagerForm) form;
      request.setAttribute(RESULT_COUNT, Integer.valueOf(pagerForm.getResultCount()));
      request.setAttribute(RESULTS, pagerForm.getResults());

      return mapping.getInputForward();
   }
}
