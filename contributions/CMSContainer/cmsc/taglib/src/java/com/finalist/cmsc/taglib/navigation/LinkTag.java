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
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.Step;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
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
	private HashMap params = new HashMap();

	private Page channel;

	public void doTag() throws JspException, IOException {
		PageContext ctx = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

		if (channel != null) {
			String link = SiteManagement.getPath(channel, !ServerUtil.useServerName());
			if (link != null) {
				// handle body, call any nested tags
				JspFragment frag = getJspBody();
				if (frag != null) {
					StringWriter buffer = new StringWriter();
					frag.invoke(buffer);
				}

                PortalURL u = new PortalURL(request, link);
                if (element != null) {
                    String portletWindowName = getPortletWindow(channel, element);
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

	private String getPortletWindow(Page pageObject, String elementNumber) {
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
        Node node = cloud.getNode(elementNumber);
        if (ContentElementUtil.isContentElement(node)) {
            NodeList channels = RepositoryUtil.getContentChannels(node);
            channels.add(node);
            
            NodeManager parameterManager = cloud.getNodeManager(PortletUtil.NODEPARAMETER);
            NodeManager portletManager = cloud.getNodeManager(PortletUtil.PORTLET);
            NodeManager pageManager = cloud.getNodeManager(PagesUtil.PAGE);

            Query query = cloud.createQuery();
            Step parameterStep = query.addStep(parameterManager);
            RelationStep step2 = query.addRelationStep(portletManager, PortletUtil.PARAMETERREL, "SOURCE");
            RelationStep step4 = query.addRelationStep(pageManager, PortletUtil.PORTLETREL, "SOURCE");
            Step pageStep = step4.getNext();

            query.addField(parameterStep, parameterManager.getField(PortletUtil.KEY_FIELD));
            query.addField(parameterStep, parameterManager.getField(PortletUtil.VALUE_FIELD));
            query.addField(step4, cloud.getRelationManager(PortletUtil.PORTLETREL).getField(PortletUtil.LAYOUTID_FIELD));
            query.addField(pageStep, pageManager.getField("number"));
            
            SearchUtil.addNodesConstraints(query, parameterManager.getField(PortletUtil.VALUE_FIELD), channels);
            NodeList pages = cloud.getList(query);
            if (!pages.isEmpty()) {
                for (Iterator iter = pages.iterator(); iter.hasNext();) {
                    Node pageNode = (Node) iter.next();
                    int pageNumber = pageNode.getIntValue(PagesUtil.PAGE + ".number");
                    if (pageObject.getId() == pageNumber) {
                        return pageNode.getStringValue(PortletUtil.PORTLETREL + "." + PortletUtil.LAYOUTID_FIELD);
                    }
                }
            }
        }

        return null;
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
        channel = n;
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
		if (StringUtils.isNumeric(s)) {
			channel = SiteManagement.getPage(Integer.parseInt(s));
		} else {
			channel = SiteManagement.getPageFromPath(s);
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
