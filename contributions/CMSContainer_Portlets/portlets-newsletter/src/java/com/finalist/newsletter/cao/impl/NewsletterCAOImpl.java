package com.finalist.newsletter.cao.impl;

import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Query;

import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.domain.Newsletter;

public class NewsletterCAOImpl implements NewsletterCAO {
   private Cloud cloud;

   public void setCloud(Cloud cloud) {
      this.cloud = cloud;
   }

   public List<Newsletter> getAllNewsletters() {
      Query query = cloud.createQuery();
      query.addStep(cloud.getNodeManager("newsletter"));
      NodeList list = query.getList();

      return list;

   }

   public Newsletter getNewsletterById(int id) {
      Node newsletterNode = cloud.getNode(id);
      Newsletter newsletter = new Newsletter();
      newsletter.setNumber(newsletterNode.getIntValue("number"));
      newsletter.setTitle(newsletterNode.getStringValue("title"));
      return newsletter;

   }


}
