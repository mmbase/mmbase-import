/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.irc.communication.irc.tcpip;

public 	abstract class TcpipThread 
		extends Thread
{
	private String 			classname 	= getClass().getName();	
	private boolean			doit		= false;
	private	boolean			started		= false;
	public TcpipConnection	tcpipcon;

	public	abstract void 	performAction();

	public TcpipThread( TcpipConnection com )
	{
		this.tcpipcon	= com;
	}

	public void startThread()
	{
		doit = true;
		this.start();
	}
		
	public void stopThread()
	{
		doit = false;
		this.stop();
	}

	public void started()
	{
		while( !started ) 
		try
		{
			sleep(100);
		}
		catch( InterruptedException e )
		{

		}
	}

	public void run()
	{
		if( !started ) 
			started=true;

		while( doit )
		{
			performAction();
		}
	}

	private void debug( String msg )
	{
		System.out.println( classname +":"+ msg );
	}
}
