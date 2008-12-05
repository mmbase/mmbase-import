package com.finalist.preferences.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.domain.PreferenceVO;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.preferences.PreferenceService;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.preferences.domain.UserProfile;

public class ProfileUtil {

   private static Log log = LogFactory.getLog(ProfileUtil.class);

   public static void updateUserProfile(UserProfile profile) {

      String accountName = profile.getAccount();
      AuthenticationService as = getAuthenticationService();
      PersonService ps = getPersonService();
      Long authenticationId = as.getAuthenticationIdForUserId(accountName);
      if (authenticationId != null) {
         String newPassword1 = profile.getPasswordText();
         String newPassword2 = profile.getPasswordConfirmation();
         if (StringUtils.isNotBlank(newPassword1) && StringUtils.isNotBlank(newPassword2)) {
            if (newPassword1.equals(newPassword2)) {
               as.updateAuthenticationPassword(accountName, newPassword1);
            }
         }
         // First retrieve the right person object from the database
         Person person = ps.getPersonByAuthenticationId(authenticationId);

         if (person == null) { // User did not exists, so create it.
            person = new Person();
            person.setAuthenticationId(authenticationId);
         }
         // Also save other fields entered in the form to the right person
         // object
         person.setFirstName(profile.getFirstName());
         person.setInfix(profile.getPrefix());
         person.setLastName(profile.getLastName());
         person.setEmail(profile.getEmail());
         // Store the new person data to the database again.
         ps.updatePerson(person);
      }
   }

   public static List<PreferenceVO> getPreferences() {
      List<PreferenceVO> preferences = new ArrayList<PreferenceVO>();

      PreferenceService ps = getPreferenceService();
      Map<String, Map<String, String>> modulePreferenceMap = ps.getPreferencesByUserId(getUserIdByAuthenticationId(getCurrentUserId()));
      if (modulePreferenceMap != null && modulePreferenceMap.size() > 0) {

         Iterator<Map.Entry<String, Map<String, String>>> keyValues = modulePreferenceMap.entrySet().iterator();
         while (keyValues.hasNext()) {
            Map.Entry<String, Map<String, String>> entry = keyValues.next();

            Map<String, String> values = entry.getValue();
            Iterator<Map.Entry<String, String>> keys = values.entrySet().iterator();
            while (keys.hasNext()) {
               PreferenceVO preference = new PreferenceVO();
               preference.setModule(entry.getKey());
               Map.Entry<String, String> keyvalue = keys.next();
               preference.setKey(keyvalue.getKey());
               preference.setValue(keyvalue.getValue());
               preferences.add(preference);
            }
         }
      }
      return preferences;
   }

   public static UserProfile getUserProfile(Long authId) {
      Authentication auth = null;
      UserProfile profile = new UserProfile();

      AuthenticationService as = getAuthenticationService();
      if (authId != null) {
         auth = as.getAuthenticationById(Long.valueOf(authId));
      }

      if (auth != null) {
         profile.setAccount(auth.getUserId());

         PersonService ps = getPersonService();
         // Returns null when no Person object was found!
         Person person = ps.getPersonByAuthenticationId(auth.getId());

         if (person != null) {
            profile.setFirstName(person.getFirstName());
            profile.setPrefix(person.getInfix());
            profile.setLastName(person.getLastName());
            profile.setEmail(person.getEmail());
         } else {
            log.debug("person failed");
         }
      }
      return profile;
   }

   public static UserProfile getUserProfile() {
      return getUserProfile(getCurrentUserId());
   }

   public static Person getCurrentUser() {

      PersonService personService = (PersonService) ApplicationContextFactory.getApplicationContext().getBean("personService");
      SecurityContext securityContext = SecurityContextHolder.getContext();
      org.acegisecurity.Authentication authentication = securityContext.getAuthentication();
      Person person = null;

      if (null != authentication) {
         Object obj = authentication.getPrincipal();
         if (obj instanceof UserDetails) {
            String username = ((UserDetails) obj).getUsername();
            person = personService.getPersonByUserId(username);
         }
      }
      return person;
   }

   public static Long getCurrentUserId() {
      Person person = getCurrentUser();
      if (null == person) {
         return new Long(-1);
      }

      return person.getAuthenticationId();
   }

   public static boolean isUserLogin() {
      return null != getCurrentUser();
   }

   public static void addPreferences(List<PreferenceVO> preferences) {

      PreferenceService ps = getPreferenceService();
      for (PreferenceVO preference : preferences) {
         List<String> values = ps.getPreferenceValues(preference.getModule(), getUserIdByAuthenticationId(Long.parseLong(preference
               .getAuthenticationId())), preference.getKey());
         if (values == null || values.size() == 0) {
            ps.createPreference(preference.getModule(), getUserIdByAuthenticationId(Long.parseLong(preference.getAuthenticationId())), preference
                  .getKey(), preference.getValue());
         } else {
            ps.updatePreference(preference.getModule(), getUserIdByAuthenticationId(Long.parseLong(preference.getAuthenticationId())), preference
                  .getKey(), values.get(0), preference.getValue());
         }
      }
   }

   private static String getUserIdByAuthenticationId(Long authenticationId) {
      Authentication authentication = getAuthenticationService().getAuthenticationById(authenticationId);
      return authentication.getUserId();
   }

   private static AuthenticationService getAuthenticationService() {
      return (AuthenticationService) ApplicationContextFactory.getBean("authenticationService");
   }

   private static PersonService getPersonService() {
      return (PersonService) ApplicationContextFactory.getBean("personService");
   }

   private static PreferenceService getPreferenceService() {
      return (PreferenceService) ApplicationContextFactory.getBean("preferenceService");
   }

   public static boolean isEmail(String s) {
      String regex = "[a-zA-Z][\\w_]+.?[\\w_]+@\\w+(\\.\\w+)+";
      Pattern p = Pattern.compile(regex);
      Matcher m = p.matcher(s);
      return m.matches();
   }
}
