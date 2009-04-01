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
package org.mmbase.remotepublishing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.FieldIterator;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NotFoundException;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationIterator;
import org.mmbase.bridge.RelationList;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.StringList;
import org.mmbase.datatypes.DataType;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The primary functionality of the <code>Publishmanager</code> is to publish
 * nodes to the live / staging cloud which depends on the context in which this
 * class is being used. The problem with publishing is that nodes in different
 * contexts have different numbers. The mapping between node numbers in different
 * cloud contexts is maintained in the table <code>remotenodes</code>. This table
 * contains 4 important fields: sourcecloud, sourcenumber, destinationcloud and
 * destinationnumber. The source fields always keep track of where a node was
 * first produced. The destination indicates to what cloud the node was published.
 * The cloud fields keep a reference to one of the clouds stored in the
 * <code>cloud</code> table. The number fields are a reference to a node in one
 * of the clouds.
 * <p>
 * Example: if a node is published from the staging to the live context the sourcecloud
 * and sourcenumber in both contexts refer to the sourcenode in the staging context.
 * The sourcecloud field will contain different numbers for the records in the
 * <code>table</code> will have different id's in both clouds. The sourcenumber
 * field will be exact the same in both contexts. The same goes for the destination
 * fields with this difference that the destinationnumber field in both clouds will
 * refer to the number of the node in the live cloud.
 * <p>
 * The <code>PublishManager</code> distinguishes two different processes which are
 * a) publishing - which is the process of copying a node from cloud X to cloud Y and
 * b) importing - which is the process of copying a node from cloud Y to cloud X
 *
 * @author Finalist IT
 */
public final class PublishManager {

    private static final String TIMESTAMP = "timestamp";
    private static final String SOURCE_CLOUD = "sourcecloud";
    private static final String SOURCE_NUMBER = "sourcenumber";
    private static final String DESTINATION_NUMBER = "destinationnumber";
    private static final String DESTINATION_CLOUD = "destinationcloud";

    /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(PublishManager.class.getName());
   private static String MMBASE_PUBLISH_MANAGER = "remotenodes";

   private static Object publishLock = new Object();

   private PublishManager() {
       // Utility class
   }

   /**
    *  quick test to see if node is a relation by testing fieldnames
    */
   protected static boolean isRelation(Node node) {
      FieldIterator fi = node.getNodeManager().getFields().fieldIterator();
      int count = 0;

      while (fi.hasNext()) {
         String name = fi.nextField().getName();

         if (name.equals("rnumber") || name.equals("snumber") ||
               name.equals("dnumber")) {
            count++;
         }
      }

      if (count == 3) {
         return true;
      }

      return false;
   }

   /**
    * Publish a node to a different cloud, keeping records This will also publish
    * all relations inNode has with other published nodes
    *
    * @param localCloudInfo the cloud which the local node come from
    * @param localNode the node to be published
    * @param remoteCloudInfo the external cloud
    * @return  the published copy of inNode
    * @throws PublishException - when publication fails
    */
   public static Node createNodeAndRelations(CloudInfo localCloudInfo, Node localNode,
           CloudInfo remoteCloudInfo) throws PublishException {
       return createNodeAndRelations(localCloudInfo, localNode, remoteCloudInfo, true);
    }
   
   /**
    * Publish a node to a different cloud, keeping records This will also publish
    * all relations inNode has with other published nodes
    *
    * @param localCloudInfo the cloud which the local node come from
    * @param localNode the node to be published
    * @param remoteCloudInfo the external cloud
    * @param createRelations create the relations to other nodes
    * @return  the published copy of inNode
    * @throws PublishException - when publication fails
    */
   public static Node createNodeAndRelations(CloudInfo localCloudInfo, Node localNode, CloudInfo remoteCloudInfo, boolean createRelations)
      throws PublishException {
      synchronized (publishLock) {
         Node remoteNode = createNode(localCloudInfo, localNode, remoteCloudInfo);
         if (createRelations) {
             cloneRelations(localCloudInfo, localNode, remoteCloudInfo);
         }
         return remoteNode;
      }
   }

   private static Node createNode(CloudInfo localCloudInfo, Node localNode, CloudInfo remoteCloudInfo)
            throws PublishException {
      synchronized (publishLock) {
        log.debug("publishNode called with node (number,type)" +
                   localNode.getNumber() + "," + localNode.getNodeManager().getName() + ")");
    
         NodeManager nm = localNode.getNodeManager();
    
         if (nm.getName().equals(MMBASE_PUBLISH_MANAGER)) {
            throw new PublishException("Cannot publish publishing info!");
         }
    
         if (isPublished(localCloudInfo, localNode.getNumber(), remoteCloudInfo) || isImported(localCloudInfo, localNode.getNumber(), remoteCloudInfo)) {
            throw new PublishException("Attempt to publish Node #" + localNode.getNumber() + " twice to the same cloud");
         }
    
         if (isRelation(localNode)) {
            if ((localNode.getValue("rnumber") == null) ||
                  (localNode.getValue("snumber") == null) ||
                  (localNode.getValue("dnumber") == null)) {
               throw new PublishException("Attempt to publish invalid relation");
            }
    
            // relations should only be published when both the source and the destination node are either
            // 1) published to the other cloud
            // 2) imported from the othercloud
            Node source = localCloudInfo.getCloud().getNode(localNode.getStringValue("snumber"));
            Node destination = localCloudInfo.getCloud().getNode(localNode.getStringValue("dnumber"));
            if (!( (isPublished(localCloudInfo, source) && (isPublished(localCloudInfo, destination) || isImported(localCloudInfo, destination))) ||
                   (isImported(localCloudInfo, source) && (isPublished(localCloudInfo, destination) || isImported(localCloudInfo, destination))) ) ) {
               throw new PublishException("Attempt to publish relation between unpublished nodes or of unpublished type");
            }
         }
    
         // copy the node to the remote cloud
         Node remoteNode = cloneNode(localCloudInfo, localNode, remoteCloudInfo);
    
         if (remoteNode == null) {
            throw new PublishException("cloneNodeRemote for node #" + localNode.getNumber() + " returned null");
         }
         else {
             if (log.isDebugEnabled()) {
                //getNumber is a rmi call to the other server don't want that unless debugging
                log.debug("cloned the node to the new cloud new node(number,type)" +
                      remoteNode.getNumber() + "," + remoteNode.getNodeManager().getName() + ")");
             }
         }
         createPublishingInfo(localCloudInfo, localNode, remoteCloudInfo, remoteNode);
         return remoteNode;
      }
   }

   /**
    * Clone a node to a cloud, including any fields without keeping administrative information
    *
    * @param localCloudInfo the cloud which the local node come from
    * @param localNode the node to clone
    * @param remoteCloudInfo the cloud to clone the node to
    * @return the newly created node in the other cloud
    */
   protected static Node cloneNode(CloudInfo localCloudInfo, Node localNode, CloudInfo remoteCloudInfo) {
      if (isRelation(localNode)) {
         return cloneRelation(localCloudInfo, localNode, remoteCloudInfo);
      }
      else {
          synchronized (publishLock) {
             NodeManager localNodeManager = localNode.getNodeManager();
             NodeManager remoteNodeManager = remoteCloudInfo.getCloud().getNodeManager(localNodeManager.getName());
             Node remoteNode = remoteNodeManager.createNode();
    
             FieldIterator fields = localNodeManager.getFields().fieldIterator();
             while (fields.hasNext()) {
                Field field = fields.nextField();
                String fieldName = field.getName();
    
                if (!(fieldName.equals("owner") || fieldName.equals("number") ||
                      fieldName.equals("otype") ||
                      (fieldName.indexOf("_") == 0))) {
                   cloneNodeField(localCloudInfo, localNode, remoteCloudInfo, remoteNode, field);
                }
             }
             remoteNode.commit();
    
             cloneAliasses(localNode, remoteNode);
             return remoteNode;
          }
      }
   }

   /**
    * cloneNodeField copies node fields from one node to an other
    * @param localCloudInfo the cloud which the local node come from
    * @param sourceNode the source node
    * @param remoteCloudInfo the remote cloud which the destination node exist in
    * @param destinationNode destination node
    * @param field the field to clone
    */
   protected static void cloneNodeField(CloudInfo localCloudInfo, Node sourceNode, CloudInfo remoteCloudInfo,
            Node destinationNode, Field field) {
      String fieldName = field.getName();
      
      int fieldType = field.getType();
      if (fieldType == Field.TYPE_NODE) {
         Node nodeValue = sourceNode.getNodeValue(fieldName);
         if (isPublished(localCloudInfo, nodeValue.getNumber(), remoteCloudInfo)) {
            Node remoteNodeValue = getPublishedNode(localCloudInfo, nodeValue, remoteCloudInfo);
            destinationNode.setNodeValue(fieldName, remoteNodeValue);
         }
         else {
            String message = "Could not fill node field (" + fieldName + "), because the referred node " +
                    "(" + nodeValue.getNumber() + ") doesn't exist yet at the remotecloud. Perhaps there are some " +
                    "contentelements which are not live and don't have a workflow. ";
            log.warn(message);
         }
      }
      else {
         Field sourceField = sourceNode.getNodeManager().getField(fieldName);
         if (sourceField.getState() != Field.STATE_SYSTEM && !sourceField.isVirtual()) {
            destinationNode.setValueWithoutProcess(fieldName, 
                    sourceNode.getValueWithoutProcess(fieldName));
          }
      }
   }

   private static Node cloneRelation(CloudInfo localCloudInfo, Node localRelation, CloudInfo remoteCloudInfo) {
      synchronized (publishLock) {
         Node relationTypeNode = localRelation.getNodeValue("rnumber");
         String relName = relationTypeNode.getStringValue("sname");
         Node remoteSourceNode = getPublishedNode(localCloudInfo, localRelation.getNodeValue("snumber"), remoteCloudInfo);
         Node remoteDestinationNode = getPublishedNode(localCloudInfo, localRelation.getNodeValue("dnumber"), remoteCloudInfo);

         RelationManager remoteRelationManager =
               remoteCloudInfo.getCloud().getRelationManager(remoteSourceNode.getNodeManager().getName(),
                                              remoteDestinationNode.getNodeManager().getName(),
                                              relName);
         if (log.isDebugEnabled()) {
            //getNumber is a rmi call to the other server don't want that unless debugging
            log.debug("cloneNode remoteRelationManager (name)=(" + remoteRelationManager.getName() + ")");
         }
         Relation remoteRelation = remoteRelationManager.createRelation(remoteSourceNode, remoteDestinationNode);

         FieldIterator fields = localRelation.getNodeManager().getFields().fieldIterator();
         while (fields.hasNext()) {
            Field field = fields.nextField();
            String fieldName = field.getName();

            if (!(fieldName.equals("owner") || fieldName.equals("number") ||
                  fieldName.equals("otype") ||
                  (fieldName.indexOf("_") == 0) ||
                  fieldName.equals("snumber") || fieldName.equals("dir") ||
                  fieldName.equals("dnumber") ||
                  fieldName.equals("rnumber"))) {
               cloneNodeField(localCloudInfo, localRelation, remoteCloudInfo, remoteRelation, field);
            }
         }
         remoteRelation.commit();

         cloneAliasses(localRelation, remoteRelation);
         return remoteRelation;
      }
   }

   private static void cloneAliasses(Node localNode, Node remoteNode) {
        StringList list = localNode.getAliases();
        int listSize = list.size();
        for (int x = 0; x < listSize; x++) {
            remoteNode.createAlias(list.getString(x));
        }
    }

   private static void cloneRelations(CloudInfo localCloudInfo, Node localNode, CloudInfo remoteCloudInfo) throws PublishException {
      RelationList result = localCloudInfo.getCloud().getCloudContext().createRelationList();
      // Just add the result of both methods together
      NodeManager nodeManager = localCloudInfo.getCloud().getNodeManager("object");
      result.addAll(localNode.getRelations(null, nodeManager, "source"));
      result.addAll(localNode.getRelations(null, nodeManager, "destination"));
      
      cloneRelations(localCloudInfo, localNode, remoteCloudInfo, result);
   }

   private static void cloneRelations(CloudInfo localCloudInfo, Node localNode,
        CloudInfo remoteCloudInfo, List<Relation> result) throws PublishException {

      Iterator<Relation> ri = result.iterator();
      while (ri.hasNext()) {
         Relation rel = ri.next();
         Node relatedNode = null;

         if (rel.getSource().getNumber() == localNode.getNumber()) {
            relatedNode = rel.getDestination();
         } else {
            if (rel.getDestination().getNumber() == localNode.getNumber()) {
              relatedNode = rel.getSource();
            }
         }
         if (relatedNode == null) {
            throw new PublishException("Error examining nodes related to node #" + localNode.getNumber());
         }
         log.debug("relation (number)=(" + rel.getNumber() + ") points to  object=(number,type)=(" +
                   relatedNode.getNumber() + "," + relatedNode.getNodeManager().getName() + ")");
         if (isPublished(localCloudInfo, relatedNode.getNumber(), remoteCloudInfo) || isImported(localCloudInfo, relatedNode.getNumber(), remoteCloudInfo)) {
               // relatedNode is published to this localCloud or is imported from the remoteCloud
               // check whether the relation is published or imported too
               // if so update the relation else publish a new relation
               if (isPublished(localCloudInfo, rel.getNumber(), remoteCloudInfo) || isImported(localCloudInfo, rel.getNumber(), remoteCloudInfo)) {
                  if (isPublished(localCloudInfo, rel.getNumber(), remoteCloudInfo)) {
                     log.debug("the related object is published/imported and the relation is also published " +
                               "(we will just update the relation node)");
                     updateNodesAndRelations(localCloudInfo, rel, true, true);
                  }
                  else {
                     log.debug("the related object is published/imported and the relation is imported " +
                               "(Skipping, localCloud is not the owner of the relation node)");
                  }
               } else {
                  log.debug("the related object is published/imported but the relation is not yet published");
                  createNodeAndRelations(localCloudInfo, rel, remoteCloudInfo, true);
            }
         } else {
            log.debug("The related object is not published/imported skipping this relation");
         }
      }
   }

   public static void createPublishingInfo(
      CloudInfo localCloudInfo,
      Node localNode,
      CloudInfo remoteCloudInfo,
      Node remoteNode) {

      int localNumber = localNode.getNumber();
      int remoteNumber = remoteNode.getNumber();

      // create administatrive info ( which node has been published etc.)
      // in the source cloud
      Node admin = localCloudInfo.getCloud().getNodeManager(MMBASE_PUBLISH_MANAGER).createNode();
      admin.setIntValue(SOURCE_CLOUD, CloudInfo.getCloudNumberInRemoteCloud(localCloudInfo, localCloudInfo));
      admin.setIntValue(SOURCE_NUMBER, localNumber);
      admin.setIntValue(DESTINATION_CLOUD, CloudInfo.getCloudNumberInRemoteCloud(localCloudInfo, remoteCloudInfo));
      admin.setIntValue(DESTINATION_NUMBER, remoteNumber);
      admin.commit();

      // now create administatrive info in the destination cloud
      admin = remoteCloudInfo.getCloud().getNodeManager(MMBASE_PUBLISH_MANAGER).createNode();
      admin.setIntValue(SOURCE_CLOUD, CloudInfo.getCloudNumberInRemoteCloud(remoteCloudInfo, localCloudInfo));
      admin.setIntValue(SOURCE_NUMBER, localNumber);
      admin.setIntValue(DESTINATION_CLOUD, CloudInfo.getCloudNumberInRemoteCloud(remoteCloudInfo, remoteCloudInfo));
      admin.setIntValue(DESTINATION_NUMBER, remoteNumber);
      admin.commit();
   }

   /**
    * Test whether a node is published to one or more other clouds.
    * From the perspective of one cloud a node is said to be published when the node
    * number is available in the sourcenumber of the remotenodes table and the sourcecloud
    * equals the cloud context (so if the context is staging the sourcecloud should refer
    * the staging record in the cloud table)
    *
    * @param localNode The node to be checked
    * @return <code>true</code> if the node has been published to the other cloud
    */
   public static boolean isPublished(Node localNode) {
      return isPublished(CloudInfo.getDefaultCloudInfo(), localNode);
   }

   public static boolean isPublished(CloudInfo localCloudInfo, Node localNode) {
       return isPublished(localCloudInfo, localNode.getNumber());
    }

   /**
     * Test whether a node is published to one or more other clouds. From the perspective of one
     * cloud a node is said to be published when the node number is available in the sourcenumber of
     * the remotenodes table and the sourcecloud equals the cloud context (so if the context is
     * staging the sourcecloud should refer the staging record in the cloud table)
     * 
     * @param localCloudInfo the cloud from which the node has been published
     * @param localNumber the number of the node
     * @return <code>true</code> if the node has been published to the other cloud
     */
   public static boolean isPublished(CloudInfo localCloudInfo, int localNumber) {
        NodeManager nm = localCloudInfo.getCloud().getNodeManager(MMBASE_PUBLISH_MANAGER);

        NodeList nodeList = nm.getList("sourcenumber=" + localNumber + " AND sourcecloud="
                + CloudInfo.getCloudNumberInRemoteCloud(localCloudInfo, localCloudInfo), null, null);

        int nodeListSize = nodeList.size();
        if (nodeListSize == 0) {
            return false;
        } else {
            if (nodeListSize > 1) {
                log.debug("isPublished detected multiple remote nodes for node number{"
                        + localNumber + "} Node is published to multiple clouds.");
            }
        }
        return true;
    }

   /**
    * Test whether a node is published to another cloud.
    * From the perspective of one cloud a node is said to be published when the node
    * number is available in the sourcenumber of the remotenodes table and the sourcecloud
    * equals the cloud context (so if the context is staging the sourcecloud should refer
    * the staging record in the cloud table)
    *
    * @param localNode the node to test
    * @param remoteCloudInfo the cloud to which the node is published
    * @return <code>true</code> if node has been published to cloud
    */
   public static boolean isPublished(Node localNode, CloudInfo remoteCloudInfo) {
      return isPublished(CloudInfo.getDefaultCloudInfo(), localNode.getNumber(), remoteCloudInfo);
   }

   /**
     * Test whether a node is published to another cloud. From the perspective of one cloud a node
     * is said to be published when the node number is available in the sourcenumber of the
     * remotenodes table and the sourcecloud equals the cloud context (so if the context is staging
     * the sourcecloud should refer the staging record in the cloud table)
     * 
     * @param localCloudInfo the cloud from which the node has been published
     * @param localNumber the node number
     * @param remoteCloudInfo the cloud to which the node is published
     * @return <code>true</code> if node has been published to cloud
     */
   public static boolean isPublished(CloudInfo localCloudInfo, int localNumber, CloudInfo remoteCloudInfo) {
      NodeManager nm = localCloudInfo.getCloud().getNodeManager(MMBASE_PUBLISH_MANAGER);

      NodeList nl = nm.getList("sourcenumber=" + localNumber + " AND sourcecloud=" +
                               CloudInfo.getCloudNumberInRemoteCloud(localCloudInfo, localCloudInfo) +
                               " AND destinationcloud = " +
                               CloudInfo.getCloudNumberInRemoteCloud(localCloudInfo, remoteCloudInfo), null, null);

      int nodeListSize = nl.size();
      if (nodeListSize == 0) {
         return false;
      }
      else {
      if (nodeListSize > 1) {
         log.error("isPublished detected multiple remote nodes for node number{" +
                   localNumber + "} for the same remote cloud still  returning true.");
      }
      }
      return true;
   }

   /**
    * From the perspective of one cloud a node is said to be imported when the node
    * number is available in the destinationnumber of the remotenodes table and the
    * destinationcloud equals the cloud context (so if the context is staging the
    * destinationcloud should refer the staging record in the cloud table)
    *
    * @param localCloudInfo the cloud from which the node has been published
    * @param localNode  Node to test
    * @return true if node was imported, false otherwise
    */
    public static boolean isImported(CloudInfo localCloudInfo, Node localNode) {
       return isImported(localCloudInfo, localNode.getNumber());
    }

    /**
     * From the perspective of one cloud a node is said to be imported when the node
     * number is available in the destinationnumber of the remotenodes table and the
     * destinationcloud equals the cloud context (so if the context is staging the
     * destinationcloud should refer the staging record in the cloud table)
     * @param localCloudInfo the Cloud to which the node might be published
     * @param localNodeNumber  Node to test
     *
     * @return true if node was imported, false otherwise
     */
    public static boolean isImported(CloudInfo localCloudInfo, int localNodeNumber) {
       NodeManager nm = localCloudInfo.getCloud().getNodeManager(MMBASE_PUBLISH_MANAGER);

       NodeList nl = nm.getList("destinationnumber=" + localNodeNumber + " AND destinationcloud=" +
               CloudInfo.getCloudNumberInRemoteCloud(localCloudInfo, localCloudInfo), null, null);
       return nl.size() > 0;
    }
    
    
    /**
     * From the perspective of <b>the local cloud</b> a node is said to be imported when the node
     * number is available in the destinationnumber of the remotenodes table and the
     * destinationcloud equals the cloud context (so if the context is staging the
     * destinationcloud should refer the staging record in the cloud table)
     * 
     * @param localNode this localNode must come from local cloud.
     * @return is node imported
     */
    public static boolean isImported(Node localNode) {
        return isImported(CloudInfo.getDefaultCloudInfo(), localNode.getNumber());
     }
    
   /**
    * Tests whether the node has been published from another cloud to this cloud
    *
    * @param localNode the node to test
    * @param remoteCloudInfo the Cloud from which the node might be published
    * @return <cdeo>true</code> if node was imported from cloud
    */
   public static boolean isImported(Node localNode, CloudInfo remoteCloudInfo) {
      return isImported(CloudInfo.getDefaultCloudInfo(), localNode.getNumber(), remoteCloudInfo);
   }

   /**
    * Tests whether the node has been published from another cloud to this cloud
    * @param localCloudInfo the Cloud to which the node might be published
    * @param localNumber the node to test
    * @param remoteCloudInfo the Cloud from which the node might be published
    *
    * @return <cdeo>true</code> if node was imported from cloud
    */
   public static boolean isImported(CloudInfo localCloudInfo, int localNumber, CloudInfo remoteCloudInfo) {
      NodeManager nm = localCloudInfo.getCloud().getNodeManager(MMBASE_PUBLISH_MANAGER);
      NodeList nl = nm.getList("destinationnumber=" + localNumber +
              " AND destinationcloud=" + CloudInfo.getCloudNumberInRemoteCloud(localCloudInfo, localCloudInfo) +
              " AND sourcecloud=" + CloudInfo.getCloudNumberInRemoteCloud(localCloudInfo, remoteCloudInfo), null, null);
      return nl.size() > 0;

   }


   /**
     * Unlink node from all other nodes (means you can then edit/delete them) This method can also
     * be used to republish the node from the remoteCloud to this localCloud if the localNode is
     * deleted too.
     * 
     * @param localNode Node from which to remove the publish info
     */
    public static void unLinkNode(Node localNode) {
        CloudInfo localCloudInfo = CloudInfo.getDefaultCloudInfo(); 
        unLinkNode(localCloudInfo, localNode);
    }
   
   /**
     * Unlink node from all other nodes (means you can then edit/delete them) This method can also
     * be used to republish the node from the remoteCloud to this localCloud if the localNode is
     * deleted too.
     * 
     * @param localCloudInfo source cloud info which the source node got from
     * @param localNode Node from which to remove the publish info
     */
    public static void unLinkNode(CloudInfo localCloudInfo, Node localNode) {
        if (isPublished(localCloudInfo, localNode)) {
            CloudInfo sourceCloudInfo = localCloudInfo;
            Node sourceNode = localNode;
            Map<Integer, List<Node>> nodesListMap = getPublishedNodesIncludeCorrupt(sourceCloudInfo, sourceNode.getNumber());

            Iterator<Integer> cloudNumberIterator = nodesListMap.keySet().iterator();
            while (cloudNumberIterator.hasNext()) {
                int remoteCloudNumber = cloudNumberIterator.next();
                CloudInfo remoteCloudInfo = CloudInfo.getCloudInfo(sourceCloudInfo, remoteCloudNumber);
                Iterator<Node> remoteNodeIterator = nodesListMap.get(remoteCloudNumber).iterator();

                while (remoteNodeIterator.hasNext()) {
                    Node remoteNode = remoteNodeIterator.next();
                    unLinkNode(sourceCloudInfo, sourceNode, remoteCloudInfo, remoteNode);
                }
            }
        } else {
            if (isImported(localCloudInfo, localNode)) {
                CloudInfo destinationCloudInfo = localCloudInfo;
                Node destinationNode = localNode;
                //TODO: need to find the reference part to change the invoke name
                int destionationNumber = destinationNode.getNumber();
                CloudInfo sourceCloudInfo = getSourceCloud(destinationCloudInfo, destionationNumber);
                unLinkNode(sourceCloudInfo, destinationCloudInfo, destionationNumber);
            }
        }
    }

   /**
    * Remove the publish-info between a source node and a published version
    *
    * @param sourceCloudInfo source cloud from which the source node are published
    * @param sourceNode the original node
    * @param destinationCloudInfo destination cloud to which the destination node are published
    * @param destinationNode the published copy
    */
   public static void unLinkNode(CloudInfo sourceCloudInfo, Node sourceNode,
            CloudInfo destinationCloudInfo, Node destinationNode) {
      unLinkNode(sourceCloudInfo, destinationCloudInfo, destinationNode.getNumber());
   }
   
   /**
    * Remove the publish-info between a source node and a published version
    *
    * @param sourceCloudInfo
    * @param destinationCloudInfo
    * @param destinationNumber
    */
   public static void unLinkNode(CloudInfo sourceCloudInfo, CloudInfo destinationCloudInfo,
                                 int destinationNumber) {
       
      synchronized (publishLock) {
         // remove info in source cloud
         NodeList sourcenl = getSourceInfoNodes(sourceCloudInfo, destinationCloudInfo, destinationNumber);
         int sourcenlSize = sourcenl.size();
        // There shouldn't be more then one, but just clean up all records
         for(int i = 0; i < sourcenlSize; i++) {
            sourcenl.getNode(i).delete(true);
         }

         // remove info in destination cloud
         NodeList destnl = getDestinationNodes(sourceCloudInfo, destinationCloudInfo, destinationNumber);
         int destnlSize = destnl.size();
         // There shouldn't be more then one, but just clean up all records
         for(int i = 0; i < destnlSize; i++) {
            destnl.getNode(i).delete(true);
         }
      }
   }

   private static NodeList getDestinationNodes(CloudInfo sourceCloudInfo,
            CloudInfo destinationCloudInfo, int destinationNumber) {
        NodeManager destnm = destinationCloudInfo.getCloud().getNodeManager(MMBASE_PUBLISH_MANAGER);
         NodeList destnl = destnm.getList("destinationnumber=" + destinationNumber +
                 " AND sourcecloud=" + CloudInfo.getCloudNumberInRemoteCloud(destinationCloudInfo, sourceCloudInfo) +
                 " AND destinationcloud=" + CloudInfo.getCloudNumberInRemoteCloud(destinationCloudInfo, destinationCloudInfo),
                         null, null);
        return destnl;
   }

   private static NodeList getSourceInfoNodes(CloudInfo sourceCloudInfo,
        CloudInfo destinationCloudInfo, int destinationNumber) {
        NodeManager sourcenm = sourceCloudInfo.getCloud().getNodeManager(MMBASE_PUBLISH_MANAGER);
        NodeList sourcenl = sourcenm.getList("destinationnumber=" + destinationNumber +
                 " AND sourcecloud=" + CloudInfo.getCloudNumberInRemoteCloud(sourceCloudInfo, sourceCloudInfo) +
                 " AND destinationcloud=" + CloudInfo.getCloudNumberInRemoteCloud(sourceCloudInfo, destinationCloudInfo),
                         null, null);
        return sourcenl;
    }


   private static void updatePublishInfoNode(CloudInfo sourceCloudInfo, CloudInfo destinationCloudInfo,
           int destinationNumber) {

       synchronized (publishLock) {
           // remove info in source cloud
           NodeList sourcenl = getSourceInfoNodes(sourceCloudInfo, destinationCloudInfo, destinationNumber);
           int sourcenlSize = sourcenl.size();
          // There shouldn't be more then one, but just clean up all records
           for(int i = 0; i < sourcenlSize; i++) {
              sourcenl.getNode(i).commit();
           }
        }
    }

   
   /**
    * Get the published nodes from a remote cloud. What should be kept in mind is that
    * a node could also have been imported from the remote cloud. The query for retrieving
    * the 'published' node is dependent of this.
    *
    * @param localNode the node that has been published
    * @param remoteCloudInfo  the remote cloud
    * @return the remote node or null if not published
    */
   public static Node getPublishedNode(Node localNode, CloudInfo remoteCloudInfo) {
      CloudInfo localCloudInfo = CloudInfo.getDefaultCloudInfo();
      return getPublishedNode(localCloudInfo, localNode, remoteCloudInfo);
   }
   
   public static Node getPublishedNode(CloudInfo localCloudInfo, Node localNode, CloudInfo remoteCloudInfo) {
      NodeManager nm = localCloudInfo.getCloud().getNodeManager(MMBASE_PUBLISH_MANAGER);

      boolean isPublished = isPublished(localCloudInfo, localNode);

      NodeList nl = null;
      if (isPublished) {
          nl = nm.getList("sourcenumber = " + localNode.getNumber() +
                 " AND sourcecloud=" + CloudInfo.getCloudNumberInRemoteCloud(localCloudInfo, localCloudInfo) +
                 " AND destinationcloud=" + CloudInfo.getCloudNumberInRemoteCloud(localCloudInfo, remoteCloudInfo),
                 null, null);
       }
       else {
          // maybe imported. Don't check if it is imported. We will return null if it is not.
          nl = nm.getList("destinationnumber = " + localNode.getNumber() +
                 " AND sourcecloud=" + CloudInfo.getCloudNumberInRemoteCloud(localCloudInfo, remoteCloudInfo) +
                 " AND destinationcloud=" + CloudInfo.getCloudNumberInRemoteCloud(localCloudInfo, localCloudInfo),
                 null, null);
       }

      int size = nl.size();
      if (size == 0) {
         log.debug("search for node in live environment returned an empty list");
         return null;
      }

      if (size > 1) {
         log.error("getPublishedNode detected multiple remote nodes for node number{" +
                   localNode.getNumber() + "} in the same cloud, returning the first one in the list.");
      }
      if (isPublished) {
         return remoteCloudInfo.getCloud().getNode(nl.getNode(0).getIntValue(DESTINATION_NUMBER));
      }
      return remoteCloudInfo.getCloud().getNode(nl.getNode(0).getIntValue(SOURCE_NUMBER));
   }

   /**
    * Get the published nodes from all remote clouds
    *
    * @param localNode The node thas has been published
    * @return List of all remote nodes
    */
   public static Map<Integer,Node> getPublishedNodes(Node localNode) {
      return getPublishedNodes(CloudInfo.getDefaultCloudInfo(), localNode.getNumber());
   }

   /**
    * Get the published nodes from all remote clouds
    *
    * @param localCloudInfo The source cloud
    * @param localNumber The node number that has been published
    * @return List of all remote nodes
    */
   public static Map<Integer,Node> getPublishedNodes(CloudInfo localCloudInfo, int localNumber) {
      NodeList publishInfoNodes = getPublishInfoNodes(localCloudInfo, localNumber);
      return getPublishedNodes(publishInfoNodes);
   }

   /**
    * Get the published nodes from all remote clouds
    *
    * @param publishInfoNodes The publish Info Nodes
    * @return List of all remote nodes
    */
   public static Map<Integer, Node> getPublishedNodes(NodeList publishInfoNodes) {
      NodeIterator ni = publishInfoNodes.nodeIterator();
      Map<Integer,Node> returnMap = new HashMap<Integer,Node>();
      while (ni.hasNext()) {
         Node admin = ni.nextNode();
         int destinationCloudNumber = admin.getIntValue(DESTINATION_CLOUD);
         if (returnMap.containsKey(destinationCloudNumber)) {
             log.error(
                     "Detected multiple instances of a publish-info node, but it should only be one " +
                     "in all xases (published from or imported to the local cloud). " +
                     "still returning the first node");
         }
         Node remotePublishedNode = getRemoteNode(admin);
         returnMap.put(destinationCloudNumber, remotePublishedNode);
      }

      return returnMap;
    }
    
   /**
    * Get the published node numbers from all remote clouds
    *
    * @param localNode The node thas has been published
    * @return List of all remote nodes
    */
   public static Map<Integer,Integer> getPublishedNodeNumbers(Node localNode) {
      return getPublishedNodeNumbers(CloudInfo.getDefaultCloudInfo(), localNode.getNumber());
   }

   /**
    * Get the published node numbers from all remote clouds
    *
    * @param localCloudInfo The source cloud
    * @param localNumber The node number that has been published
    * @return List of all remote nodes
    */
   public static Map<Integer,Integer> getPublishedNodeNumbers(CloudInfo localCloudInfo, int localNumber) {
      NodeList publishInfoNodes = getPublishInfoNodes(localCloudInfo, localNumber);
      return getPublishedNodeNumbers(publishInfoNodes);
   }

   /**
    * Get the published node numbers from all remote clouds
    *
    * @param publishInfoNodes The publish Info Nodes
    * @return List of all remote nodes
    */
   public static Map<Integer, Integer> getPublishedNodeNumbers(NodeList publishInfoNodes) {
      NodeIterator ni = publishInfoNodes.nodeIterator();
      Map<Integer, Integer> returnMap = new HashMap<Integer,Integer>();
      while (ni.hasNext()) {
         Node admin = ni.nextNode();
         int destinationCloudNumber = admin.getIntValue(DESTINATION_CLOUD);
         if (returnMap.containsKey(destinationCloudNumber)) {
             log.error(
                     "Detected multiple instances of a publish-info node, but it should only be one " +
                     "in all xases (published from or imported to the local cloud). " +
                     "still returning the first node");
         }
         int destinationNodeNumber = admin.getIntValue(DESTINATION_NUMBER);
         returnMap.put(destinationCloudNumber, destinationNodeNumber);
      }

      return returnMap;
    }
   
   
    private static Node getRemoteNode(Node admin) {
        int destinationCloudNumber = admin.getIntValue(DESTINATION_CLOUD);
        CloudInfo remoteCloudInfo = CloudInfo.getCloudInfo(destinationCloudNumber);
        Node remotePublishedNode = remoteCloudInfo.getCloud().getNode(admin.getIntValue(DESTINATION_NUMBER));
        return remotePublishedNode;
    }

   public static Collection<Integer> getPublishedClouds(Node localNode) {
       return getPublishedClouds(CloudInfo.getDefaultCloudInfo(), localNode.getNumber());
   }

   public static Collection<Integer> getPublishedClouds(CloudInfo localCloudInfo, int localNumber) {
       NodeIterator ni = getPublishInfoNodes(localCloudInfo, localNumber).nodeIterator();
       Collection<Integer> clouds = new HashSet<Integer>();

       while (ni.hasNext()) {
           Node admin = ni.nextNode();
           clouds.add(admin.getIntValue(DESTINATION_CLOUD));
        }
        return clouds;
   }


   /**
    * Get the source node of this localNode if this localNode is imported
    *
    * @param localNode a published node
    * @return the remoteNode from which the localNode was published
    *         or null if node was not imported
    */
   public static Node getSourceNode(Node localNode) {
       CloudInfo localCloudInfo = CloudInfo.getDefaultCloudInfo();
      synchronized (publishLock) {
         Node adminNode = getPublishInfoNode(localCloudInfo, localNode.getNumber(), localCloudInfo);

         if (adminNode == null) {
            return null;
         }

         Cloud sourceCloud = CloudManager.getCloud(localNode.getCloud(), adminNode.getIntValue(SOURCE_CLOUD));
         Node sourceNode = null;
         try {
            sourceNode = sourceCloud.getNode(adminNode.getIntValue(SOURCE_NUMBER));
         } catch (NotFoundException nfe) {
             log.debug(Logging.stackTrace(nfe));
         }
         return sourceNode;
      }
   }

   /**
    * Get the source node number of this localNode if this localNode is imported
    *
    * @param localNode a published node
    * @return the remoteNode number from which the localNode was published
    *         or -1 if node was not imported
    */
   public static int getSourceNodeNumber(Node localNode) {
      CloudInfo localCloudInfo = CloudInfo.getDefaultCloudInfo();
      synchronized (publishLock) {
         Node adminNode = getPublishInfoNode(localCloudInfo, localNode.getNumber(), localCloudInfo);

         if (adminNode == null) {
            return -1;
         }
         return adminNode.getIntValue(SOURCE_NUMBER);
      }
   }

   
   /**
    * Gets the source cloud of this destination node
    * @param destinationCloudInfo destination cloud to which the destination node are published
    * @param destinationNumber a published node
    * @return the sourceCloud from which the destinationNode was published
    *         or null if node was not imported
    */
   private static CloudInfo getSourceCloud(CloudInfo destinationCloudInfo, int destinationNumber) {
      Node adminNode = getPublishInfoNode(destinationCloudInfo, destinationNumber, destinationCloudInfo);

      if (adminNode == null) {
         return null;
      }

      return CloudInfo.getCloudInfo(destinationCloudInfo, adminNode.getIntValue(SOURCE_CLOUD));
   }


   public static NodeList getPublishInfoNodes(CloudInfo localCloudInfo, int localNumber) {
      synchronized (publishLock) {
         NodeManager nm = localCloudInfo.getCloud().getNodeManager(MMBASE_PUBLISH_MANAGER);
         return nm.getList("sourcenumber = " + localNumber +
                 " AND sourcecloud=" + CloudInfo.getCloudNumberInRemoteCloud(localCloudInfo, localCloudInfo),
                 null, null);
      }
   }

   public static int getDestinationNumber(CloudInfo localCloudInfo, CloudInfo destinationCloudInfo, int localNumber) {
      synchronized (publishLock) {
         NodeManager nm = localCloudInfo.getCloud().getNodeManager(MMBASE_PUBLISH_MANAGER);
         NodeList nl = nm.getList("sourcenumber = " + localNumber +
                 " AND sourcecloud=" + CloudInfo.getCloudNumberInRemoteCloud(localCloudInfo, localCloudInfo) +
                 " AND destinationcloud=" + destinationCloudInfo.getNumber(),
                              null, null);
        if (nl.isEmpty()) {
           return -1;
        }
        return nl.getNode(0).getIntValue(DESTINATION_NUMBER);
      }
   }

   /**
    *
    * @param sourceCloudInfo
    * @param destinationNumber
    * @param destinationCloudInfo
    * @return node with published information
    */
   public static Node getPublishInfoNode(CloudInfo sourceCloudInfo, int destinationNumber, CloudInfo destinationCloudInfo) {
      synchronized (publishLock) {
         NodeManager nm = sourceCloudInfo.getCloud().getNodeManager(MMBASE_PUBLISH_MANAGER);

         NodeList nl = nm.getList("destinationnumber = " + destinationNumber
                    + " AND destinationcloud="
                    + CloudInfo.getCloudNumberInRemoteCloud(sourceCloudInfo, destinationCloudInfo),
                    null, null);

         int size = nl.size();
         if (size == 0) {
            return null;
         }

         if (size > 1) {
            log.error(
               "Detected multiple instances of a publish-info node, but it should only be one " +
               "in all xases (published from or imported to the local cloud). " +
               "still returning the first node");
         }

         return nl.getNode(0);
      }
   }

   /**
    * syncronize all nodes that are published from this one
    *
    * @param localCloudInfo the cloud from which the local node are published
    * @param localNode the source node
    * @throws PublishException - when publication fails
    **/
   public static void updateNodesAndRelations(CloudInfo localCloudInfo, Node localNode) throws PublishException {
       updateNodesAndRelations(localCloudInfo, localNode, true, true, null);
   }


   /**
    * syncronize all nodes that are published from this one
    *
    * @param localCloudInfo the cloud from which the local node are published
    * @param localNode the source node
    * @param updateNode sync the node
    * @param updateRelations sync the relations to other nodes
    * @throws PublishException - when publication fails
    **/
   public static void updateNodesAndRelations(CloudInfo localCloudInfo, Node localNode,
           boolean updateNode, boolean updateRelations) throws PublishException {
       updateNodesAndRelations(localCloudInfo, localNode, updateNode, updateRelations, null);
   }
   
   /**
    * syncronize all nodes that are published from this one
    *
    * @param localCloudInfo the cloud from which the local node are published
    * @param localNode the source node
    * @param updateNode sync the node
    * @param updateRelations sync the relations to other nodes
    * @param relatedNodes relations between localnode and these numbers should be updated
    * @throws PublishException - when publication fails
    **/
   public static void updateNodesAndRelations(CloudInfo localCloudInfo, Node localNode,
           boolean updateNode, boolean updateRelations, List<Integer> relatedNodes) throws PublishException {
       
      synchronized (publishLock) {
         if (!isPublished(localCloudInfo, localNode)) {
            return;
         }
         NodeList publishInfoNodes = getPublishInfoNodes(localCloudInfo, localNode.getNumber());
         List<Integer> updatedAfterTimestamp = new ArrayList<Integer>();
         if (updateNode) {
             Date lastmodified = getLastModifiedDate(localNode);
             for (Iterator<Node> iterator = publishInfoNodes.iterator(); iterator.hasNext();) {
                Node publishInfoNode = iterator.next();
                Date timestamp = publishInfoNode.getDateValue(TIMESTAMP);
                if (lastmodified.after(timestamp)) {
                    updatedAfterTimestamp.add(localNode.getNumber());
                }
             }
         }
         
         // simple check to prevent remote calls while nothing has to be done
         if (updateRelations || !updatedAfterTimestamp.isEmpty()) {
             Map<Integer,Node> publishedNodesMap = getPublishedNodes(publishInfoNodes);
             Iterator<Integer> cloudNumberIterator = publishedNodesMap.keySet().iterator();
    
             while(cloudNumberIterator.hasNext()) {
                 int remoteCloudNumber = cloudNumberIterator.next();
                 CloudInfo remoteCloudInfo = CloudInfo.getCloudInfo(remoteCloudNumber);
                 Node remoteNode = publishedNodesMap.get(remoteCloudNumber);
    
                 if (updateNode && updatedAfterTimestamp.contains(localNode.getNumber())) {
                     syncNode(localCloudInfo, localNode, remoteCloudInfo, remoteNode);    
                 }
                 if (updateRelations) {
                     if (relatedNodes != null && relatedNodes.size() > 0) {
                         syncRelations(localCloudInfo, localNode, remoteCloudInfo, remoteNode, relatedNodes);
                     }
                     else {
                         syncRelations(localCloudInfo, localNode, remoteCloudInfo, remoteNode);
                     }
                 }
             }
          }
      }
   }

    private static Date getLastModifiedDate(Node localNode) {
        String fieldname = getFieldNameWithDatatype(localNode, Field.TYPE_DATETIME, "lastmodified");
        if (fieldname != null) {
            return localNode.getDateValue(fieldname);
        }
        return new Date();
    }

    private static String getFieldNameWithDatatype(Node localNode, int fieldType,
            String datatypeName) {
        String fieldname = null;
        for (Iterator<Field> iterator = localNode.getNodeManager().getFields().iterator(); iterator.hasNext();) {
            Field field = iterator.next();
            if (field.getType() == fieldType) {
                DataType dtype = field.getDataType();
                while (dtype != null && !datatypeName.equals(dtype.getName())) {
                    dtype = dtype.getOrigin();
                }
                if (dtype != null) {
                    fieldname = field.getName();
                }
            }
        }
        return fieldname;
    }

    private static void syncNodeAndRelations(CloudInfo localCloudInfo, Node localNode,
            CloudInfo remoteCloudInfo, Node remoteNode) throws PublishException {
        if (remoteNode != null) {
            syncNode(localCloudInfo, localNode, remoteCloudInfo, remoteNode);
            syncRelations(localCloudInfo, localNode, remoteCloudInfo, remoteNode);
        }
    }

    public static void syncNode(CloudInfo localCloudInfo, Node localNode,
            CloudInfo remoteCloudInfo, Node remoteNode) {
        if (remoteNode != null) {
            syncFields(localCloudInfo, localNode, remoteCloudInfo, remoteNode);
            
            boolean aliasChanged = syncAliasses(localNode, remoteNode);
            if (aliasChanged || remoteNode.isChanged()) {
                remoteNode.commit();
                updatePublishInfoNode(localCloudInfo, remoteCloudInfo, remoteNode.getNumber());
            }
        }
    }

    public static void updatePublishedNodeAndRelations(CloudInfo localCloudInfo, Node localNode, CloudInfo remoteCloudInfo) throws PublishException {
       synchronized (publishLock) {
           if (!isPublished(localCloudInfo, localNode)) {
               return;
            }

           Node remoteNode = getPublishedNode(localCloudInfo, localNode, remoteCloudInfo);
           syncNodeAndRelations(localCloudInfo, localNode, remoteCloudInfo, remoteNode);
       }
   }

   private static void syncFields(CloudInfo localCloudInfo, Node localNode, CloudInfo remoteCloudInfo, Node remoteNode) {
      NodeManager nm = localNode.getNodeManager();
      FieldIterator fi = nm.getFields().fieldIterator();

      while (fi.hasNext()) {
         Field field = fi.nextField();
         String fieldName = field.getName();

         if (!(fieldName.equals("owner") ||
               fieldName.equals("number") ||
               fieldName.equals("otype") ||
               (fieldName.indexOf("_") == 0) ||
               fieldName.equals("snumber") ||
               fieldName.equals("dir") ||
               fieldName.equals("dnumber") ||
               fieldName.equals("rnumber"))) {
             
             int fieldType = field.getType();
             if (fieldType == Field.TYPE_BINARY) {
                 syncBinaryField(localNode, remoteNode, fieldName);
             }
             else {
                 cloneNodeField(localCloudInfo, localNode, remoteCloudInfo, remoteNode, field);
             }
         }
      }
   }

   private static void syncBinaryField(Node sourceNode, Node destinationNode, String fieldName) {
       boolean syncField = true;
       String checksumField = getFieldNameWithDatatype(sourceNode, Field.TYPE_STRING, "checksum");
       if (checksumField != null) {
           String localChecksum = sourceNode.getStringValue(checksumField);
           try {
               String remoteChecksum = destinationNode.getStringValue(checksumField);
               if ((localChecksum == null && remoteChecksum == null)
                       || (localChecksum != null && localChecksum.equals(remoteChecksum))) {
                   syncField = false;
               }
           }
           catch (Throwable t) {
               // The remote side could throw an exception when the field does not exist.
               log.debug("Errror while remote checksum check. Node " + destinationNode.getNumber() + " field " + fieldName, t);
           }
       }
       if (syncField) {
           destinationNode.setValueWithoutProcess(fieldName, sourceNode
                   .getValueWithoutProcess(fieldName));
       }
   }

   
   private static boolean syncAliasses(Node localNode, Node remoteNode) {
      StringList list = localNode.getAliases();
      StringList outList = remoteNode.getAliases();
      boolean aliasChanged = false;
      
      int outListSize = outList.size();
      for (int x = 0; x < outListSize; x++) {
         String remoateAlias = outList.getString(x);
         if (!list.contains(remoateAlias)) {
            remoteNode.deleteAlias(remoateAlias);
            aliasChanged = true;
         }
      }

      int listSize = list.size();
      for (int x = 0; x < listSize; x++) {
         String localAlias = list.getString(x);
         if (!outList.contains(localAlias)) {
            remoteNode.createAlias(localAlias);
            aliasChanged = true;
         }
      }
      return aliasChanged;
   }

   public static void syncRelations( CloudInfo localCloudInfo, Node localNode, CloudInfo remoteCloudInfo, Node remoteNode) throws PublishException {
       if (localNode.getNodeManager().getAllowedRelations().isEmpty()) {
           return;
       }
       if (remoteNode != null) {
          RelationList remoteRelationList = remoteNode.getRelations();
          removeRemoteRelationWhenLocalRemoved(localCloudInfo, remoteCloudInfo, remoteRelationList);
          cloneRelations(localCloudInfo, localNode, remoteCloudInfo);
       }
   }
   
   public static void syncRelations( CloudInfo localCloudInfo, Node localNode, CloudInfo remoteCloudInfo,
           Node remoteNode, List<Integer> relatedNodes) throws PublishException {
       if (localNode.getNodeManager().getAllowedRelations().isEmpty()) {
           return;
       }
       if (remoteNode != null) {
           String remoteNumbers = "";
           for (Integer localNumber : relatedNodes) {
               int remoteNumber = getDestinationNumber(localCloudInfo, remoteCloudInfo, localNumber);
               if (remoteNumber > -1) {
                   remoteNumbers += (remoteNumbers.length() > 0) ? "," + remoteNumber : remoteNumber; 
               }
           }
           if (remoteNumbers.length() > 0) {
              List<Node> remoteRelationList = new ArrayList<Node>();
              
              NodeManager insrel = remoteCloudInfo.getCloud().getNodeManager("insrel");
              NodeList relations = insrel.getList("snumber = " + remoteNode.getNumber() + " AND dnumber in (" + remoteNumbers + ")" , null, null);
              remoteRelationList.addAll(relations);
              relations = insrel.getList("dnumber = " + remoteNode.getNumber() + " AND snumber in (" + remoteNumbers + ")" , null, null);
              remoteRelationList.addAll(relations);
              
              String localNumbers = "";
              for (Integer localNumber : relatedNodes) {
                  localNumbers += (localNumbers.length() > 0) ? "," + localNumber : localNumber;
              }
              
              List<Relation> localRelationList = new ArrayList<Relation>();
              NodeManager insrellocal = localCloudInfo.getCloud().getNodeManager("insrel");
              NodeList relationslocal = insrellocal.getList("snumber = " + localNode.getNumber() + " AND dnumber in (" + localNumbers + ")" , null, null);
              for (Iterator<Node> iterator = relationslocal.iterator(); iterator.hasNext();) {
                 Node localRel = iterator.next();
                 localRelationList.add(localRel.toRelation());
              }
              relationslocal = insrellocal.getList("dnumber = " + localNode.getNumber() + " AND snumber in (" + localNumbers + ")" , null, null);
              for (Iterator<Node> iterator = relationslocal.iterator(); iterator.hasNext();) {
                  Node localRel = iterator.next();
                  localRelationList.add(localRel.toRelation());
              }
              
              removeRemoteRelationWhenLocalRemoved(localCloudInfo, remoteCloudInfo, remoteRelationList);
              cloneRelations(localCloudInfo, localNode, remoteCloudInfo, localRelationList);
           }
       }
   }
   
   
   

    private static void removeRemoteRelationWhenLocalRemoved(CloudInfo localCloudInfo,
            CloudInfo remoteCloudInfo, List<? extends Node> remoteRelationList) {
        int size = remoteRelationList.size();
          for (int x = 0; x < size; x++) {
             Node remoteRelation = remoteRelationList.get(x);
       
             Node adminNode = getPublishInfoNode(localCloudInfo, remoteRelation.getNumber(), remoteCloudInfo);
             if (adminNode != null) {
                boolean deleted = false;
                try {
                   Node sourceNode = localCloudInfo.getCloud().getNode(adminNode.getIntValue(SOURCE_NUMBER));
                   if (sourceNode == null) {
                      deleted = true;
                   }
       
                } catch(NotFoundException nfe) {
                   deleted = true;
                }
                if (deleted) {
                   if (log.isDebugEnabled()) {
                      //getNumber is a rmi call to the other server don't want that unless debugging
                      log.debug("found publishinginfo for remote relation " + remoteRelation.getNumber() +
                                ", but the node is deleted. localCloud is onwer. Unlink and delete remoteRelation");
                   }
                   deleteNodeFromRemoteCloud(localCloudInfo, remoteRelation, remoteCloudInfo);
                }
             }
          }
    }

   /**
    * delete the remote instances of a node(with relations and relation information)
    * @param localNode the local node to remove
    */
   public static void deletePublishedNode(Node localNode) {
      deletePublishedNode(CloudInfo.getDefaultCloudInfo(), localNode.getNumber());
   }

   /**
    * delete the remote instances of a node(with relations and relation information)
    *
    * @param localCloudInfo the cloud from which the local node are published
    * @param localCloud
    * @param localNumber
    */
   public static void deletePublishedNode(CloudInfo localCloudInfo, int localNumber) {
      log.debug("deletePublishedNode called on node " + localNumber);

      if (isPublished(localCloudInfo, localNumber)) {
         NodeList publishInfoNodes = getPublishInfoNodes(localCloudInfo, localNumber);

         Map<Integer, List<Node>> publishedNodesMap = getPublishedNodesIncludeCorrupt(publishInfoNodes);
         log.debug("the node is published on " + publishedNodesMap.size() + " clouds");

         Iterator<Integer> cloudNumberIterator = publishedNodesMap.keySet().iterator();
         while (cloudNumberIterator.hasNext()) {
             int remoteCloudNumber = cloudNumberIterator.next();
             CloudInfo remoteCloudInfo = CloudInfo.getCloudInfo(remoteCloudNumber);
             Iterator<Node> remoteNodeIterator = publishedNodesMap.get(remoteCloudNumber).iterator();

            while (remoteNodeIterator.hasNext()) {
               Node remoteNode = remoteNodeIterator.next();
               if (log.isDebugEnabled()) {
                  //getNumber is a rmi call to the other server don't want that unless debugging
                  log.debug("the published node has number " + remoteNode.getNumber());
               }
               deleteNodeFromRemoteCloud(localCloudInfo, remoteNode, remoteCloudInfo);
            }
         }
     } else {
         log.debug("deletePublishedNode called on unpublished node " + localNumber);
      }
   }

   /**
    * Get the published nodes from all remote clouds
    *
    * @param localCloudInfo The source cloud
    * @param localNumber The node number that has been published
    * @return List of all remote nodes
    */
   public static Map<Integer,List<Node>> getPublishedNodesIncludeCorrupt(CloudInfo localCloudInfo, int localNumber) {
      NodeList publishInfoNodes = getPublishInfoNodes(localCloudInfo, localNumber);
      return getPublishedNodesIncludeCorrupt(publishInfoNodes);
   }
   
   /**
    * Get the published nodes from all remote clouds
    *
    * @param publishInfoNodes The publish Info Nodes
    * @return List of all remote nodes
    */
   private static Map<Integer, List<Node>> getPublishedNodesIncludeCorrupt(NodeList publishInfoNodes) {
      NodeIterator ni = publishInfoNodes.nodeIterator();
      Map<Integer,List<Node>> returnMap = new HashMap<Integer,List<Node>>();
      while (ni.hasNext()) {
         Node admin = ni.nextNode();
         int destinationCloudNumber = admin.getIntValue(DESTINATION_CLOUD);
         List<Node> publishNodesList = returnMap.get(destinationCloudNumber);
         if (publishNodesList==null) {
             publishNodesList = new ArrayList<Node>();
             returnMap.put(destinationCloudNumber, publishNodesList);
         }
         Node remotePublishedNode = getRemoteNode(admin);
         publishNodesList.add(remotePublishedNode);
      }

      return returnMap;
    }
   
   private static void deleteNodeFromRemoteCloud(CloudInfo localCloudInfo, Node remoteNode, CloudInfo remoteCloudInfo) {
      //get a list of relations from the remote node
      //since the local node may not be present any more
      RelationIterator ri = remoteNode.getRelations().relationIterator();

      if (!ri.hasNext()) {
         log.debug("the published node has no relations");
      }

      while (ri.hasNext()) {
         Relation remoteRelation = ri.nextRelation();

         // here we search for publish-info which was added by the local cloud
         // when publishing the node. When node is imported then no publish-info is returned
         Node adminNode = getPublishInfoNode(localCloudInfo,
               remoteRelation.getNumber(), remoteCloudInfo);

         if (adminNode == null) {
            if (log.isDebugEnabled()) {
               //getNumber is a rmi call to the other server don't want that unless debugging
               log.debug("no publishinformation known about node + " + remoteRelation.getNumber());
            }
         } else {
            unLinkNode(localCloudInfo, remoteCloudInfo, remoteRelation.getNumber());
         }
         // Always delete the remote relation to keep the remote cloud stable
         // even if it is an imported relation
         remoteRelation.delete();
      }

      unLinkNode(localCloudInfo, remoteCloudInfo, remoteNode.getNumber());
      remoteNode.delete();
   }

    public static void unLinkImportedNode(int number) {
        CloudInfo localCloudInfo = CloudInfo.getDefaultCloudInfo();
        CloudInfo sourceCloudInfo = getSourceCloud(localCloudInfo, number);
        unLinkNode(sourceCloudInfo, localCloudInfo, number);
    }

}
