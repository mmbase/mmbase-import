package com.finalist.newsletter.builder;

import com.finalist.cmsc.navigation.NavigationTreeItemRenderer;
import com.finalist.cmsc.navigation.PortletUtil;
import com.finalist.cmsc.navigation.NavigationItemRenderer;
import com.finalist.cmsc.portalImpl.PageNavigationItemManager;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.newsletter.tree.NewsletterPublicationTreeItemRenderer;
import com.finalist.newsletter.util.NewsletterUtil;
import com.finalist.newsletter.beans.om.Newsletter;
import com.finalist.newsletter.beans.om.Publication;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.RelationList;
import org.mmbase.bridge.RelationIterator;
import org.mmbase.bridge.Relation;
import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

public class NewsletterPublicationNavigationItemManager extends NewsletterNavigationItemManager {

   private NavigationTreeItemRenderer treeRenderer = new NewsletterPublicationTreeItemRenderer();
   private NavigationItemRenderer renderer = new PublicationNavigationRenderer();

      public Class<? extends NavigationItem> getItemClass() {
      return Publication.class;
   }

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
