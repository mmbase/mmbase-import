package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

public abstract class NewsletterEditionAction extends MMBaseFormlessAction{
   /**
    * name of submit button in jsp to cancel 
    */
   private static final String ACTION_CANCEL = "cancel";
   /**
    * name of submit button in jsp to save 
    */
   private static final String ACTION_SAVE = "save";
   private static final String SUCCESS_FORWARD = "success";
   @Override
   public ActionForward execute(ActionMapping mapping,
         HttpServletRequest request, Cloud cloud) throws Exception {
      int number = Integer.parseInt(getParameter(request, "number", true));

      if(isSaveAction(request)) {
         Node edition = cloud.getNode(number);
         request.setAttribute("action", getAction());
         doSave(request,edition);
         if (StringUtils.isNotBlank(request.getParameter("forward"))) {
            ActionForward ret = new ActionForward(mapping.findForward("publicationedit").getPath() + "?newsletterId="
                     + request.getParameter("newsletterId"));
            ret.setRedirect(true);
            return ret;
         }
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
      request.setAttribute("action", getAction());
      return mapping.findForward("confirm");
   }
   private boolean isCancelAction(HttpServletRequest request) {
      return getParameter(request, ACTION_CANCEL) != null;
   }
   private boolean isSaveAction(HttpServletRequest request) {
      return getParameter(request, ACTION_SAVE) != null;
   }
   protected abstract void  doSave(HttpServletRequest request,Node edition) throws Exception;
   
   protected abstract String getAction();
}
