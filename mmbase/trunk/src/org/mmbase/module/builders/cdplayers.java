/*
$Id: cdplayers.java,v 1.2 2000-03-17 12:36:49 wwwtech Exp $

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

$Log: not supported by cvs2svn $
*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.net.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

// import org.module.hardware.linux.cdrom.*;

/**
 * @author Daniel Ockeloen
 * @version $Revision: 1.2 $ $Date: 2000-03-17 12:36:49 $ 
 */
public class cdplayers extends ServiceBuilder implements MMBaseObserver {

	private boolean debug=false;

	public cdplayers() {
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
		return(true);
	}


	// changed need to be handles from data driven to its implementation interface. so for example "record" needs to be mapped to impl.doRecord.

	public Object getValue(MMObjectNode node,String field) {
		if (field.equals("getdir(info)")) {
			// send the command to get the dir
			node.setValue("state","getdir");
			node.commit();
			boolean changed=false;
			MMObjectNode newnode=null;
			while (!changed) {	
				waitUntilNodeChanged(node);
				newnode=getNode(node.getIntValue("number"));
				String state=newnode.getStringValue("state");
				if (state.equals("waiting")) changed=true;
			}
			String val=newnode.getStringValue("info");
			if (debug) {
				System.out.println("CDROM getdir ->"+val);
			}
			return(val);
		} else return super.getValue( node, field );
	}
	
	public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("getdir")) return(getHTMLDir(tagger,tok));
		}
		return(null);
	}

	public Vector getHTMLDir(StringTagger tagger, StringTokenizer tok) {
		Vector result=new Vector();
		String id=tagger.Value("NODE");
		MMObjectNode node=getNode(id);
		if (node!=null) {
			String info=(String)getValue(node,"getdir(info)");
			StringTagger infotagger=new StringTagger(info);
			try {
				int i=Integer.parseInt(infotagger.Value("NROFTRACKS"));	
				for (int j=0;j<i;j++) {
					String trlen=infotagger.Value("TR"+j+"LEN");
					String title=infotagger.Value("TR"+j+"TITLE");
					result.addElement(""+(j+1));
					result.addElement(title);
					result.addElement(trlen);
				}
			} catch(Exception e) {
			}
		}
		tagger.setValue("ITEMS","3");
		return(result);
	}


	/**
	* replace all for frontend code
	*/
	public String replace(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();
			System.out.println("cdplayers -> "+cmd);
			if (cmd.equals("claim")) {
				doClaim(tok);
			}
		}
		return("");
	}

	
}
