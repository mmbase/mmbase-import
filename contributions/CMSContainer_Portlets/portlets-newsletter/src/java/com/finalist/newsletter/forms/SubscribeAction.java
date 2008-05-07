package com.finalist.newsletter.forms;

import com.finalist.newsletter.services.*;
import com.finalist.cmsc.services.community.person.PersonService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.springframework.web.struts.DispatchActionSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SubscribeAction extends DispatchActionSupport {
   private static Log log = LogFactory.getLog(SubscribeAction.class);

   NewsletterSubscriptionServices service;

   protected void onInit() {
      super.onInit();
      service = (NewsletterSubscriptionServices) getWebApplicationContext().getBean("subscriptionServices");
   }

   public ActionForward modifyStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
         throws Exception {

      int userId = CommunityModuleAdapter.getCurrentUserId();
      int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));

      log.debug(String.format("user %s modify status of newsletter %s",userId,newsletterId));

      if (service.noSubscriptionRecord(userId, newsletterId)) {
         service.addNewRecord(userId, newsletterId);
      }else{
         service.changeStatus(userId,newsletterId);
      }

      return mapping.findForward("success");
   }

   public ActionForward modifyFormat(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      int userId = CommunityModuleAdapter.getCurrentUserId();
      int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
      String format = request.getParameter("format");

      log.debug(String.format("user %s modify prefered MIME of newsletter %s to %s",userId,format,newsletterId));

      service.modifyFormat(userId, newsletterId, format);

      return mapping.findForward("success");
   }

   public ActionForward modifyTag(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
         throws Exception {

      Boolean hasSelect = Boolean.parseBoolean(request.getParameter("select"));
      int userId = CommunityModuleAdapter.getCurrentUserId();
      int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
      int termId = Integer.parseInt(request.getParameter("termId"));

      log.debug(String.format("user %s modify term %s in newsletter %s",userId,termId,newsletterId));

      if (hasSelect) {
         service.selectTermInLetter(userId, newsletterId, termId);
      }
      else {
         service.unSelectTermInLetter(userId, newsletterId, termId);
      }

      return mapping.findForward("success");
   }
}

