/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.io.*;

import org.mmbase.module.builders.*;
import org.mmbase.module.core.*;
import org.mmbase.module.Module;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.tomcat.core.*;
import org.mmbase.util.logging.*;

/**
 * jumpersInterceptor is a Tomcat Interceptor based on the servjumpers-servlet.
 * It intercepts all requests to see if it has a jumper it wants to redirect
 * (like www.vpro.nl/3voor12). It's controlled by the jumpers builder.
 * @version 1.0
 * @author Gerard van Enk

 */

public class jumpersInterceptor extends BaseInterceptor { 

    private static Logger log = Logging.getLoggerInstance(jumpersInterceptor.class.getName());
    static MMBase mmbase = null;
    
    ContextManager cm; 
    protected String methods[]=new String[0];

               
    public jumpersInterceptor() {
    }
 
    public void setContextManager( ContextManager cm ) {
        this.cm=cm;
    }
   
	/*
	* get the needed module so we can return it
	*/
	protected final Object getModule(String name) {
		return(Module.getModule(name));
	}


    /* 
     * Doesn't work with Tomcat 3.2, but keep it here for backwards comp.
     */
    public int preService(Request req, Response res) {
        if (mmbase==null) mmbase=(MMBase)getModule("MMBASEROOT");
        try {
            String url=null;
            String tmpr=req.getRequestURI().substring(1);
            if (tmpr.indexOf('.')==-1 && (!tmpr.endsWith("/"))) url=getUrl(tmpr);
            if (url!=null) {
                res.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                res.setContentType("text/html");
                res.setHeader("Location",url);
                return OK;
            }
	    } finally { 
	    }
	    return OK;
    }


    private String getUrl(String key) {
        String url=null;
		Jumpers bul=(Jumpers)mmbase.getMMObject("jumpers");
		if (bul!=null) {
			if (key.endsWith("/")) {
                url=bul.getJump(key.substring(0,key.length()-1));
            } else {
                url=bul.getJump(key);
            }
			if (url!=null) return(url);
		} else {
			log.error("servjumpers -> can't access nodeManager jumpers (check jumpers.xml)");
		}
		return(null);
	}


    public int requestMap(Request request ) {
		return 0;
    }

    public int contextMap( Request req ) {
        return 0;
    }

    public int authenticate(Request request, Response response) {
		return 0;
    }

    public int authorize(Request request, Response response) {
		return 0;
    }

    public int authorize(Request request, Response response, String reqRoles[]) {
		return 0;
	}
    
	public int newSessionRequest( Request request, Response response) {
		return 0;
	}

    public int beforeBody( Request req, Response res ) {
        if (mmbase==null) mmbase=(MMBase)getModule("MMBASEROOT");
        try {
            String url=null;
            String tmpr=req.getRequestURI().substring(1);
            if (tmpr.indexOf('.')==-1 && (!tmpr.endsWith("/"))) url=getUrl(tmpr);
            if (url!=null) {
                res.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                res.setContentType("text/html");
                res.setHeader("Location",url);
                return OK;
            }
	    } finally {
	    }
	    return OK;
    }

    public int beforeCommit( Request request, Response response) {
		return 0;
    }


    public int afterBody( Request request, Response response) {
		return 0;
    }

    public int postService(Request request, Response response) {
		return 0;
    }

    public String []getMethods()  {
		return methods;
    }
}
