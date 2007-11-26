package com.finalist.cmsc.resources.forms;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.storage.search.SortOrder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchInitAction extends Action {

   @Override
   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
         HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
      SearchForm searchForm = (SearchForm) actionForm;

      if (StringUtil.isEmpty(searchForm.getOffset())) {
         searchForm.setOffset("0");
      }

      if (searchForm.getDirection() != SortOrder.ORDER_DESCENDING) {
         searchForm.setDirection(SortOrder.ORDER_ASCENDING);
      }
      return actionMapping.findForward("searchoptions");
   }
}
