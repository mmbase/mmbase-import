/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.irc.communication.irc;

import org.mmbase.module.irc.communication.*;
import org.mmbase.module.irc.communication.irc.tcpip.*;
import java.util.Enumeration;
import java.io.IOException;
import org.mmbase.util.logging.*;

/**
 * Eerste opzet : 1 class die connect naar server
 */

public 	class 		IrcConnection 
		extends 	TcpipConnection
		implements	CommunicationInterface

{
    private static Logger log = Logging.getLoggerInstance(IrcConnection.class.getName());
	private	boolean		accepted	= false;
	private boolean 	loggedin	= false;

	private	String		nickname	= null;
	private IrcChannels	ircChannels = null;
	private String		password	= null;


// -------------------------------------------------------------------

	public IrcConnection( CommunicationUserInterface com )
	{
		initialize( com );
		ircChannels = new IrcChannels();
	}

	public synchronized boolean connect( String server, String name, String group, String password )
	{
		// if (debug) debug("connect( server("+server+"), name("+name+"), group("+group+"), password("+password+") ");
		if (!connect( server, 6667))
		{
			log.debug("connect(): not accepted!");
			return false;
		}
		// if (debug) debug("connect(): successfull!");

		int tries = 0, maxtries = 10;
		do
		{
				nickname = name;
				//this.startit();
				login();
	
				try
				{
					// if( debug ) debug("connect(): waiting for signal login");
					wait( 40000 ); // was ( 10000 );	
				}
				catch( InterruptedException e ) { 
					log.error( "connect(): Interrupted! " + e.toString() ); 
				}
				// if( debug ) debug("End of wait");
		
				if( !isaccepted() ) {
					log.debug("connect(): Connect not accepted. ["+tries+"/"+maxtries+"], retrying!");
					tries++;
					if (tries>maxtries)
						return false;
				}
		} while (!accepted);

		// if (debug) debug("connect(): accepted, joining channel");

		IrcChannel channel = new IrcChannel( server, group, password );
		join( channel );
		if ( channel.waitJoin( 30000 )) // was 10000
		{
			// if (debug) debug("Join accepted!");
			return true;
		}
		// debug("Join not accepted!");
		stopit(); 
		return false;
	}

	public boolean reconnect()
	{
		boolean result = false;

		// if( debug ) debug("reconnect()");

		if( super.reconnect() )
		{
			if( isaccepted() )
			{
				rejected();		
				// debug("reconnect(): connected, accepted("+isaccepted()+")");
				int tries = 0, maxtries = 10;
			
				while( !isaccepted() && tries < maxtries) 
				{
					// if( debug ) debug("reconnect(): connected!, logging in");

					login();	
					if( isaccepted() )
					{
						// debug("reconnect(): accepted!, rejoining");
						rejoin();
						result = true;
					}
					else
					{
						tries++;
						// debug("reconnect(): After["+tries+"/"+maxtries+"] nick not accepted after "+tries+" time, its prolly in use, sleeping for 10 secs.");
						try
						{
							sleep(10000);
						}
						catch( InterruptedException e )
						{
							log.debug("reconnect(): interrupted: " + e.toString() );
						}
					}
				}
			}
		}
		return result;
	}

    public void rejoin()
    {
		// debug("rejoin(), start()");
        IrcChannel ircChannel = null;
        for( Enumeration e = ircChannels.elements(); e.hasMoreElements();)
        {
            ircChannel = (IrcChannel) e.nextElement();
			// debug("rejoin(): found channel("+ircChannel.toString()+")");
            if( ircChannel.isJoined() )
			{
				ircChannel.parted();
                join( ircChannel.getServer(), ircChannel.getChannelName(), ircChannel.getChannelKey() );
				if( ircChannel.waitJoin( 10000 ) ) 
				{
					log.debug("rejoin("+ircChannel.getChannelName()+"): successfull rejoin");
				}
				else
				{
					log.debug("rejoin("+ircChannel.getChannelName()+"): could not rejoin, banned !?!?!");
				}
			}
        }
    }

// -------------------------------------------------------------------

	public void send( String s ) 
	{
		log.debug("send("+s+")");	
		super.send( s );
	}

	public void sendMessage( IrcMessage msg )
	{
		String line = msg.construct();
		log.debug("sendMessage("+line+")");
		send( line + '\n' );
	}


	public void sendPublic( String line )
    {
        log.debug("send("+line+")");

        if( line!=null && !line.equals(""))
        {
            IrcChannel current = ircChannels.getCurrentChannel();
            if( current != null)
            {
                send( current.getChannelName(), line );
            }
            /*
            else
                log.error("send("+line+"): ERROR: Cannot send(no channel defined yet)");
            */
        }
        /*
        else
            log.error("send("+line+"): ERROR: want to send nothing!");
        */
    }

    public void sendPrivate( String who, String line )
    {
        log.debug("sendPrivate("+who+","+line+")");

        if (line!=null && !line.equals(""))
        {
            if (who != null && !who.equals(""))
            {
                send( who, line );
            }
            else
                log.error("sendPrivate("+who+","+line+"): ERROR: 'who' not specified!");
        }
        else
            log.error("sendPrivate("+who+","+line+"): ERROR: want to send nothing!");
    }

    public void send( String who, String line )
    {
        log.debug("send("+who+","+line+")");

        //debug("send("+who+","+line+")");
        if( who != null )
        {
            if( line!=null && !line.equals(""))
            {
                if (line.indexOf('\n') != -1 )
                {
                    // send all lines
                    while (line.indexOf('\n') != -1  && !line.equals(""))
                    {
                         int index = line.indexOf('\n');
                         String line2 = line.substring( 0, index );
                         line = line.substring( index + 1 );
                         privmsg( who, line2 );
                    }

                    // last line (could be only return!)
                    if (!line.equals(""))
                        privmsg( who, line );
                }
                else
                   privmsg( who, line );
            }
            else
                log.error("send("+who+","+line+"): ERROR: want to send nothing!");
        }
        else
            log.error("send("+who+","+line+"): ERROR: Dont know who to send to!");
    }

	public void receive( String msg ) {
		String line = parseMessage( new IrcMessage( msg ));
		if (line!=null && !line.equals("")) {
			try {
				comuser.receive( line );
			} catch (Exception e) {
				System.out.println("org.mmbase.module.irc.communication.IrcConnection something when wrong in the user code");
			}
		}
	}

// -------------------------------------------------------------------

	public synchronized boolean isaccepted()
	{
		return accepted;
	}

	private synchronized void accepted()
	{
		accepted = true;
		notify();
	}

	private void rejected()
	{
		accepted = false;
	}

// -------------------------------------------------------------------

	public boolean isloggedin()
	{
		return loggedin;	
	}

	private void loggedin()
	{
		loggedin = true;
	}

	private void loggedout()
	{
		loggedin = false;
	}

// -------------------------------------------------------------------

	public void stopit()
	{
		if (isloggedin())
			quit();
		
		super.stopit();
	}

// -------------------------------------------------------------------

    public String parseMessage( IrcMessage message )
    {

        String result=null;

        if (message != null)
        {
			log.debug("parseMessage("+message+")");

            if( message.command() != null && message.command().equals("PING") )
			{
                    pong( message.from(), message.params() );
			}
            else
            {
                // check if this is a number command, if so act accordingly

                if (checkint( "parseMessage", "message.command()", message.command() ))
                {
                    // for internal use (like, isLoggedIn)
                    // -----------------------------------

					if( checkint( "parseMessage", "message.command()", message.command())) 
					{
						int number = getint( "parseMessage", "message.command()", message.command());
						// debug("ParseMsg: "+number);
                        switch( number )
                        {
                            case    433: {  
											log.debug("parseMessage(): Nick already known.. " + message.toString() +": reconnect!"); 
											// stopit(); 
											break; 
										 }

											// accepted
                            case    376: { 	log.debug("parseMessage(): 376 Signal that logged in"); 
											accepted();
											//debug("accepted("+isaccepted()+")") ; 
											break; 
										 }

											// join channel
                            case    366: { 
											log.debug("Got 366: join channel");
											/* 
											if(ircChannels.containsChannel( message.middle() )) 
											{ 
												ircChannels.getChannel( message.middle()).joined(); 
												ircChannels.setCurrentChannel( ircChannels.getChannel(message.middle() )); 
											} 
											break; 
											*/
										 }
                        }
					}
					else
                    {
                        log.error("receiveMessage("+message.toString()+"): ERROR: Cannot convert("+message.command()+") to int!");
                    }

                    // return sended line from user/server/channel etc
                    // -----------------------------------------------

                    if(message.params() != null)
                    {
                        result="[server='"+message.server()+"', from='"+message.from()+"', to='"+message.to()+"'] "+message.params();

                        //result = message.params();
                    }
                }
                else
				{
					if( checkstring( "parsemessage("+message.toString()+")", "message.command()", message.command() ))
					{
						if( message.command().equals("JOIN") )
						{
							if( checkstring("parseMessage("+message.toString()+")", "message.from()", message.from() ))
							{
								// did i join or somebody else ?
								// -----------------------------

								String nicky = message.from().substring(0, message.from().indexOf("!"));
								if( nickname.trim().toLowerCase().equals( nicky.trim().toLowerCase() ))
								{
									if( checkstring("parseMessage("+message.toString()+")", "messsage.params()", message.params() ))
									{
										log.debug("parseMessage(): "+message.command()+" accepted for channel("+message.params()+")");
										if(ircChannels.containsChannel( message.params() )) 
										{ 
											ircChannels.getChannel( message.params() ).joined(); 
											ircChannels.setCurrentChannel( ircChannels.getChannel(message.params() )); 
										}
									}
									else
										log.debug("parseMessage("+message.toString()+"): "+message.command()+" accepted, but dunno what channel("+message.params()+")");
								} 
							}
							else
								log.debug("parseMessage("+message.toString()+"): "+message.command()+" accepted, but dunno what("+message.middle()+")");
						}
						else
						if( message.command().equals("PRIVMSG") )
						{
                        	result="[server='"+message.server()+"', from='"+message.from()+"', to='"+message.to()+"'] "+message.params();
							//result = message.toString();
						}	
					}
					else
                    	log.error("parseMessage("+message+"): ERROR: Unknown command!");
				}
            }
        }
        /*
        else
            log.error("parseMessage(): ERROR: Empty message!");
        */

        return(result);
    }

// -------------------------------------------------------------------

    public void pass()
    {
        if( password != null && !password.equals(""))
            sendMessage( IrcCommands.pass( servername, nickname, password ));
    }

    public void nick()
    {
        sendMessage( IrcCommands.nick( servername, nickname, nickname ));
    }

    public void user()
    {
        // these can be implemented here, like ic.getHostname(), ic.getServername()
        //String  hostname    = "hostname";
        //String  servername  = "servername";
        String  realname    = "realname";

        sendMessage( IrcCommands.user( servername, nickname, nickname, hostname, servername, realname ));
    }


    public void login()
    {
        pass();
        nick();
        user();
    }

    public void join( IrcChannel channel )
    {
        if(!ircChannels.containsChannel( channel))
            ircChannels.addChannel( channel );

        // signal that its being joined, but not accepted yet
        channel.parted();

        ircChannels.setCurrentChannel( channel );

        join( channel.getServer(), channel.getChannelName(), channel.getChannelKey() );
    }

    public void join( String server, String channelname, String channelkey )
    {
        sendMessage( IrcCommands.join( server, nickname, channelname, channelkey ));
    }

    public void ping( String who )
    {
        String time = "" + System.currentTimeMillis();
        sendMessage( IrcCommands.ping( servername, nickname, who, time ));
    }

    public void pong( String who, String time )
    {
        sendMessage( IrcCommands.pong( servername, nickname, time ));
    }

    public void privmsg( String who, String line )
    {
        // debug("privmsg("+who+","+line+")");
        // check message to channel, check if joined, else send
        if (who!= null && !who.equals(""))
        {
            if( ircChannels.containsChannel( who ) )
            {
                if( ircChannels.getChannel( who ).isJoined() )
                    sendMessage( IrcCommands.privmsg( servername, nickname, who, line ));

            }
            else
                sendMessage( IrcCommands.privmsg( servername, nickname, who, line ));
        }
    }

    public void quit()
    {
        sendMessage( IrcCommands.quit( servername, nickname, "Quit-signal received" ));
    }


// -------------------------------------------------------------------

	private boolean checkstring( String method, String name, String s )
    {
        boolean result = false;
        if( s == null )
        {
            log.error( method+"(): ERROR: string("+name+") is null!");
        }
        else
        if( s.equals("") )
        {
            log.error( method+"(): ERROR: string("+name+") is empty!");
        }
        else
            result = true;

        return result;
    }

    private boolean checkint( String method, String name, String s )
    {
        boolean result = false;

        if( s == null )
        {
            log.error( method+"(): ERROR: string("+name+") is null!");
        }
        else
        if( s.equals("") )
        {
            log.error( method+"(): ERROR: string("+name+") is empty!");
        }
        else
        {
            try
            {
                Integer.parseInt( s );
                result = true;
            }
            catch( NumberFormatException e )
            {
                //debug( method+"(): ERROR: string("+name+") is not a number!");
            }
        }
		
		return result;
	}

	private int getint( String method, String name, String s )
    {
        int result = -1;

        if( s == null )
        {
            log.error( method+"(): ERROR: string("+name+") is null!");
        }
        else
        if( s.equals("") )
        {
            log.error( method+"(): ERROR: string("+name+") is empty!");
        }
        else
        {
            try
            {
                result = Integer.parseInt( s );
            }
            catch( NumberFormatException e )
            {
                log.error( method+"(): ERROR: string("+name+") is not a number!");
            }
        }
        return result;
	}

}
