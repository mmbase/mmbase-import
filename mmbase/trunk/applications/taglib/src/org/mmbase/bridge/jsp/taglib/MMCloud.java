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
import org.mmbase.security.Rank; // hmm.

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
    private String authenticate = "name/password"; 

    private String method = null; // how to log on, method can only be 'http'.
    private String logon = null;  
    private String pwd = null;
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

    public void setAuthenticate(String authenticate){
        this.authenticate = authenticate;
    }
    public void setMethod(String m){
        this.method = m;
    }


    /**
     *  Deny access to this page
     *
     * @return false
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

        if ("asIs".equals(method)) {
            // this is handy. 'logon' will be ignored, the cloud is as is was in the session
            debug("requested the cloud 'as is'");
            logon = null;   // that means in practice, to ignore the logon name.  
        }              
        
        if (cloud != null) {         
            // we have a cloud, check if it is a desired one
            // otherwise make it null.

            if ("anonymous".equals(method)) { 
                // explicity anonymous. 'logon' will be ignored
                debug("explicityly requested anonymous cloud");
                // an anonymous cloud was requested, check if it is.
                if (cloud.getUser().getRank() != Rank.ANONYMOUS) { 
                    debug("cloud is not anonymous, throwing it away");
                    cloud = null;
                    logon = null;
                    session.setAttribute("cloud_" + cloudName, null);
                }
            } else if (logon == null && method != null) { 
                // authorisation was requested, but not indicated for whom 
                if (cloud.getUser().getRank() == Rank.ANONYMOUS) { // so it simply may not be anonymous
                    debug("there was a cloud, but anonymous. log it on");
                    cloud = null;
                    session.setAttribute("cloud_" + cloudName, null);
                }
            } else  if (logon != null) { 
                // a logon name was given, check if logged on as the right one
                if (! cloud.getUser().getIdentifier().equals(logon)) { // no!
                    debug("logged on, but as wrong user. log out first.");
                    cloud = null;
                    session.setAttribute("cloud_" + cloudName, null);
                }
            }
        }

        if (cloud == null) { // we did't have a cloud, or it was not a good one:
            debug("logging on the cloud...");
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
                    if (username == null) { // there must be at least known a username...
                        debug("no username known");
                        deny(response);
                    }
                    logon = username;
                }
                pwd = password;
            } // http
            
            // do the MMCI cloud logging on
            if (logon != null) {
                debug("Username found. logging in");
                User user = getDefaultCloudContext().getNewUser();
                user.put("username", logon);
                user.put("password", pwd);
                try {
                    cloud = getDefaultCloudContext().getCloud(cloudName, authenticate, user);
                } catch (BridgeException e) { 
                    // did not succeed, so problably the password was wrong.
                    // give a deny, people can retry the password then.
                    if ( "http".equals(method)) {
                        deny(response);                    
                        // in case they give up, give an anonymous cloud:
                        cloud = getDefaultCloudContext().getCloud(cloudName);
                    } else { // strange, no method given, password wrong (or missing)
                        throw new JspTagException("Logon of user " + logon + " failed.");
                    }
                }
            } else { 
                cloud = getDefaultCloudContext().getCloud(cloudName);
            }

            if (cloud == null) { // stil null, give it up then...
                debug("Could not create Cloud, denying access");
                deny(response);
                return SKIP_BODY;
    	    } else {
    	        session.setAttribute("cloud_"+cloudName,cloud);
    	    }
        }
        
        setPageCloud(cloud);

        return EVAL_BODY_TAG;
    }

    public void doInitBody() throws JspException {
        pageContext.setAttribute("cloud",getPageCloud());
    }
    
    public int doAfterBody() throws JspException {
        try {
            bodyOut.writeOut(bodyOut.getEnclosingWriter());
            return Tag.SKIP_BODY;
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
    }

}

