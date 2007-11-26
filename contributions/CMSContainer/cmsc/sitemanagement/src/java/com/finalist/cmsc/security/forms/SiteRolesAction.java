package com.finalist.cmsc.security.forms;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;

/**
 * @author Nico Klasens
 */
public class SiteRolesAction extends RolesAction {

   @Override
   protected void setGroupRights(Cloud cloud, RolesForm groupForm, Node groupNode) {
      NavigationUtil.setGroupRights(cloud, groupNode, groupForm.getRoles());
   }


   @Override
   protected RolesInfo getRolesInfo(Cloud cloud, Node group) {
      return NavigationUtil.getRolesInfo(cloud, group);
   }

}