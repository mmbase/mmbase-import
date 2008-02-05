package nl.didactor.taglib;

import java.io.IOException;
import java.util.*;
import java.text.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import javax.servlet.Servlet;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.util.Referids;
import org.mmbase.security.Action;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.functions.Parameters;

import nl.didactor.component.Component;
import java.util.StringTokenizer;

/**
 * MayTag: retrieve a security privilege for a component
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class MayTag extends CloudReferrerTag implements Condition {
    private static final Logger log = Logging.getLoggerInstance(MayTag.class);
    private Attribute component = Attribute.NULL;
    private Attribute  action   = Attribute.NULL;
    private Attribute referids =  Attribute.NULL;
    private Attribute inverse =  Attribute.NULL;

    /**
     * Set the value for the 'component' argument of the May tag
     * @param component Component value
     */
    public void setComponent(String component) throws JspException {
        this.component = getAttribute(component);
    }

    /**
     * Set the value for the 'action' argument of the May tag
     * @param action Action identifier
     */
    public void setAction(String action) throws JspTagException {
        this.action = getAttribute(action);
    }

    /**
     */
    public void setReferids(String arguments) throws JspTagException {
        this.referids = getAttribute(arguments);
    }
    public void setInverse(String i) throws JspTagException {
        this.inverse = getAttribute(i);
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
        Component comp = Component.getComponent(component.getString(this));
        if (comp == null) {
            log.error("Component "+ component + "cannot be found" );
            return SKIP_BODY;
        }

        boolean[] value;

        Action a = comp.getActions().get(action.getString(this));
        if (a == null) {
            throw new JspTagException("No such action " + action + " defined for component " + comp + ". The possible actions are " + comp.getActions());
        }
        Parameters params = a.createParameters();
        fillStandardParameters(params);
        DidactorHelper.fillStandardParameters(this, params);

        Map m = Referids.getReferids(referids, this);
        for (Map.Entry entry : ((Collection<Map.Entry>) m.entrySet())) {
            String key = (String) entry.getKey();
            params.set(key, entry.getValue());
        }
        log.debug("Checking " + a + " with " + params);
        params.checkRequiredParameters();
        value = comp.may(getCloudVar(), a, params);

        if (value[0] != inverse.getBoolean(this, false)) {
            return EVAL_BODY;
        } else {
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
