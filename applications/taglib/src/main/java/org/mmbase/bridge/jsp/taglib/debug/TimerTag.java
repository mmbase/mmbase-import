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
import org.mmbase.util.logging.Level;


/**
 * Times how long the executing of the body took, and logs this.
 * Child elements can also time theirselves, and appear in the 'total
 * times' overview.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class TimerTag extends ContextReferrerTag {

    private static final Logger log = Logging.getLoggerInstance(TimerTag.class);

    private List<Long> timers;
    private List<String> timerIds;
    private Map<String, Long> totalTimes;

    private Attribute name = Attribute.NULL;
    private Attribute enabled = Attribute.NULL;

    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }


    /**
     * @since MMBase-1.9.6
     */
    public void setEnabled(String e) throws JspTagException {
        enabled = getAttribute(e, true);
    }


    /**
     * @since MMBase-1.9.6
     */
    public boolean isTimerEnabled() throws JspTagException {
        return log.isEnabledFor(Level.INFO) && (enabled == Attribute.NULL || "true".equals(enabled.getString(this)));
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
        if (timers != null) {
            if (log.isDebugEnabled()) {
                log.debug("Starting timer " + name.getString(this) + ": " + id);
            }
            timers.add(System.currentTimeMillis());
            if (totalTimes.get(id) == null) {
                totalTimes.put(id, 0L);
            }
            timerIds.add(id);
            return timers.size() - 1;
        }
        return 0;
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
        totalTimes.put(id, totalTimes.get(id) + duration);
        return duration;
    }

    /**
     * Initialize timer.
     */
    public int doStartTag() throws JspTagException {
        if (isTimerEnabled()) {
            log.info("Starting timer " + name.getString(this));
            timers     = new ArrayList<Long>(1);
            timerIds   = new ArrayList<String>(1);
            totalTimes = new HashMap<String, Long>();
            startTimer(getId(), getClass().getName());
        }
        return EVAL_BODY;
    }

    /**
     *
     */

    public int doAfterBody() throws JspTagException {
        if (isTimerEnabled()) {
            haltTimer(0);
            StringBuilder result = new StringBuilder("Timer ").append(name.getString(this)).append(" totals:\n");
            for (Map.Entry<String, Long> entry  : totalTimes.entrySet()) {
                result.append("   ").append(entry.getKey()).append(": ").append((double) entry.getValue()).append(" ms\n");
            }
            log.info(result.toString());
        }
        if (EVAL_BODY == EVAL_BODY_BUFFERED) {
            try {
                if (bodyContent != null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch (IOException ioe){
                throw new JspTagException(ioe.toString());
            }
        }
        return SKIP_BODY;

    }
    public int doEndTag() throws JspTagException {
        timers = null;
        timerIds = null;
        totalTimes = null;
        return super.doEndTag();
    }

}

