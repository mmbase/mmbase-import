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

	private static Log log = LogFactory.getLog(NewsletterPublicationManagementAction.class);

	NewsletterPublicationService publicationService;

	@Override
	protected void onInit() {
		super.onInit();
		publicationService = (NewsletterPublicationService) getWebApplicationContext().getBean("publicationService");
	}

	@Override
	protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.debug("No parameter specified,go to edit page ,show related publications");

		int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
		List<Publication> publications;
		Date now = new Date();
		int pagesize = 10;
		if (StringUtils.isNotEmpty(PropertiesUtil.getProperty("repository.search.results.per.page"))) {
			pagesize = Integer.parseInt(PropertiesUtil.getProperty("repository.search.results.per.page"));
		}
		int offset = 0;
		if (StringUtils.isNotBlank(request.getParameter("offset"))) {
			offset = Integer.parseInt(request.getParameter("offset"));
		}
		int resultCount = publicationService.searchPublicationCountForEdit(newsletterId, "", "", null, now);
		publications = publicationService.searchPublication(newsletterId, "", "", null, now, pagesize, offset * pagesize, "number", "UP");
		List<Map<String, String>> results = convertPublicationsToMap(publications);
		request.setAttribute("results", results);
		request.setAttribute("resultCount", resultCount);
		request.setAttribute("newsletterId", newsletterId);
		request.setAttribute("order", "number");
		request.setAttribute("direction", "1");
		return mapping.findForward("newsletterpublicationlist");
	}

	@SuppressWarnings("deprecation")
	public ActionForward searchPublication(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		log.debug("parameter specified, search newsletterpublication ");

		int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
		int pagesize = 10;
		if (StringUtils.isNotEmpty(PropertiesUtil.getProperty("repository.search.results.per.page"))) {
			pagesize = Integer.parseInt(PropertiesUtil.getProperty("repository.search.results.per.page"));
		}
		int offset = 0;
		if (StringUtils.isNotBlank(request.getParameter("offset"))) {
			offset = Integer.parseInt(request.getParameter("offset"));
		}
		String order = "number";
		if (StringUtils.isNotBlank(request.getParameter("order"))) {
			order = request.getParameter("order");
		}
		String direction = "UP";
		String paramDir = request.getParameter("direction");
		if (StringUtils.isNotBlank(paramDir)) {
			direction = "1".equals(paramDir) ? "UP" : "DOWN";
		}

		NewsletterPublicationManageForm myForm = (NewsletterPublicationManageForm) form;
		String tmpTitle = myForm.getTitle();
		String tmpSubject = myForm.getSubject();
		String tmpPeriod = myForm.getPeriod();

		Calendar calendar = Calendar.getInstance();
		Date endTime = calendar.getTime();
		Date startTime = null;

		switch (Integer.parseInt(tmpPeriod)) {

		case 1:
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			startTime = calendar.getTime();
			break;
		case 7:
			calendar.add(Calendar.DAY_OF_YEAR, -7);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			startTime = calendar.getTime();
			break;
		case 14:
			calendar.add(Calendar.DAY_OF_YEAR, -14);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			startTime = calendar.getTime();
			break;
		case 30:
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			startTime = calendar.getTime();
			break;
		case 365:
			calendar.set(Calendar.YEAR, -1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			startTime = calendar.getTime();
			break;
		default:
			break;
		}
		List<Publication> publications;
		int resultCount = publicationService.searchPublicationCountForEdit(newsletterId, tmpTitle, tmpSubject, startTime, endTime);
		publications = publicationService.searchPublication(newsletterId, tmpTitle, tmpSubject, startTime, endTime, pagesize, offset * pagesize,
				order, direction);
		request.setAttribute("newsletterId", newsletterId);
		request.setAttribute("results", publications);
		request.setAttribute("resultCount", resultCount);
		request.setAttribute("offset", offset);

		request.setAttribute("order", order);
		request.setAttribute("direction", paramDir);
		return mapping.findForward("newsletterpublicationlist");
	}

	private List convertPublicationsToMap(List<Publication> publications) {
		List<Map> results = new ArrayList<Map>();
		for (Publication publication : publications) {
			Map result = new HashMap();
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
