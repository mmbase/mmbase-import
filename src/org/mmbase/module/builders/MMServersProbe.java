/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Daniel Ockeloen
 * @version $Id: MMServersProbe.java,v 1.13 2004-01-28 14:45:57 daniel Exp $
 */
public class MMServersProbe implements Runnable {

    private static Logger log = Logging.getLoggerInstance(MMServersProbe.class.getName());

        Thread kicker = null;
        MMServers parent=null;

        public MMServersProbe(MMServers parent) {
                this.parent=parent;
                init();
        }

        public void init() {
                this.start();
        }


        /**
         * Starts the main Thread.
         */
        public void start() {
                /* Start up the main thread */
                if (kicker == null) {
                        kicker = new Thread(this,"mmserversprobe");
			kicker.setDaemon(true);
                        kicker.start();
                }
        }

        /**
         * Stops the main Thread.
         */
        public void stop() {
                kicker.interrupt();
                kicker = null;
        }

        /**
         * Main loop, exception protected
         */
        public void run () {
                kicker.setPriority(Thread.MIN_PRIORITY+1);
                while (kicker!=null) {
                        try {
                                doWork();
                        } catch(Exception e) {
                                log.error("Exception in mmservers thread!" + Logging.stackTrace(e));
                        	try { 
					Thread.sleep(60*1000);
		        	} catch (InterruptedException f) {
               	 			log.debug(f.toString());
            			}
                        }
                }
        }

        /**
         * Main work loop
         */
        public void doWork() {
                kicker.setPriority(Thread.MIN_PRIORITY+1);

        int probeInterval = 60 * 1000;

                // ugly pre up polling
                while (parent.mmb==null || parent.mmb.getState()==false) {
                        try {
                                Thread.sleep(2*1000);
                        } catch (InterruptedException e){
                log.debug(e.toString());
                        }
                }

        String tmp = parent.getInitParameter("ProbeInterval");
        if (tmp != null) {
            if (log.isDebugEnabled()) log.debug("ProbeInterval was configured to be " + tmp + " seconds");
            probeInterval = Integer.parseInt(tmp) * 1000;
        }

                while (kicker != null) {
                        parent.probeCall();
                        try { 
				Thread.sleep(probeInterval);
		        } catch (InterruptedException e) {
               	 		log.debug(e.toString());
            		}
                }
        }


}
