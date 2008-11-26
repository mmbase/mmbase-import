package com.finalist.cmsc.login;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.mmbase.applications.crontab.AbstractCronJob;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.person.RegisterStatus;
import com.finalist.cmsc.services.community.security.AuthenticationService;

public class RegisterCleanCronJob extends AbstractCronJob {

   @Override
   public void run() {
      clean();
   }

   private void clean() {
      AuthenticationService authenticationService = (AuthenticationService)ApplicationContextFactory.getBean("authenticationService");
      PersonService personService = (PersonService)ApplicationContextFactory.getBean("personService");
      Person example = new Person();
      example.setActive(RegisterStatus.UNCONFIRMED.getName());
      List<Person> persons = personService.getPersons(example);
      for(Person person:persons) {
         if(person.getRegisterDate() != null) {
            Date expireDate  = DateUtils.addDays(person.getRegisterDate(), 3);
            if(expireDate.before(new Date())) {
               Long authId = person.getAuthenticationId();
               if (authId > 0) {
                  personService.deletePersonByAuthenticationId(authId);           
                  authenticationService.deleteAuthentication(authId);
               }
            }
         }         
      }
   }
}
