package com.finalist.cmsc.community.forms;

import org.springframework.web.struts.ActionSupport;
import org.springframework.web.context.WebApplicationContext;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.person.PersonService;

/**
 * @author Remco Bos
 */
public class AbstractCommunityAction extends ActionSupport {

    protected AuthenticationService getAuthenticationService() {
        WebApplicationContext ctx = getWebApplicationContext();
        return (AuthenticationService)ctx.getBean("authenticationService");
    }

    protected PersonService getPersonService() {
        WebApplicationContext ctx = getWebApplicationContext();
        return (PersonService)ctx.getBean("personService");
    }
}
