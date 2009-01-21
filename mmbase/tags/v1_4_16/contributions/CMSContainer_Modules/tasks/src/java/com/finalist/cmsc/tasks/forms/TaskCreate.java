/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.tasks.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.tasks.TasksUtil;

public class TaskCreate extends MMBaseFormlessAction {

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String action = getParameter(request, "action");

      if (action != null && "save".equals(action)) {
         String lastEdited = getParameter(request, "ewnodelastedited");

         Node taskNode = cloud.getNode(lastEdited);
         NodeList userNodes = taskNode.getRelatedNodes("user");
         Node userNode = (userNodes.size() > 0) ? userNodes.getNode(0) : null;

         if (userNode != null) {
            Node userFromNode = SecurityUtil.getUserNode(cloud);

            TasksUtil.sendNotification(userNode, userFromNode, taskNode);
         }
      }
      return mapping.findForward(SUCCESS);
   }

}
