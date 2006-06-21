package com.finalist.cmsc.security.forms;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.security.UserRole;

/**
 * @author Nico Klasens
 */
public class SiteRolesRenderer extends RolesRenderer {

    public SiteRolesRenderer(Cloud cloud, RolesForm form) {
        super(cloud, form);
    }

    protected UserRole getRoleForUser(Node page) {
        return NavigationUtil.getRoleForUser(user, page);
    }

    
}
