/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework.basic;
import org.mmbase.framework.*;
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
 * @version $Id$
 * @since MMBase-1.9
 */
public class State {

    private static final Logger log = Logging.getLoggerInstance(State.class);

    public final static String KEY = "org.mmbase.framework.state";

    /**
     * Returns the framework 'State' object for the given request.
     * @return a State. Never <code>null</code>.
     */
    public static State getState(ServletRequest request) {
        State state = (State) request.getAttribute(KEY);
        if (state == null) {
            state = new State(request);
        }
        return state;
    }


    private int count = 1;
    private String id = "";
    private final int depth;
    private Renderer renderer = null;
    private Renderer.Type type = Renderer.Type.NOT;
    private Processor processor = null;
    private Processor processed = null;
    private Parameters frameworkParameters = null;
    private final ServletRequest request;
    private final State previousState;
    private Object originalLocalizationContext = null;
    private String action = null;

    /**
     * Use this constructor, if you want to explicitely create a new State object. E.g. when
     * starting a <em>sub</com>component.
     * <code>
     *   state = getState(req);
     *   if (state.isRendering()) {
     *      state = new State(req);
     *   }
     * </code>
     * But this is only used by code which want to initiate a new component itself. Normally {@link
     * #getState(ServletRequest)} should suffice.
     */
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
    public WindowState getWindowState() {
        return WindowState.NORMAL;
    }

    /**
     * With recursive includes of blocks, it may occur that the state is only for components inside
     * a certain other component's block. In that case the depth &gt; 0.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * If the state is leading, then the currently rendered block is not a 'block in a block'.
     */
    public boolean isLeading() {
        return getDepth() == 0;
    }

    public ServletRequest getRequest() {
        return request;
    }

    /**
     * At the end of the request (or subcomponent), this method must be called, to indicate that
     * this state is no longer in use.
     */
    public void end() {
        request.setAttribute(KEY, previousState);
        count = 0;
        id = "";
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
    public void endProcess() {
        processed = processor;
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

    /**
     * Sets up a LocalizationContext attributes based on {@link Component#getBundle()}and puts in on
     * the request. This is recognized by fmt:message-tag, which in a JspRenderer can therefore be
     * used without fmt:bundle or fmt:setbundle.
     */
    protected void localizeContext() {
        String b = getBlock().getComponent().getBundle();
        if (b != null) {
            Locale locale = (Locale) request.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request");
            if (locale == null) {
                locale = org.mmbase.module.core.MMBase.getMMBase().getLocale();
            }
            originalLocalizationContext = request.getAttribute(Config.FMT_LOCALIZATION_CONTEXT + ".request");
            ResourceBundle bundle = ResourceBundle.getBundle(b, locale);
            request.setAttribute(Config.FMT_LOCALIZATION_CONTEXT + ".request",
                                 new LocalizationContext(bundle, Locale.getDefault()));
        }
    }

    protected void setType(Renderer.Type t) {
        if (t.ordinal() < type.ordinal()) {
            throw new IllegalStateException(t + " should not have come after " + type);
        }
        if (t.ordinal() > type.ordinal()) {
            // restart keeping the track.
            count = 1;
        }
        type = t;
    }

    public void setAction(String a) {
        action = a;
    }

    protected boolean needsProcess() {
        return processed == null && id.equals(action);
    }

    /**
     * @return Whether action must be performed.
     * @param frameworkParameters The parameters that are required by the framework
     * @param renderer Proposed renderer (State may decide to render another one, and return that)
     * @throws IllegalStateException When renderers which should occur 'later' were already rendered,
     * or when the belonging request was already 'ended'.
     */
    public Renderer startBlock(Parameters frameworkParameters, Renderer renderer) {
        if (count == 0) {
            throw new IllegalStateException("State " + this + " was already marked for end.");
        }
        ++count;
        setType(renderer != null ? renderer.getType() : Renderer.Type.NOT);

        this.frameworkParameters = frameworkParameters;
        log.debug("Start rendering for " + frameworkParameters);

        id = generateId(count);

        request.setAttribute(Framework.COMPONENT_ID_KEY, "mm_" + getId());

        return renderer != null ? getRenderer(renderer) : null;
    }

    protected String getBlockRequestKey() {
        return "__b" + getId();
    }

    /**
     * Determines what should be rendered now.
     */
    protected Renderer getRenderer(Renderer r) {
        String blockName = request.getParameter(getBlockRequestKey());
        log.debug("found block " + blockName + " at parameters");
        Block block = r.getBlock();
        if (blockName == null) {
            log.debug("No such block " + blockName, new Exception());
            return r;
        } else {
            Block toBlock = block.getComponent().getBlock(blockName);
            log.debug("Using alternative block " + toBlock);
            if (toBlock == null) {
                throw new RuntimeException("No such block '" + blockName + "' in " + block.getComponent());
            }
            return toBlock.getRenderer(r.getType());
        }
    }

    public void setBlock(Map<String, Object> map, Block toBlock) {
        map.put(getBlockRequestKey(), toBlock.getName());
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

    protected String generateId(int c) {
        return previousState == null ? "" + c : previousState.getId() + '.' + c;
    }
    /**
     * If rendering not yet started, this returns the id of a component which would begin now.
     */
    public String getUpcomingId() {
        return generateId(count + 1);
    }

    public String toString() {
        return "state:" + getDepth() + ":" + getId() + (isRendering() ? (":" + (renderer != null ? renderer : processor)) : "");
    }

}

