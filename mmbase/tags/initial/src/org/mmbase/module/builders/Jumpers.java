/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class Jumpers extends MMObjectBuilder {


	LRUHashtable jumpCache = new LRUHashtable(1000);

	
	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("url")) {
			String url=node.getStringValue("url");
			return("<A HREF=\""+url+"\" TARGET=\"extern\">"+url+"</A>");
		} else if (field.equals("id")) {
			return(""+node.getIntValue(field));
		} else if (field.equals("name")) {
			return(""+node.getStringValue(field));
		}
		return(null);
	}


	public String getJump(StringTokenizer tok) {
		String key = tok.nextToken();
		return(getJump(key));
	}

	public void delJumpCache(String key) {
		jumpCache.remove(key);
	}


	public String getJump(String key) {

		String url = null;
		if (key.equals("")) return("/index.html");
		url = (String)jumpCache.get(key);
		if (url != null && !url.equals("null")) return (url);

		Enumeration res=search("name=E'"+key+"'");
		if (res.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)res.nextElement();	
			url=node.getStringValue("url");
			jumpCache.put(key,url);
			return(url);
		} else {
			jumpCache.put(key,"null");	
		}				

		// do jumper
		try {
			int t=Integer.parseInt(key);
			Enumeration res2=search("id=E"+key);
			if (res2.hasMoreElements()) {
				// so it has a direct url
				MMObjectNode node=(MMObjectNode)res2.nextElement();	
				url=node.getStringValue("url");
			} else {
				// no direct url call its builder
				MMObjectNode node=getNode(key);
				if (node!=null) {
					String buln=mmb.getTypeDef().getValue(node.getIntValue("otype"));
					MMObjectBuilder bul2=mmb.getMMObject(buln);
					if (bul2!=null) url=bul2.getDefaultUrl(Integer.parseInt(key));
					jumpCache.put(key,url);
					return(url);
				}
			}
		} catch (Exception e) {
		}
		return (null);		
	}


	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		super.nodeRemoteChanged(number,builder,ctype);
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		super.nodeLocalChanged(number,builder,ctype);
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeChanged(String number,String builder,String ctype) {
		System.out.println("JUMPERS="+builder+" no="+number+" "+ctype);
		jumpCache.clear();
		return(true);
	}


}
