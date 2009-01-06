package com.finalist.cmsc.resources.forms;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AttachmentInitAction extends SearchInitAction {

   private static final String CREATION = "creation";
   private static final String CHANNELID = "channelid";
   
   @Override
   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
         HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
      AttachmentForm searchForm = (AttachmentForm) actionForm;

      String channelid = (String) httpServletRequest.getSession().getAttribute(CREATION);
      if (StringUtils.isEmpty(searchForm.getOrder())) {
         searchForm.setOrder("title");
      }
      httpServletRequest.setAttribute(CHANNELID, channelid);
      return super.execute(actionMapping, actionForm, httpServletRequest, httpServletResponse);
   }
}
