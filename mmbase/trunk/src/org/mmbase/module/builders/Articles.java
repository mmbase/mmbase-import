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
public class Articles extends MMObjectBuilder {


	public String getGUIIndicator(MMObjectNode node) {
		String str=node.getStringValue("orderinfo");
		if (str.length()>15) {
			return(str.substring(0,12)+"...");
		} else {
			return(str);
		}
	}


	public Object getValue(MMObjectNode node,String field) {
		if (field.equals("flcost")) {
			int i=node.getIntValue("cost");
			String str=""+i;
			if (str.length()>2) {
				int j=str.length()-2;
				return(str.substring(0,j)+","+str.substring(j));
			} else {
				return(str);
			}
		}
		return(null);
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("cost")) {
			int i=node.getIntValue("cost");
			String str=""+i;
			if (str.length()>2) {
				int j=str.length()-2;
				return(str.substring(0,j)+","+str.substring(j));
			} else {
				return(str);
			}
		}
		return(null);
	}
}
