package com.finalist.newsletter.forms;

import java.util.Set;

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
	public ActionForward execute(ActionMapping mapping,
			HttpServletRequest request, Cloud cloud) throws Exception {
		// TODO Auto-generated method stub
		int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
		String tmpName = request.getParameter("name");
		int pagesize = 10;
		int offset = 0;

		if(StringUtils.isNotEmpty(PropertiesUtil.getProperty("repository.search.results.per.page"))) {
			pagesize = Integer.parseInt(PropertiesUtil.getProperty("repository.search.results.per.page"));
		}
		if(StringUtils.isNotBlank(request.getParameter("offset"))){
			offset = Integer.parseInt(request.getParameter("offset"));
		}
		NewsletterService newsletterService = (NewsletterService) ApplicationContextFactory.getBean("newsletterServices");
		Set<Term> terms = newsletterService.getNewsletterTermsByName(newsletterId, tmpName, pagesize, offset);
		if(terms != null){
			request.setAttribute("results", terms);
			request.setAttribute("resultCount", terms.size());
		}
		request.setAttribute("newsletterId",newsletterId);
		return mapping.findForward("success");
	}

}
