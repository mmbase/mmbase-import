package org.mmbase.applications.friendlylink;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Element;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Some class
 *
 * @author Andr&eacute; vanToly &lt;andre@toly.nl&gt;
 * @version $Id: FriendlyLink.java,v 1.5 2007-04-30 20:53:12 andre Exp $
 */
abstract public class FriendlyLink {
    private static final Logger log = Logging.getLoggerInstance(FriendlyLink.class);
    
    protected Parameters parameters = null;
    
    /**
     * @return A List with parameters to configure a friendlylink
     */
    public final Parameters getParameters() {
        if (parameters == null) {
            parameters = new Parameters(getParameterDefinition());
            parameters.setAutoCasting(true);
        }
        return parameters;
    }

    protected  Parameter[] getParameterDefinition() {
        return Parameter.EMPTY;
    }

    /**
     * Configure method parses a DOM element passed by UrlFilter with the configuration
     * that is specific for this type of friendlylink.
     *
     * @param  element  The DOM element friendlylink from 'friendlylinks.xml' 
     *
     */
    protected abstract void configure(Element el);

    /**
     * Converts a technical url to a friendlylink to
     *
     * @param cloud     MMBase cloud
     * @param req       the http request
     * @param node      the (start)node the technical url points to
     * @param convert   whether really to convert or not (for debugging)
     * @return a 'userfriendly' link to print in a page
     */
    public abstract String convertToFriendlyLink(Cloud cloud, HttpServletRequest req, Node node, Boolean convert);
    
    /**
     * Should convert a friendlylink to a technical (tipically a jsp) link
     *
     * @param   flink   the friendlylink to convert
     * @param   params  parameters
     * @return  the original technical (jsp) url
     */
    public abstract String convertToJsp(String flink, String params);

}
