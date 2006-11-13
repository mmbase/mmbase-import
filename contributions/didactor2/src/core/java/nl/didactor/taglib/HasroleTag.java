package nl.didactor.taglib;

import java.io.IOException;
import java.util.*;
import java.text.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import javax.servlet.Servlet;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import nl.didactor.component.Component;
import nl.didactor.security.*;
import nl.didactor.util.ClassRoom;
/**
 * HasroleTag: retrieve a setting for a component
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class HasroleTag extends CloudReferrerTag {
    private final static Logger log = Logging.getLoggerInstance(HasroleTag.class);

    private String role;
    private String inverse;
    private String referid;
    private String education;

    /**
     * Set the value for the 'role' argument of the Hasrole tag
     * @param role role name
     */
    public void setRole(String role) {
        this.role = role;
    }


    /**
     * Set the value for the 'inverse' argument of the Hasrole tag
     * @param inverse whether or not we need to inverse the result
     */
    public void setInverse(String inverse) {
        this.inverse = inverse;
    }
    /**
     * Set the value for the 'inverse' argument of the Hasrole tag
     * @param inverse whether or not we need to inverse the result
     */

    public void setReferid(String referid) {
        this.referid = referid;
    }

    /**
     * Set the value for the 'education' argument of the Hasrole tag
     * @param inverse the education for which the user has the role
     */

    public void setEducation(String education) {
        this.education = education;
    }

    /**
     * Execute the body of the tag if the current user has the given role.
     */
    public int doStartTag() throws JspTagException {
        //Get User
        // default: logged in user
        String userid= referid;
        if (userid == null) {
            userid= "user";
        }
        Object user = getContextProvider().getContextContainer().get(userid);
        if (user == null) {
            throw new JspTagException("Context variable with id '" + userid + "' not found");
        }


        boolean inv = false;
        if (inverse != null && !"".equals(inverse)) {
            inv = "true".equalsIgnoreCase(inverse);
        }
        String number = "" + user;
        if ("0".equals(number)) return inv ? EVAL_BODY : SKIP_BODY;

        MMObjectNode usernode = MMBase.getMMBase().getBuilder("people").getNode("" + user);
        if (usernode == null) {
            throw new JspTagException("User with number '" + user + "' not found");
        }
        // Get Education no
       int educationno= 0;
       String educationStr= education;
       if ((educationStr == null) || "".equals( educationStr) ) {
           // if education in tag
           educationStr= "education";
       }
       // obtain education via context
       Object in_education= getContextProvider().getContextContainer().get( educationStr);
       if (in_education != null) {
           if (in_education instanceof Integer) {
               educationno= ((Integer) in_education).intValue();
           } else if (in_education instanceof String) {
               educationno= Integer.parseInt( (String) in_education);
           } else {
               throw new JspTagException( "Education of unknown type");
           }
        }
        if (role == null) {
            throw new JspTagException( "No role defined");
        }


        try {

            if (ClassRoom.hasRole( usernode, role, educationno, getCloudVar())) {
                return inv?SKIP_BODY:EVAL_BODY;
            } else {
                return inv?EVAL_BODY:SKIP_BODY;
            }
        } catch (JspTagException e) {
            //             throw new JspTagException(e.getMessage());
            log.error("hasrole: " + e.getMessage());
            return SKIP_BODY;
        }
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
        return SKIP_BODY;
    }
}
