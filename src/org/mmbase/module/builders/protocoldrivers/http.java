/* -*- tab-width: 4 -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.protocoldrivers;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This is the http implementation of the ProtocolDriver interface. 
 * It can signal a specific remote builder node using HTTP GET.
 * 
 * @version $Revision: 1.8 $ $Date: 2001-05-07 13:41:18 $ 
 * @author Daniel Ockeloen
 */
public class http implements ProtocolDriver {

    private static Logger log = Logging.getLoggerInstance(http.class.getName()); 
   
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
		log.debug("Initializing HTTP protocoldriver with  remoteHost="+remoteHost
		        +" remotePort=" + remotePort);
		this.remoteHost=remoteHost;
		this.remotePort=remotePort;
	}

	/**
	 * Commits the node?, well this implementation returns true immediately?!
	 * @return true
	 */
	public boolean commitNode(String nodename,String tableName,String xml) {
		return true;
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
		log.debug("("+number+","+builder+","+ctype+"): Signalling remote machine");
		try {
			Socket connect=new Socket(remoteHost,remotePort);
			PrintStream out=new PrintStream(connect.getOutputStream());
			DataInputStream in=new DataInputStream(connect.getInputStream());
			log.debug("signalRemoteNode("+number+","+builder+","+ctype+"): Requesting " + builder 
			        + " node " +number+" in XML format from " + remoteHost + ":" + remotePort
				    +" using GET /remoteXML.db?"+number+"+"+builder+"+"+ctype+" HTTP/1.1\r\n");
			out.print("GET /remoteXML.db?"+number+"+"+builder+"+"+ctype+" HTTP/1.1\r\n");
			out.print("Pragma: no-cache\r\n");
			out.print("User-Agent: org.mmbase\r\n");
			out.print("\r\n");
			out.flush();
			String line=in.readLine();
			log.debug("signalRemoteNode("+number+","+builder+","+ctype+"): Result of GET request (1stline):"+line);
			out.close();
		} catch(Exception e) {
			log.error("Exception " + Logging.stackTrace(e));
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
