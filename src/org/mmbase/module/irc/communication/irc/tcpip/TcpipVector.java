package org.mmbase.module.irc.communication.irc.tcpip;

import java.util.Vector;

public class TcpipVector extends Vector
{
	

	public synchronized void mywait()
		throws InterruptedException 
	{
		if( size() == 0 )
			wait();
	}

	public synchronized void mywait( long time )
		throws InterruptedException 
	{
		if (size() == 0 )
			wait( time );
	}

	public synchronized void mynotify()
		//throws InterruptedException 
	{
		notify();
	}
}
