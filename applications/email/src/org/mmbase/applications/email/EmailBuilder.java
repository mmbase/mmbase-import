/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.email;

import java.util.*;

import org.mmbase.module.core.*;

import org.mmbase.util.*;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * New Email builder, 
 *
 * rewrite of the email system that became too complex to handle
 * focus on the new one is different. code is now split per mail
 * type to allow for easer debug and better control over the 'simple'
 * mail action. The delayed and repeat mail will be handled with
 * the upcomming crontab builder
 *
 * @javadoc is a bit lame
 *
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 */
public class EmailBuilder extends MMObjectBuilder {

    private static final Logger log = Logging.getLoggerInstance(EmailBuilder.class);

    public final static Parameter[] MAIL_PARAMETERS = {
        new Parameter("type",    String.class)
    };


    public final static Parameter[] STARTMAIL_PARAMETERS = MAIL_PARAMETERS;
    public final static Parameter[] SETTYPE_PARAMETERS   = MAIL_PARAMETERS;



    // defined values for state ( node field "mailstatus" )
    public final static int STATE_UNKNOWN   = -1; // unknown
    public final static int STATE_WAITING   = 0; // waiting
    public final static int STATE_DELIVERED = 1; // delivered
    public final static int STATE_FAILED    = 2; // failed
    public final static int STATE_SPAMGARDE = 3; // spam filter hit, not mailed
    public final static int STATE_QUEUED    = 4; // queued


    // defined values for state ( node field "mailtype" )
    public final static int TYPE_ONESHOT     = 1; // Email will be sent and removed after sending.
    public final static int TYPE_REPEATMAIL  = 2; // Email will be sent and scheduled after sending for a next time
    public final static int TYPE_ONESHOTKEEP = 3; // Email will be sent and will not be removed.


    static String usersBuilder;
    static String usersEmailField;
    static String groupsBuilder;


    // number of emails send sofar since startup
    private int numberofmailsend = 0;

    // reference to the sendmail module
    private static SendMailInterface sendmail;

    // reference to the expire handler
    private static EmailExpireHandler expirehandler;

    /**
     * init
     */
    public boolean init() {
        super.init ();

        // get the sendmail module
        sendmail = (SendMailInterface) mmb.getModule("sendmail");
        
        // start the email nodes expire handler, deletes
        // oneshot email nodes after the defined expiretime 
        // check every defined sleeptime
        expirehandler = new EmailExpireHandler(this, 60, 30 * 60);

        usersBuilder = getInitParameter("users-builder");
        if (usersBuilder == null) usersBuilder = "users";

        usersEmailField = getInitParameter("users-email-field");
        if (usersEmailField == null) usersEmailField = "email";

        groupsBuilder = getInitParameter("groups-builder");
        if (groupsBuilder == null) usersBuilder = "groups";

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Parameter[] getParameterDefinition(String function) {
        return org.mmbase.util.functions.NodeFunction.getParametersByReflection(EmailBuilder.class, function);
    }

 
    /**
     * {@inheritDoc}
     *
     * Override the function call to receive the functions called from
     * the outside world (mostly from the taglibs)
     */
    protected Object executeFunction(MMObjectNode node, String function, List arguments) {
        if (log.isDebugEnabled()) {
            log.debug("function=" + function);
        }

        // function setType(type) called, normally not used
        if (function.equals("setType") || function.equals("settype") ) {
            setType(node, arguments);
            return null;
        } else  if (function.equals("mail")) {  // function mail(type) called            
            // check if we have arguments ifso call setType()
            if (arguments.size() > 0) {
                setType(node, arguments);
            }
            
            // get the mailtype so we can call the correct handler/method
            int mailtype = node.getIntValue("mailtype");
            switch(mailtype) {
            case TYPE_ONESHOT :
                EmailHandlerOneShot.mail(node);
                break;
            case TYPE_ONESHOTKEEP :
                EmailHandlerOneShotKeep.mail(node);
                break;
            }
            return null;
        } else if (function.equals("startmail")) {         // function mail(type) called (starts a background thread)
            
            // check if we have arguments ifso call setType()
            if (arguments.size() > 0) {
                setType(node, arguments);
            }
            
            // get the mailtype so we can call the correct handler/method
            int mailtype = node.getIntValue("mailtype");
            switch(mailtype) {
            case TYPE_ONESHOT :
                EmailHandlerOneShot.startmail(node);
                break;
            case TYPE_ONESHOTKEEP :
                EmailHandlerOneShotKeep.startmail(node);
                break;
            }
            return null;
        } else {
            return super.executeFunction(node, function, arguments);
        }
    }
    
    
    /**
     * return the sendmail module
     */
    static SendMailInterface getSendMail() {
        return sendmail;
    }
    
    /**
     * set the mailtype based on the first argument in the list
     */
    private static void setType(MMObjectNode node, List arguments) {
        String type = (String) arguments.get(0);
        if ("oneshot".equals(type)) {
            node.setValue("mailtype", TYPE_ONESHOT);
        } else if ("oneshotkeep".equals(type)) {
            node.setValue("mailtype", TYPE_ONESHOTKEEP);
        } else {
            node.setValue("mailtype", TYPE_ONESHOT);
        }
    }
    
    
    /**
     * Returns all the one-shot delivered mail nodes older than a specified time.
     * This is used by {@link EmailExpireHandler} to remove expired emails.
     * @param expireAge The minimum age of the desired nodes in seconds
     * @return a unmodifiable List of MMObjectNodes
     */
    List getDeliveredMailOlderThan(long expireAge) {
        // calc search time based on expire time
        long age = (System.currentTimeMillis() / 1000) - expireAge;
        // query database for the nodes

        // should use Query object!
        return Collections.unmodifiableList(searchVector("WHERE mailedtime < " + age +
                                                         " and mailstatus = " + STATE_DELIVERED +
                                                         " and mailtype = " + TYPE_ONESHOT ));
    }
}
