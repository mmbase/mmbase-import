package com.finalist.cmsc.versioning.forms;

import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.cmsc.services.versioning.VersioningService;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.repository.RepositoryUtil;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.util.SearchUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import java.util.Locale;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class ShowVersionsAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      Locale locale = request.getLocale();

      String nodeNumber = request.getParameter("nodenumber");
      if (!StringUtil.isEmptyOrWhitespace(nodeNumber) && cloud.hasNode(nodeNumber)) {

         Node node = cloud.getNode(nodeNumber);
         int number = node.getNumber();
         NodeManager manager = cloud.getNodeManager(VersioningService.ARCHIVE);
         NodeQuery query = manager.createQuery();
         SearchUtil.addEqualConstraint(query, manager, VersioningService.ORIGINAL_NODE, number);
         SearchUtil.addSortOrder(query, manager, VersioningService.DATE, "UP");
         NodeList archiveNodeList = manager.getList(query);
         request.setAttribute("archiveNodesSize", archiveNodeList.size());
         request.setAttribute("archiveNodes", archiveNodeList);

         UserRole role = RepositoryUtil.getRole(cloud, RepositoryUtil.getCreationChannel(node), false);
         request.setAttribute("isAllowed", SecurityUtil.isWriter(role));
      }
      else {
         String message = getResources(request, "VERSIONING").getMessage(locale, "incorrect.nodenumber", nodeNumber);
         request.setAttribute("error", message);
      }

      return mapping.findForward("success");
   }
}
