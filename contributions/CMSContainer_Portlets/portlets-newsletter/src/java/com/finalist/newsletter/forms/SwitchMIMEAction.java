package com.finalist.newsletter.forms;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SwitchMIMEAction extends Action {
   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
      String targetMIME = httpServletRequest.getParameter("target");
      String number = httpServletRequest.getParameter("number");

      httpServletRequest.getSession(true).setAttribute("contentType", targetMIME);

      return new ActionForward(String.format("/editors/site/NavigatorPanel.do?nodeId=%s", number));
   }
}
