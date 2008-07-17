package com.finalist.cmsc.subsite.builder;

import com.finalist.cmsc.navigation.NavigationBuilder;
import com.finalist.cmsc.navigation.NavigationManager;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.subsite.SubSiteNavigationItemManager;

/**
 * @author Jacobjob
 */
public class SubSiteBuilder extends NavigationBuilder {
    
    @Override
    protected String getNameFieldname() {
        return PagesUtil.TITLE_FIELD;
    }
	
	public SubSiteBuilder() {
		NavigationManager.registerNavigationManager(new SubSiteNavigationItemManager());
	}

    @Override
    protected String getFragmentField() {
        return PagesUtil.FRAGMENT_FIELD;
    }

    @Override
    protected boolean isRoot() {
        return false;
    }

}
