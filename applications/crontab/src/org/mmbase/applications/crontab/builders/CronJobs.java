/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab.builders;

import org.mmbase.applications.crontab.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.Event;
import org.mmbase.util.logging.*;
import java.util.*;

/**
 * Builder that holds cronjobs and listens to changes.
 *  The builder also starts the CronDeamon. on startup the list of cronjobs is loaded into memory.
 *  <b>The builder uses the bridge to get a cloud using class security.</b>
 * @author Kees Jongenburger
 * @version $Id: CronJobs.java,v 1.8 2008-08-04 15:32:28 michiel Exp $
 */
public class CronJobs extends MMObjectBuilder implements Runnable {

    private static Logger log = Logging.getLoggerInstance(CronJobs.class);

    CronDaemon cronDaemon = null;

    public CronJobs() {
        org.mmbase.util.ThreadPools.jobsExecutor.execute(this);
    }

    /**
     * This thread wait's for MMBase to be started and then adds all the crontEntries to the CronDaemon
     */
    public void run() {
        while (!MMBase.getMMBase().getState()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.warn("thread interrupted, cronjobs will not be loaded");
                return;
            }
        }

        cronDaemon = CronDaemon.getInstance();
        NodeIterator nodeIterator = getCloud().getNodeManager(getTableName()).getList(null, null, null).nodeIterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            CronEntry entry = null;
            try {
                entry = new NodeCronEntry(node);
                NodeList servers = node.getRelatedNodes("mmservers");
                if (servers.size() > 0) {
                    String machineName = MMBaseContext.getMachineName();
                    boolean found = false;
                    for (int i=0; i<servers.size(); i++) {
                        Node server = servers.getNode(i);
                        String name = server.getStringValue("name");
                        if (name != null && name.equalsIgnoreCase(machineName)) {
                            log.service("Adding cron entry [" + entry + "] for server [" + name + "]");
                            cronDaemon.add(entry);
                            found = true;
                            break;
                        } else {
                            log.service("Ignoring related server [" + name + "], does not equal servername [" + machineName + "]");
                        }
                    }
                    if (!found) {
                        log.service("NOT Adding cron entry [" + entry + "], not related to server [" + machineName + "]");
                    }
                } else {
                    log.service("Adding cron entry [" + entry + "]");
                    cronDaemon.add(entry);
                }
            } catch (Exception e) {
                log.warn("did not add cronjob with id " + node.getNumber() + " because of error " + e.getMessage());
            }
        }
    }

    public void notify(NodeEvent event) {
        log.info("Received " + event);
        switch(event.getType()) {
        case Event.TYPE_NEW: {
            try {
                Node node = getCloud().getNode(event.getNodeNumber());
                cronDaemon.add(new NodeCronEntry(node));
            } catch (Exception e) {
                throw new RuntimeException("error while creating cron entry with id " + event.getNodeNumber() + " error " + e.getMessage());
            }
            break;
        }
        case Event.TYPE_DELETE: {
            String number = "" + event.getNodeNumber();
            CronEntry entry = cronDaemon.getCronEntry(number);
            if (entry != null) {
                cronDaemon.remove(entry);
            }
            break;
        }
        case Event.TYPE_CHANGE: {
            CronEntry entry = cronDaemon.getCronEntry("" + event.getNodeNumber());
            if (entry == null) {
                log.warn("cron entry with ID " + event.getNodeNumber() + " was not found. this usualy means it was invalid");
            } else {
                if (entry instanceof NodeCronEntry) {
                    Set<String> changed = event.getChangedFields();
                    if (changed.contains("classfile") ||
                        changed.contains("name") ||
                        changed.contains("crontime") ||
                        changed.contains("type")) {
                        log.debug("Changed fields " + changed);
                        cronDaemon.remove(entry);
                        try {
                            Node n = getCloud().getNode(event.getNodeNumber());
                            CronEntry newEntry = new NodeCronEntry(n);
                            log.debug("Replacing cronentry " + entry + " with " + newEntry);
                            cronDaemon.add(newEntry);
                        } catch (Exception e) {
                            throw new RuntimeException("error while creating cron entry with id " + event.getNodeNumber() + " error " + e.getMessage());
                        }
                    } else {
                        log.debug("Ignored " + event);
                    }
                } else {
                    log.warn("How come, " + entry + " is not a node-entry");
                }
            }
            break;
        }
        default: {
            log.debug("Ignored " + event);
        }

        }

    }

    private Cloud getCloud() {
        return LocalContext.getCloudContext().getCloud("mmbase", "class", null);
    }



}
