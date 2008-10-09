package nl.didactor.component.assessment.education_menu.utils;

import java.util.*;
import nl.didactor.util.ClassRoom;

import nl.didactor.component.assessment.AssessmentField;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.Casting;
import nl.didactor.component.Component;
import org.mmbase.util.logging.*;


/**
 * @javadoc
 * @version $Id: LessonChecker.java,v 1.5 2008-10-09 16:07:24 michiel Exp $
 */

public class LessonChecker {


    private static final Logger log = Logging.getLoggerInstance(LessonChecker.class);

    protected static Component getComponent() {
        return Component.getComponent("assessment");
    }

    protected static boolean checkRelated(Cloud cloud, Node education) {
        Map wtf = new HashMap();
        wtf.put("education", education);
        return  Casting.toBoolean(getComponent().getSetting("relate_component", cloud, wtf));
    }

   /**
    * Checks that learnblocks are blocked for this particular user.
    * It is advised to call this method only once during the education menu building
    * The goal is performance improving.
    *
    * @param education Node
    * @param user Node
    * @return Set
    */

    public static Set<Node> getBlockedLearnblocksForThisUser(Node education, Node user) {
       Set<Node> resultSet = new HashSet<Node>();
       Cloud cloud = education.getCloud();

       // Check whether this education indeeds needs 'assessment'.

       Node assessment = cloud.getNode(getComponent().getNumber());
       if (! education.getRelatedNodes("components", "settingrel", "destination").contains(assessment)) {
           return resultSet;
       }

       Collection<String> roles = ClassRoom.getRoles(user, education.getNumber());
       if (roles.contains("teacher") ||
           roles.contains("courseeditor") ||
           roles.contains("systemadministrator")) {
           // nothing blocked
           return resultSet;
       }

       NodeList relatedLearnBlocks = cloud.getList("" + education.getNumber(),
                                                   "educations,posrel,learnblocks",
                                                   "learnblocks.number",
                                                   null,
                                                   "posrel.pos",
                                                   null, null, true);

       boolean checkRelated = checkRelated(cloud, education);

       int counter = 0;
       boolean statusBlocked = false;
       boolean firstHasFeedback = false;

       for (NodeIterator it = relatedLearnBlocks.nodeIterator(); it.hasNext(); ) {
           Node clusterNode = it.nextNode();
           Node learnBlock = cloud.getNode(clusterNode.getIntValue("learnblocks.number"));


           if (statusBlocked) {
               //It means the rest of learnblocks is closed.
               log.debug("Learnblock=" + learnBlock.getNumber() + " is blocked because the previous one is blocked.");
               resultSet.add(learnBlock);
           } else {
               NodeList classRels = cloud.getList("" + learnBlock.getNumber(),
                                                  "learnblocks,classrel,people",
                                                  "classrel.number",
                                                  "people.number='" + user.getNumber() + "'",
                                                  null,
                                                  null, null, true);


               if (classRels.size() == 0) {
                   if (checkRelated) {
                       if (Queries.count(AssessmentField.getRelationsQuery(learnBlock)) != 0) {
                           statusBlocked = noFeedbackRelated(learnBlock, resultSet, counter, statusBlocked, firstHasFeedback);
                       }
                   } else {
                       //blocked
                       statusBlocked = noFeedbackRelated(learnBlock, resultSet, counter, statusBlocked, firstHasFeedback);
                   }
               } else {
                   if (cloud.getNode(classRels.getNode(0).getIntValue("classrel.number")).countRelatedNodes("popfeedback") > 0) {
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
                log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " is blocked because the previous one has got no feedback.");
                resultSet.add(nodeLearnBlock);
            }
        }

        log.debug("Learnblock=" + nodeLearnBlock.getNumber() + " has got no feedback related.");

        return statusBlocked;
    }


}





