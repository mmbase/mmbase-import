/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.tools;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.util.Date;
import java.io.*;
import java.sql.*;

import org.mmbase.util.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.database.support.*;


/**
 * The module which provides access to the multimedia database
 * it creates, deletes and gives you methods to keep track of
 * multimedia objects. It does not give you direct methods for
 * inserting and reading them thats done by other objects
 *
 * @author Daniel Ockeloen
 */
public class MMExport extends ProcessorModule {

	MMBase mmb=null;

	public void init() {
		mmb=(MMBase)getModule("MMBASEROOT");		
	}


	/**
	 */
	public MMExport() {
	}

	/**
	 * Generate a list of values from a command to the processor
	 */
	 public Vector  getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
    	String line = Strip.DoubleQuote(value,Strip.BOTH);
		StringTokenizer tok = new StringTokenizer(line,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			//if (cmd.equals("OBJECTS")) return(doObjects(req,tagger));

		}
		return(null);
	}

	/**
	 * Execute the commands provided in the form values
	 */
	public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
		String cmdline,token;

		for (Enumeration h = cmds.keys();h.hasMoreElements();) {
			cmdline=(String)h.nextElement();	
			StringTokenizer tok = new StringTokenizer(cmdline,"-\n\r");
			token = tok.nextToken();
			if (token.equals("EXPORTXML")) {
				doExportXML(cmds,vars);
			}
		}
		return(false);
	}

	/**
	*	Handle a $MOD command
	*/
	public String replace(scanpage sp, String cmds) {
		StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("FIELD")) { 
			}
		}
		return("No command defined");
	}

	public void maintainance() {
	}

	public void doExportXML(Hashtable cmds, Hashtable vars) {
		System.out.println("MMExport -> doExportXML started");
		System.out.println("MMExport -> cmd="+cmds);
		System.out.println("MMExport -> vars="+vars);

		String buildername=(String)vars.get("builder");
		String exportdir=(String)vars.get("exportdir")+".xml";

		MMObjectBuilder bul=(MMObjectBuilder)mmb.getMMObject(buildername);
		System.out.println("MMExport -> "+buildername+" "+exportdir+" "+bul);
		if (bul!=null) {
			String body="";
			MMObjectNode node;
			Enumeration e=bul.search("");
			for (;e.hasMoreElements();) {
				node=(MMObjectNode)e.nextElement();
				String xmlpart=node.toXML();
				body+=xmlpart+"\n";
			}	
			saveFile(exportdir,body);
		}
		System.out.println("MMExport -> doExportXML finished");
	}



	public boolean saveFile(String filename,String value) {
		System.out.println("SAVE TO DISK="+filename);
		File sfile = new File(filename);
		try {
			DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
			scan.writeBytes(value);
			scan.flush();
			scan.close();
		} catch(Exception e) {
			e.printStackTrace();
			return(false);
		}
		return(true);
	}

}
