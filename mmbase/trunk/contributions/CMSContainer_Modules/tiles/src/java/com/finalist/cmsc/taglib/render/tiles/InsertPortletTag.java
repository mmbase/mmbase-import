/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.render.tiles;

import com.finalist.pluto.portalImpl.aggregation.PortletFragment;
import com.finalist.pluto.portalImpl.servlet.ServletObjectAccess;
import com.finalist.pluto.portalImpl.servlet.ServletResponseImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

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


        PortletFragment portlet = getPortlet(request, layoutid);

        if (portlet == null) {
            log.warn("No (Portlet)Fragment to insert for position: " + layoutid);
            return;
        }


        try {
            StringWriter storedWriter = new StringWriter();
            // create a wrapped response which the Portlet will be rendered
            // to
            ServletResponseImpl wrappedResponse = (ServletResponseImpl) ServletObjectAccess
                    .getStoredServletResponse((HttpServletResponse) ctx.getResponse(), new PrintWriter(storedWriter));
            // let the Portlet do it's thing
            portlet.writeToResponse(request, wrappedResponse);

            if (StringUtils.isNotEmpty(var)) {
                request.setAttribute(var, storedWriter.toString());
            } else {
                ctx.getOut().print(storedWriter.toString());
            }
        }
        catch (IOException e) {
            log.error("Error in portlet");
            ctx.getOut().print("Error in portlet");
        }
    }


    private PortletFragment getPortlet(HttpServletRequest request, String layoutId) throws JspException {

        ScreenTag container = (ScreenTag) findAncestorWithClass(this, ScreenTag.class);

        if (null == container) {
            container = (ScreenTag) request.getAttribute("container");
        }

        if (null == container) {
            throw new JspException("Couldn't find screen tag");
        }

        container.registerLayout(layoutId);
        return container.getPortlet(layoutId);

    }
}
