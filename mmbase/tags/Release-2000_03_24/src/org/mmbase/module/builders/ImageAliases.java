/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

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
public class ImageAliases extends MMObjectBuilder {

/*
"(name varchar(32) not null, commands char(1024) not null)"
"(unique (name), primary key(number))"
*/

	public int insert(String owner,MMObjectNode node) {
		String name=node.getStringValue("name");
		String commands=node.getStringValue("commands");
		return(super.insert(owner,"'"+name+"','"+commands+"'"));
	}


	public String getDefaultUrl(int src) {
		MMObjectNode node=getNode(src);
		String url=node.getStringValue("url");
		return(url);
	}
}
