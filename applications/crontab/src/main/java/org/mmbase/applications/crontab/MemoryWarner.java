/*
 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license
*/
package org.mmbase.applications.crontab;

import org.mmbase.bridge.*;
import org.mmbase.bridge.Module;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;

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
 * @version $Id$
 */

public class MemoryWarner extends AbstractCronJob  {
    private static final Logger log = Logging.getLoggerInstance(MemoryWarner.class);


    @Override
    public final void run() {
        try {
            String[] config = cronEntry.getConfiguration().split(";");

            Runtime rt = Runtime.getRuntime();
            rt.gc();

            long usedMemory = rt.totalMemory() - rt.freeMemory();

            double use  = (double) usedMemory / rt.maxMemory();
            double limit = Double.parseDouble(config[0]);
            String usePerc = NumberFormat.getPercentInstance(Locale.US).format(use);
            String limitPerc = NumberFormat.getPercentInstance(Locale.US).format(limit);
            if (use > limit) {
                log.info("Memory use " + usePerc + " > " +  limitPerc);
                log.info("Used memory over " + limitPerc + " , mailing " + config[1]);
                Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);

                String emailBuilder = "email";
                try {
                    Module sendmail = cloud.getCloudContext().getModule("sendmail");
                    emailBuilder = sendmail.getProperty("emailbuilder");
                } catch (NotFoundException nfe) {
                    log.warn("No email module " + nfe);
                    emailBuilder = "email";
                }
                if (cloud.hasNodeManager(emailBuilder)) {
                    NodeManager email = cloud.getNodeManager(emailBuilder);
                    Node message = email.createNode();
                    String from = "memorywarner@" + java.net.InetAddress.getLocalHost().getHostName();
                    Pattern p = Pattern.compile("(?i)\\A[A-Z0-9_\\-\\+\\&\\*\\#]+([\\.-]?[A-Z0-9_\\-\\+\\&\\*\\#])*@([A-Z0-9_-]{2,}[\\.]{1})+([A-Z]{2,})\\z");
                    if (! p.matcher(from).matches()) {  // email not valid, use 'to' address
                        from = config[1];
                    }
                    message.setValue("from", from);
                    message.setValue("to", config[1]);
                    message.setValue("subject", "Out of memory warning: more than " + limitPerc + " in use, for " +
                                     org.mmbase.module.core.MMBaseContext.getHtmlRootUrlPath() + "@" +
                                     java.net.InetAddress.getLocalHost().getHostName());
                    message.setValue("body", "Memory use " + usePerc + " > " + limitPerc);
                    message.commit();
                    Function  mail = message.getFunction("mail");
                    Parameters params = mail.createParameters();
                    params.set("type", "oneshot");
                    mail.getFunctionValue(params);
                    log.info("Used memory over " + limitPerc + " , mailed: " + config[1] + " (from: " + from + ")");
                } else {
                    log.warn("No mail builder installed");
                    // could introduce depedency on java-mail here.
                }

            } else {
                log.info("Memory use " + usePerc + " < " + limitPerc);
            }
        } catch (java.net.UnknownHostException uhe) {
            log.error(uhe);
        }
    }
}
