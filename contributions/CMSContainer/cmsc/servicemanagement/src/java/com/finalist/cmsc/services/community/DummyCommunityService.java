package com.finalist.cmsc.services.community;

import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

/**
 * DummyCommunityService, this is a CMSc service class.
 * This class contains dummy methods that stand in the real uses service class.
 * 
 * @author menno menninga
 */
public class DummyCommunityService extends CommunityService {

   public boolean loginUser(ActionRequest request, ActionResponse response) {
      return false;
   }

   public boolean logoutUser(ActionRequest request, ActionResponse response) {
      return false;
   }
   
   public Map<String, Map<String,List<String>>> getPreferences(String module, String userId, String key, String value){
      return (null);
   }
   
   public void createPreference(String module, String userId, String key, List<String> values){
      
   }
   
   public void removePreferences(String module, String userId, String key){
      
   }
   
   public Map<String, Map<String, String>> getUserProperty(String userName){
      return (null);
   }
}
