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

   protected static final int ANY_PAGE = -1;

   protected Map<String, Integer> priorities = new HashMap<String, Integer>();
   protected boolean usePosition;
   protected boolean preferContentChannels;


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
      preferContentChannels = aProperties.getBoolean("filter.preferContentChannels", true);
   }


   protected int getPriority(String name) {
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
      return determineDetailPage(content, serverName, pages);
   }


   protected PageInfo determineDetailPage(Node content, String serverName, List<Node> pages) {
      if (!pages.isEmpty()) {
         if (content != null) {
             filterPageQueryNodes(pages, content);
         }
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


   protected void filterPageQueryNodes(List<Node> pages, Node content) {
      for (Iterator<Node> iter = pages.iterator(); iter.hasNext();) {
         Node pageQueryNode = iter.next();
         boolean keep = evaluatePageQueryNode(pageQueryNode, content);
         if (!keep) {
            iter.remove();
         }
      }
   }


   protected boolean evaluatePageQueryNode(Node pageQueryNode, Node content) {
      Page page = (Page) SiteManagement.getNavigationItem(pageQueryNode.getIntValue(PagesUtil.PAGE + ".number"));
      String key = pageQueryNode.getStringValue(PortletUtil.NODEPARAMETER + "." + PortletUtil.KEY_FIELD);
      if (CONTENTCHANNEL.equals(key)) {
         String portletWindowName = pageQueryNode.getStringValue(PortletUtil.PORTLETREL + "."
               + PortletUtil.LAYOUTID_FIELD);
         Integer portletId = page.getPortlet(portletWindowName);
         Portlet portlet = SiteManagement.getPortlet(portletId);
         if (portlet != null) {
            return evaluateContentTypes(portletId, content) && evaluateArchive(portlet, content)
                  && evalutateContentchannelPosition(portlet, content);
         }
      }
      return true;
   }


   protected boolean evaluateContentTypes(Integer portletId, Node content) {
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


   protected boolean evaluateArchive(Portlet portlet, Node content) {
      String archive = portlet.getParameterValue(ARCHIVE);
      return ContentElementUtil.matchArchive(content, archive);
   }


   protected boolean evalutateContentchannelPosition(Portlet portlet, Node content) {
      if (usePosition) {
         String startIndex = portlet.getParameterValue(START_INDEX);
         String maxElements = portlet.getParameterValue(MAX_ELEMENTS);
         int start = startIndex == null || startIndex.length() == 0 ? 0 : Integer.valueOf(startIndex);
         int end = maxElements == null || maxElements.length() == 0 ? -1 : Integer.valueOf(maxElements);

         if (start > 0 || end > 0) {
            if (start <= 0) {
               start = 1;
            }
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

            NodeList l = RepositoryUtil.getLinkedElements(channel, contenttypes, orderby, direction, useLifecycleBool,
                  archive, start - 1, end, -1, -1, -1);
            for (Iterator<Node> iterator = l.iterator(); iterator.hasNext();) {
               Node node = iterator.next();
               if (node.getNumber() == content.getNumber()) {
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

      // The homepage (Site object) has a lower preference than a page deeper in
      // the tree
      // For detail pages skip the homepage
      for (PageInfo info : infos) {
         Page page = (Page) SiteManagement.getNavigationItem(info.getPageNumber());
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


   protected List<PageInfo> findAllDetailPages(Node content) {
      return findAllDetailPages(content, ANY_PAGE);
   }


   protected List<PageInfo> findAllDetailPages(Node content, int pageId) {
       List<Node> pages = findPagesForContent(content, null, pageId, preferContentChannels);
      return convertToPageInfos(pages);
   }


   @Override
   public List<PageInfo> findPagesForContentElement(Node content) {
      return findPagesForContentElement(content, null);
   }


   @Override
   public List<PageInfo> findPagesForContentElement(Node content, Node channel) {
      List<Node> pages = findPagesForContent(content, channel);
      return convertToPageInfos(pages);
   }


   protected List<PageInfo> convertToPageInfos(List<Node> pages) {
       List<PageInfo> infos = new ArrayList<PageInfo>();
       for (Node pageNode : pages) {
          PageInfo pageInfo = getPageInfo(pageNode, true);
          if (pageInfo != null && !infos.contains(pageInfo)) {
             infos.add(pageInfo);
          }
       }
       // put the best page as first
       Collections.sort(infos, new PageInfoComparator());
       return infos;
    }


   protected PageInfo getPageInfo(Node pageQueryNode, boolean clicktopage) {
      NavigationItem item = SiteManagement.getNavigationItem(pageQueryNode.getIntValue(PagesUtil.PAGE + ".number"));
      if (item != null && Page.class.isInstance(item)) {
         Page page = Page.class.cast(item);
         String portletWindowName = pageQueryNode.getStringValue(PortletUtil.PORTLETREL + "."
               + PortletUtil.LAYOUTID_FIELD);
         String parameterName = pageQueryNode.getStringValue(PortletUtil.NODEPARAMETER + "." + PortletUtil.KEY_FIELD);
         String parameterValue = pageQueryNode
               .getStringValue(PortletUtil.NODEPARAMETER + "." + PortletUtil.VALUE_FIELD);

         if (clicktopage) {
            if (CONTENTCHANNEL.equals(parameterName)) {
               Integer portletId = page.getPortlet(portletWindowName);
               Portlet portlet = SiteManagement.getPortlet(portletId);

               if (portlet != null) {
                  String pageNumber = portlet.getParameterValue(PAGE);
                  if (pageNumber != null) {
                      NavigationItem clickItem = SiteManagement.getNavigationItem(Integer.valueOf(pageNumber));
                      if (clickItem != null && Page.class.isInstance(clickItem)) {
                          page = Page.class.cast(clickItem);
                          portletWindowName = portlet.getParameterValue(WINDOW);
                      }
                  }
               }
            }
         }
         // Check if a portlet exists on this position
         Integer portletId = page.getPortlet(portletWindowName);
         if (portletId == -1) {
            return null;
         }
         else {
             Portlet portlet = SiteManagement.getPortlet(portletId);
             if (!isDetailPortlet(portlet)) {
                 return null;
             }
         }

         String host = null;
         if (ServerUtil.useServerName()) {
            host = SiteManagement.getSite(page);
         }

         String pagePath = SiteManagement.getPath(page, !ServerUtil.useServerName());
         Layout layout = SiteManagement.getLayout(page.getLayout());
         String prioKey = layout.getResource() + "." + portletWindowName;
         int infoPrio = getPriority(prioKey);
         boolean isSite = (page instanceof Site);

         PageInfo pageInfo = new PageInfo(page.getId(), host, pagePath, portletWindowName, layout.getResource(),
               infoPrio, parameterName, parameterValue, isSite);
         return pageInfo;
      }
      return null;
   }


   @Override
   public boolean hasContentPages(Node content) {
      NodeList pages = findPagesForContent(content, null, preferContentChannels);
      return (pages != null && pages.size() > 0);
   }

   protected NodeList findPagesForContent(Node content, Node channel) {
      return findPagesForContent(content, channel, preferContentChannels);
   }
   
   protected NodeList findPagesForContent(Node content, Node channel, boolean preferContentChannels) {
      return findPagesForContent(content, channel, ANY_PAGE, preferContentChannels);
   }


   protected NodeList findPagesForContent(Node content, Node channel, int pageid, boolean preferContentChannels) {
      Cloud cloud;
      if (content != null) {
          cloud = content.getCloud();
      }
      else {
          if (channel != null) {
              cloud = channel.getCloud();
          }
          else {
              throw new IllegalArgumentException("content and channel are null");
          }
      }

      NodeList channels;
      if (channel != null) {
         channels = cloud.createNodeList();
         channels.add(channel);
      }
      else {
         channels = RepositoryUtil.getContentChannelsForContent(content);
      }
      NodeList pages = null;
      if (preferContentChannels) {
         if (content != null) {
            channels.add(content);
         }
   
         Query query = createPagesForContentQuery(cloud, channels, pageid);
   
         pages = cloud.getList(query);
         if (pages.isEmpty()) {
            if (content != null) {
               channels.remove(content);
            }
            NodeList collectionchannels = getCollectionsForChannels(cloud, channels);
            if (!collectionchannels.isEmpty()) {
               Query collectionquery = createPagesForContentQuery(cloud, collectionchannels, pageid);
               pages = cloud.getList(collectionquery);
            }
         }
      }
      else {
         NodeList collectionchannels = getCollectionsForChannels(cloud, channels);
         channels.addAll(collectionchannels);
         
         if (content != null) {
            channels.add(content);
         }
   
         Query query = createPagesForContentQuery(cloud, channels, pageid);
         pages = cloud.getList(query);
      }
      if (pages != null) {
         
         if (content != null) {
            filterPageQueryNodes(pages, content);
         }
         return pages;
      }
      else {
         return cloud.createNodeList();
      }
   }


   private NodeList getCollectionsForChannels(Cloud cloud, NodeList channels) {
      NodeList collectionchannels = cloud.createNodeList();
      for (Iterator<Node> iter = channels.iterator(); iter.hasNext();) {
         Node contentchannel = iter.next();
         NodeList cc = RepositoryUtil.getCollectionChannels(contentchannel);
         if (!cc.isEmpty()) {
            collectionchannels.addAll(cc);
         }
      }
      return collectionchannels;
   }


   protected Query createPagesForContentQuery(Cloud cloud, NodeList channels, int pageid) {
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

      if (pageid != ANY_PAGE) {
         SearchUtil.addEqualConstraint(query, pageManager.getField("number"), pageid);
      }
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


   protected Set<Node> findContentElementsForPage(Node page, boolean detailOnly) {
      Set<Node> result = new HashSet<Node>();
      if (page != null) {
         Cloud cloud = page.getCloud();

         NavigationItem item = SiteManagement.getNavigationItem(page.getNumber());
         if (item == null || !Page.class.isInstance(item)) {
             return result;
         }

         Page pageObject = Page.class.cast(item);
         Collection<Integer> portlets = pageObject.getPortlets();
         for (Integer portletId : portlets) {
            Portlet portlet = SiteManagement.getPortlet(portletId);

            if (portlet != null) {
                if (detailOnly && !isDetailPortlet(portlet)) {
                   continue;
                }

                List<Object> parameters = portlet.getPortletparameters();
                for (Object param : parameters) {
                   if (param instanceof NodeParameter) {
                      String value = ((NodeParameter) param).getValueAsString();
                      if (value != null) {
                         Node found = cloud.getNode(value);
                         if (RepositoryUtil.isChannel(found)) {
                            NodeList elements = RepositoryUtil.getLinkedElements(found);
                            for (Iterator<Node> iterator = elements.iterator(); iterator.hasNext();) {
                               Node contentElement = iterator.next();
                               if (evaluateArchive(portlet, contentElement)) {
                                  result.add(contentElement);
                               }
                            }
                         }
                         else {
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
      }
      log.debug("found: '" + result.size() + "' elements for page: '" + page.getNumber() + "'");
      return result;
   }


   protected boolean isDetailPortlet(Portlet portlet) {
      if (portlet == null) {
         return false;
      }
      String contentchannel = portlet.getParameterValue(CONTENTCHANNEL);
      if (contentchannel != null) {
         String pageNumber = portlet.getParameterValue(PAGE);
         if (pageNumber != null) {
            return false;
         }
         else {
             return isDetailView(portlet);
         }
      }
      else {
         return isDetailView(portlet);
      }
   }

   protected boolean isDetailView(Portlet portlet) {
      int viewNumber = portlet.getView();
      if (viewNumber > 0) {
         View view = SiteManagement.getView(viewNumber);
         return view.isDetailsupport();
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
   public PageInfo getPortletInformation(int pageId, String elementNumber) {
      Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
      Node content = cloud.getNode(elementNumber);
      if (ContentElementUtil.isContentElement(content)) {
         List<PageInfo> infos = findAllDetailPages(content, pageId);
         if (infos.isEmpty()) {
            infos = findAllDetailPages(content, ANY_PAGE);
         }

         if (!infos.isEmpty()) {
            Collections.sort(infos, new PageInfoComparator());
            for (PageInfo pageInfo : infos) {
               if (pageId == pageInfo.getPageNumber()) {
                  return pageInfo;
               }
            }
         }
      }

      return null;
   }

}
