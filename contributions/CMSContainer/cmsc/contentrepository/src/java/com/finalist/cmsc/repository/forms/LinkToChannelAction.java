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
import org.mmbase.bridge.*;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

import java.util.Enumeration;


public class LinkToChannelAction extends MMBaseFormlessAction {

    public ActionForward execute(ActionMapping mapping,
            HttpServletRequest request, Cloud cloud) throws Exception {

        String action = getParameter(request, "action");
        String channelnumber = getParameter(request, "channelnumber");
        Node channelNode = cloud.getNode(channelnumber);

        String objectnumber = getParameter(request, "objectnumber");


        if (action != null && action.equals("unlink")) {

           Node objectNode = cloud.getNode(objectnumber);

            if(RepositoryUtil.isCreationChannel(objectNode, channelNode)) {
                NodeList contentchannels = RepositoryUtil.getContentChannels(objectNode);
                if (contentchannels.size() <= 1) {
                    RepositoryUtil.removeContentFromChannel(objectNode, channelNode);
                    RepositoryUtil.removeCreationRelForContent(objectNode);
                    RepositoryUtil.addContentToChannel(objectNode, RepositoryUtil.getTrash(cloud));
                }
                else {
                    String destinationnumber = getParameter(request, "destionationchannel");
                    if (!StringUtil.isEmpty(destinationnumber)) {
                        Node newCreationNode = cloud.getNode(destinationnumber);
                        
                        RepositoryUtil.removeContentFromChannel(objectNode, channelNode);
                        RepositoryUtil.removeCreationRelForContent(objectNode);
                        
                        if (RepositoryUtil.isTrash(newCreationNode)) {
                            RepositoryUtil.removeContentFromAllChannels(objectNode);
                            RepositoryUtil.addContentToChannel(objectNode, newCreationNode.getStringValue("number"));
                        }
                        else {
                            RepositoryUtil.addCreationChannel(objectNode, newCreationNode);
                        }
                    }
                    else {
                        addToRequest(request, "content", objectNode);
                        addToRequest(request, "creationchannel", channelNode);
                        addToRequest(request, "contentchannels", contentchannels);
                        addToRequest(request, "trashchannel", RepositoryUtil.getTrashNode(cloud));
                        return mapping.findForward("unlinkcreation");
                    }
                }
            }
            else {
                RepositoryUtil.removeContentFromChannel(objectNode, channelNode);
            }
        }
        else {
           // Link them all.

           Enumeration parameters = request.getParameterNames();
           while (parameters.hasMoreElements()) {
              String parameter = (String) parameters.nextElement();

              if (parameter.startsWith("link_")) {
                 String link = request.getParameter(parameter);
                 RepositoryUtil.addContentToChannel(cloud.getNode(link), channelnumber);
              }
           }
        }

        String returnurl = request.getParameter("returnurl");

        if(returnurl != null) {
           return new ActionForward(returnurl, true);
        }
        String url = mapping.findForward(SUCCESS).getPath() + "?parentchannel=" + channelnumber;
        return new ActionForward(url, true);
    }

}
