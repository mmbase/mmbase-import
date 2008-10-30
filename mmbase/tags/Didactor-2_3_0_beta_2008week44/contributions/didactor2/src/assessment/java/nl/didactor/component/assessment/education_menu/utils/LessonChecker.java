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
 * @version $Id: LessonChecker.java,v 1.7 2008-10-23 16:00:38 michiel Exp $
 */

public class LessonChecker {


    private static final Logger log = Logging.getLoggerInstance(LessonChecker.class);

    protected static Component getComponent() {
        return Component.getComponent("assessment");
    }


    /**
     * Wether, or not, for given neducation the 'relate_learnblocks' setting is set.
     */
    protected static boolean checkRelated(Cloud cloud, Node education) {
        Map wtf = new HashMap();
        wtf.put("education", education);
        return  Casting.toBoolean(getComponent().getSetting("relate_learnblocks", cloud, wtf));
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
           log.debug("Nothing blocked because education not related to " + assessment);
           return resultSet;
       }

       Collection<String> roles = ClassRoom.getRoles(user, education.getNumber());
       if (roles.contains("teacher") ||
           roles.contains("courseeditor") ||
           roles.contains("systemadministrator")) {
           // nothing blocked
           log.debug("Nothing blocked because high role");
           return resultSet;
       }

       NodeList relatedLearnBlocks = cloud.getList("" + education.getNumber(),
                                                   "educations,posrel,learnblocks",
                                                   "learnblocks.number",
                                                   null,
                                                   "posrel.pos",
                                                   null, null, true);

       boolean checkRelated = checkRelated(cloud, education);

       boolean statusBlocked = false;

       for (NodeIterator it = relatedLearnBlocks.nodeIterator(); it.hasNext(); ) {
           Node clusterNode = it.nextNode();
           Node learnBlock = cloud.getNode(clusterNode.getIntValue("learnblocks.number"));


           if (statusBlocked) {
               //It means the rest of learnblocks is closed.
               log.debug("Learnblock=" + learnBlock.getNumber() + " is blocked because the previous one is blocked.");
               resultSet.add(learnBlock);
           } else {
               log.debug("Checking relation " + learnBlock.getNumber() + " -> " + user.getNumber());

               if (checkRelated) {
                   boolean related = Queries.count(AssessmentField.getRelationsQuery(learnBlock)) > 0;
                   if (! related) continue;
               }
               NodeList classRels = cloud.getList("" + learnBlock.getNumber(),
                                                  "learnblocks,classrel,people",
                                                  "classrel.number",
                                                  "people.number='" + user.getNumber() + "'",
                                                  null,
                                                  null, null, true);
               boolean assessed = classRels.size() > 0;

               if (assessed) {
                   boolean hasFeedBack = cloud.getNode(classRels.getNode(0).getIntValue("classrel.number")).countRelatedNodes("popfeedback") > 0;
                   if (! hasFeedBack) statusBlocked = true;
               } else {
                   statusBlocked = true;
               }
           }
       }

       return resultSet;
    }




}





