package org.mmbase.module.builders.protocoldrivers;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 *
 * @version 27 Mar 1997
 * @author Daniel Ockeloen
 */
public class multicast implements ProtocolDriver {


	String remoteHost;
	int remotePort;

	public multicast() {
	}

	public void init(String remoteHost,int remotePort) {
		this.remoteHost=remoteHost;
		this.remotePort=remotePort;
	}


	public boolean commitNode(String nodename,String tableName,String xml) {
		return(true);
	}


	public String getProtocol() {
		return("multicast");
	}


	public boolean signalRemoteNode(String number, String builder, String ctype) {
		return(true);
	}
}
