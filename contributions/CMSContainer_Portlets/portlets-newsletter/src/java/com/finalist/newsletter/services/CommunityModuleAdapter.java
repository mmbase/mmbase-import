package com.finalist.newsletter.services;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.Authentication;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

public class CommunityModuleAdapter {
   /**
    * @param servletContext get servletContext:
    *In portlet : ((PortletContextImpl) this.getPortletContext()).getServletContext()
    * @return get current Person.
    */
   public static Person getCurrentUser(ServletContext servletContext) {

      WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);

      SecurityContext securityContext = SecurityContextHolder.getContext();
      Authentication authentication = securityContext.getAuthentication();

      Person person = null;

      if (null == authentication) {
         Object obj = authentication.getPrincipal();
         if (obj instanceof UserDetails) {
            String username = ((UserDetails) obj).getUsername();
            PersonService personService = (PersonService) ctx.getBean("personService");
            person = personService.getPersonByUserId(username);
         }
      }


      return person;

   }

}
