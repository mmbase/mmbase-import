/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 */
package com.finalist.cmsc.recyclebin.forms;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.*;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.services.workflow.Workflow;

public class DeleteAction extends MMBaseFormlessAction {

   private static Log log = LogFactory.getLog(DeleteAction.class);

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      if (!RepositoryUtil.hasRecyclebinRights(cloud, "webmaster")) {
         return redirectLogin(request);
      }

      String action = getParameter(request, "action");
      String type = request.getParameter("type");

      if ("deleteall".equals(action)) {
         Node objectNode = null;
         Node trash = RepositoryUtil.getTrashNode(cloud);
         NodeList garbage;
         if ("content".equals(type)) {
            garbage = RepositoryUtil.getLinkedElements(trash);
         } else {
            garbage = RepositoryUtil.getCreatedAssetElements(trash);
         }
         for (Iterator<Node> iter = garbage.iterator(); iter.hasNext();) {
            try {
               objectNode = iter.next();
               if (Workflow.hasWorkflow(objectNode)) {
                  // at this time complete is the same as remove
                  Workflow.complete(objectNode);
               }
               objectNode.delete(true);
            } catch (Exception e) {
               log.warn("Unable to remove from trash " + ((objectNode == null) ? null : objectNode.getNumber()));
            }
         }
      } else {
         String objectnumber = getParameter(request, "objectnumber");
         Node objectNode = cloud.getNode(objectnumber);
         if (Workflow.hasWorkflow(objectNode)) {
            // at this time complete is the same as remove
            Workflow.complete(objectNode);
         }
         objectNode.delete(true);
      }
      addToRequest(request, "fresh", "true");
      if (type.equalsIgnoreCase("content")) {
         return mapping.findForward("content");
      } else {
         return mapping.findForward("asset");
      }
   }

}
