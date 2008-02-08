/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.render;

import java.io.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.pluto.portalImpl.aggregation.PortletFragment;
import com.finalist.pluto.portalImpl.servlet.ServletObjectAccess;
import com.finalist.pluto.portalImpl.servlet.ServletResponseImpl;

/**
 * Tag to insert a Portlet, the corresponding Screen keeps track of the Portlets
 * 
 * @author Wouter Heijke
 */
public class InsertPortletTag extends SimpleTagSupport {
    private static Log log = LogFactory.getLog(InsertPortletTag.class);

    private String layoutid;
	private String var;

    public String getLayoutid() {
       return layoutid;
    }

    public void setLayoutid(String layoutid) {
       this.layoutid = layoutid;
    }
	public void setVar(String var) {
		this.var = var;
	 }	

    @Override
    public void doTag() throws JspException, IOException {
       PageContext ctx = (PageContext) getJspContext();
       HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
       HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

       ScreenTag container = (ScreenTag) findAncestorWithClass(this, ScreenTag.class);
       if (container != null) {
          PortletFragment portlet = container.getPortlet(layoutid);
          if (portlet != null) {
             try {
                StringWriter storedWriter = new StringWriter();
                // create a wrapped response which the Portlet will be rendered
                // to
                ServletResponseImpl wrappedResponse = (ServletResponseImpl) ServletObjectAccess
                      .getStoredServletResponse(response, new PrintWriter(storedWriter));
                // let the Portlet do it's thing
                portlet.writeToResponse(request, wrappedResponse);
                
                if (StringUtils.isNotEmpty(var)) {
                    request.setAttribute(var, storedWriter.toString());
                }
                else {
                    ctx.getOut().print(storedWriter.toString());
                }
             }
             catch (IOException e) {
                log.error("Error in portlet");
                ctx.getOut().print("Error in portlet");
             }
          }
          else {
             log.warn("No (Portlet)Fragment to insert for position: " + layoutid);
          }
       }
       else {
          throw new JspException("Couldn't find screen tag");
       }
    }
}