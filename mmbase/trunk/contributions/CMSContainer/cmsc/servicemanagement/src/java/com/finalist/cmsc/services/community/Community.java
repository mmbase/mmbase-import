package com.finalist.cmsc.services.community;

import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.finalist.cmsc.services.ServiceManager;
import com.finalist.cmsc.services.community.CommunityService;

/**
 * Community, this is a CMSc service class.
 * This class is the abstract service class that wil be implemented
 * by the "CommunityServiceMysqlImpl".
 * In this class comes the request from a portlet or module and will be
 * redirected to the "CommunityServiceMysqlImpl".
 * 
 * @author menno menninga
 */
public class Community {
   private final static CommunityService cService = (CommunityService) ServiceManager
         .getService(CommunityService.class);


   public static boolean loginUser(ActionRequest request, ActionResponse response) {
      return cService.loginUser(request, response);
   }


   public static boolean logoutUser(/** HttpServletRequest HttpRequest, * */
   ActionRequest request, ActionResponse response) {
      return cService.logoutUser(/** HttpRequest, * */
      request, response);
   }
   
   public Map<String, Map<String,List<String>>> getPreferences(String module, String userId, String key, String value){
      return cService.getPreferences(module, userId, key, value);
   }
   
   public void createPreference(String module, String userId, String key, List<String> values){
      cService.createPreference(module, userId, key, values);
   }
   
   public void removePreferences(String module, String userId, String key){
      cService.removePreferences(module, userId, key);
   }
   
   public Map<String, Map<String, String>> getUserProperty(String userName){
      return cService.getUserProperty(userName);
   }
}
