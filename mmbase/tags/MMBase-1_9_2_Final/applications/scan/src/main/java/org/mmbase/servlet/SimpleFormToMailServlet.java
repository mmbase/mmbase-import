/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import org.mmbase.util.*;
import org.mmbase.module.*;
import org.mmbase.util.logging.*;

/**
 * 	Post Servlet a example of how to use different Post methods.
 *
 * 	This servlet will mail a form to a (set of) specified user(s).
 *  Inherit from it (its abstract), specify the methods:
 *  	public abstract String getSubject();
 *  	public abstract String getToEmailAddress();
 *  and the form will be mailed.
 *
 * @javadoc
 * @application SCAN (depends from JamesServlet). Also depends on Email.
 * @deprecated Abstract and not used anywhere.
 * @author  marmaa@vpro.nl (Marcel Maatkamp)
 * @version $Id$
 */
public abstract class SimpleFormToMailServlet extends JamesServlet {
    static Logger log = Logging.getLoggerInstance(SimpleFormToMailServlet.class);

    protected SendMailInterface sendmail;
    boolean first=true;

    String entries[] = null;

    public void init() {
        sendmail=(SendMailInterface)Module.getModule("sendmail");
        if( sendmail == null ) {
            log.error("SimpleFormToMailServlet - init(): sendmail is null!!!");
        } else {
            log.debug("SimpleFormToMailServlet - init(): successfully initialized.");
        }
    }

    /**
     * reload
     */
    public void reload() {
        sendmail=(SendMailInterface)Module.getModule("sendmail");
        if( sendmail == null ) {
            log.error("SimpleFormToMailServlet - reload(): sendmail is null!!!");
        } else {
            log.debug("SimpleFormToMailServlet - reload(): successfully reloaded.");
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
            log.error("SimpleFormToMailServlet - service(): ERROR: mail from("+from+"), to("+to+"), subject("+subject+"), text("+text+"): not mailed!");
            displayErrorMail(res);
        } else {
            log.debug("SimpleFormToMailServlet - service(): mail from("+from+"), to("+to+"), subject("+subject+"), text("+text+"): mailed!");
            displaySuccess(res);
        }
    }

    /**
     * @rename getEntries
     */
    protected String getentries( HttpPost post ) {
        String result = "";
        Hashtable<String, Object> postparams = post.getPostParameters();
        Enumeration<String> e = postparams.keys();
        String key, value;
        while( e.hasMoreElements() ) {
            key = e.nextElement();

            Vector<Object> v = post.getPostMultiParameter( key );
            Enumeration<Object> e2 = v.elements();

            if( e2.hasMoreElements() ) {
                value = "";
                while( e2.hasMoreElements() ) {
                    value += (String)e2.nextElement();
                    if( e2.hasMoreElements() )
                        value += ",";
                }
            } else
                value = "unknown";

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
        } catch( Exception e ) {
            log.debug("displayResults(): ERROR: " + e );
        }
    }

    private void displaySuccess( HttpServletResponse res ) {
        String titel = "Formulier is verstuurd";
        String body  = "Uw formulier is verstuurd.<BR>\n";
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
        log.debug("SimpleFormToMailServlet - sendmail(): from("+from+"), to("+to+"), subject("+subject+"), text("+text+")");

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
