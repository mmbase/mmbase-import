/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab.builders;

import  org.mmbase.applications.crontab.CronEntry;

import org.mmbase.bridge.Node;
import java.util.*;

import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeCronEntry.java,v 1.3 2008-07-29 10:01:21 michiel Exp $
 * @since MMBase-1.8.6
 */

public class NodeCronEntry extends CronEntry {

    private static final Logger log = Logging.getLoggerInstance(NodeCronEntry.class);


    public NodeCronEntry(Node node) throws Exception {
        super("" + node.getNumber(), node.getStringValue("crontime"), node.getStringValue("name"), node.getStringValue("classfile"), node.getStringValue("config"), CronEntry.Type.valueOf(node.getIntValue("type")));
        assert node != null;
        if (node.getNodeManager().hasField("lastrun")) {
            lastRun = node.getDateValue("lastrun");
        }
        if (node.getNodeManager().hasField("count")) {
            count   = node.getIntValue("count");
        }
        if (node.getNodeManager().hasField("lastcost")) {
            lastCost = node.getIntValue("lastcost");
        }
    }

    protected Node getNode() {
        return org.mmbase.bridge.ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null).getNode(getId());
    }


    protected void setCronTime(String ct) {
        String prev = cronTime;
        super.setCronTime(ct);
        if (prev == null) {
            Node n = getNode();
            n.setStringValue("crontime", cronTime);
            n.commit();
        }
    }


    public void setConfiguration(String conf) {
        super.setConfiguration(conf);
        Node n = getNode();
        n.setStringValue("config", conf);
        n.commit();
    }

    protected void setLastRun(Date d) {
        super.setLastRun(d);
        Node node = getNode();
        if (node.getNodeManager().hasField("lastrun")) {
            log.debug("Setting last run to " + d);
            node.setDateValue("lastrun", d);
            node.commit();
        } else {
            log.debug("No field lastrun");
        }
    }

    protected void incCount() {
        super.incCount();
        Node node = getNode();
        if (node.getNodeManager().hasField("count")) {
            log.debug("Setting count to " + count);
            node.setIntValue("count", count);
            node.commit();
        }
    }

    protected void setLastCost(int i) {
        super.setLastCost(i);
        Node node = getNode();
        if (node.getNodeManager().hasField("lastcost")) {
            log.debug("Setting lastcost to " + i);
            node.setIntValue("lastcost", i);
            node.commit();
        } else {
            log.debug("No field lastcost");
        }
    }


    public String toString() {
        return "NODE: " + super.toString();
    }

}
