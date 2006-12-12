/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.search;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.services.ServiceManager;

public class Search {

    private static Log log = LogFactory.getLog(Search.class);
    private final static SearchService cService = (SearchService) ServiceManager.getService(SearchService.class);
    
    public static PageInfo findDetailPageForContent(Node node) {
        if (cService == null) {
            log.info("SearchService not started");
            return null;
        }
        return cService.findDetailPageForContent(node);
    }

    public static List<PageInfo> findPagesForContentElement(Node content) {
        if (cService == null) {
            log.info("SearchService not started");
            return null;
        }
        return cService.findPagesForContentElement(content);
    }

    public static List<PageInfo> findPagesForContentElement(Node content, Node channel) {
        if (cService == null) {
            log.info("SearchService not started");
            return null;
        }
        return cService.findPagesForContentElement(content, channel);
    }

    public static Set<Node> findContentElementsForPage(Node page) {
        if (cService == null) {
            log.info("SearchService not started");
            return null;
        }
        return cService.findContentElementsForPage(page);
    }
    
    public static Set<Node> findLinkedSecondaryContent(Node contentElement, String nodeManager) {
        if (cService == null) {
            log.info("SearchService not started");
            return null;
        }
        return cService.findLinkedSecondaryContent(contentElement, nodeManager);
    }
}
