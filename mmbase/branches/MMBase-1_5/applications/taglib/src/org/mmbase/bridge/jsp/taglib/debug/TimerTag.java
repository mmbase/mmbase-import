/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.debug;

import java.io.IOException;
import javax.servlet.jsp.JspTagException;

import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;

import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


class LongContainer {
    long value;
    LongContainer() {
        value = 0;
    }    
}

/**
 * Times how long the executing of the body took, and logs this.
 * Child elements can also time theirselves, and appear in the 'total
 * times' overview.
 *
 * @author Michiel Meeuwissen
 *
 **/


public class TimerTag extends ContextReferrerTag {

    private static Logger log = Logging.getLoggerInstance(TimerTag.class.getName());
    
    private Vector timers; 
    private Vector timerIds;
    private HashMap totalTimes;

    private int lastTimer;
    private String name = null;

    public void setName(String n) throws JspTagException {
        name = getAttributeValue(n);
    }

    /**
     * Starts a timer. 
     *
     * @param id  An id which optionally can be null. Can e.g. be getId().
     * @param id2 Another id which cannot be null. Something descriptive.
     * @return an integer handle, which you need to remember to halt the timer.
     */

    
    public int startTimer(String id, String id2) {
        if (id == null) {
            return startTimer(id2);
        } else {
            return startTimer(id + ":" + id2);
        }
    }

    /**
     *
     */

    public int startTimer(String id) {
        if (log.isDebugEnabled()) {
            log.debug("Starting timer " + name + ": " + id);
        }
        timers.add(new Long(System.currentTimeMillis()));
        if (totalTimes.get(id) == null) {
            totalTimes.put(id, new LongContainer());
        }
        timerIds.add(id);
        return timers.size() - 1;
    }

    /**
     * Stops the timer identified by the handle, and logs and returns the result in second.
     */

    public long haltTimer(int handle) {
        long duration = System.currentTimeMillis() - ((Long)timers.get(handle)).longValue();
        String id = (String)timerIds.get(handle);
        if (log.isDebugEnabled()) {
            log.debug("Timer " + (name != null ? name + ":"  : "")  + id + ": " + (double)duration / 1000 + " s");
        }
        ((LongContainer)totalTimes.get(id)).value += duration;
        return duration;
    }

    /**
     * Initialize timer.
     */
    public int doStartTag() throws JspTagException {        
        log.info("Starting timer " + name );
        timers     = new Vector(1);
        timerIds   = new Vector(1);
        totalTimes = new HashMap();
        lastTimer = 0;
        startTimer(getId(), getClass().getName());
        return EVAL_BODY_TAG;
    }

    /**
     * 
     */
    
    public int doAfterBody() throws JspTagException {
        haltTimer(0);
        String result = "Timer " + name + " totals:\n";
        Iterator i = totalTimes.keySet().iterator();
        
        while (i.hasNext()) {
            String key = (String)i.next();
            result += "   " + key + ": " +  (double)(((LongContainer) totalTimes.get(key)).value) + " ms\n";
        }
        log.info(result);        
        
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());            
            return SKIP_BODY;
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
    }


}

