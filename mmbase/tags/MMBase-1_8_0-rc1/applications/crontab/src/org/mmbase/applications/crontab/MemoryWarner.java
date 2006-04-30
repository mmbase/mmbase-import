/*
 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.
 
 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license
*/
package org.mmbase.applications.crontab;

import org.mmbase.bridge.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * Performs Runtime.gc(), and if afterwards the used memory is above a certain fraction of the
 * maximally used memory, mail a warning to someone. The idea is that that someone can then take
 * action, like exploring what is the matter, or clearing some caches.
 *
 * You need mmbase-email.jar installed for this.
 *
   <pre>
   &lt;property name="memory"&gt;*&#047;10 * * * *|org.mmbase.applications.crontab.MemoryWarner||0.8;Michiel.Meeuwissen@omroep.nl&lt;/property&gt;
   </pre>
 * @author Michiel Meeuwissen
 * @version $Id: MemoryWarner.java,v 1.1 2006-03-03 15:53:25 michiel Exp $
 */

public class MemoryWarner extends AbstractCronJob  {
    private static final Logger log = Logging.getLoggerInstance(MemoryWarner.class);


    public final void run() {
        try {
            String[] config = cronEntry.getConfiguration().split(";");

            Runtime rt = Runtime.getRuntime();
            rt.gc();

            long usedMemory = rt.totalMemory() - rt.freeMemory();
            long maxMemory  = rt.maxMemory();

            double use  = (double) usedMemory / rt.maxMemory();
            double limit = Double.parseDouble(config[0]);
            if (use > limit) {
                log.info("Memory use " + (use * 100) + "% > " + (limit * 100) + "%");
                log.info("Used memory over " + (limit * 100) + "% , mailing " + config[1]);
                Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null); 
                if (cloud.hasNodeManager("email")) {
                    NodeManager email = cloud.getNodeManager("email");
                    Node message = email.createNode();
                    message.setValue("from", "memorywarner@" + java.net.InetAddress.getLocalHost().getHostName());
                    message.setValue("to", config[1]);
                    message.setValue("subject", "Out of memory warning, less then " + (limit * 100) + "% free, for " + 
                                     org.mmbase.module.core.MMBaseContext.getHtmlRootUrlPath() + "@" + 
                                     java.net.InetAddress.getLocalHost().getHostName());
                    message.setValue("body", "Memory use " + (use * 100) + "% > " + (limit * 100) + "%");
                    message.commit();
                    Function mail = message.getFunction("mail");
                    Parameters params = mail.createParameters();
                    params.set("type", "oneshot");
                    mail.getFunctionValue(params);
                } else {
                    log.warn("No mail builder installed");
                    // could introduce depedency on java-mail here.
                }
            } else {
                log.info("Memory use " + (use * 100) + "% < " + (limit * 100) + "%");
            }
        } catch (java.net.UnknownHostException uhe) {
            log.error(uhe);
        }
    }
}
