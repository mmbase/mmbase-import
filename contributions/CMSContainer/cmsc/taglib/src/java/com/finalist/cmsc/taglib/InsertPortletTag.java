/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.taglib;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.pluto.portalImpl.aggregation.Fragment;
import com.finalist.pluto.portalImpl.servlet.ServletObjectAccess;
import com.finalist.pluto.portalImpl.servlet.ServletResponseImpl;

/**
 * Tag to insert a Portlet, the corresponding Screen keeps track of the Portlets
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class InsertPortletTag extends SimpleTagSupport {
	private static Log log = LogFactory.getLog(InsertPortletTag.class);

    private String layoutid;

    public String getLayoutid() {
        return layoutid;
    }

    public void setLayoutid(String layoutid) {
        this.layoutid = layoutid;
    }
    
	public void doTag() throws JspException, IOException {
		PageContext ctx = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
		HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

		ServletResponseImpl wrappedResponse = (ServletResponseImpl) ServletObjectAccess.getServletResponse(response);

		StringWriter storedWriter = new StringWriter();
		PrintWriter writer2 = new PrintWriter(storedWriter);

		// create a wrapped response which the Portlet will be rendered to
		wrappedResponse = (ServletResponseImpl) ServletObjectAccess.getStoredServletResponse(response, writer2);

		ScreenTag container = (ScreenTag) findAncestorWithClass(this, ScreenTag.class);
		if (container != null) {
			Fragment portlet = container.getPortlet(layoutid);
			if (portlet != null) {
				try {
					// let the Portlet do it's thing
					portlet.service(request, wrappedResponse);
				} catch (ServletException e) {
					log.error("Error in portlet servlet");
					writer2.println("Error in portlet servlet");
				} catch (IOException e) {
					log.error("Error in portlet");
					writer2.println("Error in portlet");
				}
			} else {
				log.warn("No (Portlet)Fragment to insert for position: " + layoutid);
			}
			ctx.getOut().print(storedWriter.toString());
		} else {
			throw new JspException("Couldn't find screen tag");
		}
	}

}
