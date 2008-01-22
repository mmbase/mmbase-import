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
 * GetSettingTag: retrieve a setting for a component
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class GetSettingTag extends CloudReferrerTag implements Writer {
    private static final Logger log = Logging.getLoggerInstance(GetSettingTag.class);
    private String component;
    private String setting;

    /**
     * @param component Component value
     */
    public void setComponent(String component) {
        this.component = component;
    }

    /**
     * @param setting Setting name
     */
    public void setSetting(String setting) {
        this.setting = setting;
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
        Component comp = Component.getComponent(component);
        if (comp == null) {
            return SKIP_BODY;
        }

        Object value = comp.getSetting(setting, getCloudVar(), getContextProvider().getContextContainer());

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
