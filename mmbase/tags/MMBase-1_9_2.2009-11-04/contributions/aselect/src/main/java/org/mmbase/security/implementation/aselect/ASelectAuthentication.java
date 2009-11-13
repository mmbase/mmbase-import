/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
*/
package org.mmbase.security.implementation.aselect;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.module.core.MMBase;
import org.mmbase.security.*;
import org.mmbase.security.SecurityException;
import org.mmbase.bridge.CloudContext;

import org.mmbase.security.implementation.cloudcontext.builders.*;
import org.mmbase.security.implementation.cloudcontext.CloudContextAuthentication;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.http.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;

import org.mmbase.util.FileWatcher;
import org.mmbase.util.XMLErrorHandler;
import org.mmbase.util.XMLEntityResolver;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.functions.*;

import org.mmbase.util.transformers.Url;
import org.mmbase.util.transformers.CharTransformer;

import org.aselect.system.communication.client.IClientCommunicator;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ASelect Authentication implementation for MMBase based on the ASelect agent. See
 * http://aselect.surfnet.
 *
 * @author Arnout Hannink     (Alfa & Ariss)
 * @author Michiel Meeuwissen (Publieke Omroep Internet Services)
 *
 * @version $Id$
 * @since  MMBase-1.7
 */
public class ASelectAuthentication extends CloudContextAuthentication {

    private static final Logger log = Logging.getLoggerInstance(ASelectAuthentication.class);



    private static final CharTransformer paramEscaper = new Url(Url.PARAM_ESCAPE);

    /**
     * When logged in, an object with this name is stored in the session. That is, it is postfixed
     * by the a-select-application.
     */
    private static final String SESSION_ASELECT_RESPONSE = "aselect_server_response";

    /**
     * the (default) application id as registered in the A-Select Server
     */
    private static final String defaultApplication = "mmbase";

    public static final String XSD = "aselectauthentication.xsd";
    public static final String XSD_LOC = "http://www.mmbase.org/xmlns/aselectauthentication.xsd";
    public static final String NAMESPACE = "http://www.mmbase.org/xmlns/aselect";

    static {
        XMLEntityResolver.registerSystemID(XSD_LOC, XSD, ASelectAuthentication.class);
    }


    /**
     * Whether to use the A-Select agent or communicate with ASelectServer directly.
     */
    private boolean useAgent = true;

    /**
     * The address of the A-Select Agent. This should always be localhost, so this is a constant.
     */
    private static final String agentAddress = "127.0.0.1";

    /**
     * The communication port of the A-Select Agent. Configurable, but specifying the agent's config file itself.
     */
    private int agentPort = 1495; // default

    /**
     * If communicating directly with the server, this is the object which does it.
     */
    private IClientCommunicator communicator = null;

    /**
     * ASelect server address, needed when not using agent.
     */
    private String aselectServer = null; // as yet undetermined "https://localhost/aselectserver/server"; // default

    private String aselectLogout = null; // as yet undetermined When remains null, there is not aselectlogout method

    /**
     * ASelect server id, needed when not using agent.
     */
    private String aselectServerId = null; // as yet undetermined e.g. aselectserver1


    /**
     * List of possible ASelect applications id's.
     */
    private List<String> applications = new ArrayList<String>();


    /**
     * Whether to use 'Cloud context' compatible User objects. If so, then you can use the cloud
     * context security implementation's authorization, so you have groups, ranks and users,
     * represented by MMBase objects.
     *
     * Since A-Select defines the users, a user object is automaticly created if one is
     * missing. Rights can then be related to this user object. The password field of course is not
     * used then.
     */
    private boolean useCloudContext = false;

    /**
     * If not using cloud context security users, all users are 'basic' user on default, but it can
     * be overridden in the property file 'accounts.properties', stored in this object.
     */
    private Properties knownUsers;

    /**
     * If not using cloud context security users, you can still have extra ranks. Using the property
     * file 'ranks.properties', stored in this object.
     */
    private Properties registeredRanks = null;

    /**
     * I hate XML
     */
    private String getNodeTextValue(Node n) {
        NodeList nl = n.getChildNodes();
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < nl.getLength(); i++) {
            Node textnode = nl.item(i);
            if (textnode.getNodeType() == Node.TEXT_NODE) {
                res.append(textnode.getNodeValue().trim());
            } else if (textnode.getNodeType() == Node.CDATA_SECTION_NODE) {
                res.append(textnode.getNodeValue());
            }
        }
        return res.toString();
    }

    /**
     * A-Select 1.3 backwards compatibility
     */
    protected void configureByProperties(String agentConf) {
        Properties config = new Properties();
        useAgent = true;
        InputStream in;
        try {
            // absolute
            in = new FileInputStream(agentConf);
        } catch (FileNotFoundException nfe) {
            // relative
            in = MMBaseCopConfig.securityLoader.getResourceAsStream(agentConf);
        }
        if (in == null) {
            log.error("No agent-configuration " + agentConf + " could be found");
            return;
        }
        try {
            config.load(in);
        } catch (IOException ioe) {
            log.error(ioe);
        }
        try {
            agentPort    = new Integer(config.getProperty("serviceport")).intValue();
        } catch (NumberFormatException nfe) {
            log.warn("Error reading serviceport: " + config.getProperty("serviceport") +
                     " read from "  + agentConf + ", using port " + agentPort);
        }
        String firstServer = config.getProperty("aselect.server.1");
        aselectServer = config.getProperty("aselect.server." + firstServer);
    }

    protected void load() {
        log.service("Loading A-Select authentication");
        // get some parameters
        try {
            log.service("Reading resource: " + configResource);
            if (configResource.endsWith(".xml")) {
                DocumentBuilder db = DocumentReader.getDocumentBuilder(true, true, new XMLErrorHandler(), new XMLEntityResolver(true, ASelectAuthentication.class));
                InputSource is = MMBaseCopConfig.securityLoader.getInputSource(configResource);
                if (is != null) {
                    Document doc = db.parse(is);
                    NodeList nl = doc.getElementsByTagName("aselectauthentication");
                    if (nl.getLength() == 0) { // The configuration _is_ the agent's configuration, this is old style configuration. It might be good enough.
                        useAgent = true;
                        NodeList nl1 = doc.getElementsByTagName("serviceport");
                        agentPort = Integer.parseInt(getNodeTextValue(nl1.item(0)));

                    } else {

                        NodeList nl0 = doc.getElementsByTagName("agent");
                        if (nl0.getLength() > 0) {
                            useAgent = true;
                            String agentConfiguration = getNodeTextValue(nl0.item(0));
                            log.service("Reading agent configuration " + agentConfiguration);
                            if (agentConfiguration.endsWith(".xml")) {
                                DocumentBuilder db1 = DocumentReader.getDocumentBuilder(false, false, null, null);
                                Document doc1 = db1.parse(new InputSource(agentConfiguration));
                                NodeList nl1 = doc1.getElementsByTagName("serviceport");
                                agentPort = Integer.parseInt(getNodeTextValue(nl1.item(0)));
                            } else { // old style agent configuration (with a property file).
                                configureByProperties(agentConfiguration);
                            }
                        } else {
                            useAgent = false;
                            NodeList nl1 = doc.getElementsByTagName("protocol");

                            String prot = getNodeTextValue(nl1.item(0));
                            if (prot.equals("raw")) {
                                communicator = new org.aselect.system.communication.client.raw.RawCommunicator(new MMBaseSystemLogger());
                            } else if (prot.equals("soap11")) {
                                communicator = new org.aselect.system.communication.client.soap11.SOAP11Communicator("ASelect", new MMBaseSystemLogger());
                            } else if (prot.equals("soap12")) {
                                communicator = new org.aselect.system.communication.client.soap12.SOAP12Communicator("ASelect", new MMBaseSystemLogger());
                            } else {
                                log.error("Protocol '" + prot + "' is not 'raw', 'soap11' or 'soap12', taking 'raw'");
                                communicator = new org.aselect.system.communication.client.raw.RawCommunicator(new MMBaseSystemLogger());
                            }
                            nl1 = doc.getElementsByTagName("uri");
                            aselectServer = getNodeTextValue(nl1.item(0));

                            nl1 = doc.getElementsByTagName("aselect-server-id");
                            aselectServerId = getNodeTextValue(nl1.item(0));

                        }
                        nl0 = doc.getElementsByTagName("applications");

                        applications.clear();
                        if (nl0 != null && nl0.item(0) != null) {
                            NodeList apps = nl0.item(0).getChildNodes();
                            for (int i = 0 ; i < apps.getLength() ; i++) {
                                Node app = apps.item(i);

                                if (app.getNodeName().equals("application")) {
                                    String a = getNodeTextValue(app);
                                    if (! applications.contains(a)) {
                                        applications.add(a);
                                    }
                                } else if (app.getNodeName().equals("server-config")) {
                                    InputSource aselect = MMBaseCopConfig.securityLoader.getInputSource(getNodeTextValue(app));
                                    DocumentBuilder nonValidatingDb = DocumentReader.getDocumentBuilder(false, null, null);
                                    Document aselectDoc = nonValidatingDb.parse(aselect);
                                    NodeList nl1 = aselectDoc.getElementsByTagName("application");
                                    for (int j = 0; j < nl1.getLength() ; j++) {
                                        Node configApp = nl1.item(j);
                                        String a = getNodeTextValue(configApp.getAttributes().getNamedItem("id"));
                                        if (! applications.contains(a)) {
                                            applications.add(a);
                                        }
                                    }
                                }
                            }
                        }
                        log.service("Found A-Select applications " + applications);

                        nl0 = doc.getElementsByTagName("logouturi");
                        if (nl0 != null && nl0.item(0) != null) {
                            aselectLogout = getNodeTextValue(nl0.item(0));
                        } else {
                            aselectLogout = null;
                        }
                    }

                } else {
                    log.warn("Resource '" + configResource + "' not found");
                }
            } else {
                // old style agent configuration (with a property file) (in previous version of this class, there was no config.xml).
                configureByProperties(configResource);
            }

            useCloudContext = manager.getAuthorization() instanceof org.mmbase.security.implementation.cloudcontext.Verify;

            if (useAgent) {
                log.info("Using aselect agent on port " + agentPort);
            } else {
                log.info("Using aselect-server " + aselectServerId + " on " + aselectServer + " with protocol " + communicator);
            }
            configWatcher.add("accounts.properties");
            InputStream accounts =  MMBaseCopConfig.securityLoader.getResourceAsStream("accounts.properties");
            log.service("Reading resource security/accounts.properties");
            knownUsers = new Properties();
            if (accounts != null) {
                knownUsers.load(accounts);
            }
            log.service("Found " + knownUsers.size() + " known accounts (unknown accounts will be considered 'basic user')");

            if (useCloudContext) {
                log.info("Detected cloud context authorization, will supply compatible User objects (associated with 'mmbaseusers' MMBase object).");
            } else {
                log.info("Using dumb user objects (can be used with Owner authorization)");

                configWatcher.add("ranks.properties");
                InputStream ranks =  MMBaseCopConfig.securityLoader.getResourceAsStream("ranks.properties");
                log.service("Reading file: security/ranks.properties");


                if (registeredRanks != null) {
                    Iterator i = registeredRanks.keySet().iterator();
                    while (i.hasNext()) {
                        String r = (String) i.next();
                        Rank.deleteRank(r);
                    }
                }
                registeredRanks = new Properties();
                try {
                    if (ranks != null) {
                        registeredRanks.load(ranks);
                    }
                } catch (Exception e) {

                }
                Iterator i = registeredRanks.keySet().iterator();
                while (i.hasNext()) {
                    String r = (String) i.next();
                    try {
                        int    j = new Integer(registeredRanks.getProperty(r)).intValue();
                        Rank.createRank(j, r);
                    } catch (Exception e) {
                        log.warn(e.toString());
                    }
                }
            }
        } catch (IOException e) {
            log.error("IOException " + e.getMessage(), e);
        } catch (org.xml.sax.SAXException e) {
            log.error("SAXException " + e.getMessage(), e);
        }


    }


    protected UserContext getAnonymousUser() {
        if (useCloudContext) {
            Users users = Users.getBuilder();
            return new ASelectCloudContextUser(users.getAnonymousUser(), getKey(), "anonymous");
        } else {
            return new ASelectUser("anonymous", Rank.ANONYMOUS, getKey(), "anonymous");
        }
    }

    protected Rank getRank(String userName) {
        Rank   rank = Rank.BASICUSER;
        String rankString = knownUsers.getProperty(userName);
        if (rankString != null && ! rankString.equals("")) {
            rank = Rank.getRank(rankString);
            if (rank == null) {
                log.warn("Unknown rank '" + rankString + "'");
                rank = Rank.ANONYMOUS;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Logging in user '" + userName + "' (rank: '" + rankString + "' -> " + rank + ")");
        }
        return rank;
    }

    // javadoc inherited
    public UserContext login(String application, Map loginInfo, Object[] parameters) throws SecurityException {

        if (log.isDebugEnabled()) {
            log.debug("app: '" + application + "' loginInfo: " + loginInfo);
        }

        if ("class".equals(application)) {
            org.mmbase.security.classsecurity.ClassAuthentication.Login li = org.mmbase.security.classsecurity.ClassAuthentication.classCheck("class");
            if (li == null) {
                throw new SecurityException("Class authentication failed  '" + application + "' (class not authorized)");
            }
            String userName = (String) li.getMap().get(PARAMETER_USERNAME.getName());
            String rank     = (String) li.getMap().get(PARAMETER_RANK.getName());
            if (useCloudContext) {

                Rank r;
                if (rank != null) {
                    r = Rank.getRank(rank);
                    if (userName == null) userName = r.toString();
                } else {
                    rank = knownUsers != null && userName != null ? knownUsers.getProperty(userName) : null;
                    r = rank != null ? Rank.BASICUSER : Rank.getRank(rank);
                }
                if (userName == null) throw new SecurityException("Should specify at least a username or a rank for class authenication (given " + li + ":" + li.getMap() + ")");
                return new ASelectCloudContextUser(userName, getKey(), "class", r.toString());
            } else {
                if (userName == null) {
                    if (rank != null) {
                        userName = rank;
                    }
                }
                Rank r = null;
                if (rank == null) {
                    r = getRank(userName);
                } else {
                    r = Rank.getRank(rank);
                }
                return new ASelectUser(userName, r, getKey(), "class");
            }
        }

        if (loginInfo == null) {
            return getAnonymousUser();
        }

        HttpServletRequest request   = (HttpServletRequest)  loginInfo.get(Parameter.REQUEST.getName());
        HttpServletResponse response = (HttpServletResponse) loginInfo.get(Parameter.RESPONSE.getName());



        if ("anonymous".equals(application)) {

            if (Boolean.TRUE.equals(loginInfo.get("logout"))) {
                String app = (String) loginInfo.get("authenticate");
                if (app == null) {
                    //try default..
                    if (applications.size() < 1) {
                        throw new RuntimeException ("No 'authenticate' given and no default defined. Don't know how to log in. (Perhaps the A-Select configuration file was not found?)");
                    }
                    app = applications.get(0);

                    // throw new RuntimeException("No authenticate given");
                }
                if (request != null && response != null) {
                    logout(request, response, app);  // logout in aselect-server
                    log.debug("Trying to remove the aselectticket cookie " + app);
                }
                // Hmm, I don't really understand why I cannot find the application back with the ticket or so.
                //response.sendRedirect(aselectServer);
                //return null;
            }
            return getAnonymousUser();
        }
        List userNames     = (List)loginInfo.get(PARAMETER_USERNAMES.getName());

        if (log.isDebugEnabled()) {
            log.debug("Users " + userNames);
        }
        Rank requiredRank  = (Rank)loginInfo.get(PARAMETER_RANK.getName());


        UserContext newUser;
        // make connection  to A-Select agent, and create an ASelectUser object
        //try {
        if (application == null || application.equals("")) {
            application = "mmbase";
        }

        String requestedUser;
        if (userNames != null && userNames.size() == 1) {
            requestedUser = (String) userNames.get(0);
            if (requestedUser.equals("")) requestedUser = null;
            userNames = null;
        } else {
            requestedUser = null;
        }

        if (useAgent) {
            if (authentication(request, response, application, requestedUser)) {
                String userName = getASelectUserId(request);
                if (useCloudContext) {
                    String r = knownUsers.getProperty(userName);
                    newUser = new ASelectCloudContextUser(userName, getKey(), application, r);
                } else {
                    Rank rank = getRank(userName);
                    newUser = new ASelectUser(userName, rank, getKey(), application);
                }
            } else {
                log.debug("User not fully authenticated and has been redirected to A-Select Agent.");
                return null;
                // throw new SecurityException("user has been redirected to A-Select");
            }
            /*
              } catch (Exception e) {
              throw new SecurityException(e.getMessage(), e);
              }
            */
        } else {
            try {
                Map aselectResponse = authenticate(request, response, application, requestedUser);
                if (aselectResponse == null) {
                    log.service("User needs authentication and has been redirected to A-Select Server.");
                    return null;
                } else {
                    String userName = (String) aselectResponse.get("uid");
                    if(userNames != null && !userNames.contains(userName)) {
                        if (log.isDebugEnabled()) {
                            log.debug("User " + userName + " not contained by " + userNames + ", logging out again");
                        }
                        logout(request, response, application);
                        // try again
                        return null;
                    }
                    if (useCloudContext) {
                        String r = knownUsers.getProperty(userName);
                        newUser = new ASelectCloudContextUser(userName, getKey(), application, r);
                    } else {
                        Rank rank = getRank(userName);
                        newUser = new ASelectUser(userName, rank, getKey(), application);
                    }
                    if (requiredRank != null && newUser.getRank().getInt() < requiredRank.getInt()) {
                        if (log.isDebugEnabled()) {
                            log.debug("User " + userName + "'s rank too low (" + newUser.getRank() + " < " + requiredRank + ")");
                        }
                        logout(request, response, application);
                        return null;
                    }
                }
            } catch (org.aselect.system.exception.ASelectCommunicationException asce) {
                throw new RuntimeException(asce);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }

        }

        if (userNames != null && !userNames.contains(newUser.getIdentifier())) {
            throw new SecurityException("Logged on as wrong user ('" + newUser.getIdentifier() + "', but must be one of " + userNames + ")");
        }

        return newUser;
    }

    // javadoc inherited
    public boolean isValid(UserContext userContext) throws SecurityException {
        if (useCloudContext) {
            if (! (userContext instanceof ASelectCloudContextUser)) {
                log.debug("Changed to other security implementation");
                return false;
            }
            ASelectCloudContextUser user = (ASelectCloudContextUser) userContext;
            if (user.getKey() != getKey()) {
                log.service(user.toString() + " was NOT valid (different unique number, " + user.getKey() + " != " + getKey());
                return false;
            }
            if (! user.isValidNode()) {
                log.service(user.toString() + " was NOT valid (node " + user.getNode());
                return false;
            }
            log.debug(user.toString() + " was valid");
            return true;
        } else {
            return ((ASelectUser) userContext).key == getKey();
        }
    }

    /**
     * Returns an URL for the the current request.
     */
    protected String getAppUrl(HttpServletRequest request, HttpServletResponse response) {
        StringBuffer url   = request.getRequestURL();
        Iterator i = request.getParameterMap().entrySet().iterator();
        char sep = '?';
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            String key = (String) entry.getKey();
            String[] values = (String[]) entry.getValue();
            for (int j = 0; j < values.length; j++) {
                url.append(sep).append(key).append('=').append(paramEscaper.transform(values[j]));
                sep = '&';
            }
        }

        return response.encodeURL(url.toString());
        /*
        try {

            //return response.encodeURL(url.toString());
            //return java.net.URLEncoder.encode(response.encodeURL(url.toString()), "UTF-8");
        } catch (java.io.UnsupportedEncodingException uee) {
            // could not happen.
            return response.encodeURL(url.toString());
        }
        */
    }

    protected Map authenticate(HttpServletRequest request, HttpServletResponse response, String application, String user)
        throws org.aselect.system.exception.ASelectCommunicationException, IOException {

        if (request == null) throw new SecurityException("No request given");
        HttpSession session = request.getSession(false);
        if (session != null) {
            Map aselectServerResponse = (Map) session.getAttribute(SESSION_ASELECT_RESPONSE + "_" + application);
            if (aselectServerResponse != null) {
                return aselectServerResponse;
            }
        }

        String credentials = request.getParameter("aselect_credentials");
        if (credentials == null) { // no credentials found, redirecting to server
            Hashtable parameters = new Hashtable();  // don't ask me why it needs to be synchronized.
            parameters.put("request", "authenticate");
            parameters.put("app_url",  getAppUrl(request, response));
            parameters.put("app_id",   application);
            parameters.put("a-select-server",  aselectServerId);
            if (log.isDebugEnabled()) {
                log.debug("Sending " + parameters + " to " + aselectServer);
            }
            Map aselectServerResponse = communicator.sendMessage(parameters, aselectServer);
            if (log.isDebugEnabled()) {
                log.debug("response " + aselectServerResponse);
            }
            String resultCode = (String) aselectServerResponse.get("result_code");
            if (resultCode == null) {
                throw new RuntimeException("Got no result from A-Select server!");
            }
            if (! resultCode.equals("0000")) {
                throw new RuntimeException("ASelect Error: '" + ASelectErrors.getMessage(resultCode) + "' for application " + application);
            }
            // redirect.
            String url = java.net.URLDecoder.decode((String) aselectServerResponse.get("as_url"), "UTF-8");
            String rid = (String) aselectServerResponse.get("rid");

            if (user == null) {
                log.debug("No user");
                response.sendRedirect(url + "&rid=" + rid + "&a-select-server=" + aselectServerId);
            } else {
                log.debug("User " + user + " requested");
                url = url.replaceAll("login1", "login2");
                response.sendRedirect(url + "&rid=" + rid + "&a-select-server=" + aselectServerId + "&user_id=" + user);
            }
            return null;
        } else {
            Hashtable parameters = new Hashtable();  // don't ask me why it needs to be synchronized.
            parameters.put("request",          "verify_credentials");
            parameters.put("a-select-server",  aselectServerId);
            parameters.put("aselect_credentials",  credentials);
            parameters.put("rid",  request.getParameter("rid"));
            if (log.isDebugEnabled()) {
                log.debug("Sending " + parameters + " to " + aselectServer);
            }
            Map aselectServerResponse = communicator.sendMessage(parameters, aselectServer);
            if (log.isDebugEnabled()) {
                log.debug("response " + aselectServerResponse);
            }
            String resultCode = (String) aselectServerResponse.get("result_code");
            if (resultCode == null) {
                throw new RuntimeException("Got no result from A-Select server!");
            }
            if (! resultCode.equals("0000")) {
                throw new RuntimeException(ASelectErrors.getMessage(resultCode));
            }
            // Put stuff in the session.
            if (session == null) {
                session = request.getSession(true);
            }
            session.setAttribute(SESSION_ASELECT_RESPONSE + "_" + application, aselectServerResponse);
            return aselectServerResponse;
        }
    }


    /**
    * Agent code.
    */


    /**
     * Performs the work of authentication and session management.
     *
     * This function should be called for each request to the Servlet.<br>
     * If the user has a valid session, true will be returned and de Servlet can process the request.<br>
     * If not, this module has redirected the user to A-Select or has thrown an exception.<br>.
     * In the situation a user is not yet authenticated the Servlet wil get severel requests with authentication
     * parameters. That's why this function should be called before processing the request by the servlet.
     *
     * @param request the current HTTP request. Used to obtain the parameters
     * for authentication.
     * @param response the current HTTP response
     * @return true if the user was authenticated, false otherwise.<br>
     * If false is returned the Servlet should not write anything to the
     * client because the client was already redirected!
     * @throws ASelectException If the module could not perform the authenticate request
     */
    protected boolean authentication(HttpServletRequest request, HttpServletResponse response, String application, String requestedUser)  {

        // look if there is a valid ticket
        // if not do the authentication process
        if (verifyTicket(request, response, application, requestedUser)) {
            return true;
        } else if (verifyCredentials(request, response, requestedUser)) {
            return false; //process continues
        } else if (authenticateUser(request, response, application, requestedUser)) {
            return false; //process continues
        } else {
            throw new ASelectException("The A-Select Authentication Module has received an unknown request. Please try again.");
        }
        ///throw new ASelectException("Could not perform authentication: " + e.getMessage(),  e);
    } //end of function : authentication

    /**
     * Retrieves the A-Select User Id from the cookies.
     *
     * @param request the current HTTP request. Used to obtain the cookie(s)
     * @return The A-Select user id or null if not set
     */
    protected String getASelectUserId(HttpServletRequest request) {
        String userId = getCookie(request, "aselectuid");
        return decodeCGI(userId);
    } //end of function : getASelectUserId

    /**
     * Retrieves the A-Select Organization Id from the cookies.
     *
     * @param request the current HTTP request. Used to obtain the cookie(s)
     * @return The A-Select organization id or null if not set
     */
    protected String getASelectOrganization(HttpServletRequest request) {
        String organization = getCookie(request, "aselectorganization");
        return decodeCGI(organization);

    } //end of function : getASelectOrganization

    /**
     * Retrieves the A-Select Session Id from the cookies.
     *
     * @param request the current HTTP request. Used to obtain the cookie(s)
     * @return The A-Select session id or null if not set
     */
    protected String getASelectSessionId(HttpServletRequest request) {
        String sessionId = getCookie(request, "aselectticket");
        //   xSessionId = decodeCGI( xSessionId );
        return sessionId;
    } //end of function : getASelectSessionId


    protected void logoutASelectServer(HttpServletRequest request, HttpServletResponse response, String application) throws SecurityException {
        HttpSession session = request.getSession(false);
        if (session == null && !useAgent) throw new RuntimeException("No session found!");
        if (session.getAttribute(SESSION_ASELECT_RESPONSE + "_" + application) != null) {
            session.removeAttribute(SESSION_ASELECT_RESPONSE + "_" + application);
            if (aselectLogout != null) {
                try {
                    String redirectURL = aselectLogout + "?app_url=" + getAppUrl(request, response);
                    log.service("Redirected to " + redirectURL);
                    response.sendRedirect(redirectURL);
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            } else {
                log.service("Cannot logout on server, because no logouturi specified");
            }
        } else {
            session.removeAttribute(SESSION_ASELECT_RESPONSE + "_" + application);
            log.debug("logged out successfully");
        }
    }
    /**
     * Processes the logout of a user.
     *
     * @param request the current HTTP request
     * @param response the current HTTP response.
     */
    protected void logout(HttpServletRequest request, HttpServletResponse response, String application) throws SecurityException {

        if (useAgent) { // destroy the session on the agent.
            String uid    = getASelectUserId(request);
            String ticket = getASelectSessionId(request);

            if (log.isDebugEnabled()) {
                log.debug("Logging out uid '" + uid + "' with ticket '" + ticket + "'");
            }
            if (ticket != null) {
                transferRequest("request=kill_ticket&ticket=" + ticket + "&app_id=" + application + "&uid="  + uid);
           } else {
               log.debug("No ticket found, cannot log out");
            }
            Cookie ticketCookie = new Cookie("aselectticket", "");
            ticketCookie.setPath(request.getContextPath());
            ticketCookie.setMaxAge(0);
            response.addCookie(ticketCookie);

            Cookie uidCookie = new Cookie("aselectuid", "");
            uidCookie.setPath(request.getContextPath());
            uidCookie.setMaxAge(0);
            response.addCookie(uidCookie);

            Cookie orgCookie = new Cookie("aselectorganization", "");
            orgCookie.setPath(request.getContextPath());
            orgCookie.setMaxAge(0);
            response.addCookie(orgCookie);



        } else {
            // if no agent, cloud-tag will destroy session object, which suffices.
        }
        logoutASelectServer(request, response, application);
    } //end of function : logout

    /**
     * This function will perform the verify ticket function.
     * @return 'true' if the user has a valid A-Select ticket (at the A-Select Agent!)
     *
     * @param request the current HTTP request
     * @param response the current HTTP response.
     */
    private boolean verifyTicket(HttpServletRequest request, HttpServletResponse response, String application, String requestedUser)  {
        String ticket = getASelectSessionId(request);
        if (ticket == null) {
            log.debug("ticket is null returning ");
            return false;
        }

        String uid = getASelectUserId(request);
        if (uid == null)
            return false;

        String organization = getASelectOrganization(request);
        if (organization == null) {
            return false;
        }

        Map agentResponse = transferRequest("request=verify_ticket&ticket=" + ticket + "&app_id=" + application + "&uid=" + uid + "&organization=" + organization);

        //        } catch (Exception e) {
            //throw new Exception("The A-Select Agent could not be reached. (system code : " +
        //                         e.getMessage() + "NOTE : The A-Select agent is not started or misconfigured", e);
        // }

        String resultCode = (String) agentResponse.get("result_code");
        if (resultCode == null) {
            log.debug("No result code found");
            return false;
        }
        if(! resultCode.equals(ASelectErrors.ASELECT_NO_ERROR)) {
            log.error("ASelect error " + resultCode);
            return false;
        }

        return true;
    } //end of function : verify_ticket

    /**
     * This function will perform the verify credentials function.
     * This function will return 'true' if the credentials provided in the parametrs are correct.
     * The credentials are being set by the A-Select server
     *
     * @param request the current HTTP request
     * @param response the current HTTP response.
     */
    private boolean verifyCredentials(HttpServletRequest request, HttpServletResponse response, String requestedUser) {

        log.debug("Verifying credentials");
        String rid = request.getParameter("rid");
        if (rid == null) {
            log.debug("No rid");
            return false;
        }

        String credentials = request.getParameter("aselect_credentials");
        if (credentials == null) {
            log.debug("No credentials found");
            return false;
        }

        Map agentResponse;
        agentResponse = transferRequest("request=verify_credentials&rid=" + rid + "&aselect_credentials=" + credentials);
        log.debug("Agent response " + agentResponse);

        //throw new SecurityException("The A-Select Agent could not be reached. (system code : " + e.getMessage() + ". NOTE : The A-Select agent is not started or misconfigured", e);

        String resultCode = (String)agentResponse.get("result_code");
        if (resultCode == null) {
            throw new ASelectException("The A-Select Agent did not return a  response. Response from A-Select Agent : " + agentResponse);
        }
        if (! resultCode.equals(ASelectErrors.ASELECT_NO_ERROR)) {
            throw new ASelectException("The A-Select Agent did return a malformed response. Response from A-Select Agent : " + resultCode + " " + agentResponse);
        }

        // get the parameters to set as cookie
        String uid = (String)agentResponse.get("uid");
        if (uid == null) {
            throw new ASelectException("The A-Select Agent did return a mallformed response : missing param 'uid'.");
        }

        String organization = (String)agentResponse.get("organization");
        if (organization == null) {
            throw new ASelectException("The A-Select Agent did return a mallformed response : missinge param 'organization'.");
        }

        String ticket = (String)agentResponse.get("ticket");
        if (ticket == null) {
            throw new ASelectException("The A-Select Agent did return a mallformed response : missinge param 'ticket'.");
        }

        uid = doCGIEncode(uid);

        // set the cookies
        Cookie ticketCookie = new Cookie("aselectticket", ticket);
        ticketCookie.setPath(request.getContextPath());
        Cookie uidCookie = new Cookie("aselectuid", uid);
        uidCookie.setPath(request.getContextPath());
        Cookie orgCookie = new Cookie("aselectorganization", organization);
        orgCookie.setPath(request.getContextPath());

        response.addCookie(ticketCookie);
        response.addCookie(uidCookie);
        response.addCookie(orgCookie);

        String redirectUrl = new String(HttpUtils.getRequestURL(request) + "?" + request.getQueryString());

        log.debug("Redirecting to " + redirectUrl);
        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException ioe) {
            throw new ASelectException(ioe);
        }

        //throw new SecurityException("The system could not redirect to the following URL : " + redirectUrl + " " + e.getMessage(), e);

        log.debug("success " + uidCookie);
        return true;
    } //end of function : verify_credentials



    /**
     * This function will perform the authenticate function.
     * This function will call the agent and the redirect the user to the A-Select server.
     *
     * @param request the current HTTP request
     * @param response the current HTTP response.
     */
    private boolean authenticateUser(HttpServletRequest request, HttpServletResponse response, String application, String requestedUser) {
        log.debug("Authenticating user for aselect-application '" + application + "'");

        String params = request.getQueryString();
        if (params != null) {
            Cookie orgParamsCookie = new Cookie("aselectorgurlparams", params);
            response.addCookie(orgParamsCookie);
        }

        String appUrl = getAppUrl(request, response);
        log.debug("original url " + appUrl);


        Map agentResponse = transferRequest("request=authenticate&app_url=" + appUrl + "&app_id=" + application + (requestedUser == null ? "" : "&user_id=" + requestedUser));
        // Put stuff in the session.
        HttpSession  session = request.getSession(false);
        if (session != null) {
            session.setAttribute(SESSION_ASELECT_RESPONSE + "_" + application, agentResponse);
        }
        /*
        } catch (Exception e) {
            throw new SecurityException("The A-Select Agent could not be reached. (system code : " + e.getMessage() + "  NOTE : The A-Select agent is not started or misconfigured. " + e.getMessage(), e);
        }
        */
        String resultCode = (String) agentResponse.get("result_code");
        if (resultCode == null) {
            throw new ASelectException("The A-Select Agent did return a malformed response (no resultCode). Response from A-Select Agent : " + agentResponse);
        } else if (! resultCode.equals(ASelectErrors.ASELECT_NO_ERROR)) {
            if (resultCode.equals(ASelectErrors.ASELECT_UNKNOWN_USER) ||
                resultCode.equals(ASelectErrors.ASELECT_COULD_NOT_AUTHENTICATE_USER)) {
                String redirectUrl = new String(HttpUtils.getRequestURL(request));
                log.debug("Redirecting to " + redirectUrl);
                try {
                    response.sendRedirect(redirectUrl);
                } catch (IOException ioe) {
                    throw new ASelectException(ioe);
                }

                //throw new SecurityException("The system could not redirect to the following URL : " + redirectUrl);
            } else {
                throw new ASelectException("The A-Select Agent did return an error : " + resultCode);
            }
        }

        // get the parameters to set as cookie
        String rid = (String) agentResponse.get("rid");
        if (rid == null) {
            throw new ASelectException("The A-Select Agent did return a malformed response : missinge param 'rid'.");
        }

        String asUrl = (String) agentResponse.get("as_url");
        if (asUrl == null) {
            throw new ASelectException("The A-Select Agent did return a malformed response : missinge param 'as_url'.");
        }

        String aSelectServer = (String) agentResponse.get("a-select-server");
        if (aSelectServer == null) {
            throw new ASelectException("The A-Select Agent did return a malformed response : missinge param 'a-select-server'.");
        }

        String redirectUrl = asUrl + "&rid=" + rid + "&a-select-server=" + aSelectServer;
        log.debug("Redirecting to " + redirectUrl);
        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException ioe) {
            throw new ASelectException(ioe);
        }
        //throw new SecurityException("The system could not redirect to the following URL : " + redirectUrl + " " + e.getMessage(), e);
        return true;
    } //authenticateUser

    /**
     * This function will transfer the request to the A-Select Agent.
     *
     * @param request the request which has to be sent to the A-Select Agent.
     * @returns Map This hashtable contains the response from the Agent.
     */
    private Map transferRequest(String request)  {
        try {
            log.debug("Transferring request '" + request + "' to agent on " + agentAddress + ":" + agentPort);
            Socket socket     = new Socket(agentAddress, agentPort);
            PrintStream out   = new PrintStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(request);
            String agentResponse = in.readLine();
            Map res = convertCGIMessage(agentResponse);
            if (log.isDebugEnabled()) {
                log.debug("Got " + res + " from agent");
            }
            return res;
        } catch (IOException ioe) {
            throw new ASelectException("Could not transfer request to aselect agent " + ioe, ioe);
        }
    }

    /**
     * This function will get the cookie form the (servlet)request
     *
     * @param request the servlet request
     * @param cookieName the name of the cookie to obtain from the request.
     * @returns String The value of the cookie.
     */
    private String getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.debug("no cookies here");
            return null;
        }

        for (int i = 0; i < cookies.length; i++) {
            if (log.isDebugEnabled()) {
                log.debug("looking at cookie " + cookies[i].getName() + " with value " + cookies[i].getValue());
            }
            if (cookies[i].getName().equals(cookieName)) {
                if (log.isDebugEnabled()) {
                    log.debug("found cookie " + cookieName + " with value " + cookies[i].getValue());
                }
                return cookies[i].getValue();
            }
        }
        log.debug("failed to find cookie " + cookieName);
        return null;

    } //end of function : getCookie

    /**
     * This function will do a little CGI encoding
     * For now it is not complete implemented
     *
     *@param value The value to encode
     *@return the encoded value
     */
    private String doCGIEncode(String value) {
        if (value != null) {
            return value.replace(' ', '+');
        } else {
            return value;
        }
    }

    /**
     * This function will do a little CGI decoding
     * For now it is not completely implemented
     *
     *@param value The value to decode
     *@return the decoded value
     * hmm
     */
    private String decodeCGI(String value) {
        if (value != null) {
            String decoded = value.replace('+', ' ');
            int pos = decoded.indexOf("%2B");
            while (pos != -1) {
                decoded = decoded.substring(0, pos) + decoded.substring(pos + 3);
                pos = decoded.indexOf("%2B");
            }
            return decoded;
        } else {
            return null;
        }

    } //end of function : decodeCGI

    /**
     * This method will convert a string of <code>key=value&key=value</code>
     * etc. tuples (aka a CGI request string) into a hashtable for much easier
     * processing.<br />
     * <b>Note:</b> The key names are all converted to lowercase.
     * @todo can we not simply use request.getParameter?
     */
    public static Map convertCGIMessage(String message) {

        Map response = new HashMap();
        if (message != null) {
            StringTokenizer st = new StringTokenizer(message, "&");

            while (st.hasMoreElements()) {
                String token = (String)st.nextElement();
                if (!token.trim().equals("")) {
                    int pos = token.indexOf('=');
                    if (pos != -1) {
                        String key = token.substring(0, pos);
                        String value;
                        try {
                            value = token.substring(pos + 1);
                        } catch (Exception e) {
                            value = "";
                        }

                        if (key != null && value != null) {
                            response.put(key.toLowerCase(), value);
                        }
                    }
                }
            }
        }
        return response;
    }

    public int getDefaultMethod(String protocol) {
        log.debug("protocol : " + protocol);
        if (protocol == null || protocol.substring(0, 4).equalsIgnoreCase("HTTP")) {
            return AuthenticationData.METHOD_SESSIONDELEGATE;
        } else {
            return AuthenticationData.METHOD_DELEGATE;
        }
    }
    public String[] getTypes(int method) {
        int size = applications.size() + 2;
        String[] result = new String[size];
        if (method == AuthenticationData.METHOD_ASIS) {
            result[0] = "anonymous";
            result[1] = "class";
            System.arraycopy(applications.toArray(result), 0, result, 2, applications.size());
        } else {
            System.arraycopy(applications.toArray(result), 0, result, 0, applications.size());
            result[size - 2] = "anonymous";
            result[size - 1] = "class";
        }
        return result;
    }

    protected final Parameter[] CREDENTIALS = new Parameter[] { new Parameter(Parameter.REQUEST,  true),
                                                                new Parameter(Parameter.RESPONSE, true),
                                                                new Parameter.Wrapper(PARAMETERS_USERS) };
    protected final Parameter[] LOGOUT      = new Parameter[] { new Parameter(Parameter.REQUEST, true),
                                                                new Parameter(Parameter.RESPONSE, true),
                                                                new Parameter.Wrapper(PARAMETERS_ANONYMOUS) };
    public Parameters createParameters(String application) {
        if (application.equals("class")) {
            return Parameters.VOID;
        } else if (application.equals("anonymous")) {
            return new Parameters(LOGOUT);
        } else {
            return new Parameters(CREDENTIALS);
        }

    }

    public static void main(String[] args) {
    }

}
