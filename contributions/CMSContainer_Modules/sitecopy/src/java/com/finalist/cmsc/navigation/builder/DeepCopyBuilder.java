package com.finalist.cmsc.navigation.builder;

import com.finalist.cmsc.navigation.NavigationBuilder;
import com.finalist.cmsc.navigation.NavigationManager;
import com.finalist.cmsc.navigation.SiteCopyNavigationItemManager;
import com.finalist.cmsc.navigation.SiteCopyUtil;

public class DeepCopyBuilder extends NavigationBuilder {
   public DeepCopyBuilder() {
      NavigationManager.registerNavigationManager(new SiteCopyNavigationItemManager());
   }
   @Override
   protected String getFragmentField() {
      return SiteCopyUtil.FRAGMENTFIELD;
   }

   @Override
   protected String getNameFieldname() {
      return SiteCopyUtil.TITLE;
   }

   @Override
   protected boolean isRoot() {
      return false;
   }

}
