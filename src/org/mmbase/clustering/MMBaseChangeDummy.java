/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.RelationEvent;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * Builds a MultiCast Thread to receive  and send  changes from other MMBase Servers. (no it doesn't)
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version $Id: MMBaseChangeDummy.java,v 1.7 2005-09-22 19:54:44 ernst Exp $
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
