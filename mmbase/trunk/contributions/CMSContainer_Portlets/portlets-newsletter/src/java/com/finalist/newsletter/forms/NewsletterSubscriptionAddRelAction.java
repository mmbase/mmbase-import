package com.finalist.newsletter.forms;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.web.struts.DispatchActionSupport;

import com.finalist.newsletter.services.NewsletterSubscriptionServices;

public class NewsletterSubscriptionAddRelAction extends DispatchActionSupport {

	NewsletterSubscriptionServices subscriptionServices;

	protected void onInit() {
		super.onInit();
		subscriptionServices = (NewsletterSubscriptionServices) getWebApplicationContext().getBean("subscriptionServices");
	}

	protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.debug("No parameter specified,go to dashboard");
		if (StringUtils.isNotBlank(request.getParameter("newsletterId")) && StringUtils.isNotBlank(request.getParameter("authid"))) {
			int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
			int authId = Integer.parseInt(request.getParameter("authid"));
			subscriptionServices.addNewRecord(authId, newsletterId);
		}
		request.setAttribute("newsletterId", request.getParameter("newsletterId"));
		return mapping.findForward("success");
	}

	public ActionForward subscribeNewsletters(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.debug("With parameter subscribeNewsletters,go to search page");
		if (StringUtils.isNotBlank(request.getParameter("newsletterId"))) {
			int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
			String[] authIds = request.getParameterValues("chk_");
			for (String authId : authIds) {
				if (subscriptionServices.noSubscriptionRecord(Integer.parseInt(authId), newsletterId)) {
					subscriptionServices.addNewRecord(Integer.parseInt(authId), newsletterId);
				}
			}
		}
		ActionForward ret = new ActionForward(mapping.findForward("success").getPath() + "?newsletterId=" + request.getParameter("newsletterId"));
		ret.setRedirect(true);
		return ret;
	}
}
