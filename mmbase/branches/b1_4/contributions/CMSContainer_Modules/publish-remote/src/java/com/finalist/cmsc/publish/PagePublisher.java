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
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.Step;

import com.finalist.cmsc.mmbase.TypeUtil;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.Workflow;

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
        addPageNodes(node, nodes);
        
        publishNodes(nodes);
    }

	protected void addPageNodes(Node node, Map<Node, Date> nodes) {
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
                    if (!isPublished(valueNode)) {
                        addChannels(nodes, valueNode);
                    }
                    NodeList contentNodes = RepositoryUtil.getContentChannelsForCollection(valueNode);
                    for (Iterator<Node> iter = contentNodes.iterator(); iter.hasNext();) {
                        Node contentChannel = iter.next();
                        publishContentChannel(nodes, contentChannel, date);
                    }
                }
                if (ContentElementUtil.isContentElement(valueNode)) {
                    if (!isPublished(valueNode)) {
                        NodeList channels = RepositoryUtil.getContentChannelsForContent(valueNode);
                        for (Iterator<Node> iter = channels.iterator(); iter.hasNext();) {
                            Node channel = iter.next();
                            addChannels(nodes, channel);
                        }
                        addContentBlock(nodes, valueNode);
                    }
                }
            }
        }
        
        nodes.putAll(pageNodes);

        addReferredPageParameters(node, nodes, publishDate);
	}

	protected void publishContentChannel(Map<Node, Date> nodes, Node valueNode, Long date) {
        if (!isPublished(valueNode)) {
            addChannels(nodes, valueNode);
            
            NodeList contentNodes = getContentElements(valueNode, date);
            for (Iterator<Node> iter = contentNodes.iterator(); iter.hasNext();) {
                Node contentElement = iter.next();
                addContentBlock(nodes, contentElement);
            }
        }
        else {
            NodeList contentNodes = getContentElements(valueNode, date);
            for (Iterator<Node> iter = contentNodes.iterator(); iter.hasNext();) {
                Node contentElement = iter.next();
                if (!isPublished(contentElement)) {
                    addContentBlock(nodes, contentElement);
                }
            }
        }
    }

    @Override
    public void remove(Node node) {
        Date publishDate = node.getDateValue(PagesUtil.PUBLISHDATE_FIELD);
        Map<Node, Date> pageNodes = findPageNodes(node, publishDate);
        Set<Node> removeNodes = pageNodes.keySet();
        removeNodes(removeNodes);
    }

    protected NodeList getContentElements(Node contentChannel, Long date) {
        NodeQuery query = RepositoryUtil.createLinkedContentQuery(contentChannel, null, null, null, false, null, -1, -1, -1, -1, -1);
        ContentElementUtil.addNotExpiredConstraint(contentChannel, query, date);
        NodeList contentNodes = query.getNodeManager().getList(query);
        return contentNodes;
    }

    protected void addChannels(Map<Node, Date> nodes, Node contentChannel) {
        List<Node> path = RepositoryUtil.getPathToRoot(contentChannel);
        for (Iterator<Node> iter = path.iterator(); iter.hasNext();) {
            Node pathElement = iter.next();
            if (!Publish.isPublished(pathElement) && !nodes.containsKey(pathElement)) {
                nodes.put(pathElement, new Date());
            }
        }
    }

    protected void addContentBlock(Map<Node, Date> nodes, Node content) {
        if (Workflow.hasWorkflow(content) && !Workflow.mayPublish(content)) {
            return;
         }
        Date contentPublishDate = content.getDateValue(ContentElementUtil.PUBLISHDATE_FIELD);
        List<Node> contentBlockNodes = findContentBlockNodes(content);
        for (Node contentNode : contentBlockNodes) {
            nodes.put(contentNode, contentPublishDate);    
        }
    }

    protected void addReferredPageParameters(Node node, Map<Node, Date> nodes, Date publishDate) {
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
        for (Iterator<Node> iter = referredPages.iterator(); iter.hasNext();) {
            Node queryNode = iter.next();
            int pageNumber = queryNode.getIntValue(PagesUtil.PAGE + ".number");
            int parameterNumber = queryNode.getIntValue(PortletUtil.NODEPARAMETER + ".number");
            Node referredPage = cloud.getNode(pageNumber);
            if (isPublished(referredPage)){
                nodes.put(cloud.getNode(parameterNumber), publishDate);
            }
        }
    }

    public Map<Node, Date> findPageNodes(Node node, Date publishDate) {
        Map<Node, Date> nodes = new LinkedHashMap<Node, Date>();
        findPageNodes(node, nodes, publishDate);
        return nodes;
    }

    
    protected void findPageNodes(Node node, Map<Node, Date> nodes, Date publishDate) {
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
                       if (!isPublished(childNode)) {
                           nodes.put(childNode, new Date());
                       }
                   }
                   else {
                       if (PagesUtil.isLayout(childNode)
                               || PortletUtil.isDefinition(childNode)) {
                           if (!isPublished(childNode)) {
                               findPageNodes(childNode, nodes, new Date());
                           }
                       }
                       else {
                           if (PortletUtil.isPortlet(childNode) && PortletUtil.isSinglePortlet(childNode)) {
                               if (!isPublished(childNode)) {
                                   findPageNodes(childNode, nodes, new Date());
                               }
                           }
                           else {
                               if (PortletUtil.isNodeParameter(childNode)) {
                                   int qNodeNumber = childNode.getIntValue(PortletUtil.VALUE_FIELD);
                                   if (childNode.getCloud().hasNode(qNodeNumber)) {
                                       Node qNode = childNode.getNodeValue(PortletUtil.VALUE_FIELD);
                                       if (PagesUtil.isPageType(qNode)) {
                                           if (isPublished(qNode)) {
                                               nodes.put(childNode, publishDate);
                                           }
                                       }
                                       else {
                                           nodes.put(childNode, publishDate);
                                       }
                                   }
                               }
                               else {
                                  boolean isNavigationType = NavigationUtil.getTreeManagers().containsKey(childNode.getNodeManager().getName());
                                  if (!isNavigationType) {
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
    
}
