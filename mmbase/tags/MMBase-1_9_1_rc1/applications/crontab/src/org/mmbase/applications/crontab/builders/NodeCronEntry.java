/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab.builders;

import  org.mmbase.applications.crontab.CronEntry;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import java.util.*;

import org.mmbase.util.logging.*;

/**
 * CronEntries defined by nodes of the type 'cronjobs' store several aspects of the cron entries in
 * fields. {@link #isActive} is implemented using related 'mmservers' objects.
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeCronEntry.java,v 1.8 2009-04-02 08:08:57 michiel Exp $
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

    @Override public String getServers() {
        Node jobNode = getNode();
        NodeIterator servers = jobNode.getRelatedNodes("mmservers").nodeIterator();

        if ((! servers.hasNext()) &&  (! "true".equals(jobNode.getNodeManager().getProperty(CronJobs.MMSERVERS_REQUIRED)))) {
            servers = SearchUtil.findNodeList(getNode().getCloud(), "mmservers", "state", org.mmbase.module.builders.MMServers.ACTIVE).nodeIterator();
        }
        StringBuilder bul = new StringBuilder();
        while (servers.hasNext()) {
            Node server = servers.nextNode();
            if (bul.length() > 0) bul.append(" ");
            bul.append(server.getStringValue("name"));
        }
        return bul.toString();

    }

    @Override public boolean isActive() {
        Node jobNode = getNode();
        NodeIterator servers = jobNode.getRelatedNodes("mmservers").nodeIterator();
        if (! servers.hasNext() &&
            ! "true".equals(jobNode.getNodeManager().getProperty(CronJobs.MMSERVERS_REQUIRED))) {
            return true;
        }

        String machineName = org.mmbase.module.core.MMBaseContext.getMachineName();
        while (servers.hasNext()) {
            Node server = servers.nextNode();
            String name = server.getStringValue("name");
            if (name != null && name.equalsIgnoreCase(machineName)) {
                log.debug("Active [" + this + "] for server [" + name + "]");
                return true;
            } else {
                log.debug("Ignoring related server [" + name + "], does not equal servername [" + machineName + "]");
            }
        }
        log.debug("NOT active  cron entry [" + this + "], not related to server [" + machineName + "]");
        return false;
    }

    @Override protected void setCronTime(String ct) {
        String prev = cronTime;
        super.setCronTime(ct);
        if (prev == null) {
            Node n = getNode();
            n.setStringValue("crontime", cronTime);
            n.commit();
        }
    }


    @Override public void setConfiguration(String conf) {
        super.setConfiguration(conf);
        Node n = getNode();
        n.setStringValue("config", conf);
        n.commit();
    }

    @Override protected void setLastRun(Date d) {
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

    @Override protected void incCount() {
        super.incCount();
        Node node = getNode();
        if (node.getNodeManager().hasField("count")) {
            log.debug("Setting count to " + count);
            node.setIntValue("count", count);
            node.commit();
        }
    }

    @Override protected void setLastCost(int i) {
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


    @Override public String toString() {
        return "NODE: " + super.toString();
    }

}
