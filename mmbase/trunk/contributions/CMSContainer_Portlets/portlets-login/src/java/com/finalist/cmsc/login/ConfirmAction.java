package com.finalist.cmsc.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.util.Encode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.person.RegisterStatus;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.util.HttpUtil;

public class ConfirmAction extends Action{
   private static final Logger log = Logging.getLoggerInstance(ConfirmAction.class.getName());

   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
         HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

      String emailAddress = httpServletRequest.getParameter("s");
      String returnUrl = httpServletRequest.getParameter("returnurl");
      Encode encoder = new org.mmbase.util.Encode("BASE64");
      emailAddress = encoder.decode(emailAddress);
      String target = "failure";
      if (StringUtils.isNotBlank(returnUrl)) {
         returnUrl = encoder.decode(returnUrl);
      }
//      Cloud cloud = getCloudForAnonymousUpdate(false);
      if (emailAddress != null) {
         AuthenticationService authenticationService = (AuthenticationService)ApplicationContextFactory.getBean("authenticationService");
         PersonService personService = (PersonService)ApplicationContextFactory.getBean("personService");
         Long authenticationId = authenticationService.getAuthenticationIdForUserId(emailAddress);
         if(authenticationId > 0) {
            Person person = personService.getPersonByAuthenticationId(authenticationId);
            if(person != null) {
               if (person.getActive().equals(RegisterStatus.ACTIVE.getName())) {
                  target = "actived";
               }
               else {
                  person.setActive(RegisterStatus.ACTIVE.getName());
                  personService.updatePerson(person);
                  target = "success";
               }
            }
         }
      }
      returnUrl += target;
      returnUrl = HttpUtil.getWebappUri(httpServletRequest) + returnUrl;
      httpServletResponse.sendRedirect(httpServletResponse.encodeRedirectURL(returnUrl));
      return null;
   }
}
