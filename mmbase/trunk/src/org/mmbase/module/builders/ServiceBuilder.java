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

	Hashtable cache=null;

	public void setTableName(String tableName) {
		super.setTableName(tableName);
		MMServers bul=(MMServers)mmb.getMMObject("mmservers");
		if (bul!=null) {
			bul.setCheckService(tableName);
		}
	}

	
	public String doClaim(StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String cdplayer=tok.nextToken();
			if (tok.hasMoreTokens()) {
				String name=tok.nextToken();
				MMObjectNode node=getNode(cdplayer);
				if (node!=null) {
					node.setValue("state","claimed");
					node.setValue("info","user="+name+" lease=3");
					node.commit();
				}
			}
		}
		return("");
	}


	public MMObjectNode getClaimedBy(String owner) {
		MMObjectNode result=null;

		if (cache==null) cache=getCache();
		// hunt down the claimed node by this user
		Enumeration e=cache.elements();
		while (e.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			System.out.println("CLAIMNode="+node);
			node=getNode(node.getIntValue("number"));
			System.out.println("CLAIMNode2="+node);
			String info=node.getStringValue("info");
			StringTagger tagger=new StringTagger(info);
			String user=tagger.Value("user");
			if (user!=null && user.equals(owner)) {
				System.out.println("USER CLAIMED FOUND ! : "+node);
				result=node;
			}
		}
		if (result!=null) return(getNode(result.getIntValue("number")));
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
		System.out.println("CACHE="+cache);
		return(cache);
	}


	// adds a service to this builder, name is allways valid and correct
	// the Service builder _must_ update all the mmbase admins to reflect
	// new state. Next version will support name with authentication.
	public void addService(String name, String localclass, MMObjectNode mmserver) throws Exception {
		System.out.println("Service Builder ("+tableName+") add service called !");
		System.out.println("Service Builder "+name+" "+localclass+" "+mmserver+" ");
		MMObjectNode newnode=getNewNode("system");
		newnode.setValue("name",name);
		newnode.setValue("devtype","class="+localclass);
		newnode.setValue("state","waiting");
		newnode.setValue("info","");
		int newid=insert("system",newnode);
		if (newid!=-1) {
			InsRel bul=(InsRel)mmb.getMMObject("insrel");
			RelDef bul2=(RelDef)mmb.getMMObject("reldef");
			bul.insert("system",newid,mmserver.getIntValue("number"),bul2.getGuessedByName("related"));
		}
		
	}
	
	// adds a service to this builder, name is allways valid and correct
	// the Service builder _must_ update all the mmbase admins to reflect
	// new state. Next version will support name with authentication.
	// adds a init hashtable for use for authentication and other startup
	// params.
	public void addService(String name, String localclass, MMObjectNode mmserver, Hashtable initparams) throws Exception {
		System.out.println("Service Builder ("+tableName+") add service called !");
		System.out.println("Service Builder "+name+" "+localclass+" "+mmserver+" "+initparams);
		
	}

	// remove a service, does not mean it will be 100% removed from mmbase 
	// but that its not running at the moment. possible model will be removed 
	// results in offline state for X hours and removed in X days.
	public void removeService(String name) throws Exception {
	}

	// remove a service, does not mean it will be 100% removed from mmbase 
	// but that its not running at the moment. possible model will be removed 
	// results in offline state for X hours and removed in X days.
	public void removeService(String name, Hashtable initparams) throws Exception {
	}


	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		System.out.println("Service Node remote ="+number);
		sendToRemote(number,builder,ctype);
		return(true);
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		System.out.println("Service Node local ="+number);
		sendToRemote(number,builder,ctype);
		return(true);
	}

	public void sendToRemote(String number,String builder,String ctype) {
		// figure out what server we are attached to.
		Enumeration e=mmb.getInsRel().getRelated(number,"mmservers");
		if  (e.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)e.nextElement();	
			String name=node.getStringValue("name");
			MMServers bul=(MMServers)mmb.getMMObject("mmservers");
			ProtocolDriver pd=bul.getDriverByName(name);
			MMObjectNode thisnode=getNode(number);
			if (thisnode!=null) {
				pd.signalRemoteNode(thisnode.getStringValue("name"),builder,ctype);
			}
		}	
	}
}
