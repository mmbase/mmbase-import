/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.versioning;

import nl.leocms.util.*;

import org.mmbase.bridge.*;

import org.mmbase.util.logging.*;

import java.util.List;

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
 * @version $Revision: 1.2 $
 */
public class PublishManager extends VersioningBase {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(PublishManager.class.getName());
   private static String MMBASE_PUBLISH_MANAGER = "remotenodes";

   /**
    * Publish a node to a different cloud, keeping records This will also publish
    * all relations inNode has with other published nodes
    *
    * @param localNode the node to be published
    * @param remoteCloud the external cloud
    * @return  the published copy of inNode
    */
   public static Node publishNode(Node localNode, Cloud remoteCloud)
      throws PublishException {
      synchronized (versionLock) {
         log.debug("publishNode called with node (number,type)" +
                   localNode.getNumber() + "," + localNode.getNodeManager().getName() + ")");

         NodeManager nm = localNode.getNodeManager();

         if (nm.getName().equals(MMBASE_PUBLISH_MANAGER)) {
            throw new PublishException("Cannot publish publishing info!");
         }

         Cloud localCloud = localNode.getCloud();
         if (isPublished(localNode, remoteCloud) || isImported(localNode, remoteCloud)) {
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
            Node source = localCloud.getNode(localNode.getStringValue("snumber"));
            Node destination = localCloud.getNode(localNode.getStringValue("dnumber"));
            if (!( (isPublished(source) && (isPublished(destination) || isImported(destination))) ||
                   (isImported(source) && (isPublished(destination) || isImported(destination))) ) ) {
               throw new PublishException("Attempt to publish relation between unpublished nodes or of unpublished type");
            }
         }

         // copy the node to the remote cloud
         Node remoteNode = cloneNode(localNode, remoteCloud);

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
         createPublishingInfo(localCloud, localNode, remoteCloud, remoteNode);
         cloneRelations(localNode, remoteCloud);

         return remoteNode;
      }
   }

   /**
    * Clone a node to a cloud, including any fields without keeping administrative information
    *
    * @param localNode the node to clone
    * @param remoteCloud the cloud to clone the node to
    * @return the newly created node in the other cloud
    */
   protected static Node cloneNode(Node localNode, Cloud remoteCloud) {
      if (isRelation(localNode)) {
         return cloneRelation(localNode, remoteCloud);
      }
      else {
         synchronized (versionLock) {
            NodeManager localNodeManager = localNode.getNodeManager();
            NodeManager remoteNodeManager = remoteCloud.getNodeManager(localNodeManager.getName());
            Node remoteNode = remoteNodeManager.createNode();

            FieldIterator fields = localNodeManager.getFields().fieldIterator();
            while (fields.hasNext()) {
               Field field = fields.nextField();
               String fieldName = field.getName();

               if (!(fieldName.equals("owner") || fieldName.equals("number") ||
                     fieldName.equals("otype") ||
                     (fieldName.indexOf("_") == 0))) {
                  cloneNodeField(localNode, remoteNode, field);
               }
            }
            remoteNode.commit();

            cloneAliasses(localNode, remoteNode);
            return remoteNode;
         }
      }
   }

   private static Node cloneRelation(Node localRelation, Cloud remoteCloud) {
      synchronized (versionLock) {
         Node relationTypeNode = localRelation.getNodeValue("rnumber");
         String relName = relationTypeNode.getStringValue("sname");
         Node remoteSourceNode = getPublishedNode(localRelation.getNodeValue("snumber"), remoteCloud);
         Node remoteDestinationNode = getPublishedNode(localRelation.getNodeValue("dnumber"), remoteCloud);
         
         RelationManager remoteRelationManager = 
               remoteCloud.getRelationManager(remoteSourceNode.getNodeManager().getName(),
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
               cloneNodeField(localRelation, remoteRelation, field);
            }
         }
         remoteRelation.commit();
         
         cloneAliasses(localRelation, remoteRelation);
         return remoteRelation;
      }
   }

   private static void cloneAliasses(Node localNode, Node remoteNode) {
      StringList list = localNode.getAliases();
      for (int x = 0; x < list.size(); x++) {
         remoteNode.createAlias(list.getString(x));
      }
   }

   private static void cloneRelations(Node localNode, Cloud remoteCloud) throws PublishException {
      RelationIterator ri = localNode.getRelations().relationIterator();
      if (ri.hasNext()) {
         log.debug("the local node has relations");
      }
      while (ri.hasNext()) {
         Relation rel = ri.nextRelation();
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
         if (isPublished(relatedNode, remoteCloud) || isImported(relatedNode, remoteCloud)) {
               // relatedNode is published to this localCloud or is imported from the remoteCloud
               // check whether the relation is published or imported too
               // if so update the relation else publish a new relation
               if (isPublished(rel, remoteCloud) || isImported(rel, remoteCloud)) {
                  if (isPublished(rel, remoteCloud)) {
                     log.debug("the related object is published/imported and the relation is also published " +
                               "(we will just update the relation node)");
                     updatePublishedNodes(rel.getCloud().getNode(rel.getNumber()));
                  }
                  else {
                     log.debug("the related object is published/imported and the relation is imported " +
                               "(Skipping, localCloud is not the owner of the relation node)");
                  }
               } else {
                  log.debug("the related object is published/imported but the relation is not yet published");
                  publishNode(rel.getCloud().getNode(rel.getNumber()), remoteCloud);
            }
         } else {
            log.debug("The related object is not published/imported skipping this relation");
         }
      }
   }

   private static void createPublishingInfo(
      Cloud localCloud,
      Node localNode,
      Cloud remoteCloud,
      Node remoteNode) {
         
      int localNumber = localNode.getNumber();
      int remoteNumber = remoteNode.getNumber();
      
      // create administatrive info ( which node has been published etc.)
      // in the source cloud
      /* hh remote publishing not used 
      Node admin = localCloud.getNodeManager(MMBASE_PUBLISH_MANAGER).createNode();
      admin.setIntValue("sourcecloud",CloudManager.getCloudNumber(localCloud, localCloud));
      admin.setIntValue("sourcenumber", localNumber);
      admin.setIntValue("destinationcloud", CloudManager.getCloudNumber(localCloud, remoteCloud));
      admin.setIntValue("destinationnumber", remoteNumber);
      admin.setIntValue("timestamp", (int) (System.currentTimeMillis() / 1000));
      admin.commit();
      */
      
      // now create administatrive info in the destination cloud
      /* hh remote publishing not used 
      admin = remoteCloud.getNodeManager(MMBASE_PUBLISH_MANAGER).createNode();
      admin.setIntValue("sourcecloud", CloudManager.getCloudNumber(remoteCloud, localCloud));
      admin.setIntValue("sourcenumber", localNumber);
      admin.setIntValue("destinationcloud", CloudManager.getCloudNumber(remoteCloud, remoteCloud));
      admin.setIntValue("destinationnumber", remoteNumber);
      admin.setIntValue("timestamp", (int) (System.currentTimeMillis() / 1000));
      admin.commit();
      */
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
      return isPublished(localNode.getNumber(), localNode.getCloud());
   }

   /**
    * Test whether a node is published to one or more other clouds.
    * From the perspective of one cloud a node is said to be published when the node
    * number is available in the sourcenumber of the remotenodes table and the sourcecloud
    * equals the cloud context (so if the context is staging the sourcecloud should refer
    * the staging record in the cloud table)
    *
    * @param localNumber the number of the node
    * @param localCloud the cloud from which the node has been published
    * @return <code>true</code> if the node has been published to the other cloud
    */
   public static boolean isPublished(int localNumber, Cloud localCloud) {

      /* hh remote publishing not used 

      NodeManager nm = localCloud.getNodeManager(MMBASE_PUBLISH_MANAGER);

      NodeList nl = nm.getList("sourcenumber=" + localNumber + " AND sourcecloud=" +
                    CloudManager.getCloudNumber(localCloud, localCloud), null, null);

      if (nl.size() == 0) {
         return false;
      }
      else {
         if (nl.size() > 1) {
           log.debug("isPublished detected multiple remote nodes for node number{" + localNumber +
                     "} Node is published to multiple clouds.");
         }
      }
      */
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
    * @param remoteCloud the cloud to which the node is published
    * @return <code>true</code> if node has been published to cloud
    */
   public static boolean isPublished(Node localNode, Cloud remoteCloud) {
      return isPublished(localNode.getNumber(), localNode.getCloud(), remoteCloud);
   }

   /**
    * Test whether a node is published to another cloud.
    * From the perspective of one cloud a node is said to be published when the node
    * number is available in the sourcenumber of the remotenodes table and the sourcecloud
    * equals the cloud context (so if the context is staging the sourcecloud should refer
    * the staging record in the cloud table)
    *
    * @param localNumber the node number
    * @param localCloud the cloud from which the node was published
    * @param remoteCloud the cloud to which the node is published
    * @return <code>true</code> if node has been published to cloud
    */
   public static boolean isPublished(int localNumber, Cloud localCloud, Cloud remoteCloud) {
      /* hh remote publishing not used 

      NodeManager nm = localCloud.getNodeManager(MMBASE_PUBLISH_MANAGER);

      NodeList nl = nm.getList("sourcenumber=" + localNumber + " AND sourcecloud=" +
                               CloudManager.getCloudNumber(localCloud, localCloud) +
                               " AND destinationcloud = " +
                               CloudManager.getCloudNumber(localCloud, remoteCloud), null, null);

      if (nl.size() == 0) {
         return false;
      } 
      else {
          if (nl.size() > 1) {
             log.error("isPublished detected multiple remote nodes for node number{" +
                       localNumber + "} for the same remote cloud still  returning true.");
         }
      }
      */
      return true;
   }

   /**
    * From the perspective of one cloud a node is said to be imported when the node
    * number is available in the destinationnumber of the remotenodes table and the
    * destinationcloud equals the cloud context (so if the context is staging the
    * destinationcloud should refer the staging record in the cloud table)
    *
    * @param localNode  Node to test
    * @return true if node was imported, false otherwise
    */
    public static boolean isImported(Node localNode) {
       return isImported(localNode.getNumber(), localNode.getCloud());
    }

    /**
     * From the perspective of one cloud a node is said to be imported when the node
     * number is available in the destinationnumber of the remotenodes table and the
     * destinationcloud equals the cloud context (so if the context is staging the
     * destinationcloud should refer the staging record in the cloud table)
     *
     * @param localNumber  Node to test
     * @param localCloud the Cloud to which the node might be published
     * @return true if node was imported, false otherwise
     */
    public static boolean isImported(int localNumber, Cloud localCloud) {
      /* hh remote publishing not used 

       NodeManager nm = localCloud.getNodeManager(MMBASE_PUBLISH_MANAGER);

       NodeList nl = nm.getList("destinationnumber=" + localNumber + " AND destinationcloud=" +
                                CloudManager.getCloudNumber(localCloud, localCloud), null, null);
       return nl.size() > 0;
       */
      return true;
    }

   /**
    * Tests whether the node has been published from another cloud to this cloud
    *
    * @param localNode the node to test
    * @param remoteCloud the Cloud from which the node might be published
    * @return <cdeo>true</code> if node was imported from cloud
    */
   public static boolean isImported(Node localNode, Cloud remoteCloud) {
      return isImported(localNode.getNumber(), localNode.getCloud(), remoteCloud);
   }

   /**
    * Tests whether the node has been published from another cloud to this cloud
    *
    * @param localNumber the node to test
    * @param localCloud the Cloud to which the node might be published
    * @param remoteCloud the Cloud from which the node might be published
    * @return <cdeo>true</code> if node was imported from cloud
    */
   public static boolean isImported(int localNumber, Cloud localCloud, Cloud remoteCloud) {
      /* hh remote publishing not used 

      NodeManager nm = localCloud.getNodeManager(MMBASE_PUBLISH_MANAGER);
      NodeList nl = nm.getList("destinationnumber=" + localNumber +
                               " AND destinationcloud=" + CloudManager.getCloudNumber(localCloud, localCloud) +
                               " AND sourcecloud=" + CloudManager.getCloudNumber(localCloud, remoteCloud), null, null);
      return nl.size() > 0;
      */
      return true;
   }

   /**
    * Unlink node from all other nodes (means you can then edit/delete them)
    * This method can also be used to republish the node from the remoteCloud to
    * this localCloud if the localNode is deleted too.
    *
    * @param localNode Node from which to remove the publish info
    */
   public static void unLinkNode(Node localNode)  {
      if (isPublished(localNode)) {
         NodeList nl = getPublishedNodes(localNode);

         if (nl.size() > 0) {
            NodeIterator ni = nl.nodeIterator();

            while (ni.hasNext()) {
               Node remoteNode = ni.nextNode();
               unLinkNode(localNode, remoteNode);
            }
         }
      }
      else {
         if (isImported(localNode)) {
            Node sourceNode = getSourceNode(localNode);
            unLinkNode(sourceNode, localNode);
         }
      }
   }

   /**
    * Remove the publish-info between a source node and a published version
    *
    * @param sourceNode the original node
    * @param destinationNode the published copy
    */
   public static void unLinkNode(Node sourceNode, Node destinationNode) {
      unLinkNode(sourceNode.getCloud(), destinationNode.getCloud(),
                 sourceNode.getNumber(), destinationNode.getNumber());
   }

   /**
    * Remove the publish-info between a source node and a published version
    *
    * @param sourceCloud
    * @param destinationCloud
    * @param sourceNumber
    * @param destinationNumber
    */
   public static void unLinkNode(Cloud sourceCloud, Cloud destinationCloud,
                                 int sourceNumber, int destinationNumber) {

      /* hh remote publishing not used 

      synchronized (versionLock) {
         // remove info in source cloud
         NodeManager sourcenm = sourceCloud.getNodeManager(MMBASE_PUBLISH_MANAGER);
         NodeList sourcenl = sourcenm.getList("sourcenumber=" + sourceNumber +
                         " AND destinationnumber=" + destinationNumber +
                         " AND sourcecloud=" + CloudManager.getCloudNumber(sourceCloud, sourceCloud) + 
                         " AND destinationcloud=" + CloudManager.getCloudNumber(sourceCloud, destinationCloud),
                         null, null);
         // There shouldn't be more then one, but just clean up all records
         for(int i = 0; i < sourcenl.size(); i++) {
            sourcenl.getNode(i).delete(true);
         }

         // remove info in destination cloud
         NodeManager destnm = destinationCloud.getNodeManager(MMBASE_PUBLISH_MANAGER);
         NodeList destnl = destnm.getList("sourcenumber=" + sourceNumber +
                         " AND destinationnumber=" + destinationNumber +
                         " AND sourcecloud=" + CloudManager.getCloudNumber(destinationCloud, sourceCloud) + 
                         " AND destinationcloud=" + CloudManager.getCloudNumber(destinationCloud, destinationCloud),
                         null, null);
         // There shouldn't be more then one, but just clean up all records
         for(int i = 0; i < destnl.size(); i++) {
            destnl.getNode(i).delete(true);
         }
      }
      */
   }

   /**
    * Get the published nodes from a remote cloud. What should be kept in mind is that
    * a node could also have been imported from the remote cloud. The query for retrieving
    * the 'published' node is dependent of this.
    *
    * @param localNode the node that has been published
    * @param remoteCloud  the remote cloud
    * @return the remote node or null if not published
    */
   public static Node getPublishedNode(Node localNode, Cloud remoteCloud) {
      Cloud localCloud = localNode.getCloud();
      NodeManager nm = localCloud.getNodeManager(MMBASE_PUBLISH_MANAGER);

      boolean isPublished = isPublished(localNode);
      /* hh remote publishing not used 

      NodeList nl = null;
      if (isPublished) {
         nl = nm.getList("sourcenumber = " + localNode.getNumber() +
                " AND sourcecloud=" + CloudManager.getCloudNumber(localCloud, localCloud) +
                " AND destinationcloud=" + CloudManager.getCloudNumber(localCloud, remoteCloud),
                null, null);
      }
      else {
         // maybe imported. Don't check if it is imported. We will return null if it is not.
         nl = nm.getList("destinationnumber = " + localNode.getNumber() +
                " AND sourcecloud=" + CloudManager.getCloudNumber(localCloud, remoteCloud) +
                " AND destinationcloud=" + CloudManager.getCloudNumber(localCloud, localCloud),
                null, null);
      }
      
      if (nl.size() == 0) {
         log.debug("search for node in live environment returned an empty list");
         return null;
      }

      if (nl.size() > 1) {
         log.error("getPublishedNode detected multiple remote nodes for node number{" +
                   localNode.getNumber() + "} in the same cloud, returning the first one in the list.");
      }
      if (isPublished) {
         return remoteCloud.getNode(nl.getNode(0).getIntValue("destinationnumber"));
      }
      else {
         return remoteCloud.getNode(nl.getNode(0).getIntValue("sourcenumber"));
      }
      */
      return null;
   }

   /**
    * Get the published nodes from all remote clouds
    *
    * @param localNode The node thas has been published
    * @return List of all remote nodes
    */
   public static NodeList getPublishedNodes(Node localNode) {
      return getPublishedNodes(localNode.getCloud(), localNode.getNumber());
   }

   /**
    * Get the published nodes from all remote clouds
    *
    * @param localCloud The source cloud
    * @param localNumber The node number that has been published
    * @return List of all remote nodes
    */
   public static NodeList getPublishedNodes(Cloud localCloud, int localNumber) {
      NodeList output = new GenericNodeList();
      
      /* hh remote publishing not used 
      NodeManager nm = localCloud.getNodeManager(MMBASE_PUBLISH_MANAGER);
      log.info("localNumber: " + localNumber);

      NodeIterator ni = nm.getList("sourcenumber = " + localNumber +
                           " AND sourcecloud=" + CloudManager.getCloudNumber(localCloud, localCloud),
                           null, null).nodeIterator();

      log.info("ni.hasNext(): " + ni.hasNext());
      while (ni.hasNext()) {
         Node admin = ni.nextNode();
         Cloud remoteCloud = CloudManager.getCloud(localCloud, admin.getIntValue("destinationcloud"));
         output.add(remoteCloud.getNode(admin.getIntValue("destinationnumber")));
      }
      */
      return output;
   }

   /**
    * Get the source node of this localNode if this localNode is imported
    *
    * @param localNode a published node
    * @return the remoteNode from which the localNode was published
    *         or null if node was not imported
    */
   public static Node getSourceNode(Node localNode) {
      /* hh remote publishing not used 
      
      synchronized (versionLock) {
         Node adminNode = getPublishInfoNode(localNode.getCloud(), localNode.getNumber(), localNode.getCloud());

         if (adminNode == null) {
            return null;
         }

         Cloud sourceCloud = CloudManager.getCloud(localNode.getCloud(), adminNode.getIntValue("sourcecloud"));
         return sourceCloud.getNode(adminNode.getIntValue("sourcenumber"));
      }
      */
       return null;
   }

   /**
    *
    * @param localCloud
    * @param destinationNumber
    * @param destinationCloud
    * @return
    */
   public static Node getPublishInfoNode(Cloud localCloud, int destinationNumber, Cloud destinationCloud) {
      /* hh remote publishing not used 

      synchronized (versionLock) {
         NodeManager nm = localCloud.getNodeManager(MMBASE_PUBLISH_MANAGER);

         NodeList nl = nm.getList("destinationnumber = " + destinationNumber +
                        " AND destinationcloud=" + CloudManager.getCloudNumber(localCloud, destinationCloud),
                        null, null);

         if (nl.size() == 0) {
            return null;
         }

         if (nl.size() > 1) {
            log.error(
               "Detected multiple instances of a publish-info node, but it should only be one " +
               "in all xases (published from or imported to the local cloud). " +
               "still returning the first node");
         }

         return nl.getNode(0);
      }
      */
      return null;
   }

   /**
    * syncronize all nodes that are published from this one
    *
    * @param localNode the source node
    *
    * NOTE for WEB-IN-A-BOX developers:
    * Use of this method is <b>NOT RECOMMENDED</b> as it encourages inplace modifications
    * to nodes and that allows editing of published information
    * ** use an editNode() saveNode() combination if workflow content **
    **/
   public static void updatePublishedNodes(Node localNode) throws PublishException {
      synchronized (versionLock) {
         log.info("isPublished: " + (!isPublished(localNode)));
         if (!isPublished(localNode)) {
            return;
         }

         NodeIterator ni = getPublishedNodes(localNode).nodeIterator();
         log.info("ni: " + ni.hasNext());
         while (ni.hasNext()) {
            Node remoteNode = ni.nextNode();
            syncFields(localNode, remoteNode);
            syncAliasses(localNode, remoteNode);
            remoteNode.commit();
            syncRelations(localNode, remoteNode);
         }
      }
   }

   /**
    * synchronize a list of fields of a node to the remote cloud
    *
    * @param localNode the source node
    **/
   public static void updateRemoteStagingNodeField(Node localNode, String field) throws PublishException {
      /* hh remote publishing not used 

      Cloud localCloud = localNode.getCloud();
      NodeManager nm = localCloud.getNodeManager(MMBASE_PUBLISH_MANAGER);
      int localNumber = localNode.getNumber();

      NodeIterator ni = nm.getList("destinationnumber = " + localNumber +
                           " AND destinationcloud=" + CloudManager.getCloudNumber(localCloud, localCloud),
                           null, null).nodeIterator();

      NodeList output = new GenericNodeList();

      while (ni.hasNext()) {
         Node admin = ni.nextNode();
         Cloud remoteCloud = CloudManager.getCloud(localCloud, admin.getIntValue("sourcecloud"));
         output.add(remoteCloud.getNode(admin.getIntValue("sourcenumber")));
      }

      NodeIterator remoteNodes = output.nodeIterator();

      NodeManager nodeManager = localNode.getNodeManager();
      Field mmbaseField = nodeManager.getField(field);
      // there should only be one node
      if (remoteNodes.hasNext()) {
         Node remoteNode = remoteNodes.nextNode();
         log.info("remotenode: " + remoteNode.getNumber());
         cloneNodeField(localNode, remoteNode, mmbaseField);
         remoteNode.commit();
         log.info("remotenode updated with field: " + field);
      }
      */
   }

   private static void syncFields(Node localNode, Node remoteNode) {
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
            cloneNodeField(localNode, remoteNode, field);
         }
      }
   }

   private static void syncAliasses(Node localNode, Node remoteNode) {
      StringList list = localNode.getAliases();
      StringList outList = remoteNode.getAliases();
      
      for (int x = 0; x < outList.size(); x++) {
         String remoateAlias = outList.getString(x);
         if (!list.contains(remoateAlias)) {
            remoteNode.deleteAlias(remoateAlias);
         }
      }
      
      for (int x = 0; x < list.size(); x++) {
         String localAlias = list.getString(x);
         if (!outList.contains(localAlias)) {
            remoteNode.createAlias(localAlias);
         }
      }
   }

   private static void syncRelations(Node localNode, Node remoteNode) throws PublishException {
      Cloud localCloud = localNode.getCloud();
      Cloud remoteCloud = remoteNode.getCloud();
      
      RelationList remoteList = remoteNode.getRelations();
      for (int x = 0; x < remoteList.size(); x++) {
         Relation remoteRelation = remoteList.getRelation(x);
         
         Node adminNode = getPublishInfoNode(localCloud, remoteRelation.getNumber(), remoteRelation.getCloud());
         if (adminNode != null) {
            boolean deleted = false;
            try {
               Node sourceNode = localCloud.getNode(adminNode.getIntValue("sourcenumber"));
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
               deleteNodeFromRemoteCloud(localCloud, adminNode.getIntValue("sourcenumber"), remoteRelation);
            }
         }
      }
      
      cloneRelations(localNode, remoteCloud);
   }

   /**
    * delete the remote instances of a node(with relations and relation information)
    * @param localNode the local node to remove
    **/
   public static void deletePublishedNode(Node localNode) {
      deletePublishedNode(localNode.getCloud(), localNode.getNumber());
   }

   /**
    * delete the remote instances of a node(with relations and relation information)
    *
    * @param localCloud
    * @param localNumber
    */
   public static void deletePublishedNode(Cloud localCloud, int localNumber) {
      log.debug("deletePublishedNode called on node " + localNumber);

      if (isPublished(localNumber, localCloud)) {
         NodeList nl = getPublishedNodes(localCloud, localNumber);
         log.debug("the node is published on " + nl.size() + " clouds");

         if (nl.size() > 0) {
            NodeIterator ni = nl.nodeIterator();

            while (ni.hasNext()) {
               Node remoteNode = ni.nextNode();
               if (log.isDebugEnabled()) {
                  //getNumber is a rmi call to the other server don't want that unless debugging
                  log.debug("the published node has number " + remoteNode.getNumber());
               }
               deleteNodeFromRemoteCloud(localCloud, localNumber, remoteNode);
            }
         }
      } else {
         log.debug("deletePublishedNode called on unpublished node " + localNumber);
      }
   }

   private static void deleteNodeFromRemoteCloud(Cloud localCloud, int localNumber, Node remoteNode) {
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
         Node adminNode = getPublishInfoNode(localCloud,
               remoteRelation.getNumber(), remoteRelation.getCloud());

         if (adminNode == null) {
            if (log.isDebugEnabled()) {
               //getNumber is a rmi call to the other server don't want that unless debugging
               log.debug("no publishinformation known about node + " + remoteRelation.getNumber());
            }
         } else {
            unLinkNode(localCloud, remoteNode.getCloud(),
               adminNode.getIntValue("sourcenumber"), remoteRelation.getNumber());
         }
         // Always delete the remote relation to keep the remote cloud stable
         // even if it is an imported relation
         remoteRelation.delete(true);
      }
      
      unLinkNode(localCloud, remoteNode.getCloud(), localNumber, remoteNode.getNumber());
      remoteNode.delete(true);
   }
}