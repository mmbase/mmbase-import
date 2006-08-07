/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.navigation;

import java.util.Iterator;
import java.util.List;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.portalImpl.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.taglib.AbstractListTag;

/**
 * path of pages
 */
public class PathTag extends AbstractListTag {

    private static final String MODE_ALL = "all";
    private static final String MODE_HIDDEN = "hidden";
    private static final String MODE_MENU = "menu";
    
    private String mode = MODE_MENU;

	protected List<? extends Page> getList() {
		String path = getPath();
        
        List<? extends Page> pages = SiteManagement.getListFromPath(path);
        
        if (pages != null ) {
            if (MODE_MENU.equalsIgnoreCase(mode) || MODE_ALL.equalsIgnoreCase(mode)) {
                boolean hideChildren = false;
                for (Iterator iter = pages.iterator(); iter.hasNext();) {
                    Page page = (Page) iter.next();
                    if (hideChildren || !page.isInmsenu()) {
                        iter.remove();
                        hideChildren = true;
                    }
                }
            }
            if (MODE_HIDDEN.equalsIgnoreCase(mode) || MODE_ALL.equalsIgnoreCase(mode)) {
                boolean showChildren = false;
                for (Iterator iter = pages.iterator(); iter.hasNext();) {
                    Page page = (Page) iter.next();
                    if (showChildren || page.isInmsenu()) {
                        iter.remove();
                        showChildren = true;
                    }
                }
            }
        }

        return pages;
	}
}
