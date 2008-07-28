package com.finalist.cmsc.community.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.services.community.domain.PreferenceVO;
import com.finalist.cmsc.services.community.preferences.PreferenceService;
import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.paging.PagingStatusHolder;

public class PreferenceAction extends DispatchAction {

   private static Log log = LogFactory.getLog(PreferenceAction.class);

   private PreferenceService preferenceService;


   public void setPreferenceService(PreferenceService preferenceService) {
      this.preferenceService = preferenceService;
   }

   public ActionForward add(ActionMapping mapping, ActionForm form,
                            HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      PreferenceForm preferenceForm = (PreferenceForm) form;
      PreferenceVO preference = new PreferenceVO();
      BeanUtils.copyProperties(preference, preferenceForm);
      setNull(preferenceForm);
      preferenceService.createPreference(preference);
      request.setAttribute("isAddSuccess", "true");
      return mapping.findForward("success");
   }

   private void setNull(PreferenceForm preferenceForm) {
      preferenceForm.setKey("");
      preferenceForm.setModule("");
      preferenceForm.setValue("");
      preferenceForm.setUserId("");
      preferenceForm.setId("");
   }

   public ActionForward addInit(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      List<String> userIds = preferenceService.getAllUserIds();
      request.setAttribute("users", userIds);
      return mapping.findForward("init");
   }

   public ActionForward init(ActionMapping mapping, ActionForm form,
                             HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      PreferenceForm preferenceForm = (PreferenceForm) form;
      setNull(preferenceForm);
      return mapping.findForward("success");
   }

   public ActionForward delete(ActionMapping mapping, ActionForm form,
                               HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      String id = request.getParameter("id");
      if (id != null) {
         preferenceService.deletePreference(id);
      }
      return mapping.findForward("list");
   }

   public ActionForward modify(ActionMapping mapping, ActionForm form,
                               HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      PreferenceVO preference = new PreferenceVO();
      preference.setId(request.getParameter("id"));
      preference.setKey(request.getParameter("key"));
      preference.setValue(request.getParameter("value"));
      preferenceService.updatePreference(preference);
      return null;
   }

   public ActionForward list(ActionMapping mapping, ActionForm form,
                             HttpServletRequest request, HttpServletResponse response)
         throws Exception {

      PreferenceForm preferenceForm = (PreferenceForm) form;
      PreferenceVO preference = new PreferenceVO();
      BeanUtils.copyProperties(preference, preferenceForm);

      PagingStatusHolder pagingHolder = PagingUtils.getStatusHolder(request);
      

      int offset = pagingHolder.getOffset();
      int pagesize = pagingHolder.getPageSize();
      /*pagingHolder.getSort();
      pagingHolder.getDir();*/
      
      List<PreferenceVO> preferences = preferenceService.getPreferences(preference, offset,
            pagesize, preferenceForm.getOrder(), preferenceForm.getDirection());
      int totalCount = preferenceService.getTotalCount(preference);
      if (preferences == null || preferences.size() == 0) {
         if (pagingHolder.getPage() >= 1) {
            preferences = preferenceService.getPreferences(preference, pagingHolder.getOffset(),
                  pagingHolder.getPageSize(), preferenceForm.getOrder(), preferenceForm.getDirection());
         }
      }
      request.setAttribute("totalCount", totalCount);
      request.setAttribute("results", preferences);
      request.setAttribute("isList", "true");
      return mapping.findForward("success");
   }
}
