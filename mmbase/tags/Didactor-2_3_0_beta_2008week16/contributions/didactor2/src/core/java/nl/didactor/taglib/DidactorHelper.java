package nl.didactor.taglib;

import java.util.*;
import javax.servlet.*;

import nl.didactor.component.Component;
import org.mmbase.util.functions.Parameters;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Provide some Didactor specify functionality as EL-functions too.
 *
 * @author Michiel Meeuwissen
 * @version $Id: DidactorHelper.java,v 1.4 2008-02-05 15:05:36 michiel Exp $
 * @since Didactor-2.3
 */
public class DidactorHelper {
    private static final Logger log = Logging.getLoggerInstance(DidactorHelper.class);


    public static void fillStandardParameters(ContextReferrerTag tag, Parameters params) {
        ServletRequest request = tag.getPageContext().getRequest();
        params.setAutoCasting(true);
        params.setIfDefined(Component.EDUCATION, request.getAttribute("education"));
        params.setIfDefined(Component.CLASS, request.getAttribute("class"));
    }

}
