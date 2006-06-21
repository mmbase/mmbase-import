/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.navigation;

import java.util.List;

import com.finalist.cmsc.portalImpl.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.taglib.AbstractListTag;

/**
 * path of pages
 */
public class PathTag extends AbstractListTag {

	protected List getList() {
		String path = getPath();
        return SiteManagement.getListFromPath(path);
	}
}
