/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.irc.communication.irc;
import org.mmbase.util.logging.*;

import java.util.StringTokenizer;

public class IrcMessage
{
    private static Logger log = Logging.getLoggerInstance(IrcMessage.class.getName());
	// message: [:<server|user> ]command [to] :[<params>]

	private String message;

	private String server;

	private String from;
	private String command;
	private String to;
	private String middle;
	private String params;

	public IrcMessage()
	{

	}
	
	public IrcMessage( String line )
	{
		if (line != null && !line.equals(""))
			decode( line );
		/*
		else
			debug("IrcMessage("+line+"): This is not a valid response!");
		*/
	}

	public IrcMessage( String server, String from, String command, String to, String middle, String params )
	{
		this.server = server;
		this.from = from;
		this.command = command;
		this.to	= to;
		this.middle = middle;
		this.params	= params;
	}

	public String construct()
	{
		String result = "";

		if (from != null && !from.equals(""))
			result += ":" + from + " ";

		if (command != null && !command.equals(""))
			result += command + " ";
		else
			log.debug("construct: Parameter command is null or empty!");

		if (to != null && !to.equals(""))
			result += to + " ";

		if (middle != null && !middle.equals(""))
			result += middle + " ";

		if (params != null && !params.equals(""))
		{
			result += ":";
			if (params.indexOf("\n") != -1)
			{
			// send sentences with header prefix 

				String tmpMsg = result;
				StringTokenizer tok = new StringTokenizer( "\n", params );
				boolean first = true;
				String line;
				while (tok.hasMoreTokens())
				{
					line = tok.nextToken();
					System.out.println(" line = " + line );
					if (!line.equals(""))
					{
						result += line + '\n' + tmpMsg;
						first = false;
					}
				}
				if (first)
					result += params;
			}
			else
				result += params;
		}
		return result;
	}

	public String construct( String _from, String _command, String _to, String _middle, String _params )
	{
		String result = "";

		if (_from != null && !_from.equals(""))
			result += ":" + _from + " ";

		if (_command != null && !_command.equals(""))
			result += _command + " ";
		else
			log.error("construct("+_from+","+_command+","+_to+"," + _middle + ", " +_params+"): ERROR: Parameter command is null or empty!");

		if (_to != null && !_to.equals(""))
			result += _to + " ";

		if (_middle != null && !_middle.equals(""))
			result += _middle + " ";

		result += ":";

		if (_params != null && !_params.equals(""))
		{
			// send sentences with header prefix 

			String tmpMsg = result;
			StringTokenizer tok = new StringTokenizer( "\n", _params );
			boolean first = true;
			String line;
			while (tok.hasMoreTokens())
			{
				line = tok.nextToken();
				if (!line.equals(""))
				{
					result +=  line + '\n' + tmpMsg;
					first = false;
				}
			}
			if (first)
				result += _params;
		}
		//System.out.println("****** " + result );
		return result;
	}

	public void decode( String message )
	{
		String originalMessage = message;

		from = null; command = null; to = null; middle = null; params = null;

		if (checkMessage(message))
		{
			// keep whole message for eq printout

			this.message = message;

			// check from (begins with ':')
			// ----------------------------

			if (message.startsWith(":"))
			{
				int spaceIndex = message.indexOf(" ");
				if (spaceIndex > 0)
				{
					from 	= message.substring( 1, spaceIndex 	).trim();
					message = message.substring( spaceIndex+1 	);
				}
				else
					log.error("decode("+originalMessage+"): ERROR: Trying to decode FROM-clause, but seems that it is a strange message (missing space after ':')! (parsed: " + message+")");
			}

			// check command
			// -------------

			{
				int spaceIndex = message.indexOf(" ");
				if (spaceIndex > 0)
				{
					command = message.substring( 0, spaceIndex ).trim();
					message = message.substring( spaceIndex+1  );
				}
				else
					log.error("decode("+originalMessage+"): ERROR: Trying to decode COMMAND-clause, but seems that it is a strange message (missing space after COMMAND)! (parsed: " + message+")");
			}

			// decode to
			// ---------
		
			{
				if ( !message.startsWith(":") )
				{
					int spaceIndex = message.indexOf(" ");
					if( spaceIndex >= 0 )
					{
						to = message.substring( 0, spaceIndex ).trim();
						message = message.substring( spaceIndex+1 );
					}
					else
						log.debug("decode(): spaceindex == -1");
				}
			}


			// middle decode (till ':')
			// ------------------------
			{

				int position = message.indexOf(":");
				if (position == -1)
				{
					middle = message;
					message = "";
				}
				else
				{
					middle = message.substring(0, position).trim();
					message = message.substring( position );
				}
			}



			// check ':'
			// ---------

			{
				if (message.startsWith(":"))
				{
					message = message.substring(1).trim();
				}
				//else
					//debug("decode("+originalMessage+"): ERROR: Trying to decode :-clause, but seems that it is a strange message (missing ':')! (parsed: " + message+")");
			}

			// decode params
			// -------------
			{
				params = message;
			}
		}
		else
			log.error("decode("+originalMessage+"): ERROR: Could not decode message!");
	}
	
	public boolean checkMessage()
	{
		return checkMessage( this.message );
	}

	public boolean checkMessage( String message )
	{
		boolean result = false;

		if (message == null)
			log.error( "checkMessage("+message+"): ERROR: Parameter message is null!");
		else
			if (message.equals(""))
				log.error( "checkMessage("+message+"): ERROR: Parameter message is empty!");
		else
			result = true;

		return result;
	}

	public String server()
	{
		return this.server;
	}

	public String from()
	{
		return this.from;
	}

	public String command()
	{
		return this.command;
	}

	public String to()
	{
		return this.to;
	}

	public String middle()
	{
		return this.middle;
	}

	public String params()
	{
		return this.params;
	}

	public void setServer( String server )
	{
		this.server = server;
	}

	public String getServer()
	{
		return server;
	}
	
	public String toString()
	{
		String result = "";
		//result = construct();
		result += "server [" + getServer() + "], from [" + from() + "], command [" + command() + "], to [" + to() + "], middle["+ middle() + "], params: [" + params() + "]";	
		return result;	
	}

	public static void main(String args[])
	{
		IrcMessage im = new IrcMessage( );
/*
		im.decode( ":irc.vpro.nl 101 ronnie :Welcome" );
		System.out.println( "from   :" + im.from() 	  );
		System.out.println( "command:" + im.command() );
		System.out.println( "to     :" + im.to() 	  );
		System.out.println( "params :" + im.params()  );
		System.out.println( "1.)" + im.construct() );		
*/
		im.decode( im.construct("toronto.on.ca.undernet.org", "001", "ronnie", "", "Welcome") ); System.out.println( im.toString() );
		im.decode( im.construct("", "PING", "", "", "91227373 This is a Ping message : message") ); System.out.println( im.toString() );
		//System.out.println( "2.)" + im.decode( im.construct("toronto.on.ca.undernet.org", "001", "ronnie", "", "Welcome") ).toString() );
		//System.out.println( "3.)" + im.construct("", "PING", "", "", "91234566") );
	}
}
