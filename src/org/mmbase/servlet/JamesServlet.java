/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

// import the needed packages
import java.io.*;
import java.util.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.Properties;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
* JamesServlet is a addaptor class its used to extend the basic Servlet
* to with the calls that where/are needed for 'James' servlets to provide
* services not found in suns Servlet API.
* @version $Id: JamesServlet.java,v 1.25 2001-05-20 08:23:58 michiel Exp $
*/


public class JamesServlet extends HttpServlet {

    /**
     * File to which to send logging output.
     * Needed for the most simple logging system.
     */
    static String outputfile=null;

    /**
     * Logging
     */
    static Logger log;

    private static int servletCount;
    private static Object servletCountLock = new Object();
    private static Hashtable runningServlets = new Hashtable();
    private static int printCount;

    /**
     * Debug method for logging. obsolete
     */
    protected void debug( String msg ) { log.debug(msg + " <deprecated call>"); }

    // Initializing the servlet.
    // This starts and configures the logging system.
    static {

        /*
        String dtmp=System.getProperty("mmbase.mode");
        if (dtmp!=null && dtmp.equals("demo")) {
            String curdir=System.getProperty("user.dir");
            outputfile=curdir+"/log/mmbase.log";
        } else {
            outputfile = System.getProperty("mmbase.outputfile");
        }
        */

        // Remaining output and error can still be redirected.
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


        /* Michiel:
           This doesn't seem to be such a bad place to initialise our logging stuff.
        */

        System.out.println("MMBase starts now");
        Logging.configure(System.getProperty("mmbase.config") + File.separator + "log" + File.separator + "log.xml");
        log = Logging.getLoggerInstance(JamesServlet.class.getName());
        System.out.println("Logging starts now");
        log.info("\n====================\nStarting MMBase\n====================");

    }

    /**
     * Retrieves a module.
     * Creates the module if it hasn't been created yet (but does not initialize).
     * @param name the name of the module to retrieve
     * @return the {@link Module}, or <code>null</code> if it doesn't exist.
     */
    protected final Object getModule(String name) {
        return Module.getModule(name);
    }

    /**
     * Retrieves an initialization parameter.
     * Note: overides the normal way to set init params in javax.
     */
    public String getInitParameter(String var) {
        return null;
    }

    /**
     * Retrieves all initialization parameters.
     * Note: overides the normal way to set init params in javax.
     */
    protected final Hashtable getInitParameters() {
        return null;
    }

    /**
     * Gets properties. If allowed.
     */
    protected final Hashtable getProperties(String name) {
        return null;
     }

    /**
     * Gets a property out of the Environment. If allowed
     */
    protected final String getProperty(String name, String var) {
        return null;
    }

    /**
     * Try to get the default authorisation
     * @param req The HttpServletRequest.
     * @param res The HttpServletResponse.
     * @exception AuthorizationException if the authorization fails.
     * @exception NotLoggedInException if the user hasn't logged in yet.
     */
    public String getAuthorization(HttpServletRequest req, HttpServletResponse res) throws Exception {
        return HttpAuth.getAuthorization(req,res,"www","Basic");
    }
// better:
//    public String getAuthorization(HttpServletRequest req,HttpServletResponse res) throws AuthorizationException, NotLoggedInException {
//            return getAuthorization(req,res);
//    }

    /**
     * Authenticates a user, If the user cannot be authenticated a login-popup will appear
     * @param req The HttpServletRequest.
     * @param res The HttpServletResponse.
     * @param server server-account. (for exameple 'film' or 'www')
     * @param level loginlevel. (for example 'Basic' or 'MD5')
     * @exception AuthorizationException if the authorization fails.
     * @exception NotLoggedInException if the user hasn't logged in yet.
     */
    public String getAuthorization(HttpServletRequest req,HttpServletResponse res,String server, String level) throws AuthorizationException, NotLoggedInException {
            return HttpAuth.getAuthorization(req,res,server,level);
    }

    /**
     * This method retrieves the users' MMBase cookie name & value as 'name/value'.
     * When the cookie can't be found, a new cookie will be added.
     * When an old James cookie is found, the related MMBaseProperty 'value' field will be replaced
     * with a new MMBase cookie name & value. The cookies domain will be implicit or explicit
     * depending on the MMBASEROOT.properties value.
     * @param req The HttpServletRequest.
     * @param res The HttpServletResponse.
     * @return A String with the users' MMBase cookie as 'name/value' or null when MMBase core module
     * can't be found, or when the MMBase cookie is located but can't be retrieved from the cookies list.
     */
    public String getCookie(HttpServletRequest req, HttpServletResponse res) {

        String methodName = "getCookie()";
        String HEADERNAME = "COOKIE";
        String JAMES_COOKIENAME = "James_Ident";
        String MMBASE_COOKIENAME = "MMBase_Ident";
        String FUTUREDATE = "Sunday, 09-Dec-2020 23:59:59 GMT";
        String PATH = "/";
        String domain = null;

        // somehow client has a cookie but does not return it in getHeader(COOKIE)
        // this will explain why :)

/*
        if( debug ) {
            debug("getCookie(): header of client:");
            debug("getCookie(): -----------------");

            Enumeration e     = req.getHeaderNames();
            String        key    = null;

            while( e.hasMoreElements() ) {
                key = (String)e.nextElement();
                debug("getCookie(): "+key+"("+req.getHeader(key)+")");
            }
            debug("getCookie(): -----------------");
            debug("getCookie():");

            Cookie[] c = req.getCookies();
            int      i = 0;
            if( c != null ) {
                i = c.length;

                debug("getCookie():Cookies["+i+"]:");
                debug("getCookie():-----------");

                Cookie cookie = null;
                for( int j = 0; j<i; j++ ) {
                    cookie = c[j];
                    if( cookie != null ) {
                        debug("getCookie(): cookie["+j+"]: comment("+cookie.getComment()+") domain("+cookie.getDomain()+") maxage("+cookie.getMaxAge()+") name("+cookie.getName()+") path("+cookie.getPath()+") secure("+cookie.getSecure()+") value("+cookie.getValue()+") version("+cookie.getVersion()+")");

                    } else {
                        debug("getCookie(): cookie["+j+"]: "+cookie);
                    }
                }
            } else {
                debug("getCookie(): no cookies found in header!");
            }
            debug("getCookie():");
            debug("getCookie():--------");
        }
*/
        String cookies = req.getHeader(HEADERNAME); // Returns 1 or more cookie NAME=VALUE pairs seperated with a ';'.

        if ((cookies!= null) && (cookies.indexOf(MMBASE_COOKIENAME) != -1)) {
            // debug(methodName+": User has a "+MMBASE_COOKIENAME+" cookie, returning it now.");
            StringTokenizer st = new StringTokenizer(cookies, ";");
            while (st.hasMoreTokens()) {
                String cookie = st.nextToken().trim();
                if (cookie.startsWith(MMBASE_COOKIENAME)) { // Return the first cookie with a MMBASE_COOKIENAME.
                    return cookie.replace('=','/');
                }
            }
            log.debug("JamesServlet:"+methodName+": ERROR: Can't retrieve "+MMBASE_COOKIENAME+" from "+cookies);
            return null;
        } else {

            // Added address in output to see if multiple cookies are being requested from same computer.
            // This would imply improper use of our service :)
            //
            // added output to see why certain browsers keep asking for new cookie (giving a cookie
            // with no reference in them with 'MMBASE_COOKIENAME'
            // ------------------------------------------------------------------------------------------

            // debug(methodName+": address("+getAddress(req)+"), oldcookie("+cookies+"), this user has no "+MMBASE_COOKIENAME+" cookie yet, adding now.");
            MMBase mmbase = (MMBase)Module.getModule("MMBASEROOT");
            if (mmbase == null) {
                log.debug("JamesServlet:"+methodName+": ERROR: mmbase="+mmbase+" can't create cookie.");
                return null;
            }

            String mmbaseCookie = MMBASE_COOKIENAME+"="+System.currentTimeMillis();
            domain = mmbase.getCookieDomain();
            if (domain == null) {
                // debug(methodName+": Using implicit domain.");
                res.setHeader("Set-Cookie", (mmbaseCookie+"; path="+PATH+"; expires="+FUTUREDATE));
            } else {
                // debug(methodName+": Using explicit domain: "+domain);
                res.setHeader("Set-Cookie", (mmbaseCookie+"; path="+PATH+"; domain="+domain+"; expires="+FUTUREDATE));
            }

            if ((cookies!= null) && (cookies.indexOf(JAMES_COOKIENAME) != -1)) {

                // Change all old JAMES cookie entries in the properties table to MMBASE cookie values.
                // eg. key:'SID' value: 'James_Ident/936797541271' gets value: 'Mmbase_Ident/curtimemillis#'

                Properties propBuilder = null;
                propBuilder = (Properties) mmbase.getMMObject("properties");
                if (propBuilder==null) {
                    log.debug("JamesServlet:"+methodName+": ERROR: Properties builder ="+propBuilder+", can't change old "+JAMES_COOKIENAME+" property if it was necessary, (maybe Properties builder is not activated in mmbase?");
                }
                StringTokenizer st = new StringTokenizer(cookies, ";");
                while (st.hasMoreTokens()) {
                    String cookie = st.nextToken().trim();
                    if (cookie.startsWith(JAMES_COOKIENAME)) {

                        // Change the value field of the related property to the mmbaseIdent value.
                        log.debug(methodName+": Changing property with value:"+cookie+" to: "+mmbaseCookie);
                        Enumeration e = propBuilder.search("WHERE key='SID' AND value='"+cookie.replace('=','/')+"'");
                        if (e.hasMoreElements()) {
                            MMObjectNode propNode = (MMObjectNode)e.nextElement();
                            propNode.setValue("value", mmbaseCookie.replace('=','/'));
                            propNode.commit();
                        }

                        /* Skipping expiry of old cookie for now.
                        DateFormat formatter=new SimpleDateFormat("EEEE, dd-MMM-yyyy HH:mm:ss 'GMT'", Locale.US);
                        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                        Date d = new Date(System.currentTimeMillis()+24*3600*1000);
                        String expires = formatter.format(d);
                        debug("newGC: Found 'old' cookie: "+cookie+" setting new expiry to: "+expires);
                        res.setHeader("Set-Cookie", (cookie+"; path="+PATH+"; expires="+expires));
                        */
                    }
                }
            } else {
                // debug(methodName+": User has no "+JAMES_COOKIENAME+" also, *new user*");
            }
            return mmbaseCookie.replace('=','/');
        }
    }

    /**
     * Get the parameter specified.
     * @param req The HttpServletRequest.
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
        return str;
    }

    /**
     * Extracts the request line parameters.
     * SCAN-format
     * @param req The HttpServletRequest.
     * @return a <code>Vector</code> with the request line parameters
     */
    private Vector buildparams(HttpServletRequest req) {
        Vector params=new Vector();
        if (req.getQueryString()!=null) {
            StringTokenizer tok=new StringTokenizer(req.getQueryString(),"+\n\r");
            // rico
            while(tok.hasMoreTokens()) {
                params.addElement(tok.nextToken());
            }
        }
        return params;
    }

    /**
     * Get the Vector containing all parameters
     * @param req The HttpServletRequest.
     */
    public Vector getParamVector(HttpServletRequest req) {
        Vector params=buildparams(req);
        return params;
    }

    /**
     * Return URI with QueryString appended
     * @param req The HttpServletRequest.
     */
    public static String getRequestURL(HttpServletRequest req)
    {
        String result = req.getRequestURI();
        String queryString = req.getQueryString();
        if (queryString!=null) result += "?" + queryString;
        return result;
    }

    /**
     * Decrease the reference count of the servlet (for debugging)
     * @param req The HttpServletRequest.
     */
    public void decRefCount(HttpServletRequest req) {
        String URL = getRequestURL(req);
        URL += " " + req.getMethod();
        synchronized (servletCountLock) {
            servletCount--;
            DebugServlet s = (DebugServlet) runningServlets.get(this);
            if (s!=null) {
                if (s.refCount==0) runningServlets.remove(this);
                else {
                    s.refCount--;
                    int i = s.URIs.indexOf(URL);
                    if (i>=0) s.URIs.removeElementAt(i);
                }
            }
        }//sync
    }

    /**
     * Increase the reference count of the servlet (for debugging)
     * @param req The HttpServletRequest.
     */
    public void incRefCount(HttpServletRequest req) {
        String URL = getRequestURL(req);
        URL += " " + req.getMethod();
        int curCount;
        synchronized (servletCountLock)    {
            servletCount++; curCount=servletCount; printCount++;
            DebugServlet s = (DebugServlet) runningServlets.get(this);
            if (s==null) runningServlets.put(this, new DebugServlet(this, URL, 0));
            else { s.refCount++; s.URIs.addElement(URL); }
        }// sync
        
        if ((printCount & 31)==0) {
			if (curCount>0) {
	            log.info("Running servlets: "+curCount);
	            for(Enumeration e=runningServlets.elements(); e.hasMoreElements();) {
	                log.info(e.nextElement());
				}
			}
        }
       
    }

    /**
     * Initializes the servlet.
     * @param config the servlet configuration
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        init();
    }

    /**
     * Initializes the servlet.
     */
    public void init() {
        log.debug("init van JamesServlet");
        ServletConfig sc=getServletConfig();
        ServletContext sx=sc.getServletContext();
        MMBaseContext.setServletContext(sx);
    }

    /**
     * Notifies through logging that the servlet was removed.
     */
    protected void finalize() {
        System.out.println("end"); // obsolete call
        log.info("end of MMBase \n\n");
    }

//  ------------------------------------------------------------------------------------------------------------
// these vars should be renamed!

    private static  boolean     isForVPRO        = true;                // is this class used by vpro or others
    private static  String      VPRODomain       = "145.58";            // well not quite, but does the trick :)
    private static  String      VPROProxyName    = "vpro6d.vpro.nl";    // name of proxyserver
    private static  String      VPROProxyAddress = "145.58.172.6";      // address of proxyserver

    /**
     * Determines if a user comes from an internal or external host, i.e.
     * when using two streaming servers, one for external users and one for internal users.
     * <br>
     * XXX: uses VPROProxyName, VPROProxyAddress, VPRODomain. Should be made more generic
     * by using propertie values in mmbaseroot (?).
     * The name of the method should be changed (i.e to isInternalAddres)
     * @param req The HTTP request, which contains hostname as ipaddress
     * @return <code>true</code> if the user is internal, <code>false</code> if external.
     */
    public boolean isInternalVPROAddress(HttpServletRequest req) {
        boolean intern  = false;
        String  ip      = req.getRemoteAddr();
        // computers within vpro domain, use *.vpro.nl as server, instead *.omroep.nl
        // --------------------------------------------------------------------------
        if( ip != null && !ip.equals("")) {
            // is address from proxy?
            // ----------------------
            if( ip.indexOf( VPROProxyName )!= -1  || ip.indexOf( VPROProxyAddress )!= -1 ) {
                // positive on proxy, get real ip
                // ------------------------------
                ip = req.getHeader("X-Forwarded-For");

                // come from internal host?
                // ------------------------
                if( ip != null && !ip.equals("") && ip.indexOf( VPRODomain ) != -1) {
                    intern = true;
                }
            } else {
                // no proxy, this is the real thing
                // --------------------------------
                if( ip.indexOf("145.58") != -1 ) {
                    intern = true;
                }
            }
        }
        return intern;
    }

    /**
     * Extract hostname from request, get address and determine the proxies between it.
     * Needed to determine if user a comes from an internal or external host, i.e.
     * when using two streaming servers, one for external users and one for internal users.
     * <br>
     * XXX: uses VPROProxyName, VPROProxyAddress, VPRODomain. Should be made more generic.
     * @param req The HTTP request, which contains hostname as ipaddress
     * @return a string containing the proxy chain. in the format
     *         "clientproxy.clientside.com->dialin07.clientside.com"
     */
    public String getAddress(HttpServletRequest req) {
        String  result      = null;
        boolean fromProxy   = false;
        String  addr        = req.getRemoteHost();

        if( addr != null && !addr.equals("") ) {
            // from proxy ?
            // ------------
            if( addr.indexOf( VPROProxyName ) != -1 || addr.indexOf( VPROProxyAddress ) != -1 ) {
                // get real address
                // ----------------
                fromProxy = true;
                addr = req.getHeader("X-Forwarded-For");
                if(addr != null && !addr.equals("")) {
                    result = addr;
                }
            } else {
                result = addr;
            }
        }
        result = getHostNames( addr );
        if( fromProxy ) {
            result = "zen.vpro.nl->" + result;
        }
        return result;
    }

    /**
     * Determine the host names of a String containing a comma-separated list of
     * ip addresses and/or host names.
     * Used to retrieve a list of proxies.
     * @param host an comma-seperated String containing ip-addressed and/or host names
     * @return a string containing the proxy chain. in the format
     *         "clientproxy.clientside.com->dialin07.clientside.com"
     */
    private String getHostNames( String host ) {
        String result   = null;
        String hn       = null;

        // comes client from his own proxy?
        // --------------------------------
        if( host.indexOf(",") != -1 ) {
            int pos;
            // filter and display the clientproxies
            // ------------------------------------
            while( (pos = host.indexOf(",")) != -1) {
                hn = host.substring( 0, pos );
                host = host.substring( pos + 2 );
                if( result == null ) {
                    result  = getHostName( hn );
                } else {
                    result += "->" + getHostName( hn );
                }
            }
            // which results in "proxy.clientside.com->dailin07.clientside.com"
            // ----------------------------------------------------------------
        } else {
            result = getHostName( host );
        }
        return result;
    }

    /**
     * Determine the host name of a passed ip address or host name.
     * If the passed parameter is an IP-address, the hostname for
     * that address is retrieved if possible.
     * @param hostname an ip-address or host name
     * @return the resulting hostname
     */
    private String getHostName( String hostname ) {
        // if hostname == ipaddress, try to get a hostname for it
        // ------------------------------------------------------

        String hn = null;
        if( hostname != null && !hostname.equals("")) {
            try {
                hn = InetAddress.getByName( hostname ).getHostName();
            } catch( UnknownHostException e ) {
                hn = hostname;
            }
        } else {
            hn = hostname;
        }
        return hn;
    }
}

/**
 * This class maintains current state information for a running servlet.
 * It contains a reference count, as well as a list of URI's being handled by the servlet.
 */
class DebugServlet {

    // logger
    static Logger log = Logging.getLoggerInstance(DebugServlet.class.getName());
    // XXX: can be made static. use in logger call...
    public String classname = getClass().getName();

    /**
     * The servlet do debug
     */
    JamesServlet servlet;
    /**
     * List of URIs that call the servlet
     */
    Vector URIs = new Vector();
    /**
     * Nr. of references
     */
    int refCount;

    /**
     * Craete a new DebugServlet using teh jamesServlet
     */
    DebugServlet( JamesServlet servlet, String URI, int refCount) {
        this.servlet = servlet;
        URIs.addElement(URI);
        this.refCount = refCount;
    }

    /**
     * Returns a sdescripto containing servlet info and URI's
     */
    public String toString() {
        return classname +" servlet("+servlet+"), refcount("+(refCount+1)+"), uri's("+URIs+")";
    }
}
