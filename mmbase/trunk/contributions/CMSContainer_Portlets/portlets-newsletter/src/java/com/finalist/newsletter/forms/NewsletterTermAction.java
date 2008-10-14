package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.newsletter.util.NewsletterTermUtil;

public class NewsletterTermAction extends DispatchAction {

   private static final String MESSAGE_KEY = "term.exist";
   private static final String MESSAGE_TERM_MANDATORY = "newsletter.term.mandatory";
   private static final String MESSAGE_TERM_EXIST = "newsletter.term.exist";

   private static final String ACTION_FORWORD_ADD = "add";
   private static final String ACTION_FORWORD_LIST = "list";
   private static final String ACTION_FORWORD_SUCCESS = "success";
   private static final String LIST_OFFSET = "offset";
   private static final String LIST_NEWSLETTER = "newsletter";
   private static final String TERM_NUMBER = "id";

   public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
      NewsletterTermForm termForm = (NewsletterTermForm) form;
      ActionMessages messages = new ActionMessages();
      if (StringUtils.isBlank(termForm.getName())) {
         messages.add(MESSAGE_KEY, new ActionMessage(MESSAGE_TERM_MANDATORY));
         saveMessages(request, messages);
         return mapping.findForward(ACTION_FORWORD_ADD);
      }
      boolean hasTerm = NewsletterTermUtil.hasTerm(termForm.getName());
      if (hasTerm) {
         messages.add(MESSAGE_KEY, new ActionMessage(MESSAGE_TERM_EXIST));
         saveMessages(request, messages);
         return mapping.findForward(ACTION_FORWORD_ADD);
      } else {
         NewsletterTermUtil.addTerm(termForm.getName());
      }
      request.setAttribute("lastAction", "add");
      ActionForward forward = mapping.findForward(ACTION_FORWORD_LIST);
      return forward;
   }

   public ActionForward addInit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
      NewsletterTermForm termForm = (NewsletterTermForm) form;
      termForm.clear();
      return mapping.findForward(ACTION_FORWORD_ADD);
   }

   public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
      String id = request.getParameter(TERM_NUMBER);
      String requestIds = request.getParameter("deleteRequest");
      if (StringUtils.isNotEmpty(requestIds)) {
         String[] ids = requestIds.split(",");
         for (String number : ids) {
            if (StringUtils.isNotEmpty(number)) {
               NewsletterTermUtil.deleteTerm(Integer.parseInt(number));
            }
         }
      } else {
         if (StringUtils.isNotEmpty(id)) {
            NewsletterTermUtil.deleteTerm(Integer.parseInt(id));
         }
      }
      return mapping.findForward(ACTION_FORWORD_LIST);
   }

   public ActionForward modify(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
      String id = request.getParameter(TERM_NUMBER);
      String nameValue = request.getParameter("name");
      if (StringUtils.isBlank(nameValue)) {
         response.getWriter().print("term.mandatory");
         return null;
      }
      boolean hasTerm = NewsletterTermUtil.hasTerm(nameValue);
      if (hasTerm) {
         response.getWriter().print(MESSAGE_KEY);
      } else {
         NewsletterTermUtil.updateTerm(Integer.parseInt(id), nameValue);
         response.getWriter().print("term.modify.success");
      }
      return null;
   }

   public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
      NewsletterTermForm termForm = (NewsletterTermForm) form;
      Object lastAction = request.getAttribute("lastAction");
      if (lastAction != null && lastAction.equals("add")) {
         termForm.clear();
      }
      String init = request.getParameter("init");
      if (StringUtils.isNotEmpty(init) && init.equals("true")) {
         termForm.clear();
      }
      int pageSize = 12;
      int offset = 0;
      if (StringUtils.isNotEmpty(termForm.getOffset())) {
         offset = Integer.parseInt(termForm.getOffset());
      }
      if (StringUtils.isNotEmpty(PropertiesUtil.getProperty("repository.search.results.per.page"))) {
         pageSize = Integer.parseInt(PropertiesUtil.getProperty("repository.search.results.per.page"));
      }

      NodeList resultList = NewsletterTermUtil.searchTerms(termForm.getName(), offset * pageSize, pageSize);
      int totalCount = NewsletterTermUtil.countTotalTerms(termForm.getName());
      if (resultList == null || resultList.size() == 0) {
         if (offset >= 1) {
            resultList = NewsletterTermUtil.searchTerms(termForm.getName(), (offset - 1) * pageSize, pageSize);
            termForm.setOffset(String.valueOf(offset - 1));
         }
      }

      request.setAttribute("resultList", resultList);
      request.setAttribute("resultCount", totalCount);
      request.setAttribute(LIST_OFFSET, termForm.getOffset());

      if (StringUtils.isNotEmpty(request.getParameter(LIST_NEWSLETTER))) {
         request.setAttribute("newsletterId", request.getParameter(LIST_NEWSLETTER));
      }
      return mapping.findForward(ACTION_FORWORD_SUCCESS);
   }
}
