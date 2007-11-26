package com.finalist.cmsc.publish;

import java.util.*;

import org.mmbase.core.event.*;
import org.mmbase.remotepublishing.PublishManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.bridge.*;

import com.finalist.cmsc.mmbase.TypeUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.navigation.PortletUtil;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

public class UnPublishNodeEventListener implements NodeEventListener, RelationEventListener {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(UnPublishNodeEventListener.class.getName());


   public void notify(NodeEvent event) {
      if (event.getType() == Event.TYPE_DELETE) {
         int nodeNumber = event.getNodeNumber();
         Cloud cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();

         if (log.isDebugEnabled() && PublishManager.isImported(cloud.getNode(nodeNumber))) {
            log.debug("node removed but not unlinked: " + nodeNumber);
         }
      }
   }


   public void notify(RelationEvent event) {
      if (event.getType() == Event.TYPE_DELETE) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();

         String sourceType = event.getRelationSourceType();
         String destType = event.getRelationDestinationType();

         NodeManager sourceManager = cloud.getNodeManager(sourceType);
         NodeManager destManager = cloud.getNodeManager(destType);

         int destNumber = event.getRelationDestinationNumber();

         if (TypeUtil.isSystemType(destType)) {
            return;
         }

         if (PagesUtil.isPageType(sourceManager)) {
            if (PortletUtil.isPortletType(destManager)) {
               Node portletNode = cloud.getNode(destNumber);
               if (!PortletUtil.isSinglePortlet(portletNode)) {
                  Set<Node> nodes = new HashSet<Node>();
                  PortletUtil.findPortletNodes(portletNode, nodes, true, true);

                  for (Node deleteNode : nodes) {
                     // Portlet parameter nodes will be cleaned by this method
                     // when the relation is deleted
                     if (deleteNode.isRelation()) {
                        deleteNode(deleteNode);
                     }
                  }
                  deleteNode(portletNode);
               }
            }
         }
         else {
            if (PortletUtil.isPortletType(sourceManager)) {
               if (PortletUtil.isParameterType(destManager)) {
                  deleteNode(cloud, destNumber);
               }
            }
         }
      }
   }


   private void deleteNode(Cloud cloud, int number) {
      deleteNode(cloud.getNode(number));
   }


   private void deleteNode(Node deleteNode) {
      PublishManager.unLinkNode(deleteNode);
      deleteNode.delete(false);
   }
}
