package com.finalist.newsletter.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

/**
 * using for deleting newsletter subscriber from newsletter
 * 
 * @author Lisa
 * 
 */
public class NewsletterSubscriberDeleteAction extends MMBaseFormlessAction {

   /**
    * @param mapping
    *           Description of Parameter
    * @param request
    *           Description of Parameter
    * @param cloud
    *           Description of Parameter
    * @exception Description
    *               of Exception
    * @return refreshing newsletter subscriber list
    */
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String authId = request.getParameter("authid");
      String newsletterId = request.getParameter("newsletterId");

      if (StringUtils.isNotBlank(authId)) {
         Node newsletterNode = cloud.getNode(Integer.parseInt(newsletterId));
         List<Node> subscriptions = newsletterNode.getRelatedNodes(cloud.getNodeManager("subscriptionrecord"));

         for (Node subscription : subscriptions) {
            String subscriberId = subscription.getStringValue("subscriber");

            if (subscriberId.equals(authId)) {
               subscription.deleteRelations();
               subscription.delete();
            }
         }
      }
      request.setAttribute("newsletterId", newsletterId);
      return mapping.findForward("success");
   }
}