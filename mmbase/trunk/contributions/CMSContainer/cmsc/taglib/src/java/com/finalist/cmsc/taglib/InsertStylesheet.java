/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.taglib;

import java.util.List;

import com.finalist.cmsc.portalImpl.services.sitemanagement.SiteManagement;

/**
 * Tag to include stylesheets in Page
 */
public class InsertStylesheet extends AbstractListTag {
        
        protected List getList() {   
            return SiteManagement.getStylesheetForPageByPath(getPath());
        }
	
}
