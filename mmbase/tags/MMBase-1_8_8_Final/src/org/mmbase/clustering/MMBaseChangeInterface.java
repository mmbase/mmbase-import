/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectNode;


/**
 * Builds a Thread to receive from and send changes to other MMBase Servers.
 *
 * @version $Id$
 * @author Daniel Ockeloen
 */
public interface MMBaseChangeInterface {
    
    /**
     * Initialize MMBaseChangeInterface 
     * @param mmb MMBase instance
     */
    void init(MMBase mmb);
    
    
    void changedNode(NodeEvent event);
    
    /** 
     * Wait until the node change notification is completed.
     * @param node Node to wait for
     * @return <code>true</code> if done waiting
     */
    boolean waitUntilNodeChanged(MMObjectNode node);
    
    
    /**
     * @param number
     * @param tableName
     * @param ctype
     * @deprecated (i think) fire an event in stead
     */
    //boolean changedNode(int number, String tableName, String ctype);
}
