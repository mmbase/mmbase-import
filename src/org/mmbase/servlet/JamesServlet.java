/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.servlet;

// import the needed packages
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;


/**
* JamesServlet is a addaptor class its used to extend the basic Servlet
* to with the calls that where/are needed for 'James' servlets to provide
* services not found in suns Servlet API.
*/
public class JamesServlet extends HttpServlet {

	// org.mmbase

	static String outputfile=null;

	static {
		outputfile = System.getProperty("mmbase.outputfile");       
		if (outputfile != null) {
			try {
				PrintStream mystream=new PrintStream(new FileOutputStream(outputfile,true));
				System.setOut(mystream);
				System.setErr(mystream);
				System.err.println("Setting mmbase.outputfile to "+outputfile);
			} catch (IOException e) {
				System.err.println("Oops, failed to set mmbase.outputfile '"+outputfile+"'");
				e.printStackTrace();
			}
		} else {
			System.err.println("mmbase.outputfile = null, no redirection of System.out to file");
		}
	}

	/*
	* get the needed module so we can return it
	*/
	protected final Object getModule(String name) {
		return(Module.getModule(name));
	}	


	/**
	 *
	 */ 
	// warning warning overides a normal way to set init params in javax !!!
	public String getInitParameter(String var) {
		return(null);
	}


	/**
	 *
	 */ 
	protected final Hashtable getInitParameters() {
		return(null);
	}



	/**
	 * Gets properties. If allowed
	 */
	protected final Hashtable getProperties(String name) {
		return(null);
 	}	

	/**
	 * Gets a property out of the Environment. If allowed
	 */
	protected final String getProperty(String name, String var) {
		return(null);
	}


	/**
	* Try to get the default authorisation 
	* @exception Exception throws an AuthorisationFailedException if
	* the password of the user isn't correct, or throws a NotLoggedInException
	* if the user isn't logged in.
	*/
	public String getAuthorization(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return(HttpAuth.getAuthorization(req,res,"www","Basic"));
	}




	/** 
	 * Authenticates a user, If the user cannot be authenticated a login-popup will appear
	 * @param server server-account. (for exameple 'film' or 'www')
	 * @param level loginlevel. (for example 'Basic' or 'MD5')
     * @exception AuthorizationException if the authorization fails. 
     * @exception NotLoggedInException if the user hasn't logged in yet. 
	 */
	public String getAuthorization(HttpServletRequest req,HttpServletResponse res,String server, String level) throws AuthorizationException, NotLoggedInException {
			return(HttpAuth.getAuthorization(req,res,server,level));
	} 


    public String getCookie(HttpServletRequest req, HttpServletResponse res)
    {
		String name=null;

        String string1 = req.getHeader("Cookie");
        if (string1 == null || string1.indexOf("James_Ident=") == -1)
        {
            string1 = "James_Ident=" + System.currentTimeMillis();
	            //res.setHeader("Set-Cookie", string1 + "; path=/; expires=Sunday, 09-Dec-99 23:59:59 GMT");
	            res.setHeader("Set-Cookie", string1 + "; path=/; expires=Sunday, 09-Dec-2020 23:59:59 GMT");
        	return (string1.replace('=','/'));
            // bug fix, daniel 23 Okt return string1;
        }
        int i = string1.indexOf("James_Ident=");
        String string2 = string1.substring(i);
		//System.out.println(string2);
        if (string2.indexOf(59) != -1)
            string2 = string2.substring(0, string2.indexOf(59));

        //fix to return just one cookiie line return string1;
//		System.out.println("Cookie="+string2.replace('=','/'));
        return (string2.replace('=','/'));
    }


	/**
	 * Get the parameter specified.
	 */
	public String getParam(HttpServletRequest req,int num) {
		String str;

		// needs a new caching way
		Vector params=buildparams(req);
		try {
			str=(String)params.elementAt(num);
		} catch(IndexOutOfBoundsException e) {
			str=null;
		}
		return(str);
	}


	// Support for params
	private Vector buildparams(HttpServletRequest req) {
		Vector params=new Vector();
		if (req.getQueryString()!=null) {
			StringTokenizer tok=new StringTokenizer(req.getQueryString(),"+\n\r");
			// rico 
			while(tok.hasMoreTokens()) {
				params.addElement(tok.nextToken());
			}
		}
		return(params);
	}

	/**
	 * Get the Vector containing all parameters
	 */
	public Vector getParamVector(HttpServletRequest req) {
		Vector params=buildparams(req);
		return(params);
	}


    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	init();
    }

    public void init() {
	ServletConfig sc=getServletConfig();
        ServletContext sx=sc.getServletContext();
        MMBaseContext.setServletContext(sx);
    }
}
