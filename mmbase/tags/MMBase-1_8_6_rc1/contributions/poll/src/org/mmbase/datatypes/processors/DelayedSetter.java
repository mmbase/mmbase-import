/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * If you use a DelaySetter on a field, than the process of the field will make sure that the field
 * is not actually changed (the original value is returned). In stead, the field is only scheduled
 * for update. If another update for this field occurs, only this scheduled task is changed. This
 * way, very many updates on this field, result in only few updates in the database.
 *
 * @author Michiel Meeuwissen
 * @version $Id: DelayedSetter.java,v 1.2 2007-01-02 19:49:19 michiel Exp $
 * @since MMBase-1.9
 */

public class DelayedSetter implements Processor {

    private static final Logger log = Logging.getLoggerInstance(DelayedSetter.class);
    private static final long serialVersionUID = 1L;
    static final DelayQueue<Setter> queue = new DelayQueue<Setter>();
    static Map<NodeField, Setter> queued = new ConcurrentHashMap<NodeField, Setter>();
    static {
        SetThread t = new SetThread();
        t.start();
    }
    private long delay = 100 * 1000; // s
    public void setDelay(long d) {
        delay = d * 1000;
        log.info("Set delay to " + d + " seconds");
    }

    public final Object process(Node node, Field field, Object value) {
        NodeField nf = new NodeField(node, field);
        Setter ps = queued.get(nf);
        if (ps != null) {
            if (log.isTraceEnabled()) {
                log.trace("Scheduled for set already " + ps + "->" + value);
            }
            ps.setValue(value);
        } else {
            Setter s = new Setter(nf, value);
            queued.put(nf, s);
            queue.offer(s);
            if (log.isTraceEnabled()) {
                log.trace("Scheduling for set " + s);
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("" + queue);
        }
        return node.getValueWithoutProcess(field.getName());
    }

    public String toString() {
        return "delayedset";
    }

    static class SetThread extends Thread {

        SetThread() {
            super("DelayThread");
            setDaemon(true);
        }

        public void run() {
            while (true) {
                try {
                    Setter setter = DelayedSetter.queue.take();
                    queued.remove(setter.nodeField);
                    log.service("Using " + setter);
                    setter.set();
                } catch (InterruptedException e) {
                    log.service(Thread.currentThread().getName() +" was interruped.");
                    continue;
                } catch (RuntimeException rte) {
                    log.error(rte.getMessage(), rte);
                }
            }
        }
    }

    protected static class NodeField {
        public final Node node;
        public final String fieldName;
        private int hashCode;
        NodeField (Node n, Field field) {
            node = n;
            fieldName = field.getName();
            hashCode = node.getNumber() * 13 + fieldName.hashCode();
        }
        public boolean equals(Object o) {
            if (o instanceof NodeField) {
                NodeField s = (NodeField) o;
                return s.node.getNumber() == node.getNumber() && s.fieldName.equals(fieldName);
            } else {
                return false;
            }
        }
        public int hashCode() {
            return hashCode;
        }
        public String toString() {
            return node.getNumber() + ":" + fieldName;
        }
    }

    protected class Setter implements Delayed {
        private final NodeField nodeField;
        private final long endTime = System.currentTimeMillis() + DelayedSetter.this.delay;
        private Object value;

        Setter(NodeField nf, Object v) {
            nodeField = nf;
            value = v;
        }
        public void set() {
            nodeField.node.setValueWithoutProcess(nodeField.fieldName, value);
            nodeField.node.commit();
        }
        public Object getValue() {
            return value;
        }
        public Object setValue(Object v) {
            Object ov = value;
            value = v;
            return ov;
        }
        public long getDelay(TimeUnit unit) {
            return unit.convert(endTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
        public boolean equals(Object o) {
            if (o instanceof Setter) {
                Setter s = (Setter) o;
                return s.nodeField.equals(nodeField);
            } else {
                return false;
            }
        }
        public int compareTo(Delayed o) {
            return (int) (getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }

        public int hashCode() {
            return nodeField.hashCode();
        }
        public String toString() {
            return nodeField.toString() + "->" + value + " (due at " + new Date(endTime) + ")";
        }
    }
}


