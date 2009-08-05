package com.finalist.portlets.banner.search;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.*;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.struts.MMBaseAction;

/**
 *
 *
 */
public class SearchBannerAction extends MMBaseAction {

   private static Log log = LogFactory.getLog(SearchBannerAction.class);

   private static final String REPOSITORY_SEARCH_RESULTS_PER_PAGE = "repository.search.results.per.page";
   private static final int MAX_NUMBER_EXPORT = 1024;


   /**
    * Puts the results in a list of maps, so the searching is done inside the
    * action because most of the mm-tags do not work with a remote cloud. The
    * result are put on the request and displayed by the JSP view. The searching
    * might be a bit complicated but I don't have a clue how mmbase works.
    */
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cld) throws Exception {

      List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

      BannerForm bannerForm = (BannerForm) form;
      log.debug("Searching on remote cloud? " + bannerForm.isRemote());
      Cloud cloud = getCloudForAnonymousUpdate(bannerForm.isRemote());

      int count = 0;
      int offset = 0;
      if (bannerForm.getOffset() != null && bannerForm.getOffset().matches("\\d+")) {
         offset = Integer.parseInt(bannerForm.getOffset());
      }
      String resultsPerPage = PropertiesUtil.getProperty(REPOSITORY_SEARCH_RESULTS_PER_PAGE);
      int maxNumber = 50;
      if (resultsPerPage != null && resultsPerPage.matches("\\d+")) {
         maxNumber = Integer.parseInt(resultsPerPage);
      }
      boolean isExport = false;
      if (request.getParameter("exportButton") != null) {
         isExport = true;
      }
      if (isExport) {
         maxNumber = MAX_NUMBER_EXPORT;
      }
      int queryOffset = maxNumber * offset;

      String action = request.getParameter("action");
      String page = request.getParameter("pagepath");
      String position = request.getParameter("position");
      String name = request.getParameter("name");
      Date from = bannerForm.getFromDate();
      Date to = bannerForm.getToDate();

      Query query = createBannerQuery(cloud, page, position, name, from, to, queryOffset, maxNumber);

      NodeList nodes = query.getList();
      count = Queries.count(query);
      rows = populateBannersAndPositions(nodes);

      request.setAttribute("offset", Integer.valueOf(offset));
      request.setAttribute("rows", rows);
      request.setAttribute("resultCount", count);
      request.setAttribute("allPositions", bannerForm.getAllPositions());

      if (isExport) {
         action = "export";
      }
      else if (bannerForm.isRemote()) {
         action = "searchLive";
      }
      else {
         action = "searchStaging";
      }

      return mapping.findForward(action);
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


   private List<Map<String, Object>> populateBannersAndPositions(NodeList nodes) {
      List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
      for (NodeIterator iter = nodes.nodeIterator(); iter.hasNext();) {

         Node node = iter.nextNode();
         Map<String, Object> columns = new HashMap<String, Object>();
         rows.add(columns);
         columns.put("number", node.getStringValue("bannercounter.number"));
         columns.put("page", node.getStringValue("bannercounter.page"));
         columns.put("position", node.getStringValue("bannercounter.position"));
         columns.put("startDate", node.getDateValue("bannercounter.startdate"));
         columns.put("endDate", node.getDateValue("bannercounter.enddate"));
         columns.put("clicks", node.getStringValue("bannercounter.clicks"));

         columns.put("title", node.getStringValue("banner.title"));
         columns.put("maxClicks", node.getStringValue("banner.maxclicks"));
         columns.put("useMaxClicks", node.getStringValue("banner.use_maxclicks"));
         columns.put("expireDate", node.getDateValue("banner.expiredate"));

         columns.put("name", node.getStringValue("customer.name"));
         String type = "img";
         Node banner = node.getCloud().getNode(node.getIntValue("banner.number"));
         if (hasAttachment(banner)) {
            type = "flash";
         }
         columns.put("type", type);
         log.debug("Adding columns: " + columns);
      }
      return rows;
   }


   private NodeQuery createAttachmentsByBannerQuery(Node banner, String direction, int offset, int maxNumber) {
      NodeQuery query = SearchUtil.createRelatedNodeListQuery(banner, "attachments", "posrel", null, null, "title",
            direction);
      SearchUtil.addLimitConstraint(query, offset, maxNumber);
      return query;
   }


   private boolean hasAttachment(Node banner) {
      if (banner == null) {
         return false;
      }
      NodeQuery query = createAttachmentsByBannerQuery(banner, "up", 0, 1);
      NodeList attachments = query.getNodeManager().getList(query);
      return attachments != null && attachments.size() > 0;
   }

   private void addConstraint(Query query, NodeManager manager, Step step, String fieldName, String value) {
      if (StringUtils.isBlank(value)) {
         return;
      }
      Field field = manager.getField(fieldName);
      FieldValueConstraint constraint = query
            .createConstraint(query.createStepField(step, field), 7, "%" + value + "%");
      addConstraint(query, constraint);
      query.setCaseSensitive(constraint, false);
   }


   private void addConstraint(Query query, Constraint constraint) {
      if (query.getConstraint() == null) {
         query.setConstraint(constraint);
      }
      else {
         CompositeConstraint newc = query.createConstraint(query.getConstraint(), 2, constraint);
         query.setConstraint(newc);
      }
   }


   private Query createBannerQuery(Cloud cloud, String page, String position, String name, Date from, Date to,
         int offset, int maxNumber) {
      NodeManager customerManager = cloud.getNodeManager("customer");
      NodeManager bannerManager = cloud.getNodeManager("banner");
      NodeManager counterManager = cloud.getNodeManager("bannercounter");

      Query query = cloud.createQuery();
      Step customerStep = query.addStep(customerManager);
      RelationStep step2 = query.addRelationStep(bannerManager, "posrel", "SOURCE");
      Step bannerStep = step2.getNext();
      RelationStep step4 = query.addRelationStep(counterManager, "posrel", "DESTINATION");
      Step counterStep = step4.getNext();

      query.addField(customerStep, customerManager.getField("name"));
      query.addField(bannerStep, bannerManager.getField("title"));
      query.addField(bannerStep, bannerManager.getField("use_maxclicks"));
      query.addField(bannerStep, bannerManager.getField("maxclicks"));
      query.addField(bannerStep, bannerManager.getField("expiredate"));
      query.addField(counterStep, counterManager.getField("page"));
      query.addField(counterStep, counterManager.getField("position"));
      query.addField(counterStep, counterManager.getField("startdate"));
      query.addField(counterStep, counterManager.getField("enddate"));
      query.addField(counterStep, counterManager.getField("clicks"));

      addConstraint(query, counterManager, counterStep, "page", page);
      addConstraint(query, counterManager, counterStep, "position", position);
      addConstraint(query, customerManager, customerStep, "name", name);

      Field startdate = counterManager.getField("startdate");
      Field enddate = counterManager.getField("enddate");
      Constraint one = createDatetimeConstraint(query, counterStep, startdate, from, to);
      Constraint two = createDatetimeConstraint(query, counterStep, enddate, from, to);
      Constraint orConstraint = SearchUtil.addORConstraint(query, one, two);
      addConstraint(query, orConstraint);
      query.setOffset(offset);
      query.setMaxNumber(maxNumber);
      log.debug("query: " + query.toSql());

      return query;
   }


   private FieldValueBetweenConstraint createDatetimeConstraint(Query query, Step step, Field field, Date from, Date to) {
      FieldValueBetweenConstraint constraint = null;
      StepField stepField = query.createStepField(step, field);
      constraint = query.createConstraint(stepField, from, to);
      return constraint;
   }

}
