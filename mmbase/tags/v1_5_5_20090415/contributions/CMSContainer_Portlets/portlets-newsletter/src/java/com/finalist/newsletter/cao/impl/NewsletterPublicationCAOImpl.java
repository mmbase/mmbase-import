package com.finalist.newsletter.cao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.Step;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.util.NewsletterPublicationUtil;

public class NewsletterPublicationCAOImpl implements NewsletterPublicationCAO {

   private static Logger log = Logging.getLoggerInstance(NewsletterPublicationCAOImpl.class.getName());

   Cloud cloud;

   public void setCloud(Cloud cloud) {
      this.cloud = cloud;
   }

   public List<Integer> getIntimePublicationIds() {

      NodeQuery query = cloud.createNodeQuery();

      NodeManager pubManager = cloud.getNodeManager("newsletterpublication");
      Step theStep = query.addStep(pubManager);
      query.setNodeStep(theStep);

      Field field = pubManager.getField("status");
      Constraint statusConstraint = SearchUtil.createEqualConstraint(query, field, Publication.STATUS.READY.toString());
      SearchUtil.addConstraint(query, statusConstraint);

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
      return NewsletterPublicationUtil.getPublication(cloud,number);
   }

   public Node getPublicationNode(int number) {
      return cloud.getNode(number);
   }

   public String getPublicationURL(int publicationId) {
      return NewsletterPublicationUtil.getPublicationURL(cloud,publicationId);
   }

   public int getNewsletterId(int publicationId) {
      Node newsletterPublicationNode = cloud.getNode(publicationId);
      NodeList relatedNewsletters = newsletterPublicationNode.getRelatedNodes("newsletter");

      log.debug("Get " + relatedNewsletters.size() + " related newsletter");

      return relatedNewsletters.getNode(0).getNumber();
   }

   public List<Publication> getAllPublications() {
      NodeQuery query = cloud.createNodeQuery();
      Step step = query.addStep(cloud.getNodeManager("newsletterpublication"));
      query.setNodeStep(step);
      NodeList list = query.getList();
      return MMBaseNodeMapper.convertList(list, Publication.class);
   }

   public List<Publication> getPublicationsByNewsletter(int id, Publication.STATUS status) {
      Node newsletterNode = cloud.getNode(id);
      List<Node> publicationNodes = newsletterNode.getRelatedNodes("newsletterpublication");

      Set<Publication> publications = new HashSet<Publication>();

      for (Node publicationNode : publicationNodes) {
         if (null == status || status.toString().equals(publicationNode.getStringValue("status"))) {
            publications.add(convertFromNode(publicationNode));
         }
      }

      log.debug(String.format("Get %s publications of newsletter %s in %s status", publications.size(), id, status));
      return new ArrayList<Publication>(publications);
   }

   private Publication convertFromNode(Node node) {
      return MMBaseNodeMapper.copyNode(node, Publication.class);
   }

   protected String getNewsletterPath(Node newsletterPublicationNode) {
      return NavigationUtil.getPathToRootString(newsletterPublicationNode, true);
   }

   public Set<Term> getTermsByPublication(int publicationId) {
      Node newsletterPublicationNode = cloud.getNode(publicationId);
      NodeList relatedNewsletters = newsletterPublicationNode.getRelatedNodes("newsletter");
      NodeList terms = relatedNewsletters.getNode(0).getRelatedNodes("term");

      Iterator<Node> termsIt = terms.iterator();
      Set<Term> termSet = new HashSet<Term>();
      for (int i = 0; i < terms.size(); i++) {
         Node termNode = termsIt.next();
         Term term = MMBaseNodeMapper.copyNode(termNode, Term.class);
         termSet.add(term);
      }
      return termSet;
   }

   public void renamePublicationTitle(int publicationId) {

      String now = DateFormatUtils.format(new Date(), "dd-MM-yyyy hh:mm");
      Node publicationNode = cloud.getNode(publicationId);
      String oldTitle = publicationNode.getStringValue("title");
      String newTile = oldTitle;
      String dateTime = "";
      if (oldTitle.length() > 18) {
         dateTime = oldTitle.substring(oldTitle.length() - 16);
      }
      if (dateTime.indexOf("-") > 0 && dateTime.indexOf(":") > 0) {
         newTile = oldTitle.substring(0, oldTitle.length() - 18);
      }

      publicationNode.setStringValue("title", newTile + "  " + now);
      publicationNode.commit();

   }

   private List<Publication> convertPublicationsToMap(List<Node> publicationNodes) {
      List<Publication> publications = new ArrayList<Publication>();
      for (Node publicationNode : publicationNodes) {
         publications.add(convertFromNode(publicationNode));
      }
      return publications;
   }

   public List<Publication> getPublicationsByNewsletterAndPeriod(
         int newsletterId, String title, String subject, Date startDate, Date endDate, boolean paging) {

      NodeManager manager = cloud.getNodeManager("newsletterpublication");
      Node newsletterNode = cloud.getNode(newsletterId);

      NodeQuery nodeQuery = SearchUtil.createRelatedNodeListQuery(newsletterNode, "newsletterpublication", "related");

      SearchUtil.addLikeConstraint(nodeQuery, manager.getField("subject"), "%" + subject + "%");
      SearchUtil.addLikeConstraint(nodeQuery, manager.getField("title"), "%" + title + "%");

      if (startDate != null) {
         SearchUtil.addDatetimeConstraint(nodeQuery, manager.getField("creationdate"), startDate.getTime(), endDate.getTime());
      }
      else {
         SearchUtil.addDatetimeConstraint(nodeQuery, manager.getField("creationdate"), Long.decode("1"), endDate.getTime());
      }

      if (paging) {
         PagingUtils.setPagingAndSortingInformation(nodeQuery);
      }

      return convertPublicationsToMap(nodeQuery.getList());
   }

	public List<Publication> getPublications(String title, String subject,
			String description, String intro, boolean paging) {
		PagingStatusHolder pagingHolder = PagingUtils.getStatusHolder();
		NodeManager publicationManager = cloud.getNodeManager("newsletterpublication");
		NodeQuery query = cloud.createNodeQuery();
		Step step = query.addStep(publicationManager);
		query.setNodeStep(step);
		if (StringUtils.isNotBlank(title)) {
			SearchUtil.addLikeConstraint(query, publicationManager.getField("title"), title);
		}
		if (StringUtils.isNotBlank(subject)) {
			SearchUtil.addLikeConstraint(query, publicationManager.getField("subject"), subject);
		}
		if (StringUtils.isNotBlank(description)) {
			SearchUtil.addLikeConstraint(query, publicationManager.getField("description"), description);
		}
		if (StringUtils.isNotBlank(intro)) {
			SearchUtil.addLikeConstraint(query, publicationManager.getField("intro"), intro);
		}
		if (paging) {
			query.setMaxNumber(pagingHolder.getPageSize());
			query.setOffset(pagingHolder.getOffset());
		}
		if (pagingHolder != null) {		
			Queries.addSortOrders(query, pagingHolder.getSort(), pagingHolder.getMMBaseDirection());
		}
		NodeList list = query.getList();
		return MMBaseNodeMapper.convertList(list, Publication.class);
	}

}
