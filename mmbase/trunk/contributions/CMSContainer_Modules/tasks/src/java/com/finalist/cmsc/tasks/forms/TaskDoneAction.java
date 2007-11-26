package com.finalist.cmsc.tasks.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.tasks.TasksUtil;

/**
 * @author Nico Klasens
 */
public class TaskDoneAction extends MMBaseFormlessAction {

   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
      String id = request.getParameter("id");
      if (id != null) {
         TasksUtil.finishTask(cloud, id);
      }
      return mapping.findForward(SUCCESS);
   }

}