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
