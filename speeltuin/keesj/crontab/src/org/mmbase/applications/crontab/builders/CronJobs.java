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
import org.mmbase.util.logging.*;
/**
 *  MMBase cronjobs builder
 * @mmbase-application-name MMBaseCrontabApp
 *
 * @mmbase-nodemanager-name cronjobs
 * @mmbase-nodemanager-classfile org.mmbase.applications.crontab.builders.CronJobs
 * @mmbase-nodemanager-field name string
 * @mmbase-nodemanager-field crontime string
 * @mmbase-nodemanager-field classfile string
 * @mmbase-nodemanager-field config string
 *
 * @mmbase-relationtype-name related
 *
 * @mmbase-relationmanager-source cronjobs
 * @mmbase-relationmanager-destination mmservers
 * @mmbase-relationmanager-type related
 */
public class CronJobs extends MMObjectBuilder implements Runnable {

    private static Logger log = Logging.getLoggerInstance(CronJobs.class);

    CronDaemon cronDaemon = null;

    public CronJobs() {
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    public void run() {
        while (!MMBase.getMMBase().getState()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.warn("thread interrupted, cronjobs will not be loaded");
            }
        }
        cronDaemon = CronDaemon.getInstance();
        NodeIterator nodeIterator = LocalContext.getCloudContext().getCloud("mmbase").getNodeManager(getTableName()).getList(null, null, null).nodeIterator();
        while (nodeIterator.hasNext()) {
            cronDaemon.add(createJCronEntry(nodeIterator.nextNode()));
        }
    }

    public int insert(String owner, MMObjectNode objectNodenode) {
        int number = super.insert(owner, objectNodenode);
        Node node = LocalContext.getCloudContext().getCloud("mmbase").getNode(number);
        cronDaemon.add(createJCronEntry(node));
        return number;
    }

    private CronEntry createJCronEntry(Node node) {
        try {
            return new CronEntry("" + node.getNumber(), node.getStringValue("crontime"), node.getStringValue("name"), node.getStringValue("classfile"), node.getStringValue("config"));
        } catch (Throwable e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public boolean commit(MMObjectNode objectNodenode) {
        boolean retval = super.commit(objectNodenode);
        Node node = LocalContext.getCloudContext().getCloud("mmbase").getNode(objectNodenode.getNumber());
        cronDaemon.remove(cronDaemon.getCronEntry("" + node.getNumber()));
        cronDaemon.add(createJCronEntry(node));
        return retval;
    }

    public void removeNode(MMObjectNode objectNodenode) {
        super.removeNode(objectNodenode);
        Node node = LocalContext.getCloudContext().getCloud("mmbase").getNode(objectNodenode.getNumber());
        cronDaemon.remove(cronDaemon.getCronEntry("" + node.getNumber()));
    }
}
