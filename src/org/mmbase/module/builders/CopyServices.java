/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Rico Jansen
 * @version $Id: CopyServices.java,v 1.7 2003-03-10 11:50:18 pierre Exp $
 */
public class CopyServices extends MMObjectBuilder implements MMBaseObserver {

    private static Logger log = Logging.getLoggerInstance(CopyServices.class.getName()); 

	public final static String buildername = "CopyServices";
	private final Hashtable busyServices=new Hashtable();


	/**
	* create a new object, normally not used (only subtables are used)
	*/
	/*
	public boolean create() {
		// create the main object table
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t (from varchar(32)"
				+", to varchar(32)"
				+", fromroot varchar(255)"
				+", toroot varchar(255)"
				+", state char(32)"
				+", info varchar(255) not null) under "+mmb.baseName+"_object_t");
			log.debug("Created "+tableName);
			stmt.close();
			con.close();
		} catch (SQLException e) {
			log.debug("can't create type "+tableName);
			e.printStackTrace();
		}
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t ( primary key(number)) under "+mmb.baseName+"_object");
			stmt.close();
			con.close();
		} catch (SQLException e) {
			log.debug("can't create table "+tableName);
			e.printStackTrace();
		}
		return(false);
	}
	*/

	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	/*
	public int insert(String owner,MMObjectNode node) {
		String from=node.getStringValue("from");
		String fromroot=node.getStringValue("fromroot");
		String to=node.getStringValue("to");
		String toroot=node.getStringValue("toroot");
		String state=node.getStringValue("state");
		String info=node.getStringValue("info");
		
		if (state==null) state="";
		if (info==null) info="";

		int number=getDBKey();
		if (number==-1) return(-1);
		try {
			MultiConnection con=mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+tableName+" values(?,?,?,?,?,?,?,?,?)");
				stmt.setInt(1,number);
				stmt.setInt(2,oType);
				stmt.setString(3,owner);
				stmt.setString(4,from);
				stmt.setString(5,to);
				stmt.setString(6,fromroot);
				stmt.setString(7,toroot);
				stmt.setString(8,state);
				stmt.setString(9,info);
				stmt.executeUpdate();
				stmt.close();
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug("Error on : "+number+" "+owner+" fake");
			return(-1);
		}
		signalNewObject(tableName,number);
		return(number);	
	}
	*/



	public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
		super.nodeRemoteChanged(machine,number,builder,ctype);
		return(nodeChanged(machine,number,builder,ctype));
	}

	public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
		super.nodeLocalChanged(machine,number,builder,ctype);
		return(nodeChanged(machine,number,builder,ctype));
	}

	public boolean nodeChanged(String machine,String number,String builder,String ctype) {
		MMObjectNode node=getNode(number);
		if (node!=null) {
            log.debug("CopyServices node="+node.toString());
			// is if for me ?, enum related mmservers to check
			Enumeration e=mmb.getInsRel().getRelated(number,"mmservers");
			while  (e.hasMoreElements()) {
				MMObjectNode node2=(MMObjectNode)e.nextElement();
				String wantedServer=node2.getStringValue("name");
				if (wantedServer.equals(mmb.getMachineName())) {
					doAction(node);
				}
			}
		}
		return(true);
	}

	/**
	* handles commands for CopyServices found in this machine
	*/
	private void doAction(MMObjectNode node) {
		String state=node.getStringValue("state");

		if (!state.equals("busy")) {
			log.debug("Action called on CopyServices : "+node);
			// start a thread to handle command
			new CopyServicesProbe(this,node);
		} else {
			log.debug("Problem action called on copyservice : "+node+" while it was busy");
		}
	}

	public String copy(MMObjectNode src,MMObjectNode dst) {
		MMObjectNode node;
		String info,val,state;
		String srcserver,dstserver;
		srcserver=src.getStringValue("mmserver");
		dstserver=dst.getStringValue("mmserver");

		Enumeration e=search("WHERE from='"+srcserver+"' AND to='"+dstserver+"'");
		if (e.hasMoreElements()) {
			boolean changed=false;
			MMObjectNode newnode=null;
			Integer busyInt;

			node=(MMObjectNode)e.nextElement();

			busyInt=node.getIntegerValue("number");
			obtainLock(busyInt);

			log.debug(buildername+" -> send to "+node);

			info="FROM="+src.getIntValue("number")+" TO="+dst.getIntValue("number");
			changed=false;
			newnode=null;
			state=node.getStringValue("state");
			if (!state.equals("waiting")) {
				while (!changed) {	
					waitUntilNodeChanged(node);
					newnode=getNode(node.getIntValue("number"));
					state=newnode.getStringValue("state");
					log.debug(buildername+" Claim wait="+state);
					if (state.equals("waiting")||state.equals("error")) changed=true;
				}
			}

			node.setValue("info",info);
			node.setValue("state","copy");
			node.commit();
			changed=false;
			newnode=null;
			while (!changed) {	
				waitUntilNodeChanged(node);
				newnode=getNode(node.getIntValue("number"));
				state=newnode.getStringValue("state");
				log.debug(buildername+" Operation wait="+state);
				if (state.equals("waiting")||state.equals("error")) changed=true;
			}
			val=newnode.getStringValue("info");
			log.debug(buildername+": copy : "+val);

			releaseLock(busyInt);
		} else {
			val="error";
		}
		return(val);
	}

	public String remove(MMObjectNode src) {
		MMObjectNode node;
		String info,val,state;
		String srcserver;
		srcserver=src.getStringValue("mmserver");

		Enumeration e=search("WHERE from='"+srcserver+"'");
		if (e.hasMoreElements()) {
			boolean changed=false;
			MMObjectNode newnode=null;
			Integer busyInt;

			node=(MMObjectNode)e.nextElement();

			busyInt=node.getIntegerValue("number");
			obtainLock(busyInt);

			log.debug(buildername+" -> send to "+node);
			info="FROM="+src.getIntValue("number");

			changed=false;
			newnode=null;
			state=node.getStringValue("state");
			if (!state.equals("waiting")) {
				while (!changed) {	
					waitUntilNodeChanged(node);
					newnode=getNode(node.getIntValue("number"));
					state=newnode.getStringValue("state");
					log.debug(buildername+" Claim wait="+state);
					if (state.equals("waiting")||state.equals("error")) changed=true;
				}
			}

			node.setValue("info",info);
			node.setValue("state","remove");
			node.commit();
			while (!changed) {	
				waitUntilNodeChanged(node);
				newnode=getNode(node.getIntValue("number"));
				state=newnode.getStringValue("state");
				log.debug(buildername+" Operation wait="+state);
				if (state.equals("waiting")||state.equals("error")) changed=true;
			}
			val=newnode.getStringValue("info");
			log.debug(buildername+": remove : "+val);

			releaseLock(busyInt);
		} else {
			val="error";
		}
		return(val);
	}

	private void obtainLock(Integer number) {
		synchronized(busyServices) {
			while (busyServices.containsKey(number)) {
				try {
					busyServices.wait(60000);	
				} catch (InterruptedException ie) {}
				log.debug("CopyServices -> Waiting for "+number);
			}
			busyServices.put(number,number);
		}
	}

	private void releaseLock(Integer number) {
		synchronized(busyServices) {
			if (busyServices.containsKey(number)) {
				busyServices.remove(number);
			}
			try {
				busyServices.notify();
			} catch (Exception xx) {
				log.debug("CopyServices -> Notify -> "+xx);
				xx.printStackTrace();
			}
		}
	}
}
