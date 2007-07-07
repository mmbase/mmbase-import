/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import java.util.*;
import org.mmbase.util.*;
import java.io.*;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.util.functions.*;
import org.mmbase.util.transformers.Url;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Cloud;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

/**
 * The URLConverter which deals with urls in /mmbase
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: MMBaseUrlConverter.java,v 1.4 2007-07-07 07:50:20 michiel Exp $
 * @since MMBase-1.9
 */
public class MMBaseUrlConverter implements UrlConverter {
    private static final Logger log = Logging.getLoggerInstance(MMBaseUrlConverter.class);

    private final Framework framework;

    protected String dir = "/mmbase/";

    public MMBaseUrlConverter(Framework fw) {
        framework = fw;
    }

    public void setDir(String d) {
        dir = d;
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

        if (component == null) {
            // if no explicit component specified, suppose current component, if there is one:
            if (state != null && state.isRendering()) {
                component = state.getBlock().getComponent();
            } else {
                log.debug("No state object found");
            }
        }

        if (component == null) {
            log.debug("Not currently rendering a component, nor a component was specified generating url to " + path);
            return null;
        } else {

            // can explicitely state new block by either 'path' (of mm:url) or framework parameter  'block'.

            boolean filteredMode =
                (state == null && explicitComponent) ||
                request.getServletPath().startsWith(dir);


            Map<String, Object> map = new TreeMap<String, Object>();

            Block block;
            String blockParam = frameworkParameters.get(BasicFramework.BLOCK);
            if (blockParam != null) {
                if (path != null && ! "".equals(path)) throw new IllegalArgumentException("Cannot use both 'path' argument and 'block' parameter");
                block = component.getBlock(blockParam);
                if (! filteredMode) {
                    map.put(BasicFramework.BLOCK.getName(), block.getName());
                    map.put(BasicFramework.COMPONENT.getName(), component.getName());
                 }
            } else {
                block = component.getBlock(path);
                if (block != null) {
                    if (! filteredMode) {
                        path = null; // used, determin path with block name
                        map.put(BasicFramework.BLOCK.getName(), block.getName());
                        map.put(BasicFramework.COMPONENT.getName(), component.getName());
                    }
                } else {
                    // no such block
                    if (path != null && ! "".equals(path)) {
                        log.debug("No block '" + path + "' found");
                        return null;
                    }

                }
                if (block == null && state != null) {
                    block = state.getRenderer().getBlock();
                }
            }


            if (block == null) {
                log.debug("Cannot determin a block for '" + path + "' suppose it a normal link");
                if (filteredMode) {
                    return null;
                } else {
                    throw new IllegalArgumentException("not such block '" + path + " for component " + block);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Rendering component " + component + " generating URL to " + block + " State " + state);
            }
            boolean processUrl = Boolean.TRUE.equals(frameworkParameters.get("process"));
            if (processUrl) {
                // get current compoennts ids
                if (state != null) {
                    map.put("action", state.getId());
                } else {
                    log.warn("Needing state, but no state found ");
                }
            }

            Processor processor = (Processor) request.getAttribute(Processor.KEY);

            if (processor == null) {
                for (Object e : request.getParameterMap().entrySet()) {
                    Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) e;
                    String k = entry.getKey();
                    if (k.equals(BasicFramework.CATEGORY.getName())) continue; // already in  servletpath, or not relevant
                    if (filteredMode && k.equals(BasicFramework.BLOCK.getName())) continue; // already in servletpath
                    if (filteredMode && k.equals(BasicFramework.COMPONENT.getName())) continue; // already in servletpath
                    map.put(k, entry.getValue()[0]);
                }
            } else {
                log.debug("Now processing " + processor);
            }

            Parameters blockParameters = block.createParameters();
            for (Map.Entry<String, Object> entry : parameters) {
                blockParameters.set(entry.getKey(), entry.getValue());
            }
            if (state != null) {
                map.putAll(framework.prefix(state, blockParameters.toMap()));
            }

            String category = request.getParameter(BasicFramework.CATEGORY.getName());
            if (category == null) {
                Block.Type[] classification = block.getClassification();
            }
            boolean subComponent = state != null && state.getDepth() > 0;
            String page = filteredMode && ! subComponent ?
                dir + (category == null ? "_" : category) + "/" + component.getName() + "/" + block.getName() :
                path == null || subComponent ? FrameworkFilter.getPath(request) : path;
            StringBuilder sb = BasicUrlConverter.getUrl(page, map.entrySet(), request, escapeAmps);
            return sb;

        }
    }
    public StringBuilder getInternalUrl(String page, Collection<Map.Entry<String, Object>> params, Parameters frameworkParameters) {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        if (page == null) throw new IllegalArgumentException();
        if (page.startsWith(dir)) {
            String sp = FrameworkFilter.getPath(request);
            String[] path = sp.split("/");
            if (log.isDebugEnabled()) {
                log.debug("Going to filter " + Arrays.asList(path));
            }
            if (path.length >= 3) {
                assert path[0].equals("");
                //assert path[1].equals("mmbase");
                String category = path[2];
                if (! category.equals("_")) {
                    boolean categoryOk = false;
                    for (Block.Type rootType : ComponentRepository.getInstance().getBlockClassification("mmbase")[0].getSubTypes()) {
                        categoryOk = rootType.getName().equals(category);
                        if (categoryOk) break;
                    }
                    if (! categoryOk) {
                        log.debug("No such component clasification, ignoring this");
                        return null;
                    }
                }

                StringBuilder url = new StringBuilder("/mmbase/admin/index.jsp?category=" + category);
                if (path.length == 3) return url;

                Component comp = ComponentRepository.getInstance().getComponent(path[3]);
                if (comp == null) {
                    log.debug("No such component, ignoring this too");
                    return null;
                }
                url.append("&component=" + comp.getName());

                if (path.length == 4) return url;

                Block block = comp.getBlock(path[4]);
                if (log.isDebugEnabled()) {
                    log.debug("Will try to display " + block);
                }
                if (block == null) {
                    log.debug("No block " + path[4] + " in component " + path[3]);
                    return null;

                }
                url.append("&block=" + block.getName());
                log.debug("internal URL " + url);
                return url;
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
        return dir;
    }

}
