package com.finalist.cmsc.security.forms;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.security.UserRole;

/**
 * @author Nico Klasens
 */
public class SiteRolesRenderer extends RolesRenderer {

   public SiteRolesRenderer(HttpServletRequest request, Cloud cloud, RolesForm form) {
      super(request, cloud, form);
   }


   @Override
   protected UserRole getRole(Node page) {
      return NavigationUtil.getRole(user, page);
   }

}
