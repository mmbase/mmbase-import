package nl.didactor.security.aselect;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUtils;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMBase;
import org.mmbase.security.Rank;
import org.mmbase.security.SecurityException;
import org.mmbase.security.Authentication;

import org.mmbase.security.implementation.aselect.ASelectErrors;
import org.mmbase.security.implementation.aselect.ASelectException;
import org.mmbase.security.implementation.aselect.ASelectUser;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.didactor.builders.PeopleBuilder;
import nl.didactor.component.Component;
import nl.didactor.security.AuthenticationComponent;
import nl.didactor.security.UserContext;

/**
 * What is this?
 * @javadoc
 */

public class ASelectSecurityComponent extends Component implements AuthenticationComponent {
    private static final Logger log = Logging.getLoggerInstance(ASelectSecurityComponent.class);

    private ResourceWatcher fileWatcher = null;

    private PeopleBuilder users;


    private final Map properties = new HashMap();

    /**
     * the address of the A-Select Agent
     */
    private static final String agentAddress = "127.0.0.1";

    /**
     * the communication port of the A-Select Agent
     */
    private int agentPort = 1495; // default

    /**
     * ASelect server address
     */
    private String aselectServer = "https://ext1.alfa-ariss.com/aselectserver/server"; // default

    /**
     * A-Select Application
     */
    private String sASelectApplication = "mmbase";

    public String getName() {
        return "aselect";
    }

    public String getVersion() {
        return "2.0";
    }

    public Component[] dependsOn() {
        return new Component[0];
    }

    public ASelectSecurityComponent() {
        load();
    }

    private void checkBuilder() throws org.mmbase.security.SecurityException {
        if (users == null) {
            org.mmbase.module.core.MMBase mmb = (org.mmbase.module.core.MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
            users = (PeopleBuilder) mmb.getMMObject("people");
            if (users == null) {
                String msg = "builder people not found";
                log.error(msg);
                throw new org.mmbase.security.SecurityException(msg);
            }
        }
    }


    protected String getLoginPage(HttpServletRequest request) {
        String page = (String) properties.get(request.getServerName() + ".aselect.login_page");
        if (page == null) {
            page = (String) properties.get("aselect.login_page");
        }
        return page == null ? "/login_aselect.jsp" : page;
    }

    public String getLoginPage(HttpServletRequest request, HttpServletResponse response) {
        return getLoginPage(request);
    }

    public UserContext isLoggedIn(HttpServletRequest request, HttpServletResponse response) {
        // Check the users session for a valid login component. If there's no component found
    	// the user has been authenticated by A-Select, but the session has not been fully
    	// initialised.
    	HttpSession session = request.getSession(false);

    	if (session != null) {
	    String loginComponent = (String)session.getAttribute("didactor-logincomponent");

	    if (loginComponent != null) {
	        // the session seems to be initialised correctly, verify the ticket with A-Select
		try {
		    if (verify_ticket(request, response, "a-select")) {
			String userName = getASelectUserId(request);
			MMObjectNode user = users.getUser(userName);

			if (user == null) {
			    try {
				response.getWriter().println("<b>A-Select error:</b><br/>");
				response.getWriter().println("The authorization passed, but user with id=<b>" +
                                                             userName + "</b> is NOT present in Didactor's database<br/>");
				response.getWriter().println("Can't create security context for him.");
			    } catch (Exception ex) {
				throw new SecurityException("no user (id = " + userName + ") in db", null);
			    }
			}

			return new UserContext(user, "delegate");
		    }
		} catch (Exception ex) {
		    throw new SecurityException("Exception while verifying ticket: " + ex.getMessage(), null);
		}
	    }
    	}

        return null;
    }

    /**
     * Here something else then a authentication implemtation produces User objects any way.
     * @javadoc
     */
    public UserContext processLogin(HttpServletRequest request, HttpServletResponse response, String application) {
        if ("a-select".equals(application)) {
            checkBuilder();

            ASelectUser newUser;
            // make connection to A-Select agent, and create an ASelectUser
            // object
            try {
                if (authentication(request, response, sASelectApplication)) {
                    String userName = getASelectUserId(request);
                    Rank rank = Rank.BASICUSER;

                    if (log.isDebugEnabled()) {
                        log.debug("Logging in user: " + userName);

                    }
                    // wtf
                    Authentication a  = MMBase.getMMBase().getMMBaseCop().getAuthentication();
                    newUser = new ASelectUser(userName, rank, a.getKey(), "a-select");

                } else {
                    log.debug("User needs authentication and has been redirected to A-Select");
                    return null;
                }
            } catch (Exception e) {
                try {
                    response.getWriter().println("<b>A-Select error:</b><br/>");
                    response.getWriter().println(e.toString());
                } catch (Exception ex) {

                }
                throw new SecurityException(e.getMessage(), e);
            }

            MMObjectNode user = users.getUser(newUser.getIdentifier());
            if (user == null) {
                try {
                    response.getWriter().println("<b>A-Select error:</b><br/>");
                    response.getWriter().println("The authorization passed, but user with id=<b>" + newUser.getIdentifier() + "</b> is NOT present in Didactor's database<br/>");
                    response.getWriter().println("Can't create security context for him.");
                } catch (Exception ex) {
                }
                throw new SecurityException("no user (id=" + newUser.getIdentifier() + ") in db", null);
            }
            return new UserContext(user, application);
        }
        return null;
    }


    protected void configure() {
        // get some parameters

        String aSelectConfig = "security/agent.conf";
        String globalConfig = "security/login.properties";

        try {
            Properties config = new Properties();
            log.service("Reading file: " + aSelectConfig);

            config.load(ResourceLoader.getConfigurationRoot().getResource(aSelectConfig).openStream());
            agentPort = new Integer(config.getProperty("serviceport")).intValue();

            String firstServer = config.getProperty("aselect.server.1");
            aselectServer = config.getProperty("aselect.server." + firstServer);
            sASelectApplication = config.getProperty("application");
        } catch (Exception e) {
            log.error(e);
        }
        try {

            Properties props = new Properties();
            log.service("Reading file: " + globalConfig);
            props.load(ResourceLoader.getConfigurationRoot().getResource(globalConfig).openStream());
            properties.putAll(props);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }



    /**
     * Called once my MMBase core during security starting
     *
     */
    protected void load() {
        if (fileWatcher == null) {
            fileWatcher = new ResourceWatcher() {
                    public void onChange(String file) {
                        configure();
                    }
                };
            fileWatcher.add("security/agent.conf");
            fileWatcher.add("security/login.properties");
            fileWatcher.setDelay(10 * 1000);
            fileWatcher.start();
        }
        configure();
    }


    /**
     * Processes the logout of a user.
     *
     * @param request
     *            the current HTTP request
     * @param response
     *            the current HTTP response.
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String xTicket = null;
        String xUid = null;

        xUid = getASelectUserId(request);
        xTicket = getASelectSessionId(request);

        if (log.isDebugEnabled()) {
            log.debug("Logging out uid '" + xUid + "' with ticket '" + xTicket + "'");
        }

        if (xTicket != null) {
            try {
                transferRequest("request=kill_ticket&ticket=" + xTicket + "&app_id=" + sASelectApplication);
            } catch (Exception e) {
                log.error(e);
            }
        }

        // added by AZ:
        request.getSession().removeAttribute("aselectticket");
        request.getSession().removeAttribute("aselectuid");
        request.getSession().removeAttribute("aselect_credentials");
        request.getSession().removeAttribute("aselectorgurlparams");
        request.getSession().removeAttribute("aselectorganization");
    }

    /**
     * This function will transfer the request to the A-Select Agent.
     *
     * @param request
     *            the request which has to be sent to the A-Select Agent.
     * @returns Hashtable This hashtable contains the response from the Agent.
     */
    private Hashtable transferRequest(String request) throws Exception {
        // System.out.println("111111111111111111111111111111111" + request);
        Socket xSocket = null;
        PrintStream xOut = null;
        BufferedReader xIn = null;
        String xAgentResponse = null;

        xSocket = new Socket(agentAddress, agentPort);
        xOut = new PrintStream(xSocket.getOutputStream());
        xIn = new BufferedReader(new InputStreamReader(xSocket.getInputStream()));

        xOut.println(request);
        xAgentResponse = xIn.readLine();
        // System.out.println("222222222222222222222222222222222" +
        // xAgentResponse);
        return convertCGIMessage(xAgentResponse);
    } // end of function : transferRequest

    /**
     * This function will get the cookie form the (servlet)request
     *
     * @param request
     *            the servlet request
     * @param xCookieName
     *            the name of the cookie to obtain from the request.
     * @returns String The value of the cookie.
     */
    private String getCookie(HttpServletRequest request, String xCookieName) {
        if (request.getSession().getAttribute(xCookieName) != null) {
            return (String) request.getSession().getAttribute(xCookieName);
        } else {
            return null;
        }
    }

    /**
     * This function will perform the verify ticket function.
     *
     * @return 'true' if the user has a valid A-Select ticket (at the A-Select
     *         Agent!)
     *
     * @param request
     *            the current HTTP request
     * @param response
     *            the current HTTP response.
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
        if (xUid == null) {
            log.debug("Userid is null, returning");
            return false;
        }

        xOrganization = getASelectOrganization(request);
        if (xOrganization == null) {
            log.debug("Organization is null, returning");
            return false;
        }

        try {
            xAgentResponse = transferRequest("request=verify_ticket&ticket=" + xTicket + "&app_id=" + application + "&uid=" + xUid + "&organization=" + xOrganization);
        } catch (Exception e) {
            throw new Exception(xMethod + "The A-Select Agent could not be reached. (system code : " + e.getMessage() + "<br />  NOTE : The A-Select agent is not started or misconfigured <br />");
        }

        xResultCode = (String) xAgentResponse.get("result_code");
        log.debug("Agent response: " + xResultCode);

        if (xResultCode == null || !xResultCode.equals(ASelectErrors.ASELECT_NO_ERROR)) {
            return false;
        }

        return true;
    } // end of function : verify_ticket

    /**
     * This function will perform the verify credentials function. This function
     * will return 'true' if the credentials provided in the parametrs are
     * correct. The credentials are being set by the A-Select server
     *
     * @param request
     *            the current HTTP request
     * @param response
     *            the current HTTP response.
     */
    private boolean verify_credentials(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.debug("verify_credentials(" + request + "," + response + ")");
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
        if (xRID == null) {
            log.debug("No 'rid' parameter specified");
            return false;
        }

        xCredentials = request.getParameter("aselect_credentials");
        if (xCredentials == null) {
            log.debug("No 'aselect_credentials' parameter specified");
            return false;
        }

        try {
            xAgentResponse = transferRequest("request=verify_credentials&rid=" + xRID + "&aselect_credentials=" + xCredentials);
            log.debug("Agent response " + xAgentResponse);
        } catch (Exception e) {
            throw new Exception(xMethod + "The A-Select Agent could not be reached. (system code : " + e.getMessage() + "<br>  NOTE : The A-Select agent is not started or misconfigured<br>");
        }

        xResultCode = (String) xAgentResponse.get("result_code");
        if (xResultCode == null || !xResultCode.equals(ASelectErrors.ASELECT_NO_ERROR)) {
            throw new Exception(xMethod + "The A-Select Agent did return a malformed response.<br>Response from A-Select Agent : " + xAgentResponse + "<br>");
        }

        // get the parameters to set as cookie
        xUid = (String) xAgentResponse.get("uid");
        if (xUid == null) {
            throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'uid'.<br>");
        }

        xOrganization = (String) xAgentResponse.get("organization");
        if (xOrganization == null) {
            throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'organization'.<br>");
        }

        xTicket = (String) xAgentResponse.get("ticket");
        if (xTicket == null) {
            throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'ticket'.<br>");
        }

        xUid = doCGIEncode(xUid);

        /*
         * // set the cookies Cookie xTicketCookie = new Cookie("aselectticket",
         * xTicket); xTicketCookie.setPath(request.getContextPath()); Cookie
         * xUidCookie = new Cookie("aselectuid", xUid);
         * xUidCookie.setPath(request.getContextPath()); Cookie xOrgCookie = new
         * Cookie("aselectorganization", xOrganization);
         * xOrgCookie.setPath(request.getContextPath());
         *
         * response.addCookie(xTicketCookie); response.addCookie(xUidCookie);
         * response.addCookie(xOrgCookie);
         */
        request.getSession().setAttribute("aselectticket", xTicket);
        request.getSession().setAttribute("aselectuid", xUid);
        request.getSession().setAttribute("aselectorganization", xOrganization);

        xRedirectUrl = new String(HttpUtils.getRequestURL(request));
        xRedirectUrl += "?" + request.getQueryString();

        try {
            response.sendRedirect(xRedirectUrl);
        } catch (Exception e) {
            throw new Exception(xMethod + "The system could not redirect to the following URL : " + xRedirectUrl + "<br>");
        }
        log.debug("success " + xUid);
        return true;
    } // end of function : verify_credentials

    /**
     * This function will perform the authenticate function. This function will
     * call the agent and the redirect the user to the A-Select server.
     *
     * @param request
     *            the current HTTP request
     * @param response
     *            the current HTTP response.
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
            request.getSession().setAttribute("aselectorgurlparams", xParams);
        }
        xAppUrl = request.getRequestURL().toString();
        log.debug("Application URL: " + xAppUrl);

        try {
            xAgentResponse = transferRequest("request=authenticate&app_url=" + xAppUrl + "&app_id=" + application);
        } catch (Exception e) {
            throw new Exception("The A-Select Agent could not be reached. (system code : " + e.getMessage() + "<br>  NOTE : The A-Select agent is not started or misconfigured<br>");
        }

        xResultCode = (String) xAgentResponse.get("result_code");
        log.debug("Result code: " + xResultCode);

        if (xResultCode == null) {
            throw new Exception(xMethod + "The A-Select Agent did return a malformed response.<br>Response from A-Select Agent : " + xAgentResponse + "<br>");
        } else if (!xResultCode.equals(ASelectErrors.ASELECT_NO_ERROR)) {
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
        xRid = (String) xAgentResponse.get("rid");
        if (xRid == null) {
            throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'rid'.<br>");
        }

        xAsUrl = (String) xAgentResponse.get("as_url");
        if (xAsUrl == null) {
            throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'as_url'.<br>");
        }

        xASelectServer = (String) xAgentResponse.get("a-select-server");
        if (xASelectServer == null) {
            throw new Exception(xMethod + "The A-Select Agent did return a mallformed response :<br>missinge param 'a-select-server'.<br>");
        }

        xRedirectUrl = xAsUrl + "&rid=" + xRid + "&a-select-server=" + xASelectServer;
        try {
            response.sendRedirect(URLDecoder.decode(xRedirectUrl, "ISO-8859-1"));
        } catch (Exception e) {
            throw new Exception(xMethod + "The system could not redirect to the following URL : " + xRedirectUrl + "<br>");
        }
        return true;
    } // end of function : authenticate_user

    /**
     * Performs the work of authentication and session management.
     *
     * This function should be called for each request to the Servlet.<br>
     * If the user has a valid session, true will be returned and de Servlet can
     * process the request.<br>
     * If not, this module has redirected the user to A-Select or has thrown an
     * exception.<br>. In the situation a user is not yet authenticated the
     * Servlet wil get severel requests with authentication parameters. That's
     * why this function should be called before processing the request by the
     * servlet.
     *
     * @param request
     *            the current HTTP request. Used to obtain the parameters for
     *            authentication.
     * @param response
     *            the current HTTP response
     * @return true if the user was authenticated, false otherwise.<br>
     *         If false is returned the Servlet should not write anything to the
     *         client because the client was already redirected!
     * @throws ASelectException
     *             If the module could not perform the authenticate request
     */
    protected boolean authentication(HttpServletRequest request, HttpServletResponse response, String application) throws ASelectException {
        try {
            // look if there is a valid ticket
            // if not do the authentication process
            if (verify_ticket(request, response, application)) {
                return true;
            }
            if (verify_credentials(request, response)) {
                return false; // process continues
            }
            if (authenticate_user(request, response, application)) {
                return false; // process continues
            }
            throw new Exception("The A-Select Authentication Module has received an unknown request. Please try again.");
        } catch (Exception e) {
            throw new ASelectException("Could not perform authentication: " + e.getMessage(), e);
        }
    } // end of function : authentication



    /**
     * This method will convert a string of <code>key=value&key=value</code>
     * etc. tuples (aka a CGI request string) into a hashtable for much easier
     * processing.<br />
     * <b>Note:</b> The key names are all converted to lowercase.
     *
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
                xToken = (String) xST.nextElement();
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

    /**
     * This function will do a little CGI encoding For now it is not complete
     * implemented
     *
     * @param xValue
     *            The value to encode
     * @return the encoded value
     */
    private String doCGIEncode(String xValue) {
        String xEncoded = null;

        if (xValue != null) {
            xEncoded = xValue.replace(' ', '+');
        }

        return xEncoded;
    }

    /**
     * This function will do a little CGI decoding For now it is not complete
     * implemented
     *
     * @param xValue
     *            The value to decode
     * @return the decoded value hmm
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
    }

    /**
     * Retrieves the A-Select User Id from the cookies.
     *
     * @param request
     *            the current HTTP request. Used to obtain the cookie(s)
     * @return The A-Select user id or null if not set
     */
    protected String getASelectUserId(HttpServletRequest request) {
        String xUserId = getCookie(request, "aselectuid");
        return decodeCGI(xUserId);
    }

    /**
     * Retrieves the A-Select Organization Id from the cookies.
     *
     * @param request
     *            the current HTTP request. Used to obtain the cookie(s)
     * @return The A-Select organization id or null if not set
     */
    protected String getASelectOrganization(HttpServletRequest request) {
        String xOrganization = getCookie(request, "aselectorganization");
        return decodeCGI(xOrganization);
    }

    /**
     * Retrieves the A-Select Session Id from the cookies.
     *
     * @param request
     *            the current HTTP request. Used to obtain the cookie(s)
     * @return The A-Select session id or null if not set
     */
    protected String getASelectSessionId(HttpServletRequest request) {
        String xSessionId = getCookie(request, "aselectticket");
        return xSessionId;
    }
}
