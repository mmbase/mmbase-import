/*
*/

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
public class http implements ProtocolDriver {


	String remoteHost;
	int remotePort;

	public http() {
	}

	public void init(String remoteHost,int remotePort) {
		this.remoteHost=remoteHost;
		this.remotePort=remotePort;
	}


	public boolean commitNode(String nodename,String tableName,String xml) {
		return(true);
	}


	public String getProtocol() {
		return("http");
	}


	public boolean signalRemoteNode(String number, String builder, String ctype) {
		System.out.println("Signal remote machine ! "+number);
		try {
			Socket connect=new Socket(remoteHost,remotePort);
			PrintStream out=new PrintStream(connect.getOutputStream());
			DataInputStream in=new DataInputStream(connect.getInputStream());
			out.print("GET /remoteXML.db?"+number+"+"+builder+"+"+ctype+" HTTP/1.1\r\n");
			System.out.print("GET /remoteXML.db?"+number+"+"+builder+"+"+ctype+" HTTP/1.1\r\n");
			out.print("Pragma: no-cache\r\n");
			out.print("User-Agent: org.mmbase\r\n");
			out.print("\r\n");
			out.flush();

			String line=in.readLine();
			System.out.println("BACK="+line);
			out.close();
		} catch(Exception e) {
		}
		return(true);
	}

}
