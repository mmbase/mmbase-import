/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;


/**
 * @author Daniel Ockeloen
 * @version $Id: Urls.java,v 1.15 2003-03-10 11:50:21 pierre Exp $
 */
public class Urls extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(Urls.class.getName());

	public String getGUIIndicator(MMObjectNode node) {
		String str=node.getStringValue("url");
		if (str!=null) {
			if (str.indexOf("http://")==0) {
				str=str.substring(7);
			}
		}
		return(str);
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("url")) {
			String url=node.getStringValue("url");
			if (url!=null) return("<A HREF=\""+url+"\" TARGET=\"extern\">"+url+"</A>");
		}
		return(null);
	}


	public String getDefaultUrl(int src) {
		MMObjectNode node=getNode(src);
		String url=node.getStringValue("url");
		return(url);
	}
	
	
	private boolean nodeChanged(String number,String builder,String ctype) {
        if (builder.equals(tableName)) {
			int nr = Integer.parseInt(number);
			Jumpers jumpers = (Jumpers)mmb.getMMObject("jumpers");
			if (jumpers==null) {
				log.debug("Urls builder - Could not get Jumper builder");
			} else {
				jumpers.delJumpCache(number);
			}
		}
		return true;
	}

	public boolean nodeLocalChanged(String machine, String number,String builder,String ctype) {
        	super.nodeLocalChanged(machine, number,builder,ctype);
		return nodeChanged( number, builder, ctype);
	}

	public boolean nodeRemoteChanged(String machine, String number,String builder,String ctype) {
		super.nodeRemoteChanged(machine, number,builder,ctype);
		return nodeChanged(number, builder, ctype);
	}
}
