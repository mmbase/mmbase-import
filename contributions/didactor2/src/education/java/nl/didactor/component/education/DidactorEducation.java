package nl.didactor.component.education;

import java.util.Map;
import java.util.Vector;
import java.lang.Integer;
import javax.servlet.jsp.JspTagException;
import nl.didactor.component.Component;
import nl.didactor.util.ClassRoom;
import org.mmbase.module.core.*;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class DidactorEducation extends Component {
    private Vector interestedComponents = new Vector();
    private static Logger log = Logging.getLoggerInstance(DidactorEducation.class.getName());

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

    public int castIdentifier( Object object)
        throws JspTagException
    {
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

    // javadoc inherited
    public boolean[] may (String operation, Cloud cloud, Map context, String[] arguments)
    {
        boolean mayvalue[]= new boolean[] {false, false};
        try
        {
            if (operation.equals( "isSelfOrTeacherOf") || operation.equals( "isTeacherOf"))
            {

                Object user = context.get( "user");
                Object educationobj= context.get( "education");
                Object classobj= context.get( "class");

                MMObjectNode usernode = MMBase.getMMBase().getBuilder("people").getNode(  ((Integer)user).intValue());
                if (usernode == null)
                {
                    throw new JspTagException("User with number '" + user + "' not found");
                }
                int educationno = castIdentifier( educationobj);


                int classno;
                if((classobj != null) && ( (classobj instanceof String) && (!classobj.equals("null"))) )
                {//the class is a number
                   classno = castIdentifier(classobj);
                }
                else
                {//the class is null
                   classno = -1;
                }


                int subjectno = 0;

                if ((arguments.length > 0) && (arguments[0] != null))
                {
                    subjectno= castIdentifier( context.get( arguments[0]));
                }
                else
                {
                    throw new JspTagException("1 argument required: subject person ID");
                }
                //System.out.println( subjectno);
                //System.out.println( usernode.getNumber());

                boolean isTeacherOf= ClassRoom.isClassMember(usernode, subjectno, classno, educationno, "teacher", cloud)
                || ClassRoom.isWorkgroupMember(usernode, subjectno, classno, educationno, "teacher", cloud);

                if (operation.equals( "isTeacherOf"))
                {
                    mayvalue[0]= isTeacherOf;
                }
                else
                {
                    mayvalue[0]= (subjectno == usernode.getNumber()) || isTeacherOf;
                }
            }
            return mayvalue;
        }
        catch (JspTagException e) {
            //             throw new JspTagException(e.getMessage());
            System.err.println( "may: education: " + operation + " "  +e.getMessage());
            return new boolean[] {false, false};
        }
    }

    // javadoc inherited
    public String getValue(String setting, Cloud cloud, Map context, String[] arguments) {
        if ("showlo".equals(setting)) {
            String lo = arguments[0];
            int res = showLo(cloud, context, lo);
            if (res > 0) {
                for (int i=0; i<interestedComponents.size(); i++) {
                    Component comp = (Component)interestedComponents.get(i);
                    // delegate the call to the component. We give the current 'max' level as an extra
                    // argument, so that no useless checking needs to be done
                    String value = comp.getValue("showlo", cloud, context, new String[]{lo, ""+res});
                    if (!"".equals(value)) {
                        res = Math.min(Integer.parseInt(value), res);
                    }
                    if (res == 0)
                        break;
                }
            }
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
        String classno = (String)context.get("class");
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

        for (int i=0; i<events.size(); i++) {
            long start = events.getNode(i).getLongValue("start");
            long stop = events.getNode(i).getLongValue("stop");
            if (currenttime > (classStart + start) && currenttime < (classStart + stop)) {
                return 2;
            }
        }

        return 0;
    }
}
