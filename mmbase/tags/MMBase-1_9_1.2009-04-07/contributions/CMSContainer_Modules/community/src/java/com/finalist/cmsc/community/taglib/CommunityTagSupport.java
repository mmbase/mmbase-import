package com.finalist.cmsc.community.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.preferences.PreferenceService;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.AuthorityService;

/**
 * @author Wouter Heijke
 */
public abstract class CommunityTagSupport extends SimpleTagSupport {

   private WebApplicationContext ctx;

   private AuthenticationService authenticationService;

   private AuthorityService authorityService;

   private PersonService personService;

   private PreferenceService preferenceService;

   @Override
   public void doTag() throws JspException, IOException {
      PageContext pctx = (PageContext) getJspContext();
      ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(pctx.getServletContext());
      ctx.getAutowireCapableBeanFactory().autowireBeanProperties(this, Autowire.BY_NAME.value(), false);

      doTagLogic();
   }

   public AuthenticationService getAuthenticationService() {
      return authenticationService;
   }

   public void setAuthenticationService(AuthenticationService authenticationService) {
      this.authenticationService = authenticationService;
   }

   public AuthorityService getAuthorityService() {
      return authorityService;
   }

   public void setAuthorityService(AuthorityService authorityService) {
      this.authorityService = authorityService;
   }

   public PersonService getPersonService() {
      return personService;
   }

   public void setPersonService(PersonService personService) {
      this.personService = personService;
   }

   public PreferenceService getPreferenceService() {
      return preferenceService;
   }

   public void setPreferenceService(PreferenceService preferenceService) {
      this.preferenceService = preferenceService;
   }

   /**
    * @throws JspException
    *            If something goes wrong when writing to the page
    * @throws IOException
    *            If something goes wrong when writing to the page
    */
   protected abstract void doTagLogic() throws JspException, IOException;
}