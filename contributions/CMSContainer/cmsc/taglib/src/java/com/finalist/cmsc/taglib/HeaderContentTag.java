/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.taglib;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.portalImpl.headerresource.HeaderResource;
import com.finalist.pluto.portalImpl.aggregation.Fragment;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;


public class HeaderContentTag extends SimpleTagSupport {

    private boolean meta = true;
    private boolean javascript = true;
    private boolean style = true;
    
    public void setMeta(String meta) {
        this.meta = Boolean.valueOf(meta);
    }

    public void setJavascript(String javascript) {
        this.javascript = Boolean.valueOf(javascript);
    }
    
    public void setStyle(String style) {
        this.style = Boolean.valueOf(style);
    }
    
    public void doTag() throws JspException, IOException {
        PageContext ctx = (PageContext) getJspContext();

        ScreenTag container = (ScreenTag) findAncestorWithClass(this, ScreenTag.class);
        if (container != null) {
            Iterator portlets = container.getAllPortlets().iterator();
            while(portlets.hasNext()) {
                Fragment fragment = (Fragment) portlets.next();
                if (fragment instanceof PortletFragment) {
                    PortletFragment pf = (PortletFragment) fragment;
                    HeaderResource resource = pf.getHeaderResource();
                    ctx.getOut().print(resource.toString(meta, javascript, style, true));
                }
            }
        } else {
            throw new JspException("Couldn't find screen tag");
        }
    }
}
