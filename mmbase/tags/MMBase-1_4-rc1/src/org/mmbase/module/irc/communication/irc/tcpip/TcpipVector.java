/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

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
