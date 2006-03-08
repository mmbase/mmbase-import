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
package nl.leocms.forms;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * Form bean for the ReactionForm page.
 *
 * @author Jeoffrey Bakker
 * @version $Revision: 1.2 $, $Date: 2006-03-08 22:23:51 $
 *
 * @struts:form name="ReactionForm"
 */

public class ReactionForm extends ActionForm {

   private static final Logger log = Logging.getLoggerInstance(ReactionForm.class);

   private String referer;
   private String email;
   private String name;
   private String reaction;
   private String objectnumber;

   /**
    * Actual validate function
    * @param mapping
    * @param request
    * @return
    */
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      ActionErrors errors = new ActionErrors();

      if (name == null || name.length() == 0) {
         errors.add("name", new ActionError("struts.validation.not_allowed_empty"));
      }

      if (email == null || email.length() == 0) {
         errors.add("email", new ActionError("struts.validation.not_allowed_empty"));
      }
//      else if (!email.matches()){
//         errors.add("email", new ActionError("struts.validation.invalid_email"));
//      }

      if (reaction == null || reaction.length() == 0) {
         errors.add("reaction", new ActionError("struts.validation.not_allowed_empty"));
      }
      else if (reaction.length() > 2048) {
         errors.add("reaction", new ActionError("reaction.tolarge"));
      }


      if (objectnumber == null || objectnumber.length() == 0) {
         errors.add("objectnumber", new ActionError("global.error"));
      }

      if (!errors.isEmpty()) {
         log.debug("There are some errors");
      }

      return errors;
   }


   /**
    * Getter for property referer.
    * @return property referer.
    */
   public String getReferer() {
      return referer;
   }


   /**
    * Setter for property referer.
    * @param referer property referer.
    */
   public void setReferer(String referer) {
      this.referer = referer;
   }


   /**
    * Getter for property email.
    * @return property email.
    */
   public String getEmail() {
      return email;
   }


   /**
    * Setter for property email.
    * @param email property email.
    */
   public void setEmail(String email) {
      this.email = email;
   }


   /**
    * Getter for property name.
    * @return property name.
    */
   public String getName() {
      return name;
   }


   /**
    * Setter for property name.
    * @param name property name.
    */
   public void setName(String name) {
      this.name = name;
   }


   /**
    * Getter for property reaction.
    * @return property reaction.
    */
   public String getReaction() {
      return reaction;
   }


   /**
    * Setter for property reaction.
    * @param reaction property reaction.
    */
   public void setReaction(String reaction) {
      this.reaction = reaction;
   }


   /**
    * Getter for property objectnumber.
    * @return property objectnumber.
    */
   public String getObjectnumber() {
      return objectnumber;
   }


   /**
    * Setter for property objectnumber.
    * @param objectnumber property objectnumber.
    */
   public void setObjectnumber(String objectnumber) {
      this.objectnumber = objectnumber;
   }
}

/**
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/05 21:43:58  henk
 * First version of the NatMM contribution.
 *
 * Revision 1.3  2003/12/12 08:54:47  nico
 * unused imports and other small issues
 *
 * Revision 1.2  2003/10/31 14:19:43  jeoffrey
 * use redirect forward
 * and open reaction form on errors
 *
 * Revision 1.1  2003/10/30 11:14:01  jeoffrey
 * added reaction to an artikel
 *
 */