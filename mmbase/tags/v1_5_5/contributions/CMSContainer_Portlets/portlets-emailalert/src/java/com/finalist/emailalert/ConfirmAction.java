package com.finalist.emailalert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.struts.action.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.util.HttpUtil;

public class ConfirmAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(ConfirmAction.class.getName());


   @Override
   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
         HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

      String emailAddress = httpServletRequest.getParameter("s");
      String returnUrl = null;
      Cloud cloud = getCloudForAnonymousUpdate(false);
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

   public Cloud getCloudForAnonymousUpdate(boolean isRemote) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      if (isRemote) {
         return Publish.getRemoteCloud(cloud);
      }
      return cloud;
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
