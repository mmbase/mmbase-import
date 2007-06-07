/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.publish;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.remotepublishing.util.PublishUtil;

import com.finalist.cmsc.mmbase.TypeUtil;
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

    public abstract void publish(Node node);

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
    
    public abstract void remove(Node node);

    public abstract void unpublish(Node node);

    
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
               else {
                   if (!RepositoryUtil.isContentChannel(childNode) &&
                           !Workflow.isWorkflowElement(childNode)) {
                      findContentBlockNodes(childNode, nodes);
                   }
               }
            }
        }
    }

    public int getLiveNumber(Node node) {
//        Map<Integer,Integer> numbers = PublishManager.getPublishedNodeNumbers(node);
//        Iterator<Integer> iter = numbers.values().iterator();
//        if (iter.hasNext()) {
//            return iter.next();
//        }
        return -1;
    }

}
