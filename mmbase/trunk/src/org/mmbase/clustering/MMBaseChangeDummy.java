/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.RelationEvent;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.*;

/**
 * Builds a MultiCast Thread to receive  and send  changes from other MMBase Servers. (no it doesn't)
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version $Id: MMBaseChangeDummy.java,v 1.5 2005-09-20 11:40:29 michiel Exp $
 */
public class MMBaseChangeDummy implements MMBaseChangeInterface {

    private static final Logger log = Logging.getLoggerInstance(MMBaseChangeDummy.class);
    
    private MMBase mmbase;

    /**
     * @see org.mmbase.module.core.MMBaseChangeInterface#init(org.mmbase.module.core.MMBase)
     */
    public void init(MMBase mmb) {
        this.mmbase = mmb;
    }
    
    /**
     * maybe this method will have to go as well. not sure
     * @see org.mmbase.clustering.MMBaseChangeInterface#changedNode(int, java.lang.String, java.lang.String)
     */
    public boolean changedNode(int number, String tableName, String ctype) {
        // let's fire some events.
        MMObjectBuilder bul = mmbase.getBuilder(tableName);
        if (bul != null) { // backwards compatibility
            bul.nodeLocalChanged(null, "" + number, tableName, ctype);
        }
        MMObjectNode node = bul.getNode(number);
        NodeEvent event = new NodeEvent(node, NodeEvent.oldTypeToNewType(ctype));
        changedNode(event);
        return true;
    }

    public boolean waitUntilNodeChanged(MMObjectNode node) {
        return true;
    }

    /**
     * @see org.mmbase.clustering.MMBaseChangeInterface#changedNode(org.mmbase.core.event.NodeEvent)
     * @since MMBase-1.8
     */
    public void changedNode(NodeEvent event) {
        //notify all listeners
        if(event.getType() == NodeEvent.EVENT_TYPE_RELATION_CHANGED) {
            //the relation event broker will make shure that listeners
            //for node-relation changes to a specific builder, will be
            //notified if this builder is either source or destination type
            //in the relation event
            mmbase.propagateEvent((RelationEvent)event);
        }else{
            mmbase.propagateEvent(event);
        }
    }


}
