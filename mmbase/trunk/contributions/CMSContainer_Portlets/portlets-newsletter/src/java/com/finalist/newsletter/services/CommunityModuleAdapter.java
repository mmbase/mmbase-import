package com.finalist.newsletter.services;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;

public class CommunityModuleAdapter {

   private static Logger log = Logging.getLoggerInstance(CommunityModuleAdapter.class.getName());

   public static Person getCurrentUser() {

      PersonService personService = (PersonService) ApplicationContextFactory.getApplicationContext().getBean("personService");

      SecurityContext securityContext = SecurityContextHolder.getContext();
      Authentication authentication = securityContext.getAuthentication();

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

   public static boolean isUserLogin(){
      return null != getCurrentUser();
   }

   public static int getCurrentUserId(){
      Person person = getCurrentUser();
      if(null==person){
         return -1;
      }

     return person.getAuthenticationId().intValue();
   }

   public static Person getUserById(String id) {

      PersonService personService = (PersonService) ApplicationContextFactory.getApplicationContext().getBean("personService");
      Person person = personService.getPersonByAuthenticationId(Long.decode(id));
      log.debug("Get user(Person) " + id + " from community module,get " + person);
      return person;

   }


}
