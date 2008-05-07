package com.finalist.newsletter.forms;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
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
import java.util.*;

public class SubscriptioManagementAction extends DispatchActionSupport {

   private static Log log = LogFactory.getLog(SubscriptioManagementAction.class);

   NewsletterService newsletterService;
   NewsletterSubscriptionServices subscriptionServices;
   NewsletterPublicationService publicationService;
   PersonService personServices;

   protected void onInit() {
      super.onInit();
      newsletterService = (NewsletterService) getWebApplicationContext().getBean("newsletterServices");
      subscriptionServices = (NewsletterSubscriptionServices) getWebApplicationContext().getBean("subscriptionServices");
      personServices = (PersonService) getWebApplicationContext().getBean("personService");
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

      String title = request.getParameter("title");
      String subscriber = request.getParameter("subscriber");

      newsletters = newsletterService.getNewsletters(subscriber, title);

      List<Map> results = convertToMap(newsletters);

      request.setAttribute("results", results);
      return mapping.findForward("newsletteroverview");

   }

   public ActionForward listSubscription(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response) {

      String name = request.getParameter("name");
      String email = request.getParameter("email");
      String newsletterid = request.getParameter("newsletterId");

      log.debug(String.format("List all Subscriptions of Newsletter %s", newsletterid));

      List<Subscription> subscriptions;

      if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(email)) {
         subscriptions = new ArrayList(subscriptionServices.getSubscriptions(newsletterid, name, email));
      }
      else {
         subscriptions = subscriptionServices.getSubscriptionsByNewsletterId(newsletterid);
      }
      List<Map<String, String>> results = convertSubscriptionsToMap(subscriptions);
      request.setAttribute("results", results);
      request.setAttribute("newsletter", newsletterService.getNewsletterName(newsletterid));

      return mapping.findForward("newsletterdetail");
   }

   public ActionForward listSubscriptionByPerson(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response) {

      String name = request.getParameter("name");
      String email = request.getParameter("email");
      String subsriberId = request.getParameter("subsriberId");

      log.debug(String.format("List all Subscriptions of person %s", subsriberId));

      List<Subscription> subscriptions;

      if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(email)) {
         subscriptions = new ArrayList(subscriptionServices.getSubscriptions(subsriberId, name, email));
      }
      else {
         subscriptions = subscriptionServices.getSubscriptionBySubscriber(subsriberId);
      }
      List<Map<String, String>> results = convertSubscriptionsToMap(subscriptions);
      request.setAttribute("results", results);

      return mapping.findForward("ubscriber_subscriptions");
   }

   private List convertSubscriptionsToMap(List<Subscription> subscriptions) {

      List<Map> results = new ArrayList<Map>();
      for (Subscription subscription1 : subscriptions) {
         Map result = new HashMap();
         Subscription subscription = subscription1;
         result.put("id", subscription.getId());
         result.put("status",subscription.getStatus().toString());
         result.put("newsletter",newsletterService.getNewsletterBySubscription(subscription.getId()).getTitle());
         results.add(result);
      }
      return results;
   }

   public ActionForward listNewsletter(ActionMapping mapping, ActionForm form,
                                       HttpServletRequest request, HttpServletResponse response) {
      log.debug("Show newsletters");

      List<Newsletter> newsletters;

      String title = request.getParameter("title");

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

   public ActionForward listSubscribers(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) {
      log.debug("Show persons who have newsletter subscription");


      String name = request.getParameter("name");
      String email = request.getParameter("email");

      List<Person> persons;
      if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(email)) {
         persons = subscriptionServices.getAllSubscribers(name, email);
      }
      else {
         persons = subscriptionServices.getAllSubscribers();
      }

      List results = convertPersonsToMap(persons);

      request.setAttribute("results", results);
      return mapping.findForward("listsubscribers");
   }

   public ActionForward showImportPage(ActionMapping mapping, ActionForm form,
                                       HttpServletRequest request, HttpServletResponse response) {
      log.debug("Show import page");
      return mapping.findForward("importpage");

   }

   private List<Map> convertPersonsToMap(List<Person> persons) {
      List<Map> results = new ArrayList<Map>();
      for (int i = 0; i < persons.size(); i++) {
         Map result = new HashMap();
         Person person = persons.get(i);
         result.put("id", person.getId());
         result.put("username", person.getNickname());
         result.put("email", person.getEmail());
         result.put("fullname", person.getLastName() + " " + person.getFirstName());
         results.add(result);
      }
      return results;
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

   private List<Map<String, String>> convertToMap(String name, String email, Set<Subscription> subscriptions) {
      List<Map<String, String>> results = new ArrayList<Map<String, String>>();
      for (Subscription subscription : subscriptions) {
         Person person = CommunityModuleAdapter.getUserById(subscription.getSubscriberId());
         boolean sameName = StringUtils.isBlank(name) || (person.getLastName().equals(name) || person.getFirstName().equals(name));
         boolean sameemail = StringUtils.isBlank(email) || (person.getEmail().equals(email));
         Map result = new HashMap();
         if (sameName && sameemail) {

            result.put("id", person.getId());
            result.put("username", person.getNickname());
            result.put("email", person.getEmail());
            result.put("fullname", person.getLastName() + "" + person.getFirstName());

            results.add(result);
         }
      }
      return results;
   }




}
