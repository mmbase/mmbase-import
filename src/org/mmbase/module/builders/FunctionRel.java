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
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.corebuilders.RelDef;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Rico Jansen
 */
public class FunctionRel extends InsRel {

	public FunctionRel() {
	}
	
	/**
	* get GUIIndicator
	*/
	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("task")) {
			int val=node.getIntValue("task");
			switch (val) {
			case 1: return "Uitvoerende";
			case 2: return "Maker";
			case 3: return "Auteur";
			case 4: return "Recensent";
			case 5: return "Panellid";
			default: return "Onbekend";
			}
		}
		return(null);
	}

}
