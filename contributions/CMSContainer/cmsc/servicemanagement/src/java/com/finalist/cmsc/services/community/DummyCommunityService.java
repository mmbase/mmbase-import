package com.finalist.cmsc.services.community;

import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

public class DummyCommunityService extends CommunityService {

   public boolean loginUser(ActionRequest request, ActionResponse response, String username, String password) {
      return false;
   }

   public boolean logoutUser(/** HttpServletRequest HttpRequest, * */
   ActionRequest request, ActionResponse response) {
      return false;
   }
   
   public Map<String, Map<String,List<String>>> getPreferences(String module, String userId, String key, String value){
      return (null);
   }
   
   public void createPreference(String module, String userId, String key, List<String> values){
      
   }
   
   public void removePreferences(String module, String userId, String key){
      
   }
}
