package com.finalist.newsletter.cao.impl;

import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.domain.Publication;

import java.util.List;
import java.util.Date;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;


public class NewsletterPublicationCAOImpl implements NewsletterPublicationCAO {


   Cloud cloud;

   public NewsletterPublicationCAOImpl() {
   }

   public NewsletterPublicationCAOImpl(Cloud cloud) {
      this.cloud = cloud;
   }

   public List<Publication> getIntimePublication(Date date) {
      return null;
   }

   public void setStatus(Publication publication, Publication.STATUS status) {
      Node publicationNode = cloud.getNode(publication.getId());
      publicationNode.setStringValue("status", status.toString());
      publicationNode.commit();
   }
}
