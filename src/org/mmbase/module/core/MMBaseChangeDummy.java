/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;

/**
 * Builds a MultiCast Thread to receive  and send 
 * changes from other MMBase Servers.
 *
 * @author Daniel Ockeloen
 */
public class MMBaseChangeDummy implements MMBaseChangeInterface {

	MMBase parent; 

	public MMBaseChangeDummy(MMBase parent) {
		this.parent=parent;
	}


	public boolean handleMsg(String machine,String vnr,String id,String tb,String ctype) {
		System.out.println("M='"+machine+"' vnr='"+vnr+"' id='"+id+"' tb='"+tb+"' ctype='"+ctype+"'");
		return(true);
	}

	public boolean changedNode(int nodenr,String tableName,String type) {
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
