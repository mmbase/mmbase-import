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
 * @version $Id: Cassettes.java,v 1.7 2003-03-10 11:50:16 pierre Exp $
 */
public class Cassettes extends MMObjectBuilder {

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("length")) {
			String str;
			int val=(node.getIntValue("length")/1000);
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
		return("/winkel/cassette.shtml?"+src);
	}
}
