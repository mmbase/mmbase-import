/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.sql.*;
import java.util.*;
import java.net.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.protocoldrivers.*;


/**
 */
public class MMServers extends MMObjectBuilder implements MMBaseObserver {

	private String javastr;
	private String osstr;
	private String host;
	private static boolean debug=false;
	private Vector possibleServices=new Vector();

	Hashtable name2driver;
	Hashtable url2driver;

	public MMServers() {
		javastr=System.getProperty("java.version")+"/"+System.getProperty("java.vm.name");
		osstr=System.getProperty("os.name")+"/"+System.getProperty("os.version");
		new MMServersProbe(this);
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("state")) {
			int val=node.getIntValue("state");
			switch(val) {
				case -1: return("Unknown");
				case 1: return("Active");
				case 2: return("Inactive");
				case 3: return("Error");
				default: return("Unknown");
			}
		} else if (field.equals("atime")) {
			int now=(int)(System.currentTimeMillis()/1000);
			int then=node.getIntValue("atime");
			String tmp=""+(now-then)+"sec";
			return(tmp);
		}
		return(null);
	}

	public Object getValue(MMObjectNode node, String field) {
		if (field.equals("showstate")) {
			int val=node.getIntValue("state");
			switch(val) {
				case -1: return("Unknown");
				case 1: return("Active");
				case 2: return("Inactive");
				case 3: return("Error");
				default: return("Unknown");
			}
		} else if (field.equals("showatime")) {
			int now=(int)(System.currentTimeMillis()/1000);
			int then=node.getIntValue("atime");
			String tmp=""+(now-then)+"sec";
			return(tmp);
		}
		return(null);
	}

	public void probeCall() {
		boolean imoke=false;
		String machineName=mmb.getMachineName();
		host=mmb.getHost();
		if (debug) System.out.println("MMServers -> machine="+machineName);
		Enumeration e=search("");
		while (e.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			String tmpname=node.getStringValue("name");
			if (tmpname.equals(machineName)) {
				imoke=checkMySelf(node);
			} else {
				checkOther(node);
			}
		}
		if (imoke==false) {
			createMySelf(machineName);
		}
		if (name2driver==null) startProtocolDrivers();
	}

	private boolean checkMySelf(MMObjectNode node) {
		boolean state=true;
		String tmphost=node.getStringValue("host");
		if (!tmphost.equals(host)) {
			System.out.println("MMServers-> Running on a new HOST possible problem");
		}
		node.setValue("state",1);
		node.setValue("atime",(int)(System.currentTimeMillis()/1000));
		node.commit();	
		return(state);
	}	

	private void checkOther(MMObjectNode node) {
		int now=(int)(System.currentTimeMillis()/1000);
		int then=node.getIntValue("atime");
		if ((now-then)>(60*2)) {
			if (node.getIntValue("state")!=2) {
				node.setValue("state",2);
				node.commit();
				
				// now also signal all its services are down !
				setServicesDown(node);
			}
		}
	}	

	private void createMySelf(String machineName) {

		MMObjectNode node=getNewNode("system");
		node.setValue("name",machineName);
		node.setValue("state",1);
		node.setValue("atime",(int)(System.currentTimeMillis()/1000));
		node.setValue("os",osstr);
		node.setValue("host",host);
		node.setValue("jdk",javastr);
		insert("system",node);
	}

	private void setServicesDown(MMObjectNode node) {
		Enumeration f=possibleServices.elements();
		while (f.hasMoreElements()) {
			String type=(String)f.nextElement();
			Enumeration e=mmb.getInsRel().getRelated(node.getIntValue("number"),type);
			while (e.hasMoreElements()) {
				MMObjectNode node2=(MMObjectNode)e.nextElement();
				System.out.println("DOWNNODE="+node2);
				node2.parent.removeRelations(node2);
				node2.parent.removeNode(node2);
			
				//node2.setValue("state","down");
				//node2.commit();
				
			}		
		}
	}


	public void setCheckService(String name) {
		if (!possibleServices.contains(name)) {
			possibleServices.addElement(name);
		}
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


	public void startProtocolDrivers() {
		name2driver=new Hashtable();
		url2driver=new Hashtable();
		Enumeration e=search("");
		while (e.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			String name=node.getStringValue("name");
			String url=node.getStringValue("host");
			int pos=url.indexOf("://");
			if (pos!=-1) {
				// do i allready have this url driver running ?
				ProtocolDriver pd=(ProtocolDriver)url2driver.get(url);
				if (pd!=null) {
					name2driver.put(name,pd);
				} else {
					String tmp=url;
					String protocol=tmp.substring(0,pos);
					tmp=tmp.substring(pos+3);
					pos=tmp.indexOf(':');
					String host;
					int port=80;
					if (pos==-1) {
						host=tmp;
					} else {
						host=tmp.substring(0,pos);	
						try {
							port=Integer.parseInt(tmp.substring(pos+1));
						} catch(Exception f) {}
					}

					try {
						Class newclass=Class.forName("org.mmbase.module.builders.protocoldrivers."+protocol);
						pd = (ProtocolDriver)newclass.newInstance();
						pd.init(host,port);
						url2driver.put(url,pd);
						name2driver.put(name,pd);
						System.out.println("Started Protocol Driver ="+pd);
					} catch (Exception f) {
						System.out.println("MMServers -> Can't load class : "+protocol);
						//f.printStackTrace();
					}
				}
			}
		}
	}	

	public ProtocolDriver getDriverByName(String name) {
		return((ProtocolDriver)name2driver.get(name));
	}
}
