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
 * Builder that holds cronjobs and listens to changes.
 *  The builder also starts the CronDeamon. on startup the list of cronjobs is loaded into memory.
 *  <b>The builder uses the bridge to get a cloud using class security.</b> 
 * @author Kees Jongenburger
 */
public class CronJobs extends MMObjectBuilder implements Runnable {

    private static Logger log = Logging.getLoggerInstance(CronJobs.class);

    CronDaemon cronDaemon = null;

    public CronJobs() {
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
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
            cronDaemon.add(createJCronEntry(nodeIterator.nextNode()));
        }
    }

    /**
     * Inserts a cronjob into the database, and create and adds a  cronEntry to the CronDeamon 
     */
    public int insert(String owner, MMObjectNode objectNodenode) {
        int number = super.insert(owner, objectNodenode);
        Node node = getCloud().getNode(number);
        cronDaemon.add(createJCronEntry(node));
        return number;
    }


    /**
     * Commits a cronjob to the database.
     * On commit of a cronjob, the job is first removed from the cronDeamon and a new cronEntry is created and added to the CronDaemon.
     */
    public boolean commit(MMObjectNode objectNodenode) {
        boolean retval = super.commit(objectNodenode);
        Node node = getCloud().getNode(objectNodenode.getNumber());
        cronDaemon.remove(cronDaemon.getCronEntry("" + node.getNumber()));
        cronDaemon.add(createJCronEntry(node));
        return retval;
    }

    /**
     * removes the node from the database and also removes it from the CronDaemon
     */
    public void removeNode(MMObjectNode objectNodenode) {
        String number = "" + objectNodenode.getNumber();
        super.removeNode(objectNodenode);
        cronDaemon.remove(cronDaemon.getCronEntry(number));
    }
    
    
	private CronEntry createJCronEntry(Node node) {
		try {
			return new CronEntry("" + node.getNumber(), node.getStringValue("crontime"), node.getStringValue("name"), node.getStringValue("classfile"), node.getStringValue("config"));
		} catch (Throwable e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

    private Cloud getCloud() {
        return LocalContext.getCloudContext().getCloud("mmbase", "class", null);
    }
}
