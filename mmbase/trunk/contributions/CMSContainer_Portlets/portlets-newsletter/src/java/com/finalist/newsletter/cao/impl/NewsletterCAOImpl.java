package com.finalist.newsletter.cao.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Query;

import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Tag;

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
	  CloudProvider provider = CloudProviderFactory.getCloudProvider();
	  cloud = provider.getCloud();
      Node newsletterNode = cloud.getNode(id);
      Newsletter newsletter = new Newsletter();
      newsletter.setNumber(newsletterNode.getIntValue("number"));
      newsletter.setTitle(newsletterNode.getStringValue("title"));
      List<Node> tagList = newsletterNode.getRelatedNodes("tag");
      Iterator tagIt = tagList.iterator();
      for(int i=0;i<tagList.size();i++){
    	  Tag tag = new Tag();
    	  Node tagNode = (Node) tagIt.next();
    	  tag.setId(tagNode.getNumber());
    	  tag.setName(tagNode.getStringValue("name"));
    	  tag.setSubscription(false);
    	  newsletter.getTags().add(tag);
      }
      return newsletter;
   }


}
