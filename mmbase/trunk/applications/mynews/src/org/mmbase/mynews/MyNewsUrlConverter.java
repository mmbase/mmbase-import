/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.mynews;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.framework.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * The UrlConverter that can filter and create urls for the MyNews example application.
 * Links start with '/magazine/'.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: MyNewsUrlConverter.java,v 1.7 2007-11-16 13:47:22 michiel Exp $
 * @since MMBase-1.9
 */
public class MyNewsUrlConverter implements UrlConverter {
    private static final Logger log = Logging.getLoggerInstance(MyNewsUrlConverter.class);

    private final Framework framework;

    public MyNewsUrlConverter(Framework fw) {
        framework = fw;
    }

    public Parameter[] getParameterDefinition() {
        return new Parameter[] {};
    }

    public StringBuilder getUrl(String path,
                                Map<String, Object> parameters,
                                Parameters frameworkParameters, boolean escapeAmps) {

        Block block = framework.getBlock(frameworkParameters);
        if (block == null) {
            return null;
        } else {
            if (block.getComponent().getName().equals("mynews")) {
                Object n = parameters.get("n");
                return new StringBuilder("/magazine/" + (block.getName().equals("article") ? n : ""));
            } else {
                return null;
            }
        }
    }

    public StringBuilder getInternalUrl(String page, Map<String, Object> params, Parameters frameworkParameters) {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        if (page == null) throw new IllegalArgumentException();
        if (page.startsWith("/magazine")) {
            String sp = FrameworkFilter.getPath(request);
            String[] path = sp.split("/");
            if (log.isDebugEnabled()) {
                log.debug("Going to filter " + Arrays.asList(path));
            }
            if (path.length >= 2) {
                StringBuilder result = new StringBuilder("/mmbase/components/mynews/render.jspx");
                assert path[0].equals("");
                assert path[1].equals("magazine");
                if (path.length == 2) {
                    return result;
                } else {
                    result.append("?block=article&n=" + path[2]);
                    return result;
                }
            } else {
                log.debug("path length " + path.length);
                return null;
            }
        } else {
            log.debug("Leaving unfiltered");
            return null;
        }
    }

    public String toString() {
        return "/magazine/";
    }


}
