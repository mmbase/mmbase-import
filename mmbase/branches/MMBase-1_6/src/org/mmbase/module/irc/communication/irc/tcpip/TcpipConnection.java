/* -*- tab-width:4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.irc.communication.irc.tcpip;

import java.io.*;
import java.net.*;
import java.util.*;

import org.mmbase.module.irc.communication.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public 	class TcpipConnection extends Thread
{
    private static Logger log = Logging.getLoggerInstance(TcpipConnection.class.getName());

//  -----------------------------------------------------------------
	private	Socket						socket		= null;
	private	DataInputStream				in			= null;
	private	DataOutputStream			out			= null;	
	public	CommunicationUserInterface 	comuser		= null;
//  -----------------------------------------------------------------
	public	String 						hostname	= null;
	public	String 						servername	= null;
	public	int							port		= -1;
//  -----------------------------------------------------------------
	boolean						connected 	= false;
	boolean						doit		= false;
//  -----------------------------------------------------------------
	private	TcpipSendThread		sender		= null;
	TcpipVector					sendbuf		= null;
	String						sendline	= null;

	private	TcpipReceiveThread	receiver	= null;
	TcpipVector					receivebuf	= null;
	String						receiveline = null;

	String						receivedline= null;
//  -----------------------------------------------------------------

	public TcpipConnection( )
	{
	
	}
		
	public void initialize( CommunicationUserInterface comuser )
	{
		this.comuser 	= comuser;
		this.sendbuf 	= new TcpipVector();
		this.receivebuf = new TcpipVector();
	}

	public void startit()
	{
		doit = true;
		this.start();
	}

	public void run()
	{
		//log.debug("run(): start");
		while( doit )
		{
			if( isconnected() )
			{
				if (log.isDebugEnabled()) {
                    log.debug("run(): received(), doit("+doit+"), isconnected("+isconnected()+")");
                }
				received();
			}
			else
			{
				log.info("disconnected, reconnect()!");
				try
				{
					sleep( 1000 );
				}
				catch(InterruptedException e )
				{
                    log.info("run(): Interrupted(): " + e.toString());
				}
				reconnect();	
			}
		}
	}

	public boolean connect( String servername, int port )
	{
		boolean result = false;

		int tries = 0, maxtries = 10;
		
		while( !isconnected() && tries < maxtries )
		{
			try
			{
				if( checkstring("connect", "servername", servername) )
				{
					this.socket 	= new Socket( servername, port );
					this.servername = servername;
					this.port		= port;
	
					this.hostname	= socket.getInetAddress().getHostName();
					this.in			= new DataInputStream(  socket.getInputStream()  );	
					this.out		= new DataOutputStream( socket.getOutputStream() );
	
					this.sender		= new TcpipSendThread( this );
					this.sender.startThread();
					this.sender.started();
	
					this.receiver	= new TcpipReceiveThread( this );
					this.receiver.startThread();
					this.receiver.started();

					this.connected 	= true;

					this.startit();
	
					result = true;
				}
			}
			/*
			// macintoshes do not know a ...
			catch( ConnectException e3 )
			{
				log.debug("connect( hostname("+hostname+"), port("+port+"): ERROR: Could not connect, refused! " + e3.toString() );
			}
			*/
			catch( UnknownHostException e )
			{
				log.error("connect( hostname("+hostname+"), port("+port+"): Host not found! " + e.toString() );
			}
			catch( IOException e2 )
			{
				log.error("connect( hostname("+hostname+"), port("+port+"): Could not connect! " + e2.toString() );
			}
			catch( Exception e3 )
			{
				log.error("connect( hostname("+hostname+"), port("+port+"): Could not connect! " + e3.toString() );
			}
			
			if( !isconnected() )
			{
				tries++;
				try
				{
					log.warn("connect(): not connected after " + tries +" time(s), waiting for 10 secs to retry!");
					sleepfor( 10000 );
				}
				catch( InterruptedException e )
				{
					log.warn("connect(): interrupted!" + e.toString() );
				}
			}
		}
		if( !isconnected() )
			log.warn( "connect(): giving up after "+maxtries+" times to connect.");
		return result;
	}

	public boolean reconnect()
	{
		log.info("reconnect(): start");

		boolean result = false;
		int tries = 0, maxtries = 10;

		log.info("reconnect(): stopping threads");
		haltit();

		while( !isconnected() && tries < maxtries )
		{

			try
			{
				sleep( 1000 ); 	// let socket peacefully come up
			}
			catch( InterruptedException e )
			{
				log.warn("reconnect(): Interrupted!: " + e.toString() );
			}
				
			try
			{
		log.debug("reconnect(): starting connection");
				this.socket 	= new Socket( servername, port );
				this.in			= new DataInputStream(  socket.getInputStream() );
				this.out		= new DataOutputStream( socket.getOutputStream() );
	
	/**	
					sendbuf			= new TcpipVector();	
					this.sender		= new TcpipSendThread( this );
					this.sender.startThread();
					this.sender.started();
	
					receivebuf		= new TcpipVector();	
					this.receiver	= new TcpipReceiveThread( this );
					this.receiver.startThread();
					this.receiver.started();
	*/

				this.connected	= true;
				resumeit();
				result = true;
			}
			/*
			catch( ConnectException e3 )
			{
				log.error("reconnect( hostname("+hostname+"), port("+port+"): Connection refused! " + e3.toString() );
			}
			*/
			catch( UnknownHostException e ) 
			{
				log.error("reconnect( hostname("+hostname+"), port("+port+"): Host not found! " + e.toString() );
			}
			catch( IOException e2 )
			{
				log.error("reconnect( hostname("+hostname+"), port("+port+"): Could not connect! " + e2.toString() );
			}
			catch( Exception e3 )
			{
				log.error("reconnect( hostname("+hostname+"), port("+port+"): Could not connect! " + e3.toString() );
			}	

			if( !isconnected() )
			{
				tries++;
				try
				{
					log.warn("reconnect(): could not connect for " + tries +" time(s), wait for 10 secs before retrying!");	
					sleepfor( 10000 );
				}
				catch( InterruptedException e )
				{
					log.warn("reconnect(): Interrupted! " + e.toString() );
				}
			}
		}
		return result;
	}

	public synchronized void sleepfor( long time )
		throws InterruptedException 
	{
		sleep( time );
	}

//  ----------------------------------------------------------------------------------------------------------------

	public void connected()
	{
		connected = true;
	}

	public void disconnected()
	{
		connected = false;
	}

	public boolean isconnected()
	{
		return this.connected;
	}

//  ----------------------------------------------------------------------------------------------------------------

	public void write( String s )
		throws IOException 
	{
		if (log.isDebugEnabled()) {
            log.debug("write("+s+")");
        }
		out.writeBytes( s );
		out.flush();
	}

	/**
	 * called by thread
	 */
	public void send()
	{
		if (log.isDebugEnabled()) {
            log.debug("send()");
        }
		try
		{
			if( sendline != null )
			{
				log.info("send(): Saw a reconnect, resending("+sendline+")!");
				write( sendline );
				sendline = null;
			}
	
			try
			{
				sendbuf.mywait( 1000 );
			}
			catch( InterruptedException e )
			{
				log.warn("send(): Interrupted! " + e.toString() );
			}

			if (log.isDebugEnabled()) {
                log.debug("send(): sendbufsize("+sendbuf.size()+")");
            }
	
			while( !sendbuf.isEmpty() )
			{
				sendline = (String) sendbuf.firstElement(); // get
				sendbuf.removeElement( sendline );			// remove from list in case of IOException
				write( sendline );							// send, if fail, resend takes care
				sendline = null;
			}
		}
		catch( IOException e )
		{
			connected = false;
			haltit();
			log.error( "send(): Could not send("+sendline+"), signalling reconnect! " + e.toString() );
		}
	}

	/**
	 * called by user
	 */
	public  void send( String s )
	{
		sendbuf.addElement( s );
		sendbuf.mynotify();
	}

//  -----------------------------------------------------------------

	public String read()
		throws IOException 
	{
		String result = null;

		result = in.readLine();

		return result;
	}

	/**
	 * called by thread
	 */
	public void receive()
	{
		try
		{
			receiveline = read();
			if( receiveline != null )
			{
				//if( receiveline.equals("") )
				{
					receivebuf.addElement( receiveline );
					//log.debug("receive("+receiveline+")");
					receiveline = null;
					receivebuf.mynotify();
				}
			}
			else
			{
				log.warn("receive("+receiveline+"): Got null, reconnect()!");
				connected = false;
				haltit();
			}
		}
		catch( IOException e )
		{
			connected = false;
			haltit();
			log.error("receive(): Could not receive("+receiveline+"), signalling reconnect! " + e.toString() );
		}
	}

	/**
	 * called by user
	 */
	public void received()
	{
		try
		{
			receivebuf.mywait( 1000 );
		}
		catch( InterruptedException e )
		{
			log.warn("received(): Interrupted! " + e.toString() );
		}

		if (log.isDebugEnabled()) {
            log.debug("receive(): receivebuf.size("+receivebuf.size()+")");
        }

		Enumeration e = receivebuf.elements();
		while( e.hasMoreElements() )
		{
			receivedline = (String) e.nextElement();
			receivebuf.removeElement( receivedline );
			// log.debug("received(): pushing("+receivedline+")");
			receive( receivedline );
		}
	/**
		while( !receivebuf.isEmpty() )	
		{
			Enumeration e = receivebuf.elements();
			if( e.hasMoreElements() )
			{

			receivedline = (String) receivebuf.firstElement();
			receivebuf.removeElement( receivedline );
			if (log.isDebugeEnabled()) {
                 log.debug("received(): pushing line("+receivedline+")");
            }
			receive( receivedline );
		}
	*/
	}

	public void receive( String line )
	{
		// debug("receive("+line+"): pushing to comuser("+comuser+")");
		comuser.receive( line );
	}
	
//  -----------------------------------------------------------------

	public void haltit()
	{
		//sender.stop();
		//receiver.stop();
		sender.suspend();
		receiver.suspend();
	}
	
	public void resumeit()
	{
		//sender.start();
		//receiver.start();
		
		sender.resume();	
		receiver.resume();
	}

	public void stopit()
	{
		doit = false;
		this.stop();

		sender.stop(); 		sender   = null;
		receiver.stop();	receiver = null;

		sendbuf.removeAllElements();	
		sendbuf = null;

		receivebuf.removeAllElements();
		receivebuf = null;		
	}

//  -----------------------------------------------------------------

	private boolean checkstring( String method, String name, String s )
	{
		boolean result = false;
		if( s == null )
		{
			log.error( method+"(): string("+name+") is null!");
		}
		else
		if( s.equals("") )
		{
			log.error( method+"(): string("+name+") is empty!");
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
			log.error( method+"(): string("+name+") is null!");
		}
		else
		if( s.equals("") )
		{
			log.error( method+"(): string("+name+") is empty!");
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
				log.error( method+"(): string("+name+") is not a number!");
			}
		}

		return result;
	}

	private int getint( String method, String name, String s )
	{
		int result = -1;

		if( s == null )
		{
			log.error( method+"(): string("+name+") is null!");
		}
		else
		if( s.equals("") )
		{
			log.error( method+"(): string("+name+") is empty!");
		}
		else
		{
			try
			{
				result = Integer.parseInt( s );
			}
			catch( NumberFormatException e )
			{
				log.error( method+"(): string("+name+") is not a number!");
			}
		}
		return result;
	}

	/**
	public static void main( String[] args )
	{
		TcpipConnection tcpipcon = new TcpipConnection();
		if (tcpipcon.connect( "beep.vpro.nl", 6667) )
		{
			tcpipcon.startit();
		}
		else
			log.warn("Could not connect!");
	}
	*/
}
