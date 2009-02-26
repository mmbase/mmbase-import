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

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.newsletter.util.NewsletterUtil;

/**
 * Newsletter Delete Action
 *
 * @author Lisa Chen
 */
public class NewsletterDelete extends MMBaseFormlessAction {

   /**
    * name of submit button in jsp to confirm removal
    */
   private static final String ACTION_REMOVE = "remove";

   /**
    * name of submit button in jsp to cancel removal
    */
   private static final String ACTION_CANCEL = "cancel";

   /**
    * @param mapping Description of Parameter
    * @param request Description of Parameter
    * @param cloud   Description of Parameter
    * @return ActionForward refreshing newsletter list
    * @throws Exception Description of Exception
    */
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      if (isRemoveAction(request)) {
         String objectnumber = getParameter(request, "number", true);
         Node newsletterNode = cloud.getNode(objectnumber);

         UserRole role = NavigationUtil.getRole(newsletterNode.getCloud(), newsletterNode, false);
         boolean isWebMaster = (role != null && SecurityUtil.isWebmaster(role));

         if (NavigationUtil.getChildCount(newsletterNode) > 0 && !isWebMaster) {
            return mapping.findForward("newsletterdeletewarning");
         }

         int number = newsletterNode.getNumber();
         NewsletterUtil.deleteRelatedElement(number);
         newsletterNode.deleteRelations();
         NavigationUtil.deleteItem(newsletterNode);

         if (StringUtils.isNotEmpty(getParameter(request, "forward"))) {
            return mapping.findForward("newslettermanage");
         }
         return mapping.findForward(SUCCESS);
      }

      if (isCancelAction(request)) {
         if (StringUtils.isNotEmpty(getParameter(request, "forward"))) {
            return mapping.findForward("newslettermanage");
         }
         return mapping.findForward(SUCCESS);
      }

      // neither remove or cancel, show confirmation page
      return mapping.findForward("newsletterdelete");
   }

   private boolean isCancelAction(HttpServletRequest request) {
      return getParameter(request, ACTION_CANCEL) != null;
   }

   private boolean isRemoveAction(HttpServletRequest request) {
      return getParameter(request, ACTION_REMOVE) != null;
   }

}
