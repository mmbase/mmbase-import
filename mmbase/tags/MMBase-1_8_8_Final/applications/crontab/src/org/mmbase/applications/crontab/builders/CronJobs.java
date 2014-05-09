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
 * @version $Id$
 */
public class CronJobs extends MMObjectBuilder  {

    private static final Logger log = Logging.getLoggerInstance(CronJobs.class);

    public static String MMSERVERS_REQUIRED = "RelatedMMServersRequired";

    /**
     * Adds all the crontEntries to the CronDaemon
     */
    @Override public boolean init() {
        boolean res = super.init();
        org.mmbase.util.ThreadPools.jobsExecutor.execute(new Runnable() {
                public void run() {
                    CronJobs.this.readJobs();
                }
            });
        return res;
    }

    private final Set<NodeCronEntry> myJobs = new HashSet<NodeCronEntry>();


    public static CronJobs getBuilder() {
        return (CronJobs) MMBase.getMMBase().getBuilder("cronjobs");
    }

    public void readJobs() {
        Cloud cloud = getCloud();

        CronDaemon cronDaemon = CronDaemon.getInstance();
        for(NodeCronEntry e : myJobs) {
            cronDaemon.remove(e);
        }
        myJobs.clear();

        log.service("Loading jobs from " + this);
        NodeIterator nodeIterator = cloud.getNodeManager(getTableName()).getList(null, null, null).nodeIterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            try {
                NodeCronEntry entry = new NodeCronEntry(node);
                log.service("Adding cron entry [" + entry + "]");
                myJobs.add(entry);
                cronDaemon.add(entry);
            } catch (Exception e) {
                log.warn("did not add cronjob with id " + node.getNumber() + " because of error " + e.getMessage());
            }
        }
    }

    @Override public void notify(NodeEvent event) {
        log.debug("Received " + event);
        CronDaemon cronDaemon = CronDaemon.getInstance();
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
        LocalContext.getCloudContext().assertUp();
        return LocalContext.getCloudContext().getCloud("mmbase", "class", null);
    }



}
