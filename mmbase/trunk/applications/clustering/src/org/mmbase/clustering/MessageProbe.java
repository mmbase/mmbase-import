/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering;

import org.mmbase.clustering.ClusterManager;
import org.mmbase.module.core.*;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.RelationEvent;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * MessageProbe a thread object started to handle all nofity's needed when
 * one is received.
 * @javadoc
 *
 * @author Daniel Ockeloen
 * @version $Id: MessageProbe.java,v 1.3 2005-09-26 20:05:49 ernst Exp $
 */
public class MessageProbe implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(MessageProbe.class);

    private ClusterManager parent;
    private NodeEvent event;
    private boolean remote;

    /**
     * @javadoc
     */
    MessageProbe(ClusterManager parent, NodeEvent event, boolean remote) {
        this.parent = parent;
        this.event = event;
        this.remote=remote;
    }

    /**
     * @javadoc
     */
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("Handling event " + event + " for " + (remote ? "REMOTE " + event.getMachine()  : "LOCALE"));
        }
        try {

            // we don't have to do that here. it is done in MMObjectbuilder.notify
            
//            { // backwards compatibilty:
//              
//                MMObjectNode node = event.getNode();
//                MMObjectBuilder bul = node.getBuilder();
//                if (bul instanceof MMBaseObserver) {
//                    if (remote) {
//                        ((MMBaseObserver) bul).nodeRemoteChanged(event.getMachine(), "" + node.getNumber(), bul.getTableName(), NodeEvent.newTypeToOldType(event.getType()));
//                        parent.checkWaitingNodes("" + event.getNode().getNumber());
//                    } else {
//                        ((MMBaseObserver) bul).nodeLocalChanged(event.getMachine(), "" + node.getNumber(), bul.getTableName(), NodeEvent.newTypeToOldType(event.getType()));
//                        parent.checkWaitingNodes("" + event.getNode().getNumber());
//                    }
//                }
//                
//            }
            

        //notify all listeners. we ONLY have to do this for remote events
        //the local events are bening send by the ChangeManager, and are
        //no longer handeled by the clustering system.
        
            if(! event.getMachine().equals(parent.mmbase.getMachineName())){
                if(event.getType() == NodeEvent.EVENT_TYPE_RELATION_CHANGED) {
                    //the relation event broker will make shure that listeners
                    //for node-relation changes to a specific builder, will be
                    //notified if this builder is either source or destination type
                    //in the relation event
                    parent.getMMBase().propagateEvent((RelationEvent)event);
                } else {
                    parent.getMMBase().propagateEvent(event);
                }
            }
        } catch(Throwable t) {
            log.error(Logging.stackTrace(t));
        }

    }
}
