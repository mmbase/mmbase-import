/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;

/**
 * Builds a MultiCast Thread to receive  and send 
 * changes from other MMBase Servers.
 *
 * @version 12 May 1999
 * @author Daniel Ockeloen
 * @author Rico Jansen
 */
public class MMBaseMultiCast implements Runnable {

	Thread kicker = null;
	MMBase parent=null;
	int follownr=1;
	private Vector waitingNodes = new Vector();
	private Queue nodesTosend=new Queue(64);
	private Queue nodesTospawn=new Queue(64);
	public int incount=0;
	public int outcount=0;
	public int spawncount=0;
	private MultiCastChangesSender mcs;
	private MultiCastChangesReceiver mcr;

	public static String multicastaddress="ALL-SYSTEMS.MCAST.NET";
	public static int dpsize=64*1024;
	public static int mport=4243;

	public MMBaseMultiCast(MMBase parent) {
		this.parent=parent;
		if (parent.multicasthost!=null) multicastaddress=parent.multicasthost;
		if (parent.multicastport!=-1) mport=parent.multicastport;

		init();
	}

	public void init() {
		this.start();	
	}


	/**
	 * Starts the admin Thread.
	 */
	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			kicker = new Thread(this,"MMBaseMultiCast");
			kicker.start();
			mcs=new MultiCastChangesSender(this,nodesTosend);
			mcr=new MultiCastChangesReceiver(this,nodesTospawn);
		}
	}
	
	/**
	 * Stops the admin Thread.
	 */
	public void stop() {
		/* Stop thread */
		kicker.setPriority(Thread.MIN_PRIORITY);  
		kicker.suspend();
		kicker.stop();
		kicker = null;
	}

	/**
	 * admin probe, try's to make a call to all the maintainance calls.
	 */
	public void run() {
		try {
			kicker.setPriority(Thread.NORM_PRIORITY+1);  
			doWork();
		} catch(Exception e) {
			System.out.println("MMBaseMultiCast -> ");
			e.printStackTrace();
		}
	}

	/**
	 * admin probe, try's to make a call to all the maintainance calls.
	 */
	public void doWork() {
		InetAddress ia=null;
		String s;
		StringTokenizer tok;

		//System.out.println("MMBaseMultiCast started");
		try {
			ia = InetAddress.getByName(multicastaddress);
		} catch(Exception e) {
			System.out.println("MMBaseMultiCast -> ");
			e.printStackTrace();
		}
		try {
			MulticastSocket ms = new MulticastSocket(mport);
			ms.joinGroup(ia);
			while (true) {
				DatagramPacket dp = new DatagramPacket(new byte[dpsize], dpsize);
				try {
					ms.receive(dp);
					s=new String(dp.getData(),0,0,dp.getLength());
					nodesTospawn.append(s);
				} catch (Exception f) {
					System.out.println("MMBaseMultiCast -> ");
					f.printStackTrace();
				}
			}
		} catch(Exception e) {
			System.out.println("MMBaseMultiCast -> ");
			e.printStackTrace();
		}
	}

	public boolean handleMsg(String machine,String vnr,String id,String tb,String ctype) {
		//System.out.println("M='"+machine+"' vnr='"+vnr+"' id='"+id+"' tb='"+tb+"' ctype='"+ctype+"'");
		MMObjectBuilder bul=parent.getMMObject(tb);
		if (bul==null) {
			System.out.println("MMBaseMultiCast -> Unknown builder="+tb);
			return(false);
		}
		if (machine.equals(parent.machineName)) {
			// changed if for jikes
			if (bul!=null) {
				new MMBaseMultiCastProbe(this,bul,id,tb,ctype,false);
			}
			/*
			try { 
				new MMBaseMultiCastProbe(this,bul,id,tb,ctype,false);
			} catch(Exception e) {
				e.printStackTrace();
			}
			*/
		
		} else {
			try { 
				if (!ctype.equals("g")) {
					new MMBaseMultiCastProbe(this,bul,id,tb,ctype,true);
				} else {
					if (bul!=null) {
						MMObjectNode node=bul.getNode(id);
						if (node!=null) {
							// well send it back !
							String chars=parent.machineName+","+(follownr++)+","+id+","+tb+",x,"+node.toXML()+"\n";
							nodesTosend.append(chars);

						} else {
							System.out.println("MMBaseMultiCast-> can't get node "+id);
						}
					} else {
						System.out.println("MMBaseMultiCast-> can't find builder "+bul);
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return(true);
	}

	public boolean changedNode(int nodenr,String tableName,String type) {
		String chars=parent.machineName+","+(follownr++)+","+nodenr+","+tableName+","+type;
		nodesTosend.append(chars);
		return(true);
	}

	public boolean waitUntilNodeChanged(MMObjectNode node) {
		try {
			MMBaseMultiCastWaitNode wnode = new MMBaseMultiCastWaitNode(node);
			waitingNodes.addElement(wnode);
			wnode.doWait(60*1000);
			waitingNodes.removeElement(wnode);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return(true);
	}


	public void checkWaitingNodes(String snumber) {
		try {
			int number=Integer.parseInt(snumber);
			for (Enumeration e=waitingNodes.elements();e.hasMoreElements();) {
				MMBaseMultiCastWaitNode n=(MMBaseMultiCastWaitNode)e.nextElement();
				if (n.doNotifyCheck(number)) {
					waitingNodes.removeElement(n);
					System.out.println("MMBaseMultiCast-> waitingNodes size="+waitingNodes.size());
				}
			}
		} catch(Exception e) {
			System.out.println("MMBaseMultiCast-> not a valid number "+snumber);
		}
	}



	public boolean commitXML(String machine,String vnr,String id,String tb,String ctype,String xml) {
		try {
		//System.out.println("M='"+machine+"' vnr='"+vnr+"' id='"+id+"' tb='"+tb+"' ctype='"+ctype+"'"+xml);
		MMObjectBuilder bul=parent.getMMObject(tb);
		if (bul==null) {
			System.out.println("MMBaseMultiCast -> Unknown builder="+tb);
			return(false);
		}
		if (machine.equals(parent.machineName)) {
			// do nothing	
		} else {
			MMObjectNode node=bul.getNode(id);
			if (node!=null) {
				// well send it back !
				mergeXMLNode(node,xml);
				node.commit();
				
			} else {
				System.out.println("MMBaseMultiCast-> can't get node "+id);
			}
		}
		} catch(Exception e) {
			System.out.println("MMBaseMultiCast-> commitXML error");
			e.printStackTrace();	
		}
		return(true);
	}

	private void mergeXMLNode(MMObjectNode node,String body) {
		StringTokenizer tok = new StringTokenizer(body,"\n\r");
		String xmlline=tok.nextToken();
		String docline=tok.nextToken();
		
	
		String builderline=tok.nextToken();
		String endtoken="</"+builderline.substring(1);
		

		// weird way
		String nodedata=body.substring(body.indexOf(builderline)+builderline.length());
		nodedata=nodedata.substring(0,nodedata.indexOf(endtoken));

		int bpos=nodedata.indexOf("<");
		while (bpos!=-1) {
			String key=nodedata.substring(bpos+1);
			key=key.substring(0,key.indexOf(">"));
			String begintoken="<"+key+">";
			endtoken="</"+key+">";
			
			String value=nodedata.substring(nodedata.indexOf(begintoken)+begintoken.length());
			value=value.substring(0,value.indexOf(endtoken));

			// set the node
			int dbtype=node.getDBType(key);
			if (!key.equals("number") && !key.equals("otype") && !key.equals("owner"))
				node.setValue( key, dbtype, value );

			nodedata=nodedata.substring(nodedata.indexOf(endtoken)+endtoken.length());
			bpos=nodedata.indexOf("<");
		}
	}
}
