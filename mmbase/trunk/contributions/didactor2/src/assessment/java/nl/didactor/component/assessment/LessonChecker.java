package nl.didactor.component.assessment;

import java.util.*;
import nl.didactor.util.ClassRoom;

import nl.didactor.component.assessment.AssessmentField;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.Casting;
import org.mmbase.util.functions.Name;
import org.mmbase.util.functions.Required;
import nl.didactor.component.Component;
import org.mmbase.util.logging.*;


/**
 * The assessment component attributes some statuses to 'learnblocks' directly related to the
 * education ('lessons'). E.g. it can keep track on whether the lesson is 'closed' and whether
 * feedback on a lesson was given by a coach (and it can also maintain this feedback).
 *
 * This class collects some functionalilty for this, it e.g. implements what are lessons {@link
 * #getLessons}), which lessons are still inaccessible ({@link #getBlockedLearnblocksForUser}) and
 * whether a lesson already given feedback on ({@link lessonHasFeedback}).

 * Functionality are made accessible to front-end jsps using (node) functions (see e.g. people.xml).
 *
 * @version $Id: LessonChecker.java,v 1.4 2009-01-08 11:09:20 michiel Exp $
 */

public class LessonChecker {


    private static final Logger log = Logging.getLoggerInstance(LessonChecker.class);

    protected static Component getComponent() {
        return Component.getComponent("assessment");
    }


    /**
     * Wether, or not, for given education the 'relate_learnblocks' setting is set.
     */
    protected static boolean checkRelated(Cloud cloud, Node education) {
        Map<String, Object> wtf = new HashMap<String, Object>();
        wtf.put("education", education);
        return  Casting.toBoolean(getComponent().getSetting("relate_learnblocks", cloud, wtf));
    }


    /**
     * Calculates and returns those learnblocks that according to this component are to be considered
     * 'lessons'. These are all learnblocks directly related to the education if the
     * 'related_learnblocks' setting is false, or all those learnblocsk directly related to the
     * education and to this component, if that setting is true.
     */

    protected static List<Node> getLessons(@Required @Name("education") Node education) {
        List<Node> result = new ArrayList<Node>();
        Cloud cloud = education.getCloud();
        NodeQuery q = Queries.createRelatedNodesQuery(education,
                                                      cloud.getNodeManager("learnblocks"),
                                                      "posrel",
                                                      "destination");
        Queries.addSortOrders(q, "posrel.pos", "UP");
        NodeList relatedLearnBlocks = q.getNodeManager().getList(q);

        boolean checkRelated = checkRelated(cloud, education);
        if (! checkRelated) return relatedLearnBlocks;

        for (Node learnBlock : relatedLearnBlocks) {
            boolean related = Queries.count(AssessmentField.getRelationsQuery(learnBlock)) > 0;
            if (related) {
                result.add(learnBlock);
            }
        }
        return result;
    }

    protected static List<Node> getLearnBlocks(@Required @Name("education") Node education) {
        List<Node> result = new ArrayList<Node>();
        Cloud cloud = education.getCloud();
        NodeQuery q = Queries.createRelatedNodesQuery(education,
                                                      cloud.getNodeManager("learnblocks"),
                                                      "posrel",
                                                      "destination");
        Queries.addSortOrders(q, "posrel.pos", "UP");
        NodeList relatedLearnBlocks = q.getNodeManager().getList(q);

        for (Node learnBlock : relatedLearnBlocks) {
            boolean related = Queries.count(AssessmentField.getRelationsQuery(learnBlock)) > 0;
            result.add(learnBlock);
        }
        return result;
    }


    protected static Node getClassRel(Node lesson, Node user) {
        NodeList classRels = user.getCloud().getList("" + lesson.getNumber(),
                                           "learnblocks,classrel,people",
                                           "classrel.number",
                                           "people.number='" + user.getNumber() + "'",
                                           null,
                                           null, null, true);
        if (classRels.size() == 0) {
            return null;
        } else {
            return classRels.get(0).getNodeValue("classrel.number");
        }
    }

   /**
    * Returns all learnblocks (or 'lessons') that are blocked for a certain user. A learnblock is
    * blocked if
    <ol>
      <li>the assessment module is active for its education</li>
      <li>the user did not yet close the previous lesson (in the assement module)
      A lesson is considered closed, if it has a classrel relatation to the user.
      </li>
    </ol>
    * If the setting  relate_learnblocks is true, then the lesson is ignored in this process, if it
    * itself is not related to the component (the 'assessment' dropdown in the editwizards of the
    * lesson). This makes it possible to have 'pseudo' lessons, which are not assessed.

    *
    * @param education Node
    * @param user Node
    * @return Set
    */

    public static Set<Node> getBlockedLearnblocksForThisUser(@Required @Name("education") Node education,
                                                             @Required @Name("node") Node user) {
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

       List<Node> lessons = getLessons(education);
       boolean foundALesson = false;

       boolean statusBlocked = false;

       for (Node learnBlock : getLearnBlocks(education)) {

           if (lessons.contains(learnBlock)) foundALesson = true;

           if (statusBlocked) {
               //It means the rest of learnblocks is closed.
               log.debug("Learnblock=" + learnBlock.getNumber() + " is blocked because the previous one is blocked.");
               resultSet.add(learnBlock);
           } else {
               log.debug("Checking relation " + learnBlock.getNumber() + " -> " + user.getNumber());

               Node classrel = getClassRel(learnBlock, user);
               boolean closed = classrel != null;
               log.debug("" + learnBlock.getNumber() + " closed " + closed);
               if (! closed && foundALesson) {
                   statusBlocked = true;
               }
           }
       }

       return resultSet;
    }

    public static boolean lessonHasFeedback(@Required @Name("node") Node user,
                                            @Required @Name("lesson") Node lesson) {

        Node classRel = getClassRel(lesson, user);
        Cloud cloud = user.getCloud();
        if (cloud.hasNodeManager("popfeedback")) {
            NodeList feedbacks = classRel.getRelatedNodes("popfeedback");
            if (feedbacks.size() > 0 && ! "".equals(feedbacks.get(0).getStringValue("text").trim())) return true;
        }
        if (cloud.hasNodeManager("shouts")) {
            NodeQuery q = Queries.createRelatedNodesQuery(user, cloud.getNodeManager("shouts"), "posrel", "destination");
            Queries.addConstraint(q, Queries.createConstraint(q, "reference", Queries.getOperator("="), lesson));
            org.mmbase.storage.search.Constraint c = Queries.createConstraint(q, "from", Queries.getOperator("="), user);
            q.setInverse(c, true);
            Queries.addConstraint(q, c);
            if (Queries.count(q) > 0) return true;
        }
        return false;
    }


    /**
     * Determins whether a certain lesson can be closed by a certain user.
     * This is always true if the setting needs_feedback_to_close_next_lesson is false.
     * Otherwise it does return true if the previous lessons has feedback (as implemented in {@link
     * #lessonHasFeedback}
     */
    public static boolean canCloseLesson(@Required @Name("node") Node user,
                                         @Required @Name("lesson") Node lesson) {
        Map<String, Object> wtf = new HashMap<String, Object>();
        Node education = lesson.getRelatedNodes("educations", "posrel", "source").getNode(0);
        wtf.put("education", education);
        boolean needsFeedback =  Casting.toBoolean(getComponent().getSetting("needs_feedback_to_close_next_lesson",  user.getCloud(), wtf));
        if (! needsFeedback) {
            return true;
        }
        List<Node> lessons = getLessons(education);
        int i = lessons.indexOf(lesson);
        if (i == 0) return true;
        Node prevLesson = lessons.get(i - 1);
        return lessonHasFeedback(user, prevLesson);
    }


}




