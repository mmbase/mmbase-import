package nl.didactor.component.competence;
import nl.didactor.component.Component;
import org.mmbase.bridge.*;
import java.util.Map;

public class DidactorCompetence extends Component {
    /**
     * Returns the version of the component
     */
    public String getVersion() {
        return "2.0";
    }

    /**
     * Returns the name of the component
     */
    public String getName() {
        return "competence";
    }

    public void init() {
        super.init();
        Component.getComponent("education").registerInterested(this);
    }

    /**
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    public Component[] dependsOn() {
        return new Component[] {Component.getComponent("education")};
    }

    @Override
    public String getValue(String setting, Cloud cloud, Map context, String[] arguments) {
        if ("showlo".equals(setting)) {
            return "" + showLo(cloud, context, Integer.parseInt(arguments[0]), Integer.parseInt(arguments[1]));
        }
        return "";
    }

    /**
     * Return the 'show' status of a given object (either learnobject or learnblock).
     * Possible values:
     * <ul>
     *   <li>0, Do not show this object
     *   <li>1, Show the object, but 'grayed out'.
     *   <li>2, Show the object
     * </ul>
     * For competence management, this means that:
     * <ul>
     *  <li>If the user doesn't have competence that is related to the object in a
     *      'must-have' relation, it returns '0';
     *  <li>If the user has the competence that is related to the object in a
     *      'develops' relation, it returns '1'.
     * </ul>
     */
    private int showLo(Cloud cloud, Map context, int lonumber, int maxlevel) {
        if (maxlevel == 0)
            return 0;
        Node lo = cloud.getNode(lonumber);

        // if maxlevel == 1, then we don't need to check for 'developComp', max is
        // always needcomp anyways
        NodeList needComp = lo.getRelatedNodes("competencies", "needcomp", "destination");
        if (needComp.size() > 0) {
            // Check if the user has all of the needed competencies
            Node user = cloud.getNode(Integer.parseInt((String)context.get("user")));
            NodeList hasComp = user.getRelatedNodes("competencies", "havecomp", "destination");
            for (int i=0; i<needComp.size(); i++) {
                if (!hasComp.contains(needComp.get(i))) {
                    return 0;
                }
            }
        }

        if (maxlevel > 1) {
            NodeList developComp = lo.getRelatedNodes("competencies", "needcomp", "destination");
            Node user = cloud.getNode(Integer.parseInt((String)context.get("user")));
            NodeList hasComp = user.getRelatedNodes("competencies", "havecomp", "destination");
            int retval = 2;
            for (int i=0; i<developComp.size(); i++) {
                if (!hasComp.contains(developComp.get(i))) {
                    retval = 1; // a competence is being developt that the user doesn't already have
                }
            }
            return retval;
        }

        return 2;
    }

}
