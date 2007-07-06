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
 * @version $Id: State.java,v 1.1 2007-07-06 20:28:30 michiel Exp $
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
    private int id = 0;
    private Renderer renderer = null;
    private Processor processor = null;
    private final ServletRequest request;
    private final Object previousState;
    
    private State(ServletRequest r) {
        request = r;
        previousState = r.getAttribute(KEY);
        request.setAttribute(KEY, this);
    }
    
    
    public Renderer.WindowState getWindowState() {
        return Renderer.WindowState.NORMAL;
    }
    
    public ServletRequest getRequest() {
        return request;
    }
    public void end() {
        request.setAttribute(KEY, previousState);
    }
    public void endBlock() {
        renderer = null;
        processor = null;
            
    }
    public boolean componentRendering() {
        return renderer != null || processor != null;
    }
   
    public Block getBlock() {
        return renderer != null ? renderer.getBlock() :
            (processor != null ? processor.getBlock() : null);
    }
    /**
     * @returns whether action must be performed
     */
    public boolean render(Renderer rend) {
        renderer = rend;
        if (processor != null) {
            log.debug("Just processed " + processor);
        }
        id = count;
        count++;
        String a = request.getParameter(Framework.PARAMETER_ACTION.getName());
        log.debug("Action " + a);
        int action = a == null ? -1 : Integer.parseInt(a);
        return action == id;
    }
    public void process(Processor processor) {
        if (renderer != null) throw new IllegalStateException();
        this.processor = processor;
    }
    
    public Renderer getRenderer() {
        return renderer;
    }
    public Processor getProcessor() {
        return processor;
    }
    /**
     * Returns the id of the current renderer
     */
    public int getId() {
        return id;
    }
    public String toString() {
        return "state:" + getId() + ":" + (renderer != null ? renderer : processor);
    }
    
}

