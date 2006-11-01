/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
*/
package org.mmbase.security.implementation.aselect;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.security.*;
import org.mmbase.security.SecurityException;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.http.*;

import org.mmbase.util.FileWatcher;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ASelect Authentication implementation for MMBase based on the ASelect agent. See
 * http://aselect.surfnet.
 *
 * @author Arnout Hannink     (Alfa & Ariss)
 * @author Michiel Meeuwissen (Publieke Omroep Internet Services)
 *
 * @version $Id: ASelectAuthentication.java,v 1.2 2006-11-01 10:03:45 mmeeuwissen Exp $
 * @since  MMBase-1.7
 */
public class ASelectAuthentication extends Authentication
{

    private static final Logger log = Logging.getLoggerInstance(ASelectAuthentication.class);

    private static final String KEY_REQUEST   = "request";
    private static final String KEY_RESPONSE  = "response";
    private static final String KEY_USERNAMES = "usernames";

    /**
     * the (default) application id as registered in the A-Select Server
     */
    private static final String defaultApplication = "mmbase";

    /**
     * the address of the A-Select Agent
     */
    private static final String agentAddress = "127.0.0.1";

    /**
     * the communication port of the A-Select Agent
     */
    private int agentPort = 1495; // default

    private Properties knownUsers;
    private Properties registeredRanks = null;

    /**
     * ASelect server address
     */
    private String aselectServer = "https://ext1.alfa-ariss.com/aselectserver/server"; // default



    private FileWatcher fileWatcher = null;



    // javadoc inherited
    protected void load()
    {
        log.service("Loading A-Select authtenication");
        if (fileWatcher == null)
        {
            fileWatcher = new ConfigurationWatcher();
            //fileWatcher.add(configFile);
            //fileWatcher.add(new File(MMBaseContext.getConfigPath() + File.separator + "security" + File.separator + "accounts.properties"));
            fileWatcher.add(new File(MMBaseContext.getConfigPath() + File.separator + "security" + File.separator + "ranks.properties"));
            fileWatcher.setDelay(10 * 1000);
            fileWatcher.start();
        }
        configure();
    }



    protected void configure()
    {
        // get some parameters

        Properties config = new Properties();
        try {
            log.service("Reading file: " + configFile);
            config.load(new FileInputStream(configFile));
            agentPort    = new Integer(config.getProperty("serviceport")).intValue();

            String firstServer = config.getProperty("aselect.server.1");
            aselectServer = config.getProperty("aselect.server." + firstServer);

            String accounts =  MMBaseContext.getConfigPath() + File.separator + "security" + File.separator + "accounts.properties";
            log.service("Reading file: " + accounts);
            knownUsers = new Properties();
            knownUsers.load(new FileInputStream(accounts));
            log.service("Found " + knownUsers.size() + " known accounts (unknown accounts will be considered 'basic user')");

            String ranks =  MMBaseContext.getConfigPath() + File.separator + "security" + File.separator + "ranks.properties";
            log.service("Reading file: " + ranks);


            if (registeredRanks != null) {
                Iterator i = registeredRanks.keySet().iterator();
                while (i.hasNext()) {
                    String r = (String) i.next();
                    Rank.deleteRank(r);
                }
            }
            registeredRanks = new Properties();
            try {
                registeredRanks.load(new FileInputStream(ranks));
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



        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    // javadoc inherited
    public UserContext login(String application, Map loginInfo, Object[] parameters) throws SecurityException {

        if (log.isDebugEnabled()) {
            log.debug("app: '" + application + "' loginInfo: " + loginInfo);
        }

        if (loginInfo == null) {
            return new ASelectUser("anonymous", Rank.ANONYMOUS, getKey(), "anonymous");
        }

        HttpServletRequest request;
        HttpServletResponse response;
        try {
            request = (HttpServletRequest) loginInfo.get(KEY_REQUEST);
            if (request == null)
            {
                throw new SecurityException("No '" + KEY_REQUEST + "' found in credentials Map, this is needed by this implementation");
            }

            response = (HttpServletResponse) loginInfo.get(KEY_RESPONSE);
            if (response == null)
            {
                throw new SecurityException("No '" + KEY_RESPONSE + "' found in credentials Map, this is needed by this implementation");
            }


        }
        catch (ClassCastException cce)
        {
            throw new SecurityException(cce);
        }



        if ("anonymous".equals(application)) {
            try {
                logout(request, response, "mmbase");
                // Hmm, I don't really understand why I cannot find the application back with the ticket or so.

                response.sendRedirect(aselectServer);
                return null;
                //return new ASelectUser("anonymous", Rank.ANONYMOUS);
            } catch (ASelectException ase) {
                throw new SecurityException(ase);
            } catch (Exception ioe) {
                return null;
            }
        }



        ASelectUser newUser;
        // make connection  to A-Select agent, and create an ASelectUser object
        try
        {
            if (application == null || application.equals("")) application = "mmbase";

            if (authentication(request, response, application))
            {
                String userName = getASelectUserId(request);
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
                newUser = new ASelectUser(userName, rank, getKey(), application);
            } else {
                log.debug("User needs authentication and has been redirected to A-Select");
                return null;
                // throw new SecurityException("user has been redirected to A-Select");
            }
        } catch (Exception e) {
            throw new SecurityException(e.getMessage(), e);
        }

        List userNames = (List)loginInfo.get(KEY_USERNAMES);
        if (userNames != null && !userNames.contains(newUser.getIdentifier())) {
            throw new SecurityException("Logged on as wrong user ('" + newUser.getIdentifier() + "', but must be one of " + userNames + ")");
        }

        return newUser;
    }

    // javadoc inherited
    public boolean isValid(UserContext usercontext) throws SecurityException {

        // could perhaps do check, but better not..
        return true;
    }

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
    protected boolean authentication(HttpServletRequest request, HttpServletResponse response, String application) throws ASelectException {

        try {
            // look if there is a valid ticket
            // if not do the authentication process
            if (verify_ticket(request, response, application)) {
                return true;
            } else if (verify_credentials(request, response)) {
                return false; //process continues
            } else if (authenticate_user(request, response, application)) {
                return false; //process continues
            } else {
                throw new Exception("The A-Select Authentication Module has received an unknown request. Please try again.");
            }
        } catch (Exception e) {
            throw new ASelectException("Could not perform authentication: " + e.getMessage(),  e);
        }
    } //end of function : authentication

    /**
     * Retrieves the A-Select User Id from the cookies.
     *
     * @param request the current HTTP request. Used to obtain the cookie(s)
     * @return The A-Select user id or null if not set
     */
    protected String getASelectUserId(HttpServletRequest request) {
        String xUserId = getCookie(request, "aselectuid");
        return decodeCGI(xUserId);
    } //end of function : getASelectUserId

    /**
     * Retrieves the A-Select Organization Id from the cookies.
     *
     * @param request the current HTTP request. Used to obtain the cookie(s)
     * @return The A-Select organization id or null if not set
     */
    protected String getASelectOrganization(HttpServletRequest request) {
        String xOrganization = getCookie(request, "aselectorganization");
        return decodeCGI(xOrganization);

    } //end of function : getASelectOrganization

    /**
     * Retrieves the A-Select Session Id from the cookies.
     *
     * @param request the current HTTP request. Used to obtain the cookie(s)
     * @return The A-Select session id or null if not set
     */
    protected String getASelectSessionId(HttpServletRequest request) {
        String xSessionId = getCookie(request, "aselectticket");
        //   xSessionId = decodeCGI( xSessionId );
        return xSessionId;
    } //end of function : getASelectSessionId

    /**
     * Processes the logout of a user.
     *
     * @param request the current HTTP request
     * @param response the current HTTP response.
     */
    protected void logout(HttpServletRequest request, HttpServletResponse response, String application) throws ASelectException {
        String xTicket = null;
        String xUid = null;
        Hashtable xAgentResponse = null;

        String xResultCode = null;

        xUid    = getASelectUserId(request);
        xTicket = getASelectSessionId(request);

        if (log.isDebugEnabled()) {
            log.debug("Logging out uid '" + xUid + "' with ticket '" + xTicket + "'");
        }

        if (xTicket != null) {
            try {
                xAgentResponse = transferRequest("request=kill_ticket&ticket=" + xTicket + "&app_id=" + application);
            } catch (Exception e) {
                log.error(e);
            }
        }
    } //end of function : logout

    /**
     * This function will perform the verify ticket function.
     * @return 'true' if the user has a valid A-Select ticket (at the A-Select Agent!)
     *
     * @param request the current HTTP request
     * @param response the current HTTP response.
     */
    private boolean verify_ticket(HttpServletRequest request, HttpServletResponse response, String application) throws Exception {
        String xUid = null;
        String xOrganization = null;
        String xTicket = null;
        Hashtable xAgentResponse = null;

        String xResultCode = null;
        String xMethod = "ASelectServletFilter.verify_ticket -> ";

        log.debug("entering" + xMethod);

        xTicket = getASelectSessionId(request);
        if (xTicket == null) {
            log.debug("ticket is null returning ");
            return false;
        }

        xUid = getASelectUserId(request);
        if (xUid == null)
            return false;

        xOrganization = getASelectOrganization(request);
        if (xOrganization == null)
            return false;

        try {
            xAgentResponse = transferRequest("request=verify_ticket&ticket=" + xTicket + "&app_id=" + application + "&uid=" + xUid + "&organization=" + xOrganization);
        } catch (Exception e) {
            throw new Exception(
                xMethod + "The A-Select Agent could not be reached. (system code : " + e.getMessage() + "<br />  NOTE : The A-Select agent is not started or misconfigured <br />");
        }

        xResultCode = (String)xAgentResponse.get("result_code");
        if (xResultCode == null || !xResultCode.equals(ASelectErrors.ASELECT_NO_ERROR))
            return false;

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
    private boolean verify_credentials(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String xRID = null;
        String xCredentials = null;
        Hashtable xAgentResponse = null;

        String xResultCode = null;
        String xUid = null;
        String xOrganization = null;
        String xTicket = null;
        String xRedirectUrl = null;
        String xMethod = "ASelectServletFilter.verify_credentials -> ";

        xRID = request.getParameter("rid");
        if (xRID == null)
            return false;

        xCredentials = request.getParameter("aselect_credentials");
        if (xCredentials == null)
            return false;

        try {
            xAgentResponse = transferRequest("request=verify_credentials&rid=" + xRID + "&aselect_credentials=" + xCredentials);
            log.debug("Agent response " + xAgentResponse);
        } catch (Exception e) {
            throw new Exception(
                xMethod + "The A-Select Agent could not be reached. (system code : " + e.getMessage() + "<br>  NOTE : The A-Select agent is not started or misconfigured<br>");
        }

        xResultCode = (String)xAgentResponse.get("result_code");
        if (xResultCode == null || !xResultCode.equals(ASelectErrors.ASELECT_NO_ERROR))
            throw new Exception(xMethod + "The A-Select Agent did return a malformed response.<br>Response from A-Select Agent : " + xAgentResponse + "<br>");

        // get the parameters to set as cookie
        xUid = (String)xAgentResponse.get("uid");
        if (xUid == null)
            throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'uid'.<br>");

        xOrganization = (String)xAgentResponse.get("organization");
        if (xOrganization == null)
            throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'organization'.<br>");

        xTicket = (String)xAgentResponse.get("ticket");
        if (xTicket == null)
            throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'ticket'.<br>");

        xUid = doCGIEncode(xUid);

        // set the cookies
        Cookie xTicketCookie = new Cookie("aselectticket", xTicket);
        xTicketCookie.setPath(request.getContextPath());
        Cookie xUidCookie = new Cookie("aselectuid", xUid);
      xUidCookie.setPath(request.getContextPath());
        Cookie xOrgCookie = new Cookie("aselectorganization", xOrganization);
      xOrgCookie.setPath(request.getContextPath());

        response.addCookie(xTicketCookie);
        response.addCookie(xUidCookie);
        response.addCookie(xOrgCookie);

        xRedirectUrl = new String(HttpUtils.getRequestURL(request));
        xRedirectUrl += "?" + request.getQueryString();

        try {
            response.sendRedirect(xRedirectUrl);
        } catch (Exception e) {
            throw new Exception(xMethod + "The system could not redirect to the following URL : " + xRedirectUrl + "<br>");
        }
        log.debug("success " + xUidCookie);
        return true;
    } //end of function : verify_credentials

    /**
     * This function will perform the authenticate function.
     * This function will call the agent and the redirect the user to the A-Select server.
     *
     * @param request the current HTTP request
     * @param response the current HTTP response.
     */
    private boolean authenticate_user(HttpServletRequest request, HttpServletResponse response, String application) throws Exception {
        String xAppUrl = null;
        String xParams = null;
        Hashtable xAgentResponse = null;
        String xResultCode = null;
        String xRedirectUrl = null;
        String xAsUrl = null;
        String xASelectServer = null;
        String xRid = null;
        String xMethod = "ASelectServletFilter.verify_credentials -> ";

        xParams = request.getQueryString();
        if (xParams != null) {
            Cookie xOrgParamsCookie = new Cookie("aselectorgurlparams", xParams);
            response.addCookie(xOrgParamsCookie);
        }
        xAppUrl = request.getRequestURL().toString();
        System.out.println(xAppUrl);

        try {
            xAgentResponse = transferRequest("request=authenticate&app_url=" + xAppUrl + "&app_id=" + application);
        } catch (Exception e) {
            throw new Exception(
                "The A-Select Agent could not be reached. (system code : " + e.getMessage() + "<br>  NOTE : The A-Select agent is not started or misconfigured<br>");
        }
        xResultCode = (String)xAgentResponse.get("result_code");
        if (xResultCode == null)
            throw new Exception(xMethod + "The A-Select Agent did return a malformed response.<br>Response from A-Select Agent : " + xAgentResponse + "<br>");
        else if (!xResultCode.equals(ASelectErrors.ASELECT_NO_ERROR)) {
            if (xResultCode.equals(ASelectErrors.ASELECT_UNKNOWN_USER) || xResultCode.equals(ASelectErrors.ASELECT_COULD_NOT_AUTHENTICATE_USER)) {
                xRedirectUrl = new String(HttpUtils.getRequestURL(request));
                try {
                    response.sendRedirect(xRedirectUrl);
                } catch (Exception e) {
                    throw new Exception(xMethod + "The system could not redirect to the following URL : " + xRedirectUrl + "<br>");
                }
            } else
                throw new Exception(xMethod + "The A-Select Agent did return an error : " + xResultCode + "<br>");
        }

        // get the parameters to set as cookie
        xRid = (String)xAgentResponse.get("rid");
        if (xRid == null)
            throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'rid'.<br>");

        xAsUrl = (String)xAgentResponse.get("as_url");
        if (xAsUrl == null)
            throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'as_url'.<br>");

        xASelectServer = (String)xAgentResponse.get("a-select-server");
        if (xASelectServer == null)
            throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'a-select-server'.<br>");

        xRedirectUrl = xAsUrl + "&rid=" + xRid + "&a-select-server=" + xASelectServer;
        try {
            response.sendRedirect(xRedirectUrl);
        } catch (Exception e) {
            throw new Exception(xMethod + "The system could not redirect to the following URL : " + xRedirectUrl + "<br>");
        }
        return true;
    } //end of function : authenticate_user

    /**
     * This function will transfer the request to the A-Select Agent.
     *
     * @param request the request which has to be sent to the A-Select Agent.
     * @returns Hashtable This hashtable contains the response from the Agent.
     */
    private Hashtable transferRequest(String request) throws Exception {
        Socket xSocket = null;
        PrintStream xOut = null;
        BufferedReader xIn = null;
        String xAgentResponse = null;

        xSocket = new Socket(agentAddress, agentPort);
        xOut = new PrintStream(xSocket.getOutputStream());
        xIn = new BufferedReader(new InputStreamReader(xSocket.getInputStream()));

        xOut.println(request);
        xAgentResponse = xIn.readLine();
        return convertCGIMessage(xAgentResponse);
    } //end of function : transferRequest

    /**
     * This function will get the cookie form the (servlet)request
     *
     * @param request the servlet request
     * @param xCookieName the name of the cookie to obtain from the request.
     * @returns String The value of the cookie.
     */
    private String getCookie(HttpServletRequest request, String xCookieName) {
        Cookie[] xCookies = null;

        xCookies = request.getCookies();
        if (xCookies == null) {
            log.debug("no cookies here");
            return null;
        }

        for (int i = 0; i < xCookies.length; i++) {
            log.debug("looking at cookie " + xCookies[i].getName() + " with value " + xCookies[i].getValue());
            if (xCookies[i].getName().equals(xCookieName)) {
                log.debug("found cookie " + xCookieName + " with value " + xCookies[i].getValue());
                return xCookies[i].getValue();
            }
        }
        log.debug("failed to find cookie " + xCookieName);
        return null;

    } //end of function : getCookie

    /**
     * This function will do a little CGI encoding
     * For now it is not complete implemented
     *
     *@param xValue The value to encode
     *@return the encoded value
     */
    private String doCGIEncode(String xValue) {
        String xEncoded = null;

        if (xValue != null) {
            xEncoded = xValue.replace(' ', '+');
        }

        return xEncoded;
    } //end of function : doCGIEncode

    /**
     * This function will do a little CGI decoding
     * For now it is not complete implemented
     *
     *@param xValue The value to decode
     *@return the decoded value
     * hmm
     */
    private String decodeCGI(String xValue) {

        int iPos;
        String xDecoded = null;
        if (xValue != null) {
            xDecoded = xValue.replace('+', ' ');
            iPos = xDecoded.indexOf("%2B");
            while (iPos != -1) {
                xDecoded = xDecoded.substring(0, iPos) + xDecoded.substring(iPos + 3);
                iPos = xDecoded.indexOf("%2B");
            }
        }
        return xDecoded;
    } //end of function : decodeCGI

    /**
     * This method will convert a string of <code>key=value&key=value</code>
     * etc. tuples (aka a CGI request string) into a hashtable for much easier
     * processing.<br />
     * <b>Note:</b> The key names are all converted to lowercase.
     * @todo can we not simply use request.getParameter?
     */
    public static Hashtable convertCGIMessage(String xMessage) {
        String xToken, xKey, xValue;
        StringTokenizer xST = null;
        int iPos;
        Hashtable response = new Hashtable();

        if (xMessage != null) {
            xST = new StringTokenizer(xMessage, "&");

            while (xST.hasMoreElements()) {
                xToken = (String)xST.nextElement();
                if (!xToken.trim().equals("")) {
                    iPos = xToken.indexOf('=');
                    if (iPos != -1) {
                        xKey = xToken.substring(0, iPos);

                        try {
                            xValue = xToken.substring(iPos + 1);
                        } catch (Exception e) {
                            xValue = "";
                        }

                        if (xKey != null && xValue != null) {
                            response.put(xKey.toLowerCase(), xValue);
                        }
                    }
                }
            }
        }
        return response;
    }

    private class ConfigurationWatcher extends FileWatcher {
        ConfigurationWatcher() {
            super();
        }
        public void onChange(File file) {
            configure();
        }
    }

} // end of class
