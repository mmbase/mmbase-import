/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.newsletter.util.NewsletterPublicationUtil;

/**
 * Newsletter Publication Delete Action
 * 
 * @author Lisa
 */
public class NewsletterPublicationDelete extends MMBaseFormlessAction {

   /** name of submit button in jsp to confirm removal */
   private static final String ACTION_REMOVE = "remove";

   /** name of submit button in jsp to cancel removal */
   private static final String ACTION_CANCEL = "cancel";

   /**
    * @param mapping
    * @param request
    * @param cloud
    * @return ActionForward refreshing NewsletterList
    * @throws Exception
    */
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
      String forwardType = getParameter(request, "forward");
      String parent = getParameter(request, "parent");

      if (isRemoveAction(request)) {
         int objectnumber = Integer.parseInt(getParameter(request, "number", true));
         Node newsletterPublicationNode = cloud.getNode(objectnumber);

         UserRole role = NavigationUtil.getRole(newsletterPublicationNode.getCloud(), newsletterPublicationNode, false);
         boolean isWebMaster = (role != null && SecurityUtil.isWebmaster(role));

         if (NavigationUtil.getChildCount(newsletterPublicationNode) > 0 && !isWebMaster) {
            return mapping.findForward("newsletterpublicationdeletewarning");
         }
         NewsletterPublicationUtil.deletePublication(objectnumber);
         return actionReturn(mapping, request, forwardType, parent);
      }

      if (isCancelAction(request)) {
         return actionReturn(mapping, request, forwardType, parent);
      }

      // neither remove or cancel, show confirmation page
      request.setAttribute("forward", forwardType);
      request.setAttribute("parent", parent);
      return mapping.findForward("newsletterpublicationdelete");
   }

   private boolean isCancelAction(HttpServletRequest request) {
      return getParameter(request, ACTION_CANCEL) != null;
   }

   private boolean isRemoveAction(HttpServletRequest request) {
      return getParameter(request, ACTION_REMOVE) != null;
   }

   /**
    * 
    * @param mapping
    * @param request
    * @param forwardType
    *           use to distinguish different return back forward
    * @param parent
    *           use to record parent id
    * @return ActionForward turn to correct page
    */
   protected ActionForward actionReturn(ActionMapping mapping, HttpServletRequest request, String forwardType,
         String parent) {
      ActionForward ret = null;
      if (StringUtils.isNotEmpty(forwardType)) {
         ret = new ActionForward(mapping.findForward(forwardType).getPath() + "?newsletterId=" + parent);
         request.setAttribute("newsletterId", parent);
      } else {
         ret = mapping.findForward(SUCCESS);
      }
      return ret;
   }

}
