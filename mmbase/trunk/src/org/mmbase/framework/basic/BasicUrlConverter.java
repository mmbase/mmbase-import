/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework.basic;
import org.mmbase.framework.*;
import java.util.*;
import org.mmbase.util.*;
import java.io.*;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.util.functions.*;
import org.mmbase.util.transformers.Url;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Basic implementation of UrlConverter. Essential, should typically be
 * chained last in ChainedUrlConverter by the framework.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: BasicUrlConverter.java,v 1.8 2008-02-23 13:46:11 michiel Exp $
 * @since MMBase-1.9
 */
public final class BasicUrlConverter implements UrlConverter {
    private static final Logger log = Logging.getLoggerInstance(BasicUrlConverter.class);

    private static final CharTransformer paramEscaper = new Url(Url.ESCAPE);

    /**
     * General utility function to create an Url
     *
     * @param page servletPath
     * @param params The query to be added
     * @param req A request object is needed to determin context-paths and so on.
     * @param writeamp Wheter amperstands must be XML-escaped. Typically needed if the URL is used
     * in (X)HTML.
     */
    public static String getUrl(String page, Map<String, Object> params, HttpServletRequest req, boolean escapeamp) {
        if (log.isDebugEnabled()) {
            log.debug("(static) constructing " + page + params);
        }
        StringBuilder show = new StringBuilder();
        if (escapeamp && page != null) {
            page = page.replaceAll("&", "&amp;");
        }
        if (page == null || page.equals("")) { // means _this_ page
            String requestURI = req.getRequestURI();
            if (requestURI.endsWith("/")) {
                page = ".";
            } else {
                page = new File(requestURI).getName();
            }
            //page = FrameworkFilter.getPath(req); No good, it will produce something which starts
            //with /, which at least is not what mm:url wants in this case.

            log.debug("page not given, -> supposing it " + page);
        }
        show.append(page);

        if (params != null && ! params.isEmpty()) {
            // url is now complete up to query string, which we are to construct now
            String amp = (escapeamp ? "&amp;" : "&");
            String connector = (show.indexOf("?") == -1 ? "?" : amp);

            Writer w = new StringBuilderWriter(show);
            for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
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
        return show.toString();
    }

    private final BasicFramework framework;

    public BasicUrlConverter(BasicFramework fw) {
        framework = fw;

    }

    /**
     * @todo Actually these paremters are only added here, because this urlconverter is always in
     * BasicFramework. Actually BasicFramework should add them itself.
     */
    public Parameter[] getParameterDefinition() {
        return new Parameter[] {Parameter.REQUEST};
    }
    protected String getUrl(String path,
                            Map<String, Object> parameters,
                            Parameters frameworkParameters, boolean escapeAmps, boolean action) {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        State state = State.getState(request);
        Map<String, Object> map = new TreeMap<String, Object>();
        if (log.isDebugEnabled()) {
            log.debug("path " + path + " " + parameters + " " + frameworkParameters);
        }
        for (Map.Entry<String, Object> e : parameters.entrySet()) {
            map.put(e.getKey(), e.getValue());
        }
        if (state.isRendering()) {
            map = new TreeMap<String, Object>(framework.prefix(state, map));
            for (Object e : request.getParameterMap().entrySet()) {
                Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) e;

                String k = entry.getKey();
                // TODO: this is ad hoc (and incorrect if more than 9 blocks)
                if (k.startsWith("_" + state.getId())) {
                    // for this block, don't add that,
                    // because should be in parameters then
                    continue;
                }
                if (! map.containsKey(k)) {
                    map.put(k, entry.getValue()[0]);
                }
            }
            Block block = state.getBlock();
            if (log.isDebugEnabled()) {
                log.debug("current block " + block);
            }
            Block toBlock = block.getComponent().getBlock(path);
            if (toBlock != null) {
                path = null;
                if (! toBlock.equals(block)) {
                    log.debug("New block " + toBlock);
                    state.setBlock(map, toBlock);
                } else {
                    log.debug("staying at " + block);
                }
            } else {
                log.debug("No block '" + path + "' found");
            }
        }
        log.debug("constructing '" + path + "'" + map);
        return BasicUrlConverter.getUrl(path, map, request, escapeAmps);
    }

    public String getUrl(String path,
                            Map<String, Object> parameters,
                            Parameters frameworkParameters, boolean escapeAmps) {
        return getUrl(path, parameters, frameworkParameters, escapeAmps, false);
    }
    public String getProcessUrl(String path,
                            Map<String, Object> parameters,
                            Parameters frameworkParameters, boolean escapeAmps) {
        return getUrl(path, parameters, frameworkParameters, escapeAmps, true);
    }

    public String getInternalUrl(String page, Map<String, Object> params, Parameters frameworkParameters) {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        return BasicUrlConverter.getUrl(page, params, request, false);
    }

    public boolean equals(Object o) {
        return o instanceof BasicUrlConverter && ((BasicUrlConverter) o).framework.equals(framework);
    }
    public String toString() {
        return "COPY";
    }

}
