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
 * @version $Revision: 1.24 $ $Date: 2001-05-09 10:08:00 $
 */
public class ServiceBuilder extends MMObjectBuilder implements MMBaseObserver {

    private static Logger log = Logging.getLoggerInstance(ServiceBuilder.class.getName());

	// Used for retrieving selected cdtrack.
	private sessionsInterface sessions; 

	/**
	 * Initializes builder, calls setCheckService? and loads session module. 
	 * @return true.
	 */	
	public boolean init() {
		super.init();
		MMServers bul=(MMServers)mmb.getMMObject("mmservers");
		if (bul!=null)
			bul.setCheckService(getTableName());
		else
			log.error("Can't get mmservers builder.");

		// Used for retrieving selected cdtrack.
		sessions = (sessionsInterface) org.mmbase.module.Module.getModule("SESSION");
		return true;
	}

	/**
	 * Claims (state=claimed) the service node and fills its info field with 
	 * owner and selected tracknr that has to be ripped. 
	 * The tracknr is retrieved from the session var -> name='serviceobj#TRACKNR'
	 * This is done through a reference variable(either alias or objnumber) stored in tok parameter.
	 * @param sp the scanpage object.
	 * @param tok a StringTokenizer with a servicebuilder type objectnumber (eg. cdplayers) 
	 * and a username of who used the service.
	 * @return an empty String!?
	 */
	public String doClaim(scanpage sp, StringTokenizer tok) {
		log.debug("Getting servicebuildertype node reference and username.");
		if (tok.hasMoreTokens()) {
			String cdplayernumber=tok.nextToken();
			if (tok.hasMoreTokens()) {
				String user=tok.nextToken();
				MMObjectNode node = null;
				try {
					node=getHardNode(Integer.parseInt(cdplayernumber));
				} catch (NumberFormatException nfe) {
					log.error("cdplayerobjnumber:"+cdplayernumber+" is not an integer!, "+Logging.stackTrace(nfe));
				}
				if (node!=null) {
					String name=node.getStringValue("name");
					
					// Retrieve selected track from session (instead from ap.info) 
					// and add it together with user & lease to cdplayers.info.
					String tracknr=null;
					sessionInfo session=sessions.getSession(sp,sp.sname);
					tracknr = sessions.getValue(session,cdplayernumber+"TRACKNR");
					if (tracknr!=null) {
						log.debug("Retrieved tracknr from session value="+tracknr);
						log.debug("Filling info field with: user="+user+" lease=3"
						        +" tracknr="+tracknr);
						node.setValue("info","user="+user+" lease=3"+" tracknr="+tracknr);
						log.debug("Changing "+name+" ("+cdplayernumber+") state "
						        +"from '"+node.getStringValue("state")+"' to 'claimed'");
						node.setValue("state","claimed");
					} else {
						log.error("Can't get tracknr from session '"+cdplayernumber+"TRACKNR'");
						node.setValue("state","error");
						node.setValue("info","ERROR: doClaim(): Can't get tracknr from "
						        +"session '"+cdplayernumber+"TRACKNR' value="+tracknr);
					}
					node.commit();
					log.debug("Service "+name+" ("+cdplayernumber+") successfully claimed by "+user);
				} else
					log.error("Can't claim service "+cdplayernumber+" for user:"+user+" because node is null");
			} else
				log.error("No username in StringTokenizer so I won't claim service: "+cdplayernumber);
		} else
			log.error("Empty StringTokenizer so there's nothing to claim!");
		return "";
	}

	/**
	 * Gets the first service node (cdplayers) that was claimed by a certain user. 
	 * @param owner a String with the name of the owner that claimed the service.
	 * @return the service node that was claimed.
	 */
	public MMObjectNode getClaimedService(String owner) {
		if(owner == null) {
			log.error("Owner value = null, returning null");
			return null;
		}
		MMObjectNode node = null;
		String info=null, user=null;
		StringTagger infotagger = null;
		log.info("Searching for the service that was claimed by "+owner);
		Enumeration e=search("WHERE state='claimed' AND number>0");
		while (e.hasMoreElements()) {
			node= (MMObjectNode)e.nextElement();
			info = node.getStringValue("info");
			if (info!=null) { 
				infotagger=new StringTagger(info);
				if (infotagger.containsKey("user")) {
					user=infotagger.Value("user");
					if (user!=null) {
						if (user.equals(owner)) {
							String name = node.getStringValue("name");
							log.info("("+owner+"): Found claimed node "+name+" of type "+node.getName());
							return node;
						} else 
							log.debug("("+owner+"): Wrong user:"+user+", skipping.");
					} else 
						log.error("("+owner+"): User value = null, for node:"+node+", skipping.");
				} else
					log.debug("("+owner+"): Key 'user' not found in info field, info:"+info+", skipping.");
			} else
				log.error("("+owner+"): 'info' field is null for node:"+node+", skipping.");
		}
		log.error("("+owner+"): Can't find any services claimed by "+owner+", returning null.");
		return null;
	}
	
	/**
	 * adds a service to this builder, name is allways valid and correct
	 * the Service builder _must_ update all the mmbase admins to reflect
	 * new state. Next version will support name with authentication.
	 *
	 * Currently always a service of type cdplayers is expected for insertion.
	 */
	public void addService(String name, String localclass, MMObjectNode mmserver) 
	        throws Exception {
		log.info("Adding new "+getTableName()+" service called: "+name+", and "
		        +"connecting it to mmserver "+mmserver.getStringValue("name"));
		MMObjectNode newnode=getNewNode("system");
		newnode.setValue("name",name);
		// cdtype should be altered to devtype.
		newnode.setValue("cdtype","C="+localclass); 
		newnode.setValue("state","waiting");
		newnode.setValue("info","");
		int newid=insert("system",newnode);
		if (newid!=-1) {
			log.info(getTableName()+" SERVICE: "+name+" ADDED.");
			InsRel bul=(InsRel)mmb.getMMObject("insrel");
			int relid = bul.insert("system",newid,mmserver.getIntValue("number"),14);
			if (relid!=-1)
				log.info("RELATION INSERTED between service: "+name+" ("+newid+")"
				        +" and mmserver: "+mmserver.getIntValue("number"));
			else
				log.error("INSERT RELATION FAILED between service: "+name
				        +" ("+newid+") and mmserver: "+mmserver.getIntValue("number"));
		} else
			log.error("INSERT FAILED for "+getTableName()+" service: "+name);
	}

	/**	
	 * adds a service to this builder. name is allways valid and correct
	 * the Service builder _must_ update all the mmbase admins to reflect
	 * new state. Next version will support name with authentication.
	 * adds a init hashtable for use for authentication and other startup
	 * params.
	 */
	public void addService(String name,String localclass,MMObjectNode mmserver,
	        Hashtable initparams) throws Exception {
		log.debug("("+name+","+localclass+","+mmserver+","+initparams+"): "
		        +"Called, but we do nothing.");
	}

	/**
	 * remove a service. Does not mean it will be 100% removed from mmbase 
	 * but that its not running at the moment. possible model will be removed
	 * results in offline state for X hours and removed in X days.
	 */
	public void removeService(String name) throws Exception {
		log.debug("("+name+"), called, but we do nothing.");
	}

	/**
	 * remove a service. does not mean it will be 100% removed from mmbase 
	 * but that its not running at the moment. possible model will be removed 
	 * results in offline state for X hours and removed in X days.
	 */
	public void removeService(String name,Hashtable initparams) throws Exception {
		log.debug("("+name+","+initparams+"), called, but we do nothing.");
	}
	
	/**
	 * Called when another mmbse changed the service, if so then we print 
	 * service state and info contents..
	 * @param machine Name of the machine that changed the node.
	 * @param number Object number of the changed service.
	 * @param builder The buildername of the changed service.
	 * @param ctype The node change type.
	 * @return true, always!?
	 */
	public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
		super.nodeRemoteChanged(machine,number,builder,ctype);

		// Print state and info contents.
		if (!ctype.equals("d")) {
			try {
				MMObjectNode node = getHardNode(Integer.parseInt(number));
				if (log.isDebugEnabled()) {
					log.debug("("+machine+","+number+","+builder+","+ctype+"): Printing state="
				            + node.getStringValue("state") + " and info="
				            + node.getStringValue("info") + " , returning.");
				}
			} catch (NumberFormatException nfe) {
				log.error("number:"+number+" is not an integer!, " + Logging.stackTrace(nfe));
			}
		}

		// Don't call sendToRemoteBuilder because nodeLocalChanged already 
		// sends a signal to remote side.
		return true;
	}

	/**
	 * Called when an operation is done on a service node eg insert, commit, it calls method
	 * to send the node change to the remote side.
	 * However, if change was done by the remote side (change to busy, or change to waiting 
	 * with exitvalue), we don't send this change to the remote side. 
	 * Also node change of type new 'n' or delete 'd' aren't sent to remote side.
	 * @param machine Name of the machine that changed the node.
	 * @param number a String with the object number of the node that was operated on.
	 * @param builder a String with the buildername of the node that was operated on.
	 * @param ctype a String with the node change type.
	 * @return true, always!?
	 */
	public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
		super.nodeLocalChanged(machine,number,builder,ctype);
		try {
			int num = 0;
			MMObjectNode node = null;
			String state=null, info=null;
			// Print state and info contents.
			if (!ctype.equals("d")) {
					num = Integer.parseInt(number);
					node = getHardNode(num);
					state= node.getStringValue("state");
					info = node.getStringValue("info");
					if (log.isDebugEnabled()) {
						log.debug("("+machine+","+number+","+builder+","+ctype+"): "
						        +"Printing state="+state+" and info="+info+", returning.");
					}
			}

			// You can't signal new or delete changes because there's no 
			// mmserver related when these changes occur.
			if (!(ctype.equals("n") || ctype.equals("d")) ) {
				// If the node change was performed by the remote side then we don't 
				// send te node change signal back to the remote side.
				// Changes done by remote are when service tells us he's busy or when he tells us
				// he just finished encoding.
				if (state.equals("busy") || (state.equals("waiting") && (!info.equals(""))) ) {
					log.debug("Not sending to remote side because this change was done "
					        +"by remote side, state:"+state+", info:"+info);
				} else {
					log.debug("Calling sendToRemoteBuilder to send node change to remote side.");
					sendToRemoteBuilder(num,builder,ctype);
				}
			} else {
				log.debug("Not sending to remote side since because ctype="+ctype);
			} 
		} catch (NumberFormatException nfe) {
			log.error("number:"+number+" is not an integer!, wont signal to remote builder! "+Logging.stackTrace(nfe));
		}
		return true;
	}

	/**
	 * Sends a signal to the remote side to indicate that service has been changed.
	 * The information sent is a service reference name, buildertype and change type.
	 * Signalling is done using the protocoldriver attached to the first mmserver 
	 * that in turn is attached to the current service(builder) in progress.
	 * @param number Objectnumber of the changed service.
	 * @param builder The buildername of changed service.
	 * @param ctype The node change type.
	 */
	public void sendToRemoteBuilder(int number,String builder,String ctype) {
		//Get the first mmserver that's attached to the service node number
		Enumeration e=mmb.getInsRel().getRelated(number,"mmservers");
		if  (e.hasMoreElements()) {
			MMObjectNode mmservernode=(MMObjectNode)e.nextElement();	
			String mmservername=mmservernode.getStringValue("name");
			log.debug("("+number+","+builder+","+ctype+"): Found attached mmserver:"+mmservername);

			//Get the protocol driver using the mmserver that's attached to current service. 
			MMServers bul = (MMServers)mmb.getMMObject("mmservers");
			ProtocolDriver pd=bul.getDriverByName(mmservername);
			log.debug("("+number+","+builder+","+ctype+"): Retrieved its protocoldriver: "+pd);
			MMObjectNode node=getHardNode(number);
			if (node!=null) {
				String servicename = node.getStringValue("name");
				log.debug("("+number+","+builder+","+ctype+"): Sending signal to remote "
				        +"side using remote service reference:"+servicename);
				pd.signalRemoteNode(servicename,builder,ctype);
				log.debug("("+number+","+builder+","+ctype+"): Signal has been send with "
				        +"remote service reference used:"+servicename);
			}else
				log.error("("+number+","+builder+","+ctype+"): Can't get node (null) for "+number);
		} else
			log.error("("+number+","+builder+","+ctype+"): No related mmserver found attached "
			        +"to this service with number:"+number);
	}
}
