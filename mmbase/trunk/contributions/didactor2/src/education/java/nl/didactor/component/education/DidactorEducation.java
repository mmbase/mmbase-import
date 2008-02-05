package nl.didactor.component.education;

import java.util.*;

import javax.servlet.jsp.JspTagException;
import nl.didactor.component.Component;
import nl.didactor.util.ClassRoom;
import org.mmbase.module.core.*;
import org.mmbase.bridge.*;
import org.mmbase.security.Action;
import org.mmbase.security.ActionChecker;
import org.mmbase.security.UserContext;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * @javadoc
 * @version $Id: DidactorEducation.java,v 1.15 2008-02-05 15:05:55 michiel Exp $
 */
public class DidactorEducation extends Component {
    private static Logger log = Logging.getLoggerInstance(DidactorEducation.class);

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
        return "education";
    }

    /**
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    public Component[] dependsOn() {
        return new Component[0];
    }

    public int castIdentifier( Object object) throws JspTagException {
        int value= 0;
//      obtain education via context
         if (object != null) {
             if (object instanceof Integer) {
                 value= ((Integer) object ).intValue();
             } else if (object instanceof String) {
                 value= Integer.parseInt( (String) object);
             } else {
                 throw new JspTagException( "Unknown type" + object);
             }
         }
         return value;
    }

    private static final Parameter SUBJECT   = new Parameter("subject", Node.class, true);

    private static final Parameter[] PARAMS = new Parameter[] {Component.EDUCATION, Component.CLASS, SUBJECT};


    /**
     * TODO, 'view answers' actually sounds like normal 'node based' mmbase security.
     * you can principally per answer node calculate whether you may see it or not.
     */
    private static final Action VIEW_ANSWERS = new Action("education","viewAnswers", new ActionChecker() {
            public boolean check(UserContext user, Action ac, Parameters parameters) {
                if (user.getRank() == org.mmbase.security.Rank.ADMIN) return true;
                Node subject = (Node) parameters.get(SUBJECT);
                Node education = (Node) parameters.get(Component.EDUCATION);
                Node clazz = (Node) parameters.get(Component.CLASS);
                int u = ((nl.didactor.security.UserContext)user).getUserNumber();
                return u == subject.getNumber() ||
                    isTeacherOf(subject.getCloud(), u, subject.getNumber(), education.getNumber(), clazz == null ? -1 : clazz.getNumber());
            }
        }) {
            public Parameters createParameters() {
                return new Parameters(PARAMS);
            }
        };



    /**
     * Rating an answer is changing a certain field of a node. MMBase security is based on entire
     * nodes. So we need something special. 'Action' framework is used.
     */
    private static final Action RATE         = new Action("education", "rate", new ActionChecker() {
            public boolean check(UserContext user, Action ac, Parameters parameters) {
                if (user.getRank() == org.mmbase.security.Rank.ADMIN) return true;
                Node subject = (Node) parameters.get(SUBJECT);
                Node education = (Node) parameters.get(EDUCATION);
                Node clazz = (Node) parameters.get(CLASS);
                return isTeacherOf(subject.getCloud(), Integer.parseInt(user.getIdentifier()), subject.getNumber(), education.getNumber(), clazz == null ? -1 : clazz.getNumber());
            }
        }) {
            public Parameters createParameters() {
                return new Parameters(PARAMS);
            }
        };
    private static final Map<String, Action> actions = new HashMap<String, Action>();

    static {
        actions.put(VIEW_ANSWERS.getName(), VIEW_ANSWERS);
        actions.put(RATE.getName(), VIEW_ANSWERS);
    }

    public Map<String, Action> getActions() {
        return Collections.unmodifiableMap(actions);
    }

    protected static boolean  isTeacherOf(Cloud cloud, int user, int subject, int education, int clazz) {
        MMObjectNode usernode = MMBase.getMMBase().getBuilder("people").getNode(user);
        return ClassRoom.isClassMember(usernode, subject, clazz, education, "teacher", cloud)
            || ClassRoom.isWorkgroupMember(usernode, subject, clazz, education, "teacher", cloud);
    }

    // javadoc inherited
    @Override
    public boolean[] may(Cloud cloud, Action action, Parameters arguments) {
        boolean mayvalue[]= new boolean[] {false, false};
        mayvalue[0] = action.getDefault().check(cloud.getUser(), action, arguments);
        return mayvalue;
    }

    @Override
    public String getValue(String setting, Cloud cloud, Map context, String[] arguments) {
        if ("showlo".equals(setting)) {
            String lo = arguments[0];
            int res = showLo(cloud, context, lo);
            return "" + res;
        }
        return "";
    }

    /**
     * Return the 'show' status of a given object (either learnobject or learnblock).
     * It will base the decision on related 'mmevents' objects:
     * <ul>
     *  <li>If there are no mmevents, then show this object
     *  <li>If there are mmevents, check if there is one that allows viewing
     * </ul>
     * The mmevents save an 'offset' that has to be added to the startdate of the current education.
     * Possible return values:
     * <ul>
     *   <li>0, Do not show this object
     *   <li>1, Show the object, but 'grayed out'.
     *   <li>2, Show the object
     * </ul>
     */
    private int showLo(Cloud cloud, Map context, String lo) {
        String classno = org.mmbase.util.Casting.toString(context.get("class"));
        if (classno == null || "".equals(classno) || "null".equals(classno)) {
            // no class, so it must be an administrator, who may always view everything
            return 2;
        }

        Node learnobject = cloud.getNode(lo);
        NodeList events = learnobject.getRelatedNodes("mmevents", "related", "destination");
        if (events.size() == 0) {
            return 2;
        }
        Node cls = cloud.getNode(classno);
        NodeList clsRuntime = cls.getRelatedNodes("mmevents", "related", "destination");
        if (clsRuntime.size() == 0) {
            // THIS SHOULD NEVER HAPPEN!
            log.error("Class '" + classno + "' does not have a runtime!!! FIX THIS!");
            return 2;
        }
        Node classRuntime = clsRuntime.getNode(0);
        long classStart = classRuntime.getLongValue("start");
        long currenttime = System.currentTimeMillis() / 1000;

        for (int i = 0; i < events.size(); i++) {
            long start = events.getNode(i).getLongValue("start");
            long stop = events.getNode(i).getLongValue("stop");
            if (currenttime > (classStart + start) && currenttime < (classStart + stop)) {
                return 2;
            }
        }
        return 0;
    }
}
