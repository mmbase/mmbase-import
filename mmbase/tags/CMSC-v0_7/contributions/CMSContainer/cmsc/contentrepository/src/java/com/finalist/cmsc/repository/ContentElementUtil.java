/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository;

import java.util.*;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.*;

import com.finalist.cmsc.security.SecurityUtil;

public class ContentElementUtil {

    private static final String SOURCE = "SOURCE";
    private static final String DESTINATION = "DESTINATION";

    public static final String NUMBER_FIELD = "number";
    public static final String TITLE_FIELD = "title";
    public static final String CREATIONDATE_FIELD = "creationdate";
    public static final String EMBARGODATE_FIELD = "embargodate";
    public static final String EXPIREDATE_FIELD = "expiredate";
    public static final String LASTMODIFIEDDATE_FIELD = "lastmodifieddate";
    public static final String LASTMODIFIER_FIELD = "lastmodifier";
    public static final String NOTIFICATIONDATE_FIELD = "notificationdate";
    public static final String USE_EXPIRY_FIELD = "use_expirydate";
    
    public static final String CONTENTELEMENT = "contentelement";
    public static final String USER = SecurityUtil.USER;

    public static final String AUTHORREL = "authorrel";
    public static final String OWNERREL = null;

    private ContentElementUtil() {
        // utility
    }

    public static List getContentTypes(Cloud cloud) {
        List<NodeManager> result = new ArrayList<NodeManager>();
        NodeManagerList nml = cloud.getNodeManagers();
        Iterator v = nml.iterator();
        while (v.hasNext()) {
            NodeManager nm = (NodeManager) v.next();
            if (ContentElementUtil.isContentType(nm)) {
                result.add(nm);
            }
        }
        return result;
    }
    
    public static boolean isContentElementField(Field field) {
        Cloud cloud = field.getNodeManager().getCloud();;
        return cloud.getNodeManager(CONTENTELEMENT).hasField(field.getName());
    }


    /** Is element from one of the content types
     * @param element node to check
     * @return is content type
     */
    public static boolean isContentElement(Node element) {
       NodeManager nm = element.getNodeManager();
       return isContentType(nm);
    }

    /** Is ModeManager of the content types
     * @param nm NodeManager to check
     * @return is content type
     */
    public static boolean isContentType(NodeManager nm) {
       try {
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

    /** Is type of content type
     * @param type to check
     * @return is content type
     */
    public static boolean isContentType(String type) {
        NodeManager nm = CloudProviderFactory.getCloudProvider().getAnonymousCloud().getNodeManager(type);
        return isContentType(nm);
    }

    public static void removeContentBlock(Node node) {
        List nodes = findContentBlockNodes(node);
        for (Iterator iter = nodes.iterator(); iter.hasNext();) {
            Node removeNode = (Node) iter.next();
            removeNode.delete(true);
        }
    }

    public static List findContentBlockNodes(Node node) {
        List<Node> nodes = new ArrayList<Node>();
        findContentBlockNodes(node, nodes, false);
        return nodes;
    }

    public static List findContentBlockNodesWithRelations(Node node) {
        List<Node> nodes = new ArrayList<Node>();
        findContentBlockNodes(node, nodes, true);
        return nodes;
    }

    private static void findContentBlockNodes(Node node, List<Node> nodes, boolean withRelation) {
        NodeIterator childs = node.getRelatedNodes("object", null, DESTINATION).nodeIterator();
        while (childs.hasNext()) {
           Node childNode = childs.nextNode();
           if (isContentElement(childNode)) {
               if (!RepositoryUtil.hasContentChannel(childNode)) {
                   findContentBlockNodes(childNode, nodes, withRelation);
               }
           }
           else {
               if (!RepositoryUtil.isChannel(childNode)) {
				   //TODO: should this be checked? Only important to remove?
                  if (childNode.countRelatedNodes(null, null, SOURCE) <= 1) {
                      findContentBlockNodes(childNode, nodes, withRelation);
                  }
               }
           }
        }

        if(withRelation) {
            RelationIterator relations = node.getRelations().relationIterator();
            while (relations.hasNext()) {
               Relation rel = (Relation) relations.next();
               nodes.add(rel);
            }
        }

        nodes.add(node);
    }

    public static Node createContentElement(Cloud cloud, String manager, String creationPath, boolean linkToChannel) {
        Node creationChannel = RepositoryUtil.getChannelFromPath(cloud, creationPath);
        if (creationChannel == null) {
            throw new IllegalArgumentException("Contentchannel '"+creationPath+"' does not exist");
        }
        String title = creationChannel.getStringValue(RepositoryUtil.TITLE_FIELD);
        return createContentElement(cloud, manager, creationChannel, title, linkToChannel);
    }

    public static Node createContentElement(Cloud cloud, String manager, String creationPath, String title, boolean linkToChannel) {
        Node creationChannel = RepositoryUtil.getChannelFromPath(cloud, creationPath);
        if (creationChannel == null) {
            throw new IllegalArgumentException("Contentchannel '"+creationPath+"' does not exist");
        }
        return createContentElement(cloud, manager, creationChannel, title, linkToChannel);
    }

    public static Node createContentElement(Cloud cloud, String manager, Node creationChannel, boolean linkToChannel) {
        String title = creationChannel.getStringValue(RepositoryUtil.TITLE_FIELD);
        return createContentElement(cloud, manager, creationChannel, title, linkToChannel);
    }

    public static Node createContentElement(Cloud cloud, String manager, Node creationChannel, String title, boolean linkToChannel) {
        NodeManager contentManager = cloud.getNodeManager(manager);
        if (contentManager == null) {
            throw new IllegalArgumentException("Manager '"+manager+"' does not exist");
        }
        Node content = contentManager.createNode();
        content.setStringValue(TITLE_FIELD, title);
        content.commit();
        addAuthor(content);
        if (linkToChannel) {
            RepositoryUtil.addContentToChannel(content, creationChannel);
        }
        else {
            RepositoryUtil.addCreationChannel(content, creationChannel);
        }
        return content;
    }

    /** Add Author
     * @param content - content
     */
    public static void addAuthor(Node content) {
       Cloud cloud = content.getCloud();
       Node user = SecurityUtil.getUserNode(cloud);
       RelationManager author = cloud.getRelationManager(CONTENTELEMENT, USER, AUTHORREL);
       Relation authorrel = content.createRelation(user, author);
       authorrel.commit();
    }

    /** Check if a contentnode has an author
     * @param content - Content Node
     * @return true if the node has a related workflowitem
     */
    public static boolean hasAuthor(Node content) {
       int count = content.countRelatedNodes(content.getCloud().getNodeManager(USER), AUTHORREL, DESTINATION);
       return count > 0;
    }

    /**
     * Get author of the content element
     * @param content - Content Node
     * @return Author node
     */
    public static Node getAuthor(Node content) {
       NodeList list = content.getRelatedNodes(USER, AUTHORREL, DESTINATION);
       if (!list.isEmpty()) {
           return list.getNode(0);
       }
       return null;
    }

    /**
     * Get owner of the content element
     * @param content - Content Node
     * @return Owner node
     */
    public static Node getOwner(Node content) {
        NodeList list = content.getRelatedNodes(USER, OWNERREL, DESTINATION);
        if (!list.isEmpty()) {
            return list.getNode(0);
        }
        else {
            return getAuthor(content);
        }
    }
    
    public static void addLifeCycleConstraint(Node channel, NodeQuery query, long date) {
        NodeManager contentManager = channel.getCloud().getNodeManager(CONTENTELEMENT);

        Field useExpireField = contentManager.getField(USE_EXPIRY_FIELD);
        Field expireField = contentManager.getField(EXPIREDATE_FIELD);
        Field embargoField = contentManager.getField(EMBARGODATE_FIELD);

        Constraint useExpire = query.createConstraint(query.getStepField(useExpireField),
                FieldCompareConstraint.EQUAL, Boolean.FALSE);
        
        Object expireDateObj = (embargoField.getType() == Field.TYPE_DATETIME) ? new Date(date) : new Long(date);
        Constraint expirydate = query.createConstraint(query.getStepField(expireField),
                FieldCompareConstraint.GREATER_EQUAL, expireDateObj);

        Object embargoDateObj = (embargoField.getType() == Field.TYPE_DATETIME) ? new Date(date) : new Long(date);
        Constraint embargodate = query.createConstraint(query.getStepField(embargoField),
                FieldCompareConstraint.LESS_EQUAL, embargoDateObj);

        Constraint lifecycleComposite = query.createConstraint(expirydate, CompositeConstraint.LOGICAL_AND, embargodate);
        
        Constraint composite = query.createConstraint(useExpire, CompositeConstraint.LOGICAL_OR, lifecycleComposite);
        SearchUtil.addConstraint(query, composite);
    }

}
