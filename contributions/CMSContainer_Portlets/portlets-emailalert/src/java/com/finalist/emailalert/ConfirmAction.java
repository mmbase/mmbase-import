package com.finalist.emailalert;

import net.sf.mmapps.commons.util.HttpUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfirmAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(ConfirmAction.class.getName());


   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
         HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

      String emailAddress = httpServletRequest.getParameter("s");
      String returnUrl = null;
      CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = cloudProvider.getCloud();
      if (emailAddress != null) {
         Node subscriberNode = null;
         try {
            subscriberNode = SearchUtil.findNode(cloud, "subscriber", "emailaddress", emailAddress);
         }
         catch (Exception e) {
            log.debug(e);
         }
         if (subscriberNode != null) {
            if (!subscriberNode.getBooleanValue("valid")) {
               subscriberNode.setBooleanValue("valid", true);
               subscriberNode.commit();
            }
            returnUrl = getConfirmationLink(cloud);
         }
      }

      if (returnUrl == null) {
         Node page404 = SearchUtil.findNode(cloud, "page", "urlfragment", "404");
         returnUrl = "/content/" + page404.getNumber();
      }
      returnUrl = HttpUtil.getWebappUri(httpServletRequest) + returnUrl;
      httpServletResponse.sendRedirect(httpServletResponse.encodeRedirectURL(returnUrl));
      return null;
   }


   private String getConfirmationLink(Cloud cloud) {
      String link = null;
      NodeList emailalerts = SearchUtil.findNodeList(cloud, "emailalert");
      Node emailAlert = emailalerts.getNode(0);
      if (emailalerts.size() > 1) {
         log.error("found " + emailalerts.size() + " emailalert nodes; first one will be used");
      }
      NodeList pages = emailAlert.getRelatedNodes("page");
      if (pages != null && pages.size() > 0) {
         Node page = pages.getNode(0);
         link = "/content/" + page.getNumber();
      }
      return link;
   }
}
