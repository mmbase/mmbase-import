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
 * The URLConverter which deals with urls in /mmbase
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: MyNewsUrlConverter.java,v 1.2 2007-07-07 07:22:34 michiel Exp $
 * @since MMBase-1.9
 */
public class MyNewsUrlConverter implements UrlConverter {
    private static final Logger log = Logging.getLoggerInstance(MyNewsUrlConverter.class);

    private final Framework framework;

    public MyNewsUrlConverter(Framework fw) {
        framework = fw;
    }

    public StringBuilder getUrl(String path,
                                Collection<Map.Entry<String, Object>> parameters,
                                Parameters frameworkParameters, boolean escapeAmps) {
        if (log.isDebugEnabled()) {
            log.debug(" framework parameters " + frameworkParameters);
        }
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        State state = State.getState(request, false);
        // BasicFramework always shows only one component
        Component component  = ComponentRepository.getInstance().getComponent(frameworkParameters.get(BasicFramework.COMPONENT));
        boolean explicitComponent = component != null;
        if (state != null && state.isRendering()) {
            component = state.getBlock().getComponent();
        } else {
            log.debug("No state object found");
        }

        if (component == null || !component.getName().equals("mynews")) {
            log.debug("Not currently rendering mynews component");
            return null;
        } else {
            // can explicitely state new block by either 'path' (of mm:url) or framework parameter  'block'.
            boolean filteredMode =
                (state == null && explicitComponent) ||
                request.getServletPath().startsWith("/magazine");

            log.debug("Using " + component);

            Block block;
            String blockParam = frameworkParameters.get(BasicFramework.BLOCK);
            if (blockParam != null) {
                if (path != null && ! "".equals(path)) throw new IllegalArgumentException("Cannot use both 'path' argument and 'block' parameter");
                block = component.getBlock(blockParam);
            } else {
                block = component.getBlock(path);
                if (block == null && path != null && ! "".equals(path)) {
                    log.debug("No block '" + path + "' found");
                    return null;
                }

            }
            if (block == null && state != null) {
                block = state.getRenderer().getBlock();
            }

            if (block == null) {
                log.debug("Cannot determin a block for '" + path + "' suppose it a normal link");
                if (filteredMode) {
                    return null;
                } else {
                    throw new IllegalArgumentException("not such block '" + path + " for component " + block);
                }
            }

            Object n = null;
            for (Map.Entry e : parameters) {
                if (e.getKey().equals("n")) n = e.getValue();
            }

            return new StringBuilder("/magazine/" + (block.getName().equals("article") ? n : ""));
        }
    }

    public StringBuilder getInternalUrl(String page, Collection<Map.Entry<String, Object>> params, Parameters frameworkParameters) {
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
