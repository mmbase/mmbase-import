/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.publish;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.remotepublishing.PublishManager;
import org.mmbase.remotepublishing.util.PublishUtil;

import com.finalist.cmsc.mmbase.TypeUtil;
import com.finalist.cmsc.repository.AssetElementUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.workflow.Workflow;


public abstract class Publisher {

    protected static final String SOURCE = "SOURCE";
    protected static final String DESTINATION = "DESTINATION";

    protected Cloud cloud;

    public Publisher(Cloud cloud) {
        this.cloud = cloud;
    }

    public abstract boolean isPublishable(Node node);

    public void publish(Node node) {
        PublishUtil.publishOrUpdateNode(cloud, node.getNumber());
    }

    public void publish(Node node, NodeList nodes) {
        publish(node);

        for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext();) {
            Node pnode = iterator.next();
            Date publishDate;
            if (node.getNodeManager().hasField("publishdate")) {
                publishDate = node.getDateValue("publishdate");
            }
            else {
                publishDate = new Date();
            }
            PublishUtil.publishOrUpdateNode(cloud, pnode.getNumber(), publishDate);
        }
    }

    public void remove(Node node) {
        PublishUtil.removeFromQueue(node);
    }

    public final void unpublish(Node node) {
        PublishUtil.removeNode(cloud, node.getNumber());
    }


    public static List<Node> findContentBlockNodes(Node node) {
        List<Node> nodes = new ArrayList<Node>();
        findContentBlockNodes(node, nodes);
        return nodes;
    }

    private static void findContentBlockNodes(Node node, List<Node> nodes) {
        if (nodes.contains(node) || TypeUtil.isSystemType(node.getNodeManager().getName())) {
            return;
        }

        nodes.add(node);
        RelationManagerList rml = node.getNodeManager().getAllowedRelations((NodeManager) null, null, DESTINATION);
        if (!rml.isEmpty()) {
            NodeIterator childs = node.getRelatedNodes("object", null, DESTINATION).nodeIterator();
            while (childs.hasNext()) {
               Node childNode = childs.nextNode();
               if (ContentElementUtil.isContentElement(childNode)) {
                   if (!RepositoryUtil.hasContentChannel(childNode)) {
                       findContentBlockNodes(childNode, nodes);
                   }
               }
               else if(AssetElementUtil.isAssetElement(childNode)){
                  nodes.add(childNode);
               }
               else {
                   if (!RepositoryUtil.isContentChannel(childNode) &&
                           !Workflow.isWorkflowElement(childNode)) {
                      findContentBlockNodes(childNode, nodes);
                   }
               }
            }
        }
    }


    public int getRemoteNumber(Node node) {
       if (PublishManager.isPublished(node)) {
           Map<Integer,Integer> numbers = PublishManager.getPublishedNodeNumbers(node);
           Iterator<Integer> iter = numbers.values().iterator();
           if (iter.hasNext()) {
               return iter.next();
           }
       }
       return -1;
    }

    public Node getRemoteNode(Node node) {
       if (PublishManager.isPublished(node)) {
          Map<Integer, Node> numbers = PublishManager.getPublishedNodes(node);
          Iterator<Node> iter = numbers.values().iterator();
          if (iter.hasNext()) {
             return iter.next();
          }
       }
       else {
          if (PublishManager.isImported(node)) {
             return PublishManager.getSourceNode(node);
          }
       }
       return null;
    }

    protected boolean isPublished(Node node) {
        return PublishManager.isPublished(node);
    }


    protected void publishNodes(Map<Node, Date> nodes) {
        for (Map.Entry<Node, Date> entry : nodes.entrySet()) {
            Node pnode = entry.getKey();
            Date publish = entry.getValue();
            PublishUtil.publishOrUpdateNode(cloud, pnode.getNumber(), publish);
        }
    }

    protected void publishNode(Node parent, List<Integer> relatedNodes) {
        PublishUtil.publishOrUpdateRelations(cloud, parent.getNumber(), relatedNodes);
    }

    protected void removeNodes(Collection<Node> removeNodes) {
        for (Node pnode : removeNodes) {
            PublishUtil.removeFromQueue(pnode);
        }
    }

}
