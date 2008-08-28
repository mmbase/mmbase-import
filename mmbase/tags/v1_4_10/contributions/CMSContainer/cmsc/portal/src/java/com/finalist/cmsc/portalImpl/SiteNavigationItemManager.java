package com.finalist.cmsc.portalImpl;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.navigation.NavigationTreeItemRenderer;
import com.finalist.cmsc.navigation.SiteUtil;
import com.finalist.cmsc.navigation.tree.SiteTreeItemRenderer;

public class SiteNavigationItemManager extends PageNavigationItemManager {
    
	private NavigationTreeItemRenderer treeRenderer = new SiteTreeItemRenderer();
	
    @Override
	public String getTreeManager() {
		return SiteUtil.SITE;
	}

    @Override
    public boolean isRoot() {
        return true;
    }
	
	@Override
    public NavigationTreeItemRenderer getTreeRenderer() {
        return treeRenderer;
    }
	
    @Override
    protected Class<? extends Page> getPageClass() {
        return Site.class;
    }

    @Override
    public Class<? extends NavigationItem> getItemClass() {
        return Site.class;
    }

}
