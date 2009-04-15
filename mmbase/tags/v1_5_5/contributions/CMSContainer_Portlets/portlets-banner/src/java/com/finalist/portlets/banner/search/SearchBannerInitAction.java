package com.finalist.portlets.banner.search;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.struts.MMBaseAction;

/**
 *
 *
 */
public class SearchBannerInitAction extends MMBaseAction {

   private static Log log = LogFactory.getLog(SearchBannerInitAction.class);


   /**
    * Puts the results in a list of maps, so the searching is done inside the
    * action because most of the mm-tags do not work with a remote cloud. The
    * result are put on the request and displayed by the JSP view. The searching
    * might be a bit complicated but I don't have a clue how mmbase works.
    */
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cld) throws Exception {

      BannerForm bannerForm = (BannerForm) form;
      Cloud cloud = getCloudForAnonymousUpdate(bannerForm.isRemote());

      NodeManager manager = cloud.getNodeManager("customer");
      NodeQuery query = manager.createQuery();
      SearchUtil.addSortOrder(query, manager, "name", "up");
      log.debug("Query: " + query.toSql());
      NodeList nodes = query.getList();
      List<Map<String, Object>> customers = populateCustomersRows(nodes);

      request.setAttribute("customers", customers);

      return mapping.findForward("success");
   }

   public Cloud getCloudForAnonymousUpdate(boolean isRemote) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      if (isRemote) {
         return Publish.getRemoteCloud(cloud);
      }
      return cloud;
   }

   @Override
   public String getRequiredRankStr() {
      return null;
   }

   private List<Map<String, Object>> populateCustomersRows(NodeList nodes) {
      List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
      for (NodeIterator iter = nodes.nodeIterator(); iter.hasNext();) {
         Node node = iter.nextNode();
         Map<String, Object> columns = new HashMap<String, Object>();
         rows.add(columns);
         columns.put("number", node.getStringValue("number"));
         columns.put("name", node.getStringValue("name"));
         log.debug("Adding columns: " + columns);
      }
      return rows;
   }

}
