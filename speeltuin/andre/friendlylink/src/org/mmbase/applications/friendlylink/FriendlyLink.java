package org.mmbase.applications.friendlylink;

import java.util.*;

import org.w3c.dom.*;
import org.mmbase.util.*;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Some class
 *
 * @author Andr&eacute; vanToly &lt;andre@toly.nl&gt;
 * @version $Id: FriendlyLink.java,v 1.3 2007-03-15 23:02:17 andre Exp $
 */
abstract public class FriendlyLink {
    private static final Logger log = Logging.getLoggerInstance(FriendlyLink.class);

    protected Parameters parameters = null;
    
    /**
     * @return A List with the parameters of the FriendlyLink.
     */
    public final Parameters getParameters() {
        if (parameters == null) {
            parameters = new Parameters(getParameterDefinition());
            parameters.setAutoCasting(true);
        }
        return parameters;
    }

    protected Parameter[] getParameterDefinition() {
        return Parameter.EMPTY;
    }

    /**
     * Configure method parses a DOM element passed by UrlFilter with the configuration
     * that is specific for this type of friendlylink.
     *
     * @param  element  The DOM element friendlylink from 'friendlylinks.xml' 
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
