/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

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

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @rename Cdplayers
  * @author Daniel Ockeloen
 * @version $Revision: 1.13 $ $Date: 2001-12-14 09:33:18 $ 
 */
public class cdplayers extends ServiceBuilder implements MMBaseObserver {

	private static Logger log = Logging.getLoggerInstance(Users.class.getName()); 

	/**
	 * Calls super.
	 * @param machine Name of the machine that changed the node.
	 * @param number object number of node who's state has been changed remotely.
	 * @param builder a String with the buildername of the node that was changed remotely.
	 * @param ctype a String with the node change type.
	 * @return result value of suer which is either true or false.
	 */
	public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
		return super.nodeRemoteChanged(machine,number,builder,ctype);
	}

	/**
	 * Calls super.
	 * @param machine Name of the machine that changed the node.
	 * @param number object number of node who's state has been changed.
	 * @param builder a String with the buildername of the node that was changed.
	 * @param ctype a String with the node change type.
	 * @return result value of suer which is either true or false.
	 */
	public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
		return super.nodeLocalChanged(machine,number,builder,ctype);
	}

	/**
	 * Returns the value for a requested node field.
	 * When the field is 'getdir(info)' the node state is set to 'getdir'
	 * and we wait until node is changed.
	 * If the state has become 'waiting' we return info field value, 
	 * else we wait again.
	 * @param node a cdplayers type node
	 * @param field the field requested
	 * @return the request field value
	 */
	public Object getValue(MMObjectNode node,String field) {
		if (field.equals("getdir(info)")) {
			// send the command to get the dir
			node.setValue("state","getdir");
			node.commit();
			if (log.isDebugEnabled()) {
				log.info("State set to getdir, now wait until node is changed "
				        + " sothat dir is available.");
			}
			boolean changed=false;
			MMObjectNode newnode=null;
			while (!changed) {	
				waitUntilNodeChanged(node);
				newnode=getHardNode(node.getIntValue("number"));
				String state=newnode.getStringValue("state");
				if (state.equals("waiting"))
					changed=true;
				else 
					if (log.isDebugEnabled()) {
						log.debug("Retrieved node " + newnode.getStringValue("name")
						        + " ("+newnode.getIntValue("number") + "), state is "+state
						        + "!=waiting, entering waitUntilNodeChanged again");
					}
			}
			String val=newnode.getStringValue("info");
			if (log.isDebugEnabled()) {
				log.debug(newnode.getStringValue("name")+"("+newnode.getIntValue("number")
			        + ") State is waiting again, " +"returning: " + val);
			}
			return(val);
		} else return super.getValue(node, field);
	}

	/**
	 * Generate a list of values depending on the command given.
	 * This getList returns the tracklisting from the cd that's in the cdplayer.
	 * @param sp the scanpage
	 * @param tagger the StringTagger object
	 * @param tok tokenizer with the listing command.
	 * @return a Vector with the tracklisting
	 */
	public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) 
	        throws ParseException {
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("getdir")) 
				return(getHTMLDir(tagger,tok));
		}
		return null;
	}

	/**
	 * Requests for the trackslisting and parses it and returns it as a 
	 * vector (tracknr,title,length).
	 * @param tagger the StringTagger object with the rest of the list commands.
	 * @param tok tokenizer object
	 * @return a vector with the tracklisting
	 */
	public Vector getHTMLDir(StringTagger tagger, StringTokenizer tok) {
		Vector result=new Vector();
		String id=tagger.Value("NODE");
		MMObjectNode node = null;
		try {
			node=getHardNode(Integer.parseInt(id));
		} catch (NumberFormatException nfe) {
			log.error("id:"+id+" is not an integer!, " + Logging.stackTrace(nfe));
		}

		if (node!=null) {
			String info=(String)getValue(node,"getdir(info)");
			StringTagger infotagger=new StringTagger(info);
			String nroftracks = infotagger.Value("NROFTRACKS");	
			try {
				int i=Integer.parseInt(nroftracks);	
				for (int j=0;j<i;j++) {
					String trlen=infotagger.Value("TR"+j+"LEN");
					String title=infotagger.Value("TR"+j+"TITLE");
					result.addElement(""+(j+1));
					result.addElement(title);
					result.addElement(trlen);
				}
			} catch(NumberFormatException nfe) {
				log.error("NROFTRACKS:"+nroftracks+" is not an integer!, " + Logging.stackTrace(nfe));
			}
		}
		tagger.setValue("ITEMS","3");
		log.service("Returning result: " + result);
		return result;
	}

	/**
	 * Replace a command by a string.
	 * Command implemented is the claim command to claim the cdplayers node.
	 * @param sp the scanpage
	 * @param sp the StringTokenizer with the replace command.
	 * @return an empty String 
	 */
	public String replace(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();
			if (log.isDebugEnabled()) {
				log.debug("Token is " + cmd);
			}
			if (cmd.equals("claim")) {
				if (log.isDebugEnabled()) { 
					log.info("Command is 'claim', calling doClaim(sp,tok)");
				}
				doClaim(sp,tok);
			}
		}
		return("");
	}
}
