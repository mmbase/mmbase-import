package org.mmbase.module.irc.communication.irc;

import java.util.Hashtable;
import java.util.Enumeration;

public class IrcChannels
{
	private String 		classname = getClass().getName();
	private Hashtable	channels = null;

	private String 		currentChannelName = null;

	public IrcChannels()
	{
		channels = new Hashtable();
	}

	public void addChannel( String server, String channelname )
	{
		if (channelname == null)
			debug("addChannel("+channelname+"): ERROR: channelname is null!");
		else
			if( channelname.equals(""))
				debug("addChannel("+channelname+"): ERROR: channelname is empty!");
			else
				addChannel( new IrcChannel(server, channelname) );
	}


	public void addChannel( IrcChannel channel )
	{
		if( channel==null )
			debug("addChannel("+channel+"): ERROR: channel is null!");
		else
			if (channel.getChannelName() == null)
				debug("addChannel("+channel+"): ERROR: channel has invalid(null) name!");
			else
				if( channel.getChannelName().equals(""))
					debug("addChannel("+channel+"): ERROR: channel has invalid(empty) name!");
				else
					if (!containsChannel( channel ))
						channels.put( channel.getChannelName(), channel);
					else
						debug("addChannel("+channel+"): WARNING: channel already in list!");
	}

	public IrcChannel getChannel( String channelname )
	{
		IrcChannel result = null;
	
		if( channelname == null )
			debug("getChannel("+channelname+"): ERROR: channelname is null!");
		else
			if( channelname.equals(""))
				debug("getChannel("+channelname+"): ERROR: channelname is empty!");
			else
				if (channels.containsKey(channelname))
					result = (IrcChannel) channels.get( channelname );
				else
					debug("getChannel("+channelname+"): ERROR: unknown channelname!");

		return result;
	}

	public boolean containsChannel( IrcChannel channel )
	{
		return containsChannel( channel.getChannelName() );
	}

	public boolean containsChannel( String channelname )
	{
		boolean result = false;

			if (channelname != null && !channelname.equals(""))
				result = channels.containsKey( channelname );
			else
				debug("containsChannel("+channelname+"): ERROR: Invalid channelname!");

		return result;
	}

	public void removeChannel( IrcChannel channel )
	{
		 removeChannel( channel.getChannelName() );
	} 

	public void removeChannel( String channelname ) 
	{
		if( channelname == null )
			debug("removeChannel("+channelname+"): ERROR: channelname is null!");
		else
			if( channelname.equals(""))
				debug("removeChannel("+channelname+"): ERROR: channelname is empty!");
			else
				if (containsChannel(channelname))
					channels.remove( channelname );
				else
					debug("removeChannel("+channelname+"): ERROR: channel is not in list!");
	}


	public void setCurrentChannel( IrcChannel channel )
	{
		//debug("setCurrentChannel("+channel+")");
		this.currentChannelName = channel.getChannelName();
	}

	public IrcChannel getCurrentChannel()	
	{
		IrcChannel result = null;
		if ( currentChannelName != null && !currentChannelName.equals(""))
		{
			if (containsChannel( currentChannelName ))
			{
				result = getChannel(currentChannelName);
			}
			else
			{
				debug("getCurrentChannel(null): ERROR: Channel is not in list!");
			}
		}
		//debug("getCurrentChannel("+result+")");
		return result;
	}

	/**
 	 * remove all channels (at exit)
	 */
	public void removeAll()
	{
		for(Enumeration e=elements(); e.hasMoreElements(); )
			removeChannel( (IrcChannel) e.nextElement() );
	}

	/**
	 * Call when disconnected, so we can see that these channels have to be rejoined
	 */
	public void partAll()
	{
		for( Enumeration e=elements(); e.hasMoreElements(); )
			((IrcChannel)e.nextElement()).parted();
	}	

	/**
	 * Gimme all channels 
	 */
	public Enumeration elements()
	{
		return channels.elements();
	}

	private void debug( String msg )
	{
		System.out.println( classname + ":" + msg );
	}

	public String toString()
	{
		String result = "";

		if (channels.size() > 0)
		{
			result += "You are on " + channels.size() + " channels:\n" ;
			for(Enumeration e=channels.elements(); e.hasMoreElements(); )
				result += ((IrcChannel)e.nextElement()).toString();
			result += "-------------------------------------------\n";
			result += "Current channel is : " + getChannel(currentChannelName)+"\n";
		}
		else
			result +="You are not on any channel on this server\n";

		return result;
	}
}
