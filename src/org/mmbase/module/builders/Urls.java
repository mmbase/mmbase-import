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
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class Urls extends MMObjectBuilder {


	public String getGUIIndicator(MMObjectNode node) {
		String str=node.getStringValue("url");
		if (str!=null) {
			if (str.indexOf("http://")==0) {
				str=str.substring(7);
			}
			if (str.length()>15) {
				str=str.substring(0,12)+"...";
			}
		}
		return(str);
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("url")) {
			String url=node.getStringValue("url");
			if (url!=null) return("<A HREF=\""+url+"\" TARGET=\"extern\">"+url+"</A>");
			else return(null);
		} else {
			String t;
			if (field.equals("number")) {
				t = "" + node.getIntValue(field);
			}
			else
			{
				t=node.getStringValue(field);
			}
			if (t!=null && t.length()>15) {
				t=t.substring(0,12)+"...";
			}
			return(t);
		}
	}


	public String getDefaultUrl(int src) {
		MMObjectNode node=getNode(src);
		String url=node.getStringValue("url");
		return(url);
	}
	
	
	private boolean nodeChanged(String number,String builder,String ctype) {
        if (builder.equals(tableName)) {
			int nr = Integer.parseInt(number);
			debug("Removing "+number+" from jumper cache");
			Jumpers jumpers = (Jumpers)mmb.getMMObject("jumpers");
			if (jumpers==null) debug("ERROR: Could not get Jumper builder");
			else jumpers.delJumpCache(number);
		}
		return true;
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
        super.nodeLocalChanged(number,builder,ctype);
		return nodeChanged( number, builder, ctype);
	}

	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
        super.nodeRemoteChanged(number,builder,ctype);
		return nodeChanged( number, builder, ctype);
	}

}
