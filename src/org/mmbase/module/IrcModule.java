package org.mmbase.module;

import	org.mmbase.module.irc.communication.*;
import	org.mmbase.module.irc.communication.irc.*;
import	java.io.*;

public 	class 		IrcModule
		implements	CommunicationUserInterface
{
	private String classname = getClass().getName();

	private	CommunicationInterface	com;

	public IrcModule()
	{
		com = (CommunicationInterface) new IrcUser( this );

		if( com.connect( "irc.xs4all.nl", "mmbase", "#mmbase", "#mmbase") )
		//if( com.connect( "toronto.on.ca.undernet.org", "[java]ron", "#codehozers", "#codehozers") )
		{
			String l = "test";
			DataInputStream dis = new DataInputStream( System.in );

			try
			{
				while( !l.equals("STOP") )
				{
					if( com.isconnected() )
					{
						if( (l=dis.readLine()) != null && !l.equals("STOP"))
						{
							com.sendPublic( l );
							if( l.equals("STOP") )
								com.stopit();
						}
					}
					else
					{
						debug("calling reconned()!");
						//com.reconnect();
					}
				}
			}
			catch( IOException e )
			{
				debug( e.toString() );
				com.stopit();
			}
		}
		else
		{
			debug("Could not connect!");
			com.stopit();
		}
	}

	public void receive( String msg )
	{
		debug( classname +":"+ msg );
	}

	private void debug( String msg )
	{
		System.out.println( classname +":"+ msg );
	}
}
