/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.jsp;

import java.util.*;
import java.sql.*;
import org.mmbase.module.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.core.*;

/**
 * First try at a JSP access routine it doesn't have to be used and
 * a beans version might be easer but he its sunday and ill give it
 * a try.
 *
 * @author Daniel Ockeloen
 */
public class MMJsp {

	private static MMBase mmb;
	private static MMObjectBuilder dbul;

	public static String test() {
		return("'test() reply from MMJsp'");
	}

	public static MMObjectNode MMGetNode(int number) {
		if (mmb==null) getMMbase();

		MMObjectNode node=dbul.getNode(number);
		return(node);
	}


	public static String MMGetField(int number,String fieldname) {
		if (mmb==null) getMMbase();

		MMObjectNode node=dbul.getNode(number);
		
		int dbt=node.getDBType(fieldname);
		if (dbt==FieldDefs.TYPE_INTEGER) {
			String str=""+node.getIntValue(fieldname);
			return(str);
		} else if (dbt==FieldDefs.TYPE_STRING) {
			String str=node.getStringValue(fieldname);
			return(str);
		}
		return(null);
	}

	public static Enumeration MMGetList(String builder,String where) {
		if (mmb==null) getMMbase();

		MMObjectBuilder bul=mmb.getMMObject(builder);
		if (bul!=null) {
			return(bul.search(where));
		}
		return(null);
	}

	private static void getMMbase() {
		mmb=(MMBase)Module.getModule("MMBASEROOT");
	   	dbul=mmb.getMMObject("fielddef");
	}
}
