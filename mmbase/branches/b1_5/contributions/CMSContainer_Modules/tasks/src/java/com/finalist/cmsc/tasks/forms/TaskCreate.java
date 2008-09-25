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
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.RelationManager;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.tasks.TasksUtil;

public class TaskCreate extends MMBaseFormlessAction {

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
		ActionMessages messages = new ActionMessages();
		String action = getParameter(request, "action");

		if (action != null && "save".equals(action)) {
			String lastEdited = getParameter(request, "ewnodelastedited");

			Node taskNode = cloud.getNode(lastEdited);

			Node curUserNode = SecurityUtil.getUserNode(cloud);
			RelationManager creatorrel = cloud.getRelationManager("task", "user", "creatorrel");
			taskNode.createRelation(curUserNode, creatorrel).commit();

			NodeList assignUserNodes = taskNode.getRelatedNodes("user", "assignedrel", "destination");
			String assignUserName = (assignUserNodes != null) ? assignUserNodes.getNode(0).getStringValue("username") : "";

			Node assignUserNode = (assignUserNodes.size() > 0) ? assignUserNodes.getNode(0) : null;

			if (assignUserNode != null) {
				TasksUtil.sendNotification(assignUserNode, curUserNode, taskNode);
			}
			messages.add("createSuccess", new ActionMessage("task.create.message", assignUserName));
			saveMessages(request, messages);
		}
		return mapping.findForward(SUCCESS);
	}
}
