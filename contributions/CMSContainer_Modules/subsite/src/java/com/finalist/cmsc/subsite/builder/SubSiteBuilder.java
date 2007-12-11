package com.finalist.cmsc.subsite.builder;

import com.finalist.cmsc.navigation.NavigationBuilder;
import com.finalist.cmsc.navigation.NavigationManager;
import com.finalist.cmsc.subsite.SubSiteNavigationItemManager;
import com.finalist.cmsc.subsite.util.SubSiteUtil;

/**
 * @author Freek Punt
 */
public class SubSiteBuilder extends NavigationBuilder {
    
    @Override
    protected String getNameFieldname() {
        return SubSiteUtil.TITLE_FIELD;
    }
	
	public SubSiteBuilder() {
		NavigationManager.registerNavigationManager(new SubSiteNavigationItemManager());
	}

    @Override
    protected String getFragmentField() {
        return SubSiteUtil.FRAGMENT_FIELD;
    }

    @Override
    protected boolean isRoot() {
        return false;
    }

}
