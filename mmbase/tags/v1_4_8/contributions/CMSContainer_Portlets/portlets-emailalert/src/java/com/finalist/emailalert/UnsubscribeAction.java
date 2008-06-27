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
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.CompositeConstraint;
import org.mmbase.storage.search.Constraint;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UnsubscribeAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(UnsubscribeAction.class.getName());


   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
         HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
      String pageNumber = httpServletRequest.getParameter("p");
      String emailAddress = httpServletRequest.getParameter("s");
      String returnUrl = null;
      CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = cloudProvider.getCloud();
      if (emailAddress != null && pageNumber != null) {
         Node subscriberNode = null;
         try {
            subscriberNode = SearchUtil.findNode(cloud, "subscriber", "emailaddress", emailAddress);
         }
         catch (Exception e) {
            log.debug(e);
         }
         if (subscriberNode != null) {
            if (pageNumber.equals("all")) {
               unsubscribeAllPages(cloud, subscriberNode);
            }
            else {
               Node pageNode = null;
               try {
                  pageNode = cloud.getNode(pageNumber);
               }
               catch (Exception e) {
                  log.debug(e);
               }
               if (pageNode != null) {
                  unsubscribePage(cloud, subscriberNode, pageNode);
               }
            }
            returnUrl = getUnsubscribeLink(cloud);
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


   private void unsubscribePage(Cloud cloud, Node subscriberNode, Node pageNode) {
      NodeManager relationNodeManager = cloud.getNodeManager("subscriberel");
      NodeList relations = getRelations(relationNodeManager, pageNode.getNumber(), subscriberNode.getNumber());
      if (relations != null && relations.size() > 0) {
         NodeIterator nodeIterator = relations.nodeIterator();
         while (nodeIterator.hasNext()) {
            Node relation = nodeIterator.nextNode();
            relation.delete();
         }
      }
      if (!subscriberNode.hasRelations()) {
         // if there are no more relations delete the subscriber too
         subscriberNode.delete();
      }
   }


   private void unsubscribeAllPages(Cloud cloud, Node subscriberNode) {
      // delete the subscriber and all relations
      subscriberNode.delete(true);
   }


   private String getUnsubscribeLink(Cloud cloud) {
      String link = null;
      NodeList emailalerts = SearchUtil.findNodeList(cloud, "emailalert");
      Node emailAlert = emailalerts.getNode(0);
      if (emailalerts.size() > 1) {
         log.error("found " + emailalerts.size() + " emailalert nodes; first one will be used");
      }
      NodeList pages = emailAlert.getRelatedNodes("page");
      if (pages != null && pages.size() > 1) {
         Node page = pages.getNode(pages.size() - 1);
         link = "/content/" + page.getNumber();
      }
      return link;
   }


   private static NodeList getRelations(NodeManager builder, int source, int destination) {
      NodeQuery query = builder.createQuery();
      Constraint s = SearchUtil.createEqualConstraint(query, builder.getField("snumber"), source);
      Constraint d = SearchUtil.createEqualConstraint(query, builder.getField("dnumber"), destination);
      Constraint composite = query.createConstraint(s, CompositeConstraint.LOGICAL_AND, d);
      query.setConstraint(composite);
      NodeList list = builder.getList(query);
      return list;
   }
}
