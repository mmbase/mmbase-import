/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util;

/**
 * @javadoc
 * @author vpro
 * @application SCAN - used for authentication by JamesServlet
 * @deprecated should be done by implementing and using the MMBase security Authorization
 * @version $Id$
 */
public class HttpAuth {
    private static org.mmbase.util.logging.Logger log = org.mmbase.util.logging.Logging.getLoggerInstance(HttpAuth.class.getName());

    private static org.mmbase.module.core.MMBase mmbase = org.mmbase.module.core.MMBase.getMMBase();

    private static String remoteAuthenticationHost = null;
    private static String remoteAuthenticationPage = null;
    private static int remoteAuthenticationPort = 80;

    // Initializes HttpAuth by reading AUTH401URL from the mmbaseroot.xml file.
    static {
        String tmp = mmbase.getInitParameter("AUTH401URL");
        if (tmp != null && !tmp.equals("")) {
            HttpAuth.setLocalCheckUrl(tmp);
        }
    }

    /**
     * With a given mimeline, the username and password will be retrieved, and with it
     * there will be looked if it is an valid login. If it is a valid login, with a rank higher
     * or equals as Rank::BASICUSER, it will return a userid, otherwise null.
     * @param mimeline The mimeline of the request
     * @return a userid for the given user, of <code>null</code> when something goes wrong
     */
    public static String checkUser(String mimeline) {
        String user_password = org.mmbase.util.Encode.decode("BASE64", mimeline.substring(6));
        java.util.HashMap userInfo = new java.util.HashMap();
        java.util.StringTokenizer t = new java.util.StringTokenizer(user_password, ":");
        if (t.countTokens() == 2) {
            userInfo.put("username", t.nextToken());
            userInfo.put("password", t.nextToken());
        }
        org.mmbase.security.UserContext user = null;
        try {
            user = mmbase.getMMBaseCop().getAuthentication().login("name/password", userInfo, null);
        }
        catch(org.mmbase.security.SecurityException se) {
            log.warn("user login of name: '" + userInfo.get("username") + "' failed("+se+")");
            return null;
        }
        // when login failed, or when it was an anonymous user, it will not work...
        if (user == null || user.getRank().getInt() < org.mmbase.security.Rank.BASICUSER_INT) {
            log.warn("user login of name: '" + userInfo.get("username") + "' failed(invalid)");
            return null;
        }
        return user.getIdentifier();
    }

    /**
     * Authenticates a user, If the user cannot be authenticated a login-popup will appear
     * @todo remove logging on using remoteAuthenticationHost - this should be moved to the security
     *       layer
     * @param server server-account. (for exameple 'film' or 'www')
     * @param level loginlevel. (for example 'Basic' or 'MD5')
     * @return username foan exception will be thrown.
     * @exception AuthorizationException if the authorization fails.
     * @exception NotLoggedInException if the user hasn't logged in yet.
     */
    public static String getAuthorization(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse res,String server, String level) throws AuthorizationException, NotLoggedInException {
        if (log.isDebugEnabled()) {
            log.debug("server: " + server + ", level: " + level);
        }
        String mimeline = getMimeline(req);
        if (mimeline == null) {
            log.info("page " + req.getRequestURI() + " is secure, and user not yet authenticated");
            res.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
            res.setHeader("WWW-Authenticate","Basic realm=\""+server+"\"");
            throw new NotLoggedInException("Not logged in Exception");
        }
        if(remoteAuthenticationHost == null) {
            // use local validating
            String username = checkUser(mimeline);
            if (username == null) {
                log.service("Logging in of user failed");
                res.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                res.setHeader("WWW-Authenticate","Basic realm=\""+server+"\"");
                throw new AuthorizationException("User authorization failed");
            } else {
                log.debug("User " + username + " succesfully logged in");
                return username;
            }
        }
        else {
            try {
                // use remote validation
                java.util.StringTokenizer t = new java.util.StringTokenizer(org.mmbase.util.Encode.decode("BASE64", mimeline.substring(6)), ":");
                String username = t.nextToken();
                String password = t.nextToken();

                java.net.Socket socket = new java.net.Socket(remoteAuthenticationHost, remoteAuthenticationPort);
                java.io.BufferedInputStream instream = new java.io.BufferedInputStream(socket.getInputStream());
                java.io.BufferedOutputStream outstream = new java.io.BufferedOutputStream(socket.getOutputStream());

                // vpro???
                write(outstream,"GET "+remoteAuthenticationPage+" HTTP/1.0\nContent-Type: vpro/ballyhoo\nUser-Agent: VPRO/James remote password check\nAuthorization: "+password+"\n\n");
                String result = read(instream);
                if (result.indexOf("401") < 0) {
                    // 401 not found, thus granted..
                    return username;
                }
                else {
                    // was not granted...
                    String msg = "User authorization failed(server "+remoteAuthenticationHost+":"+remoteAuthenticationPort+remoteAuthenticationPage+")";
                    throw new AuthorizationException(msg);
                }
            }
            catch(java.net.UnknownHostException uhe) {
                String msg = "host not found " + uhe;
                log.error(msg);
                throw new AuthorizationException(msg);
            }
            catch(java.io.IOException ioe) {
                String msg = "communication failure " + ioe;
                log.error(msg);
                throw new AuthorizationException(msg);
            }
        }
    }

    /**
     * getRemoteUser
     * @param req
     * @return the remote user
     */
    public static String getRemoteUser(javax.servlet.http.HttpServletRequest req) {
        return checkUser(getMimeline(req));
    }

    /**
     * getRemoteUser
     * @param sp
     * @return the remote user
     */
    public static String getRemoteUser(PageInfo sp) {
        return getRemoteUser(sp.req);
    }


    /**
     * Sets the url on which an authentication has to be checked.
     * @param url
     */
    public static void setLocalCheckUrl(String url) {
        if (remoteAuthenticationHost != null) {
            log.error("check url was already set ('" + remoteAuthenticationHost + "')");
            return;
        }
        int pos=url.indexOf('/');
        if (pos!=-1) {
            remoteAuthenticationHost = url.substring(0,pos);
            remoteAuthenticationPage = url.substring(pos);
        }
        else {
            remoteAuthenticationHost = url;
            remoteAuthenticationPage = "/";
        }
        pos = remoteAuthenticationHost.indexOf(':');
        if (pos!=-1) {
            try {
                remoteAuthenticationPort = Integer.parseInt(remoteAuthenticationHost.substring(pos+1));
            }
            catch (Exception e) {
                log.error(e.toString());
            }
            remoteAuthenticationHost = remoteAuthenticationHost.substring(0,pos);
        }
    }

    private static String getMimeline(javax.servlet.http.HttpServletRequest req) {
        return (req.getHeader("Authorization"));
    }

    private static int write(java.io.BufferedOutputStream out,String line) {
        try {
            out.write(line.getBytes());
            out.flush();
        } catch(java.io.IOException e) {
            return -1;
        }
        return line.length();
    }

    private static String read(java.io.BufferedInputStream in) {
        StringBuffer str=new StringBuffer();
        int rtn=0;
        do {
            try {
                rtn=in.read();
            }
            catch(java.io.IOException e) {
                return null;
            }
            if (rtn==-1) {
                return null;
            }
            str.append((char)rtn);
        }
        while(rtn!='\n');
        return str.toString();
    }
}
