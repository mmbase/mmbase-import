package com.finalist.newsletter.module;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.finalist.newsletter.module.bean.GlobalOverviewBean;
import com.finalist.newsletter.util.BeanUtil;

public class InitAction extends Action {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      ActionForward ret;
      GlobalOverviewBean bean = BeanUtil.createGlobalOverviewBean();
      if (bean != null) {
         request.setAttribute("globalOverviewBean", bean);
         ret = mapping.findForward("success");
      } else {
         ret = mapping.findForward("error");
      }
      return (ret);
   }
}
