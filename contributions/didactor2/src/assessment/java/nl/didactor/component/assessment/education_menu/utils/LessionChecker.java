package nl.didactor.component.assessment.education_menu.utils;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;


/**
 * WTF is a 'Lession'?
 * @javadoc
 * @version $Id: LessionChecker.java,v 1.4 2007-07-03 08:33:57 michiel Exp $
 */

public class LessionChecker {


   private static final Logger log = Logging.getLoggerInstance(LessionChecker.class);
    
   /**
    * Checks that learnblocks are blocked for this particular user.
    * It is advised to call this method only once during the education menu building
    * The goal is performance improving.
    *
    * @param nodeEducation Node
    * @param nodeUser Node
    * @return Set
    */
    
    public static Set<String> getBlockedLearnblocksForThisUser(Node nodeEducation, Node nodeUser) {
       Set<String> hsetResult = new HashSet<String>();
       Cloud cloud = nodeEducation.getCloud();
       
       NodeList nlVirtual = cloud.getList("" + nodeEducation.getNumber(),
                                          "educations,posrel,learnblocks",
                                          "learnblocks.number",
                                          null,
                                          "posrel.pos",
                                          null, null, true);
       
       int intCounter = 0;
       boolean boolStatusBlocked = false;
       boolean boolFirstHasFeedback = false;
       
       for (NodeIterator it = nlVirtual.nodeIterator(); it.hasNext(); ) {
           Node nodeVirtual = it.nextNode();
           Node nodeLearnBlock = cloud.getNode(nodeVirtual.getStringValue("learnblocks.number"));
           
           
           if (boolStatusBlocked) {
               //It means the rest of learnblocks is closed.
               previousOne_HasGot_No_FeedbackRelated(nodeLearnBlock);
               hsetResult.add("" + nodeLearnBlock.getNumber());
           } else {
               NodeList nlVirtual2 = cloud.getList("" + nodeLearnBlock.getNumber(),
                                                   "learnblocks,classrel,people",
                                                   "classrel.number",
                                                   "people.number='" + nodeUser.getNumber() + "'",
                                                   null,
                                                   null, null, true);
               
               
               if (nlVirtual2.size() == 0) {
                   //blocked
                   boolStatusBlocked = noFeedbackRelated(nodeLearnBlock, hsetResult, intCounter, boolStatusBlocked, boolFirstHasFeedback);
               } else {
                   if (cloud.getNode(nlVirtual2.getNode(0).getStringValue("classrel.number")).countRelatedNodes("popfeedback") > 0) {
                       feedbackRelated(nodeLearnBlock);                       
                       if (intCounter == 0) {
                           boolFirstHasFeedback = true;
                       }
                   } else {
                       //blocked
                       boolStatusBlocked = noFeedbackRelated(nodeLearnBlock, hsetResult, intCounter, boolStatusBlocked, boolFirstHasFeedback);
                   }
               }
           }
           
           intCounter++;
       }
       
       return hsetResult;
    }
    
    
    
    

    private static boolean noFeedbackRelated(Node nodeLearnBlock, Set<String> hsetResult, int intCounter, boolean boolStatusBlocked, boolean boolFirstHasFeedback){
      if(intCounter == 0){
          log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " is open because it is the first one in the list");
      } else{
          boolStatusBlocked = true;
      }
      
      if (intCounter == 1) {
          if (!boolFirstHasFeedback) {
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





