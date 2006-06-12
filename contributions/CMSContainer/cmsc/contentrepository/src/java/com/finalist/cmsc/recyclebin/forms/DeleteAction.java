/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.recyclebin.forms;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.*;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;


public class DeleteAction extends MMBaseFormlessAction {

    public ActionForward execute(ActionMapping mapping,
            HttpServletRequest request, Cloud cloud) throws Exception {

        String action = getParameter(request, "action");
        
        if ("deleteall".equals(action)) {
            Node trash = RepositoryUtil.getTrashNode(cloud);
            NodeList garbage = RepositoryUtil.getLinkedElements(trash);
            for (Iterator iter = garbage.iterator(); iter.hasNext();) {
                Node objectNode = (Node) iter.next();
                ContentElementUtil.removeContentBlock(objectNode);
            }
        }
        else {
            String objectnumber = getParameter(request, "objectnumber");
            Node objectNode = cloud.getNode(objectnumber);
            ContentElementUtil.removeContentBlock(objectNode);
        }
        return mapping.findForward(SUCCESS);
    }

    @Override
    public String getRequiredRankStr() {
        return ADMINISTRATOR;
    }

}
