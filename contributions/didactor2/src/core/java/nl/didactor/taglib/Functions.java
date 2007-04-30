package nl.didactor.taglib;

import java.util.*;
import javax.servlet.jsp.PageContext;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Provide some Didactor specify functionality as EL-functions too.
 * 
 * @author Michiel Meeuwissen
 * @version $Id: Functions.java,v 1.1 2007-04-30 16:49:53 michiel Exp $
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

}
