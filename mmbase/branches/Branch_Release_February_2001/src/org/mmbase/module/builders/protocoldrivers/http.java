/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*$Id: http.java,v 1.6 2000-12-20 16:35:34 vpro Exp $
$Log: not supported by cvs2svn $
Revision 1.5  2000/11/22 17:33:19  vpro
davzev: Added getRemoteHost,getRemotePort and implemented toString() methods

Revision 1.4  2000/11/22 14:14:36  vpro
davzev: Added debug method and comments to the methods.

*/
package org.mmbase.module.builders.protocoldrivers;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * This is the http implementation of the ProtocolDriver interface. It can signal a 
 * specific remote builder node using HTTP GET.
 * 
 * @version $Revision: 1.6 $ $Date: 2000-12-20 16:35:34 $ 
 * @author Daniel Ockeloen
 */
public class http implements ProtocolDriver {
	private	String classname = getClass().getName();
	private boolean	debug = true;
	private void debug(String msg) {System.out.println( classname +":"+ msg );}

	private String remoteHost;
	private int remotePort;

	public http() {
	}

	/**
	 * Initializes remotehost and remoteport.
	 * @param remotHost the remote host.
	 * @param remotPortt the remote port.
	 */
	public void init(String remoteHost,int remotePort) {
		debug("init: Initializing HTTP protocoldriver with remoteHost="+remoteHost+" remotePort="+remotePort);
		this.remoteHost=remoteHost;
		this.remotePort=remotePort;
	}

	/**
	 * Commits the node?, well this implementation returns true immediately?!
	 * @return true
	 */
	public boolean commitNode(String nodename,String tableName,String xml) {
		return(true);
	}

	/**
	 * Gets the name of this protocoldriver.
	 * @return a String containing the word "http".
	 */
	public String getProtocol() {
		return("http");
	}
	/**
	 * Gets the remote hostname.
	 * @return the remote hostname.
	 */
	public String getRemoteHost() {
		return this.remoteHost;
	}
	/**
	 * Gets the remote portnumber.
	 * @return the remote portnumber.
	 */
	public int getRemotePort() {
		return this.remotePort;
	}
	
	/**
	 * Sends a signal from mmbase to the remote side to tell that a remote node has a 
	 * status has been changed.
	 * @param number a String with the object number of the remote builder node
	 * @param builder the typename of builder that's been signalled to.
	 * @param ctype the mmbase node changed symbol.
	 * @return true, always...?
	 */
	public boolean signalRemoteNode(String number, String builder, String ctype) {
		debug("signalRemoteNode("+number+","+builder+","+ctype+"): Signalling remote machine");
		try {
			Socket connect=new Socket(remoteHost,remotePort);
			PrintStream out=new PrintStream(connect.getOutputStream());
			DataInputStream in=new DataInputStream(connect.getInputStream());
			if (debug) debug("signalRemoteNode("+number+","+builder+","+ctype+"): Requesting "+builder+" node "+number+" in XML format from "+remoteHost+":"+remotePort+" using GET /remoteXML.db?"+number+"+"+builder+"+"+ctype+" HTTP/1.1\r\n");
			out.print("GET /remoteXML.db?"+number+"+"+builder+"+"+ctype+" HTTP/1.1\r\n");
			out.print("Pragma: no-cache\r\n");
			out.print("User-Agent: org.mmbase\r\n");
			out.print("\r\n");
			out.flush();
			String line=in.readLine();
			debug("signalRemoteNode("+number+","+builder+","+ctype+"): GET result, in.readLine: "+line);
			out.close();
		} catch(Exception e) {
			debug("Exception "+e);
		}
		return(true);
	}
	
	/**
	 * Gets the protocolname, remotehost and remote port.
	 * @return a String with info about this protocoldriver.
	 */
	public String toString() {
		return "protocol:"+getProtocol()+", connected at "+getRemoteHost()+" port:"+getRemotePort();
	}
}
