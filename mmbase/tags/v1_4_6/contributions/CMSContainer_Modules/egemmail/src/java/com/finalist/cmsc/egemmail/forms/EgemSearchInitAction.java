package com.finalist.cmsc.egemmail.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.struts.MMBaseAction;

public class EgemSearchInitAction extends MMBaseAction {

   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      if (!(form instanceof EgemSearchForm)) {
         throw new IllegalArgumentException("The form is not an instance of " + EgemSearchForm.class);
      }

      return execute(mapping, (EgemSearchForm) form, request, response, cloud);
   }


   protected ActionForward execute(ActionMapping mapping, EgemSearchForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      form.setLimitToLastWeek(true);
      form.setSelectResults(true);

      return mapping.findForward(SUCCESS);
   }
}
