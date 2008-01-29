/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.community;

import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.ServletConfig;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.finalist.cmsc.services.Properties;
import com.finalist.cmsc.services.community.preferences.PreferenceService;

/**
 * CommunityServiceImpl, a CMSc service class.
 * 
 * @author Remco Bos
 */
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

    public Map<Long, Map<String, String>> getPreferencesByModule(String userId) {
    	return preferenceService.getPreferencesByModule(userId);
    }

    public Map<String, Map<String, String>> getPreferencesByUserId(String userId) {
    	return preferenceService.getPreferencesByUserId(userId);
    }

    public Map<String, String> getPreferences(String module, String userId, String key) {
    	return preferenceService.getPreferences(module, userId, key);
    }

    public void createPreference(String module, String userId, String key, String value) {
    	preferenceService.createPreference(module, userId, key, value);
    }

    public void deletePreference(String module, String userId, String key, String value) {
    	preferenceService.deletePreference(module, userId, key, value);
    }

    //TODO: replace the previous methods by methods who accept the following
    //      properties!
	@Override
	public void createPreference(String module, String userId, String key,
			List<String> values) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Map<String, List<String>>> getPreferences(String module,
			String userId, String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Map<String, String>> getUserProperty(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removePreferences(String module, String userId, String key) {
		// TODO Auto-generated method stub
		
	}
}
