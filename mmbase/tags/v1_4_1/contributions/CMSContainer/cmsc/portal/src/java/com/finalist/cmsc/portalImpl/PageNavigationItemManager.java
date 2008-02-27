package com.finalist.cmsc.portalImpl;

import java.util.List;
import java.util.Map;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationIterator;
import org.mmbase.bridge.RelationList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.navigation.tree.PageTreeItemRenderer;

public class PageNavigationItemManager implements NavigationItemManager {

    private static Logger log = Logging.getLoggerInstance(PageNavigationItemManager.class.getName());
	
	private NavigationItemRenderer renderer = new PageNavigationRenderer();

	private NavigationTreeItemRenderer treeRenderer = new PageTreeItemRenderer();
	
	public NavigationItemRenderer getRenderer() {
		return renderer;
	}

	public String getTreeManager() {
		return PagesUtil.PAGE;
	}

    public boolean isRoot() {
        return false;
    }

	public NavigationItem loadNavigationItem(Integer key, Node node) {
        if (node == null || !PagesUtil.isPageType(node)) {
            log.debug("Page not found: " + key);
            return null;
        }
        
        Class<? extends Page> clazz = getPageClass();
        
        Page page = MMBaseNodeMapper.copyNode(node, clazz);

        RelationList rellist = PortletUtil.getPortletRelations(node); 
        RelationIterator r = rellist.relationIterator();
        while (r.hasNext()) {
            Relation relation = r.nextRelation();
            Node relatedPortletNode = relation.getDestination();

            log.debug("portlet='" + relatedPortletNode.getNumber() + "' :"
                    + relatedPortletNode.getNodeManager().getName());
            String layoutid = relation.getStringValue(PortletUtil.LAYOUTID_FIELD);
            page.addPortlet(layoutid, relatedPortletNode.getNumber());
        }

        loadLayout(node, page);
        loadStylesheet(node, page);
        loadPageImages(node, page);
        return page;
    }

    private void loadLayout(Node pageNode, Page page) {
        Node layoutNode = PagesUtil.getLayout(pageNode);
        if (layoutNode != null) {
            page.setLayout(layoutNode.getNumber());
        } else {
            log.error("NO LAYOUT");
        }
    }

     private void loadStylesheet(Node pageNode, Page page) {
        NodeList styleNode = PagesUtil.getStylesheet(pageNode);
        if (!styleNode.isEmpty()) {
            for (NodeIterator iter = styleNode.nodeIterator(); iter.hasNext();) {
                Node stylesheetNode = iter.nextNode();
                page.addStylesheet(stylesheetNode.getNumber());
            }
        }
    }

     private void loadPageImages(Node pageNode, Page page) {
         Map<String, List<Integer>> pageImages = PagesUtil.getPageImages(pageNode);
         page.setPageImages(pageImages);
     }

     /**
      * publishing of sites and pages is done by the publish module
      */
    public Object getPublisher(Cloud cloud, String type) {
		return null;
	}

    public NavigationTreeItemRenderer getTreeRenderer() {
        return treeRenderer;
    }

    protected Class<? extends Page> getPageClass() {
        return Page.class;
    }

    public Class<? extends NavigationItem> getItemClass() {
        return Page.class;
    }

   public void deleteNode(Node pageNode) {
      PagesUtil.deletePage(pageNode);
   }

}
