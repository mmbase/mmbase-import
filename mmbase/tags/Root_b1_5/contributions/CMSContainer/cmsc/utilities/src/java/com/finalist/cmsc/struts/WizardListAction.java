package com.finalist.cmsc.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.*;

/**
 * @author Nico Klasens
 */
public class WizardListAction extends MMBaseFormlessAction {

   private static String DEFAULT_SESSION_KEY = "editwizard";


   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String nodetype = request.getParameter("nodetype");
      String wizardname = request.getParameter("wizardname");

      if (nodetype == null && wizardname == null) {
         throw new IllegalArgumentException(" Provide a nodetype or wizardname requestparameter");
      }
      String sessionkey = request.getParameter("sessionkey");
      if (sessionkey == null || sessionkey.length() == 0) {
         sessionkey = DEFAULT_SESSION_KEY;
      }

      // We want to be able to pass constraints:
      String constraints = request.getParameter("constraints");

      NodeManager manager = cloud.getNodeManager("editwizards");
      NodeList list = null;

      if (wizardname != null) {
         list = manager.getList("name = '" + wizardname + "'", null, null);
      }
      else {
         list = manager.getList("nodepath = '" + nodetype + "'", null, null);

      }
      if (list == null || list.isEmpty()) {
         throw new IllegalArgumentException("Unable to find a wizard for nodetype " + nodetype + " or wizardname " + wizardname);
      }
      Node wizard = list.getNode(0);

      StringBuffer forward = new StringBuffer();
      forward.append(mapping.findForward("list").getPath() + "?language=" + cloud.getLocale().getLanguage());
      addParameter(forward, sessionkey, "sessionkey", null);
      addParameter(forward, mapping, "templates", "templates", null);
      addParameter(forward, mapping, "referrer", "referrer", null);
      addParameter(forward, wizard, "wizard", "wizard", null);
      addParameter(forward, wizard, "nodepath", "nodepath", null);
      addParameter(forward, wizard, "fields", "fields", null);
      addParameter(forward, wizard, "pagelength", "pagelength", "50");
      addParameter(forward, wizard, "maxpagecount", "maxpagecount", "100");
      addParameter(forward, wizard, "constraints", "constraints", constraints);
      addParameter(forward, wizard, "age", "age", null);
      addParameter(forward, wizard, "distinct", "distinctlist", null);
      addParameter(forward, wizard, "searchdir", "searchdir", null);
      addParameter(forward, wizard, "orderby", "orderby", null);
      addParameter(forward, wizard, "directions", "directions", null);
      addParameter(forward, wizard, "maxupload", "maxupload", null);
      addParameter(forward, wizard, "searchfields", "searchfields", null);
      addParameter(forward, wizard, "searchtype", "searchtype", null);
      addParameter(forward, wizard, "searchvalue", "searchvalue", null);
      addParameter(forward, wizard, "search", "search", null);
      addParameter(forward, wizard, "origin", "origin", null);
      addParameter(forward, wizard, "title", "title", null);

      // Editwizard starten:
      ActionForward ret = new ActionForward(forward.toString());
      ret.setRedirect(true);
      return ret;
   }


   private void addParameter(StringBuffer forward, ActionMapping mapping, String paramname, String mappingName,
         String defaultvalue) {
      String value = mapping.findForward(mappingName).getPath();
      addParameter(forward, value, paramname, defaultvalue);
   }


   private void addParameter(StringBuffer forward, Node node, String paramname, String fieldname, String defaultvalue) {
      String value = node.getStringValue(fieldname);
      addParameter(forward, value, paramname, defaultvalue);
   }


   private void addParameter(StringBuffer forward, String value, String paramname, String defaultvalue) {
      if (StringUtils.isBlank(value)) {
         value = defaultvalue;
      }
      if (StringUtils.isNotBlank(value)) {
         forward.append("&").append(paramname).append("=").append(value);
      }
   }

}