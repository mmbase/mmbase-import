/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;

import java.util.*;

import javax.servlet.http.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mmbase.bridge.*;
import org.mmbase.security.Rank; // hmm, not from bridge, but we do need it anyway

import org.mmbase.bridge.jsp.taglib.util.StringSplitter;

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

public class CloudTag extends ContextReferrerTag implements CloudProvider {
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
    private static final int METHOD_UNSET = -1;
    private static final int METHOD_HTTP = 0;
    private static final int METHOD_ASIS = 1;
    private static final int METHOD_ANONYMOUS = 2;
    private static final int METHOD_LOGOUT = 3;

    private static Logger log = Logging.getLoggerInstance(CloudTag.class.getName());

    private static String DEFAULT_CLOUD_NAME = "mmbase";
    private String jspvar;

    private static final String REALM = "realm_";

    private Cookie[] cookies;

    private CloudContext cloudContext;

    private String cloudName  = DEFAULT_CLOUD_NAME;
    private String cloudURI  = null;
    private Cloud  cloud;

    private static HashMap anonymousClouds = new HashMap();

    private String authenticate = "name/password";

    private int method = METHOD_UNSET; // how to log on, method can eg be 'http'.
    private String logonatt =  "";
    private String pwd = null;
    private Rank   rank = null;
    private String sessionName = null;

    private static String FAILMESSAGE = "<h1>CloudTag Error</h1>";

    private HttpSession session;
    private HttpServletRequest  request;
    private HttpServletResponse response;

    /**
     * @return the default cloud context
     **/
    public CloudContext getDefaultCloudContext(){
        if (cloudContext == null){
            cloudContext = ContextProvider.getCloudContext(cloudURI);
//            cloudContext = LocalContext.getCloudContext();
        }
        return cloudContext;
    }

    public void setUri(String uri) throws JspTagException {
        cloudURI = getAttributeValue(uri);
    }

    public void setName(String name) throws JspTagException {
        cloudName = getAttributeValue(name);
    }

    public void setLogon(String l) throws JspTagException {
        logonatt = getAttributeValue(l);
        if ("".equals(l)) {
            logonatt = null;   // that also means to ignore the logon name
        }

    }
    public void setRank(String r) throws JspTagException {
        rank = Rank.getRank(getAttributeValue(r));
        if (rank == null) {
            throw new JspTagException("Unknown rank " + r);
        }
    }

    public void setPwd(String pwd) throws JspTagException {
        this.pwd = getAttributeValue(pwd);
    }

    public void setJspvar(String jv) {
        jspvar = jv;
    }

    public void setAuthenticate(String authenticate) {
        if (! "".equals(authenticate) ) {   // this makes it easier to ignore.
            this.authenticate = authenticate;
        }
    }

    public void setMethod(String mm) throws JspTagException {
        String m = getAttributeValue(mm);
        if ("http".equals(m)) {
            method = METHOD_HTTP;
        } else if ("asis".equals(m)) {
            method = METHOD_ASIS;
        } else if ("anonymous".equals(m)) {
            method = METHOD_ANONYMOUS;
        } else if ("logout".equals(m)) {
            method = METHOD_LOGOUT;
        } else {
            throw new JspTagException("Unknown value for 'method'  attribute (" + m + ")");
        }
    }

    public Cloud getCloudVar() {
        return cloud;
    }

    public void setCloudVar(Cloud c) {
        cloud = c;
    }

    public void setSessionname(String s) throws JspTagException {
        sessionName = getAttributeValue(s);
    }



    private Cookie searchCookie() {
        String cookie = REALM + getSessionName();
        if (cookies != null) {
            for (int i=0; i< cookies.length; i++) {
                if (cookies[i].getName().equals(cookie) && (! "".equals(cookies[i].getValue()))) {
                    return cookies[i];
                }
            }
        }
        return null;
    }
    private void setRealm(String r) {
        if (session == null) {
            Cookie c = searchCookie();
            if (c == null) {
                c = new Cookie(REALM + getSessionName(), r);
                c.setMaxAge(-1); // a day
            } else {
                c.setValue(r);
            }
            if (cookies.length == 0 ){
                cookies = new Cookie[1];
            }
            cookies[0] = c;
            response.addCookie(c);
        } else {
            session.setAttribute(REALM + getSessionName(), r);
        }
    }
    private void removeRealm() {
        if (session == null) {
            String cookie = REALM + getSessionName();
            log.debug("removing cookie");
            if (cookies != null) {
                for (int i=0; i< cookies.length; i++) {
                    if (cookies[i].getName().equals(cookie)) {
                        log.debug("removing cookie with value " + cookies[i]);
                        cookies[i].setValue("");
                        cookies[i].setMaxAge(0); // remove
                        response.addCookie(cookies[i]);
                    }
                }
            }
        } else {
            session.removeAttribute(REALM + getSessionName());
        }
    }
    private  String  getRealm() {
        if (session == null) { // try with cookie
            Cookie c = searchCookie();
            if (c != null) {
                log.debug("found cookie on path = " + c);
                return c.getValue();
            }
            return null;
        } else {
            return (String) session.getAttribute(REALM + getSessionName());
        }
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


        if (getRealm() == null) {
            // in the Realm is the time, this makes it unique, and is used by browser to
            // store the name password.
            // if you throw away the realm name from the session, then the browser does
            // not know the password anymore.
            // this is how 'logout' works.
            setRealm("MMBase@" + request.getServerName() + "." + java.util.Calendar.getInstance().getTime().getTime());
        }

        log.debug("setting header: " + getRealm());
        response.setHeader("WWW-Authenticate", "Basic realm=\"" + getRealm() + "\"");

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
        setAnonymousCloud(); // there must also be _some_ cloud, to avoid exception on u
        evalBody(); //  register var
        return SKIP_BODY;
    }

    private void logout() {
        removeRealm();
    }

    private void setAnonymousCloud() {
        log.debug("using a anonymous cloud");
        String key = cloudName + (cloudURI != null ? "@" +  cloudURI : "");
        cloud = (Cloud) anonymousClouds.get(key);
        if (cloud == null) {
            log.debug("couldn't find one");
            cloud = getDefaultCloudContext().getCloud(cloudName);
            anonymousClouds.put(key, cloud);
            log.debug("put in hashMap");
        }
        // check if cloud was expired:
        if (! cloud.getUser().isValid()) {
            log.debug("anonymous cloud was expired, creating a new one");
            cloud = getDefaultCloudContext().getCloud(cloudName);
            anonymousClouds.put(key, cloud);
        }
    }
    private String getSessionName() {
        if (sessionName == null) {
            return "cloud_" + cloudName;
        } else {
            return sessionName;
        }
    }


    private int evalBody() throws JspTagException {

        if (jspvar != null) {
            pageContext.setAttribute(jspvar, cloud);
        }
        if (getId() != null) { // write to context.
            getContextTag().register(getId(), cloud);
        }
        // the surround context tag sometimes also want so server information from the cloud context.
        getContextTag().setCloudContext(cloud.getCloudContext());


        return EVAL_BODY_TAG;
    }

    /**
     *  Check name and retrieve cloud
     */
    public int doStartTag() throws JspTagException {

        Vector logon = logonatt != null ? StringSplitter.split(logonatt) : null;
        if (logon != null && logon.size() == 0) logon = null;

        // check if this is a reuse:
        if (getReferid() != null) {
            if (method != METHOD_UNSET || logon != null) { // probably add some more
                throw new JspTagException ("The 'referid' attribute of cloud cannot be used together with 'method' or 'logon' attributes");
            }
            cloud = (Cloud) getContextTag().getObject(getReferid());
            return evalBody();
        }

        // first check if we need an anonymous cloud,
        // in which case we don't want to use the session. Pages get
        // better chachable then.
        if ( (method == METHOD_UNSET && logon == null && rank == null) ||
              method == METHOD_ANONYMOUS) { // anonymous cloud:
            log.debug("Implicitely requested anonymous cloud. Not using session");
            setAnonymousCloud();
            return evalBody();
        }


        request  = (HttpServletRequest) pageContext.getRequest();
        response = (HttpServletResponse)pageContext.getResponse();

        cookies = request.getCookies();
        if (cookies == null) {
            cookies = new Cookie[0];
        }

        session  = (HttpSession)pageContext.getSession();

        if (session != null) { // some people like to disable their session
            cloud = (Cloud)session.getAttribute(getSessionName());
        }

        log.debug("startTag " + cloud);

        if (method == METHOD_LOGOUT) {
            log.debug("request to log out, remove session atributes, give anonymous cloud.");
            logout();
            if (session != null) {
                log.debug("session is not null");
                session.removeAttribute(getSessionName());       // remove cloud itself
            }
            setAnonymousCloud();
            // the available cloud in this case is a anonymous one
            return evalBody();
        }

        if (method == METHOD_ASIS) {
            // this is handy. 'logon' will be ignored, the cloud is as is was in the session
            log.debug("requested the cloud 'as is'");
            logon = null;   // that means in practice, to ignore the logon name.
            rank  = null;
        }



        if (cloud != null && (! cloud.getUser().isValid())) {
            // cloud expired (security changed)
            log.debug("found a cloud in the session, but is was expired, throwing it away");
            cloud = null;
        }

        if (cloud != null) {
            // we have a cloud, check if it is a desired one
            // otherwise make it null.
            if (log.isDebugEnabled()) {
                log.debug("found cloud in session m: " + method + " l: " + logon);
            }
            if (logon == null && rank == null && method != METHOD_UNSET) {
                // authorisation was requested, but not indicated for whom
                log.debug("implicitily requested non-anonymous cloud. Current user: " + cloud.getUser().getIdentifier());
                if (cloud.getUser().getRank().equals(Rank.ANONYMOUS.toString())) { // so it simply may not be anonymous
                    log.debug("there was a cloud, but anonymous. log it on");
                    cloud = null;
                    if (session != null) {
                        session.removeAttribute(getSessionName());
                    }
                }
            } else  if (logon != null) {
                log.debug("explicitily requested non-anonymous cloud. Current user: " + cloud.getUser().getIdentifier());
                // a logon name was given, check if logged on as the right one
                if (! logon.contains(cloud.getUser().getIdentifier())) { // no!
                    log.debug("logged on, but as wrong user. log out first.");
                    cloud = null;
                    if (session != null) {
                        session.removeAttribute(getSessionName());
                    }
                } else {
                    log.debug("Cloud is ok already");
                }
            } else if (rank != null) {
                log.debug("explicitily requested non-anonymous cloud. Current user: " + cloud.getUser().getIdentifier());
                Rank curRank = Rank.getRank(cloud.getUser().getRank());
                if (curRank.getInt() < rank.getInt()) {
                    if (log.isDebugEnabled()) {
                        log.debug("logged on, but rank of user is to low (" + rank.toString() + ". log out first.");
                    }
                    cloud = null;
                    if (session != null) {
                        session.removeAttribute(getSessionName());
                    }
                } else {
                    log.debug("Cloud is ok already");
                }
            }
        }

        if (cloud == null) { // we did't have a cloud, or it was not a good one:
            log.debug("logging on the cloud...");
            // check how to log on:
            if (method == METHOD_HTTP) {
                log.debug("with http");

                if (getRealm() == null) {
                    log.debug("no realm found, need to log on again");
                    return deny("<h2>Need to log in again</h2> You logged out");
                }
                log.debug("authent: " + request.getHeader("WWW-Authenticate") + " realm: " + getRealm());
                // find logon, password with http authentication
                String username = null;
                String password = null;
                try {
                    String mime_line     = request.getHeader("Authorization");
                    if (mime_line != null) {
                        String user_password = org.mmbase.util.Encode.decode("BASE64", mime_line.substring(6));
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
                if (method == METHOD_HTTP) {
                    if (logon != null) { // if there was a username specified as well, it must be the same
                        log.debug("http with username");
                        if (! logon.contains(username)) {
                            log.debug("username not correct");
                            return deny("<h2>Wrong username</h2> must be " + logon + "");
                        } else {
                            logon = new Vector(); logon.add(username);
                        }
                    } else { // logon == null
                        log.debug("http without username");
                        if (username == null) { // there must be at least known a username...
                            log.debug("no username known");
                            return deny("<h2>No username given</h2>");
                        }
                        logon = new Vector(); logon.add(username);
                    }
                }
                pwd = password;
            } // http

            // do the MMCI cloud logging on
            if (logon != null) {
                log.debug("Username found. logging in");
                HashMap user = new HashMap();
                user.put("username", logon.get(0));
                user.put("password", pwd);
                try {
                    cloud = getDefaultCloudContext().getCloud(cloudName, authenticate, user);
                    // ok, logging on work, now check rank if necessary
                    if (rank != null) {
                        Rank curRank = Rank.getRank(cloud.getUser().getRank());
                        if (curRank.getInt() < rank.getInt()) {
                            log.debug("logged on, but rank of user is to low (" + cloud.getUser().getRank() + ". log out first.");
                            cloud = null;
                            if (session != null) {
                                session.removeAttribute(getSessionName());
                            }
                            return deny("<h2>Rank to low for this page (is " + curRank.toString() + ", must be at least " + rank.toString() + ")</h2>");


                        }

                    }

                } catch (BridgeException e) {
                    // did not succeed, so problably the password was wrong.
                    if (method == METHOD_HTTP) { // give a deny, people can retry the password then.
                        return deny("<h2>This page requires authentication</h2>");
                     } else { // strange, no method given, password wrong (or missing), that's really wrong.
                        throw new JspTagException("Logon of user " + logon.get(0) + " failed." +
                            (pwd == null ? " (no password given)" : " (wrong password)"));
                    }
                }
            } else {
                log.debug("no login given, creating anonymous cloud");
                // no logon, create an anonymous cloud.
                setAnonymousCloud();
            }

            if (cloud == null) { // stil null, give it up then...
                log.debug("Could not create Cloud.");
                throw new JspTagException("Could not create cloud.");
            } else {
                if (session != null) {
                    session.setAttribute(getSessionName(), cloud);
                }
            }
        }
        return evalBody();
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

