package com.finalist.cmsc.security.forms;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.repository.RepositoryUtil;

/**
 * @author Nico Klasens
 */
public class ContentRolesAction extends RolesAction {

    protected void setGroupRights(Cloud cloud, RolesForm groupForm, Node groupNode) {
        RepositoryUtil.setGroupRights(cloud, groupNode, groupForm.getRoles());
    }

    protected RolesInfo getRolesInfo(Cloud cloud, Node group) {
        return RepositoryUtil.getRolesInfo(cloud, group);
    }
}