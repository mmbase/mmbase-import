/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.navigation.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.*;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class MassModify extends MMBaseAction {

   /** name of submit button in jsp to confirm modification */
   private static final String ACTION_MODIFY = "modify";


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      if (isCancelled(request)) {
         return mapping.findForward(SUCCESS);
      }
      if (isModifyAction(request)) {
         MassModifyForm mform = (MassModifyForm) form;
         int objectnumber = mform.getId();
         int newLayoutNr = mform.getNewLayout();
         if (objectnumber > -1 && newLayoutNr > -1) {
            Node page = cloud.getNode(objectnumber);

            Node newLayout = cloud.getNode(newLayoutNr);
            replaceLayout(page, mform.getRequiredLayout(), newLayout, mform.isLinkPortlets());

            return mapping.findForward(SUCCESS);
         }
      }

      List<LabelValueBean> layoutList = new ArrayList<LabelValueBean>();
      List<Node> layouts = PagesUtil.getLayouts(cloud);
      for (Node layout : layouts) {
         LabelValueBean bean = new LabelValueBean(layout.getStringValue(PagesUtil.TITLE_FIELD), String.valueOf(layout
               .getNumber()));
         layoutList.add(bean);
      }
      addToRequest(request, "layoutList", layoutList);

      // neither modification or cancel, show confirmation page
      return mapping.findForward("massmodify");
   }


   private void replaceLayout(Node page, int requiredLayout, Node newLayout, boolean linkPortlets) {
      boolean replace = true;
      if (requiredLayout > 0) {
         Node layout = PagesUtil.getLayout(page);
         replace = layout.getNumber() == requiredLayout;
      }

      if (replace) {
         PagesUtil.replaceLayout(page, newLayout);
         PagesUtil.removePortlets(page, true);
         if (linkPortlets) {
            PagesUtil.linkPortlets(page, newLayout);
         }
      }
      NodeList pages = NavigationUtil.getChildren(page);
      for (Iterator<Node> iterator = pages.iterator(); iterator.hasNext();) {
         Node child = iterator.next();
         replaceLayout(child, requiredLayout, newLayout, linkPortlets);
      }
   }


   private boolean isModifyAction(HttpServletRequest request) {
      return getParameter(request, ACTION_MODIFY) != null;
   }

}
