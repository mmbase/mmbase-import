package org.mmbase.applications.friendlylink;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.bridge.*;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Some of the Pages methods for more general use. This class can also be used as a more 
 * function (functionset 'friendlylinks') since it has access to all parameters.
 *
 * @author Andr\U00e9 van Toly &lt;andre@toly.nl&gt;
 * @version $Rev$
 */
public final class PagesFunctions {
    
    private static final Logger log = Logging.getLoggerInstance(PagesFunctions.class);
    private static FriendlyLink pages = (FriendlyLink) UrlFilter.getFriendlylinks().get("pages");
    
    /**
     * Converts to friendlylinks, called by functionset
     *
     * @param  
     * @return 
     */
    public String convertToFriendlyLink(Cloud cloud, HttpServletRequest request, Node page, Boolean convert) {
        return pages.convertToFriendlyLink(cloud, request, page, convert);
    }

    /**
     * Finds a pages' parentpages in a site, presumes posrel relation between pages.
     * Walks from the current page to the root page.
     *
     * @param   page    A MMBase node to get parentpage from
     * @return  A list with pages nodes including the original one
     */
    public static List listPagesToRoot(Node page) {
        List pageList = new ArrayList();
        org.mmbase.bridge.NodeList parentList = page.getRelatedNodes("pages", "posrel", "SOURCE");
        while (parentList.size() != 0) {
            org.mmbase.bridge.Node parent = (org.mmbase.bridge.Node) parentList.get(0);
            pageList.add(parent);
            parentList = parent.getRelatedNodes("pages", "posrel", "SOURCE");
        }
        Collections.reverse(pageList);  // reverse the list
        //pageList.add(page);             // and add our page at the end
        
        return pageList;
    }

    /**
     * Returns the (jsp) template, in this case the field 'template' of the page node or
     * a related node of type 'templates' in which it looks for the 'url' field. 
     * Visitors get send to the default template if none is found.
     *
     * @param   cloud       MMBase cloud
     * @param   template    default template in case none is found
     * @param   pagenr      nodenumber
     * @return  template url
     */
    public static String getTemplate(Cloud cloud, Node page) {
        String template = pages.getParameters().getString("template");
        if (page != null) {
            NodeManager pnm = page.getNodeManager();    // should be 'pages'
            if (pnm.hasField("template")) {
                if (page.getStringValue("template") != null && !page.getStringValue("template").equals("")) {
                    return page.getStringValue("template");
                } else {
                    log.error("Field 'template' is empty");
                    return template;
                }
            } else if (cloud.hasNodeManager("templates")) {
                NodeList templateList = page.getRelatedNodes("templates", "related", "DESTINATION");
                if (templateList.size() == 1) {
                    return templateList.getNode(0).getStringValue("url");
                } else {
            		return template;
                }
            } else {
                return template;
            }
        } else {
            return template;
        }
    }
}
