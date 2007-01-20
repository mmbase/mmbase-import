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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.services.search.Search;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.core.PortalURL;

/**
 * A tag to make URLs to other locations in the website.
 * 
 * @author Wouter Heijke
 * @author R.W. van 't Veer
 */
public class LinkTag extends SimpleTagSupport {

    /**
     * element.
     */
    private String element;
    
	/**
	 * JSP variable name.
	 */
	public String var;

	/**
	 * Params added by nested param tag
	 */
	private Map params = new HashMap();

	private Page page;

	public void doTag() throws JspException, IOException {
		PageContext ctx = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

		if (page != null) {
			String link = SiteManagement.getPath(page, !ServerUtil.useServerName());
         
			if (link != null) {
				// handle body, call any nested tags
				JspFragment frag = getJspBody();
				if (frag != null) {
					StringWriter buffer = new StringWriter();
					frag.invoke(buffer);
				}

                String host = null;
                if(ServerUtil.useServerName()) {
                   host = SiteManagement.getSite(page);
                }
                PortalURL u = new PortalURL(host, request, link);
                
                if (element != null) {
                    int pageId = page.getId();
                    String portletWindowName = Search.getPortletWindow(pageId, element);
                    if (portletWindowName != null) {
                        u.setRenderParameter(portletWindowName, "elementId", new String[] { element } );
                    }
                }
                
                String newlink = u.toString();
                
                if(newlink != null && newlink.length() == 0) {
                	newlink = "/";
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
				setDestPage((Page) dest);
			} else if (dest instanceof Integer) {
				setDestInteger((Integer) dest);
			} else if (dest instanceof String) {
				setDestString((String) dest);
			} else {
				throw new IllegalArgumentException("only Page, integer or string allowed: " + dest.getClass());
			}
		}
	}

    public void setElement(String element) {
        this.element = element;
    }
    
	/**
	 * Set destination node to navigate to.
	 * 
	 * @param n the node
	 */
	private void setDestPage(Page n) {
        page = n;
	}

	/**
	 * Set destination node number to navigate to.
	 * 
	 * @param n the node number
	 */
	private void setDestInteger(Integer n) {
		page = SiteManagement.getPage(n.intValue());
	}

	/**
	 * Set the destination node path to navigate to.
	 * 
	 * @param s comma, slash or space separated list of node numbers and/or
	 *        aliases
	 */
	private void setDestString(String s) {
		if (StringUtils.isNumeric(s)) {
			page = SiteManagement.getPage(Integer.parseInt(s));
		} else {
			page = SiteManagement.getPageFromPath(s);
		}
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
