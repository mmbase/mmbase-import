/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 */
package com.finalist.cmsc.workflow.forms;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.cmsc.util.HttpUtil;
import com.finalist.cmsc.util.bundles.JstlUtil;
import com.finalist.cmsc.workflow.WorkflowAjaxRenderer;
import com.finalist.cmsc.workflow.WorkflowTreeModel;
import com.finalist.cmsc.workflow.WorkflowUtil;
import com.finalist.tree.TreeInfo;
import com.finalist.tree.ajax.AjaxTree;

public class NavigatorAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      JstlUtil.setResourceBundle(request, "cmsc-workflow");

      TreeInfo info = getTreeInfo(cloud);
      String action = request.getParameter("action");
      if (StringUtils.isNotEmpty(action)) {
         response.setContentType("text/xml");
         if ("expand".equals(action)) {
            String persistentid = request.getParameter("persistentid");
            info.expand(persistentid);
         }
         if ("collapse".equals(action)) {
            String persistentid = request.getParameter("persistentid");
            info.collapse(persistentid);
         }
         if ("inittree".equals(action)) {
            PrintWriter out = HttpUtil.getWriterForXml(response);
            String persistentid = request.getParameter("persistentid");
            AjaxTree t = getTree(request, response, cloud, info, persistentid);
            t.render(out);
         }
         if ("loadchildren".equals(action)) {
            PrintWriter out = HttpUtil.getWriterForXml(response);
            String persistentid = request.getParameter("persistentid");
            AjaxTree t = getTree(request, response, cloud, info, persistentid);
            t.renderChildren(out, persistentid);
         }
      } else {
         ActionForward ret = mapping.findForward(SUCCESS);
         return ret;
      }
      return null;
   }

   protected TreeInfo getTreeInfo(Cloud cloud) {
      return WorkflowUtil.getWorkflowInfo(cloud);
   }

   protected AjaxTree getTree(HttpServletRequest request, HttpServletResponse response, Cloud cloud, TreeInfo info,
         String persistentid) {
      WorkflowTreeModel model = new WorkflowTreeModel();
      WorkflowAjaxRenderer chr = new WorkflowAjaxRenderer(request, response, null);
      AjaxTree t = new AjaxTree(model, chr, info);
      t.setImgBaseUrl("../gfx/tree/");
      return t;
   }
}
