/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: Todos.java,v 1.7 2000-03-30 13:11:34 wwwtech Exp $

$Log: not supported by cvs2svn $
Revision 1.6  2000/03/29 10:59:25  wwwtech
Rob: Licenses changed

Revision 1.5  2000/03/20 14:58:37  wwwtech
Rico: added Rejected status

Revision 1.4  2000/03/20 10:47:26  wwwtech
Wilbert: Merged double mapping of integer status field to text into one method

Revision 1.3  2000/03/17 12:37:11  wwwtech
- (marcel) added better support for functions in getValue

*/
package org.mmbase.module.builders;

import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @version $Revision: 1.7 $ $Date: 2000-03-30 13:11:34 $
 */
public class Todos extends MMObjectBuilder {

	private String getStatusString( int status ) {
		switch(status) {
				case 1: return("Described");
				case 2: return("Claimed");
				case 3: return("Researched");
				case 4: return("Underway");
				case 5: return("Stopped");
				case 6: return("Testing");
				case 7: return("Finished");
				case 8: return("Rejected");
				default: return("Unknown");
		}
	}
	
	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("status")) {
			return getStatusString( node.getIntValue("status"));
		}
		return(null);
	}

	public Object getValue(MMObjectNode node, String field) {
		if (field.equals("showstatus")) {
			return getStatusString( node.getIntValue("status") );
		} else return super.getValue( node, field );
	}
}
