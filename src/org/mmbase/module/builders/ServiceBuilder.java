/*

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

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.builders.protocoldrivers.*;
import org.mmbase.util.*;


/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class ServiceBuilder extends MMObjectBuilder implements MMBaseObserver {

	private String classname = getClass().getName();
	private boolean debug = true;
	// private void debug(String msg){System.out.println(classname+":"+msg);}

	Hashtable cache=null;

	public boolean init() {
		MMServers bul=(MMServers)mmb.getMMObject("mmservers");
		if (bul!=null) {
			bul.setCheckService(getTableName());
		} else {
			if( debug ) debug("setTableName("+tableName+"): ERROR: mmservers not found!");
		}
		return(true);
	}

	
	public String doClaim(StringTokenizer tok) {
		boolean 		result 		= false;

		String 			cdplayer 	= null;
		String 			name		= null;
		MMObjectNode 	node 		= null;

		if( debug ) debug("doClaim("+tok+"), getting username and nodenr..");

		if (tok.hasMoreTokens()) {
			cdplayer=tok.nextToken();
			if (tok.hasMoreTokens()) {
				name=tok.nextToken();
				node=getNode(cdplayer);
				if (node!=null) {
					if( debug ) debug("doClaim("+cdplayer+","+name+"): claiming resource");
					node.setValue("state","claimed");
					node.setValue("info","user="+name+" lease=3");
					node.commit();
					result = true;
				}
				else
					debug("doClaim("+cdplayer+","+name+"): ERROR: Could not get node for this cdplayer("+cdplayer+")!");
			}
		}
		if( result ) 
		{
			if( debug ) debug("doClaim("+cdplayer+","+name+"), successfully claimed.");
		}
		else
			debug("doClaim("+cdplayer+","+name+"): ERROR: Not claimed!");
			
		return("");
	}


	public MMObjectNode getClaimedBy(String owner) {
		MMObjectNode result	= null;
		if( owner != null ) {

			if( debug ) debug("getClaimedBy("+owner+"), seeking nodenr..");
		
			MMObjectNode node 	= null;
			MMObjectNode node2 	= null;
			if (cache==null) cache=getCache();
	
			// hunt down the claimed node by this user
			// ---------------------------------------
			Enumeration e=cache.elements();
			while (e.hasMoreElements()) {
				node=(MMObjectNode)e.nextElement();
				if( debug ) debug("getClaimedBy("+owner+"): node("+node+")");
	
				node2=getNode(node.getIntValue("number"));

				if( node2 != null ) {
					if( debug ) debug("getClaimedBy("+owner+"): node2("+node2+")");
		
					String info=node2.getStringValue("info");
					if( info != null ) { 
						StringTagger tagger=new StringTagger(info);
						String user=tagger.Value("user");
			
						if (user!=null && user.equals(owner)) {
							if( debug ) debug("getClaimedBy("+owner+"): found node("+node2+")");
							result=node2;
						}
					} else { 
						debug("getClaimedBy("+owner+"): ERROR: in node("+node2+"): no info-field detected!");
					}
				} else { 
					debug("getClaimedBy("+owner+"): ERROR: node("+node+"), node2("+node2+") is null!");
				}
			}
		} else { 
			debug("getClaimedBy("+owner+"): ERROR: No owner!");
		}

		if (result!=null) 
			return(getNode(result.getIntValue("number")));
		else
			return(null);
	}

	public Hashtable getCache() {
		Hashtable cache=new Hashtable();
		Enumeration e=search("WHERE number>0");
		while (e.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			int number=node.getIntValue("number");
			cache.put(new Integer(number),node);
		}
		if( debug ) 
			debug("getCache(): cache("+cache+"), size("+cache.size()+")");
		return(cache);
	}


	// adds a service to this builder, name is allways valid and correct
	// the Service builder _must_ update all the mmbase admins to reflect
	// new state. Next version will support name with authentication.
	public void addService(String name, String localclass, MMObjectNode mmserver) throws Exception {

		boolean result = false;

		if( debug ) debug("addService("+name+","+localclass+","+mmserver+"), inserting.");

		//System.out.println("Service Builder ("+tableName+") add service called !");
		//System.out.println("Service Builder "+name+" "+localclass+" "+mmserver+" ");

		if (debug) debug("addService : CREATING A NEW OBJECT NOW !!!!!!!!!!");
		MMObjectNode newnode=getNewNode("system");
		newnode.setValue("name",name);
		newnode.setValue("cdtype","C="+localclass); // Warning this will be altered to devtype
		newnode.setValue("state","waiting");
		newnode.setValue("info","");
		int newid=insert("system",newnode);
		if (debug) debug("addService : CREATING A NEW OBJECT NOW !!!!!!!!!! IS DONE"+(new java.util.Date()).toGMTString());
		if (newid!=-1) {
			InsRel bul=(InsRel)mmb.getMMObject("insrel");
			bul.insert("system",newid,mmserver.getIntValue("number"),14);	
			result = true;
		}

		if( result ) 
		{
			if( debug )	
				debug("addService("+name+","+localclass+","+mmserver+"): successfully inserted");
		}
		else
			debug("addService("+name+","+localclass+","+mmserver+"): ERROR: failure to insert!");
	}
	
	// adds a service to this builder, name is allways valid and correct
	// the Service builder _must_ update all the mmbase admins to reflect
	// new state. Next version will support name with authentication.
	// adds a init hashtable for use for authentication and other startup
	// params.
	public void addService(String name, String localclass, MMObjectNode mmserver, Hashtable initparams) throws Exception {

		if( debug ) debug("addService("+name+","+localclass+","+mmserver+","+initparams+"), called, but we do nothing.");
		//System.out.println("Service Builder ("+tableName+") add service called !");
		//System.out.println("Service Builder "+name+" "+localclass+" "+mmserver+" "+initparams);
		
	}

	// remove a service, does not mean it will be 100% removed from mmbase 
	// but that its not running at the moment. possible model will be removed 
	// results in offline state for X hours and removed in X days.
	public void removeService(String name) throws Exception {
		if( debug ) debug("removeService("+name+"), called, but we do nothing.");

	}

	// remove a service, does not mean it will be 100% removed from mmbase 
	// but that its not running at the moment. possible model will be removed 
	// results in offline state for X hours and removed in X days.
	public void removeService(String name, Hashtable initparams) throws Exception {
		if( debug ) debug("removeService("+name+","+initparams+"), called, but we do nothing.");

	}


	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		if( debug ) debug("nodeRemoteChanged("+number+","+builder+","+ctype+"), sending to remote");
		sendToRemote(number,builder,ctype);
		return(true);
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		if( debug ) debug("nodeLocalChanged("+number+","+builder+","+ctype+"), sending to remote");
		System.out.println("Service Node local ="+number);
		sendToRemote(number,builder,ctype);
		return(true);
	}

	public void sendToRemote(String number,String builder,String ctype) {

		if( debug ) debug("sendToRemote("+number+","+builder+","+ctype+"), sending to remote..");

		boolean result = false;
		String name = null;
		String nodename = null;
		MMObjectNode node = null;
		MMObjectNode thisnode = null;

		MMServers bul = (MMServers)mmb.getMMObject("mmservers");
		
		// figure out what server we are attached to.
		// ------------------------------------------
		Enumeration e=mmb.getInsRel().getRelated(number,"mmservers");
		if  (e.hasMoreElements()) {
			node=(MMObjectNode)e.nextElement();	
			if(debug)debug("sendToRemote(): got node("+node+"), checking name..");
			name=node.getStringValue("name");
			if(debug)debug("sendToRemote(): got name("+name+"), getting proto..");
			ProtocolDriver pd=bul.getDriverByName(name);
			if(debug)debug("sendToRemote(): got prot("+pd  +"), getting  node..");
			thisnode=getNode(number);
			if (thisnode!=null) {
				nodename = thisnode.getStringValue("name");
				if( debug ) debug("sendToRemote("+number+","+builder+","+ctype+"), sending a signalRemoteNode("+nodename+","+builder+","+ctype+")..");
				pd.signalRemoteNode(nodename,builder,ctype);
				result = true;
			}
			else
				debug("sendToRemote("+number+","+builder+","+ctype+"): ERROR: Could not get node("+number+")!");
		}

		if( result ) 	
		{
			if( debug ) debug("sendToRemote("+number+","+builder+","+ctype+"), successfully signalled to node("+nodename+") with number("+number+")");
		}
		else
			debug("sendToRemote("+number+","+builder+","+ctype+"): ERROR: could not signal node("+nodename+") with number("+number+")");  
	}
}
