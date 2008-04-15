package com.finalist.cmsc.security.forms;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ChangePasswordAction
 * 
 * @author Nico Klasens
 */
public class ChangeLanguageAction extends MMBaseAction {

   /** MMbase logging system */
   private static transient Logger log = Logging.getLoggerInstance(ChangeLanguageAction.class.getName());


   /**
    * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
    *      org.apache.struts.action.ActionForm,
    *      javax.servlet.http.HttpServletRequest,
    *      javax.servlet.http.HttpServletResponse)
    */
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      if (!isCancelled(request)) {
         // Make sure we have the logged in user and not a user from a
         // cloudprovider
         Cloud userCloud = getCloudFromSession(request);

         ChangeLanguageForm languageForm = (ChangeLanguageForm) form;

         String language = languageForm.getLanguage();
         if (language == "") {
            language = null;
         }

         Node userNode = SecurityUtil.getUserNode(userCloud);
         userNode.setStringValue("language", language);
         userNode.commit();

         request.setAttribute("done", Boolean.TRUE);

         return mapping.findForward(SUCCESS);
      }
      else {
         return mapping.findForward("cancel");
      }
   }

}