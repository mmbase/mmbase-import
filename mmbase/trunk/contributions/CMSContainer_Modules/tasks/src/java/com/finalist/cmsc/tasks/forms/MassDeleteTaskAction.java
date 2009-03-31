package com.finalist.cmsc.tasks.forms;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

/**
 * MassDeleteTaskAction
 * 
 * @author Marco
 */
public class MassDeleteTaskAction extends MMBaseFormlessAction {

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
      Enumeration<String> parameters = request.getParameterNames();
      while (parameters.hasMoreElements()) {
         String parameter = parameters.nextElement();
         if (parameter.startsWith("chk_")) {
            String objectnumber = request.getParameter(parameter);
            if (StringUtils.isNotBlank(objectnumber)) {
               Node task = cloud.getNode(objectnumber);
               task.delete(true);
            }
         }
      }
      return mapping.findForward(SUCCESS);
   }
}