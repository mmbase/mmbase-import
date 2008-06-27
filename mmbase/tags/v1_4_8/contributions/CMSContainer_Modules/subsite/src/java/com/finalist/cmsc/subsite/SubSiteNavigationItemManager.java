package com.finalist.cmsc.subsite;

import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.navigation.NavigationTreeItemRenderer;
import com.finalist.cmsc.portalImpl.PageNavigationItemManager;
import com.finalist.cmsc.subsite.beans.om.SubSite;
import com.finalist.cmsc.subsite.publish.SubSitePublisher;
import com.finalist.cmsc.subsite.tree.SubSiteTreeItemRenderer;
import com.finalist.cmsc.subsite.util.SubSiteUtil;

public class SubSiteNavigationItemManager extends PageNavigationItemManager {

	private NavigationTreeItemRenderer treeRenderer = new SubSiteTreeItemRenderer();

	public String getTreeManager() {
		return SubSiteUtil.SUBSITE;
	}

    public boolean isRoot() {
        return false;
    }

    public NavigationTreeItemRenderer getTreeRenderer() {
        return treeRenderer;
    }

    public Class<? extends NavigationItem> getItemClass() {
        return SubSite.class;
    }
    
    protected Class<? extends Page> getPageClass() {
        return SubSite.class;
    }
    
    public Object getPublisher(Cloud cloud, String type) {
		return new SubSitePublisher(cloud);  
	}

}
