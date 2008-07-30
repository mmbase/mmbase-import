/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import java.util.concurrent.*;

/**
 * @javadoc
 * @application Tools
 * @author Daniel Ockeloen
 * @version $Id: MMEvents.java,v 1.26 2008-07-30 11:35:28 michiel Exp $
 */
public class MMEvents extends MMObjectBuilder {
    private static final Logger log = Logging.getLoggerInstance(MMEvents.class);

    DateStrings datestrings;
    private int notifyWindow = 3600;
    private ScheduledFuture future = null;

    public boolean init() {
        super.init();
        datestrings = new DateStrings(mmb.getLanguage());
        String tmp = getInitParameter("NotifyWindow");
        if (tmp != null) {
            try {
                int nw = Integer.parseInt(tmp);
                notifyWindow = nw;
            } catch (NumberFormatException xx) {}
        }
        boolean enableNotify = true;

        tmp = getInitParameter("EnableNotify");
        if (tmp != null && (tmp.equals("false") || tmp.equals("no"))) {
            enableNotify = false;
        }
        if (enableNotify) {
            future =  ThreadPools.scheduler.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    MMEvents.this.probeCall();
                }
                },
                100,  // shortly after
                300, TimeUnit.SECONDS);
        }
        return true;
    }

    public void shutdown() {
        if (future != null) {
            future.cancel(true);
        }
        super.shutdown();
    }

    public String getGUIIndicator(MMObjectNode node) {
        int tmp = node.getIntValue("start");
        //String str=DateSupport.getMonthDay(tmp)+"/"+DateSupport.getMonth(tmp)+"/"+DateSupport.getYear(tmp);
        String str = DateSupport.getTime(tmp) + "/" + DateSupport.getMonthDay(tmp) + "/" + DateSupport.getMonth(tmp) + "/" + DateSupport.getYear(tmp);
        return str;
    }

    public String getGUIIndicator(String field, MMObjectNode node) {
        if (field.equals("start")) {
            int str = node.getIntValue("start");
            return(DateSupport.getTimeSec(str) + " on " + DateSupport.getMonthDay(str) + "/" + DateSupport.getMonth(str) + "/" + DateSupport.getYear(str));
        } else if (field.equals("stop")) {
            int str = node.getIntValue("stop");
            return(DateSupport.getTimeSec(str) + " on " + DateSupport.getMonthDay(str) + "/" + DateSupport.getMonth(str) + "/" + DateSupport.getYear(str));
        } else if (field.equals("playtime")) {
            int str=node.getIntValue("playtime");
            return DateSupport.getTimeSecLen(str);
        }
        return null;
    }

    public Object getValue(MMObjectNode node, String field) {
        if (field.indexOf("time_") !=- 1) {
            int str = node.getIntValue(field.substring(5));
            return DateSupport.getTime(str);
        } else if (field.equals("time(start)")) {
            //node.prefix = "mmevents.";
            int str = node.getIntValue("start");
            //node.prefix="";
            return DateSupport.getTime(str);
        } else if (field.equals("time(stop)")) {
            //node.prefix="mmevents.";
            int str = node.getIntValue("stop");
            //node.prefix="";
            return DateSupport.getTime(str);
        } else if (field.indexOf("timesec_") != -1) {
            int str = node.getIntValue(field.substring(8));
            return DateSupport.getTimeSec(str);
        } else if (field.indexOf("longmonth_") != -1) {
            int str = node.getIntValue(field.substring(10));
            return datestrings.getMonth(DateSupport.getMonthInt(str));
        } else if (field.indexOf("month_") != -1) {
            int str = node.getIntValue(field.substring(6));
            return datestrings.getShortMonth(DateSupport.getMonthInt(str));
        } else if (field.indexOf("weekday_") != -1) {
            int str = node.getIntValue(field.substring(8));
            return datestrings.getDay(DateSupport.getWeekDayInt(str));
        } else if (field.indexOf("shortday_") != -1) {
            int str = node.getIntValue(field.substring(8));
            return datestrings.getShortDay(DateSupport.getWeekDayInt(str));
        } else if (field.indexOf("day_") != -1) {
            int str = node.getIntValue(field.substring(4));
            return ""+DateSupport.getDayInt(str);
        } else if (field.indexOf("year_") != -1) {
            int str = node.getIntValue(field.substring(5));
            return DateSupport.getYear(str);
        }
        return super.getValue(node, field);
    }

    private void probeCall() {
        // the queue is really a bad idea have to make up
        // a better way.
        final List<MMObjectNode> also = new ArrayList<MMObjectNode>();
        log.debug("MMEvent probe CALL");
        int now = (int)(System.currentTimeMillis()/1000);
        log.debug("The currenttime in seconds NOW="+now);
        MMObjectNode snode = null, enode = null;


        try {
            NodeSearchQuery query = new NodeSearchQuery(this);
            StepField startField = query.getField(getField("start"));
            query.addSortOrder(startField);
            query.setConstraint(new BasicFieldValueBetweenConstraint(startField, now, now + notifyWindow));
            if (log.isDebugEnabled()) log.debug("Executing query " + query);
            also.addAll(getNodes(query));
            if (also.size() > 0) {
                snode = also.get(also.size() - 1);
            }
        } catch (SearchQueryException e) {
            log.error(e);
        } catch (IllegalArgumentException iea) {
            if (mmb.getState()) {
                throw iea;
            }
        }

        try {
            NodeSearchQuery query = new NodeSearchQuery(this);
            StepField stopField = query.getField(getField("stop"));
            query.addSortOrder(stopField);
            query.setConstraint(new BasicFieldValueBetweenConstraint(stopField, now, now + notifyWindow));
            if (log.isDebugEnabled()) log.debug("Executing query " + query);
            also.addAll(getNodes(query));
            if (also.size() > 0 ) {
                enode =  also.get(also.size() - 1);
            }
        } catch (SearchQueryException e) {
            log.error(e);
        }
        MMObjectNode wnode = null;
        int sleeptime = -1;
        if (snode != null && enode == null) {
            sleeptime = snode.getIntValue("start");
            wnode = snode;
        }
        if (snode == null && enode != null) {
            sleeptime = enode.getIntValue("stop");
            wnode = enode;
        }
        if (snode != null && enode != null) {
            if (snode.getIntValue("start") < enode.getIntValue("stop")) {
                sleeptime = snode.getIntValue("start");
                wnode = snode;
            } else {
                sleeptime = enode.getIntValue("stop");
                wnode = enode;
            }
        }

        if (sleeptime != -1) {
            // WTF?

            if (log.isDebugEnabled()) {
                log.debug("SLEEPTIME=" + (sleeptime - now) + " wnode=" + wnode + " also=" + also);
            }
            final MMObjectNode waitNode = wnode;
            final int sleep = sleeptime;
            ThreadPools.scheduler.schedule(new Runnable() {
                    public void run() {
                        log.debug("Node local change " + waitNode.getNumber());
                        MMEvents.super.nodeLocalChanged(mmb.getMachineName(), "" + waitNode.getNumber(), tableName, "c");
                        for (MMObjectNode a : also) {
                            if ((a.getIntValue("start") == sleep) || (a.getIntValue("stop") == sleep)) {
                                log.debug("Node local change " + a.getIntValue("number"));
                                MMEvents.super.nodeLocalChanged(mmb.getMachineName(),"" + a.getNumber(), tableName,"c");
                            }
                        }
                    }
                }, (sleeptime - now), TimeUnit.SECONDS);
        }
    }

    public int insert(String owner,MMObjectNode node) {
        int val = node.getIntValue("start");
        int newval = (int)(System.currentTimeMillis()/1000);
        if (val == -1) {
            node.setValue("start", newval);

        }
        val = node.getIntValue("stop");
        if (val == -1) {
            node.setValue("stop", newval);

        }
        return super.insert(owner, node);
    }

    public boolean commit(MMObjectNode node) {
        int val = node.getIntValue("start");
        int newval= ( int)(System.currentTimeMillis()/1000);
        if (val == -1) {
            node.setValue("start", newval);

        }
        val = node.getIntValue("stop");
        if (val == -1) {
            node.setValue("stop", newval);

        }
        return super.commit(node);
    }
}
