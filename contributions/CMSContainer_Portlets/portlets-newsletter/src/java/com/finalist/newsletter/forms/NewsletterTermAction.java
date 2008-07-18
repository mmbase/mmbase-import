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


public class NewsletterTermAction  extends DispatchAction{
   
   public ActionForward add(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      NewsletterTermForm termForm = (NewsletterTermForm)form;
      ActionMessages messages = new  ActionMessages();
      if(StringUtils.isBlank(termForm.getName())) {
         messages.add("term.exist",new ActionMessage("newsletter.term.mandatory"));
         saveMessages(request, messages);
         return mapping.findForward("add");
      }
      boolean hasTerm = NewsletterTermUtil.hasTerm(termForm.getName());
      if(hasTerm){
         messages.add("term.exist",new ActionMessage("newsletter.term.exist"));
         saveMessages(request, messages);
         return mapping.findForward("add");
      }
      else {
         NewsletterTermUtil.addTerm(termForm.getName());
         termForm.reset();
      }
      return mapping.findForward("success");
   }
   
   public ActionForward addInit(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      NewsletterTermForm termForm = (NewsletterTermForm)form;
      termForm.reset();
      return mapping.findForward("add");
   }
   
   public ActionForward delete(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      String id = request.getParameter("id");
      String requestIds = request.getParameter("deleteRequest");
      if(StringUtils.isNotEmpty(requestIds)) {
         String[] ids = requestIds.split(",");
         for(String number :ids) {
            if(StringUtils.isNotEmpty(number)) {
               NewsletterTermUtil.deleteTerm(Integer.parseInt(number));
            }
         }
      }
      else {
         if(StringUtils.isNotEmpty(id)) {
            NewsletterTermUtil.deleteTerm(Integer.parseInt(id));
         }
      }
      return mapping.findForward("list");
   }
   
  
   public ActionForward modify(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      String id = request.getParameter("id");
      String nameValue = request.getParameter("name");
      if(StringUtils.isBlank(nameValue)) {
         response.getWriter().print("term.mandatory");
         return null;
      }
      boolean hasTerm = NewsletterTermUtil.hasTerm(nameValue);
      if(hasTerm){
         response.getWriter().print("term.exist");
      }
      else {
         NewsletterTermUtil.updateTerm(Integer.parseInt(id), nameValue);
         response.getWriter().print("term.modify.success");
      }
      return null;
   }
   
   public ActionForward list(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      NewsletterTermForm termForm = (NewsletterTermForm)form;

      int pageSize = 12;
      int offset = 0;
      if(StringUtils.isNotEmpty(termForm.getOffset())) {
         offset = Integer.parseInt(termForm.getOffset());
      }
      if(StringUtils.isNotEmpty(PropertiesUtil.getProperty("repository.search.results.per.page"))) {
         pageSize = Integer.parseInt(PropertiesUtil.getProperty("repository.search.results.per.page"));
      }
      
      NodeList resultList = NewsletterTermUtil.searchTerms(termForm.getName(),  offset*pageSize, pageSize);
      int totalCount = NewsletterTermUtil.countTotalTerms(termForm.getName());
      if(resultList == null || resultList.size() ==0) {
         if(offset >= 1) {
            resultList = NewsletterTermUtil.searchTerms(termForm.getName(), (offset-1)*pageSize, pageSize);
            request.setAttribute("offset", (offset-1));
            termForm.setOffset(String.valueOf(offset-1));
         }
      }
      
      request.setAttribute("resultList", resultList);
      request.setAttribute("resultCount", totalCount);
      request.setAttribute("offset", termForm.getOffset());
      return mapping.findForward("success");
   }
}
