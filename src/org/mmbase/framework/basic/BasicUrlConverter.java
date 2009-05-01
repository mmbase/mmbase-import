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
import javax.servlet.http.HttpServletRequestWrapper;
import org.mmbase.util.functions.*;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Basic implementation of UrlConverter. Essential, should typically be
 * chained last in ChainedUrlConverter by the framework.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public final class BasicUrlConverter implements UrlConverter {
    private static final Logger log = Logging.getLoggerInstance(BasicUrlConverter.class);

    private static final CharTransformer PARAM_ESCAPER= new org.mmbase.util.transformers.Url(org.mmbase.util.transformers.Url.ESCAPE);




    /**
     * General utility function to create an Url
     *
     * @param page servletPath
     * @param params The query to be added
     * @param req A request object is needed to determine context-paths and so on.
     * @param escapeamp Whether ampersands must be XML-escaped. Typically needed if the URL is used
     * in (X)HTML.
     * @return An URL relative to the root of this web application (i.e. without a context path),
     */
    public static String getUrl(String page, Map<String, ?> params, HttpServletRequest req, boolean escapeamp) {
        if (log.isDebugEnabled()) {
            if (log.isTraceEnabled()) {
                log.trace("(static) constructing " + page + params + " because ", new Exception());
            } else {
                log.debug("(static) constructing " + page + params);
            }

        }
        req = getUserRequest(req);
        StringBuilder show = new StringBuilder();
        if (escapeamp && page != null) {
            page = page.replaceAll("&", "&amp;");
        }
        if (page == null || page.equals("")) { // means _this_ page
            page = FrameworkFilter.getPath(req); //No good, it will produce something which starts
            //with /, which at least is not what mm:url wants in this case.

            log.debug("page not given, -> supposing it " + page + " determined");
        }
        show.append(page);

        if (params != null && ! params.isEmpty()) {
            // url is now complete up to query string, which we are to construct now
            String amp = (escapeamp ? "&amp;" : "&");
            String connector = (show.indexOf("?") == -1 ? "?" : amp);

            Writer w = new StringBuilderWriter(show);
            for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    if (value.getClass().isArray()) {
                        for (Object v : (Object[]) value) {
                            if (v == null || Casting.isStringRepresentable(v.getClass())) { // if not string representable, that suppose it was an 'automatic' parameter which does need presenting on url
                                show.append(connector).append(entry.getKey()).append("=");
                                PARAM_ESCAPER.transform(new StringReader(Casting.toString(v)), w);
                                connector = amp;
                            }
                        }
                    } else if (value instanceof Iterable) {
                        for (Object v : (Iterable<?>) value) {
                            if (v == null || Casting.isStringRepresentable(v.getClass())) {
                                show.append(connector).append(entry.getKey()).append("=");
                                PARAM_ESCAPER.transform(new StringReader(Casting.toString(v)), w);
                                connector = amp;
                            }
                        }
                    } else {
                        if (Casting.isStringRepresentable(value.getClass())) {
                            show.append(connector).append(entry.getKey()).append("=");
                            PARAM_ESCAPER.transform(new StringReader(Casting.toString(value)), w);
                            connector = amp;
                        }
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
     * This URLConverter can work on any url, so is wlays in 'filtered' mode'.
     */
    public boolean isFilteredMode(Parameters frameworkParameters) throws FrameworkException {
        return true;
    }

    public int getDefaultWeight() {
        return Integer.MIN_VALUE + 1000;
    }

    /**
     * The BasicUrlConverter is unable to explicitely define a block and hence returns  <code>null</code>.
     */
    public Block getBlock(String path, Parameters frameworkParameters) {
        return null;
    }

    /**
     * @todo Actually these parameters are only added here, because this urlconverter is always in
     * BasicFramework. Actually BasicFramework should add them itself.
     */
    public Parameter[] getParameterDefinition() {
        return new Parameter[] {Parameter.REQUEST};
    }

    /**
     * Entirely unwraps the request. So, this returns the original request object, without implicit
     * additions by e.g. the Filter.
     */
    public static HttpServletRequest getUserRequest(HttpServletRequest req) {
        while (req instanceof HttpServletRequestWrapper) {
            req = (HttpServletRequest) ((HttpServletRequestWrapper) req).getRequest();
        }
        return req;
    }



    protected String getUrl(String path,
                            Map<String, ?> parameters,
                            Parameters frameworkParameters, boolean escapeAmps, boolean action) {
        HttpServletRequest request = getUserRequest(frameworkParameters.get(Parameter.REQUEST));
        State state = State.getState(request);
        Map<String, Object> map = new TreeMap<String, Object>();
        if (log.isDebugEnabled()) {
            log.debug("path '" + path + "' p:" + parameters + " fwp:" + frameworkParameters + " " + state + " rp:" + request.getParameterMap());
        }
        for (Map.Entry<String, ?> e : parameters.entrySet()) {
            map.put(e.getKey(), e.getValue());
        }
        if (state.isRendering()) {
            Block block = state.getBlock();
            if (log.isDebugEnabled()) {
                log.debug("current block " + block);
            }
            Block toBlock = block.getComponent().getBlock(path);

            if (toBlock != null) {

                map = new TreeMap<String, Object>(framework.prefix(state, map));
                String prefix = framework.getPrefix(state);
                log.debug("Using prefix " + prefix);
                for (Object e : request.getParameterMap().entrySet()) {
                    Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) e;

                    String k = entry.getKey();
                    if (k.startsWith(framework.getPrefix(state))) {
                        // for this block, don't add that,
                        // because should be in parameters then
                        log.trace("skipping " + entry);
                        continue;
                    }
                    if (! map.containsKey(k)) {
                        log.trace("Adding " + entry);
                        map.put(k, entry.getValue());
                    }
                }

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


    public Url getUrl(String path,
                            Map<String, ?> parameters,
                            Parameters frameworkParameters, boolean escapeAmps) {
        return new BasicUrl(this, getUrl(path, parameters, frameworkParameters, escapeAmps, false));
    }
    public Url getProcessUrl(String path,
                            Map<String, ?> parameters,
                            Parameters frameworkParameters, boolean escapeAmps) {
        return new BasicUrl(this, getUrl(path, parameters, frameworkParameters, escapeAmps, true));
    }

    public Url getInternalUrl(String page, Map<String, ?> params, Parameters frameworkParameters) {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        return new BasicUrl(this, BasicUrlConverter.getUrl(page, params, request, false));
    }

    public boolean equals(Object o) {
        return o instanceof BasicUrlConverter && ((BasicUrlConverter) o).framework.equals(framework);
    }
    public String toString() {
        return "COPY";
    }

}
