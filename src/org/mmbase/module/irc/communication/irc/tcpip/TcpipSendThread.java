/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.irc.communication.irc.tcpip;

public 	class 	TcpipSendThread
		extends	TcpipThread
{
	private String classname = getClass().getName();

	public TcpipSendThread( TcpipConnection con )
	{
		super( con );
		setName("TcpipSendThread");
	}

	public void performAction()
	{
		tcpipcon.send();	
	}

	private	void debug( String msg )
	{
		System.out.println( classname +":"+ msg );
	}
}
