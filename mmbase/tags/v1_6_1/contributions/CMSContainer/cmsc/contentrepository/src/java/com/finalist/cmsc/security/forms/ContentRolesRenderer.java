package com.finalist.cmsc.security.forms;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.UserRole;

/**
 * @author Nico Klasens
 */
public class ContentRolesRenderer extends RolesRenderer {

   public ContentRolesRenderer(HttpServletRequest request, Cloud cloud, RolesForm form) {
      super(request, cloud, form);
   }


   @Override
   protected UserRole getRole(Node channel) {
      return RepositoryUtil.getRole(user, channel);
   }

}
