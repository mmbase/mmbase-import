/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.notifications;
import org.mmbase.module.*;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.core.event.*;
import org.mmbase.bridge.*;
import java.util.concurrent.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class maintains all notifyables and takes care of the actual submission of all
 * notifications.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Notifier.java,v 1.1 2007-10-08 10:00:54 michiel Exp $
 **/
public class Notifier extends ReloadableModule implements NodeEventListener,Runnable {

    private static final Logger log = Logging.getLoggerInstance(Notifier.class);

    protected boolean running = true;

    DelayQueue<Notifyable> queue = new DelayQueue<Notifyable>();
    @Override
    public void reload() {
        loadNotifyables();
    }


    protected void loadNotifyables() {
        queue.clear();
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
        NodeIterator ni = cloud.getNodeManager("notifyables").getList(null).nodeIterator();
        while (ni.hasNext()) {
            queue.add(new Notifyable(ni.nextNode()));
        }
    }

    @Override
    public void onload() {
        loadNotifyables();
        Thread thread = new Thread(MMBaseContext.getThreadGroup(), this);
        thread.start();

    }

    @Override
    public void shutdown() {
        running = false;
    }

    public void run() {
        while (running) {
            try {
                Notifyable notifyable = queue.take();
                log.service("Found notifyiable " + notifyable);
                notifyable.send();
            } catch (InterruptedException ie) {
                log.service(ie);
                return;
            }
        }
        log.service("Shut down " + this);
    }

    public void notify(NodeEvent ne) {
        // TODO
    }





}
