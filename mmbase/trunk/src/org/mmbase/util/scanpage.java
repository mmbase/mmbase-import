/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.module.*;
import org.mmbase.util.logging.*;


/**
 * The scanpage is a container class.
 * It holds all information that is needed to identify a user, including
 * servlet objects and MMBase session information.
 * It was introduced to make {@link org.mmbase.servlet.servscan} threadsafe but will probably in the future
 * hold all request related information, because we want to extend the model
 * of offline page generation.
 *
 * @author Daniel Ockeloen
 * @version $Id: scanpage.java,v 1.14 2001-07-02 15:09:52 pierre Exp $
 */
public class scanpage {
    // logger
    private static Logger log = Logging.getLoggerInstance(scanpage.class.getName());

    /**
     * The request object associated with the current page.
     */
    public HttpServletRequest req;
    /**
     * The response object associated with the current page.
     */
    public HttpServletResponse res;
    /**
     * The parameters of this page.
     * These are either set mnaually using {@link #setParamsVector}, or
     * determined from the page using the {@link #req} field
     */
    public Vector params;
    /**
     * The processor set for this page.
     * This values is set and used by scanparser to determine the default
     * processor to call when interpreting LIST tags.
     */
    public ProcessorInterface processor;
    /**
     * Object for accessing values sent by a form using
     * enctype multipart/form-data.
     */
    public HttpPost poster;
    /**
     * The user's MMBase session object, if available.
     */
    public sessionInfo session;
    /**
     * The session name.
     */
    public String sname=null;

    public String name=null;
    public int rstatus=0;
    public String body;
    public String req_line;
    public String wantCache=null;
    public String mimetype=null;
    public String querystring=null;
    public int partlevel=0;
    public String loadmode="cache";
    /**
     * Indicates whether elements sucha s 'multilevel' should be reloaded, or
     * whether results stored in cache should be used.
     */
    public boolean reload=false;

    /**
     * Sets the HttpServletRequest.
     * This method is invoked either by the MMBase servlets, or through the MMCI.
     */
    public void setReq(HttpServletRequest req) {
        this.req=req;
    }

    /**
     * Sets the HttpServletResponse.
     * This method is invoked either by the MMBase servlets, or through the MMCI.
     */
    public void setRes(HttpServletResponse res) {
        this.res=res;
    }

    /**
     * Get the parameter specified.
     * @param num index of the parameter to retrieve
     * @return the parametervalue
     */
    public String getParam(int num) {
        String str;
        if (params==null) {
            params=buildparams();
        }
        try {
            str=(String)params.elementAt(num);
        } catch(IndexOutOfBoundsException e) {
            str=null;
        }
        return str;
    }


    /**
     * Parse the querystring of the current page to retrieve all paarmeters
     * @return a <code>Vector</code> of parameter values
     */
    private Vector buildparams() {
        Vector params=new Vector();
        if (querystring!=null) {
            String paramline=querystring;
            //StringTokenizer tok=new StringTokenizer(querystring,"+\n\r",true);
            int pos=paramline.indexOf("+");
            while(pos!=-1) {
                params.addElement(paramline.substring(0,pos));
                paramline=paramline.substring(pos+1);
                pos=paramline.indexOf("+");
            }
            params.addElement(paramline);
        }
        return params;
    }

    /**
     * Manually set the parameters of a page.
     * @param params a <code>Vector</code> of parameter values
     */
    public boolean setParamsVector(Vector params) {
        this.params=params;
        return true;
    }

    /**
     * Retrieve all parameters of a page.
     * @return a <code>Vector</code> of parameter values
     */
    public Vector getParamsVector() {
        if (params==null) params=buildparams();
        if (params.size()==0) return null;
        return params;
    }


    /**
     * Manually set the parameterline of a page (as if it was a querystring).
     * @param paramline  a string containing teh parametervalues seperated by '+' characters
     */
    public boolean setParamsLine(String paramline) {
        this.params=new Vector();
        //StringTokenizer tok=new StringTokenizer(paramline,"+\n\r");
        // rico
        int pos=paramline.indexOf("+");
        while(pos!=-1) {
            params.addElement(paramline.substring(0,pos));
            paramline=paramline.substring(pos+1);
            pos=paramline.indexOf("+");
        }
        params.addElement(paramline);
        return true;
    }

    /**
     * Retrieve a HTTP request header, if available
     * @param name the name of the header
     * @return the header, <code>nul</code> if it can not be retrieved
     */
    public String getHeader(String name) {
        if (req!=null) {
            return req.getHeader(name);
        } else {
            return null;
        }
    }

    /**
     * Return the session name.
     */
    public String getSessionName() {
        return sname;
    }

    /**
     * Set the session name.
     */
    public void setSessionName(String name) {
        this.sname=name;
    }

//  ------------------------------------------------------------------------------------------------------------

    /**
     * Return page URL and parameters
     */
    public String getUrl() {
        String result = null;
        if( req != null ) {
            result = req.getRequestURI();
            if( req.getQueryString() != null )
                result += "?" + req.getQueryString();
            return result;
        } else {
            return null;
        }
    }

//  ------------------------------------------------------------------------------------------------------------

    private static  boolean     isForVPRO        = true;                // is this class used by vpro or others
    private static  String      VPRODomain       = "145.58";            // well not quite, but does the trick :)
    private static  String      VPROProxyName    = "vpro6d.vpro.nl";    // name of proxyserver
    private static  String      VPROProxyAddress = "145.58.172.6";      // address of proxyserver

    // methods
    // -------

    /**
     *
     * uses      : VPROProxyName, VPROProxyAddress, VPRODomain
     */
    public boolean isInternalVPROAddress() {
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
                if( ip != null && !ip.equals("") && ip.indexOf( VPRODomain ) != -1 ) {
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
    * Extract hostname from scanpage, get address and determine the proxies between it.<br>
    * Needed to determine if user comes from internal or external host, because
    * we use two streaming servers, one for external users and one for internal users.
    * <br>
    * input     : scanpage sp, contains hostname as ipaddress<br>
    * output    : String "clientproxy.clientside.com->dialin07.clientside.com"<br>
    * <br>
    * uses      : VPROProxyName, VPROProxyAddress, VPRODomain
    */
    public String getAddress() {
        String  result      = null;
        boolean fromProxy   = false;
        String  addr        = req.getRemoteHost();

        if( addr != null && !addr.equals("") ) {
            // from proxy ?
            // ------------
            if( addr.indexOf( VPROProxyName ) != -1 || addr.indexOf( VPROProxyAddress ) != -1 ) {
                // get real address
                fromProxy = true;
                addr = req.getHeader("X-Forwarded-For");
                if( addr != null && !addr.equals("") ) {
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
     * @return a strong containing hostnames separated by "->" tokens
     */
    private String getHostNames(String host) {
        String result   = null;
        String hn       = null;
        // comes client from his own proxy?
        // --------------------------------
        if( host.indexOf(",") != -1 ) {
            int pos;
            // filter and display the clientproxies
            // ------------------------------------
            while( (pos = host.indexOf(",")) != -1 ) {
                hn = host.substring( 0, pos );
                host = host.substring( pos + 2 );
                if( result == null )
                    result  = getHostName( hn );
                else
                    result += "->" + getHostName( hn );
            }
            // which results in "proxy.clientside.com->dailin07.clientside.com"
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
        String hn = hostname;
        if( hn != null && !hn.equals("")) {
            try {
                hn = InetAddress.getByName( hostname ).getHostName();
            } catch( UnknownHostException e) {
            }
        }
        return hn;
    }

    /**
     * Gets the referrer from the request header.
     * @return a <code>String</code> with the referer,
     *         <code>null</code> when reqheader is <code>null</code>.
     */
    public String getReferer() {
        if (req==null) {
            log.error("scanpage:getReferer: req="+req+", can't get referer.");
            return null;
        } else {
            return req.getHeader("Referer");
        }
    }

}
