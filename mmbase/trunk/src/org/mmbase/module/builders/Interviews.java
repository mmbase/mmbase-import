/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Arjan Houtman
 */
public class Interviews extends MMObjectBuilder {


	public String getDefaultUrl(int src) {
		Enumeration e=mmb.getInsRel().getRelated(src,873);
		if (e.hasMoreElements()) {
		MMObjectNode prgnode=(MMObjectNode)e.nextElement();
		int src2=prgnode.getIntValue("number");
		MMObjectBuilder bul=mmb.getMMObject("jumpers");
		System.out.println("Program="+src);
		Enumeration res=bul.search("id=E"+src);
		if (res.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)res.nextElement();	
			String url=node.getStringValue("url");
			System.out.println("url="+url);
			return(url);
		}
		return("/data/"+src2+"/interview.shtml?"+src);
		}
		return("");
	}

}
