package com.finalist.cmsc.subsite.forms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;
import org.mmbase.storage.search.SortOrder;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.forms.SearchForm;
import com.finalist.cmsc.struts.MMBaseAction;

public class SearchInitAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      SearchForm searchForm = (SearchForm) form;

      if (StringUtil.isEmpty(searchForm.getExpiredate())) {
         searchForm.setExpiredate("0");
      }

      if (StringUtil.isEmpty(searchForm.getPublishdate())) {
         searchForm.setPublishdate("0");
      }

      if (StringUtil.isEmpty(searchForm.getOffset())) {
         searchForm.setOffset("0");
      }

      if (StringUtil.isEmpty(searchForm.getOrder())) {
         searchForm.setOrder("title");
      }

      if (searchForm.getDirection() != SortOrder.ORDER_DESCENDING) {
         searchForm.setDirection(SortOrder.ORDER_ASCENDING);
      }
      List<LabelValueBean> typesList = new ArrayList<LabelValueBean>();

      List<NodeManager> types = ContentElementUtil.getContentTypes(cloud);
      List<String> hiddenTypes = PropertiesUtil.getHiddenTypes();
      for (NodeManager manager : types) {
         String name = manager.getName();
         if (!hiddenTypes.contains(name)) {
            LabelValueBean bean = new LabelValueBean(manager.getGUIName(), name);
            typesList.add(bean);
         }
      }
      addToRequest(request, "typesList", typesList);

      return mapping.findForward("searchoptions");
   }

}
