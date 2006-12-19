package com.finalist.cmsc.versioning.forms;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import javax.servlet.http.HttpServletRequest;

import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.services.versioning.Versioning;
import com.finalist.cmsc.services.versioning.VersioningException;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.util.bundles.InheritedBundleManager;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.repository.RepositoryUtil;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.HashMap;

import net.sf.mmapps.commons.util.StringUtil;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class RestoreAction  extends MMBaseFormlessAction {

   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      Locale locale = request.getLocale();
      MessageResources resources = getResources(request, "VERSIONING");

      String nodeNumber = request.getParameter("archivenumber");
      if (cloud.hasNode(nodeNumber)) {
         Node archiveNode = cloud.getNode(nodeNumber);

         Node orginalNode = cloud.getNode(archiveNode.getIntValue("original_node"));
         UserRole role = RepositoryUtil.getRole(cloud, RepositoryUtil.getCreationChannel(orginalNode), false);
         boolean isWriter = SecurityUtil.isWriter(role);
         request.setAttribute("isAllowed", isWriter);
         if (isWriter) {
            try {
               Node restoredNode = Versioning.restoreVersion(archiveNode);

               if (restoredNode != null && !Workflow.hasWorkflow(restoredNode)) {
                  Workflow.create(restoredNode, resources.getMessage(locale, "workflow.remark"));
               }

               request.setAttribute("message", resources.getMessage(locale, "versioning.succesfull.restored"));
            }
            catch (VersioningException e) {
               request.setAttribute("error", resources.getMessage(locale, "versioning.failure"));
            }
         }
      }
      else {
         request.setAttribute("error", resources.getMessage(locale, "incorrect.nodenumber", nodeNumber));
      }

      return mapping.findForward("success");
   }
}
