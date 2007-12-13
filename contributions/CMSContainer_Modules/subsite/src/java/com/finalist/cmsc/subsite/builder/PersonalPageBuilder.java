package com.finalist.cmsc.subsite.builder;

import com.finalist.cmsc.navigation.NavigationBuilder;
import com.finalist.cmsc.navigation.NavigationManager;
import com.finalist.cmsc.subsite.PersonalPageNavigationItemManager;
import com.finalist.cmsc.subsite.util.PersonalPageUtil;

/**
 * @author Freek Punt
 */
public class PersonalPageBuilder extends NavigationBuilder {
    
    @Override
    protected String getNameFieldname() {
        return PersonalPageUtil.TITLE_FIELD;
    }
	
	public PersonalPageBuilder() {
		NavigationManager.registerNavigationManager(new PersonalPageNavigationItemManager());
	}

    @Override
    protected String getFragmentField() {
        return PersonalPageUtil.FRAGMENT_FIELD;
    }

    @Override
    protected boolean isRoot() {
        return false;
    }

}
