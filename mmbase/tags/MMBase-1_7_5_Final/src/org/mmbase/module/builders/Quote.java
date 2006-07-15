/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.module.core.*;

/**
 * @author Arjan Houtman
 */
public class Quote extends MMObjectBuilder {

	public String getGUIIndicator (MMObjectNode node) {
		String str = node.getStringValue ("context");
		if (str.length () > 15) {
			return (str.substring (0,12) + "...");
		} else {
			return (str);
		}
	}

}
