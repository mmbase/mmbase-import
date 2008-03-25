
package nl.didactor.xsl;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;


public  class Utilities {
    private static final Logger log = Logging.getLoggerInstance(Utilities.class);

    /**
     * @param  cloud The  Cloud.
     * @param  number  The number (or alias) of the Node
     * @return The nodemanager's name for the node of that number
     */
    public static String nodeManagerName(Cloud cloud, String number) {
        log.debug("calling base");
        try {
            return cloud.getNode(number).getNodeManager().getName();
        } catch (BridgeException e) {
            return "could not fetch nodemanager name from node '" + number + "' (" + e.toString() + ")";
        }
    }


    /**
     * @param  cloud The  Cloud.
     * @param  number  The number (or alias) of the Node
     * @param  plurality 1 or 2
     * @return The nodemanager's GUI name for the node of that number
     */
    public static String nodeManagerGUIName(Cloud cloud, String number, int plurality) {
        log.debug("calling base");
        try {
            return cloud.getNode(number).getNodeManager().getGUIName(plurality);
        } catch (BridgeException e) {
            return "could not fetch nodemanager name from node '" + number + "' (" + e.toString() + ")";
        }
    }




}
