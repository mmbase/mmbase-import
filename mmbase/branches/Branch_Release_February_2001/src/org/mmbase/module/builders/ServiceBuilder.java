/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: ServiceBuilder.java,v 1.11 2000-12-20 16:33:00 vpro Exp $
$Log: not supported by cvs2svn $
Revision 1.10  2000/11/27 13:28:58  vpro
davzev: Disabled sendToRemote call in method nodeRemoteChanged since nodeLocalChanged already calls it, also added several method comments.

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
 * @version $Revision: 1.11 $ $Date: 2000-12-20 16:33:00 $
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

	/**
	 * Sets the node state of this service builder type instance to a 'claimed' state. 
	 * This is done through the reference variable(either alias or objnumber) stored in the 
	 * tok parameter.
	 * It is assumed that the StringTokenizer var contains this type of reference variable.
	 * @param tok a StringTokenizer with a servicebuilder type objectnumber (eg. cdplayers) 
	 * and a username of who used the service.
	 * @return an empty String!?
	 */
	public String doClaim(StringTokenizer tok) {
		if (debug) debug("doClaim("+tok+"): Getting servicebuilder type node reference and username");
		if (tok.hasMoreTokens()) {
			String cdplayer=tok.nextToken();
			if (tok.hasMoreTokens()) {
				String user=tok.nextToken();
				MMObjectNode node=getNode(cdplayer);
				if (node!=null) {
					String name=node.getStringValue("name");
					if (debug) debug("doClaim("+tok+"): Changing "+cdplayer+" name:"+name+"state to 'claimed' for "+user);
					node.setValue("state","claimed");
					node.setValue("info","user="+user+" lease=3");
					node.commit();
					if (debug) debug("doClaim("+tok+"):service: "+name+"successfully claimed.");
				} else {
					debug("doClaim("+tok+"): ERROR: Couldn't get node for "+cdplayer+" claimed by "+user);
				}
			} else {
				debug("doClaim("+tok+"): ERROR: No username in StringTokenizer so I won't claim service: "+cdplayer);
			}
		} else {
			debug("doClaim("+tok+"): ERROR: Empty StringTokenizer so there's nothing to claim!");
		}
		return "";
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
	
	/**
	 * Prints that a remote server (other mmbase or remote builder) changed this service node.
	 * @param number a String with the object number of the node that was operated on.
	 * @param builder a String with the buildername of the node that was operated on.
	 * @param ctype a String with the node change type.
	 * @return true, always!?
	 */
	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		if (debug) debug("nodeRemoteChanged("+number+","+builder+","+ctype+"): Node Remote Changed!?,do nothing");
		// Disabled since the localchanged already sends signal to remote side 
		//sendToRemote(number,builder,ctype); 
		return(true);
	}

	/**
	 * Called when an operation is done on a service node eg insert, commit, it calls method
	 * to send the node change to the remote side.
	 * @param number a String with the object number of the node that was operated on.
	 * @param builder a String with the buildername of the node that was operated on.
	 * @param ctype a String with the node change type.
	 * @return true, always!?
	 */
	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		if (debug) debug("nodeLocalChanged("+number+","+builder+","+ctype+"): Calling sendToRemoteBuilder, to send node change to remote side.");
		sendToRemoteBuilder(number,builder,ctype);
		return(true);
	}

	/**
	 * Sends a signal to indicate that a certain service(builder) node was changed to the remote side.
	 * The information that's send is a service reference name, the builtertype and the mmbase changetype.
	 * Signalling is done using the protocoldriver attached to the first mmserver that in turn is 
	 * attached to the current service(builder) in progress.
	 * @param number a String with the objectnumber of the mmbase service(builder) that was changed.
	 * @param builder a String with the buildername of the node that was changed.
	 * @param ctype a String with the node change type.
	 */
	public void sendToRemoteBuilder(String number,String builder,String ctype) {
		//Get the first mmserver that's attached to the service node number
		Enumeration e=mmb.getInsRel().getRelated(number,"mmservers");
		if  (e.hasMoreElements()) {
			MMObjectNode mmservernode=(MMObjectNode)e.nextElement();	
			String mmservername=mmservernode.getStringValue("name");
			if (debug) debug("sendToRemoteBuilder("+number+","+builder+","+ctype+"): Found attached mmserver:"+mmservername);
			//Get the protocol driver using the mmserver that's attached to current service. 
			MMServers bul = (MMServers)mmb.getMMObject("mmservers");
			ProtocolDriver pd=bul.getDriverByName(mmservername);
			if (debug) debug("sendToRemoteBuilder("+number+","+builder+","+ctype+"): Retrieved it's protocoldriver: "+pd);
			MMObjectNode node=getNode(number);
			if (node!=null) {
				String servicename = node.getStringValue("name");
				if (debug) debug("sendToRemoteBuilder("+number+","+builder+","+ctype+"): Sending signal to remote side using remote service reference:"+servicename);
				pd.signalRemoteNode(servicename,builder,ctype);
				if (debug) debug("sendToRemoteBuilder("+number+","+builder+","+ctype+"): Signal has been send with remote service reference used:"+servicename);
			}else
				debug("sendToRemoteBuilder("+number+","+builder+","+ctype+"): ERROR: Couldn't get node("+number+")!");
		} else {
			debug("sendToRemoteBuilder("+number+","+builder+","+ctype+"): ERROR: No related mmserver found attached to this service with number:"+number);
		}
	}
}
