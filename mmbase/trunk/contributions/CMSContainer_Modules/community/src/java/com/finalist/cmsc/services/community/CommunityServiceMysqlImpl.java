package com.finalist.cmsc.services.community;

import javax.security.auth.login.LoginContext;
import javax.security.auth.*;
import java.util.*;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.PortletSession;

public class CommunityServiceMysqlImpl extends CommunityService {
	
	private static Log log = LogFactory.getLog(CommunityServiceMysqlImpl.class);
	
	private PortletSession session;
	
	//@Override
	public boolean loginUser(ActionRequest request, ActionResponse response, String userText, String passText) {
		
		boolean loginSuccesfull;
		
		String firstName = "";
		String lastName = "";
		String emailAdress = "";
	    
		try{
		    PassiveCallbackHandler cbh = new PassiveCallbackHandler(userText, passText);

		    LoginContext lc = new LoginContext("jaasdb", cbh);

		    lc.login();
		    
		    log.info("PortletSession: " + request.getPortletSession());
		    
		    //Subject usb = lc.getSubject();
		    //Subject.doAs(usb, action);		    
		    PortletSession session = request.getPortletSession();
		    
		    //log.info("Subject usb= " + usb);
		    
		    //session.setAttribute("userSubject", usb);
		    
		    Iterator it = lc.getSubject().getPrincipals().iterator();
	        while (it.hasNext()){
	        	String tempPrin = it.next().toString();
	        	//log.info("Testen: " + tempPrin);
	        	String values[]=tempPrin.split(" ");
	        	//log.info("lengte string values[] " + values.length);
	        	for(int i=0; i < values.length; i++) {
	        		if(i == 0){
	    	        	String def = "First name: ";
		        		log.info(def + values[i]);
		        		firstName = values[i];
		        		session.setAttribute("firstName", firstName, PortletSession.APPLICATION_SCOPE);
	        		}
	        		if (i == 1){
	        			String def = "Last name: ";
		        		log.info(def + values[i]);
		        		lastName = values[i];
		        		session.setAttribute("lastName", lastName, PortletSession.APPLICATION_SCOPE);
		    		}
	        		if (i == 2){
	        			String def = "E-mail adres: ";
	        			log.info(def + values[i]);
	        			emailAdress = values[i];
	        			session.setAttribute("emailAdress", emailAdress, PortletSession.APPLICATION_SCOPE);
	        		}
	        	}
	        }
	        session.setAttribute("logout", "false", PortletSession.APPLICATION_SCOPE);
	        
	        it = lc.getSubject().getPublicCredentials(Properties.class).iterator();

	        while (it.hasNext()) 
	        	log.info(it.next().toString());
		    
	        lc.logout();
	        loginSuccesfull = true;
	    } catch (Exception e) {
	    	log.error("Caught Exception: ", e);
	    	loginSuccesfull = false;
	    }
	    
	    return loginSuccesfull;
	}
	
	public boolean logoutUser(/**HttpServletRequest HttpRequest, **/ActionRequest request, ActionResponse response) {
		boolean logoutSuccesfull;
		
	    PortletSession session = request.getPortletSession();

		try {
			session.removeAttribute("firstName", PortletSession.APPLICATION_SCOPE);
			session.removeAttribute("lastName", PortletSession.APPLICATION_SCOPE);
			session.removeAttribute("emailAdress", PortletSession.APPLICATION_SCOPE);
			session.setAttribute("logout", "true", PortletSession.APPLICATION_SCOPE);
			if (request.getPortletSession().getAttribute("firstName") == null){	
				log.info("logout succesvol");
			}
			//Cookie[] cookies = HttpRequest.getCookies();
			//log.debug(cookies);
			logoutSuccesfull = true;
		}
		catch (Exception e){
			log.error("Caught Exception: ", e);
			logoutSuccesfull = false;
		}
		return logoutSuccesfull;
	}
}
