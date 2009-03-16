/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository;

import java.util.*;

import net.sf.mmapps.commons.util.StringUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.*;

import com.finalist.cmsc.mmbase.TypeUtil;
import com.finalist.cmsc.security.SecurityUtil;

public class ContentElementUtil {

   private static final String SOURCE = "SOURCE";
   private static final String DESTINATION = "DESTINATION";

   public static final String NUMBER_FIELD = "number";
   public static final String TITLE_FIELD = "title";
   public static final String CREATIONDATE_FIELD = "creationdate";
   public static final String PUBLISHDATE_FIELD = "publishdate";
   public static final String EXPIREDATE_FIELD = "expiredate";
   public static final String USE_EXPIRY_FIELD = "use_expirydate";
   public static final String LASTMODIFIEDDATE_FIELD = "lastmodifieddate";
   public static final String CREATOR_FIELD = "creator";
   public static final String LASTMODIFIER_FIELD = "lastmodifier";
   public static final String NOTIFICATIONDATE_FIELD = "notificationdate";
   public static final String KEYWORD_FIELD = "keywords";
   public static final String ARCHIVEDATE_FIELD = "archivedate";

   public static final String CONTENTELEMENT = "contentelement";
   public static final String USER = SecurityUtil.USER;

   public static final String OWNERREL = "ownerrel";


   private ContentElementUtil() {
      // utility
   }


   public static NodeManager getNodeManager(Cloud cloud) {
      return cloud.getNodeManager(CONTENTELEMENT);
   }


   public static List<NodeManager> getContentTypes(Cloud cloud) {
      List<NodeManager> result = new ArrayList<NodeManager>();
      NodeManagerList nml = cloud.getNodeManagers();
      Iterator<NodeManager> v = nml.iterator();
      while (v.hasNext()) {
         NodeManager nm = v.next();
         if (ContentElementUtil.isContentType(nm)) {
            result.add(nm);
         }
      }
      Collections.sort(result, new Comparator<NodeManager>() {
         public int compare(NodeManager o1, NodeManager o2) {
            return o1.getGUIName().compareTo(o2.getGUIName());
         }
      });

      return result;
   }


   public static boolean isContentElementField(Field field) {
      Cloud cloud = field.getNodeManager().getCloud();
      ;
      return cloud.getNodeManager(CONTENTELEMENT).hasField(field.getName());
   }


   /**
    * Is element from one of the content types
    * 
    * @param element
    *           node to check
    * @return is content type
    */
   public static boolean isContentElement(Node element) {
      if (element == null) return false;
      
      NodeManager nm = element.getNodeManager();
      return isContentType(nm);
   }


   /**
    * Is ModeManager of the content types
    * 
    * @param nm
    *           NodeManager to check
    * @return is content type
    */
   public static boolean isContentType(NodeManager nm) {
      if (CONTENTELEMENT.equals(nm.getName())) {
         // contentelement manager is not a content type
         return false;
      }
      try {
         nm = nm.getParent();
         while (!CONTENTELEMENT.equals(nm.getName())) {
            nm = nm.getParent();
         }
         return true;
      }
      catch (NotFoundException nfe) {
         // Ran out of NodeManager parents
      }
      return false;
   }


   /**
    * Is type of content type
    * 
    * @param type
    *           to check
    * @return is content type
    */
   public static boolean isContentType(String type) {
      NodeManager nm = CloudProviderFactory.getCloudProvider().getAnonymousCloud().getNodeManager(type);
      return isContentType(nm);
   }


   public static void removeContentBlock(Node node) {
      List<Node> nodes = findContentBlockNodes(node);
      for (Iterator<Node> iter = nodes.iterator(); iter.hasNext();) {
         Node removeNode = iter.next();
         removeNode.delete(true);
      }
   }


   public static List<Node> findContentBlockNodes(Node node) {
      List<Node> nodes = new ArrayList<Node>();
      findContentBlockNodes(node, nodes, false, false);
      return nodes;
   }


   public static List<Node> findContentBlockNodesWithRelations(Node node) {
      List<Node> nodes = new ArrayList<Node>();
      findContentBlockNodes(node, nodes, true, false);
      return nodes;
   }


   public static void findContentBlockNodes(Node node, List<Node> nodes, boolean withRelation, boolean remove) {
      if (!remove) {
         if (!nodes.contains(node)) {
            nodes.add(node);
         }
      }

      NodeIterator childs = node.getRelatedNodes("object", null, DESTINATION).nodeIterator();
      while (childs.hasNext()) {
         Node childNode = childs.nextNode();
         if (isContentElement(childNode)) {
            if (!RepositoryUtil.hasContentChannel(childNode)) {
               findContentBlockNodes(childNode, nodes, withRelation, remove);
            }
         }
         else {
            if (!RepositoryUtil.isContentChannel(childNode)) {
               if (remove) {
                  if (childNode.countRelatedNodes(null, null, SOURCE) <= 1) {
                     findContentBlockNodes(childNode, nodes, withRelation, remove);
                  }
               }
               else {
                  if (!TypeUtil.isSystemType(childNode.getNodeManager().getName())) {
                     findContentBlockNodes(childNode, nodes, withRelation, remove);
                  }
               }
            }
         }
      }

      if (withRelation) {
         RelationIterator relations = node.getRelations().relationIterator();
         while (relations.hasNext()) {
            Relation rel = (Relation) relations.next();
            if (!nodes.contains(rel)) {
               nodes.add(rel);
            }
         }
      }
      if (remove) {
         if (!nodes.contains(node)) {
            nodes.add(node);
         }
      }
   }


   public static Node createContentElement(Cloud cloud, String manager, String creationPath, boolean linkToChannel) {
      Node creationChannel = RepositoryUtil.getChannelFromPath(cloud, creationPath);
      if (creationChannel == null) {
         throw new IllegalArgumentException("Contentchannel '" + creationPath + "' does not exist");
      }
      String title = creationChannel.getStringValue(RepositoryUtil.TITLE_FIELD);
      return createContentElement(cloud, manager, creationChannel, title, linkToChannel);
   }


   public static Node createContentElement(Cloud cloud, String manager, String creationPath, String title,
         boolean linkToChannel) {
      Node creationChannel = RepositoryUtil.getChannelFromPath(cloud, creationPath);
      if (creationChannel == null) {
         throw new IllegalArgumentException("Contentchannel '" + creationPath + "' does not exist");
      }
      return createContentElement(cloud, manager, creationChannel, title, linkToChannel);
   }


   public static Node createContentElement(Cloud cloud, String manager, Node creationChannel, boolean linkToChannel) {
      String title = creationChannel.getStringValue(RepositoryUtil.TITLE_FIELD);
      return createContentElement(cloud, manager, creationChannel, title, linkToChannel);
   }


   public static Node createContentElement(Cloud cloud, String manager, Node creationChannel, String title,
         boolean linkToChannel) {
      NodeManager contentManager = cloud.getNodeManager(manager);
      if (contentManager == null) {
         throw new IllegalArgumentException("Manager '" + manager + "' does not exist");
      }
      Node content = contentManager.createNode();
      content.setStringValue(TITLE_FIELD, title);
      content.commit();
      addOwner(content);
      if (linkToChannel) {
         RepositoryUtil.addContentToChannel(content, creationChannel);
      }
      else {
         RepositoryUtil.addCreationChannel(content, creationChannel);
      }
      return content;
   }


   /**
    * Add owner
    * 
    * @param content -
    *           content
    */
   public static void addOwner(Node content) {
      Cloud cloud = content.getCloud();
      Node user = SecurityUtil.getUserNode(cloud);
      RelationManager author = cloud.getRelationManager(CONTENTELEMENT, USER, OWNERREL);
      Relation ownerrel = content.createRelation(user, author);
      ownerrel.commit();
   }


   /**
    * Check if a contentnode has an owner
    * 
    * @param content -
    *           Content Node
    * @return true if the node has a related workflowitem
    */
   public static boolean hasOwner(Node content) {
      int count = content.countRelatedNodes(content.getCloud().getNodeManager(USER), OWNERREL, DESTINATION);
      return count > 0;
   }


   /**
    * Get author of the content element
    * 
    * @param content -
    *           Content Node
    * @return Author node
    */
   public static Node getAuthor(Node content) {
      String creator = content.getStringValue(CREATOR_FIELD);
      return SecurityUtil.getUserNode(content.getCloud(), creator);
   }


   /**
    * Get owner of the content element
    * 
    * @param content -
    *           Content Node
    * @return Owner node
    */
   public static Node getOwner(Node content) {
      NodeList list = content.getRelatedNodes(USER, OWNERREL, DESTINATION);
      if (!list.isEmpty()) {
         return list.getNode(0);
      }
      return null;
   }


   public static void addNotExpiredConstraint(Node channel, NodeQuery query, long date) {
      NodeManager contentManager = channel.getCloud().getNodeManager(CONTENTELEMENT);

      Constraint useExpire = getUseExpireConstraint(query, contentManager, Boolean.FALSE);
      Constraint expirydate = getExpireConstraint(query, date, contentManager, true);

      Constraint composite = query.createConstraint(useExpire, CompositeConstraint.LOGICAL_OR, expirydate);
      SearchUtil.addConstraint(query, composite);
   }


   public static void addLifeCycleConstraint(NodeQuery query, long date) {
      NodeManager contentManager = query.getCloud().getNodeManager(CONTENTELEMENT);

      Constraint useExpire = getUseExpireConstraint(query, contentManager, Boolean.FALSE);
      Constraint expirydate = getExpireConstraint(query, date, contentManager, true);
      Constraint publishdate = getPublishConstraint(query, date, contentManager, false);

      Constraint lifecycleComposite = query.createConstraint(expirydate, CompositeConstraint.LOGICAL_AND, publishdate);

      Constraint composite = query.createConstraint(useExpire, CompositeConstraint.LOGICAL_OR, lifecycleComposite);
      SearchUtil.addConstraint(query, composite);
   }


   public static void addLifeCycleInverseConstraint(NodeQuery query, long date) {
      NodeManager contentManager = query.getCloud().getNodeManager(CONTENTELEMENT);

      Constraint useExpire = getUseExpireConstraint(query, contentManager, Boolean.TRUE);
      Constraint expirydate = getExpireConstraint(query, date, contentManager, false);
      Constraint publishdate = getPublishConstraint(query, date, contentManager, true);

      Constraint lifecycleComposite = query.createConstraint(expirydate, CompositeConstraint.LOGICAL_OR, publishdate);

      Constraint composite = query.createConstraint(useExpire, CompositeConstraint.LOGICAL_AND, lifecycleComposite);
      SearchUtil.addConstraint(query, composite);
   }


   public static Constraint getUseExpireConstraint(NodeQuery query, NodeManager contentManager, Boolean value) {
      Field useExpireField = contentManager.getField(USE_EXPIRY_FIELD);
      Constraint useExpire = query.createConstraint(query.getStepField(useExpireField), FieldCompareConstraint.EQUAL,
            value);
      return useExpire;
   }


   public static Constraint getExpireConstraint(NodeQuery query, long date, NodeManager contentManager, boolean greater) {
      int operator = (greater ? FieldCompareConstraint.GREATER_EQUAL : FieldCompareConstraint.LESS_EQUAL);

      Field expireField = contentManager.getField(EXPIREDATE_FIELD);
      Object expireDateObj = (expireField.getType() == Field.TYPE_DATETIME) ? new Date(date) : new Long(date);
      Constraint expirydate = query.createConstraint(query.getStepField(expireField), operator, expireDateObj);
      return expirydate;
   }


   public static Constraint getPublishConstraint(NodeQuery query, long date, NodeManager contentManager, boolean greater) {
      int operator = (greater ? FieldCompareConstraint.GREATER_EQUAL : FieldCompareConstraint.LESS_EQUAL);

      Field publishField = contentManager.getField(PUBLISHDATE_FIELD);
      Object publishDateObj = (publishField.getType() == Field.TYPE_DATETIME) ? new Date(date) : new Long(date);
      Constraint publishdate = query.createConstraint(query.getStepField(publishField), operator, publishDateObj);
      return publishdate;
   }


   public static void addArchiveConstraint(Node channel, NodeQuery query, Long date, String archive) {
      if (StringUtil.isEmpty(archive) || "all".equalsIgnoreCase(archive)) {
         return;
      }
      NodeManager contentManager = channel.getCloud().getNodeManager(CONTENTELEMENT);

      Field archiveDateField = contentManager.getField(ARCHIVEDATE_FIELD);
      Object archiveDateObj = (archiveDateField.getType() == Field.TYPE_DATETIME) ? new Date(date) : new Long(date);

      Constraint archivedate = null;
      if ("old".equalsIgnoreCase(archive)) {
         archivedate = query.createConstraint(query.getStepField(archiveDateField), FieldCompareConstraint.LESS_EQUAL,
               archiveDateObj);
      }
      else {
         // "new".equalsIgnoreCase(archive)
         archivedate = query.createConstraint(query.getStepField(archiveDateField),
               FieldCompareConstraint.GREATER_EQUAL, archiveDateObj);
      }
      SearchUtil.addConstraint(query, archivedate);
   }


   public static boolean matchArchive(Node content, String archive) {
      if (StringUtil.isEmpty(archive) || "all".equalsIgnoreCase(archive)) {
         return true;
      }
      // Precision of now is based on minutes.
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      long date = cal.getTimeInMillis();

      Date archiveDate = content.getDateValue(ARCHIVEDATE_FIELD);
      if ("old".equalsIgnoreCase(archive)) {
         return archiveDate.getTime() < date;
      }
      else {
         return archiveDate.getTime() >= date;
      }
   }

}
