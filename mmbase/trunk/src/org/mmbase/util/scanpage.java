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

/**
 * scanpage is a container class it holds all objects needed per scan request
 * it was introduced to make servscan threadsafe but will probably in the future
 * hold all request related info instead of HttpServletRequest and HttpServletResponse
 * because we want extend the model of offline page generation.
 *
 * @author Daniel Ockeloen
 * @version $Id: scanpage.java,v 1.7 2000-03-30 13:11:57 wwwtech Exp $
 */
public class scanpage {
	public ProcessorInterface processor;
	public int rstatus=0;
	public HttpServletRequest req;
	public Vector params;
	public HttpPost poster;
	public String name=null;
	public sessionInfo session;
	public String body;
	public String req_line;
	public String wantCache=null;
	public String mimetype=null;
	public String querystring=null;
	public String sname=null;
	public int partlevel=0;

	public void setReq(HttpServletRequest req) {
		this.req=req;
	}

	/**
	 * Get the parameter specified.
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
		return(str);
	}


	// Support for params
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
		return(params);
	}

	public boolean setParamsVector(Vector params) {
		this.params=params;
		return(true);
	}

	public Vector getParamsVector() {
		if (params==null) params=buildparams();

		if (params.size()==0) return(null);

		return(params);
	}


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
		return(true);
	}

	public String getHeader(String name) {
		if (req!=null) {
			return(req.getHeader(name));
		} else {
			return(null);
		}
	}

	public String getSessionName() {
		return(sname);
	}

//  ------------------------------------------------------------------------------------------------------------

	/**
	* return page URL and parameters
	*/
	public String getUrl()
	{
		if( req != null )
			return req.getRequestURI()+"?"+req.getQueryString();
		else
			return null;
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
    public boolean isInternalVPROAddress()
    {
        boolean intern  = false;

           String  ip      = req.getRemoteAddr();

            // computers within vpro domain, use *.vpro.nl as server, instead *.omroep.nl
            // --------------------------------------------------------------------------

            if( ip != null && !ip.equals(""))
            {
                // is address from proxy?
                // ----------------------
                if( ip.indexOf( VPROProxyName )!= -1  || ip.indexOf( VPROProxyAddress )!= -1 )
                {
                    // positive on proxy, get real ip
                    // ------------------------------
                    ip = req.getHeader("X-Forwarded-For");

                    // come from internal host?
                    // ------------------------
                    if( ip != null && !ip.equals("") && ip.indexOf( VPRODomain ) != -1 )
                        intern = true;
                }
                else
                    // no proxy, this is the real thing
                    // --------------------------------
                    if( ip.indexOf("145.58") != -1 )
                        intern = true;
            }

        return intern;
    }

    /**
    * Extract hostname from scanpage, get address and determine the proxies between it.
    * Needed to determine if user comes from internal or external host, because
    * we use two streaming servers, one for external users and one for internal users.
    *
    * input     : scanpage sp, contains hostname as ipaddress
    * output    : String "clientproxy.clientside.com->dialin07.clientside.com"
    *
    * uses      : VPROProxyName, VPROProxyAddress, VPRODomain
    */
    public String getAddress()
    {
        String  result      = null;
        boolean fromProxy   = false;
        String  addr        = req.getRemoteHost();

        if( addr != null && !addr.equals("") )
        {
                // from proxy ?
                // ------------

                if( addr.indexOf( VPROProxyName ) != -1 || addr.indexOf( VPROProxyAddress ) != -1 )
                {
                    // get real address
                    // ----------------

                    fromProxy = true;
                    addr = req.getHeader("X-Forwarded-For");
                    if( addr != null && !addr.equals("") )
                        result = addr;
                }
                else
                    result = addr;
            }

            result = getHostNames( addr );
            if( fromProxy )
                result = "zen.vpro.nl->" + result;

        return result;
    }

    /**
    *
    */
    private String getHostNames( String host )
    {
        String result   = null;
        String hn       = null;

        // comes client from his own proxy?
        // --------------------------------
        if( host.indexOf(",") != -1 )
        {
            int pos;

            // filter and display the clientproxies
            // ------------------------------------
            while( (pos = host.indexOf(",")) != -1 )
            {
                hn = host.substring( 0, pos );
                host = host.substring( pos + 2 );
                if( result == null )
                    result  = getHostName( hn );
                else
                    result += "->" + getHostName( hn );
            }
            // which results in "proxy.clientside.com->dailin07.clientside.com"
            // ----------------------------------------------------------------
        }
        else
            result = getHostName( host );

        return result;
    }

    /**
    *
    */
    private String getHostName( String hostname )
    {
        // if hostname == ipaddress, try to get a hostname for it
        // ------------------------------------------------------

        String hn = null;
        if( hostname != null && !hostname.equals(""))
        {
            try
            {
                hn = InetAddress.getByName( hostname ).getHostName();
            }
            catch( UnknownHostException e )
            {
                hn = hostname;
            }
        }
        else
            hn = hostname;
        return hn;
    }
}
