/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * Builds a MultiCast Thread to receive  and send 
 * changes from other MMBase Servers.
 *
 * @version $Id: MMBaseChangeInterface.java,v 1.3 2003-03-10 11:50:30 pierre Exp $
 * @author Daniel Ockeloen
 */
public interface MMBaseChangeInterface {

	public boolean handleMsg(String machine,String vnr,String id,String tb,String ctype);
	public boolean changedNode(int nodenr,String tableName,String type);
	public boolean waitUntilNodeChanged(MMObjectNode node);
	public void checkWaitingNodes(String snumber);
	public boolean commitXML(String machine,String vnr,String id,String tb,String ctype,String xml);
}
