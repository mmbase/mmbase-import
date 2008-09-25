package com.finalist.cmsc.security.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.*;
import org.mmbase.bridge.*;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseAction;

/**
 * UserAction
 * 
 * @author Nico Klasens
 */
public class UserAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      boolean changedOwnLanguage = false;
      if (!isCancelled(request)) {
         boolean isNewUser = false;

         UserForm userForm = (UserForm) form;
         Node userNode = getOrCreateNode(userForm, cloud, SecurityUtil.USER);
         if (userForm.getId() == -1) {
            isNewUser = true;
            userNode.setStringValue("username", userForm.getUsername());
         }

         userNode.setStringValue("firstname", userForm.getFirstname());
         userNode.setStringValue("prefix", userForm.getPrefix());
         userNode.setStringValue("surname", userForm.getSurname());
         userNode.setStringValue("company", userForm.getCompany());
         userNode.setStringValue("department", userForm.getDepartment());
         userNode.setStringValue("function", userForm.getFunction());
         userNode.setBooleanValue("emailsignal", userForm.isEmailSignal());
         userNode.setStringValue("emailaddress", userForm.getEmail());
         userNode.setStringValue("website", userForm.getWebsite());

         String oldLanguage = userNode.getStringValue("language");
         String newLanguage = userForm.getLanguage();
         changedOwnLanguage = changedOwnLanguage(oldLanguage, newLanguage, cloud, userNode);
         userNode.setStringValue("language", newLanguage);

         if (StringUtils.isNotEmpty(userForm.getPassword1())) {
            userNode.setStringValue("password", userForm.getPassword1());
            // TODO: what should we do with an admin password change?
            // if ("admin".equals(userNode.getStringValue("account"))) {
            // UsersUtil.updateAdminPassword(userForm.getPassword());
            // }
         }
         userNode.setStringValue("note", userForm.getNote());
         if (isNewUser) {
            Node newContextNode = SecurityUtil.getContext(cloud);
            userNode.setNodeValue("defaultcontext", newContextNode);
         }

         // String oldContext = "";
         // String newContext = userForm.getDefaultcontext();
         // if (!userNode.isNew()) {
         // Node contextNode = userNode.getNodeValue("defaultcontext");
         // if (contextNode != null) {
         // oldContext = contextNode.getStringValue("name");
         // }
         // }
         // if (!oldContext.equals(newContext)) {
         // Node newContextNode = cloud.getNode(newContext);
         // userNode.setNodeValue("defaultcontext", newContextNode);
         // }
         userNode.setIntValue("status", userForm.getStatus());
         if (userForm.getValidfrom() != null) {
            userNode.setDateValue("validfrom", userForm.getValidfrom());
         }
         if (userForm.getValidto() != null) {
            userNode.setDateValue("validto", userForm.getValidto());
         }

         userNode.commit();

         if (!"admin".equals(userNode.getStringValue("username"))) {
            String oldRank = "";
            String newRank = userForm.getRank();
            if (!userNode.isNew()) {
               Node rankNode = SecurityUtil.getRank(userNode);
               if (rankNode != null) {
                  oldRank = rankNode.getStringValue("number");
               }
            }
            if (!oldRank.equals(newRank)) {
               Node newRankNode = cloud.getNode(newRank);
               SecurityUtil.setRank(cloud, userNode, newRankNode);
            }
         }
      }
      removeFromSession(request, form);

      if (changedOwnLanguage) {
         return mapping.findForward("changedLanguage");
      }
      else {
         return mapping.findForward(SUCCESS);
      }
   }


   private boolean changedOwnLanguage(String oldLanguage, String newLanguage, Cloud cloud, Node userNode) {

      if (cloud.getUser().getIdentifier().equals(userNode.getStringValue("username"))) {
         if (oldLanguage == null)
            oldLanguage = "";
         return !oldLanguage.equals(newLanguage);
      }
      return false;
   }

}