/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.tasks.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.*;

import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.cmsc.tasks.TasksUtil;

public class TaskInitAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      TaskForm taskForm = (TaskForm) form;

      String id = request.getParameter("id");
      if (id != null) {
         taskForm.setId(Integer.parseInt(id));

         Node node = cloud.getNode(id);
         MMBaseNodeMapper.copyNode(node, taskForm);

         Node user = TasksUtil.getAssignedUser(node);
         taskForm.setUser(user.getNumber());
      }
      else {
         // new
         taskForm.setId(-1);
         String user = request.getParameter("user");
         if (user != null) {
            taskForm.setUser(Integer.parseInt(user));
         }
      }

      List<LabelValueBean> usersList = new ArrayList<LabelValueBean>();
      NodeList users = SecurityUtil.getUsers(cloud);
      for (Iterator<Node> iter = users.iterator(); iter.hasNext();) {
         Node user = iter.next();
         String label = getLabel(user);
         LabelValueBean bean = new LabelValueBean(label, String.valueOf(user.getNumber()));
         usersList.add(bean);
      }
      addToRequest(request, "usersList", usersList);

      return mapping.findForward(SUCCESS);
   }


   private String getLabel(Node user) {
      String label = user.getStringValue("username") + " {" + SecurityUtil.getFullname(user) + ")";
      return label;
   }

}
