package org.mmbase.module;

import org.mmbase.module.irc.communication.*;
import org.mmbase.module.irc.communication.irc.*;
import java.io.*;
import java.util.*;
import org.mmbase.util.*;
import org.mmbase.module.corebuilders.*;

public class IrcModule extends ProcessorModule implements CommunicationUserInterface, Runnable
{
	private String classname = getClass().getName();

	private	CommunicationInterface	com;
    private Thread kicker = null;
	private MMObjectBuiler answers = null;
	private MMObjectBuiler questions = null;

	public IrcModule() {}

	public void receive( String msg ) {
		debug( classname +":"+ msg );
	}

	private void debug( String msg )
	{
		System.out.println( classname +":"+ msg );
	}

	public void init() {
		super.init();
		answers=(MMObjectBuilder)mmbase.getMMObject("answers");
		questions=(MMObjectBuilder)mmbase.getMMObject("questions");
		start();
	}

	public void onload() {
	}

	public void unload() {
	}

	public void shutdown() {
	}

	public Vector getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
		return(null);
	}

	public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
		System.out.println("CMDS="+cmds);
		System.out.println("VARS="+vars);
		System.out.println("vraag="+vars.get("vraag"));
		com.sendPublic((String)vars.get("vraag"));
		return(false);
	}

	public String replace(scanpage sp, String cmds) {
		return "";
	}

    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"IrcModule");
            kicker.start();
        }
    }

    public void stop() {
        /* Stop thread */
        kicker.setPriority(Thread.MIN_PRIORITY);
        kicker.suspend();
        kicker.stop();
        kicker = null;
    }

    public void run () {
		com = (CommunicationInterface) new IrcUser( this );

		if( com.connect( "irc.xs4all.nl", "mmbase", "#mmbase2", "#mmbase2") )
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
}
