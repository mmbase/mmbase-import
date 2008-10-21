package com.finalist.cmsc.community.forms;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.services.community.domain.PreferenceVO;
import com.finalist.cmsc.services.community.preferences.PreferenceService;

public class PreferenceAction extends DispatchAction {

   private static final String FORWARD_LIST = "list";
   private static final String FORWARD_SUCCESS = "success";
   private static final String FORWARD_INIT = "init";
   private PreferenceService preferenceService;

   public void setPreferenceService(PreferenceService preferenceService) {
      this.preferenceService = preferenceService;
   }

   public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      PreferenceForm preferenceForm = (PreferenceForm) form;
      PreferenceVO preference = new PreferenceVO();
      BeanUtils.copyProperties(preference, preferenceForm);
      preferenceForm.clear();
      preferenceService.createPreference(preference);
      request.setAttribute("isAddSuccess", "true");
      preference.clean();
      return mapping.findForward(FORWARD_LIST);
   }

   public ActionForward addInit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      PreferenceForm preferenceForm = (PreferenceForm) form;
      preferenceForm.clear();
      List < String > userIds = preferenceService.getAllUserIds();
      request.setAttribute("users", userIds);
      return mapping.findForward(FORWARD_INIT);
   }

   public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      String id = request.getParameter("id");
      if (id != null) {
         preferenceService.deletePreference(id);
      }
      return mapping.findForward(FORWARD_LIST);
   }

   public ActionForward modify(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      PreferenceVO preference = new PreferenceVO();
      preference.setId(request.getParameter("id"));
      preference.setKey(request.getParameter("key"));
      preference.setValue(request.getParameter("value"));
      preferenceService.updatePreference(preference);
      return null;
   }

   public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {

      PreferenceForm preferenceForm = (PreferenceForm) form;
      Object isAdd = request.getAttribute("isAddSuccess");
      if (isAdd != null && isAdd.equals("true")) {
         preferenceForm.clear();
      }
      String reload = request.getParameter("reload");
      if (StringUtils.isNotEmpty(reload) && reload.equals("true")) {
         preferenceForm.clear();
      }
      PreferenceVO preference = new PreferenceVO();
      BeanUtils.copyProperties(preference, preferenceForm);

      PagingUtils.initStatusHolder(request);
      PagingStatusHolder pagingHolder = PagingUtils.getStatusHolder();

      int offset = pagingHolder.getOffset();
      int pagesize = pagingHolder.getPageSize();
      String sortName = pagingHolder.getSort();
      /*
       * pagingHolder.getSort(); pagingHolder.getDir();
       */
      List < PreferenceVO > preferences = new ArrayList < PreferenceVO >();
      if(StringUtils.isEmpty(sortName)){
         preferences = preferenceService.getPreferences(preference, offset, pagesize, preferenceForm
            .getOrder(), preferenceForm.getDirection());
      }else{
         preferences = preferenceService.getPreferences(preference, offset, pagesize, sortName, pagingHolder.getDir());
      }
      int totalCount = preferenceService.getTotalCount(preference);
      if (preferences == null || preferences.size() == 0) {
         if (pagingHolder.getPage() >= 1) {
            preferences = preferenceService.getPreferences(preference, pagingHolder.getOffset(), pagingHolder
                  .getPageSize(), preferenceForm.getOrder(), preferenceForm.getDirection());
         }
      }
      request.setAttribute("totalCount", totalCount);
      request.setAttribute("results", preferences);
      request.setAttribute("isList", "true");
      request.setAttribute("page", pagingHolder.getPage());
      request.setAttribute("forward", mapping.findForward("list").getPath() + "/page=" + pagingHolder.getPage());
      return mapping.findForward(FORWARD_SUCCESS);
   }
}
