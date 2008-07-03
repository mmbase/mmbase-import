package org.jahia.jahiawebapps.jforum;

import javax.servlet.http.*;
import java.io.IOException;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.UserDAO;
import net.jforum.util.MD5;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.ConfigKeys;
import org.apache.log4j.Logger;


/**
 *  Manage Auto portal connect user to JForum
 *
 *@author    Khaled TLILI
 */
public class PortalAutoConnectUserManager {
	private final String GUEST = "guest";
	private final String MD5_KEY = "org.jahia.portlet.jforum";
	private HttpServletRequest request;
	private HttpServletResponse response;
        private final String ADMINISTRATOR_ROLE = "administrator";
        private static Logger logger = Logger.getLogger(PortalAutoConnectUserManager.class);




	/**
	 *  Constructor for the PortalAutoConnectUserManager object
	 *
	 *@param  request   Description of Parameter
	 *@param  response  Description of Parameter
	 */
	public PortalAutoConnectUserManager(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		logger.debug("Portal user: " + request.getRemoteUser());
	}


	/**
	 *  Gets the Registred attribute of the PortalAutoConnectUserManager class
	 *
	 *@return    The Registred value
	 */
	public boolean isRegistred() {
		try {
			UserDAO um = DataAccessDriver.getInstance().newUserDAO();
			logger.debug("Is registred: " + um.isUsernameRegistered(request.getRemoteUser()));
			return um.isUsernameRegistered(request.getRemoteUser());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return true;
		}
	}


	/**
	 *  Gets the Administrator attribute of the PortalAutoConnectUserManager
	 *  object
	 *
	 *@return    The Administrator value
	 */
	public boolean isAdministrator() {
		return request.isUserInRole(ADMINISTRATOR_ROLE);
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public boolean processSSO() {
		// set pass to be used by sso
		String password = getPortalUserPassword();
		request.getSession().setAttribute(SystemGlobals.getValue(ConfigKeys.SSO_PASSWORD_ATTRIBUTE), password);
		//set it's group
		if (isAdministrator()) {
			request.getSession().setAttribute(SystemGlobals.getValue("sso.role.attribute"), "ROLE_ADMIN");

		}

		return true;
	}




	/**
	 *  process auto actions (register, connect)
	 *
	 *@return    True if an auto action was performed
	 */
	public boolean process() {
		if (isPortlatUser()) {
			if (!isRegistred()) {
				logger.debug("Process: Not already register");

				// register
				if (hasToMakeRegister()) {
					logger.debug("Process: do register");
					doAutoRegisteUser();
					return true;
				}
				else {
					return false;
				}
			}
			else {

				// is already registred
				if (hasToMakeLogin()) {
					logger.debug("Process: do connect");
					doAutoConnectUser();
					return true;
				}
				else {
					//already connected
					logger.debug("Process: already connected");
					return false;
				}
			}
		}
		else {
			logger.debug("AutoConnect: do nothing");
			// not a portal user --> do nothink
			return false;
		}
	}


	/**
	 *  Gets the PortalUserPassword attribute of the PortalAutoConnectUserManager
	 *  object
	 *
	 *@return    The PortalUserPassword value
	 */
	private String getPortalUserPassword() {
		String username = request.getRemoteUser();
		return MD5.crypt(username + MD5_KEY);
	}


	/**
	 *  Gets the PortlatUser attribute of the PortalAutoConnectUserManager object
	 *
	 *@return    The PortlatUser value
	 */
	private boolean isPortlatUser() {
		String user = request.getRemoteUser();
		if (user == null) {
			return false;
		}

		// for jahia: non portal user = user GUEST
		return !user.equalsIgnoreCase(GUEST);
	}


	/**
	 *  Auto connect user
	 */
	private void doAutoConnectUser() {
		response.setContentType("text/html");
		System.out.println("Do auto login ");
		String username = request.getRemoteUser();
		String password = getPortalUserPassword();

		// build request
		StringBuffer redirectUrl = new StringBuffer();
		redirectUrl.append(request.getContextPath() + "?");
		redirectUrl.append("module='user'");
		redirectUrl.append("&");
		redirectUrl.append("action='validateLogin'");
		redirectUrl.append("&");
		redirectUrl.append("username='" + username + "'");
		redirectUrl.append("&");
		redirectUrl.append("password='" + password + "'");

		// perform redirection
		autoLoginDone();
		doRedirection(redirectUrl.toString());
	}


	/**
	 *  Do a ajavascript redirection
	 *
	 *@param  redirectUrl  Description of Parameter
	 */
	private void doRedirection(String redirectUrl) {
		String script = "<script> document.location='" + response.encodeURL(redirectUrl) + "'; </script>";
		try {
			response.getOutputStream().write(script.getBytes());
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}



	/**
	 *  Auto register user
	 */
	private void doAutoRegisteUser() {
		response.setContentType("text/html");
		System.out.println("Do auto register ");
		String username = request.getRemoteUser();
		String password = getPortalUserPassword();
		String email = "joe.lafousse@yahoo.com";

		// build request
		StringBuffer redirectUrl = new StringBuffer();
		redirectUrl.append(request.getContextPath() + "?");
		redirectUrl.append("module='user'");
		redirectUrl.append("&");
		redirectUrl.append("action='insertSave'");
		redirectUrl.append("&");
		redirectUrl.append("username='" + username + "'");
		redirectUrl.append("&");
		redirectUrl.append("password='" + password + "'");
		redirectUrl.append("&");
		redirectUrl.append("email='" + email + "'");

		// perform redirection
		autoRegisterDone();
		doRedirection(redirectUrl.toString());
	}



	/**
	 *  Validate auto login
	 */
	private void autoLoginDone() {
		request.getSession().setAttribute("autologin", "1");
	}


	/**
	 *  Validate auto register
	 */
	private void autoRegisterDone() {
		request.getSession().setAttribute("autoRegister", "1");
	}



	/**
	 *  True if autologin has to be performed
	 *
	 *@return    Description of the Returned Value
	 */
	private boolean hasToMakeLogin() {
		HttpSession session = request.getSession();
		Object o = session.getAttribute("autologin");
		if (o == null) {
			return true;
		}
		else {
			return false;
		}
	}


	/**
	 *  True if autoregistering has to be performed
	 *
	 *@return    Description of the Returned Value
	 */
	private boolean hasToMakeRegister() {
		HttpSession session = request.getSession();
		Object o = session.getAttribute("autoRegister");
		if (o == null) {
			return true;
		}
		else {
			return false;
		}
	}

}
