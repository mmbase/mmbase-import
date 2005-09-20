package nl.didactor.taglib;

import java.io.IOException;
import java.util.*;
import java.text.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import javax.servlet.Servlet;
import org.mmbase.bridge.jsp.taglib.*;
import nl.didactor.component.Component;
import java.util.StringTokenizer;

/**
 * MayTag: retrieve a security privilege for a component
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class MayTag extends CloudReferrerTag { 
    private String component;
    private String action;
    private String[] arguments = new String[0];

    /**
     * Set the value for the 'component' argument of the May tag
     * @param component Component value
     */
    public void setComponent(String component) {
        this.component = component;
    }

    /**
     * Set the value for the 'action' argument of the May tag
     * @param action Action identifier
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Set the value for the 'arguments' argument of the May tag, Comma seperated.
     * @param arguments Optional arguments
     */
    public void setArguments(String arguments) {
        StringTokenizer st = new StringTokenizer(arguments, ",");
        if (st.countTokens() > 0) {
            this.arguments = new String[st.countTokens()];
            for (int i=0; i<st.countTokens(); i++) {
                this.arguments[i] = st.nextToken();
            }
        }
    }

    /**
     * Decide whether or not the body of the tag should be executed, based on: 
     * <ul>
     *   <li>Given component name (is used to find the correct class to handle the setting)</li>
     *   <li>Given action name</li>
     *   <li>Optional given arguments</li>
     *   <li>Context in which this tag is called (values are passed to the component)</li>
     *   <li>Cloud in which this setting tag is used</li>
     * </ul>
     */
    public int doStartTag() throws JspTagException {
        Component comp = Component.getComponent(component);
        if (comp == null) {
            System.err.println( "Component "+ component + "cannot be found" );
            return SKIP_BODY;
        }

        boolean[] value = new boolean[0];
        
        try {
            value = comp.may(action, getCloudVar(), getContextProvider().getContextContainer(), arguments);
        } catch (IllegalArgumentException e) {
            throw new JspTagException(e.getMessage());
        }
    
        if (value[0]) {
            return EVAL_BODY;
        } else {
            try {
                pageContext.getOut().print( "<h1>Permission denied!</h1>");
            }
            catch (java.io.IOException e) {
               System.err.println( "di:may IO error:" + e.getMessage());
            }
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
