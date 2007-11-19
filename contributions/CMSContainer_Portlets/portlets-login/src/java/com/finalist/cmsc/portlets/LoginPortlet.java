package com.finalist.cmsc.portlets;
	
import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.services.community.Community;

/**
 * Fulltext login portlet
 * 
 * @author Menno Menninga
 * @version $Revision: 1.1 $
 */
public class LoginPortlet extends CmscPortlet {
	
	private static final Log log = LogFactory.getLog(LoginPortlet.class);
	
	private static final String ACTION_PARAM = "action";
	
	private static final String USER_TEXT = "userText";
	
	private static final String PASS_TEXT = "passText";
	
	
	public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		
		String action = request.getParameter(ACTION_PARAM);
        getLogger().info("Action: " + action);
        if (action.equals("login")){
    		executeLogin(request, response, request.getParameter(USER_TEXT), request.getParameter(PASS_TEXT));
    	}
        if (action.equals("logout")){
        	executeLogout(/**HttpRequest, **/request, response);
        	
        }
        response.setPortletMode(PortletMode.VIEW);
        log.error("Unknown action: '" + action + "'");
	}
	
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {		
		
		super.doView(request, response);
	}
	
	private void executeLogin(ActionRequest request, ActionResponse response, String userText, String passText) throws PortletException, IOException {
		
		String configFileLocation = getPortletContext().getRealPath("/WEB-INF/config/jaas.config");
		getLogger().info(configFileLocation);
		System.setProperty("java.security.auth.login.config", configFileLocation );
		
		boolean loginSuccesfull = Community.loginUser(request, response, userText, passText);
		
		getLogger().info(loginSuccesfull);
		
		if (loginSuccesfull == false){
			response.setPortletMode(PortletMode.VIEW);
		}
		if (loginSuccesfull == true){
			getLogger().info("Gelukt als het goed is");
			log.info(request.getPortletSession().getAttribute("firstName"));
			log.info(request.getPortletSession().getAttribute("lastName"));
		}
	}
	
	private void executeLogout(/**HttpServletRequest HttpRequest, **/ActionRequest request, ActionResponse response) throws PortletException, IOException{
		
		log.info(request.getPortletSession());
		
		boolean logoutSuccesfull = Community.logoutUser(/**HttpRequest, **/request, response);
		
		if (logoutSuccesfull == false){
			log.info("er is iets fout gegaan");
			
			response.setPortletMode(PortletMode.VIEW);
		}
		if (logoutSuccesfull == true){
			log.info("Gelukt als het goed is");
		}
	}
}