package org.mmbase.applications.friendlylink;

import java.util.*;

import org.w3c.dom.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Some class
 *
 * @author Andr\U00e9 vanToly &lt;andre@toly.nl&gt;
 * @version $Rev$
 */
abstract public class FriendlyLink {
    private static final Logger log = Logging.getLoggerInstance(FriendlyLink.class);
//    private static final Map friendlylinks = new HashMap();
    
//    public static final String FRIENDLYLINKS = "friendlylinks.xsd";
//    public static final String NAMESPACE = "http://www.mmbase.org/xmlns/friendlylink";
    
    private final String name = null;
    private final LocalizedString description = null;

//    static {
//        XMLEntityResolver.registerSystemID(NAMESPACE + ".xsd", FRIENDLYLINKS, FriendlyLink.class);
//    }
    
    /**
     * Reads the configuration
     *
     * @return 
     */
    protected abstract void configure(Element el);

    /**
     * Should converts a friendlylink to a technical, normally jsp, link
     *
     * @param   flink   the friendlylink to convert
     * @param   params  parameters
     * @return  string being the original technical (jsp) url
     */
    public abstract String convertToJsp(String flink, String params);

}
