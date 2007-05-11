package nl.didactor.taglib;

import java.io.IOException;
import java.util.*;
import java.text.*;
import java.lang.Integer;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import javax.servlet.Servlet;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.StringSplitter;
import org.mmbase.bridge.jsp.taglib.util.ContextContainer;
import nl.didactor.component.Component;
import nl.didactor.security.*;
import nl.didactor.util.ClassRoom;
/**
 * entityTag: retrieve entity data. 
 * 
 *
 * @author UNKNOWN
 * @javadoc What kind of 'entities'?
 * @version $Id: EntityTag.java,v 1.4 2007-04-24 12:12:11 michiel Exp $
 */
public class EntityTag extends CloudReferrerTag { 
    private static final Logger log = Logging.getLoggerInstance(EntityTag.class);

    private String name;
    private String user;
    private String education;
    private String classId;
    private String workgroup;
    private String role;
    private java.util.Iterator it= null;
    
    private String var;
    /**
     * Set the value for the 'role' argument of the entity tag
     * @param role role name
     */
    public void setName( String name) {
        this.name= name;
    }
    
    /**
     * Set the value for the 'user' argument of the entity tag
     * @param user , if not set, user logged in
     */
    public void setUser( String user) {
        this.user= user;
    }

    /**
     * Set the value for the 'role' argument of the entity tag
     * @param role role name
     */
    public void setRole( String role) {
        this.role= role;
    }

    /**
     * Set the value for the 'education' argument of the entity tag
     * @param inverse whether or not we need to inverse the result
     */
    public void setEducation( String education) {
        this.education= education;
    }
    /**
     * Set the value for the 'workgroup' argument of the entity tag
     * @param inverse whether or not we need to inverse the result
     */

    public void setWorkgroup( String workgroup) {
        this.workgroup= workgroup;
    }

    /**
     * Set the value for the 'class' argument of the entity tag
     * @param the class for which the user has the role
     */

    public void setClassId( String classId) {
        this.classId= classId;
    }
    public int resolveIdentifier( String reference, ContextContainer container)
        throws JspTagException
    {
        int value= 0;
//      obtain education via context
        Object object= container.get( reference);
        if (object != null) {
            if (object instanceof Integer) {
                value= ((Integer) object ).intValue();    
            } else if (object instanceof String) {
                value= Integer.parseInt( (String) object);
            } else {
                throw new JspTagException( reference + " has unknown type.");
            }
         }
         return value;
    }
     
    /**
     * Execute the body of the tag if the current user has the given role.
     */
    public int doStartTag() throws JspTagException {
        int ret_value= EVAL_BODY_INCLUDE;
        
        //Get User        
        // default: logged in user 
        String userid= user;
        if (userid == null) {
            userid= "user";
        }
        Object user = getContextProvider().getContextContainer().get( userid);
        if (user == null) {
            throw new JspTagException("Context variable with id '" + userid + "' not found");
        }
        MMObjectNode usernode = MMBase.getMMBase().getBuilder("people").getNode("" + user);
        if (usernode == null) {
            throw new JspTagException("User with number '" + user + "' not found");
        }
        // Get Class no
        String classStr= classId;
        if ((classStr == null) || "".equals( classStr) ) {
            classStr= "class";
        }
        int classno= resolveIdentifier( classStr, getContextProvider().getContextContainer());

        // Get Education no
        String educationStr= education;
        if ((educationStr == null) || "".equals( educationStr) ) {
            educationStr= "education";
        }
        int educationno= resolveIdentifier( educationStr, getContextProvider().getContextContainer());
        
        this.var= "entity" + name.subSequence(0,4) + (new Integer( usernode.getNumber())).toString();
        
        try {
            if (name.equals( "workgroupmembers")) {
                List value= ClassRoom.getWorkgroupMembers( usernode, classno, educationno, role, getCloudVar());
                it = value.iterator();
                if (it.hasNext()) {
                    pageContext.setAttribute( var, it.next());
                } else {
                    pageContext.setAttribute( var, null);
                    ret_value= SKIP_BODY;
                }
            } else if (name.equals( "workgroupmember")) {
                Object object= pageContext.getAttribute( var);
                if (object== null) {
                    pageContext.getOut().print( "exception");
                    throw new JspTagException( "No parent workgroupmembers");
                } else {
                    pageContext.getOut().print( object.toString());
                }
            } else if (name.equals( "classmembers")) {
                List value= ClassRoom.getWorkgroupMembers( usernode, classno, educationno, role, getCloudVar());
                it = value.iterator();
                if (it.hasNext()) {
                    pageContext.setAttribute( var, it.next());
                } else {
                    ret_value= SKIP_BODY;
                }
            } else if (name.equals( "classmember")) {
                pageContext.getOut().print( pageContext.getAttribute( var).toString());
            }
        } catch (JspTagException e) {
            log.error( "di:entity: IO " + e.getMessage());
            return SKIP_BODY;
         }
        catch (java.io.IOException e) {
            log.error( "di:entity: IO " + e.getMessage());
            return SKIP_BODY;
        }
        return ret_value;
    }

    // Code copied from MMBase 'CloudTag' code. Don't know why this is the case
    // but apparently there is no body written if this code is not included here
    // if EVAL_BODY == EVAL_BODY_BUFFERED
    public int doAfterBody() throws JspTagException {

        if (EVAL_BODY == EVAL_BODY_BUFFERED) {
            try {
                if (bodyContent != null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch (IOException ioe) {
                throw new TaglibException(ioe);
            }
        }
        if (it== null) {
            return SKIP_BODY;
        }

        if (it.hasNext()) {
            pageContext.setAttribute( var, it.next());
            return EVAL_BODY_AGAIN;
        } else {
            return SKIP_BODY;
        }
    }
}
