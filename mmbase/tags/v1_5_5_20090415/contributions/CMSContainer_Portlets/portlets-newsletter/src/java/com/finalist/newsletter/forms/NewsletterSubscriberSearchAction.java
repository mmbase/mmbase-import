package com.finalist.newsletter.forms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.web.struts.DispatchActionSupport;

import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.newsletter.services.NewsletterPublicationService;
import com.finalist.newsletter.services.NewsletterService;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;
import com.finalist.newsletter.services.SubscriptionHibernateService;

/**
 * using for searching Newsletter Subscriber
 * 
 * @author Lisa
 * 
 */
public class NewsletterSubscriberSearchAction extends DispatchActionSupport {

   private static Log log = LogFactory.getLog(NewsletterPublicationManagementAction.class);

   private NewsletterPublicationService publicationService;
   private PersonService personService;
   private NewsletterSubscriptionServices subscriptionService;
   private AuthenticationService authenticationService;
   private NewsletterService newsletterService;
   private SubscriptionHibernateService subscriptionHService;

   /**
    * Initialize service object: publicationService , personService, subscriptionService, authenticationService,
    * newsletterService, subscriptionHService
    */
   protected void onInit() {
      super.onInit();
      publicationService = (NewsletterPublicationService) getWebApplicationContext().getBean("publicationService");
      personService = (PersonService) getWebApplicationContext().getBean("personService");
      subscriptionService = (NewsletterSubscriptionServices) getWebApplicationContext().getBean("subscriptionServices");
      authenticationService = (AuthenticationService) getWebApplicationContext().getBean("authenticationService");
      newsletterService = (NewsletterService) getWebApplicationContext().getBean("newsletterServices");
      subscriptionHService = (SubscriptionHibernateService) getWebApplicationContext().getBean("subscriptionHService");

   }

   /**
    * unspecified searching newsletter subscriber with sorting, ordering, paging
    */
   protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {

      log.debug("No parameter specified,go to subscriber page ,show related subscribers");

      PagingUtils.initStatusHolder(request);

      int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
      int resultCount = countsearchSubscribers(newsletterId, "", "", "", "");
      if (resultCount > 0) {
         List<Map<Object,Object>> results = searchSubscribers(newsletterId, "", "", "", "");
         request.setAttribute("results", results);
      }
      request.setAttribute("resultCount", resultCount);
      request.setAttribute("newsletterId", newsletterId);
      String forwardPath = (String) mapping.findForward("newUserLinkBack").getPath() + "?newsletterId=" + newsletterId;
      request.setAttribute("forwardPath", forwardPath);

      return mapping.findForward("success");
   }

   /**
    * specified searching subscriber with form, showing some information of subscriber, and subscribed newsletter
    * names,subscribed terms
    * 
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward showing newsletter subscriber List
    */
   public ActionForward subScriberSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) {

      log.debug("parameter action specified, go to the subscribers page, show related subscriber list");
      PagingUtils.initStatusHolder(request);
      int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
      NewsletterSubscriberSearchForm myForm = (NewsletterSubscriberSearchForm) form;
      String tmpTerm = myForm.getTerm();
      String tmpFullName = myForm.getFullname();
      String tmpUserName = myForm.getUsername();
      String tmpEmail = myForm.getEmail();

      int resultCount = countsearchSubscribers(newsletterId, tmpTerm, tmpFullName, tmpUserName, tmpEmail);
      if (resultCount > 0) {
         List<Map<Object,Object>> results = searchSubscribers(newsletterId, tmpTerm, tmpFullName, tmpUserName, tmpEmail);
         request.setAttribute("results", results);
      }

      request.setAttribute("resultCount", resultCount);
      request.setAttribute("newsletterId", newsletterId);
      String forwardPath = (String) mapping.findForward("newUserLinkBack").getPath() + "?newsletterId=" + newsletterId;
      request.setAttribute("forwardPath", forwardPath);
      return mapping.findForward("success");
   }

   /**
    * convert newsletter subscribers to map
    * 
    * @param results
    *           subscriber List, containing result information
    * @param fullName
    *           subscriber's full name
    * @param userName
    *           sbuscriber's user name
    * @param email
    *           subscriber's email address
    * @param newsletters
    *           newsletter name string subscribed by subscriber
    * @param terms
    *           newsletter's term name string subscribed by subscriber
    * @param authenticationId
    *           subscriber's authenticationId
    */
   private void addToMap(List<Map<Object, Object>> results, String fullName, String userName, String email,
         String newsletters, String terms, int authenticationId) {

      Map<Object, Object> result = new LinkedHashMap<Object, Object>();
      result.put("fullname", fullName);
      result.put("username", userName);
      result.put("email", email);
      result.put("newsletters", newsletters);
      result.put("terms", terms);
      result.put("id", authenticationId);
      results.add(result);

   }

   /**
    * getting subscriber's subscription information
    * 
    * @param newsletterId
    *           Description of Parameter
    * @param terms
    *           Description of Parameter
    * @param fullName
    *           Description of Parameter
    * @param userName
    *           Description of Parameter
    * @param email
    *           Description of Parameter
    * @return newsletter Subscriber search result List
    */
   private List<Map<Object, Object>> searchSubscribers(int newsletterId, String terms, String fullName,
         String userName, String email) {
      
      Set<Long> authenticationIds = new HashSet<Long>();
      List<Map<Object, Object>> results = new ArrayList<Map<Object, Object>>();
      
      authenticationIds = subscriptionService.getAuthenticationIdsByTerms(newsletterId, terms);
      List<Object[]> qResults = subscriptionHService.getSubscribersRelatedInfo(authenticationIds, fullName, userName,
            email, true);
      for (Object[] result : qResults) {
         
         String tmpFullName = result[0].toString(); //Firstname
         if(StringUtils.isNotEmpty(result[1].toString())) { //If infix is not empty, add it
            tmpFullName += " " + result[1].toString();
         }
         tmpFullName += " " + result[2].toString(); //Add Lastname
         String tmpEmail = result[3].toString();
         int tmpAuthenticationId = Integer.parseInt(result[4].toString());
         String tmpNewsletters = subscriptionService.getNewsletterNameList(tmpAuthenticationId);
         String tmpTerms = subscriptionService.getTermsNameList(tmpAuthenticationId);
         String tmpUserName = result[5].toString();
         addToMap(results, tmpFullName, tmpUserName, tmpEmail, tmpNewsletters, tmpTerms, tmpAuthenticationId);
      }
      return results;
   }

   /**
    * counting subscriber
    * 
    * @param newsletterId
    *           subscribed newsletterId
    * @param terms
    *           subscribed newsletter term name list
    * @param fullName
    *           subscriber's full name
    * @param userName
    *           subscriber's user name
    * @param email
    *           subscriber's email
    * @return subscriber totalCount
    */
   private int countsearchSubscribers(int newsletterId, String terms, String fullName, String userName, String email) {
      
      int resultCount = 0;
      Set<Long> authenticationIds = new HashSet<Long>();
      
      authenticationIds = subscriptionService.getAuthenticationIdsByTerms(newsletterId, terms);
      if (authenticationIds.size() > 0) {
         resultCount = subscriptionHService.getSubscribersRelatedInfoCount(authenticationIds, fullName, userName, email, false);
      }
      return resultCount;
   }
   
}