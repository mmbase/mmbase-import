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

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import sun.misc.BASE64Decoder;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.CloudContext;
import org.mmbase.bridge.LocalContext;
import org.mmbase.bridge.BridgeException;
import org.mmbase.security.Rank; // hmm, not from bridge, but we do need it anyway

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* Creates a cloud object (pulling it from if session necessary). While
* creating a cloud object one also has to authenticate itself, so this
* functionality is also in this tag.
*
* @author Pierre van Rooden
* @author Michiel Meeuwissen
*
**/
public class CloudTag extends BodyTagSupport implements CloudProvider {
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

        michiel: I don't really know if this is so very ugly. It does
        make some sense to me. After all, the Cloud takes care for the
        authentication, and TagLibs are for http. So i don't think it
        strange that CloudTag takes care for both.

        The authentication is in the cloud, which is in the
        session. Are you sure that this will not work with 2 users?
        Would that be a problem of this tag?

    */

    private static Logger log = Logging.getLoggerInstance(CloudTag.class.getName());

    public  static String DEFAULT_CLOUD_NAME = "mmbase";   

    private static CloudContext cloudContext;
    
    private String cloudName  = DEFAULT_CLOUD_NAME;
    private Cloud  cloud;

    private static HashMap anonymousClouds = new HashMap(); 
    
    private String authenticate = "name/password"; 
    
    private String method = null; // how to log on, method can eg be 'http'.
    private String logon = null;  
    private String pwd = null;

    private static String FAILMESSAGE = "<h1>CloudTag Error</h1>";
    
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
    
    public Cloud getCloudVar() {
        return cloud;
    }

    public void setCloudVar(Cloud c) {
        cloud = c;
    }
    
    public void  registerNode(String id, Node n) {        
        // does nothing.
    }
    
    public Node getNode(String id) throws JspTagException {
        throw new JspTagException("Cannot get Nodes directly from Cloud (use a group tag)");
    }


    /**
    * Deny access to this page.
    *
    * @param A message to provide to the user if she does not accepts authorization (enters 'cancel')
    *
    * REMARK: Perhaps we should do some i18n on this message
    *
    * @return SKIP_BDDY;
    */    
    private int deny(String message) throws JspTagException {
        log.debug("sending deny");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", "Basic realm=\"www\"");
        //res.setHeader("Authorization", logon);   would ne nice...
        //keesj:look at the php3 tutorial for an example

        // if cancel
        // this cannot be done with an exception (which can be a redirect), because the page must 
        // flow ahead, to give the popup opportunity to pop up.
        try {
            pageContext.getOut().print(FAILMESSAGE + message); 
        } catch (IOException ioe) {
            throw new JspTagException(ioe.toString());
        }
        return SKIP_BODY; 
    }
    
    
    /**
    *  Check name and retrieve cloud
    */
    public int doStartTag() throws JspTagException{
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
                    session.removeAttribute("cloud_" + cloudName);
                }
            } else if (logon == null && method != null) { 
                // authorisation was requested, but not indicated for whom 
                log.debug("implicitily requested non-anonymous cloud. Current user: " + cloud.getUser().getIdentifier());                
                if (cloud.getUser().getRank() == Rank.ANONYMOUS) { // so it simply may not be anonymous
                    log.debug("there was a cloud, but anonymous. log it on");
                    cloud = null;
                    session.removeAttribute("cloud_" + cloudName);
                }
            } else  if (logon != null) { 
                log.debug("explicitily requested non-anonymous cloud. Current user: " + cloud.getUser().getIdentifier());
                // a logon name was given, check if logged on as the right one
                if (! cloud.getUser().getIdentifier().equals(logon)) { // no!
                    log.debug("logged on, but as wrong user. log out first.");
                    cloud = null;
                    session.removeAttribute("cloud_" + cloudName);
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
                        return deny("<h2>Wrong username</h2> must be " + logon + "");
                    }
                } else { // logon == null
                    log.debug("http without username");
                    if (username == null) { // there must be at least known a username...
                        log.debug("no username known");
                        return deny("<h2>No username given</h2>");
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
                        return deny("<h2>This page requires authentication</h2>");                    
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
    
    public int doAfterBody() throws JspTagException {
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());            
            return SKIP_BODY;
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
    }


}

