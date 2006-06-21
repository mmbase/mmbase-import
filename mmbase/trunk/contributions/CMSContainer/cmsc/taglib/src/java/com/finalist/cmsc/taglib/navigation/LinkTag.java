/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.navigation;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.portalImpl.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.core.PortalURL;

/**
 * A tag to make URLs to other locations in the website.
 * 
 * @author Wouter Heijke
 * @author R.W. van 't Veer
 * @version $Revision: 1.1 $
 */
public class LinkTag extends SimpleTagSupport {

    /**
	 * Destination.
	 */
	private Object dest;

	/**
	 * JSP variable name.
	 */
	public String var;

	/**
	 * Params added by nested param tag
	 */
	private HashMap params = new HashMap();

	private Page channel;

	public void doTag() throws JspException, IOException {
		PageContext ctx = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

		if (channel != null) {
			String link = SiteManagement.getPageLink(channel, !ServerUtil.useServerName());
			if (link != null) {
                PortalURL u = new PortalURL(request, link);
                String newlink = u.toString();

				// handle body, call any nested tags
				JspFragment frag = getJspBody();
				if (frag != null) {
					StringWriter buffer = new StringWriter();
					frag.invoke(buffer);
				}

				// handle result
				if (var != null) {
					// put in variable
					if (newlink != null) {
						request.setAttribute(var, newlink);
					} else {
						request.removeAttribute(var);
					}
				} else {
					// write
					ctx.getOut().print(newlink);
				}
			} else {
				// log.warn("NO LINK");
			}
		} else {
			// log.warn("NO CHANNEL");
		}
	}

	/**
	 * Set destination to navigate to.
	 * 
	 * @param dest the destination node, list of nodes or comma or slash
	 *        separated node numbers or aliases
	 */
	public void setDest(Object dest) {
		if (dest != null) {
			if (dest instanceof Page) {
				setDestNavChannel((Page) dest);
			} else if (dest instanceof Integer) {
				setDestInteger((Integer) dest);
			} else if (dest instanceof String) {
				setDestString((String) dest);
			} else {
				throw new IllegalArgumentException("only Page, integer or string allowed: " + dest.getClass());
			}
		}
	}

	/**
	 * Set destination node to navigate to.
	 * 
	 * @param n the node
	 */
	private void setDestNavChannel(Page n) {
		// TODO WOUTZ
	}

	/**
	 * Set destination node number to navigate to.
	 * 
	 * @param n the node number
	 */
	private void setDestInteger(Integer n) {
		channel = SiteManagement.getPage(n.intValue());
	}

	/**
	 * Set the destination node path to navigate to.
	 * 
	 * @param s comma, slash or space separated list of node numbers and/or
	 *        aliases
	 */
	private void setDestString(String s) {
		channel = SiteManagement.getPage(Integer.parseInt(s));
	}

	public void setVar(String var) {
		this.var = var;
	}

	protected void addParam(String name, Object value) {
		if (name != null && name.length() > 0) {
			params.put(name, value);
		}
	}
}
