package com.finalist.newsletter.builder;

import com.finalist.cmsc.navigation.NavigationBuilder;
import com.finalist.cmsc.navigation.NavigationManager;
import com.finalist.cmsc.navigation.PagesUtil;

public class NewsletterPublicationBuilder extends NavigationBuilder {

   public NewsletterPublicationBuilder() {
      NavigationManager.registerNavigationManager(new NewsletterPublicationNavigationItemManager());
   }

   @Override
   protected String getFragmentField() {
      return PagesUtil.FRAGMENT_FIELD;
   }

   @Override
   protected String getNameFieldname() {
      return PagesUtil.TITLE_FIELD;
   }

   @Override
   protected boolean isRoot() {
      return false;
   }

}
