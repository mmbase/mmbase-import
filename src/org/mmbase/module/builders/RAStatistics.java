/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.module.core.*;

/**
 * @author Rob Vermeulen  
 * @version $Id: RAStatistics.java,v 1.7 2003-03-10 11:50:20 pierre Exp $
 */
public class RAStatistics extends MMObjectBuilder {

	public RAStatistics(MMBase m) {
		this.mmb=m;
		this.tableName="rastatistics";
		this.description="Rastatistics";
		init();
	}

	public String getGUIIndicator(MMObjectNode node) {
		String str=node.getStringValue("title");
		if (str.length()>15) {
			return(str.substring(0,12)+"...");
		} else {
			return(str);
		}
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		return(null);
	}

}
