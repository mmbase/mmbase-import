package com.finalist.cmsc.subsite;

import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.navigation.NavigationTreeItemRenderer;
import com.finalist.cmsc.portalImpl.PageNavigationItemManager;
import com.finalist.cmsc.subsite.beans.om.PersonalPage;
import com.finalist.cmsc.subsite.publish.SubSitePublisher;
import com.finalist.cmsc.subsite.tree.PersonalPageTreeItemRenderer;
import com.finalist.cmsc.subsite.util.SubSiteUtil;

public class PersonalPageNavigationItemManager extends PageNavigationItemManager {

	private NavigationTreeItemRenderer treeRenderer = new PersonalPageTreeItemRenderer();

	public String getTreeManager() {
		return SubSiteUtil.PERSONALPAGE;
	}

    public NavigationTreeItemRenderer getTreeRenderer() {
        return treeRenderer;
    }

    public Class<? extends NavigationItem> getItemClass() {
        return PersonalPage.class;
    }
    
    protected Class<? extends Page> getPageClass() {
        return PersonalPage.class;
    }

    public Object getPublisher(Cloud cloud, String type) {
		return new SubSitePublisher(cloud);  
	}
}
