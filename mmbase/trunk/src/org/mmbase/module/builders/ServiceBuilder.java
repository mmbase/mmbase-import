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
import org.mmbase.module.sessionsInterface;
import org.mmbase.module.sessionInfo;
import org.mmbase.util.logging.*;

/**
 * @author Daniel Ockeloen
 * @version $Revision: 1.16 $ $Date: 2001-04-05 12:18:51 $
 */
public class ServiceBuilder extends MMObjectBuilder implements MMBaseObserver {

    private static Logger log = Logging.getLoggerInstance(ServiceBuilder.class.getName());

	// Cache of service nodes 
	Hashtable cache=null;

	// Used for retrieving selected cdtrack.
	private sessionsInterface sessions; 

	public boolean init() {
		// Initializing builder.
		super.init();
		MMServers bul=(MMServers)mmb.getMMObject("mmservers");
		if (bul!=null) {
			bul.setCheckService(getTableName());
		} else {
			log.error("setTableName("+tableName+"): ERROR: mmservers not found!");
		}

		// Used for retrieving selected cdtrack.
		sessions = (sessionsInterface) org.mmbase.module.Module.getModule("SESSION");
		return true;
	}

	/**
	 * Claims (state=claimed) the service node and fills its info field with owner and selected tracknr
	 * that has to be ripped. The tracknr is retrieved from the session var -> name='serviceobj#TRACKNR'
	 * This is done through the reference variable(either alias or objnumber) stored in tok parameter.
	 * @param sp the scanpage object.
	 * @param tok a StringTokenizer with a servicebuilder type objectnumber (eg. cdplayers) 
	 * and a username of who used the service.
	 * @return an empty String!?
	 */
	public String doClaim(scanpage sp, StringTokenizer tok) {
		log.debug("doClaim: Getting servicebuildertype node reference and username");
		if (tok.hasMoreTokens()) {
			String cdplayernumber=tok.nextToken();
			if (tok.hasMoreTokens()) {
				String user=tok.nextToken();
				MMObjectNode node=getNode(cdplayernumber);
				if (node!=null) {
					String name=node.getStringValue("name");
					
					// Retrieve selected track from session (instead from ap.info) 
					// and add it together with user & lease to cdplayers.info.
					String tracknr=null;
					sessionInfo session=sessions.getSession(sp,sp.sname);
					tracknr = sessions.getValue(session,cdplayernumber+"TRACKNR");
					if (tracknr!=null) {
						log.debug("doClaim: Retrieved tracknr from session value="+tracknr);
						log.debug("doClaim: Filling info field with: user="+user+" lease=3"+" tracknr="+tracknr);
						node.setValue("info","user="+user+" lease=3"+" tracknr="+tracknr);
						log.debug("doClaim: Changing "+name+" (obj "+cdplayernumber+") state from '"+node.getStringValue("state")+"' to 'claimed'");
						node.setValue("state","claimed");
					} else {
						log.error("doClaim: ERROR: Can't get tracknr from session '"+cdplayernumber+"TRACKNR'");
						node.setValue("state","error");
						node.setValue("info","ERROR: doClaim(): Can't get tracknr from session '"+cdplayernumber+"TRACKNR' value="+tracknr);
					}
					node.commit();
					log.debug("doClaim: Service "+name+" (obj "+cdplayernumber+") successfully claimed.");
				} else
					log.error("doClaim: ERROR: Couldn't get node for "+cdplayernumber+" claimed by "+user);
			} else
				log.error("doClaim: ERROR: No username in StringTokenizer so I won't claim service: "+cdplayernumber);
		} else
			log.error("doClaim: ERROR: Empty StringTokenizer so there's nothing to claim!");
		return "";
	}

	/**
	 * Gets the first service node (cdplayers) that was claimed by a certain user. 
	 * @param owner a String with the name of the owner that claimed the service.
	 * @return the service node that was claimed.
	 */
	public MMObjectNode getClaimedBy(String owner) {
		if(owner != null) {
			log.debug("getClaimedBy("+owner+"): Seeking service node that was claimed by "+owner);
			int number;
			MMObjectNode node = null;

			// Cache built in to relief database of frequent queries service node (cdplayers) table.
			if (cache==null) cache=getCache();
	
			// Hunt down the claimed node by checking the info field for the 'user=username' tag.
			Enumeration e=cache.keys();
			while (e.hasMoreElements()) {
				number = ((Integer)e.nextElement()).intValue();
				node = getNode(number);
				if(node!=null) {
					log.debug("getClaimedBy("+owner+"): Possible candidate node: "+node.getStringValue("name")+" number:"+number);
					String info=node.getStringValue("info");
					if(info!=null) { 
						StringTagger tagger=new StringTagger(info);
						String user=tagger.Value("user");
						if ((user!=null) && user.equals(owner)) {
							log.debug("getClaimedBy("+owner+"): Found claimed node!: "+node);
							return node;
						}
					} else
						log.error("getClaimedBy("+owner+"): ERROR: 'info' field is null for node:"+node);
				} else
					log.error("getClaimedBy("+owner+"): ERROR: Can't get node for number "+number);
			}
		} else
			log.error("getClaimedBy("+owner+"): ERROR: No owner value="+owner);
		return null;
	}

	/**
	 * Creates a new hashtable and fills it with all the service nodes.
	 * @return a Hashtable with nodes as key=number, value=node.
	 */
	public Hashtable getCache() {
		Hashtable cache=new Hashtable();
		Enumeration e=search("WHERE number>0");
		while (e.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			int number=node.getIntValue("number");
			cache.put(new Integer(number),node);
		}
		log.debug("getCache(): Created cache, size("+cache.size()+") contents="+cache);
		return(cache);
	}


	/**
	 * adds a service to this builder, name is allways valid and correct
	 * the Service builder _must_ update all the mmbase admins to reflect
	 * new state. Next version will support name with authentication.
	 */
	public void addService(String name, String localclass, MMObjectNode mmserver) throws Exception {
		boolean result = false;
		log.info("addService("+name+","+localclass+","+mmserver+"), inserting.");

		log.debug("addService - creating a new object now !
		MMObjectNode newnode=getNewNode("system");
		newnode.setValue("name",name);
		newnode.setValue("cdtype","C="+localclass); // Warning this will be altered to devtype
		newnode.setValue("state","waiting");
		newnode.setValue("info","");
		int newid=insert("system",newnode);
		log.debug("addService : Creating a new object now ! Is done"+(new java.util.Date()).toGMTString());
		if (newid!=-1) {
			InsRel bul=(InsRel)mmb.getMMObject("insrel");
			bul.insert("system",newid,mmserver.getIntValue("number"),14);	
			result = true;
		}

		if(result) {
			log.debug("addService("+name+","+localclass+","+mmserver+"): successfully inserted");
		} else {
			log.error("addService("+name+","+localclass+","+mmserver+"): ERROR: failure to insert!");
		}
	}

	/**	
	 * adds a service to this builder. name is allways valid and correct
	 * the Service builder _must_ update all the mmbase admins to reflect
	 * new state. Next version will support name with authentication.
	 * adds a init hashtable for use for authentication and other startup
	 * params.
	 */
	public void addService(String name, String localclass, MMObjectNode mmserver, Hashtable initparams) throws Exception {
		log.debug("addService("+name+","+localclass+","+mmserver+","+initparams+"), called, but we do nothing.");
	}

	/**
	 * remove a service. Does not mean it will be 100% removed from mmbase 
	 * but that its not running at the moment. possible model will be removed 
	 * results in offline state for X hours and removed in X days.
	 */
	public void removeService(String name) throws Exception {
		log.debug("removeService("+name+"), called, but we do nothing.");
	}

	/**
	 * remove a service. does not mean it will be 100% removed from mmbase 
	 * but that its not running at the moment. possible model will be removed 
	 * results in offline state for X hours and removed in X days.
	 */
	public void removeService(String name, Hashtable initparams) throws Exception {
		log.debug("removeService("+name+","+initparams+"), called, but we do nothing.");
	}
	
	/**
	 * Prints that a remote server (other mmbase or remote builder) changed this service node.
	 * @param number a String with the object number of the node that was operated on.
	 * @param builder a String with the buildername of the node that was operated on.
	 * @param ctype a String with the node change type.
	 * @return true, always!?
	 */
	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		log.debug("nodeRemoteChanged("+number+","+builder+","+ctype+"): Calling super.");
		super.nodeRemoteChanged(number,builder,ctype);
		// Don't sendToRemoteBuilder because nodeLocalChanged already sends a signal to remote side.
		log.debug("nodeRemoteChanged("+number+","+builder+","+ctype+"): Node Remote Changed!?,do nothing");
		return true;
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
		log.debug("nodeLocalChanged("+number+","+builder+","+ctype+"): Calling super.");
		super.nodeLocalChanged(number,builder,ctype);
		log.debug("nodeLocalChanged("+number+","+builder+","+ctype+"): Calling sendToRemoteBuilder, to send node change to remote side.");
		// You can't signal new or delete changes because there's no mmserver related when these changes occur.
		if (!(ctype.equals("n") || ctype.equals("d")) )
			sendToRemoteBuilder(number,builder,ctype);
		return true;
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
			log.debug("sendToRemoteBuilder("+number+","+builder+","+ctype+"): Found attached mmserver:"+mmservername);
			//Get the protocol driver using the mmserver that's attached to current service. 
			MMServers bul = (MMServers)mmb.getMMObject("mmservers");
			ProtocolDriver pd=bul.getDriverByName(mmservername);
			log.debug("sendToRemoteBuilder("+number+","+builder+","+ctype+"): Retrieved its protocoldriver: "+pd);
			MMObjectNode node=getNode(number);
			if (node!=null) {
				String servicename = node.getStringValue("name");
				log.debug("sendToRemoteBuilder("+number+","+builder+","+ctype+"): Sending signal to remote side using remote service reference:"+servicename);
				pd.signalRemoteNode(servicename,builder,ctype);
				log.debug("sendToRemoteBuilder("+number+","+builder+","+ctype+"): Signal has been send with remote service reference used:"+servicename);
			}else
				log.error("sendToRemoteBuilder("+number+","+builder+","+ctype+"): ERROR: Couldn't get node("+number+")!");
		} else {
			log.error("sendToRemoteBuilder("+number+","+builder+","+ctype+"): ERROR: No related mmserver found attached to this service with number:"+number);
		}
	}
}
