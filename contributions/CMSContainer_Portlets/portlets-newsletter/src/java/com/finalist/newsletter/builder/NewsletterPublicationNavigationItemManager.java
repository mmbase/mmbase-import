package com.finalist.newsletter.builder;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

import org.mmbase.bridge.*;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.*;
import com.finalist.newsletter.beans.om.Publication;
import com.finalist.newsletter.tree.NewsletterPublicationTreeItemRenderer;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterPublicationNavigationItemManager extends NewsletterNavigationItemManager {

   private NavigationTreeItemRenderer treeRenderer = new NewsletterPublicationTreeItemRenderer();
   private NavigationItemRenderer renderer = new PublicationNavigationRenderer();

      @Override
      public Class<? extends NavigationItem> getItemClass() {
      return Publication.class;
   }

   @Override
   public NavigationItem loadNavigationItem(Integer key, Node node) {

      Publication publication = MMBaseNodeMapper.copyNode(node, Publication.class);

      RelationList rellist = PortletUtil.getPortletRelations(node);
      RelationIterator r = rellist.relationIterator();
      while (r.hasNext()) {
         Relation relation = r.nextRelation();
         Node relatedPortletNode = relation.getDestination();

         String layoutid = relation.getStringValue(PortletUtil.LAYOUTID_FIELD);
         publication.addPortlet(layoutid, relatedPortletNode.getNumber());
      }

      loadLayout(node, publication);
      loadStylesheet(node, publication);
      loadPageImages(node, publication);
      return publication;
   }

   @Override
   public NavigationItemRenderer getRenderer() {
      return renderer;
   }

   @Override
   public String getTreeManager() {
      return NewsletterUtil.NEWSLETTERPUBLICATION;
   }

   @Override
   public NavigationTreeItemRenderer getTreeRenderer() {
      return treeRenderer;
   }

}
