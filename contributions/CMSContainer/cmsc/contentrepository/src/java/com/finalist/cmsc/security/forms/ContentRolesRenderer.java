package com.finalist.cmsc.security.forms;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.UserRole;

/**
 * @author Nico Klasens
 */
public class ContentRolesRenderer extends RolesRenderer {
    
   public ContentRolesRenderer(Cloud cloud, RolesForm form) {
        super(cloud, form);
    }

   protected UserRole getRoleForUser(Node channel) {
       return RepositoryUtil.getRoleForUser(user, channel);
   }

}
