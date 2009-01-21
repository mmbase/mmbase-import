/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.navigation.builder;

import com.finalist.cmsc.navigation.NavigationBuilder;
import com.finalist.cmsc.navigation.PagesUtil;


public class PageBuilder extends NavigationBuilder {

    protected String getNameFieldname() {
        return PagesUtil.TITLE_FIELD;
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
