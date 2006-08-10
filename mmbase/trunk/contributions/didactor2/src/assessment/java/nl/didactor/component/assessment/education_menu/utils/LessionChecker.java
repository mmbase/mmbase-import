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

      int iCounter = 0;
      for (NodeIterator it = nlVirtual.nodeIterator(); it.hasNext(); ) {
         Node nodeVirtual = it.nextNode();
         Node nodeLearnBlock = cloud.getNode(nodeVirtual.getStringValue("learnblocks.number"));

         if (iCounter == 0) {
            log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " is open because it is the first one in the list");
         }
         else {

            NodeList nlVirtual2 = cloud.getList("" + nodeLearnBlock.getNumber(),
                                                "learnblocks,classrel,people",
                                                "classrel.number",
                                                "people.number='" + nodeUser.getNumber() + "'",
                                                null,
                                                null, null, true);

            if (nlVirtual2.size() == 0) {
               hsetResult.add("" + nodeLearnBlock.getNumber());
               log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " has got no feedback related.");
            }
            else {
               if (cloud.getNode(nlVirtual2.getNode(0).getStringValue("classrel.number")).countRelatedNodes("popfeedback") > 0) {
                  log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " has got a feedback related.");
               }
               else {
                  hsetResult.add("" + nodeLearnBlock.getNumber());
                  log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " has got no feedback related.");

               }
            }
         }
         iCounter++;
      }

      return hsetResult;
   }
}





