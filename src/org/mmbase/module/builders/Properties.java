/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * @version $Id: Properties.java,v 1.10 2003-03-04 14:12:22 nico Exp $
 */
public class Properties extends MMObjectBuilder implements MMBaseObserver {

    private static Logger log = Logging.getLoggerInstance(Properties.class.getName());

	public String getGUIIndicator(MMObjectNode node) {
		String str=node.getStringValue("key");
		if (str.length()>15) {
			return(str.substring(0,12)+"...");
		} else {
			return(str);
		}
	}
	
	public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
		super.nodeRemoteChanged(machine,number,builder,ctype);
		return(nodeChanged(machine,number,builder,ctype));
	}

	public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
		super.nodeLocalChanged(machine,number,builder,ctype);
		return(nodeChanged(machine,number,builder,ctype));
	}

	
	/* Notifies memo:

	* passed ctype:

	* d: node deleted

	* c: node changed

	* n: new node

	* f: node field changed

	* r: node relation changed
	* x: some xml notify?

	*/

	public boolean nodeChanged(String machine,String number, String builder, String ctype) {
		if (builder.equals(tableName)) {
			log.debug("nodeChanged(): Property change ! "+machine+","+number+","+builder+","+ctype);
			if (ctype.equals("d")) {
				// Should zap node prop cache parent, but node already gone...
			}
/*
			 else if (ctype.equals("p")) {
				// The passed node number is node number of parent!
				if (isNodeCached(Integer.parseInt(number))) {
					MMObjectNode pnode=getNode(number);
					if (pnode!=null) {
						log.debug("nodeChanged(): Zapping node prop cache for "+number);
						pnode.delPropertiesCache();
					}
				}
			}
*/
			    else if (ctype.equals("c") || ctype.equals("n") || ctype.equals("f")) { 
				// The passed node number is node of prop node
				MMObjectNode node=getNode(number);
				if (node!=null) {
					int parent=node.getIntValue("parent");
					if (isNodeCached(parent)) {
						log.debug("nodeChanged(): Zapping node properties cache for "+parent);
						MMObjectNode pnode=getNode(parent);	
						if (pnode!=null) pnode.delPropertiesCache();
					}	
				}
			}
		}
		return(true);
	}
}
