/*
 * MMBase Remote Publishing
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package org.mmbase.remotepublishing.util;

import java.util.*;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.remotepublishing.builders.PublishingQueueBuilder;
import org.mmbase.util.logging.*;

/**
 * Util class to put nodes in a publish queue.
 * 
 * @author Ronald Kramp
 * @version PublishUtil.java,v 1.2 2003/07/28 09:44:16 nico Exp
 */
public class PublishUtil {

    private static Logger log = Logging.getLoggerInstance(PublishUtil.class.getName());

    /**
     * add a node to the publish queue
     * 
     * @param objectNodenode the node to put in the publish queue
     */
    public static void publishOrUpdateNode(MMObjectNode objectNodenode) {
        int number = objectNodenode.getIntValue("number");
        publishOrUpdateNode(getAdminCloud(), number, null);
    }

    /**
     * add a node to the publish queue
     * 
     * @param number the node to put in the publish queue
     */
    public static void publishOrUpdateNode(int number) {
        publishOrUpdateNode(getAdminCloud(), number, null);
    }

    /**
     * add a node to the publish queue
     * 
     * @param node the node to put in the publish queue
     */
    public static void publishOrUpdateNode(Node node) {
        int number = node.getNumber();
        publishOrUpdateNode(node.getCloud(), number, null);
    }

    /**
     * add a node to the publish queue
     * 
     * @param number the number of the node to put in the publish queue
     * @param cloud cloud instance
     */
    public static void publishOrUpdateNode(Cloud cloud, int number) {
        publishOrUpdateNode(cloud, number, null);
    }

    /**
     * add a node to the publish queue
     * 
     * @param number the number of the node to put in the publish queue
     * @param cloud cloud instance
     * @param publishDate publish when this date has passed
     */
    public static void publishOrUpdateNode(Cloud cloud, int number, Date publishDate) {
        log.debug("PublishOrUpdateNode with number = " + number);
        createQueueNode(cloud, number, -1, PublishingQueueBuilder.ACTION_UPDATE, publishDate, null);
    }

    /**
     * add the node to the queue to remove all published instances of a node
     * 
     * @param objectNodenode the node to be removed
     */
    public static void removeNode(MMObjectNode objectNodenode) {
        int number = objectNodenode.getIntValue("number");
        removeNode(getAdminCloud(), number);
    }

    /**
     * add the node to the queue to remove all published instances of a node
     * 
     * @param number the number of the node to be removed
     */
    public static void removeNode(int number) {
        removeNode(getAdminCloud(), number, null);
    }

    /**
     * add the node to the queue to remove all published instances of a node
     * 
     * @param number the number of the node to be removed
     * @param cloud cloud instance
     */
    public static void removeNode(Cloud cloud, int number) {
        removeNode(cloud, number, null);
    }

    public static void removeNode(Cloud cloud, int number, Date publishDate) {
        log.debug("removeNode with number = " + number);
        String action = PublishingQueueBuilder.ACTION_REMOVE;
        createQueueNode(cloud, number, -1, action, publishDate, null);
    }

    public static void removeFromQueue(Node node) {
        log.debug("removeFromQueue with number = " + node.getNumber());
        NodeManager nodeManager = node.getCloud().getNodeManager("publishqueue");

        NodeList list = nodeManager.getList(
                "[status] != 'fail' and [status] != 'done' and sourcenumber = " + node.getNumber(),
                null, null);
        NodeIterator iter = list.nodeIterator();
        while (iter.hasNext()) {
            Node element = iter.nextNode();
            element.delete();
        }
    }

    public static void removeFromQueue(Node node, List<Node> clouds) {
        log.debug("removeFromQueue with number = " + node.getNumber());
        NodeManager nodeManager = node.getCloud().getNodeManager("publishqueue");

        for (Node cloudNode : clouds) {
            NodeList list = nodeManager.getList(
                    "[status] != 'fail' and [status] != 'done' and sourcenumber = "
                            + node.getNumber() + " and sourcecloud = " + cloudNode.getNumber(),
                    null, null);
            NodeIterator iter = list.nodeIterator();
            while (iter.hasNext()) {
                Node element = iter.nextNode();
                element.delete();
            }
        }
    }

    public static void publishOrUpdateNode(Node node, Node cloudNode) {
        if (node == null) { throw new IllegalArgumentException("Node = null"); }
        if (cloudNode == null) { throw new IllegalArgumentException("CloudNode = null "
                + (node == null ? -1 : node.getNumber())); }
        publishOrUpdateNode(node.getCloud(), node.getNumber(), cloudNode.getNumber());
    }

    public static void publishOrUpdateNode(Cloud cloud, int number, int cloudNumber) {
        publishOrUpdateNode(cloud, number, cloudNumber, null);
    }

    public static void publishOrUpdateNode(Cloud cloud, int number, int cloudNumber,
            Date publishDate) {
        publishOrUpdateNode(cloud, number, cloudNumber, publishDate, true);
    }

    public static void publishOrUpdateNode(Cloud cloud, int number, int cloudNumber,
            Date publishDate, boolean relations) {
        log.debug("PublishOrUpdateNode with number = " + number);
        String action;
        if (relations) {
            action = PublishingQueueBuilder.ACTION_UPDATE;
        }
        else {
            action = PublishingQueueBuilder.ACTION_UPDATE_NODE;
        }

        createQueueNode(cloud, number, cloudNumber, action, publishDate, null);
    }

    public static void publishOrUpdateRelations(int number, List<Integer> relatedNodes) {
        publishOrUpdateRelations(getAdminCloud(), number, -1, relatedNodes);
    }

    public static void publishOrUpdateRelations(Cloud cloud, int number, List<Integer> relatedNodes) {
        publishOrUpdateRelations(cloud, number, -1, relatedNodes);
    }

    public static void publishOrUpdateRelations(Cloud cloud, int number, int cloudNumber,
            List<Integer> relatedNodes) {
        String action = PublishingQueueBuilder.ACTION_UPDATE_RELATIONS;
        createQueueNode(cloud, number, cloudNumber, action, null, relatedNodes);
    }

    private static Cloud getAdminCloud() {
        return CloudProviderFactory.getCloudProvider().getAdminCloud();
    }

    private static void createQueueNode(Cloud cloud, int number, int cloudNumber, String action,
            Date publishDate, List<Integer> relatedNodes) {
        NodeManager nodeManager = cloud.getNodeManager("publishqueue");
        Node node = nodeManager.createNode();
        node.setIntValue(PublishingQueueBuilder.FIELD_SOURCENUMBER, number);
        node.setStringValue(PublishingQueueBuilder.FIELD_ACTION, action);
        if (cloudNumber > 0) {
            node.setIntValue(PublishingQueueBuilder.FIELD_DESTINATIONCLOUD, cloudNumber);
        }
        if (publishDate != null) {
            node.setDateValue(PublishingQueueBuilder.FIELD_PUBLISHDATE, publishDate);
        }
        if (relatedNodes != null && relatedNodes.size() > 0) {
            String localNumbers = "";
            for (Integer localNumber : relatedNodes) {
                localNumbers += (localNumbers.length() > 0) ? "," + localNumber : localNumber;
            }
            node.setStringValue(PublishingQueueBuilder.FIELD_RELATEDNODES, localNumbers);
        }

        node.commit();
    }

}
