/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.search;

import java.util.List;
import java.util.Set;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.services.Service;

public abstract class SearchService extends Service {

   public abstract PageInfo findDetailPageForContent(Node node);


   public abstract PageInfo findDetailPageForContent(Node node, String serverName);


   public abstract List<PageInfo> findAllDetailPagesForContent(Node content);


   public abstract List<PageInfo> findPagesForContentElement(Node content);


   public abstract List<PageInfo> findPagesForContentElement(Node content, Node channel);


   public abstract boolean hasContentPages(Node content);


   public abstract Set<Node> findContentElementsForPage(Node page);


   public abstract Set<Node> findLinkedSecondaryContent(Node contentElement, String nodeManager);


   public abstract Set<Node> findLinkedSecondaryContent(Node contentElement, String nodeManager, String relType,
         String direction);


   public abstract Set<Node> findDetailContentElementsForPage(Node page);

   public abstract PageInfo getPortletInformation(int pageId, String elementNumber);
}
