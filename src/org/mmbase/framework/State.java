/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import java.util.*;
import javax.servlet.ServletRequest;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * State is a wrapper arround HttpServletRequest which maintains the current state of framework
 * component rendering.
 * 
 *
 * @author Michiel Meeuwissen
 * @version $Id: State.java,v 1.5 2007-07-07 09:20:12 michiel Exp $
 * @since MMBase-1.9
 */
public class State {
    
    private static final Logger log = Logging.getLoggerInstance(State.class);

    public final static String KEY = "org.mmbase.framework.state";
    public static State getState(ServletRequest request, boolean create) {
        State state = (State) request.getAttribute(KEY);
        if (state == null && create) {            
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
    private final ServletRequest request;
    private final State previousState;
    
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
            
    }

    /**
     * Whether something is rendered right now. 
     */
    public boolean isRendering() {
        return renderer != null || processor != null;
    }
   
    /**
     * The currently rendered block, or <code>null</code>
     */
    public Block getBlock() {
        return renderer != null ? renderer.getBlock() :
            (processor != null ? processor.getBlock() : null);
    }
    protected int start() {
        if (count == 0) {
            throw new IllegalStateException("State " + this + " was already marked for end.");
        }
        if (processor == null) {
            id = previousState == null ? "" + count : previousState.getId() + '.' + count;
            return count++;
        } else {
            log.debug("Just processed " + processor);
            processor = null;
            return count; // processor already increaded count.
        }
    }
    /**
     * Puts this state in 'render' mode.
     * @return whether action must be performed
     * @throws IllegalStateException When renderers which should occur 'later' were already rendered,
     * or when the belonging request was already 'ended'.
     */
    public boolean render(Renderer rend) {
        if (rend.getType().ordinal() < type.ordinal()) {
            throw new IllegalStateException();
        }
        if (rend.getType().ordinal() > type.ordinal()) {
            // restart keeping the track.
            count = 1;
        }
        type = rend.getType();

        int i = start();
        renderer = rend;

        String a = request.getParameter(Framework.PARAMETER_ACTION.getName());
        log.debug("Action " + a);
        int action = a == null ? -1 : Integer.parseInt(a);
        return action == i;
    }
    /**
     * Puts this state in 'process' mode
     * @throws IllegalStateException If the renderer for block block was already rendered.
     * or when the belonging request was already 'ended'.
     */
    public void process(Processor processor) {
        if (renderer != null) throw new IllegalStateException();
        start();
        this.processor = processor;
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
        return "state:" + getId() + ":" + (renderer != null ? renderer : processor);
    }
    
}

