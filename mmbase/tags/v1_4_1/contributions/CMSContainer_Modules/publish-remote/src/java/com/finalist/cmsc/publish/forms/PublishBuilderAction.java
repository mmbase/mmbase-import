package com.finalist.cmsc.publish.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.remotepublishing.util.PublishUtil;
import org.mmbase.security.Rank;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.navigation.PortletUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class PublishBuilderAction extends MMBaseFormlessAction {

   protected static final String DESTINATION = "DESTINATION";

   private static Logger log = Logging.getLoggerInstance(PublishBuilderAction.class);


   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      Locale locale = request.getLocale();

      MessageResources resources = getResources(request, "PUBLISH-REMOTE");

      List<String> errors = new ArrayList<String>();
      if ("on".equals(request.getParameter("builder_properties"))) {
         publishNodesOfBuilder(cloud, "properties", errors, resources, false);
      }
      if ("on".equals(request.getParameter("builder_portletdefinition"))) {
         publishPortletdefinition(cloud, "portletdefinition", errors, resources, locale);
      }
      if ("on".equals(request.getParameter("builder_layout"))) {
         publishNodesOfBuilder(cloud, "layout", errors, resources, false);
      }
      if ("on".equals(request.getParameter("builder_view"))) {
         publishNodesOfBuilder(cloud, "view", errors, resources, false);
      }
      if ("on".equals(request.getParameter("builder_stylesheet"))) {
         publishNodesOfBuilder(cloud, "stylesheet", errors, resources, false);
      }
      if ("on".equals(request.getParameter("builder_contentchannel"))) {
         publishNodesOfBuilder(cloud, "contentchannel", errors, resources, false);
      }

      if (!errors.isEmpty()) {
         request.setAttribute("errors", errors);
      }

      request.setAttribute("result", true);

      return mapping.findForward("result");
   }


   private boolean publishPortletdefinition(Cloud cloud, String builderName, List<String> errors,
         MessageResources messageResources, Locale locale) {
      if (cloud.hasNodeManager(builderName)) {
         NodeIterator nodeIterator = SearchUtil.findNodeList(cloud, builderName).nodeIterator();

         while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            PublishUtil.publishOrUpdateNode(node);
            if (PortletUtil.isSingleDefinition(node)) {
               Node portlet = PortletUtil.getPortletForDefinition(node);
               if (portlet != null) {
                  PublishUtil.publishOrUpdateNode(portlet);
                  NodeList params = PortletUtil.getParameters(portlet);
                  for (Iterator<Node> iter = params.iterator(); iter.hasNext();) {
                     Node param = iter.next();
                     PublishUtil.publishOrUpdateNode(param);
                  }
               }
            }
         }

         return true;
      }
      else {
         log.warn("Could not publish nodes of " + builderName);
         errors.add(messageResources.getMessage(locale, "publish.builder.notfound", builderName));
         return false;
      }
   }


   @Override
   public String getRequiredRankStr() {
      return Rank.ADMIN.toString();
   }


   public boolean publishNodesOfBuilder(Cloud cloud, String builderName, List<String> errors,
         MessageResources messageResources, boolean children) {
      if (cloud.hasNodeManager(builderName)) {
         NodeIterator nodeIterator = SearchUtil.findNodeList(cloud, builderName).nodeIterator();

         while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            PublishUtil.publishOrUpdateNode(node);
            if (children) {
               publishChildren(node);
            }
         }

         return true;
      }
      else {
         log.warn("Could not publish nodes of " + builderName);
         errors.add(messageResources.getMessage("publish.builder.notfound", builderName));
         return false;
      }
   }


   private void publishChildren(Node node) {
      RelationManagerList rml = node.getNodeManager().getAllowedRelations((NodeManager) null, null, DESTINATION);
      if (!rml.isEmpty()) {
         NodeIterator childs = node.getRelatedNodes("object", null, DESTINATION).nodeIterator();
         while (childs.hasNext()) {
            Node childNode = childs.nextNode();
            PublishUtil.publishOrUpdateNode(childNode);
            publishChildren(childNode);
         }
      }
   }
}
