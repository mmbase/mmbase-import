/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.navigation.forms;

import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;


public class PageDelete extends MMBaseFormlessAction {

    public ActionForward execute(ActionMapping mapping,
            HttpServletRequest request, Cloud cloud) throws Exception {
        
        String objectnumber = getParameter(request, "number", true);
        String action = getParameter(request, "remove");
        Node pageNode = cloud.getNode(objectnumber);

        if (StringUtil.isEmptyOrWhitespace(action)) {
            ActionForward ret = mapping.findForward("pagedelete");
            return ret;
        }
        else {
            if (NavigationUtil.getChildCount(pageNode) > 0) {
                ActionForward ret = mapping.findForward("pagedeletewarning");
                return ret;
            }
            else {
                NavigationUtil.deletePage(pageNode);
                ActionForward ret = mapping.findForward(SUCCESS);
                return ret;
            }
        }
    }

}
