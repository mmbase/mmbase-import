/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

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

    public MMBaseChangeDummy(MMBase parent) {
        this.parent=parent;
    }

    public boolean handleMsg(String machine,String vnr,String id,String tb,String ctype) {
        log.debug("M='"+machine+"' vnr='"+vnr+"' id='"+id+"' tb='"+tb+"' ctype='"+ctype+"'");

        MMObjectBuilder bul=parent.getMMObject(tb);
        if (bul==null) {
            System.out.println("MMBaseChangeDummy -> Unknown builder="+tb);
            return(false);
        } 
    
        bul.nodeLocalChanged(machine, id,tb,ctype);
        return(true);
    }

    public boolean changedNode(int nodenr,String tableName,String type) {
        MMObjectBuilder bul=parent.getMMObject(tableName);
        if (bul!=null) {
            bul.nodeLocalChanged(null, ""+nodenr,tableName,type);
        }
        return(true);
    }

    public boolean waitUntilNodeChanged(MMObjectNode node) {
        return(true);
    }


    public void checkWaitingNodes(String snumber) {
    }



    public boolean commitXML(String machine,String vnr,String id,String tb,String ctype,String xml) {
        return(true);
    }

}
