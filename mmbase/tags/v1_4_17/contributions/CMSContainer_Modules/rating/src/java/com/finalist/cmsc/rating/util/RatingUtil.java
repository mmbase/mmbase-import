package com.finalist.cmsc.rating.util;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Relation;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

public class RatingUtil {

   /**
    * Gets the rating of a certain content element for a certain user
    * 
    * @param contentNumber
    *           The number (identifier) of the content
    * @param userNumber
    *           The identifier of the user
    * @return The rating of this element given by the given user set in a Rating
    *         object. returns -1 when it has not been rated.
    * @todo implement
    */
   public static int getUserRating(int contentNumber, String user) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList ratingList = cloud.getList(null, "contentelement,rating", "rating.rating", "contentelement.number = "
            + contentNumber + " AND rating.user = '" + user + "'", null, null, null, false);
      if (ratingList.size() == 0) {
         return -1;
      }
      else {
         Node node = ratingList.getNode(0);
         return node.getIntValue("rating.rating");
      }
   }


   /**
    * Gets the rating of a certain content element for all users
    * 
    * @param contentNumber
    *           The number (identifier) of the content
    * @return The average rating for this element given by all the users This
    *         object will hold the rating. And the number of users which rated.
    *         Returns 0 for the count and -1 for the rating when the element has
    *         not been rated.
    * @todo implement
    */
   public static Rating getContentRating(int contentNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList ratingList = cloud.getList(null, "contentelement,rating", "rating.rating", "contentelement.number = "
            + contentNumber, null, null, null, false);
      float rating = -1;
      int count = 0;
      for (NodeIterator ni = ratingList.nodeIterator(); ni.hasNext();) {
         Node node = ni.nextNode();
         int thisRating = node.getIntValue("rating.rating");

         rating = (rating * count + thisRating) / (count + 1);
         count++;
      }
      return new Rating(rating, count);
   }


   /**
    * Sets the rating of a certain content element for the given user Rating can
    * only be done once!
    * 
    * @param contentNumber
    *           The number (identifier) of the content
    * @param user
    *           The identifier of the user
    * @param rating
    *           The rating given by the user
    * @todo implement
    */
   public synchronized static void setUserRating(int contentNumber, String user, int rating) {
      int oldUserRating = getUserRating(contentNumber, user);
      if (oldUserRating == -1) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node contentNode = cloud.getNode(contentNumber);

         Node newNode = cloud.getNodeManager("rating").createNode();
         newNode.setStringValue("user", user);
         newNode.setIntValue("rating", rating);
         newNode.commit();

         Relation newRelation = newNode.createRelation(contentNode, cloud.getRelationManager("related"));
         newRelation.commit();
      }
   }
}
