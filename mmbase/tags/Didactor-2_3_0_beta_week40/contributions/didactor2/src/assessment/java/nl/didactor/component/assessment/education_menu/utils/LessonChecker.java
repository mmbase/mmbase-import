package nl.didactor.component.assessment.education_menu.utils;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;


/**
 * @javadoc
 * @version $Id: LessonChecker.java,v 1.2 2007-07-26 09:09:36 michiel Exp $
 */

public class LessonChecker {


   private static final Logger log = Logging.getLoggerInstance(LessonChecker.class);
    
   /**
    * Checks that learnblocks are blocked for this particular user.
    * It is advised to call this method only once during the education menu building
    * The goal is performance improving.
    *
    * @param nodeEducation Node
    * @param nodeUser Node
    * @return Set
    */
    
    public static Set<Node> getBlockedLearnblocksForThisUser(Node nodeEducation, Node nodeUser) {
       Set<Node> resultSet = new HashSet<Node>();
       Cloud cloud = nodeEducation.getCloud();
       
       NodeList relatedLearnBlocks = cloud.getList("" + nodeEducation.getNumber(),
                                             "educations,posrel,learnblocks",
                                             "learnblocks.number",
                                             null,
                                             "posrel.pos",
                                             null, null, true);
       
       int counter = 0;
       boolean statusBlocked = false;
       boolean firstHasFeedback = false;
       
       for (NodeIterator it = relatedLearnBlocks.nodeIterator(); it.hasNext(); ) {
           Node clusterNode = it.nextNode();
           Node learnBlock = cloud.getNode(clusterNode.getIntValue("learnblocks.number"));
           
           
           if (statusBlocked) {
               //It means the rest of learnblocks is closed.
               previousOneHasGotNoFeedbackRelated(learnBlock);
               resultSet.add(learnBlock);
           } else {
               NodeList classRels = cloud.getList("" + learnBlock.getNumber(),
                                                  "learnblocks,classrel,people",
                                                  "classrel.number",
                                                  "people.number='" + nodeUser.getNumber() + "'",
                                                  null,
                                                  null, null, true);
               
               
               if (classRels.size() == 0) {
                   //blocked
                   statusBlocked = noFeedbackRelated(learnBlock, resultSet, counter, statusBlocked, firstHasFeedback);
               } else {
                   if (cloud.getNode(classRels.getNode(0).getIntValue("classrel.number")).countRelatedNodes("popfeedback") > 0) {
                       feedbackRelated(learnBlock);                       
                       if (counter == 0) {
                           firstHasFeedback = true;
                       }
                   } else {
                       //blocked
                       statusBlocked = noFeedbackRelated(learnBlock, resultSet, counter, statusBlocked, firstHasFeedback);
                   }
               }
           }
           
           counter++;
       }
       
       return resultSet;
    }
    
    
    
    

    private static boolean noFeedbackRelated(Node nodeLearnBlock, Set<Node> resultSet, int counter, boolean statusBlocked, boolean firstHasFeedback){
      if(counter == 0) {
          log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " is open because it is the first one in the list");
      } else {
          statusBlocked = true;
      }
      
      if (counter == 1) {
          if (!firstHasFeedback) {
              //The first learnblock has got no feedback
              previousOneHasGotNoFeedbackRelated(nodeLearnBlock);
              resultSet.add(nodeLearnBlock);
          }
      }
      
      log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " has got no feedback related.");
      
      return statusBlocked;
   }





   private static void feedbackRelated(Node nodeLearnBlock){
      log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " has got a feedback related.");
   }


   private static void previousOneHasGotNoFeedbackRelated(Node nodeLearnBlock){
      log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " is blocked because the previous one has got no feedback.");
   }
}





