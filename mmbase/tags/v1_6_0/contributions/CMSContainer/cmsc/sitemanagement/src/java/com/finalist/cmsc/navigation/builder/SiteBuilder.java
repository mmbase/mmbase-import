/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.navigation.builder;

import com.finalist.cmsc.navigation.*;


public class SiteBuilder extends NavigationBuilder {

    @Override
    protected String getNameFieldname() {
        return PagesUtil.TITLE_FIELD;
    }

    @Override
    protected String getFragmentField() {
        return SiteUtil.FRAGMENT_FIELD;
    }

    @Override
    protected boolean isRoot() {
        return true;
    }

}
