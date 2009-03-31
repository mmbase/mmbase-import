package com.finalist.cmsc.tasks.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

/**
 * DeleteTaskAction
 * 
 * @author Nico Klasens
 */
public class DeleteTaskAction extends MMBaseFormlessAction {

   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
      String objectnumber = request.getParameter("objectnumber");
      if (objectnumber != null) {
         Node task = cloud.getNode(objectnumber);
         task.delete(true);
      }
      return mapping.findForward(SUCCESS);
   }
}