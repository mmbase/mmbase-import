package com.finalist.cmsc.services.community;

/**
 * CommunityServiceImpl, a CMSc service class.
 * @author Remco Bos
 */

import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.finalist.cmsc.services.Properties;
import com.finalist.cmsc.services.community.preferences.PreferenceService;

public class CommunityServiceImpl extends CommunityService {

    private static Log log = LogFactory.getLog(CommunityServiceImpl.class);
    
	private WebApplicationContext applicationContext;
	private AuthenticationManager authenticationManager;
    private PreferenceService preferenceService;
    
    @Override
	protected void init(ServletConfig config, Properties properties) throws Exception {
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
		authenticationManager = (AuthenticationManager)applicationContext.getBean("authenticationManager");
		preferenceService = (PreferenceService)applicationContext.getBean("preferenceService");
	}

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
            Authentication authentication = authenticationManager.authenticate(authRequest);
            loginSuccesfull = authentication.isAuthenticated();
        } catch (AuthenticationException ae) {
            log.info("Authentication attempt failed for user " + userName);
        }
        return loginSuccesfull;
    }


    public boolean logoutUser(ActionRequest request, ActionResponse response) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        authentication.setAuthenticated(false);
        return true;
    }

    public Map<String, Map<String, String>> getUserProperty(String userName) {
    	return null;
//    	preferenceService..getUserProperty(userName);
    }

    public Map<String, Map<String, List<String>>> getPreferences(String module, String userId, String key, String value) {
    	return null;
//    	preferenceService.getPreferences(userName);
    }

    public void createPreference(String module, String userId, String key, List<String> values) {
//    	preferenceService.createPreferences(module, userName, key, value);
    }

    public void removePreferences(String module, String userId, String key) {
//    	preferenceService.deletePreferences(module, userId, key);
    }
}
