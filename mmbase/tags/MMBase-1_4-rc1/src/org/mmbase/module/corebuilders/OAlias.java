/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.util.*;

/**
 * @author Rico Jansen
 * @version 3-Feb-1999
 */

public class OAlias extends MMObjectBuilder {

	public final static String buildername = "OAlias";
	private final static LRUHashtable numbercache=new LRUHashtable(128);

	public OAlias() {
	}

	public OAlias(MMBase m) {
		this.mmb=m;
		this.tableName="oalias";
		this.description="Object Aliases name substitution for objects";
		init();
		m.mmobjs.put(tableName,this);
	}


	public int getNumber(String name) {
		int rtn=-1;
		MMObjectNode node;

		node=(MMObjectNode)numbercache.get(name);
		if (node==null) {
			Enumeration e=search("name=='"+name+"'");
			if (e.hasMoreElements()) {
				node=(MMObjectNode)e.nextElement();
				rtn=node.getIntValue("destination");
				numbercache.put(name,node);
			}
		} else {
			rtn=node.getIntValue("destination");
		}
		return(rtn);
	}


	public String getAlias(int number) {
		MMObjectNode node;
		Enumeration e=search("destination=="+number);
		if (e.hasMoreElements()) {
			node=(MMObjectNode)e.nextElement();
			return(node.getStringValue("name"));
		}
		return(null);
	}

	public MMObjectNode getAliasedNode(String nodename) {
		int nr;
		MMObjectNode node=null;

		nr=getNumber(nodename);
		if (nr>0) {
			node=getNode(nr);
		}
		return(node);
	}
	
    /**
    * Remove a node from the cloud and uopdate the cache
    * @param node The node to remove.
    */
    public void removeNode(MMObjectNode node) {
        String name=node.getStringValue("name");
        super.removeNode(node);
		numbercache.remove(name);
    }
}
