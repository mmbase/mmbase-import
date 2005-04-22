package nl.didactor.component.education;
import nl.didactor.component.Component;
import org.mmbase.bridge.Cloud;
import java.util.Map;
import java.util.Vector;
import java.lang.Integer;
import org.mmbase.module.core.*;
import javax.servlet.jsp.JspTagException;
import nl.didactor.util.ClassRoom;

public class DidactorEducation extends Component {
    private Vector interestedComponents = new Vector();

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
        return "DidactorEducation";
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
        try {
            if (operation.equals( "isSelfOrTeacherOf")
            ||operation.equals( "isTeacherOf")) {
    
                Object user = context.get( "user");
                Object educationobj= context.get( "education");
                Object classobj= context.get( "class");

                MMObjectNode usernode = MMBase.getMMBase().getBuilder("people").getNode(  ((Integer)user).intValue());
                if (usernode == null) {
                    throw new JspTagException("User with number '" + user + "' not found");
                }
                int educationno= castIdentifier( educationobj);  
                int classno=    castIdentifier( classobj); 
                int subjectno= 0;
                if ((arguments.length>0) && (arguments[0] != null)) {
                    subjectno= castIdentifier( context.get( arguments[0]));
                } else {
                    throw new JspTagException("1 argument required: subject person ID");
                }
                //System.out.println( subjectno);
                //System.out.println( usernode.getNumber());
                
                boolean isTeacherOf= ClassRoom.isClassMember(
                        usernode, subjectno, classno, educationno, "teacher", cloud
                ) || ClassRoom.isWorkgroupMember( 
                        usernode, subjectno, classno, educationno, "teacher", cloud
                );
                 
                if (operation.equals( "isTeacherOf")) {
                    mayvalue[0]= isTeacherOf;
                } else {
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
    public String getSetting(String setting, Cloud cloud, Map context, String[] arguments) {
        if ("showlo".equals(setting)) {
            String lo = arguments[0];
            int res = showLo(cloud, context, lo);
            if (res > 0) {
                for (int i=0; i<interestedComponents.size(); i++) {
                    Component comp = (Component)interestedComponents.get(i);
                    // delegate the call to the component. We give the current 'max' level as an extra
                    // argument, so that no useless checking needs to be done
                    String value = comp.getSetting("showlo", cloud, context, new String[]{lo, ""+res});
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
     * Possible values:
     * <ul>
     *   <li>0, Do not show this object
     *   <li>1, Show the object, but 'grayed out'.
     *   <li>2, Show the object
     * </ul>
     */
    private int showLo(Cloud cloud, Map context, String lo) {
        return 2;
    }
}
