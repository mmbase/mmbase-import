package com.finalist.cmsc.pagewizard.forms;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class CreateContentAction extends MMBaseAction {

   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      CreateContentForm createForm = (CreateContentForm) form;
      String contentType = createForm.getContentType();

      if (contentType == null || contentType.length() == 0 || contentType.equals("contentelement")) {

         List<LabelValueBean> typesList = new ArrayList<LabelValueBean>();
         List<NodeManager> types = ContentElementUtil.getContentTypes(cloud);
         for (NodeManager manager : types) {
            LabelValueBean bean = new LabelValueBean(manager.getGUIName(), manager.getName());
            typesList.add(bean);
         }
         addToRequest(request, "typesList", typesList);

         return mapping.findForward("picktype");
      }
      else {
         ActionForward forward = mapping.findForward(SUCCESS);
         String returnUrl = createForm.getReturnUrl();
         if (returnUrl == null || returnUrl.length() == 0) {
            returnUrl = "pagewizard/createdcontent.jsp";
         }
         forward = new ActionForward(forward.getPath() + "?action=create" + "&creation=" + createForm.getCreation()
               + "&returnurl=" + URLEncoder.encode(returnUrl, "UTF-8") + "&contenttype=" + createForm.getContentType());

         return forward;
      }
   }

}
