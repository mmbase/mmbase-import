/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: cdplayers.java,v 1.6 2000-11-27 14:18:25 vpro Exp $

$Log: not supported by cvs2svn $
Revision 1.5  2000/03/31 13:27:52  wwwtech
Wilbert: Introduction of ParseException for method getList

Revision 1.4  2000/03/30 13:11:35  wwwtech
Rico: added license

Revision 1.3  2000/03/29 10:59:26  wwwtech
Rob: Licenses changed

Revision 1.2  2000/03/17 12:36:49  wwwtech
- (marcel) added better support for functions in getValue

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.net.*;

import org.mmbase.module.ParseException;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

// import org.module.hardware.linux.cdrom.*;

/**
 * @author Daniel Ockeloen
 * @version $Revision: 1.6 $ $Date: 2000-11-27 14:18:25 $ 
 */
public class cdplayers extends ServiceBuilder implements MMBaseObserver {

	private boolean debug=true;

	public cdplayers() {
	}

	/**
	 * Calls its super method and than calls nodeChanged. 
     * @param number object number of node who's state has been changed remotely.
     * @param builder a String with the buildername of the node that was changed remotely.
     * @param ctype a String with the node change type.
	 * @return result value of nodeChanged call.
	 */
	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		if (debug) debug("nodeRemoteChanged("+number+","+builder+","+ctype+"): Call super");
		super.nodeRemoteChanged(number,builder,ctype);
		return(nodeChanged(number,builder,ctype));
	}

	/**
	 * Calls its super method and than calls nodeChanged. 
     * @param number object number of node who's state has been changed.
     * @param builder a String with the buildername of the node that was changed.
     * @param ctype a String with the node change type.
	 * @return result value of nodeChanged call.
	 */
	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		if (debug) debug("nodeLocalChanged("+number+","+builder+","+ctype+"): Call super");
		super.nodeLocalChanged(number,builder,ctype);
		return(nodeChanged(number,builder,ctype));
	}

	/**
	 * Does nothing, returns immediately.
     * @param number object number of node who's state has been changed.
     * @param builder a String with the buildername of the node that was changed.
     * @param ctype a String with the node change type.
	 * @return true, always.
	 */
	public boolean nodeChanged(String number,String builder,String ctype) {
		if (debug) debug("nodeChanged("+number+","+builder+","+ctype+"): Do nothing and return");
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
	
	public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) throws ParseException {
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
				debug("getHTMLDir: Error, NROFTRACKS doesn't contain an integer! "+e);
			}
		}
		tagger.setValue("ITEMS","3");
		debug("getHTMLDir: returning result: "+result);
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
				debug("replace: Command is: 'claim', calling doClaim()");
				doClaim(tok);
			}
		}
		return("");
	}
}
