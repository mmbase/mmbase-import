package com.finalist.action.unsubscribe;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.finalist.newsletter.services.*;


public class UnsubscribeAction extends Action {
		
	   private static final String ACTION_REMOVE = "remove";
	   private static final String ACTION_CANCEL = "cancel";
	
	 @Override
	   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	         HttpServletResponse response) throws Exception {

		 int userId = Integer.parseInt(request.getParameter("userId")); 
		 int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
		 
		 if (isRemoveAction(request)) {
			 NewsletterSubscriptionServices service = NewsletterServiceFactory.getNewsletterSubscriptionServices();
			 service.modifyStauts(userId, newsletterId,"INACTIVE");
			 return mapping.findForward("finish");
		 }
		 if (isCancelAction(request)) {
	         return mapping.findForward("canel");
	      }
	      	return mapping.findForward("delete");
	 }

	   private boolean isRemoveAction(HttpServletRequest request) {
	      return getParameter(request, ACTION_REMOVE) != null;
	   }

	   private boolean isCancelAction(HttpServletRequest request) {
	      return getParameter(request, ACTION_CANCEL) != null;
	   }
	   
	   public String getParameter(HttpServletRequest request, String name) {
		   String value = request.getParameter(name);
		      if (value == null) {
		         value = (String) request.getAttribute(name);
		      }
		      return value;
		   }
}
