package org.mmbase.applications.crontab.builders;

import org.mmbase.module.core.MMBase;

import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;

import org.mmbase.applications.crontab.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
/**
 * The MMBase cronjobs builder has 2 purposes
 * @mmbase-application-name MMBaseCrontabApp
 *
 * @mmbase-nodemanager-name cronjobs
 * @mmbase-nodemanager-classfile org.mmbase.applications.crontab.builders.CronJobs
 * @mmbase-nodemanager-field name string
 * @mmbase-nodemanager-field crontime string
 * @mmbase-nodemanager-field classfile string
 *
 * @mmbase-relationtype-name related
 *
 * @mmbase-relationmanager-source cronjobs
 * @mmbase-relationmanager-destination mmservers
 * @mmbase-relationmanager-type related
 */
public class CronJobs extends MMObjectBuilder implements Runnable {

    private static Logger log = Logging.getLoggerInstance(CronJobs.class);

    JCronDaemon jCronDaemon = null;

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

        jCronDaemon = JCronDaemon.getInstance();
        NodeIterator nodeIterator = LocalContext.getCloudContext().getCloud("mmbase").getNodeManager("cronjobs").getList(null, null, null).nodeIterator();
        while (nodeIterator.hasNext()) {
            jCronDaemon.add(createJCronEntry(nodeIterator.nextNode()));
        }
    }

    public int insert(String owner, MMObjectNode objectNodenode) {
        int number = super.insert(owner, objectNodenode);
        Node node = LocalContext.getCloudContext().getCloud("mmbase").getNode(number);
        jCronDaemon.add(createJCronEntry(node));
        return number;
    }

    private JCronEntry createJCronEntry(Node node) {
        try {
            return new JCronEntry("" + node.getNumber(), node.getStringValue("crontime"), node.getStringValue("name"), node.getStringValue("classfile"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public boolean commit(MMObjectNode objectNodenode) {
        boolean retval = super.commit(objectNodenode);
        Node node = LocalContext.getCloudContext().getCloud("mmbase").getNode(objectNodenode.getNumber());
        return retval;
    }

    public void removeNode(MMObjectNode objectNodenode) {
        super.removeNode(objectNodenode);
        Node node = LocalContext.getCloudContext().getCloud("mmbase").getNode(objectNodenode.getNumber());
        //jCronDaemon.getJCronEntries().getJCronEntry("" +  node.getNumber()).();
    }
}
