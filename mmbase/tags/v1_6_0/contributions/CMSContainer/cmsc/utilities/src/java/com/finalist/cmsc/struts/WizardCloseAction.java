package com.finalist.cmsc.struts;

import java.io.PrintWriter;

import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.*;

/**
 * Struts action to clean up attributes in the session used for the editwizards
 * 
 * @author Nico Klasens (Finalist IT Group)
 */
public class WizardCloseAction extends Action {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {

      HttpSession session = request.getSession();

      session.removeAttribute("contenttype");
      session.removeAttribute("creation");

      String returnurl = request.getParameter("returnurl");
      if (StringUtils.isEmpty(returnurl)) {
         returnurl = (String) session.getAttribute("returnurl");
         session.removeAttribute("returnurl");
      }
      String url = "empty.html";
      if (StringUtils.isNotEmpty(returnurl)) {
         url = returnurl;
      }

      String action = request.getParameter("action");
      if (StringUtils.isEmpty(action)) {
         action = (String) session.getAttribute("wizardaction");
         session.removeAttribute("wizardaction");
      }
      url = addParam(url, "action", action);

      String ewnode = request.getParameter("ewnode-lastedited");
      if (StringUtils.isEmpty(ewnode)) {
         ewnode = (String) session.getAttribute("ewnode-lastedited");
         session.removeAttribute("ewnode-lastedited");
      }
      url = addParam(url, "ewnodelastedited", ewnode);

      String popup = request.getParameter("popup");
      if (StringUtils.isEmpty(popup)) {
         popup = (String) session.getAttribute("popup");
         session.removeAttribute("popup");
      }

      if ("true".equalsIgnoreCase(popup)) {
         if (!url.startsWith(request.getContextPath())) {
            url = request.getContextPath() + url;
         }
         PrintWriter out = response.getWriter();
         out.write("<script type=\"text/javascript\">" + "  opener.location = \"" + url + "\";" + "  window.close();"
               + "</script>");
         return null;
      }
      else {
         if (url.startsWith(request.getContextPath())) {
            url = url.substring(request.getContextPath().length());
         }
         // Editwizard starten:
         ActionForward ret = new ActionForward(url);
         ret.setRedirect(true);
         return ret;
      }
   }


   private String addParam(String url, String name, String value) {
      if (StringUtils.isNotEmpty(value)) {
         if (url.indexOf("?") > -1) {
            return url + "&" + name + "=" + value;
         }
         else {
            return url + "?" + name + "=" + value;
         }
      }
      return url;
   }

}
