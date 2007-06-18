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
 * The framework that does nothing, besides adding the block-parameters to the URL. No support for
 * conflicting block parameters.
 *
 * @author Michiel Meeuwissen
 * @version $Id: BasicUrlConverter.java,v 1.1 2007-06-18 22:18:24 michiel Exp $
 * @since MMBase-1.9
 */
public class BasicUrlConverter implements UrlConverter {
    private static final Logger log = Logging.getLoggerInstance(BasicUrlConverter.class);


    private static final CharTransformer paramEscaper = new Url(Url.ESCAPE);

    protected final BasicFramework framework;

    BasicUrlConverter(BasicFramework parent) {
        framework = parent;
    }

    /**
     * General utility function to create an Url
     * @param page servletPath
     * @param params The query to be added
     * @param req A request object is needed to determin context-paths and so on.
     * @param writeamp Wheter amperstands must be XML-escaped. Typically needed if the URL is used
     * in (X)HTML.
     */
    public static StringBuilder getUrl(String page, Collection<Map.Entry<String, Object>> params, HttpServletRequest req, boolean escapeamp) {
        StringBuilder show = new StringBuilder();
        if (escapeamp) {
            page = page.replaceAll("&", "&amp;");
        }
        if (page.equals("")) { // means _this_ page
            String requestURI = req.getRequestURI();
            if (requestURI.endsWith("/")) {
                page = ".";
            } else {
                page = new File(requestURI).getName();
            }
        }
        show.append(page);

        if (params != null && ! params.isEmpty()) {
            // url is now complete up to query string, which we are to construct now
            String amp = (escapeamp ? "&amp;" : "&");
            String connector = (show.indexOf("?") == -1 ? "?" : amp);

            Writer w = new StringBuilderWriter(show);
            for (Map.Entry<String, ? extends Object> entry : params) {
                Object value = entry.getValue();
                if (value != null && Casting.isStringRepresentable(value.getClass())) { // if not string representable, that suppose it was an 'automatic' parameter which does need presenting on url
                    if (value instanceof Iterable) {
                        for (Object v : (Iterable<?>) value) {
                            show.append(connector).append(entry.getKey()).append("=");
                            paramEscaper.transform(new StringReader(Casting.toString(v)), w);
                            connector = amp;
                        }
                    } else {
                        show.append(connector).append(entry.getKey()).append("=");
                        paramEscaper.transform(new StringReader(Casting.toString(value)), w);
                        connector = amp;
                    }
                }
            }
        }
        return show;
    }
    
    public StringBuilder getUrl(String path, Collection<Map.Entry<String, Object>> parameters,
                                Parameters frameworkParameters, boolean escapeAmps) {
        if (log.isDebugEnabled()) {
            log.debug(" framework parameters " + frameworkParameters);
        }
        HttpServletRequest req = frameworkParameters.get(Parameter.REQUEST);

        // BasicFramework always shows only one component
        Component component  = ComponentRepository.getInstance().getComponent(frameworkParameters.get(BasicFramework.COMPONENT));
        if (component == null) {
            // if no explicit component specified, suppose current component, if there is one:
            BasicFramework.State state = framework.getState(req, false);
            if (state != null) {
                component = state.getRenderer().getBlock().getComponent();
            }
        }
        
        if (component == null) {
            log.debug("Not currently rendering a component");
            // cannot be handled by Framework
            StringBuilder sb = BasicUrlConverter.getUrl(path, parameters, req, escapeAmps);
            return sb;
        } else {
            // can explicitely state new block by either 'path' (of mm:url) or framework parameter  'block'.

            if ("".equals(path)) path = null; 
            if (path == null) path = frameworkParameters.get(BasicFramework.BLOCK);

            BasicFramework.State state = framework.getState(req, false);

            // if no explicit block, then this will be an URL to the current block.
            Block block = path != null ? component.getBlock(path) : state.getRenderer().getBlock();

            if (log.isDebugEnabled()) {
                log.debug("Rendering component " + component + " generating URL to " + block);
            }

            Parameters blockParameters = block.createParameters();

            Map<String, Object> map = new TreeMap<String, Object>();
            for (Object e : req.getParameterMap().entrySet()) {
                Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) e;
                String k = entry.getKey();
                if (k.equals(BasicFramework.CATEGORY.getName())) continue; // already in servletpath
                if (k.equals(BasicFramework.BLOCK.getName())) continue; // already in servletpath
                if (k.equals(BasicFramework.COMPONENT.getName())) continue; // already in servletpath
                map.put(k, entry.getValue()[0]);
            }
            for (Map.Entry<String, Object> entry : parameters) {
                blockParameters.set(entry.getKey(), entry.getValue());                
            }
            map.putAll(state.getMap(blockParameters.toMap()));

            String category = req.getParameter(BasicFramework.CATEGORY.getName());
            if (category == null) {
                Block.Type[] classification = block.getClassification();
            }
            String page = "/mmbase/" + category + "/" + component.getName() + "/" + block.getName();
            StringBuilder sb = BasicUrlConverter.getUrl(page, map.entrySet(), req, escapeAmps);
            return sb;            

        }
    }
    public StringBuilder getInternalUrl(String page, Collection<Map.Entry<String, Object>> params, Parameters frameworkParameters) {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        if (page.startsWith("/mmbase")) {
            String sp = request.getServletPath();
            String[] path = sp.split("/");
            log.debug("Going to filter " + Arrays.asList(path));           
            if (path.length >= 3) { 
                assert path[0].equals("");
                assert path[1].equals("mmbase");
                String category = path[2];
                StringBuilder url = new StringBuilder("/mmbase/admin/index.jsp?category=" + category);
                if (path.length == 3) return url;
                
                Component comp = ComponentRepository.getInstance().getComponent(path[3]);
                if (comp == null) {
                    log.debug("No such component, ignoring this too");
                    return BasicUrlConverter.getUrl(page, params, request, false);
                }
                url.append("&component=" + comp.getName());

                if (path.length == 4) return url;

                Block block = comp.getBlock(path[4]);
                log.debug("Will try to display " + block);
                if (block == null) {
                    throw new RuntimeException("No block " + path[4] + " in component " + path[3]);
                }
                url.append("&block=" + block.getName());
                log.debug("internal URL " + url);
                return url;
            } else {
                log.debug("path length " + path.length);
                return BasicUrlConverter.getUrl(page, params, request, false);
            }
        } else {            
            return BasicUrlConverter.getUrl(page, params, request, false);
        }
    }

}
