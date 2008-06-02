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
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.taglib.AbstractListTag;

/**
 * path of pages
 * 
 * valid attributes for this tag are:
 * <ul>
 * <li>mode := <strong>menu</strong>|hidden|all</li>
 * <li>includeSite := <strong>true</strong>|false</li>
 * <li>page := 1..n</li>
 * </ul>
 * 
 * Examples:
 * <cmsc:path var="listPath" />
 * <cmsc:path var="listPath" mode="all" includeSite="false" page="${myPage}"/>
 * 
 */
public class PathTag extends AbstractListTag<Page> {

    private static final String MODE_ALL = "all";
    private static final String MODE_HIDDEN = "hidden";
    private static final String MODE_MENU = "menu";
    
    private String mode = MODE_MENU;
    private boolean includeSite = true;
    private int page;

	public boolean isIncludeSite() {
		return includeSite;
	}

	/**
	 * Controls whether the name of the site should be included as part of the path. 
	 * 
	 * @param includeSite true to include the name of the site (default), false otherwise
	 */
	public void setIncludeSite(boolean includeSite) {
		this.includeSite = includeSite;
	}

	public String getMode() {
		return mode;
	}

	/**
	 * Controls which part of path should be shown depending whether it is in the menu or not.
	 * <br>menu: show all up to the first one which is not part of the menu (default)
	 * <br>hidden: show all up to the first one which is in the menu
	 * <br>all: show all
	 * 
	 * @param mode String literal menu (default), hidden or all 
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	public int getPage() {
		return page;
	}

	/**
	 * Specify the page for which the path should be constructed, if zero (default) the current path is used
	 * 
	 * @param page positive integer indicating a page or zero (default) for the current page
	 */
	public void setPage(int page) {
		this.page = page;
	}

	@Override
    protected List<Page> getList() {
		String path;
		
		if (page > 0) {
			// get path for a specific page
			path = getPathForPage();
		} else {
			// get path for current page
			path = getPath();
		}
		if (path == null) {
			return null;
		}
        
        List<Page> pages = SiteManagement.getListFromPath(path);
        if (pages == null ) {
        	return pages;
        }
        
        if (MODE_MENU.equalsIgnoreCase(mode)) {
        	boolean hideChildren = false;
        	for (Iterator<? extends Page> iter = pages.iterator(); iter.hasNext();) {
        		Page page = iter.next();
        		if (hideChildren || !page.isInmenu()) {
        			iter.remove();
        			hideChildren = true;
        		}
        	}
        } else if (MODE_HIDDEN.equalsIgnoreCase(mode)) {
        	boolean showChildren = false;
        	for (Iterator<? extends Page> iter = pages.iterator(); iter.hasNext();) {
        		Page page = iter.next();
        		if (showChildren || page.isInmenu()) {
        			iter.remove();
        			showChildren = true;
        		}
        	}
        }

        // remove the first entry if the site itself should not be shown
        if (!includeSite && pages.size() > 0) {
        		pages.remove(0);
        }
        
        return pages;
	}
	
	private String getPathForPage() {
		String path = null;

		Page tmpPage = SiteManagement.getPage(page);
		if (tmpPage != null) {
			path = SiteManagement.getPath(tmpPage, true);
		}
		
		return path;
	}

}
