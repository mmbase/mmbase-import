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
 * @version 10 Aug 2000
 * @author Daniel Ockeloen
 */
public interface MMBaseChangeInterface {

	public boolean handleMsg(String machine,String vnr,String id,String tb,String ctype);
	public boolean changedNode(int nodenr,String tableName,String type);
	public boolean waitUntilNodeChanged(MMObjectNode node);
	public void checkWaitingNodes(String snumber);
	public boolean commitXML(String machine,String vnr,String id,String tb,String ctype,String xml);
}
