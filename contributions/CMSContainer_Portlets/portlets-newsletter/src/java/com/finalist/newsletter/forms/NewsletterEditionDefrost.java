package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.newsletter.util.NewsletterPublicationUtil;

public class NewsletterEditionDefrost extends MMBaseFormlessAction{

   /**
    * name of submit button in jsp to cancel removal
    */
   private static final String ACTION_CANCEL = "cancel";
   
   private static final String SUCCESS_FORWARD = "success";
   
   private static final String ACTION_CONFIRM = "save";
   @Override
   public ActionForward execute(ActionMapping mapping,
         HttpServletRequest request, Cloud cloud) throws Exception {
      int number = Integer.parseInt(getParameter(request, "number", true));
      if(isSaveAction(request)) {
         Node edition = cloud.getNode(number);
         request.setAttribute("action", "defrost");
         NewsletterPublicationUtil.defrostEdition(edition);
         return mapping.findForward(SUCCESS_FORWARD);
      }
      if(isCancelAction(request)) {
         String forwardPath = mapping.findForward("cancel").getPath();
         forwardPath = forwardPath.concat("?showpage=" + number);
         if (StringUtils.isNotBlank(request.getParameter("forward"))) {
            ActionForward ret = new ActionForward(mapping.findForward("publicationedit").getPath() + "?newsletterId="
                     + request.getParameter("newsletterId"));
            ret.setRedirect(true);
            return ret;
         }
         return new ActionForward(forwardPath);
      }
      request.setAttribute("action", "defrost");
      return mapping.findForward("confirm");
   }
   
   private boolean isCancelAction(HttpServletRequest request) {
      return getParameter(request, ACTION_CANCEL) != null;
   }
   private boolean isSaveAction(HttpServletRequest request) {
      return getParameter(request, ACTION_CONFIRM) != null;
   }
}
