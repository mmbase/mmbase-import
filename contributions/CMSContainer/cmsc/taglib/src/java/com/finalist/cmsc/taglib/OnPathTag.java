/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.portalImpl.services.sitemanagement.SiteManagement;

/**
 * Checks if a Site or Page is on the path
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class OnPathTag extends LocationTag {

	private Object origin;
	
	public void doTag() throws JspException, IOException {
		String path = getPath();
		List contents = SiteManagement.getListFromPath(path);
		if (contents != null) {
			for (int i = 0; i < contents.size(); i++) {
				Object item = contents.get(i);
				if (item instanceof Page && origin instanceof Page) {
					if (((Page)item).getId() == ((Page)origin).getId()) {
						// handle body, call any nested tags
						JspFragment frag = getJspBody();
						if (frag != null) {
							frag.invoke(null);
						}
					}
				}
			}
		}
	}

	public void setOrigin(Object origin) {
		this.origin = origin;
	}

}
