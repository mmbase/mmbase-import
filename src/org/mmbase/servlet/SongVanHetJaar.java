/*
        -----------------------------------------------------------------------
        WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING

								---------------
								PORT NEW SERVER
								---------------
        -----------------------------------------------------------------------
        -----------------------------------------------------------------------
								---------------
								PORT NEW SERVER
								---------------

        WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING
        -----------------------------------------------------------------------

*/

/**
 * File 		: SongVanHetJaar.java
 * Date			: $Date: 2000-02-04 11:59:23 $ 
 * Description 	: Read, check, evaluate and mail form for the 'Song van het Jaar'.  
 *
 * Pages are at : 
 *
 * Update (marmaa) : 
 * 		- made the files with answers little more flexible 
 * 		  (will work even if number of questions not specified)
 *		- extensive logging
 * 		- used methods to implement different functions
 *		- will check if name exists in form and only mail it when it exists
 * 		- Updated it for using it with multiple answersfiles (junior and senior questions)
 *		- and plugged in some documentation ...
 * 
 * todo :
 * 		- send page "No equal songs allowed"
 * 		- make sure user sends only one entry
 *  
 * @author  marmaa@vpro.nl (Marcel Maatkamp) 
 * @version $Revision: 1.1.1.1 $
 *
 * $Log: not supported by cvs2svn $
 * Revision 2.1  1999/12/02 14:51:00  wwwtech
 * - Changed email to marmaa for testing.
 *
 * Revision 2.0  1999/12/02 14:27:55  wwwtech
 * - This version is compilable under orion.
 *
 * Revision 1.5  1999/12/02 14:26:43  wwwtech
 * - Added database in import
 * - PrintStream is now in try/catch block :)
 *
 * Revision 1.4  1999/12/02 14:18:58  wwwtech
 * - Extended from jamesServlet
 * - copied imports from nwo.java
 * - PrintStream incorporated in try/catch block
 * - HttpPost used for getPostParameters
 *
 * Revision 1.3  1999/12/02 11:23:07  wwwtech
 * - commented the import statements to look at compile-time which packages are also to be ported
 *
 * Revision 1.2  1999/12/02 11:21:59  wwwtech
 * - Added comment-tags
 * - removed package tag
 *
 */

//package vpro.james.servlets;
package org.mmbase.servlet;

//import vpro.james.coreserver.*;
//import vpro.james.coremodules.*;
//import vpro.james.modules.*;
//import vpro.james.util.*;
//import vpro.james.modules.database.*;
//import java.sql.*;

import javax.servlet.http.*;
import javax.servlet.*;

import org.mmbase.servlet.JamesServlet;
import org.mmbase.util.HttpPost;
import org.mmbase.util.Mail;
import org.mmbase.module.SendMail;
import org.mmbase.module.SendMailInterface;
import org.mmbase.module.database.*;

import java.io.*;
import java.util.*;
import java.sql.*;

/**
 * Get the following parameters from form:
 * 		- username
 *		- email
 *
 *		- cdtracknr1
 *		- cdtracknr2
 *		- cdtracknr3
 * 
 * 	Get additional information:
 *		- ipaddress
 *		- cookie
 *
 *  Database:
 *
 *   table: songjaar under vpro
 *		rows:
 *			- naam			varchar(64)	not null
 *			- email			varchar(32)	not null
 * 			- cdtrack1 		integer 	not null
 * 			- cdtrack1 		integer 	not null
 * 			- cdtrack1 		integer 	not null
 *			- cookie		varchar(64)	not null
 *			- ipnumber		varchar(64)	not null
 * 	
 *	Put these in a file, mail username, email and
 *	cdtracks to "", log all and put all in database
 */

public class SongVanHetJaar extends JamesServlet 
{
	private String classname 	= getClass().getName();

	private	boolean debug		= true;

	private	String songFilename = "/export/home/wwwtech/SongVanHetJaar/1999/song.log";

	private String toEmail  	= "song1999@vpro.nl";
	//private	String toEmail		= "marmaa@vpro.nl";

	SendMailInterface sendmailinterface;
	JDBCInterface		jdbc;
	public void init() 
	{
		sendmailinterface=(SendMailInterface)getModule("sendmail");
		if( sendmailinterface == null )
			debug("init(): ERROR: Reference to module( sendmail ) could not be established!");

		jdbc = (JDBCInterface)getModule("JDBC");
		if( jdbc == null )
			debug("init(): ERROR: Reference to module( jdbc ) could not be established!");
	}

	/** 
	 * reload
	 */
	public void reload() 
	{
		sendmailinterface=(SendMailInterface)getModule("sendmail");
		if( sendmailinterface == null )
			debug("reload: ERROR: Reference to module( sendmail ) could not be established!");

		jdbc = (JDBCInterface)getModule("JDBC");
		if( jdbc == null )
			debug("reload: ERROR: Reference to module( jdbc ) could not be established!");
	}

	/**
 	* service call will be called by the server when a request is done
	* by a user.
 	*/
	public synchronized void service(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException,IOException 
	{
		// Get name, form and emailaddie
		// -----------------------------


		HttpPost hp = new HttpPost( req );

		String naam  	= hp.getPostParameter("naam");
		String email 	= hp.getPostParameter("email");

		String cdtrack1	= hp.getPostParameter("cdtrack1");
		String cdtrack2	= hp.getPostParameter("cdtrack2");
		String cdtrack3	= hp.getPostParameter("cdtrack3");

		//String cookie	= hp.getSessionName();	
		String cookie	= getCookie(req,res);	
		String ipnumber	= req.getRemoteAddr();

		String opm		= hp.getPostParameter("opmerking");
		
		if( debug )
			debug( "service( naam("+naam+"), email("+email+"), cookie("+cookie+"), ipnr("+ipnumber+"), cdtracks("+cdtrack1+","+cdtrack2+","+cdtrack3+"), opm("+opm+") )");
	
		if ( !checkstring( "service", "naam", naam ))
		{
			String err = "service( naam("+naam+"), email("+email+"), cookie("+cookie+"), ipnr("+ipnumber+") ): ERROR: No name given!";
			displayErrorUsername(res);
		}
		else
		{
			if( !checkstring( "service", "email", email )) 
			{
				email = toEmail;
				String err = "service( naam("+naam+"), email("+email+"), cookie("+cookie+"), ipnr("+ipnumber+") ): ERROR: No email given!";
				displayErrorMail( res );
			}
			if( !checkstring( "service", "cookie", cookie ))
			{
				String err = "service( naam("+naam+"), email("+email+"), cookie("+cookie+"), ipnr("+ipnumber+") ): ERROR: No cookie could be found, no email send!";
				debug( err );
				displayFailure( res );
			}
			else
			{
				if( !checkstring( "service", "ipnumber", ipnumber ))
				{
					String err = "service( naam("+naam+"), email("+email+"), cookie("+cookie+"), ipnr("+ipnumber+") ): ERROR: ipnumber not valid, no email send!";
					debug( err );
					displayFailure( res );
				}
				else
				{
					if( !cdtrack1.equals( cdtrack2 ))
					{
						if( !cdtrack2.equals(cdtrack3) )
						{
							if( !cdtrack3.equals(cdtrack1) )
							{

								try
								{
									int icd1 = Integer.parseInt( cdtrack1 );
									int icd2 = Integer.parseInt( cdtrack2 );
									int icd3 = Integer.parseInt( cdtrack3 );

									if( icd1 > 0 && icd2 > 0 && icd3 > 0 )
									{

											boolean isMailed = false;
	
										naam	= knip( naam,		64 );
										email  	= knip( email,		32 );

										//if ( email.indexOf(",") != -1 )
										//	email = email.substring( 0, email.
										cookie  = knip( cookie,		64 );
										ipnumber= knip( ipnumber,	64 );
					
										if( opm == null || opm.equals(""))
											opm = "(geen opmerking)";
		
										String body = constructEmail( naam, email, cdtrack1, cdtrack2, cdtrack3, opm );
			
										if ( !sendMail(naam, email, toEmail, body ) )
										{
											String err = "service( naam("+naam+"), email("+email+"), cookie("+cookie+"), ipnr("+ipnumber+") ): ERROR: Email is not been send!";
											debug( err );
											isMailed = false;
											displayErrorMail(res);
										}
										else
										{
											String err = "service( naam("+naam+"), email("+email+"), cookie("+cookie+"), ipnr("+ipnumber+") ): Succesfully mailed."; 
											debug( err );
											isMailed = true;
											displaySuccess(res);
										}
		
										if( addDatabase( naam, email, cookie, ipnumber, icd1, icd2, icd3 ) )
										{
											String err = "service( naam("+naam+"), email("+email+"), cookie("+cookie+"), ipnr("+ipnumber+") ): Successfully added in database.";
											writelog( body, isMailed, true );
											debug( err );  
										}
										else
										{
											String err = "service( naam("+naam+"), email("+email+"), cookie("+cookie+"), ipnr("+ipnumber+") choice("+cdtrack1+","+cdtrack2+","+cdtrack3+"), opm("+opm+")): ERROR: This entry is NOT added into the database!";
											debug( err );
											writelog( body, isMailed, false );
										} 
									}
									else
									{
										debug("service( naam("+naam+"), email("+email+"), cookie("+cookie+"), ipnr("+ipnumber+") choice("+cdtrack1+","+cdtrack2+","+cdtrack3+"), opm("+opm+")): ERROR: One of the tracks is negative!");
										displayErrorMail(res);
									}
								}
								catch( NumberFormatException e )
								{
									debug("service( naam("+naam+"), email("+email+"), cookie("+cookie+"), ipnr("+ipnumber+") choice("+cdtrack1+","+cdtrack2+","+cdtrack3+"), opm("+opm+")): ERROR: One of the tracks is not a number!");

									displayErrorMail(res);
								}
							}
							else
							{
								debug("service( naam("+naam+"), email("+email+"), cookie("+cookie+"), ipnr("+ipnumber+") choice("+cdtrack1+","+cdtrack2+","+cdtrack3+"), opm("+opm+")): ERROR: track1 equals track3!");
								displaySameTracks( res, 1,3 );
							}
						}
						else
						{
							debug("service( naam("+naam+"), email("+email+"), cookie("+cookie+"), ipnr("+ipnumber+") choice("+cdtrack1+","+cdtrack2+","+cdtrack3+"), opm("+opm+")): ERROR: track2 equals track3!");
							displaySameTracks( res, 2,3 );

						}
					}
					else
					{
						debug("service( naam("+naam+"), email("+email+"), cookie("+cookie+"), ipnr("+ipnumber+") choice("+cdtrack1+","+cdtrack2+","+cdtrack3+"), opm("+opm+")): ERROR: track1 equals track2!");
						displaySameTracks( res, 1,2 );

					}
				}
			}
		}
	}
	
	private void displayResult( HttpServletResponse res, String title, String message )
	{
		String result = ""; 

		// Set the content type of this request
		try
		{
			res.setContentType("text/html");
			//res.writeHeaders();
		PrintStream out = new PrintStream(res.getOutputStream());
		
		result += "<HTML>\n";
		result += "<HEAD>\n";
		result += "   <TITLE>"+title+"</TITLE>\n";
		result += "</HEAD>\n";
		result += "<BODY BGCOLOR=\"#000000\" LINK=\"#FFFFFF\" VLINK=\"#FFFFFF\" ALINK=\"#FFFFFF\">\n";
		result += "\n";
		result += "<P><CENTER><TABLE BORDER=0 WIDTH=500 HEIGHT=\"85%\">\n";
		result += "   <TR>\n";
		result += "      <TD bgcolor=\"#FF0000\">\n";
		result += "         <P><CENTER><B><FONT SIZE=\"+1\" FACE=\"Helvetica, Arial\">";
		
		result += message;
	
		result += " Op vrijdag 24 december om 22:00 uur zijn de songs te beluisteren in <B>Het Lek</B> op radio 3.\n";	
		result += "         </FONT></B></CENTER></P>\n";
		result += "      </TD></TR>\n";
		result += "</TABLE></CENTER></P>\n";
		result += "</BODY>\n";
		result += "</HTML>\n";

		out.println( result );
		}
		catch (IOException e) { e.printStackTrace(); }
	}


	private void displayFailure( HttpServletResponse res )
	{
		String titel = "Formulier is NIET verstuurd";
		String body  = "Uw 'songs van het jaar' zijn NIET verstuurd naar 3voor12.<BR>\n";
			   body += "Er is een fout opgetreden tijdens de verwerking van uw formulier!!<BR>\n";
		displayResult(res, titel, body);
	}

	private void displaySameTracks( HttpServletResponse res , int track1, int track2 )
	{
		String titel = "Formulier is NIET verstuurd";
		String body  = "Uw 'songs van het jaar' zijn NIET verstuurd naar 3voor12.<BR>\n";
			   body += "Uw geselecteerde track("+track1+") en track("+track2+") zijn hetzelfde!<BR>\n";
		displayResult(res, titel, body);
	}

	private void displaySuccess( HttpServletResponse res )
	{
		String titel = "Formulier is verstuurd";
		String body  = "Uw 'songs van het jaar' zijn verstuurd naar 3voor12.<BR>\n";
			   body += "Bedankt voor het meedoen!<BR>\n";
		displayResult(res, titel, body);
	}
	
	private void displayErrorUsername ( HttpServletResponse res )
	{
		String titel = "Uw naam is niet ingevuld";
		String body  = "Uw naam is niet ingevuld in uw formulier. <BR>\n";
			   body += "<STRONG>Uw formulier is daarom NIET opgestuurt</STRONG><BR>\n";
		displayResult(res, titel, body);
	}

	private void displayErrorMail ( HttpServletResponse res )
	{
		String titel = "Fout tijdens versturen email";
		String body  = "Er is een fout opgetreden tijdens het versturen van uw formulier.<BR>\n";
			   body += "Er is melding van gemaakt aan de beheerder, maar u kunt het later nog eens opnieuw opsturen voor de zekerheid.<BR>\n";
		displayResult(res, titel, body);
	}

	public String constructEmail( String naam, String email, String cdtrack1, String cdtrack2, String cdtrack3, String opm )
	{
		String result = null;
		StringBuffer buf = new StringBuffer();

		buf = makeup( buf, "naam",		naam 	);
		buf = makeup( buf, "email",		email 	);	
		buf = makeup( buf, "datum",		getDate());
		buf = makeup( buf, "cdtrack1",	cdtrack1);	
		buf = makeup( buf, "cdtrack2",	cdtrack2);
		buf = makeup( buf, "cdtrack3",	cdtrack3);
		buf = makeup( buf, "opmerking",	opm		);
		
		result = buf.toString();
		return result;
	}

	private StringBuffer makeup( StringBuffer buf, String item, String value )
	{
		if( checkstring( "makeup", "item", item ))
		{
			if( checkstring( "makeup", "value", value ))
			{
				buf.append( item );
				if (item.length() < 12 && item.length() > 0 )
				{
					int tabLength = ((12 - item.length()+2) / 4) ;
					for (int i =1; i< tabLength; i++)
						buf.append( "\t" );
				}

				buf.append( " = " );

				if (item.toLowerCase().equals("opmerking"))
					buf.append( "\n\n" );
				buf.append( value + "\n" );

				if (item.toLowerCase().equals("datum") || item.toLowerCase().equals("cdtrack3"))
					buf.append( "\n" );
			}
		}
		return buf;
	}


	/** 
	 * Send mail
	 *
	 * @returns true when send, false otherwise
	 */
	private boolean sendMail( String name, String from, String to, String body )
	{
		boolean result = false;

		// debug("sendMail( name("+name+"), from("+from+"), to("+to+"), body("+body+"): request for send");

		Mail mail = new Mail( to, from );
		if (mail != null)
		{
			mail.setDate();
			mail.setSubject("[SONG1999] - " + name);
			mail.setReplyTo(to);
			mail.setText(body);

			if (sendmailinterface != null) 
			{
				result = sendmailinterface.sendMail(mail);
			}
			else
			{
				debug("sendMail( name("+name+"), from("+from+"), to("+to+"), body("+body+"): ERROR: Object Sendmail is null! Possible cause: no rights given for module sendmail in ACL to ["+classname+"].");
			}
		}
		else
		{
			debug("sendMail( name("+name+"), from("+from+"), to("+to+"), body("+body+") ERROR: Not mailed, no object Mail");
			result = false;
		}
		return result;
	}

	/**
 	 * Writes mail to logbestand
	 */
	private void writelog( String verzend , boolean isMailed , boolean isAddedDB) 
	{
   		try 
		{
			RandomAccessFile  SecurityLog = new RandomAccessFile(songFilename,"rw"); 
			if( SecurityLog != null )
			{
				SecurityLog.seek(SecurityLog.length());
				
					SecurityLog.writeBytes("-------------------------------------------------------------\n");
			
				if (!isMailed )
				{
					SecurityLog.writeBytes("*************************************************************\n");
					SecurityLog.writeBytes("*** THIS ENTRY COULD NOT BE MAILED, PLEASE VERIFY BY HAND ***\n");
					SecurityLog.writeBytes("*************************************************************\n");
					SecurityLog.writeBytes("\n");
				}

				if( !isAddedDB )
				{
					SecurityLog.writeBytes("*************************************************************\n");
					SecurityLog.writeBytes("*** THIS ENTRY NOT INSERTED IN DATABASE, COUNT SEPERATELY ***\n");
					SecurityLog.writeBytes("*************************************************************\n");
					SecurityLog.writeBytes("\n");
				}

				SecurityLog.writeBytes(verzend);
				SecurityLog.close();
			}
			else
				debug("writelog(): ERROR: while writing("+verzend+"): Could not write to file("+SecurityLog+"), filename("+songFilename+"): not created ?!?!");
		}
		catch (IOException ioe)
		{
			debug("writelog(): ERROR: While writing("+verzend+"): " + ioe.toString() );
		}
	}
	
	/**
	* Info method, provides the user/server with some basic info on
	* this Servlet
 	*/
	public String getServletInfo() 
	{
		return ("Song vh Jaar, 1999, Marcel Maatkamp (marmaa@vpro.nl)");
	}

	private void debug( String msg )
	{
		System.out.println(classname + ":" + msg);
	}

	private boolean checkstring( String method, String name, String s )
	{
		boolean result = false;
		if( method != null )
		{
			if( !method.equals("") )
			{
				if( name != null )
				{
					if( !name.equals("") )
					{
						if( s != null )
						{
							if( !s.equals("") )
							{
									result = true;
							}
							else
								debug(method + "(): ERROR: "+name+"("+s+") is empty!");	
						}
						else	
							debug(method + "(): ERROR: "+name+"("+s+") is null!");	
					}
					else
						debug("checkstring("+method+","+name+","+s+"): ERROR: name("+name+") is empty!");
				}
				else
					debug("checkstring("+method+","+name+","+s+"): ERROR: name("+name+") is null!");
			}
			else
				debug("checkstring("+method+","+name+","+s+"): ERROR: method("+method+") is empty!");
		}
		else
			debug("checkstring("+method+","+name+","+s+"): ERROR: method("+method+") is null!");

		return result;
	}

	private String knip( String s, int l )
	{
		String result = null;
		if( s.length() > l )
			result = s.substring( 0, l );
		else
			result = s;

		s = s.trim();

		return result;
	}

	private boolean addDatabase( String naam, String email, String cookie, String ip, int cdtrack1, int cdtrack2, int cdtrack3)
	{
		boolean result = false;

		try
		{
			MultiConnection con = jdbc.getConnection(jdbc.makeUrl("vpro"));
			PreparedStatement stm = null;
			try
			{
            	stm=con.prepareStatement("insert into songjaar2 values(?,?,?,?,?,?,?);");

				if( stm != null )
				{
					stm.setString(	1, naam  	);
					stm.setString(	2, email 	);
					stm.setInt(		3, cdtrack1 );
					stm.setInt(		4, cdtrack2 );
					stm.setInt(		5, cdtrack3 );
					stm.setString(	6, cookie	);
					stm.setString(	7, ip		);
	
   		            stm.executeUpdate();
					result = true;
				}
				else
					debug("addDatabase( naam("+naam+"), email("+email+"), cookie("+cookie+"), ip("+ip+"), cdtracks("+cdtrack1+","+cdtrack2+","+cdtrack3+"): ERROR: statemement("+stm+") is null!");
		
			}
			catch( SQLException queryException )
			{
				debug("addDatabase( naam("+naam+"), email("+email+"), cookie("+cookie+"), ip("+ip+"), cdtracks("+cdtrack1+","+cdtrack2+","+cdtrack3+"): ERROR: Could not execute query("+stm+")!");
			}

			try
			{
				if( stm != null ) stm.close();
				if( con != null ) con.close();
			}
			catch( SQLException conClose )
			{
				debug("addDatabase( naam("+naam+"), email("+email+"), cookie("+cookie+"), ip("+ip+"), cdtracks("+cdtrack1+","+cdtrack2+","+cdtrack3+"): ERROR: Could not close database!" + conClose.toString());

			}
		}
		catch( SQLException conException )
		{
			debug("addDatabase( naam("+naam+"), email("+email+"), cookie("+cookie+"), ip("+ip+"), cdtracks("+cdtrack1+","+cdtrack2+","+cdtrack3+"): ERROR: Could not open database!" + conException.toString() );
		}
		
		return result;
	}

	public String getDate()
	{
		String result = null;

		long l = System.currentTimeMillis();
		java.util.Date d = new java.util.Date( l );
		result = d.toString();
	
		return result;
	}

	private static void main( String args[] )
	{
		SongVanHetJaar s = new SongVanHetJaar();
		System.out.println( s.constructEmail( "Marcel Maatkamp", "marmaa@vpro.nl", "1", "2", "3", "Goed man!") );
	}
}
