/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.util.Date;
import java.sql.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class Cassettes extends MMObjectBuilder {


	public String getGUIIndicator(MMObjectNode node) {
		String str=node.getStringValue("title");
		if (str.length()>15) {
			return(str.substring(0,12)+"...");
		} else {
			return(str);
		}
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("playtime")) {
			String str;
			int val=(node.getIntValue("playtime")/1000);
			int h=val/3600;val=val-(h*3600);
			int m=val/60;val=val-(m*60);
			if (m<10) { str="0"+h; } else { str=""+h; }
			if (m<10) { str+=":0"+m; } else { str+=":"+m; }
			if (val<10) { str+=":0"+val; } else { str+=":"+val; }
			return(str);			
		}
		return(null);
	}

	public String getDefaultUrl(int src) {
		return("/winkel/cassettes.shtml?"+src);
	}
}
