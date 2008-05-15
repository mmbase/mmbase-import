package com.finalist.newsletter.builder;

import com.finalist.cmsc.navigation.NavigationTreeItemRenderer;
import com.finalist.cmsc.navigation.NavigationItemRenderer;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.navigation.PortletUtil;
import com.finalist.cmsc.portalImpl.PageNavigationItemManager;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Page;
import com.finalist.newsletter.tree.NewsletterTreeItemRenderer;
import com.finalist.newsletter.util.NewsletterUtil;
import com.finalist.newsletter.beans.om.Newsletter;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

public class NewsletterNavigationItemManager extends PageNavigationItemManager {
   private static Logger log = Logging.getLoggerInstance(NewsletterNavigationItemManager.class.getName());

   private NavigationItemRenderer renderer = new NewsletterNavigationRenderer();
   private NavigationTreeItemRenderer treeRenderer = new NewsletterTreeItemRenderer();

   public Class<? extends NavigationItem> getItemClass() {
      return Newsletter.class;
   }

   public NavigationItem loadNavigationItem(Integer key, Node node) {

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
