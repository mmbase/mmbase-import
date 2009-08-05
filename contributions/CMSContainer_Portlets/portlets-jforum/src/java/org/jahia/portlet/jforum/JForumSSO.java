package org.jahia.portlet.jforum;

import net.jforum.context.RequestContext;
import net.jforum.entities.UserSession;
import net.jforum.sso.SSO;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

public class JForumSSO implements SSO {

   public String authenticateUser(RequestContext requestContext) {
      // HttpSession session = JForum.getRequest().getSession();
      String user = null;
      if (requestContext.getSessionContext().getAttribute("username") != null) {
         user = (String) requestContext.getSessionContext().getAttribute(
                  "username");
      }
      return user;
   }

   public boolean isSessionValid(UserSession userSession, RequestContext requestContext) {

      String remoteUser = null;
      if (requestContext.getSessionContext().getAttribute("username") != null)
         remoteUser = (String) requestContext.getSessionContext().getAttribute("username"); // jforum username

      // user has since logged out
      if (remoteUser == null && userSession.getUserId() != SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
         return false;

         // user has since logged in
      } else
      if (remoteUser != null && userSession.getUserId() == SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
         return false;

         // user has changed user
      } else if (remoteUser != null
               && !remoteUser.equals(userSession.getUsername())) {
         return false;
      }
      return true; // myapp user and forum user the same
   }

}
