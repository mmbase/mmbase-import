/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.rssfeed.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class RssFeedDelete extends MMBaseFormlessAction {

   /** name of submit button in jsp to confirm removal */
   private static final String ACTION_REMOVE = "remove";

   /** name of submit button in jsp to cancel removal */
   private static final String ACTION_CANCEL = "cancel";


   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      if (isRemoveAction(request)) {
         String objectnumber = getParameter(request, "number", true);
         Node rssNode = cloud.getNode(objectnumber);
         Node pageNode = rssNode.getRelatedNodes("page").getNode(0);

         UserRole role = NavigationUtil.getRole(pageNode.getCloud(), pageNode, false);
         boolean isEditor = (role != null && SecurityUtil.isEditor(role));

         if (isEditor) {
            NavigationUtil.deleteItem(rssNode);
         }
         return mapping.findForward(SUCCESS);
      }

      if (isCancelAction(request)) {
         return mapping.findForward(SUCCESS);
      }

      // neither remove or cancel, show confirmation page
      return mapping.findForward("delete");
   }


   private boolean isRemoveAction(HttpServletRequest request) {
      return getParameter(request, ACTION_REMOVE) != null;
   }


   private boolean isCancelAction(HttpServletRequest request) {
      return getParameter(request, ACTION_CANCEL) != null;
   }

}
