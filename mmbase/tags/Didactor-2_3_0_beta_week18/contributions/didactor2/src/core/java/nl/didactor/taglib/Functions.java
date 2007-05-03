package nl.didactor.taglib;

import java.util.*;
import javax.servlet.jsp.*;

import nl.didactor.component.Component;
import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Provide some Didactor specify functionality as EL-functions too.
 * 
 * @author Michiel Meeuwissen
 * @version $Id: Functions.java,v 1.2 2007-05-02 14:03:23 michiel Exp $
 * @since Didactor-2.3
 */
public class Functions {
    private static final Logger log = Logging.getLoggerInstance(Functions.class);


    public static String translate(PageContext pageContext, String key) {
        TranslateTable.init(pageContext);
        Locale loc = (Locale) pageContext.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request", PageContext.REQUEST_SCOPE);
        
        TranslateTable tt = new TranslateTable(loc == null ? null : loc.toString());
        return tt.translate(key);
    }
    public static Object setting(PageContext pageContext, String component, String setting) throws JspTagException {
        Component comp = Component.getComponent(component);
        Cloud cloud = (Cloud) pageContext.getAttribute(CloudTag.KEY, CloudTag.SCOPE);
        ContextTag pageContextTag = (ContextTag) pageContext.getAttribute(ContextTag.CONTEXTTAG_KEY);
        return comp.getSetting(setting, cloud, pageContextTag.getContextProvider().getContextContainer());
    }

}
