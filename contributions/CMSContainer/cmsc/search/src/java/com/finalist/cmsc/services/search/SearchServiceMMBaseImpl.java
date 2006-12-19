/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.search;

import java.util.*;

import javax.servlet.ServletConfig;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.Step;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.Properties;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;


public class SearchServiceMMBaseImpl extends SearchService {

    private static Log log = LogFactory.getLog(SearchServiceMMBaseImpl.class);
    private CloudProvider cloudProvider;

    @Override
    protected void init(ServletConfig aConfig, Properties aProperties) throws Exception {      
        this.cloudProvider = CloudProviderFactory.getCloudProvider();
        log.info("SearchServiceMMBaseImpl STARTED");
    }
    
    @Override
    public PageInfo findDetailPageForContent(Node content) {
        NodeList pages = findPagesForContent(content, null);
        if (!pages.isEmpty()) {
            Node pageQueryNode = null;
            if (pages.size() == 1) {
                pageQueryNode = pages.getNode(0);
            }
            else {
                for (Iterator iter = pages.iterator(); iter.hasNext();) {
                    Node pageNode = (Node) iter.next();
                    String key = pageNode.getStringValue(PortletUtil.NODEPARAMETER + "." + PortletUtil.KEY_FIELD);
                    if ("contentelement".equals(key)) {
                        pageQueryNode = pageNode;
                        break;
                    }
                }
            }
            if (pageQueryNode != null) {
                // It was one page match or the content is placed in a detail portlet
                return getPageInfo(pageQueryNode, true);
            }
            else {
                // The homepage (Site object) has a lower preference than a page deeper in the tree 
                List<Node> pageNodes = new ArrayList<Node>();
                for (Iterator iter = pages.iterator(); iter.hasNext();) {
                    Node pageNode = (Node) iter.next();
                    Page page = SiteManagement.getPage(pageNode.getIntValue(PagesUtil.PAGE + ".number"));
                    if (page != null && !(page instanceof Site)) {
                        pageNodes.add(pageNode);
                    }
                }
                if (pageNodes.isEmpty()) {
                    // all matches are sites
                    pageQueryNode = pages.getNode(0);
                    return getPageInfo(pageQueryNode, true);
                }
                else {
                    // find page which is most suitable to use as detail page
                    pageQueryNode = pageNodes.get(0);
                    return getPageInfo(pageQueryNode, true);
                }
            }
        }
        return null;
    }

    @Override
    public List<PageInfo> findAllDetailPagesForContent(Node content) {
        NodeList pages = findPagesForContent(content, null);
        List<PageInfo> infos = new ArrayList<PageInfo>();
        for (Iterator iter = pages.iterator(); iter.hasNext();) {
            Node pageNode = (Node) iter.next();
            PageInfo pageInfo = getPageInfo(pageNode, true);
            if (!infos.contains(pageInfo)) {
                infos.add(pageInfo);
            }
        }
        return infos;
    }
    
    @Override
    public List<PageInfo> findPagesForContentElement(Node content) {
        return findPagesForContentElement(content, null);
    }

    @Override
    public List<PageInfo> findPagesForContentElement(Node content, Node channel) {
        NodeList pages = findPagesForContent(content, channel);
        
        List<PageInfo> infos = new ArrayList<PageInfo>();
        for (Iterator iter = pages.iterator(); iter.hasNext();) {
            Node pageNode = (Node) iter.next();
            PageInfo pageInfo = getPageInfo(pageNode, false);
            infos.add(pageInfo);
        }
        return infos;
    }
    
    private PageInfo getPageInfo(Node pageQueryNode, boolean clicktopage) {
        Page page = SiteManagement.getPage(pageQueryNode.getIntValue(PagesUtil.PAGE + ".number"));
        if (page != null) {
            String portletWindowName = pageQueryNode.getStringValue(PortletUtil.PORTLETREL + "." + PortletUtil.LAYOUTID_FIELD);
            
            if (clicktopage) {
                String key = pageQueryNode.getStringValue(PortletUtil.NODEPARAMETER + "." + PortletUtil.KEY_FIELD);
                if ("contentchannel".equals(key)) {
                    Integer portletId = page.getPortlet(portletWindowName);
                    Portlet portlet = SiteManagement.getPortlet(portletId);
                    
                    String pageNumber = portlet.getParameterValue("page");
                    if (pageNumber != null) {
                        page = SiteManagement.getPage(Integer.valueOf(pageNumber));
                        portletWindowName = portlet.getParameterValue("window");
                    }
                }
            }

            String pagePath = SiteManagement.getPath(page, !ServerUtil.useServerName());
            PageInfo pageInfo = new PageInfo(page.getId(), pagePath, portletWindowName);
            return pageInfo;
        }
        return null;
    }
    
    private NodeList findPagesForContent(Node content, Node channel) {
        NodeList channels;
        
        if (channel != null) {
            channels = content.getCloud().createNodeList();
            channels.add(channel);
        }
        else {
            channels = RepositoryUtil.getContentChannels(content);
        }

        if (content != null) {
            channels.add(content);
        }
        
        Cloud cloud = getCloud();
        Query query = createPagesForContentQuery(cloud, channels);
        
        NodeList pages = cloud.getList(query);
        if (pages.isEmpty()) {
            if (content != null) {
                channels.remove(content);
            }
            NodeList collectionchannels = content.getCloud().createNodeList();
            for (Iterator iter = channels.iterator(); iter.hasNext();) {
                Node contentchannel = (Node) iter.next();
                NodeList cc = RepositoryUtil.getCollectionChannels(contentchannel);
                if (!cc.isEmpty()) {
                    collectionchannels.addAll(cc);
                }
            }
            if (!collectionchannels.isEmpty()) {
                Query collectionquery = createPagesForContentQuery(cloud, collectionchannels);
                pages = cloud.getList(collectionquery);
            }
        }
        return pages;
    }

    private Query createPagesForContentQuery(Cloud cloud, NodeList channels) {
        NodeManager parameterManager = cloud.getNodeManager(PortletUtil.NODEPARAMETER);
        NodeManager portletManager = cloud.getNodeManager(PortletUtil.PORTLET);
        NodeManager pageManager = cloud.getNodeManager(PagesUtil.PAGE);

        Query query = cloud.createQuery();
        Step parameterStep = query.addStep(parameterManager);
        RelationStep step2 = query.addRelationStep(portletManager, PortletUtil.PARAMETERREL, "SOURCE");
        RelationStep step4 = query.addRelationStep(pageManager, PortletUtil.PORTLETREL, "SOURCE");
        Step pageStep = step4.getNext();

        query.addField(parameterStep, parameterManager.getField(PortletUtil.KEY_FIELD));
        query.addField(parameterStep, parameterManager.getField(PortletUtil.VALUE_FIELD));
        query.addField(step4, cloud.getRelationManager(PortletUtil.PORTLETREL).getField(PortletUtil.LAYOUTID_FIELD));
        query.addField(pageStep, pageManager.getField("number"));
        
        SearchUtil.addNodesConstraints(query, parameterManager.getField(PortletUtil.VALUE_FIELD), channels);
        return query;
    }
    
    @Override
    public Set<Node> findContentElementsForPage(Node page) {
        Set<Node> result = new HashSet<Node>();
        if (page != null) {
            Cloud cloud = page.getCloud();

            Page pageObject = SiteManagement.getPage(page.getNumber());
            Collection<Integer> portlets = pageObject.getPortlets();
            for (Integer portletId : portlets) {
                Portlet portlet = SiteManagement.getPortlet(portletId); 
                List parameters = portlet.getPortletparameters();
                for (Iterator iter = parameters.iterator(); iter.hasNext();) {
                    Object param = iter.next();
                    if (param instanceof NodeParameter) {
                        String value = ((NodeParameter) param).getValueAsString();
                        if (value != null) {
                            Node found = cloud.getNode(value);
                            if (RepositoryUtil.isContentChannel(found)) {
                                NodeList elements = RepositoryUtil.getLinkedElements(found);
                                result.addAll(elements);
                            } else if (ContentElementUtil.isContentElement(found)) {
                                result.add(found);
                            }
                        }
                    }
                }
            }
        }
        log.debug("found: '" + result.size() + "' elements for page: '" + page.getNumber() + "'");
        return result;
    }

    @Override
    public Set<Node> findLinkedSecondaryContent(Node contentElement, String nodeManager) {
        Set<Node> result = new HashSet<Node>();

        NodeList celist = contentElement.getRelatedNodes(nodeManager, "posrel", "DESTINATION");
        Iterator attachmentsIter = celist.iterator();
        while (attachmentsIter.hasNext()) {
            Node attachmentNode = (Node) attachmentsIter.next();
            log.debug("linked content (" + nodeManager + ") '" + attachmentNode.getNumber() + "'");
            result.add(attachmentNode);
        }

        NodeList ce2list = contentElement.getRelatedNodes(nodeManager, "inlinerel", "DESTINATION");
        Iterator attachmentsIter2 = ce2list.iterator();
        while (attachmentsIter2.hasNext()) {
            Node attachmentNode2 = (Node) attachmentsIter2.next();
            log.debug("linked inline content (" + nodeManager + ") '" + attachmentNode2.getNumber() + "'");
            result.add(attachmentNode2);
        }

        return result;

    }

    private Cloud getCloud() {
        Cloud cloud = cloudProvider.getAnonymousCloud();
        return cloud;
    }
}
