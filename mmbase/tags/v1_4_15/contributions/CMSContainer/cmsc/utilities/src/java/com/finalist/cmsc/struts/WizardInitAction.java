package com.finalist.cmsc.struts;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Nico Klasens
 */
public class WizardInitAction extends MMBaseFormlessAction {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(WizardInitAction.class.getName());

   private static String DEFAULT_SESSION_KEY = "editwizard";


   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String objectNumber = null;
      String action = request.getParameter("action");
      if ("create".equals(action)) {
         objectNumber = "new";
      }
      else {
         objectNumber = request.getParameter("objectnumber");
      }

      HttpSession session = request.getSession();

      String returnurl = request.getParameter("returnurl");
      if (!StringUtil.isEmpty(returnurl)) {
         session.setAttribute("returnurl", returnurl);
      }

      String popup = request.getParameter("popup");
      if (!StringUtil.isEmpty(popup)) {
         session.setAttribute("popup", popup);
      }

      String creation = request.getParameter("creation");
      if (!StringUtil.isEmpty(creation)) {
         session.setAttribute("creation", creation);
      }
      else {
         session.removeAttribute("creation");
      }

      String[] contenttypes = request.getParameterValues("contenttype");
      String contenttype = null;
      if (contenttypes == null || contenttypes.length == 0) {
         if (objectNumber != null && !"new".equals(objectNumber)) {
            Node node = cloud.getNode(objectNumber);
            contenttype = node.getNodeManager().getName();
         }
         else {
            throw new RuntimeException("No criteria available to find a wizard."
                  + " Provide a contenttype or objectnumber");
         }
      }
      else {
         if (contenttypes.length == 1) {
            contenttype = contenttypes[0];
         }
         else {
            List<String> list = Arrays.asList(contenttypes);
            addToRequest(request, "contenttypes", list);
            ActionForward ret = mapping.findForward("newtypes");
            return ret;
         }
      }
      session.setAttribute("contenttype", contenttype);

      String wizardConfigName = request.getParameter("wizardConfigName");
      if (StringUtil.isEmpty(wizardConfigName)) {
         NodeList list = null;
         NodeManager manager = cloud.getNodeManager("editwizards");
         list = manager.getList("nodepath = '" + contenttype + "'", null, null);
         if (list.isEmpty()) {
            throw new RuntimeException("Unable to find a wizard for contenttype " + contenttype + " or objectnumber "
                  + objectNumber);
         }

         Node wizard = list.getNode(0);
         wizardConfigName = wizard.getStringValue("wizard");
      }

      String sessionkey = request.getParameter("sessionkey");
      if (sessionkey == null || sessionkey.length() == 0) {
         sessionkey = DEFAULT_SESSION_KEY;
      }

      String templates = mapping.findForward("templates").getPath();
      String contextpath = request.getContextPath();
      if (templates.startsWith(contextpath)) {
         templates = templates.substring(contextpath.length());
      }

      // Editwizard starten:
      String actionForward = mapping.findForward("wizard").getPath() + "?wizard=" + wizardConfigName + "&objectnumber="
            + objectNumber + "&templates=" + templates + "&referrer=" + mapping.findForward("referrer").getPath()
            + "&sessionkey=" + sessionkey + "&language=" + cloud.getLocale().getLanguage();

      if (log.isDebugEnabled()) {
         log.debug("actionForward: " + actionForward);
         actionForward += "&debug=true";
      }

      ActionForward ret = new ActionForward(actionForward);
      ret.setRedirect(true);
      return ret;
   }
}