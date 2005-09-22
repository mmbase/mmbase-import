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
 * @version $Id: Properties.java,v 1.11 2005-09-22 19:51:07 ernst Exp $
 */
public class Properties extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(Properties.class.getName());

	public String getGUIIndicator(MMObjectNode node) {
		String str=node.getStringValue("key");
		if (str.length()>15) {
			return(str.substring(0,12)+"...");
		} else {
			return(str);
		}
	}
	
	/* (non-Javadoc)
     * @see org.mmbase.module.core.MMObjectBuilder#notify(org.mmbase.core.event.NodeEvent)
     */
    public void notify(NodeEvent event) {
        if (event.getNode().getBuilder() == this) {
            log.debug("nodeChanged(): Property change ! "+ event.getMachine() + " " + event.getNode().getNumber() +
                " " + event.getNode().getBuilder().getTableName() + " "+ NodeEvent.newTypeToOldType(event.getType()));
                if (event.getType() == NodeEvent.EVENT_TYPE_CHANGED || event.getType() == NodeEvent.EVENT_TYPE_NEW ) { 
                // The passed node number is node of prop node
                
                    int parent=event.getNode().getIntValue("parent");
                    if (isNodeCached(parent)) {
                        log.debug("nodeChanged(): Zapping node properties cache for "+parent);
                        MMObjectNode pnode=getNode(parent); 
                        if (pnode!=null) pnode.delPropertiesCache();
                    }   
            }
        }
        super.notify(event);
    }
}
