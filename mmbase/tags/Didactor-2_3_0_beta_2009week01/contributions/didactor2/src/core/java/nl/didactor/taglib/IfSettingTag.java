package nl.didactor.taglib;

import java.io.IOException;
import java.util.*;
import java.text.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import javax.servlet.Servlet;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.ContextContainer;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import nl.didactor.component.Component;

/**
 * IfSettingTag: retrieve a setting for a component
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class IfSettingTag extends CloudReferrerTag { 
    private static Logger log = Logging.getLoggerInstance(IfSettingTag.class.getName());
    private String component;
    private String setting;
    private String value;
    private String inverse;

    /**
     * Set the value for the 'component' argument of the IfSetting tag
     * @param component Component value
     */
    public void setComponent(String component) {
        this.component = component;
    }

    /**
     * Set the value for the 'inverse' argument of the IfSetting tag
     * @param inverse Inverse value
     */
    public void setInverse(String inverse) {
        this.inverse = inverse;
    }

    /**
     * Set the value for the 'setting' argument of the IfSetting tag
     * @param setting Setting name
     */
    public void setSetting(String setting) {
        this.setting = setting;
    }

    /**
     * Set the value for the 'value' argument of the IfSetting tag
     * @param value Setting value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Retrieve the value for the setting based on:
     * <ul>
     *   <li>Given component name (is used to find the correct class to handle the setting)</li>
     *   <li>Given setting name</li>
     *   <li>Context in which this tag is called (values are passed to the component)</li>
     *   <li>Cloud in which this setting tag is used</li>
     * </ul>
     */
    public int doStartTag() throws JspTagException {
        if (setting == null) {
            throw new JspTagException("ifsetting may not be called without a 'setting' parameter");
        }
        if (component == null) {
            throw new JspTagException("ifsetting may not be called without a 'component' parameter");
        }
        boolean isInverse = false;
        if (inverse != null && "true".equals(inverse)) {
            isInverse = true;
        }

        Object retval = getSetting(setting, component, getCloudVar(), getContextProvider().getContextContainer());
        if (value == null && !(retval instanceof Boolean)) {
            throw new JspTagException("the 'value' parameter may only be ommitted for boolean parameters");
        }

        boolean showBody = false;
        if (retval instanceof Boolean) {
            showBody = ((Boolean)retval).booleanValue();
        } else if (retval instanceof String) {
            showBody = value.equals((String)retval);
        } else if (retval instanceof Integer) {
            int givenValue = Integer.parseInt(value);
            int retValue = ((Integer)retval).intValue();
            showBody = (givenValue == retValue);
        }

        if (showBody && isInverse || (!showBody && !isInverse)) {
            return SKIP_BODY;
        } else {
            return EVAL_BODY_INCLUDE;
        }
    }

    public void release() {
        value = null;
        component = null;
        inverse = null;
        setting = null;
    }


    public Object getSetting(String settingName, String componentName, Cloud cloud, Map context) {
        Component comp = Component.getComponent(component);
        // Hardcoded setting: 'installed', can be used to test if a component is installed
        if ("installed".equals(setting)) {
            return new Boolean(comp != null);
        }

        Object retval = comp.getSetting(settingName, cloud, context);
        if (log.isDebugEnabled()) {
            log.debug("" + componentName + "." + settingName + " = '" + retval + "' (" + retval.getClass() + ")");
        }
        return retval;
    }
}
