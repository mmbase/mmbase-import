package com.finalist.newsletter.builder;

import com.finalist.cmsc.navigation.NavigationTreeItemRenderer;
import com.finalist.cmsc.portalImpl.PageNavigationItemManager;
import com.finalist.newsletter.tree.NewsletterPublicationTreeItemRenderer;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterPublicationNavigationItemManager extends NewsletterNavigationItemManager {

   private NavigationTreeItemRenderer treeRenderer = new NewsletterPublicationTreeItemRenderer();

   @Override
   public String getTreeManager() {
      return NewsletterUtil.NEWSLETTERPUBLICATION;
   }

   @Override
   public NavigationTreeItemRenderer getTreeRenderer() {
      return treeRenderer;
   }

}
