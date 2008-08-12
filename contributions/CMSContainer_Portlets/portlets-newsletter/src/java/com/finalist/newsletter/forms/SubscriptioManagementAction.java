package com.finalist.newsletter.forms;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.services.NewsletterPublicationService;
import com.finalist.newsletter.services.NewsletterService;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;
import com.finalist.newsletter.services.SubscriptionHibernateService;

public class SubscriptioManagementAction extends DispatchActionSupport {

	private static final String FORWARD_DASHBOARD = "newsletterdashboard";
	private static final String FORWARD_OVERVIEW = "newsletteroverview";
	private static final String FORWARD_SUBSCRIPTION = "newsletterSubscriptionDetail";
	private static final String FORWARD_SUBSCRIBEROVERVIEW = "subscriberOverview";
	private static final String FORWARD_SUBSCRIBER_SUBSCRIPTIONS="subscriberSubscriptionDetail";
	private static final String FORWARD_SUBSCRIPTION_IMPORT = "importpage";

	private static final String RESULTS = "results";
	private static final String RESULTCOUNT = "resultCount";

	private static final String PARAM_NAME = "name";
	private static final String PARAM_EMAIL = "email";
	private static final String PARAM_TITLE = "title";
	private static final String PARAM_SUBSCRIBER = "subscriber";
	private static final String PARAM_NEWSLETTERTITLE = "newsletterTitle";
	private static final String PARAM_NEWSLETTERID = "newsletterId";
	private static final String PARAM_SUBSRIBERID = "subsriberId";
	private static final String PARAM_IMPORTTYPE = "importType";
	
	private static Log log = LogFactory.getLog(SubscriptioManagementAction.class);
	
	NewsletterService newsletterService;
	NewsletterSubscriptionServices subscriptionServices;
	NewsletterPublicationService publicationService;
	PersonService personServices;
	SubscriptionHibernateService subscriptionHService;

	@Override
	protected void onInit() {
		super.onInit();
		newsletterService = (NewsletterService) getWebApplicationContext().getBean("newsletterServices");
		subscriptionServices = (NewsletterSubscriptionServices) getWebApplicationContext().getBean("subscriptionServices");
		personServices = (PersonService) getWebApplicationContext().getBean("personService");
		publicationService = (NewsletterPublicationService) getWebApplicationContext().getBean("publicationService");
		subscriptionHService = (SubscriptionHibernateService) getWebApplicationContext().getBean("subscriptionHService");
	}

	@Override
   protected ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                       HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.debug("No parameter specified,go to dashboard");
		PagingUtils.initStatusHolder(request);
		String title = request.getParameter(PARAM_TITLE);
		String subscriber = request.getParameter(PARAM_SUBSCRIBER);

		List<Newsletter> newsletters = newsletterService.getNewsletters(subscriber, title, false);
		List<Map> results = convertToMap(newsletters);
		
		request.setAttribute(RESULTS, results);		
		return mapping.findForward(FORWARD_DASHBOARD);
	}

   public ActionForward newsletterOverview(ActionMapping mapping, ActionForm form, 
		   									HttpServletRequest request, HttpServletResponse response) {
		log.debug("Show newsletterOverview");

		PagingUtils.initStatusHolder(request);
		String title = request.getParameter(PARAM_TITLE);
		String subscriber = request.getParameter(PARAM_SUBSCRIBER);

		int resultCount = newsletterService.getNewsletters(subscriber, title, false).size();
		List<Newsletter> newsletters = newsletterService.getNewsletters(subscriber, title, true);
		List<Map> results = convertToMap(newsletters);
		
		request.setAttribute(RESULTS, results);
		request.setAttribute(RESULTCOUNT, resultCount);		
		return mapping.findForward(FORWARD_OVERVIEW);
	}

   public ActionForward listSubscription(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response) {
	    PagingUtils.initStatusHolder(request);
		String name = request.getParameter(PARAM_NAME);
		String email = request.getParameter(PARAM_EMAIL);
		int newsletterId = Integer.parseInt(request.getParameter(PARAM_NEWSLETTERID));
		
		log.debug(String.format("List all Subscriptions of Newsletter %s", newsletterId));
		int resultCount = countSubscriptionByNewsletter(newsletterId, name, email);
		if (resultCount > 0) {
			List<Map> results = getSubscriptionByNewsletter(newsletterId, name, email);
			request.setAttribute(RESULTS, results);
		}
		request.setAttribute(RESULTCOUNT, resultCount);
		request.setAttribute(PARAM_NEWSLETTERTITLE, newsletterService.getNewsletterName(newsletterId));
		return mapping.findForward(FORWARD_SUBSCRIPTION);
	}

	private List<Map> getSubscriptionByNewsletter(int newsletterId, String name, String email) {
		List<Map> results = new ArrayList<Map>();

		Set<Long> authenticationIds = new HashSet<Long>();
		authenticationIds = subscriptionServices.getAuthenticationIdsByNewsletter(newsletterId);
		List<Object[]> qResults = subscriptionHService.getSubscribersRelatedInfo(authenticationIds, name, "", email, true);
		for (Object[] result : qResults) {
			String tmpFullName = result[0].toString() + " " + result[1].toString();
			String tmpEmail = result[2].toString();
			int tmpAuthenticationId = Integer.parseInt(result[3].toString());
			String tmpUserName = result[4].toString();
			AddToSubscriptionMap(results, tmpFullName, tmpUserName, tmpEmail, tmpAuthenticationId);
		}
		return results;
	}

	private void AddToSubscriptionMap(List<Map> results, String fullName, String userName, String email, int authenticationId) {
		Map result = new LinkedHashMap();
		result.put("fullname", fullName);
		result.put("username", userName);
		result.put("email", email);
		result.put("id", authenticationId);
		results.add(result);
	}

	private int countSubscriptionByNewsletter(int newsletterId, String name, String email) {
		int resultCount = 0;
		Set<Long> authenticationIds = new HashSet<Long>();
		authenticationIds = subscriptionServices.getAuthenticationIdsByNewsletter(newsletterId);
		if (authenticationIds.size() > 0) {
			resultCount = subscriptionHService.getSubscribersRelatedInfo(authenticationIds, name, "", email, false).size();
		}
		return resultCount;
	}

	public ActionForward listSubscriptionByPerson(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		PagingUtils.initStatusHolder(request);
		int subscriberId = Integer.parseInt(request.getParameter(PARAM_SUBSRIBERID));
		String title = request.getParameter(PARAM_TITLE);
		log.debug(String.format("List all Subscriptions of person %s", subscriberId));

		int resultCount = subscriptionServices.getNewslettersBySubscription(subscriberId, title, false).size();
		Set<Newsletter> results = subscriptionServices.getNewslettersBySubscription(subscriberId, title, true);

		request.setAttribute(RESULTS, results);
		request.setAttribute(RESULTCOUNT, resultCount);
		request.setAttribute(PARAM_SUBSRIBERID, subscriberId);
		return mapping.findForward(FORWARD_SUBSCRIBER_SUBSCRIPTIONS);
	}

   public ActionForward listSubscribers(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) {
		log.debug("Show persons who have newsletter subscription");
		PagingUtils.initStatusHolder(request);
		String fullname = request.getParameter(PARAM_NAME);
		String email = request.getParameter(PARAM_EMAIL);
		
		int resultCount = countSubscription(fullname, email);
		if (resultCount > 0) {
			List<Map> results = getSubscription(fullname, email);
			request.setAttribute(RESULTS, results);
		}
		request.setAttribute(RESULTCOUNT, resultCount);
		return mapping.findForward(FORWARD_SUBSCRIBEROVERVIEW);
	}

   private List<Map> getSubscription(String fullname, String email) {
		List<Map> results = new ArrayList<Map>();

		Set<Long> authenticationIds = new HashSet<Long>();
		authenticationIds = subscriptionServices.getAuthenticationIds();
		List<Object[]> qResults = subscriptionHService.getSubscribersRelatedInfo(authenticationIds, fullname, "", email, true);
		for (Object[] result : qResults) {
			String tmpFullName = result[0].toString() + " " + result[1].toString();
			String tmpEmail = result[2].toString();
			int tmpAuthenticationId = Integer.parseInt(result[3].toString());
			String tmpUserName = result[4].toString();
			AddToSubscriptionMap(results, tmpFullName, tmpUserName, tmpEmail, tmpAuthenticationId);
		}
		return results;
	}

	private int countSubscription(String fullname, String email) {
		int resultCount = 0;
		Set<Long> authenticationIds = new HashSet<Long>();
		authenticationIds = subscriptionServices.getAuthenticationIds();
		if (authenticationIds.size() > 0) {
			resultCount = subscriptionHService.getSubscribersRelatedInfo(authenticationIds, fullname, "", email, false).size();
		}
		return resultCount;
	}

	public ActionForward showImportPage(ActionMapping mapping, ActionForm form,
										HttpServletRequest request, HttpServletResponse response) {
		log.debug("Show import page");
		String importType = request.getParameter(PARAM_IMPORTTYPE);
		if (StringUtils.isNotEmpty(importType)) {
			int newsletterId = Integer.parseInt(request.getParameter(PARAM_NEWSLETTERID));
			request.setAttribute(PARAM_IMPORTTYPE, importType);
			request.setAttribute(PARAM_NEWSLETTERID, newsletterId);
		}
		return mapping.findForward(FORWARD_SUBSCRIPTION_IMPORT);
	}

	public ActionForward unsubscribe(ActionMapping mapping, ActionForm form,
									HttpServletRequest request, HttpServletResponse response) {
		String[] newsletterIds = request.getParameterValues("ids");
		for (String id : newsletterIds) {
			subscriptionServices.unSubscribeAllInNewsletter(Integer.decode(id));
		}
		return newsletterOverview(mapping, form, request, response);
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
}
