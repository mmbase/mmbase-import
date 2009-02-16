/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.cmsc.services.community;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.User;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.finalist.cmsc.mmbase.EmailUtil;
import com.finalist.cmsc.services.Properties;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.preferences.PreferenceService;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.util.NameUtil;

/**
 * CommunityServiceImpl, a CMSc service class.
 * 
 * @author Remco Bos
 */
public class CommunityServiceImpl extends CommunityService {

   private static Log log = LogFactory.getLog(CommunityServiceImpl.class);

   private AuthenticationManager authenticationManager;
   private PreferenceService preferenceService;
   private PersonService personService;
   private AuthenticationService authenticationService;

   @Override
   protected void init(ServletConfig config, Properties properties) throws Exception {
      /* Some Spring magic. Sets the AuthenticationManager and PreferenceService */
      ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
      ac.getAutowireCapableBeanFactory().autowireBeanProperties(this, Autowire.BY_NAME.value(), false);
   }

   @Override
   public void login(String userName, String password) {
      UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userName, password);
      try {
         org.acegisecurity.Authentication authentication = authenticationManager.authenticate(authRequest);
         SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (AuthenticationException ae) {
         SecurityContextHolder.clearContext();
         log.debug(String.format("Authentication attempt failed for user %s", userName), ae);
      }
   }

   @Override
   public void logout() {
      SecurityContextHolder.clearContext();
   }

   @Override
   public boolean isAuthenticated() {
      SecurityContext context = SecurityContextHolder.getContext();
      org.acegisecurity.Authentication authentication = context.getAuthentication();
      return (authentication != null) && authentication.isAuthenticated();
   }

   @Override
   public String getAuthenticatedUser() {
      User principal = getPrincipal();
      return principal != null ? principal.getUsername() : null;
   }

   @Override
   public List < String > getAuthorities() {
      List < String > authorities = new ArrayList < String >();
      User principal = getPrincipal();
      if (principal != null) {
         GrantedAuthority[] grantedAuthorities = principal.getAuthorities();
         for (GrantedAuthority grantedAuthoritie : grantedAuthorities) {
            authorities.add(grantedAuthoritie.getAuthority());
         }
      }
      return authorities;
   }

   @Override
   public boolean hasAuthority(String authority) {
      User principal = getPrincipal();
      if (principal != null) {
         GrantedAuthority[] grantedAuthorities = principal.getAuthorities();
         for (GrantedAuthority grantedAuthoritie : grantedAuthorities) {
            if (grantedAuthoritie.getAuthority().equals(authority)) {
               return true;
            }
         }
      }
      return false;
   }

   private User getPrincipal() {
      SecurityContext context = SecurityContextHolder.getContext();
      org.acegisecurity.Authentication authentication = context.getAuthentication();
      return authentication != null ? (User) authentication.getPrincipal() : null;
   }

   public Map < Long , Map < String , String > > getPreferencesByModule(String module) {
      return preferenceService.getPreferencesByModule(module);
   }

   public Map < String , Map < String , String > > getPreferencesByUserId(String userId) {
      return preferenceService.getPreferencesByUserId(userId);
   }

   @Override
   public List < String > getPreferenceValues(String module, String userId, String key) {
      return preferenceService.getPreferenceValues(module, userId, key);
   }

   public void createPreference(String module, String userId, String key, String value) {
      preferenceService.createPreference(module, userId, key, value);
   }

   public void deletePreference(String module, String userId, String key, String value) {
      preferenceService.deletePreference(module, userId, key, value);
   }

   // TODO: replace the previous methods by methods who accept the following
   // properties!

   /**
    * @deprecated please try to use another service
    */
   @Override
   public void createPreference(String module, String userId, String key, List < String > values) {
      for (String value : values) {
         preferenceService.createPreference(module, userId, key, value);
      }
   }

   /**
    * @deprecated please try to use another service
    */
   @Override
   public Map < String , Map < String , List < String > > > getPreferences(String module, String userId, String key,
         String value) {
      return null;
   }

   /**
    * @deprecated please try to use another service
    */
   @Override
   public Map < String , Map < String , String > > getUserProperty(String userId) {
      // return preferenceService.getPreferencesByUserId(userId);
      return null;
   }

   /** {@inheritDoc} */
   @Override
   boolean sendPassword(String email, String senderName, String senderEmail, String emailSubject, String emailBody) {
      boolean authenticationFound = false;

      if (StringUtils.isEmpty(email)) {
         throw new IllegalArgumentException("Username not found.");
      }
      if (StringUtils.isEmpty(emailSubject)) {
         emailSubject = "<email subject is missing>";
      }
      if (StringUtils.isEmpty(emailBody)) {
         emailBody = "<email body is missing>";
      }

      String name = "";
      StringBuilder body = new StringBuilder(emailBody);

      Person example = new Person();
      example.setEmail(email);
      // Retrieve a list of persons that match this example
      List < Person > persons = personService.getPersons(example);

      for (Person person : persons) {
         Authentication authentication = authenticationService.getAuthenticationById(person.getAuthenticationId());

         if (authentication != null) {
            if ("".equals(name)) {
               name = NameUtil.getFullName(person.getFirstName(), person.getInfix(), person.getLastName());
            }

            body.append("\n---\n");
            body.append("Gebruikersnaam: ").append(authentication.getUserId());
            body.append("\n");
            body.append("Wachtwoord    : ").append(authentication.getPassword());

            authenticationFound = true;
         }
      }

      if (authenticationFound) {
         EmailUtil.send(null, name, email, senderName, senderEmail, emailSubject, body.toString());
      }

      return authenticationFound;
   }

   /**
    * @deprecated please try to use another service
    */
   @Override
   public void removePreferences(String module, String userId, String key) {
      List < String > valueList = preferenceService.getPreferenceValues(module, userId, key);
      for (String value : valueList) {
         preferenceService.deletePreference(module, userId, key, value);
      }
   }

   @Required
   public void setAuthenticationManager(AuthenticationManager authenticationManager) {
      this.authenticationManager = authenticationManager;
   }

   @Required
   public void setPreferenceService(PreferenceService preferenceService) {
      this.preferenceService = preferenceService;
   }

   @Required
   public void setPersonService(PersonService personService) {
      this.personService = personService;
   }

   @Required
   public void setAuthenticationService(AuthenticationService authenticationService) {
      this.authenticationService = authenticationService;
   }
}
