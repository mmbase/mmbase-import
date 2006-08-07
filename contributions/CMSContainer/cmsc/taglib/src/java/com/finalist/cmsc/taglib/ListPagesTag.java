/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib;

import java.util.Iterator;
import java.util.List;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.beans.om.Site;
import com.finalist.cmsc.portalImpl.services.sitemanagement.SiteManagement;

/**
 * List the available Pages
 * 
 * @author Wouter Heijke
 */
public class ListPagesTag extends AbstractListTag {

    private static final String MODE_ALL = "all";
    private static final String MODE_HIDDEN = "hidden";
    private static final String MODE_MENU = "menu";
    
    private String mode = MODE_MENU;
    
	protected List<? extends Page> getList() {
        List<? extends Page> pages = null;
		if (origin != null) {
			if (origin instanceof Site) {
                pages = SiteManagement.getPages((Site)origin);
	        } else if (origin instanceof Page) {
                pages =  SiteManagement.getPages((Page)origin);
	        }
		} else {
            pages = SiteManagement.getSites();
		}
        if (pages != null ) {
            if (MODE_MENU.equalsIgnoreCase(mode) || MODE_ALL.equalsIgnoreCase(mode)) {
                for (Iterator iter = pages.iterator(); iter.hasNext();) {
                    Page page = (Page) iter.next();
                    if (!page.isInmsenu()) {
                        iter.remove();
                    }
                }
            }
            if (MODE_HIDDEN.equalsIgnoreCase(mode) || MODE_ALL.equalsIgnoreCase(mode)) {
                for (Iterator iter = pages.iterator(); iter.hasNext();) {
                    Page page = (Page) iter.next();
                    if (page.isInmsenu()) {
                        iter.remove();
                    }
                }
            }
        }
		return pages;
	}
    
    public String getMode() {
        return mode;
    }
    
    public void setMode(String mode) {
        this.mode = mode;
    }
    
}
