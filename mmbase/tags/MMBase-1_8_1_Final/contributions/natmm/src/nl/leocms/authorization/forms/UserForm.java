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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;
import org.mmbase.bridge.NodeList;
import org.mmbase.security.Rank;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import com.finalist.mmbase.util.CloudFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Form bean for the UserForm page.
 *
 * @author Edwin van der Elst
 * @version $Revision: 1.6 $, $Date: 2006-03-28 07:55:54 $
 *
 * @struts:form name="UserForm"
 */

public class UserForm extends ActionForm {

   private static final Logger log = Logging.getLoggerInstance(UserForm.class);
   
   public static String ANONYMOUS = "Verlopen";
   public static String BASIC_USER = "Gebruiker";
   public static String CHIEFEDITOR = "Hoofdgebruiker";
   public static String ADMINISTRATOR = "Admin";

   public static String ACTIVATE_ACTION = "Activeer account";
   public static String SAVE_ACTION = "Opslaan";
   public static String CANCEL_ACTION = "Annuleer";

   private String action = "";
   private String username;
   private String password;
   private String password2;
   private String tussen;
   private String email;
   private String afdeling;
   private String notitie;
   private String voornaam;
   private String achternaam;
   private boolean emailSignalering;
   private String rank;

   private int nodeNumber;

   private Map rollen;

   /**
    * Actual validate function
    * @param mapping
    * @param request
    * @return
    */
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      ActionErrors errors = new ActionErrors();

      if (getNodeNumber() == -1) {
         if (getUsername() == null || getUsername().length() == 0) {
            errors.add("username", new ActionError("missing.username"));
         }
         else if (getUsername() == null || getUsername().length() < 5 || getUsername().length() > 15) {
            errors.add("username", new ActionError("invalid.username"));
         }
         else {
            String user = getUsername();
            NodeList list = CloudFactory.getCloud().getNodeManager("users").getList("account='"+user+"'",null,null);
            if (list.size()!=0) {
               errors.add("username", new ActionError("error_code_3"));
            }
         }
         if (getPassword() == null || getPassword().length() < 5 || getPassword().length() > 15) {
            errors.add("password", new ActionError("invalid.password"));
         }
         if (getPassword2() == null || getPassword2().length() < 5 || getPassword2().length() > 15) {
            errors.add("password2", new ActionError("invalid.password"));
         }
         if (errors.size() <= 0) {
            if (!getPassword().equals(getPassword2())) {
               errors.add("password", new ActionError("nomatch.password"));
            }
         }
      }
      else {
         if (getPassword() != null && getPassword().length() > 0) {
            if (getPassword() == null || getPassword().length() < 5 || getPassword().length() > 15) {
               errors.add("password", new ActionError("invalid.password"));
            }
            if (getPassword2() == null || getPassword2().length() < 5 || getPassword2().length() > 15) {
               errors.add("password2", new ActionError("invalid.password"));
            }
            if (errors.size() <= 0) {
               if (!getPassword().equals(getPassword2())) {
                  errors.add("password", new ActionError("nomatch.password"));
               }
            }
         }
      }

      if (getEmail() == null || getEmail().length() == 0) {
         errors.add("email", new ActionError("error_code_11"));
      }

      // rebuild Map because after validation the roles are removed from the form-instance
      this.rollen = Util.buildRolesFromRequest(request);
      //
      return errors;
   }

   /**
    * Getter for property action.
    * @return property action
    */
   public String getAction() {
      return action;
   }

   /**
    * Setter for property action.
    * @param username property action
    */
   public void setAction(String action) {
      this.action = action;
   }

   /**
    * Getter for property username.
    * @return property username
    */
   public String getUsername() {
      return username;
   }

   /**
    * Setter for property username.
    * @param username property username
    */
   public void setUsername(String username) {
      this.username = username;
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
   public int getNodeNumber() {
      return nodeNumber;
   }

   /**
    * @param nodeNumber
    */
   public void setNodeNumber(int nodeNumber) {
      this.nodeNumber = nodeNumber;
   }

   /**
    * @return
    */
   public String getAfdeling() {
      return afdeling;
   }

   /**
    * @param afdeling
    */
   public void setAfdeling(String afdeling) {
      this.afdeling = afdeling;
   }

   /**
    * @return
    */
   public String getEmail() {
      return email;
   }

   /**
    * @param email
    */
   public void setEmail(String email) {
      this.email = email;
   }

   /**
    * @return
    */
   public String getNotitie() {
      return notitie;
   }

   /**
    * @param notitie
    */
   public void setNotitie(String notitie) {
      this.notitie = notitie;
   }

   /**
    * @return
    */
   public String getPassword2() {
      return password2;
   }

   /**
    * @param password2
    */
   public void setPassword2(String password2) {
      this.password2 = password2;
   }

   /**
    * @return
    */
   public String getTussen() {
      return tussen;
   }

   /**
    * @param tussen
    */
   public void setTussen(String tussen) {
      this.tussen = tussen;
   }

   /**
    * @return
    */
   public String getAchternaam() {
      return achternaam;
   }

   /**
    * @param achternaam
    */
   public void setAchternaam(String achternaam) {
      this.achternaam = achternaam;
   }

   /**
    * @return
    */
   public String getVoornaam() {
      return voornaam;
   }

   /**
    * @param voornaam
    */
   public void setVoornaam(String voornaam) {
      this.voornaam = voornaam;
   }

   /**
    * @return
    */
   public boolean isEmailSignalering() {
      return emailSignalering;
   }

   /**
    * @param emailSignalering
    */
   public void setEmailSignalering(boolean emailSignalering) {
      this.emailSignalering = emailSignalering;
   }

   public Map getRollen() {
      return rollen;
   }

   /**
    * @param rollen
    */
   public void setRollen(Map rollen) {
      this.rollen = rollen;
   }

   /**
    * @return Returns the rank.
    */
   public String getRank() {
      return rank;
   }

   /**
    * @param rank The rank to set.
    */
   public void setRank(String rank) {
      this.rank = rank;
   }
   
   public void setRanks(List ranks) {
      
   }

   public List getRanks() {
      List ret=new ArrayList();
      ret.add(new UserRank(ANONYMOUS,"anonymous"));
      ret.add(new UserRank(BASIC_USER,"basic user"));
      ret.add(new UserRank(CHIEFEDITOR,"chiefeditor"));
      ret.add(new UserRank(ADMINISTRATOR,"administrator"));
      return ret; 
   }
   
   public static class UserRank {
      private String description;
      private String value;
      
      /**
       * @param description
       * @param value
       */
      public UserRank(String description, String value) {
         super();
         this.description = description;
         this.value = value;
      }
      /**
       * @return Returns the value.
       */
      public String getValue() {
         return value;
      }

      /**
       * @param value The value to set.
       */
      public void setValue(String value) {
         this.value = value;
      }

      /**
       * @return Returns the description.
       */
      public String getDescription() {
         return description;
      }

      /**
       * @param description The description to set.
       * @param value The value
       */
      public void setDescription(String description) {
         this.description = description;
      }
   }
   
   
}

