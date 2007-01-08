/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.publish;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.remotepublishing.PublishManager;
import org.mmbase.remotepublishing.util.PublishUtil;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.Step;

import com.finalist.cmsc.mmbase.TypeUtil;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.publish.Publish;

public class PagePublisher extends Publisher {
    
    public PagePublisher(Cloud cloud) {
        super(cloud);
    }

    @Override
    public boolean isPublishable(Node node) {
        return PagesUtil.isPageType(node);
    }

    @Override
    public void publish(Node node) {
        Map<Node, Date> nodes = new LinkedHashMap<Node, Date>();
        Date publishDate = node.getDateValue(PagesUtil.PUBLISHDATE_FIELD);

        Long date = new Long(System.currentTimeMillis());
        Map<Node, Date> pageNodes = findPageNodes(node, publishDate);
        for (Node pnode : pageNodes.keySet()) {
            if (PortletUtil.isNodeParameter(pnode)) {
                
                Node valueNode = pnode.getNodeValue(PortletUtil.VALUE_FIELD);
                if (RepositoryUtil.isContentChannel(valueNode)) {
                    publishContentChannel(nodes, valueNode, date);
                }
                if (RepositoryUtil.isCollectionChannel(valueNode)) {
                    if (!PublishManager.isPublished(valueNode)) {
                        addChannels(nodes, valueNode);
                    }
                    NodeList contentNodes = RepositoryUtil.getContentChannels(valueNode);
                    for (Iterator iter = contentNodes.iterator(); iter.hasNext();) {
                        Node contentChannel = (Node) iter.next();
                        publishContentChannel(nodes, contentChannel, date);
                    }
                }
                if (ContentElementUtil.isContentElement(valueNode)) {
                    if (!PublishManager.isPublished(valueNode)) {
                        NodeList channels = RepositoryUtil.getContentChannels(valueNode);
                        for (Iterator iter = channels.iterator(); iter.hasNext();) {
                            Node channel = (Node) iter.next();
                            addChannels(nodes, channel);
                        }
                        addContentBlock(nodes, valueNode);
                    }
                }
            }
        }
        
        nodes.putAll(pageNodes);

        addReferredPageParameters(node, nodes, publishDate);
        
        for (Map.Entry<Node, Date> entry : nodes.entrySet()) {
            Node pnode = entry.getKey();
            Date publish = entry.getValue();
            PublishUtil.publishOrUpdateNode(cloud, pnode.getNumber(), publish);
        }
    }

    private void publishContentChannel(Map<Node, Date> nodes, Node valueNode, Long date) {
        if (!PublishManager.isPublished(valueNode)) {
            addChannels(nodes, valueNode);
            
            NodeList contentNodes = getContentElements(valueNode, date);
            for (Iterator iter = contentNodes.iterator(); iter.hasNext();) {
                Node contentElement = (Node) iter.next();
                addContentBlock(nodes, contentElement);
            }
        }
        else {
            NodeList contentNodes = getContentElements(valueNode, date);
            for (Iterator iter = contentNodes.iterator(); iter.hasNext();) {
                Node contentElement = (Node) iter.next();
                if (!PublishManager.isPublished(contentElement)) {
                    addContentBlock(nodes, contentElement);
                }
            }
        }
    }

    @Override
    public void remove(Node node) {
        Date publishDate = node.getDateValue(PagesUtil.PUBLISHDATE_FIELD);
        Map<Node, Date> pageNodes = findPageNodes(node, publishDate);
        for (Node pnode : pageNodes.keySet()) {
            PublishUtil.removeFromQueue(pnode);
        }
    }
    
    @Override
    public void unpublish(Node node) {
        PublishUtil.removeNode(cloud, node.getNumber());
    }
    
    private NodeList getContentElements(Node contentChannel, Long date) {
        NodeQuery query = RepositoryUtil.createLinkedContentQuery(contentChannel, null, null, null, false, null, -1, -1, -1, -1, -1);
        ContentElementUtil.addNotExpiredConstraint(contentChannel, query, date);
        NodeList contentNodes = query.getNodeManager().getList(query);
        return contentNodes;
    }

    private void addChannels(Map<Node, Date> nodes, Node contentChannel) {
        List path = RepositoryUtil.getPathToRoot(contentChannel);
        for (Iterator iter = path.iterator(); iter.hasNext();) {
            Node pathElement = (Node) iter.next();
            if (!Publish.isPublished(pathElement) && !nodes.containsKey(pathElement)) {
                nodes.put(pathElement, new Date());
            }
        }
    }

    private void addContentBlock(Map<Node, Date> nodes, Node content) {
        Date contentPublishDate = content.getDateValue(ContentElementUtil.PUBLISHDATE_FIELD);
        List<Node> contentBlockNodes = findContentBlockNodes(content);
        for (Node contentNode : contentBlockNodes) {
            nodes.put(contentNode, contentPublishDate);    
        }
    }

    private void addReferredPageParameters(Node node, Map<Node, Date> nodes, Date publishDate) {
        NodeManager parameterManager = cloud.getNodeManager(PortletUtil.NODEPARAMETER);
        NodeManager portletManager = cloud.getNodeManager(PortletUtil.PORTLET);
        NodeManager pageManager = cloud.getNodeManager(PagesUtil.PAGE);

        Query query = cloud.createQuery();
        Step pageStep = query.addStep(pageManager);
        query.addRelationStep(portletManager, PortletUtil.PORTLETREL, DESTINATION);
        RelationStep step4 = query.addRelationStep(parameterManager, PortletUtil.PARAMETERREL, DESTINATION);
        Step parameterStep = step4.getNext();

        query.addField(pageStep, pageManager.getField("number"));
        query.addField(parameterStep, parameterManager.getField(PortletUtil.VALUE_FIELD));
        query.addField(parameterStep, parameterManager.getField("number"));
        SearchUtil.addEqualConstraint(query, parameterManager.getField(PortletUtil.VALUE_FIELD), Integer.valueOf(node.getNumber()));
        
        NodeList referredPages = cloud.getList(query);
        for (Iterator iter = referredPages.iterator(); iter.hasNext();) {
            Node queryNode = (Node) iter.next();
            int pageNumber = queryNode.getIntValue(PagesUtil.PAGE + ".number");
            int parameterNumber = queryNode.getIntValue(PortletUtil.NODEPARAMETER + ".number");
            Node referredPage = cloud.getNode(pageNumber);
            if (PublishManager.isPublished(referredPage)){
                nodes.put(cloud.getNode(parameterNumber), publishDate);
            }
        }
    }

    public static Map<Node, Date> findPageNodes(Node node, Date publishDate) {
        Map<Node, Date> nodes = new LinkedHashMap<Node, Date>();
        findPageNodes(node, nodes, publishDate);
        return nodes;
    }

    
    private static void findPageNodes(Node node, Map<Node, Date> nodes, Date publishDate) {
        if (nodes.containsKey(node) || TypeUtil.isSystemType(node.getNodeManager().getName())) {
            return;
        }
        
        nodes.put(node, publishDate);
        
        RelationManagerList rml = node.getNodeManager().getAllowedRelations((NodeManager) null, null, DESTINATION);        
        if (!rml.isEmpty()) {
            NodeIterator childs = node.getRelatedNodes("object", null, DESTINATION).nodeIterator();
            while (childs.hasNext()) {
               Node childNode = childs.nextNode();
               if (!PagesUtil.isPageType(childNode)) {
                   if (PagesUtil.isStylesheet(childNode) || PortletUtil.isView(childNode)) {
                       if (!PublishManager.isPublished(childNode)) {
                           nodes.put(childNode, new Date());
                       }
                   }
                   else {
                       if (PagesUtil.isLayout(childNode)
                               || PortletUtil.isDefinition(childNode)) {
                           if (!PublishManager.isPublished(childNode)) {
                               findPageNodes(childNode, nodes, new Date());
                           }
                       }
                       else {
                           if (PortletUtil.isPortlet(childNode) && PortletUtil.isSinglePortlet(childNode)) {
                               if (!PublishManager.isPublished(childNode)) {
                                   findPageNodes(childNode, nodes, new Date());
                               }
                           }
                           else {
                               if (PortletUtil.isNodeParameter(childNode)) {
                                   int qNodeNumber = childNode.getIntValue(PortletUtil.VALUE_FIELD);
                                   if (childNode.getCloud().hasNode(qNodeNumber)) {
                                       Node qNode = childNode.getNodeValue(PortletUtil.VALUE_FIELD);
                                       if (PagesUtil.isPageType(qNode)) {
                                           if (PublishManager.isPublished(qNode)) {
                                               nodes.put(childNode, publishDate);
                                           }
                                       }
                                       else {
                                           nodes.put(childNode, publishDate);
                                       }
                                   }
                               }
                               else {
                                   findPageNodes(childNode, nodes, publishDate);
                               }
                           }
                       }
                   }
               }
            }
        }
    }
    
}
