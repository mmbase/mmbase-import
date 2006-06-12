/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib;

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

	protected List getList() {
		if (origin != null) {
			if (origin instanceof Site) {
				return SiteManagement.getPages((Site)origin);
	        } else if (origin instanceof Page) {
				return SiteManagement.getPages((Page)origin);
	        }
		} else {
			return SiteManagement.getSites();
		}
		return null;
	}
}
