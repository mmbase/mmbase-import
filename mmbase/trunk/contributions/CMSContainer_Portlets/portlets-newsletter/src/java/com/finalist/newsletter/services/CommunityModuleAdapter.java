package com.finalist.newsletter.services;
//
//import com.finalist.cmsc.services.community.person.Person;
//import com.finalist.cmsc.services.community.person.PersonService;
//import org.acegisecurity.context.SecurityContextHolder;
//import org.acegisecurity.userdetails.UserDetails;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

public class CommunityModuleAdapter {
   /**
    * @param servletContext use RequestContextUtils.getWebApplicationContext(ServletRequest request) to get a WebApplicationContext
    * @return get current Person.
    */
//   public static Person getCurrentUserId(ServletContext servletContext) {
//
//      WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
//
//      Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//      Person person = null;
//
//      if (obj instanceof UserDetails) {
//         String username = ((UserDetails) obj).getUsername();
//         PersonService personService = (PersonService) ctx.getBean("personService");
//         person = personService.getPersonByUserId(username);
//
//         System.out.println("000000000000" + username);
//      }
//
//      return person;
//
//   }

}
