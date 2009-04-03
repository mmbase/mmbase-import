package nl.didactor.taglib;

import java.io.IOException;
import java.util.*;
import java.text.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import javax.servlet.Servlet;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import nl.didactor.component.Component;

/**
 * GetValueTag: retrieve a value for a component
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class GetValueTag extends CloudReferrerTag implements Writer { 
    private static final Logger log = Logging.getLoggerInstance(GetValueTag.class);
    private String component;
    private String name;
    private String[] arguments = new String[0];

    /**
     * Set the value for the 'component' argument of the GetValue tag
     * @param component Component value
     */
    public void setComponent(String component) {
        this.component = component;
    }

    /**
     * Set the value for the 'name' argument of the GetValue tag
     * @param name Value name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the value for the 'arguments' argument of the GetValue tag
     * @param arguments The optional arguments
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
     * Retrieve the value for the variable based on:
     * <ul>
     *   <li>Given component name (is used to find the correct class to get the value)</li>
     *   <li>Given name</li>
     *   <li>Context in which this tag is called (values are passed to the component)</li>
     *   <li>Cloud in which this value tag is used</li>
     * </ul>
     */
    public int doStartTag() throws JspTagException {
        Component comp = Component.getComponent(component);
        if (comp == null) {
            return SKIP_BODY;
        }

        String value = comp.getValue(name, getCloudVar(), getContextProvider().getContextContainer(), arguments);
        helper.setValue(value);
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;    
    }
    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    /**
     *
     **/
    public int doEndTag() throws JspTagException {
        helper.doEndTag();
        return super.doEndTag();
    }

}
