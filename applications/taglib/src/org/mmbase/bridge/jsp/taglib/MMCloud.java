/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import sun.misc.BASE64Decoder;

import org.mmbase.bridge.*;

/**
 * MMCloud
 * Creates a cloud object (pulling it from if session necessary)
 *
 * @author Pierre van Rooden
 *
 **/
public class MMCloud extends MMTaglib implements BodyTag{

    private String cloudName=DEFAULT_CLOUD_NAME;

    public static boolean debug = true;
    private String authenticate = "PropertiesLogon";

    private String method = null; // how to log on.
    private String logon = null;  
    private String pwd = null;
    private String cache = null;
    private String expire = "";
    String request_line = null;
    private HttpSession session;
    private HttpServletRequest  request;
    private HttpServletResponse response;

    
    //simple method to dump debug data into System.err
    public void debug(String debugdata){
	System.err.println("MMCloud:" + debugdata);
    }

    /**
     * implementation of TagExtraInfo return values declared here
     * should be filled at one point, currently fillVars is responsible for 
     * that ant gets called before every 
     **/
    public VariableInfo[] getVariableInfo(TagData data){
        VariableInfo[] variableInfo =    null;
        if (data.getAttribute("name")!= null){
            cloudName= "" + data.getAttribute("name");
        } else {
            cloudName=DEFAULT_CLOUD_NAME;
        }
	    variableInfo =    new VariableInfo[1];
	    variableInfo[0] =  new VariableInfo("cloud","org.mmbase.bridge.Cloud",true,VariableInfo.NESTED);
    	return variableInfo;
    }
    
    
    public void setName(String name){
        cloudName = name;
    }

    public void setLogon(String logon){
        this.logon = logon;
    }

    public void setPwd(String pwd){
        this.pwd = pwd;
    }

    public void setExpire(String expire){
        this.expire = expire;
    }

    public void setAuthenticate(String authenticate){
        this.authenticate = authenticate;
    }
    public void setMethod(String m){
        this.method = m;
    }

    public void setCache(String cache){
        this.cache = cache;
    }


    /**
     * Deny access to this page
     */    
    private boolean deny(HttpServletResponse res) {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        debug("Not logged on.");
        res.setHeader("WWW-Authenticate", "Basic realm=\"www\"");
        return false; 
    }


    /**
     *  Check name and retrieve cloud
     **/
    public int doStartTag() throws JspException{
        session  = (HttpSession)pageContext.getSession();
        request  = (HttpServletRequest)pageContext.getRequest();
        response = (HttpServletResponse)pageContext.getResponse();
        Cloud cloud = (Cloud)session.getAttribute("cloud_"+cloudName);

        if (cloud != null && (logon != null || method != null)) { // check if logged on as the right one

            if (method != null) { // we need to know if cloud was logged on at all
                
            }

            if (! cloud.getUser().getIdentifier().equals(logon)) { // no!
                debug("logged on, but as wrong user, logging out first.");
                cloud = null;
                session.setAttribute("cloud_" + cloudName, null);
            }
        }

        if (cloud == null) {
            debug("no cloud found. Loggin in...");
            // check how to log on:
            if ("http".equals(method)) {
                debug("with http");
                // find logon, password with http authentication       
                String username = null;
                String password = null;
                try {
                    BASE64Decoder dec    = new BASE64Decoder();
                    String mime_line     = request.getHeader("Authorization"); 
                    if (mime_line != null) {
                        String user_password = new String(dec.decodeBuffer(mime_line.substring(6))); 
                        StringTokenizer t    = new StringTokenizer(user_password, ":"); 
                        if (t.countTokens() == 2) {
                            username = t.nextToken(); 
                            password = t.nextToken(); 
                        }
                    }		
                } catch (Exception e) {			
                    debug("oooops" + e);
                }
                // Authenticate user 
                debug("u " + username + " p " + password);
                if (logon != null) { // if there was a username specified as well, it must be the same
                    debug("http with username");
                    if (! logon.equals(username)) {
                        debug("username not correct");
                        logon = null;
                        deny(response);
                    }
                } else {
                    debug("http without username");
                    logon = username;
                }
                pwd = password;
            } 
            
            if (logon != null) {
                debug("Username found. logging in");
                User user = getDefaultCloudContext().getNewUser();
                user.put("username", logon);
                user.put("password", pwd);
                cloud = getDefaultCloudContext().getCloud(cloudName, "name/password", user);
            } else { 
                cloud = getDefaultCloudContext().getCloud(cloudName);
            }
            if (cloud == null) { // stil null, give it up then...
                debug ("Could not create Cloud, denying access");
                deny(response);
                return SKIP_BODY;
    	    } else {
    	        session.setAttribute("cloud_"+cloudName,cloud);
    	    }
        }
        setPageCloud(cloud);
        if (cache!=null) { // cache spul
            Module cacher = getDefaultCloudContext().getModule("scancache");
            if (cacher instanceof CacheModule) {
                try {
                    // caching of cloud=part of page
                    request_line = request.getRequestURI();
                    String reload = (String)session.getAttribute("reload");
                    String rst=((CacheModule)cacher).get(cache,request_line,expire+">");
	                if (rst!=null && !("Y".equals(reload))) {
	                    pageContext.getOut().write(rst);
                        return Tag.SKIP_BODY;
                    }
                } catch (IOException ioe){
                    throw new JspTagException(ioe.toString());
                }
            }
        }
    	return EVAL_BODY_TAG;
    }

    public void doInitBody() throws JspException {
        pageContext.setAttribute("cloud",getPageCloud());
    }
    
    public int doAfterBody() throws JspException {
        try {
            if (cache!=null) {
                Module cacher = getDefaultCloudContext().getModule("scancache");
                if (cacher instanceof CacheModule) {
                    ((CacheModule)cacher).put(cache,request_line,bodyOut.getString());
                }
            }
            bodyOut.writeOut(bodyOut.getEnclosingWriter());
            return Tag.SKIP_BODY;
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
    }

}

