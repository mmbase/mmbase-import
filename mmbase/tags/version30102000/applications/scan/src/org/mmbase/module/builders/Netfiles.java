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
public class Netfiles extends MMObjectBuilder {

	NetFileSrv netfilesrv;

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("status")) {
			int val=node.getIntValue("status");
			switch(val) {
				case 1: return("Verzoek");
				case 2: return("Onderweg");
				case 3: return("Gedaan");
				case 4: return("Aangepast");
				case 5: return("CalcPage");
				default: return("Onbepaald");
			}
		}
		return(null);
	}

	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		super.nodeRemoteChanged(number,builder,ctype);
		if (mmb.getMachineName().equals("twohigh")) {
		debug("Change : "+number+" "+builder+" "+ctype);
		if (netfilesrv==null) {
			netfilesrv=(NetFileSrv)mmb.getMMObject("netfilesrv");
			if (netfilesrv!=null) netfilesrv.fileChange(number,ctype);
		} else {
			netfilesrv.fileChange(number,ctype);
		}
		}
		return(true);
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		super.nodeLocalChanged(number,builder,ctype);
		if (mmb.getMachineName().equals("twohigh")) {
		debug("Change : "+number+" "+builder+" "+ctype);
		if (netfilesrv==null) {
			netfilesrv=(NetFileSrv)mmb.getMMObject("netfilesrv");
			if (netfilesrv!=null) netfilesrv.fileChange(number,ctype);
		} else {
			netfilesrv.fileChange(number,ctype);
		}
		}
		return(true);
	}


}
