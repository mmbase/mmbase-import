/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;


/**
 * Builds a Thread to receive from and send changes to other MMBase Servers.
 *
 * @version $Id: MMBaseChangeInterface.java,v 1.3.2.1 2004-10-09 10:51:47 nico Exp $
 * @author Daniel Ockeloen
 */
public interface MMBaseChangeInterface {

	/**
     * Initialize MMBaseChangeInterface 
	 * @param mmb MMBase instance
	 */
	void init(MMBase mmb);

    /**
	 * @param nodenr Node number
	 * @param tableName Node type (tablename)
	 * @param type the type of change: "n": new, "c": commit, "d": delete, "r" : relation changed
	 * @return <code>true</code> if added to queue
	 */
	boolean changedNode(int nodenr,String tableName,String type);
    
	/** 
     * Wait until the node change notification is completed.
	 * @param node Node to wait for
	 * @return <code>true</code> if done waiting
	 */
	boolean waitUntilNodeChanged(MMObjectNode node);
}
