package org.mmbase.module.irc.communication.irc.tcpip;

public 	class 	TcpipReceiveThread
		extends	TcpipThread
{
	private String classname = getClass().getName();

	public TcpipReceiveThread( TcpipConnection con )
	{
		super(con);
		setName("TcpipReceiveThread");
	}

	public void performAction()
	{
		tcpipcon.receive();	
	}

	private	void debug( String msg )
	{
		System.out.println( classname +":"+ msg );
	}
}
