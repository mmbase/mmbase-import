/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository.forms;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.*;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;


public class ChannelDelete extends MMBaseFormlessAction {

    public ActionForward execute(ActionMapping mapping,
            HttpServletRequest request, Cloud cloud) throws Exception {
        
        String objectnumber = getParameter(request, "number", true);
        String action = getParameter(request, "remove");
        Node channelNode = cloud.getNode(objectnumber);

        if (StringUtil.isEmptyOrWhitespace(action)) {
            if (RepositoryUtil.hasCreatedContent(channelNode)) {
                return mapping.findForward("channeldeletewarning");
            }
            else {
                return mapping.findForward("channeldelete");
            }
        }
        else {
            if ("unlinkall".equals(action)) {
                NodeList createdElements = RepositoryUtil.getCreatedElements(channelNode);
                for (Iterator iter = createdElements.iterator(); iter.hasNext();) {
                    Node objectNode = (Node) iter.next();
                    RepositoryUtil.removeContentFromChannel(objectNode, channelNode);
                    RepositoryUtil.removeCreationRelForContent(objectNode);
                    
                    RepositoryUtil.removeContentFromAllChannels(objectNode);
                    RepositoryUtil.addContentToChannel(objectNode, RepositoryUtil.getTrashNode(cloud));
                }
                return mapping.findForward("channeldelete");
            }
            else {
                channelNode.delete(true);
                return mapping.findForward(SUCCESS);
            }
        }
    }

}
