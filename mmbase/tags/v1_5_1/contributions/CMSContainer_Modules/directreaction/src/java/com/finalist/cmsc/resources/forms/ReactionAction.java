package com.finalist.cmsc.resources.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.struts.action.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.Step;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.services.publish.Publish;

public class ReactionAction extends SearchAction {

   private static final Logger log = Logging.getLoggerInstance(ReactionAction.class.getName());

   public static final String NAME_FIELD = "name";
   public static final String EMAIL_FIELD = "email";
   public static final String TITLE_FIELD = "title";
   public static final String BODY_FIELD = "body";


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      // TODO retrieve articles/newsitems in one query along with the reactions
      // (I don't know how to do it)
      // search for the reactions
      super.execute(mapping, form, request, response, cloud);
      Map<Integer, String> titles = new HashMap<Integer, String>();
      // get the reactions from the request
      NodeList results = (NodeList) request.getAttribute("results");
      // for every reaction search for the contentelement it belongs to
      for (Iterator<Node> iter = results.iterator(); iter.hasNext();) {
         Node node = iter.next();
         String title = getArticleTitles(cloud, node);
         // store the title in a map
         titles.put(node.getNumber(), title);
      }
      log.debug("Found titles: " + titles);
      // put the titles as a map into the request
      request.setAttribute("titles", titles);

      return mapping.getInputForward();
   }


   @Override
   public Cloud getCloud() {
      /* The DirectReactions should use the staging cloud if we are
       *  running in single-war-file mode.
       */
      return getCloudForAnonymousUpdate(ServerUtil.isLive());
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


   @Override
   protected void addConstraints(SearchForm searchForm, NodeManager nodeManager,
         QueryStringComposer queryStringComposer, NodeQuery query) {
      ReactionForm form = (ReactionForm) searchForm;
      addField(nodeManager, queryStringComposer, query, NAME_FIELD, form.getName());
      addField(nodeManager, queryStringComposer, query, EMAIL_FIELD, form.getEmail());
      addField(nodeManager, queryStringComposer, query, TITLE_FIELD, form.getTitle());
      addField(nodeManager, queryStringComposer, query, BODY_FIELD, form.getBody());
   }


   private String getArticleTitles(Cloud cloud, Node parentNode) {
      // this query is hacked from the relatednodestag, which doesn't work with
      // a remotecloud
      NodeQuery query = cloud.createNodeQuery();
      Step step1 = query.addStep(parentNode.getNodeManager());
      query.setAlias(step1, parentNode.getNodeManager().getName() + "0");
      query.addNode(step1, parentNode);

      NodeManager otherManager = cloud.getNodeManager("contentelement");

      RelationStep step2 = query.addRelationStep(otherManager);
      Step step3 = step2.getNext();
      query.setNodeStep(step3);
      query.setNodeStep((Step) query.getSteps().get(2));
      NodeList nodeList = query.getNodeManager().getList(query);
      if (nodeList.size() > 0) {
         Node node = (Node) nodeList.get(0);
         return node.getStringValue("title");
      }
      return "";
   }

}
