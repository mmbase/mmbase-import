package com.finalist.newsletter.util;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.RelationList;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.util.ServerUtil;

/**
 * term util class.
 *
 * @author kevin
 */
public abstract class NewsletterTermUtil {

   private static Log log = LogFactory.getLog(NewsletterTermUtil.class);

   /**
    * delete a term by it's number.
    *
    * @param termNumber term number
    */
   public static void deleteTerm(int termNumber) {
      if (!getCloud().hasNode(termNumber)) {
         return;
      }
      Node termNode = getCloud().getNode(termNumber);
      RelationList relations = termNode.getRelations();

      if (relations != null) {
         termNode.deleteRelations();
      }
      termNode.delete();
   }

   /**
    * add a new term
    *
    * @param name new term name
    */
   public static void addTerm(String name) {
      NodeManager termNodeManager = getCloud().getNodeManager("term");
      Node termNode = termNodeManager.createNode();
      termNode.setStringValue("name", name);
      termNode.commit();
      if(ServerUtil.isStaging() && !ServerUtil.isSingle()) {
         Publish.publish(termNode);
      }
   }

   /**
    * be sure that the them has existed or not.
    *
    * @param name term name
    * @return <code>true</code> if the term has been in the application
    *         <code>false</code> no term
    */
   public static boolean hasTerm(String name) {
      NodeManager termNodeManager = getCloud().getNodeManager("term");
      NodeQuery query = termNodeManager.createQuery();
      SearchUtil.addEqualConstraint(query, termNodeManager.getField("name"), name);
      NodeList terms = query.getList();
      return terms != null && terms.size() > 0;
   }

   /**
    * update term name by it's number
    *
    * @param termNumber term number
    * @param name       the new name of the term
    */
   public static void updateTerm(int termNumber, String name) {
      Node termNode = getTermNodeById(termNumber);
      termNode.setStringValue("name", name);
      termNode.commit();
      if(ServerUtil.isStaging() && !ServerUtil.isSingle()) {
         Publish.publish(termNode);
      }
   }

   /**
    * get a term node by it's number.
    *
    * @param termNumber term number.
    * @return term node ojbect
    */
   public static Node getTermNodeById(int termNumber) {
      if (!getCloud().hasNode(termNumber)) {
         return null;
      }
      return getCloud().getNode(termNumber);
   }

   /**
    * get results of term paging list
    *
    * @param name     the name of a term
    * @param offset   the position where get results from.
    * @param pageSize max size for per page.
    * @return Nodelist object ,contains terms objects
    */
   public static NodeList searchTerms(String name, int offset, int pageSize) {

      NodeManager termNodeManager = getCloud().getNodeManager("term");
      NodeQuery query = termNodeManager.createQuery();
      if (StringUtils.isNotBlank(name)) {
         SearchUtil.addLikeConstraint(query, termNodeManager.getField("name"), name);
      }
      SearchUtil.addSortOrder(query, termNodeManager, "number", "down");
      query.setMaxNumber(pageSize);
      query.setOffset(offset);
      return query.getList();
   }

   /**
    * calculate the count of terms by term's name.
    *
    * @param name term name
    * @return the terms's count
    */
   public static int countTotalTerms(String name) {
      int size = 0;
      NodeManager termNodeManager = getCloud().getNodeManager("term");
      NodeQuery query = termNodeManager.createQuery();
      if (StringUtils.isNotBlank(name)) {
         SearchUtil.addLikeConstraint(query, termNodeManager.getField("name"), name);
      }
      int count = query.getMaxNumber();
      if (query.getList() != null) {
         size = query.getList().size();
      }
      return size;
   }

   /**
    * get cloud object
    *
    * @param no
    * @return cloud object
    */
   public static Cloud getCloud() {
      return CloudProviderFactory.getCloudProvider().getCloud();
   }
}
