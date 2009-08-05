package com.finalist.cmsc.navigation.select.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

public class SelectorExtensionAction  extends SelectorAction{

   private String methodName = "";
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      methodName = request.getParameter("method");
      return super.execute(mapping, form, request, response, cloud);
   }
   @Override
   public String getLinkPattern() {
      return super.getLinkPattern() + "&method=" + methodName;
   }
}
