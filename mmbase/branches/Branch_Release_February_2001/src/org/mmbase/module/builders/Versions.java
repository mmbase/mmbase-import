/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class Versions extends MMObjectBuilder {

	public int getInstalledVersion(String name,String type) {
		String query="name=='"+name+"'+type=='"+type+"'";
		Enumeration b=search(query);
		if (b.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)b.nextElement();
			return(node.getIntValue("version"));
		}
		return(-1);
	}

	public void setInstalledVersion(String name,String type,String maintainer,int version) {
		MMObjectNode node=getNewNode("system");
		node.setValue("name",name);
		node.setValue("type",type);
		node.setValue("maintainer",maintainer);
		node.setValue("version",version);
		insert("system",node);
	}


	public void updateInstalledVersion(String name,String type,String maintainer,int version) {

		String query="name=='"+name+"'+type=='"+type+"'";
		Enumeration b=search(query);
		if (b.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)b.nextElement();
			node.setValue("version",version);
			node.commit();
		}
	}
}
