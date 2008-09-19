package com.finalist.newsletter.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.web.struts.DispatchActionSupport;

import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.services.NewsletterPublicationService;

public class NewsletterPublicationAction extends DispatchActionSupport {
	private static Log log = LogFactory
			.getLog(NewsletterPublicationManagementAction.class);

	final String FORWARD_SUCCESS = "success";
	NewsletterPublicationService publicationService;
	
	protected void onInit() {
		super.onInit();
		publicationService = (NewsletterPublicationService) getWebApplicationContext().getBean("publicationService");
	}

	protected ActionForward unspecified(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.debug("No parameter specified, go to newsletterPublicationSearch.jsp showing search results");

		PagingUtils.initStatusHolder(request);
		List<Publication> publications;
		int resultCount = publicationService.searchPublication("", "", "", "", false).size();
		publications = publicationService.searchPublication("", "", "", "", true);
		if (publications.size() > 0) {
			request.setAttribute("results", publications);
		}
		request.setAttribute("resultCount", resultCount);
		return mapping.findForward(FORWARD_SUCCESS);
	}

	public ActionForward searchNewsletterPublication(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("parameter specified, search newsletter publication ");

		PagingUtils.initStatusHolder(request);
		NewsletterPublicationForm myForm = (NewsletterPublicationForm) form;
		String title = myForm.getTitle();
		String subject = myForm.getSubject();
		String description = myForm.getDescription();
		String intro = myForm.getIntro();
		
		List<Publication> publications;
		int resultCount = publicationService.searchPublication(title, subject, description, intro, false).size();
		publications = publicationService.searchPublication(title, subject, description, intro, true);
		if (publications.size() > 0) {
			request.setAttribute("results", publications);
		}
		request.setAttribute("resultCount", resultCount);
		return mapping.findForward(FORWARD_SUCCESS);
	}
	
}
