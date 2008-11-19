/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Page;

/**
 * List the available Pages
 * 
 * @author Wouter Heijke
 */
public class ListPagesTag extends ListNavigationItemsTag {

    @Override
    protected Class<? extends NavigationItem> getChildNavigationClass() {
        return Page.class;
    }
}
