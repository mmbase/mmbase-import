/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.portalImpl.security.LoginSession;
import com.finalist.cmsc.portalImpl.services.sitemanagement.SiteManagement;


public class ProtectedTag extends SimpleTagSupport {

    @Override
    public void doTag() throws JspException, IOException {
        PageContext ctx = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        LoginSession ls = SiteManagement.getLoginSession(request);

        // handle body, call any nested tags
        JspFragment frag = getJspBody();
        if (frag != null) {
            if (ls != null && ls.isAuthenticated()) {
                frag.invoke(null);
            }
        }
    }
}
