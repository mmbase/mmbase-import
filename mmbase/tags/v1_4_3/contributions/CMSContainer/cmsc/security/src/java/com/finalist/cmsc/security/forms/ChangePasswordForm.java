package com.finalist.cmsc.security.forms;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.ContextProvider;

import com.finalist.cmsc.struts.MMBaseAction;

/**
 * Form bean for the ChangePasswordForm page.
 * 
 * @author Nico Klasens
 */
@SuppressWarnings("serial")
public class ChangePasswordForm extends ActionForm {

   private String password1;
   private String newpassword;
   private String confirmnewpassword;


   @Override
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      ActionErrors errors = new ActionErrors();

      if (getPassword1() == null || getPassword1().trim().length() == 0) {
         errors.add("password1", new ActionMessage("error.password.incorrect"));
      }
      if (getNewpassword() == null || getNewpassword().trim().length() < 5 || getNewpassword().trim().length() > 15) {
         errors.add("newpassword", new ActionMessage("error.password.invalid"));
      }
      if (getConfirmnewpassword() == null || getConfirmnewpassword().trim().length() < 5
            || getConfirmnewpassword().trim().length() > 15) {
         errors.add("confirmnewpassword", new ActionMessage("error.password.invalid"));
      }
      if (!getConfirmnewpassword().equals(getNewpassword())) {
         errors.add("newpassword", new ActionMessage("error.password.nomatch"));
      }
      if (errors.size() == 0) {
         if (getPassword1().equals(getNewpassword())) {
            errors.add("newpassword", new ActionMessage("error.newpassword.incorrect"));
         }
         else {
            try {
               Cloud cloud = MMBaseAction.getCloudFromSession(request);
               HashMap<String, String> user = new HashMap<String, String>();
               user.put("username", cloud.getUser().getIdentifier());
               user.put("password", password1);
               ContextProvider.getCloudContext(ContextProvider.getDefaultCloudContextName()).getCloud("mmbase",
                     "name/password", user);
            }
            catch (java.lang.SecurityException se) {
               errors.add("password1", new ActionMessage("error.password.incorrect"));
            }
         }

      }
      return errors;
   }


   public String getPassword1() {
      return password1;
   }


   public void setPassword1(String password) {
      this.password1 = password;
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