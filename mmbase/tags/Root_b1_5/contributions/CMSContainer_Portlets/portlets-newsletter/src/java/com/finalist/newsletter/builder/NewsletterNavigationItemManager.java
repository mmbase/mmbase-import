package com.finalist.newsletter.builder;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.portalImpl.PageNavigationItemManager;
import com.finalist.newsletter.beans.om.Newsletter;
import com.finalist.newsletter.tree.NewsletterTreeItemRenderer;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterNavigationItemManager extends PageNavigationItemManager {
   private static Logger log = Logging.getLoggerInstance(NewsletterNavigationItemManager.class.getName());

   private NavigationItemRenderer renderer = new NewsletterNavigationRenderer();
   private NavigationTreeItemRenderer treeRenderer = new NewsletterTreeItemRenderer();

   @Override
   public Class<? extends NavigationItem> getItemClass() {
      return Newsletter.class;
   }

   @Override
   public NavigationItem loadNavigationItem(Node node) {

      Newsletter newsletter = MMBaseNodeMapper.copyNode(node, Newsletter.class);

      RelationList rellist = PortletUtil.getPortletRelations(node);
      RelationIterator r = rellist.relationIterator();
      while (r.hasNext()) {
         Relation relation = r.nextRelation();
         Node relatedPortletNode = relation.getDestination();

         log.debug("portlet='" + relatedPortletNode.getNumber() + "' :"
               + relatedPortletNode.getNodeManager().getName());
         String layoutid = relation.getStringValue(PortletUtil.LAYOUTID_FIELD);
         newsletter.addPortlet(layoutid, relatedPortletNode.getNumber());
      }

      loadLayout(node, newsletter);
      loadStylesheet(node, newsletter);
      loadPageImages(node, newsletter);
      return newsletter;
   }

   @Override
   public NavigationItemRenderer getRenderer() {
      return renderer;
   }

   @Override
   public String getTreeManager() {
      return NewsletterUtil.NEWSLETTER;
   }

   @Override
   public NavigationTreeItemRenderer getTreeRenderer() {
      return treeRenderer;
   }

}
