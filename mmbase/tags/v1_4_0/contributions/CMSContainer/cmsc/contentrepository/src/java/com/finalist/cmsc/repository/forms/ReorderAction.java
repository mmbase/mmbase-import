/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class ReorderAction extends MMBaseFormlessAction {

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String action = getParameter(request, "action");

      if (!StringUtil.isEmptyOrWhitespace(action)) {
         if ("reorder".equals(action)) {
            String parent = request.getParameter("parent");
            if (!isCancelled(request)) {
               String ids = request.getParameter("ids");
               ids = reverseIds(ids);
               List<Integer> changeNumbers = RepositoryUtil.reorderContent(cloud, parent, ids);
               if (!changeNumbers.isEmpty()) {
                  Node parentNode = cloud.getNode(parent);
                  List<Node> nodes = new ArrayList<Node>();
                  for (int changeNodeNumber : changeNumbers) {
                     Node contentNode = cloud.getNode(changeNodeNumber);
                     nodes.add(contentNode);
                  }

                  Workflow.create(parentNode, "", nodes);
               }
            }
            String returnurl = request.getParameter("returnurl");
            if (returnurl != null) {
               return new ActionForward(returnurl, true);
            }
            String url = mapping.findForward(SUCCESS).getPath() + "?parentchannel=" + parent + "&direction=down";
            return new ActionForward(url, true);
         }
         if ("reorderpartial".equals(action)) {
            String parent = request.getParameter("parent");
            String direction = request.getParameter("direction");
            String offsetStr = request.getParameter("offset");
            String[] ids = request.getParameterValues("ids[]");
            if (!StringUtil.isEmptyOrWhitespace(direction) && direction.equalsIgnoreCase("down")) {
               ids = reverseIds(ids).split(",");
            }
            List<Integer> changeNumbers = RepositoryUtil
                  .reorderContent(cloud, parent, ids, Integer.parseInt(offsetStr));
            if (!changeNumbers.isEmpty()) {

               Node parentNode = cloud.getNode(parent);

               List<Node> nodes = new ArrayList<Node>();
               for (int changeNodeNumber : changeNumbers) {
                  Node contentNode = cloud.getNode(changeNodeNumber);
                  nodes.add(contentNode);
               }
               Workflow.create(parentNode, "", nodes);
            }
            return null;
         }
      }

      return mapping.findForward("reorder");
   }


   private String reverseIds(String ids) {
      String[] strings = ids.split(",");
      return reverseIds(strings);
   }


   private String reverseIds(String[] strings) {
      StringBuffer result = new StringBuffer();
      for (int count = strings.length - 1; count >= 0; count--) {
         result.append(strings[count]);
         if (count > 0) {
            result.append(",");
         }
      }
      return result.toString();
   }

}
