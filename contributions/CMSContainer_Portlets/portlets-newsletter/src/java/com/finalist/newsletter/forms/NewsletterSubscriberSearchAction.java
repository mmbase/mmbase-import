package com.finalist.newsletter.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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


import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.security.AuthenticationService;

import com.finalist.newsletter.services.NewsletterPublicationService;
import com.finalist.newsletter.services.NewsletterService;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;

public class NewsletterSubscriberSearchAction extends DispatchActionSupport{

	private static Log log = LogFactory.getLog(NewsletterPublicationManagementAction.class);

	NewsletterPublicationService publicationService;
	PersonService personService;
	NewsletterSubscriptionServices subscriptionService ;
	AuthenticationService authenticationService;
	NewsletterService newsletterService;
	
	protected void onInit() {
		super.onInit();
		publicationService = (NewsletterPublicationService) getWebApplicationContext().getBean("publicationService");
		personService = (PersonService) getWebApplicationContext().getBean("personService");
		subscriptionService = (NewsletterSubscriptionServices) getWebApplicationContext().getBean("subscriptionServices");
		authenticationService = (AuthenticationService)getWebApplicationContext().getBean("authenticationService");
		newsletterService = (NewsletterService)getWebApplicationContext().getBean("newsletterServices");
		
	}
	
	
	protected ActionForward unspecified(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.debug("No parameter specified,go to subscriber page ,show related subscribers");
		int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
		int pagesize = 10;
		if(StringUtils.isNotEmpty(PropertiesUtil.getProperty("repository.search.results.per.page"))) {
			pagesize = Integer.parseInt(PropertiesUtil.getProperty("repository.search.results.per.page"));
		}
		List results = searchSubscribers(newsletterId, "", "", "", "", pagesize, 0);
		if(results!=null){
			request.setAttribute("results", results);
			request.setAttribute("resultCount", results.size());			
		}
		request.setAttribute("newsletterId",newsletterId);
		return mapping.findForward("success");
	}
	
	public ActionForward subScriberSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		log.debug("parameter action specified, go to the subscribers page, show related subscriber list");
		int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
		int pagesize = 10;
		if(StringUtils.isNotEmpty(PropertiesUtil.getProperty("repository.search.results.per.page"))) {
			pagesize = Integer.parseInt(PropertiesUtil.getProperty("repository.search.results.per.page"));
		}
		newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
		
		NewsletterSubscriberSearchForm myForm = (NewsletterSubscriberSearchForm)form;
		String tmpTerm = myForm.getTerm();
		String tmpFullName = myForm.getTerm();
		String tmpUserName = myForm.getUsername();
		String tmpEmail = myForm.getEmail();
		
		List results = searchSubscribers(newsletterId, tmpTerm, tmpFullName, tmpUserName, tmpEmail, pagesize, 0);
		
		if(results!=null){
			request.setAttribute("results", results);
			request.setAttribute("resultCount", results.size());			
		}
		request.setAttribute("newsletterId",newsletterId);
		return mapping.findForward("success");
	}
	
	private void AddToMap(List<Map<String, String>> results, String fullName,String userName, String email, String newsletters, String terms) {
		
		Map<String, String> result = new HashMap<String, String>();
        result.put("fullname", fullName);
        result.put("username", userName);
        result.put("email", email);
        result.put("newsletters", newsletters);
        result.put("terms", terms);
        results.add(result);
		
	}
	
	private List<Map<String, String>> searchSubscribers(int newsletterId, String term, String fullName, String userName, String email, int pageSize, int offset){
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		Set<Integer> authenticationIdList = new HashSet<Integer>();
		authenticationIdList = subscriptionService.getRecordIdByNewsletterAndName(newsletterId, StringUtils.isNotBlank(term)?term : "");
		for(int authenticationId : authenticationIdList){
			Person tmpPerson = personService.getPersonByAuthenticationId(new Long(authenticationId));
			String tmpFullName = "";
			if(StringUtils.isNotBlank(tmpPerson.getFirstName())){
				tmpFullName += tmpPerson.getFirstName();
			}
			if(StringUtils.isNotBlank(tmpPerson.getLastName())){
				tmpFullName += " " + tmpPerson.getLastName();
			}
			String tmpUserName = authenticationService.getAuthenticationById(new Long(authenticationId)).getUserId();
			String tmpEmail = tmpPerson.getEmail();				
			String tmpNewsletters = subscriptionService.getNewsletterNameList(authenticationId);
			String tmpTerms = subscriptionService.getTermsNameList(authenticationId);
			
			AddToMap(results, tmpFullName, tmpUserName, tmpEmail, tmpNewsletters, tmpTerms);
		}
		return results;
	}
}
