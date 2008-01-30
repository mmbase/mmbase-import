package com.finalist.cmsc.services.community;

import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.finalist.cmsc.services.Service;

/**
 * CommunityService, this is a CMSc service class.
 * This class is the abstract service class that wil be used by the
 * community class.
 * 
 * @author menno menninga
 */
public abstract class CommunityService extends Service {
	
   public abstract boolean loginUser(ActionRequest request, ActionResponse response);

   public abstract boolean logoutUser(ActionRequest request, ActionResponse response);

   public abstract Map<String, Map<String,List<String>>> getPreferences(String module, String userId, String key, String value);
   
   public abstract void createPreference(String module, String userId, String key, List<String> values);
   
   public abstract void removePreferences(String module, String userId, String key);
   
   public abstract Map<String, Map<String, String>> getUserProperty(String userName);
}
