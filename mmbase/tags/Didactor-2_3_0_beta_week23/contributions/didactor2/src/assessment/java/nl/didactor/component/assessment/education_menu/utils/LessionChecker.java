package nl.didactor.component.assessment.education_menu.utils;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

public class LessionChecker {


   private static Logger log = Logging.getLoggerInstance(LessionChecker.class.getName());

   /**
    * Checks that learnblocks are blocked for this particular user.
    * It is advised to call this method only once during the education menu building
    * The goal is perfomance improving.
    *
    * @param nodeEducation Node
    * @param nodeUser Node
    * @return HashSet
    */

   public static HashSet getBlockedLearnblocksForThisUser(Node nodeEducation, Node nodeUser) {
      HashSet hsetResult = new HashSet();
      Cloud cloud = nodeEducation.getCloud();

      NodeList nlVirtual = cloud.getList("" + nodeEducation.getNumber(),
                                         "educations,posrel,learnblocks",
                                         "learnblocks.number",
                                         null,
                                         "posrel.pos",
                                         null, null, true);

      Integer intCounter = new Integer(0);
      Boolean boolStatusBlocked = new Boolean(false);
      Boolean boolFirstHasFeedback = new Boolean(false);

      for (NodeIterator it = nlVirtual.nodeIterator(); it.hasNext(); ) {
         Node nodeVirtual = it.nextNode();
         Node nodeLearnBlock = cloud.getNode(nodeVirtual.getStringValue("learnblocks.number"));



         if (boolStatusBlocked.booleanValue()) {
            //It means the rest of learnblocks is closed.
            previousOne_HasGot_No_FeedbackRelated(nodeLearnBlock);
            hsetResult.add("" + nodeLearnBlock.getNumber());
         }
         else {
            NodeList nlVirtual2 = cloud.getList("" + nodeLearnBlock.getNumber(),
                                                "learnblocks,classrel,people",
                                                "classrel.number",
                                                "people.number='" + nodeUser.getNumber() + "'",
                                                null,
                                                null, null, true);


            if (nlVirtual2.size() == 0) {
               //blocked
               boolStatusBlocked = noFeedbackRelated(nodeLearnBlock, hsetResult, intCounter, boolStatusBlocked, boolFirstHasFeedback);
            }
            else {
               if (cloud.getNode(nlVirtual2.getNode(0).getStringValue("classrel.number")).countRelatedNodes("popfeedback") > 0) {
                  feedbackRelated(nodeLearnBlock);

                  if (intCounter.intValue() == 0) {
                     boolFirstHasFeedback = new Boolean(true);
                  }
               }
               else {
                  //blocked
                  boolStatusBlocked = noFeedbackRelated(nodeLearnBlock, hsetResult, intCounter, boolStatusBlocked, boolFirstHasFeedback);
               }
            }
         }

         intCounter = new Integer(intCounter.intValue() + 1);
      }

      return hsetResult;
   }





   private static Boolean noFeedbackRelated(Node nodeLearnBlock, HashSet hsetResult, Integer intCounter, Boolean boolStatusBlocked, Boolean boolFirstHasFeedback){
      if(intCounter.intValue() == 0){
         log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " is open because it is the first one in the list");
      }
      else{
         boolStatusBlocked = new Boolean(true);
      }

      if (intCounter.intValue() == 1) {
         if (!boolFirstHasFeedback.booleanValue()) {
            //The first learnblock has got no feedback
            previousOne_HasGot_No_FeedbackRelated(nodeLearnBlock);
            hsetResult.add("" + nodeLearnBlock.getNumber());
         }
      }

      log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " has got no feedback related.");

      return boolStatusBlocked;
   }





   private static void feedbackRelated(Node nodeLearnBlock){
      log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " has got a feedback related.");
   }


   private static void previousOne_HasGot_No_FeedbackRelated(Node nodeLearnBlock){
      log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " is blocked because the previous one has got no feedback.");
   }
}





