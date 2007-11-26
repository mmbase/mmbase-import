/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.tasks.forms;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.cmsc.tasks.TasksUtil;

public class TaskAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      if (!isCancelled(request)) {
         TaskForm taskForm = (TaskForm) form;
         if (taskForm.getId() == 0) {
            return mapping.findForward(SUCCESS);
         }
         if (taskForm.getId() == -1) {
            String title = taskForm.getTitle();
            String description = taskForm.getDescription();
            Date deadline = taskForm.getDeadline();
            String nodetype = taskForm.getNodetype();
            int user = taskForm.getUser();

            TasksUtil.createUserTask(cloud, title, description, deadline, nodetype, user);
         }
         else {
            Node taskNode = getOrCreateNode(taskForm, cloud, TasksUtil.TASK);
            MMBaseNodeMapper.copyBean(taskForm, taskNode);
            taskNode.commit();
         }
      }
      removeFromSession(request, form);
      return mapping.findForward(SUCCESS);
   }

}
