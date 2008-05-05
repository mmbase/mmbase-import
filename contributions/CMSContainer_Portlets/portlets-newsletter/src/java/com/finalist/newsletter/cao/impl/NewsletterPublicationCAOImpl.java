package com.finalist.newsletter.cao.impl;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.newsletter.NewsletterSendFailException;
import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.util.POConvertUtils;
import com.finalist.portlets.newsletter.NewsletterContentPortlet;
import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.Step;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


public class NewsletterPublicationCAOImpl implements NewsletterPublicationCAO {

   private static Logger log = Logging.getLoggerInstance(NewsletterPublicationCAOImpl.class.getName());

   Cloud cloud;

   public NewsletterPublicationCAOImpl() {
   }

   public NewsletterPublicationCAOImpl(Cloud cloud) {
      this.cloud = cloud;
   }

   public List<Integer> getIntimePublicationIds() {

      NodeQuery query = cloud.createNodeQuery();

      NodeManager pubManager = cloud.getNodeManager("newsletterpublication");
      Step theStep = query.addStep(pubManager);
      query.setNodeStep(theStep);

      Field field = pubManager.getField("status");
      Constraint titleConstraint = SearchUtil.createEqualConstraint(query, field, Publication.STATUS.READY.toString());
      SearchUtil.addConstraint(query, titleConstraint);

      List<Node> pubNodes = query.getList();

      List<Integer> inTimePUblicationIds = new ArrayList<Integer>();
      for (Node node : pubNodes) {
         inTimePUblicationIds.add(node.getNumber());
      }

      return inTimePUblicationIds;
   }

   public void setStatus(int publicationId, Publication.STATUS status) {
      Node publicationNode = cloud.getNode(publicationId);
      publicationNode.setStringValue("status", status.toString());
      publicationNode.commit();
   }

   public Publication getPublication(int number) {
      Node newsletterPublicationNode = cloud.getNode(number);

      List<Node> relatedNewsletters = newsletterPublicationNode.getRelatedNodes("newsletter");
      log.debug("Get " + relatedNewsletters.size() + " related newsletter");

      Publication pub = new Publication();
      pub.setId(newsletterPublicationNode.getNumber());
      pub.setStatus(Publication.STATUS.valueOf(newsletterPublicationNode.getStringValue("status")));
      pub.setUrl(getPublicationURL(number));
      Newsletter newsletter = new Newsletter();

      new POConvertUtils<Newsletter>().convert(newsletter, relatedNewsletters.get(0));
      pub.setNewsletter(newsletter);

      return pub;
   }

   public String getPublicationURL(int publciationId) {

      Node publicationNode = cloud.getNode(publciationId);
      String hostUrl = getHostUrl();
      String newsletterPath = getNewsletterPath(publicationNode);


      return "".concat(hostUrl).concat(newsletterPath);
   }

   public int getNewsletterId(int publicationId) {
      Node newsletterPublicationNode = cloud.getNode(publicationId);
      List<Node> relatedNewsletters = newsletterPublicationNode.getRelatedNodes("newsletter");

      log.debug("Get " + relatedNewsletters.size() + " related newsletter");

      return relatedNewsletters.get(0).getNumber();
   }

   public List<Publication> getAllPublications() {
      NodeQuery query = cloud.createNodeQuery();
      Step step = query.addStep(cloud.getNodeManager("newsletterpublication"));
      query.setNodeStep(step);
      NodeList list = query.getList();
      return list;
   }

   public List<Publication> getPublicationsByNewsletter(int id, Publication.STATUS status) {
       Node newsletterNode = cloud.getNode(id);
       List<Node> publicationNodes = newsletterNode.getRelatedNodes("newsletterpublication");

       Set<Publication> publications = new HashSet<Publication>();

       for(Node publicationNode:publicationNodes){
          if(null==status||status.toString().equals(publicationNode.getStringValue("status"))){
             publications.add(convertFromNode(publicationNode));
          }
       }

      log.debug(String.format("Get %s publications of newsletter %s in %s status",publications.size(),id,status));
      return new ArrayList(publications);
    }

   private Publication convertFromNode(Node node){
      Publication publication = new Publication();
      publication.setId(node.getNumber());
      return publication;
   }


   protected String getNewsletterPath(Node newsletterPublicationNode) {
      return NavigationUtil.getPathToRootString(newsletterPublicationNode, true);
   }

   protected String getHostUrl() {
      String hostUrl = PropertiesUtil.getProperty("host");

      if (StringUtils.isEmpty(hostUrl)) {
         throw new NewsletterSendFailException("get property <host> from system property and get nothing");
      }

      log.debug("get property <host> from system property and get:" + hostUrl);

      if (!hostUrl.endsWith("/")) {
         hostUrl += "/";
      }
      return hostUrl;
   }
}
