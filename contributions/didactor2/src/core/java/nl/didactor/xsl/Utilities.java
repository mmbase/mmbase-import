
package nl.didactor.xsl;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

import org.apache.xpath.XPathAPI;

public  class Utilities {
    private static final Logger log = Logging.getLoggerInstance(Utilities.class);

    /**
     * Supposes the default cloud 'mmbase'.
     * @see #realNodeType
     */

    public static String realNodeType(String node) {
        return realNodeType("mmbase", node);
    }

    /**
     * @param  cloudName The name of the Cloud.
     * @param  number  The number (or alias) of the Node
     * @return The nodemanager's name for the node of that number
     */
    public static String realNodeType(String cloudName, String number) {
        log.debug("calling base");
        try {
            Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud(cloudName);
            return cloud.getNode(number).getNodeManager().getName();
        } catch (BridgeException e) {
            return "could not fetch nodemanager name from node '" + number + "' (" + e.toString() + ")";
        }
    }

}
