package com.finalist.newsletter.forms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.web.struts.DispatchActionSupport;

import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.newsletter.services.NewsletterPublicationService;
import com.finalist.newsletter.services.NewsletterService;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;
import com.finalist.newsletter.services.SubscriptionHibernateService;

public class NewsletterSubscriberSearchAction extends DispatchActionSupport {

	private static Log log = LogFactory.getLog(NewsletterPublicationManagementAction.class);

	NewsletterPublicationService publicationService;
	PersonService personService;
	NewsletterSubscriptionServices subscriptionService;
	AuthenticationService authenticationService;
	NewsletterService newsletterService;
	SubscriptionHibernateService subscriptionHService;

	protected void onInit() {
		super.onInit();
		publicationService = (NewsletterPublicationService) getWebApplicationContext().getBean("publicationService");
		personService = (PersonService) getWebApplicationContext().getBean("personService");
		subscriptionService = (NewsletterSubscriptionServices) getWebApplicationContext().getBean("subscriptionServices");
		authenticationService = (AuthenticationService) getWebApplicationContext().getBean("authenticationService");
		newsletterService = (NewsletterService) getWebApplicationContext().getBean("newsletterServices");
		subscriptionHService = (SubscriptionHibernateService) getWebApplicationContext().getBean("subscriptionHService");

	}

	protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PagingStatusHolder pagingHolder = PagingUtils.getStatusHolder(request);

		log.debug("No parameter specified,go to subscriber page ,show related subscribers");
		int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));

		int resultCount = countsearchSubscribers(newsletterId, "", "", "", "");
		if (resultCount > 0) {
			List results = searchSubscribers(newsletterId, "", "", "", "");
			request.setAttribute("results", results);
		}
		request.setAttribute("resultCount", resultCount);
		request.setAttribute("newsletterId", newsletterId);

		return mapping.findForward("success");
	}

	public ActionForward subScriberSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		PagingUtils.getStatusHolder(request);

		log.debug("parameter action specified, go to the subscribers page, show related subscriber list");
		int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));

		NewsletterSubscriberSearchForm myForm = (NewsletterSubscriberSearchForm) form;
		String tmpTerm = myForm.getTerm();
		String tmpFullName = myForm.getFullname();
		String tmpUserName = myForm.getUsername();
		String tmpEmail = myForm.getEmail();

		int resultCount = countsearchSubscribers(newsletterId, tmpTerm, tmpFullName, tmpUserName, tmpEmail);
		if (resultCount > 0) {
			List results = searchSubscribers(newsletterId, tmpTerm, tmpFullName, tmpUserName, tmpEmail);
			request.setAttribute("results", results);
		}

		request.setAttribute("resultCount", resultCount);
		request.setAttribute("newsletterId", newsletterId);

		return mapping.findForward("success");
	}

	private void AddToMap(List<Map> results, String fullName, String userName, String email, String newsletters, String terms, int authenticationId) {

		Map result = new LinkedHashMap();
		result.put("fullname", fullName);
		result.put("username", userName);
		result.put("email", email);
		result.put("newsletters", newsletters);
		result.put("terms", terms);
		result.put("id", authenticationId);
		results.add(result);

	}

	private List<Map> searchSubscribers(int newsletterId, String terms, String fullName, String userName, String email) {
		List<Map> results = new ArrayList<Map>();

		PagingStatusHolder pagingHolder = PagingUtils.getStatusHolderInSorting("number", "asc");

		Set<Long> authenticationIds = new HashSet<Long>();
		authenticationIds = subscriptionService.getAuthenticationByTerms(newsletterId, terms);
		List<Object[]> qResults = subscriptionHService.getSubscribersRelatedInfo(authenticationIds, fullName, userName, email, true);
		for (Object[] result : qResults) {
			String tmpFullName = result[0].toString() + " " + result[1].toString();
			String tmpEmail = result[2].toString();
			int tmpAuthenticationId = Integer.parseInt(result[3].toString());
			String tmpNewsletters = subscriptionService.getNewsletterNameList(tmpAuthenticationId);
			String tmpTerms = subscriptionService.getTermsNameList(tmpAuthenticationId);
			String tmpUserName = result[4].toString();
			AddToMap(results, tmpFullName, tmpUserName, tmpEmail, tmpNewsletters, tmpTerms, tmpAuthenticationId);
		}
		return results;
	}

	private int countsearchSubscribers(int newsletterId, String terms, String fullName, String userName, String email) {
		int resultCount = 0;
		Set<Long> authenticationIds = new HashSet<Long>();
		authenticationIds = subscriptionService.getAuthenticationByTerms(newsletterId, terms);
		if (authenticationIds.size() > 0) {
			resultCount = subscriptionHService.getSubscribersRelatedInfo(authenticationIds, fullName, userName, email, false).size();
		}
		return resultCount;
	}
}
