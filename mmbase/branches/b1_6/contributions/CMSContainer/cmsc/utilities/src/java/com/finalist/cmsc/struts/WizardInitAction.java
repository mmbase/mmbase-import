package com.finalist.cmsc.struts;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Nico Klasens
 */
public class WizardInitAction extends MMBaseFormlessAction {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(WizardInitAction.class.getName());

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
      if (StringUtils.isNotEmpty(returnurl)) {
         session.setAttribute("returnurl", returnurl);
      }

      String popup = request.getParameter("popup");
      if (StringUtils.isNotEmpty(popup)) {
         session.setAttribute("popup", popup);
      }

      String creation = request.getParameter("creation");
      if (StringUtils.isNotEmpty(creation)) {
         session.setAttribute("creation", creation);
      }
      else {
         session.removeAttribute("creation");
      }

      String[] elementtypes = request.getParameterValues("contenttype");
      if(elementtypes == null || elementtypes.length == 0){
         elementtypes = request.getParameterValues("assettype");
      }
      String elementtype = null;
      if (elementtypes == null || elementtypes.length == 0) {
         if (objectNumber != null && !"new".equals(objectNumber)) {
            Node node = cloud.getNode(objectNumber);
            elementtype = node.getNodeManager().getName();
         }
         else {
            throw new IllegalStateException("No criteria available to find a wizard."
                  + " Provide a elementtype or objectnumber");
         }
      }
      else {
         if (elementtypes.length == 1) {
            elementtype = elementtypes[0];
         }
         else {
            List<String> list = Arrays.asList(elementtypes);
            if(request.getParameterValues("contenttype").length > 1){
               addToRequest(request, "contenttypes", list);
            }
            else {
               addToRequest(request, "assettypes", list);
            }
            ActionForward ret = mapping.findForward("newtypes");
            return ret;
         }
      }

      String wizardConfigName = request.getParameter("wizardConfigName");
      if (StringUtils.isEmpty(wizardConfigName)) {
         NodeList list = null;
         NodeManager manager = cloud.getNodeManager("editwizards");
         list = manager.getList("nodepath = '" + elementtype + "'", null, null);
         if (!list.isEmpty()) {
            Node wizard = list.getNode(0);
            wizardConfigName = wizard.getStringValue("wizard");
         }
         else {
            String typeWizard = "config/" + elementtype + "/" + elementtype;
            if (editwizardExists(typeWizard)) {
               wizardConfigName = typeWizard;
            }
            else {
               throw new IllegalStateException("Unable to find a wizard for elementtype " + elementtype + " or objectnumber "
                     + objectNumber);
            }
         }
      }

      if ("images".equals(elementtype) || "attachments".equals(elementtype) || "urls".equals(elementtype)) {
         session.setAttribute("assettype", elementtype);
      } else {
         session.setAttribute("contenttype", elementtype);
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


   private boolean editwizardExists(String wizard) {
      String wizardSchema = "/editors/" + wizard + ".xml";
      Set<String> webInfResources = this.getServlet().getServletContext().getResourcePaths(wizardSchema);
      /*
       * @see javax.servlet.ServletContext#getResourcePaths(String) getResourcePaths returns a Set
       *      containing the directory listing, or null if there are no resources in the web
       *      application whose path begins with the supplied path. we are using a full path instead
       *      of a partial path. webInfResources.isEmpty() is true when the resource exists
       */
      return webInfResources != null;
   }
}