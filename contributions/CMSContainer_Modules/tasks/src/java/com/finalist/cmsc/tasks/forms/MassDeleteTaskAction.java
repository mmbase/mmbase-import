package com.finalist.cmsc.tasks.forms;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.tasks.TasksUtil;

/**
 * MassDeleteTaskAction
 * 
 * @author Marco
 */
public class MassDeleteTaskAction extends MMBaseFormlessAction {

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
      ActionMessages messages = new ActionMessages();
      Enumeration<String> parameters = request.getParameterNames();
      int successCount=0;
      int failureCount=0;
      while (parameters.hasMoreElements()) {
         String parameter = parameters.nextElement();
         if (parameter.startsWith("chk_")) {
            String objectnumber = request.getParameter(parameter);
            if (StringUtils.isNotBlank(objectnumber)) {
               Node task = cloud.getNode(objectnumber);
               if(task!=null&&TasksUtil.isDeleteable(task, cloud)){
                  task.delete(true);
                  successCount++;
               } else {
                  failureCount++;
               }
            }
         }
      }
      if (successCount > 0) {
         messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("task.massdelete.message.success", successCount));
      }
      if (failureCount > 0) {
         messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("task.massdelete.message.failure", failureCount));
      }
      saveMessages(request, messages);
      return mapping.findForward(SUCCESS);
   }
}