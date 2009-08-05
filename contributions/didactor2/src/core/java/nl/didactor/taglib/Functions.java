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
 * @version $Id: Functions.java,v 1.7 2008-10-30 12:40:03 michiel Exp $
 * @since Didactor-2.3
 */
public class Functions {
    private static final Logger log = Logging.getLoggerInstance(Functions.class);


    public static String translate(String key) {
        PageContext pageContext = ContextReferrerTag.getThreadPageContext();
        TranslateTable.init();
        Locale loc = (Locale) pageContext.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request", PageContext.REQUEST_SCOPE);

        TranslateTable tt = new TranslateTable(loc);
        return tt.translate(key);
    }
    public static Object setting(String component, String setting) throws JspTagException {
        PageContext pageContext = ContextReferrerTag.getThreadPageContext();
        Component comp = Component.getComponent(component);
        if (comp == null) throw new IllegalArgumentException("No such component '" + component + "'");
        Cloud cloud = (Cloud) pageContext.getAttribute(CloudTag.KEY, CloudTag.SCOPE);
        ContextTag pageContextTag = (ContextTag) pageContext.getAttribute(ContextTag.CONTEXTTAG_KEY);
        return comp.getSetting(setting, cloud, pageContextTag.getContextProvider().getContextContainer());
    }

    public static Component component(String component) throws JspTagException {
        return Component.getComponent(component);
    }

}
