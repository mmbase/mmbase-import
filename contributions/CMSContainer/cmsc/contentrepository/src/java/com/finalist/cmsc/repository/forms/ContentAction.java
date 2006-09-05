/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.*;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.*;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseAction;


public class ContentAction extends MMBaseAction {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception {

        List<LabelValueBean> typesList = new ArrayList<LabelValueBean>();

        List<NodeManager> types = ContentElementUtil.getContentTypes(cloud);
        for (NodeManager manager : types) {
            LabelValueBean bean = new LabelValueBean(manager.getGUIName(), manager.getName());
            typesList.add(bean);
        }
        addToRequest(request, "typesList", typesList);
        
        
        String parentchannel = request.getParameter("parentchannel");
        String orderby = request.getParameter("orderby");
        String direction = request.getParameter("direction");
        if (StringUtil.isEmpty(orderby)) {
            orderby = null;
        }
        if (StringUtil.isEmpty(direction)) {
            direction = null;
        }
        
        if (!StringUtil.isEmpty(parentchannel)) {
            Node channel = cloud.getNode(parentchannel);
            NodeList elements = RepositoryUtil.getLinkedElements(channel, null, orderby, direction, false, -1, -1);;
            addToRequest(request, "elements", elements);
            
            NodeList created = RepositoryUtil.getCreatedElements(channel);
            Map<String,Node> createdNumbers = new HashMap<String,Node>();
            for (Iterator iter = created.iterator(); iter.hasNext();) {
                Node createdElement = (Node) iter.next();
                createdNumbers.put(String.valueOf(createdElement.getNumber()), createdElement);
            }
            addToRequest(request, "createdNumbers", createdNumbers);
        }
        
        return mapping.findForward(SUCCESS);
    }

}
