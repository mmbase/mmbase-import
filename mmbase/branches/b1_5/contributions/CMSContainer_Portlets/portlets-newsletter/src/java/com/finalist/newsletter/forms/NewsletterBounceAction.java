package com.finalist.newsletter.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.newsletter.domain.NewsletterBounce;
import com.finalist.newsletter.util.NewsletterBounceUtil;

public class NewsletterBounceAction extends DispatchAction {

   public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      int pageSize = 12;
      int offset = 0;
      if (StringUtils.isNotEmpty(PropertiesUtil.getProperty("repository.search.results.per.page"))) {
         pageSize = Integer.parseInt(PropertiesUtil.getProperty("repository.search.results.per.page"));
      }
      String strOffset = request.getParameter("offset");
      String direction = request.getParameter("direction");
      String order = request.getParameter("order");
      if (StringUtils.isNotEmpty(strOffset)) {
         offset = Integer.parseInt(strOffset);
      }
      List<NewsletterBounce> bounces = NewsletterBounceUtil.getBounceRecord(offset * pageSize, pageSize, order,
            direction);
      int count = NewsletterBounceUtil.getTotalCount();
      request.setAttribute("resultList", bounces);
      request.setAttribute("resultCount", count);
      request.setAttribute("offset", offset);
      request.setAttribute("direction", direction);
      request.setAttribute("order", order);
      return mapping.findForward("success");
   }

   public ActionForward getItem(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      String number = request.getParameter("objectnumber");
      NewsletterBounce bounce = NewsletterBounceUtil.getNewsletterBounce(Integer.parseInt(number));
      request.setAttribute("bounce", bounce);
      return mapping.findForward("info");
   }
}
