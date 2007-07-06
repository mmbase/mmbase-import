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
 * @version $Id: BasicFramework.java,v 1.46 2007-07-06 11:00:20 michiel Exp $
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

    public static final Parameter<Boolean> PROCESS  = new Parameter<Boolean>("process", Boolean.class);

    protected UrlConverter urlConverter = new BasicUrlConverter(this); // could be made configurable


    public StringBuilder getUrl(String path, Collection<Map.Entry<String, Object>> parameters,
                                Parameters frameworkParameters, boolean escapeAmps) {
        return urlConverter.getUrl(path, parameters, frameworkParameters, escapeAmps);
    }
    public StringBuilder getInternalUrl(String page, Collection<Map.Entry<String, Object>> params, Parameters frameworkParameters) {
        return urlConverter.getInternalUrl(page, params, frameworkParameters);
    }

    public String getName() {
        return "BASIC";
    }

    public Block getBlock(Parameters frameworkParameters) {
        Component comp  = ComponentRepository.getInstance().getComponent(frameworkParameters.get(COMPONENT));
        if (comp == null) return null;
        Block block = comp.getBlock(frameworkParameters.get(BLOCK));
        return block;
    }

    
    public Block getBlock(Component component, String blockName) {
        return component.getBlock(blockName);
    }

    public Parameters createParameters() {
        return new Parameters(Parameter.REQUEST, Parameter.CLOUD, N, COMPONENT, BLOCK, ACTION, CATEGORY, PROCESS);
    }

    public boolean makeRelativeUrl() {
        return false;
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

    
    public State getState(HttpServletRequest request, boolean create) {
        State state = (State) request.getAttribute(KEY);
        if (state == null && create) {            
            state = new State(request);
        }
        return state;
    }


    /**
     * 
     */
    public static class State {
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
        public String toString() {
            return "state:" + getPrefix();
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
