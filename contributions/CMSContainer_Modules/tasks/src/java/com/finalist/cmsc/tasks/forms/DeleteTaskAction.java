package com.finalist.cmsc.tasks.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.tasks.TasksUtil;

/**
 * DeleteTaskAction
 * 
 * @author Nico Klasens
 */
public class DeleteTaskAction extends MMBaseFormlessAction {

   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
      String objectnumber = request.getParameter("objectnumber");
      if (StringUtils.isNotBlank(objectnumber)) {
         Node task = cloud.getNode(objectnumber);
         if(task!=null&&TasksUtil.isDeleteable(task, cloud)){
            task.delete(true);
         }
      }
      return mapping.findForward(SUCCESS);
   }
}