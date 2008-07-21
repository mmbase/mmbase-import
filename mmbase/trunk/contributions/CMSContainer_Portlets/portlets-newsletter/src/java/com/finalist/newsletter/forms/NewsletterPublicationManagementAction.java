package com.finalist.newsletter.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.*;
import org.springframework.web.struts.DispatchActionSupport;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.services.NewsletterPublicationService;

public class NewsletterPublicationManagementAction extends DispatchActionSupport {

	private static Log log = LogFactory
			.getLog(NewsletterPublicationManagementAction.class);

	NewsletterPublicationService publicationService;

	@Override
   protected void onInit() {
		super.onInit();
		publicationService = (NewsletterPublicationService) getWebApplicationContext().getBean("publicationService");
	}

	@Override
   protected ActionForward unspecified(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.debug("No parameter specified,go to edit page ,show related publications");

		int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
		Set<Publication> publications;

		Date now = new Date();
		int pagesize = 10;
		if(StringUtils.isNotEmpty(PropertiesUtil.getProperty("repository.search.results.per.page"))) {
			pagesize = Integer.parseInt(PropertiesUtil.getProperty("repository.search.results.per.page"));
		}
		int offset = 0;
		if(StringUtils.isNotBlank(request.getParameter("offset"))){
			offset = Integer.parseInt(request.getParameter("offset"));
		}
		String forwardType = "";
		if(StringUtils.isNotBlank(request.getParameter("searchForwardType"))) {
         forwardType =request.getParameter("searchForwardType");
      }
		if(forwardType.equals("statistics")){
			request.setAttribute("newsletterId", newsletterId);
			ActionForward ret= new ActionForward(mapping.findForward("statisticmanagement").getPath() + "?newsletterId=" + newsletterId);
			return ret;
		}
		int resultCount = publicationService.searchPublicationCountForEdit(newsletterId, "", "", null, now);
		publications = publicationService.searchPublication(newsletterId, "", "", null, now, pagesize, offset*pagesize);
		List<Map<String, String>> results = convertPublicationsToMap(publications);
		request.setAttribute("results", results);
		request.setAttribute("resultCount",resultCount);
		request.setAttribute("newsletterId", newsletterId);
		return mapping.findForward("newsletterpublicationlist");
	}

	@SuppressWarnings("deprecation")
	public ActionForward searchPublication(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		log.debug("parameter specified, search newsletterpublication ");

		int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
		int pagesize = 10;
		if(StringUtils.isNotEmpty(PropertiesUtil.getProperty("repository.search.results.per.page"))) {
			pagesize = Integer.parseInt(PropertiesUtil.getProperty("repository.search.results.per.page"));
		}
		int offset = Integer.parseInt(request.getParameter("offset"));
		NewsletterPublicationManageForm myForm = (NewsletterPublicationManageForm)form;
		String tmpTitle = myForm.getTitle();
		String tmpSubject = myForm.getSubject();
		String tmpPeriod = myForm.getPeriod();

		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		Date tmpDate = null;

		switch(Integer.parseInt(tmpPeriod)){
		case 1:
			calendar.add(Calendar.DAY_OF_YEAR,-1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			tmpDate = calendar.getTime();
			break;
		case 7:
			calendar.add(Calendar.DAY_OF_YEAR,-7);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			tmpDate = calendar.getTime();
			break;
		case 14:
			calendar.add(Calendar.DAY_OF_YEAR,-14);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			tmpDate = calendar.getTime();
			break;
		case 30:
			calendar.add(Calendar.MONTH,-1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			tmpDate = calendar.getTime();
			break;
		case 365:
			calendar.set(Calendar.YEAR, -1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			tmpDate = calendar.getTime();
			break;
		default: break;
		}
		Set<Publication> publications;
		int resultCount = publicationService.searchPublicationCountForEdit(newsletterId, tmpTitle, tmpSubject, tmpDate, now);
		publications = publicationService.searchPublication(newsletterId, tmpTitle, tmpSubject, tmpDate, now, pagesize, offset*pagesize);
		request.setAttribute("results", publications);
		request.setAttribute("resultCount",resultCount);
		request.setAttribute("offset", offset);
		request.setAttribute("newsletterId", newsletterId);
		return mapping.findForward("newsletterpublicationlist");
	}

	public ActionForward termList(ActionMapping mapping, ActionForm form,	HttpServletRequest request, HttpServletResponse response){
		if(StringUtils.isNotBlank(request.getParameter("newsletterId"))){
			request.setAttribute("newsletterId", request.getParameter("newsletterId"));
		}
		return mapping.findForward("termlist");
	}

	private List convertPublicationsToMap(Set<Publication> publications) {

		List<Map> results = new ArrayList<Map>();
		for (Publication publication1 : publications) {
			Map result = new HashMap();
			Publication publication = publication1;
	         result.put("id", publication.getId());
	         result.put("title", publication.getTitle());
	         result.put("subject", publication.getSubject());
	         result.put("lastmodifier", publication.getLastmodifier());
	         result.put("lastmodifieddate", publication.getLastmodifieddate());
	         results.add(result);
		}
		return results;
	}

}
