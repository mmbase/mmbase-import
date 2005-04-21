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
public class Publications extends MMObjectBuilder {

	
	public String getDefaultUrl(int src) {
		return("/winkel/publicatie.shtml?"+src);
	}


	public void setDefaults(MMObjectNode node) {
		node.setValue("subtitle","");
		node.setValue("body","");
		node.setValue("intro","");
		node.setValue("copyright","");
		node.setValue("city","");
		node.setValue("isbn","");
	}
}
