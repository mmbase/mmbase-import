package com.finalist.newsletter.cao.impl;

import java.util.ArrayList;
import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.Step;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.util.POConvertUtils;
import com.finalist.newsletter.NewsletterSendFailException;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.mmbase.PropertiesUtil;


public class NewsletterPublicationCAOImpl implements NewsletterPublicationCAO {

   private static Logger log = Logging.getLoggerInstance(NewsletterPublicationCAOImpl.class.getName());

   Cloud cloud;

   public NewsletterPublicationCAOImpl() {
   }

   public NewsletterPublicationCAOImpl(Cloud cloud) {
      this.cloud = cloud;
   }

   public List<Publication> getIntimePublication() {
      NodeQuery query = cloud.createNodeQuery();
      NodeManager pubManager = cloud.getNodeManager("newsletterpublication");
      Step theStep = query.addStep(pubManager);
      query.setNodeStep(theStep);
      Field field = pubManager.getField("status");
      Constraint titleConstraint = SearchUtil.createEqualConstraint(query, field, Publication.STATUS.READY.toString());
      SearchUtil.addConstraint(query, titleConstraint);

      List<Node> pubNodes = query.getList();
      return convert(pubNodes);
   }

   public void setStatus(Publication publication, Publication.STATUS status) {
      Node publicationNode = cloud.getNode(publication.getId());
      publicationNode.setStringValue("status", status.toString());
      publicationNode.commit();
   }

   private Publication convert(Node node) {
      Publication pub = new Publication();
      pub.setId(node.getNumber());
      pub.setStatus(Publication.STATUS.valueOf(node.getStringValue("status")));
      return pub;
   }

   private List<Publication> convert(List<Node> nodeList) {
      List<Publication> pubs = new ArrayList<Publication>();

      for (Node node : nodeList) {
         pubs.add(convert(node));
      }

      return pubs;
   }

   public Publication getPublication(int number) {
      Node newsletterPublicationNode = cloud.getNode(number);

      String hostUrl = getHostUrl();
      String newsletterPath = getNewsletterPath(newsletterPublicationNode);
      String newsletterUrl = "".concat(hostUrl).concat(newsletterPath);
      
      List<Node> relatedNewsletters = newsletterPublicationNode.getRelatedNodes("newsletter");

      log.debug("Get "+relatedNewsletters.size() +" related newsletter");
      
      Publication pub = new Publication();
      pub.setId(newsletterPublicationNode.getNumber());
      pub.setStatus(Publication.STATUS.valueOf(newsletterPublicationNode.getStringValue("status")));
      pub.setUrl(newsletterUrl);

      Newsletter newsletter = new Newsletter();
      new POConvertUtils<Newsletter>().convert(newsletter,relatedNewsletters.get(0));
      pub.setNewsletter(newsletter);

      return pub;
   }

   protected String getNewsletterPath(Node newsletterPublicationNode) {
      String newsletterPath = NavigationUtil.getPathToRootString(newsletterPublicationNode, true);
      return newsletterPath;
   }

   protected String getHostUrl() {
      String hostUrl = PropertiesUtil.getProperty("host");

      if(StringUtils.isEmpty(hostUrl)){
         throw new NewsletterSendFailException("get property <host> from system property and get nothing");
      }

      log.debug("get property <host> from system property and get:"+hostUrl);

      if (!hostUrl.endsWith("/")) {
         hostUrl += "/";
      }
      return hostUrl;
   }
}
