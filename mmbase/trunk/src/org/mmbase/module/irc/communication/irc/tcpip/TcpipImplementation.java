package org.mmbase.module.irc.communication.irc.tcpip;

import org.mmbase.module.irc.communication.*;
import org.mmbase.module.irc.communication.irc.tcpip.*;

public 	class 		TcpipImplementation
		implements	CommunicationUserInterface
{
	private	String classname = getClass().getName();

	public void receive( String msg )
	{
		debug("receive("+msg+")");
	}

	private void debug( String msg )
	{
		System.out.println( classname +":"+ msg );
	}
}
