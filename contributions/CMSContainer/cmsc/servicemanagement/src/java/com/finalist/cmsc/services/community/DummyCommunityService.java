package com.finalist.cmsc.services.community;

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
}
