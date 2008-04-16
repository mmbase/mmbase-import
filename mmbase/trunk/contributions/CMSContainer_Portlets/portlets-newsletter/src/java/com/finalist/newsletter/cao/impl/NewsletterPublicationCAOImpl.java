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

import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.domain.Publication;


public class NewsletterPublicationCAOImpl implements NewsletterPublicationCAO {


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

   private Publication convert(Node node){
      Publication pub = new Publication();
      pub.setId(node.getNumber());
      pub.setStatus(Publication.STATUS.valueOf(node.getStringValue("status")));
      return pub;
   }

   private List<Publication> convert(List<Node> nodeList){
      List<Publication> pubs = new ArrayList<Publication>();

      for (Node node : nodeList) {
         pubs.add(convert(node));
      }

      return pubs;
   }

   public Publication getPublication(int number) {
      Node newsletterPublicationNode = cloud.getNode(number);
      return convert(newsletterPublicationNode);
   }
}
