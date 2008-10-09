package com.finalist.cmsc.security.forms;

import java.util.*;

import org.apache.struts.action.*;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.cmsc.struts.MMBaseForm;

import javax.servlet.http.HttpServletRequest;

/**
 * Form bean for the UserForm page.
 * 
 * @author Nico Klasens
 */
@SuppressWarnings("serial")
public class UserForm extends MMBaseForm {

   private String username;
   private String password1;
   private String password2;
   private String rank;

   private String firstname;
   private String prefix;
   private String surname;

   private String language;

   private String email;
   private boolean emailSignal;
   private String company;
   private String department;
   private String function;
   private String note;
   private String website;
   private String defaultcontext;
   private int status;
   private Date validfrom;
   private Date validto;

   private List<Option> ranks = new ArrayList<Option>();
   private List<Option> contexts = new ArrayList<Option>();


   @Override
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      ActionErrors errors = new ActionErrors();

      if (getId() == -1) {
         if (getUsername() == null || getUsername().length() < 3 || getUsername().length() > 15) {
            errors.add("username", new ActionMessage("error.username.invalid"));
         }
         else {
            String user = getUsername();
            NodeList list = MMBaseAction.getCloudFromSession(request).getNodeManager("user").getList(
                  "username='" + user + "'", null, null);
            if (list.size() != 0) {
               errors.add("username", new ActionMessage("error.username.alreadyexists"));
            }
         }
         if (getPassword1() == null || getPassword1().length() < 5 || getPassword1().length() > 15) {
            errors.add("password", new ActionMessage("error.password.invalid"));
         }
         if (getPassword2() == null || getPassword2().length() < 5 || getPassword2().length() > 15) {
            errors.add("password2", new ActionMessage("error.password.invalid"));
         }
         if (errors.size() <= 0) {
            if (!getPassword1().equals(getPassword2())) {
               errors.add("password", new ActionMessage("error.password.nomatch"));
            }
         }
      }
      else {
         if (getPassword1() != null && getPassword1().length() > 0) {
            if (getPassword1() == null || getPassword1().length() < 5 || getPassword1().length() > 15) {
               errors.add("password", new ActionMessage("error.password.invalid"));
            }
            if (getPassword2() == null || getPassword2().length() < 5 || getPassword2().length() > 15) {
               errors.add("password2", new ActionMessage("error.password.invalid"));
            }
            if (errors.size() <= 0) {
               if (!getPassword1().equals(getPassword2())) {
                  errors.add("password", new ActionMessage("error.password.nomatch"));
               }
            }
         }
      }

      if (getEmail() == null || getEmail().length() == 0) {
         errors.add("email", new ActionMessage("error.email.empty"));
      }

      return errors;
   }


   public String getUsername() {
      return username;
   }


   public void setUsername(String username) {
      this.username = username;
   }


   public String getPassword1() {
      return password1;
   }


   public void setPassword1(String password) {
      this.password1 = password;
   }


   public String getDepartment() {
      return department;
   }


   public void setDepartment(String department) {
      this.department = department;
   }


   public String getEmail() {
      return email;
   }


   public void setEmail(String email) {
      this.email = email;
   }


   public String getNote() {
      return note;
   }


   public void setNote(String note) {
      this.note = note;
   }


   public String getPassword2() {
      return password2;
   }


   public void setPassword2(String password2) {
      this.password2 = password2;
   }


   public String getPrefix() {
      return prefix;
   }


   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }


   public String getSurname() {
      return surname;
   }


   public void setSurname(String surname) {
      this.surname = surname;
   }


   public String getFirstname() {
      return firstname;
   }


   public void setFirstname(String firstname) {
      this.firstname = firstname;
   }


   public boolean isEmailSignal() {
      return emailSignal;
   }


   public void setEmailSignal(boolean emailSignal) {
      this.emailSignal = emailSignal;
   }


   public String getFunction() {
      return function;
   }


   public void setFunction(String function) {
      this.function = function;
   }


   public String getWebsite() {
      return website;
   }


   public void setWebsite(String website) {
      this.website = website;
   }


   public String getCompany() {
      return company;
   }


   public void setCompany(String company) {
      this.company = company;
   }


   public String getDefaultcontext() {
      return defaultcontext;
   }


   public void setDefaultcontext(String defaultcontext) {
      this.defaultcontext = defaultcontext;
   }


   public int getStatus() {
      return status;
   }


   public void setStatus(int status) {
      this.status = status;
   }


   public Date getValidfrom() {
      return validfrom;
   }


   public void setValidfrom(Date validfrom) {
      this.validfrom = validfrom;
   }


   public Date getValidto() {
      return validto;
   }


   public void setValidto(Date validto) {
      this.validto = validto;
   }


   public String getRank() {
      return rank;
   }


   public void setRank(String rank) {
      this.rank = rank;
   }


   public void addRank(String number, String name) {
      ranks.add(new Option(name, number));
   }


   public void resetRanks() {
      ranks = new ArrayList<Option>();
   }


   public List<Option> getRanks() {
      return ranks;
   }


   public void addContext(String number, String name) {
      contexts.add(new Option(name, number));
   }


   public List<Option> getContexts() {
      return contexts;
   }


   public List<Option> getStatuses() {
      List<Option> statusList = new ArrayList<Option>();
      statusList.add(new Option("Actief", "1"));
      statusList.add(new Option("Geblokkeerd", "-1"));
      return statusList;
   }


   public String getLanguage() {
      return language;
   }


   public void setLanguage(String language) {
      this.language = language;
   }

   public static class Option {
      private String description;
      private String value;


      public Option(String description, String value) {
         super();
         this.description = description;
         this.value = value;
      }


      public String getValue() {
         return value;
      }


      public void setValue(String value) {
         this.value = value;
      }


      public String getDescription() {
         return description;
      }


      public void setDescription(String description) {
         this.description = description;
      }
   }
}