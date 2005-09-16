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
 * Builds a MultiCast Thread to receive  and send 
 * changes from other MMBase Servers. (no it doesn't)
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 */
public class MMBaseChangeDummy implements MMBaseChangeInterface {

    // debug routines
    private static Logger log = Logging.getLoggerInstance(MMBaseChangeDummy.class.getName());
    
    MMBase parent;

    /**
     * @see org.mmbase.module.core.MMBaseChangeInterface#init(org.mmbase.module.core.MMBase)
     */
    public void init(MMBase mmb) {
        this.parent=mmb;
    }
    
    //xxx: what to do with this. we don't know what message we are going to receive...
    public boolean handleMsg(String machine,String vnr,String id,String tb,String ctype) {
        log.debug("M='"+machine+"' vnr='"+vnr+"' id='"+id+"' tb='"+tb+"' ctype='"+ctype+"'");

        MMObjectBuilder bul=parent.getMMObject(tb);
        if (bul==null) {
            log.warn("MMBaseChangeDummy -> Unknown builder="+tb);
            return(false);
        } 
    
        //this dous not compile anymore
        //bul.nodeLocalChanged(machine, id,tb,ctype);
        return(true);
    }

//    public boolean changedNode(int nodenr,String tableName,String type) {
//        MMObjectBuilder bul=parent.getMMObject(tableName);
//        if (bul!=null) {
//            bul.nodeLocalChanged(null, ""+nodenr,tableName,type);
//        }
//        return(true);
//    }

    public boolean waitUntilNodeChanged(MMObjectNode node) {
        return(true);
    }

    /* (non-Javadoc)
     * @see org.mmbase.clustering.MMBaseChangeInterface#changedNode(org.mmbase.core.event.NodeEvent)
     */
    public void changedNode(NodeEvent event) {
        //notify all listeners
        if(event.getType() == NodeEvent.EVENT_TYPE_RELATION_CHANGED){
            //the relation event broker will make shure that listeners
            //for node-relation changes to a specific builder, will be
            //notified if this builder is either source or destination type
            //in the relation event
            parent.propagateEvent((RelationEvent)event);
        }else{
            parent.propagateEvent(event);
        }
    }

    /**
     * maybe this method will have to go as well. not shure
     * @see org.mmbase.clustering.MMBaseChangeInterface#changedNode(int, java.lang.String, java.lang.String)
     */
    public boolean changedNode(int number, String tableName, String ctype) {
        // let's fire some events.
        MMObjectNode node = parent.getBuilder(tableName).getNode(number);
        NodeEvent event = new NodeEvent(node, NodeEvent.oldTypeToNewType(ctype));
        changedNode(event);
        return true;
    }

}
