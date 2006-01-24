/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * Probably your builder must extend this if 'properties' on its nodes must work decently?
 *
 * @javadoc
 *
 * @version $Id: Properties.java,v 1.14 2006-01-24 10:43:26 michiel Exp $
 */
public class Properties extends MMObjectBuilder {

    private static final Logger log = Logging.getLoggerInstance(Properties.class);

    public String getGUIIndicator(MMObjectNode node) {
        String str = node.getStringValue("key");
        if (str.length() > 15) {
            return str.substring(0, 12) + "...";
        } else {
            return str;
        }
    }

    /* (non-Javadoc)
     * @see org.mmbase.module.core.MMObjectBuilder#notify(org.mmbase.core.event.NodeEvent)
     */
    public void notify(NodeEvent event) {
        if (event.getBuilderName().equals(this.getTableName())) {
            if (log.isDebugEnabled()) {
                log.debug("nodeChanged(): Property change ! "+ event.getMachine() + " " + event.getNodeNumber() +
                          " " + event.getBuilderName() + " "+ NodeEvent.newTypeToOldType(event.getType()));
            }
            if (event.getType() == NodeEvent.EVENT_TYPE_CHANGED || event.getType() == NodeEvent.EVENT_TYPE_NEW ) {
                // The passed node number is node of prop node
                
                int parent = getNode(event.getNodeNumber()).getIntValue("parent");
                if (isNodeCached(new Integer(parent))) {
                    log.debug("nodeChanged(): Zapping node properties cache for " + parent);
                    MMObjectNode pnode = getNode(parent);
                    if (pnode != null) pnode.delPropertiesCache();
                }
            }
        }
        super.notify(event);
    }
}
