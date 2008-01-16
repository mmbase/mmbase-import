package com.finalist.cmsc.services.community;

import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.finalist.cmsc.services.Service;

public abstract class CommunityService extends Service {
   public abstract boolean loginUser(ActionRequest request, ActionResponse response, String userText, String passText);

   public abstract boolean logoutUser(ActionRequest request, ActionResponse response);
   
                   //key/userId    values
   public abstract Map<String, Map<String,List<String>>> getPreferences(String module, String userId, String key, String value);
   
   public abstract void createPreference(String module, String userId, String key, List<String> values);
   
   public abstract void removePreferences(String module, String userId, String key);
}
