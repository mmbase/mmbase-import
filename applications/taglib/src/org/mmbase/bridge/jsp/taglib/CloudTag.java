/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;

import java.util.HashMap;
import java.util.StringTokenizer;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import sun.misc.BASE64Decoder;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.CloudContext;
import org.mmbase.bridge.LocalContext;
import org.mmbase.bridge.BridgeException;
import org.mmbase.security.Rank; // hmm.

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* CloudTag
* Creates a cloud object (pulling it from if session necessary)
*
* @author Pierre van Rooden
* @author Michiel Meeuwissen
*
**/
public class CloudTag extends BodyTagSupport {
    /*
        keesj: This class is full of ugly authentication code
        we should create an authenticationFactory. An other problem
        is that the "HTTP-authentication" has nothing to do
        with sessions. this is the reason why MMEdtiors do not
        work well when 2 users are logged in using the same user/passwd combination.
        HTTP-authentication. Therefore authetication should happend based on a session.
        maybe it is good idea to bind a HTTP-authentication to a HTTP-session
        
        One other question we should have answer to is if the tags are created once and reused
        or are created for every page etc...
    */

    public  static String DEFAULT_CLOUD_NAME = "mmbase";
    
    private static Logger log = Logging.getLoggerInstance(CloudTag.class.getName());

    private static CloudContext cloudContext;
    
    private String cloudName  = DEFAULT_CLOUD_NAME;
    private Cloud  cloud;
    
    private String authenticate = "name/password"; 
    
    private String method = null; // how to log on, method can only be 'http'.
    private String logon = null;  
    private String pwd = null;

    private BodyContent bodyContent; 

    private boolean success = true;
    private String  failMessage = "<h1>CloudTag</h1>";
    
    private HttpSession session;
    private HttpServletRequest  request;
    private HttpServletResponse response;
    
        
    /**
    * @return the default cloud context 
    **/
    public CloudContext getDefaultCloudContext(){
        if (cloudContext == null){
            cloudContext=LocalContext.getCloudContext();
        } 
        return cloudContext;
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
    
    public void setAuthenticate(String authenticate) {
        if (! "".equals(authenticate) ) {   // this makes it easier to ignore.
            this.authenticate = authenticate;
        }
    }
    
    public void setMethod(String m){
        this.method = m;
    }
    
    public Cloud getCloud() {
        return cloud;
    }

    public void setCloud(Cloud c) {
        cloud = c;
    }
    
    /**
    *  Deny access to this page
    *
    * @return false
    */    
    private boolean deny(HttpServletResponse res) {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        log.debug("Not logged on.");
        res.setHeader("WWW-Authenticate", "Basic realm=\"www\"");
        //res.setHeader("Authorization", blabla);   would ne nice...
        //keesj:look at the php3 tutorial for an example
        return false; 
    }
    
    
    /**
    *  Check name and retrieve cloud
    */
    public int doStartTag() throws JspException{
        session  = (HttpSession)pageContext.getSession();
        request  = (HttpServletRequest)pageContext.getRequest();
        response = (HttpServletResponse)pageContext.getResponse();
        cloud = (Cloud)session.getAttribute("cloud_" + cloudName);

        log.debug("startTag " + cloud);
        if ("asIs".equals(method)) {
            // this is handy. 'logon' will be ignored, the cloud is as is was in the session
            log.debug("requested the cloud 'as is'");
            logon = null;   // that means in practice, to ignore the logon name.  
        }              
        
        if ("".equals(logon)) {
            logon = null;   // that also means to ignore the logon name
        }
        
        if (cloud != null) {         
            // we have a cloud, check if it is a desired one
            // otherwise make it null.
            log.debug("found cloud in session m: " + method + " l: " + logon);
            
            if ("anonymous".equals(method)) { 
                // explicity anonymous. 'logon' will be ignored
                log.debug("explicityly requested anonymous cloud");
                // an anonymous cloud was requested, check if it is.
                if (cloud.getUser().getRank() != Rank.ANONYMOUS) { 
                    log.debug("cloud is not anonymous, throwing it away");
                    cloud = null;
                    logon = null;
                    session.setAttribute("cloud_" + cloudName, null);
                }
            } else if (logon == null && method != null) { 
                // authorisation was requested, but not indicated for whom 
                log.debug("implicitily requested non-anonymous cloud. Current user: " + cloud.getUser().getIdentifier());                
                if (cloud.getUser().getRank() == Rank.ANONYMOUS) { // so it simply may not be anonymous
                    log.debug("there was a cloud, but anonymous. log it on");
                    cloud = null;
                    session.setAttribute("cloud_" + cloudName, null);
                }
            } else  if (logon != null) { 
                log.debug("explicitily requested non-anonymous cloud. Current user: " + cloud.getUser().getIdentifier());
                // a logon name was given, check if logged on as the right one
                if (! cloud.getUser().getIdentifier().equals(logon)) { // no!
                    log.debug("logged on, but as wrong user. log out first.");
                    cloud = null;
                    session.setAttribute("cloud_" + cloudName, null);
                } else {
                    log.debug("Cloud is ok already");
                }
            }
        }
        
        if (cloud == null) { // we did't have a cloud, or it was not a good one:
            log.debug("logging on the cloud...");
            // check how to log on:
            if ("http".equals(method)) {
                log.debug("with http");
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
                    log.error("oooops" + e);
                }
                // Authenticate user 
                if (log.isDebugEnabled()) {
                    log.debug("u " + username + " p " + password);
                }
                if (logon != null) { // if there was a username specified as well, it must be the same
                    log.debug("http with username");
                    if (! logon.equals(username)) {
                        log.debug("username not correct");
                        deny(response);
                        // if cancel

                        // this cannot be done with an exception, because the page must 
                        // flow ahead, to give the popup opportunity to pop up.
                        success = false;
                        failMessage += "<h2>Wrong username</h2> must be " + logon + "";
                        logon = null;                       
                        
                    }
                } else { // logon == null
                    log.debug("http without username");
                    if (username == null) { // there must be at least known a username...
                        log.debug("no username known");
                        deny(response);
                        success = false;
                        failMessage += "<h2>No username given</h2>";
                    }
                    logon = username;
                }
                pwd = password;
            } // http
            
            // do the MMCI cloud logging on
            if (logon != null) {
                log.debug("Username found. logging in");
                HashMap user = new HashMap();
                user.put("username", logon);
                user.put("password", pwd);
                try {
                    cloud = getDefaultCloudContext().getCloud(cloudName, authenticate, user);
                } catch (BridgeException e) { 
                    // did not succeed, so problably the password was wrong.
                    if ( "http".equals(method)) { // give a deny, people can retry the password then.
                        deny(response);                    
                        // if people cancel:
                        success = false;
                        failMessage = "<h2>This page requires authentication</h2>"; 
                     } else { // strange, no method given, password wrong (or missing), that's really wrong.
                        throw new JspTagException("Logon of user " + logon + " failed." + 
                            (pwd == null ? " (no password given)" : " (wrong password)"));
                    }
                }
            } else { 
                log.debug("no login given, creating anonymous cloud");
                // no logon, create an anonymous cloud.
                cloud = getDefaultCloudContext().getCloud(cloudName);
            }
            
            if (cloud == null) { // stil null, give it up then...
                log.debug("Could not create Cloud.");
                throw new JspTagException("Could not create cloud.");           
            } else {
                session.setAttribute("cloud_" + cloudName, cloud);
            }
        }        
        pageContext.setAttribute("cloud", cloud);
        return EVAL_BODY_TAG;
    }
    
    public void doInitBody() throws JspException {
        pageContext.setAttribute("cloud", cloud);
    }
    
    public int doAfterBody() throws JspException {
        try {
            bodyContent = getBodyContent();
            if (success) {                
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } else {
                bodyContent.getEnclosingWriter().write(failMessage);
            }
            return SKIP_BODY;
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
    }

    public int doEndTag() throws JspException {
        log.debug("end tag");
        return EVAL_PAGE;
    }

}

