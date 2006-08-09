package nl.didactor.component.assessment.education_menu.utils;

import java.util.*;

import org.mmbase.bridge.*;

public class LessionChecker {

   /**
    * Checks that learnblocks are open for this particular user.
    * It is advised to call this method only once during the education menu building
    * The goal is perfomance improving.
    *
    * @param nodeEducation Node
    * @param nodeUser Node
    * @return HashSet
    */

   public static HashSet getOpenLearnblocksForThisUser(Node nodeEducation, Node nodeUser) {
      HashSet hsetResult = new HashSet();
      Cloud cloud = nodeEducation.getCloud();

      NodeList nlVirtual = cloud.getList("" + nodeEducation.getNumber(),
                                         "people,classrel,learnblocks,posrel,educations",
                                         "classrel.number,learnblocks.number",
                                         "educations.number='" + nodeEducation.getNumber() + "'",
                                         null, null, null, true);

      int iCounter = 0;
      for (NodeIterator it = nlVirtual.nodeIterator(); it.hasNext(); ) {
         Node nodeLearnBlock = cloud.getNode((it.nextNode()).getStringValue("learnblocks.number"));
         Node nodeClassrel = cloud.getNode((it.nextNode()).getStringValue("classrel.number"));
         if(iCounter == 0){
            hsetResult.add("" + nodeLearnBlock.getNumber());
         }
         else{
            NodeList nlFeedbacks = nodeClassrel.getRelatedNodes("popfeedback");
            if(nlFeedbacks.size() > 0){
               hsetResult.add("" + nodeLearnBlock.getNumber());
            }
         }
         iCounter++;
      }

      return hsetResult;
   }
}



