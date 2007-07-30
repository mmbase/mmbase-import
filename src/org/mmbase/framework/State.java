/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import java.util.*;
import javax.servlet.ServletRequest;

import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * State is a wrapper arround HttpServletRequest which maintains the current state of framework
 * component rendering.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: State.java,v 1.10 2007-07-30 16:36:05 michiel Exp $
 * @since MMBase-1.9
 */
public class State {

    /**
     * A framework parameter which is required for frameworks which base them selves around this class.
     */
    public static final Parameter<String> ACTION   = new Parameter<String>("action", String.class, "");


    private static final Logger log = Logging.getLoggerInstance(State.class);

    public final static String KEY = "org.mmbase.framework.state";

    /**
     * Returns the framework 'State' object for the given request.
     * @return a State. Never <code>null</code>
     */
    public static State getState(ServletRequest request) {
        State state = (State) request.getAttribute(KEY);
        if (state == null) {
            state = new State(request);
        }
        return state;
    }


    private int count = 1;
    private String id;
    private final int depth;
    private Renderer renderer = null;
    private Renderer.Type type = Renderer.Type.NOT;
    private Processor processor = null;
    private Parameters frameworkParameters = null;
    private final ServletRequest request;
    private final State previousState;
    private Object originalLocalizationContext = null;

    public State(ServletRequest r) {
        request = r;
        previousState = (State) r.getAttribute(KEY);
        depth = previousState != null  ? previousState.getDepth() + 1 : 0;
        request.setAttribute(KEY, this);
    }

    /**
     * The current window state of rendering. As yet unimplemented.
     * @todo
     */
    public Renderer.WindowState getWindowState() {
        return Renderer.WindowState.NORMAL;
    }

    /**
     * With recursive includes of blocks, it may occur that the state is only for components inside
     * a certain other component's block. In that case the depth &gt; 0.
     */
    public int getDepth() {
        return depth;
    }

    public ServletRequest getRequest() {
        return request;
    }

    /**
     * At the end of the request, this method must be called, to indicate that
     * this state is no longer in use.
     */
    public void end() {
        request.setAttribute(KEY, previousState);
        count = 0;
    }

    /**
     * After rendering (a certain renderer of) a block, the state must be informed about that.
     */
    public void endBlock() {
        renderer = null;
        processor = null;
        frameworkParameters = null;
        request.setAttribute(Config.FMT_LOCALIZATION_CONTEXT + ".request", originalLocalizationContext);
    }

    /**
     * Whether something is rendered right now.
     */
    public boolean isRendering() {
        return renderer != null || processor != null;
    }

    /**
     * Returns the parameters which were used to initialize rendering of the current block.
     * Or <code>null</code> if currently this state is not 'rendering'.
     */
    public Parameters getFrameworkParameters() {
        return frameworkParameters;
    }

    /**
     * The currently rendered block, or <code>null</code> if not rendering.
     */
    public Block getBlock() {
        return renderer != null ? renderer.getBlock() :
            (processor != null ? processor.getBlock() : null);
    }
    protected void localizeContext() {
        String b = getBlock().getComponent().getBundle();
        if (b != null) {
            Locale locale = (Locale) request.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request");
            if (locale == null) org.mmbase.module.core.MMBase.getMMBase().getLocale();
            originalLocalizationContext = request.getAttribute(Config.FMT_LOCALIZATION_CONTEXT + ".request");
            request.setAttribute(Config.FMT_LOCALIZATION_CONTEXT + ".request",
                                 new LocalizationContext(ResourceBundle.getBundle(b, locale), Locale.getDefault()));
        }
    }

    protected void setType(Renderer.Type t) {
        if (t.ordinal() < type.ordinal()) {
            throw new IllegalStateException();
        }
        if (t.ordinal() > type.ordinal()) {
            // restart keeping the track.
            count = 1;
        }
        type = t;
    }

    protected boolean needsProcess() {
        String a = frameworkParameters.get(ACTION);
        log.debug("Action " + a);
        return id.equals(a);
    }

    /**
     * @return Whether action must be performed.
     * @throws IllegalStateException When renderers which should occur 'later' were already rendered,
     * or when the belonging request was already 'ended'.

     */
    public void startBlock(Parameters frameworkParameters, Renderer.Type t) {
        if (count == 0) {
            throw new IllegalStateException("State " + this + " was already marked for end.");
        }
        count++;

        setType(t);

        this.frameworkParameters = frameworkParameters;
        log.info("Start rendering for " + frameworkParameters);

        id = previousState == null ? "" + count : previousState.getId() + '.' + count;
        request.setAttribute(Framework.COMPONENT_ID_KEY, "mm_" + getId());

    }


    /**
     * Puts this state in 'render' mode.
     */
    public void render(Renderer rend) {
        renderer = rend;
        localizeContext();
    }
    /**
     * Puts this state in 'process' mode
     * @throws IllegalStateException If the renderer for block block was already rendered.
     * or when the belonging request was already 'ended'.
     */
    public void process(Processor proc) {
        if (renderer != null) throw new IllegalStateException(); // works for basic-framework, which
                                                                 // processes in render method.

        processor = proc;
        localizeContext();

    }

    public Renderer getRenderer() {
        return renderer;
    }
    public Processor getProcessor() {
        return processor;
    }

    /**
     * Returns the id of the current block, which uniquely identifies it on the current page (or
     * http request).
     */
    public String getId() {
        return id;
    }
    public String toString() {
        return "state:" + getId() + (isRendering() ? (":" + (renderer != null ? renderer : processor)) : "");
    }

}

