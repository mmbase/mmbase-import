package org.mmbase.module.irc.communication.irc;

public class IrcChannel
{
	private String 	classname 	= getClass().getName();
	private	boolean	debug		= false;
	private String	server		= null;
	private String 	channelname = null;
	private String 	channelkey  = null;

	private	boolean	isJoined	= false;

	public IrcChannel( String server, String channelname )
	{
		setServer( server );
		setChannelName( channelname );
	}

	public IrcChannel( String server, String channel, String key )
	{
		setServer( server );
		setChannelName( channel );
		setChannelKey ( key     );
	}

	/**
	 *
	 */
	public void setServer( String name )
	{
		this.server = name;
	}

	public String getServer()
	{
		return server;
	}


	public void setChannelName( String name )
	{
		if (name == null)
			debug("setChannelName("+name+"): ERROR: channelname is null!");
		else
			if (name.equals(""))
				debug("setChannelName("+name+"): ERROR: channelname is empty!");
			else
				this.channelname = name;
	}

	public String getChannelName()
	{
		return channelname;
	}

	/**
	 */
	public void setChannelKey( String key )
	{
		this.channelkey = key;
	}

	public String getChannelKey()
	{
		return channelkey;
	}


	public synchronized void joined()
	{
		//debug("joined("+getChannelName()+")");

		// signal channel joined
		// ---------------------
		isJoined = true;

		// notify communication.connect() that channel is joined
		// -----------------------------------------------------

		if (debug) debug("joined().notify()");
		notify();
	}

	public void parted()
	{
		isJoined = false;
	}

	public boolean isJoined()
	{
		return isJoined;
	}

	// ------------------------------------------------
	// communication.connect calls this method to
	// see if channel is joined. Waits for notification
	// from server that channel is valid/joined.
	// ------------------------------------------------

	public synchronized boolean waitJoin( long time )
	{
		boolean result = false;
		try
		{
			wait( time );
			result = isJoined();
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		return result;
	}	

	/**
	 */
	private void debug( String msg )
	{
		System.out.println( classname + ":" + msg );
	}

	public String toString()
	{
		String result = "";
		
		result += "["+server+"] " + getChannelName();
		if (getChannelKey() != null)
			result +=", channelkey["+getChannelKey()+"]";

		if(isJoined())
			result += " isJoined()";

		result += "\n";

		return result;
	}
}
