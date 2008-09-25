package com.finalist.cmsc.security.forms;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;

public class SiteRolesInitAction extends RolesInitAction {

   @Override
   protected RolesInfo getRolesInfo(Cloud cloud, Node group) {
      return NavigationUtil.getRolesInfo(cloud, group);
   }

}