/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.subsite;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.NavigationItemRenderer;
import com.finalist.cmsc.portalImpl.PageNavigationRenderer;
import com.finalist.cmsc.subsite.beans.om.PersonalPage;
import com.finalist.cmsc.subsite.util.SubSiteUtil;


public class PersonalPageNavigationRenderer extends PageNavigationRenderer implements
        NavigationItemRenderer {


    @Override
    public void render(NavigationItem item, HttpServletRequest request,
            HttpServletResponse response, ServletConfig sc) throws IOException {

        if (item instanceof PersonalPage) {
            PersonalPage pp = (PersonalPage) item;
            request.setAttribute(SubSiteUtil.PERSONAL_PAGE_ID, pp.getUserid());
        }
        super.render(item, request, response, sc);
    }
    
}
