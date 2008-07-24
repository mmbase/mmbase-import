package com.finalist.newsletter.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.services.NewsletterService;

public class NewsletterTermSearchAction extends MMBaseFormlessAction {

	@Override
	public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
		// TODO Auto-generated method stub
		int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
		String tmpName = request.getParameter("name");
		int pagesize = 10;
		int offset = 0;
		int resultCount = 0;
		if (StringUtils.isNotEmpty(PropertiesUtil.getProperty("repository.search.results.per.page"))) {
			pagesize = Integer.parseInt(PropertiesUtil.getProperty("repository.search.results.per.page"));
		}
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
		NewsletterService newsletterService = (NewsletterService) ApplicationContextFactory.getBean("newsletterServices");
		resultCount = newsletterService.getNewsletterTermsCountByName(newsletterId, tmpName);
		List<Term> terms = newsletterService.getNewsletterTermsByName(newsletterId, tmpName, pagesize, offset * pagesize, order, direction);

		if (terms != null) {
			request.setAttribute("results", terms);
			request.setAttribute("resultCount", resultCount);
			request.setAttribute("offset", offset);

			request.setAttribute("order", order);
			request.setAttribute("direction", paramDir);
		}
		request.setAttribute("newsletterId", newsletterId);
		return mapping.findForward("success");
	}

}
