/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.search;

import java.util.*;

import javax.servlet.ServletConfig;

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

    protected static final String CONTENTCHANNEL = "contentchannel";
    
    protected static final String ARCHIVE = "archive";
    
    protected static final String USE_LIFECYCLE = "useLifecycle";
    protected static final String DIRECTION = "direction";
    protected static final String ORDERBY = "orderby";
    protected static final String START_INDEX = "startindex";
    protected static final String MAX_ELEMENTS = "maxElements";

    protected static final String PAGE = "page";
    protected static final String WINDOW = "window";
    
    private Map<String,Integer> priorities = new HashMap<String, Integer>();
    private boolean usePosition;

    @Override
    protected void init(ServletConfig aConfig, Properties aProperties) throws Exception {      
        log.info("SearchServiceMMBaseImpl STARTED");
        
        String[] priorityHigh = aProperties.getStrings("priority.high");
        if (priorityHigh != null) {
            for (String high : priorityHigh) {
                priorities.put(high, 4);
            }
        }
        String[] priorityMedium = aProperties.getStrings("priority.medium");
        if (priorityMedium != null) {
            for (String medium : priorityMedium) {
                priorities.put(medium, 3);
            }
        }
        String[] priorityLow = aProperties.getStrings("priority.low");
        if (priorityLow != null) {
            for (String low : priorityLow) {
                priorities.put(low, 2);
            }
        }
        
        usePosition = aProperties.getBoolean("filter.usePosition", false);
    }
    
    private int getPriority(String name) {
        Integer prio = priorities.get(name);
        if (prio != null) {
            return prio;
        }
        return 1;
    }
    
    @Override
    public PageInfo findDetailPageForContent(Node content) {
        return findDetailPageForContent(content, null);
    }

    @Override
    public PageInfo findDetailPageForContent(Node content, String serverName) {
        List<Node> pages = findPagesForContent(content, null);
        if (!pages.isEmpty()) {
            filterPageQueryNodes(pages, content);
            if (!pages.isEmpty()) {
                List<PageInfo> pageInfos = new ArrayList<PageInfo>(); 
                for (Node pageNode : pages) {
                    PageInfo info = getPageInfo(pageNode, true);
                    if (info != null && !pageInfos.contains(info)) {
                        pageInfos.add(info);
                    }
                }
                if (!pageInfos.isEmpty()) {
                    Collections.sort(pageInfos, new PageInfoComparator(serverName));
                    return pageInfos.get(0);
                }
            }
        }
        return null;
    }

    private void filterPageQueryNodes(List<Node> pages, Node content) {
        for (Iterator<Node> iter = pages.iterator(); iter.hasNext();) {
            Node pageQueryNode = iter.next();
            boolean keep = evaluatePageQueryNode(pageQueryNode, content);
            if (! keep) {
                iter.remove();
            }
        }
    }
    
    private boolean evaluatePageQueryNode(Node pageQueryNode, Node content) {
        Page page = SiteManagement.getPage(pageQueryNode.getIntValue(PagesUtil.PAGE + ".number"));
        String key = pageQueryNode.getStringValue(PortletUtil.NODEPARAMETER + "." + PortletUtil.KEY_FIELD);
        if (CONTENTCHANNEL.equals(key)) {
            String portletWindowName = pageQueryNode.getStringValue(PortletUtil.PORTLETREL + "." + PortletUtil.LAYOUTID_FIELD);
            Integer portletId = page.getPortlet(portletWindowName);
            Portlet portlet = SiteManagement.getPortlet(portletId);
            if (portlet != null) {
                return evaluateContentTypes(portletId, content) 
                    && evaluateArchive(portlet, content)
                    && evalutateContentchannelPosition(portlet, content);
            }
        }
        return true;
    }

    private boolean evaluateContentTypes(Integer portletId, Node content) {
        List<String> nodeManagerNames = SiteManagement.getContentTypes(portletId.toString());
        if (!nodeManagerNames.isEmpty()) {
            for (String nmName : nodeManagerNames) {
                if (content.getNodeManager().getName().equals(nmName)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private boolean  evaluateArchive(Portlet portlet, Node content) {
        String archive = portlet.getParameterValue(ARCHIVE);
        return ContentElementUtil.matchArchive(content, archive);
    }

    private boolean evalutateContentchannelPosition(Portlet portlet, Node content) {
        if (usePosition) {
            String startIndex = portlet.getParameterValue(START_INDEX);
            String maxElements = portlet.getParameterValue(MAX_ELEMENTS);
            int start = startIndex == null || startIndex.length() == 0 ? 0 : Integer.valueOf(startIndex);
            int end = maxElements == null || maxElements.length() == 0 ? -1 : Integer.valueOf(maxElements);
            
            if (start > 0 || end > 0) {
                List<String> contenttypes = SiteManagement.getContentTypes(String.valueOf(portlet.getId()));

                String contentchannel = portlet.getParameterValue(CONTENTCHANNEL);
                String orderby = portlet.getParameterValue(ORDERBY);
                String direction = portlet.getParameterValue(DIRECTION);
                String archive = portlet.getParameterValue(ARCHIVE);
                
                String useLifecycle = portlet.getParameterValue(USE_LIFECYCLE);
                boolean useLifecycleBool = Boolean.valueOf(useLifecycle).booleanValue();
                if (useLifecycleBool && ServerUtil.isLive()) {
                    // A live server will remove expired nodes.
                    useLifecycleBool = false;
                }
                
                Cloud cloud = content.getCloud();
                Node channel = cloud.getNode(contentchannel);
                
                NodeList l = RepositoryUtil.getLinkedElements(channel, contenttypes, orderby, direction, useLifecycleBool, archive, start, end, -1, -1, -1); 
                for (Iterator iterator = l.iterator(); iterator.hasNext();) {
                    Node node = (Node) iterator.next();
                    if(node.getNumber() == content.getNumber()) {
                        return true;
                    }
                }
                return false;
            }
        }
        
        return true;
    }

    
    @Override
    public List<PageInfo> findAllDetailPagesForContent(Node content) {
        List<PageInfo> result = new ArrayList<PageInfo>();
        
        List<PageInfo> infos = findAllDetailPages(content);
        
        // The homepage (Site object) has a lower preference than a page deeper in the tree
        // For detail pages skip the homepage
        for (PageInfo info : infos) {
            Page page = SiteManagement.getPage(info.getPageNumber());
            if (page != null && !(page instanceof Site)) {
                result.add(info);
            }
        }
        // No pages left then reset
        if (result.isEmpty()) {
            result = infos;
        }
        
        
        
        return result;
    }

    private List<PageInfo> findAllDetailPages(Node content) {
        NodeList pages = findPagesForContent(content, null);
        
        List<PageInfo> infos = new ArrayList<PageInfo>();
        for (Iterator<Node> iter = pages.iterator(); iter.hasNext();) {
            Node pageNode = iter.next();
            PageInfo pageInfo = getPageInfo(pageNode, true);
            if (pageInfo != null && !infos.contains(pageInfo)) {
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
        for (Iterator<Node> iter = pages.iterator(); iter.hasNext();) {
            Node pageNode = iter.next();
            PageInfo pageInfo = getPageInfo(pageNode, false);
            if (pageInfo != null) {
                infos.add(pageInfo);
            }
        }
        return infos;
    }
    
    private PageInfo getPageInfo(Node pageQueryNode, boolean clicktopage) {
        Page page = SiteManagement.getPage(pageQueryNode.getIntValue(PagesUtil.PAGE + ".number"));
        if (page != null) {
            String portletWindowName = pageQueryNode.getStringValue(PortletUtil.PORTLETREL + "." + PortletUtil.LAYOUTID_FIELD);
            String parameterName = pageQueryNode.getStringValue(PortletUtil.NODEPARAMETER + "." + PortletUtil.KEY_FIELD);
            String parameterValue = pageQueryNode.getStringValue(PortletUtil.NODEPARAMETER + "." + PortletUtil.VALUE_FIELD);

            if (clicktopage) {
                if (CONTENTCHANNEL.equals(parameterName)) {
                    Integer portletId = page.getPortlet(portletWindowName);
                    Portlet portlet = SiteManagement.getPortlet(portletId);
                    
                    if (portlet != null) {
                        String pageNumber = portlet.getParameterValue(PAGE);
                        if (pageNumber != null) {
                            page = SiteManagement.getPage(Integer.valueOf(pageNumber));
                            portletWindowName = portlet.getParameterValue(WINDOW);
                        }
                    }
                }
            }
            // Check if a portlet exists on this position
            Integer portletId = page.getPortlet(portletWindowName);
            if (portletId == -1) {
                return null;
            }

            String host = null;
            if(ServerUtil.useServerName()) {
               host = SiteManagement.getSite(page);
            }

            String pagePath = SiteManagement.getPath(page, !ServerUtil.useServerName());
            Layout layout = SiteManagement.getLayout(page.getLayout());
            String prioKey = layout.getResource() + "." + portletWindowName;
            int infoPrio = getPriority(prioKey);
            boolean isSite = (page instanceof Site);
            
            PageInfo pageInfo = new PageInfo(page.getId(), host, pagePath, portletWindowName,
                    layout.getResource(), infoPrio, parameterName, parameterValue, isSite);
            return pageInfo;
        }
        return null;
    }
    
    @Override
    public boolean hasContentPages(Node content) {
       NodeList pages = findPagesForContent(content, null); 
       return (pages != null && pages.size() > 0);
    }
    
    private NodeList findPagesForContent(Node content, Node channel) {
        NodeList channels;
        Cloud cloud = content.getCloud();
        
        if (channel != null) {
            channels = cloud.createNodeList();
            channels.add(channel);
        }
        else {
            channels = RepositoryUtil.getContentChannels(content);
        }

        if (content != null) {
            channels.add(content);
        }
        
        Query query = createPagesForContentQuery(cloud, channels);
        
        NodeList pages = cloud.getList(query);
        if (pages.isEmpty()) {
            if (content != null) {
                channels.remove(content);
            }
            NodeList collectionchannels = cloud.createNodeList();
            for (Iterator<Node> iter = channels.iterator(); iter.hasNext();) {
                Node contentchannel = iter.next();
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
        if (content != null) {
            filterPageQueryNodes(pages, content);
        }
        
        return pages;
    }

    private Query createPagesForContentQuery(Cloud cloud, NodeList channels) {
        NodeManager parameterManager = cloud.getNodeManager(PortletUtil.NODEPARAMETER);
        NodeManager portletManager = cloud.getNodeManager(PortletUtil.PORTLET);
        NodeManager pageManager = cloud.getNodeManager(PagesUtil.PAGE);

        Query query = cloud.createQuery();
        Step parameterStep = query.addStep(parameterManager);
        query.addRelationStep(portletManager, PortletUtil.PARAMETERREL, "SOURCE");
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
        return findContentElementsForPage(page, false);
    }

    @Override
    public Set<Node> findDetailContentElementsForPage(Node page) {
        return findContentElementsForPage(page, true);
    }

    private Set<Node> findContentElementsForPage(Node page, boolean detailOnly) {
        Set<Node> result = new HashSet<Node>();
        if (page != null) {
            Cloud cloud = page.getCloud();

            Page pageObject = SiteManagement.getPage(page.getNumber());
            if (pageObject == null) {
                return result;
            }
            Collection<Integer> portlets = pageObject.getPortlets();
            for (Integer portletId : portlets) {
                Portlet portlet = SiteManagement.getPortlet(portletId);
                
                if (detailOnly && !isDetailPortlet(portlet)) {
                    continue;
                }
                
                List<Object> parameters = portlet.getPortletparameters();
                for (Object param : parameters) {
                    if (param instanceof NodeParameter) {
                        String value = ((NodeParameter) param).getValueAsString();
                        if (value != null) {
                            Node found = cloud.getNode(value);
                            if (RepositoryUtil.isContentChannel(found)) {
                                NodeList elements = RepositoryUtil.getLinkedElements(found);
                                for (Iterator<Node> iterator = elements.iterator(); iterator.hasNext();) {
                                    Node contentElement = iterator.next();
                                    if (evaluateArchive(portlet, contentElement)) {
                                        result.add(contentElement);
                                    }
                                }
                            } else { 
                                if (ContentElementUtil.isContentElement(found)) {
                                    if (evaluateArchive(portlet, found)) {
                                        result.add(found);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        log.debug("found: '" + result.size() + "' elements for page: '" + page.getNumber() + "'");
        return result;
    }

    private boolean isDetailPortlet(Portlet portlet) {
        String contentchannel = portlet.getParameterValue(CONTENTCHANNEL);
        if (contentchannel != null) {
            String pageNumber = portlet.getParameterValue(PAGE);
            if (pageNumber != null) {
                return false;
            }
        }
        return true;
    }

    
    @Override
    public Set<Node> findLinkedSecondaryContent(Node contentElement, String nodeManager) {
        Set<Node> result = findLinkedSecondaryContent(contentElement, nodeManager, "posrel", "DESTINATION");
        result.addAll(findLinkedSecondaryContent(contentElement, nodeManager, "inlinerel", "DESTINATION"));
        
        return result;        
    }
    
    @Override
    public Set<Node> findLinkedSecondaryContent(Node contentElement, String nodeManager, String relType, String direction) {
    	Set<Node> result = new HashSet<Node>();
    	
    	NodeList ceList = contentElement.getRelatedNodes(nodeManager, relType, direction);
    	Iterator<Node> ceIt = ceList.iterator();
    	while (ceIt.hasNext()) {
    		Node node = ceIt.next();
    		log.debug("Linked content (" + nodeManager + ") '" + node.getNumber() + "'");
    		result.add(node);
    	}
    	return result;
    }
    @Override
    public String getPortletWindow(int pageId, String elementNumber) {
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
        Node node = cloud.getNode(elementNumber);
        if (ContentElementUtil.isContentElement(node)) {
            List<PageInfo> infos = findAllDetailPages(node);
            
            if (!infos.isEmpty()) {
                for (PageInfo pageInfo : infos) {
                    if (pageId == pageInfo.getPageNumber()) {
                        return pageInfo.getWindowName();
                    }
                }
            }
        }

        return null;
    }

}
