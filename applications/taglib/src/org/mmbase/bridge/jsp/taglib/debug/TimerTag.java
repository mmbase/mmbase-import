/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.debug;

import java.io.IOException;
import javax.servlet.jsp.JspTagException;

import java.util.*;

import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

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
 * @version $Id: TimerTag.java,v 1.10 2007-02-24 21:58:51 nklasens Exp $ 
 */

public class TimerTag extends ContextReferrerTag {

    private static final Logger log = Logging.getLoggerInstance(TimerTag.class);

    private List<Long> timers;
    private List<String> timerIds;
    private Map<String, LongContainer> totalTimes;

    private Attribute name = Attribute.NULL;

    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }

    /**
     * Starts a timer.
     *
     * @param id  An id which optionally can be null. Can e.g. be getId().
     * @param id2 Another id which cannot be null. Something descriptive.
     * @return an integer handle, which you need to remember to halt the timer.
     */


    public int startTimer(String id, String id2) throws JspTagException {
        if (id == null) {
            return startTimer(id2);
        } else {
            return startTimer(id + ":" + id2);
        }
    }

    /**
     *
     */

    public int startTimer(String id) throws JspTagException  {
        if (log.isDebugEnabled()) {
            log.debug("Starting timer " + name.getString(this) + ": " + id);
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

    public long haltTimer(int handle) throws JspTagException  {
        long duration = System.currentTimeMillis() - timers.get(handle).longValue();
        String id = timerIds.get(handle);
        if (log.isDebugEnabled()) {
            log.debug("Timer " + (name != Attribute.NULL ? name.getString(this) + ":"  : "")  + id + ": " + (double)duration / 1000 + " s");
        }
        totalTimes.get(id).value += duration;
        return duration;
    }

    /**
     * Initialize timer.
     */
    public int doStartTag() throws JspTagException {
        log.info("Starting timer " + name.getString(this));
        timers     = new ArrayList<Long>(1);
        timerIds   = new ArrayList<String>(1);
        totalTimes = new HashMap<String, LongContainer>();
        startTimer(getId(), getClass().getName());
        return EVAL_BODY_BUFFERED;
    }

    /**
     *
     */

    public int doAfterBody() throws JspTagException {
        haltTimer(0);
        String result = "Timer " + name.getString(this) + " totals:\n";
        Iterator<String> i = totalTimes.keySet().iterator();

        while (i.hasNext()) {
            String key = i.next();
            result += "   " + key + ": " +  (double)(totalTimes.get(key).value) + " ms\n";
        }
        log.info(result);

        try {
            if (bodyContent != null) {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            }
            return SKIP_BODY;
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
    }
    public int doEndTag() throws JspTagException {
        timers = null;
        timerIds = null;
        totalTimes = null;
        return super.doEndTag();
    }

}

