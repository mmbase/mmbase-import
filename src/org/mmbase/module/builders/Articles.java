/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.module.core.*;

/**
 * @author Daniel Ockeloen
 * @version $Id: Articles.java,v 1.7 2003-03-10 11:50:15 pierre Exp $
 */
public class Articles extends MMObjectBuilder {


	public String getGUIIndicator(MMObjectNode node) {
		String str=node.getStringValue("orderinfo");
		return(str);
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
		return(super.getValue(node,field));
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
