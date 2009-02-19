package com.finalist.cmsc.resources.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.storage.search.SortOrder;

public class SearchInitAction extends Action {

   private static final String STRICT = "strict";
   
   @Override
   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
         HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
      SearchForm searchForm = (SearchForm) actionForm;

      if (StringUtils.isEmpty(searchForm.getOffset())) {
         searchForm.setOffset("0");
      }

      if (searchForm.getDirection() != SortOrder.ORDER_DESCENDING) {
         searchForm.setDirection(SortOrder.ORDER_ASCENDING);
      }
      httpServletRequest.setAttribute(STRICT, searchForm.getStrict());
      return actionMapping.findForward("searchoptions");
   }
}
