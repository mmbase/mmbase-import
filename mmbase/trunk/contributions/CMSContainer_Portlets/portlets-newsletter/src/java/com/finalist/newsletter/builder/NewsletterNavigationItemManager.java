package com.finalist.newsletter.builder;

import com.finalist.cmsc.navigation.NavigationTreeItemRenderer;
import com.finalist.cmsc.portalImpl.PageNavigationItemManager;
import com.finalist.newsletter.tree.NewsletterTreeItemRenderer;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterNavigationItemManager extends PageNavigationItemManager {

   private NavigationTreeItemRenderer treeRenderer = new NewsletterTreeItemRenderer();

   @Override
   public String getTreeManager() {
      return NewsletterUtil.NEWSLETTER;
   }

   @Override
   public NavigationTreeItemRenderer getTreeRenderer() {
      return treeRenderer;
   }

}
