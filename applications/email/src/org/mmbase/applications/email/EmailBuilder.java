/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.email;

import java.util.*;

import org.mmbase.module.SendMailInterface;
import org.mmbase.module.core.*;
import org.mmbase.module.*;
import org.mmbase.util.logging.*;

/**
 * New Email builder, 
 *
 * rewrite of the email system that became too complex to handle
 * focus on the new one is different. code is now split per mail
 * type to allow for easer debug and better control over the 'simple'
 * mail action. The delayed and repeat mail will be handled with
 * the upcoming crontab builder
 *
 * @author Daniel Ockeloen
 * @version $Id: EmailBuilder.java,v 1.3 2003-07-08 17:27:47 michiel Exp $
 */
public class EmailBuilder extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(EmailBuilder.class);

    // defined values for state ( node field "mailstatus" )
    public final static int STATE_UNKNOWN   = -1; 
    public final static int STATE_WAITING   = 0; 
    public final static int STATE_DELIVERED = 1; 
    public final static int STATE_FAILED    = 2; 
    public final static int STATE_SPAMGARDE = 3; // spam filter hit, not mailed
    public final static int STATE_QUEUED    = 4; 

    // defined values for type ( node field "mailtype" )
    public final static int TYPE_ONESHOT     = 1; // Email will be sent and removed after sending.
    public final static int TYPE_REPEATMAIL  = 2; // Email will be sent and scheduled after sending for a next time
    public final static int TYPE_ONESHOTKEEP = 3; // Email will be sent and will not be removed.


    // @todo following three private members are currently unused.

    // number of emails send sofar since startup
    private int numberOfMailSend = 0;

    // reference to the sendmail module
    private static SendMailInterface sendMail; 

    private static EmailExpireHandler expireHandler;

    // javadoc inherited
    public boolean init() {
        super.init();


        // get the sendmail module
        sendMail = (SendMailInterface)Module.getModule("sendmail");

        // start the email nodes expire handler, deletes
        // oneshot email nodes after the defined expiretime 
        // check every defined sleeptime
        expireHandler = new EmailExpireHandler(this, 60, 30 * 60);

        return true;
    }

    /**
     * Override the function call to receive the functions called from
     * the outside world (mostly from the taglibs)
     */
    protected Object executeFunction(MMObjectNode node, String function, List arguments) {
        if (log.isDebugEnabled()) {
            log.debug("function=" + function);
        }

        // function setType(type) called, normally not used
        if (function.equals("setType") || function.equals("settype")) {
            setType(node, arguments);
        } else  if (function.equals("mail")) {    // function mail(type) called

            // check if we have arguments ifso call setType()
            if (arguments.size() > 0)
                setType(node, arguments);

            // get the mailtype so we can call the correct handler/method
            int mailtype = node.getIntValue("mailtype");
            switch (mailtype) {
            case TYPE_ONESHOT :
                EmailHandlerOneShot.mail(node);
                break;
            case TYPE_ONESHOTKEEP :
                EmailHandlerOneShotKeep.mail(node);
                break;
            }
        } else if (function.equals("startmail")) {  // function mail(type) called (starts a background thread)

            // check if we have arguments ifso call setType()
            if (arguments.size() > 0) {
                setType(node, arguments);
            }

            // get the mailtype so we can call the correct handler/method
            int mailtype = node.getIntValue("mailtype");
            switch (mailtype) {
            case TYPE_ONESHOT :
                EmailHandlerOneShot.startmail(node);
                break;
            case TYPE_ONESHOTKEEP :
                EmailHandlerOneShotKeep.startmail(node);
                break;
            }
        }
        return null;
    }

    /**
     * set the mailtype based on the first argument in the list
     */
    private static void setType(MMObjectNode node, List arguments) {
        String type = (String)arguments.get(0);
        if (type.equals("oneshot")) {
            node.setValue("mailtype", 1);
        } else if (type.equals("oneshotkeep")) {
            node.setValue("mailtype", 3);
        }
    }

    /**
     * return all the mailed nodes older than time given
     */
    public Enumeration getMailedOlderThen(int expireTime) {
        // calc search time based on expire time
        int time = (int) (System.currentTimeMillis() / 1000) - expireTime;

        // query database for the nodes
        Enumeration e = search("mailedtime=S" + time + " and mailstatus='1' and mailtype='1'"); //S?

        // return the nodes
        return e;
    }
}
