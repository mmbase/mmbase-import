/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.authorization.forms;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import com.finalist.mmbase.util.CloudFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Form bean for the ChangePasswordForm page.
 *
 * @author Edwin van der Elst
 * @version $Revision: 1.2 $, $Date: 2006-03-08 22:23:51 $
 *
 * @struts:form name="ChangePasswordForm"
 */

public class ChangePasswordForm extends ActionForm {

   private static final Logger log = Logging.getLoggerInstance(ChangePasswordForm.class);

   private String password;
   private String newpassword;
   private String confirmnewpassword;
   private int nodenumber;

   /**
    * Actual validate function
    * @param mapping
    * @param request
    * @return
    */
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      ActionErrors errors = new ActionErrors();

      if (getPassword() == null || getPassword().trim().length() == 0) {
         errors.add("password", new ActionError("incorrect.password"));
      }
      if (getNewpassword() == null || getNewpassword().trim().length() < 5 || getNewpassword().trim().length() > 15) {
         errors.add("newpassword", new ActionError("invalid.password"));
      }
      if (getConfirmnewpassword() == null || getConfirmnewpassword().trim().length() < 5 || getConfirmnewpassword().trim().length() > 15) {
         errors.add("confirmnewpassword", new ActionError("invalid.password"));
      }
      if (!getConfirmnewpassword().equals(getNewpassword())) {
         errors.add("newpassword", new ActionError("nomatch.password"));
      }
      if (errors.size() == 0) {
         if (getPassword().equals(getNewpassword())) {
            errors.add("newpassword", new ActionError("incorrect.newpassword"));
         }
         else {
            Cloud cloud = CloudFactory.getCloud();
            Node userNode = cloud.getNode(getNodenumber());
            String currentPassword = userNode.getStringValue("password");
            if (!currentPassword.equals(password)) {
               errors.add("password", new ActionError("incorrect.password"));
            }
         }
      }
      return errors;
   }

   /**
    * Getter for property password.
    * @return property password.
    */
   public String getPassword() {
      return password;
   }

   /**
    * Setter for property password.
    * @param password property password.
    */
   public void setPassword(String password) {
      this.password = password;
   }
   /**
    * @return
    */
   public int getNodenumber() {
      return nodenumber;
   }

   /**
    * @param nodeNumber
    */
   public void setNodenumber(int nodenumber) {
      this.nodenumber = nodenumber;
   }

   /**
    * @return
    */
   public String getNewpassword() {
      return newpassword;
   }

   /**
    * @param password2
    */
   public void setNewpassword(String newpassword) {
      this.newpassword = newpassword;
   }
   
   /**
    * @return
    */
   public String getConfirmnewpassword() {
      return confirmnewpassword;
   }

   /**
    * @param password2
    */
   public void setConfirmnewpassword(String confirmnewpassword) {
      this.confirmnewpassword = confirmnewpassword;
   }
}