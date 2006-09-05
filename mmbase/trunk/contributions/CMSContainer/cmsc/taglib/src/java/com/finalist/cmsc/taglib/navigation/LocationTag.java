/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.navigation;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.beans.om.Site;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.taglib.CmscTag;

/**
 * A tag determine to locate in the website for a given type. This tag will
 * iterator over the breadcrumb trail backwards till it finds a node of the
 * given type.
 * 
 * @author Wouter Heijke
 * @author R.W. van 't Veer
 * @version $Revision: 1.2 $
 */
public class LocationTag extends CmscTag {

	/**
	 * JSP variable name.
	 */
	private String var;

	/**
	 * JSP variable name.
	 */
	private String sitevar;

	/**
	 * Find and put location in variable.
	 */
	public void doTag() throws JspException, IOException {
		PageContext ctx = (PageContext) getJspContext();
		HttpServletRequest req = (HttpServletRequest) ctx.getRequest();

		String path = getPath();
        Page result = SiteManagement.getPageFromPath(path);

		// handle result
		if (result == null) {
			req.removeAttribute(var);
		} else {
			req.setAttribute(var, result);
		}

		if (sitevar != null) {
			Site site = SiteManagement.getSiteFromPath(path);
			if (site == null) {
				req.removeAttribute(sitevar);
			} else {
				req.setAttribute(sitevar, site);
			}
		}
	}

	/**
	 * Set the JSP variable name the URL choose be passed on to.
	 * 
	 * @param var the JSP variable name
	 */
	public void setVar(String var) {
		this.var = var;
	}

	public void setSitevar(String sitevar) {
		this.sitevar = sitevar;
	}

}
