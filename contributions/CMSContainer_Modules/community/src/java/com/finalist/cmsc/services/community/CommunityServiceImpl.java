package com.finalist.cmsc.services.community;

/**
 * CommunityServiceImpl, a CMSc service class.
 * @author Remco Bos
 */

import javax.security.auth.login.LoginContext;
import java.util.*;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationManager;

import javax.portlet.PortletSession;
import javax.servlet.ServletContext;

import com.finalist.cmsc.services.community.HibernateCommunityService;

public class CommunityServiceImpl extends CommunityService {

    private static Log log = LogFactory.getLog(CommunityServiceImpl.class);

    public boolean loginUser(ActionRequest request, ActionResponse response, String userName, String password) {
        if (userName == null) {
            userName = "";
        }
        if (password == null) {
            password = "";
        }
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userName, password);
        // Place the last username attempted into PortletSession for views
        request.getPortletSession().setAttribute("ACEGI_SECURITY_LAST_USERNAME", userName);

        boolean loginSuccesfull = false;
        try {
            Authentication authentication = getAuthenticationManager().authenticate(authRequest);
            loginSuccesfull = authentication.isAuthenticated();
        } catch (AuthenticationException ae) {
            log.info("Authentication attempt failed for user " + userName);
        }
        return loginSuccesfull;
    }

    private AuthenticationManager getAuthenticationManager() {
// typically a Spring WebApplicationContext is retrieved from the HttpSession, in a portal environment it will be different. TODO FINDOUT getWewApplicationContext for portlets
//        ServletContext servletContext = portletRequest.
//        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        return null;

    }


    public boolean logoutUser(ActionRequest request, ActionResponse response) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        authentication.setAuthenticated(false);
        return true;
    }
    // Yikes!! A new Spring context is loaded every time a property is retrieved..

    public Map<String, Map<String, String>> getUserProperty(String userName) {
        aC = new ClassPathXmlApplicationContext("applicationContext.xml");
        hibservice = (HibernateCommunityService) aC.getBean("serviceCommunity");
        return hibservice.getUserProperty(userName);
    }

    public Map<String, Map<String, List<String>>> getPreferences(String module, String userId, String key, String value) {
        aC = new ClassPathXmlApplicationContext("applicationContext.xml");
        hibservice = (HibernateCommunityService) aC.getBean("serviceCommunity");
        return hibservice.getPreferences(module, userId, key, value);
    }

    public void createPreference(String module, String userId, String key, List<String> values) {
        aC = new ClassPathXmlApplicationContext("applicationContext.xml");
        hibservice = (HibernateCommunityService) aC.getBean("serviceCommunity");
        hibservice.createPreference(module, userId, key, values);
    }

    public void removePreferences(String module, String userId, String key) {
        aC = new ClassPathXmlApplicationContext("applicationContext.xml");
        hibservice = (HibernateCommunityService) aC.getBean("serviceCommunity");
        hibservice.removePreferences(module, userId, key);
    }
}
