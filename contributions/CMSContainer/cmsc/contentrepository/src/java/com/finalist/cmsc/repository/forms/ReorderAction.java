/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository.forms;

import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;


public class ReorderAction extends MMBaseFormlessAction {

    public ActionForward execute(ActionMapping mapping,
            HttpServletRequest request, Cloud cloud) throws Exception {

        String action = getParameter(request, "action");
        
        if (!StringUtil.isEmptyOrWhitespace(action)) {
            if ("reorder".equals(action)) {
                String parent = request.getParameter("parent");
                if (!isCancelled(request)) {
                    String ids = request.getParameter("ids");
                    RepositoryUtil.reorderContent(cloud, parent, ids);
                }
                String returnurl = request.getParameter("returnurl");
                if(returnurl != null) {
                   return new ActionForward(returnurl, true);
                }
                String url = mapping.findForward(SUCCESS).getPath() + "?parentchannel=" + parent;
                return new ActionForward(url, true);
            }
            if ("reorderpartial".equals(action)) {
                String parent = request.getParameter("parent");
                String offsetStr = request.getParameter("offset");
                String[] ids = request.getParameterValues("ids[]");
                RepositoryUtil.reorderContent(cloud, parent, ids, Integer.parseInt(offsetStr));
                return null;
            }
         }
         
         return mapping.findForward("reorder");
    }

}
