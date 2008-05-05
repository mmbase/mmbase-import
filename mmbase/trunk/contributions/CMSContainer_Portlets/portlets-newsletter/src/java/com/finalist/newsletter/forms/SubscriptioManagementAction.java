package com.finalist.newsletter.forms;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.services.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.web.struts.DispatchActionSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptioManagementAction extends DispatchActionSupport {

   private static Log log = LogFactory.getLog(SubscriptioManagementAction.class);

   NewsletterService newsletterService;
   NewsletterSubscriptionServices subscriptionServices;
   NewsletterPublicationService publicationService;

   protected void onInit() {
      super.onInit();
      newsletterService = (NewsletterService) getWebApplicationContext().getBean("newsletterServices");
      subscriptionServices = (NewsletterSubscriptionServices) getWebApplicationContext().getBean("newsletterSubscriptionServices");
      publicationService = NewsletterServiceFactory.getNewsletterPublicationService();
   }

   protected ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                       HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      log.debug("No parameter specified,go to dashboard");
      request.setAttribute("newslettercount", newsletterService.countAllNewsletters());
      request.setAttribute("termcount", newsletterService.countAllTerms());
      request.setAttribute("subscriptioncount", subscriptionServices.countAllSubscriptions());
      request.setAttribute("publicationcount", publicationService.countAllPublications());
      return mapping.findForward("newsletterdashboard");
   }

   public ActionForward newsletterOverview(ActionMapping mapping, ActionForm form,
                                           HttpServletRequest request, HttpServletResponse response) {
      log.debug("Show newsletterOverview");

      List<Newsletter> newsletters;

      String title = request.getParameter("query_parameter_title");
      if (StringUtils.isBlank(title)) {
         newsletters = newsletterService.getAllNewsletter();
      }
      else {
         newsletters = newsletterService.getNewslettersByTitle(title);
      }

      List<Map> results = convertToMap(newsletters);

      request.setAttribute("results", results);
      return mapping.findForward("newsletteroverview");

   }

   public ActionForward newsletterDetail(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response) {
      String newsletterid = request.getParameter("nesletterId");
      log.debug(String.format("Show Newsletter %s's detail", newsletterid));

      String name = request.getParameter("query_parameter_name");
      String email = request.getParameter("query_parameter_email");

      int newsletterId = Integer.parseInt(newsletterid);
      List<Subscription> subscriptions = subscriptionServices.getSubscriptionsByNewsletterId(newsletterId);
      List<Map<String, String>> results = convertToMap(name, email, subscriptions);

      request.setAttribute("results", results);
      request.setAttribute("newsletter", newsletterService.getNewsletterName(newsletterId));

      return mapping.findForward("newsletterdetail");
   }

   private List<Map> convertToMap(List<Newsletter> newsletters) {
      List<Map> results = new ArrayList<Map>();
      for (Newsletter newsletter : newsletters) {
         Map result = new HashMap();
         int newsletterId = newsletter.getId();
         result.put("id", newsletter.getId());
         result.put("title", newsletter.getTitle());
         result.put("countpublications", publicationService.countPublicationByNewsletter(newsletterId));
         result.put("countSentPublicatons", publicationService.countSentPublications(newsletterId));
         result.put("countSubscriptions", subscriptionServices.countSubscriptionByNewsletter(newsletterId));
         results.add(result);
      }
      return results;
   }

   private List<Map<String, String>> convertToMap(String name, String email, List<Subscription> subscriptions) {
      List<Map<String, String>> results = new ArrayList<Map<String, String>>();
      for (Subscription subscription : subscriptions) {
         Person person = CommunityModuleAdapter.getUserById(subscription.getSubscriberId());
         boolean sameName = StringUtils.isBlank(name) || (person.getLastName().equals(name) || person.getFirstName().equals(name));
         boolean sameemail = StringUtils.isBlank(email) || (person.getEmail().equals(email));
         Map result = new HashMap();
         if (sameName && sameemail) {

            result.put("id", Integer.toString(subscription.getId()));
            result.put("username", person.getNickname());
            result.put("email", person.getEmail());
            result.put("fullname", person.getLastName() + "" + person.getFirstName());

            results.add(result);
         }
      }
      return results;
   }

   


}
