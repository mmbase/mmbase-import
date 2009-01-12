package com.finalist.portlets.banner.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.remotepublishing.CloudManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;

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
      Cloud cloud = getCloud(bannerForm.isRemote());

      NodeManager manager = cloud.getNodeManager("customer");
      NodeQuery query = manager.createQuery();
      SearchUtil.addSortOrder(query, manager, "name", "up");
      log.debug("Query: " + query.toSql());
      NodeList nodes = query.getList();
      List<Map> customers = populateCustomersRows(nodes);

      request.setAttribute("customers", customers);

      return mapping.findForward("success");
   }


   @Override
   public String getRequiredRankStr() {
      return null;
   }


   private Cloud getCloud(boolean isRemote) {
      CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = cloudProvider.getCloud();
      log.debug("Using remote cloud?: " + isRemote);
      if (isRemote) {
         return CloudManager.getCloud(cloud, "live.server");
      }
      return cloud;
   }


   private List<Map> populateCustomersRows(NodeList nodes) {
      List<Map> rows = new ArrayList<Map>();
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
