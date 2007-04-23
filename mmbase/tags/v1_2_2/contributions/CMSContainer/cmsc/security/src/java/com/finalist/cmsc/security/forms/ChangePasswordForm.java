package com.finalist.cmsc.security.forms;

import java.util.HashMap;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.ContextProvider;

import com.finalist.cmsc.struts.MMBaseAction;

import javax.servlet.http.HttpServletRequest;

/**
 * Form bean for the ChangePasswordForm page.
 *
 * @author Nico Klasens
 */
@SuppressWarnings("serial")
public class ChangePasswordForm extends ActionForm {

   private String password;
   private String newpassword;
   private String confirmnewpassword;

   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      ActionErrors errors = new ActionErrors();

      if (getPassword() == null || getPassword().trim().length() == 0) {
         errors.add("password", new ActionError("error.password.incorrect"));
      }
      if (getNewpassword() == null || getNewpassword().trim().length() < 5 || getNewpassword().trim().length() > 15) {
         errors.add("newpassword", new ActionError("error.password.invalid"));
      }
      if (getConfirmnewpassword() == null || getConfirmnewpassword().trim().length() < 5 || getConfirmnewpassword().trim().length() > 15) {
         errors.add("confirmnewpassword", new ActionError("error.password.invalid"));
      }
      if (!getConfirmnewpassword().equals(getNewpassword())) {
         errors.add("newpassword", new ActionError("error.password.nomatch"));
      }
      if (errors.size() == 0) {
         if (getPassword().equals(getNewpassword())) {
            errors.add("newpassword", new ActionError("error.newpassword.incorrect"));
         }
         else {
             try {
             Cloud cloud = MMBaseAction.getCloudFromSession(request);
             HashMap<String,String> user = new HashMap<String,String>();
             user.put("username", cloud.getUser().getIdentifier());
             user.put("password", password);
             ContextProvider.getCloudContext(ContextProvider.getDefaultCloudContextName()).getCloud("mmbase","name/password", user);
             }
             catch(java.lang.SecurityException se) {
                 errors.add("password", new ActionError("error.password.incorrect")); 
             }
          }

      }
      return errors;
   }

   public String getPassword() {
      return password;
   }

  public void setPassword(String password) {
      this.password = password;
   }

   public String getNewpassword() {
      return newpassword;
   }

   public void setNewpassword(String newpassword) {
      this.newpassword = newpassword;
   }
   
   public String getConfirmnewpassword() {
      return confirmnewpassword;
   }

   public void setConfirmnewpassword(String confirmnewpassword) {
      this.confirmnewpassword = confirmnewpassword;
   }
}