/**
 * File 		: SimpleFormServlet.java
 *
 * Description	: 
 * 
 * 	This servlet will mail a form to a (set of) specified user(s). 
 *  Inherit from it (its abstract), specify the methods:
 *  	public abstract String getSubject();
 *  	public abstract String getToEmailAddress();
 *  and the form will be mailed.
 *
 * @author  marmaa@vpro.nl (Marcel Maatkamp) 
 * @version 2.0.1  
 */

package org.mmbase.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.http.*;
import java.util.*;

import org.mmbase.util.*;
import org.mmbase.module.*;


/**
 * 	Post Servlet a example of how to use different Post methods
 */

public abstract class SimpleFormToMailServlet extends JamesServlet 
{
	private boolean debug = false;

	protected SendMailInterface sendmail;
	boolean first=true;

	String entries[] = null;
	
	public void init() {
		sendmail=(SendMailInterface)Module.getModule("sendmail");
		if( sendmail == null ) {
			debug("init(): sendmail is null!!!");
		} else {
			if( debug ) 
				debug("init(): successfully initialized.");
		}
	}

	/** 
	 * reload
	 */
	public void reload() {
		sendmail=(SendMailInterface)Module.getModule("sendmail");
		if( sendmail == null ) {
			debug("reload(): sendmail is null!!!");
		} else {
			if( debug ) 
				debug("reload(): successfully reloaded.");
		}
	}

	/**
 	* service call will be called by the server when a request is done
	* by a user.
 	*/
	public synchronized void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {

		String from		= null;
		String to		= null;
		String subject	= null;
		String text		= null;
	
		HttpPost hp 	= new HttpPost( req );
		from 			= hp.getPostParameter("email");
		to				= getToEmailAddress();
		subject			= getSubject();
		text			= getentries( hp );

		if ( !sendmail( from, to, subject, text) ) {
			debug("service(): ERROR: mail from("+from+"), to("+to+"), subject("+subject+"), text("+text+"): not mailed!");
			displayErrorMail(res);
		} else {
			debug("service(): mail from("+from+"), to("+to+"), subject("+subject+"), text("+text+"): mailed!");
			displaySuccess(res);
		}
	}

	
	protected String getentries( HttpPost post ) {
		String result = "";
		Hashtable postparams = post.getPostParameters(); 
		Enumeration e = postparams.keys();
		String key, value;
		while( e.hasMoreElements() ) {
			key = (String) e.nextElement();
			value = (String) post.getPostParameter( key );
			result += key +":\t\t" + value + "\n";	
		}
		return result;
	}

	public abstract String getSubject();

	public abstract String getToEmailAddress();

	/**
	* Produces a 'standard' header of html-form.
	* Override when in need for different layout.
	*/
	public String getHtmlHeader( String title ) {
		StringBuffer b = new StringBuffer();

	    b.append( "<HTML>\n" 													);
        b.append( "<HEAD>\n"													);
        b.append( "   <TITLE>"+title+"</TITLE>\n"								);
        b.append( "</HEAD>\n"													);
        b.append( "<BODY BGCOLOR=\"#000000\">\n"								);
        b.append( "\n"															);
        b.append( "<P><CENTER><TABLE BORDER=0 WIDTH=500 HEIGHT=\"95%\">\n"		);
        b.append( "   <TR>\n"													);
        b.append( "      <TD bgcolor=\"#FFFFFF\">\n"							);
        b.append( "         <P><CENTER><B><FONT SIZE=\"+1\" FACE=\"Arial\">"	);	

		return b.toString();
	}

	public String getHtmlFooter() {
		StringBuffer b = new StringBuffer();
        b.append( "         </FONT></B></CENTER></P>\n"							);
        b.append( "      </TD></TR>\n"											);
        b.append( "</TABLE></CENTER></P>\n"										);
        b.append( "</BODY>\n"													);
        b.append( "</HTML>\n"													);
		return b.toString();
	}

	/**
	* write this semi-html page to user's browserwindow, displaying status.
	*/
	private void displayResult( HttpServletResponse res, String title, String message ) {
		try {
			String result = ""; 
			PrintStream out = new PrintStream(res.getOutputStream());
			// Set the content type of this request
			try {
				res.setContentType("text/html");
				//res.writeHeaders();
				res.flushBuffer();
			} catch (IOException e) { e.printStackTrace(); }
			result += getHtmlHeader( title );		
			result += message;
			result += getHtmlFooter();
			out.println( result );
		} catch( Exception e ) { debug("displayResults(): ERROR: " + e ); }
	}

	private void displaySuccess( HttpServletResponse res ) {
		String titel = "Formulier is verstuurd";
		String body  = "Uw formulier is verstuurd.<BR>\n";
			   body += "Bedankt voor het meedoen aan deze quiz!";
		displayResult(res, titel, body);
	}
	
	private void displayErrorUsername ( HttpServletResponse res ) {
		String titel = "Uw naam is niet ingevuld";
		String body  = "Uw naam is niet ingevuld in uw formulier. <BR>\n";
			   body += "<STRONG>Uw formulier is daarom NIET opgestuurt</STRONG>";
		displayResult(res, titel, body);
	}

	private void displayErrorEntry ( HttpServletResponse res ) {
		String titel = "Entry niet gevonden in dokument";
		String body  = "Het systeem heeft een fout gedetecteerd tijdens het verwerken van uw formulier.<BR>\n";
			   body += "<STRONG>Uw formulier is daarom NIET opgestuurt</STRONG>";
		displayResult(res, titel, body);
	}

	private void displayErrorMail ( HttpServletResponse res ) {
		String titel = "Fout tijdens versturen email";
		String body  = "Er is een fout opgetreden tijdens het versturen van uw formulier.<BR>\n";
			   body += "Er is melding van gemaakt aan de beheerder, maar u kunt het later nog eens opnieuw opsturen voor de zekerheid.";
		displayResult(res, titel, body);
	}


	/** 
	 * Send mail
	 *
	 * @returns true when send, false otherwise
	 */
	private boolean sendmail( String from, String to, String subject, String text ) {
		debug("sendmail(): from("+from+"), to("+to+"), subject("+subject+"), text("+text+")");

		boolean result = false;
		Mail mail = new Mail(to, from);
		mail.setTo( to );
		mail.setFrom( from );
		mail.setSubject( subject );
		mail.setDate();
		mail.setReplyTo(from );
		mail.setText(text);
		result = sendmail.sendMail(mail);
		return result;
	}

	
	/**
	* Info method, provides the user/server with some basic info on
	* this Servlet
 	*/
	public String getServletInfo() {
		return ("SimpleFormToMailServlet - Marcel Maatkamp");
	}
}
