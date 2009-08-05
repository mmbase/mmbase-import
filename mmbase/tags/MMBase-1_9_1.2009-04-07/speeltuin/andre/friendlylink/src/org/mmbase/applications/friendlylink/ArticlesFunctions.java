package org.mmbase.applications.friendlylink;

import javax.servlet.http.HttpServletRequest;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Some of the Articles methods for more general use. This class can also be used as a 
 * function (functionset 'friendlylinks').
 *
 * @author Andr\U00e9 vanToly &lt;andre@toly.nl&gt;
 * @version $Rev$
 */
public final class ArticlesFunctions {
    
    private static final Logger log = Logging.getLoggerInstance(ArticlesFunctions.class);
    private static FriendlyLink articles = (FriendlyLink) UrlFilter.getFriendlylinks().get("news");
    
    /**
     * Converts to friendlylinks, called by functionset
     *
     * @param  
     * @return 
     */
    public String convertToFriendlyLink(Cloud cloud, HttpServletRequest request, Node article, Boolean convert) {
        return articles.convertToFriendlyLink(cloud, request, article, convert);
    }

    /**
     * Returns the (jsp) template.
     * Visitors get send to the default template if none is found.
     *
     * @param   cloud       MMBase cloud
     * @param   pagenr      nodenumber
     * @return  template url
     */
    public static String getTemplate(Cloud cloud, Node article) {
        String template = articles.getParameters().getString("template");
        /*
        if (article != null) {
            NodeList l = article.getRelatedNodes("pages", "posrel", "SOURCE");
            if (l.size() == 1) {
                Node pageNode = l.getNode(0);
                String pagetemplate = PagesFunctions.getTemplate(cloud, pageNode);
                template = pagetemplate + "?nr=" + pageNode.getStringValue("number");
                
            }
        }
        */
        log.debug("returning: " + template);
        return template;
    }


}
