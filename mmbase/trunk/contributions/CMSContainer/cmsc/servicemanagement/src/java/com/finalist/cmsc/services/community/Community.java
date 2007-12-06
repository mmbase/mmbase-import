package com.finalist.cmsc.services.community;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.finalist.cmsc.services.ServiceManager;
import com.finalist.cmsc.services.community.CommunityService;

public class Community {
   private final static CommunityService cService = (CommunityService) ServiceManager
         .getService(CommunityService.class);


   public static boolean loginUser(ActionRequest request, ActionResponse response, String userText, String passText) {
      return cService.loginUser(request, response, userText, passText);
   }


   public static boolean logoutUser(/** HttpServletRequest HttpRequest, * */
   ActionRequest request, ActionResponse response) {
      return cService.logoutUser(/** HttpRequest, * */
      request, response);
   }
}
