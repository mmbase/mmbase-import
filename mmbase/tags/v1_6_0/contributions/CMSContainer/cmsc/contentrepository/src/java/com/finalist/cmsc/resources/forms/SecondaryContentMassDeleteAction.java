package com.finalist.cmsc.resources.forms;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.struts.MMBaseAction;

public class SecondaryContentMassDeleteAction extends MMBaseAction{

   @SuppressWarnings("unchecked")
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      String object_type = request.getParameter("object_type");
      Enumeration<String> parameters = request.getParameterNames();
      while (parameters.hasMoreElements()) {
         String parameter = parameters.nextElement();
         if (parameter.startsWith("chk_")) {
            String number = request.getParameter(parameter);
            Node objectNode = cloud.getNode(number);
           
            Publish.remove(objectNode);
            Publish.unpublish(objectNode);

            objectNode.delete(true);
         }
      }
      return mapping.findForward(object_type);
   }

}
