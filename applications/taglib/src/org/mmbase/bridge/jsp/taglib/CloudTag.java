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
import javax.servlet.jsp.PageContext;

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
    private static final int METHOD_LOGINPAGE = 4;


    /**
     * Constants needed for the loginpage attribute functionality
     */
    private static final String LOGINPAGE_COMMAND_PARAMETER = "command";
    private static final String LOGINPAGE_COMMAND_LOGIN  = "login";
    private static final String LOGINPAGE_COMMAND_LOGOUT = "logout";
    private static final String LOGINPAGE_AUTHENTICATE_PARAMETER  = "authenticate";
    private static final String LOGINPAGE_CLOUD_PARAMETER  = "cloud";

    private static final String LOGINPAGE_DENYREASON_NEED = "please";
    private static final String LOGINPAGE_DENYREASON_FAIL = "failed";
    private static final String LOGINPAGE_DENYREASON_RANKTOOLOW = "rank";

    private static final int DENYREASON_NEED = 0;
    private static final int DENYREASON_FAIL = 1;
    private static final int DENYREASON_RANKTOOLOW = 2;

    private static Logger log = Logging.getLoggerInstance(CloudTag.class.getName());

    private static String DEFAULT_CLOUD_NAME = "mmbase";
    private static String DEFAULT_AUTHENTICATION = "name/password";
    private String jspvar;

    private static final String REALM = "realm_";

    private Cookie[] cookies;

    private CloudContext cloudContext;

    private String cloudName  = DEFAULT_CLOUD_NAME;
    private String cloudURI  = null;
    private Cloud  cloud;

    private String authenticate = DEFAULT_AUTHENTICATION;

    private String loginpage =  null;
    private int method = METHOD_UNSET; // how to log on, method can eg be 'http'.    
    private String logonatt =  "";
    private List   logon;
    private String pwd = null;
    private Rank   rank = null;
    private String sessionName = null;

    private static String FAILMESSAGE = "<h1>CloudTag Error</h1>";

    private HttpSession session;
    private HttpServletRequest  request;
    private HttpServletResponse response;
    private Locale locale;

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
        } else if ("loginpage".equals(m)) {
            method = METHOD_LOGINPAGE;
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

    public void setLoginpage(String loginpage) throws JspTagException {
        this.loginpage = loginpage;
    }

    /**
     * @return The cookie with the sessionname.
     */

    private Cookie searchCookie() {
        String cookie = REALM + getSessionName();
        if (log.isDebugEnabled()) log.debug("Searching cookie " + cookie);
        if (cookies != null) {
            for (int i=0; i< cookies.length; i++) {
                if (cookies[i].getName().equals(cookie) && (! "".equals(cookies[i].getValue()))) {
                    return cookies[i];
                }
            }
        }
        log.debug("Cookie not found");
        return null;
    }

    /**
     * Sets the 'realm' to r, and write it to the session, or if not possible in a cookie.
     * Changing the realm makes it possible to 'logout' when using http-authentication.
     * @return false on failure
     */
    private boolean setRealm(String r) throws JspTagException {
        if (session == null) {
            log.debug("setting realm in cookie"); // Is this ever used? Can teh session be null in Tomcat or so?
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
            if (session.isNew()) { 
                log.debug("New session!? That is very suspicious. Perhaps URL was not encoded, and cookies disabled, sending redirect to make sure the url is encoded.");
                String query = request.getQueryString();
                String thisPage = request.getRequestURI() + (query == null ? "" : "?" + query);
                try {
                    String url = response.encodeRedirectURL(thisPage);
                    response.sendRedirect(url);
                } catch (IOException e) {
                    throw new JspTagException(e.toString());
                }
                return false;
            }
            log.debug("setting realm in session");
            session.setAttribute(REALM + getSessionName(), r);
        }
        return true;
    }
    /**
     * Removing the realm will cause a new one to be created, and if
     * you have a good browser, it will pop up a new 'login' screen (when using http-authentication).
     */

    private void removeRealm() {
        if (session == null) {
            String cookie = REALM + getSessionName();
            log.debug("removing cookie");
            if (cookies != null) {
                for (int i=0; i< cookies.length; i++) {
                    if (cookies[i].getName().equals(cookie)) {
                        if (log.isDebugEnabled()) log.debug("removing cookie with value " + cookies[i]);
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
    /**
     * Gets the current realm in use for http-authentication.
     */
    private  String  getRealm() {
        if (session == null) { // try with cookie
            Cookie c = searchCookie();
            if (c != null) {
                if (log.isDebugEnabled()) log.debug("found cookie on path = " + c);
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
    private int denyHTTP(String message) throws JspTagException {
        log.debug("sending deny");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);


        if (getRealm() == null) {
            // in the Realm is the time, this makes it unique, and is used by browser to
            // store the name password.
            // if you throw away the realm name from the session, then the browser does
            // not know the password anymore.
            // this is how 'logout' works.
            if (!setRealm("MMBase@" + request.getServerName() + "." + java.util.Calendar.getInstance().getTime().getTime())) {
                return SKIP_BODY;
            }
        }

        if (log.isDebugEnabled()) log.debug("setting header: " + getRealm());
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
        setAnonymousCloud(); // there must also be _some_ cloud, to avoid exception on 
        evalBody(); //  register var
        return SKIP_BODY;
    }

    /**
     * Sets the cloud member variable to an anonymous cloud. 
     * @return true on success (cloud is set), false on failure (cloud is set to null)
     */

    private boolean setAnonymousCloud() {
        try {
            log.debug("using an anonymous cloud");
            cloud = getDefaultCloudContext().getCloud(cloudName);
            cloud.setLocale(locale);
            log.debug("put in hashMap");
            return true;
        } catch (org.mmbase.security.SecurityException e) {
            log.debug("Could not create anonymous cloud because " + e.toString());
            cloud = null;
            return false;
        }
    }

    /**
     * Return the name of session variable in which the cloud is
     * stored. This is on default "cloud_mmbase" but this can be
     * influenced with the sessionname attribute. If tho pages have
     * different sessionnames for the cloud, they can be logged in
     * simultaniously with different clouds, but in the same session.
     */
    public String getSessionName() {
        if (sessionName == null) {
            return "cloud_" + cloudName;
        } else {
            return sessionName;
        }
    }

    /**
     * If the 'cloud' member is set, and everything is ok, then this function is called in doStartTag()
     * @return EVAL_BODY_BUFFERED or SKIP_BODY if cloud is null (which normally means that no anonymous is present)
     */

    private int evalBody() throws JspTagException {

        if (getId() != null) { // write to context.
            getContextTag().register(getId(), cloud);
        }

        if (cloud  == null) return SKIP_BODY;

        if (jspvar != null) {
            pageContext.setAttribute(jspvar, cloud);
        }

        if (locale != null) cloud.setLocale(locale);

        // the surround context tag sometimes also want so server information from the cloud context.
        getContextTag().setCloudContext(cloud.getCloudContext());


        return EVAL_BODY_BUFFERED;
    }

    /**
     * Checks if this is a 'reuse' of the cloud tag (using the referid
     * attribute), and sets the cloud member variable if this is the
     * cases
     *
     * @return true if this is a reuse (the caller returns eval-body) and false otherwise (the caller continues).
     */

    private final boolean checkReuse() throws JspTagException {
        if (getReferid() != null) {
            if (method != METHOD_UNSET || logon != null) { // probably add some more
                throw new JspTagException ("The 'referid' attribute of cloud cannot be used together with 'method' or 'logon' attributes");
            }
            log.debug("found cloud with referid");
            cloud = (Cloud) getContextTag().getObject(getReferid());
            return true;
        }
        return false;
    }

    /**
     * Checks if an anonymous cloud is requested. If so, set the cloud variable accordingly.
     * @return true if cloud must be anonymous (the caller returns eval-body) and false otherwise (the caller continues).
     */

    private final boolean checkAnonymous() {        
        if ( (method == METHOD_UNSET && logon == null && rank == null && loginpage == null) ||
              method == METHOD_ANONYMOUS) { // anonymous cloud:
            log.debug("Implicitely requested anonymous cloud. Not using session");
            setAnonymousCloud();
            return true;
        }
        return false;
    }

    /**
     * Checks if the cloud of the session if requested to be 'logged out'.
     */
    private final boolean checkLogoutLoginPage() {
        if (loginpage != null && LOGINPAGE_COMMAND_LOGOUT.equals(request.getParameter(LOGINPAGE_COMMAND_PARAMETER)))  {
            log.debug("request to log out, remove session atributes, give anonymous cloud.");
            if (cloud != null) {
                removeRealm();
                if (session != null) {
                    log.debug("ok. session is not null");                
                    session.removeAttribute(getSessionName());       // remove cloud itself
                }
            }
            setAnonymousCloud();
            return true;
        }
        return false;
    }

    /**
     * Checks if the cloud of the session if requested to be 'logged out'.
     */
    private final boolean checkLogoutMethod() {
        if(method == METHOD_LOGOUT) {
            if (cloud != null) {
                removeRealm();
                if (session != null) {
                    log.debug("ok. session is not null");                
                    session.removeAttribute(getSessionName());       // remove cloud itself
                }
            }
            setAnonymousCloud();
            return true;
        }
        return false;
    }


    /**
     * Creates the member variables session and cookies variables member 
     */
     private final void setupSession() {        
        cookies = request.getCookies();
        if (cookies == null) {
            cookies = new Cookie[0];
        }
        log.debug("getting (thus creating) session now: " + session);
        session  = (HttpSession) pageContext.getSession();
        if (session != null) { // some people like to disable their session
            cloud = (Cloud)session.getAttribute(getSessionName());
            if (log.isDebugEnabled()) {
                if (cloud != null) {
                    log.debug("Created/found a session. Cloud in it is of: " + cloud.getUser().getIdentifier());
                } else {
                    log.debug("No cloud found");
                }
            }
        } else {
            log.debug("Not succeeded creating a session");
        }

    }

    /**
     * Checks if there is a surround locale tag, and sets the 'locale' member variable accordingly.
     */

    private final void checkLocale() throws JspTagException {
        locale = null;
        LocaleTag localeTag = (LocaleTag) findParentTag("org.mmbase.bridge.jsp.taglib.LocaleTag", null, false);
        if (localeTag != null) {
            locale = localeTag.getLocale();
        }

    }

    /**
     * Checks wether the cloud is requested 'as is', meaning that is must be tried to get it from the session.
     * 
     */

    private final boolean checkAsis() throws JspTagException {
        if (method == METHOD_ASIS) {
            session  = request.getSession(false);
            if (session != null) { 
                cloud = (Cloud) session.getAttribute(getSessionName());
            }
            if (cloud == null) { 
                setAnonymousCloud();
            }
            return true;
        }
        return false;

    }

    /**
     * Makes the cloud variable null (may not be null already) if it
     * is 'expired'. This means normally that the security
     * configuration has been changed, or MMBase restarted or
     * something like that. This means that the cloud (probably gotten
     * from the session), cannot be used anymore.
     */


    private final void checkValid() {
        if (! cloud.getUser().isValid()) {
            log.debug("found a cloud in the session, but is was expired, throwing it away");
            cloud = null;
        }
    }

    /**
     * Checks if the current cloud satisfies the other conditions set
     * on it (method=anonymous, logon, rank attribute). If not the
     * cloud member variable is made null.
     */
    private final void checkCloud() {
        // we have a cloud, check if it is a desired one
        // otherwise make it null.
        if (log.isDebugEnabled()) {
            log.debug("found cloud in session m: " + method + " l: " + logon);
        }
        if (logon == null && rank == null && method != METHOD_UNSET) {
            // authorisation was requested, but not indicated for whom
            if (log.isDebugEnabled()) log.debug("implicitily requested non-anonymous cloud. Current user: " + cloud.getUser().getIdentifier());
            if (cloud.getUser().getRank().equals(Rank.ANONYMOUS.toString())) { // so it simply may not be anonymous
                log.debug("there was a cloud, but anonymous. log it on");
                cloud = null;
                if (session != null) {
                    session.removeAttribute(getSessionName());
                }
            }
        } else  if (logon != null) {
            if (log.isDebugEnabled()) log.debug("explicitily requested non-anonymous cloud. Current user: " + cloud.getUser().getIdentifier());
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
            if (log.isDebugEnabled()) log.debug("explicitily requested non-anonymous cloud. Current user: " + cloud.getUser().getIdentifier());
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

    /**
     * Sets logon and password using http-authentication in the Map argument.
     * @param uses A Map which will be passed to the security system.
     * @return SKIP_BODY if a deny was sent, the caller must not
     * continue then. EVAL_BODY_BUFFERED otherwise (can be ignored).
     */

    private final int doHTTPAuthentication(Map user) throws JspTagException {
        log.debug("with http");
        
        if (getRealm() == null) {
            log.debug("no realm found, need to log on again");
            return denyHTTP("<h2>Need to log in again</h2> You logged out");
        }
        if (log.isDebugEnabled()) log.debug("authent: " + request.getHeader("WWW-Authenticate") + " realm: " + getRealm());
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

        if (logon != null) { // if there was a username specified as well, it must be the same
            log.debug("http with username");
            if (! logon.contains(username)) {
                log.debug("username not correct");
                return denyHTTP("<h2>Wrong username</h2> must be " + logon + "");
            } else {
                logon = new Vector(); logon.add(username);
            }
        } else { // logon == null
            log.debug("http without username");
            if (username == null) { // there must be at least known a username...
                log.debug("no username known");
                return denyHTTP("<h2>No username given</h2>");
            }
            logon = new Vector(); 
            logon.add(username);
        }
        pwd = password;
        user.put("username", logon.get(0));
        user.put("password", pwd);
        return EVAL_BODY_BUFFERED;

    }

    /**
     * Sets logon-variables using the login page (with the loginpage attribute) in the Map argument.
     * @param uses A Map which will be passed to the security system,.
     * @return SKIP_BODY if a redirect was given (to the login page),
     * the caller must not continue then. EVAL_BODY_BUFFERED otherwise
     * (can be ignored).
     */


    private int doLoginPage(Map user) throws JspTagException {
        log.debug("login page required to acces this cloud data!");            

        // look if we need to login(param command='login') with methods and with the params...
        // otherwise redirect!
        if(LOGINPAGE_COMMAND_LOGIN.equals(request.getParameter(LOGINPAGE_COMMAND_PARAMETER))) {

            if (log.isDebugEnabled()) {
                log.debug("Request to login with loginpage, trying to perform an login");
            }
            // now pass all the attributes to the cloud-login method of the cloudcontext!
            String authenticatePassed=request.getParameter(LOGINPAGE_AUTHENTICATE_PARAMETER);
            if(authenticatePassed != null) {
                authenticate=authenticatePassed;
            }
            String cloudNamePassed=request.getParameter(LOGINPAGE_CLOUD_PARAMETER);
            if (cloudNamePassed!=null) {
                cloudName =cloudNamePassed; 
            }
            Enumeration enum = request.getParameterNames();                 
            while(enum.hasMoreElements()) {
                String key = (String) enum.nextElement();
                String value = request.getParameter(key);
                if (log.isDebugEnabled()) log.debug("security info --> key:" + key + " value:" + value);
                user.put(key, value);
            }
            return EVAL_BODY_BUFFERED;
        } else {
            // no command give, send redirect to specified login page
            return denyLoginPage(LOGINPAGE_DENYREASON_NEED);
        }
    }

    /**
     * Denies access to this page and sends are redirect to the login-page (given by login page attribute).
     * @param The reason for deny, can be LOGINPAGE_DENYREASON_NEED or LOGINPAGE_DENYREASON_FAIL.
     * @return SKIP_BODY
     */

    private int denyLoginPage(String reason) throws JspTagException {
        // we need to do a login on the page specified by the loginpage attribute
        // TODO: does this one need to be absolute?
        String from = request.getRequestURI();
        String reference = from;
        if(request.getQueryString() != null) {
            reference += "?" + request.getQueryString();
        }        
        reference = org.mmbase.util.Encode.encode("ESCAPE_URL_PARAM", reference);
        String url = response.encodeRedirectURL(loginpage + "?referrer=" + reference + "&reason=" + reason);
        try {
            if (log.isDebugEnabled()) log.debug("redirecting to:" + url);
            response.sendRedirect(url);            
            return SKIP_BODY;
        } catch(java.io.IOException ioe) {
            throw new JspTagException ("error sending redirect:" + ioe);
        }
    }


    private final int deny(int reason) throws JspTagException {
        
        // did not succeed, so problably the password was wrong.
        if (method == METHOD_HTTP) { // give a deny, people can retry the password then.
            switch(reason) {
            case DENYREASON_FAIL: return denyHTTP("<h2>This page requires authentication</h2>");
            case DENYREASON_RANKTOOLOW: return denyHTTP("<h2>Rank too low for this page (must be at least " + rank.toString() + ")</h2>");
            default: return denyHTTP("<h2>This page requires authentication</h2>");
            }
        } else if (loginpage != null) {
            switch(reason) {
            case DENYREASON_FAIL: return denyLoginPage(LOGINPAGE_DENYREASON_FAIL);
            case DENYREASON_RANKTOOLOW: return denyLoginPage(LOGINPAGE_DENYREASON_RANKTOOLOW);
            default:  return denyLoginPage(LOGINPAGE_DENYREASON_FAIL);
            }
        } else { // strange, no method given, password wrong (or missing), that's really wrong.
            switch(reason) {
            case DENYREASON_FAIL: {
                if ("name/password".equals(authenticate)){
                    throw new JspTagException("Logon of with " + logon.get(0) + " failed." +
                                              (pwd == null ? " (no password given)" : " (wrong password)"));
                } else {
                    throw new JspTagException("Non name/password authentication failed");
                }
            }
            case DENYREASON_RANKTOOLOW: {
                throw new JspTagException("Rank too low");               
            }
            default: throw new JspTagException("Denied for unknown reason");
            }
        }
    }

    /**
     * Performs logging in the bridge and sets the cloud variable, using the provided Map argument.
     *
     * @return SKIP_BODY on fail and EVAL_BODY_BUFFERED otherwise.
     *
     */
    private final int doLogin(Map user) throws JspTagException {
        log.debug("Username found. logging in");
        try {
            cloud = getDefaultCloudContext().getCloud(cloudName, authenticate, user);
            // ok, logging on work, now check rank if necessary
            if (rank != null) {
                Rank curRank = Rank.getRank(cloud.getUser().getRank());
                if (curRank.getInt() < rank.getInt()) {
                  if (log.isDebugEnabled()) log.debug("logged on, but rank of user is too low (" + cloud.getUser().getRank() + ". log out first.");
                    cloud = null;
                    if (session != null) {
                        session.removeAttribute(getSessionName());
                    }
                    return deny(DENYREASON_RANKTOOLOW);                   
                }                
            }      
            return EVAL_BODY_BUFFERED;
        } catch (BridgeException e) {
            return deny(DENYREASON_FAIL);
        }
    }
    /**
     * Makes a logged-in cloud.
     * @return SKIP_BODY if failed to do so. EVAL_BODY_BUFFERED otherwise.
     */

    private final int makeCloud() throws JspTagException {
        log.debug("logging on the cloud...");
        Map user = null;
        // check how to log on:
        if (method == METHOD_HTTP) {           
            user = new HashMap();
            if (doHTTPAuthentication(user) == SKIP_BODY) return SKIP_BODY;
        } else if (loginpage != null) {
            user = new HashMap();
            if (doLoginPage(user) == SKIP_BODY) return SKIP_BODY;
        } else if (logon != null && pwd != null) { 
            user = new HashMap();
            user.put("username", logon.get(0));
            user.put("password", pwd);
        }

        // do the MMCI cloud logging on
        if (user != null) {
            if (doLogin(user) == SKIP_BODY) return SKIP_BODY;
        } else {
            log.debug("no login given, creating anonymous cloud");
            // no logon, create an anonymous cloud.
            setAnonymousCloud();
        }
        
        if (cloud == null) { // stil null, give it up then...
            log.debug("Could not create Cloud.");
            // throw new JspTagException("Could not create cloud (even not anonymous)");
            return SKIP_BODY;
        } else {
            if (session != null) {
                session.setAttribute(getSessionName(), cloud);
            }
        } 
        return EVAL_BODY_BUFFERED;
    }

    /**
     * request and response can be determined once a page..
     */

    public void setPageContext(PageContext pc) {
        super.setPageContext(pc);
        request  = (HttpServletRequest) pageContext.getRequest();
        response = (HttpServletResponse)pageContext.getResponse();
        
    }

    /**
     *  Sets the cloud variable considering all requirements. SKIP_BODY if this can not be done.
     *
     */
    public int doStartTag() throws JspTagException {
        checkLocale();       
        logon = logonatt != null ? StringSplitter.split(logonatt) : null;
        if (logon != null && logon.size() == 0) logon = null;

        if (checkReuse())     return evalBody(); 
        if (checkAnonymous()) return evalBody();
        if (checkAsis())      return evalBody();
        setupSession();
        if(log.isDebugEnabled()) log.debug("startTag " + cloud);
        if (checkLogoutLoginPage()) {
            // TODO: find a better page to redirect to!
            try {
                String url = request.getRequestURI();
                response.sendRedirect(url);
            }
            catch(java.io.IOException ioe) {
                throw new JspTagException(Logging.stackTrace(ioe));
            }
            return SKIP_BODY;
        }
        if (checkLogoutMethod()) return evalBody();
        if (cloud != null) checkValid();
        if (cloud != null) checkCloud();
        if (cloud == null) { 
            if (makeCloud() == SKIP_BODY) { // we did't have a cloud, or it was not a good one:
                return SKIP_BODY;
            }
        }
        pwd = null;
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
