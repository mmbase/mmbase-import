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
 * @version $Id: BasicFramework.java,v 1.42 2007-06-18 22:00:48 michiel Exp $
 * @since MMBase-1.9
 */
public class BasicFramework implements Framework {
    private static final Logger log = Logging.getLoggerInstance(BasicFramework.class);

    private static final CharTransformer paramEscaper = new Url(Url.ESCAPE);

    public final static String KEY = "org.mmbase.framework.state";
    public final static String RENDER_ID = "org.mmbase.framework.render_id";

    public static final Parameter<Node>   N         = new Parameter<Node>("n", Node.class);
    public static final Parameter<String> COMPONENT = new Parameter<String>("component", String.class);
    public static final Parameter<String> BLOCK     = new Parameter<String>("block", String.class);
    public static final Parameter<Integer> ACTION   = new Parameter<Integer>("action", Integer.class);
    public static final Parameter<String> CATEGORY  = new Parameter<String>("category", String.class);


    public String getName() {
        return "BASIC";
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
        Component component  = ComponentRepository.getInstance().getComponent(frameworkParameters.get(COMPONENT));
        if (component == null) {
            // if no explicit component specified, suppose current component, if there is one:
            State state = getState(req, false);
            if (state != null) {
                component = state.getRenderer().getBlock().getComponent();
            }
        }
        
        if (component == null) {
            log.debug("Not currently rendering a component");
            // cannot be handled by Framework
            StringBuilder sb = BasicFramework.getUrl(path, parameters, req, escapeAmps);
            return sb;
        } else {
            // can explicitely state new block by either 'path' (of mm:url) or framework parameter  'block'.

            if ("".equals(path)) path = null; 
            if (path == null) path = frameworkParameters.get(BLOCK);

            State state = getState(req, false);

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
                if (k.equals(CATEGORY.getName())) continue; // already in servletpath
                if (k.equals(BLOCK.getName())) continue; // already in servletpath
                if (k.equals(COMPONENT.getName())) continue; // already in servletpath
                map.put(k, entry.getValue()[0]);
            }
            for (Map.Entry<String, Object> entry : parameters) {
                blockParameters.set(entry.getKey(), entry.getValue());                
            }
            map.putAll(state.getMap(blockParameters.toMap()));

            String category = req.getParameter(CATEGORY.getName());
            if (category == null) {
                Block.Type[] classification = block.getClassification();
            }
            String page = "/mmbase/" + category + "/" + component.getName() + "/" + block.getName();
            StringBuilder sb = BasicFramework.getUrl(page, map.entrySet(), req, escapeAmps);
            return sb;            

        }
    }

    public Block getBlock(Parameters frameworkParameters) {
        Component comp  = ComponentRepository.getInstance().getComponent(frameworkParameters.get(COMPONENT));
        if (comp == null) return null;
        Block block = comp.getBlock(frameworkParameters.get(BLOCK));
        return block;
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
                    return BasicFramework.getUrl(page, params, request, false);
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
                return BasicFramework.getUrl(page, params, request, false);
            }
        } else {            
            return BasicFramework.getUrl(page, params, request, false);
        }
    }
    
    public Block getBlock(Component component, String blockName) {
        return component.getBlock(blockName);
    }

    public Parameters createParameters() {
        return new Parameters(Parameter.REQUEST, Parameter.CLOUD, N, COMPONENT, BLOCK, ACTION, CATEGORY);
    }

    public boolean makeRelativeUrl() {
        return false;
    }

    /**
     * Returns the state object for the request.
     */
    protected State getState(HttpServletRequest request, boolean create) {
        State state = (State) request.getAttribute(KEY);
        if (state == null && create) {            
            state = new State(request);
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

    public void render(Renderer renderer, Parameters blockParameters, Parameters frameworkParameters, Writer w, Renderer.WindowState windowState) throws FrameworkException {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        Object previousRenderer = request.getAttribute(Renderer.KEY);
        request.setAttribute(Renderer.KEY, renderer);
        if (log.isDebugEnabled()) {
            log.info("Rendering " + renderer);
        }
        State state = getState(request, true);

        request.setAttribute(COMPONENT_CLASS_KEY, "mm_fw_basic");
        if (state.render(renderer)) {
            log.info("Processing " + renderer.getBlock() + " " + renderer.getBlock().getProcessor());
            renderer.getBlock().getProcessor().process(blockParameters, frameworkParameters);
        }
        request.setAttribute(COMPONENT_ID_KEY, "mm" + state.getPrefix());
        setBlockParameters(state, blockParameters);
        request.setAttribute(Config.FMT_LOCALIZATION_CONTEXT + ".request", 
                             new LocalizationContext(renderer.getBlock().getComponent().getBundle(), Locale.getDefault())); 
        // should _not_ use default locale!

        try {
            renderer.render(blockParameters, frameworkParameters, w, windowState);
        } finally {
            request.setAttribute(Renderer.KEY, previousRenderer);
            state.end();
        }
    }

    public void process(Processor processor, Parameters blockParameters, Parameters frameworkParameters) throws FrameworkException {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        for (Map.Entry<String, ?> entry : blockParameters.toMap().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        processor.process(blockParameters, frameworkParameters);
    }

    public Node getUserNode(Parameters frameworkParameters) {
        Cloud cloud = frameworkParameters.get(Parameter.CLOUD);
        return cloud.getCloudContext().getAuthentication().getNode(cloud.getUser());
    }

    public String getUserBuilder() {
        return org.mmbase.module.core.MMBase.getMMBase().getMMBaseCop().getAuthentication().getUserBuilder();
    }

    /**
     * 
     */
    protected static class State {
        private int count = 1;
        private int id = 0;
        private Renderer renderer = null;
        private final HttpServletRequest request;
        private final Object previousState;

        State(HttpServletRequest r) {
            request = r;
            previousState = r.getAttribute(KEY);
            request.setAttribute(KEY, this);
        }


        public Renderer.WindowState getWindowState() {
            return Renderer.WindowState.NORMAL;
        }

        public HttpServletRequest getRequest() {
            return request;
        }
        public void end() {
            request.setAttribute(KEY, previousState);
        }

        /**
         * @Returns whether action must be performed
         */
        public boolean render(Renderer rend) {
            renderer = rend;
            id = count;
            count++;
            String a = request.getParameter(ACTION.getName());
            int action = a == null ? -1 : Integer.parseInt(a);
            return action == id;
        }
        public String getPrefix() {
            //return "_" + renderer.getBlock().getComponent().getName() + "_" +
            //renderer.getBlock().getName() + "_" + count + "_";
            return "_bfw_" + count + "_";
        }
        public Renderer getRenderer() {
            return renderer;
        }
        /**
         * Returns the id of the current renderer
         */
        public int getId() {
            return id;
        }

        public Map<String, Object> getMap(final Map<String, Object> params) {
            return new AbstractMap<String, Object>() {
                public Set<Map.Entry<String, Object>> entrySet() {
                    return new AbstractSet<Map.Entry<String, Object>>() {
                        public int size() { return params.size(); }
                        public Iterator<Map.Entry<String, Object>> iterator() {
                            return new Iterator<Map.Entry<String, Object>>() {
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
