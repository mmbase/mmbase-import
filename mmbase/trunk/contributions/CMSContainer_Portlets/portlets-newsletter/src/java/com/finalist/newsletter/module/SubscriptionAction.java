/**
 * 
 */
package com.finalist.newsletter.module;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class SubscriptionAction extends Action {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

      String action = request.getParameter("action");
      if (action != null) {

      } else {

      }
      ActionForward ret = new ActionForward(mapping.findForward("succes").getPath());
      return (ret);
   }

}