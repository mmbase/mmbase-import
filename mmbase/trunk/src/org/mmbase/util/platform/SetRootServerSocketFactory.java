/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.platform;

import java.io.*;
import java.net.*;
import javax.net.*;

public class SetRootServerSocketFactory extends ServerSocketFactory {

	public ServerSocket createServerSocket(int port) throws IOException
	{
		ServerSocket serv=new ServerSocket(port);
		checkUserLevel();
		return(serv);
	}

	public ServerSocket createServerSocket(int port, int backlog) throws IOException
	{
		ServerSocket serv=new ServerSocket(port, backlog);
		checkUserLevel();
		return(serv);
	}

	public ServerSocket createServerSocket(int port, int backlog, InetAddress address) throws IOException
	{
		ServerSocket serv=new ServerSocket(port, backlog, address);
		checkUserLevel();
		return(serv);
	}


	public void checkUserLevel() {
		System.out.println("CheckUserLevel ->  mmmbase.userlevel="+System.getProperty("mmbase.userlevel"));
		String level=System.getProperty("mmbase.userlevel");
		if (level!=null) {
			int pos=level.indexOf(':');
			if (pos!=-1) {
				String user=level.substring(0,pos);
				String group=level.substring(pos+1);
	 			setUser setuser=new setUser();
				setuser.setUserGroup(user,group);
			} else {
				System.out.println("CheckUserLevel ->  mmmbase.userlevel= not defined as user:group");
			}
		}
	}

}
