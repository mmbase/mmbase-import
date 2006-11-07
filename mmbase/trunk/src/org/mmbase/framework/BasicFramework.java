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

/**
 * The framework that does nothing, besides adding the block-parameters to the URL. No support for
 * conflicting block parameters.
 *
 * @author Michiel Meeuwissen
 * @version $Id: BasicFramework.java,v 1.9 2006-11-07 20:23:19 michiel Exp $
 * @since MMBase-1.9
 */
public class BasicFramework implements Framework {
    private static final CharTransformer paramEscaper = new Url(Url.ESCAPE);

    public final static String KEY = "org.mmbase.framework.state";

    public String getName() {
        return "BASIC";
    }

    /**
     * General utility function to create an Url
     */
    public static StringBuilder getUrl(String page, Map<String, ? extends Object> params, HttpServletRequest req, boolean writeamp) {
        StringBuilder show = new StringBuilder();
        if (writeamp) {
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
            String amp = (writeamp ? "&amp;" : "&");
            String connector = (show.indexOf("?") == -1 ? "?" : amp);

            Writer w = new StringBuilderWriter(show);
            for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                if (value != null && Casting.isStringRepresentable(value.getClass())) { // if not string representable, that suppose it was an 'automatic' parameter which does need presenting on url
                    show.append(connector).append(entry.getKey()).append("=");
                    paramEscaper.transform(new StringReader(Casting.toString(value)), w);
                    connector = amp;
                }
            }
        }
        return show;
    }

    public StringBuilder getUrl(String page, Renderer renderer, Component component, Parameters blockParameters, Parameters frameworkParameters) {
        return getUrl(page, component, blockParameters, frameworkParameters, false);
    }

    public StringBuilder getUrl(String page, Processor processor, Component component, Parameters blockParameters, Parameters frameworkParameters) {
        return getUrl(page, component, blockParameters, frameworkParameters, false);
    }


    public StringBuilder getUrl(String page, Component component, Parameters blockParameters, Parameters frameworkParameters, boolean writeamp) {
        // just generate the URL
        HttpServletRequest req = frameworkParameters.get(Parameter.REQUEST);
        if (component == null) {
            StringBuilder sb = getUrl(page, blockParameters.toMap(), req, writeamp);
            return sb;
        } else {
            State state = getState(req);
            StringBuilder sb = getUrl(page, state.getMap(blockParameters.toMap()), req, writeamp);
            return sb;
        }
    }

    public Block getBlock(Component component, String blockName) {
        return component.getBlock(blockName);
    }

    public Parameters createFrameworkParameters() {
        return new Parameters(Parameter.REQUEST);
    }

    public boolean makeRelativeUrl() {
        return false;
    }

    protected State getState(HttpServletRequest request) {
        State state = (State) request.getAttribute(KEY);
        if (state == null) {
            state = new State(request);
            request.setAttribute(KEY, state);
        }
        return state;
    }

    protected void setBlockParameters(State state, Parameters blockParameters) {
        for (Map.Entry<String, ?> entry : blockParameters.toMap().entrySet()) {
            if (entry.getValue() == null) {
                blockParameters.set(entry.getKey(), state.getRequest().getParameter(state.getPrefix() + entry.getKey()));
            }
        }
    }

    public void render(Renderer renderer, Parameters blockParameters, Parameters frameworkParameters, Writer w) throws IOException {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        Object previousRenderer = request.getAttribute(Renderer.KEY);
        request.setAttribute(Renderer.KEY, renderer);
        State state = getState(request);
        state.render(renderer);
        setBlockParameters(state, blockParameters);
        try{
            renderer.render(blockParameters, frameworkParameters, w);
        } finally {
            request.setAttribute(Renderer.KEY, previousRenderer);
        }
    }

    public void process(Processor processor, Parameters blockParameters, Parameters frameworkParameters) throws IOException {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        for (Map.Entry<String, ?> entry : blockParameters.toMap().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        processor.process(blockParameters, frameworkParameters);
    }

    protected static class  State {
        private Map<Renderer, Integer> renderers = new HashMap<Renderer, Integer>();
        private int count;
        private Renderer renderer;
        private final HttpServletRequest request;

        State(HttpServletRequest r) {
            request = r;
        }

        public HttpServletRequest getRequest() {
            return request;
        }

        public void render(Renderer rend) {
            renderer = rend;
            count = renderers.containsKey(rend) ? renderers.get(rend) : 0;
            renderers.put(rend, ++count);
        }
        public String getPrefix() {
            return "_" + renderer.getBlock().getComponent().getName() + "_" + renderer.getBlock().getName() + "_" + count + "_";
        }
        public Map<String, Object> getMap(final Map<String, Object> params) {
            return new AbstractMap() {
                public Set<Map.Entry<String, Object>> entrySet() {
                    return new AbstractSet() {
                        public int size() { return params.size(); }
                        public Iterator<Map.Entry<String, Object>> iterator() {
                            return new Iterator() {
                                private Iterator<Map.Entry<String, Object>> i = params.entrySet().iterator();
                                public boolean hasNext() { return i.hasNext(); };
                                public Map.Entry<String, Object> next() {
                                    Map.Entry<String, Object> e = i.next();
                                    return new org.mmbase.util.Entry<String, Object>(State.this.getPrefix() + e.getKey(), e.getValue());
                                }
                                public void remove() { throw new UnsupportedOperationException(); }

                            };
                        }
                    };
                }
            };
        }
    }

}
