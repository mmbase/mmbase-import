/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.util.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import org.mmbase.module.core.*;
import org.mmbase.util.AuthorizationException;
import org.mmbase.util.HttpAuth;
import org.mmbase.util.NotLoggedInException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * JamesServlet is a adaptor class.
 * It is used to extend the basic Servlet to provide services not found in suns Servlet API.
 *
 * @dependency this code relies on HttpAuth for its authorization.
 *             This should be done through the MMBase security.
 * @duplicate this code is aimed at the SCAN servlets, and some features (i.e. cookies) do
 *            not communicate well with jsp pages.
 *            Functionality might need to be moved or adapted so that it uses the MMCI.
 *
 * @application SCAN - the cookie code is specific for SCAN
 * @author vpro
 * @version $Id$
 */

public class JamesServlet extends MMBaseServlet {
    private static final Logger log = Logging.getLoggerInstance(JamesServlet.class);
    protected static Logger pageLog;

    /**
     * Initializes the servlet.
     */
    public void init() throws ServletException {
        super.init();
        // Initializing log here because log4j has to be initialized first.
        pageLog = Logging.getLoggerInstance(Logging.PAGE_CATEGORY);
    }

    /**
     * Retrieves a module.
     * Creates the module if it hasn't been created yet (but does not initialize).
     * @todo type returned should be Module
     * @param name the name of the module to retrieve
     * @return the {@link org.mmbase.module.Module}, or <code>null</code> if it doesn't exist.
     */
    protected final Object getModule(String name) {
        return org.mmbase.module.Module.getModule(name);
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
     * @deprecation-used should call Security, not HttpAuth
     * @param req The HttpServletRequest.
     * @param res The HttpServletResponse.
     * @exception AuthorizationException if the authorization fails.
     * @exception NotLoggedInException if the user hasn't logged in yet.
     */
    public String getAuthorization(HttpServletRequest req,HttpServletResponse res) throws AuthorizationException, NotLoggedInException {
        return getAuthorization(req, res, "www", "Basic");
    }

    /**
     * Authenticates a user, If the user cannot be authenticated a login-popup will appear
     * @deprecation-used should call Security, not HttpAuth
     * @param req The HttpServletRequest.
     * @param res The HttpServletResponse.
     * @param server server-account. (for exameple 'film' or 'www')
     * @param level loginlevel. (for example 'Basic' or 'MD5')
     * @exception AuthorizationException if the authorization fails.
     * @exception NotLoggedInException if the user hasn't logged in yet.
     */
    public String getAuthorization(HttpServletRequest req,HttpServletResponse res,String server, String level) throws AuthorizationException, NotLoggedInException {
        return HttpAuth.getAuthorization(req, res, server, level);
    }

    /**
     * This method retrieves the users' MMBase cookie name & value as 'name/value'.
     * When the cookie can't be found, a new cookie will be added.
     * The cookies domain will be implicit or explicit depending on the MMBASEROOT.properties value.
     * @dependency org.mmbase.module.builder.Properties should not depend on this builder ?
     * @param req The HttpServletRequest.
     * @param res The HttpServletResponse.
     * @return A String with the users' MMBase cookie as 'name/value' or null when MMBase core module
     * can't be found, or when the MMBase cookie is located but can't be retrieved from the cookies list.
     */
    public String getCookie(HttpServletRequest req, HttpServletResponse res) {

        final String MMBASE_COOKIENAME = "MMBase_Ident";
        //String FUTUREDATE = "Sunday, 09-Dec-2020 23:59:59 GMT";   // weird date?
        final String PATH = "/";
        String domain = null;
        Cookie[] cookies = req.getCookies(); // Returns 1 or more cookie NAME=VALUE pairs seperated with a ';'.

        if (cookies!= null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(MMBASE_COOKIENAME)) {
                    return MMBASE_COOKIENAME + '/' + cookie.getValue();
                }
            }
        }

        log.debug("No mmbase cookie found");
        // Added address in output to see if multiple cookies are being requested from same computer.
        // This would imply improper use of our service :)
        //
        // added output to see why certain browsers keep asking for new cookie (giving a cookie
        // with no reference in them with 'MMBASE_COOKIENAME'
        // ------------------------------------------------------------------------------------------
        MMBase mmbase = MMBase.getMMBase();
        if (mmbase == null) {
            log.warn("No mmbase found");
            return null;
        }
        String cookieValue = "" + System.currentTimeMillis();
        domain = mmbase.getInitParameter("COOKIEDOMAIN");
        log.debug("Setting MMBase cookie on domain " + domain);
        Cookie cookie = new Cookie(MMBASE_COOKIENAME, cookieValue);
        cookie.setPath(PATH);
        if (domain != null) {
            cookie.setDomain(domain);
        }
        cookie.setMaxAge((int) (20 * 365.25 * 24 * 60 * 60));
        res.addCookie(cookie);
        if (res.isCommitted()) {
            log.error("Could not add cookie " + cookie + " because response is already committed");
        }
        return MMBASE_COOKIENAME + '/' + cookieValue;

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
     * @javadoc
     * @vpro should be made configurable, possibly per servlet.
     *       The best way would likely be a system where a set of names can be configured
     *       (in other words, a hashtable with clientaddress/proxyname value pairs)
     */
    private static  String VPROProxyName    = "vpro6d.vpro.nl";    // name of proxyserver
    /**
     * @javadoc
     * @vpro should be made configurable, possibly per servlet
     */
    private static  String VPROProxyAddress = "145.58.172.6";      // address of proxyserver
    /**
     * @javadoc
     * @vpro should be made configurable, possibly per servlet
     */
    private static  String proxyName    = "zen.vpro.nl";    // name of proxyserver

    /**
     * Extract hostname from request, get address and determine the proxies between it.
     * Needed to determine if user comes from an internal or external host, i.e.
     * when using two streaming servers, one for external users and one for internal users.
     * @javadoc
     * <br />
     * @vpro uses VPROProxyName, VPROProxyAddress. Should be made more generic.
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
            result = proxyName+"->" + result;
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
